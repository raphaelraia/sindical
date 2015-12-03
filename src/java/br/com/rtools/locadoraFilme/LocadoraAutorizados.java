package br.com.rtools.locadoraFilme;

import br.com.rtools.associativo.Parentesco;
import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_autorizados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_titular", "id_parentesco"})
)
public class LocadoraAutorizados implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_nome")
    private String nome;
    @Column(name = "ds_sexo")
    private String sexo;
    @JoinColumn(name = "id_titular", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa titular;
    @JoinColumn(name = "id_parentesco", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Parentesco parentesco;

    public LocadoraAutorizados() {
        this.id = null;
        this.nome = "";
        this.sexo = "";
        this.titular = null;
        this.parentesco = null;
    }

    public LocadoraAutorizados(Integer id, String nome, String sexo, Pessoa titular, Parentesco parentesco) {
        this.id = id;
        this.nome = nome;
        this.sexo = sexo;
        this.titular = titular;
        this.parentesco = parentesco;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Pessoa getTitular() {
        return titular;
    }

    public void setTitular(Pessoa titular) {
        this.titular = titular;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
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
        final LocadoraAutorizados other = (LocadoraAutorizados) obj;
        return true;
    }

    @Override
    public String toString() {
        return "LocadoraAutorizados{" + "id=" + id + ", nome=" + nome + ", sexo=" + sexo + ", titular=" + titular + ", parentesco=" + parentesco + '}';
    }

}
