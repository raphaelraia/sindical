package br.com.rtools.locadoraFilme;

import br.com.rtools.financeiro.Servicos;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_taxa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_servico_diaria", "id_servico_multa_diaria"})
)
@NamedQueries({
    @NamedQuery(name = "LocadoraTaxa.findAll", query = "SELECT LT FROM LocadoraTaxa AS LT ORDER BY LT.servicoDiaria.descricao ASC, LT.servicoMultaDiaria.descricao ASC")
})
public class LocadoraTaxa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico_diaria", referencedColumnName = "id")
    @ManyToOne
    private Servicos servicoDiaria;
    @JoinColumn(name = "id_servico_multa_diaria", referencedColumnName = "id")
    @ManyToOne
    private Servicos servicoMultaDiaria;

    public LocadoraTaxa() {
        this.id = null;
        this.servicoDiaria = null;
        this.servicoMultaDiaria = null;
    }

    public LocadoraTaxa(Integer id, Servicos servicoDiaria, Servicos servicoMultaDiaria) {
        this.id = id;
        this.servicoDiaria = servicoDiaria;
        this.servicoMultaDiaria = servicoMultaDiaria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServicoDiaria() {
        return servicoDiaria;
    }

    public void setServicoDiaria(Servicos servicoDiaria) {
        this.servicoDiaria = servicoDiaria;
    }

    public Servicos getServicoMultaDiaria() {
        return servicoMultaDiaria;
    }

    public void setServicoMultaDiaria(Servicos servicoMultaDiaria) {
        this.servicoMultaDiaria = servicoMultaDiaria;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final LocadoraTaxa other = (LocadoraTaxa) obj;
        return true;
    }

    @Override
    public String toString() {
        return "LocadoraTaxa{" + "id=" + id + ", servicoDiaria=" + servicoDiaria + ", servicoMultaDiaria=" + servicoMultaDiaria + '}';
    }

}
