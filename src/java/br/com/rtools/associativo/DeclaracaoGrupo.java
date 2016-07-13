/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.financeiro.ContaTipo;
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
@Table(name = "soc_declaracao_grupo")
public class DeclaracaoGrupo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_declaracao_tipo", referencedColumnName = "id")
    @ManyToOne
    private DeclaracaoTipo declaracaoTipo;
    @JoinColumn(name = "id_subgrupo", referencedColumnName = "id")
    @ManyToOne
    private SubGrupoConvenio subGrupoConvenio;

    public DeclaracaoGrupo() {
        this.id = -1;
        this.declaracaoTipo = new DeclaracaoTipo();
        this.subGrupoConvenio = new SubGrupoConvenio();
    }

    public DeclaracaoGrupo(int id, DeclaracaoTipo declaracaoTipo, SubGrupoConvenio subGrupoConvenio) {
        this.id = id;
        this.declaracaoTipo = declaracaoTipo;
        this.subGrupoConvenio = subGrupoConvenio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeclaracaoTipo getDeclaracaoTipo() {
        return declaracaoTipo;
    }

    public void setDeclaracaoTipo(DeclaracaoTipo declaracaoTipo) {
        this.declaracaoTipo = declaracaoTipo;
    }

    public SubGrupoConvenio getSubGrupoConvenio() {
        return subGrupoConvenio;
    }

    public void setSubGrupoConvenio(SubGrupoConvenio subGrupoConvenio) {
        this.subGrupoConvenio = subGrupoConvenio;
    }

}
