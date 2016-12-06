package br.com.rtools.seguranca.beans;

import br.com.rtools.seguranca.UsuarioHistoricoAcesso;
import br.com.rtools.seguranca.dao.UsuarioHistoricoAcessoDao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class UsuarioHistoricoAcessoBean implements Serializable {

    private List<UsuarioHistoricoAcesso> listUsuarioHistoricoAcesso;

    @PostConstruct
    public void init() {
        listUsuarioHistoricoAcesso = new ArrayList();
        loadList();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("usuarioHistoricoAcessoBean");
    }

    public void loadList() {
        listUsuarioHistoricoAcesso = new ArrayList();
        listUsuarioHistoricoAcesso = new UsuarioHistoricoAcessoDao().findResume();
    }

    public List<UsuarioHistoricoAcesso> getListUsuarioHistoricoAcesso() {
        return listUsuarioHistoricoAcesso;
    }

    public void setListUsuarioHistoricoAcesso(List<UsuarioHistoricoAcesso> listUsuarioHistoricoAcesso) {
        this.listUsuarioHistoricoAcesso = listUsuarioHistoricoAcesso;
    }

}
