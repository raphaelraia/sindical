package br.com.rtools.configuracao;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "conf_sms", uniqueConstraints = @UniqueConstraint(columnNames = {"id_sms_grupo", "ds_usuario", "is_principal"}))
public class ConfiguracaoSms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_sms_grupo", referencedColumnName = "id", unique = true)
    @ManyToOne
    private ConfiguracaoSmsGrupo configuracaoSmsGrupo;
    @Column(name = "ds_usuario", length = 50, nullable = false)
    private String login;
    @Column(name = "ds_senha", length = 50, nullable = false)
    private String senha;
    @Column(name = "ds_url_servico", length = 50, nullable = false)
    private String urlServico;
    @Column(name = "is_principal", length = 50, nullable = false, columnDefinition = "boolean default false")
    private Boolean principal;

    public ConfiguracaoSms() {
        this.id = null;
        this.configuracaoSmsGrupo = null;
        this.login = "";
        this.senha = "";
        this.urlServico = "";
    }

    public ConfiguracaoSms(Integer id, ConfiguracaoSmsGrupo configuracaoSmsGrupo, String login, String senha, String urlServico) {
        this.id = id;
        this.configuracaoSmsGrupo = configuracaoSmsGrupo;
        this.login = login;
        this.senha = senha;
        this.urlServico = urlServico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ConfiguracaoSmsGrupo getConfiguracaoSmsGrupo() {
        return configuracaoSmsGrupo;
    }

    public void setConfiguracaoSmsGrupo(ConfiguracaoSmsGrupo configuracaoSmsGrupo) {
        this.configuracaoSmsGrupo = configuracaoSmsGrupo;
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

    public String getUrlServico() {
        return urlServico;
    }

    public void setUrlServico(String urlServico) {
        this.urlServico = urlServico;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

}
