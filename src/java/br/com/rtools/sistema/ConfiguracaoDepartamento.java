package br.com.rtools.sistema;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.SisEmailProtocolo;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "conf_departamento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_departamento", "id_filial", "ds_email"})
)
@NamedQueries({
    @NamedQuery(name = "ConfiguracaoDepartamento.findAll", query = "SELECT CD FROM ConfiguracaoDepartamento CD ORDER BY CD.filial.filial.pessoa.nome ASC, CD.departamento.descricao ASC, CD.email ASC ")
})
public class ConfiguracaoDepartamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne
    private Filial filial;
    @Column(name = "ds_email", length = 50, nullable = false)
    private String email;
    @Column(name = "ds_senha", length = 20, nullable = false)
    private String senha;
    @Column(name = "ds_smtp", length = 50)
    private String smtp;
    @Column(name = "ds_email_resposta", length = 50)
    private String emailResposta;
    @Column(name = "nr_porta")
    private Integer porta;
    @Column(name = "is_autenticado")
    private Boolean autenticado;
    @JoinColumn(name = "id_email_protocolo", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private SisEmailProtocolo sisEmailProtocolo;
    @Column(name = "ds_assinatura", length = 500)
    private String assinatura;
    @Column(name = "is_servidor_smtp")
    private Boolean servidorSmtp;

    @Transient
    private Boolean selected;

    @Transient
    private String senhaConfirma;

    @Transient
    private String emailTest;

    public ConfiguracaoDepartamento() {
        this.id = null;
        this.departamento = null;
        this.filial = null;
        this.email = "";
        this.senha = "";
        this.smtp = "";
        this.emailResposta = "";
        this.porta = 0;
        this.autenticado = false;
        this.sisEmailProtocolo = null;
        this.assinatura = "";
        this.selected = false;
        this.senhaConfirma = "";
        this.emailTest = "";
        this.servidorSmtp = false;
    }

    public ConfiguracaoDepartamento(Integer id, Departamento departamento, Filial filial, String email, String senha, String smtp, String emailResposta, Boolean autenticado, Integer porta, SisEmailProtocolo sisEmailProtocolo, String assinatura, Boolean servidorSmtp) {
        this.id = id;
        this.departamento = departamento;
        this.filial = filial;
        this.email = email;
        this.senha = senha;
        this.smtp = smtp;
        this.emailResposta = emailResposta;
        this.porta = porta;
        this.autenticado = autenticado;
        this.sisEmailProtocolo = sisEmailProtocolo;
        this.assinatura = assinatura;
        this.selected = false;
        this.senhaConfirma = "";
        this.emailTest = "";
        this.servidorSmtp = servidorSmtp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public String getEmailResposta() {
        return emailResposta;
    }

    public void setEmailResposta(String emailResposta) {
        this.emailResposta = emailResposta;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public SisEmailProtocolo getSisEmailProtocolo() {
        return sisEmailProtocolo;
    }

    public void setSisEmailProtocolo(SisEmailProtocolo sisEmailProtocolo) {
        this.sisEmailProtocolo = sisEmailProtocolo;
    }

    public Boolean getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getSenhaConfirma() {
        return senhaConfirma;
    }

    public void setSenhaConfirma(String senhaConfirma) {
        this.senhaConfirma = senhaConfirma;
    }

    public String getEmailTest() {
        return emailTest;
    }

    public void setEmailTest(String emailTest) {
        this.emailTest = emailTest;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    // SE USA SERVIDOR STMP EXTERNO / SERVIÇO CONTRATADO
    public Boolean getServidorSmtp() {
        return servidorSmtp;
    }

    public void setServidorSmtp(Boolean servidorSmtp) {
        this.servidorSmtp = servidorSmtp;
    }

    @Override
    public String toString() {
        return "ConfiguracaoDepartamento{" + "id=" + id + ", departamento=" + departamento + ", filial=" + filial + ", email=" + email + ", senha=" + senha + ", smtp=" + smtp + ", emailResposta=" + emailResposta + ", porta=" + porta + ", autenticado=" + autenticado + ", sisEmailProtocolo=" + sisEmailProtocolo + ", assinatura=" + assinatura + '}';
    }

}
