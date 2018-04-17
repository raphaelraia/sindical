package br.com.rtools.sistema.beans;

import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.BackupPostgres;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.sistema.Resolucao;
import br.com.rtools.sistema.Servidor;
import br.com.rtools.sistema.SisConfiguracaoEmail;
import br.com.rtools.sistema.TipoResolucao;
import br.com.rtools.sistema.dao.BackupPostgresDao;
import br.com.rtools.sistema.dao.SisConfiguracaoEmailDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Upload;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class ConfiguracaoBean implements Serializable {

    private List<Configuracao> listaConfiguracao;
    private Configuracao configuracao;
    private SisConfiguracaoEmail sisConfiguracaoEmail;
    private String mensagem;
    private String descricaoPesquisa;
    private Juridica juridica;
    private Usuario usuario;

    private int indexTipoResolucao;
    private List<SelectItem> listaTipoResolucao;
    private Resolucao resolucao;
    private String resolucaoUsuario;
    private List<SisConfiguracaoEmail> listSisConfiguracaoEmail;
    private Boolean backup;
    private List<SelectItem> listServidor;
    private Integer idServidor;

    @PostConstruct
    public void init() {
        listaConfiguracao = new ArrayList();
        listSisConfiguracaoEmail = new ArrayList();
        configuracao = new Configuracao();
        mensagem = "";
        descricaoPesquisa = "";
        juridica = new Juridica();
        usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        sisConfiguracaoEmail = new SisConfiguracaoEmail();
        indexTipoResolucao = 2;
        listaTipoResolucao = new ArrayList();
        listServidor = new ArrayList();
        resolucao = new Resolucao();
        backup = false;
        loadListServidor();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoBean");
        GenericaSessao.remove("configuracaoPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("configuracaoBean");
    }

    public void loadResolucao() {
        getUsuario();
        getListaTipoResolucao();
        getResolucao();
    }

    public String salvarResolucao() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (getResolucao().getId() == -1) {
            resolucao.setUsuario(usuario);
            resolucao.setTipoResolucao((TipoResolucao) dao.find(new TipoResolucao(), Integer.valueOf(listaTipoResolucao.get(indexTipoResolucao).getDescription())));
            if (dao.save(resolucao)) {
                dao.commit();
            } else {
                dao.rollback();
            }
        } else {
            resolucao.setTipoResolucao((TipoResolucao) dao.find(new TipoResolucao(), Integer.valueOf(listaTipoResolucao.get(indexTipoResolucao).getDescription())));
            if (dao.update(resolucao)) {
                dao.commit();
            } else {
                dao.rollback();
            }
        }

//        String retorno = GenericaSessao.getString("urlRetorno").isEmpty() ? "menuPrincipal" : GenericaSessao.getString("urlRetorno");
//        GenericaSessao.put("linkClicado", true);
        HttpServletRequest paginaRequerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String urlAtual = paginaRequerida.getRequestURI();
        urlAtual = urlAtual.substring(urlAtual.lastIndexOf("/") + 1, urlAtual.lastIndexOf("."));

        return urlAtual;
    }

    public void save() {

        Dao dao = new Dao();

        configuracao.setJuridica(juridica);

        if (configuracao.getJuridica().getId() == -1) {
            setMensagem("Pesquisar pessoa jurídica!");
            return;
        }
        if (configuracao.getIdentifica().equals("")) {
            setMensagem("Informar o identificador do cliente, deve ser único!");
            return;
        }

        if (configuracao.getIdentifica().equals("")) {
            setMensagem("Informar o identificador do cliente, deve ser único!");
            return;
        }

        if (getConfiguracao().getId() == null) {
            ConfiguracaoDao configuracaoDB = new ConfiguracaoDao();
            if (configuracaoDB.existeIdentificador(configuracao)) {
                setMensagem("Identificador já existe!");
                return;
            }

            if (configuracaoDB.existeIdentificadorPessoa(configuracao)) {
                setMensagem("Identificador já existe para essa pessoa!");
                return;
            }
            dao.openTransaction();
            if (dao.save(configuracao)) {
                dao.commit();
                setMensagem("Configuração efetuada com sucesso");
            } else {
                dao.rollback();
                setMensagem("Erro ao criar configuração.");
            }
        } else {
            dao.openTransaction();
            if (dao.update(configuracao)) {
                dao.commit();
                setMensagem("Configuração atualizada com sucesso");
            } else {
                dao.rollback();
                setMensagem("Erro ao atualizar configuração.");
            }
        }
    }

    public void add() {
        if (configuracao.getId() != null) {
            if (sisConfiguracaoEmail.getId() != null) {
                sisConfiguracaoEmail.setConfiguracao(configuracao);
                if (new Dao().save(sisConfiguracaoEmail, true)) {
                    GenericaMensagem.info("Sucesso", "Registro inserido");
                    listSisConfiguracaoEmail.clear();
                } else {
                    GenericaMensagem.warn("Erro", "Registro já existe!");
                }
            }
        }
    }

    public void remove(SisConfiguracaoEmail sce) {
        if (new Dao().delete(sce, true)) {
            listSisConfiguracaoEmail.clear();
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    /**
     * SocketServer 5465
     *
     * @param c
     */
    public void backup(Configuracao c) throws IOException, ClassNotFoundException {
        BackupPostgres exists = new BackupPostgresDao().exist();
        if (exists != null) {
            GenericaMensagem.warn("Validação", "Existe um backup em andamento para o cliente " + exists.getConfiguracao().getIdentifica() + "!!!");
            return;
        }
        BackupPostgres bp = new BackupPostgres();
        bp.setUsuario(Usuario.getUsuario());
        bp.setConfiguracao(c);
        if (new Dao().save(bp, true)) {
            backup = true;
            // Socket cliente = new Socket();
            // cliente.connect(new InetSocketAddress("192.168.15.160", 5465), 1000);
            // cliente.connect(new InetSocketAddress("192.168.15.160", 5465), (1000 * 60 * 10));
            // ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
            // Date data_atual = (Date) entrada.readObject();
            // entrada.close();
            // System.out.println("Conexão encerrada");
            GenericaMensagem.info("Sucesso", "Backup em processamento!!! Ficara pronto dentro de 5 minutos. Backup ID: " + bp.getId());
            GenericaMensagem.info("Se o arquivo não existir", "/backupbancos/" + DataHoje.livre(new Date(), "yyyy") + "/" + DataHoje.livre(new Date(), "MM") + "/" + DataHoje.livre(new Date(), "dd") + "/" + c.getDatabaseServerAlias() + "/" + c.getIdentifica() + ".backup");
            GenericaMensagem.info("Se o arquivo já existir", "/backupbancos/" + DataHoje.livre(new Date(), "yyyy") + "/" + DataHoje.livre(new Date(), "MM") + "/" + DataHoje.livre(new Date(), "dd") + "/" + c.getDatabaseServerAlias() + "/" + c.getIdentifica() + "_" + bp.getId() + ".backup");
        } else {
            GenericaMensagem.warn("Erro", "Ao enviar pedido de backup!");
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (getConfiguracao().getId() != -1) {
            if (dao.delete((Configuracao) dao.find(configuracao))) {
                dao.commit();
                configuracao = new Configuracao();
                setMensagem("Configuração excluída com sucesso");
            } else {
                dao.commit();
                setMensagem("Erro ao excluir configuração.");
            }
        }
    }

    public String edit(Configuracao c) {
        GenericaSessao.put("linkClicado", true);
        configuracao = c;
        juridica = configuracao.getJuridica();
        listSisConfiguracaoEmail.clear();
        return "configuracao";
    }

    public List<Configuracao> getListaConfiguracao() {
        if (listaConfiguracao.isEmpty()) {
            if (!descricaoPesquisa.equals("")) {
                ConfiguracaoDao configuracaoDB = new ConfiguracaoDao();
                listaConfiguracao = (List<Configuracao>) configuracaoDB.listaConfiguracao(descricaoPesquisa);
            } else {
                Dao dao = new Dao();
                listaConfiguracao = (List<Configuracao>) dao.list("Configuracao");
            }
        }
        return listaConfiguracao;
    }

    public void limparListaConfiguracao() {
        listaConfiguracao.clear();
    }

    public void setListaConfiguracao(List<Configuracao> listaConfiguracao) {
        this.listaConfiguracao = listaConfiguracao;
    }

    public Configuracao getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(Configuracao configuracao) {
        this.configuracao = configuracao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public Juridica getJuridica() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload cu = new ConfiguracaoUpload();
        cu.setArquivo(event.getFile().getFileName());
        cu.setDiretorio("Imagens");
        cu.setSubstituir(true);
        cu.setRenomear("LogoCliente" + ".png");
        cu.setEvent(event);
        Upload.enviar(cu, true);
    }

    public Usuario getUsuario() {
        usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getIndexTipoResolucao() {
        return indexTipoResolucao;
    }

    public void setIndexTipoResolucao(int indexTipoResolucao) {
        this.indexTipoResolucao = indexTipoResolucao;
    }

    public List<SelectItem> getListaTipoResolucao() {
        if (listaTipoResolucao.isEmpty()) {
            Dao dao = new Dao();

            List<TipoResolucao> result = dao.list(new TipoResolucao());

            for (int i = 0; i < result.size(); i++) {
                listaTipoResolucao.add(new SelectItem(i, result.get(i).getDescricao(), "" + result.get(i).getId()));
            }
        }
        return listaTipoResolucao;
    }

    public void setListaTipoResolucao(List<SelectItem> listaTipoResolucao) {
        this.listaTipoResolucao = listaTipoResolucao;
    }

    public Resolucao getResolucao() {
        if (resolucao.getId() == -1) {
            ConfiguracaoDao db = new ConfiguracaoDao();

            if (usuario != null) {
                resolucao = db.pesquisaResolucaoUsuario(usuario.getId());
            }

            if (resolucao.getId() != -1) {
                for (int i = 0; i < listaTipoResolucao.size(); i++) {
                    if (resolucao.getTipoResolucao().getId() == Integer.valueOf(listaTipoResolucao.get(i).getDescription())) {
                        indexTipoResolucao = i;
                    }
                }
            }
        }
        return resolucao;
    }

    public void setResolucao(Resolucao resolucao) {
        this.resolucao = resolucao;
    }

    public List<SisConfiguracaoEmail> getListSisConfiguracaoEmail() {
        if (configuracao.getId() != null) {
            if (listSisConfiguracaoEmail.isEmpty()) {
                listSisConfiguracaoEmail = new SisConfiguracaoEmailDao().findByConfiguracao(configuracao.getId());
            }
        }
        return listSisConfiguracaoEmail;
    }

    public void setListSisConfiguracaoEmail(List<SisConfiguracaoEmail> listSisConfiguracaoEmail) {
        this.listSisConfiguracaoEmail = listSisConfiguracaoEmail;
    }

    public SisConfiguracaoEmail getSisConfiguracaoEmail() {
        return sisConfiguracaoEmail;
    }

    public void setSisConfiguracaoEmail(SisConfiguracaoEmail sisConfiguracaoEmail) {
        this.sisConfiguracaoEmail = sisConfiguracaoEmail;
    }

    public void loadListServidor() { 
        Dao dao = new Dao();
        listServidor = new ArrayList();
        List<Servidor> list = dao.list(new Servidor(), true);
        for (int i = 0; i < list.size(); i++) {
            if(i == 0) {
                configuracao.setDatabaseServerAlias(list.get(i).getAlias());
            }
            listServidor.add(new SelectItem(list.get(i).getAlias(), list.get(i).getApelido(), list.get(i).getApelido()));
        }
    }

    public List<SelectItem> getListServidor() {
        return listServidor;
    }

    public void setListServidor(List<SelectItem> listServidor) {
        this.listServidor = listServidor;
    }

    public Integer getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(Integer idServidor) {
        this.idServidor = idServidor;
    }
}
