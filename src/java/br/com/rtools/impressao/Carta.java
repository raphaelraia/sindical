package br.com.rtools.impressao;

public class Carta {

    private Object titulo;
    private Object texto;
    private Object assinatura;
    private Object rodape;
    private Object remetente_logo;
    private Object remetente_nome;
    private Object remetente_endereco;
    private Object remetente_complemento;
    private Object destinatario_nome;
    private Object destinatario_endereco;
    private Object destinatario_complemento;

    public Carta() {
        this.titulo = null;
        this.texto = null;
        this.assinatura = null;
        this.rodape = null;
        this.remetente_logo = null;
        this.remetente_nome = null;
        this.remetente_endereco = null;
        this.remetente_complemento = null;
        this.destinatario_nome = null;
        this.destinatario_endereco = null;
        this.destinatario_complemento = null;
    }

    public Carta(Object titulo, Object texto, Object assinatura, Object rodape) {
        this.titulo = titulo;
        this.texto = texto;
        this.assinatura = assinatura;
        this.rodape = rodape;

    }

    public Carta(Object titulo, Object texto, Object assinatura, Object rodape, Object remetente_logo, Object remetente_nome, Object remetente_endereco, Object remetente_complemento, Object destinatario_nome, Object destinatario_endereco, Object destinatario_complemento) {
        this.titulo = titulo;
        this.texto = texto;
        this.assinatura = assinatura;
        this.rodape = rodape;
        this.remetente_logo = remetente_logo;
        this.remetente_nome = remetente_nome;
        this.remetente_endereco = remetente_endereco;
        this.remetente_complemento = remetente_complemento;
        this.destinatario_nome = destinatario_nome;
        this.destinatario_endereco = destinatario_endereco;
        this.destinatario_complemento = destinatario_complemento;
    }

    public Object getTitulo() {
        return titulo;
    }

    public void setTitulo(Object titulo) {
        this.titulo = titulo;
    }

    public Object getTexto() {
        return texto;
    }

    public void setTexto(Object texto) {
        this.texto = texto;
    }

    public Object getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(Object assinatura) {
        this.assinatura = assinatura;
    }

    public Object getRodape() {
        return rodape;
    }

    public void setRodape(Object rodape) {
        this.rodape = rodape;
    }

    public Object getRemetente_logo() {
        return remetente_logo;
    }

    public void setRemetente_logo(Object remetente_logo) {
        this.remetente_logo = remetente_logo;
    }

    public Object getRemetente_nome() {
        return remetente_nome;
    }

    public void setRemetente_nome(Object remetente_nome) {
        this.remetente_nome = remetente_nome;
    }

    public Object getRemetente_endereco() {
        return remetente_endereco;
    }

    public void setRemetente_endereco(Object remetente_endereco) {
        this.remetente_endereco = remetente_endereco;
    }

    public Object getRemetente_complemento() {
        return remetente_complemento;
    }

    public void setRemetente_complemento(Object remetente_complemento) {
        this.remetente_complemento = remetente_complemento;
    }

    public Object getDestinatario_nome() {
        return destinatario_nome;
    }

    public void setDestinatario_nome(Object destinatario_nome) {
        this.destinatario_nome = destinatario_nome;
    }

    public Object getDestinatario_endereco() {
        return destinatario_endereco;
    }

    public void setDestinatario_endereco(Object destinatario_endereco) {
        this.destinatario_endereco = destinatario_endereco;
    }

    public Object getDestinatario_complemento() {
        return destinatario_complemento;
    }

    public void setDestinatario_complemento(Object destinatario_complemento) {
        this.destinatario_complemento = destinatario_complemento;
    }

}
