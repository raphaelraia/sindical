package br.com.rtools.pessoa.beans;

import br.com.rtools.pessoa.PessoaProfissao;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class PessoaProfissaoBean implements Serializable {

    private PessoaProfissao pessoaProfissao = new PessoaProfissao();

    public PessoaProfissaoBean() {
        //htmlTable = new HtmlDataTable();
    }

    public PessoaProfissao getPessoaProfissao() {
        return pessoaProfissao;
    }

    public void setPessoaProfissao(PessoaProfissao pessoaProfissao) {
        this.pessoaProfissao = pessoaProfissao;
    }

    public String salvar() {
        Dao dao = new Dao();
        if (pessoaProfissao.getId() == -1) {
            dao.save(pessoaProfissao, true);
        } else {
            dao.update(pessoaProfissao, true);
        }
        return null;
    }

    public String novo() {
        pessoaProfissao = new PessoaProfissao();
        return "cadPessoaProfissao";
    }

    public String excluir() {
        if (pessoaProfissao.getId() != -1) {
            Dao dao = new Dao();
            pessoaProfissao = (PessoaProfissao) dao.find(pessoaProfissao);
            if (dao.delete(pessoaProfissao, true)) {

            } else {

            }
        }
        pessoaProfissao = new PessoaProfissao();
        return "pesquisaPessoaProfissao";
    }

    public String editar() {
        //pessoaProfissao = (PessoaProfissao) getHtmlTable().getRowData();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        return "pessoaProfissao";
    }

    public List getListaPessoaProfissao() {
        List result = null;
        Dao dao = new Dao();
        result = new Dao().list(new PessoaProfissao(), true);
        return result;
    }

    public int getPegaPessoaProfissao() {
        return pessoaProfissao.getId();
    }

    public void CarregarPessoa() {
    }
}
