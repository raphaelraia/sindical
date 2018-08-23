package br.com.rtools.sistema;

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
@Table(name = "sis_cor")
@NamedQueries({
    @NamedQuery(name = "Cor.findAll", query = "SELECT C FROM Cor AS C ORDER BY C.descricao ASC "),
    @NamedQuery(name = "Cor.findName", query = "SELECT C FROM Cor AS C WHERE UPPER(C.descricao) LIKE :pdescricao ORDER BY C.descricao ASC ")
})
public class Cor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 100, nullable = false, unique = true)
    private String descricao;

    public Cor() {
        this.id = null;
        this.descricao = "";
    }

    public Cor(Integer id, String descricao) {
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
        return "Cor{" + "id=" + id + ", descricao=" + descricao + '}';
    }
}
