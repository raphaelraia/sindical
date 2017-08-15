package br.com.rtools.utilitarios;

import br.com.rtools.financeiro.StatusRetorno;

public final class GenericaRetorno {

    private String cnpj;
    private String codigoCedente;
    private String nossoNumero;
    private String valorPago;
    private String valorTaxa;
    private String valorCredito;
    private String dataPagamento;
    private String dataVencimento;
    private String acrescimo;
    private String valorDesconto;
    private String valorAbatimento;
    private String valorRepasse;
    private String nomePasta;
    private String nomeArquivo;
    private String dataCredito;
    private String sequencialArquivo;
    private StatusRetorno statusRetorno;
    
    public GenericaRetorno(String cnpj, String codigoCedente, String nossoNumero, String valorPago, String valorTaxa, String valorCredito, String dataPagamento, String dataVencimento, String acrescimo, String valorDesconto, String valorAbatimento, String valorRepasse, String nomePasta, String nomeArquivo, String dataCredito, String sequencialArquivo, StatusRetorno statusRetorno) {
        this.cnpj = cnpj;
        this.codigoCedente = codigoCedente;
        this.nossoNumero = nossoNumero;
        this.valorPago = valorPago;
        this.valorTaxa = valorTaxa;
        this.valorCredito = valorCredito;
        this.dataPagamento = dataPagamento;
        this.dataVencimento = dataVencimento;
        this.acrescimo = acrescimo;
        this.valorDesconto = valorDesconto;
        this.valorAbatimento = valorAbatimento;
        this.valorRepasse = valorRepasse;
        this.nomePasta = nomePasta;
        this.nomeArquivo = nomeArquivo;
        this.dataCredito = dataCredito;
        this.sequencialArquivo = sequencialArquivo;
        this.statusRetorno = statusRetorno;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCodigoCedente() {
        return codigoCedente;
    }

    public void setCodigoCedente(String codigoCedente) {
        this.codigoCedente = codigoCedente;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public String getValorPago() {
        return valorPago;
    }

    public void setValorPago(String valorPago) {
        this.valorPago = valorPago;
    }

    public String getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(String valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public String getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(String valorCredito) {
        this.valorCredito = valorCredito;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(String acrescimo) {
        this.acrescimo = acrescimo;
    }

    public String getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(String valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getValorAbatimento() {
        return valorAbatimento;
    }

    public void setValorAbatimento(String valorAbatimento) {
        this.valorAbatimento = valorAbatimento;
    }

    public String getValorRepasse() {
        return valorRepasse;
    }

    public void setValorRepasse(String valorRepasse) {
        this.valorRepasse = valorRepasse;
    }

    public String getNomePasta() {
        return nomePasta;
    }

    public void setNomePasta(String nomePasta) {
        this.nomePasta = nomePasta;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(String dataCredito) {
        this.dataCredito = dataCredito;
    }

    public String getSequencialArquivo() {
        return sequencialArquivo;
    }

    public void setSequencialArquivo(String sequencialArquivo) {
        this.sequencialArquivo = sequencialArquivo;
    }

    public StatusRetorno getStatusRetorno() {
        return statusRetorno;
    }

    public void setStatusRetorno(StatusRetorno statusRetorno) {
        this.statusRetorno = statusRetorno;
    }
}
