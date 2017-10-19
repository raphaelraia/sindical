package br.com.rtools.agendamentos;

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
@Table(name = "ag_agendamento_horario", uniqueConstraints = @UniqueConstraint(columnNames = {"id_agendamento", "id_horario"}))
public class AgendamentoHorario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_agendamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Agendamentos agendamento;
    @JoinColumn(name = "id_horario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AgendaHorarios agendaHorarios;

    public AgendamentoHorario() {
        this.id = null;
        this.agendamento = null;
        this.agendaHorarios = null;
    }

    public AgendamentoHorario(Integer id, Agendamentos agendamento, AgendaHorarios agendaHorarios) {
        this.id = id;
        this.agendamento = agendamento;
        this.agendaHorarios = agendaHorarios;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Agendamentos getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamentos agendamento) {
        this.agendamento = agendamento;
    }

    public AgendaHorarios getAgendaHorarios() {
        return agendaHorarios;
    }

    public void setAgendaHorarios(AgendaHorarios agendaHorarios) {
        this.agendaHorarios = agendaHorarios;
    }

}
