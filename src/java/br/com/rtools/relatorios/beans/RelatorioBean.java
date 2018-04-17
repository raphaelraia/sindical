package br.com.rtools.relatorios.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.relatorios.RelatorioGrupo;
import br.com.rtools.relatorios.RelatorioJoin;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.RelatorioTipo;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class RelatorioBean implements Serializable {

    private Relatorios relatorio;
    private RelatorioOrdem relatorioOrdem;
    private RelatorioParametros relatorioParametros;
    private RelatorioGrupo relatorioGrupo;
    private RelatorioJoin relatorioJoin;
    private List<SelectItem> listRotina;
    private List<Relatorios> listRelatorio;
    private List<RelatorioOrdem> listRelatorioOrdem;
    private Integer rotina_id;
    private Integer rotina_pesquisa_id;
    private List<RelatorioParametros> listaRelatorioParametro;
    private List<RelatorioGrupo> listaRelatorioGrupo;
    private List<RelatorioJoin> listaRelatorioJoin;
    private List<SelectItem> listRelatorioTipo;
    private Integer relatorio_tipo_id;
    private List<SelectItem> listClientes;

    private String textQuery;
    private String description;
    private String cliente;

    @PostConstruct
    public void init() {
        relatorio = new Relatorios();
        relatorioOrdem = new RelatorioOrdem();
        listRotina = new ArrayList<>();
        listRelatorio = new ArrayList<>();
        listRelatorioOrdem = new ArrayList<>();
        rotina_id = 0;
        rotina_pesquisa_id = null;
        relatorioParametros = new RelatorioParametros();
        relatorioGrupo = new RelatorioGrupo();
        relatorioJoin = new RelatorioJoin();
        listaRelatorioParametro = new ArrayList();
        listClientes = new ArrayList();
        listaRelatorioGrupo = new ArrayList();
        listaRelatorioJoin = new ArrayList();
        textQuery = "";
        description = "";
        loadListRotinas();
        loadListRelatorioTipo();
        loadListClientes();
        listRelatorio = new Dao().list(new Relatorios(), true);
    }

    @PreDestroy
    public void destroy() {
        clear();
        GenericaSessao.remove("rotinaBean");
    }

    public void verQuery() {
        loadListaRelatorioParametro();
        textQuery = "";
        if (listaRelatorioParametro.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhuma Query para ser Visualizada!");
            PF.update("form_relatorio");
            return;
        }

        loadListaRelatorioJoin();
        loadListaRelatorioGrupo();

        textQuery = "SELECT \n ";

        if (!listaRelatorioParametro.isEmpty()) {
            String s = "";
            for (RelatorioParametros rp : listaRelatorioParametro) {
                if (s.isEmpty()) {
                    s = rp.getParametro() + " AS " + rp.getApelido();
                } else {
                    s += ", " + " \n " + rp.getParametro() + " AS " + rp.getApelido();
                }
            }
            textQuery += s;
        }
        textQuery += " \n ";
        if (!listaRelatorioJoin.isEmpty()) {
            String j = "";
            for (RelatorioJoin rj : listaRelatorioJoin) {
                j += " " + rj.getJoin() + " \n ";
            }
            textQuery += j;
        }

        textQuery += " FROM movimentos_vw AS m \n";

        if (!listaRelatorioGrupo.isEmpty()) {
            String g = "";
            for (RelatorioGrupo rg : listaRelatorioGrupo) {
                if (g.isEmpty()) {
                    g = rg.getGrupo();
                } else {
                    g += ", " + " \n " + rg.getGrupo();
                }
            }
            textQuery += " GROUP BY \n ";
            textQuery += g;
        }

        textQuery += " \n ";

        List<RelatorioOrdem> list = new RelatorioOrdemDao().findAllByRelatorio(relatorio.getId());
        if (list.size() == 1) {
            textQuery += " ORDER BY \n ";
            textQuery += list.get(0).getQuery();
        }

        PF.openDialog("dlg_ver_query");
        PF.update("form_relatorio:panel_ver_query");
    }

    public void adicionarRelatorioJoin() {
        if (relatorioJoin.getJoin().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Join não pode ser vazio!");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        relatorioJoin.setRelatorio(relatorio);

        if (!dao.save(relatorioJoin)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Join!");
            return;
        }

        dao.commit();

        relatorioJoin = new RelatorioJoin();
        loadListaRelatorioJoin();
        GenericaMensagem.info("Sucesso", "Join Adicionado!");
    }

    public void adicionarRelatorioGrupo() {
        if (relatorioGrupo.getGrupo().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Grupo não pode ser vazio!");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        relatorioGrupo.setRelatorio(relatorio);

        if (!dao.save(relatorioGrupo)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Grupo!");
            return;
        }

        dao.commit();

        relatorioGrupo = new RelatorioGrupo();
        loadListaRelatorioGrupo();
        GenericaMensagem.info("Sucesso", "Grupo Adicionado!");
    }

    public void adicionarRelatorioParametro() {
        if (relatorioParametros.getApelido().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Apelido do Campo não pode ser vazio!");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        relatorioParametros.setRelatorio(relatorio);

        if (!dao.save(relatorioParametros)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Campo!");
            return;
        }

        dao.commit();

        relatorioParametros = new RelatorioParametros();
        loadListaRelatorioParametro();
        GenericaMensagem.info("Sucesso", "Campo Adicionado!");
    }

    public void excluirRelatorioJoin(RelatorioJoin rj) {
        Dao dao = new Dao();

        dao.openTransaction();
        rj = (RelatorioJoin) dao.find(rj);

        if (!dao.delete(rj)) {
            GenericaMensagem.error("Erro", "Não foi possível excluir Join!");
            return;
        }

        dao.commit();
        loadListaRelatorioJoin();
        GenericaMensagem.info("Sucesso", "Join Excluído!");
    }

    public void excluirRelatorioGrupo(RelatorioGrupo rg) {
        Dao dao = new Dao();

        dao.openTransaction();
        rg = (RelatorioGrupo) dao.find(rg);

        if (!dao.delete(rg)) {
            GenericaMensagem.error("Erro", "Não foi possível excluir Campo!");
            return;
        }

        dao.commit();
        loadListaRelatorioGrupo();
        GenericaMensagem.info("Sucesso", "Grupo Excluído!");
    }

    public void excluirRelatorioParametro(RelatorioParametros rp) {
        Dao dao = new Dao();

        dao.openTransaction();
        rp = (RelatorioParametros) dao.find(rp);

        if (!dao.delete(rp)) {
            GenericaMensagem.error("Erro", "Não foi possível excluir Campo!");
            return;
        }

        dao.commit();
        loadListaRelatorioParametro();
        GenericaMensagem.info("Sucesso", "Campo Excluído!");
    }

    public void loadListaRelatorioJoin() {
        getListaRelatorioJoin().clear();

        RelatorioDao dao = new RelatorioDao();

        setListaRelatorioJoin(dao.listaRelatorioJoin(relatorio.getId()));
    }

    public void loadListRelatorioTipo() {
        listRelatorioTipo = new ArrayList();
        List<RelatorioTipo> list = new Dao().list(new RelatorioTipo(), true);
        relatorio_tipo_id = 1;
        for (int i = 0; i < list.size(); i++) {
            listRelatorioTipo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListRotinas() {
        listRotina = new ArrayList();
        List<Rotina> list = new Dao().list(new Rotina(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                rotina_id = list.get(i).getId();
            }
            listRotina.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
        }
    }

    public void loadListClientes() {
        String sessaoCliente = GenericaSessao.getString("sessaoCliente");
        listClientes = new ArrayList();
        if (sessaoCliente.equals("Sindical") || sessaoCliente.equals("ComercioLimeira") || sessaoCliente.equals("Rtools")) {
            listClientes.add(new SelectItem("default", "Padrão"));
        }
        if (sessaoCliente.equals("Rtools")) {
            List<Configuracao> list = new ConfiguracaoDao().listAllActives();
            cliente = "default";
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getNotificacoes()) {
                    listClientes.add(new SelectItem(list.get(i).getIdentifica(), list.get(i).getIdentifica()));
                }
            }
        } else {
            cliente = sessaoCliente;
            listClientes.add(new SelectItem(sessaoCliente, sessaoCliente));
        }
    }

    public void loadListaRelatorioGrupo() {
        getListaRelatorioGrupo().clear();

        RelatorioDao dao = new RelatorioDao();

        setListaRelatorioGrupo(dao.listaRelatorioGrupo(relatorio.getId()));
    }

    public void loadListaRelatorioParametro() {
        listaRelatorioParametro.clear();

        RelatorioDao dao = new RelatorioDao();

        listaRelatorioParametro = dao.listaRelatorioParametro(relatorio.getId());
    }

    public void addRelatorioOrdem() {
        if (relatorioOrdem.getNome().isEmpty() || relatorioOrdem.getQuery().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar descrição e query!");
            return;
        }
        Dao dao = new Dao();
        Boolean sucess = false;
        String message;
        if (relatorioOrdem.getId() == null) {
            relatorioOrdem.setRelatorios(relatorio);
            if (dao.save(relatorioOrdem, true)) {
                sucess = true;
                message = "Registro inserido";
            } else {
                message = "Ao inserir registro!";
            }
        } else if (dao.update(relatorioOrdem, true)) {
            sucess = true;
            message = "Registro atualizado";
        } else {
            message = "Ao atualizar registro!";
        }
        if (!defaultRelatorioOrdem(relatorioOrdem)) {
            sucess = false;
            message = "Ao definir default!";
        }
        if (sucess) {
            GenericaMensagem.info("Sucesso", message);
            relatorioOrdem = new RelatorioOrdem();
            listRelatorioOrdem.clear();
        } else {
            GenericaMensagem.warn("Erro", message);

        }
    }

    public Boolean defaultRelatorioOrdem(RelatorioOrdem ro) {
        if (new RelatorioOrdemDao().defineDefault(ro)) {
            return true;
        }
        return false;
    }

    public void defaultOrdem(RelatorioOrdem ro) {
        if (defaultRelatorioOrdem(ro)) {
            listRelatorioOrdem.clear();
            getListRelatorioOrdem();
        }
    }

    public void deleteRelatorioOrdem(RelatorioOrdem ro) {
        Dao dao = new Dao();
        if (dao.delete(ro, true)) {
            GenericaMensagem.info("Sucesso", "Registro excluído");
            relatorioOrdem = new RelatorioOrdem();
            listRelatorioOrdem.clear();
        } else {
            GenericaMensagem.warn("Erro", "Ao excluir registro!");
        }
    }

    public void editRelatorioOrdem(RelatorioOrdem ro) {
        relatorioOrdem = ro;
    }

    public void save() {
        if (relatorio.getNome().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite uma descrição!");
            return;
        }

        if (relatorio.getJasper().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite um caminho para o Jasper!");
            return;
        }

        Dao dao = new Dao();
        NovoLog log = new NovoLog();

        relatorio.setRotina((Rotina) dao.find(new Rotina(), rotina_id));
        relatorio.setRelatorioTipo((RelatorioTipo) dao.find(new RelatorioTipo(), relatorio_tipo_id));

        dao.openTransaction();
        if (relatorio.getId() == -1) {
            if (dao.save(relatorio)) {
                GenericaMensagem.info("Sucesso!", "Registro inserido");
                // new RotinaContadorDao().incrementar(RotinaBean.getRotinaAtual().getId(), relatorio.getRotina().getId(), ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
                log.save("Relatório inserido " + relatorio.getId() + " - " + relatorio.getNome() + " / " + relatorio.getJasper());
            } else {
                GenericaMensagem.warn("Erro", "Ao salvar registro!");
                dao.rollback();
                return;
            }
        } else {
            Relatorios rel = (Relatorios) dao.find(new Relatorios(), relatorio.getId());
            String antes = "De: " + rel.getNome() + " / " + relatorio.getNome() + " -  " + rel.getJasper() + " / " + relatorio.getJasper();
            if (dao.update(relatorio)) {
                GenericaMensagem.info("Sucesso!", "Registro atualizado");
                log.update(antes, relatorio.getId() + " - " + relatorio.getNome() + " / " + relatorio.getJasper());
                //new RotinaContadorDao().incrementar(RotinaBean.getRotinaAtual().getId(), relatorio.getRotina().getId(), ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
                dao.rollback();
                return;
            }
        }
        listRelatorio.clear();
        dao.commit();
    }

    public void clear() {
        clear(0);
    }

    public void clear(Integer tcase) {
        if (tcase == 0) {
            GenericaSessao.remove("relatorioBean");
        } else if (tcase == 1) {
            relatorio = new Relatorios();
            listRelatorioOrdem.clear();
            relatorioOrdem = new RelatorioOrdem();
            listRelatorio.clear();
            rotina_id = -1;
        }
    }

    public void delete() {
        if (relatorio.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquise um relatório para exclusão!");
            return;
        }

        Dao dao = new Dao();
        NovoLog log = new NovoLog();
        dao.openTransaction();

        if (dao.delete(relatorio)) {
            GenericaMensagem.info("Sucesso!", "Registro excluído");
            log.delete(relatorio.getId() + " - " + relatorio.getNome() + " / " + relatorio.getJasper());
        } else {
            GenericaMensagem.warn("Erro", "Erro ao excluir registro!");
            dao.rollback();
        }

        dao.commit();
        relatorio = new Relatorios();
        listRelatorio.clear();
        rotina_id = 0;
    }

    public String edit(Relatorios r) {
        this.relatorio = r;
        GenericaSessao.put("linkClicado", true);
        loadListRotinas();
        listRelatorioOrdem.clear();
        relatorioOrdem = new RelatorioOrdem();
        rotina_id = relatorio.getRotina().getId();
        relatorio_tipo_id = relatorio.getRelatorioTipo().getId();
        loadListaRelatorioParametro();
        loadListaRelatorioGrupo();
        loadListaRelatorioJoin();
        return "relatorio";
    }

    public Relatorios getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorios relatorio) {
        this.relatorio = relatorio;
    }

    public List<SelectItem> getListRotina() {
        return listRotina;
    }

    public void setListRotina(List<SelectItem> listRotina) {
        this.listRotina = listRotina;
    }

    public Integer getRotina_id() {
        return rotina_id;
    }

    public void setRotina_id(Integer rotina_id) {
        this.rotina_id = rotina_id;
    }

    public List<Relatorios> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<Relatorios> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public void loadList() {
        find();
    }

    public void find() {
        listRelatorio = new ArrayList();
        if (rotina_pesquisa_id == null && description.isEmpty()) {
            return;
        }
        listRelatorio = new RelatorioDao().find(rotina_pesquisa_id, description);
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

    public List<RelatorioOrdem> getListRelatorioOrdem() {
        if (relatorio.getId() != -1) {
            if (listRelatorioOrdem == null || listRelatorioOrdem.isEmpty()) {
                RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
                listRelatorioOrdem = relatorioOrdemDao.findAllByRelatorio(relatorio.getId());
                for (int i = 0; i < listRelatorioOrdem.size(); i++) {
                    listRelatorioOrdem.set(i, (RelatorioOrdem) new Dao().rebind(listRelatorioOrdem.get(i)));
                }
            }
        }
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<RelatorioOrdem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    /**
     * 0 - Ultimas usadas 1 - Mais usadas
     *
     * @param tcase
     */
    public void loadRotinaCombo(Integer tcase) {
        List<Rotina> list = new ArrayList();
        //RotinaContadorDao rcd = new RotinaContadorDao();
        if (tcase == 0) {
            //rcd.orderData();
            // list = rcd.findRotinasByRotinaTela(RotinaBean.getRotinaAtual().getId(), ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
        } else if (tcase == 1) {
            //rcd.orderContador();
            // list = rcd.findRotinasByRotinaTela(RotinaBean.getRotinaAtual().getId(), ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
        }
        if (!list.isEmpty()) {
            listRotina.clear();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    rotina_id = list.get(i).getId();
                }
                listRotina.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        }
    }

    /**
     * 1 - Update no relatório default para a rotina
     *
     * @param tcase
     */
    public void listener(Integer tcase) {
        // 1 - Update no relatório default para a rotina
        switch (tcase) {
            case 1:
                if (new RelatorioDao().defineDefault(relatorio)) {
                    GenericaMensagem.info("Sucesso", "Definido como default desta rotina");
                    relatorio = (Relatorios) new Dao().rebind(relatorio);
                } else {
                    GenericaMensagem.warn("Erro", "Ao definir como default!");
                }
                break;
        }
    }

    public RelatorioParametros getRelatorioParametros() {
        return relatorioParametros;
    }

    public void setRelatorioParametros(RelatorioParametros relatorioParametros) {
        this.relatorioParametros = relatorioParametros;
    }

    public List<RelatorioParametros> getListaRelatorioParametro() {
        return listaRelatorioParametro;
    }

    public void setListaRelatorioParametro(List<RelatorioParametros> listaRelatorioParametro) {
        this.listaRelatorioParametro = listaRelatorioParametro;
    }

    public RelatorioGrupo getRelatorioGrupo() {
        return relatorioGrupo;
    }

    public void setRelatorioGrupo(RelatorioGrupo relatorioGrupo) {
        this.relatorioGrupo = relatorioGrupo;
    }

    public List<RelatorioGrupo> getListaRelatorioGrupo() {
        return listaRelatorioGrupo;
    }

    public void setListaRelatorioGrupo(List<RelatorioGrupo> listaRelatorioGrupo) {
        this.listaRelatorioGrupo = listaRelatorioGrupo;
    }

    public RelatorioJoin getRelatorioJoin() {
        return relatorioJoin;
    }

    public void setRelatorioJoin(RelatorioJoin relatorioJoin) {
        this.relatorioJoin = relatorioJoin;
    }

    public List<RelatorioJoin> getListaRelatorioJoin() {
        return listaRelatorioJoin;
    }

    public void setListaRelatorioJoin(List<RelatorioJoin> listaRelatorioJoin) {
        this.listaRelatorioJoin = listaRelatorioJoin;
    }

    public String getTextQuery() {
        return textQuery;
    }

    public void setTextQuery(String textQuery) {
        this.textQuery = textQuery;
    }

    public Integer getRotina_pesquisa_id() {
        return rotina_pesquisa_id;
    }

    public void setRotina_pesquisa_id(Integer rotina_pesquisa_id) {
        this.rotina_pesquisa_id = rotina_pesquisa_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SelectItem> getListRelatorioTipo() {
        return listRelatorioTipo;
    }

    public void setListRelatorioTipo(List<SelectItem> listRelatorioTipo) {
        this.listRelatorioTipo = listRelatorioTipo;
    }

    public Integer getRelatorio_tipo_id() {
        return relatorio_tipo_id;
    }

    public void setRelatorio_tipo_id(Integer relatorio_tipo_id) {
        this.relatorio_tipo_id = relatorio_tipo_id;
    }

    public void uploadJasper(FileUploadEvent event) {
        upload(event, "jasper");
    }

    public void upload(FileUploadEvent event, String tcase) {
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        List extensions = new ArrayList();
        if (tcase.equals("jasper")) {
            extensions.add("jasper");
            configuracaoUpload.setTiposPermitidos(extensions);
        }
        if (!event.getFile().getFileName().equals(relatorio.getJasperName())) {
            PF.update("form_relatorio");
            Messages.warn("Validação", "Arquivo enviado tem nome diferente do nome do JASPER!!!");
            return;
        }
        if (cliente.equals("default")) {
            configuracaoUpload.setCliente(null);
            configuracaoUpload.setFolder("Relatorios");
        } else {
            configuracaoUpload.setCliente(cliente);
            configuracaoUpload.setDiretorio("Relatorios");
        }
        configuracaoUpload.setEvent(event);
        configuracaoUpload.setSubstituir(true);
        configuracaoUpload.setResourceFolder(false);
        if (Upload.enviar(configuracaoUpload, false)) {
            // listFiles.clear();
        }
        // getListFiles();
    }

    public void remove() {
        if (getModelo().contains("Personalizado")) {
            String sessaoCliente = GenericaSessao.getString("sessaoCliente");
            String c = sessaoCliente;
            if (sessaoCliente.equals("Rtools")) {
                c = cliente;
            }
            String diretorio = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + c + "/" + "Relatorios" + "/" + relatorio.getJasperName());
            if (new File(diretorio).exists()) {
                new File(diretorio).delete();
                Messages.info("Sucesso", "Arquivo removido!!!");
                return;
            }
        }
        Messages.warn("Importante", "Não é possível remover o arquivo principal!!!");
    }

    public String getModelo() {
        return relatorio.getModelo(cliente);
    }

    public List<SelectItem> getListClientes() {
        return listClientes;
    }

    public void setListClientes(List<SelectItem> listClientes) {
        this.listClientes = listClientes;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

}
