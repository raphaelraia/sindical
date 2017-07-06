package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CampeonatoDao;
import br.com.rtools.financeiro.Evt;
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
public class CampeonatoBean implements Serializable {

    private Campeonato campeonato;
    private List<SelectItem> listModalidades = new ArrayList();
    private Integer idModalidade;
    private List<SelectItem> listDescricaoEvento = new ArrayList();
    private Integer idDescricaoEvento;
    private List<Campeonato> listCampeonatos;

    @PostConstruct
    public void init() {
        campeonato = new Campeonato();
        listDescricaoEvento = new ArrayList();
        listModalidades = new ArrayList();
        listCampeonatos = new ArrayList();
        loadListModalidades();
        loadListDescricaoEventos();
        loadListCampeonatos();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("campeonatoBean");
    }

    public void loadListDescricaoEventos() {
        listDescricaoEvento = new ArrayList();
        List<DescricaoEvento> list = new DescricaoEventoDao().pesquisaDescricaoPorGrupo(4);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDescricaoEvento = list.get(i).getId();
            }
            listDescricaoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
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

    public void loadListCampeonatos() {
        listCampeonatos = new ArrayList();
        listCampeonatos = (List<Campeonato>) new Dao().list(new Campeonato());
    }

    public void save() {
        if (campeonato.getTituloComplemento().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR COMPLEMENTO");
            return;
        }
        if (listDescricaoEvento.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR EVENTOS");
            return;
        }
        if (listModalidades.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR MODALIDADES");
            return;
        }

        Dao dao = new Dao();
        AEvento aEvento = new AEvento();
        dao.openTransaction();

        DescricaoEvento de = (DescricaoEvento) dao.find(new DescricaoEvento(), idDescricaoEvento);

        campeonato.setModalidade((CampeonatoModalidade) dao.find(new CampeonatoModalidade(), idModalidade));


        if (campeonato.getId() == null) {
            if (new CampeonatoDao().exists(de.getId(), idModalidade, campeonato.getTituloComplemento()) != null) {
                GenericaMensagem.warn("Validação", "CAMPEONATO JÁ CADASTRADO!");
                return;
            }
            Evt evt = new Evt();
            if (!dao.save(evt)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVT");
                dao.rollback();
                return;
            }
            campeonato.setEvt(evt);

            aEvento.setDescricaoEvento(de);

            if (!dao.save(aEvento)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVENTO!");
                dao.rollback();
                return;
            }

            campeonato.setEvento(aEvento);
            if (!dao.save(campeonato)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                dao.commit();
                loadListCampeonatos();
            }
        } else {
            if (campeonato.getEvt() == null) {
                Evt evt = new Evt();
                if (!dao.save(evt)) {
                    GenericaMensagem.warn("Erro", "AO SALVAR EVT!");
                    dao.rollback();
                    return;
                }
                campeonato.setEvt(evt);
            }
            aEvento = (AEvento) dao.find(new AEvento(), campeonato.getEvento().getId());
            aEvento.setDescricaoEvento(de);
            if (!dao.update(aEvento)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EVENTO!");
                dao.rollback();
                return;
            }
            campeonato.setEvento(aEvento);
            if (!dao.update(campeonato)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
                dao.commit();
                loadListCampeonatos();
            }
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (campeonato.getId() != null) {
            campeonato = (Campeonato) dao.find(campeonato);
            AEvento aEvento = (AEvento) dao.find(campeonato.getEvento());

            if (!dao.delete(campeonato)) {
                GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO");
                dao.rollback();
                return;
            }

            if (!dao.delete(aEvento)) {
                GenericaMensagem.warn("Erro", "AO REMOVER EVENTO!");
                dao.rollback();
            } else {
                dao.commit();
                loadListCampeonatos();
                campeonato = new Campeonato();
                GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            }
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UM REGISTRO!");
            dao.rollback();
        }
    }

    public void clear() {
        GenericaSessao.remove("campeonatoBean");
    }

    public String edit(Campeonato c) {
        campeonato = (Campeonato) new Dao().rebind(c);
        idDescricaoEvento = campeonato.getEvento().getDescricaoEvento().getId();
        idModalidade = campeonato.getModalidade().getId();
        String url = (String) GenericaSessao.getString("urlRetorno");
        GenericaSessao.put("linkClicado", true);
        if (url != null) {
            GenericaSessao.put("campeonatoPesquisa", campeonato);
            return url;
        }
        return "campeonato";
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
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

    public List<SelectItem> getListDescricaoEvento() {
        return listDescricaoEvento;
    }

    public void setListDescricaoEvento(List<SelectItem> listDescricaoEvento) {
        this.listDescricaoEvento = listDescricaoEvento;
    }

    public Integer getIdDescricaoEvento() {
        return idDescricaoEvento;
    }

    public void setIdDescricaoEvento(Integer idDescricaoEvento) {
        this.idDescricaoEvento = idDescricaoEvento;
    }

    public List<Campeonato> getListCampeonatos() {
        return listCampeonatos;
    }

    public void setListCampeonatos(List<Campeonato> listCampeonatos) {
        this.listCampeonatos = listCampeonatos;
    }

}
