package br.com.rtools.impressao;

public class RelatorioCobrancaExterna {

    private Object tipo_cobranca_descricao;
    private Object socio_codigo;
    private Object socio_nome;
    private Object mes;
    private Object ano;
    private Object valor;
    private Object endereco_cobranca;

    /**
     *
     * @param tipo_cobranca_descricao
     * @param socio_codigo
     * @param socio_nome
     * @param mes
     * @param ano
     * @param valor
     * @param endereco_cobranca
     */
    public RelatorioCobrancaExterna(Object tipo_cobranca_descricao, Object socio_codigo, Object socio_nome, Object mes, Object ano, Object valor, Object endereco_cobranca) {
        this.tipo_cobranca_descricao = tipo_cobranca_descricao;
        this.socio_codigo = socio_codigo;
        this.socio_nome = socio_nome;
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.endereco_cobranca = endereco_cobranca;
    }

    public Object getTipo_cobranca_descricao() {
        return tipo_cobranca_descricao;
    }

    public void setTipo_cobranca_descricao(Object tipo_cobranca_descricao) {
        this.tipo_cobranca_descricao = tipo_cobranca_descricao;
    }

    public Object getSocio_codigo() {
        return socio_codigo;
    }

    public void setSocio_codigo(Object socio_codigo) {
        this.socio_codigo = socio_codigo;
    }

    public Object getSocio_nome() {
        return socio_nome;
    }

    public void setSocio_nome(Object socio_nome) {
        this.socio_nome = socio_nome;
    }

    public Object getMes() {
        return mes;
    }

    public void setMes(Object mes) {
        this.mes = mes;
    }

    public Object getAno() {
        return ano;
    }

    public void setAno(Object ano) {
        this.ano = ano;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Object getEndereco_cobranca() {
        return endereco_cobranca;
    }

    public void setEndereco_cobranca(Object endereco_cobranca) {
        this.endereco_cobranca = endereco_cobranca;
    }

}
