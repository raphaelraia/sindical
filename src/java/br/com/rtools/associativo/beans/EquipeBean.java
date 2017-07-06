package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.EquipeDao;
import br.com.rtools.pessoa.Juridica;
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
public class EquipeBean implements Serializable {

    private Equipe equipe;
    private List<SelectItem> listModalidades = new ArrayList();
    private Integer idModalidade;
    private List<Equipe> listEquipes;

    @PostConstruct
    public void init() {
        equipe = new Equipe();
        listModalidades = new ArrayList();
        listEquipes = new ArrayList();
        loadListModalidades();
        loadListModalidades();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("equipeBean");
    }

    public void loadListModalidades() {
        listModalidades = new ArrayList();
        List<CampeonatoModalidade> list = new Dao().list(new CampeonatoModalidade(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idModalidade = list.get(i).getId();
            }
            listModalidades.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListEquipes() {
        listEquipes = new ArrayList();
        if (idModalidade != null) {
            listEquipes = (List<Equipe>) new EquipeDao().findByModalidade(idModalidade);
        }
    }

    public void save() {
        if (equipe.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR COMPLEMENTO");
            return;
        }
        if (listModalidades.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR MODALIDADES");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        equipe.setModalidade((CampeonatoModalidade) dao.find(new CampeonatoModalidade(), idModalidade));

        if (equipe.getId() == null) {
            if (new EquipeDao().exists(idModalidade, equipe.getDescricao()) != null) {
                GenericaMensagem.warn("Validação", "EQUIPE JÁ EXISTE PARA ESTA MODALIDADE");
                return;
            }
            if (!dao.save(equipe)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
            dao.commit();
            loadListEquipes();
        } else {
            if (!dao.update(equipe)) {
                GenericaMensagem.warn("Erro", "AO INSERIR ATUALIZADO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
            dao.commit();
            loadListEquipes();
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (equipe.getId() != null) {
            equipe = (Equipe) dao.find(equipe);
            if (!dao.delete(equipe)) {
                GenericaMensagem.warn("Erro", "AO REMOVER CARAVANA");
                dao.rollback();
                return;
            }
            dao.commit();
            equipe = new Equipe();
            GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            loadListEquipes();
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UMA CARAVANA!");
            dao.rollback();
        }
    }

    public void clear() {
        GenericaSessao.remove("equipeBean");
    }

    public String edit(Equipe e) {
        equipe = (Equipe) new Dao().rebind(e);
        idModalidade = e.getModalidade().getId();
//        String url = (String) GenericaSessao.getString("urlRetorno");
//        GenericaSessao.put("linkClicado", true);
//        if (url != null) {
//            GenericaSessao.put("campeonatoPesquisa", e);
//            return url;
//        }
        return null;
        //return "equipe";
    }

    public Equipe getEquipe() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            equipe.setPatrocinador((Juridica) GenericaSessao.getObject("juridicaPesquisa", true));
        }
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public List<SelectItem> getListModalidades() {
        return listModalidades;
    }

    public void setListModalidades(List<SelectItem> listModalidades) {
        this.listModalidades = listModalidades;
    }

    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    public List<Equipe> getListEquipes() {
        return listEquipes;
    }

    public void setListEquipes(List<Equipe> listEquipes) {
        this.listEquipes = listEquipes;
    }

}
