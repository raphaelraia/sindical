package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Cartao;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class CartaoBean implements Serializable {

    private Cartao cartao;
    private Cartao cartaoExcluir;
    private List<Cartao> listaCartao;

    private Integer indexPlano5 = 0;
    private List<SelectItem> listaPlano5 = new ArrayList();

    private Integer indexPlano5Baixa = 0;
    private List<SelectItem> listaPlano5Baixa = new ArrayList();
    
    private Integer indexPlano5Despesa = 0;
    private List<SelectItem> listaPlano5Despesa = new ArrayList();

    @PostConstruct
    public void init() {
        cartao = new Cartao();
        listaCartao = new ArrayList();

        loadListaPlano5();
        loadListaPlano5Baixa();
        loadListaPlano5Despesa();
        loadListaCartao();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("cartaoBean");
        GenericaSessao.remove("pesquisaPlano");
    }

    public final void loadListaCartao() {
        Dao dao = new Dao();
        listaCartao = dao.list(new Cartao(), true);
    }

    public final void loadListaPlano5() {
        listaPlano5.clear();

        FinanceiroDao dao = new FinanceiroDao();

        List<Plano5> result = dao.listaContas();

        listaPlano5.add(new SelectItem(0, "Selecione uma Conta", "-1"));

        for (int i = 0; i < result.size(); i++) {
            listaPlano5.add(
                    new SelectItem(
                            i + 1,
                            result.get(i).getConta(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaPlano5Baixa() {
        listaPlano5Baixa.clear();

        FinanceiroDao dao = new FinanceiroDao();

        List<Plano5> result = dao.listaContasBaixa();

        listaPlano5Baixa.add(new SelectItem(0, "Selecione uma Conta", "-1"));

        for (int i = 0; i < result.size(); i++) {
            listaPlano5Baixa.add(
                    new SelectItem(
                            i + 1,
                            result.get(i).getConta(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }
    
    public final void loadListaPlano5Despesa() {
        listaPlano5Despesa.clear();

        FinanceiroDao dao = new FinanceiroDao();

        List<Object> result = dao.listaContasDespesa();

        listaPlano5Despesa.add(new SelectItem(0, "Selecione uma Conta", "-1"));

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);
            
            listaPlano5Despesa.add(
                    new SelectItem(
                            i + 1,
                            linha.get(1).toString(),
                            "" + (Integer) linha.get(0)
                    )
            );
        }
    }

    public void salvar() {
        if (cartao.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Erro", "O campo descrição não pode estar vazio!");
            return;
        }

        if (Integer.valueOf(listaPlano5.get(indexPlano5).getDescription()) == -1) {
            GenericaMensagem.warn("Atenção", "Selecione um Banco a ser creditado!");
            return;
        }

        if (Integer.valueOf(listaPlano5Baixa.get(indexPlano5Baixa).getDescription()) == -1) {
            GenericaMensagem.warn("Atenção", "Selecione uma Conta para Baixa!");
            return;
        }

        if (Integer.valueOf(listaPlano5Despesa.get(indexPlano5Despesa).getDescription()) == -1) {
            GenericaMensagem.warn("Atenção", "Selecione uma Conta Despesa para Taxa!");
            return;
        }

        NovoLog novoLog = new NovoLog();
        Dao dao = new Dao();

        cartao.setPlano5((Plano5) dao.find(new Plano5(), Integer.valueOf(listaPlano5.get(indexPlano5).getDescription())));
        cartao.setPlano5Baixa((Plano5) dao.find(new Plano5(), Integer.valueOf(listaPlano5Baixa.get(indexPlano5Baixa).getDescription())));
        cartao.setPlano5Despesa((Plano5) dao.find(new Plano5(), Integer.valueOf(listaPlano5Despesa.get(indexPlano5Despesa).getDescription())));

        if (cartao.getId() == -1) {
            if (dao.save(cartao, true)) {
                novoLog.save(
                        "ID: " + cartao.getId()
                        + " - Plano 5: (" + cartao.getPlano5().getId() + ") " + cartao.getPlano5().getConta()
                        + " - Descrição: " + cartao.getDescricao()
                        + " - Dias: " + cartao.getDias()
                        + " - Taxa: " + cartao.getTaxa()
                        + " - Débito/Crédito: " + cartao.getDebitoCredito()
                );

                loadListaCartao();
                novo();
                GenericaMensagem.info("Sucesso", "Cartão salvo com Sucesso!");
            } else {
                GenericaMensagem.warn("Erro", "Não foi possível salvar este Cartão!");
            }
        } else {
            Cartao c = (Cartao) dao.find(cartao);
            String beforeUpdate
                    = "ID: " + c.getId()
                    + " - Plano 5: (" + c.getPlano5().getId() + ") " + c.getPlano5().getConta()
                    + " - Descrição: " + c.getDescricao()
                    + " - Dias: " + c.getDias()
                    + " - Taxa: " + c.getTaxa()
                    + " - Débito/Crédito: " + c.getDebitoCredito();

            if (dao.update(cartao, true)) {
                novoLog.update(beforeUpdate,
                        "ID: " + cartao.getId()
                        + " - Plano 5: (" + cartao.getPlano5().getId() + ") " + cartao.getPlano5().getConta()
                        + " - Descrição: " + cartao.getDescricao()
                        + " - Dias: " + cartao.getDias()
                        + " - Taxa: " + cartao.getTaxa()
                        + " - Débito/Crédito: " + cartao.getDebitoCredito()
                );

                loadListaCartao();
                novo();
                GenericaMensagem.info("Sucesso", "Cartão alterado com Sucesso!");
            } else {
                GenericaMensagem.warn("Erro", "Não foi possível alterar este Cartão!");
            }
        }
    }

    public void novo() {
        cartao = new Cartao();
        cartaoExcluir = new Cartao();
        indexPlano5 = 0;
        indexPlano5Baixa = 0;
        indexPlano5Despesa = 0;
    }

    public void excluir() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();

        if (dao.delete(cartaoExcluir, true)) {
            novoLog.delete(
                    "ID: " + cartaoExcluir.getId()
                    + " - Plano 5: (" + cartaoExcluir.getPlano5().getId() + ") " + cartaoExcluir.getPlano5().getConta()
                    + " - Plano 5 Baixa: (" + cartaoExcluir.getPlano5Baixa().getId() + ") " + cartaoExcluir.getPlano5Baixa().getConta()
                    + " - Plano 5 Despesa: (" + cartaoExcluir.getPlano5Despesa().getId() + ") " + cartaoExcluir.getPlano5Despesa().getConta()
                    + " - Descrição: " + cartaoExcluir.getDescricao()
                    + " - Dias: " + cartaoExcluir.getDias()
                    + " - Taxa: " + cartaoExcluir.getTaxa()
                    + " - Débito/Crédito: " + cartaoExcluir.getDebitoCredito()
            );

            cartaoExcluir = new Cartao();
            loadListaCartao();
            
            GenericaMensagem.info("Sucesso", "Cartão excluído com Sucesso!");
        } else {
            GenericaMensagem.warn("Erro", "Este cartão não pode ser excluido!");
        }
    }

    public void selecionarCartaoExcluir(Cartao c){
        cartaoExcluir = c;
    }
    
    public void editar(Cartao c) {
        cartao = c;

        for (int i = 0; i < listaPlano5.size(); i++) {
            if (cartao.getPlano5().getId() == Integer.valueOf(listaPlano5.get(i).getDescription())) {
                indexPlano5 = i;
            }
        }

        for (int i = 0; i < listaPlano5Baixa.size(); i++) {
            if (cartao.getPlano5Baixa() != null) {
                if (cartao.getPlano5Baixa().getId() == Integer.valueOf(listaPlano5Baixa.get(i).getDescription())) {
                    indexPlano5Baixa = i;
                }
            } else {
                indexPlano5Baixa = 0;
            }
        }
        
        for (int i = 0; i < listaPlano5Despesa.size(); i++) {
            if (cartao.getPlano5Despesa()!= null) {
                if (cartao.getPlano5Despesa().getId() == Integer.valueOf(listaPlano5Despesa.get(i).getDescription())) {
                    indexPlano5Despesa = i;
                }
            } else {
                indexPlano5Despesa = 0;
            }
        }
    }

    public Cartao getCartao() {
        return cartao;
    }

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public List<Cartao> getListaCartao() {
        return listaCartao;
    }

    public void setListaCartao(List<Cartao> listaCartao) {
        this.listaCartao = listaCartao;
    }

    public Integer getIndexPlano5() {
        return indexPlano5;
    }

    public void setIndexPlano5(Integer indexPlano5) {
        this.indexPlano5 = indexPlano5;
    }

    public List<SelectItem> getListaPlano5() {
        return listaPlano5;
    }

    public void setListaPlano5(List<SelectItem> listaPlano5) {
        this.listaPlano5 = listaPlano5;
    }

    public Integer getIndexPlano5Baixa() {
        return indexPlano5Baixa;
    }

    public void setIndexPlano5Baixa(Integer indexPlano5Baixa) {
        this.indexPlano5Baixa = indexPlano5Baixa;
    }

    public List<SelectItem> getListaPlano5Baixa() {
        return listaPlano5Baixa;
    }

    public void setListaPlano5Baixa(List<SelectItem> listaPlano5Baixa) {
        this.listaPlano5Baixa = listaPlano5Baixa;
    }

    public Integer getIndexPlano5Despesa() {
        return indexPlano5Despesa;
    }

    public void setIndexPlano5Despesa(Integer indexPlano5Despesa) {
        this.indexPlano5Despesa = indexPlano5Despesa;
    }

    public List<SelectItem> getListaPlano5Despesa() {
        return listaPlano5Despesa;
    }

    public void setListaPlano5Despesa(List<SelectItem> listaPlano5Despesa) {
        this.listaPlano5Despesa = listaPlano5Despesa;
    }

    public Cartao getCartaoExcluir() {
        return cartaoExcluir;
    }

    public void setCartaoExcluir(Cartao cartaoExcluir) {
        this.cartaoExcluir = cartaoExcluir;
    }

}
