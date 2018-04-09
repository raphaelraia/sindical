package br.com.rtools.sql.beans;

import br.com.rtools.principal.DB;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.conf.DataBase;
import br.com.rtools.sql.AtualizarBase;
import br.com.rtools.sql.AtualizarBaseCliente;
import br.com.rtools.sql.dao.AtualizarBaseClienteDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @PostConstruct
    public void init() {
        listAtualizarBase = new ArrayList<>();
        atualizarBase = new AtualizarBase();
        selected = new ArrayList();
        updateScript = false;
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
        if (atualizarBase.getScript().isEmpty()) {
            GenericaMensagem.info("Validação", "Cadastrar script!");
            return;
        }
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
        updateScript = true;
        selected = new ArrayList();
        selectedConfiguracao = null;
        atualizarBase = (AtualizarBase) new Dao().rebind(ab);
        atualizarBase.setScript(atualizarBase.getScript().replace("'''", "'"));
        GenericaSessao.put("linkClicado", true);
        loadListAtualizarBase();
        loadListAtualizarBaseCliente();
        loadListConfiguracao();
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
            GenericaMensagem.warn("Erro", "Salvar o script!!!");
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
        PreparedStatement ps;
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

                    if (dbe.getConnection(true) != null) {
                        ps = dbe.getConnection().prepareStatement(atualizarBase.getScript());
                        ps.executeUpdate();
                        GenericaMensagem.info((i + 1) + " - " + selected.get(i).getCliente().getIdentifica(), "Script executado!!!");
                        selected.get(i).setDtAtualizacao(new Date());
                        selected.get(i).setUsuario(Usuario.getUsuario());
                        dao.update(selected.get(i), true);
//                        if (ps.executeUpdate() > 0) {
//                        } else {
//                            success = false;
//                            GenericaMensagem.warn((i + 1) + " - " + selected.get(i).getCliente().getIdentifica(), "Script falhou!!!");
//                        }
                        dbe.closeStatment();
                    }
                } catch (SQLException e) {
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
        listAtualizarBase = new Dao().list(new AtualizarBase());
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
        if (listAtualizarBase.isEmpty()) {
            listAtualizarBase = new Dao().list(new AtualizarBase());
        }
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
            if(list.get(i).getAtivo()) {
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

}
