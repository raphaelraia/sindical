package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ContaTipoPagamento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.ContaRecebimentoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author claudemir
 */
@ManagedBean
@SessionScoped
public class ContaRecebimentoBean implements Serializable {

    private List<ContaTipoPagamento> listaContaTipoPagamento = new ArrayList();
    private ContaTipoPagamento ctpSelecionado = new ContaTipoPagamento();
    private List<ObjectPlano5> listaPlano5 = new ArrayList();
    private ObjectPlano5 plano5Selecionado = null;
    private String pesquisarConta = "";

    public ContaRecebimentoBean() {
        loadListaContaTipoPagamento();
        loadListaPlano5();
    }

    public final void loadListaContaTipoPagamento() {
        listaContaTipoPagamento.clear();

        listaContaTipoPagamento = new ContaRecebimentoDao().listaContaTipoPagamento();
    }

    public final void loadListaPlano5() {
        listaPlano5.clear();
        plano5Selecionado = null;

        List<Object> result = new ContaRecebimentoDao().listaPlano5(pesquisarConta);

        for (Object ob : result) {
            List linha = (List) ob;

            listaPlano5.add(
                    new ObjectPlano5(
                            (Integer) linha.get(0),
                            (String) linha.get(1) + " - " + (String) linha.get(2) + " - " + (String) linha.get(3)
                    )
            );
        }
    }

    public void selecionar(ContaTipoPagamento ctp) {
        ctpSelecionado = ctp;

        plano5Selecionado = null;
    }

    public void salvar() {
        if (plano5Selecionado == null) {
            GenericaMensagem.error("Atenção", "Selecione uma Conta");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        ctpSelecionado.setPlano5((Plano5) dao.find(new Plano5(), plano5Selecionado.getId()));

        if (!dao.update(ctpSelecionado)) {
            GenericaMensagem.error("Atenção", "Erro ao Atualizar Tipo de Pagamento!");
            return;
        }

        dao.commit();

        loadListaContaTipoPagamento();
        
        GenericaMensagem.info("Sucesso", "Tipo de Pagamento atualizado!");
        
    }

    public List<ContaTipoPagamento> getListaContaTipoPagamento() {
        return listaContaTipoPagamento;
    }

    public void setListaContaTipoPagamento(List<ContaTipoPagamento> listaContaTipoPagamento) {
        this.listaContaTipoPagamento = listaContaTipoPagamento;
    }

    public ContaTipoPagamento getCtpSelecionado() {
        return ctpSelecionado;
    }

    public void setCtpSelecionado(ContaTipoPagamento ctpSelecionado) {
        this.ctpSelecionado = ctpSelecionado;
    }

    public List<ObjectPlano5> getListaPlano5() {
        return listaPlano5;
    }

    public void setListaPlano5(List<ObjectPlano5> listaPlano5) {
        this.listaPlano5 = listaPlano5;
    }

    public class ObjectPlano5 {

        private int id;
        private String conta;

        public ObjectPlano5() {
            this.id = -1;
            this.conta = "";
        }
        
        public ObjectPlano5(Integer id, String conta) {
            this.id = id;
            this.conta = conta;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

    }

    public ObjectPlano5 getPlano5Selecionado() {
        return plano5Selecionado;
    }

    public void setPlano5Selecionado(ObjectPlano5 plano5Selecionado) {
        this.plano5Selecionado = plano5Selecionado;
    }

    public String getPesquisarConta() {
        return pesquisarConta;
    }

    public void setPesquisarConta(String pesquisarConta) {
        this.pesquisarConta = pesquisarConta;
    }

}
