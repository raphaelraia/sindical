package br.com.rtools.escola;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

/**
 * <p>
 * <b>Professor</b></p>
 * <p>
 * Cadastro único de professores. </p>
 * <p>
 * O valor percentual da comissão será usado para gerar um bônus ao professor
 * conforme o valor final do serviço após ser gravado na tabela movimento
 * financeiro.</p>
 *
 * @author rtools
 */
@Entity
@Table(name = "esc_professor")
@NamedQueries({
    @NamedQuery(name = "Professor.pesquisaID", query = "SELECT P FROM Professor AS P WHERE P.id = :pid"),
    @NamedQuery(name = "Professor.findAll", query = "SELECT P FROM Professor AS P ORDER BY P.professor.nome ASC"),
    @NamedQuery(name = "Professor.findName", query = "SELECT P FROM Professor AS P WHERE UPPER(P.professor.nome) LIKE :pdescricao ORDER BY P.professor.nome ASC ")
})
public class Professor implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_professor", referencedColumnName = "id", unique = true)
    @ManyToOne
    private Pessoa professor;
    @Column(name = "nr_comissao")
    private double nrComissao;

    public Professor() {
        this.id = -1;
        this.professor = new Pessoa();
        this.nrComissao = 0;
    }

    public Professor(Integer id, Pessoa professor, double nrComissao) {
        this.id = id;
        this.professor = professor;
        this.nrComissao = nrComissao;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getProfessor() {
        return professor;
    }

    public void setProfessor(Pessoa professor) {
        this.professor = professor;
    }

    public double getNrComissao() {
        return nrComissao;
    }

    public void setNrComissao(double nrComissao) {
        this.nrComissao = nrComissao;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.professor);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.nrComissao) ^ (Double.doubleToLongBits(this.nrComissao) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Professor other = (Professor) obj;
        if (Double.doubleToLongBits(this.nrComissao) != Double.doubleToLongBits(other.nrComissao)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.professor, other.professor)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "Professor{" + "id=" + id + ", professor=" + professor + ", nrComissao=" + nrComissao + '}';
    }

}
