/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro;

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
@Table(name = "fin_conta_tipo_plano5")
public class ContaTipoPlano5 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_conta_tipo", referencedColumnName = "id")
    @ManyToOne
    private ContaTipo contaTipo;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;

    public ContaTipoPlano5() {
        this.id = -1;
        this.contaTipo = new ContaTipo();
        this.plano5 = new Plano5();
    }

    public ContaTipoPlano5(int id, ContaTipo contaTipo, Plano5 plano5) {
        this.id = id;
        this.contaTipo = contaTipo;
        this.plano5 = plano5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContaTipo getContaTipo() {
        return contaTipo;
    }

    public void setContaTipo(ContaTipo contaTipo) {
        this.contaTipo = contaTipo;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

}
