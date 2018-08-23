package br.com.rtools.estoque;

import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "est_pedido")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_quantidade", columnDefinition = "integer default 0")
    private Integer quantidade;
    @Column(name = "nr_valor_unitario", columnDefinition = "double precision default 0")
    private Double valorUnitario;
    @Column(name = "nr_desconto_unitario", columnDefinition = "double precision default 0")
    private Double descontoUnitario;
    @JoinColumn(name = "id_lote", referencedColumnName = "id")
    @OneToOne
    private Lote lote;
    @JoinColumn(name = "id_produto", referencedColumnName = "id")
    @OneToOne
    private Produto produto;
    @JoinColumn(name = "id_tipo", referencedColumnName = "id", columnDefinition = "integer default 1")
    @OneToOne
    private EstoqueTipo estoqueTipo;
    @JoinColumn(name = "id_servico", referencedColumnName = "id")
    @ManyToOne
    private Servicos servicos;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro", nullable = false, columnDefinition = "timestamp without time zone DEFAULT current_date")
    private Date dtCadastro;

    @Transient
    private Boolean controlaEstoque;

    public Pedido() {
        this.id = null;
        this.quantidade = 0;
        this.valorUnitario = new Double(0);
        this.descontoUnitario = new Double(0);
        this.lote = new Lote();
        this.produto = new Produto();
        this.estoqueTipo = new EstoqueTipo();
        this.servicos = null;
        this.dtCadastro = new Date();
        this.controlaEstoque = false;
    }

    public Pedido(Integer id, Integer quantidade, Double valorUnitario, Double descontoUnitario, Lote lote, Produto produto, EstoqueTipo estoqueTipo, Servicos servicos, Date dtCadastro) {
        this.id = id;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.descontoUnitario = descontoUnitario;
        this.lote = lote;
        this.produto = produto;
        this.estoqueTipo = estoqueTipo;
        this.servicos = servicos;
        this.dtCadastro = dtCadastro;
        this.controlaEstoque = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getDescontoUnitario() {
        return descontoUnitario;
    }

    public void setDescontoUnitario(Double descontoUnitario) {
        this.descontoUnitario = descontoUnitario;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public EstoqueTipo getEstoqueTipo() {
        return estoqueTipo;
    }

    public void setEstoqueTipo(EstoqueTipo estoqueTipo) {
        this.estoqueTipo = estoqueTipo;
    }

    public String getValorUnitarioString() {
        try {
            Double d = valorUnitario;
            String[] splitter = d.toString().split("\\.");
            if (splitter[1].length() == 1 || splitter[1].length() == 2) {
                return Moeda.converteR$Double(valorUnitario);
            } else {
                return Moeda.converteR$Double(valorUnitario, 4);
            }
        } catch (Exception e) {

        }
        return "0,00";
    }

    public void setValorUnitarioString(String valorUnitarioString) {
        this.valorUnitario = Moeda.substituiVirgulaDouble(valorUnitarioString);
    }

    public String getDescontoUnitarioString() {
        return Moeda.converteR$Double(descontoUnitario);
    }

    public void setDescontoUnitarioString(String descontoUnitarioString) {
        this.descontoUnitario = Moeda.substituiVirgulaDouble(descontoUnitarioString);
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public Boolean getControlaEstoque() {
        return controlaEstoque;
    }

    public void setControlaEstoque(Boolean controlaEstoque) {
        this.controlaEstoque = controlaEstoque;
    }

}
