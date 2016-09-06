package br.com.rtools.agenda;

import br.com.rtools.agenda.dao.CompromissoUsuarioDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "age_compromisso")
public class Compromisso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_compromisso_categoria", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CompromissoCategoria compromissoCategoria;
    @JoinColumn(name = "id_secretaria", referencedColumnName = "id")
    @ManyToOne
    private Usuario secretaria;
    @JoinColumn(name = "id_periodo_repeticao", referencedColumnName = "id")
    @ManyToOne
    private Periodo periodoRepeticao;
    @JoinColumn(name = "id_semana", referencedColumnName = "id")
    @ManyToOne
    private Semana semana;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data")
    private Date dtData;
    @Column(name = "ds_hora_inicial", length = 5)
    private String horaInicial;
    @Column(name = "ds_hora_final", length = 5)
    private String horaFinal;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro")
    private Date dtCadastro;
    @Column(name = "ds_local_nome", length = 150)
    private String localNome;
    @JoinColumn(name = "id_local", referencedColumnName = "id")
    @ManyToOne
    private Endereco local;
    @Column(name = "ds_complemento", length = 150)
    private String complemento;
    @Column(name = "ds_numero", length = 50)
    private String numero;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "ds_descricao", length = 8000)
    private String descricao;
    @Column(name = "ds_detalhes", length = 8000)
    private String detalhes;
    @Column(name = "ds_relatorio", length = 8000)
    private String relatorio;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cancelamento")
    private Date dtCancelamento;
    @JoinColumn(name = "id_usuario_cancelador", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuarioCancelador;
    @Column(name = "ds_motivo_cancelamento", length = 8000)
    private String motivoCancelamento;
    @Column(name = "is_particular", columnDefinition = "boolean default false", nullable = false)
    private Boolean particular;

    @Transient
    private List<CompromissoUsuario> listCompromissoUsuario;

    public Compromisso() {
        this.id = null;
        this.compromissoCategoria = null;
        this.periodoRepeticao = null;
        this.semana = null;
        this.secretaria = null;
        this.dtData = new Date();
        this.horaInicial = DataHoje.hora();
        this.horaFinal = DataHoje.hora();
        this.dtCadastro = new Date();
        this.localNome = "";
        this.local = null;
        this.complemento = "";
        this.numero = "";
        this.pessoa = null;
        this.descricao = "";
        this.detalhes = "";
        this.relatorio = "";
        this.dtCancelamento = null;
        this.usuarioCancelador = null;
        this.motivoCancelamento = "";
        this.listCompromissoUsuario = null;
        this.particular = false;
    }

    public Compromisso(Integer id, CompromissoCategoria compromissoCategoria, Periodo periodoRepeticao, Semana semana, Usuario secretaria, Date dtData, String horaInicial, String horaFinal, Date dtCadastro, String localNome, Endereco local, String complemento, String numero, Pessoa pessoa, String descricao, String detalhes, String relatorio, Date dtCancelamento, Usuario usuarioCancelador, String motivoCancelamento, Boolean particular) {
        this.id = id;
        this.compromissoCategoria = compromissoCategoria;
        this.periodoRepeticao = periodoRepeticao;
        this.semana = semana;
        this.secretaria = secretaria;
        this.dtData = dtData;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
        this.dtCadastro = dtCadastro;
        this.localNome = localNome;
        this.local = local;
        this.complemento = complemento;
        this.numero = numero;
        this.pessoa = pessoa;
        this.descricao = descricao;
        this.detalhes = detalhes;
        this.relatorio = relatorio;
        this.dtCancelamento = dtCancelamento;
        this.usuarioCancelador = usuarioCancelador;
        this.motivoCancelamento = motivoCancelamento;
        this.particular = particular;
        this.listCompromissoUsuario = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CompromissoCategoria getCompromissoCategoria() {
        return compromissoCategoria;
    }

    public void setCompromissoCategoria(CompromissoCategoria compromissoCategoria) {
        this.compromissoCategoria = compromissoCategoria;
    }

    public Usuario getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Usuario secretaria) {
        this.secretaria = secretaria;
    }

    public Periodo getPeriodoRepeticao() {
        return periodoRepeticao;
    }

    public void setPeriodoRepeticao(Periodo periodoRepeticao) {
        this.periodoRepeticao = periodoRepeticao;
    }

    public Semana getSemana() {
        return semana;
    }

    public void setSemana(Semana semana) {
        this.semana = semana;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getLocalNome() {
        return localNome;
    }

    public void setLocalNome(String localNome) {
        this.localNome = localNome;
    }

    public Endereco getLocal() {
        return local;
    }

    public void setLocal(Endereco local) {
        this.local = local;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    public Date getDtCancelamento() {
        return dtCancelamento;
    }

    public void setDtCancelamento(Date dtCancelamento) {
        this.dtCancelamento = dtCancelamento;
    }

    public Usuario getUsuarioCancelador() {
        return usuarioCancelador;
    }

    public void setUsuarioCancelador(Usuario usuarioCancelador) {
        this.usuarioCancelador = usuarioCancelador;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getCadastro() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setCadastro(String cadastro) {
        this.dtCadastro = DataHoje.converte(cadastro);
    }

    public String getHoraCadastro() {
        return DataHoje.livre(dtCadastro, "HH:mm");
    }

    public String getCancelamento() {
        return DataHoje.converteData(dtCancelamento);
    }

    public void setCancelamento(String cancelamentoString) {
        this.dtCancelamento = DataHoje.converte(cancelamentoString);
    }

    public String getData() {
        return DataHoje.converteData(dtData);
    }

    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
    }

    public List<CompromissoUsuario> getListCompromissoUsuario() {
        if (this != null) {
            if (listCompromissoUsuario == null) {
                if (this.id != null) {
                    listCompromissoUsuario = new CompromissoUsuarioDao().findByCompromisso(this.id);
                }
            }
        }
        return listCompromissoUsuario;
    }

    public void setListCompromissoUsuario(List<CompromissoUsuario> listCompromissoUsuario) {
        this.listCompromissoUsuario = listCompromissoUsuario;
    }

    public Boolean getParticular() {
        return particular;
    }

    public void setParticular(Boolean particular) {
        this.particular = particular;
    }

}
