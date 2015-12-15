package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_configuracao_email",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_configuracao", "ds_contato", "ds_email"})
)
public class SisConfiguracaoEmail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_configuracao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Configuracao configuracao;
    @Column(name = "ds_contato", nullable = false, length = 100)
    private String contato;
    @Column(name = "ds_email", nullable = false, length = 100)
    private String email;

    public SisConfiguracaoEmail() {
        this.id = null;
        this.configuracao = null;
        this.contato = "";
        this.email = "";
    }

    public SisConfiguracaoEmail(Integer id, Configuracao configuracao, String contato, String email) {
        this.id = id;
        this.configuracao = configuracao;
        this.contato = contato;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Configuracao getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(Configuracao configuracao) {
        this.configuracao = configuracao;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
