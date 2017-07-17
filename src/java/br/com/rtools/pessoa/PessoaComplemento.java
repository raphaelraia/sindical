package br.com.rtools.pessoa;

import javax.persistence.*;

@Entity
@Table(name = "pes_pessoa_complemento")
@NamedQuery(name = "PessoaComplemento.pesquisaID", query = "select pec from PessoaComplemento pec where pec.id = :pid")
public class PessoaComplemento implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @Column(name = "nr_dia_vencimento", nullable = true)
    private Integer nrDiaVencimento;
    @Column(name = "is_cobranca_bancaria")
    private Boolean cobrancaBancaria;
    @JoinColumn(name = "id_responsavel", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa responsavel;
    @Column(name = "is_bloqueia_obs_aviso", columnDefinition = "boolean default false")
    private Boolean bloqueiaObsAviso;
    @Column(name = "ds_obs_aviso")
    private String obsAviso;
    @Column(name = "is_cobranca_email", columnDefinition = "boolean default false", nullable = false)
    private Boolean cobrancaEmail;
    @JoinColumn(name = "id_status_cobranca", referencedColumnName = "id")
    @ManyToOne
    private StatusCobranca statusCobranca;

    public PessoaComplemento() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.nrDiaVencimento = 0;
        this.cobrancaBancaria = false;
        this.responsavel = null;
        this.bloqueiaObsAviso = false;
        this.obsAviso = "";
        this.cobrancaEmail = false;
        this.statusCobranca = null;
    }

    public PessoaComplemento(Integer id, Pessoa pessoa, Integer nrDiaVencimento, Boolean cobrancaBancaria, Pessoa responsavel, Boolean bloqueiaObsAviso, String obsAviso, Boolean cobrancaEmail, StatusCobranca statusCobranca) {
        this.id = id;
        this.pessoa = pessoa;
        this.nrDiaVencimento = nrDiaVencimento;
        this.cobrancaBancaria = cobrancaBancaria;
        this.responsavel = responsavel;
        this.bloqueiaObsAviso = bloqueiaObsAviso;
        this.obsAviso = obsAviso;
        this.cobrancaEmail = cobrancaEmail;
        this.statusCobranca = statusCobranca;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Integer getNrDiaVencimento() {
        return nrDiaVencimento;
    }

    public void setNrDiaVencimento(Integer nrDiaVencimento) {
        this.nrDiaVencimento = nrDiaVencimento;
    }

    public boolean getCobrancaBancaria() {
        return cobrancaBancaria;
    }

    public void setCobrancaBancaria(Boolean cobrancaBancaria) {
        this.cobrancaBancaria = cobrancaBancaria;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getBloqueiaObsAviso() {
        return bloqueiaObsAviso;
    }

    public void setBloqueiaObsAviso(Boolean bloqueiaObsAviso) {
        this.bloqueiaObsAviso = bloqueiaObsAviso;
    }

    public String getObsAviso() {
        return obsAviso;
    }

    public void setObsAviso(String obsAviso) {
        this.obsAviso = obsAviso.trim();
    }

    public Boolean getCobrancaEmail() {
        return cobrancaEmail;
    }

    public void setCobrancaEmail(Boolean cobrancaEmail) {
        this.cobrancaEmail = cobrancaEmail;
    }

    public StatusCobranca getStatusCobranca() {
        return statusCobranca;
    }

    public void setStatusCobranca(StatusCobranca statusCobranca) {
        this.statusCobranca = statusCobranca;
    }

}
