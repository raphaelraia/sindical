package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.dao.ParentescoDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.locadoraFilme.LocadoraAutorizados;
import br.com.rtools.locadoraFilme.dao.LocadoraAutorizadosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
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
public class LocadoraAutorizadosBean implements Serializable {

    private LocadoraAutorizados locadoraAutorizados;
    private Integer idParentesco;
    private List<SelectItem> listParentesco;
    private List<LocadoraAutorizados> listLocadoraAutorizados;
    private Pessoa titular;
    private String sexo;
    private String message;

    @PostConstruct
    public void init() {
        titular = new Pessoa();
        sexo = "F";
        locadoraAutorizados = new LocadoraAutorizados();
        idParentesco = null;
        listParentesco = new ArrayList<>();
        loadParentesco();
        listLocadoraAutorizados = new ArrayList<>();
        loadLocadoraAutorizados();
        message = null;
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("locadoraAutorizadosBean");
        GenericaSessao.remove("fisicaPesquisa");
    }

    public void loadParentesco() {
        listParentesco.clear();
        ParentescoDao parentescoDao = new ParentescoDao();
        List<Parentesco> list = parentescoDao.findBySexo(sexo);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idParentesco = list.get(i).getId();
            }
            listParentesco.add(new SelectItem(list.get(i).getId(), list.get(i).getParentesco()));
        }
    }

    public void loadLocadoraAutorizados() {
        if (titular.getId() != -1) {
            listLocadoraAutorizados.clear();
            listLocadoraAutorizados = new LocadoraAutorizadosDao().findAllByTitular(titular.getId());
        }
    }

    public void save() {
        Dao dao = new Dao();
        if (titular.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar o titular!");
            return;
        }
        if (locadoraAutorizados.getNome().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar nome!");
            return;
        }
        if (idParentesco == null) {
            GenericaMensagem.warn("Erro", "Informar parentesco!");
            return;
        }
        locadoraAutorizados.setParentesco((Parentesco) dao.find(new Parentesco(), idParentesco));
        locadoraAutorizados.setTitular(titular);
        if (new Dao().save(locadoraAutorizados, true)) {
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("loc_autorizados");
            novoLog.setCodigo(locadoraAutorizados.getId());
            novoLog.save(
                    "ID" + locadoraAutorizados.getId()
                    + " - Titular: (" + locadoraAutorizados.getTitular().getId() + ") " + locadoraAutorizados.getTitular().getNome()
                    + " - Nome: " + locadoraAutorizados.getNome()
                    + " - Parentesco: (" + locadoraAutorizados.getParentesco().getId() + ") " + locadoraAutorizados.getParentesco().getParentesco()
            );
            locadoraAutorizados = new LocadoraAutorizados();
            listener(1);
            GenericaMensagem.info("Sucesso", "Registro inserido");
        } else {
            GenericaMensagem.warn("Erro", "Registro já existe!");
        }
    }

    public void delete(LocadoraAutorizados la) {
        if (new Dao().delete(la, true)) {
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("loc_autorizados");
            novoLog.setCodigo(la.getId());
            novoLog.save(
                    "ID" + la.getId()
                    + " - Titular: (" + la.getTitular().getId() + ") " + la.getTitular().getNome()
                    + " - Nome: " + la.getNome()
                    + " - Parentesco: (" + la.getParentesco().getId() + ") " + la.getParentesco().getParentesco()
            );
            locadoraAutorizados = new LocadoraAutorizados();
            listener(1);
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void listener(Integer tcase) {
        if (tcase == 1) {
            loadParentesco();
            loadLocadoraAutorizados();
        } else if (tcase == 2) {
            loadParentesco();
        }
    }

    public Pessoa getTitular() {
        return titular;
    }

    public void setTitular(Pessoa titular) {
        this.titular = titular;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocadoraAutorizados getLocadoraAutorizados() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            message = "";
            titular = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            if (titular.getSocios().getMatriculaSocios().getTitular().getId() != titular.getId()) {
                message = "Sócio dependente não pode autorizar!";
            }
            loadLocadoraAutorizados();
        }
        return locadoraAutorizados;
    }

    public void setLocadoraAutorizados(LocadoraAutorizados locadoraAutorizados) {
        this.locadoraAutorizados = locadoraAutorizados;
    }

    public Integer getIdParentesco() {
        return idParentesco;
    }

    public void setIdParentesco(Integer idParentesco) {
        this.idParentesco = idParentesco;
    }

    public List<SelectItem> getListParentesco() {
        return listParentesco;
    }

    public void setListParentesco(List<SelectItem> listParentesco) {
        this.listParentesco = listParentesco;
    }

    public List<LocadoraAutorizados> getListLocadoraAutorizados() {
        return listLocadoraAutorizados;
    }

    public void setListLocadoraAutorizados(List<LocadoraAutorizados> listLocadoraAutorizados) {
        this.listLocadoraAutorizados = listLocadoraAutorizados;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
