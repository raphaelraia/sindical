package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_erros")
@NamedQueries({
    @NamedQuery(name = "Erros.findAll", query = "SELECT E FROM Erros AS E ORDER BY E.id ASC ")
})
public class Erros implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 500, nullable = false, unique = true)
    private String descricao;

    public Erros() {
        this.id = -1;
        this.descricao = "";
    }

    public Erros(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
