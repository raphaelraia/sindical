package br.com.rtools.digitalizacao;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dig_assunto")
public class Assunto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_grupo", referencedColumnName = "id")
    @ManyToOne
    private GrupoDigitalizacao grupo;
    @Column(name = "ds_descricao", length = 1000)
    private String descricao;

    public Assunto() {
        this.id = -1;
        this.grupo = new GrupoDigitalizacao();
        this.descricao = "";
    }

    public Assunto(int id, GrupoDigitalizacao grupo, String descricao) {
        this.id = id;
        this.grupo = grupo;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GrupoDigitalizacao getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoDigitalizacao grupo) {
        this.grupo = grupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
