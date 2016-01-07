package br.com.rtools.seguranca;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "seg_web_service")
public class WebService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_login", length = 15, nullable = false, unique = true)
    private String login;
    @Column(name = "ds_senha", length = 6, nullable = false)
    private String senha;
    @Column(name = "is_ativo", columnDefinition = "boolean default false", nullable = false)
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}
