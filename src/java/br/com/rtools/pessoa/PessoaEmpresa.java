package br.com.rtools.pessoa;

import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.utilitarios.DataHoje;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import org.primefaces.event.SelectEvent;

@Entity
@Table(name = "pes_pessoa_empresa")
@NamedQuery(name = "PessoaEmpresa.pesquisaID", query = "select pe from PessoaEmpresa pe where pe.id = :pid")
public class PessoaEmpresa implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_fisica", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Fisica fisica;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Juridica juridica;
    @JoinColumn(name = "id_funcao", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Profissao funcao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_admissao")
    private Date dtAdmissao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_demissao")
    private Date dtDemissao;
    @Column(name = "ds_setor", length = 30, nullable = false)
    private String setor;
    @Column(name = "aviso_trabalhado", nullable = true)
    private boolean avisoTrabalhado;
    @Column(name = "ds_codigo", length = 30)
    private String codigo;
    @Column(name = "is_principal", nullable = false, columnDefinition = "boolean default true")
    private boolean principal;
    @Column(name = "is_socio", nullable = false, columnDefinition = "boolean default false")
    private boolean socio;
    @Column(name = "nr_registro", nullable = false, columnDefinition = "integer default 1")
    private Integer nrRegistro;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_alterna_principal")
    private Date dtAlternaPrincipal;

    public PessoaEmpresa() {
        this.id = -1;
        this.fisica = new Fisica();
        this.juridica = new Juridica();
        this.funcao = new Profissao();
        setAdmissao("");
        setDemissao("");
        this.setor = "";
        this.avisoTrabalhado = true;
        this.codigo = "";
        this.principal = true;
        this.socio = false;
        this.nrRegistro = 1;
        this.dtAlternaPrincipal = null;
    }

    public PessoaEmpresa(int id, Fisica fisica, Juridica juridica, Profissao funcao, String admissao, String demissao, String setor, boolean avisoTrabalhado, String codigo, boolean principal, boolean socio, Integer nrRegistro, Date dtAlternaPrincipal) {
        this.id = id;
        this.fisica = fisica;
        this.juridica = juridica;
        this.funcao = funcao;
        setAdmissao(admissao);
        setDemissao(demissao);
        this.setor = setor;
        this.avisoTrabalhado = avisoTrabalhado;
        this.codigo = codigo;
        this.principal = principal;
        this.socio = socio;
        this.nrRegistro = nrRegistro;
        this.dtAlternaPrincipal = dtAlternaPrincipal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Profissao getFuncao() {
        return funcao;
    }

    public void setFuncao(Profissao funcao) {
        this.funcao = funcao;
    }

    public Date getDtAdmissao() {
        return dtAdmissao;
    }

    public void setDtAdmissao(Date dtAdmissao) {
        this.dtAdmissao = dtAdmissao;
    }

    public String getAdmissao() {
        if (dtAdmissao != null) {
            return DataHoje.converteData(dtAdmissao);
        } else {
            return "";
        }
    }

    public void setAdmissao(String admissao) {
        this.dtAdmissao = DataHoje.converte(admissao);

    }

    public Date getDtDemissao() {
        return dtDemissao;
    }

    public void setDtDemissao(Date dtDemissao) {
        this.dtDemissao = dtDemissao;
    }

    public String getDemissao() {
        if (dtDemissao != null) {
            return DataHoje.converteData(dtDemissao);
        } else {
            return "";
        }
    }

    public void setDemissao(String demissao) {
        this.dtDemissao = DataHoje.converte(demissao);
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public boolean isAvisoTrabalhado() {
        return avisoTrabalhado;
    }

    public void setAvisoTrabalhado(boolean avisoTrabalhado) {
        this.avisoTrabalhado = avisoTrabalhado;
    }

    public void selecionaDataAdmissao(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dtAdmissao = DataHoje.converte(format.format(event.getObject()));
    }

    public void selecionaDataDemissao(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dtDemissao = DataHoje.converte(format.format(event.getObject()));
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public Agendamento getAgendamento() {
        if (this != null && this.id != -1) {
            return new HomologacaoDao().findByEmpresa(this.id);
        }
        return null;
    }

    public boolean isSocio() {
        return socio;
    }

    public void setSocio(boolean socio) {
        this.socio = socio;
    }

    public Integer getNrRegistro() {
        return nrRegistro;
    }

    public void setNrRegistro(Integer nrRegistro) {
        this.nrRegistro = nrRegistro;
    }

    public Date getDtAlternaPrincipal() {
        return dtAlternaPrincipal;
    }

    public void setDtAlternaPrincipal(Date dtAlternaPrincipal) {
        this.dtAlternaPrincipal = dtAlternaPrincipal;
    }

}
