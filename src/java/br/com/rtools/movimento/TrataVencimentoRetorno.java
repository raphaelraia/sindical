/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.movimento;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;

/**
 *
 * @author rtools2
 */
public class TrataVencimentoRetorno {
    
        private Boleto boleto;
        private Movimento movimento;
        private Juridica contabilidade;
        private Date vencimentoMovimento;
        private Date vencimentoBoleto;
        private Double valor;
        private Double juros;
        private Double multa;
        private Double correcao;
        private Double valor_calculado;
        private Boolean calcular;
        private Boolean vencido;
        private Boolean registrado;
        
        public TrataVencimentoRetorno(Boleto boleto, Movimento movimento, Juridica contabilidade, Date vencimentoMovimento, Date vencimentoBoleto, Double valor, Double juros, Double multa, Double correcao, Double valor_calculado, Boolean calcular, Boolean vencido, Boolean registrado) {
            this.boleto = boleto;
            this.movimento = movimento;
            this.contabilidade = contabilidade;
            this.vencimentoMovimento = vencimentoMovimento;
            this.vencimentoBoleto = vencimentoBoleto;
            this.valor = valor;
            this.juros = juros;
            this.multa = multa;
            this.correcao = correcao;
            this.valor_calculado = valor_calculado;
            this.calcular = calcular;
            this.vencido = vencido;
            this.registrado = registrado;
        }
        
        public TrataVencimentoRetorno(Boleto boleto, Date vencimentoBoleto, Double juros, Double multa, Double correcao, Double valor_calculado, Boolean calcular, Boolean vencido, Boolean registrado) {
            this.boleto = boleto;
            this.vencimentoBoleto = vencimentoBoleto;
            this.juros = juros;
            this.multa = multa;
            this.correcao = correcao;
            this.valor_calculado = valor_calculado;
            this.calcular = calcular;
            this.vencido = vencido;
            this.registrado = registrado;
        }

        public Boleto getBoleto() {
            return boleto;
        }

        public void setBoleto(Boleto boleto) {
            this.boleto = boleto;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Juridica getContabilidade() {
            return contabilidade;
        }

        public void setContabilidade(Juridica contabilidade) {
            this.contabilidade = contabilidade;
        }

        public Date getVencimentoMovimento() {
            return vencimentoMovimento;
        }

        public void setVencimentoMovimento(Date vencimentoMovimento) {
            this.vencimentoMovimento = vencimentoMovimento;
        }

        public String getVencimentoMovimentoString() {
            return DataHoje.converteData(vencimentoMovimento);
        }

        public void setVencimentoMovimentoString(String vencimentoMovimentoString) {
            this.vencimentoMovimento = DataHoje.converte(vencimentoMovimentoString);
        }

        public Date getVencimentoBoleto() {
            return vencimentoBoleto;
        }

        public void setVencimentoBoleto(Date vencimentoBoleto) {
            this.vencimentoBoleto = vencimentoBoleto;
        }

        public String getVencimentoBoletoString() {
            return DataHoje.converteData(vencimentoBoleto);
        }

        public void setVencimentoBoletooString(String vencimentoBoletoString) {
            this.vencimentoBoleto = DataHoje.converte(vencimentoBoletoString);
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteDoubleToString(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteStringToDouble(valorString);
        }

        public Double getJuros() {
            return juros;
        }

        public void setJuros(Double juros) {
            this.juros = juros;
        }

        public String getJurosString() {
            return Moeda.converteDoubleToString(juros);
        }

        public void setJurosString(String jurosString) {
            this.juros = Moeda.converteStringToDouble(jurosString);
        }

        public Double getMulta() {
            return multa;
        }

        public void setMulta(Double multa) {
            this.multa = multa;
        }

        public String getMultaString() {
            return Moeda.converteDoubleToString(multa);
        }

        public void setMultaString(String multaString) {
            this.multa = Moeda.converteStringToDouble(multaString);
        }

        public Double getCorrecao() {
            return correcao;
        }

        public void setCorrecao(Double correcao) {
            this.correcao = correcao;
        }

        public String getCorrecaoString() {
            return Moeda.converteDoubleToString(correcao);
        }

        public void setCorrecaoString(String correcaoString) {
            this.correcao = Moeda.converteStringToDouble(correcaoString);
        }

        public Double getValor_calculado() {
            return valor_calculado;
        }

        public void setValor_calculado(Double valor_calculado) {
            this.valor_calculado = valor_calculado;
        }

        public String getValor_calculadoString() {
            return Moeda.converteDoubleToString(valor_calculado);
        }

        public void setValor_calculadoString(String valor_calculadoString) {
            this.valor_calculado = Moeda.converteStringToDouble(valor_calculadoString);
        }

        public Boolean getCalcular() {
            return calcular;
        }

        public void setCalcular(Boolean calcular) {
            this.calcular = calcular;
        }

    public Boolean getVencido() {
        return vencido;
    }

    public void setVencido(Boolean vencido) {
        this.vencido = vencido;
    }

    public Boolean getRegistrado() {
        return registrado;
    }

    public void setRegistrado(Boolean registrado) {
        this.registrado = registrado;
    }
}
