package br.com.rtools.impressao;

public class ParametroMalaDireta {

    private Boolean selected;
    private Object grupo;
    private Object documento;
    private Object nome;
    private Object logradouro;
    private Object endereco;
    private Object numero;
    private Object complemento;
    private Object bairro;
    private Object cidade;
    private Object uf;
    private Object cep;
    private Object telefone1;
    private Object telefone2;
    private Object telefone3;
    private Object email1;
    private Object email2;
    private Object email3;

    public ParametroMalaDireta(Boolean selected, Object grupo, Object documento, Object nome, Object logradouro, Object endereco, Object numero, Object complemento, Object bairro, Object cidade, Object uf, Object cep, Object telefone1, Object telefone2, Object telefone3, Object email1, Object email2, Object email3) {
        this.selected = selected;
        this.grupo = grupo;
        this.documento = documento;
        this.nome = nome;
        this.logradouro = logradouro;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.telefone3 = telefone3;
        this.email1 = email1;
        this.email2 = email2;
        this.email3 = email3;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Object getGrupo() {
        return grupo;
    }

    public void setGrupo(Object grupo) {
        this.grupo = grupo;
    }

    public Object getDocumento() {
        return documento;
    }

    public void setDocumento(Object documento) {
        this.documento = documento;
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

    public Object getComplemento() {
        return complemento;
    }

    public void setComplemento(Object complemento) {
        this.complemento = complemento;
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

    public Object getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(Object telefone1) {
        this.telefone1 = telefone1;
    }

    public Object getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(Object telefone2) {
        this.telefone2 = telefone2;
    }

    public Object getTelefone3() {
        return telefone3;
    }

    public void setTelefone3(Object telefone3) {
        this.telefone3 = telefone3;
    }

    public Object getEmail1() {
        return email1;
    }

    public void setEmail1(Object email1) {
        this.email1 = email1;
    }

    public Object getEmail2() {
        return email2;
    }

    public void setEmail2(Object email2) {
        this.email2 = email2;
    }

    public Object getEmail3() {
        return email3;
    }

    public void setEmail3(Object email3) {
        this.email3 = email3;
    }
}
