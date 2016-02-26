package br.com.rtools.associativo;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sort_categoria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_sorteio", "id_categoria"})
)
public class SorteioCategoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_sorteio", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Sorteio sorteio;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Categoria categoria;

    public SorteioCategoria() {
        this.id = null;
        this.sorteio = null;
        this.categoria = null;
    }

    public SorteioCategoria(Integer id, Sorteio sorteio, Categoria categoria) {
        this.id = id;
        this.sorteio = sorteio;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sorteio getSorteio() {
        return sorteio;
    }

    public void setSorteio(Sorteio sorteio) {
        this.sorteio = sorteio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

}
