package br.com.rtools.pessoa;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

/**
 * <p>
 * <strong>Filial</strong></p>
 * <p>
 * <strong>Definição:</strong> Estabelecimento dependente de outro.</p>
 * <p>
 * <strong>Importante:</strong> Utilizar filiais somente caso houver um
 * endereçamento diferente da sede!</p>
 *
 * @author rtools
 */
@Entity
@Table(name = "pes_filial",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_matriz", "id_filial", "nr_centro_custo"})
)
@NamedQueries({
    @NamedQuery(name = "Filial.pesquisaID", query = "SELECT FIL FROM Filial AS FIL WHERE FIL.id = :pid")
    ,
    @NamedQuery(name = "Filial.findAll", query = "SELECT FIL FROM Filial AS FIL ORDER BY FIL.filial.pessoa.nome ASC ")
})
public class Filial implements Serializable, BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_matriz", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Juridica matriz;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Juridica filial;
    @Column(name = "nr_centro_custo")
    private Integer centroCusto;
    @Column(name = "nr_quantidade_agendamentos_por_empresa")
    private Integer quantidadeAgendamentosPorEmpresa;
    @Column(name = "ds_apelido", length = 30)
    private String apelido;

    public Filial() {
        this.id = -1;
        this.matriz = new Juridica();
        this.filial = new Juridica();
        this.centroCusto = 0;
        this.quantidadeAgendamentosPorEmpresa = 50;
        this.apelido = "";
    }

    public Filial(Integer id, Juridica matriz, Juridica filial, Integer centroCusto, Integer quantidadeAgendamentosPorEmpresa, String apelido) {
        this.id = id;
        this.matriz = matriz;
        this.filial = filial;
        this.centroCusto = centroCusto;
        this.quantidadeAgendamentosPorEmpresa = quantidadeAgendamentosPorEmpresa;
        this.apelido = apelido;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Juridica getMatriz() {
        return matriz;
    }

    public void setMatriz(Juridica matriz) {
        this.matriz = matriz;
    }

    public Juridica getFilial() {
        return filial;
    }

    public void setFilial(Juridica filial) {
        this.filial = filial;
    }

    public Integer getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(Integer centroCusto) {
        this.centroCusto = centroCusto;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.matriz);
        hash = 97 * hash + Objects.hashCode(this.filial);
        hash = 97 * hash + Objects.hashCode(this.centroCusto);
        hash = 97 * hash + Objects.hashCode(this.quantidadeAgendamentosPorEmpresa);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Filial other = (Filial) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.matriz, other.matriz)) {
            return false;
        }
        if (!Objects.equals(this.filial, other.filial)) {
            return false;
        }
        if (!Objects.equals(this.centroCusto, other.centroCusto)) {
            return false;
        }
        if (!Objects.equals(this.quantidadeAgendamentosPorEmpresa, other.quantidadeAgendamentosPorEmpresa)) {
            return false;
        }
        return true;
    }

    public Integer getQuantidadeAgendamentosPorEmpresa() {
        return quantidadeAgendamentosPorEmpresa;
    }

    public void setQuantidadeAgendamentosPorEmpresa(Integer quantidadeAgendamentosPorEmpresa) {
        this.quantidadeAgendamentosPorEmpresa = quantidadeAgendamentosPorEmpresa;
    }

    @Override
    public String toString() {
        return "Filial{" + "id=" + id + ", matriz=" + matriz + ", filial=" + filial + ", centroCusto=" + centroCusto + ", quantidadeAgendamentosPorEmpresa=" + quantidadeAgendamentosPorEmpresa + '}';
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

}
