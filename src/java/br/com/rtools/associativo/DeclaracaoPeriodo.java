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
@Table(name = "soc_declaracao_periodo")
public class DeclaracaoPeriodo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_declaracao_tipo", referencedColumnName = "id")
    @ManyToOne
    private DeclaracaoTipo declaracaoTipo;
    @Column(name = "ds_descricao", length = 255)
    private String descricao;
    @Column(name = "nr_ano")
    private Integer ano;

    public DeclaracaoPeriodo() {
        this.id = -1;
        this.declaracaoTipo = new DeclaracaoTipo();
        this.descricao = "";
        this.ano = null;
    }

    public DeclaracaoPeriodo(int id, DeclaracaoTipo declaracaoTipo, String descricao, Integer ano) {
        this.id = id;
        this.declaracaoTipo = declaracaoTipo;
        this.descricao = descricao;
        this.ano = ano;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeclaracaoTipo getDeclaracaoTipo() {
        return declaracaoTipo;
    }

    public void setDeclaracaoTipo(DeclaracaoTipo declaracaoTipo) {
        this.declaracaoTipo = declaracaoTipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
