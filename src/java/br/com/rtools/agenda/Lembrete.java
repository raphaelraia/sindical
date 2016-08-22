package br.com.rtools.agenda;

import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "age_lembrete")
@NamedQueries({
    @NamedQuery(name = "Lembrete.pesquisaID", query = "SELECT L FROM Lembrete L WHERE L.id = :pid"),
    @NamedQuery(name = "Lembrete.findAll", query = "SELECT L FROM Lembrete L ORDER BY L.periodo.descricao ASC ")
})
public class Lembrete implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_compromisso", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Compromisso compromisso;
    @JoinColumn(name = "id_periodo", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Periodo periodo;
    @Column(name = "nr_tempo", nullable = false)
    private Integer tempo;

    public Lembrete() {
        this.id = null;
        this.periodo = null;
        this.compromisso = null;
        this.tempo = null;
    }

    public Lembrete(Integer id, Compromisso compromisso, Periodo periodo, Integer tempo) {
        this.id = id;
        this.periodo = periodo;
        this.compromisso = compromisso;
        this.tempo = tempo;
    }

    @Override
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

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public Compromisso getCompromisso() {
        return compromisso;
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

}
