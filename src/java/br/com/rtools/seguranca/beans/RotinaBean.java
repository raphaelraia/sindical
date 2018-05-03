package br.com.rtools.seguranca.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.RotinaGrupo;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.seguranca.dao.RotinaGrupoDao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Defaults;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Https;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.json.JSONObject;

@ManagedBean
@SessionScoped
public class RotinaBean implements Serializable {

    private Rotina rotina;
    private String message;
    private String descricaoPesquisa;
    private List<Rotina> listRotina;
    private List<RotinaGrupo> listRotinaGrupo;
    private List<RotinaGrupo> listShowRotinas;
    private Boolean acao;
    private List<SelectItem> listRotinas;
    private Integer rotina_id;
    private String funcionamento;

    @PostConstruct
    public void init() {
        rotina = new Rotina();
        message = "";
        descricaoPesquisa = "";
        listRotina = new ArrayList<>();
        listRotinas = new ArrayList<>();
        listRotinaGrupo = new ArrayList<>();
        listShowRotinas = new ArrayList<>();
        acao = false;
        funcionamento = "";
        // find();        
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void save() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (rotina.getId() == -1) {
            if (rotina.getRotina().equals("")) {
                GenericaMensagem.warn("Validação", "Informe o nome da rotina!");
            } else {
                RotinaDao rotinaDao = new RotinaDao();
                if (!rotinaDao.existeRotina(rotina)) {
                    dao.openTransaction();
                    if (dao.save(rotina)) {
                        dao.commit();
                        novoLog.save("ID: " + rotina.getId() + " - Rotina: " + rotina.getRotina() + " - Página: " + rotina.getRotina() + " - Ativa: " + rotina.isAtivo());
                        GenericaMensagem.info("Sucesso", "Registro inserido");
                        find();
                    } else {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "Ao inserir registro!");
                    }
                } else {
                    GenericaMensagem.warn("Validação", "Rotina já existe!");
                }
            }
        } else {
            Rotina r = (Rotina) dao.find(rotina);
            String beforeUpdate = "ID: " + r.getId() + " - Rotina: " + r.getRotina() + " - Página: " + r.getRotina() + " - Ativa: " + r.isAtivo();
            dao.openTransaction();
            if (dao.update(rotina)) {
                novoLog.update(beforeUpdate, "ID: " + rotina.getId() + " - Rotina: " + rotina.getRotina() + " - Página: " + rotina.getRotina() + " - Ativa: " + rotina.isAtivo());
                dao.commit();
                find();
                GenericaMensagem.info("Sucesso", "Registro atualizado");
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public void loadRotinaGrupo() {
        listRotinaGrupo = new ArrayList();
        listRotinaGrupo = new RotinaGrupoDao().findByGrupo(rotina.getId());
    }

    public void add() {
        RotinaGrupo rotinaGrupo = new RotinaGrupo();
        rotinaGrupo.setGrupo(rotina);
        rotinaGrupo.setRotina((Rotina) new Dao().find(new Rotina(), rotina_id));
        if (new Dao().save(rotinaGrupo, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            listRotinas.clear();
            getListRotinas();
            loadRotinaGrupo();
        } else {
            GenericaMensagem.warn("Validação", "Registro já existe!");
        }
    }

    public void remove(RotinaGrupo rg) {
        if (new Dao().delete(rg, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            listRotinas.clear();
            getListRotinas();
            loadRotinaGrupo();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void clear() {
        GenericaSessao.remove("rotinaBean");
    }

    public void delete() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (rotina.getId() != -1) {
            dao.openTransaction();
            if (dao.delete(rotina)) {
                novoLog.delete("ID: " + rotina.getId() + " - Rotina: " + rotina.getRotina() + " - Página: " + rotina.getRotina() + " - Ativa: " + rotina.isAtivo());
                dao.commit();
                find();
                GenericaMensagem.info("Sucesso", "Registro removido");
                loadRotinaGrupo();
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao remover registro!");
            }
        }
        rotina = new Rotina();
    }

    public String edit(Rotina r) {
        listRotina.remove(r);
        Dao dao = new Dao();
        rotina = new Rotina();
        rotina = (Rotina) dao.rebind(r);
        GenericaSessao.put("rotinaPesquisa", rotina);
        GenericaSessao.put("linkClicado", true);
        if (GenericaSessao.exists("urlRetorno")) {
            if (!GenericaSessao.getString("urlRetorno").equals("menuPrincipal")) {
                return (String) GenericaSessao.getString("urlRetorno");
            }
        } else {
            return "rotina";
        }
        listRotinas = new ArrayList();
        getListRotinas();
        loadRotinaGrupo();
        return null;
    }

    public void find() {
        listRotina = new ArrayList();
        listRotina = new RotinaDao().pesquisaRotinaPorDescricao(descricaoPesquisa, acao);
    }

    public List<Rotina> getListRotina() {
        return listRotina;
    }

    public void setListRotina(List<Rotina> listRotina) {
        this.listRotina = listRotina;
    }

    public String rotinaAtiva(boolean ativo) {
        if (ativo) {
            return "Ativo";
        }
        return "Inativo";
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public Boolean getAcao() {
        return acao;
    }

    public void setAcao(Boolean acao) {
        this.acao = acao;
    }

    public List<RotinaGrupo> getListRotinaGrupo() {
        return listRotinaGrupo;
    }

    public void setListRotinaGrupo(List<RotinaGrupo> listRotinaGrupo) {
        this.listRotinaGrupo = listRotinaGrupo;
    }

    public List<SelectItem> getListRotinas() {
        if (listRotinas.isEmpty()) {
            List<Rotina> list = new RotinaDao().findNotInByTabela("seg_rotina_grupo", "id_grupo", "id_rotina", "" + rotina.getId(), true);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    rotina_id = list.get(i).getId();
                }
                listRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        }
        return listRotinas;
    }

    public void setListRotinas(List<SelectItem> listRotinas) {
        this.listRotinas = listRotinas;
    }

    public Integer getRotina_id() {
        return rotina_id;
    }

    public void setRotina_id(Integer rotina_id) {
        this.rotina_id = rotina_id;
    }

    public void loadShowRotinas() {
        listShowRotinas = new ArrayList();
        Rotina r = new Rotina().get();
        listShowRotinas.add(new RotinaGrupo(null, r, r));
        List<RotinaGrupo> list = new RotinaGrupoDao().findByGrupo(r.getId());
        if (!list.isEmpty()) {
            listShowRotinas.addAll(list);
        }
    }

    public List<RotinaGrupo> getListShowRotinas() {
        return listShowRotinas;
    }

    public void setListShowRotinas(List<RotinaGrupo> listShowRotinas) {
        this.listShowRotinas = listShowRotinas;
    }

    public void wiki() {
        funcionamento = "";
        try {
            Defaults defaults = new Defaults();
            defaults.loadJson();
            String host = defaults.getHost_local();
            if (host.isEmpty()) {
                host = defaults.getUrl_sistem_master();
            }
            Rotina r = new Rotina().get();
            Registro registro = Registro.get();
            // String result = Https.get(host + "ws/wiki_rotina.jsf?action=funcionamento&rotina_id=" + r.getId(), null);
            String result = Https.get(registro.getUrlSistemaExterno() + "ws/wiki_rotina.jsf?action=funcionamento&rotina_id=" + r.getId(), null);
            if (result != null && !result.isEmpty()) {
                JSONObject jSONObject = new JSONObject(result);
                if (jSONObject.getString("funcionamento") != null && !jSONObject.getString("funcionamento").isEmpty()) {
                    funcionamento = jSONObject.getString("funcionamento");
                    PF.openDialog("dlg_wiki_rotina");
                    PF.update("header:form_wiki_rotina");
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public String getFuncionamento() {
        return funcionamento;
    }

    public void setFuncionamento(String funcionamento) {
        this.funcionamento = funcionamento;
    }

}
