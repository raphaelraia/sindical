/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.dao.MovimentoBancarioDao;
import br.com.rtools.financeiro.dao.MovimentoCaixaDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
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
public class MovimentoCaixaBean implements Serializable {

    private Integer indexData = 0;
    private List<SelectItem> listaData = new ArrayList();

    private List<ObjectSaldo> listaSaldo = new ArrayList();
    private ObjectSaldo objectSaldo = new ObjectSaldo();
    private List<ObjectSaldoDetalhe> listaSaldoDetalhe = new ArrayList();
    private ContaSaldo contaSaldo = new ContaSaldo();

    public MovimentoCaixaBean() {

        loadListaDatas();
        loadListaSaldo();

    }

    public void loadListaSaldoDetalhe() {
        listaSaldoDetalhe.clear();

        List<Object> result = new MovimentoCaixaDao().listaSaldoDetalhe(objectSaldo.getDataString());
        Dao dao = new Dao();

        for (Object ob : result) {
            List linha = (List) ob;

            listaSaldoDetalhe.add(
                    new ObjectSaldoDetalhe(
                            (Date) linha.get(0),
                            linha.get(1).toString(),
                            linha.get(2).toString(),
                            ((Double) linha.get(3)).floatValue(),
                            linha.get(4).toString(),
                            (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(5)),
                            (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(6)),
                            (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(7))
                    )
            );
        }
    }

    public void selecionarSaldo(ObjectSaldo os) {
        objectSaldo = os;

        loadListaSaldoDetalhe();
    }

    public final void loadListaDatas() {
        listaData.clear();
        contaSaldo = new ContaSaldo();

        List<Date> result = new MovimentoCaixaDao().listaDatas();

        String dataSelecionada = new MovimentoCaixaDao().pesquisaDataSelecionada();

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);
            listaData.add(
                    new SelectItem(
                            i,
                            DataHoje.converteData((Date) linha.get(0))
                    )
            );

            if (DataHoje.converteData((Date) linha.get(0)).equals(dataSelecionada)) {
                indexData = i;
            }
        }
    }

    public final void loadListaSaldo() {
        listaSaldo.clear();

        String dataSelecionada = new DataHoje().decrementarDias(1, listaData.get(indexData).getLabel());
        contaSaldo = new MovimentoBancarioDao().pesquisaContaSaldoData(dataSelecionada, 1);

        List<Object> result = new MovimentoCaixaDao().listaSaldo(listaData.get(indexData).getLabel());

        Float valor_saldo_atual;
        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);
            if (listaSaldo.isEmpty()) {
                Float valor_saldo_anterior = contaSaldo.getSaldo();
                valor_saldo_atual = Moeda.somaValores(valor_saldo_anterior, ((Double) linha.get(1)).floatValue());
            } else {
                Float valor_saldo_anterior = listaSaldo.get(i - 1).getSaldoAtual();
                valor_saldo_atual = Moeda.somaValores(valor_saldo_anterior, ((Double) linha.get(1)).floatValue());
            }

            listaSaldo.add(
                    new ObjectSaldo(
                            (Date) linha.get(0),
                            ((Double) linha.get(1)).floatValue(),
                            valor_saldo_atual
                    )
            );
        }
    }

    public Integer getIndexData() {
        return indexData;
    }

    public void setIndexData(Integer indexData) {
        this.indexData = indexData;
    }

    public List<SelectItem> getListaData() {
        return listaData;
    }

    public void setListaData(List<SelectItem> listaData) {
        this.listaData = listaData;
    }

    public List<ObjectSaldo> getListaSaldo() {
        return listaSaldo;
    }

    public void setListaSaldo(List<ObjectSaldo> listaSaldo) {
        this.listaSaldo = listaSaldo;
    }

    public ObjectSaldo getObjectSaldo() {
        return objectSaldo;
    }

    public void setObjectSaldo(ObjectSaldo objectSaldo) {
        this.objectSaldo = objectSaldo;
    }

    public List<ObjectSaldoDetalhe> getListaSaldoDetalhe() {
        return listaSaldoDetalhe;
    }

    public void setListaSaldoDetalhe(List<ObjectSaldoDetalhe> listaSaldoDetalhe) {
        this.listaSaldoDetalhe = listaSaldoDetalhe;
    }

    public ContaSaldo getContaSaldo() {
        return contaSaldo;
    }

    public void setContaSaldo(ContaSaldo contaSaldo) {
        this.contaSaldo = contaSaldo;
    }

    public class ObjectSaldo {

        private Date data;
        private Float valor;
        private Float saldoAtual;

        public ObjectSaldo() {
            this.data = null;
            this.valor = (float) 0;
            this.saldoAtual = (float) 0;
        }

        public ObjectSaldo(Date data, Float valor, Float saldoAtual) {
            this.data = data;
            this.valor = valor;
            this.saldoAtual = saldoAtual;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public String getDataString() {
            return DataHoje.converteData(data);
        }

        public void setData(String dataString) {
            this.data = DataHoje.converte(dataString);
        }

        public Float getValor() {
            return valor;
        }

        public void setValor(Float valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteR$Float(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

        public Float getSaldoAtual() {
            return saldoAtual;
        }

        public void setSaldoAtual(Float saldoAtual) {
            this.saldoAtual = saldoAtual;
        }

        public String getSaldoAtualString() {
            return Moeda.converteR$Float(saldoAtual);
        }

        public void setSaldoAtualString(String saldoAtualString) {
            this.saldoAtual = Moeda.converteUS$(saldoAtualString);
        }
    }

    public class ObjectSaldoDetalhe {

        private Date data;
        private String operacao;
        private String es;
        private Float valor;
        private String historico;
        private Pessoa responsavel;
        private Pessoa titular;
        private Pessoa beneficiario;

        public ObjectSaldoDetalhe() {
            this.data = null;
            this.operacao = "";
            this.es = "";
            this.valor = (float) 0;
            this.historico = "";
            this.responsavel = new Pessoa();
            this.titular = new Pessoa();
            this.beneficiario = new Pessoa();
        }

        public ObjectSaldoDetalhe(Date data, String operacao, String es, Float valor, String historico, Pessoa responsavel, Pessoa titular, Pessoa beneficiario) {
            this.data = data;
            this.operacao = operacao;
            this.es = es;
            this.valor = valor;
            this.historico = historico;
            this.responsavel = responsavel;
            this.titular = titular;
            this.beneficiario = beneficiario;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public String getDataString() {
            return DataHoje.converteData(data);
        }

        public void setData(String dataString) {
            this.data = DataHoje.converte(dataString);
        }

        public Float getValor() {
            return valor;
        }

        public void setValor(Float valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteR$Float(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

        public String getOperacao() {
            return operacao;
        }

        public void setOperacao(String operacao) {
            this.operacao = operacao;
        }

        public String getEs() {
            return es;
        }

        public void setEs(String es) {
            this.es = es;
        }

        public String getHistorico() {
            return historico;
        }

        public void setHistorico(String historico) {
            this.historico = historico;
        }

        public Pessoa getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(Pessoa responsavel) {
            this.responsavel = responsavel;
        }

        public Pessoa getTitular() {
            return titular;
        }

        public void setTitular(Pessoa titular) {
            this.titular = titular;
        }

        public Pessoa getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Pessoa beneficiario) {
            this.beneficiario = beneficiario;
        }
    }
}
