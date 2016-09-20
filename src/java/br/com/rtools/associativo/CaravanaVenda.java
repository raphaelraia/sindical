package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.CaravanaReservasDao;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
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
public class CaravanaVenda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Usuario operador;
    @JoinColumn(name = "id_responsavel", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Pessoa responsavel;
    @JoinColumn(name = "id_evento", referencedColumnName = "id")
    @ManyToOne
    private AEvento evento;
    @Column(name = "nr_quarto")
    private Integer quarto;
    @Column(name = "ds_observacao")
    private String observacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @JoinColumn(name = "id_operador_cancelamento", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Usuario operadorCancelamento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cancelamento")
    private Date dtCancelamento;
    @Column(name = "ds_motivo_cancelamento", length = 255)
    private String motivoCancelamento;
    @JoinColumn(name = "id_caravana", referencedColumnName = "id")
    @ManyToOne
    private Caravana caravana;
    @JoinColumn(name = "id_lote", referencedColumnName = "id")
    @OneToOne
    private Lote lote;

    @Transient
    private List<CaravanaReservas> listReservas;

    public CaravanaVenda() {
        this.id = null;
        this.operador = new Usuario();
        this.responsavel = new Pessoa();
        this.evento = new AEvento();
        this.quarto = 0;
        this.observacao = "";
        this.dtEmissao = DataHoje.dataHoje();
        this.caravana = null;
        this.listReservas = null;
        this.operadorCancelamento = null;
        this.dtCancelamento = null;
        this.motivoCancelamento = "";
        this.lote = null;
    }

    public CaravanaVenda(Integer id, Usuario operador, Pessoa responsavel, AEvento evento, Integer quarto, String observacao, Date dtEmissao, Usuario operadorCancelamento, Date dtCancelamento, String motivoCancelamento, Caravana caravana, Lote lote) {
        this.id = id;
        this.operador = operador;
        this.responsavel = responsavel;
        this.evento = evento;
        this.quarto = quarto;
        this.observacao = observacao;
        this.dtEmissao = dtEmissao;
        this.listReservas = null;
        this.operadorCancelamento = operadorCancelamento;
        this.dtCancelamento = dtCancelamento;
        this.motivoCancelamento = motivoCancelamento;
        this.caravana = caravana;
        this.lote = lote;
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

    public String getEmissao() {
        if (dtEmissao != null) {
            return DataHoje.livre(dtEmissao, "dd/MM/yyyy") + " às " + DataHoje.livre(dtEmissao, "HH:mm") + " hr(s)";
        }
        return "";
    }

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public List<CaravanaReservas> getListReservas() {
        if (this.id != -1 || this.id != null) {
            if (listReservas == null) {
                listReservas = new ArrayList();
                listReservas = new CaravanaReservasDao().findByCaravanaVenda(this.id);
            }
        }
        return listReservas;
    }

    public void setListReservas(List<CaravanaReservas> listReservas) {
        this.listReservas = listReservas;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Usuario getOperadorCancelamento() {
        return operadorCancelamento;
    }

    public void setOperadorCancelamento(Usuario operadorCancelamento) {
        this.operadorCancelamento = operadorCancelamento;
    }

    public Date getDtCancelamento() {
        return dtCancelamento;
    }

    public void setDtCancelamento(Date dtCancelamento) {
        this.dtCancelamento = dtCancelamento;
    }

    public String getCancelamento() {
        if (dtCancelamento != null) {
            return DataHoje.livre(dtCancelamento, "dd/MM/yyyy") + " às " + DataHoje.livre(dtCancelamento, "HH:mm") + " hr(s)";
        }
        return "";
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public Caravana getCaravana() {
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }
}
