package br.com.rtools.locadoraFilme;

import br.com.rtools.pessoa.Filial;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_titulo_filial",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_filial", "id_titulo"})
)
public class Catalogo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne
    private Filial filial;
    @JoinColumn(name = "id_titulo", referencedColumnName = "id")
    @ManyToOne
    private Titulo titulo;
    @Column(name = "nr_qtde")
    private Integer quantidade;

    @Transient
    private Boolean selected;

    public Catalogo(Integer id, Filial filial, Titulo titulo, Integer quantidade) {
        this.id = id;
        this.filial = filial;
        this.titulo = titulo;
        this.quantidade = quantidade;
        selected = false;
    }

    public Catalogo() {
        id = null;
        filial = new Filial();
        titulo = new Titulo();
        quantidade = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
