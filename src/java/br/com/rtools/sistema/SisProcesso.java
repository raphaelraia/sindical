package br.com.rtools.sistema;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_processo")
public class SisProcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_tempo")
    private Long tempo;
    @Column(name = "nr_tempo_query")
    private Long tempoQuery;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data", nullable = true)
    private Date data;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "ds_processo", length = 255)
    private String processo;

    public SisProcesso() {
        this.id = null;
        this.tempo = new Long(0);
        this.tempoQuery = new Long(0);
        this.data = null;
        this.rotina = null;
        this.processo = null;
    }

    public SisProcesso(Integer id, Long tempo, Long tempoQuery, Date data, Rotina rotina, String processo) {
        this.id = id;
        this.tempo = tempo;
        this.tempoQuery = tempoQuery;
        this.data = data;
        this.rotina = rotina;
        this.processo = processo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public void start() {
        tempo = System.currentTimeMillis();
    }

    public void startQuery() {
        tempoQuery = System.currentTimeMillis();
    }

    public void finishQuery() {
        tempoQuery = System.currentTimeMillis() - tempoQuery;
    }

    public void finish() {
        if (this.processo != null && !this.processo.isEmpty()) {
            tempo = System.currentTimeMillis() - tempo;
            this.setRotina(new Rotina().get());
            this.setData(new Date());
            new Dao().save(this, true);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SisProcesso other = (SisProcesso) obj;
        return true;
    }

    @Override
    public String toString() {
        return "SisProcesso{" + "id=" + id + ", tempo=" + tempo + ", data=" + data + ", rotina=" + rotina + ", processo=" + processo + '}';
    }

    public Long getTempoQuery() {
        return tempoQuery;
    }

    public void setTempoQuery(Long tempoQuery) {
        this.tempoQuery = tempoQuery;
    }

}
