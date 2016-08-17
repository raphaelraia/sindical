/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.financeiro.ServicoPessoa;
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
@Table(name = "matr_agendamento")
public class MatriculaAgendamentoFinanceiro implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_servico_pessoa", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private ServicoPessoa servicoPessoa;

    public MatriculaAgendamentoFinanceiro() {
        this.id = -1;
        this.servicoPessoa = new ServicoPessoa();
    }

    public MatriculaAgendamentoFinanceiro(int id, ServicoPessoa servicoPessoa) {
        this.id = id;
        this.servicoPessoa = servicoPessoa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

}
