/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Rotina;
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
@Table(name = "fin_tipo_recibo")
public class TipoRecibo implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id")
    @ManyToOne
    private Rotina rotina;
    @Column(name = "ds_descricao", nullable = false)
    private String descricao;
    @Column(name = "ds_jasper", nullable = false)
    private String jasper;

    public TipoRecibo() {
        this.id = -1;
        this.rotina = new Rotina();
        this.descricao = "";
        this.jasper = "";
    }

    public TipoRecibo(int id, Rotina rotina, String descricao, String jasper) {
        this.id = id;
        this.rotina = rotina;
        this.descricao = descricao;
        this.jasper = jasper;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
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

}
