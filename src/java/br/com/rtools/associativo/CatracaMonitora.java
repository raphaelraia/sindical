/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

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
@Table(name = "soc_catraca_monitora")
public class CatracaMonitora implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_catraca", referencedColumnName = "id")
    @ManyToOne
    private Catraca catraca;
    @Column(name = "nr_ping")
    private Integer ping;
    @Column(name = "is_ativo")
    private Boolean ativo;
    @Column(name = "ds_status")
    private String status;
    @Column(name = "ds_observacao")
    private String observacao;
    @Column(name = "nr_pessoa")
    private Integer pessoa;
    @Column(name = "ds_nome")
    private String nome;
    @Column(name = "ds_foto")
    private String foto;
    @Column(name = "nr_codigo_erro")
    private Integer codigoErro;
    @Column(name = "ds_mensagem")
    private String mensagem;
    @Column(name = "nr_via")
    private Integer via;
    @Column(name = "is_liberado")
    private Boolean liberado;

    public CatracaMonitora() {
        this.id = -1;
        this.catraca = new Catraca();
        this.ping = 0;
        this.ativo = false;
        this.status = "";
        this.observacao = "";
        this.pessoa = null;
        this.nome = "";
        this.foto = "";
        this.codigoErro = null;
        this.mensagem = "";
        this.via = null;
        this.liberado = false;
    }

    public CatracaMonitora(Integer id, Catraca catraca, Integer ping, Boolean ativo, String status, String observacao, Integer pessoa, String nome, String foto, Integer codigoErro, String mensagem, Integer via, Boolean liberado) {
        this.id = id;
        this.catraca = catraca;
        this.ping = ping;
        this.ativo = ativo;
        this.status = status;
        this.observacao = observacao;
        this.pessoa = pessoa;
        this.nome = nome;
        this.foto = foto;
        this.codigoErro = codigoErro;
        this.mensagem = mensagem;
        this.via = via;
        this.liberado = liberado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Catraca getCatraca() {
        return catraca;
    }

    public void setCatraca(Catraca catraca) {
        this.catraca = catraca;
    }

    public Integer getPing() {
        return ping;
    }

    public void setPing(Integer ping) {
        this.ping = ping;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getPessoa() {
        return pessoa;
    }

    public void setPessoa(Integer pessoa) {
        this.pessoa = pessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Integer getCodigoErro() {
        return codigoErro;
    }

    public void setCodigoErro(Integer codigoErro) {
        this.codigoErro = codigoErro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Integer getVia() {
        return via;
    }

    public void setVia(Integer via) {
        this.via = via;
    }

    public Boolean getLiberado() {
        return liberado;
    }

    public void setLiberado(Boolean liberado) {
        this.liberado = liberado;
    }

}
