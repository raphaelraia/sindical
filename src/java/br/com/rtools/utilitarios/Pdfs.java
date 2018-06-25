/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.utilitarios;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class Pdfs implements Serializable {

    private static final long serialVersionUID = 1L;
    private StreamedContent pdf;

    /**
     * Esse método deixa o arquivo pronto para ser exibido na tela.
     *
     * @param arquivo O arquivo pronto para ser apresentado.
     * @param nomeArquivo Nome do arquivo.
     */
    public void create(InputStream arquivo, String nomeArquivo) {
        pdf = new DefaultStreamedContent(arquivo, "application/pdf", nomeArquivo);
    }

    public StreamedContent get() {
        try {
            if (pdf != null) {
                pdf.getStream().reset();
            }
        } catch (IOException e) {
            //logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return pdf;
    }

    public void setPdf(StreamedContent pdf) {
        this.pdf = pdf;
    }

}
