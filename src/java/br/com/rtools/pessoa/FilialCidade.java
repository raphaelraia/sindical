package br.com.rtools.pessoa;

import br.com.rtools.endereco.Cidade;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_filial_cidade",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_cidade", "id_filial"})
)
@NamedQuery(name = "FilialCidade.pesquisaID", query = "select fd from FilialCidade fd where fd.id=:pid")
public class FilialCidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_cidade", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Cidade cidade;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Filial filial;
    @Column(name = "is_principal", nullable = false, columnDefinition = "boolean default false")
    private Boolean principal;

    public FilialCidade(int id, Cidade cidade, Filial filial, Boolean principal) {
        this.id = id;
        this.cidade = cidade;
        this.filial = filial;
        this.principal = principal;
    }

    public FilialCidade() {
        this.id = -1;
        this.cidade = new Cidade();
        this.filial = new Filial();
        this.principal = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }
}
