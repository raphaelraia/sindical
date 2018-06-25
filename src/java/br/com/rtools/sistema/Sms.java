package br.com.rtools.sistema;

import br.com.rtools.configuracao.ConfiguracaoSms;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_sms")
public class Sms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_envio", nullable = true)
    private Date dtEnvio;
    @JoinColumn(name = "id_configuracao_sms", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private ConfiguracaoSms configuracaoSms;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_destinatario", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Pessoa destinatario;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "ds_numero", length = 30, nullable = true)
    private String numero;
    @Column(name = "ds_mensagem", length = 160, nullable = true)
    private String mensagem;
    @Column(name = "ds_referencia", length = 160, nullable = true)
    private String referencia;
    @Column(name = "ds_tabela", length = 50, nullable = true)
    private String tabela;
    @Column(name = "ds_chave", length = 50, nullable = true)
    private String chave;
    @Column(name = "ds_valor", length = 50, nullable = true)
    private String valor;
    @Column(name = "nr_codigo", length = 50, nullable = true)
    private Integer codigo;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_agendamento", nullable = true)
    private Date dtAgendamento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_recebimento", nullable = true)
    private Date dtRecebimento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cancelamento", nullable = true)
    private Date dtCancelamento;

    public Sms() {
        this.id = null;
        this.dtEnvio = new Date();
        this.configuracaoSms = null;
        this.destinatario = null;
        this.usuario = null;
        this.rotina = null;
        this.numero = "";
        this.mensagem = "";
        this.referencia = "";
        this.tabela = "";
        this.chave = "";
        this.valor = "";
        this.codigo = null;
        this.dtAgendamento = null;
        this.dtRecebimento = null;
        this.dtCancelamento = null;
    }

    public Sms(Integer id, Date dtEnvio, ConfiguracaoSms configuracaoSms, Usuario usuario, Pessoa destinatario, Rotina rotina, String numero, String mensagem, String referencia, String tabela, String chave, String valor, Integer codigo, Date dtAgendamento, Date dtRecebimento, Date dtCancelamento) {
        this.id = id;
        this.dtEnvio = dtEnvio;
        this.configuracaoSms = configuracaoSms;
        this.destinatario = destinatario;
        this.usuario = usuario;
        this.rotina = rotina;
        this.numero = numero;
        this.mensagem = mensagem;
        this.referencia = referencia;
        this.tabela = tabela;
        this.chave = chave;
        this.valor = valor;
        this.codigo = codigo;
        this.dtAgendamento = dtAgendamento;
        this.dtRecebimento = dtRecebimento;
        this.dtCancelamento = dtCancelamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtEnvio() {
        return dtEnvio;
    }

    public void setDtEnvio(Date dtEnvio) {
        this.dtEnvio = dtEnvio;
    }

    public ConfiguracaoSms getConfiguracaoSms() {
        return configuracaoSms;
    }

    public void setConfiguracaoSms(ConfiguracaoSms configuracaoSms) {
        this.configuracaoSms = configuracaoSms;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Pessoa getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Pessoa destinatario) {
        this.destinatario = destinatario;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getDtRecebimento() {
        return dtRecebimento;
    }

    public void setDtRecebimento(Date dtRecebimento) {
        this.dtRecebimento = dtRecebimento;
    }

    public Date getDtAgendamento() {
        return dtAgendamento;
    }

    public void setDtAgendamento(Date dtAgendamento) {
        this.dtAgendamento = dtAgendamento;
    }

    public Date getDtCancelamento() {
        return dtCancelamento;
    }

    public void setDtCancelamento(Date dtCancelamento) {
        this.dtCancelamento = dtCancelamento;
    }

}
