package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.AutorizaImpressaoCartaoDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.impressao.CartaoSocial;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class AutorizaCarteirinhaBean {

    private Usuario usuario = new Usuario();
    private Fisica fisica = new Fisica();
    private AutorizaImpressaoCartao impressaoCartao = new AutorizaImpressaoCartao();
    private int idModelo = 0;
    private List<SelectItem> listaModelo = new ArrayList<SelectItem>();
    private List<AutorizaImpressaoCartao> listaAutorizacao = new ArrayList<AutorizaImpressaoCartao>();
    private String status;
    private String filter;
    private String query;
    private Integer idOperador;
    private List<SelectItem> listOperador;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private Boolean listEmpty;

    public AutorizaCarteirinhaBean() {
        if (GenericaSessao.getObject("sessaoUsuario") != null) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }

        impressaoCartao.setFoto(((Registro) new Dao().find(new Registro(), 1)).isFotoCartao());
        status = "hoje";
        filter = "";
        query = "";
        listOperador = new ArrayList();
        loadOperador();
        typeDate = "todos";
        startDate = "";
        finishDate = "";
        listEmpty = null;
        loadListAutorizacao();
    }

    public void autorizar() {

        if (Integer.valueOf(listaModelo.get(0).getDescription()) == 0) {
            GenericaMensagem.warn("Erro", "Nenhum Modelo de carteirinha encontrado!");
            return;
        }

        Dao dao = new Dao();
        SocioCarteirinhaDao db = new SocioCarteirinhaDao();
        ModeloCarteirinha mc = (ModeloCarteirinha) dao.find(new ModeloCarteirinha(), Integer.valueOf(listaModelo.get(idModelo).getDescription()));
        dao.openTransaction();
        if (db.listaSocioCarteirinhaAutoriza(fisica.getPessoa().getId(), Integer.valueOf(listaModelo.get(idModelo).getDescription())).isEmpty()) {
            Socios s = fisica.getPessoa().getSocios();
            if (s != null && s.getId() != -1) {
                GenericaMensagem.warn("Erro", "Esta Pessoa NÃO POSSUI carteirinha para ser autorizada!");
                return;
            }
            SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
            socioCarteirinha.setPessoa(fisica.getPessoa());
            socioCarteirinha.setVia(1);
            socioCarteirinha.setAtivo(true);
            socioCarteirinha.setValidadeCarteirinha("01/01/2050");
            socioCarteirinha.setModeloCarteirinha(mc);
            if (!dao.save(socioCarteirinha)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao salvar sócio carteirinha!");
                return;
            }
            // return;
        }

        if (!db.listaAutoriza(fisica.getPessoa().getId(), Integer.valueOf(listaModelo.get(idModelo).getDescription())).isEmpty()) {
            GenericaMensagem.warn("Erro", "Esta Pessoa já esta autorizada a imprimir carteirinha!");
            return;
        }

        impressaoCartao.setFoto(mc.getFotoCartao());
        impressaoCartao.setModeloCarteirinha(mc);
        impressaoCartao.setUsuario(usuario);
        impressaoCartao.setPessoa(fisica.getPessoa());

        if (!dao.save(impressaoCartao)) {
            GenericaMensagem.warn("Erro", "Não foi possível salvar autorização!");
            dao.rollback();
            return;
        }

        SocioCarteirinha socioCarteirinha = new SocioCarteirinhaDao().pesquisaPorPessoaModelo(impressaoCartao.getPessoa().getId(), impressaoCartao.getModeloCarteirinha().getId());
        if (socioCarteirinha != null) {
            socioCarteirinha.setDtEmissao(null);
            if (!dao.update(socioCarteirinha)) {
                GenericaMensagem.warn("Erro", "Ao atualizar sócio carteirinha!");
                dao.rollback();
                return;
            }
        }

        NovoLog novoLog = new NovoLog();
        String saveLog
                = "ID" + impressaoCartao.getId()
                + " - Pessoa {ID: " + impressaoCartao.getPessoa().getId() + " - Nome: " + impressaoCartao.getPessoa().getNome() + " }"
                + " - Autorizada por {ID: " + impressaoCartao.getUsuario().getPessoa().getId() + " - Nome: " + impressaoCartao.getUsuario().getPessoa().getNome() + " }"
                + " - Modelo {ID: " + impressaoCartao.getModeloCarteirinha().getId() + " - Modelo: " + impressaoCartao.getModeloCarteirinha().getDescricao() + " }";
        novoLog.setTabela("soc_autoriza_impressao_cartao");
        novoLog.setCodigo(impressaoCartao.getId());
        novoLog.save(saveLog);

        dao.commit();
        GenericaMensagem.info("Sucesso", "Autorização Concluída!");
        loadListAutorizacao();
    }

    public void excluir(AutorizaImpressaoCartao linha) {
        if (linha.getHistoricoCarteirinha() != null) {
            GenericaMensagem.warn("Erro", "Essa autorização não pode ser excluída! Histórico de impressão gerado.");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(linha)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Autorização não pode ser excluído!");
        } else {
            NovoLog novoLog = new NovoLog();
            String deleteLog
                    = "ID" + impressaoCartao.getId()
                    + " - Pessoa {ID: " + impressaoCartao.getPessoa().getId() + " - Nome: " + impressaoCartao.getPessoa().getNome() + " }"
                    + " - Autorizada por {ID: " + impressaoCartao.getUsuario().getPessoa().getId() + " - Nome: " + impressaoCartao.getUsuario().getPessoa().getNome() + " }"
                    + " - Modelo {ID: " + impressaoCartao.getModeloCarteirinha().getId() + " - Modelo: " + impressaoCartao.getModeloCarteirinha().getDescricao() + " }";
            novoLog.setTabela("soc_autoriza_impressao_cartao");
            novoLog.setCodigo(impressaoCartao.getId());
            novoLog.delete(deleteLog);
            GenericaMensagem.info("Sucesso", "Autorização Excluída!");
            dao.commit();
            loadListAutorizacao();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Fisica getFisica() {
        if (GenericaSessao.getObject("fisicaPesquisa") != null) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa");
            GenericaSessao.remove("fisicaPesquisa");
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public AutorizaImpressaoCartao getImpressaoCartao() {
        return impressaoCartao;
    }

    public void setImpressaoCartao(AutorizaImpressaoCartao impressaoCartao) {
        this.impressaoCartao = impressaoCartao;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    public List<SelectItem> getListaModelo() {
        if (listaModelo.isEmpty()) {
            List<ModeloCarteirinha> result = (new Dao()).list(new ModeloCarteirinha());

            if (!result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    listaModelo.add(new SelectItem(i, result.get(i).getDescricao(), String.valueOf(result.get(i).getId())));
                }
            } else {
                listaModelo.add(new SelectItem(0, "Nenhum Modelo Encontrado", "0"));
            }
        }
        return listaModelo;
    }

    public void setListaModelo(List<SelectItem> listaModelo) {
        this.listaModelo = listaModelo;
    }

    public List<AutorizaImpressaoCartao> getListaAutorizacao() {
        return listaAutorizacao;
    }

    public void setListaAutorizacao(List<AutorizaImpressaoCartao> listaAutorizacao) {
        this.listaAutorizacao = listaAutorizacao;
    }

    public void loadListAutorizacao() {
        if ((filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf")) && query.isEmpty()) {
            GenericaMensagem.warn("Validação", "Específicar um valor válido para o filtro selecionado!");
            return;
        }
        listaAutorizacao = new ArrayList();
        listaAutorizacao = new AutorizaImpressaoCartaoDao().find(status, filter, query, idOperador, typeDate, startDate, finishDate);
        if (listEmpty == null && listaAutorizacao.isEmpty()) {
            listEmpty = true;
            status = "emissao";
            typeDate = "todos";
            listaAutorizacao = new AutorizaImpressaoCartaoDao().find(status, filter, query, idOperador, typeDate, startDate, finishDate);
        }
    }

    public void loadOperador() {
        listOperador = new ArrayList();
        listOperador.add(new SelectItem(null, "TODOS"));
        idOperador = null;
        List<Usuario> list = new UsuarioDao().findByTabela("soc_autoriza_impressao_cartao");
        for (int i = 0; i < list.size(); i++) {
            listOperador.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(Integer idOperador) {
        this.idOperador = idOperador;
    }

    public List<SelectItem> getListOperador() {
        return listOperador;
    }

    public void setListOperador(List<SelectItem> listOperador) {
        this.listOperador = listOperador;
    }

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public void listener(String tcase) {
        if (tcase.equals("query")) {
            query = "";
        }
    }

    public String getMascaraAlteracao() {
        if (filter != null && !filter.isEmpty()) {
            String f = filter;
            return Mask.getMascaraPesquisa(f, true);
        }
        return "";
    }

    public Integer getSize() {
        if (filter != null && !filter.isEmpty()) {
            switch (filter) {
                case "nome":
                    return 500;
                case "cpf":
                    return 150;
                case "codigo":
                case "matricula":
                    return 80;
                case "nascimento":
                    return 100;
            }
        }
        return 50;
    }
}
