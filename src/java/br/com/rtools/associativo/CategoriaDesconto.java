package br.com.rtools.associativo;

import br.com.rtools.financeiro.ServicoValor;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Moeda;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "soc_categoria_desconto")
@NamedQuery(name = "CategoriaDesconto.pesquisaID", query = "select c from CategoriaDesconto c where c.id=:pid")
public class CategoriaDesconto implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Categoria categoria;
    @Column(name = "nr_desconto", nullable = false)
    private double desconto;
    @JoinColumn(name = "id_servico_valor", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ServicoValor servicoValor;

    public CategoriaDesconto() {
        this.id = -1;
        this.categoria = new Categoria();
        this.desconto = 0;
        this.servicoValor = new ServicoValor();
    }

    public CategoriaDesconto(int id, Categoria categoria, double desconto, ServicoValor servicoValor) {
        this.id = id;
        this.categoria = categoria;
        this.desconto = desconto;
        this.servicoValor = servicoValor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
    
    
    public String getDescontoString() {
        return Moeda.convertPercentToString(desconto, 4);
        //return Moeda.converteDoubleToString(desconto, 3);
    }

    public void setDescontoString(String desconto) {
        //this.desconto = Moeda.converteStringToDouble(desconto);
        this.desconto = Moeda.converteStringToDouble(desconto, 4);
    }

    public ServicoValor getServicoValor() {
        return servicoValor;
    }

    public void setServicoValor(ServicoValor servicoValor) {
        this.servicoValor = servicoValor;
    }
}
