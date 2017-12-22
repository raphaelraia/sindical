package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.dao.ExameMedicoDao;
import br.com.rtools.locadoraFilme.Genero;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioExameMedicoDao;
import br.com.rtools.relatorios.dao.RelatorioLocadoraDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Sessions;
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
public class RelatorioExameMedicoBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private List<Filters> filters;
    private Map<String, Integer> listDepartamentos;
    private List selectedDepartamento;
    private Map<String, Integer> listOperadores;
    private List selectedOperador;
    private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;
    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private String type;
    private Pessoa pessoa;
    private SisPessoa sisPessoa;

    public RelatorioExameMedicoBean() {
        Sessions.remove("fisicaPesquisa");
        Sessions.remove("sisPessoaPesquisa");
        type = "all";
        selectedDepartamento = null;
        selectedOperador = null;
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        pessoa = new Pessoa();
        sisPessoa = new SisPessoa();
        loadFilters();
        loadRelatorios();
        loadRelatoriosOrdem();
    }

    public void clear() {
        GenericaSessao.put("relatorioExameMedicoBean", new RelatorioExameMedicoBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("relatorios")) {
            loadRelatoriosOrdem();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioExameMedicoBean", new RelatorioSociosBean());
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("departamento", "Departamento", false));
        filters.add(new Filters("datas", "Data", false));
        filters.add(new Filters("tipo", "Tipo", false));
        filters.add(new Filters("pessoa", "Pessoa", false));
        filters.add(new Filters("operador", "Operadores", false));
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

            case "tipo":
                type = "all";
                break;
            case "departamento":
                listDepartamentos = new LinkedHashMap<>();
                selectedDepartamento = new ArrayList();
                if (filter.getActive()) {
                    loadListDepartamentos();
                }
                break;
            case "operador":
                listOperadores = new LinkedHashMap<>();
                selectedOperador = new ArrayList();
                if (filter.getActive()) {
                    loadListOperadores();
                }
                break;
            case "pessoa":
                sisPessoa = new SisPessoa();
                pessoa = new Pessoa();
                break;
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
        }
    }

    public String print() {
        Relatorios relatorios = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        List<ObjectExameMedico> listObjectExameMedico = new ArrayList<>();

        String ids_deps = inIdDepartamento();

        List list = new RelatorioExameMedicoDao().find(type, listDateFilters, ids_deps, inIdOperadores(), pessoa, sisPessoa);
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }

        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listObjectExameMedico.add(
                    new ObjectExameMedico(
                            o.get(0),
                            o.get(1),
                            o.get(2),
                            o.get(3),
                            o.get(4),
                            o.get(5)
                    )
            );
        }

        Jasper.TYPE = "default";

        // CHAMADO 2249 - CLAUDEMIR
        if (ids_deps.length() == 2) {
            // CLUBE
            if (ids_deps.contains("12")) {
                Jasper.TITLE = "AVALIAÇÃO DA PISCINA";
            } else if (ids_deps.contains("11")) {
                Jasper.TITLE = "AVALIAÇÃO FÍSICA";
            } else {
                Jasper.TITLE = relatorios.getNome();
            }
        } else {
            Jasper.TITLE = relatorios.getNome();
        }

        Map map = new HashMap();
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) listObjectExameMedico, map);
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
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        Integer default_id = 0;
        for (int i = 0; i < list.size(); i++) {
            Boolean disabled = false;
            if (i == 0) {
                idRelatorio = list.get(i).getId();
                default_id = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                default_id = list.get(i).getId();
                idRelatorio = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), "", disabled));
        }
    }

    public void loadRelatoriosOrdem() {
        listRelatorioOrdem = new ArrayList();
        idRelatorioOrdem = 0;
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                if (list.get(i).getPrincipal()) {
                    idRelatorioOrdem = list.get(i).getId();
                }
            }
        }
    }

    public void loadListDepartamentos() {
        listDepartamentos = new LinkedHashMap<>();
        selectedDepartamento = new ArrayList();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        for (int i = 0; i < list.size(); i++) {
            listDepartamentos.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadListOperadores() {
        listOperadores = new LinkedHashMap<>();
        selectedOperador = new ArrayList();
        List<Usuario> list = new ExameMedicoDao().findAllOperadores();
        for (int i = 0; i < list.size(); i++) {
            listOperadores.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
        }
    }

    public String inIdDepartamento() {
        String ids = "";
        if (selectedDepartamento != null) {
            ids = "";
            for (int i = 0; i < selectedDepartamento.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedDepartamento.get(i).toString();
                } else {
                    ids += "," + selectedDepartamento.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdOperadores() {
        String ids = "";
        if (selectedOperador != null) {
            ids = "";
            for (int i = 0; i < selectedOperador.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedOperador.get(i).toString();
                } else {
                    ids += "," + selectedOperador.get(i).toString();
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

    public Map<String, Integer> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(Map<String, Integer> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public List getSelectedDepartamento() {
        return selectedDepartamento;
    }

    public void setSelectedDepartamento(List selectedDepartamento) {
        this.selectedDepartamento = selectedDepartamento;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Pessoa getPessoa() {
        if (Sessions.exists("fisicaPesquisa")) {
            pessoa = ((Fisica) Sessions.getObject("fisicaPesquisa", true)).getPessoa();
            sisPessoa = new SisPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SisPessoa getSisPessoa() {
        if (Sessions.exists("sisPessoaPesquisa")) {
            pessoa = new Pessoa();
            sisPessoa = (SisPessoa) Sessions.getObject("sisPessoaPesquisa", true);
        }
        return sisPessoa;
    }

    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }

    public Map<String, Integer> getListOperadores() {
        return listOperadores;
    }

    public void setListOperadores(Map<String, Integer> listOperadores) {
        this.listOperadores = listOperadores;
    }

    public List getSelectedOperador() {
        return selectedOperador;
    }

    public void setSelectedOperador(List selectedOperador) {
        this.selectedOperador = selectedOperador;
    }

    public class ObjectExameMedico {

        private Object tipo;
        private Object nome;
        private Object emissao;
        private Object validade;
        private Object operador;
        private Object departamento;

        public ObjectExameMedico() {
            this.tipo = null;
            this.nome = null;
            this.emissao = null;
            this.validade = null;
            this.operador = null;
            this.departamento = null;
        }

        public ObjectExameMedico(Object tipo, Object nome, Object emissao, Object validade, Object operador, Object departamento) {
            this.tipo = tipo;
            this.nome = nome;
            this.emissao = emissao;
            this.validade = validade;
            this.operador = operador;
            this.departamento = departamento;
        }

        public Object getTipo() {
            return tipo;
        }

        public void setTipo(Object tipo) {
            this.tipo = tipo;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getValidade() {
            return validade;
        }

        public void setValidade(Object validade) {
            this.validade = validade;
        }

        public Object getOperador() {
            return operador;
        }

        public void setOperador(Object operador) {
            this.operador = operador;
        }

        public Object getDepartamento() {
            return departamento;
        }

        public void setDepartamento(Object departamento) {
            this.departamento = departamento;
        }

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

    public void removeFilterDate(DateFilters df) {
        listDateFilters.remove(df);
        loadDates();
    }

    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("emissao", "Emissão"));
        listDates.add(new SelectItem("validade", "Válidade"));
        listDates.add(new SelectItem("idade", "Idade"));
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

    public String getDateItemDescription(String title) {
        for (int x = 0; x < listDates.size(); x++) {
            if (title.equals(listDates.get(x).getValue().toString())) {
                return listDates.get(x).getLabel();
            }
        }
        return "";
    }

}
