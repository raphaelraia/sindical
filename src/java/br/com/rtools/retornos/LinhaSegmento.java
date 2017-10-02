/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

import br.com.rtools.financeiro.StatusRetorno;

/**
 *
 * @author Claudemir Rtools
 */
public class LinhaSegmento {
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
        private String dataCredito;
        private String codigoMovimento;
        private StatusRetorno statusRetorno;

        public LinhaSegmento() {
            this.nossoNumero = "";
            this.valorPago = "";
            this.valorTaxa = "";
            this.valorCredito = "";
            this.dataPagamento = "";
            this.dataVencimento = "";
            this.acrescimo = "";
            this.valorDesconto = "";
            this.valorAbatimento = "";
            this.valorRepasse = "";
            this.dataCredito = "";
            this.codigoMovimento = "";
            this.statusRetorno = null;
        }

        public LinhaSegmento(String nossoNumero, String valorPago, String valorTaxa, String valorCredito, String dataPagamento, String dataVencimento, String acrescimo, String valorDesconto, String valorAbatimento, String valorRepasse, String dataCredito, String codigoMovimento, StatusRetorno statusRetorno) {
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
            this.dataCredito = dataCredito;
            this.codigoMovimento = codigoMovimento;
            this.statusRetorno = statusRetorno;
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

        public String getDataCredito() {
            return dataCredito;
        }

        public void setDataCredito(String dataCredito) {
            this.dataCredito = dataCredito;
        }

        public StatusRetorno getStatusRetorno() {
            return statusRetorno;
        }

        public void setStatusRetorno(StatusRetorno statusRetorno) {
            this.statusRetorno = statusRetorno;
        }

        public String getCodigoMovimento() {
            return codigoMovimento;
        }

        public void setCodigoMovimento(String codigoMovimento) {
            this.codigoMovimento = codigoMovimento;
        }    
}
