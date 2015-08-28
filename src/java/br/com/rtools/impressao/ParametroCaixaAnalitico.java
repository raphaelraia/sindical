package br.com.rtools.impressao;

public class ParametroCaixaAnalitico {

    private Object caixa;
    private Object dt_baixa;
    private Object lote_baixa;
    private Object operador;
    private Object responsavel;
    private Object titular;
    private Object beneficiario;
    private Object servico;
    private Object operacao;
    private Object valor;
    private Object valor_baixa;
    private Object id;
    private Object dt_fechamento;
    private Object transferencia;

    public ParametroCaixaAnalitico(Object caixa, Object dt_baixa, Object lote_baixa, Object operador, Object responsavel, Object titular, Object beneficiario, Object servico, Object operacao, Object valor, Object valor_baixa, Object id, Object dt_fechamento, Object transferencia) {
        this.caixa = caixa;
        this.dt_baixa = dt_baixa;
        this.lote_baixa = lote_baixa;
        this.operador = operador;
        this.responsavel = responsavel;
        this.titular = titular;
        this.beneficiario = beneficiario;
        this.servico = servico;
        this.operacao = operacao;
        this.valor = valor;
        this.valor_baixa = valor_baixa;
        this.id = id;
        this.dt_fechamento = dt_fechamento;
        this.transferencia = transferencia;
    }

    public Object getCaixa() {
        return caixa;
    }

    public void setCaixa(Object caixa) {
        this.caixa = caixa;
    }

    public Object getDt_baixa() {
        return dt_baixa;
    }

    public void setDt_baixa(Object dt_baixa) {
        this.dt_baixa = dt_baixa;
    }

    public Object getLote_baixa() {
        return lote_baixa;
    }

    public void setLote_baixa(Object lote_baixa) {
        this.lote_baixa = lote_baixa;
    }

    public Object getOperador() {
        return operador;
    }

    public void setOperador(Object operador) {
        this.operador = operador;
    }

    public Object getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Object responsavel) {
        this.responsavel = responsavel;
    }

    public Object getTitular() {
        return titular;
    }

    public void setTitular(Object titular) {
        this.titular = titular;
    }

    public Object getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Object beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Object getServico() {
        return servico;
    }

    public void setServico(Object servico) {
        this.servico = servico;
    }

    public Object getOperacao() {
        return operacao;
    }

    public void setOperacao(Object operacao) {
        this.operacao = operacao;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Object getValor_baixa() {
        return valor_baixa;
    }

    public void setValor_baixa(Object valor_baixa) {
        this.valor_baixa = valor_baixa;
    }

    public Object getDt_fechamento() {
        return dt_fechamento;
    }

    public void setDt_fechamento(Object dt_fechamento) {
        this.dt_fechamento = dt_fechamento;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(Object transferencia) {
        this.transferencia = transferencia;
    }
}
