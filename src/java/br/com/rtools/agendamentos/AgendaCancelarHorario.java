package br.com.rtools.agendamentos;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ag_cancelar_horario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_horario", "dt_data"})
)
public class AgendaCancelarHorario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_horario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AgendaHorarios horario;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @Column(name = "dt_data", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtData;
    @Column(name = "nr_quantidade", columnDefinition = "integer default 0", nullable = false)
    private Integer quantidade;

    public AgendaCancelarHorario() {
        this.id = null;
        this.horario = null;
        this.usuario = null;
        this.dtData = new Date();
        this.quantidade = 0;
    }

    public AgendaCancelarHorario(Integer id, AgendaHorarios horario, Usuario usuario, Date dtData, Integer quantidade) {
        this.id = id;
        this.horario = horario;
        this.usuario = usuario;
        this.dtData = dtData;
        this.quantidade = quantidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AgendaHorarios getHorario() {
        return horario;
    }

    public void setHorario(AgendaHorarios horario) {
        this.horario = horario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getData() {
        return DataHoje.converteData(dtData);
    }

    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
