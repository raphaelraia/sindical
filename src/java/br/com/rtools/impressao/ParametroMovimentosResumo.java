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
public class ParametroMovimentosResumo {
    private String mes;
    private String ano;
    private String empresa_cnpj;
    private String empresa_nome;
    private String contribuicao;
    private float valor;
    private float taxa;
    private float liquido;
    private String cidade;

    public ParametroMovimentosResumo(String mes, String ano, String contribuicao, float valor, float taxa, float liquido) {
        this.mes = mes;
        this.ano = ano;
        this.contribuicao = contribuicao;
        this.valor = valor;
        this.taxa = taxa;
        this.liquido = liquido;
    }

    public ParametroMovimentosResumo(String mes, String ano, String empresa_cnpj, String empresa_nome, String contribuicao, float valor, float taxa, float liquido) {
        this.mes = mes;
        this.ano = ano;
        this.empresa_cnpj = empresa_cnpj;
        this.empresa_nome = empresa_nome;
        this.contribuicao = contribuicao;
        this.valor = valor;
        this.taxa = taxa;
        this.liquido = liquido;
    }
    
    public ParametroMovimentosResumo(String cidade, String mes, String ano, String contribuicao, float valor, float taxa, float liquido) {
        this.cidade = cidade;
        this.mes = mes;
        this.ano = ano;
        this.contribuicao = contribuicao;
        this.valor = valor;
        this.taxa = taxa;
        this.liquido = liquido;
    }    

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getEmpresa_cnpj() {
        return empresa_cnpj;
    }

    public void setEmpresa_cnpj(String empresa_cnpj) {
        this.empresa_cnpj = empresa_cnpj;
    }

    public String getEmpresa_nome() {
        return empresa_nome;
    }

    public void setEmpresa_nome(String empresa_nome) {
        this.empresa_nome = empresa_nome;
    }

    public String getContribuicao() {
        return contribuicao;
    }

    public void setContribuicao(String contribuicao) {
        this.contribuicao = contribuicao;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public float getTaxa() {
        return taxa;
    }

    public void setTaxa(float taxa) {
        this.taxa = taxa;
    }

    public float getLiquido() {
        return liquido;
    }

    public void setLiquido(float liquido) {
        this.liquido = liquido;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    
    
}
