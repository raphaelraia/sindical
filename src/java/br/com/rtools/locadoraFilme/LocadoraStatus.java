package br.com.rtools.locadoraFilme;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.dao.FunctionsDao;
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
import javax.persistence.Transient;
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
    private Date data;
    @Column(name = "nr_qt_locacao", columnDefinition = "integer default 0")
    private Integer qtdeLocacao;
    @Column(name = "nr_qt_lancamentos", columnDefinition = "integer default 0")
    private Integer qtdeLancamentos;
    @Column(name = "nr_dias_devolucao", columnDefinition = "integer default 0")
    private Integer diasDevolucao;
    @Column(name = "is_locacao_dependente", columnDefinition = "boolean default false", nullable = false)
    private Boolean locacaoDependente;

    @Transient
    private Boolean selected;

    @Transient
    private Double valorMultaDiaria;

    public LocadoraStatus() {
        this.id = null;
        this.filial = null;
        this.semana = null;
        this.taxa = null;
        this.data = null;
        this.qtdeLocacao = 0;
        this.qtdeLancamentos = 0;
        this.diasDevolucao = 0;
        this.selected = false;
        this.locacaoDependente = false;
        this.valorMultaDiaria = new Double(0);
        try {
            valorMultaDiaria = new FunctionsDao().multaDiariaLocadora(filial.getId(), data);
        } catch (Exception e) {

        }
    }

    public LocadoraStatus(Integer id, Filial filial, Semana semana, LocadoraTaxa taxa, Date data, Integer qtdeLocacao, Integer qtdeLancamentos, Integer diasDevolucao, Boolean locacaoDependente) {
        this.id = id;
        this.filial = filial;
        this.semana = semana;
        this.taxa = taxa;
        this.data = data;
        this.qtdeLocacao = qtdeLocacao;
        this.qtdeLancamentos = qtdeLancamentos;
        this.diasDevolucao = diasDevolucao;
        this.locacaoDependente = locacaoDependente;
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

    public Date getData() {
        return data;
    }

    public void setData(Date date) {
        this.data = date;
    }

    public String getDataString() {
        return DataHoje.converteData(data);
    }

    public void setDataString(String dataString) {
        this.data = DataHoje.converte(dataString);
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

    public String getQtdeLocacaoString() {
        return Integer.toString(qtdeLocacao);
    }

    public void setQtdeLocacaoString(String qtdeLocacaoString) {
        try {
            this.qtdeLocacao = Integer.parseInt(qtdeLocacaoString);
        } catch (Exception e) {
            this.qtdeLocacao = 0;
        }
    }

    public String getQtdeLancamentosString() {
        return Integer.toString(qtdeLancamentos);
    }

    public void setQtdeLancamentosString(String qtdeLancamentosString) {
        try {
            this.qtdeLancamentos = Integer.parseInt(qtdeLancamentosString);
        } catch (Exception e) {
            this.qtdeLancamentos = 0;
        }
    }

    public String getDiasDevolucaoString() {
        return Integer.toString(diasDevolucao);
    }

    public void setDiasDevolucaoString(String diasDevolucaoString) {
        try {
            this.diasDevolucao = Integer.parseInt(diasDevolucaoString);
        } catch (Exception e) {
            this.diasDevolucao = 0;
        }
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getLocacaoDependente() {
        return locacaoDependente;
    }

    public void setLocacaoDependente(Boolean locacaoDependente) {
        this.locacaoDependente = locacaoDependente;
    }

    public Double getValorMultaDiaria() {
        return valorMultaDiaria;
    }

    public void setValorMultaDiaria(Double valorMultaDiaria) {
        this.valorMultaDiaria = valorMultaDiaria;
    }

    @Override
    public String toString() {
        return "LocadoraStatus{" + "id=" + id + ", filial=" + filial + ", semana=" + semana + ", taxa=" + taxa + ", data=" + data + ", qtdeLocacao=" + qtdeLocacao + ", qtdeLancamentos=" + qtdeLancamentos + ", diasDevolucao=" + diasDevolucao + '}';
    }
}
