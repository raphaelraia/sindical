package br.com.rtools.financeiro;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.utilitarios.Moeda;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fin_desconto_promocional")
public class DescontoPromocional implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_desconto")
    private float desconto;
    @Column(name = "ds_referencia_inicial")
    private String referenciaInicial;
    @Column(name = "ds_referencia_final")
    private String referenciaFinal;
    @JoinColumn(name = "id_servico", referencedColumnName = "id")
    @ManyToOne
    private Servicos servico;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id")
    @ManyToOne
    private Categoria categoria;
    @Column(name = "is_mensal", nullable = false, columnDefinition = "boolean default true")
    private Boolean mensal;

    public DescontoPromocional() {
        this.id = -1;
        this.desconto = 0;
        this.referenciaInicial = "";
        this.referenciaFinal = "";
        this.servico = null;
        this.categoria = null;
        this.mensal = true;
    }

    public DescontoPromocional(Integer id, float desconto, String referenciaInicial, String referenciaFinal, Servicos servico, Categoria categoria, Boolean mensal) {
        this.id = id;
        this.desconto = desconto;
        this.referenciaInicial = referenciaInicial;
        this.referenciaFinal = referenciaFinal;
        this.servico = servico;
        this.categoria = categoria;
        this.mensal = mensal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getDesconto() {
        return desconto;
    }

    public void setDesconto(float desconto) {
        this.desconto = desconto;
    }

    public String getDescontoString() {
        return Moeda.converteR$Float(desconto);
    }

    public void setDescontoString(String descontoString) {
        this.desconto = Moeda.converteUS$(descontoString);
    }

    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Boolean getMensal() {
        return mensal;
    }

    public void setMensal(Boolean mensal) {
        this.mensal = mensal;
    }

}
