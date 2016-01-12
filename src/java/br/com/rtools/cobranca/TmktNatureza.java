package br.com.rtools.cobranca;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "tlm_natureza")
@NamedQueries({
    @NamedQuery(name = "TmktNatureza.findAll", query = "SELECT N FROM TmktNatureza N ORDER BY N.descricao ASC "),
    @NamedQuery(name = "TmktNatureza.findName", query = "SELECT N FROM TmktNatureza N WHERE UPPER(N.descricao) LIKE :pdescricao ORDER BY N.descricao ASC ")
})
public class TmktNatureza implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, unique = true, nullable = false)
    private String descricao;

    public TmktNatureza() {
        this.id = -1;
        this.descricao = "";
    }

    public TmktNatureza(Integer id, String descricao) {
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

    @Override
    public String toString() {
        return "TmktNatureza{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
