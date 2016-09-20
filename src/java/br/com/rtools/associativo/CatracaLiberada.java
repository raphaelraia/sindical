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
@Table(name = "soc_catraca_liberada")
public class CatracaLiberada implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_pessoa")
    private Integer nrPessoa;
    @Column(name = "ds_cartao")
    private String cartao;
    @JoinColumn(name = "id_catraca", referencedColumnName = "id")
    @ManyToOne
    private Catraca catraca;

    public CatracaLiberada() {
        this.id = -1;
        this.nrPessoa = null;
        this.cartao = "";
        this.catraca = new Catraca();
    }

    public CatracaLiberada(Integer id, Integer nrPessoa, String cartao, Catraca catraca) {
        this.id = id;
        this.nrPessoa = nrPessoa;
        this.cartao = cartao;
        this.catraca = catraca;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNrPessoa() {
        return nrPessoa;
    }

    public void setNrPessoa(Integer nrPessoa) {
        this.nrPessoa = nrPessoa;
    }

    public String getCartao() {
        return cartao;
    }

    public void setCartao(String cartao) {
        this.cartao = cartao;
    }

    public Catraca getCatraca() {
        return catraca;
    }

    public void setCatraca(Catraca catraca) {
        this.catraca = catraca;
    }

}
