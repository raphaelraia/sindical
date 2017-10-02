package br.com.rtools.agendamentos;

import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
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
@Table(name = "ag_agendamento_servico",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_agendamento", "id_servico"})
)
public class AgendamentoServico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_agendamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Agendamentos agendamento;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servico;
    @JoinColumn(name = "id_movimento", referencedColumnName = "id")
    @ManyToOne
    private Movimento movimento;
    @Column(name = "nr_qtde", columnDefinition = "integer default 0", nullable = false)
    private Integer nrQrde;
    @Column(name = "dt_data", columnDefinition = "integer default 0", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtData;

    public AgendamentoServico() {
        this.id = null;
        this.agendamento = null;
        this.servico = null;
        this.movimento = null;
        this.nrQrde = 0;
        this.dtData = new Date();
    }

    public AgendamentoServico(Integer id, Agendamentos agendamento, Servicos servico, Movimento movimento, Integer nrQrde, Date dtData) {
        this.id = id;
        this.agendamento = agendamento;
        this.servico = servico;
        this.movimento = movimento;
        this.nrQrde = nrQrde;
        this.dtData = dtData;
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

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public Integer getNrQrde() {
        return nrQrde;
    }

    public void setNrQrde(Integer nrQrde) {
        this.nrQrde = nrQrde;
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

    public String getHora() {
        return DataHoje.converteHora(dtData);
    }

}
