/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.CatracaFrequencia;
import br.com.rtools.associativo.dao.FrequenciaCatracaDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class FrequenciaCatracaBean implements Serializable {

    private Integer indexDepartamento = 0;
    private List<SelectItem> listaDepartamento = new ArrayList();

    private Boolean chkData = false;
    private String dataInicio = "";
    private String dataFinal = "";
    private Boolean chkHora = false;
    private String horaInicio = "";
    private String horaFinal = "";

    private Pessoa pessoa = new Pessoa();
    private SisPessoa sisPessoa = new SisPessoa();
    private List<ListaFrequenciaCatraca> listaFrequenciaCatraca = new ArrayList();
    private List<CatracaFrequencia> listCatracaFrequencia = new ArrayList();
    private LineChartModel graficoFrequencia1;
    private LineChartModel graficoFrequencia2;

    private String es = "ES";

    public FrequenciaCatracaBean() {
        loadListaDepartamento();
        loadListaFrequenciaCatraca();
    }

    public void imprimir() {
        loadListaFrequenciaCatraca(true);
        Jasper.printReports("/Relatorios/FREQUENCIA_CATRACA.jasper", "Frequência Catraca", listaFrequenciaCatraca);
    }

    public final void loadListaDepartamento() {
        listaDepartamento.clear();

        List<Departamento> result = new Dao().list(new Departamento(), true);

        for (int i = 0; i < result.size(); i++) {
            listaDepartamento.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaFrequenciaCatraca() {
        loadListaFrequenciaCatraca(false);
    }

    public final void loadListaFrequenciaCatraca(Boolean relatorio) {
        listaFrequenciaCatraca.clear();

        if (!chkData) {
            dataInicio = "";
            dataFinal = "";
        }

        if (!chkHora) {
            horaInicio = "";
            horaFinal = "";
        }

        List<ArrayList> result = new FrequenciaCatracaDao().listaFrequencia(
                Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription()),
                dataInicio,
                dataFinal,
                horaInicio,
                horaFinal,
                pessoa.getId() != -1 ? pessoa.getId() : null,
                sisPessoa.getId() != -1 ? sisPessoa.getId() : null,
                es,
                relatorio
        );

        List<ArrayList> resultGraph = new FrequenciaCatracaDao().listaFrequenciaGraph(
                Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription()),
                dataInicio,
                dataFinal,
                horaInicio,
                horaFinal,
                pessoa.getId() != -1 ? pessoa.getId() : null,
                sisPessoa.getId() != -1 ? sisPessoa.getId() : null,
                es,
                relatorio
        );

        createLineModels(resultGraph);

        for (List list : result) {
            CatracaFrequencia cf = (CatracaFrequencia) new Dao().find(new CatracaFrequencia(), (Integer) list.get(0));
            listaFrequenciaCatraca.add(
                    new ListaFrequenciaCatraca(
                            (Integer) list.get(0), // ID CATRACA FREQUENCIA
                            (Integer) list.get(1), // ID DEPARTAMENTO
                            (String) list.get(2) + " " + (String) list.get(7), // NOME
                            DataHoje.converteData((Date) list.get(3)), // DATA ACESSO
                            (String) list.get(4), // HORA ACESSO
                            (String) list.get(5), // ES
                            (Integer) list.get(6), // ID PESSOA
                            (String) list.get(7), // TIPO
                            (String) list.get(8), // DEPARTAMENTO
                            (cf.getPessoa() != null) ? cf.getPessoa().getFoto() : cf.getSisPessoa().getFotoPerfil() // FOTO
                    )
            );
        }
    }

    public void removePesquisaPessoa() {
        pessoa = new Pessoa();
        loadListaFrequenciaCatraca();
    }

    public void removePesquisaSisPessoa() {
        sisPessoa = new SisPessoa();
        loadListaFrequenciaCatraca();

    }

    public Integer getIndexDepartamento() {
        return indexDepartamento;
    }

    public void setIndexDepartamento(Integer indexDepartamento) {
        this.indexDepartamento = indexDepartamento;
    }

    public List<SelectItem> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<SelectItem> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public Boolean getChkData() {
        return chkData;
    }

    public void setChkData(Boolean chkData) {
        this.chkData = chkData;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            loadListaFrequenciaCatraca();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SisPessoa getSisPessoa() {
        if (GenericaSessao.exists("sisPessoaPesquisa")) {
            sisPessoa = (SisPessoa) GenericaSessao.getObject("sisPessoaPesquisa", true);
            loadListaFrequenciaCatraca();
        }
        return sisPessoa;
    }

    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }

    public List<ListaFrequenciaCatraca> getListaFrequenciaCatraca() {
        return listaFrequenciaCatraca;
    }

    public void setListaFrequenciaCatraca(List<ListaFrequenciaCatraca> listaFrequenciaCatraca) {
        this.listaFrequenciaCatraca = listaFrequenciaCatraca;
    }

    public Boolean getChkHora() {
        return chkHora;
    }

    public void setChkHora(Boolean chkHora) {
        this.chkHora = chkHora;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public void loadListByPessoa(Integer pessoa_id) {
        listCatracaFrequencia = new FrequenciaCatracaDao().findPessoa(pessoa_id);
    }

    public void loadListBySisPessoa(Integer sis_pessoa_id) {
        listCatracaFrequencia = new FrequenciaCatracaDao().findSisPessoa(sis_pessoa_id);
    }

    public List<CatracaFrequencia> getListCatracaFrequencia() {
        return listCatracaFrequencia;
    }

    public void setListCatracaFrequencia(List<CatracaFrequencia> listCatracaFrequencia) {
        this.listCatracaFrequencia = listCatracaFrequencia;
    }

    public LineChartModel getGraficoFrequencia1() {
        return graficoFrequencia1;
    }

    public void setGraficoFrequencia1(LineChartModel graficoFrequencia1) {
        this.graficoFrequencia1 = graficoFrequencia1;
    }

    public LineChartModel getGraficoFrequencia2() {
        return graficoFrequencia2;
    }

    public void setGraficoFrequencia2(LineChartModel graficoFrequencia2) {
        this.graficoFrequencia2 = graficoFrequencia2;
    }

    public class ListaFrequenciaCatraca {

        private Integer idCatracaFrequencia;
        private Integer idDepartamento;
        private String nome;
        private String dataAcesso;
        private String horaAcesso;
        private String es;
        private Integer idPessoa;
        private String tipo;
        private String departamento;
        private String foto;

        public ListaFrequenciaCatraca() {
            this.idCatracaFrequencia = null;
            this.idDepartamento = null;
            this.nome = "";
            this.dataAcesso = "";
            this.horaAcesso = "";
            this.es = "";
            this.idPessoa = null;
            this.tipo = "";
            this.departamento = "";
            this.foto = "";
        }

        public ListaFrequenciaCatraca(Integer idCatracaFrequencia, Integer idDepartamento, String nome, String dataAcesso, String horaAcesso, String es, Integer idPessoa, String tipo, String departamento, String foto) {
            this.idCatracaFrequencia = idCatracaFrequencia;
            this.idDepartamento = idDepartamento;
            this.nome = nome;
            this.dataAcesso = dataAcesso;
            this.horaAcesso = horaAcesso;
            this.es = es;
            this.idPessoa = idPessoa;
            this.tipo = tipo;
            this.departamento = departamento;
            this.foto = foto;
        }

        public Integer getIdCatracaFrequencia() {
            return idCatracaFrequencia;
        }

        public void setIdCatracaFrequencia(Integer idCatracaFrequencia) {
            this.idCatracaFrequencia = idCatracaFrequencia;
        }

        public Integer getIdDepartamento() {
            return idDepartamento;
        }

        public void setIdDepartamento(Integer idDepartamento) {
            this.idDepartamento = idDepartamento;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getDataAcesso() {
            return dataAcesso;
        }

        public void setDataAcesso(String dataAcesso) {
            this.dataAcesso = dataAcesso;
        }

        public String getHoraAcesso() {
            return horaAcesso;
        }

        public void setHoraAcesso(String horaAcesso) {
            this.horaAcesso = horaAcesso;
        }

        public String getEs() {
            return es;
        }

        public void setEs(String es) {
            this.es = es;
        }

        public Integer getIdPessoa() {
            return idPessoa;
        }

        public void setIdPessoa(Integer idPessoa) {
            this.idPessoa = idPessoa;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getDepartamento() {
            return departamento;
        }

        public void setDepartamento(String departamento) {
            this.departamento = departamento;
        }

        public String getFoto() {
            return foto;
        }

        public void setFoto(String foto) {
            this.foto = foto;
        }

    }

    private void createLineModels(List<ArrayList> resultGraph) {
        LineChartModel model = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("");
        for (int i = 0; i < resultGraph.size(); i++) {
            List list = resultGraph.get(i);
            Integer qtde = Integer.parseInt(list.get(2).toString());
            String data = DataHoje.converteData((Date) list.get(1));
            try {
                if (list.get(3) != null) {
                    data = list.get(3).toString().substring(0, 2);
                }
            } catch (Exception e) {

            }
            series1.set(data, qtde);
        }
        model.addSeries(series1);
        graficoFrequencia1 = model;
        graficoFrequencia1.setTitle("Frequência");
        graficoFrequencia1.setLegendPosition("e");
        graficoFrequencia1.getAxes().put(AxisType.X, new CategoryAxis("Datas"));
        Axis yAxis = graficoFrequencia1.getAxis(AxisType.Y);
        // yAxis.setMin(0);
        // yAxis.setMax(10);
        yAxis.setLabel("Quantidade Acessos");

//        graficoFrequencia2 = initCategoryModel();
//        graficoFrequencia2.setTitle("Category Chart");
//        graficoFrequencia2.setLegendPosition("e");
//        graficoFrequencia2.setShowPointLabels(true);
//        graficoFrequencia2.getAxes().put(AxisType.X, new CategoryAxis("Years"));
//        yAxis = graficoFrequencia2.getAxis(AxisType.Y);
//        yAxis.setLabel("Births");
//        yAxis.setMin(0);
//        yAxis.setMax(200);
    }

    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");

        series1.set(1, 2);
        series1.set(2, 1);
        series1.set(3, 3);
        series1.set(4, 6);
        series1.set(5, 8);

        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");

        series2.set(1, 6);
        series2.set(2, 3);
        series2.set(3, 2);
        series2.set(4, 7);
        series2.set(5, 9);

        model.addSeries(series1);
        model.addSeries(series2);

        return model;
    }

    private LineChartModel initCategoryModel() {
        LineChartModel model = new LineChartModel();

        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);

        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 90);
        girls.set("2008", 120);

        model.addSeries(boys);
        model.addSeries(girls);

        return model;
    }
}
