package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioBalanceteDao;
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

@ManagedBean
@SessionScoped
public class RelatorioBalanceteBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listDataInicial;
    private List<SelectItem> listDataFinal;

    private String dataInicial;
    private String dataFinal;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        dataInicial = "";
        dataFinal = "";
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioBalanceteBean");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        if (look()) {
            GenericaMensagem.warn("Validação", "Selecione um filtro!");
            return;
        }
        if (dataInicial.isEmpty() || dataFinal.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar datas!");
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
        RelatorioBalanceteDao rbd = new RelatorioBalanceteDao();
        if (idRelatorioOrdem != null) {
            RelatorioOrdem ro = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
            rbd.setRelatorioOrdem(ro);
        }
        rbd.setRelatorios(r);
        List list = rbd.find("faixa", dataInicial, dataFinal);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10), o.get(11), o.get(12), o.get(13), o.get(14)));
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        String detalheRelatorio = "";
        listDetalhePesquisa.add("Período: " + dataInicial + " até " + dataFinal);
        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "" + listDetalhePesquisa.get(i).toString();
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i).toString();
                }
            }
        }
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = r.getNome().toUpperCase();
        Jasper.TYPE = "contabil";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        map.put("operador", Usuario.getUsuario().getPessoa().getNome());
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
        listFilters.add(new Filters("data", "Data", true, true));
        load(listFilters.get(0));

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
                loadListDataInicial();
                loadListDataFinal(false);
                break;
            case 3:
                loadListDataFinal();
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
            case "data":
                dataInicial = "";
                dataFinal = "";
                if (filter.getActive()) {
                    loadListDataInicial();
                    loadListDataFinal(false);
                } else {
                    listDataInicial = new ArrayList();
                    listDataFinal = new ArrayList();
                }
                break;
        }
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

    public void loadListDataInicial() {
        listDataInicial = new ArrayList();
        dataInicial = "";
        List list = new RelatorioBalanceteDao().findMaxDates("");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                String d = DataHoje.converteData((Date) o.get(0));
                if (i == 0) {
                    dataInicial = d;
                }
                listDataInicial.add(new SelectItem(d, d));
            }
        }
    }

    public void loadListDataFinal() {
        loadListDataFinal(false);
    }

    public void loadListDataFinal(Boolean ignore) {
        listDataFinal = new ArrayList();
        dataFinal = "";
        List list = new RelatorioBalanceteDao().findMaxDates(ignore ? "" : dataInicial);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                String d = DataHoje.converteData((Date) o.get(0));
                if (i == 0) {
                    dataFinal = d;
                }
                listDataFinal.add(new SelectItem(d, d));
            }
        }
    }

    public List<SelectItem> getListDataInicial() {
        return listDataInicial;
    }

    public void setListDataInicial(List<SelectItem> listDataInicial) {
        this.listDataInicial = listDataInicial;
    }

    public List<SelectItem> getListDataFinal() {
        return listDataFinal;
    }

    public void setListDataFinal(List<SelectItem> listDataFinal) {
        this.listDataFinal = listDataFinal;
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
        private Object codigo1;
        private Object conta1;
        private Object codigo2;
        private Object conta2;
        private Object codigo3;
        private Object conta3;
        private Object codigo4;
        private Object conta4;
        private Object codigo5;
        private Object conta5;
        private Object saldo_anterior;
        private Object debito;
        private Object credito;
        private Object saldo_atual;

        public ObjectJasper() {
            this.data = null;
            this.codigo1 = null;
            this.conta1 = null;
            this.codigo2 = null;
            this.conta2 = null;
            this.codigo3 = null;
            this.conta3 = null;
            this.codigo4 = null;
            this.conta4 = null;
            this.codigo5 = null;
            this.conta5 = null;
            this.saldo_anterior = null;
            this.debito = null;
            this.credito = null;
            this.saldo_atual = null;
        }

        public ObjectJasper(Object data, Object codigo1, Object conta1, Object codigo2, Object conta2, Object codigo3, Object conta3, Object codigo4, Object conta4, Object codigo5, Object conta5, Object saldo_anterior, Object debito, Object credito, Object saldo_atual) {
            this.data = data;
            this.codigo1 = codigo1;
            this.conta1 = conta1;
            this.codigo2 = codigo2;
            this.conta2 = conta2;
            this.codigo3 = codigo3;
            this.conta3 = conta3;
            this.codigo4 = codigo4;
            this.conta4 = conta4;
            this.codigo5 = codigo5;
            this.conta5 = conta5;
            this.saldo_anterior = saldo_anterior;
            this.debito = debito;
            this.credito = credito;
            this.saldo_atual = saldo_atual;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Object getCodigo1() {
            return codigo1;
        }

        public void setCodigo1(Object codigo1) {
            this.codigo1 = codigo1;
        }

        public Object getConta1() {
            return conta1;
        }

        public void setConta1(Object conta1) {
            this.conta1 = conta1;
        }

        public Object getCodigo2() {
            return codigo2;
        }

        public void setCodigo2(Object codigo2) {
            this.codigo2 = codigo2;
        }

        public Object getConta2() {
            return conta2;
        }

        public void setConta2(Object conta2) {
            this.conta2 = conta2;
        }

        public Object getCodigo3() {
            return codigo3;
        }

        public void setCodigo3(Object codigo3) {
            this.codigo3 = codigo3;
        }

        public Object getConta3() {
            return conta3;
        }

        public void setConta3(Object conta3) {
            this.conta3 = conta3;
        }

        public Object getCodigo4() {
            return codigo4;
        }

        public void setCodigo4(Object codigo4) {
            this.codigo4 = codigo4;
        }

        public Object getConta4() {
            return conta4;
        }

        public void setConta4(Object conta4) {
            this.conta4 = conta4;
        }

        public Object getCodigo5() {
            return codigo5;
        }

        public void setCodigo5(Object codigo5) {
            this.codigo5 = codigo5;
        }

        public Object getConta5() {
            return conta5;
        }

        public void setConta5(Object conta5) {
            this.conta5 = conta5;
        }

        public Object getSaldo_anterior() {
            return saldo_anterior;
        }

        public void setSaldo_anterior(Object saldo_anterior) {
            this.saldo_anterior = saldo_anterior;
        }

        public Object getDebito() {
            return debito;
        }

        public void setDebito(Object debito) {
            this.debito = debito;
        }

        public Object getCredito() {
            return credito;
        }

        public void setCredito(Object credito) {
            this.credito = credito;
        }

        public Object getSaldo_atual() {
            return saldo_atual;
        }

        public void setSaldo_atual(Object saldo_atual) {
            this.saldo_atual = saldo_atual;
        }

    }

}
