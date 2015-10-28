package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.BoletoNaoBaixado;
import br.com.rtools.associativo.dao.BoletoNaoBaixadoDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class BoletoNaoBaixadoBean {
    private String numeroBoleto = "";
    private Pessoa pessoa = new Pessoa();
    private List<BoletoNaoBaixado> listaBoletoNaoBaixado = new ArrayList();
    

    public void loadListaBoleto(){
        listaBoletoNaoBaixado.clear();
        
        if (pessoa.getId() != -1){
            BoletoNaoBaixadoDao dao = new BoletoNaoBaixadoDao();
            listaBoletoNaoBaixado = dao.listaBoletoNaoBaixado(pessoa.getId());
        }
    }
    
    public void removerPessoa(){
        pessoa = new Pessoa();
        listaBoletoNaoBaixado.clear();
    }
    
    public void pesquisarBoleto(){
        if (numeroBoleto.isEmpty()) {
            if (pessoa.getId() != -1) {
                loadListaBoleto();
            }
            return;
        }

        try {
            BoletoNaoBaixadoDao dao = new BoletoNaoBaixadoDao();
            Pessoa p = dao.pesquisaPessoaPorBoletoNaoBaixado(numeroBoleto);
            
            if (p != null) {
                pessoa = p;
                loadListaBoleto();
            }
        } catch (Exception e) {
            numeroBoleto = "";
            GenericaMensagem.fatal("Atenção", "Digite um número de Boleto válido!");
        }
    }
    
    public String getNumeroBoleto() {
        return numeroBoleto;
    }

    public void setNumeroBoleto(String numeroBoleto) {
        this.numeroBoleto = numeroBoleto;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")){
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            loadListaBoleto();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<BoletoNaoBaixado> getListaBoletoNaoBaixado() {
        return listaBoletoNaoBaixado;
    }

    public void setListaBoletoNaoBaixado(List<BoletoNaoBaixado> listaBoletoNaoBaixado) {
        this.listaBoletoNaoBaixado = listaBoletoNaoBaixado;
    }
}
