package br.com.rtools.seguranca;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "seg_permissao_catraca",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_pessoa", "id_departamento"})
)
@NamedQueries({
    @NamedQuery(name = "PermissaoCatraca.findAll", query = "SELECT PC FROM PermissaoCatraca AS PC ORDER BY PC.pessoa.nome")
})
public class PermissaoCatraca implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Departamento departamento;

    public PermissaoCatraca() {
        this.id = null;
        this.pessoa = null;
        this.departamento = null;
    }

    public PermissaoCatraca(int id, Pessoa pessoa, Departamento departamento) {
        this.id = id;
        this.pessoa = pessoa;
        this.departamento = departamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final PermissaoCatraca other = (PermissaoCatraca) obj;
        return true;
    }

    @Override
    public String toString() {
        return "PermissaoCatraca{" + "id=" + id + ", pessoa=" + pessoa + ", departamento=" + departamento + '}';
    }

}
