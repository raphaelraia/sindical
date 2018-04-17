package br.com.rtools.sql;

import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Usuario;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sql_atualizar_base_script")
public class AtualizarBaseScript implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_atualizar_base", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AtualizarBase atualizarBase;
    @JoinColumn(name = "id_sql_events", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SqlEvents sqlEvents;
    @Lob
    @Column(name = "ds_script", nullable = false)
    private String script;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro", nullable = false)
    private Date dtCadastro;
    @Column(name = "is_reiniciar", columnDefinition = "boolean default false")
    private Boolean reiniciar;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_processamento")
    private Date dtProcessamento;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;

    public AtualizarBaseScript() {
        this.id = null;
        this.atualizarBase = null;
        this.sqlEvents = null;
        this.script = "";
        this.dtCadastro = new Date();
        this.reiniciar = false;
        this.dtProcessamento = null;
        this.usuario = null;
    }

    public AtualizarBaseScript(Integer id, AtualizarBase atualizarBase, SqlEvents sqlEvents, String script, Date dtCadastro, Boolean reiniciar, Date dtProcessamento, Usuario usuario) {
        this.id = id;
        this.atualizarBase = atualizarBase;
        this.sqlEvents = sqlEvents;
        this.script = script;
        this.dtCadastro = dtCadastro;
        this.reiniciar = reiniciar;
        this.dtProcessamento = dtProcessamento;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AtualizarBase getAtualizarBase() {
        return atualizarBase;
    }

    public void setAtualizarBase(AtualizarBase atualizarBase) {
        this.atualizarBase = atualizarBase;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public Boolean getReiniciar() {
        return reiniciar;
    }

    public void setReiniciar(Boolean reiniciar) {
        this.reiniciar = reiniciar;
    }

    public Date getDtProcessamento() {
        return dtProcessamento;
    }

    public void setDtProcessamento(Date dtProcessamento) {
        this.dtProcessamento = dtProcessamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public SqlEvents getSqlEvents() {
        return sqlEvents;
    }

    public void setSqlEvents(SqlEvents sqlEvents) {
        this.sqlEvents = sqlEvents;
    }

}
