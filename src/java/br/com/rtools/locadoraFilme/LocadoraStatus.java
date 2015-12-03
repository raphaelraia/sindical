package br.com.rtools.locadoraFilme;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.sistema.Semana;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_filial", "id_semana", "id_taxa"})
)
public class LocadoraStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Filial filial;
    @JoinColumn(name = "id_semana", referencedColumnName = "id")
    @ManyToOne
    private Semana semana;
    @JoinColumn(name = "id_taxa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private LocadoraTaxa taxa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data")
    private Date date;
    @Column(name = "nr_qt_locacao", columnDefinition = "integer default 0")
    private Integer qtdeLocacao;
    @Column(name = "nr_qt_lancamentos", columnDefinition = "integer default 0")
    private Integer qtdeLancamentos;
    @Column(name = "nr_dias_devolucao", columnDefinition = "integer default 0")
    private Integer diasDevolucao;

    public LocadoraStatus() {
        this.id = null;
        this.filial = null;
        this.semana = null;
        this.taxa = null;
        this.date = null;
        this.qtdeLocacao = 0;
        this.qtdeLancamentos = 0;
        this.diasDevolucao = 0;
    }

    public LocadoraStatus(Integer id, Filial filial, Semana semana, LocadoraTaxa taxa, Date date, Integer qtdeLocacao, Integer qtdeLancamentos, Integer diasDevolucao) {
        this.id = id;
        this.filial = filial;
        this.semana = semana;
        this.taxa = taxa;
        this.date = date;
        this.qtdeLocacao = qtdeLocacao;
        this.qtdeLancamentos = qtdeLancamentos;
        this.diasDevolucao = diasDevolucao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Semana getSemana() {
        return semana;
    }

    public void setSemana(Semana semana) {
        this.semana = semana;
    }

    public LocadoraTaxa getTaxa() {
        return taxa;
    }

    public void setTaxa(LocadoraTaxa taxa) {
        this.taxa = taxa;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getQtdeLocacao() {
        return qtdeLocacao;
    }

    public void setQtdeLocacao(Integer qtdeLocacao) {
        this.qtdeLocacao = qtdeLocacao;
    }

    public Integer getQtdeLancamentos() {
        return qtdeLancamentos;
    }

    public void setQtdeLancamentos(Integer qtdeLancamentos) {
        this.qtdeLancamentos = qtdeLancamentos;
    }

    public Integer getDiasDevolucao() {
        return diasDevolucao;
    }

    public void setDiasDevolucao(Integer diasDevolucao) {
        this.diasDevolucao = diasDevolucao;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final LocadoraStatus other = (LocadoraStatus) obj;
        return true;
    }

    @Override
    public String toString() {
        return "LocadoraStatus{" + "id=" + id + ", filial=" + filial + ", semana=" + semana + ", taxa=" + taxa + ", date=" + date + ", qtdeLocacao=" + qtdeLocacao + ", qtdeLancamentos=" + qtdeLancamentos + ", diasDevolucao=" + diasDevolucao + '}';
    }

}
