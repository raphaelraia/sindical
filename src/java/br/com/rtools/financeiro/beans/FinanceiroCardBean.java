package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.dao.FormaPagamentoDao;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class FinanceiroCardBean implements Serializable {

    private String cliente = "";
    private Baixa baixa = new Baixa();
    private List<FormaPagamento> listFormaPagamento = new ArrayList();

    public void cardFormaPagamentoBaixa(String baixa_id) {
        try {
            cardFormaPagamentoBaixa(Integer.parseInt(baixa_id));
        } catch (NumberFormatException e) {

        }
    }

    public void cardFormaPagamentoBaixa(Integer baixa_id) {
        close();
        Dao dao = new Dao();
        baixa = (Baixa) dao.rebind(dao.find(new Baixa(), baixa_id));
        listFormaPagamento = new ArrayList();
        listFormaPagamento = new FormaPagamentoDao().findByBaixa(baixa_id);
    }

    public void close() {
        baixa = new Baixa();
        listFormaPagamento = new ArrayList();
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Baixa getBaixa() {
        return baixa;
    }

    public void setBaixa(Baixa baixa) {
        this.baixa = baixa;
    }

    public List<FormaPagamento> getListFormaPagamento() {
        return listFormaPagamento;
    }

    public void setListFormaPagamento(List<FormaPagamento> listFormaPagamento) {
        this.listFormaPagamento = listFormaPagamento;
    }

}
