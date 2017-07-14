package br.com.rtools.pessoa;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_status_cobranca")
@NamedQueries({
    @NamedQuery(name = "StatusCobranca.pesquisaID", query = "SELECT O FROM StatusCobranca AS O WHERE O.id = :pid"),
    @NamedQuery(name = "StatusCobranca.findAll", query = "SELECT O FROM StatusCobranca AS O ORDER BY O.id ASC "),
    @NamedQuery(name = "StatusCobranca.findName", query = "SELECT O FROM StatusCobranca AS O WHERE UPPER(O.descricao) LIKE :pdescricao ORDER BY O.descricao ASC ")
})
public class StatusCobranca implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;

    public StatusCobranca() {
        this.id = -1;
        this.descricao = "";
    }

    public StatusCobranca(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    @Override
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id;
        hash = 17 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StatusCobranca other = (StatusCobranca) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.descricao == null) ? (other.descricao != null) : !this.descricao.equals(other.descricao)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StatusCobranca{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
