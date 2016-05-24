package br.com.rtools.atendimento;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "ate_status")
@NamedQuery(name = "AteStatus.findAll", query = "SELECT ATS FROM AteStatus ATS ORDER BY ATS.descricao ASC")
public class AteStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;
    @Column(name = "is_reserva", columnDefinition = "boolean default false")
    private Boolean reserva;

    public AteStatus() {
        this.id = -1;
        this.descricao = "";
        this.reserva = false;
    }

    public AteStatus(int id, String descricao, Boolean reserva) {
        this.id = id;
        this.descricao = descricao;
        this.reserva = reserva;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getReserva() {
        return reserva;
    }

    public void setReserva(Boolean reserva) {
        this.reserva = reserva;
    }

    @Override
    public String toString() {
        return "AteStatus{" + "id=" + id + ", descricao=" + descricao + ", reserva=" + reserva + '}';
    }

}
