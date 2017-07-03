package br.com.rtools.impressao;

public class ParametroFechamentoBaile {
    private Object emissao;
    private Object operador;
    private Object codigo; 
    private Object convidado; 
    private Object status;
    private Object mesa;
    private Object convite;
    private Object vencimento; 
    private Object pagamento;
    private Object valor;
    private Object caixa;
    private Object obs;
    private Object servico_id;

    public ParametroFechamentoBaile(Object emissao, Object operador, Object codigo, Object convidado, Object status, Object mesa, Object convite, Object vencimento, Object pagamento, Object valor, Object caixa, Object obs, Object servico_id) {
        this.emissao = emissao;
        this.operador = operador;
        this.codigo = codigo;
        this.convidado = convidado;
        this.status = status;
        this.mesa = mesa;
        this.convite = convite;
        this.vencimento = vencimento;
        this.pagamento = pagamento;
        this.valor = valor;
        this.caixa = caixa;
        this.obs = obs;
        this.servico_id = servico_id;
    }
    
    public Object getEmissao() {
        return emissao;
    }

    public void setEmissao(Object emissao) {
        this.emissao = emissao;
    }

    public Object getOperador() {
        return operador;
    }

    public void setOperador(Object operador) {
        this.operador = operador;
    }

    public Object getCodigo() {
        return codigo;
    }

    public void setCodigo(Object codigo) {
        this.codigo = codigo;
    }

    public Object getConvidado() {
        return convidado;
    }

    public void setConvidado(Object convidado) {
        this.convidado = convidado;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getMesa() {
        return mesa;
    }

    public void setMesa(Object mesa) {
        this.mesa = mesa;
    }

    public Object getConvite() {
        return convite;
    }

    public void setConvite(Object convite) {
        this.convite = convite;
    }

    public Object getVencimento() {
        return vencimento;
    }

    public void setVencimento(Object vencimento) {
        this.vencimento = vencimento;
    }

    public Object getPagamento() {
        return pagamento;
    }

    public void setPagamento(Object pagamento) {
        this.pagamento = pagamento;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Object getCaixa() {
        return caixa;
    }

    public void setCaixa(Object caixa) {
        this.caixa = caixa;
    }

    public Object getObs() {
        return obs;
    }

    public void setObs(Object obs) {
        this.obs = obs;
    }

    public Object getServico_id() {
        return servico_id;
    }

    public void setServico_id(Object servico_id) {
        this.servico_id = servico_id;
    }
}
