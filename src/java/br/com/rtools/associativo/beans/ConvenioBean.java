package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Convenio;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.TipoTratamento;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ConvenioBean implements Serializable {

    private Convenio convenio;
    private String message;
    private List<Convenio> listConvenio;
    private List<SelectItem> listTipoTratamento;
    /**
     * <ul>
     * <li>[0] Jurídica</li>
     * <li>[1] Grupo</li>
     * <li>[2] SubGrupo</li>
     * </ul>
     */
    private Boolean order[];
    private Integer idTipoTratamento;

    @PostConstruct
    public void init() {
        idTipoTratamento = null;
        convenio = new Convenio();
        message = "";
        listConvenio = new ArrayList<Convenio>();
        order = new Boolean[3];
        order[0] = false;
        order[1] = false;
        order[2] = false;
        loadListTipoTratamento();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("convenioBean");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("subGrupoConvenioPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("convenioBean");
    }

    public void save() {
        if (convenio.getJuridica().getPessoa().getId() == -1) {
            message = "Pesquisar uma empresa!";
            return;
        }
        if (convenio.getSubGrupoConvenio().getId() == -1) {
            message = "Pesquisar um subgrupo!";
            return;
        }
        NovoLog novoLog = new NovoLog();
        Dao di = new Dao();
        if (idTipoTratamento != null) {
            if (convenio.getAbreviacao().isEmpty()) {
                message = "Informar abreviação!";
                return;
            }
            convenio.setTipoTratamento((TipoTratamento) di.find(new TipoTratamento(), idTipoTratamento));
        } else {
            convenio.setAbreviacao("");
            convenio.setTipoTratamento(null);
        }
        di.openTransaction();
        if (convenio.getId() == -1) {
            ConvenioDao db = new ConvenioDao();
            if (db.existeSubGrupoEmpresa(convenio)) {
                message = "Convênio já existe!";
                return;
            }
            if (di.save(convenio)) {
                di.commit();
                novoLog.save(
                        "ID: " + convenio.getId()
                        + " - Empresa: (" + convenio.getJuridica().getPessoa().getId() + ") " + convenio.getJuridica().getPessoa().getNome()
                        + " - SubGrupoConvenio: (" + convenio.getSubGrupoConvenio().getId() + ") " + convenio.getSubGrupoConvenio().getDescricao()
                );
                listConvenio.clear();
                message = "Registro inserido com sucesso";
            } else {
                di.rollback();
                listConvenio.clear();
                message = "Erro ao adicionar este registro!";
            }
        } else {
            Convenio c = (Convenio) di.find(convenio);
            String beforeUpdate
                    = "ID: " + c.getId()
                    + " - Empresa: (" + c.getJuridica().getPessoa().getId() + ") " + c.getJuridica().getPessoa().getNome()
                    + " - SubGrupoConvenio: (" + c.getSubGrupoConvenio().getId() + ") " + c.getSubGrupoConvenio().getDescricao();
            if (di.update(convenio)) {
                novoLog.update(beforeUpdate,
                        "ID: " + convenio.getId()
                        + " - Empresa: (" + convenio.getJuridica().getPessoa().getId() + ") " + convenio.getJuridica().getPessoa().getNome()
                        + " - SubGrupoConvenio: (" + convenio.getSubGrupoConvenio().getId() + ") " + convenio.getSubGrupoConvenio().getDescricao()
                );
                di.commit();
                message = "Registro atualizado com sucesso";
                listConvenio.clear();
            } else {
                di.rollback();
                message = "Erro ao atualizar este registro!";
            }
        }
    }

    public void novo() {
        listConvenio.clear();
        convenio = new Convenio();
        idTipoTratamento = null;
        message = "";
    }

    public void remove(Convenio c) {
        if (c.getId() != -1) {
            Dao di = new Dao();
            convenio = (Convenio) di.find(c);
            di.openTransaction();
            if (di.delete(convenio)) {
                di.commit();
                NovoLog novoLog = new NovoLog();
                novoLog.delete(
                        "ID: " + convenio.getId()
                        + " - Empresa: (" + convenio.getJuridica().getPessoa().getId() + ") " + convenio.getJuridica().getPessoa().getNome()
                        + " - SubGrupoConvenio: (" + convenio.getSubGrupoConvenio().getId() + ") " + convenio.getSubGrupoConvenio().getDescricao()
                );
                listConvenio.clear();
                message = "Registro removido com sucesso";
                convenio = new Convenio();
            } else {
                di.rollback();
                message = "Falha ao remover este registro!";
            }
        } else {
            message = "Pesquisar registro a ser excluído!";
        }
    }

    public void edit(Convenio c) {
        Dao di = new Dao();
        convenio = (Convenio) di.rebind(c);
    }

    public Convenio getConvenio() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            convenio.setJuridica((Juridica) GenericaSessao.getObject("juridicaPesquisa", true));
        }
        if (GenericaSessao.exists("subGrupoConvenioPesquisa")) {
            convenio.setSubGrupoConvenio((SubGrupoConvenio) GenericaSessao.getObject("subGrupoConvenioPesquisa", true));
        }
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Convenio> getListConvenio() {
        if (listConvenio.isEmpty()) {
            ConvenioDao db = new ConvenioDao();
            if (convenio.getJuridica().getId() != -1) {
                listConvenio = db.listaTodosPorPessoa(order[0], order[1], order[2], convenio);
            } else {
                listConvenio = db.listaTodos(order[0], order[1], order[2]);
            }
        }
        return listConvenio;
    }

    public void setListConvenio(List<Convenio> listConvenio) {
        this.listConvenio = listConvenio;
    }

    public Boolean[] getOrder() {
        return order;
    }

    public void setOrder(Boolean[] order) {
        this.order = order;
    }

    public void loadListTipoTratamento() {
        listTipoTratamento = new ArrayList();
        List<TipoTratamento> list = new Dao().list(new TipoTratamento(), true);
        listTipoTratamento.add(new SelectItem(null, "NENHUM"));
        idTipoTratamento = null;
        for (int i = 0; i < list.size(); i++) {
            listTipoTratamento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<SelectItem> getListTipoTratamento() {
        return listTipoTratamento;
    }

    public void setListTipoTratamento(List<SelectItem> listTipoTratamento) {
        this.listTipoTratamento = listTipoTratamento;
    }

    public Integer getIdTipoTratamento() {
        return idTipoTratamento;
    }

    public void setIdTipoTratamento(Integer idTipoTratamento) {
        this.idTipoTratamento = idTipoTratamento;
    }
    
    
}
