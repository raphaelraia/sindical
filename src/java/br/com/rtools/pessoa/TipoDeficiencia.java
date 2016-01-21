package br.com.rtools.pessoa;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Entity
@Table(name = "pes_tipo_deficiencia")
@NamedQueries({
    @NamedQuery(name = "TipoDeficiencia.pesquisaID", query = "SELECT TD FROM TipoDeficiencia AS TD WHERE TD.id = :pid"),
    @NamedQuery(name = "TipoDeficiencia.findAll", query = "SELECT TD FROM TipoDeficiencia AS TD ORDER BY TD.descricao ASC "),
    @NamedQuery(name = "TipoDeficiencia.findName", query = "SELECT TD FROM TipoDeficiencia AS TD WHERE UPPER(TD.descricao) LIKE :pdescricao ORDER BY TD.descricao ASC ")
})
public class TipoDeficiencia implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;

    public TipoDeficiencia() {
        this.id = -1;
        this.descricao = "";
    }

    public TipoDeficiencia(Integer id, String descricao) {
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
        hash = 29 * hash + this.id;
        hash = 29 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
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
        final TipoDeficiencia other = (TipoDeficiencia) obj;
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
        return "TipoDeficiencia{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
