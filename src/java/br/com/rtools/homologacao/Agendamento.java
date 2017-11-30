package br.com.rtools.homologacao;

import br.com.rtools.homologacao.dao.SenhaDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

@Entity
@Table(name = "hom_agendamento")
@NamedQuery(name = "Agendamento.pesquisaID", query = "SELECT A FROM Agendamento AS A WHERE A.id = :pid")
public class Agendamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data")
    private Date dtData;
    @JoinColumn(name = "id_horario", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Horarios horarios;
    @JoinColumn(name = "id_status", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Status status;
    @JoinColumn(name = "id_pessoa_empresa", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private PessoaEmpresa pessoaEmpresa;
    @JoinColumn(name = "id_agendador", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario agendador;
    @JoinColumn(name = "id_homologador", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario homologador;
    @JoinColumn(name = "id_demissao", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private Demissao demissao;
    @Column(name = "ds_contato", length = 50, nullable = true)
    private String contato;
    @Column(name = "ds_telefone", length = 20, nullable = true)
    private String telefone;
    @Column(name = "ds_email", length = 50, nullable = true)
    private String email;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Filial filial;
    @JoinColumn(name = "id_recepcao", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Recepcao recepcao;
    @Column(name = "ds_obs", length = 2000)
    private String observacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @Column(name = "is_no_prazo")
    private boolean noPrazo;
    @Lob
    @Column(name = "ds_motivo_recusa")
    private String motivoRecusa;
    @JoinColumn(name = "id_operador_recusa", referencedColumnName = "id")
    @ManyToOne
    private Usuario operadorRecusa;
    @JoinColumn(name = "id_operador_recusa2", referencedColumnName = "id")
    @ManyToOne
    private Usuario operadorRecusa2;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_solicitacao2")
    private Date dtSolicitacao2;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_recusa1")
    private Date dtRecusa1;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_recusa2")
    private Date dtRecusa2;
    @Lob
    @Column(name = "ds_motivo_recusa2")
    private String motivoRecusa2;
    @Column(name = "is_encaixe")
    private Boolean encaixe;

    public Agendamento() {
        this.id = -1;
        this.dtData = null;
        this.horarios = new Horarios();
        this.status = new Status();
        this.pessoaEmpresa = new PessoaEmpresa();
        this.agendador = null;
        this.homologador = null;
        this.demissao = new Demissao();
        this.contato = "";
        this.telefone = "";
        this.email = "";
        this.filial = new Filial();
        this.recepcao = null;
        this.observacao = "";
        this.dtEmissao = null;
        this.noPrazo = true;
        this.motivoRecusa = "";
        this.operadorRecusa = null;
        this.operadorRecusa2 = null;
        this.dtRecusa1 = null;
        this.dtRecusa2 = null;
        this.dtSolicitacao2 = null;
        this.motivoRecusa2 = "";
        this.encaixe = false;
    }

    public Agendamento(int id, String data, Horarios horarios, Status status, PessoaEmpresa pessoaEmpresa, Usuario agendador, Usuario homologador, Demissao demissao, String contato, String telefone, String email, Filial filial, Recepcao recepcao, String observacao, String emissao, Boolean noPrazo, String motivoRecusa, Usuario operadorRecusa, Usuario operadorRecusa2, Date dtRecusa1, Date dtRecusa2, Date dtSolicitacao2, String motivoRecusa2, Boolean encaixe) {
        this.id = id;
        this.setData(data);
        this.horarios = horarios;
        this.status = status;
        this.pessoaEmpresa = pessoaEmpresa;
        this.agendador = agendador;
        this.homologador = homologador;
        this.demissao = demissao;
        this.contato = contato;
        this.telefone = telefone;
        this.email = email;
        this.filial = filial;
        this.recepcao = recepcao;
        this.observacao = observacao;
        this.setEmissao(data);
        this.noPrazo = noPrazo;
        this.motivoRecusa = motivoRecusa;
        this.operadorRecusa = operadorRecusa;
        this.operadorRecusa2 = operadorRecusa2;
        this.dtRecusa1 = dtRecusa1;
        this.dtRecusa2 = dtRecusa2;
        this.dtSolicitacao2 = dtSolicitacao2;
        this.motivoRecusa2 = motivoRecusa2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getData() {
        if (dtData != null) {
            return DataHoje.converteData(dtData);
        } else {
            return "";
        }
    }

    public void setData(String data) {
        if (!(data.isEmpty())) {
            this.dtData = DataHoje.converte(data);
        }
    }

    public Horarios getHorarios() {
        return horarios;
    }

    public void setHorarios(Horarios horarios) {
        this.horarios = horarios;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public Usuario getAgendador() {
        return agendador;
    }

    public void setAgendador(Usuario agendador) {
        this.agendador = agendador;
    }

    public Usuario getHomologador() {
        return homologador;
    }

    public void setHomologador(Usuario homologador) {
        this.homologador = homologador;
    }

    public Demissao getDemissao() {
        return demissao;
    }

    public void setDemissao(Demissao demissao) {
        this.demissao = demissao;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Recepcao getRecepcao() {
        return recepcao;
    }

    public void setRecepcao(Recepcao recepcao) {
        this.recepcao = recepcao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getEmissao() {
        if (dtEmissao != null) {
            return DataHoje.converteData(dtEmissao);
        } else {
            return "";
        }
    }

    public void setEmissao(String data) {
        if (!(data.isEmpty())) {
            this.dtEmissao = DataHoje.converte(data);
        }
    }

    public void selectioDataEmissao(SelectEvent selectEvent) {
        ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        setDtEmissao(event.getStartDate());
    }

    public void selectioData(SelectEvent selectEvent) {
        ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        setDtData(event.getStartDate());
    }

    public boolean isNoPrazo() {
        return noPrazo;
    }

    public void setNoPrazo(boolean noPrazo) {
        this.noPrazo = noPrazo;
    }

    public Senha getSenha() {
        Senha senha = new Senha();
        if (this.id != -1) {
            SenhaDao senhaDao = new SenhaDao();
            senha = senhaDao.find(this.id);
            if (senha == null) {
                senha = new Senha();
            }
        }
        return senha;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public Usuario getOperadorRecusa() {
        return operadorRecusa;
    }

    public void setOperadorRecusa(Usuario operadorRecusa) {
        this.operadorRecusa = operadorRecusa;
    }

    public Usuario getOperadorRecusa2() {
        return operadorRecusa2;
    }

    public void setOperadorRecusa2(Usuario operadorRecusa2) {
        this.operadorRecusa2 = operadorRecusa2;
    }

    public Date getDtSolicitacao2() {
        return dtSolicitacao2;
    }

    public void setDtSolicitacao2(Date dtSolicitacao2) {
        this.dtSolicitacao2 = dtSolicitacao2;
    }

    public Date getDtRecusa1() {
        return dtRecusa1;
    }

    public void setDtRecusa1(Date dtRecusa1) {
        this.dtRecusa1 = dtRecusa1;
    }

    public Date getDtRecusa2() {
        return dtRecusa2;
    }

    public void setDtRecusa2(Date dtRecusa2) {
        this.dtRecusa2 = dtRecusa2;
    }

    public String getMotivoRecusa2() {
        return motivoRecusa2;
    }

    public void setMotivoRecusa2(String motivoRecusa2) {
        this.motivoRecusa2 = motivoRecusa2;
    }

    public Boolean getEncaixe() {
        return encaixe;
    }

    public void setEncaixe(Boolean encaixe) {
        this.encaixe = encaixe;
    }
}
