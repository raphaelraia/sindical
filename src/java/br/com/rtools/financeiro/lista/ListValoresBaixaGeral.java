package br.com.rtools.financeiro.lista;

import br.com.rtools.financeiro.Cartao;
import br.com.rtools.financeiro.CartaoPag;
import br.com.rtools.financeiro.CartaoRec;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;

public class ListValoresBaixaGeral {

    private String vencimento;
    private String valor;
    private String numero;
    private TipoPagamento tipoPagamento;
    private ChequePag chequePag;
    private ChequeRec chequeRec;
    private Plano5 plano5;
    private Cartao cartao;
    private CartaoPag cartaoPag;
    private CartaoRec cartaoRec;
    private String valorDigitado;
    private FStatus status;
    private Plano5 conciliacaoPlano5;
    private Date dataConciliacao;
    private Date dataOcorrencia;
    private Date dataCreditoCartao;

    public ListValoresBaixaGeral(String vencimento, String valor, String numero, TipoPagamento tipoPagamento, ChequePag chequePag, ChequeRec chequeRec, Plano5 plano5, Cartao cartao, CartaoPag cartaoPag, CartaoRec cartaoRec, String valorDigitado, FStatus status, Plano5 conciliacaoPlano5, Date dataConciliacao, Date dataOcorrencia, Date dataCreditoCartao) {
        this.vencimento = vencimento;
        this.valor = valor;
        this.numero = numero;
        this.tipoPagamento = tipoPagamento;
        this.chequePag = chequePag;
        this.chequeRec = chequeRec;
        this.plano5 = plano5;
        this.cartao = cartao;
        this.cartaoPag = cartaoPag;
        this.cartaoRec = cartaoRec;
        this.valorDigitado = valorDigitado;
        this.status = status;
        this.conciliacaoPlano5 = conciliacaoPlano5;
        this.dataConciliacao = dataConciliacao;
        this.dataOcorrencia = dataOcorrencia;
        this.dataCreditoCartao = dataCreditoCartao;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public ChequePag getChequePag() {
        return chequePag;
    }

    public void setChequePag(ChequePag chequePag) {
        this.chequePag = chequePag;
    }

    public ChequeRec getChequeRec() {
        return chequeRec;
    }

    public void setChequeRec(ChequeRec chequeRec) {
        this.chequeRec = chequeRec;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public String getValorDigitado() {
        return valorDigitado;
    }

    public void setValorDigitado(String valorDigitado) {
        this.valorDigitado = valorDigitado;
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

    public FStatus getStatus() {
        return status;
    }

    public void setStatus(FStatus status) {
        this.status = status;
    }

    public Plano5 getConciliacaoPlano5() {
        return conciliacaoPlano5;
    }

    public void setConciliacaoPlano5(Plano5 conciliacaoPlano5) {
        this.conciliacaoPlano5 = conciliacaoPlano5;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Date getDataCreditoCartao() {
        return dataCreditoCartao;
    }

    public void setDataCreditoCartao(Date dataCreditoCartao) {
        this.dataCreditoCartao = dataCreditoCartao;
    }

    public String getDataCreditoCartaoString() {
        return DataHoje.converteData(dataCreditoCartao);
    }

    public void setDataCreditoCartaoString(String dataCreditoCartaoString) {
        this.dataCreditoCartao = DataHoje.converte(dataCreditoCartaoString);
    }
}
