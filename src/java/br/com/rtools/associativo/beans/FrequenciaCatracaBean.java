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
}
