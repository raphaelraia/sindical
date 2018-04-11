package br.com.rtools.sistema;

import br.com.rtools.seguranca.Usuario;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_backup_postgres")
public class BackupPostgres implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_configuracao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Configuracao configuracao;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @Column(name = "dt_pedido")
    @Temporal(TemporalType.DATE)
    private Date dtPedido;
    @Column(name = "dt_enviado")
    @Temporal(TemporalType.DATE)
    private Date dtEnviado;
    @Column(name = "dt_processado")
    @Temporal(TemporalType.DATE)
    private Date dtProcessado;

    public BackupPostgres() {
        this.id = null;
        this.configuracao = null;
        this.usuario = null;
        this.dtPedido = new Date();
        this.dtEnviado = null;
        this.dtProcessado = null;
    }

    public BackupPostgres(Integer id, Configuracao configuracao, Usuario usuario, Date dtPedido, Date dtEnviado, Date dtProcessado) {
        this.id = id;
        this.configuracao = configuracao;
        this.usuario = usuario;
        this.dtPedido = dtPedido;
        this.dtEnviado = dtEnviado;
        this.dtProcessado = dtProcessado;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDtPedido() {
        return dtPedido;
    }

    public void setDtPedido(Date dtPedido) {
        this.dtPedido = dtPedido;
    }

    public Date getDtEnviado() {
        return dtEnviado;
    }

    public void setDtEnviado(Date dtEnviado) {
        this.dtEnviado = dtEnviado;
    }

    public Date getDtProcessado() {
        return dtProcessado;
    }

    public void setDtProcessado(Date dtProcessado) {
        this.dtProcessado = dtProcessado;
    }

}
