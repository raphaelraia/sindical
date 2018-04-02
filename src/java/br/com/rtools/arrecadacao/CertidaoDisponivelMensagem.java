/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Filial;
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
@Table(name = "arr_certidao_disponivel_mensagem")
public class CertidaoDisponivelMensagem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_certidao_disponivel", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CertidaoDisponivel certidaoDisponivel;
    @JoinColumn(name = "id_convencao_periodo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ConvencaoPeriodo convencaoPeriodo;
    @Column(name = "ds_observacao", length = 8000)
    private String observacao;

    public CertidaoDisponivelMensagem() {
        this.id = -1;
        this.certidaoDisponivel = new CertidaoDisponivel();
        this.convencaoPeriodo = new ConvencaoPeriodo();
        this.observacao = "";
    }

    public CertidaoDisponivelMensagem(Integer id, CertidaoDisponivel certidaoDisponivel, ConvencaoPeriodo convencaoPeriodo, String observacao) {
        this.id = id;
        this.certidaoDisponivel = certidaoDisponivel;
        this.convencaoPeriodo = convencaoPeriodo;
        this.observacao = observacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CertidaoDisponivel getCertidaoDisponivel() {
        return certidaoDisponivel;
    }

    public void setCertidaoDisponivel(CertidaoDisponivel certidaoDisponivel) {
        this.certidaoDisponivel = certidaoDisponivel;
    }

    public ConvencaoPeriodo getConvencaoPeriodo() {
        return convencaoPeriodo;
    }

    public void setConvencaoPeriodo(ConvencaoPeriodo convencaoPeriodo) {
        this.convencaoPeriodo = convencaoPeriodo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
