package br.com.rtools.associativo;

import br.com.rtools.financeiro.ServicoPessoa;
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
@Table(name = "eve_campeonato_dependente",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_matricula_campeonato",
                    "id_servico_pessoa",
                    "id_parentesco"
                }
        )
)
@NamedQueries({
    @NamedQuery(name = "CampeonatoDependente.findAll", query = "SELECT CD FROM CampeonatoDependente AS CD ORDER BY CD.servicoPessoa.pessoa.nome ASC")
})
public class CampeonatoDependente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_matricula_campeonato", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private MatriculaCampeonato matriculaCampeonato;
    @JoinColumn(name = "id_servico_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ServicoPessoa servicoPessoa;
    @JoinColumn(name = "id_parentesco", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Parentesco parentesco;

    public CampeonatoDependente() {
        this.id = null;
        this.matriculaCampeonato = null;
        this.servicoPessoa = null;
        this.parentesco = null;
    }

    public CampeonatoDependente(Integer id, MatriculaCampeonato matriculaCampeonato, ServicoPessoa servicoPessoa, Parentesco parentesco) {
        this.id = id;
        this.matriculaCampeonato = matriculaCampeonato;
        this.servicoPessoa = servicoPessoa;
        this.parentesco = parentesco;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MatriculaCampeonato getMatriculaCampeonato() {
        return matriculaCampeonato;
    }

    public void setMatriculaCampeonato(MatriculaCampeonato matriculaCampeonato) {
        this.matriculaCampeonato = matriculaCampeonato;
    }

    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

}
