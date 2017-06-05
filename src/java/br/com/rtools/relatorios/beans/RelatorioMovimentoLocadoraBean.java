/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.beans;

import br.com.rtools.locadoraFilme.Titulo;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioMovimentoLocadoraDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class RelatorioMovimentoLocadoraBean implements Serializable {

    private Filtros filtro = new Filtros();

    private Integer indexListaRelatorio = 0;
    private List<SelectItem> listaRelatorio = new ArrayList();

    private Integer indexListaRelatorioOrdem = 0;
    private List<SelectItem> listaRelatorioOrdem = new ArrayList();

    public RelatorioMovimentoLocadoraBean() {
        loadRelatorios();
    }

    public void limparFiltro() {
        filtro = new Filtros();
    }

    public final void loadRelatorios() {
        indexListaRelatorio = 0;
        listaRelatorio.clear();

        RelatorioDao db = new RelatorioDao();
        List<Relatorios> result = db.pesquisaTipoRelatorio(new Rotina().get().getId());

        for (int i = 0; i < result.size(); i++) {
            listaRelatorio.add(new SelectItem(i, result.get(i).getNome(), Integer.toString(result.get(i).getId())));
        }

        loadRelatoriosOrdem();
    }

    public final void loadRelatoriosOrdem() {
        indexListaRelatorioOrdem = 0;
        listaRelatorioOrdem.clear();

        RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
        List<RelatorioOrdem> result = relatorioOrdemDao.findAllByRelatorio(Integer.valueOf(listaRelatorio.get(indexListaRelatorio).getDescription()));

        for (int i = 0; i < result.size(); i++) {
            listaRelatorioOrdem.add(new SelectItem(i, result.get(i).getNome(), Integer.toString(result.get(i).getId())));
        }

    }

    public void imprimir() {
        RelatorioMovimentoLocadoraDao dao = new RelatorioMovimentoLocadoraDao();

        Relatorios re = (Relatorios) new Dao().find(new Relatorios(), Integer.valueOf(listaRelatorio.get(indexListaRelatorio).getDescription()));
        RelatorioOrdem re_o = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), Integer.valueOf(listaRelatorioOrdem.get(indexListaRelatorioOrdem).getDescription()));

        List<Object> result = dao.listaMovimentoLocadora(filtro, re, re_o);

        List<ObjetoMovimentoLocadora> list = new ArrayList();

        for (Object ob : result) {
            List linha = (List) ob;

            list.add(
                    new ObjetoMovimentoLocadora(
                            linha.get(0),
                            linha.get(1),
                            linha.get(2),
                            linha.get(3),
                            linha.get(4),
                            linha.get(5),
                            linha.get(6),
                            linha.get(7),
                            linha.get(8),
                            linha.get(9),
                            linha.get(10),
                            linha.get(11),
                            linha.get(12),
                            linha.get(13),
                            linha.get(14),
                            linha.get(15),
                            linha.get(16),
                            linha.get(17),
                            linha.get(18)
                    )
            );
        }

        Map map = new HashMap();

        if (!list.isEmpty()) {
            Jasper.printReports(re.getJasper(), re.getNome(), list);
        }
        
        GenericaMensagem.warn("Atenção", "Nenhum resultado encontrado!");
    }

    public Filtros getFiltro() {
        return filtro;
    }

    public void setFiltro(Filtros filtro) {
        this.filtro = filtro;
    }

    public Integer getIndexListaRelatorio() {
        return indexListaRelatorio;
    }

    public void setIndexListaRelatorio(Integer indexListaRelatorio) {
        this.indexListaRelatorio = indexListaRelatorio;
    }

    public List<SelectItem> getListaRelatorio() {
        return listaRelatorio;
    }

    public void setListaRelatorio(List<SelectItem> listaRelatorio) {
        this.listaRelatorio = listaRelatorio;
    }

    public Integer getIndexListaRelatorioOrdem() {
        return indexListaRelatorioOrdem;
    }

    public void setIndexListaRelatorioOrdem(Integer indexListaRelatorioOrdem) {
        this.indexListaRelatorioOrdem = indexListaRelatorioOrdem;
    }

    public List<SelectItem> getListaRelatorioOrdem() {
        return listaRelatorioOrdem;
    }

    public void setListaRelatorioOrdem(List<SelectItem> listaRelatorioOrdem) {
        this.listaRelatorioOrdem = listaRelatorioOrdem;
    }

    public class ObjetoMovimentoLocadora {

        private Object filial_cnpj;
        private Object filial_pessoa_id;
        private Object filial_nome;
        private Object cliente_id;
        private Object cliente_nome;
        private Object cliente_tel1;
        private Object cliente_tel2;
        private Object cliente_tel3;
        private Object cliente_email;
        private Object operador_id;
        private Object operador_nome;
        private Object lote;
        private Object data_locacao;
        private Object data_previsao;
        private Object data_devolucao;
        private Object operador_devolucao_id;
        private Object operador_devolucao_nome;
        private Object titulo_id;
        private Object titulo_nome;

        public ObjetoMovimentoLocadora(Object filial_cnpj, Object filial_pessoa_id, Object filial_nome, Object cliente_id, Object cliente_nome, Object cliente_tel1, Object cliente_tel2, Object cliente_tel3, Object cliente_email, Object operador_id, Object operador_nome, Object lote, Object data_locacao, Object data_previsao, Object data_devolucao, Object operador_devolucao_id, Object operador_devolucao_nome, Object titulo_id, Object titulo_nome) {
            this.filial_cnpj = filial_cnpj;
            this.filial_pessoa_id = filial_pessoa_id;
            this.filial_nome = filial_nome;
            this.cliente_id = cliente_id;
            this.cliente_nome = cliente_nome;
            this.cliente_tel1 = cliente_tel1;
            this.cliente_tel2 = cliente_tel2;
            this.cliente_tel3 = cliente_tel3;
            this.cliente_email = cliente_email;
            this.operador_id = operador_id;
            this.operador_nome = operador_nome;
            this.lote = lote;
            this.data_locacao = data_locacao;
            this.data_previsao = data_previsao;
            this.data_devolucao = data_devolucao;
            this.operador_devolucao_id = operador_devolucao_id;
            this.operador_devolucao_nome = operador_devolucao_nome;
            this.titulo_id = titulo_id;
            this.titulo_nome = titulo_nome;
        }

        public Object getFilial_cnpj() {
            return filial_cnpj;
        }

        public void setFilial_cnpj(Object filial_cnpj) {
            this.filial_cnpj = filial_cnpj;
        }

        public Object getFilial_pessoa_id() {
            return filial_pessoa_id;
        }

        public void setFilial_pessoa_id(Object filial_pessoa_id) {
            this.filial_pessoa_id = filial_pessoa_id;
        }

        public Object getFilial_nome() {
            return filial_nome;
        }

        public void setFilial_nome(Object filial_nome) {
            this.filial_nome = filial_nome;
        }

        public Object getCliente_id() {
            return cliente_id;
        }

        public void setCliente_id(Object cliente_id) {
            this.cliente_id = cliente_id;
        }

        public Object getCliente_nome() {
            return cliente_nome;
        }

        public void setCliente_nome(Object cliente_nome) {
            this.cliente_nome = cliente_nome;
        }

        public Object getCliente_tel1() {
            return cliente_tel1;
        }

        public void setCliente_tel1(Object cliente_tel1) {
            this.cliente_tel1 = cliente_tel1;
        }

        public Object getCliente_tel2() {
            return cliente_tel2;
        }

        public void setCliente_tel2(Object cliente_tel2) {
            this.cliente_tel2 = cliente_tel2;
        }

        public Object getCliente_tel3() {
            return cliente_tel3;
        }

        public void setCliente_tel3(Object cliente_tel3) {
            this.cliente_tel3 = cliente_tel3;
        }

        public Object getCliente_email() {
            return cliente_email;
        }

        public void setCliente_email(Object cliente_email) {
            this.cliente_email = cliente_email;
        }

        public Object getOperador_id() {
            return operador_id;
        }

        public void setOperador_id(Object operador_id) {
            this.operador_id = operador_id;
        }

        public Object getOperador_nome() {
            return operador_nome;
        }

        public void setOperador_nome(Object operador_nome) {
            this.operador_nome = operador_nome;
        }

        public Object getLote() {
            return lote;
        }

        public void setLote(Object lote) {
            this.lote = lote;
        }

        public Object getData_locacao() {
            return data_locacao;
        }

        public void setData_locacao(Object data_locacao) {
            this.data_locacao = data_locacao;
        }

        public Object getData_previsao() {
            return data_previsao;
        }

        public void setData_previsao(Object data_previsao) {
            this.data_previsao = data_previsao;
        }

        public Object getData_devolucao() {
            return data_devolucao;
        }

        public void setData_devolucao(Object data_devolucao) {
            this.data_devolucao = data_devolucao;
        }

        public Object getOperador_devolucao_id() {
            return operador_devolucao_id;
        }

        public void setOperador_devolucao_id(Object operador_devolucao_id) {
            this.operador_devolucao_id = operador_devolucao_id;
        }

        public Object getOperador_devolucao_nome() {
            return operador_devolucao_nome;
        }

        public void setOperador_devolucao_nome(Object operador_devolucao_nome) {
            this.operador_devolucao_nome = operador_devolucao_nome;
        }

        public Object getTitulo_id() {
            return titulo_id;
        }

        public void setTitulo_id(Object titulo_id) {
            this.titulo_id = titulo_id;
        }

        public Object getTitulo_nome() {
            return titulo_nome;
        }

        public void setTitulo_nome(Object titulo_nome) {
            this.titulo_nome = titulo_nome;
        }

    }

    public class Filtros {

        private Boolean chkFilial = true;
        private Integer indexListaFilial = 0;
        private List<SelectItem> listaFilial = new ArrayList();

        private Boolean chkCliente = false;
        private Pessoa cliente = new Pessoa();

        private Boolean chkFilme = false;
        private Titulo filme = new Titulo();

        private Boolean chkData = false;
        private String dtLocacaoInicial = "";
        private String dtLocacaoFinal = "";
        private String dtPrevisaoInicial = "";
        private String dtPrevisaoFinal = "";
        private String dtEntregaInicial = "";
        private String dtEntregaFinal = "";

        private Boolean chkStatus = false;
        private String status = "";

        public Filtros() {
            loadListaFilial();
        }

        public final void loadListaFilial() {
            indexListaFilial = 0;
            listaFilial.clear();

            List<Filial> result = new FilialDao().listaTodasFiliais();

            for (int i = 0; i < result.size(); i++) {
                listaFilial.add(new SelectItem(i, result.get(i).getFilial().getPessoa().getNome(), Integer.toString(result.get(i).getFilial().getPessoa().getId())));
            }
        }

        public void removerCliente() {
            cliente = new Pessoa();
        }

        public void removerFilme() {
            filme = new Titulo();
        }

        public Pessoa getCliente() {
            if (GenericaSessao.exists("pessoaPesquisa")) {
                cliente = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            }
            return cliente;
        }

        public void setCliente(Pessoa cliente) {
            this.cliente = cliente;
        }

        public Boolean getChkFilial() {
            return chkFilial;
        }

        public void setChkFilial(Boolean chkFilial) {
            this.chkFilial = chkFilial;
        }

        public Integer getIndexListaFilial() {
            return indexListaFilial;
        }

        public void setIndexListaFilial(Integer indexListaFilial) {
            this.indexListaFilial = indexListaFilial;
        }

        public List<SelectItem> getListaFilial() {
            return listaFilial;
        }

        public void setListaFilial(List<SelectItem> listaFilial) {
            this.listaFilial = listaFilial;
        }

        public Boolean getChkCliente() {
            return chkCliente;
        }

        public void setChkCliente(Boolean chkCliente) {
            this.chkCliente = chkCliente;
        }

        public Boolean getChkFilme() {
            return chkFilme;
        }

        public void setChkFilme(Boolean chkFilme) {
            this.chkFilme = chkFilme;
        }

        public Titulo getFilme() {
            if (GenericaSessao.exists("tituloPesquisa")) {
                filme = (Titulo) GenericaSessao.getObject("tituloPesquisa", true);
            }
            return filme;
        }

        public void setFilme(Titulo filme) {
            this.filme = filme;
        }

        public Boolean getChkData() {
            return chkData;
        }

        public void setChkData(Boolean chkData) {
            this.chkData = chkData;
        }

        public Boolean getChkStatus() {
            return chkStatus;
        }

        public void setChkStatus(Boolean chkStatus) {
            this.chkStatus = chkStatus;
        }

        public String getDtLocacaoInicial() {
            return dtLocacaoInicial;
        }

        public void setDtLocacaoInicial(String dtLocacaoInicial) {
            this.dtLocacaoInicial = dtLocacaoInicial;
        }

        public String getDtLocacaoFinal() {
            return dtLocacaoFinal;
        }

        public void setDtLocacaoFinal(String dtLocacaoFinal) {
            this.dtLocacaoFinal = dtLocacaoFinal;
        }

        public String getDtPrevisaoInicial() {
            return dtPrevisaoInicial;
        }

        public void setDtPrevisaoInicial(String dtPrevisaoInicial) {
            this.dtPrevisaoInicial = dtPrevisaoInicial;
        }

        public String getDtPrevisaoFinal() {
            return dtPrevisaoFinal;
        }

        public void setDtPrevisaoFinal(String dtPrevisaoFinal) {
            this.dtPrevisaoFinal = dtPrevisaoFinal;
        }

        public String getDtEntregaInicial() {
            return dtEntregaInicial;
        }

        public void setDtEntregaInicial(String dtEntregaInicial) {
            this.dtEntregaInicial = dtEntregaInicial;
        }

        public String getDtEntregaFinal() {
            return dtEntregaFinal;
        }

        public void setDtEntregaFinal(String dtEntregaFinal) {
            this.dtEntregaFinal = dtEntregaFinal;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

}
