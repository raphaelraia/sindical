/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao;

import br.com.rtools.atendimento.AteOperacao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "ate_operacao_departamento")
public class OperacaoDepartamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_operacao", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private AteOperacao operacao;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Filial filial;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Departamento departamento;

    public OperacaoDepartamento() {
        this.id = -1;
        this.operacao = new AteOperacao();
        this.filial = new Filial();
        this.departamento = new Departamento();
    }

    public OperacaoDepartamento(int id, AteOperacao operacao, Filial filial, Departamento departamento) {
        this.id = id;
        this.operacao = operacao;
        this.filial = filial;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AteOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(AteOperacao operacao) {
        this.operacao = operacao;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

}
