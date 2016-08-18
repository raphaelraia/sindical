package br.com.rtools.agenda;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "age_compromisso_categoria")
@NamedQueries({
    @NamedQuery(name = "CompromissoCategoria.pesquisaID", query = "SELECT CC FROM CompromissoCategoria CC WHERE CC.id = :pid"),
    @NamedQuery(name = "CompromissoCategoria.findAll", query = "SELECT CC FROM CompromissoCategoria CC ORDER BY CC.descricao ASC "),
    @NamedQuery(name = "CompromissoCategoria.findName", query = "SELECT CC FROM CompromissoCategoria CC WHERE UPPER(CC.descricao) LIKE :pdescricao ORDER BY CC.descricao ASC ")
})
public class CompromissoCategoria implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 100, nullable = false, unique = true)
    private String descricao;

    public CompromissoCategoria() {
        this.id = -1;
        this.descricao = "";
    }

    public CompromissoCategoria(Integer id, String descricao) {
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
        int hash = 7;
        hash = 23 * hash + this.id;
        hash = 23 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
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
        final CompromissoCategoria other = (CompromissoCategoria) obj;
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
        return "CategoriaCompromisso{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
