package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_carta_tipo")
public class SisCartaTipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 255, unique = true)
    private String descricao;

    public SisCartaTipo() {
        this.id = null;
        this.descricao = "";
    }

    public SisCartaTipo(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
