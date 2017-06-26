package br.com.rtools.associativo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "soc_desconto")
@NamedQueries({
    @NamedQuery(name = "DescontoSocial.pesquisaID", query = "SELECT DS FROM DescontoSocial AS DS WHERE DS.id=:pid"),
    @NamedQuery(name = "DescontoSocial.findAll", query = "SELECT DS FROM DescontoSocial AS DS ORDER BY DS.descricao ASC")
})
public class DescontoSocial implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "nr_desconto", nullable = true)
    private double nrDesconto;
    @Column(name = "ds_descricao", length = 100)
    private String descricao;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Categoria categoria;

    @Transient
    private Boolean selected;

    public DescontoSocial() {
        this.id = -1;
        this.nrDesconto = 0;
        this.descricao = "";
        this.categoria = null;
        this.selected = false;
    }

    public DescontoSocial(int id, double nrDesconto, String descricao, Categoria categoria) {
        this.id = id;
        this.nrDesconto = nrDesconto;
        this.descricao = descricao;
        this.categoria = categoria;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getNrDesconto() {
        return nrDesconto;
    }

    public void setNrDesconto(double nrDesconto) {
        this.nrDesconto = nrDesconto;
    }

    public String getNrDescontoString() {
        return String.valueOf(nrDesconto);
    }

    public void setNrDescontoString(String nrDescontoString) {
        try {
            this.nrDesconto = Double.valueOf(nrDescontoString.replace(",", "."));
        } catch (Exception e) {
            this.nrDesconto = 0;
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
