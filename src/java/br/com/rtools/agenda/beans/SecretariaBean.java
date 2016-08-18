package br.com.rtools.agenda.beans;

import br.com.rtools.agenda.Secretaria;
import br.com.rtools.agenda.dao.SecretariaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.UsuarioDao;
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
public class SecretariaBean implements Serializable {

    private Secretaria secretaria;
    private List<SelectItem> listUsuarioSecretaria;
    private Integer idSecretaria;
    private List<SelectItem> listUsuario;
    private Integer idUsuario;
    private List<Secretaria> listSecretarias;

    @PostConstruct
    public void init() {
        secretaria = new Secretaria();
        listUsuarioSecretaria = new ArrayList();
        listUsuario = new ArrayList();
        idSecretaria = null;
        idUsuario = null;
        loadListUsuarioSecretaria();
        loadListUsuario();
        loadListSecretarias();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("secretariaBean");
    }

    public void clear() {
        GenericaSessao.remove("secretariaBean");
    }

    public void listener(String tcase) {
        loadListUsuario();
        loadListSecretarias();
    }

    public void loadListUsuarioSecretaria() {
        listUsuarioSecretaria = new ArrayList();
        List<Usuario> list = new Dao().list(new Usuario(), true);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAtivo()) {
                if (idSecretaria == null) {
                    idSecretaria = list.get(i).getId();
                }
                listUsuarioSecretaria.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
            }
        }
    }

    public void loadListUsuario() {
        listUsuario = new ArrayList();
        List<Usuario> list = new UsuarioDao().findNotInByTabela("age_secretaria", "id_usuario", "id_secretaria", idSecretaria.toString(), true);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAtivo()) {
                if (idUsuario == null) {
                    idUsuario = list.get(i).getId();
                }
                listUsuario.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
            }
        }
    }

    public void loadListSecretarias() {
        listSecretarias = new ArrayList();
        listSecretarias = new SecretariaDao().findBySecretaria(idSecretaria);

    }

    public void save() {
        Dao dao = new Dao();
        Secretaria secretaria = new Secretaria();
        secretaria.setSecretaria((Usuario) dao.find(new Usuario(), idSecretaria));
        secretaria.setUsuario((Usuario) dao.find(new Usuario(), idUsuario));
        if (secretaria.getSecretaria() == null) {
            GenericaMensagem.warn("VALIDAÇÃO", "INFORMAR UMA SECRETÁRIA!");
            return;
        }
        if (secretaria.getUsuario() == null) {
            GenericaMensagem.warn("VALIDAÇÃO", "INFORMAR UM USUÁRIO!");
            return;

        }
        if (secretaria.getId() == null) {
            if (dao.save(secretaria, true)) {
                loadListUsuario();
                loadListSecretarias();
                GenericaMensagem.info("SUCESSO", "REGISTRO INSERIDO");
            } else {
                GenericaMensagem.warn("ERRO", "AO REMOVER REGISTRO!");
            }
        }
    }

    public void remove(Secretaria s) {
        if (s.getId() != null) {
            if (new Dao().delete(s, true)) {
                GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
                loadListSecretarias();
            } else {
                GenericaMensagem.warn("ERRO", "AO REMOVER REGISTRO!");
            }
        }
    }

    public List<Secretaria> getListSecretarias() {
        return listSecretarias;
    }

    public void setListSecretarias(List<Secretaria> listSecretarias) {
        this.listSecretarias = listSecretarias;
    }

    public List<SelectItem> getListUsuarioSecretaria() {
        return listUsuarioSecretaria;
    }

    public void setListUsuarioSecretaria(List<SelectItem> listUsuarioSecretaria) {
        this.listUsuarioSecretaria = listUsuarioSecretaria;
    }

    public Integer getIdSecretaria() {
        return idSecretaria;
    }

    public void setIdSecretaria(Integer idSecretaria) {
        this.idSecretaria = idSecretaria;
    }

    public List<SelectItem> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(List<SelectItem> listUsuario) {
        this.listUsuario = listUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        this.secretaria = secretaria;
    }

}
