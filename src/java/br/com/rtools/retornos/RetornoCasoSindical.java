/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

/**
 *
 * @author Claudemir Windows
 */
public class RetornoCasoSindical {

    private Boolean retorno;
    private ObjetoDetalheRetorno odr;

    public RetornoCasoSindical(Boolean retorno, ObjetoDetalheRetorno odr) {
        this.retorno = retorno;
        this.odr = odr;
    }

    public Boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(Boolean retorno) {
        this.retorno = retorno;
    }

    public ObjetoDetalheRetorno getOdr() {
        return odr;
    }

    public void setOdr(ObjetoDetalheRetorno odr) {
        this.odr = odr;
    }
}
