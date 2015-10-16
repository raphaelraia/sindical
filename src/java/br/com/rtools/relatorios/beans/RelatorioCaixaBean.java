package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCaixaDao;
import br.com.rtools.relatorios.dao.RelatorioCobrancaExternaDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class RelatorioCaixaBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private Map<String, Integer> listCaixa;
    private List selectedCaixa;

    private Map<String, Integer> listOperador;
    private List selectedOperador;

    private Map<String, Integer> listTipoPagamento;
    private List selectedTipoPagamento;

    private Date dtBaixaInicial;
    private Date dtBaixaFinal;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        listTipoPagamento = null;
        selectedTipoPagamento = new ArrayList<>();

        listCaixa = null;
        selectedOperador = new ArrayList<>();

        listOperador = null;
        selectedOperador = new ArrayList<>();

        listRelatorioOrdem = new ArrayList<>();
        idRelatorioOrdem = null;

        dtBaixaInicial = null;
        dtBaixaFinal = null;

        loadCaixa();
        loadOperador();
        loadListaFiltro();
        loadRelatorio();
        loadRelatorioOrdem();
        loadTipoPagamento();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCaixaBean");
    }

    public void print() {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        String dt_i = null;
        String dt_f = null;
        String inIdCaixa = null;
        String inIdTipoPagamento = null;
        String inIdOperador = null;
        if (listFilters.get(0).getActive()) {
            inIdCaixa = inIdCaixa();
        }
        if (listFilters.get(1).getActive()) {
            inIdOperador = inIdOperador();
        }
        if (listFilters.get(2).getActive()) {
            inIdTipoPagamento = inIdTipoPagamento();
        }
        if (listFilters.get(3).getActive()) {
            dt_i = getBaixaInicialString();
            dt_f = getBaixaFinalString();
        }
        List<RelatorioCaixa> rcs = new ArrayList<>();
        sisProcesso.startQuery();
        List list = new RelatorioCaixaDao().find(r.getId(), inIdCaixa, inIdOperador, inIdTipoPagamento, dt_i, dt_f);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (r.getId() == 60) {
                rcs.add(
                        new RelatorioCaixa(o.get(0), o.get(1), o.get(2), o.get(3))
                );
            } else if (r.getId() == 61) {
                rcs.add(
                        new RelatorioCaixa(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4))
                );
            }
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.TITLE = r.getNome();
        Jasper.TYPE = "default";
        Jasper.printReports(r.getJasper(), r.getNome(), rcs);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    // LOAD
    public void loadRelatorio() {
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
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
        if (idRelatorio != null) {
            listRelatorioOrdem.clear();
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

    public void loadTipoPagamento() {
        listTipoPagamento = new LinkedHashMap<>();
        selectedTipoPagamento = new ArrayList();
        List<TipoPagamento> list = new Dao().list(new TipoPagamento(), true);
        listTipoPagamento.put("Selecionar", null);
        if (list != null) {
            for (TipoPagamento list1 : list) {
                listTipoPagamento.put(list1.getDescricao(), list1.getId());
            }
        }
    }

    public void loadCaixa() {
        listCaixa = new LinkedHashMap<>();
        selectedCaixa = new ArrayList();
        List<Caixa> list = new Dao().list(new Caixa(), true);
        listCaixa.put("Selecionar", null);
        if (list != null) {
            for (Caixa list1 : list) {
                listCaixa.put("Caixa: " + list1.getCaixa() + " - " + list1.getDescricao(), list1.getId());
            }
        }
    }

    public void loadOperador() {
        listOperador = new LinkedHashMap<>();
        selectedOperador = new ArrayList();
        List<Usuario> list = new Dao().list(new Usuario(), true);
        listOperador.put("Selecionar", null);
        if (list != null) {
            for (Usuario list1 : list) {
                listOperador.put(list1.getPessoa().getNome(), list1.getId());
            }
        }
    }

    public void loadListaFiltro() {
        listFilters.clear();
        listFilters.add(new Filters("caixa", "Caixa", false));
        listFilters.add(new Filters("operador", "Operador", false));
        listFilters.add(new Filters("tipo_pagamento", "Tipo de Pagmamento", false));
        listFilters.add(new Filters("periodo", "Período", false));
    }

    // LISTENERS
    public void clear(Filters filter) {
        filter.setActive(!filter.getActive());
        switch (filter.getKey()) {
            case "caixa":
                selectedCaixa = null;
                break;
            case "operador":
                selectedOperador = null;
                break;
            case "periodo":
                dtBaixaInicial = null;
                dtBaixaFinal = null;
                break;
            case "tipo_pagamento":
                loadTipoPagamento();
                break;
        }
    }

    public void close(Filters filter) {
        if (!filter.getActive()) {
            switch (filter.getKey()) {
                case "caixa":
                    selectedCaixa = null;
                    break;
                case "operador":
                    selectedOperador = null;
                    break;
                case "periodo":
                    dtBaixaInicial = null;
                    dtBaixaFinal = null;
                    break;
                case "tipo_pagamento":
                    loadTipoPagamento();
                    break;
            }
        }
    }

    // TRATAMENTO
    public String inIdTipoPagamento() {
        String ids = null;
        if (selectedTipoPagamento != null) {
            for (int i = 0; i < selectedTipoPagamento.size(); i++) {
                if (selectedTipoPagamento.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedTipoPagamento.get(i);
                    } else {
                        ids += "," + selectedTipoPagamento.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdOperador() {
        String ids = null;
        if (selectedOperador != null) {
            for (int i = 0; i < selectedOperador.size(); i++) {
                if (selectedOperador.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedOperador.get(i);
                    } else {
                        ids += "," + selectedOperador.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdCaixa() {
        String ids = null;
        if (selectedCaixa != null) {
            for (int i = 0; i < selectedCaixa.size(); i++) {
                if (selectedCaixa.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedCaixa.get(i);
                    } else {
                        ids += "," + selectedCaixa.get(i);
                    }
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

    public Map<String, Integer> getListTipoPagamento() {
        return listTipoPagamento;
    }

    public void setListTipoPagamento(Map<String, Integer> listTipoPagamento) {
        this.listTipoPagamento = listTipoPagamento;
    }

    public List getSelectedTipoPagamento() {
        return selectedTipoPagamento;
    }

    public void setSelectedTipoPagamento(List selectedTipoPagamento) {
        this.selectedTipoPagamento = selectedTipoPagamento;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public Map<String, Integer> getListCaixa() {
        return listCaixa;
    }

    public void setListCaixa(Map<String, Integer> listCaixa) {
        this.listCaixa = listCaixa;
    }

    public List getSelectedCaixa() {
        return selectedCaixa;
    }

    public void setSelectedCaixa(List selectedCaixa) {
        this.selectedCaixa = selectedCaixa;
    }

    public Map<String, Integer> getListOperador() {
        return listOperador;
    }

    public void setListOperador(Map<String, Integer> listOperador) {
        this.listOperador = listOperador;
    }

    public List getSelectedOperador() {
        return selectedOperador;
    }

    public void setSelectedOperador(List selectedOperador) {
        this.selectedOperador = selectedOperador;
    }

    public void selecionaDataBaixaInicial(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dtBaixaInicial = DataHoje.converte(format.format(event.getObject()));
    }

    public void selecionaDataBaixaFinal(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dtBaixaFinal = DataHoje.converte(format.format(event.getObject()));
    }

    public Date getDtBaixaInicial() {
        return dtBaixaInicial;
    }

    public void setDtBaixaInicial(Date dtBaixaInicial) {
        this.dtBaixaInicial = dtBaixaInicial;
    }

    public Date getDtBaixaFinal() {
        return dtBaixaFinal;
    }

    public void setDtBaixaFinal(Date dtBaixaFinal) {
        this.dtBaixaFinal = dtBaixaFinal;
    }

    public String getBaixaInicialString() {
        return DataHoje.converteData(dtBaixaInicial);
    }

    public void setBaixaInicialString(String baixaInicialString) {
        this.dtBaixaInicial = DataHoje.converte(baixaInicialString);
    }

    public String getBaixaFinalString() {
        return DataHoje.converteData(dtBaixaFinal);
    }

    public void setBaixaFinalString(String baixaFinalString) {
        this.dtBaixaFinal = DataHoje.converte(baixaFinalString);
    }

    public class RelatorioCaixa {

        private Object baixa;
        private Object fechamento_caixa;
        private Object caixa;
        private Object operador;
        private Object valor;

        public RelatorioCaixa(Object baixa, Object fechamento_caixa, Object caixa, Object valor) {
            this.baixa = baixa;
            this.fechamento_caixa = fechamento_caixa;
            this.caixa = caixa;
            this.valor = valor;
        }

        public RelatorioCaixa(Object baixa, Object fechamento_caixa, Object caixa, Object operador, Object valor) {
            this.baixa = baixa;
            this.fechamento_caixa = fechamento_caixa;
            this.caixa = caixa;
            this.operador = operador;
            this.valor = valor;
        }

        public Object getBaixa() {
            return baixa;
        }

        public void setBaixa(Object baixa) {
            this.baixa = baixa;
        }

        public Object getFechamento_caixa() {
            return fechamento_caixa;
        }

        public void setFechamento_caixa(Object fechamento_caixa) {
            this.fechamento_caixa = fechamento_caixa;
        }

        public Object getCaixa() {
            return caixa;
        }

        public void setCaixa(Object caixa) {
            this.caixa = caixa;
        }

        public Object getOperador() {
            return operador;
        }

        public void setOperador(Object operador) {
            this.operador = operador;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

    }
}
