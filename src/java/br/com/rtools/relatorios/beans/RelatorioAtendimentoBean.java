package br.com.rtools.relatorios.beans;

import br.com.rtools.atendimento.AteOperacao;
import br.com.rtools.atendimento.AteStatus;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioAtendimentoDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioAtendimentoBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;

    private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;
    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private String selectedGroups;

    private List<SelectItem> listFilial;
    private Integer idFilial;

    private Map<String, Integer> listStatus;
    private List selectedStatus;

    private Map<String, Integer> listOperacao;
    private List selectedOperacao;

    private Map<String, Integer> listAtendente;
    private List selectedAtendente;

    private Map<String, Integer> listReserva;
    private List selectedReserva;

    private List<Filters> filters;

    private Boolean compactar;

    private Juridica empresa;
    private List<Juridica> listEmpresas;
    private SisPessoa pessoa;
    private List<SisPessoa> listPessoas;

    public RelatorioAtendimentoBean() {
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        compactar = false;
        empresa = new Juridica();
        loadRelatorios();
        loadRelatoriosOrdem();
        loadFilters();
        loadFilial();

    }

    public void clear() {
        GenericaSessao.put("relatorioAtendimentoBean", new RelatorioAtendimentoBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("relatorio_ordem")) {
            loadRelatoriosOrdem();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioAtendimentoBean", new RelatorioAtendimentoBean());
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("atendente", "Atendente", false));
        filters.add(new Filters("datas", "Datas", false));
        filters.add(new Filters("empresa", "Empresa", false));
        filters.add(new Filters("filial", "Filial", true, true));
        filters.add(new Filters("operacao", "Operação", false));
        filters.add(new Filters("pessoa", "Pessoa", false));
        filters.add(new Filters("reserva", "Reserva", false));
        filters.add(new Filters("status", "Status", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters f = new Filters();
        f.setKey(filter);
        f.setActive(false);
        for (Filters f1 : filters) {
            if (f1.getKey().equals(filter)) {
                f1.setActive(false);
            }
        }
        load(f);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "status":
                listStatus = new LinkedHashMap<>();
                selectedStatus = new ArrayList<>();
                if (filter.getActive()) {
                    loadStatus();
                }
                break;
            case "operacao":
                listOperacao = new LinkedHashMap<>();
                selectedOperacao = new ArrayList<>();
                if (filter.getActive()) {
                    loadOperacao();
                }
                break;
            case "pessoa":
                pessoa = new SisPessoa();
                listPessoas = new ArrayList();
                break;
            case "empresa":
                empresa = new Juridica();
                listEmpresas = new ArrayList();
                break;
            case "datas":
                listDateFilters = new ArrayList();
                listDateFilters = new ArrayList();
                listDates = new ArrayList();
                selectedDate = "";
                typeDate = "emissao";
                startDate = "";
                finishDate = "";
                if (filter.getActive()) {
                    loadDates();
                }
                break;
            case "atendente":
                listAtendente = new LinkedHashMap<>();
                selectedAtendente = new ArrayList<>();
                if (filter.getActive()) {
                    loadAtendente();
                }
                break;
            case "reserva":
                listReserva = new LinkedHashMap<>();
                selectedReserva = new ArrayList<>();
                if (filter.getActive()) {
                    loadReserva();
                }
                break;

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

    public void addPessoa() {
        for (int i = 0; i < listPessoas.size(); i++) {
            if (Objects.equals(listPessoas.get(i).getId(), pessoa.getId())) {
                GenericaMensagem.warn("Validação", "PESSOA JÁ SELECIONADO!");
                return;
            }
        }
        listPessoas.add(pessoa);
        pessoa = new SisPessoa();
    }

    public void removePessoa() {
        pessoa = new SisPessoa();
    }

    public void removePessoa(Pessoa p) {
        listPessoas.remove(pessoa);
    }

    public void addEmpresa() {
        for (int i = 0; i < listEmpresas.size(); i++) {
            if (Objects.equals(listEmpresas.get(i).getId(), empresa.getId())) {
                GenericaMensagem.warn("Validação", "PESSOA JÁ SELECIONADO!");
                return;
            }
        }
        listEmpresas.add(empresa);
        pessoa = new SisPessoa();
    }

    public void removeEmpresa() {
        empresa = new Juridica();
    }

    public void removeEmpresa(Juridica j) {
        listEmpresas.remove(j);
    }

    public void print() {
        List<JasperObject> list = loadListJasperObject();
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return;
        }
        Collection collection = new ArrayList();
        collection.addAll(list);
        RelatorioDao db = new RelatorioDao();
        Relatorios relatorios = db.pesquisaRelatorios(idRelatorio);
        Jasper.TYPE = "default";
        Jasper.TITLE = relatorios.getNome();
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) collection);
    }

    public List<JasperObject> loadListJasperObject() {
        if (!isFiltroSelecionado()) {
            GenericaMensagem.warn("Validação", "SELECIONAR UM FILTRO PARA REALIZAR A PESQUISA!");
            return new ArrayList();
        }

        RelatorioAtendimentoDao relatorioAtendimentoDao = new RelatorioAtendimentoDao();

        if (!listRelatorioOrdem.isEmpty()) {
            Dao dao = new Dao();
            relatorioAtendimentoDao.setRelatorioOrdem((RelatorioOrdem) dao.find(new RelatorioOrdem(), idRelatorioOrdem));
        }
        RelatorioDao db = new RelatorioDao();
        Relatorios relatorios = db.pesquisaRelatorios(idRelatorio);
        relatorioAtendimentoDao.setRelatorios(relatorios);
        List list = relatorioAtendimentoDao.find(Integer.toString(idFilial), inIdStatus(), inIdOperacao(), inIdAtendente(), inIdReserva(), inIdPessoas(), inIdEmpresas(), listDateFilters);
        List<JasperObject> jos = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            jos.add(new JasperObject(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10), o.get(11)));
        }
        return jos;

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

    public Juridica getEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        if (empresa == null) {
            empresa = new Juridica();
        }
        this.empresa = empresa;
    }

    public Boolean getCompactar() {
        return compactar;
    }

    public void setCompactar(Boolean compactar) {
        this.compactar = compactar;
    }

    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("emissao", "Emissão"));
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

    // TRATAMENTO
    public String inIdPessoas() {
        String ids = null;
        if (listPessoas != null) {
            if (pessoa != null && pessoa.getId() != -1) {
                ids = "";
                ids = "" + pessoa.getId();
            }
            for (int i = 0; i < listPessoas.size(); i++) {
                if (listPessoas.get(i) != null) {
                    if (ids == null) {
                        ids = "";
                        ids = "" + listPessoas.get(i).getId();
                    } else {
                        ids += "," + listPessoas.get(i).getId();
                    }
                }
            }
        }
        return ids;
    }

    public String inIdEmpresas() {
        String ids = null;
        if (listEmpresas != null) {
            if (empresa != null && empresa.getId() != -1) {
                ids = "";
                ids = "" + empresa.getId();
            }
            for (int i = 0; i < listEmpresas.size(); i++) {
                if (listEmpresas.get(i) != null) {
                    if (ids == null) {
                        ids = "";
                        ids = "" + listEmpresas.get(i).getId();
                    } else {
                        ids += "," + listEmpresas.get(i).getId();
                    }
                }
            }
        }
        return ids;
    }

    public String inIdStatus() {
        String ids = null;
        if (selectedStatus != null) {
            for (int i = 0; i < selectedStatus.size(); i++) {
                if (selectedStatus.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedStatus.get(i);
                    } else {
                        ids += "," + selectedStatus.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdOperacao() {
        String ids = null;
        if (selectedOperacao != null) {
            for (int i = 0; i < selectedOperacao.size(); i++) {
                if (selectedOperacao.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedOperacao.get(i);
                    } else {
                        ids += "," + selectedOperacao.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdAtendente() {
        String ids = null;
        if (selectedAtendente != null) {
            for (int i = 0; i < selectedAtendente.size(); i++) {
                if (selectedAtendente.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedAtendente.get(i);
                    } else {
                        ids += "," + selectedAtendente.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdReserva() {
        String ids = null;
        if (selectedReserva != null) {
            for (int i = 0; i < selectedReserva.size(); i++) {
                if (selectedReserva.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedReserva.get(i);
                    } else {
                        ids += "," + selectedReserva.get(i);
                    }
                }
            }
        }
        return ids;
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

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public void loadRelatorios() {
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelatorio = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                idRelatorio = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
    }

    public void loadRelatoriosOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadFilial() {
        listFilial = new ArrayList<>();
        idFilial = null;
        List<Filial> list = new FilialDao().findByTabela("ate_movimento");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFilial.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public void loadStatus() {
        listStatus = new LinkedHashMap<>();
        selectedStatus = new ArrayList();
        List<AteStatus> list = new Dao().list(new AteStatus(), true);
        for (int i = 0; i < list.size(); i++) {
            listStatus.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadOperacao() {
        listOperacao = new LinkedHashMap<>();
        selectedOperacao = new ArrayList();
        List<AteOperacao> list = new Dao().list(new AteOperacao(), true);
        for (int i = 0; i < list.size(); i++) {
            listOperacao.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadAtendente() {
        listAtendente = new LinkedHashMap<>();
        selectedAtendente = new ArrayList();
        List<Usuario> list = new UsuarioDao().findByTabela("ate_movimento", "id_atendente");
        for (int i = 0; i < list.size(); i++) {
            listAtendente.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
        }
    }

    public void loadReserva() {
        listReserva = new LinkedHashMap<>();
        selectedReserva = new ArrayList();
        List<Usuario> list = new UsuarioDao().findByTabela("ate_movimento", "id_reserva");
        for (int i = 0; i < list.size(); i++) {
            listReserva.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
        }
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

    public String getDateItemDescription(String title) {
        for (int x = 0; x < listDates.size(); x++) {
            if (title.equals(listDates.get(x).getValue().toString())) {
                return listDates.get(x).getLabel();
            }
        }
        return "";
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

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Boolean isFiltroSelecionado() {
        List<Filters> list = new ArrayList();
        list.addAll(filters);
        for (Filters list1 : list) {
            if (list1.getActive()) {
                return true;
            }
        }
        return false;
    }

    public List<Juridica> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Juridica> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public SisPessoa getPessoa() {
        if (GenericaSessao.exists("sisPessoaPesquisa")) {
            pessoa = ((SisPessoa) GenericaSessao.getObject("sisPessoaPesquisa", true));
        }
        return pessoa;
    }

    public void setPessoa(SisPessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SisPessoa> getListPessoas() {
        return listPessoas;
    }

    public void setListPessoas(List<SisPessoa> listPessoas) {
        this.listPessoas = listPessoas;
    }

    public String getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(String selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public List<SelectItem> getListFilial() {
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Map<String, Integer> getListStatus() {
        return listStatus;
    }

    public void setListStatus(Map<String, Integer> listStatus) {
        this.listStatus = listStatus;
    }

    public List getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(List selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public Map<String, Integer> getListOperacao() {
        return listOperacao;
    }

    public void setListOperacao(Map<String, Integer> listOperacao) {
        this.listOperacao = listOperacao;
    }

    public List getSelectedOperacao() {
        return selectedOperacao;
    }

    public void setSelectedOperacao(List selectedOperacao) {
        this.selectedOperacao = selectedOperacao;
    }

    public Map<String, Integer> getListAtendente() {
        return listAtendente;
    }

    public void setListAtendente(Map<String, Integer> listAtendente) {
        this.listAtendente = listAtendente;
    }

    public List getSelectedAtendente() {
        return selectedAtendente;
    }

    public void setSelectedAtendente(List selectedAtendente) {
        this.selectedAtendente = selectedAtendente;
    }

    public Map<String, Integer> getListReserva() {
        return listReserva;
    }

    public void setListReserva(Map<String, Integer> listReserva) {
        this.listReserva = listReserva;
    }

    public List getSelectedReserva() {
        return selectedReserva;
    }

    public void setSelectedReserva(List selectedReserva) {
        this.selectedReserva = selectedReserva;
    }

    public class JasperObject {

        private Object filial_nome;
        private Object filial_documento;
        private Object pessoa_nome;
        private Object pessoa_documento;
        private Object emissao_data;
        private Object emissao_hora;
        private Object operacao_descricao;
        private Object status_descricao;
        private Object atendente_nome;
        private Object reserva_nome;
        private Object empresa_nome;
        private Object empresa_documento;

        public JasperObject() {
            this.filial_nome = null;
            this.filial_documento = null;
            this.pessoa_nome = null;
            this.pessoa_documento = null;
            this.emissao_data = null;
            this.emissao_hora = null;
            this.operacao_descricao = null;
            this.status_descricao = null;
            this.atendente_nome = null;
            this.reserva_nome = null;
            this.empresa_nome = null;
            this.empresa_documento = null;
        }

        public JasperObject(Object filial_nome, Object filial_documento, Object pessoa_nome, Object pessoa_documento, Object emissao_data, Object emissao_hora, Object operacao_descricao, Object status_descricao, Object atendente_nome, Object reserva_nome, Object empresa_nome, Object empresa_documento) {
            this.filial_nome = filial_nome;
            this.filial_documento = filial_documento;
            this.pessoa_nome = pessoa_nome;
            this.pessoa_documento = pessoa_documento;
            this.emissao_data = emissao_data;
            this.emissao_hora = emissao_hora;
            this.operacao_descricao = operacao_descricao;
            this.status_descricao = status_descricao;
            this.atendente_nome = atendente_nome;
            this.reserva_nome = reserva_nome;
            this.empresa_nome = empresa_nome;
            this.empresa_documento = empresa_documento;
        }

        public Object getFilial_nome() {
            return filial_nome;
        }

        public void setFilial_nome(Object filial_nome) {
            this.filial_nome = filial_nome;
        }

        public Object getFilial_documento() {
            return filial_documento;
        }

        public void setFilial_documento(Object filial_documento) {
            this.filial_documento = filial_documento;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getPessoa_documento() {
            return pessoa_documento;
        }

        public void setPessoa_documento(Object pessoa_documento) {
            this.pessoa_documento = pessoa_documento;
        }

        public Object getEmissao_data() {
            return emissao_data;
        }

        public void setEmissao_data(Object emissao_data) {
            this.emissao_data = emissao_data;
        }

        public Object getEmissao_hora() {
            return emissao_hora;
        }

        public void setEmissao_hora(Object emissao_hora) {
            this.emissao_hora = emissao_hora;
        }

        public Object getOperacao_descricao() {
            return operacao_descricao;
        }

        public void setOperacao_descricao(Object operacao_descricao) {
            this.operacao_descricao = operacao_descricao;
        }

        public Object getStatus_descricao() {
            return status_descricao;
        }

        public void setStatus_descricao(Object status_descricao) {
            this.status_descricao = status_descricao;
        }

        public Object getAtendente_nome() {
            return atendente_nome;
        }

        public void setAtendente_nome(Object atendente_nome) {
            this.atendente_nome = atendente_nome;
        }

        public Object getReserva_nome() {
            return reserva_nome;
        }

        public void setReserva_nome(Object reserva_nome) {
            this.reserva_nome = reserva_nome;
        }

        public Object getEmpresa_nome() {
            return empresa_nome;
        }

        public void setEmpresa_nome(Object empresa_nome) {
            this.empresa_nome = empresa_nome;
        }

        public Object getEmpresa_documento() {
            return empresa_documento;
        }

        public void setEmpresa_documento(Object empresa_documento) {
            this.empresa_documento = empresa_documento;
        }
    }
}
