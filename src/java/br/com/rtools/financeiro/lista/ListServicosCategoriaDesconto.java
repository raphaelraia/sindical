package br.com.rtools.financeiro.lista;

import br.com.rtools.associativo.CategoriaDesconto;
import br.com.rtools.utilitarios.Moeda;

public class ListServicosCategoriaDesconto {

    private CategoriaDesconto categoriaDesconto;
    private double valorDesconto;

    public ListServicosCategoriaDesconto() {
        this.categoriaDesconto = new CategoriaDesconto();
        this.valorDesconto = 0;
    }

    public ListServicosCategoriaDesconto(CategoriaDesconto categoriaDesconto, double valorDesconto) {
        this.categoriaDesconto = categoriaDesconto;
        this.valorDesconto = valorDesconto;
    }

    public CategoriaDesconto getCategoriaDesconto() {
        return categoriaDesconto;
    }

    public void setCategoriaDesconto(CategoriaDesconto categoriaDesconto) {
        this.categoriaDesconto = categoriaDesconto;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getValorDescontoString() {
        return Moeda.converteR$Double(valorDesconto);
    }

    public void setValorDescontoString(String valorDescontoString) {
        this.valorDesconto = Moeda.converteUS$(valorDescontoString);
    }

}
