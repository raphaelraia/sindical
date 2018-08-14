package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Credenciadores;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCredenciadoresDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Reports;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioCredenciadoresBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private Map<String, Integer> listCredenciador;
    private List selectedCredenciador;


    private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;

    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        loadFilters();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCredenciadoresBean");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        String order = "";
        Integer titular_id = null;
        String detalheRelatorio = "";
        List<ObjectJasper> cs = new ArrayList<>();
        List<Etiquetas> e = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioCredenciadoresDao rcd = new RelatorioCredenciadoresDao();
        if (!listRelatorioOrdem.isEmpty()) {
            if (idRelatorioOrdem != null) {
                rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
            }
        }
        rcd.setRelatorios(r);
        List list = rcd.find(inIdCredenciador(), listDateFilters);
        sisProcesso.finishQuery();
        ObjectJasper oj = new ObjectJasper();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (null != r.getId()) {
                oj = new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5));
            }
            cs.add(oj);
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Reports reports = new Reports();
        reports.setTITLE(r.getNome());
        reports.print(r.getJasper(), r.getNome(), (Collection) cs);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    public void removeFilterDate(DateFilters df) {
        listDateFilters.remove(df);
        loadDates();
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }
            if (!list.isEmpty()) {
                idRelatorio = list.get(0).getId();
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("datas", "Datas", false, false));
        listFilters.add(new Filters("credenciador", "Credenciador", false, false));
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "datas":
                listDateFilters = new ArrayList();
                listDateFilters = new ArrayList();
                listDates = new ArrayList();
                selectedDate = "";
                typeDate = "faixa";
                startDate = "";
                finishDate = "";
                if (filter.getActive()) {
                    loadDates();
                }
                break;

            case "credenciador":
                if (filter.getActive()) {
                    loadListCredenciador();
                } else {
                    listCredenciador = new LinkedHashMap<>();
                    selectedCredenciador = new ArrayList<>();
                }
                break;
        }
    }

    public String inIdCredenciador() {
        String ids = null;
        if (selectedCredenciador != null) {
            ids = "";
            for (int i = 0; i < selectedCredenciador.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCredenciador.get(i).toString();
                } else {
                    ids += "," + selectedCredenciador.get(i).toString();
                }
            }
        }
        return ids;
    }


    public String getDateItemDescription(String title) {
        for (int x = 0; x < listDates.size(); x++) {
            if (title.equals(listDates.get(x).getValue().toString())) {
                return listDates.get(x).getLabel();
            }
        }
        return "";
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters filters = new Filters();
        filters.setKey(filter);
        filters.setActive(false);
        for (Filters f : listFilters) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        load(filters);
    }

    public void addFilterDate() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            return;
        }
        if (typeDate.equals("igual") || typeDate.equals("apartir") || typeDate.equals("ate")) {
            if (startDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA INICIAL!");
                return;
            }
        } else if (typeDate.equals("faixa")) {
            if (startDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA INICIAL!");
                return;
            }
            if (finishDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA FINAL!");
                return;
            }
        }
        listDateFilters.add(new DateFilters(true, selectedDate, typeDate, startDate, finishDate));
        loadDates();
        selectedDate = "";
        typeDate = "faixa";
        startDate = "";
        finishDate = "";
    }

    // GETTERS AND SETTERS
    public List<SelectItem> getListRelatorios() {
        return listRelatorio;
    }

    public void setListRelatorios(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    /**
     * 0 grupo finançeiro; 1 subgrupo finançeiro; 2 serviços; 3 sócios; 4 tipo
     * de pessoa; 5 meses débito
     *
     * @return
     */
    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public void loadListCredenciador() {
        listCredenciador = new LinkedHashMap<>();
        selectedCredenciador = new ArrayList<>();
        List<Credenciadores> list = new Dao().list(new Credenciadores(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listCredenciador.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
            }
        }
    }


    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("filiacao", "Filiação"));
        // RELOAD DATA
        if (listDateFilters != null) {
            for (int i = 0; i < listDateFilters.size(); i++) {
                for (int x = 0; x < listDates.size(); x++) {
                    if (listDateFilters.get(i).getTitle().equals(listDates.get(x).getValue().toString())) {
                        listDates.get(x).setDisabled(true);
                        break;
                    }
                }
            }
        }
    }

    public Map<String, Integer> getListCredenciador() {
        return listCredenciador;
    }

    public void setListCredenciador(Map<String, Integer> listCredenciador) {
        this.listCredenciador = listCredenciador;
    }

    public List getSelectedCredenciador() {
        return selectedCredenciador;
    }

    public void setSelectedCredenciador(List selectedCredenciador) {
        this.selectedCredenciador = selectedCredenciador;
    }

    public List<DateFilters> getListDateFilters() {
        return listDateFilters;
    }

    public void setListDateFilters(List<DateFilters> listDateFilters) {
        this.listDateFilters = listDateFilters;
    }

    public List<SelectItem> getListDates() {
        return listDates;
    }

    public void setListDates(List<SelectItem> listDates) {
        this.listDates = listDates;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Boolean getShow(String filtro) {
        if (listFilters.stream().filter((filters) -> (filters.getKey().equals(filtro))).anyMatch((filters) -> (filters.getActive()))) {
            return true;
        }
        return false;
    }

  

    public class ObjectJasper {

        private Object filiacao;
        private Object id_credenciador;
        private Object credenciador;
        private Object socio;
        private Object matricula;
        private Object categoria;

        public ObjectJasper() {
            this.filiacao = null;
            this.id_credenciador = null;
            this.credenciador = null;
            this.socio = null;
            this.matricula = null;
            this.categoria = null;
        }

        public ObjectJasper(Object filiacao, Object id_credenciador, Object credenciador, Object socio, Object matricula, Object categoria) {
            this.filiacao = filiacao;
            this.id_credenciador = id_credenciador;
            this.credenciador = credenciador;
            this.socio = socio;
            this.matricula = matricula;
            this.categoria = categoria;
        }

        public Object getFiliacao() {
            return filiacao;
        }

        public void setFiliacao(Object filiacao) {
            this.filiacao = filiacao;
        }

        public Object getId_credenciador() {
            return id_credenciador;
        }

        public void setId_credenciador(Object id_credenciador) {
            this.id_credenciador = id_credenciador;
        }

        public Object getCredenciador() {
            return credenciador;
        }

        public void setCredenciador(Object credenciador) {
            this.credenciador = credenciador;
        }

        public Object getSocio() {
            return socio;
        }

        public void setSocio(Object socio) {
            this.socio = socio;
        }

        public Object getMatricula() {
            return matricula;
        }

        public void setMatricula(Object matricula) {
            this.matricula = matricula;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

    }
}
