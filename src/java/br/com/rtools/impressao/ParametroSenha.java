package br.com.rtools.impressao;

public class ParametroSenha {

    private String empresa_nome;
    private String empresa_documento;
    private String preposto;
    private String funcionario;
    private String usuario_nome;
    private String data;
    private String hora;
    private String senha;
    private String operacao;

    public ParametroSenha(String empresa_nome, String empresa_documento, String preposto, String funcionario, String usuario_nome, String data, String hora, String senha) {
        this.empresa_nome = empresa_nome;
        this.empresa_documento = empresa_documento;
        this.preposto = preposto;
        this.funcionario = funcionario;
        this.usuario_nome = usuario_nome;
        this.data = data;
        this.hora = hora;
        this.senha = senha;
        this.operacao = "";
    }
    public ParametroSenha(String empresa_nome, String empresa_documento, String preposto, String funcionario, String usuario_nome, String data, String hora, String senha, String operacao) {
        this.empresa_nome = empresa_nome;
        this.empresa_documento = empresa_documento;
        this.preposto = preposto;
        this.funcionario = funcionario;
        this.usuario_nome = usuario_nome;
        this.data = data;
        this.hora = hora;
        this.senha = senha;
        this.operacao = operacao;
    }

    public String getEmpresa_nome() {
        return empresa_nome;
    }

    public void setEmpresa_nome(String empresa_nome) {
        this.empresa_nome = empresa_nome;
    }

    public String getEmpresa_documento() {
        return empresa_documento;
    }

    public void setEmpresa_documento(String empresa_documento) {
        this.empresa_documento = empresa_documento;
    }

    public String getPreposto() {
        return preposto;
    }

    public void setPreposto(String preposto) {
        this.preposto = preposto;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getUsuario_nome() {
        return usuario_nome;
    }

    public void setUsuario_nome(String usuario_nome) {
        this.usuario_nome = usuario_nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

}
