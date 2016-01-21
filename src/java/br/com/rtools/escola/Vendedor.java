package br.com.rtools.escola;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "esc_vendedor")
@NamedQueries({
    @NamedQuery(name = "Vendedor.pesquisaID", query = "SELECT V FROM Vendedor AS V WHERE V.id = :pid"),
    @NamedQuery(name = "Vendedor.findAll", query = "SELECT V FROM Vendedor AS V ORDER BY V.pessoa.nome ASC "),
    @NamedQuery(name = "Vendedor.findName", query = "SELECT V FROM Vendedor AS V WHERE UPPER(V.pessoa.nome) LIKE :pdescricao ORDER BY V.pessoa.nome ASC ")
})
public class Vendedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", unique = true)
    @ManyToOne
    private Pessoa pessoa;

    @Transient
    private Boolean selected;

    public Vendedor() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.selected = false;
    }

    public Vendedor(int id, Pessoa pessoa) {
        this.id = id;
        this.pessoa = pessoa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
