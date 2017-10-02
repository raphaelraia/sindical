/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.cobranca;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Claudemir Rtools
 */
public class RespostaArquivoRemessa implements Serializable {
    private File arquivo;
    private String mensagem;

    public RespostaArquivoRemessa(File arquivo, String mensagem) {
        this.arquivo = arquivo;
        this.mensagem = mensagem;
    }
    
    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    
}
