package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name = "fin_baixa_log", 
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_baixa", "id_movimento"})
)
public class BaixaLog implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_baixa", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Baixa baixa;
    @JoinColumn(name = "id_movimento", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Movimento movimento;

    public BaixaLog() {
        this.id = null;
        this.baixa = null;
        this.movimento = null;
    }

    public BaixaLog(Integer id, Baixa baixa, Movimento movimento) {
        this.id = id;
        this.baixa = baixa;
        this.movimento = movimento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Baixa getBaixa() {
        return baixa;
    }

    public void setBaixa(Baixa baixa) {
        this.baixa = baixa;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

}
