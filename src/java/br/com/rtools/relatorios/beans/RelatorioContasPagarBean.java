package br.com.rtools.relatorios.beans;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioContasPagarDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioContasPagarBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private String tipoDataPagamento;
    private String tipoDataVencimento;
    private String tipoDataEmissao;
    private String dtPI;
    private String dtPF;
    private String dtVI;
    private String dtVF;
    private String dtEI;
    private String dtEF;

    private Map<String, Integer> listContaCobranca;
    private List selectedContaCobranca;

    private Map<String, Integer> listContas;
    private List selectedContas;

    private Map<String, Integer> listFiliais;
    private List selectedFiliais;

    private Pessoa credor;
    private List listCredores;

    @PostConstruct
    public void init() {

        new Jasper().init();

        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        tipoDataPagamento = "todos";
        tipoDataVencimento = "todos";
        tipoDataEmissao = "todos";

        dtPI = "";
        dtEI = "";
        dtVI = "";
        dtPF = "";
        dtEF = "";
        dtVF = "";

        credor = null;
        listCredores = new ArrayList();

        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioContasPagarBean");
        GenericaSessao.remove("pessoaPesquisa");
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
        List listDetalhePesquisa = new ArrayList();
        List<ObjectJasper> oj = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioContasPagarDao rcpd = new RelatorioContasPagarDao();
        if (idRelatorioOrdem != null) {
            rcpd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
        }
        if (!dtVI.isEmpty()) {
            listDetalhePesquisa.add("Período de vencimento " + dtVI + (!dtVF.isEmpty() ? " até ".concat(dtVF) : ""));
        }
        if (!dtPI.isEmpty()) {
            listDetalhePesquisa.add("Período de pagamento " + dtPI + (!dtPF.isEmpty() ? " até ".concat(dtPF) : ""));
        }
        if (!dtEI.isEmpty()) {
            listDetalhePesquisa.add("Período de emissão " + dtEI + (!dtEF.isEmpty() ? " até ".concat(dtEF) : ""));
        }
        rcpd.setRelatorios(r);
        String in_credores = null;
        String in_filiais = inIdFiliais();
        // String in_conta_cobranca = inIdContaCobranca();
        String in_contas = inIdContas();
        if (credor != null) {
            // listDetalhePesquisa.add("Credor: " + credor.getNome());
            in_credores = credor.getId() + "";
        }
        if (in_filiais != null) {
            if (selectedFiliais != null) {
                String fs = "";
                for (int i = 0; i < selectedFiliais.size(); i++) {
                    Filial f = (Filial) new Dao().find(new Filial(), new Integer(selectedFiliais.get(i).toString()));
                    fs += f.getFilial().getPessoa().getNome() + "; ";
                }
                listDetalhePesquisa.add("Filial(s): " + fs);
            }
        }
        if (in_contas != null) {
            // listDetalhePesquisa.add("Filial: ");
            // in_contas = credor.getId() + "";
        }
        List list = rcpd.find(in_filiais, in_contas, in_credores, tipoDataPagamento, dtPI, dtPF, tipoDataVencimento, dtVI, dtVF, tipoDataEmissao, dtEI, dtEF);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9)));
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        String detalheRelatorio = "";
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
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = "RELATÓRIO " + r.getNome().toUpperCase();
        Jasper.TYPE = "default";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) oj, map);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
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
                if (list.get(i).getPrincipal()) {
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
        listFilters.add(new Filters("vencimento", "Vencimento", false, false));
        listFilters.add(new Filters("pagamento", "Pagamento", false, ((idRelatorio != null && idRelatorio == 95) ? false : true)));
        listFilters.add(new Filters("emissao", "Emissão", false, false));
        listFilters.add(new Filters("credor", "Credor", false, false));
        listFilters.add(new Filters("filial", "Filiais", false, false));
        listFilters.add(new Filters("conta", "Contas", false, false));

    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadRelatorioOrdem();
                loadFilters();
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "vencimento":
                if (!filter.getActive()) {
                    tipoDataVencimento = "todos";
                    dtVI = "";
                    dtVF = "";
                }
                break;
            case "pagamemnto":
                if (!filter.getActive()) {
                    tipoDataPagamento = "todos";
                    dtPI = "";
                    dtPF = "";
                }
                break;
            case "emissao":
                if (!filter.getActive()) {
                    tipoDataEmissao = "todos";
                    dtEI = "";
                    dtEF = "";
                }
                break;
            case "conta":
                if (!filter.getActive()) {
                    listContaCobranca = new LinkedHashMap<>();
                    selectedContaCobranca = new ArrayList<>();
                    listContas = new LinkedHashMap<>();
                    selectedContas = new ArrayList<>();
                } else {
                    loadListContaCobranca();
                    loadListContas();
                }
                break;
            case "filial":
                if (!filter.getActive()) {
                    listFiliais = new LinkedHashMap<>();
                    selectedFiliais = new ArrayList<>();
                } else {
                    loadListFiliais();
                }
                break;
            case "credor":
                if (!filter.getActive()) {
                    credor = null;
                    listCredores = new ArrayList();
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void loadListFiliais() {
        listFiliais = new LinkedHashMap<>();
        selectedFiliais = new ArrayList<>();
        List<Filial> list = new Dao().list(new Filial(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listFiliais.put(list.get(i).getFilial().getPessoa().getNome(), list.get(i).getId());
            }
        }
    }

    public void loadListContaCobranca() {
        listContaCobranca = new LinkedHashMap<>();
        selectedContaCobranca = new ArrayList<>();
        List list = new RelatorioContasPagarDao().findAllContaOperacaoGroup();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                listContaCobranca.put(o.get(1).toString(), new Integer(o.get(0).toString()));
            }
        }
    }

    public void loadListContas() {
        listContas = new LinkedHashMap<>();
        selectedContas = new ArrayList<>();
        String in_conta_cobranca = inIdContaCobranca();
        if (in_conta_cobranca != null) {
            List list = new RelatorioContasPagarDao().findByContaOperacao(in_conta_cobranca);
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                listContas.put(o.get(1).toString() + " (" + o.get(2).toString() + ")", new Integer(o.get(0).toString()));
            }
        }
    }

    // TRATAMENTO
    public String inIdFiliais() {
        String ids = null;
        if (selectedFiliais != null) {
            for (int i = 0; i < selectedFiliais.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedFiliais.get(i);
                } else {
                    ids += "," + selectedFiliais.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdContaCobranca() {
        String ids = null;
        if (selectedContaCobranca != null) {
            for (int i = 0; i < selectedContaCobranca.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedContaCobranca.get(i);
                } else {
                    ids += "," + selectedContaCobranca.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdContas() {
        String ids = null;
        if (selectedContas != null) {
            for (int i = 0; i < selectedContas.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedContas.get(i);
                } else {
                    ids += "," + selectedContas.get(i);
                }
            }
        }
        return ids;
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

    public String getTipoDataPagamento() {
        return tipoDataPagamento;
    }

    public void setTipoDataPagamento(String tipoDataPagamento) {
        this.tipoDataPagamento = tipoDataPagamento;
    }

    public String getTipoDataVencimento() {
        return tipoDataVencimento;
    }

    public void setTipoDataVencimento(String tipoDataVencimento) {
        this.tipoDataVencimento = tipoDataVencimento;
    }

    public String getTipoDataEmissao() {
        return tipoDataEmissao;
    }

    public void setTipoDataEmissao(String tipoDataEmissao) {
        this.tipoDataEmissao = tipoDataEmissao;
    }

    public String getDtPI() {
        return dtPI;
    }

    public void setDtPI(String dtPI) {
        this.dtPI = dtPI;
    }

    public String getDtPF() {
        return dtPF;
    }

    public void setDtPF(String dtPF) {
        this.dtPF = dtPF;
    }

    public String getDtVI() {
        return dtVI;
    }

    public void setDtVI(String dtVI) {
        this.dtVI = dtVI;
    }

    public String getDtVF() {
        return dtVF;
    }

    public void setDtVF(String dtVF) {
        this.dtVF = dtVF;
    }

    public String getDtEI() {
        return dtEI;
    }

    public void setDtEI(String dtEI) {
        this.dtEI = dtEI;
    }

    public String getDtEF() {
        return dtEF;
    }

    public void setDtEF(String dtEF) {
        this.dtEF = dtEF;
    }

    public Map<String, Integer> getListContas() {
        return listContas;
    }

    public void setListContas(Map<String, Integer> listContas) {
        this.listContas = listContas;
    }

    public List getSelectedContas() {
        return selectedContas;
    }

    public void setSelectedContas(List selectedContas) {
        this.selectedContas = selectedContas;
    }

    public Map<String, Integer> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(Map<String, Integer> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public List getSelectedFiliais() {
        return selectedFiliais;
    }

    public void setSelectedFiliais(List selectedFiliais) {
        this.selectedFiliais = selectedFiliais;
    }

    public Pessoa getCredor() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            credor = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
        }
        return credor;
    }

    public void setCredor(Pessoa credor) {
        this.credor = credor;
    }

    public List getListCredores() {
        return listCredores;
    }

    public void setListCredores(List listCredores) {
        this.listCredores = listCredores;
    }

    public Map<String, Integer> getListContaCobranca() {
        return listContaCobranca;
    }

    public void setListContaCobranca(Map<String, Integer> listContaCobranca) {
        this.listContaCobranca = listContaCobranca;
    }

    public List getSelectedContaCobranca() {
        return selectedContaCobranca;
    }

    public void setSelectedContaCobranca(List selectedContaCobranca) {
        this.selectedContaCobranca = selectedContaCobranca;
    }

    public class ObjectJasper {

        private Object filial;
        private Object emissao;
        private Object documento;
        private Object total;
        private Object credor;
        private Object conta;
        private Object valor;
        private Object vencimento;
        private Object valor_pago;
        private Object baixa;

        public ObjectJasper() {
            this.filial = null;
            this.emissao = null;
            this.documento = null;
            this.total = null;
            this.credor = null;
            this.conta = null;
            this.valor = null;
            this.vencimento = null;
            this.valor_pago = null;
            this.baixa = null;
        }

        public ObjectJasper(Object filial, Object emissao, Object documento, Object total, Object credor, Object conta, Object valor, Object vencimento, Object valor_pago, Object baixa) {
            this.filial = filial;
            this.emissao = emissao;
            this.documento = documento;
            this.total = total;
            this.credor = credor;
            this.conta = conta;
            this.valor = valor;
            this.vencimento = vencimento;
            this.valor_pago = valor_pago;
            this.baixa = baixa;
        }

        public Object getFilial() {
            return filial;
        }

        public void setFilial(Object filial) {
            this.filial = filial;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getDocumento() {
            return documento;
        }

        public void setDocumento(Object documento) {
            this.documento = documento;
        }

        public Object getTotal() {
            return total;
        }

        public void setTotal(Object total) {
            this.total = total;
        }

        public Object getCredor() {
            return credor;
        }

        public void setCredor(Object credor) {
            this.credor = credor;
        }

        public Object getConta() {
            return conta;
        }

        public void setConta(Object conta) {
            this.conta = conta;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
        }

        public Object getValor_pago() {
            return valor_pago;
        }

        public void setValor_pago(Object valor_pago) {
            this.valor_pago = valor_pago;
        }

        public Object getBaixa() {
            return baixa;
        }

        public void setBaixa(Object baixa) {
            this.baixa = baixa;
        }

    }

}
