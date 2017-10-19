package br.com.rtools.agendamentos;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ag_acrescentar_horario")
public class AgendaAcrescentarHorario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_horarios", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private AgendaHorarios horario;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data")
    private Date dtData;
    @Column(name = "nr_quantidade")
    private int quantidade;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    public AgendaAcrescentarHorario() {
        this.id = null;
        this.horario = new AgendaHorarios();
        this.setData("");
        this.quantidade = 0;
        this.usuario = new Usuario();
    }

    public AgendaAcrescentarHorario(Integer id, AgendaHorarios horario, String dataString, Integer quantidadeI, Usuario usuario1) {
        this.id = id;
        this.horario = horario;
        this.setData(dataString);
        this.quantidade = quantidadeI;
        this.usuario = usuario1;
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

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getData() {
        if (dtData != null) {
            return DataHoje.converteData(dtData);
        } else {
            return "";
        }
    }

    public void setData(String data) {
        if (!(data.isEmpty())) {
            this.dtData = DataHoje.converte(data);
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
