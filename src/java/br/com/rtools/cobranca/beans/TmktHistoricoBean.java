package br.com.rtools.cobranca.beans;

import br.com.rtools.cobranca.TmktContato;
import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.cobranca.TmktNatureza;
import br.com.rtools.cobranca.dao.TmktHistoricoDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
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
public class TmktHistoricoBean implements Serializable {

    private TmktHistorico historico;
    private List<SelectItem> listContato;
    private List<SelectItem> listNatureza;
    private List<SelectItem> listDepartamento;
    private Integer idContato;
    private Integer idNatureza;
    private Integer idDepartamento;
    private Pessoa pessoa;
    private String as;
    private String by;
    private String description;
    private List<TmktHistorico> listHistorico;

    @PostConstruct
    public void init() {
        historico = new TmktHistorico();
        loadListContato();
        loadListNatureza();
        pessoa = null;
        idContato = null;
        idNatureza = null;
        idDepartamento = null;
        loadListDepartamento();
        loadListNatureza();
        loadListContato();
        historico.setOperador(Usuario.getUsuario());
        by = "I";
        as = "nome";
        description = "";
        listHistorico = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("tmktHistoricoBean");
        // GenericaSessao.remove("pessoaPesquisa");
    }

    public void loadListContato() {
        listContato = new ArrayList<>();
        List<TmktContato> list = new Dao().list(new TmktContato(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idContato = list.get(i).getId();
            }
            listContato.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListNatureza() {
        listNatureza = new ArrayList<>();
        List<TmktNatureza> list = new Dao().list(new TmktNatureza(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idNatureza = list.get(i).getId();
            }
            listNatureza.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListDepartamento() {
        listDepartamento = new ArrayList<>();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDepartamento = list.get(i).getId();
            }
            listDepartamento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadList() {
        listHistorico = new TmktHistoricoDao().fimd(description, by, as);
    }

    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                GenericaSessao.remove("tmktHistoricoBean");
                break;
        }
    }

    public String edit(TmktHistorico th) {
        String url = (String) GenericaSessao.getString("urlRetorno");
        historico = th;
        idDepartamento = th.getDepartamento().getId();
        idNatureza = th.getNatureza().getId();
        idContato = th.getContato().getId();
        pessoa = th.getPessoa();
        ChamadaPaginaBean.link();
        return url;
    }

    public void save() {
        Dao dao = new Dao();
        if (pessoa == null) {
            GenericaMensagem.warn("Validação", "Informar pessoa!");
            return;
        }
        if (historico.getContatoDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar detalhes do contato!");
            return;
        }
        if (historico.getHistorico().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar histórico do atendimento!");
            return;
        }
        historico.setDepartamento((Departamento) dao.find(new Departamento(), idDepartamento));
        historico.setContato((TmktContato) dao.find(new TmktContato(), idContato));
        historico.setNatureza((TmktNatureza) dao.find(new TmktNatureza(), idNatureza));
        historico.setPessoa(pessoa);
        dao.openTransaction();
        if (historico.getId() == null) {
            historico.setOperador(Usuario.getUsuario());
            if (dao.save(historico)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro inserido");
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else if (dao.update(historico)) {
            dao.commit();
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void delete() {
        if (new Dao().delete(historico, true)) {
            historico = new TmktHistorico();
            historico.setOperador(Usuario.getUsuario());
            idDepartamento = 1;
            idNatureza = 1;
            idContato = 1;
            listHistorico.clear();
            pessoa = new Pessoa();
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void findFilter(String tcase) {
        switch (tcase) {
            case "I":
                as = tcase;
                break;
            case "P":
                as = tcase;
                break;
        }
        loadList();
    }

    public TmktHistorico getHistorico() {
        return historico;
    }

    public void setHistorico(TmktHistorico historico) {
        this.historico = historico;
    }

    public List<SelectItem> getListContato() {
        return listContato;
    }

    public void setListContato(List<SelectItem> listContato) {
        this.listContato = listContato;
    }

    public List<SelectItem> getListNatureza() {
        return listNatureza;
    }

    public void setListNatureza(List<SelectItem> listNatureza) {
        this.listNatureza = listNatureza;
    }

    public Integer getIdContato() {
        return idContato;
    }

    public void setIdContato(Integer idContato) {
        this.idContato = idContato;
    }

    public Integer getIdNatureza() {
        return idNatureza;
    }

    public void setIdNatureza(Integer idNatureza) {
        this.idNatureza = idNatureza;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SelectItem> getListDepartamento() {
        return listDepartamento;
    }

    public void setListDepartamento(List<SelectItem> listDepartamento) {
        this.listDepartamento = listDepartamento;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getMask() {
        return Mask.getMascaraPesquisa(by, true);
    }

    public List<TmktHistorico> getListHistorico() {
        return listHistorico;
    }

    public void setListHistorico(List<TmktHistorico> listHistorico) {
        this.listHistorico = listHistorico;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void loadListTmktHistorico(Integer pessoa_id) {
        listHistorico = new TmktHistoricoDao().findByPessoa(pessoa_id);
    }

}
