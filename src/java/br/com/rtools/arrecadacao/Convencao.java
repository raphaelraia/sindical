package br.com.rtools.arrecadacao;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arr_convencao")
@NamedQueries({
    @NamedQuery(name = "Convencao.pesquisaID", query = "SELECT C FROM Convencao AS C WHERE C.id = :pid")
    ,
    @NamedQuery(name = "Convencao.findAll", query = "SELECT C FROM Convencao AS C WHERE C.ativo = true ORDER BY C.descricao ASC ")
    ,
    @NamedQuery(name = "Convencao.findName", query = "SELECT C FROM Convencao AS C WHERE UPPER(C.descricao) LIKE :pdescricao ORDER BY C.descricao ASC ")
})
public class Convencao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 100, nullable = true, unique = true)
    private String descricao;
    @Column(name = "is_ativo", columnDefinition = "boolean detault true")
    private Boolean ativo;

    public Convencao() {
        this.id = -1;
        this.descricao = "";
        this.ativo = true;
    }

    public Convencao(int id, String descricao, Boolean ativo) {
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
