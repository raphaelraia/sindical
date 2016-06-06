package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioMovimentoDiarioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
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
public class RelatorioMovimentoDiarioBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private String tipoData;
    private String dataInicial;
    private String dataFinal;

    private Map<String, Integer> listCaixaBanco;
    private List selectedCaixaBanco;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        tipoData = "todos";
        dataInicial = "";
        dataFinal = "";
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioMovimentoDiario");
        GenericaSessao.remove("pessoaPesquisa");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        if (look()) {
            GenericaMensagem.warn("Validação", "Selecione um filtro!");
            return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        List listDetalhePesquisa = new ArrayList();
        List<ObjectJasper> oj = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioMovimentoDiarioDao rpd = new RelatorioMovimentoDiarioDao();
        if (idRelatorioOrdem != null) {
            RelatorioOrdem ro = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
            rpd.setRelatorioOrdem(ro);
        }
        if (!dataInicial.isEmpty()) {
            listDetalhePesquisa.add("Período de vencimento " + dataInicial + (!dataFinal.isEmpty() ? " até ".concat(dataFinal) : ""));
        }
        rpd.setRelatorios(r);
        List list = rpd.find(inIdCaixaBanco(), tipoData, dataInicial, dataFinal);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5)));
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
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void load() {
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("caixa_banco", "Caixa Banco", false, false));
        listFilters.add(new Filters("data", "Data", false, false));

    }

    public boolean look() {
        for (int i = 0; i < listFilters.size(); i++) {
            if (listFilters.get(i).getActive()) {
                return false;
            }
        }
        return true;
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadFilters();
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "caixa_banco":
                if (!filter.getActive()) {
                    listCaixaBanco = new LinkedHashMap<>();
                    selectedCaixaBanco = new ArrayList<>();
                } else {
                    loadListCaixaBanco();
                }
                break;
            case "data":
                if (!filter.getActive()) {
                    tipoData = "";
                    dataInicial = "";
                    dataFinal = "";
                } else {
                    tipoData = "igual";
                    dataInicial = DataHoje.data();
                    dataFinal = "";
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void loadListCaixaBanco() {
        listCaixaBanco = new LinkedHashMap<>();
        selectedCaixaBanco = new ArrayList<>();
        List<Plano5> list = new RelatorioMovimentoDiarioDao().findPlano5();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listCaixaBanco.put(list.get(i).getConta(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdCaixaBanco() {
        String ids = null;
        if (selectedCaixaBanco != null) {
            for (int i = 0; i < selectedCaixaBanco.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedCaixaBanco.get(i);
                } else {
                    ids += "," + selectedCaixaBanco.get(i);
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

    public Map<String, Integer> getListCaixaBanco() {
        return listCaixaBanco;
    }

    public void setListCaixaBanco(Map<String, Integer> listCaixaBanco) {
        this.listCaixaBanco = listCaixaBanco;
    }

    public List getSelectedCaixaBanco() {
        return selectedCaixaBanco;
    }

    public void setSelectedCaixaBanco(List selectedCaixaBanco) {
        this.selectedCaixaBanco = selectedCaixaBanco;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public class ObjectJasper {

        private Object data;
        private Object operacao;
        private Object historico;
        private Object entrada;
        private Object saida;
        private Object saldo_acumulado;

        public ObjectJasper() {
            this.data = null;
            this.operacao = null;
            this.historico = null;
            this.entrada = null;
            this.saida = null;
            this.saldo_acumulado = null;
        }

        public ObjectJasper(Object data, Object operacao, Object historico, Object entrada, Object saida, Object saldo_acumulado) {
            this.data = data;
            this.operacao = operacao;
            this.historico = historico;
            this.entrada = entrada;
            this.saida = saida;
            this.saldo_acumulado = saldo_acumulado;
        }

        public Object getOperacao() {
            return operacao;
        }

        public void setOperacao(Object operacao) {
            this.operacao = operacao;
        }

        public Object getHistorico() {
            return historico;
        }

        public void setHistorico(Object historico) {
            this.historico = historico;
        }

        public Object getEntrada() {
            return entrada;
        }

        public void setEntrada(Object entrada) {
            this.entrada = entrada;
        }

        public Object getSaida() {
            return saida;
        }

        public void setSaida(Object saida) {
            this.saida = saida;
        }

        public Object getSaldo_acumulado() {
            return saldo_acumulado;
        }

        public void setSaldo_acumulado(Object saldo_acumulado) {
            this.saldo_acumulado = saldo_acumulado;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

    }

}
