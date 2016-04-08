/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.endereco.Cidade;
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
@Table(name = "hom_prazo")
public class Prazo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_cidade", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Cidade cidade;
    @JoinColumn(name = "id_convencao", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Convencao convencao;
    @Column(name = "nr_prazo_dias_trabalhado")
    private Integer prazoDiasTrabalhado;
    @Column(name = "nr_prazo_dias_indenizado")
    private Integer prazoDiasIndenizado;

    public Prazo() {
        this.id = -1;
        this.cidade = new Cidade();
        this.convencao = new Convencao();
        this.prazoDiasTrabalhado = 0;
        this.prazoDiasIndenizado = 0;
    }

    public Prazo(Integer id, Cidade cidade, Convencao convencao, Integer prazoDiasTrabalhado, Integer prazoDiasIndenizado) {
        this.id = id;
        this.cidade = cidade;
        this.convencao = convencao;
        this.prazoDiasTrabalhado = prazoDiasTrabalhado;
        this.prazoDiasIndenizado = prazoDiasIndenizado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Convencao getConvencao() {
        return convencao;
    }

    public void setConvencao(Convencao convencao) {
        this.convencao = convencao;
    }

    public Integer getPrazoDiasTrabalhado() {
        return prazoDiasTrabalhado;
    }

    public void setPrazoDiasTrabalhado(Integer prazoDiasTrabalhado) {
        this.prazoDiasTrabalhado = prazoDiasTrabalhado;
    }

    public String getPrazoDiasTrabalhadoString() {
        return Integer.toString(prazoDiasTrabalhado);
    }

    public void setPrazoDiasTrabalhadoString(String prazoDiasTrabalhadoString) {
        this.prazoDiasTrabalhado = Integer.parseInt(prazoDiasTrabalhadoString);
    }

    public Integer getPrazoDiasIndenizado() {
        return prazoDiasIndenizado;
    }

    public void setPrazoDiasIndenizado(Integer prazoDiasIndenizado) {
        this.prazoDiasIndenizado = prazoDiasIndenizado;
    }

    public String getPrazoDiasIndenizadoString() {
        return Integer.toString(prazoDiasIndenizado);
    }

    public void setPrazoDiasIndenizadoString(String prazoDiasIndenizadoString) {
        this.prazoDiasIndenizado = Integer.parseInt(prazoDiasIndenizadoString);
    }

}
