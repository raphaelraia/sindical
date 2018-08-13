package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.CampeonatoDependenteDao;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.Pessoa;
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
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "matr_campeonato",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_campeonato",
                    "id_campeonato_equipe",
                    "id_servico_pessoa"
                }
        )
)
public class MatriculaCampeonato implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_campeonato", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Campeonato campeonato;
    @JoinColumn(name = "id_campeonato_equipe", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CampeonatoEquipe campeonatoEquipe;
    @JoinColumn(name = "id_servico_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ServicoPessoa servicoPessoa;
    @Column(name = "dt_inativacao")
    @Temporal(TemporalType.DATE)
    private Date dtInativacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_suspensao_inicio", nullable = true)
    private Date dtSuspensaoInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_suspensao_fim", nullable = true)
    private Date dtSuspensaoFim;
    @Column(name = "ds_motivo_suspensao", length = 200, nullable = true)
    private String motivoSuspensao;

    @Transient
    private List listCampeonatoDependente;

    public MatriculaCampeonato() {
        this.id = null;
        this.campeonato = null;
        this.campeonatoEquipe = null;
        this.servicoPessoa = null;
        this.listCampeonatoDependente = null;
        this.dtSuspensaoInicio = null;
        this.dtSuspensaoFim = null;
        this.motivoSuspensao = "";
    }

    public MatriculaCampeonato(Integer id, Campeonato campeonato, CampeonatoEquipe campeonatoEquipe, Pessoa pessoa, ServicoPessoa servicoPessoa, Date dtInativacao, Date dtSuspensaoInicio, Date dtSuspensaoFim, String motivoSuspensao) {
        this.id = id;
        this.campeonato = campeonato;
        this.campeonatoEquipe = campeonatoEquipe;
        this.servicoPessoa = servicoPessoa;
        this.dtInativacao = dtInativacao;
        this.listCampeonatoDependente = null;
        this.dtSuspensaoInicio = dtSuspensaoInicio;
        this.dtSuspensaoFim = dtSuspensaoFim;
        this.motivoSuspensao = motivoSuspensao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public CampeonatoEquipe getCampeonatoEquipe() {
        return campeonatoEquipe;
    }

    public void setCampeonatoEquipe(CampeonatoEquipe campeonatoEquipe) {
        this.campeonatoEquipe = campeonatoEquipe;
    }

    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    public Date getDtInativacao() {
        return dtInativacao;
    }

    public void setDtInativacao(Date dtInativacao) {
        this.dtInativacao = dtInativacao;
    }

    public List getListCampeonatoDependente() {
        if (this.id != null) {
            if (this.listCampeonatoDependente == null) {
                this.listCampeonatoDependente = new ArrayList();
                this.listCampeonatoDependente = new CampeonatoDependenteDao().findByMatriculaCampeonato(this.id);
            }
        }
        return listCampeonatoDependente;
    }

    public void setListCampeonatoDependente(List listCampeonatoDependente) {
        this.listCampeonatoDependente = listCampeonatoDependente;
    }

    public Date getDtSuspensaoInicio() {
        return dtSuspensaoInicio;
    }

    public void setDtSuspensaoInicio(Date dtSuspensaoInicio) {
        this.dtSuspensaoInicio = dtSuspensaoInicio;
    }

    public Date getDtSuspensaoFim() {
        return dtSuspensaoFim;
    }

    public void setDtSuspensaoFim(Date dtSuspensaoFim) {
        this.dtSuspensaoFim = dtSuspensaoFim;
    }

    public String getMotivoSuspensao() {
        return motivoSuspensao;
    }

    public void setMotivoSuspensao(String motivoSuspensao) {
        this.motivoSuspensao = motivoSuspensao;
    }

}
