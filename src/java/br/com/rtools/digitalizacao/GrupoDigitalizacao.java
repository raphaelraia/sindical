package br.com.rtools.digitalizacao;

import br.com.rtools.seguranca.Modulo;
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
@Table(name = "dig_grupo")
public class GrupoDigitalizacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_modulo", referencedColumnName = "id")
    @ManyToOne
    private Modulo modulo;
    @Column(name = "ds_descricao", length = 1000)
    private String descricao;

    public GrupoDigitalizacao() {
        this.id = -1;
        this.modulo = new Modulo();
        this.descricao = "";
    }
    
    public GrupoDigitalizacao(int id, Modulo modulo, String descricao) {
        this.id = id;
        this.modulo = modulo;
        this.descricao = descricao;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
