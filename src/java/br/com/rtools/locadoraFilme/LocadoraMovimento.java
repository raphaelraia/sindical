package br.com.rtools.locadoraFilme;

import br.com.rtools.financeiro.Evt;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
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
    @JoinColumn(name = "id_evt", referencedColumnName = "id")
    @ManyToOne
    private Evt evt;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_devolucao_previsao", nullable = false)
    private Date dtDevolucaoPrevisao;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_devolucao")
    private Date dtDevolucao;

    @Transient
    private Boolean selected;

    public LocadoraMovimento() {
        this.id = null;
        this.locadoraLote = null;
        this.titulo = null;
        this.operadorDevolucao = null;
        this.evt = null;
        this.dtDevolucaoPrevisao = null;
        this.dtDevolucao = null;
        this.selected = false;
    }

    public LocadoraMovimento(Integer id, LocadoraLote locadoraLote, Titulo titulo, Usuario operadorDevolucao, Evt evt, Date dtDevolucaoPrevisao, Date dtDevolucao) {
        this.id = id;
        this.locadoraLote = locadoraLote;
        this.titulo = titulo;
        this.operadorDevolucao = operadorDevolucao;
        this.evt = evt;
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

    public Evt getEvt() {
        return evt;
    }

    public void setEvt(Evt evt) {
        this.evt = evt;
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
        final LocadoraMovimento other = (LocadoraMovimento) obj;
        return true;
    }

    @Override
    public String toString() {
        return "LocadoraMovimento{" + "id=" + id + ", locadoraLote=" + locadoraLote + ", titulo=" + titulo + ", operadorDevolucao=" + operadorDevolucao + ", evt=" + evt + ", dtDevolucaoPrevisao=" + dtDevolucaoPrevisao + ", dtDevolucao=" + dtDevolucao + '}';
    }
}
