package br.com.rtools.locadoraFilme;

import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
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
@Table(name = "loc_movimento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_lote", "id_titulo"})
)
public class LocadoraMovimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_lote", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private LocadoraLote locadoraLote;
    @JoinColumn(name = "id_titulo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Titulo titulo;
    @JoinColumn(name = "id_operador_devolucao", referencedColumnName = "id")
    @ManyToOne
    private Usuario operadorDevolucao;
    @JoinColumn(name = "id_movimento", referencedColumnName = "id")
    @ManyToOne
    private Movimento movimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_devolucao_previsao", nullable = false)
    private Date dtDevolucaoPrevisao;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_devolucao")
    private Date dtDevolucao;

    @Transient
    private Boolean selected;

    @Transient
    private Float valorMultaDiaria;

    @Transient
    private Float valorTotal;

    public LocadoraMovimento() {
        this.id = null;
        this.locadoraLote = null;
        this.titulo = null;
        this.operadorDevolucao = null;
        this.movimento = null;
        this.dtDevolucaoPrevisao = null;
        this.dtDevolucao = null;
        this.selected = false;
    }

    public LocadoraMovimento(Integer id, LocadoraLote locadoraLote, Titulo titulo, Usuario operadorDevolucao, Movimento movimento, Date dtDevolucaoPrevisao, Date dtDevolucao) {
        this.id = id;
        this.locadoraLote = locadoraLote;
        this.titulo = titulo;
        this.operadorDevolucao = operadorDevolucao;
        this.movimento = movimento;
        this.dtDevolucaoPrevisao = dtDevolucaoPrevisao;
        this.dtDevolucao = dtDevolucao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocadoraLote getLocadoraLote() {
        return locadoraLote;
    }

    public void setLocadoraLote(LocadoraLote locadoraLote) {
        this.locadoraLote = locadoraLote;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public Usuario getOperadorDevolucao() {
        return operadorDevolucao;
    }

    public void setOperadorDevolucao(Usuario operadorDevolucao) {
        this.operadorDevolucao = operadorDevolucao;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public Date getDtDevolucaoPrevisao() {
        return dtDevolucaoPrevisao;
    }

    public void setDtDevolucaoPrevisao(Date dtDevolucaoPrevisao) {
        this.dtDevolucaoPrevisao = dtDevolucaoPrevisao;
    }

    public Date getDtDevolucao() {
        return dtDevolucao;
    }

    public void setDtDevolucao(Date dtDevolucao) {
        this.dtDevolucao = dtDevolucao;
    }

    public String getDataDevolucaoPrevisaoString() {
        return DataHoje.converteData(dtDevolucaoPrevisao);
    }

    public void setDataDevolucaoPrevisaoString(String dataDevolucaoPrevisaoString) {
        this.dtDevolucaoPrevisao = DataHoje.converte(dataDevolucaoPrevisaoString);
    }

    public String getDataDevolucaoString() {
        return DataHoje.converteData(dtDevolucao);
    }

    public void setDataDevolucaoString(String dataDevolucaoString) {
        this.dtDevolucao = DataHoje.converte(dataDevolucaoString);
    }

    public String getHoraDevolucaoString() {
        return DataHoje.converteHora(dtDevolucao);
    }

    public void setHoraDevolucaoString(String horaDevolucaoString) {
        this.dtDevolucao = DataHoje.converteDataHora(DataHoje.converteData(dtDevolucao), horaDevolucaoString);
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer getDiasAtraso() {
        if (dtDevolucao == null) {
            Integer dias = DataHoje.calculoDosDiasInt(dtDevolucaoPrevisao, new Date());
            return dias;
        }
        return 0;
    }

    public Float getValorMultaDiaria() {
        if (valorMultaDiaria == null) {
            this.valorMultaDiaria = new FunctionsDao().multaDiariaLocadora(this.locadoraLote.getFilial().getId(), this.locadoraLote.getDtLocacao());
        }
        return valorMultaDiaria;
    }

    public void setValorMultaDiaria(Float valorMultaDiaria) {
        this.valorMultaDiaria = valorMultaDiaria;
    }

    public String getValorMultaDiariaString() {
        return Moeda.converteR$Float(getValorMultaDiaria());
    }

    public void setValorMultaDiariaString(String valorMultaDiariaString) {
        this.valorMultaDiaria = Moeda.converteUS$(valorMultaDiariaString);
    }

    public Float getValorTotal() {
        if (valorTotal == null) {
            if(movimento == null) {
                valorTotal = getDiasAtraso() * valorMultaDiaria;                
            } else {
                valorTotal = movimento.getValor();
            }
        }
        return valorTotal;
    }

    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalString() {
        return Moeda.converteR$Float(getValorTotal());
    }

    public void setValorTotalString(String valorTotalString) {
        this.valorTotal = Moeda.converteUS$(valorTotalString);
    }

    public Double getValorTotalDouble() {
        return Double.parseDouble(Moeda.converteR$Float(getValorTotal()));
    }

    @Override
    public String toString() {
        return "LocadoraMovimento{" + "id=" + id + ", locadoraLote=" + locadoraLote + ", titulo=" + titulo + ", operadorDevolucao=" + operadorDevolucao + ", movimento=" + movimento + ", dtDevolucaoPrevisao=" + dtDevolucaoPrevisao + ", dtDevolucao=" + dtDevolucao + '}';
    }

}
