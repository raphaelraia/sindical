package br.com.rtools.agendamentos;

import br.com.rtools.financeiro.Movimento;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ag_cancelamento")
public class AgendamentoCancelamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data", nullable = false)
    private Date dtData;
    @JoinColumn(name = "id_agendamento_servico_horario", referencedColumnName = "id", unique = true, nullable = false)
    @ManyToOne
    private AgendamentoServicoHorario agendamentoServicoHorario;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @Lob
    @Column(name = "ds_motivo", nullable = false)
    private String motivo;

    public AgendamentoCancelamento() {
        this.id = null;
        this.dtData = new Date();
        this.agendamentoServicoHorario = null;
        this.usuario = null;
        this.motivo = "";
    }

    public AgendamentoCancelamento(Integer id, Date dtData, AgendamentoServicoHorario agendamentoServicoHorario, Usuario usuario, Movimento movimento, String motivo) {
        this.id = id;
        this.dtData = dtData;
        this.agendamentoServicoHorario = agendamentoServicoHorario;
        this.usuario = usuario;
        this.motivo = motivo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public AgendamentoServicoHorario getAgendamentoServicoHorario() {
        return agendamentoServicoHorario;
    }

    public void setAgendamentoServicoHorario(AgendamentoServicoHorario agendamentoServicoHorario) {
        this.agendamentoServicoHorario = agendamentoServicoHorario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
