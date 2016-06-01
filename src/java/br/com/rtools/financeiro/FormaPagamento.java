package br.com.rtools.financeiro;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_forma_pagamento")
@NamedQuery(name = "FormaPagamento.pesquisaID", query = "select fp from FormaPagamento fp where fp.id=:pid")
public class FormaPagamento implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_baixa", referencedColumnName = "id")
    @ManyToOne
    private Baixa baixa;
    @JoinColumn(name = "id_cheque_rec", referencedColumnName = "id")
    @ManyToOne
    private ChequeRec chequeRec;
    @JoinColumn(name = "id_cheque_pag", referencedColumnName = "id")
    @ManyToOne
    private ChequePag chequePag;
    @Column(name = "nr_valorp", length = 10)
    private float valorP;
    @Column(name = "nr_valor", length = 10)
    private float valor;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne
    private Filial filial;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "id_cartao_pag", referencedColumnName = "id")
    @ManyToOne
    private CartaoPag cartaoPag;
    @JoinColumn(name = "id_cartao_rec", referencedColumnName = "id")
    @ManyToOne
    private CartaoRec cartaoRec;
    @JoinColumn(name = "id_tipo_pagamento", referencedColumnName = "id")
    @ManyToOne
    private TipoPagamento tipoPagamento;
    @Column(name = "nr_valor_liquido", length = 10)
    private float valorLiquido;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_credito")
    private Date dtCredito;
    @Column(name = "nr_taxa", length = 10)
    private float taxa;

    public FormaPagamento() {
        this.id = -1;
        this.baixa = new Baixa();
        this.chequeRec = new ChequeRec();
        this.chequePag = new ChequePag();
        this.valorP = 0;
        this.valor = 0;
        this.filial = new Filial();
        this.plano5 = new Plano5();
        this.cartaoPag = new CartaoPag();
        this.cartaoRec = new CartaoRec();
        this.tipoPagamento = new TipoPagamento();
        this.valorLiquido = 0;
        this.dtCredito = null;
        this.taxa = 0;
    }

    public FormaPagamento(int id,
            Baixa baixa,
            ChequeRec chequeRec,
            ChequePag chequePag,
            float valorP,
            float valor,
            Filial filial,
            Plano5 plano5,
            CartaoPag cartaoPag,
            CartaoRec cartaoRec,
            TipoPagamento tipoPagamento,
            float valorLiquido,
            Date dtCredito,
            float taxa) {
        this.id = id;
        this.baixa = baixa;
        this.chequeRec = chequeRec;
        this.chequePag = chequePag;
        this.valorP = valorP;
        this.valor = valor;
        this.filial = filial;
        this.plano5 = plano5;
        this.cartaoPag = cartaoPag;
        this.cartaoRec = cartaoRec;
        this.tipoPagamento = tipoPagamento;
        this.valorLiquido = valorLiquido;
        this.dtCredito = dtCredito;
        this.taxa = taxa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Baixa getBaixa() {
        return baixa;
    }

    public void setBaixa(Baixa baixa) {
        this.baixa = baixa;
    }

    public ChequeRec getChequeRec() {
        return chequeRec;
    }

    public void setChequeRec(ChequeRec chequeRec) {
        this.chequeRec = chequeRec;
    }

    public ChequePag getChequePag() {
        return chequePag;
    }

    public void setChequePag(ChequePag chequePag) {
        this.chequePag = chequePag;
    }

    public float getValorP() {
        return valorP;
    }

    public void setValorP(float valorP) {
        this.valorP = valorP;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Float(valor);
    }

    public void setValorString(String valorString) {
        this.valor = Moeda.converteUS$(valorString);
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public CartaoPag getCartaoPag() {
        return cartaoPag;
    }

    public void setCartaoPag(CartaoPag cartaoPag) {
        this.cartaoPag = cartaoPag;
    }

    public CartaoRec getCartaoRec() {
        return cartaoRec;
    }

    public void setCartaoRec(CartaoRec cartaoRec) {
        this.cartaoRec = cartaoRec;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public float getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(float valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getValorLiquidoString() {
        return Moeda.converteR$Float(valorLiquido);
    }

    public void setValorLiquidoString(String valorLiquidoString) {
        this.valorLiquido = Moeda.converteUS$(valorLiquidoString);
    }

    public Date getDtCredito() {
        return dtCredito;
    }

    public void setDtCredito(Date dtCredito) {
        this.dtCredito = dtCredito;
    }

    public float getTaxa() {
        return taxa;
    }

    public void setTaxa(float taxa) {
        this.taxa = taxa;
    }
}
