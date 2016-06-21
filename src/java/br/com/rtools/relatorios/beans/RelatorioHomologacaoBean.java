package br.com.rtools.relatorios.beans;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.Status;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioHomologacaoDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class RelatorioHomologacaoBean implements Serializable {

    private Fisica funcionario;
    private Juridica empresa;
    private List<Juridica> listEmpresa;
    private List<Fisica> listFuncionario;
    private Usuario operador;
    private List<SelectItem>[] listSelectItem;
    private List<Filters> listFilters;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataDemissaoInicial;
    private Date dataDemissaoFinal;
    private Integer[] index;
    // 1 - AGENDADOR [web / não web / todos]
    // 2 - RECEPÇÃO
    // 3 - HOMOLOGADOR
    private String tipoUsuarioOperacional;
    private String tipo;
    private String order;
    private String sexo;
    private String tipoAgendador;
    private Boolean tipoAviso;
    private Boolean printHeader;
    private Boolean webAgendamento;

    private Map<String, Integer> listStatus;
    private List selectedStatus;

    private Boolean exportExcel;
    private String tipoPeriodo;

    @PostConstruct
    public void init() {
        listSelectItem = new ArrayList[6];
        listSelectItem[0] = new ArrayList<>();
        listSelectItem[1] = new ArrayList<>();
        listSelectItem[2] = new ArrayList<>();
        listSelectItem[3] = new ArrayList<>();
        listSelectItem[4] = new ArrayList<>();
        listSelectItem[5] = new ArrayList<>(); // CONVENCAO
        listEmpresa = new ArrayList<>(); // EMPRESA
        listFuncionario = new ArrayList<>(); // EMPRESA
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        dataDemissaoInicial = DataHoje.dataHoje();
        dataDemissaoFinal = DataHoje.dataHoje();
        index = new Integer[6];
        index[0] = null;
        index[1] = null;
        index[2] = null;
        index[3] = null;
        index[4] = null;
        index[5] = null; // CONVENCAO
        tipoAviso = null;
        tipoUsuarioOperacional = null;
        tipoAgendador = null;
        order = "";
        funcionario = new Fisica();
        empresa = new Juridica();
        operador = new Usuario();
        sexo = "";
        tipo = "todos";
        printHeader = false;
        webAgendamento = false;
        loadListaFiltro();
        loadRelatorios();
        tipoPeriodo = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioHomologacaoBean");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("usuarioPesquisa");
        GenericaSessao.remove("tipoPesquisaPessoaJuridica");
    }

    // LOAD
    public void loadListaFiltro() {
        listFilters = new ArrayList();
        /*  00 */ listFilters.add(new Filters("filial", "Filial", false));
        /*  01 */ listFilters.add(new Filters("periodo", "Período", false));
        /*  02 */ listFilters.add(new Filters("status", "Status", false));
        /*  03 */ listFilters.add(new Filters("empresa", "Empresa", false));
        /*  04 */ listFilters.add(new Filters("funcionario", "Funcionário", false));
        /*  05 */ listFilters.add(new Filters("operador", "Operador", false));
        /*  06 */ listFilters.add(new Filters("sexo", "Sexo", false));
        /*  07 */ listFilters.add(new Filters("motivo_demissao", "Motivo da demissão", false));
        /*  08 */ listFilters.add(new Filters("tipo_aviso", "Tipo de aviso", false));
        /*  10 */ listFilters.add(new Filters("order", "Ordem", false));
        /*  11 */ listFilters.add(new Filters("convencao", "Convenção", false));

    }

    public void loadRelatorios() {
        listSelectItem[0] = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = (List<Relatorios>) db.pesquisaTipoRelatorio(new Rotina().get().getId());
        loadListStatus();
        for (int i = 0; i < list.size(); i++) {
            for (Map.Entry<String, Integer> entry : listStatus.entrySet()) {
                if (selectedStatus.isEmpty()) {
                    if (entry.getValue().equals(3)) {
                        // selectedStatus.add(entry.getValue());
                        break;
                    }
                }
            }
            if (!list.get(i).getId().equals(65)) {
                listSelectItem[0].add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
        if (listSelectItem[0].isEmpty()) {
            listSelectItem[0] = new ArrayList<>();
        }
    }

    public void print() {
        print(0);
    }

    public void print(int tcase) {
        Dao dao = new Dao();
        Relatorios relatorios;
        if (!getListTipoRelatorios().isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            relatorios = rgdb.pesquisaRelatorios(index[0]);
        } else {
            GenericaMensagem.info("Sistema", "Nenhum relatório encontrado!");
            return;
        }
        if (relatorios == null) {
            return;
        }
        String detalheRelatorio = "";
        String inIdEmpresas = null;
        String inIdFuncionarios = null;
        Integer idUsuarioOperacional = null;
        Integer idFilial = null;
        String inIdStatus = null;
        Integer idMotivoDemissao = null;
        String pIStringI = "";
        String pFStringI = "";
        String sexoString = "";
        List listDetalhePesquisa = new ArrayList();
        if (listFilters.get(1).getActive()) {
            pIStringI = DataHoje.converteData(dataInicial);
            pFStringI = DataHoje.converteData(dataFinal);
            switch (tipoPeriodo) {
                case "agendamento":
                    listDetalhePesquisa.add(" Período de agendamento entre " + pIStringI + " e " + pFStringI);
                    break;
                case "emissao":
                    listDetalhePesquisa.add(" Período de emissão entre " + pIStringI + " e " + pFStringI);
                    break;
                case "demissao":
                    listDetalhePesquisa.add(" Período de demissão entre " + pIStringI + " e " + pFStringI);
                    break;
                default:
                    break;
            }
        }
        if (listFilters.get(6).getActive()) {
            if (sexo != null) {
                switch (sexo) {
                    case "M":
                        sexoString = "Masculino";
                        break;
                    case "F":
                        sexoString = "Feminino";
                        break;
                    default:
                        sexoString = "Todos";
                        break;
                }
            }
            listDetalhePesquisa.add("Sexo: " + sexoString + "");
        }
        if (listEmpresa.isEmpty() && empresa.getId() != -1) {
            inIdEmpresas = "";
            inIdEmpresas = "" + empresa.getId();
            if (empresa.getId() != -1) {
                listDetalhePesquisa.add("Empresa: " + empresa.getPessoa().getDocumento() + " - " + empresa.getPessoa().getNome());
            }
        } else if (!listEmpresa.isEmpty()) {
            inIdEmpresas = "";
            for (int i = 0; i < listEmpresa.size(); i++) {
                if (i == 0) {
                    inIdEmpresas += "" + listEmpresa.get(i).getId();
                } else {
                    inIdEmpresas += "," + listEmpresa.get(i).getId();
                }
                listDetalhePesquisa.add("Empresas: ");
                listDetalhePesquisa.add(listEmpresa.get(i).getPessoa().getDocumento() + " - " + listEmpresa.get(i).getPessoa().getNome() + "; ");
            }
        }
        if (listFuncionario.isEmpty() && funcionario.getId() != -1) {
            inIdFuncionarios = "";
            inIdFuncionarios = "" + funcionario.getId();
            if (funcionario.getId() != -1) {
                listDetalhePesquisa.add("Funcionário: " + funcionario.getPessoa().getDocumento() + " - " + funcionario.getPessoa().getNome());
            }
        } else if (!listFuncionario.isEmpty()) {
            inIdFuncionarios = "";
            for (int i = 0; i < listFuncionario.size(); i++) {
                if (i == 0) {
                    inIdFuncionarios += "" + listFuncionario.get(i).getId();
                } else {
                    inIdFuncionarios += "," + listFuncionario.get(i).getId();
                }
                listDetalhePesquisa.add("Funcionários: ");
                listDetalhePesquisa.add(listFuncionario.get(i).getPessoa().getDocumento() + " - " + listFuncionario.get(i).getPessoa().getNome() + "; ");
            }
        }
        if (operador.getId() != -1) {
            idUsuarioOperacional = operador.getId();
            listDetalhePesquisa.add("Operador: " + operador.getPessoa().getDocumento() + " - " + operador.getPessoa().getNome());
        }
        if (index[1] != null) {
            idFilial = Integer.parseInt(listSelectItem[1].get(index[1]).getDescription());
            listDetalhePesquisa.add("Filial: " + ((Filial) dao.find(new Filial(), idFilial)).getFilial().getPessoa().getNome());
        }
        if (!selectedStatus.isEmpty()) {
            inIdStatus = inIdStatus();
            if (selectedStatus.size() == 1) {
                listDetalhePesquisa.add("Status: " + ((Status) dao.find(new Status(), Integer.parseInt(selectedStatus.get(0).toString()))).getDescricao());
            }
        }
        if (index[3] != null) {
            idMotivoDemissao = Integer.parseInt(listSelectItem[3].get(index[3]).getDescription());
            listDetalhePesquisa.add("Motivo Demissão: " + ((Demissao) dao.find(new Demissao(), idMotivoDemissao)).getDescricao());
        }
        if (listFilters.get(8).getActive()) {
            if (tipoAviso != null) {
                if (tipoAviso) {
                    listDetalhePesquisa.add("Tipo de aviso: trabalhado");
                } else {
                    listDetalhePesquisa.add("Tipo de aviso: indenizado");
                }
            }
        }

        Integer idConvencao = null;
        if (listFilters.get(10).getActive()) {
            idConvencao = Integer.parseInt(listSelectItem[5].get(index[5]).getDescription());
            listDetalhePesquisa.add("Convenção: " + ((Convencao) dao.find(new Convencao(), idConvencao)).getDescricao());
        }

        if (order == null) {
            order = "";
        }
        RelatorioHomologacaoDao relatorioHomologacaoDao = new RelatorioHomologacaoDao();
        relatorioHomologacaoDao.setOrder(order);
        String operadorHeader = "";
        if (tipoUsuarioOperacional == null || tipoUsuarioOperacional.equals("id_homologador")) {
            operadorHeader = "HOMOLOGADOR";
            tipoUsuarioOperacional = "id_homologador";
        } else if (tipoUsuarioOperacional.equals("id_agendador")) {
            operadorHeader = "AGENDADOR";
        }
        relatorioHomologacaoDao.setRelatorios(relatorios);
        List list = relatorioHomologacaoDao.find(inIdEmpresas, inIdFuncionarios, tipoUsuarioOperacional, idUsuarioOperacional, inIdStatus, idFilial, tipoPeriodo, pIStringI, pFStringI, idMotivoDemissao, tipoAviso, tipoAgendador, sexo, webAgendamento, idConvencao);
        if (list.isEmpty()) {
            GenericaMensagem.info("Sistema", "Não existem registros para o relatório selecionado");
            return;
        }

        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "Detalhes: " + listDetalhePesquisa.get(i).toString();
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i).toString();
                }
            }
        }
        List<ParametroHomologacao> phs = new ArrayList<>();
        List<ParametroHomologacaoPorEscritorio> ph_por_escritorio = new ArrayList<>();
        String operadorString = "";
        for (Object list1 : list) {
            List o = (List) list1;
            if (null != relatorios.getId()) {
                switch (relatorios.getId()) {
                    case 70:
                        phs.add(new ParametroHomologacao(o.get(0), o.get(1), o.get(2), o.get(3)));
                        break;
                    case 81:
                        phs.add(new ParametroHomologacao(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10), o.get(11)));
                        Jasper.IS_HEADER_PARAMS = true;
                        break;
                    case 89:
                        ph_por_escritorio.add(new ParametroHomologacaoPorEscritorio(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10)));
                        Jasper.IS_HEADER_PARAMS = true;
                        break;
                    default:
                        if (tipoUsuarioOperacional == null || tipoUsuarioOperacional.equals("id_homologador")) {
                            operadorString = AnaliseString.converteNullString(((List) list1).get(9));
                        } else if (tipoUsuarioOperacional.equals("id_agendador")) {
                            operadorString = AnaliseString.converteNullString(((List) list1).get(9));
                            if (operadorString.isEmpty()) {
                                operadorString = "** Web ** ";
                            }
                        }
                        phs.add(new ParametroHomologacao(o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), operadorString, o.get(10), o.get(11), o.get(12), o.get(13), o.get(14)));
                        break;
                }
            }
        }

        if (!phs.isEmpty() || !ph_por_escritorio.isEmpty()) {
            Jasper.EXPORT_TO = exportExcel;
            Jasper.EXPORT_TYPE = (exportExcel ? "xls" : "pdf");
            Jasper.TYPE = "paisagem";
            Jasper.IS_HEADER = printHeader;
            Map map = new HashMap();
            map.put("operador_header", operadorHeader);
            map.put("detalhes_relatorio", detalheRelatorio);
            Jasper.TITLE = relatorios.getNome();

            if (!phs.isEmpty()) {
                Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) phs, map);
            }

            if (!ph_por_escritorio.isEmpty()) {
                Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) ph_por_escritorio, map);
            }
            new Jasper();
        }
    }

    public List<SelectItem> getListTipoRelatorios() {
        return listSelectItem[0];
    }

    public void selecionaDataInicial(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataInicial = DataHoje.converte(format.format(event.getObject()));
    }

    public void selecionaDataFinal(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataFinal = DataHoje.converte(format.format(event.getObject()));
    }

    public void loadRelatoriosStatus() {
        loadRelatoriosStatus(1);
    }

    public void loadRelatoriosStatus(Integer tcase) {
        loadRelatorios();
        if (tcase != 0) {
            clear();
        }
    }

    public void add(String tcase) {
        switch (tcase) {
            case "empresa":
                listEmpresa.add(empresa);
                empresa = new Juridica();
                break;
            case "funcionario":
                listFuncionario.add(funcionario);
                funcionario = new Fisica();
                break;
        }
    }

    public void remove(Object o) {
        switch (o.getClass().getName()) {
            case "juridica":
                listEmpresa.remove((Juridica) o);
                break;
            case "fisica":
                listFuncionario.remove((Fisica) o);
                break;
        }
    }

    public void clear() {
        if (!listFilters.get(0).getActive()) {
            listSelectItem[1] = new ArrayList();
            index[1] = null;
        }
        if (!listFilters.get(1).getActive()) {
            dataInicial = DataHoje.dataHoje();
            dataFinal = null;
        }
        if (!listFilters.get(2).getActive()) {
            loadListStatus();
        }
        if (!listFilters.get(3).getActive()) {
            empresa = new Juridica();
            listEmpresa.clear();
        }
        if (!listFilters.get(4).getActive()) {
            funcionario = new Fisica();
            listFuncionario.clear();;
        }
        if (!listFilters.get(5).getActive()) {
            operador = new Usuario();
            webAgendamento = false;
            tipoUsuarioOperacional = null;
        }
        if (!listFilters.get(6).getActive()) {
            sexo = "";
        }
        if (!listFilters.get(7).getActive()) {
            listSelectItem[3] = new ArrayList();
            index[3] = null;
        }
        if (!listFilters.get(8).getActive()) {
            tipoAviso = null;
        }
        if (!listFilters.get(9).getActive()) {
            order = "";
        }
        if (!listFilters.get(10).getActive()) {
            listSelectItem[5] = new ArrayList();
            index[5] = null;
        }

    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "filial":
                listSelectItem[1] = new ArrayList();
                index[1] = null;
                break;
            case "periodo_emissao":
                dataInicial = DataHoje.dataHoje();
                dataFinal = null;
                listFilters.get(1).setDisabled(false);
                listFilters.get(9).setDisabled(false);
                PF.update("form_relatorio:i_panel_accordion:i_panel_avancado");
                break;
            case "status":
                selectedStatus = new ArrayList();
                loadListStatus();
                break;
            case "empresa":
                empresa = new Juridica();
                listEmpresa.clear();
                break;
            case "funcionario":
                listFuncionario.clear();
                funcionario = new Fisica();
                break;
            case "operador":
                operador = new Usuario();
                webAgendamento = false;
                tipoUsuarioOperacional = null;
                break;
            case "sexo":
                sexo = "";
                break;
            case "motivo_demissao":
                listSelectItem[3] = new ArrayList();
                index[3] = null;
                break;
            case "tipo_aviso":
                tipoAviso = null;
                break;
            case "periodo_demissao":
                dataDemissaoInicial = DataHoje.dataHoje();
                dataDemissaoFinal = null;
                listFilters.get(1).setDisabled(false);
                listFilters.get(9).setDisabled(false);
                PF.update("form_relatorio:i_panel_accordion:i_panel_avancado");
                break;
            case "order":
                order = "";
                break;
            case "convencao":
                listSelectItem[5] = new ArrayList();
                index[5] = null;
                break;
        }
        PF.update("form_relatorio:id_panel");
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<SelectItem>[] getListSelectItem() {
        return listSelectItem;
    }

    public void setListSelectItem(List<SelectItem>[] listSelectItem) {
        this.listSelectItem = listSelectItem;
    }

    /**
     * <strong>Index</strong>
     * <ul>
     * <li>[0] Tipos de Relatórios</li>
     * <li>[1] List[SelectItem] Convenção Período</li>
     * </ul>
     *
     * @return Integer
     */
    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Fisica getFuncionario() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            funcionario = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true));
        }
        return funcionario;
    }

    public void setFuncionario(Fisica funcionario) {
        this.funcionario = funcionario;
    }

    public Juridica getEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTipo() {
        return tipo;
    }

    public Usuario getOperador() {
        if (GenericaSessao.exists("usuarioPesquisa")) {
            operador = ((Usuario) GenericaSessao.getObject("usuarioPesquisa", true));
        }
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public String getTipoUsuarioOperacional() {
        return tipoUsuarioOperacional;
    }

    public void setTipoUsuarioOperacional(String tipoUsuarioOperacional) {
        this.tipoUsuarioOperacional = tipoUsuarioOperacional;
    }

    public List<SelectItem> getListFiliais() {
        if (listSelectItem[1].isEmpty()) {
            Dao di = new Dao();
            List<Filial> list = (List<Filial>) di.list(new Filial(), true);
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[1].add(new SelectItem(i,
                        list.get(i).getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getPessoa().getNome(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listSelectItem[1];
    }

    public List<SelectItem> getListConvencao() {
        if (listSelectItem[5].isEmpty()) {
            List<Convencao> list = new Dao().list(new Convencao());
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[5].add(new SelectItem(
                        i,
                        list.get(i).getDescricao(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listSelectItem[5];
    }

    public void loadListStatus() {
        listStatus = new LinkedHashMap<>();
        selectedStatus = new ArrayList();
        Dao dao = new Dao();
        List<Status> list = (List<Status>) dao.list(new Status(), true);
        for (int i = 0; i < list.size(); i++) {
            listStatus.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public List<SelectItem> getListMotivoDemissao() {
        if (listSelectItem[3].isEmpty()) {
            Dao di = new Dao();
            List<Demissao> list = (List<Demissao>) di.list(new Demissao(), true);
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[3].add(new SelectItem(i,
                        list.get(i).getDescricao(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listSelectItem[3];
    }

    public Boolean getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(Boolean tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public String getTipoAgendador() {
        return tipoAgendador;
    }

    public void setTipoAgendador(String tipoAgendador) {
        this.tipoAgendador = tipoAgendador;
    }

    public Boolean getPrintHeader() {
        return printHeader;
    }

    public void setPrintHeader(Boolean printHeader) {
        this.printHeader = printHeader;
    }

    public Date getDataDemissaoInicial() {
        return dataDemissaoInicial;
    }

    public void setDataDemissaoInicial(Date dataDemissaoInicial) {
        this.dataDemissaoInicial = dataDemissaoInicial;
    }

    public Date getDataDemissaoFinal() {
        return dataDemissaoFinal;
    }

    public void setDataDemissaoFinal(Date dataDemissaoFinal) {
        this.dataDemissaoFinal = dataDemissaoFinal;
    }

    public Boolean getWebAgendamento() {
        return webAgendamento;
    }

    public void setWebAgendamento(Boolean webAgendamento) {
        this.webAgendamento = webAgendamento;
    }

    public void listener(Integer tCase) {
        if (tCase == 1) {
            if (tipoUsuarioOperacional != null && tipoUsuarioOperacional.equals("id_homologador")) {
                if (!listFilters.get(2).getActive()) {
                    listFilters.get(2).setActive(true);
                    loadListStatus();
                    for (Map.Entry<String, Integer> entry : listStatus.entrySet()) {
                        if (entry.getValue() == 4) {
                            selectedStatus.add(entry.getValue());
                            break;
                        }
                    }
                }
            }
        }
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

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<Juridica> getListEmpresa() {
        return listEmpresa;
    }

    public void setListEmpresa(List<Juridica> listEmpresa) {
        this.listEmpresa = listEmpresa;
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

    public List<Fisica> getListFuncionario() {
        return listFuncionario;
    }

    public void setListFuncionario(List<Fisica> listFuncionario) {
        this.listFuncionario = listFuncionario;
    }

    public Boolean getExportExcel() {
        return exportExcel;
    }

    public void setExportExcel(Boolean exportExcel) {
        this.exportExcel = exportExcel;
    }

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public class ParametroHomologacao {

        private Object data_final;
        private Object data;
        private Object hora;
        private Object cnpj;
        private Object empresa;
        private Object funcionario;
        private Object contato;
        private Object telefone;
        private Object operador;
        private Object obs;
        private Object status;
        private Object cancelamento_data;
        private Object cancelamento_usuario_nome;
        private Object cancelamento_motivo;
        private Object quantidade_status;

        private Object id_agendamento;
        private Object convencao;
        private Object cpf;
        private Object admissao;
        private Object demissao;
        private Object dispensa;
        private Object funcao;

        public ParametroHomologacao(Object cnpj, Object empresa, Object status, Object quantidade_status) {
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.status = status;
            try {
                this.quantidade_status = Integer.parseInt(quantidade_status.toString());
            } catch (Exception e) {
                this.quantidade_status = 0;
                e.getMessage();
            }
        }

        public ParametroHomologacao(Object data_final, Object data, Object hora, Object cnpj, Object empresa, Object funcionario, Object contato, Object telefone, Object operador, Object obs, Object status, Object cancelamento_data, Object cancelamento_usuario_nome, Object cancelamento_motivo) {
            this.data_final = data_final;
            this.data = data;
            this.hora = hora;
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.funcionario = funcionario;
            this.contato = contato;
            this.telefone = telefone;
            this.operador = operador;
            this.obs = obs;
            this.status = status;
            this.cancelamento_data = cancelamento_data;
            this.cancelamento_usuario_nome = cancelamento_usuario_nome;
            this.cancelamento_motivo = cancelamento_motivo;
        }

        public ParametroHomologacao(Object id_agendamento, Object data, Object convencao, Object cnpj, Object empresa, Object cpf, Object funcionario, Object admissao, Object demissao, Object dispensa, Object funcao, Object status) {
            this.id_agendamento = id_agendamento;
            this.data = data;
            this.convencao = convencao;
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.cpf = cpf;
            this.funcionario = funcionario;
            this.admissao = admissao;
            this.demissao = demissao;
            this.dispensa = dispensa;
            this.funcao = funcao;
            this.status = status;
        }

        public Object getData_final() {
            return data_final;
        }

        public void setData_final(Object data_final) {
            this.data_final = data_final;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Object getHora() {
            return hora;
        }

        public void setHora(Object hora) {
            this.hora = hora;
        }

        public Object getCnpj() {
            return cnpj;
        }

        public void setCnpj(Object cnpj) {
            this.cnpj = cnpj;
        }

        public Object getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Object empresa) {
            this.empresa = empresa;
        }

        public Object getFuncionario() {
            return funcionario;
        }

        public void setFuncionario(Object funcionario) {
            this.funcionario = funcionario;
        }

        public Object getContato() {
            return contato;
        }

        public void setContato(Object contato) {
            this.contato = contato;
        }

        public Object getTelefone() {
            return telefone;
        }

        public void setTelefone(Object telefone) {
            this.telefone = telefone;
        }

        public Object getOperador() {
            return operador;
        }

        public void setOperador(Object operador) {
            this.operador = operador;
        }

        public Object getObs() {
            return obs;
        }

        public void setObs(Object obs) {
            this.obs = obs;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public Object getCancelamento_data() {
            return cancelamento_data;
        }

        public void setCancelamento_data(Object cancelamento_data) {
            this.cancelamento_data = cancelamento_data;
        }

        public Object getCancelamento_usuario_nome() {
            return cancelamento_usuario_nome;
        }

        public void setCancelamento_usuario_nome(Object cancelamento_usuario_nome) {
            this.cancelamento_usuario_nome = cancelamento_usuario_nome;
        }

        public Object getCancelamento_motivo() {
            return cancelamento_motivo;
        }

        public void setCancelamento_motivo(Object cancelamento_motivo) {
            this.cancelamento_motivo = cancelamento_motivo;
        }

        public Object getQuantidade_status() {
            return quantidade_status;
        }

        public void setQuantidade_status(Object quantidade_status) {
            this.quantidade_status = quantidade_status;
        }

        public Object getId_agendamento() {
            return id_agendamento;
        }

        public void setId_agendamento(Object id_agendamento) {
            this.id_agendamento = id_agendamento;
        }

        public Object getConvencao() {
            return convencao;
        }

        public void setConvencao(Object convencao) {
            this.convencao = convencao;
        }

        public Object getCpf() {
            return cpf;
        }

        public void setCpf(Object cpf) {
            this.cpf = cpf;
        }

        public Object getAdmissao() {
            return admissao;
        }

        public void setAdmissao(Object admissao) {
            this.admissao = admissao;
        }

        public Object getDemissao() {
            return demissao;
        }

        public void setDemissao(Object demissao) {
            this.demissao = demissao;
        }

        public Object getDispensa() {
            return dispensa;
        }

        public void setDispensa(Object dispensa) {
            this.dispensa = dispensa;
        }

        public Object getFuncao() {
            return funcao;
        }

        public void setFuncao(Object funcao) {
            this.funcao = funcao;
        }
    }

    public class ParametroHomologacaoPorEscritorio {

        private Object emissao;
        private Object data;
        private Object hora;
        private Object cnpj;
        private Object empresa;
        private Object escritorio;
        private Object telefone;
        private Object email;
        private Object funcionario;
        private Object funcao;
        private Object contato;

        public ParametroHomologacaoPorEscritorio(Object emissao, Object data, Object hora, Object cnpj, Object empresa, Object escritorio, Object telefone, Object email, Object funcionario, Object funcao, Object contato) {
            this.emissao = emissao;
            this.data = data;
            this.hora = hora;
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.escritorio = escritorio;
            this.telefone = telefone;
            this.email = email;
            this.funcionario = funcionario;
            this.funcao = funcao;
            this.contato = contato;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Object getHora() {
            return hora;
        }

        public void setHora(Object hora) {
            this.hora = hora;
        }

        public Object getCnpj() {
            return cnpj;
        }

        public void setCnpj(Object cnpj) {
            this.cnpj = cnpj;
        }

        public Object getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Object empresa) {
            this.empresa = empresa;
        }

        public Object getEscritorio() {
            return escritorio;
        }

        public void setEscritorio(Object escritorio) {
            this.escritorio = escritorio;
        }

        public Object getTelefone() {
            return telefone;
        }

        public void setTelefone(Object telefone) {
            this.telefone = telefone;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }

        public Object getFuncionario() {
            return funcionario;
        }

        public void setFuncionario(Object funcionario) {
            this.funcionario = funcionario;
        }

        public Object getContato() {
            return contato;
        }

        public void setContato(Object contato) {
            this.contato = contato;
        }

        public Object getFuncao() {
            return funcao;
        }

        public void setFuncao(Object funcao) {
            this.funcao = funcao;
        }

    }
}
