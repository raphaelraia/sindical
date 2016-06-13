package br.com.rtools.associativo;

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
@Table(name = "eve_banda")
@NamedQueries({
    @NamedQuery(name = "Banda.pesquisaID", query = "SELECT B FROM Banda AS B WHERE B.id = :pid"),
    @NamedQuery(name = "Banda.findAll", query = "SELECT B FROM Banda AS B ORDER BY B.descricao ASC")
})
public class Banda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", nullable = false, unique = true)
    private String descricao;

    public Banda() {
        this.id = -1;
        this.descricao = "";
    }

    public Banda(int id, String descricao) {
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
