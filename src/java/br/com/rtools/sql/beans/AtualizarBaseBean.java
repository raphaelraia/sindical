package br.com.rtools.sql.beans;

import br.com.rtools.principal.DB;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.conf.DataBase;
import br.com.rtools.sql.AtualizarBase;
import br.com.rtools.sql.AtualizarBaseCliente;
import br.com.rtools.sql.AtualizarBaseScript;
import br.com.rtools.sql.SqlEvents;
import br.com.rtools.sql.dao.AtualizarBaseClienteDao;
import br.com.rtools.sql.dao.AtualizarBaseDao;
import br.com.rtools.sql.dao.AtualizarBaseScriptDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@ManagedBean
@SessionScoped
public class AtualizarBaseBean implements Serializable {

    private AtualizarBase atualizarBase;
    private List<AtualizarBase> listAtualizarBase;
    private AtualizarBaseCliente atualizarBaseCliente;
    private List<AtualizarBaseCliente> listAtualizarBaseCliente;
    private Map<String, Integer> listConfiguracao;
    private List selectedConfiguracao;
    private List<AtualizarBaseCliente> selected;
    private Boolean updateScript;
    private List<SelectItem> listSqlEvent;
    private Integer idSqlEvent;
    private List<AtualizarBaseScript> listAtualizarBaseScripts;
    private AtualizarBaseScript atualizarBaseScript;

    @PostConstruct
    public void init() {
        listAtualizarBase = new ArrayList<>();
        atualizarBase = new AtualizarBase();
        atualizarBaseScript = new AtualizarBaseScript();
        selected = new ArrayList();
        listAtualizarBaseScripts = new ArrayList();
        listSqlEvent = new ArrayList();
        updateScript = false;
        idSqlEvent = 6;
        loadListSqlEvent();
        loadListAtualizarBase();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("atualizarBaseBean");
    }

    public void listener(Integer tcase) {
        if (tcase == 1) {
            GenericaSessao.remove("atualizarBaseBean");
        }

    }

