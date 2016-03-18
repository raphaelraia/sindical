package br.com.rtools.seguranca;

import javax.persistence.*;

@Entity
@Table(name = "seg_rotina_grupo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_grupo", "id_rotina"})
)
public class RotinaGrupo implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_grupo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina grupo;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;

    public RotinaGrupo() {
        this.id = null;
        this.grupo = null;
        this.rotina = null;
    }

    public RotinaGrupo(Integer id, Rotina grupo, Rotina rotina) {
        this.id = id;
        this.grupo = grupo;
        this.rotina = rotina;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getGrupo() {
        return grupo;
    }

    public void setGrupo(Rotina grupo) {
        this.grupo = grupo;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

}
