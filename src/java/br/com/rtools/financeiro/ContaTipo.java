package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "fin_conta_tipo")
@NamedQueries({
    @NamedQuery(name = "ContaTipo.pesquisaID", query = "SELECT CT FROM ContaTipo CT WHERE CT.id = :pid"),
    @NamedQuery(name = "ContaTipo.findAll", query = "SELECT CT FROM ContaTipo CT ORDER BY CT.descricao")
})
public class ContaTipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 50, unique = true, nullable = false)
    private String descricao;

    public ContaTipo() {
        this.id = -1;
        this.descricao = "";
    }

    public ContaTipo(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
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

}
