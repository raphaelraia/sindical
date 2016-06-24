package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.FStatus;
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

@ManagedBean
@SessionScoped
public class RelatorioMovimentoDiarioBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listCaixaBanco;
    private Integer idCaixaBanco;

    private Map<String, Integer> listFStatus;
    private List selectedFStatus;

    private List<SelectItem> listDatas;
    private String data;
    private Boolean disabledData;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        listDatas = new ArrayList<>();
        disabledData = false;
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioMovimentoDiarioBean");
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
        if (data.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar uma data!");
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
        rpd.setRelatorios(r);
        List list = rpd.find(idCaixaBanco, inIdFStatus(), data);
        sisProcesso.finishQuery();
        Double saldo = new Double(0);
        Double saldo_anterior = rpd.findSaldoAnterior(data, idCaixaBanco);
        if (saldo_anterior == null) {
            saldo_anterior = new Double(0);
        }
        oj.add(new ObjectJasper(DataHoje.dataHojeSQL(), "SALDO ANTERIOR", "", saldo_anterior > 0 ? saldo_anterior : 0, saldo_anterior < 0 ? saldo_anterior : 0, saldo_anterior, "", -1));
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (i == 0) {
                saldo = saldo_anterior + Double.parseDouble(o.get(3).toString()) + (Double.parseDouble(o.get(4).toString()));
            } else {
                saldo = saldo + Double.parseDouble(o.get(3).toString()) + (Double.parseDouble(o.get(4).toString()));
            }
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), saldo, o.get(6), o.get(7)));
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
        map.put("saldo_anterior", saldo_anterior);
        map.put("detalhes_relatorio", detalheRelatorio);
        map.put("caixa_banco", "CAIXA / BANCO");
        if (idCaixaBanco != null) {
            Plano5 p = (Plano5) new Dao().find(new Plano5(), idCaixaBanco);
            map.put("caixa_banco", p.getConta());
        }
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
        listFilters.add(new Filters("caixa_banco", "Caixa Banco", false));
        listFilters.add(new Filters("data", "Data", true, true));
        listFilters.add(new Filters("fstatus", "Status", false));
        load(listFilters.get(1));

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
                // List o = (List) list.get(0);
                // DataHoje.converteData((Date) o.get(0))                
                loadListDatas();
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "caixa_banco":
                idCaixaBanco = null;
                if (!filter.getActive()) {
                    listCaixaBanco = new ArrayList<>();
                } else {
                    loadListCaixaBanco();
                }
                loadListDatas();
                break;
            case "data":
                data = "";
                if (!filter.getActive()) {
                    listDatas = new ArrayList<>();
                } else {
                    loadListDatas();
                }
                break;
            case "fstatus":
                if (!filter.getActive()) {
                    listFStatus = new LinkedHashMap<>();
                    selectedFStatus = new ArrayList<>();
                } else {
                    loadListFStatus();
                }
                break;
        }
    }

    public void loadListCaixaBanco() {
        listCaixaBanco = new ArrayList();
        idCaixaBanco = null;
        List<Plano5> list = new RelatorioMovimentoDiarioDao().findPlano5();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getConta().toUpperCase().equals("CAIXA")) {
                    idCaixaBanco = list.get(i).getId();
                }
                listCaixaBanco.add(new SelectItem(list.get(i).getId(), list.get(i).getConta()));
            }
            if (idCaixaBanco == null) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idCaixaBanco = list.get(i).getId();
                        break;
                    }
                }
            }
        }
    }

    public void loadListDatas() {
        listDatas = new ArrayList();
        data = null;
        List list = new RelatorioMovimentoDiarioDao().findMaxDates(idCaixaBanco);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                String d = DataHoje.converteData((Date) o.get(0));
                if (i == 0) {
                    data = d;
                }
                listDatas.add(new SelectItem(d, d));
            }
        }
        if (GenericaSessao.exists("dataRFD")) {
            data = GenericaSessao.getString("dataRFD", true);
            disabledData = true;
        }
    }

    public void loadListFStatus() {
        listFStatus = new LinkedHashMap<>();
        selectedFStatus = new ArrayList<>();
        List<FStatus> list = new Dao().find("FStatus", new int[]{7, 8, 9, 10, 11}, "", "OB.descricao");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listFStatus.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdFStatus() {
        String ids = null;
        if (selectedFStatus != null) {
            for (int i = 0; i < selectedFStatus.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedFStatus.get(i);
                } else {
                    ids += "," + selectedFStatus.get(i);
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
     * 0 CAIXA / BANCO; 1 Data
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

    public List<SelectItem> getListDatas() {
        return listDatas;
    }

    public void setListDatas(List<SelectItem> listDatas) {
        this.listDatas = listDatas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<SelectItem> getListCaixaBanco() {
        return listCaixaBanco;
    }

    public void setListCaixaBanco(List<SelectItem> listCaixaBanco) {
        this.listCaixaBanco = listCaixaBanco;
    }

    public Integer getIdCaixaBanco() {
        return idCaixaBanco;
    }

    public void setIdCaixaBanco(Integer idCaixaBanco) {
        this.idCaixaBanco = idCaixaBanco;
    }

    public Map<String, Integer> getListFStatus() {
        return listFStatus;
    }

    public void setListFStatus(Map<String, Integer> listFStatus) {
        this.listFStatus = listFStatus;
    }

    public List getSelectedFStatus() {
        return selectedFStatus;
    }

    public void setSelectedFStatus(List selectedFStatus) {
        this.selectedFStatus = selectedFStatus;
    }

    public Boolean getDisabledData() {
        return disabledData;
    }

    public void setDisabledData(Boolean disabledData) {
        this.disabledData = disabledData;
    }

    public class ObjectJasper {

        private Object data;
        private Object operacao;
        private Object historico;
        private Object entrada;
        private Object saida;
        private Object saldo_acumulado;
        private Object fstatus;
        private Object fstatus_id;

        public ObjectJasper() {
            this.data = null;
            this.operacao = null;
            this.historico = null;
            this.entrada = null;
            this.saida = null;
            this.saldo_acumulado = null;
            this.fstatus = null;
            this.fstatus_id = null;
        }

        public ObjectJasper(Object data, Object operacao, Object historico, Object entrada, Object saida, Object saldo_acumulado, Object fstatus, Object fstatus_id) {
            this.data = data;
            this.operacao = operacao;
            this.historico = historico;
            this.entrada = entrada;
            this.saida = saida;
            this.saldo_acumulado = saldo_acumulado;
            this.fstatus = fstatus;
            this.fstatus_id = fstatus_id;
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

        public Object getFstatus() {
            return fstatus;
        }

        public void setFstatus(Object fstatus) {
            this.fstatus = fstatus;
        }

        public Object getFstatus_id() {
            return fstatus_id;
        }

        public void setFstatus_id(Object fstatus_id) {
            this.fstatus_id = fstatus_id;
        }

    }

}
