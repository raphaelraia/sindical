package br.com.rtools.sistema;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_autorizacoes")
public class SisAutorizacoes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario operador;
    @JoinColumn(name = "id_gestor", referencedColumnName = "id")
    @ManyToOne
    private Usuario gestor;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "dt_solicitacao", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtSolicitacao;
    @Column(name = "ds_hora_solicitacao", length = 5, nullable = false)
    private String horaSolicitacao;
    @Column(name = "dt_autorizacao")
    @Temporal(TemporalType.DATE)
    private Date dtAutorizacao;
    @Column(name = "ds_hora_autorizacao", length = 5)
    private String horaAutorizacao;
    @Column(name = "ds_dados_originais", length = 250, nullable = false)
    private String dadosOriginais;
    @Column(name = "ds_dados_alterados", length = 250, nullable = false)
    private String dadosAlterados;
    @Column(name = "ds_motivo_solicitacao", length = 500, nullable = false)
    private String motivoSolicitacao;
    @Column(name = "ds_motivo_recusa", length = 500)
    private String motivoRecusa;
    @Column(name = "is_autorizado", nullable = false, columnDefinition = "boolean default false")
    private Boolean autorizado;
    @Column(name = "ds_tabela", length = 150, nullable = false)
    private String tabela;
    @Column(name = "ds_coluna", length = 150, nullable = false)
    private String coluna;
    @Column(name = "nr_codigo", nullable = false)
    private Integer codigo;
    @JoinColumn(name = "id_evento", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Evento evento;
    @JoinColumn(name = "id_autorizacao", referencedColumnName = "id", nullable = true)
    @OneToOne
    private SisAutorizacoes autorizacoes;
    @JoinColumn(name = "id_tipo_autorizacao", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private SisAutorizacoesTipo sisAutorizacoesTipo;
    @JoinColumn(name = "id_rotina_destino", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotinaDestino;
    @Column(name = "dt_concluido", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtConcluido;

    public SisAutorizacoes() {
        this.id = null;
        this.rotina = null;
        this.operador = null;
        this.gestor = null;
        this.pessoa = null;
        this.dtSolicitacao = new Date();
        this.horaSolicitacao = DataHoje.horaMinuto();
        this.dtAutorizacao = null;
        this.horaAutorizacao = "";
        this.dadosOriginais = "";
        this.dadosAlterados = "";
        this.motivoSolicitacao = "";
        this.motivoRecusa = "";
        this.autorizado = false;
        this.tabela = null;
        this.coluna = null;
        this.codigo = null;
        this.evento = null;
        this.autorizacoes = null;
        this.sisAutorizacoesTipo = null;
        this.rotinaDestino = null;
        this.dtConcluido = null;
    }

    public SisAutorizacoes(Integer id, Rotina rotina, Usuario operador, Usuario gestor, Pessoa pessoa, Date dtSolicitacao, String horaSolicitacao, Date dtAutorizacao, String horaAutorizacao, String dadosOriginais, String dadosAlterados, String motivoSolicitacao, String motivoRecusa, Boolean autorizado, String tabela, String coluna, Integer codigo, Evento evento, SisAutorizacoes autorizacoes, SisAutorizacoesTipo sisAutorizacoesTipo, Rotina rotinaDestino, Date dtConcluido) {
        this.id = id;
        this.rotina = rotina;
        this.operador = operador;
        this.gestor = gestor;
        this.pessoa = pessoa;
        this.dtSolicitacao = dtSolicitacao;
        this.horaSolicitacao = horaSolicitacao;
        this.dtAutorizacao = dtAutorizacao;
        this.horaAutorizacao = horaAutorizacao;
        this.dadosOriginais = dadosOriginais;
        this.dadosAlterados = dadosAlterados;
        this.motivoSolicitacao = motivoSolicitacao;
        this.motivoRecusa = motivoRecusa;
        this.autorizado = autorizado;
        this.tabela = tabela;
        this.coluna = coluna;
        this.codigo = codigo;
        this.evento = evento;
        this.autorizacoes = autorizacoes;
        this.sisAutorizacoesTipo = sisAutorizacoesTipo;
        this.rotinaDestino = rotinaDestino;
        this.dtConcluido = dtConcluido;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Usuario getGestor() {
        return gestor;
    }

    public void setGestor(Usuario gestor) {
        this.gestor = gestor;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtSolicitacao() {
        return dtSolicitacao;
    }

    public void setDtSolicitacao(Date dtSolicitacao) {
        this.dtSolicitacao = dtSolicitacao;
    }

    public String getSolicitacao() {
        return DataHoje.converteData(dtSolicitacao);
    }

    public void setSolicitacao(String solicitacao) {
        this.dtSolicitacao = DataHoje.converte(solicitacao);
    }

    public String getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public void setHoraSolicitacao(String horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }

    public Date getDtAutorizacao() {
        return dtAutorizacao;
    }

    public void setDtAutorizacao(Date dtAutorizacao) {
        this.dtAutorizacao = dtAutorizacao;
    }

    public String getAutorizacao() {
        return DataHoje.converteData(dtAutorizacao);
    }

    public void setAutorizacao(String autorizacao) {
        this.dtAutorizacao = DataHoje.converte(autorizacao);
    }

    public String getHoraAutorizacao() {
        return horaAutorizacao;
    }

    public void setHoraAutorizacao(String horaAutorizacao) {
        this.horaAutorizacao = horaAutorizacao;
    }

    public String getDadosOriginais() {
        return dadosOriginais;
    }

    public void setDadosOriginais(String dadosOriginais) {
        this.dadosOriginais = dadosOriginais;
    }

    public String getDadosAlterados() {
        return dadosAlterados;
    }

    public void setDadosAlterados(String dadosAlterados) {
        this.dadosAlterados = dadosAlterados;
    }

    public void setDadosAlterados(Integer dadosAlterados) {
        try {
            this.dadosAlterados = Integer.toString(dadosAlterados);
        } catch (Exception e) {

        }
    }

    public String getMotivoSolicitacao() {
        return motivoSolicitacao;
    }

    public void setMotivoSolicitacao(String motivoSolicitacao) {
        this.motivoSolicitacao = motivoSolicitacao;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public Boolean getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Boolean autorizado) {
        this.autorizado = autorizado;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public SisAutorizacoes getAutorizacoes() {
        return autorizacoes;
    }

    public void setAutorizacoes(SisAutorizacoes autorizacoes) {
        this.autorizacoes = autorizacoes;
    }

    public SisAutorizacoesTipo getSisAutorizacoesTipo() {
        return sisAutorizacoesTipo;
    }

    public void setSisAutorizacoesTipo(SisAutorizacoesTipo sisAutorizacoesTipo) {
        this.sisAutorizacoesTipo = sisAutorizacoesTipo;
    }

    public Rotina getRotinaDestino() {
        return rotinaDestino;
    }

    public void setRotinaDestino(Rotina rotinaDestino) {
        this.rotinaDestino = rotinaDestino;
    }

    public Date getDtConcluido() {
        return dtConcluido;
    }

    public void setDtConcluido(Date dtConcluido) {
        this.dtConcluido = dtConcluido;
    }

}
