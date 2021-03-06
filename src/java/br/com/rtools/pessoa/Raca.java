package br.com.rtools.pessoa;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "pes_raca")
@NamedQueries({
    @NamedQuery(name = "Raca.pesquisaID", query = "SELECT R FROM Raca AS R WHERE R.id = :pid"),
    @NamedQuery(name = "Raca.findAll", query = "SELECT R FROM Raca AS R ORDER BY R.descricao ASC "),
    @NamedQuery(name = "Raca.findName", query = "SELECT R FROM Raca AS R WHERE UPPER(R.descricao) LIKE :pdescricao ORDER BY R.descricao ASC ")
})
public class Raca implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;

    public Raca() {
        this.id = -1;
        this.descricao = "";
    }

    public Raca(Integer id, String descricao) {
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
        int hash = 5;
        hash = 97 * hash + this.id;
        hash = 97 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
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
        final Raca other = (Raca) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if ((this.descricao == null) ? (other.descricao != null) : !this.descricao.equals(other.descricao)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Raca{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
