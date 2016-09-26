package br.com.rtools.impressao;

public class Etiquetas {

    private Object nome;
    private Object logradouro;
    private Object endereco;
    private Object numero;
    private Object bairro;
    private Object cidade;
    private Object uf;
    private Object cep;
    private Object complemento;
    private Object observacao;

    public Etiquetas() {
        this.nome = "";
        this.logradouro = "";
        this.endereco = "";
        this.numero = "";
        this.bairro = "";
        this.cidade = "";
        this.uf = "";
        this.cep = "";
        this.complemento = "";
        this.observacao = "";
    }

    public Etiquetas(Object nome, Object logradouro, Object endereco, Object numero, Object bairro, Object cidade, Object uf, Object cep, Object complemento) {
        this.nome = nome;
        this.logradouro = logradouro;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.complemento = complemento;
    }

    public Etiquetas(Object nome, Object logradouro, Object endereco, Object numero, Object bairro, Object cidade, Object uf, Object cep, Object complemento, Object observacao) {
        this.nome = nome;
        this.logradouro = logradouro;
        this.endereco = endereco;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.complemento = complemento;
        this.observacao = observacao;
    }

    public Object getNome() {
        return nome;
    }

    public void setNome(Object nome) {
        this.nome = nome;
    }

    public Object getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Object logradouro) {
        this.logradouro = logradouro;
    }

    public Object getEndereco() {
        return endereco;
    }

    public void setEndereco(Object endereco) {
        this.endereco = endereco;
    }

    public Object getNumero() {
        return numero;
    }

    public void setNumero(Object numero) {
        this.numero = numero;
    }

    public Object getBairro() {
        return bairro;
    }

    public void setBairro(Object bairro) {
        this.bairro = bairro;
    }

    public Object getCidade() {
        return cidade;
    }

    public void setCidade(Object cidade) {
        this.cidade = cidade;
    }

    public Object getUf() {
        return uf;
    }

    public void setUf(Object uf) {
        this.uf = uf;
    }

    public Object getCep() {
        return cep;
    }

    public void setCep(Object cep) {
        this.cep = cep;
    }

    public Object getComplemento() {
        return complemento;
    }

    public void setComplemento(Object complemento) {
        this.complemento = complemento;
    }

    public Object getObservacao() {
        return observacao;
    }

    public void setObservacao(Object observacao) {
        this.observacao = observacao;
    }
}