    public void save() {
        selected = new ArrayList();
        Dao dao = new Dao();
        atualizarBase.setScript(atualizarBase.getScript().replace("'", "'''"));
        if (atualizarBase.getId() == null) {
            atualizarBase.setUsuario(Usuario.getUsuario());
            if (new Dao().save(atualizarBase, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListAtualizarBase();
                loadListConfiguracao();
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else if (dao.update(atualizarBase, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            loadListAtualizarBase();
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
        updateScript = false;
        loadListAtualizarBase();
    }

    public void delete() {
        if (new Dao().delete(atualizarBase, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            atualizarBase = new AtualizarBase();
            for (int i = 0; i < listAtualizarBaseCliente.size(); i++) {
                new Dao().delete(listAtualizarBaseCliente.get(i), true);
            }
            loadListAtualizarBase();
            loadListAtualizarBaseCliente();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void editScript() {
        atualizarBase.setScript(atualizarBase.getScript().replace("'''", "'"));
        updateScript = true;
    }

    public String edit(AtualizarBase ab) {
        atualizarBaseScript = new AtualizarBaseScript();
        updateScript = true;
        selected = new ArrayList();
        selectedConfiguracao = null;
        atualizarBase = (AtualizarBase) new Dao().rebind(ab);
        atualizarBase.setScript(atualizarBase.getScript().replace("'''", "'"));
        GenericaSessao.put("linkClicado", true);
        loadListAtualizarBase();
        loadListAtualizarBaseCliente();
        loadListConfiguracao();
        loadListAtualizarBaseScript();
        return "atualizarBase";

    }

    public void add() {
        if (selectedConfiguracao != null && !selectedConfiguracao.isEmpty()) {
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < selectedConfiguracao.size(); i++) {
                Configuracao configuracao = (Configuracao) dao.find(new Configuracao(), Integer.parseInt(selectedConfiguracao.get(i).toString()));
                AtualizarBaseCliente abc = new AtualizarBaseCliente();
                boolean add = true;
                if (listAtualizarBaseCliente != null) {
                    for (int x = 0; x < listAtualizarBaseCliente.size(); x++) {
                        if (Objects.equals(listAtualizarBaseCliente.get(x).getCliente().getId(), configuracao.getId())) {
                            add = false;
                        }
                    }
                }
                if (add) {
                    abc.setAtualizarBase(atualizarBase);
                    abc.setCliente(configuracao);
                    if (!dao.save(abc)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "Ao criar configuração!!!");
                        return;
                    }
                }
            }
            GenericaMensagem.info("Sucesso", "Registro(s) inserido(s)");
            dao.commit();
            loadListAtualizarBaseCliente();
        }
    }

    public void run() {
        if (atualizarBase.getId() == null) {
            GenericaMensagem.warn("Erro", "Salvar o registro!!!");
            return;
        }
        if (updateScript) {
            GenericaMensagem.warn("Validação", "Salvar antes de executar o script!!!");
            return;
        }
        if (listAtualizarBaseCliente.isEmpty()) {
            GenericaMensagem.warn("Erro", "Cadastrar clientes!!!");
            return;
        }
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Erro", "Selecionar clientes!!!");
            return;
        }
        if (atualizarBase.getDtProcessamento() != null) {
            GenericaMensagem.warn("Sistema", "Este script já foi executado em um ou mais clientes!!!");
        }
        Dao dao = new Dao();
        DB db = new DB();
        boolean success = true;
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getDtAtualizacao() == null) {
                DBExternal dbe = new DBExternal();
                try {
                    DataBase dataBase = new DataBase();
                    Configuracao c = db.servidor(selected.get(i).getCliente().getIdentifica());
                    dataBase.loadJson(selected.get(i).getCliente().getIdentifica());
                    Integer port = 5432;
                    String password = c.getSenha();
                    if (password.isEmpty()) {
                        password = "";
                    }
                    if (!dataBase.getHost().isEmpty()) {
                        c.setHost(dataBase.getHost());
                    }
                    if (dataBase.getPort() != null && dataBase.getPort() != 0) {
                        port = dataBase.getPort();
                    }
                    if (!dataBase.getDatabase().isEmpty()) {
                        c.setPersistence(dataBase.getDatabase());
                    }
                    if (!dataBase.getPassword().isEmpty()) {
                        password = dataBase.getPassword();
                    }

                    String user = dataBase.getUser();
                    if (user.isEmpty()) {
                        user = "postgres";
                    }

                    dbe.setDatabase(c.getPersistence());
                    dbe.setPort(port + "");
                    dbe.setUrl(c.getHost());
                    dbe.setUser(user);
                    dbe.setPassword(password);
                    dbe.setApplicationName("run scripts in database " + selected.get(i).getCliente().getIdentifica());
                    GenericaMensagem.info((i + 1) + " " + selected.get(i).getCliente().getIdentifica(), "Sucesso!!!");
                    for (int z = 0; z < listAtualizarBaseScripts.size(); z++) {
                        Connection conn = null;
                        PreparedStatement ps = null;
                        String history = "";
                        try {
                            conn = dbe.getConnection(true);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            conn.setAutoCommit(true);
                            String script = listAtualizarBaseScripts.get(z).getScript().replace("'''", "'");
                            // Statement statement = conn.createStatement();
                            ps = conn.prepareStatement(script);
                            // ps = dbe.getConnection().prepareStatement(script);
                            if (listAtualizarBaseScripts.get(z).getSqlEvents().getNrCase() == 2) {
                                ps.executeQuery();
                            } else {
                                ps.executeUpdate();
                            }
                            if (script.length() > 100) {
                                history = (z + 1) + " " + script.substring(0, 100) + "...; ";
                            } else {
                                history = (z + 1) + " " + script + "...; ";
                            }
                            GenericaMensagem.info((i + 1) + "." + (z + 1) + " - " + listAtualizarBaseScripts.get(z).getSqlEvents().getDescricao(), history);
                            selected.get(i).setDtAtualizacao(new Date());
                            selected.get(i).setUsuario(Usuario.getUsuario());
                            dao.update(selected.get(i), true);
                        } catch (SQLException e) {
                            success = false;
                            Messages.warn((z + 1) + " - " + listAtualizarBaseScripts.get(z).getSqlEvents().getDescricao(), "Exceção: " + e.getMessage() + history);
                            break;
                        } finally {
                            if (ps != null) {
                                try {
                                    ps.close();
                                } catch (SQLException e) {
                                    /* ignored */
                                }
                            }
                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (SQLException e) {
                                    /* ignored */
                                }
                            }
                        }
                        Thread.sleep(500);
                    }
                    GenericaMensagem.info("________________________________________________________________________", "");
                } catch (Exception e) {
                    success = false;
                    Messages.warn((i + 1) + " - " + selected.get(i).getCliente().getIdentifica(), "Exceção: " + e.getMessage());
                    break;
                }
            } else {
                GenericaMensagem.info((i + 1) + " - " + selected.get(i).getCliente().getIdentifica(), "Já foi executado!!!");
            }

        }
        if (success) {
            atualizarBase.setDtProcessamento(new Date());
            dao.update(atualizarBase, true);
        }
        loadListAtualizarBaseCliente();

    }

    public void remove(AtualizarBaseCliente abc) {
        if (new Dao().delete(abc, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            loadListAtualizarBaseCliente();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void loadListAtualizarBase() {
        listAtualizarBase = new ArrayList();
        listAtualizarBase = new AtualizarBaseDao().find();
    }

    public void loadListAtualizarBaseCliente() {
        selected = new ArrayList();
        listAtualizarBaseCliente = new ArrayList();
        if (atualizarBase.getId() != null) {
            listAtualizarBaseCliente = new AtualizarBaseClienteDao().find(atualizarBase.getId());

        }

    }

    public AtualizarBase getAtualizarBase() {
        return atualizarBase;
    }

    public void setAtualizarBase(AtualizarBase atualizarBase) {
        this.atualizarBase = atualizarBase;
    }

    public List<AtualizarBase> getListAtualizarBase() {
        return listAtualizarBase;
    }

    public void setListAtualizarBase(List<AtualizarBase> listAtualizarBase) {
        this.listAtualizarBase = listAtualizarBase;
    }

    public AtualizarBaseCliente getAtualizarBaseCliente() {
        return atualizarBaseCliente;
    }

    public void setAtualizarBaseCliente(AtualizarBaseCliente atualizarBaseCliente) {
        this.atualizarBaseCliente = atualizarBaseCliente;
    }

    public List<AtualizarBaseCliente> getListAtualizarBaseCliente() {
        return listAtualizarBaseCliente;
    }

    public void setListAtualizarBaseCliente(List<AtualizarBaseCliente> listAtualizarBaseCliente) {
        this.listAtualizarBaseCliente = listAtualizarBaseCliente;
    }

    public void loadListConfiguracao() {
        listConfiguracao = new LinkedHashMap();
        selectedConfiguracao = null;
        List<Configuracao> list = new Dao().list(new Configuracao(), true);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAtivo() && list.get(i).getExecutaScripts()) {
                listConfiguracao.put(list.get(i).getIdentifica(), list.get(i).getId());
            }
        }
    }

    public Map<String, Integer> getListConfiguracao() {
        return listConfiguracao;
    }

    public void setListConfiguracao(Map<String, Integer> listConfiguracao) {
        this.listConfiguracao = listConfiguracao;
    }

    public List getSelectedConfiguracao() {
        return selectedConfiguracao;
    }

    public void setSelectedConfiguracao(List selectedConfiguracao) {
        this.selectedConfiguracao = selectedConfiguracao;
    }

    public void onRowSelect(SelectEvent event) {
        // List list = ((List) event.getObject());
//         if (new Registro().isCobrancaCarteirinha()) {
//            if (!status.equals("nao_impressos")) {
//                String validadeCarteirinha = list.get(6).toString();
//                if (DataHoje.maiorData(DataHoje.data(), validadeCarteirinha)) {
//                    GenericaMensagem.warn("SISTEMA", "CARTÃO ENCONTRA-SE VENCIDO! GERAR UMA NOVA VIA");
//                    return;
//                }
//            }             
//         }
        //selected.add(((AtualizarBaseCliente) event.getObject()));
    }

    public void onRowUnselect(UnselectEvent event) {
        // selected.remove((List) event.getObject());
//        for (int i = 0; i < listaSelecionado.size(); i++) {
//            if (((List) listaSelecionado.get(i)).get(0) == ((List) event.getObject()).get(0)) {
//                listaSelecionado.remove(i);
//                break;
//            }
//        }
    }

    public List<AtualizarBaseCliente> getSelected() {
        return selected;
    }

    public void setSelected(List<AtualizarBaseCliente> selected) {
        this.selected = selected;
    }

    public Boolean getUpdateScript() {
        return updateScript;
    }

    public void setUpdateScript(Boolean updateScript) {
        this.updateScript = updateScript;
    }

    private void loadListSqlEvent() {
        List<SqlEvents> list = new Dao().list(new SqlEvents());
        for (int i = 0; i < list.size(); i++) {
            listSqlEvent.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<SelectItem> getListSqlEvent() {
        return listSqlEvent;
    }

    public void setListSqlEvent(List<SelectItem> listSqlEvent) {
        this.listSqlEvent = listSqlEvent;
    }

    public Integer getIdSqlEvent() {
        return idSqlEvent;
    }

    public void setIdSqlEvent(Integer idSqlEvent) {
        this.idSqlEvent = idSqlEvent;
    }

    public List<AtualizarBaseScript> getListAtualizarBaseScripts() {
        return listAtualizarBaseScripts;
    }

    public void setListAtualizarBaseScripts(List<AtualizarBaseScript> listAtualizarBaseScripts) {
        this.listAtualizarBaseScripts = listAtualizarBaseScripts;
    }

    public void loadListAtualizarBaseScript() {
        listAtualizarBaseScripts = new ArrayList();
        if (atualizarBase.getId() != null) {
            listAtualizarBaseScripts = new AtualizarBaseScriptDao().find(atualizarBase.getId());
        }
    }

    public void storeScript() {
        Dao dao = new Dao();
        if (atualizarBaseScript.getScript().isEmpty()) {
            Messages.warn("Validação", "Script vazio!!!");
            return;
        }
        if (idSqlEvent != 2) {
            if (atualizarBaseScript.getScript().toUpperCase().contains("SELECT")) {
                Messages.warn("Importação", "Podem ocorrer erros se o evento não for para SELECT!!!!");
            }
        }
        atualizarBaseScript.setScript(atualizarBaseScript.getScript().replace("'", "'''"));
        if (atualizarBaseScript.getId() == null) {
            atualizarBaseScript.setSqlEvents((SqlEvents) dao.find(new SqlEvents(), idSqlEvent));
            atualizarBaseScript.setAtualizarBase(atualizarBase);
            atualizarBaseScript.setUsuario(Usuario.getUsuario());
            atualizarBaseScript.setDtCadastro(new Date());
            new Dao().save(atualizarBaseScript, true);
        } else {
            new Dao().update(atualizarBaseScript, true);
        }
        atualizarBaseScript = new AtualizarBaseScript();
        loadListAtualizarBaseScript();
    }

    public void removeScript(AtualizarBaseScript abs) {
        new Dao().delete(abs, true);
        atualizarBaseScript = new AtualizarBaseScript();
        loadListAtualizarBaseScript();
    }

    public void editScript(AtualizarBaseScript abs) {
        abs.getScript().replace("'''", "'");
        atualizarBaseScript = abs;
        updateScript = true;
    }

    public AtualizarBaseScript getAtualizarBaseScript() {
        return atualizarBaseScript;
    }

    public void setAtualizarBaseScript(AtualizarBaseScript atualizarBaseScript) {
        this.atualizarBaseScript = atualizarBaseScript;
    }

    public void rollback(AtualizarBaseCliente abc) {
        abc.setDtAtualizacao(null);
        new Dao().update(abc, true);
        loadListAtualizarBaseCliente();
    }

}
