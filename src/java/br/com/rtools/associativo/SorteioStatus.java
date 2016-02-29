package br.com.rtools.associativo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sort_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_sorteio", "nr_carencia_debito"})
)
public class SorteioStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_sorteio", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Sorteio sorteio;
    @Column(name = "nr_carencia_debito", nullable = false, length = 250)
    private Integer carenciaDebito;

    public SorteioStatus() {
        this.id = null;
        this.sorteio = null;
        this.carenciaDebito = 0;
    }

    public SorteioStatus(Integer id, Sorteio sorteio, Integer carenciaDebito) {
        this.id = id;
        this.sorteio = sorteio;
        this.carenciaDebito = carenciaDebito;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sorteio getSorteio() {
        return sorteio;
    }

    public void setSorteio(Sorteio sorteio) {
        this.sorteio = sorteio;
    }

    public Integer getCarenciaDebito() {
        return carenciaDebito;
    }

    public void setCarenciaDebito(Integer carenciaDebito) {
        this.carenciaDebito = carenciaDebito;
    }

    @Override
    public String toString() {
        return "SorteioStatus{" + "id=" + id + ", sorteio=" + sorteio + ", carenciaDebito=" + carenciaDebito + '}';
    }

}
