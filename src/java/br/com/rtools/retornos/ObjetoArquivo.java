/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

import br.com.rtools.financeiro.Retorno;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Claudemir Rtools
 */
public class ObjetoArquivo {

    private String cnpj;
    private String codigoCedente;
    private String sequencialArquivo;
    private String nomePasta;
    private String nomeArquivo;
    private String erroArquivo;
    private Retorno retorno;
    private List<LinhaSegmento> linhaSegmento;
    private Boolean arquivoComErro = false;

    public ObjetoArquivo() {
        this.cnpj = "";
        this.codigoCedente = "";
        this.sequencialArquivo = "";
        this.nomePasta = "";
        this.nomeArquivo = "";
        this.erroArquivo = "";
        this.retorno = null;
        this.linhaSegmento = new ArrayList();
        this.arquivoComErro = false;
    }

    public ObjetoArquivo(String cnpj, String codigoCedente, String sequencialArquivo, String nomePasta, String nomeArquivo, String erroArquivo, Retorno retorno, List<LinhaSegmento> linhaSegmento, Boolean arquivoComErro) {
        this.cnpj = cnpj;
        this.codigoCedente = codigoCedente;
        this.sequencialArquivo = sequencialArquivo;
        this.nomePasta = nomePasta;
        this.nomeArquivo = nomeArquivo;
        this.erroArquivo = erroArquivo;
        this.retorno = retorno;
        this.linhaSegmento = linhaSegmento;
        this.arquivoComErro = arquivoComErro;
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

    public String getSequencialArquivo() {
        return sequencialArquivo;
    }

    public void setSequencialArquivo(String sequencialArquivo) {
        this.sequencialArquivo = sequencialArquivo;
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

    public String getErroArquivo() {
        return erroArquivo;
    }

    public void setErroArquivo(String erroArquivo) {
        this.erroArquivo = erroArquivo;
    }

    public Retorno getRetorno() {
        return retorno;
    }

    public void setRetorno(Retorno retorno) {
        this.retorno = retorno;
    }

    public List<LinhaSegmento> getLinhaSegmento() {
        return linhaSegmento;
    }

    public void setLinhaSegmento(List<LinhaSegmento> linhaSegmento) {
        this.linhaSegmento = linhaSegmento;
    }

    public Boolean getArquivoComErro() {
        return arquivoComErro;
    }

    public void setArquivoComErro(Boolean arquivoComErro) {
        this.arquivoComErro = arquivoComErro;
    }

}
