package br.com.rtools.seguranca;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.dao.UsuarioHistoricoAcessoDao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "seg_usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.pesquisaID", query = "SELECT U FROM Usuario U WHERE U.id = :pid ")
    ,
    @NamedQuery(name = "Usuario.findAll", query = "SELECT U FROM Usuario U ORDER BY U.pessoa.nome ASC, U.login ASC ")
})
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @Column(name = "ds_login", length = 15, nullable = false, unique = true)
    private String login;
    @Column(name = "ds_senha", length = 6, nullable = false)
    private String senha;
    @Column(name = "is_ativo", columnDefinition = "boolean default false")
    private boolean ativo;
    @Column(name = "ds_email", length = 255)
    private String email;
    @Column(name = "is_autenticado", columnDefinition = "boolean default false", nullable = false)
    private boolean autenticado;

    @Transient
    private List<UsuarioHistoricoAcesso> listUsuarioHistoricoAcesso;

    public Usuario() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.login = "";
        this.senha = "";
        this.ativo = false;
        this.email = "";
        this.autenticado = false;
        this.listUsuarioHistoricoAcesso = new ArrayList();
    }

    public Usuario(int id, Pessoa pessoa, String login, String senha, boolean ativo, String email, Boolean autenticado) {
        this.id = id;
        this.pessoa = pessoa;
        this.login = login;
        this.senha = senha;
        this.ativo = ativo;
        this.email = email;
        this.autenticado = autenticado;
        this.listUsuarioHistoricoAcesso = new ArrayList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", pessoa=" + pessoa + ", login=" + login + ", ativo=" + ativo + ", email=" + email + '}';
    }

    public static Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            return (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return null;
    }

    public void loadListUsuarioHistoricoAcesso() {
        listUsuarioHistoricoAcesso = new ArrayList();
        if (id != -1) {
            listUsuarioHistoricoAcesso = new UsuarioHistoricoAcessoDao().list(id);
        }
    }

    public List<UsuarioHistoricoAcesso> getListUsuarioHistoricoAcesso() {
        return listUsuarioHistoricoAcesso;
    }

    public void setListUsuarioHistoricoAcesso(List<UsuarioHistoricoAcesso> listUsuarioHistoricoAcesso) {
        this.listUsuarioHistoricoAcesso = listUsuarioHistoricoAcesso;
    }

}
