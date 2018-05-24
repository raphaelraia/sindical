package br.com.rtools.agendamentos;

import br.com.rtools.financeiro.Servicos;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ag_servico", uniqueConstraints = @UniqueConstraint(columnNames = {"id_servico", "is_web"}))
@NamedQueries({
    @NamedQuery(name = "AgendaServico.findAll", query = "SELECT ASE FROM AgendaServico AS ASE ORDER BY ASE.servico.descricao ASC ")
})
public class AgendaServico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servico;
//    @Column(name = "nr_qtde_horarios", columnDefinition = "integer default 0", nullable = false)
//    private Integer nrQtdeHorarios;
    @Column(name = "nr_minutos", columnDefinition = "integer default 0", nullable = false)
    private Integer nrMinutos;
    @Column(name = "is_web", columnDefinition = "boolean default true", nullable = false)
    private Boolean web;
    @Column(name = "is_encaixe", columnDefinition = "boolean default false", nullable = false)
    private Boolean encaixe;

    public AgendaServico() {
        this.id = null;
        this.servico = null;
        this.nrMinutos = 0;
        this.web = true;
        this.encaixe = false;
    }

    public AgendaServico(Integer id, Servicos servico, Integer nrMinutos, Boolean web, Boolean encaixe) {
        this.id = id;
        this.servico = servico;
        this.nrMinutos = nrMinutos;
        this.web = web;
        this.encaixe = encaixe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public Integer getNrMinutos() {
        return nrMinutos;
    }

    public void setNrMinutos(Integer nrMinutos) {
        this.nrMinutos = nrMinutos;
    }

    public Boolean getWeb() {
        return web;
    }

    public void setWeb(Boolean web) {
        this.web = web;
    }

    public Boolean getEncaixe() {
        return encaixe;
    }

    public void setEncaixe(Boolean encaixe) {
        this.encaixe = encaixe;
    }

}
