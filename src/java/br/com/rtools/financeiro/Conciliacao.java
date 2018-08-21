/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
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

@Entity
@Table(name = "fin_conciliacao")
public class Conciliacao implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario operador;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_lancamento")
    private Date dtLancamento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_conciliacao")
    private Date dtConciliacao;

    public Conciliacao() {
        this.id = -1;
        this.operador = new Usuario();
        this.dtLancamento = DataHoje.dataHoje();
        this.dtConciliacao = DataHoje.dataHoje();
    }

    public Conciliacao(int id, Usuario operador, Date dtLancamento, Date dtConciliacao) {
        this.id = id;
        this.operador = operador;
        this.dtLancamento = dtLancamento;
        this.dtConciliacao = dtConciliacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Date getDtLancamento() {
        return dtLancamento;
    }

    public void setDtLancamento(Date dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public Date getDtConciliacao() {
        return dtConciliacao;
    }

    public void setDtConciliacao(Date dtConciliacao) {
        this.dtConciliacao = dtConciliacao;
    }

}
