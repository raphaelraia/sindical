package br.com.rtools.associativo.lista;

import br.com.rtools.associativo.Parentesco;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class ListaDependentes implements Serializable {

    private Fisica fisica;
    private Integer idParentesco;
    private Integer viaCarteirinha;
    private String validadeCarteirinha;
    private String validadeDependente;
    private Double nrDesconto;
    private List<SelectItem> listParentesco;
    private Boolean ativo;
    private Double valor;
    private ServicoPessoa servicoPessoa;

    public ListaDependentes() {
        this.fisica = new Fisica();
        this.idParentesco = 0;
        this.viaCarteirinha = 0;
        this.validadeCarteirinha = "";
        this.validadeDependente = "";
        this.nrDesconto = new Double(0);
        this.listParentesco = new ArrayList<>();
        this.ativo = false;
        this.valor = new Double(0);
        this.servicoPessoa = new ServicoPessoa();
    }

    public ListaDependentes(Fisica fisica, Integer idParentesco, Integer viaCarteirinha, String validadeCarteirinha, String validadeDependente, Double nrDesconto, List listParentesco, Boolean ativo, Double valor, ServicoPessoa servidoPessoa) {
        this.fisica = fisica;
        this.idParentesco = idParentesco;
        this.viaCarteirinha = viaCarteirinha;
        this.validadeCarteirinha = validadeCarteirinha;
        this.validadeDependente = validadeDependente;
        this.nrDesconto = nrDesconto;
        this.listParentesco = listParentesco;
        this.ativo = ativo;
        this.valor = valor;
        this.servicoPessoa = servidoPessoa;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Integer getIdParentesco() {
        return idParentesco;
    }

    public void setIdParentesco(Integer idParentesco) {
        this.idParentesco = idParentesco;
    }

    public String getParentescoString() {
        return Integer.toString(idParentesco);
    }

    public void setParentescoString(String parentescoString) {
        this.idParentesco = Integer.parseInt(parentescoString);
    }

    public Parentesco getParentesco() {
        if (idParentesco != null) {
            return (Parentesco) new Dao().find(new Parentesco(), Integer.parseInt(listParentesco.get(idParentesco).getDescription()));
        }
        return new Parentesco();
    }

    public Integer getViaCarteirinha() {
        return viaCarteirinha;
    }

    public void setViaCarteirinha(Integer viaCarteirinha) {
        this.viaCarteirinha = viaCarteirinha;
    }

    public String getValidadeCarteirinha() {
        return validadeCarteirinha;
    }

    public void setValidadeCarteirinha(String validadeCarteirinha) {
        this.validadeCarteirinha = validadeCarteirinha;
    }

    public String getValidadeDependente() {
        return validadeDependente;
    }

    public void setValidadeDependente(String validadeDependente) {
        this.validadeDependente = validadeDependente;
    }

    public Double getNrDesconto() {
        return nrDesconto;
    }

    public void setNrDesconto(Double nrDesconto) {
        this.nrDesconto = nrDesconto;
    }

    public String getDescontoString() {
        return Double.toString(nrDesconto);
    }

    public void setDescontoString(String desconto) {
        this.nrDesconto = Double.parseDouble(desconto);
    }

    public List getListParentesco() {
        return listParentesco;
    }

    public void setListParentesco(List listParentesco) {
        this.listParentesco = listParentesco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Double(valor);
    }

    public void setValorString(String valorString) {
        this.valor = Moeda.converteUS$(valorString);
    }

    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }
}
