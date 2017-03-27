package br.com.rtools.associativo;

import br.com.rtools.sistema.Mes;
import br.com.rtools.sistema.Periodo;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "soc_periodo_mensalidade",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_periodo", "id_mes"})
)
@NamedQueries({
    @NamedQuery(name = "PeriodoMensalidade.findAll", query = "SELECT PM FROM PeriodoMensalidade AS PM ORDER BY PM.periodo.id")
})
public class PeriodoMensalidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_periodo", referencedColumnName = "id", nullable = false, unique = true)
    @OneToOne
    private Periodo periodo;
    @JoinColumn(name = "id_mes", referencedColumnName = "id")
    @OneToOne
    private Mes mes;

    public PeriodoMensalidade() {
        this.id = null;
        this.periodo = null;
        this.mes = null;
    }

    public PeriodoMensalidade(Integer id, Periodo periodo, Mes mes) {
        this.id = id;
        this.periodo = periodo;
        this.mes = mes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

}
