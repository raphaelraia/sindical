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
import br.com.rtools.utilitarios.DaoInterface;
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
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class RelatorioHomologacaoBean implements Serializable {

    private Fisica funcionario;
    private Juridica empresa;
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

    @PostConstruct
    public void init() {
        listSelectItem = new ArrayList[6];
        listSelectItem[0] = new ArrayList<>();
        listSelectItem[1] = new ArrayList<>();
        listSelectItem[2] = new ArrayList<>();
        listSelectItem[3] = new ArrayList<>();
        listSelectItem[4] = new ArrayList<>();
        listSelectItem[5] = new ArrayList<>(); // CONVENCAO
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
        /*  01 */ listFilters.add(new Filters("periodo_emissao", "Período de emissão", false));
        /*  02 */ listFilters.add(new Filters("status", "Status", false));
        /*  03 */ listFilters.add(new Filters("empresa", "Empresa", false));
        /*  04 */ listFilters.add(new Filters("funcionario", "Funcionário", false));
        /*  05 */ listFilters.add(new Filters("operador", "Operador", false));
        /*  06 */ listFilters.add(new Filters("sexo", "Sexo", false));
        /*  07 */ listFilters.add(new Filters("motivo_demissao", "Motivo da demissão", false));
        /*  08 */ listFilters.add(new Filters("tipo_aviso", "Tipo de aviso", false));
        /*  09 */ listFilters.add(new Filters("periodo_demissao", "Período de demissão", false));
        /*  10 */ listFilters.add(new Filters("order", "Ordem", false));
        /*  11 */ listFilters.add(new Filters("convencao", "Convenção", false));

    }

    public void loadRelatorios() {
        listSelectItem[0] = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = (List<Relatorios>) db.pesquisaTipoRelatorio(new Rotina().get().getId());
        getListStatus();
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(listSelectItem[2].get(index[2]).getDescription()) == 3) {
                if (list.get(i).getId().equals(65)) {
                    listSelectItem[0].add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                }
            } else {
                if (!list.get(i).getId().equals(65)) {
                    listSelectItem[0].add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                }
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
        Integer idEmpresa = null;
        Integer idFuncionario = null;
        Integer idUsuarioOperacional = null;
        Integer idFilial = null;
        Integer idStatus = null;
        Integer idMotivoDemissao = null;
        String pIStringI = "";
        String pFStringI = "";
        String sexoString = "";
        List listDetalhePesquisa = new ArrayList();
        Integer tCase = null;
        if (listFilters.get(1).getActive()) {
            tCase = 1;
            pIStringI = DataHoje.converteData(dataInicial);
            pFStringI = DataHoje.converteData(dataFinal);
            listDetalhePesquisa.add(" Período de Agendamento entre " + pIStringI + " e " + pFStringI);
        } else if (listFilters.get(9).getActive()) {
            tCase = 2;
            pIStringI = DataHoje.converteData(dataInicial);
            pFStringI = DataHoje.converteData(dataFinal);
            listDetalhePesquisa.add(" Período de Demissão entre " + pIStringI + " e " + pFStringI);
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
        if (empresa.getId() != -1) {
            idEmpresa = empresa.getId();
            listDetalhePesquisa.add("Empresa: " + empresa.getPessoa().getDocumento() + " - " + empresa.getPessoa().getNome());
        }
        if (funcionario.getId() != -1) {
            idFuncionario = funcionario.getId();
            listDetalhePesquisa.add("Funcionário: " + funcionario.getPessoa().getDocumento() + " - " + funcionario.getPessoa().getNome());
        }
        if (operador.getId() != -1) {
            idUsuarioOperacional = operador.getId();
            listDetalhePesquisa.add("Operador: " + operador.getPessoa().getDocumento() + " - " + operador.getPessoa().getNome());
        }
        if (index[1] != null) {
            idFilial = Integer.parseInt(listSelectItem[1].get(index[1]).getDescription());
            listDetalhePesquisa.add("Filial: " + ((Filial) dao.find(new Filial(), idFilial)).getFilial().getPessoa().getNome());
        }
        if (index[2] != null) {
            idStatus = Integer.parseInt(listSelectItem[2].get(index[2]).getDescription());
            listDetalhePesquisa.add("Status: " + ((Status) dao.find(new Status(), idStatus)).getDescricao());
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
        if (listFilters.get(11).getActive()) {
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
        List list = relatorioHomologacaoDao.find(relatorios, idEmpresa, idFuncionario, tipoUsuarioOperacional, idUsuarioOperacional, idStatus, idFilial, tCase, pIStringI, pFStringI, idMotivoDemissao, tipoAviso, tipoAgendador, sexo, webAgendamento, idConvencao);
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
        String operadorString = "";
        for (Object list1 : list) {
            List o = (List) list1;
            if (tipoUsuarioOperacional == null || tipoUsuarioOperacional.equals("id_homologador")) {
                operadorString = AnaliseString.converteNullString(((List) list1).get(9));
            } else if (tipoUsuarioOperacional.equals("id_agendador")) {
                operadorString = AnaliseString.converteNullString(((List) list1).get(9));
                if (operadorString.isEmpty()) {
                    operadorString = "** Web ** ";
                }
            }
            phs.add(new ParametroHomologacao(o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), operadorString, o.get(10), o.get(11), o.get(12), o.get(13), o.get(13)));
        }
        if (!phs.isEmpty()) {
            Jasper.TYPE = "paisagem";
            Jasper.IS_HEADER = printHeader;
            Map map = new HashMap();
            map.put("operador_header", operadorHeader);
            map.put("detalhes_relatorio", detalheRelatorio);
            Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) phs, map);
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

    public void clear() {
        if (!listFilters.get(0).getActive()) {
            listSelectItem[1] = new ArrayList();
            index[1] = null;
        }
        if (!listFilters.get(1).getActive()) {
            if (!listFilters.get(9).getActive()) {
                dataInicial = DataHoje.dataHoje();
                dataFinal = null;
                listFilters.get(1).setDisabled(false);
                listFilters.get(9).setDisabled(false);
            }
        } else {
            listFilters.get(1).setDisabled(false);
            listFilters.get(9).setDisabled(true);
            listFilters.get(9).setActive(false);
        }
        if (!listFilters.get(2).getActive()) {
            listSelectItem[2] = new ArrayList();
            index[2] = null;
        }
        if (!listFilters.get(3).getActive()) {
            empresa = new Juridica();
        }
        if (!listFilters.get(4).getActive()) {
            funcionario = new Fisica();
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
            if (!listFilters.get(1).getActive()) {
                listFilters.get(1).setDisabled(false);
                listFilters.get(9).setDisabled(false);
                dataDemissaoInicial = DataHoje.dataHoje();
                dataDemissaoInicial = null;
            }
        } else {
            listFilters.get(1).setDisabled(false);
            listFilters.get(9).setDisabled(false);
            listFilters.get(1).setActive(false);
        }
        if (!listFilters.get(10).getActive()) {
            order = "";
        }
        if (!listFilters.get(11).getActive()) {
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
                listSelectItem[2] = new ArrayList();
                index[2] = null;
                break;
            case "empresa":
                empresa = new Juridica();
                break;
            case "funcionario":
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
            DaoInterface di = new Dao();
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

    public List<SelectItem> getListStatus() {
        if (listSelectItem[2].isEmpty()) {
            DaoInterface di = new Dao();
            List<Status> list = (List<Status>) di.list(new Status(), true);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    index[2] = i;
                }
                listSelectItem[2].add(new SelectItem(i,
                        list.get(i).getDescricao(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listSelectItem[2];
    }

    public List<SelectItem> getListMotivoDemissao() {
        if (listSelectItem[3].isEmpty()) {
            DaoInterface di = new Dao();
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
                    getListStatus();
                    for (int i = 0; i < listSelectItem[2].size(); i++) {
                        if (Integer.parseInt(listSelectItem[2].get(i).getDescription()) == 4) {
                            index[2] = i;
                            break;
                        }
                    }
                }
            }
        }
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
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

    }
}
