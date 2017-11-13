package br.com.rtools.agendamentos;

import br.com.rtools.agendamentos.dao.AgendamentoCancelamentoDao;
import br.com.rtools.agendamentos.dao.AgendamentosDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ag_agendamento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dt_data", "id_agendador", "id_pessoa", "id_status"})
)
public class Agendamentos implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "dt_emissao", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtEmissao;
    @JoinColumn(name = "id_status", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AgendaStatus agendaStatus;
    @JoinColumn(name = "id_agendador", referencedColumnName = "id")
    @ManyToOne
    private Usuario agendador;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "dt_data")
    @Temporal(TemporalType.DATE)
    private Date dtData;
    @Column(name = "ds_contato", length = 150)
    private String contato;
    @Column(name = "ds_telefone", length = 15)
    private String telefone;
    @Column(name = "ds_email", length = 150)
    private String email;
    @Lob
    @Column(name = "ds_obs")
    private String obs;
    
    @Transient
    private List<AgendamentoCancelamento> listCancelamentos;
    
    public Agendamentos() {
        this.id = null;
        this.dtEmissao = new Date();
        this.agendaStatus = null;
        this.agendador = null;
        this.pessoa = null;
        this.dtData = null;
        this.contato = "";
        this.telefone = "";
        this.email = "";
        this.obs = "";
        this.listCancelamentos = null;
    }
    
    public Agendamentos(Integer id, Date dtEmissao, AgendaStatus agendaStatus, Usuario agendador, Pessoa pessoa, Date dtData, String contato, String telefone, String email, String obs) {
        this.id = id;
        this.dtEmissao = dtEmissao;
        this.agendaStatus = agendaStatus;
        this.agendador = agendador;
        this.pessoa = pessoa;
        this.dtData = dtData;
        this.contato = contato;
        this.telefone = telefone;
        this.email = email;
        this.obs = obs;
        this.listCancelamentos = null;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Date getDtEmissao() {
        return dtEmissao;
    }
    
    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }
    
    public String getEmissao() {
        return DataHoje.converteData(dtEmissao);
    }
    
    public void setEmissao(String emissao) {
        this.dtEmissao = DataHoje.converte(emissao);
    }
    
    public AgendaStatus getAgendaStatus() {
        return agendaStatus;
    }
    
    public void setAgendaStatus(AgendaStatus agendaStatus) {
        this.agendaStatus = agendaStatus;
    }
    
    public Usuario getAgendador() {
        return agendador;
    }
    
    public void setAgendador(Usuario agendador) {
        this.agendador = agendador;
    }
    
    public Pessoa getPessoa() {
        return pessoa;
    }
    
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
    
    public Date getDtData() {
        return dtData;
    }
    
    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }
    
    public String getData() {
        return DataHoje.converteData(dtData);
    }
    
    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
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
    
    public String getObs() {
        return obs;
    }
    
    public void setObs(String obs) {
        this.obs = obs;
    }
    
    public List<AgendamentoCancelamento> getListCancelamentos() {
        if (listCancelamentos == null && this.id != null) {
            listCancelamentos = new ArrayList();
            listCancelamentos = new AgendamentoCancelamentoDao().findByAgendamento(this.id);
        }
        return listCancelamentos;
    }
    
    public void setListCancelamentos(List<AgendamentoCancelamento> listCancelamentos) {
        this.listCancelamentos = listCancelamentos;
    }
    
}
