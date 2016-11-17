package br.com.rtools.impressao;

public class ConviteClube {

    // CONVIDADO
    private Object nome;
    private Object emissao;
    private Object validade;
    // LOGO DO CONVITE
    private Object foto;
    private Object barras;
    private Object semana;
    private Object obs;
    private Object rodape;

    public ConviteClube() {
        this.nome = "";
        this.emissao = "";
        this.validade = "";
        this.foto = "";
        this.barras = "";
        this.semana = "";
        this.obs = "";
        this.rodape = "";
    }

    public ConviteClube(Object nome, Object emissao, Object validade, Object foto, Object barras, Object semana, Object obs, Object rodape) {
        this.nome = nome;
        this.emissao = emissao;
        this.validade = validade;
        this.foto = foto;
        this.barras = barras;
        this.semana = semana;
        this.obs = obs;
        this.rodape = rodape;
    }

    // CONVIDADO    
    public Object getNome() {
        return nome;
    }

    // CONVIDADO
    public void setNome(Object nome) {
        this.nome = nome;
    }

    public Object getEmissao() {
        return emissao;
    }

    public void setEmissao(Object emissao) {
        this.emissao = emissao;
    }

    public Object getValidade() {
        return validade;
    }

    public void setValidade(Object validade) {
        this.validade = validade;
    }

    // LOGO DO CONVITE    
    public Object getFoto() {
        return foto;
    }

    // LOGO DO CONVITE    
    public void setFoto(Object foto) {
        this.foto = foto;
    }

    public Object getBarras() {
        return barras;
    }

    public void setBarras(Object barras) {
        this.barras = barras;
    }

    public Object getSemana() {
        return semana;
    }

    public void setSemana(Object semana) {
        this.semana = semana;
    }

    public Object getObs() {
        return obs;
    }

    public void setObs(Object obs) {
        this.obs = obs;
    }

    public Object getRodape() {
        return rodape;
    }

    public void setRodape(Object rodape) {
        this.rodape = rodape;
    }

}
