package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Credenciadores;
import br.com.rtools.associativo.dao.CredenciadoresDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class CredenciadoresBean implements Serializable {

    private Credenciadores credenciadores = new Credenciadores();
    private List<Credenciadores> listaCredenciadores = new ArrayList();

    public CredenciadoresBean() {
        this.loadListaCredenciadores();
    }

    public final void loadListaCredenciadores() {
        listaCredenciadores.clear();

        listaCredenciadores = new CredenciadoresDao().listaTodosCredenciadores();
    }

    public void salvar() {
        Dao dao = new Dao();

        dao.openTransaction();
        if (credenciadores.getId() == -1) {
            if (!dao.save(credenciadores)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao salvar credenciador!");
                return;
            }

            GenericaMensagem.info("Sucesso", "Credenciador salvo!");
        } else {
            if (!dao.update(credenciadores)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao atualizar credenciador!");
                return;
            }

            GenericaMensagem.info("Sucesso", "Credenciador atualizado!");
        }

        dao.commit();

        loadListaCredenciadores();
        
        novo();

    }

    public void excluir(Credenciadores cr) {
        
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(cr)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Erro ao excluir credenciador!");
            return;
        }

        GenericaMensagem.info("Sucesso", "Credenciador excluído!");

        dao.commit();

        loadListaCredenciadores();
        
        novo();
        
    }

    public void editar(Credenciadores cr) {
        credenciadores = cr;
    }
    
    public void removerPessoa(){
        credenciadores.setPessoa(new Pessoa());
        
    }
    
    public void novo(){
        credenciadores = new Credenciadores();
    }

    public Credenciadores getCredenciadores() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            credenciadores.setPessoa((Pessoa) GenericaSessao.getObject("pessoaPesquisa", true));
        }
        return credenciadores;
    }

    public void setCredenciadores(Credenciadores credenciadores) {
        this.credenciadores = credenciadores;
    }

    public List<Credenciadores> getListaCredenciadores() {
        return listaCredenciadores;
    }

    public void setListaCredenciadores(List<Credenciadores> listaCredenciadores) {
        this.listaCredenciadores = listaCredenciadores;
    }
}
