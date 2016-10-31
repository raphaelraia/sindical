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
@Table(name = "fin_impressora_cheque")
public class ImpressoraCheque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "nr_impressora")
    private Integer impressora;
    @Column(name = "ds_apelido")
    private String apelido;
    @Column(name = "is_ativo")
    private Boolean ativo;
    @Column(name = "ds_banco")
    private String banco;
    @Column(name = "ds_valor")
    private String valor;
    @Column(name = "ds_favorecido")
    private String favorecido;
    @Column(name = "ds_cidade")
    private String cidade;
    @Column(name = "ds_data")
    private String data;
    @Column(name = "ds_mensagem")
    private String mensagem;
    @Column(name = "ds_mensagem_erro")
    private String mensagemErro;

    public ImpressoraCheque() {
        this.id = -1;
        this.impressora = null;
        this.apelido = "";
        this.ativo = false;
        this.banco = "";
        this.valor = "";
        this.favorecido = "";
        this.cidade = "";
        this.data = "";
        this.mensagem = "";
        this.mensagemErro = "";
    }

    public ImpressoraCheque(int id, Integer impressora, String apelido, Boolean ativo, String banco, String valor, String favorecido, String cidade, String data, String mensagem, String mensagemErro) {
        this.id = id;
        this.impressora = impressora;
        this.apelido = apelido;
        this.ativo = ativo;
        this.banco = banco;
        this.valor = valor;
        this.favorecido = favorecido;
        this.cidade = cidade;
        this.data = data;
        this.mensagem = mensagem;
        this.mensagemErro = mensagemErro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImpressora() {
        return impressora;
    }

    public void setImpressora(Integer impressora) {
        this.impressora = impressora;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFavorecido() {
        return favorecido;
    }

    public void setFavorecido(String favorecido) {
        this.favorecido = favorecido;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

}
