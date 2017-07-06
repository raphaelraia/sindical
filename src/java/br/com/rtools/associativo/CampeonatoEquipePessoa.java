package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_campeonato_equipe_pessoa",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_campeonato",
                    "id_campeonato_equipe",
                    "id_pessoa"
                }
        )
)
public class CampeonatoEquipePessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_campeonato", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Campeonato campeonato;
    @JoinColumn(name = "id_campeonato_equipe", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CampeonatoEquipe campeonatoEquipe;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;

    public CampeonatoEquipePessoa() {
        this.id = null;
        this.campeonato = null;
        this.campeonatoEquipe = null;
        this.pessoa = null;
    }

    public CampeonatoEquipePessoa(Integer id, Campeonato campeonato, CampeonatoEquipe campeonatoEquipe, Pessoa pessoa) {
        this.id = id;
        this.campeonato = campeonato;
        this.campeonatoEquipe = campeonatoEquipe;
        this.pessoa = pessoa;
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

    public CampeonatoEquipe getCampeonatoEquipe() {
        return campeonatoEquipe;
    }

    public void setCampeonatoEquipe(CampeonatoEquipe campeonatoEquipe) {
        this.campeonatoEquipe = campeonatoEquipe;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

}
