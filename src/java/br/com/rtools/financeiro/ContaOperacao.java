package br.com.rtools.financeiro;

import br.com.rtools.pessoa.Filial;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "fin_conta_operacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_plano5", "id_operacao", "ds_es"})
)
@NamedQuery(name = "ContaOperacao.pesquisaID", query = "SELECT CO FROM ContaOperacao AS CO WHERE CO.id = :pid")
public class ContaOperacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "id_operacao", referencedColumnName = "id")
    @ManyToOne
    private Operacao operacao;
    @Column(name = "is_conta_fixa", columnDefinition = "boolean default false")
    private boolean contaFixa;
    @JoinColumn(name = "id_centro_custo", referencedColumnName = "id")
    @ManyToOne
    private CentroCusto centroCusto;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne
    private Filial filial;

    @Transient
    private Boolean selected;

    public ContaOperacao() {
        this.id = -1;
        this.plano5 = new Plano5();
        this.operacao = new Operacao();
        this.contaFixa = false;
        this.filial = null;
        this.selected = false;
        this.centroCusto = null;
    }

    public ContaOperacao(int id, Plano5 plano5, Operacao operacao, boolean contaFixa, CentroCusto centroCusto, Filial filial) {
        this.id = id;
        this.plano5 = plano5;
        this.operacao = operacao;
        this.contaFixa = contaFixa;
        this.centroCusto = centroCusto;
        this.filial = filial;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public boolean isContaFixa() {
        return contaFixa;
    }

    public void setContaFixa(boolean contaFixa) {
        this.contaFixa = contaFixa;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public CentroCusto getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(CentroCusto centroCusto) {
        this.centroCusto = centroCusto;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    @Override
    public String toString() {
        return "ContaOperacao{" + "id=" + id + ", plano5=" + plano5 + ", operacao=" + operacao + ", contaFixa=" + contaFixa + ", centroCusto=" + centroCusto + ", filial=" + filial + ", selected=" + selected + '}';
    }

}
