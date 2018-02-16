package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Campeonato;
import br.com.rtools.associativo.CampeonatoEquipe;
import br.com.rtools.associativo.CampeonatoModalidade;
import br.com.rtools.associativo.dao.CampeonatoDao;
import br.com.rtools.associativo.dao.CampeonatoEquipeDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCampeonatoDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioCampeonatoBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private List<Filters> filters;
    private Map<String, Integer> listModalidade;
    private List selectedModalidade;
    private Map<String, Integer> listCampeonato;
    private List selectedCampeonato;
    private Map<String, Integer> listEquipe;
    private List selectedEquipe;
    private String status;
    private String statusCampeonato;
    private String statusPagto;
    private String selectedOrder;

    public RelatorioCampeonatoBean() {
        selectedOrder = "";
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        status = "ativo";
        statusCampeonato = "ativo";
        statusPagto = "adimplente";
        loadFilters();
        loadRelatorios();
        loadRelatoriosOrdem();
    }

    public void clear() {
        GenericaSessao.put("relatorioCampeonatoBean", new RelatorioCampeonatoBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("relatorios")) {
            loadRelatoriosOrdem();
        }
        if (tcase.equals("status_campeonato")) {
            loadListCampeonato();
            loadListEquipes();
        }
        if (tcase.equals("modalidade")) {
            loadListCampeonato();
            loadListEquipes();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("modalidade", "Modalidade", false));
        filters.add(new Filters("campeonato", "Campeonato", false));
        filters.add(new Filters("status", "Status do Jogador", false));
        filters.add(new Filters("status_pagto", "Status Pagto", false));
        filters.add(new Filters("status_campeonato", "Status Campeonato", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters f = new Filters();
        f.setKey(filter);
        f.setActive(false);
        for (Filters f2 : filters) {
            if (f2.getKey().equals(filter)) {
                f2.setActive(false);
            }
        }
        load(f);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {

            case "modalidade":
                listModalidade = new LinkedHashMap<>();
                selectedModalidade = new ArrayList();
                if (filter.getActive()) {
                    loadListModalidade();
                }
                break;
            case "campeonato":
                listCampeonato = new LinkedHashMap<>();
                listEquipe = new LinkedHashMap<>();
                selectedCampeonato = new ArrayList();
                selectedEquipe = new ArrayList();
                if (filter.getActive()) {
                    loadListCampeonato();
                    loadListEquipes();
                }
                break;
            case "status":
                status = "ativo";
                break;
            case "status_pagto":
                statusPagto = "adimplente";
                break;
            case "status_campeonato":
                statusCampeonato = "ativo";
                break;
        }
    }

    public String print() {
        Relatorios relatorios = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        RelatorioCampeonatoDao rcd = new RelatorioCampeonatoDao();
        List<ObjectCampeonato> listObjectCampeonato = new ArrayList<>();
        rcd.setRelatorios(relatorios);
        if (idRelatorioOrdem != null) {
            RelatorioOrdem relatorioOrdem = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
            rcd.setRelatorioOrdem(relatorioOrdem);

        }
        rcd.setRelatorios(relatorios);
        List list = rcd.find(inIdModalidade(), inIdCampeonato(), status, statusPagto, statusCampeonato, inIdEquipe());
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }
        if (relatorios.getId() == 125) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                listObjectCampeonato.add(
                        new ObjectCampeonato(
                                o.get(0),
                                o.get(1),
                                o.get(2),
                                o.get(3),
                                o.get(4),
                                o.get(5),
                                o.get(6),
                                o.get(7),
                                o.get(8),
                                o.get(9)
                        )
                );
            }

        } else {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                listObjectCampeonato.add(
                        new ObjectCampeonato(
                                o.get(0),
                                o.get(1),
                                o.get(2),
                                o.get(3),
                                o.get(4),
                                o.get(5),
                                o.get(6),
                                o.get(7),
                                o.get(8),
                                o.get(9),
                                o.get(10),
                                o.get(11),
                                o.get(12),
                                o.get(13),
                                o.get(14),
                                o.get(15),
                                o.get(16),
                                o.get(17),
                                o.get(18),
                                o.get(19),
                                o.get(20),
                                o.get(21)
                        )
                );
            }

        }
        Jasper.TYPE = "default";
        Jasper.TITLE = relatorios.getNome();
        Map map = new HashMap();
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) listObjectCampeonato, map);
        return null;
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters f : filters) {
                if (f.getKey().equals(filtro)) {
                    if (f.getActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void loadRelatorios() {
        idRelatorio = null;
        listRelatorio = new Relatorios().loadListRelatorios();
        if (!listRelatorio.isEmpty()) {
            idRelatorio = (Integer) listRelatorio.get(0).getValue();
            idRelatorio = new Relatorios().mainRotina();
        }
    }

    public void loadRelatoriosOrdem() {
        idRelatorioOrdem = null;
        listRelatorioOrdem = new RelatorioOrdem().loadListRelatorioOrdem(idRelatorio);
        if (!listRelatorioOrdem.isEmpty()) {
            idRelatorioOrdem = (Integer) listRelatorioOrdem.get(0).getValue();
            idRelatorioOrdem = new RelatorioOrdem().mainRelatorio(idRelatorio);
        }
    }

    public void loadListModalidade() {
        listModalidade = new LinkedHashMap<>();
        selectedModalidade = new ArrayList();
        List<CampeonatoModalidade> list = new Dao().list(new CampeonatoModalidade(), true);
        for (int i = 0; i < list.size(); i++) {
            listModalidade.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadListCampeonato() {
        listCampeonato = new LinkedHashMap<>();
        selectedCampeonato = new ArrayList();
        List<Campeonato> list = new CampeonatoDao().findBy(inIdModalidade(), statusCampeonato);
        for (int i = 0; i < list.size(); i++) {
            listCampeonato.put(list.get(i).getEvento().getDescricaoEvento().getDescricao() + " " + list.get(i).getTituloComplemento(), list.get(i).getId());
        }
    }

    public void loadListEquipes() {
        listEquipe = new LinkedHashMap<>();
        selectedEquipe = new ArrayList();
        List<CampeonatoEquipe> list = new CampeonatoEquipeDao().findByCampeonato(inIdCampeonato());
        for (int i = 0; i < list.size(); i++) {
            listEquipe.put(list.get(i).getEquipe().getDescricao() + " - " + (list.get(i).getCampeonato().getEvento().getDescricaoEvento().getDescricao() + " " + list.get(i).getCampeonato().getTituloComplemento()).substring(0, 15) + "...", list.get(i).getEquipe().getId());
        }
    }

    public String inIdCampeonato() {
        String ids = null;
        if (selectedCampeonato != null) {
            ids = "";
            for (int i = 0; i < selectedCampeonato.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCampeonato.get(i).toString();
                } else {
                    ids += "," + selectedCampeonato.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdModalidade() {
        String ids = null;
        if (selectedModalidade != null) {
            ids = "";
            for (int i = 0; i < selectedModalidade.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedModalidade.get(i).toString();
                } else {
                    ids += "," + selectedModalidade.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdEquipe() {
        String ids = null;
        if (selectedEquipe != null) {
            ids = "";
            for (int i = 0; i < selectedEquipe.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedEquipe.get(i).toString();
                } else {
                    ids += "," + selectedEquipe.get(i).toString();
                }
            }
        }
        return ids;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public String getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(String selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public Map<String, Integer> getListModalidade() {
        return listModalidade;
    }

    public void setListModalidade(Map<String, Integer> listModalidade) {
        this.listModalidade = listModalidade;
    }

    public List getSelectedModalidade() {
        return selectedModalidade;
    }

    public void setSelectedModalidade(List selectedModalidade) {
        this.selectedModalidade = selectedModalidade;
    }

    public Map<String, Integer> getListCampeonato() {
        return listCampeonato;
    }

    public void setListCampeonato(Map<String, Integer> listCampeonato) {
        this.listCampeonato = listCampeonato;
    }

    public List getSelectedCampeonato() {
        return selectedCampeonato;
    }

    public void setSelectedCampeonato(List selectedCampeonato) {
        this.selectedCampeonato = selectedCampeonato;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCampeonato() {
        return statusCampeonato;
    }

    public void setStatusCampeonato(String statusCampeonato) {
        this.statusCampeonato = statusCampeonato;
    }

    public String getStatusPagto() {
        return statusPagto;
    }

    public void setStatusPagto(String statusPagto) {
        this.statusPagto = statusPagto;
    }

    public Map<String, Integer> getListEquipe() {
        return listEquipe;
    }

    public void setListEquipe(Map<String, Integer> listEquipe) {
        this.listEquipe = listEquipe;
    }

    public List getSelectedEquipe() {
        return selectedEquipe;
    }

    public void setSelectedEquipe(List selectedEquipe) {
        this.selectedEquipe = selectedEquipe;
    }

    public class ObjectCampeonato {

        private Object inicio;
        private Object fim;
        private Object id_modalidade;
        private Object modalidade;
        private Object id_campeonato;
        private Object id_campeonato_agenda;
        private Object campeonato;
        private Object id_equipe;
        private Object equipe;
        private Object id_responsavel;
        private Object responsavel;
        private Object id_categoria;
        private Object categoria;
        private Object matricula;
        private Object servico;
        private Object valor;
        private Object inativacao;
        private Object inadimplente;
        private Object id_dependente;
        private Object dependente;
        private Object parentesco;
        private Object valor_dependente;
        private Object total;
        private Object membros;

        public ObjectCampeonato() {
            this.inicio = null;
            this.fim = null;
            this.id_modalidade = null;
            this.modalidade = null;
            this.id_campeonato = null;
            this.id_campeonato_agenda = null;
            this.campeonato = null;
            this.id_equipe = null;
            this.equipe = null;
            this.id_responsavel = null;
            this.responsavel = null;
            this.id_categoria = null;
            this.categoria = null;
            this.matricula = null;
            this.servico = null;
            this.valor = null;
            this.inativacao = null;
            this.inadimplente = null;
            this.id_dependente = null;
            this.dependente = null;
            this.parentesco = null;
            this.valor_dependente = null;
            this.total = null;
            this.membros = null;
        }

        public ObjectCampeonato(Object inicio, Object fim, Object modalidade, Object campeonato, Object equipe, Object membros, Object valor, Object dependente, Object valor_dependente, Object total) {
            this.inicio = inicio;
            this.fim = fim;
            this.modalidade = modalidade;
            this.campeonato = campeonato;
            this.equipe = equipe;
            this.membros = membros;
            this.valor = valor;
            this.dependente = dependente;
            this.valor_dependente = valor_dependente;
            this.total = total;
        }

        public ObjectCampeonato(Object inicio, Object fim, Object id_modalidade, Object modalidade, Object id_campeonato, Object id_campeonato_agenda, Object campeonato, Object id_equipe, Object equipe, Object id_responsavel, Object responsavel, Object id_categoria, Object categoria, Object matricula, Object servico, Object valor, Object inativacao, Object inadimplente, Object id_dependente, Object dependente, Object parentesco, Object valor_dependente) {
            this.inicio = inicio;
            this.fim = fim;
            this.id_modalidade = id_modalidade;
            this.modalidade = modalidade;
            this.id_campeonato = id_campeonato;
            this.id_campeonato_agenda = id_campeonato_agenda;
            this.campeonato = campeonato;
            this.id_equipe = id_equipe;
            this.equipe = equipe;
            this.id_responsavel = id_responsavel;
            this.responsavel = responsavel;
            this.id_categoria = id_categoria;
            this.categoria = categoria;
            this.matricula = matricula;
            this.servico = servico;
            this.valor = valor;
            this.inativacao = inativacao;
            this.inadimplente = inadimplente;
            this.id_dependente = id_dependente;
            this.dependente = dependente;
            this.parentesco = parentesco;
            this.valor_dependente = valor_dependente;
        }

        public Object getInicio() {
            return inicio;
        }

        public void setInicio(Object inicio) {
            this.inicio = inicio;
        }

        public Object getFim() {
            return fim;
        }

        public void setFim(Object fim) {
            this.fim = fim;
        }

        public Object getId_modalidade() {
            return id_modalidade;
        }

        public void setId_modalidade(Object id_modalidade) {
            this.id_modalidade = id_modalidade;
        }

        public Object getModalidade() {
            return modalidade;
        }

        public void setModalidade(Object modalidade) {
            this.modalidade = modalidade;
        }

        public Object getId_campeonato() {
            return id_campeonato;
        }

        public void setId_campeonato(Object id_campeonato) {
            this.id_campeonato = id_campeonato;
        }

        public Object getId_campeonato_agenda() {
            return id_campeonato_agenda;
        }

        public void setId_campeonato_agenda(Object id_campeonato_agenda) {
            this.id_campeonato_agenda = id_campeonato_agenda;
        }

        public Object getCampeonato() {
            return campeonato;
        }

        public void setCampeonato(Object campeonato) {
            this.campeonato = campeonato;
        }

        public Object getId_equipe() {
            return id_equipe;
        }

        public void setId_equipe(Object id_equipe) {
            this.id_equipe = id_equipe;
        }

        public Object getEquipe() {
            return equipe;
        }

        public void setEquipe(Object equipe) {
            this.equipe = equipe;
        }

        public Object getId_responsavel() {
            return id_responsavel;
        }

        public void setId_responsavel(Object id_responsavel) {
            this.id_responsavel = id_responsavel;
        }

        public Object getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(Object responsavel) {
            this.responsavel = responsavel;
        }

        public Object getId_categoria() {
            return id_categoria;
        }

        public void setId_categoria(Object id_categoria) {
            this.id_categoria = id_categoria;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

        public Object getMatricula() {
            return matricula;
        }

        public void setMatricula(Object matricula) {
            this.matricula = matricula;
        }

        public Object getServico() {
            return servico;
        }

        public void setServico(Object servico) {
            this.servico = servico;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getInativacao() {
            return inativacao;
        }

        public void setInativacao(Object inativacao) {
            this.inativacao = inativacao;
        }

        public Object getInadimplente() {
            return inadimplente;
        }

        public void setInadimplente(Object inadimplente) {
            this.inadimplente = inadimplente;
        }

        public Object getId_dependente() {
            return id_dependente;
        }

        public void setId_dependente(Object id_dependente) {
            this.id_dependente = id_dependente;
        }

        public Object getDependente() {
            return dependente;
        }

        public void setDependente(Object dependente) {
            this.dependente = dependente;
        }

        public Object getParentesco() {
            return parentesco;
        }

        public void setParentesco(Object parentesco) {
            this.parentesco = parentesco;
        }

        public Object getValor_dependente() {
            return valor_dependente;
        }

        public void setValor_dependente(Object valor_dependente) {
            this.valor_dependente = valor_dependente;
        }

        public Object getTotal() {
            return total;
        }

        public void setTotal(Object total) {
            this.total = total;
        }

        public Object getMembros() {
            return membros;
        }

        public void setMembros(Object membros) {
            this.membros = membros;
        }

    }

}
