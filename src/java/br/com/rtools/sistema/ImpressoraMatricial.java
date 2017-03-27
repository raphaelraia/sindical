package br.com.rtools.sistema;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_impressora_matricial")
public class ImpressoraMatricial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_dispositivo", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Dispositivo dispositivo;
    @Column(name = "dt_impressao", length = 20, unique = true)
    @Temporal(TemporalType.DATE)
    private Date data;

    public ImpressoraMatricial() {
        this.id = null;
        this.dispositivo = null;
        this.data = null;
    }

    public ImpressoraMatricial(Integer id, Dispositivo dispositivo, Date data) {
        this.id = id;
        this.dispositivo = dispositivo;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Date getDate() {
        return data;
    }

    public void setDate(Date data) {
        this.data = data;
    }

}
