package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.*;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
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
public class CatracaServicoDeptoBean implements Serializable {

    private CatracaServicoDepto catracaServicoDepto;
    private List<CatracaServicoDepto> listCatracaServicoDepto;
    private Integer idServico;
    private Integer idDepartamento;
    private List<SelectItem> listDepartamentos;
    private List<SelectItem> listServicos;

    @PostConstruct
    public void init() {
        catracaServicoDepto = new CatracaServicoDepto();
        listCatracaServicoDepto = new ArrayList();
        listDepartamentos = new ArrayList();
        listServicos = new ArrayList();
        loadDepartamentos();
        loadServicos();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("catracaServicoDeptoBean");
    }

    public void add() {
        catracaServicoDepto.setDepartamento((Departamento) new Dao().find(new Departamento(), idDepartamento));
        catracaServicoDepto.setServicos((Servicos) new Dao().find(new Servicos(), idServico));
        if (new Dao().save(catracaServicoDepto, true)) {
            GenericaMensagem.info("Sucesso", "Regtistro inserido");
            listCatracaServicoDepto.clear();
            catracaServicoDepto = new CatracaServicoDepto();
            loadServicos();
        } else {
            GenericaMensagem.warn("Erro", "Registro já existe");
        }
    }

    public void loadDepartamentos() {
        listDepartamentos.clear();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDepartamento = list.get(i).getId();
            }
            listDepartamentos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadServicos() {
        listServicos.clear();
        ServicosDao servicosDao = new ServicosDao();
        servicosDao.setSituacao("A");
        List<Servicos> list = new ServicosDao().findNotInByTabela("soc_catraca_servico_depto", "id_departamento", idDepartamento.toString());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServico = list.get(i).getId();
            }
            listServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));

        }
    }

    public List<SelectItem> getListDepartamentos() {
        return listDepartamentos;
    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void remove(CatracaServicoDepto csd) {
        if (new Dao().delete(csd, true)) {
            GenericaMensagem.info("Sucesso", "Regtistro remover ");
            listCatracaServicoDepto.clear();
            loadServicos();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public CatracaServicoDepto getCatracaServicoDepto() {
        return catracaServicoDepto;
    }

    public void setCatracaServicoDepto(CatracaServicoDepto catracaServicoDepto) {
        this.catracaServicoDepto = catracaServicoDepto;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public List<CatracaServicoDepto> getListCatracaServicoDepto() {
        if (listCatracaServicoDepto.isEmpty()) {
            listCatracaServicoDepto = new Dao().list(new CatracaServicoDepto(), true);
        }
        return listCatracaServicoDepto;
    }

    public void setListCatracaServicoDepto(List<CatracaServicoDepto> listCatracaServicoDepto) {
        this.listCatracaServicoDepto = listCatracaServicoDepto;
    }

    public void setListDepartamentos(List<SelectItem> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }
}
