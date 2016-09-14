package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.CaravanaDao;
import br.com.rtools.associativo.dao.ReservasDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "car_venda")
public class CVenda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_responsavel", referencedColumnName = "id")
    @OneToOne
    private Pessoa responsavel;
    @JoinColumn(name = "id_aevento", referencedColumnName = "id")
    @ManyToOne
    private AEvento evento;
    @Column(name = "nr_quarto")
    private Integer quarto;
    @Column(name = "ds_observacao")
    private String observacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;

    @Transient
    private Caravana caravana;

    @Transient
    private List<Reservas> listReservas;

    public CVenda(Integer id, Pessoa responsavel, AEvento evento, Integer quarto, String observacao, Date dtEmissao) {
        this.id = id;
        this.responsavel = responsavel;
        this.evento = evento;
        this.quarto = quarto;
        this.observacao = observacao;
        this.dtEmissao = dtEmissao;
        this.caravana = null;
        this.listReservas = null;
    }

    public CVenda() {
        this.id = -1;
        this.responsavel = new Pessoa();
        this.evento = new AEvento();
        this.quarto = 0;
        this.observacao = "";
        this.dtEmissao = DataHoje.dataHoje();
        this.caravana = null;
        this.listReservas = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public Integer getQuarto() {
        return quarto;
    }

    public void setQuarto(Integer quarto) {
        this.quarto = quarto;
    }

    public String getQuartoString() {
        return Integer.toString(quarto);
    }

    public void setQuartoString(String quartoString) {
        this.quarto = Integer.parseInt(quartoString);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getDataEmissaoString() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setDtEmissao(String dataEmissao) {
        this.dtEmissao = DataHoje.converte(dataEmissao);
    }

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public Caravana getCaravana() {
        if (this.id != -1 || this.id != null) {
            if (caravana == null) {
                caravana = new CaravanaDao().pesquisaCaravanaPorEvento(this.evento.getId());
            }
        }
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public List<Reservas> getListReservas() {
        if (this.id != -1 || this.id != null) {
            if (listReservas == null) {
                listReservas = new ArrayList();
                listReservas = new ReservasDao().findByCVenda(this.id);
            }
        }
        return listReservas;
    }

    public void setListReservas(List<Reservas> listReservas) {
        this.listReservas = listReservas;
    }
}
