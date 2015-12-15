package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_notificacao_cliente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_notificacao", "id_configuracao"})
)
public class SisNotificacaoCliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_configuracao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Configuracao configuracao;
    @JoinColumn(name = "id_notificacao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SisNotificacao sisNotificacao;

    public SisNotificacaoCliente() {
        this.id = null;
        this.configuracao = null;
        this.sisNotificacao = null;
    }

    public SisNotificacaoCliente(Integer id, Configuracao configuracao, SisNotificacao sisNotificacao) {
        this.id = id;
        this.configuracao = configuracao;
        this.sisNotificacao = sisNotificacao;
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

    public SisNotificacao getSisNotificacao() {
        return sisNotificacao;
    }

    public void setSisNotificacao(SisNotificacao sisNotificacao) {
        this.sisNotificacao = sisNotificacao;
    }

}
