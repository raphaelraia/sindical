package br.com.rtools.associativo;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.seguranca.Departamento;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "soc_catraca_servico_depto",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_servico", "id_departamento"})
)
@NamedQueries({
    @NamedQuery(name = "CatracaServicoDepto.findAll", query = "SELECT CSD FROM CatracaServicoDepto AS CSD ORDER BY CSD.departamento.descricao ASC, CSD.servicos.descricao ASC ")
})
public class CatracaServicoDepto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id")
    @ManyToOne
    private Servicos servicos;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;

    public CatracaServicoDepto() {
        this.id = null;
        this.servicos = null;
        this.departamento = null;
    }

    public CatracaServicoDepto(Integer id, Servicos servicos, Departamento departamento) {
        this.id = id;
        this.servicos = servicos;
        this.departamento = departamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
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
        final CatracaServicoDepto other = (CatracaServicoDepto) obj;
        return true;
    }

    @Override
    public String toString() {
        return "CatracaServicoDepto{" + "id=" + id + ", servicos=" + servicos + ", departamento=" + departamento + '}';
    }

}
