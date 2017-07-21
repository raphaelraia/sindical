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
    @Column(name = "nr_dias_carencia")
    private Integer diasCarencia;

    public DeclaracaoTipo() {
        this.id = -1;
        this.descricao = "";
        this.jasper = "";
        this.idadeInicio = null;
        this.idadeFinal = null;
        this.diasCarencia = null;
    }

    public DeclaracaoTipo(int id, String descricao, String jasper, Integer idadeInicio, Integer idadeFinal, Integer diasCarencia) {
        this.id = id;
        this.descricao = descricao;
        this.jasper = jasper;
        this.idadeInicio = idadeInicio;
        this.idadeFinal = idadeFinal;
        this.diasCarencia = diasCarencia;
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

    public Integer getDiasCarencia() {
        return diasCarencia;
    }

    public void setDiasCarencia(Integer diasCarencia) {
        this.diasCarencia = diasCarencia;
    }

}
