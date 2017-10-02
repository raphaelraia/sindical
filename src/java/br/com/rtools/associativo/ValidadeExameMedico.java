/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.seguranca.Departamento;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "soc_exame_medico_validade")
public class ValidadeExameMedico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @Column(name = "nr_meses")
    private Integer meses;
    @Column(name = "is_obrigatorio", columnDefinition = "boolean default false")
    private Boolean obrigatorio;
    @Column(name = "nr_idade_inicial")
    private Integer idadeInicial;
    @Column(name = "nr_idade_final")
    private Integer idadeFinal;
    @Column(name = "nr_meses_convidado")
    private Integer mesesConvidado;

    public ValidadeExameMedico() {
        this.id = -1;
        this.departamento = new Departamento();
        this.meses = 0;
        this.obrigatorio = false;
        this.idadeInicial = 0;
        this.idadeFinal = 150;
        this.mesesConvidado = 0;
    }

    public ValidadeExameMedico(Integer id, Departamento departamento, Integer meses, Boolean obrigatorio, Integer idadeInicial, Integer idadeFinal, Integer mesesConvidado) {
        this.id = id;
        this.departamento = departamento;
        this.meses = meses;
        this.obrigatorio = obrigatorio;
        this.idadeInicial = idadeInicial;
        this.idadeFinal = idadeFinal;
        this.mesesConvidado = mesesConvidado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Integer getIdadeInicial() {
        return idadeInicial;
    }

    public void setIdadeInicial(Integer idadeInicial) {
        this.idadeInicial = idadeInicial;
    }

    public Integer getIdadeFinal() {
        return idadeFinal;
    }

    public void setIdadeFinal(Integer idadeFinal) {
        this.idadeFinal = idadeFinal;
    }

    public Integer getMesesConvidado() {
        return mesesConvidado;
    }

    public void setMesesConvidado(Integer mesesConvidado) {
        this.mesesConvidado = mesesConvidado;
    }

}
