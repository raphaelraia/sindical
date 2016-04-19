package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioBoletosBaixadosDao;
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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioBoletosBaixadosBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private String tipoDataBaixa;
    private String tipoDataImportacao;
    private String dtBS;
    private String dtBF;
    private String dtIS;
    private String dtIF;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        tipoDataBaixa = "todos";
        tipoDataImportacao = "todos";
        
        dtBS = "";
        dtBF = "";
        dtIS = "";
        dtIF = "";

        loadFilters();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioBoletosBaixadosBean");
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
        List<ObjectJasper> oj = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioBoletosBaixadosDao rbbd = new RelatorioBoletosBaixadosDao();
        if(idRelatorioOrdem != null) {
            rbbd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));            
        }
        rbbd.setRelatorios(r);
        List list = rbbd.find(tipoDataBaixa, dtBS, dtBF, tipoDataImportacao, dtIS, dtIF);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5)));
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = r.getNome();
        Jasper.TYPE = "default";
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) oj);
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
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("data_baixa", "Quitação", false, false));
        listFilters.add(new Filters("data_importacai", "Importação", false, false));

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
            case "data_importacao":
                if (!filter.getActive()) {
                    tipoDataImportacao = "todos";
                    dtIS = "";
                    dtIF = "";
                }
                break;
            case "data_baixa":
                if (!filter.getActive()) {
                    tipoDataBaixa = "todos";
                    dtBS = "";
                    dtBF = "";
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
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

    public String getTipoDataBaixa() {
        return tipoDataBaixa;
    }

    public void setTipoDataBaixa(String tipoDataBaixa) {
        this.tipoDataBaixa = tipoDataBaixa;
    }

    public String getTipoDataImportacao() {
        return tipoDataImportacao;
    }

    public void setTipoDataImportacao(String tipoDataImportacao) {
        this.tipoDataImportacao = tipoDataImportacao;
    }

    public String getDtBS() {
        return dtBS;
    }

    public void setDtBS(String dtBS) {
        this.dtBS = dtBS;
    }

    public String getDtBF() {
        return dtBF;
    }

    public void setDtBF(String dtBF) {
        this.dtBF = dtBF;
    }

    public String getDtIS() {
        return dtIS;
    }

    public void setDtIS(String dtIS) {
        this.dtIS = dtIS;
    }

    public String getDtIF() {
        return dtIF;
    }

    public void setDtIF(String dtIF) {
        this.dtIF = dtIF;
    }

    public class ObjectJasper {

        private Object conta;
        private Object data_quitacao;
        private Object data_importacao;
        private Object pessoa_nome;
        private Object boleto;
        private Object valor;

        public ObjectJasper() {
            this.conta = null;
            this.data_quitacao = null;
            this.data_importacao = null;
            this.pessoa_nome = null;
            this.boleto = null;
            this.valor = null;
        }

        public ObjectJasper(Object conta, Object data_quitacao, Object data_importacao, Object pessoa_nome, Object boleto, Object valor) {
            this.conta = conta;
            this.data_quitacao = data_quitacao;
            this.data_importacao = data_importacao;
            this.pessoa_nome = pessoa_nome;
            this.boleto = boleto;
            this.valor = valor;
        }

        public Object getConta() {
            return conta;
        }

        public void setConta(Object conta) {
            this.conta = conta;
        }

        public Object getData_quitacao() {
            return data_quitacao;
        }

        public void setData_quitacao(Object data_quitacao) {
            this.data_quitacao = data_quitacao;
        }

        public Object getData_importacao() {
            return data_importacao;
        }

        public void setData_importacao(Object data_importacao) {
            this.data_importacao = data_importacao;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getBoleto() {
            return boleto;
        }

        public void setBoleto(Object boleto) {
            this.boleto = boleto;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

    }

}
