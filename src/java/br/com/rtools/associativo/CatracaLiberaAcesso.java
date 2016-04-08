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
@Table(name = "soc_catraca_libera_acesso")
public class CatracaLiberaAcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_liberacao", nullable = false)
    private Date dtLiberacao;
    @Column(name = "ds_hora_liberacao")
    private String horaLiberacao;    
    @Column(name = "ds_observacao")
    private String observacao;
    @JoinColumn(name = "id_catraca", referencedColumnName = "id")
    @ManyToOne
    private Catraca catraca;

    public CatracaLiberaAcesso() {
        this.id = -1;
        this.departamento = new Departamento();
        this.pessoa = new Pessoa();
        this.dtLiberacao = null;
        this.horaLiberacao = "";
        this.observacao = "";
        this.catraca = new Catraca();
    }
    
    public CatracaLiberaAcesso(Integer id, Departamento departamento, Pessoa pessoa, Date dtLiberacao, String horaLiberacao, String observacao, Catraca catraca) {
        this.id = id;
        this.departamento = departamento;
        this.pessoa = pessoa;
        this.dtLiberacao = dtLiberacao;
        this.horaLiberacao = horaLiberacao;
        this.observacao = observacao;
        this.catraca = catraca;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtLiberacao() {
        return dtLiberacao;
    }

    public void setDtLiberacao(Date dtLiberacao) {
        this.dtLiberacao = dtLiberacao;
    }
    
    public String getDtLiberacaoString() {
        return DataHoje.converteData(dtLiberacao);
    }

    public void setDtLiberacaoString(String dtLiberacaoString) {
        this.dtLiberacao = DataHoje.converte(dtLiberacaoString);
    }

    public String getHoraLiberacao() {
        return horaLiberacao;
    }

    public void setHoraLiberacao(String horaLiberacao) {
        this.horaLiberacao = horaLiberacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Catraca getCatraca() {
        return catraca;
    }

    public void setCatraca(Catraca catraca) {
        this.catraca = catraca;
    }
    
    
    
}
