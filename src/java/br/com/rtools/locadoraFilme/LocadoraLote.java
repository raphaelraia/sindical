package br.com.rtools.locadoraFilme;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
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
@Table(name = "loc_lote",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dt_locacao", "id_pessoa"})
)
public class LocadoraLote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_locacao")
    private Date dtLocacao;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_autorizado", referencedColumnName = "id")
    @ManyToOne
    private LocadoraAutorizados locadoraAutorizados;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Filial filial;

    public LocadoraLote() {
        this.id = null;
        this.dtLocacao = new Date();
        this.pessoa = null;
        this.locadoraAutorizados = null;
        this.usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        this.filial = null;
    }

    public LocadoraLote(Integer id, Date dtLocacao, Pessoa pessoa, LocadoraAutorizados locadoraAutorizados, Usuario usuario, Filial filial) {
        this.id = id;
        this.dtLocacao = dtLocacao;
        this.pessoa = pessoa;
        this.locadoraAutorizados = locadoraAutorizados;
        this.usuario = usuario;
        this.filial = filial;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtLocacao() {
        return dtLocacao;
    }

    public void setDtLocacao(Date dtLocacao) {
        this.dtLocacao = dtLocacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public LocadoraAutorizados getLocadoraAutorizados() {
        return locadoraAutorizados;
    }

    public void setLocadoraAutorizados(LocadoraAutorizados locadoraAutorizados) {
        this.locadoraAutorizados = locadoraAutorizados;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDataLocacaoString() {
        return DataHoje.converteData(dtLocacao);
    }

    public void setDataLocacaoString(String dataLocacaoString) {
        this.dtLocacao = DataHoje.converte(dataLocacaoString);
    }

    public String getHoraLocacaoString() {
        return DataHoje.converteHora(dtLocacao);
    }

    public void setHoraLocacaoString(String horaLocacaoString) {
        this.dtLocacao = DataHoje.converteDataHora(DataHoje.converteData(dtLocacao), horaLocacaoString);
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    @Override
    public String toString() {
        return "LocadoraLote{" + "id=" + id + ", dtLocacao=" + dtLocacao + ", pessoa=" + pessoa + ", locadoraAutorizados=" + locadoraAutorizados + ", usuario=" + usuario + ", filial=" + filial + '}';
    }

}
