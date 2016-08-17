package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name = "fin_remessa_banco")
@NamedQuery(name = "RemessaBanco.pesquisaID", query = "select r from RemessaBanco r where r.id=:pid")
public class RemessaBanco implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_remessa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Remessa remessa;
    @JoinColumn(name = "id_movimento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Movimento movimento;

    public RemessaBanco() {
        this.id = -1;
        this.remessa = new Remessa();
        this.movimento = new Movimento();
    }

    public RemessaBanco(int id, Remessa remessa, Movimento movimento) {
        this.id = id;
        this.remessa = remessa;
        this.movimento = movimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Remessa getRemessa() {
        return remessa;
    }

    public void setRemessa(Remessa remessa) {
        this.remessa = remessa;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

}
