package br.com.rtools.seguranca;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "seg_log_definicoes")
@NamedQueries({
    @NamedQuery(name = "LogDefinicoes.findAll", query = "SELECT LDF FROM LogDefinicoes AS LDF ORDER BY LDF.diasManter ASC")
})
public class LogDefinicoes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "nr_dias_manter", nullable = true)
    private Integer diasManter;

    @Transient
    public static Integer KEEP_DAYS_LOGS = 365;

    public LogDefinicoes() {
        this.id = null;
        this.rotina = null;
        this.diasManter = null;
    }

    public LogDefinicoes(Integer id, Rotina rotina, Integer diasManter) {
        this.id = id;
        this.rotina = rotina;
        this.diasManter = diasManter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Integer getDiasManter() {
        return diasManter;
    }

    public void setDiasManter(Integer diasManter) {
        this.diasManter = diasManter;
    }

}
