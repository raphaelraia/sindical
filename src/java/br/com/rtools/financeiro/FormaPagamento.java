package br.com.rtools.financeiro;

import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
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
    private double valorP;
    @Column(name = "nr_valor", length = 10)
    private double valor;
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
    private double valorLiquido;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_credito")
    private Date dtCredito;
    @Column(name = "nr_taxa", length = 10)
    private double taxa;
    @JoinColumn(name = "id_status", referencedColumnName = "id")
    @ManyToOne
    private FStatus status;
    @Column(name = "nr_devolucao", length = 10)
    private Integer devolucao;
    @JoinColumn(name = "id_conciliacao_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 conciliacaoPlano5;
    @JoinColumn(name = "id_conciliacao", referencedColumnName = "id")
    @ManyToOne
    private Conciliacao conciliacao;
    @Column(name = "ds_documento")
    private String documento;
    @Column(name = "is_conciliado", nullable = false)
    private Boolean conciliado;

    @Transient
    private Pessoa responsavel;

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
        this.status = null;
        this.devolucao = 0;
        this.conciliacaoPlano5 = null;
        this.conciliacao = null;
        this.documento = "";
        this.conciliado = false;
    }

    public FormaPagamento(int id,
            Baixa baixa,
            ChequeRec chequeRec,
            ChequePag chequePag,
            double valorP,
            double valor,
            Filial filial,
            Plano5 plano5,
            CartaoPag cartaoPag,
            CartaoRec cartaoRec,
            TipoPagamento tipoPagamento,
            double valorLiquido,
            Date dtCredito,
            double taxa,
            FStatus status,
            Integer devolucao,
            Plano5 conciliacaoPlano5,
            Conciliacao conciliacao,
            String documento,
            Boolean conciliado) {
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
        this.status = status;
        this.devolucao = devolucao;
        this.conciliacaoPlano5 = conciliacaoPlano5;
        this.conciliacao = conciliacao;
        this.documento = documento;
        this.conciliado = conciliado;
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

    public double getValorP() {
        return valorP;
    }

    public void setValorP(double valorP) {
        this.valorP = valorP;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Double(valor);
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

    public double getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(double valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getValorLiquidoString() {
        return Moeda.converteR$Double(valorLiquido);
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

    public String getDtCreditoString() {
        return DataHoje.converteData(dtCredito);
    }

    public void setDtCreditoString(String dtCreditoString) {
        this.dtCredito = DataHoje.converte(dtCreditoString);
    }

    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public FStatus getStatus() {
        return status;
    }

    public void setStatus(FStatus status) {
        this.status = status;
    }

    public Integer getDevolucao() {
        return devolucao;
    }

    public void setDevolucao(Integer devolucao) {
        this.devolucao = devolucao;
    }

    public String getDevolucaoString() {
        return (devolucao == 0 || this.status.getId() != 10) ? "" : (devolucao == 1) ? devolucao + " VEZ" : devolucao + " VEZES";
    }

    public Plano5 getConciliacaoPlano5() {
        return conciliacaoPlano5;
    }

    public void setConciliacaoPlano5(Plano5 conciliacaoPlano5) {
        this.conciliacaoPlano5 = conciliacaoPlano5;
    }

    public Conciliacao getConciliacao() {
        return conciliacao;
    }

    public void setConciliacao(Conciliacao conciliacao) {
        this.conciliacao = conciliacao;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Pessoa getResponsavel() {
        if (this.id != -1) {
            responsavel = new FinanceiroDao().responsavelFormaPagamento(this.id);
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getConciliado() {
        return conciliado;
    }

    public void setConciliado(Boolean conciliado) {
        this.conciliado = conciliado;
    }

}
