package br.com.rtools.associativo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_campeonato_equipe",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_campeonato",
                    "id_equipe"
                }
        )
)
@NamedQueries({
    @NamedQuery(name = "CampeonatoEquipe.findAll", query = "SELECT CE FROM CampeonatoEquipe AS CE ORDER BY CE.equipe.descricao ASC")
})
public class CampeonatoEquipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_campeonato", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Campeonato campeonato;
    @JoinColumn(name = "id_equipe", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Equipe equipe;

    public CampeonatoEquipe() {
        this.id = null;
        this.campeonato = null;
        this.equipe = null;
    }

    public CampeonatoEquipe(Integer id, Campeonato campeonato, Equipe equipe) {
        this.id = id;
        this.campeonato = campeonato;
        this.equipe = equipe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

}
