package br.com.rtools.associativo;

import br.com.rtools.financeiro.Evt;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_campeonato",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_evento",
                    "id_modalidade",
                    "ds_titulo_complemento",
                    "dt_inicio",
                    "dt_fim"
                }
        )
)
public class Campeonato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_evento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AEvento evento;
    @JoinColumn(name = "id_modalidade", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CampeonatoModalidade modalidade;
    @JoinColumn(name = "id_evt", referencedColumnName = "id")
    @ManyToOne
    private Evt evt;
    @Column(name = "ds_titulo_complemento", length = 300, nullable = false)
    private String tituloComplemento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inicio", nullable = false)
    private Date dtInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_fim", nullable = false)
    private Date dtFim;

    public Campeonato() {
        this.id = null;
        this.evento = null;
        this.modalidade = null;
        this.evt = null;
        this.tituloComplemento = "";
        this.dtInicio = null;
        this.dtFim = null;
    }

    public Campeonato(Integer id, AEvento evento, CampeonatoModalidade modalidade, Evt evt, String tituloComplemento, Date dtInicio, Date dtFim) {
        this.id = id;
        this.evento = evento;
        this.modalidade = modalidade;
        this.evt = evt;
        this.tituloComplemento = tituloComplemento;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public CampeonatoModalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(CampeonatoModalidade modalidade) {
        this.modalidade = modalidade;
    }

    public Evt getEvt() {
        return evt;
    }

    public void setEvt(Evt evt) {
        this.evt = evt;
    }

    public String getTituloComplemento() {
        return tituloComplemento;
    }

    public void setTituloComplemento(String tituloComplemento) {
        this.tituloComplemento = tituloComplemento;
    }

    public Date getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    public Date getDtFim() {
        return dtFim;
    }

    public void setDtFim(Date dtFim) {
        this.dtFim = dtFim;
    }

    public String getInicio() {
        return DataHoje.converteData(dtInicio);
    }

    public void setInicio(String inicio) {
        this.dtInicio = DataHoje.converte(inicio);
    }

    public String getFim() {
        return DataHoje.converteData(dtFim);
    }

    public void setFim(String fim) {
        this.dtFim = DataHoje.converte(fim);
    }

}
