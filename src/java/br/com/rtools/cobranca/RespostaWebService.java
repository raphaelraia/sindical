/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import java.io.Serializable;

/**
 *
 * @author Claudemir Rtools
 */
public class RespostaWebService implements Serializable {
    private Boleto boleto;
    private String mensagem;

    public RespostaWebService(Boleto boleto, String mensagem) {
        this.boleto = boleto;
        this.mensagem = mensagem;
    }
    
    public Boleto getBoleto() {
        return boleto;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    
}
