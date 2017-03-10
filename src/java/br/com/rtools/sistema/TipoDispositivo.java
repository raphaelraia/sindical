package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_tipo_dispositivo")
@NamedQueries({
    @NamedQuery(name = "TipoDispositivo.findAll", query = "SELECT TD FROM TipoDispositivo AS TD ORDER BY TD.id ASC ")
})
public class TipoDispositivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", unique = true)
    private String descricao;

    public TipoDispositivo() {
        this.id = -1;
        this.descricao = "";
    }

    public TipoDispositivo(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
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

}
