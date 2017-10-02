/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "fin_retorno_banco")
public class RetornoBanco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_retorno", referencedColumnName = "id")
    @ManyToOne
    private Retorno retorno;
    @Column(name = "ds_boleto")
    private String boleto;
    @JoinColumn(name = "id_status_retorno", referencedColumnName = "id")
    @ManyToOne
    private StatusRetorno statusRetorno;

    public RetornoBanco() {
        this.id = -1;
        this.retorno = new Retorno();
        this.boleto = "";
        this.statusRetorno = new StatusRetorno();
    }

    public RetornoBanco(int id, Retorno retorno, String boleto, StatusRetorno statusRetorno) {
        this.id = id;
        this.retorno = retorno;
        this.boleto = boleto;
        this.statusRetorno = statusRetorno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Retorno getRetorno() {
        return retorno;
    }

    public void setRetorno(Retorno retorno) {
        this.retorno = retorno;
    }

    public String getBoleto() {
        return boleto;
    }

    public void setBoleto(String boleto) {
        this.boleto = boleto;
    }

    public StatusRetorno getStatusRetorno() {
        return statusRetorno;
    }

    public void setStatusRetorno(StatusRetorno statusRetorno) {
        this.statusRetorno = statusRetorno;
    }

}
