/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "soc_exame_medico")
public class ExameMedico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_validade")
    private Date dtValidade;

    public ExameMedico() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.departamento = new Departamento();
        this.dtEmissao = DataHoje.dataHoje();
        this.dtValidade = null;
    }
    
    public ExameMedico(Integer id, Pessoa pessoa, Departamento departamento, Date dtEmissao, Date dtValidade) {
        this.id = id;
        this.pessoa = pessoa;
        this.departamento = departamento;
        this.dtEmissao = dtEmissao;
        this.dtValidade = dtValidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }
    
    public String getDtEmissaoString() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setDtEmissaoString(String dtEmissaoString) {
        this.dtEmissao = DataHoje.converte(dtEmissaoString);
    }

    public Date getDtValidade() {
        return dtValidade;
    }

    public void setDtValidade(Date dtValidade) {
        this.dtValidade = dtValidade;
    }
    
    public String getDtValidadeString() {
        return DataHoje.converteData(dtValidade);
    }

    public void setDtValidadeString(String dtValidadeString) {
        this.dtValidade = DataHoje.converte(dtValidadeString);
    }
}
