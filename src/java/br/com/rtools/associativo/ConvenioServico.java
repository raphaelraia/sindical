package br.com.rtools.associativo;

import br.com.rtools.financeiro.Servicos;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "soc_convenio_servico")
@NamedQuery(name = "ConvenioServico.pesquisaID", query = "select cs from ConvenioServico cs where cs.id=:pid")
public class ConvenioServico implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_convenio_sub_grupo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SubGrupoConvenio subGrupoConvenio;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servicos;
    @Column(name = "is_agendamento", columnDefinition = "boolean default false")
    private Boolean agendamento;

    public ConvenioServico() {
        this.id = -1;
        this.subGrupoConvenio = new SubGrupoConvenio();
        this.servicos = new Servicos();
        this.agendamento = false;
    }

    public ConvenioServico(int id, SubGrupoConvenio subGrupoConvenio, Servicos servicos, Boolean agendamento) {
        this.id = id;
        this.subGrupoConvenio = subGrupoConvenio;
        this.servicos = servicos;
        this.agendamento = agendamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubGrupoConvenio getSubGrupoConvenio() {
        return subGrupoConvenio;
    }

    public void setSubGrupoConvenio(SubGrupoConvenio subGrupoConvenio) {
        this.subGrupoConvenio = subGrupoConvenio;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Boolean getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Boolean agendamento) {
        this.agendamento = agendamento;
    }

}
