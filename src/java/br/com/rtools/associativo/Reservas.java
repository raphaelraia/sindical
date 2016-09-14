package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "car_reservas")
public class Reservas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_cvenda", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CVenda venda;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Pessoa pessoa;
    @Column(name = "nr_poltrona", nullable = true)
    private Integer poltrona;
    @Column(name = "nr_desconto", nullable = true)
    private Float desconto;
    @JoinColumn(name = "id_evento_servico", referencedColumnName = "id")
    @OneToOne
    private EventoServico eventoServico;

    public Reservas() {
        this.id = -1;
        this.venda = new CVenda();
        this.pessoa = new Pessoa();
        this.poltrona = 0;
        this.desconto = new Float(0);
        this.eventoServico = new EventoServico();
    }

    public Reservas(Integer id, CVenda venda, Pessoa pessoa, Integer poltrona, Float desconto, EventoServico eventoServico) {
        this.id = id;
        this.venda = venda;
        this.pessoa = pessoa;
        this.poltrona = poltrona;
        this.desconto = desconto;
        this.eventoServico = eventoServico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CVenda getVenda() {
        return venda;
    }

    public void setVenda(CVenda venda) {
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

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public EventoServico getEventoServico() {
        return eventoServico;
    }

    public void setEventoServico(EventoServico eventoServico) {
        this.eventoServico = eventoServico;
    }
}
