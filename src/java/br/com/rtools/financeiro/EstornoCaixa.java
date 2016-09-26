package br.com.rtools.financeiro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fin_estorno_caixa")
public class EstornoCaixa implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_estorno_caixa_lote", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private EstornoCaixaLote estornoCaixaLote;
    @JoinColumn(name = "id_movimento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Movimento movimento;
    @Column(name = "nr_valor_baixa", nullable = false)
    private Float nrValorBaixa;

    public EstornoCaixa() {
        this.id = -1;
        this.estornoCaixaLote = new EstornoCaixaLote();
        this.movimento = new Movimento();
        this.nrValorBaixa = (float) 0;
    }

    public EstornoCaixa(int id, EstornoCaixaLote estornoCaixaLote, Movimento movimento, Float nrValorBaixa) {
        this.id = id;
        this.estornoCaixaLote = estornoCaixaLote;
        this.movimento = movimento;
        this.nrValorBaixa = nrValorBaixa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EstornoCaixaLote getEstornoCaixaLote() {
        return estornoCaixaLote;
    }

    public void setEstornoCaixaLote(EstornoCaixaLote estornoCaixaLote) {
        this.estornoCaixaLote = estornoCaixaLote;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public Float getNrValorBaixa() {
        return nrValorBaixa;
    }

    public void setNrValorBaixa(Float nrValorBaixa) {
        this.nrValorBaixa = nrValorBaixa;
    }

}
