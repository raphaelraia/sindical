/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.impressao;

/**
 *
 * @author Claudemir Rtools
 */
public class ParametroEstornoCaixa {

    private Object operador_id;
    private Object operador;
    private Object caixa;
    private Object responsavel_movimento;
    private Object nr_valor_baixa;
    private Object data_caixa;
    private Object data_estorno;
    private Object motivo;

    public ParametroEstornoCaixa(Object operador_id, Object operador, Object caixa, Object responsavel_movimento, Object nr_valor_baixa, Object data_caixa, Object data_estorno, Object motivo) {
        this.operador_id = operador_id;
        this.operador = operador;
        this.caixa = caixa;
        this.responsavel_movimento = responsavel_movimento;
        this.nr_valor_baixa = nr_valor_baixa;
        this.data_caixa = data_caixa;
        this.data_estorno = data_estorno;
        this.motivo = motivo;
    }

    public Object getOperador_id() {
        return operador_id;
    }

    public void setOperador_id(Object operador_id) {
        this.operador_id = operador_id;
    }

    public Object getOperador() {
        return operador;
    }

    public void setOperador(Object operador) {
        this.operador = operador;
    }

    public Object getResponsavel_movimento() {
        return responsavel_movimento;
    }

    public void setResponsavel_movimento(Object responsavel_movimento) {
        this.responsavel_movimento = responsavel_movimento;
    }

    public Object getNr_valor_baixa() {
        return nr_valor_baixa;
    }

    public void setNr_valor_baixa(Object nr_valor_baixa) {
        this.nr_valor_baixa = nr_valor_baixa;
    }

    public Object getData_caixa() {
        return data_caixa;
    }

    public void setData_caixa(Object data_caixa) {
        this.data_caixa = data_caixa;
    }

    public Object getData_estorno() {
        return data_estorno;
    }

    public void setData_estorno(Object data_estorno) {
        this.data_estorno = data_estorno;
    }

    public Object getMotivo() {
        return motivo;
    }

    public void setMotivo(Object motivo) {
        this.motivo = motivo;
    }

    public Object getCaixa() {
        return caixa;
    }

    public void setCaixa(Object caixa) {
        this.caixa = caixa;
    }
}
