/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
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
@Table(name = "soc_declaracao_pessoa")
public class DeclaracaoPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_convenio", referencedColumnName = "id")
    @ManyToOne
    private Pessoa convenio;
    @JoinColumn(name = "id_declaracao_tipo", referencedColumnName = "id")
    @ManyToOne
    private DeclaracaoTipo declaracaoTipo;
    @JoinColumn(name = "id_matricula", referencedColumnName = "id")
    @ManyToOne
    private MatriculaSocios matricula;

    public DeclaracaoPessoa() {
        this.id = -1;
        this.dtEmissao = DataHoje.dataHoje();
        this.pessoa = new Pessoa();
        this.convenio = new Pessoa();
        this.declaracaoTipo = new DeclaracaoTipo();
        this.matricula = new MatriculaSocios();
    }

    public DeclaracaoPessoa(int id, Date dtEmissao, Pessoa pessoa, Pessoa convenio, DeclaracaoTipo declaracaoTipo, MatriculaSocios matricula) {
        this.id = id;
        this.dtEmissao = dtEmissao;
        this.pessoa = pessoa;
        this.convenio = convenio;
        this.declaracaoTipo = declaracaoTipo;
        this.matricula = matricula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getConvenio() {
        return convenio;
    }

    public void setConvenio(Pessoa convenio) {
        this.convenio = convenio;
    }

    public DeclaracaoTipo getDeclaracaoTipo() {
        return declaracaoTipo;
    }

    public void setDeclaracaoTipo(DeclaracaoTipo declaracaoTipo) {
        this.declaracaoTipo = declaracaoTipo;
    }

    public MatriculaSocios getMatricula() {
        return matricula;
    }

    public void setMatricula(MatriculaSocios matricula) {
        this.matricula = matricula;
    }

}
