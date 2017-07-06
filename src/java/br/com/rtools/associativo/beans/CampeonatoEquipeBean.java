package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CampeonatoEquipeDao;
import br.com.rtools.associativo.dao.CampeonatoEquipePessoaDao;
import br.com.rtools.associativo.dao.EquipeDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
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
public class CampeonatoEquipeBean implements Serializable {

    private CampeonatoEquipe campeonatoEquipe;
    private List<SelectItem> listCampeonatos = new ArrayList();
    private List<SelectItem> listEquipes = new ArrayList();
    private Integer idCampeonato;
    private Integer idEquipe;
    private List<CampeonatoEquipe> listCampeonatoEquipes;
    private List<CampeonatoEquipePessoa> listCampeonatoEquipePessoas;
    private Pessoa membroEquipe;
    private Boolean editMembrosEquipe;

    @PostConstruct
    public void init() {
        editMembrosEquipe = false;
        campeonatoEquipe = new CampeonatoEquipe();
        listCampeonatoEquipes = new ArrayList();
        listCampeonatos = new ArrayList();
        listEquipes = new ArrayList();
        loadListCampeonatos();
        loadListEquipes();
        loadListCampeonatoEquipes();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("campeonatoEquipeBean");
    }

    public void loadListCampeonatos() {
        listCampeonatos = new ArrayList();
        List<Campeonato> list = new Dao().list(new Campeonato());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCampeonato = list.get(i).getId();
            }
            listCampeonatos.add(new SelectItem(list.get(i).getId(), list.get(i).getEvento().getDescricaoEvento().getDescricao() + " " + list.get(i).getTituloComplemento()));
        }
    }

    public void loadListEquipes() {
        listEquipes = new ArrayList();
        Campeonato c = (Campeonato) new Dao().find(new Campeonato(), idCampeonato);
        if (c != null && idCampeonato != null) {
            List<Equipe> list = new EquipeDao().findByModalidade(c.getModalidade().getId());
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idEquipe = list.get(i).getId();
                }
                listEquipes.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
    }

    public void loadListCampeonatoEquipes() {
        listCampeonatoEquipes = new ArrayList();
        if (idCampeonato != null) {
            listCampeonatoEquipes = new CampeonatoEquipeDao().findByCampeonato(idCampeonato);
        }
    }

    public void loadListCampeonatoEquipePessoas(Integer campeonato_equipe_id) {
        listCampeonatoEquipePessoas = new ArrayList();
        listCampeonatoEquipePessoas = new CampeonatoEquipePessoaDao().findByCampeonatoEquipe(campeonato_equipe_id);
    }

    public void save() {
        if (idCampeonato == null) {
            GenericaMensagem.warn("Validação", "INFORMAR/CADASTRAR CAMPEONATO!");
            return;
        }
        if (idEquipe == null) {
            GenericaMensagem.warn("Validação", "INFORMAR/CADASTRAR EQUIPE!");
            return;
        }
        if (new CampeonatoEquipeDao().exists(idEquipe, idCampeonato) != null) {
            GenericaMensagem.warn("Validação", "EQUIPE JÁ CADASTRADA PARA ESTE CAMPEONATO!");
            return;

        }
        Dao dao = new Dao();
        dao.openTransaction();

        campeonatoEquipe.setCampeonato((Campeonato) dao.find(new Campeonato(), idCampeonato));
        campeonatoEquipe.setEquipe((Equipe) dao.find(new Equipe(), idEquipe));

        if (campeonatoEquipe.getId() == null) {
            if (!dao.save(campeonatoEquipe)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
            dao.commit();
            editMembrosEquipe = true;
        } else {
            if (!dao.update(campeonatoEquipe)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
            dao.commit();
        }
        loadListCampeonatoEquipes();
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (campeonatoEquipe.getId() != null) {
            campeonatoEquipe = (CampeonatoEquipe) dao.find(campeonatoEquipe);
            if (!dao.delete(campeonatoEquipe)) {
                GenericaMensagem.warn("Erro", "AO REMOVER CARAVANA");
                dao.rollback();
                return;
            }
            dao.commit();
            campeonatoEquipe = new CampeonatoEquipe();
            GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UMA CARAVANA!");
            dao.rollback();
        }
        loadListCampeonatoEquipes();
    }

    public void clear() {
        GenericaSessao.remove("campeonatoEquipeBean");
    }

    public String edit(CampeonatoEquipe ce) {
        campeonatoEquipe = (CampeonatoEquipe) new Dao().rebind(ce);
        idCampeonato = ce.getCampeonato().getId();
        idEquipe = ce.getEquipe().getId();
        editMembrosEquipe = true;
        membroEquipe = null;
        loadListCampeonatoEquipePessoas(ce.getId());
        return null;
    }

    public void addMembroEquipe() {
        if (membroEquipe == null) {
            GenericaMensagem.warn("Validação", "PESQUISAR PESSOA!");
            return;
        }
        CampeonatoEquipePessoa cep = new CampeonatoEquipePessoa();
        cep.setCampeonato(campeonatoEquipe.getCampeonato());
        cep.setCampeonatoEquipe(campeonatoEquipe);
        cep.setPessoa(membroEquipe);
        if (new CampeonatoEquipePessoaDao().exists(campeonatoEquipe.getId(), campeonatoEquipe.getCampeonato().getId(), membroEquipe.getId()) != null) {
            GenericaMensagem.warn("Validação", "PESSOA JÁ ESTA NESSA EQUIPE!");
            return;
        }
        if (!new Dao().save(cep, true)) {
            GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
            return;
        }
        membroEquipe = null;
        GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        loadListCampeonatoEquipePessoas(campeonatoEquipe.getId());
    }

    public void deleteMembroEquipe(CampeonatoEquipePessoa cep) {
        if (!new Dao().delete(cep, true)) {
            GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO!");
            return;
        }
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        loadListCampeonatoEquipePessoas(campeonatoEquipe.getId());
    }

    public CampeonatoEquipe getCampeonatoEquipe() {
        if (GenericaSessao.exists("campeonatoEquipePesquisa")) {
            campeonatoEquipe = (CampeonatoEquipe) GenericaSessao.getObject("campeonatoEquipePesquisa", true);
        }
        return campeonatoEquipe;
    }

    public void setCampeonatoEquipe(CampeonatoEquipe campeonatoEquipe) {
        this.campeonatoEquipe = campeonatoEquipe;
    }

    public List<SelectItem> getListCampeonatos() {
        return listCampeonatos;
    }

    public void setListCampeonatos(List<SelectItem> listCampeonatos) {
        this.listCampeonatos = listCampeonatos;
    }

    public List<SelectItem> getListEquipes() {
        return listEquipes;
    }

    public void setListEquipes(List<SelectItem> listEquipes) {
        this.listEquipes = listEquipes;
    }

    public Integer getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(Integer idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public Integer getIdEquipe() {
        return idEquipe;
    }

    public void setIdEquipe(Integer idEquipe) {
        this.idEquipe = idEquipe;
    }

    public List<CampeonatoEquipe> getListCampeonatoEquipes() {
        return listCampeonatoEquipes;
    }

    public void setListCampeonatoEquipes(List<CampeonatoEquipe> listCampeonatoEquipes) {
        this.listCampeonatoEquipes = listCampeonatoEquipes;
    }

    public List<CampeonatoEquipePessoa> getListCampeonatoEquipePessoas() {
        return listCampeonatoEquipePessoas;
    }

    public void setListCampeonatoEquipePessoas(List<CampeonatoEquipePessoa> listCampeonatoEquipePessoas) {
        this.listCampeonatoEquipePessoas = listCampeonatoEquipePessoas;
    }

    public Pessoa getMembroEquipe() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            membroEquipe = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return membroEquipe;
    }

    public void setMembroEquipe(Pessoa membroEquipe) {
        this.membroEquipe = membroEquipe;
    }

    public Boolean getEditMembrosEquipe() {
        return editMembrosEquipe;
    }

    public void setEditMembrosEquipe(Boolean editMembrosEquipe) {
        this.editMembrosEquipe = editMembrosEquipe;
    }

}
