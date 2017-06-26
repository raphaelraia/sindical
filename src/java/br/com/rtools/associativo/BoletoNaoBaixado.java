/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "soc_boleto_nao_baixado")
public class BoletoNaoBaixado implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_boleto", referencedColumnName = "id")
    @ManyToOne
    private Boleto boleto;
    @Column(name = "ds_motivo", length = 1000)
    private String motivo;
    @Column(name = "nr_valor_retorno")
    private double valorRetorno;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_importacao")
    private Date dtImportacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_baixa")
    private Date dtBaixa;

    public BoletoNaoBaixado() {
        this.id = -1;
        this.boleto = null;
        this.motivo = "";
        this.valorRetorno = 0;
        this.dtImportacao = null;
        this.dtBaixa = null;
    }

    public BoletoNaoBaixado(int id, Boleto boleto, String motivo, double valorRetorno, Date dtImportacao, Date dtBaixa) {
        this.id = id;
        this.boleto = boleto;
        this.motivo = motivo;
        this.valorRetorno = valorRetorno;
        this.dtImportacao = dtImportacao;
        this.dtBaixa = dtBaixa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getValorRetorno() {
        return valorRetorno;
    }

    public void setValorRetorno(double valorRetorno) {
        this.valorRetorno = valorRetorno;
    }

    public String getValorRetornoString() {
        return Moeda.converteR$Float(valorRetorno);
    }

    public void setValorRetornoString(String valorRetornoString) {
        this.valorRetorno = Moeda.converteUS$(valorRetornoString);
    }

    public Date getDtImportacao() {
        return dtImportacao;
    }

    public void setDtImportacao(Date dtImportacao) {
        this.dtImportacao = dtImportacao;
    }

    public String getDtImportacaoString() {
        return DataHoje.converteData(dtImportacao);
    }

    public void setDtImportacaoString(String dtImportacaoString) {
        this.dtImportacao = DataHoje.converte(dtImportacaoString);
    }    
    
    public Date getDtBaixa() {
        return dtBaixa;
    }

    public void setDtBaixa(Date dtBaixa) {
        this.dtBaixa = dtBaixa;
    }
    
    public String getDtBaixaString() {
        return DataHoje.converteData(dtBaixa);
    }

    public void setDtBaixaString(String dtBaixaString) {
        this.dtBaixa = DataHoje.converte(dtBaixaString);
    }
}
