/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.StatusRemessa;
import java.io.Serializable;

/**
 *
 * @author Claudemir Rtools
 */
public class BoletoRemessa implements Serializable {
    
        private Boleto boleto;
        private StatusRemessa statusRemessa;
        private String tipo;

        public BoletoRemessa(Boleto boleto, StatusRemessa statusRemessa, String tipo) {
            this.boleto = boleto;
            this.statusRemessa = statusRemessa;
            this.tipo = tipo;
        }

        public Boleto getBoleto() {
            return boleto;
        }

        public void setBoleto(Boleto boleto) {
            this.boleto = boleto;
        }

        public StatusRemessa getStatusRemessa() {
            return statusRemessa;
        }

        public void setStatusRemessa(StatusRemessa statusRemessa) {
            this.statusRemessa = statusRemessa;
        }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
