package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "car_reservas")
public class CaravanaReservas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_caravana_venda", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CaravanaVenda venda;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Pessoa pessoa;
    @Column(name = "nr_poltrona", nullable = true)
    private Integer poltrona;
    @Column(name = "nr_desconto", nullable = true)
    private Double desconto;
    @JoinColumn(name = "id_evento_servico", referencedColumnName = "id")
    @OneToOne
    private EventoServico eventoServico;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Usuario operador;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_reserva")
    private Date dtReserva;
    @JoinColumn(name = "id_operador_cancelamento", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Usuario operadorCancelamento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cancelamento")
    private Date dtCancelamento;
    @Column(name = "ds_motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    public CaravanaReservas() {
        this.id = null;
        this.venda = new CaravanaVenda();
        this.pessoa = new Pessoa();
        this.poltrona = 0;
        this.desconto = new Double(0);
        this.eventoServico = new EventoServico();
        this.operador = new Usuario();
        this.dtReserva = new Date();
        this.operadorCancelamento = null;
        this.dtCancelamento = null;
        this.motivoCancelamento = "";
    }

    public CaravanaReservas(Integer id, CaravanaVenda venda, Pessoa pessoa, Integer poltrona, Double desconto, EventoServico eventoServico, Usuario operador, Date dtReserva, Usuario operadorCancelamento, Date dtCancelamento, String motivoCancelamento) {
        this.id = id;
        this.venda = venda;
        this.pessoa = pessoa;
        this.poltrona = poltrona;
        this.desconto = desconto;
        this.eventoServico = eventoServico;
        this.operador = operador;
        this.dtReserva = dtReserva;
        this.operadorCancelamento = operadorCancelamento;
        this.dtCancelamento = dtCancelamento;
        this.motivoCancelamento = motivoCancelamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CaravanaVenda getVenda() {
        return venda;
    }

    public void setVenda(CaravanaVenda venda) {
        this.venda = venda;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Integer getPoltrona() {
        return poltrona;
    }

    public void setPoltrona(Integer poltrona) {
        this.poltrona = poltrona;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public EventoServico getEventoServico() {
        return eventoServico;
    }

    public void setEventoServico(EventoServico eventoServico) {
        this.eventoServico = eventoServico;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Date getDtReserva() {
        return dtReserva;
    }

    public void setDtReserva(Date dtReserva) {
        this.dtReserva = dtReserva;
    }

    public String getReserva() {
        if (dtReserva != null) {
            return DataHoje.livre(dtReserva, "dd/MM/yyyy") + " às " + DataHoje.livre(dtReserva, "HH:mm") + " hr(s)";
        }
        return "";
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
}
