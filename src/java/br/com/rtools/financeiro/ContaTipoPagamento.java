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
 * @author claudemir
 */
@Entity
@Table(name = "fin_conta_tipo_pagamento")
public class ContaTipoPagamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_tipo_pagamento", referencedColumnName = "id")
    @ManyToOne
    private TipoPagamento tipoPagamento;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;
    @Column(name = "is_recebimento")
    private boolean recebimento;

    public ContaTipoPagamento() {
        this.id = -1;
        this.tipoPagamento = new TipoPagamento();
        this.plano5 = null;
        this.recebimento = true;
    }

    public ContaTipoPagamento(int id, TipoPagamento tipoPagamento, Plano5 plano5, boolean recebimento) {
        this.id = id;
        this.tipoPagamento = tipoPagamento;
        this.plano5 = plano5;
        this.recebimento = recebimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public boolean isRecebimento() {
        return recebimento;
    }

    public void setRecebimento(boolean recebimento) {
        this.recebimento = recebimento;
    }

}
