package br.com.rtools.arrecadacao;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arr_repis_status")
@NamedQuery(name = "RepisStatus.pesquisaID", query = "select r from RepisStatus r where r.id=:pid")
public class RepisStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 50, nullable = true, unique = true)
    private String descricao;
    @Column(name = "is_ativo")
    private Boolean ativo;

    public RepisStatus() {
        this.id = -1;
        this.descricao = "";
        this.ativo = true;
    }

    public RepisStatus(int id, String descricao, Boolean ativo) {
        this.id = id;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
