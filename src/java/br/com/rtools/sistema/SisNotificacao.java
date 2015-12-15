package br.com.rtools.sistema;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_notificacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ds_titulo", "id_categoria", "dt_inicial", "dt_inicial", "dt_final", "ds_hora_inicio", "ds_hora_fim"})
)
@NamedQuery(name = "SisNotificacao.findAll", query = "SELECT SN FROM SisNotificacao AS SN ORDER BY SN.dtCadastro DESC, SN.dtCadastro DESC")
public class SisNotificacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_titulo", length = 300)
    private String titulo;
    @Column(name = "ds_observacao", length = 5000)
    private String observacao;
    @JoinColumn(name = "id_notificacao_categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SisNotificacaoCategoria sisNotificacaoCategoria;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_inicial")
    private Date dtInicial;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_final")
    private Date dtFinal;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro")
    private Date dtCadastro;
    @Column(name = "is_ativo")
    private Boolean ativo;
    @Column(name = "is_destaque")
    private Boolean destaque;
    @JoinColumn(name = "id_agendador", referencedColumnName = "id")
    @ManyToOne
    private Usuario agendador;

    public SisNotificacao() {
        this.id = null;
        this.titulo = "";
        this.observacao = "";
        this.sisNotificacaoCategoria = null;
        this.dtInicial = DataHoje.converte(DataHoje.data());
        this.dtFinal = DataHoje.converte(DataHoje.data());
        this.dtCadastro = new Date();
        this.ativo = true;
        this.destaque = false;
        this.agendador = null;
    }

    public SisNotificacao(Integer id, String titulo, String observacao, SisNotificacaoCategoria sisNotificacaoCategoria, Date dtInicial, Date dtFinal, Date dtCadastro, Boolean ativo, Boolean destaque, Usuario agendador) {
        this.id = id;
        this.titulo = titulo;
        this.observacao = observacao;
        this.sisNotificacaoCategoria = sisNotificacaoCategoria;
        this.dtInicial = dtInicial;
        this.dtFinal = dtFinal;
        this.dtCadastro = dtCadastro;
        this.ativo = ativo;
        this.destaque = destaque;
        this.agendador = agendador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo.toUpperCase();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public SisNotificacaoCategoria getSisNotificacaoCategoria() {
        return sisNotificacaoCategoria;
    }

    public void setSisNotificacaoCategoria(SisNotificacaoCategoria sisNotificacaoCategoria) {
        this.sisNotificacaoCategoria = sisNotificacaoCategoria;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public String getHoraInicial() {
        return DataHoje.converteHora(dtInicial);
    }

    public void setHoraInicial(String horaInicial) {
        this.dtInicial = DataHoje.converteDataHora(DataHoje.converteData(dtInicial), horaInicial);
    }

    public String getHoraFinal() {
        return DataHoje.converteHora(dtFinal);
    }

    public void setHoraFinal(String horaFinal) {
        this.dtFinal = DataHoje.converteDataHora(DataHoje.converteData(dtFinal), horaFinal);
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDestaque() {
        return destaque;
    }

    public void setDestaque(Boolean destaque) {
        this.destaque = destaque;
    }

    public Usuario getAgendador() {
        return agendador;
    }

    public void setAgendador(Usuario agendador) {
        this.agendador = agendador;
    }

    public String getInicialString() {
        if (dtInicial != null && dtFinal == null) {
            dtFinal = dtInicial;
        }
        if (dtInicial == null && dtFinal != null) {
            dtFinal = null;
        }
        return DataHoje.converteData(dtInicial);
    }

    public void setInicialString(String inicialString) {
        this.dtInicial = DataHoje.converte(inicialString);
    }

    public String getFinalString() {
        if (dtInicial != null && dtFinal == null) {
            dtFinal = dtInicial;
        }
        if (dtInicial == null && dtFinal != null) {
            dtFinal = null;
        }
        return DataHoje.converteData(dtFinal);
    }

    public void setFinalString(String cadastroString) {
        this.dtFinal = DataHoje.converte(cadastroString);
    }

    public String getCadastroString() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setCadastroString(String cadastroString) {
        this.dtCadastro = DataHoje.converte(cadastroString);
    }

}
