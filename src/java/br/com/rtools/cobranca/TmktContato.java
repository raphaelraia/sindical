package br.com.rtools.cobranca;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "tlm_contato")
@NamedQueries({
    @NamedQuery(name = "TmktContato.findAll", query = "SELECT C FROM TmktContato C ORDER BY C.descricao ASC "),
    @NamedQuery(name = "TmktContato.findName", query = "SELECT C FROM TmktContato C WHERE UPPER(C.descricao) LIKE :pdescricao ORDER BY C.descricao ASC ")
})
public class TmktContato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, unique = true, nullable = false)
    private String descricao;

    public TmktContato() {
        this.id = -1;
        this.descricao = "";
    }

    public TmktContato(Integer id, String descricao) {
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
        return "TmktContato{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
