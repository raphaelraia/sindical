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
@Table(name = "eve_cupom_categoria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_cupom", "id_categoria"})
)
public class CupomCategoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_cupom", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Cupom cupom;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Categoria categoria;

    public CupomCategoria() {
        this.id = null;
        this.cupom = null;
        this.categoria = null;
    }

    public CupomCategoria(Integer id, Cupom cupom, Categoria categoria) {
        this.id = id;
        this.cupom = cupom;
        this.categoria = categoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "CupomCategoria{" + "id=" + id + ", cupom=" + cupom + ", categoria=" + categoria + '}';
    }

}
