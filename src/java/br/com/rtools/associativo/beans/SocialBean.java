package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SocialBean implements Serializable {

    private List listPessoasMesmaMatricula;
    private List listMatriculaAtivaAtivacaoDesordenada;

    public Boolean getExistPessoasMesmaMatricula() {
        Boolean r = false;
        if (listPessoasMesmaMatricula == null) {
            r = new SociosDao().existPessoasMesmaMatricula();
        }
        return r;
    }

    public List<Pessoa> getListPessoasMesmaMatricula() {
        if (listPessoasMesmaMatricula == null) {
            listPessoasMesmaMatricula = new ArrayList();
            listPessoasMesmaMatricula = new SociosDao().listPessoasMesmaMatricula();
        }
        return listPessoasMesmaMatricula;
    }

    public Boolean getExistatriculaAtivaAtivacaoDesordenada() {
        Boolean r = false;
        if (listMatriculaAtivaAtivacaoDesordenada == null) {
            r = new SociosDao().existMatriculaAtivaAtivacaoDesordenada();
        }
        return r;
    }

    public List<Pessoa> getListatriculaAtivaAtivacaoDesordenada() {
        if (listMatriculaAtivaAtivacaoDesordenada == null) {
            listMatriculaAtivaAtivacaoDesordenada = new ArrayList();
            listMatriculaAtivaAtivacaoDesordenada = new SociosDao().listMatriculaAtivaAtivacaoDesordenada();
        }
        return listMatriculaAtivaAtivacaoDesordenada;
    }

}
