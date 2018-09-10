package br.com.rtools.financeiro;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_cartao_rec")
@NamedQuery(name = "CartaoRec.pesquisaID", query = "select cr from CartaoRec cr where cr.id = :pid")
public class CartaoRec implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_liquidacao")
    private Date dtLiquidacao;
    @JoinColumn(name = "id_cartao", referencedColumnName = "id")
    @ManyToOne
    private Cartao cartao;
    @Column(name = "ds_parcela", length = 5)
    private String parcela;

    public CartaoRec() {
        this.id = -1;
        this.dtLiquidacao = null;
        this.cartao = new Cartao();
        this.parcela = "";
    }

    public CartaoRec(int id, Date dtLiquidacao, Cartao cartao, String parcela) {
        this.id = id;
        this.dtLiquidacao = dtLiquidacao;
        this.cartao = cartao;
        this.parcela = parcela;
    }

    public CartaoRec(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDtLiquidacao() {
        return dtLiquidacao;
    }

    public void setDtLiquidacao(Date dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }
}
