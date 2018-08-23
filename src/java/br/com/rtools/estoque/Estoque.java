package br.com.rtools.estoque;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "est_estoque",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_produto", "id_filial", "id_tipo"})
)
public class Estoque implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_estoque", columnDefinition = "integer default 0", nullable = false)
    private Integer estoque;
    @Column(name = "nr_estoque_minimo", columnDefinition = "integer default 1", nullable = false)
    private Integer estoqueMinimo;
    @Column(name = "nr_estoque_maximo", columnDefinition = "integer default 1", nullable = false)
    private Integer estoqueMaximo;
    @Column(name = "nr_custo_medio", columnDefinition = "double precision default 0", nullable = false)
    private Double custoMedio;
    @JoinColumn(name = "id_produto", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Produto produto;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Filial filial;
    @JoinColumn(name = "id_tipo", referencedColumnName = "id", nullable = false)
    @OneToOne
    private EstoqueTipo estoqueTipo;
    @JoinColumn(name = "is_ativo", columnDefinition = "boolean default true", nullable = false)
    private Boolean ativo;
    @Column(name = "is_estoque", columnDefinition = "boolean default false", nullable = false)
    private Boolean controlaEstoque;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro", nullable = false, columnDefinition = "timestamp without time zone DEFAULT current_date")
    private Date dtCadastro;

    public Estoque() {
        this.id = null;
        this.estoque = 0;
        this.estoqueMinimo = 0;
        this.estoqueMaximo = 0;
        this.custoMedio = new Double(0);
        this.produto = null;
        this.filial = null;
        this.estoqueTipo = null;
        this.ativo = true;
        this.controlaEstoque = false;
        this.dtCadastro = new Date();
    }

    public Estoque(Integer id, Integer estoque, Integer estoqueMinimo, Integer estoqueMaximo, Double custoMedio, Produto produto, Filial filial, EstoqueTipo estoqueTipo, Boolean ativo, Boolean controlaEstoque, Date dtCadastro) {
        this.id = id;
        this.estoque = estoque;
        this.estoqueMinimo = estoqueMinimo;
        this.estoqueMaximo = estoqueMaximo;
        this.custoMedio = custoMedio;
        this.produto = produto;
        this.filial = filial;
        this.estoqueTipo = estoqueTipo;
        this.ativo = ativo;
        this.controlaEstoque = controlaEstoque;
        this.dtCadastro = dtCadastro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Integer getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public void setEstoqueMaximo(Integer estoqueMaximo) {
        this.estoqueMaximo = estoqueMaximo;
    }

    public Double getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(Double custoMedio) {
        this.custoMedio = custoMedio;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public EstoqueTipo getEstoqueTipo() {
        return estoqueTipo;
    }

    public void setEstoqueTipo(EstoqueTipo estoqueTipo) {
        this.estoqueTipo = estoqueTipo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getCustoMedioString() {
        return Moeda.converteR$Double(custoMedio);
    }

    public void setCustoMedioString(String custoMedioString) {
        this.custoMedio = Moeda.converteUS$(custoMedioString);
    }

    public Boolean getControlaEstoque() {
        return controlaEstoque;
    }

    public void setControlaEstoque(Boolean controlaEstoque) {
        this.controlaEstoque = controlaEstoque;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    @Override
    public String toString() {
        return "Estoque{" + "id=" + id + ", estoque=" + estoque + ", estoqueMinimo=" + estoqueMinimo + ", estoqueMaximo=" + estoqueMaximo + ", custoMedio=" + custoMedio + ", produto=" + produto + ", filial=" + filial + ", estoqueTipo=" + estoqueTipo + ", ativo=" + ativo + '}';
    }

}
