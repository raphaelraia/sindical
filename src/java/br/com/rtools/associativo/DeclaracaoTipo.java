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
import javax.persistence.Table;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "soc_declaracao_tipo")
public class DeclaracaoTipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 100)
    private String descricao;
    @Column(name = "ds_jasper", length = 100)
    private String jasper;
    @Column(name = "nr_idade_inicio")
    private Integer idadeInicio;
    @Column(name = "nr_idade_final")
    private Integer idadeFinal;
    @Column(name = "nr_validade")
    private Integer validade;
    @Column(name = "nr_validade_tipo")
    private Integer validadeTipo; // (0 dia, 1 meses, 2 ano) 
    @Column(name = "nr_dias_atraso")
    private Integer diasAtraso;

    public DeclaracaoTipo() {
        this.id = -1;
        this.descricao = "";
        this.jasper = "";
        this.idadeInicio = null;
        this.idadeFinal = null;
        this.validade = null;
        this.validadeTipo = null;
        this.diasAtraso = null;
    }

    public DeclaracaoTipo(int id, String descricao, String jasper, Integer idadeInicio, Integer idadeFinal, Integer validade, Integer validadeTipo, Integer diasAtraso) {
        this.id = id;
        this.descricao = descricao;
        this.jasper = jasper;
        this.idadeInicio = idadeInicio;
        this.idadeFinal = idadeFinal;
        this.validade = validade;
        this.validadeTipo = validadeTipo;
        this.diasAtraso = diasAtraso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getJasper() {
        return jasper;
    }

    public void setJasper(String jasper) {
        this.jasper = jasper;
    }

    public Integer getIdadeInicio() {
        return idadeInicio;
    }

    public void setIdadeInicio(Integer idadeInicio) {
        this.idadeInicio = idadeInicio;
    }

    public Integer getIdadeFinal() {
        return idadeFinal;
    }

    public void setIdadeFinal(Integer idadeFinal) {
        this.idadeFinal = idadeFinal;
    }

    public Integer getValidade() {
        return validade;
    }

    public void setValidade(Integer validade) {
        this.validade = validade;
    }

    public Integer getValidadeTipo() {
        return validadeTipo;
    }

    public void setValidadeTipo(Integer validadeTipo) {
        this.validadeTipo = validadeTipo;
    }

    public Integer getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(Integer diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

}
