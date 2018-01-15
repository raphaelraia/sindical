package br.com.rtools.agendamentos;

import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ag_horarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_convenio", "id_convenio_sub_grupo", "id_filial", "dt_data", "id_semana", "ds_hora", "is_web", "is_socio"})
)
public class AgendaHorarios implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_convenio", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa convenio;
    @JoinColumn(name = "id_convenio_sub_grupo", referencedColumnName = "id")
    @ManyToOne
    private SubGrupoConvenio subGrupoConvenio;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Filial filial;
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
    @Column(name = "nr_quantidade", nullable = false, columnDefinition = "integer default 1")
    private Integer quantidade;
    @Column(name = "ds_hora", length = 5, nullable = false)
    private String hora;
    @JoinColumn(name = "id_semana", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Semana semana;
    @Column(name = "is_web", columnDefinition = "boolean default true", nullable = false)
    private Boolean web;
    @Column(name = "is_socio", columnDefinition = "boolean default true", nullable = false)
    private Boolean socio;

    public AgendaHorarios() {
        this.id = null;
        this.convenio = null;
        this.subGrupoConvenio = null;
        this.filial = null;
        this.ativo = true;
        this.quantidade = 1;
        this.hora = "";
        this.semana = null;
        this.web = true;
        this.socio = false;
    }

    public AgendaHorarios(Integer id, Pessoa convenio, SubGrupoConvenio subGrupoConvenio, Filial filial, Boolean ativo, Integer quantidade, String hora, Semana semana, Boolean web, Boolean socio) {
        this.id = id;
        this.convenio = convenio;
        this.subGrupoConvenio = subGrupoConvenio;
        this.filial = filial;
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.hora = hora;
        this.semana = semana;
        this.web = web;
        this.socio = socio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getConvenio() {
        return convenio;
    }

    public void setConvenio(Pessoa convenio) {
        this.convenio = convenio;
    }

    public SubGrupoConvenio getSubGrupoConvenio() {
        return subGrupoConvenio;
    }

    public void setSubGrupoConvenio(SubGrupoConvenio subGrupoConvenio) {
        this.subGrupoConvenio = subGrupoConvenio;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Semana getSemana() {
        return semana;
    }

    public void setSemana(Semana semana) {
        this.semana = semana;
    }

    public Boolean getWeb() {
        return web;
    }

    public void setWeb(Boolean web) {
        this.web = web;
    }

    public Boolean getSocio() {
        return socio;
    }

    public void setSocio(Boolean socio) {
        this.socio = socio;
    }

    public String getStatus() {
        if (ativo) {
            return "** ATIVO **";
        } else {
            return "** INATIVO **";
        }
    }

}
