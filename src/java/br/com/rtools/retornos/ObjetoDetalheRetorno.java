/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

import br.com.rtools.financeiro.Movimento;

/**
 *
 * @author Claudemir Windows
 */
public class ObjetoDetalheRetorno {

    private Movimento movimento;
    private Integer codigo;
    private String detalhe;

    public ObjetoDetalheRetorno(Movimento movimento, Integer codigo, String detalhe) {
        this.movimento = movimento;
        this.codigo = codigo;
        this.detalhe = detalhe;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

}
