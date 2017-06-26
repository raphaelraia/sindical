package br.com.rtools.estoque;

import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "est_pedido")
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "nr_quantidade", columnDefinition = "integer default 0")
    private int quantidade;
    @Column(name = "nr_valor_unitario", columnDefinition = "double precision default 0")
    private double valorUnitario;
    @Column(name = "nr_desconto_unitario", columnDefinition = "double precision default 0")
    private double descontoUnitario;
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

    public Pedido() {
        this.id = -1;
        this.quantidade = 0;
        this.valorUnitario = 0;
        this.descontoUnitario = 0;
        this.lote = new Lote();
        this.produto = new Produto();
        this.estoqueTipo = new EstoqueTipo();
        this.servicos = null;
    }

    public Pedido(int id, int quantidade, double valorUnitario, double descontoUnitario, Lote lote, Produto produto, EstoqueTipo estoqueTipo, Servicos servicos) {
        this.id = id;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.descontoUnitario = descontoUnitario;
        this.lote = lote;
        this.produto = produto;
        this.estoqueTipo = estoqueTipo;
        this.servicos = servicos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public double getDescontoUnitario() {
        return descontoUnitario;
    }

    public void setDescontoUnitario(double descontoUnitario) {
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

    @Override
    public String toString() {
        return "Pedido{" + "id=" + id + ", quantidade=" + quantidade + ", valorUnitario=" + valorUnitario + ", descontoUnitario=" + descontoUnitario + ", lote=" + lote + ", produto=" + produto + ", estoqueTipo=" + estoqueTipo + '}';
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

}
