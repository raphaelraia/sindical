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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "eve_evento_servico")
@NamedQuery(name = "EventoServico.pesquisaID", query = "select es from EventoServico es where es.id=:pid")
public class EventoServico implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_evento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private AEvento evento;
    @JoinColumn(name = "id_servicos", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Servicos servicos;
    @Column(name = "is_mesa", nullable = true)
    private boolean mesa;
    @Column(name = "is_individual", nullable = false)
    private boolean individual;
    @Column(name = "ds_descricao")
    private String descricao;
    @JoinColumn(name = "id_categoria", referencedColumnName = "id")
    @OneToOne
    private Categoria categoria;
    @Column(name = "is_socio", nullable = false, columnDefinition = "DEFAULT false")
    private boolean socio;

    public EventoServico() {
        this.id = -1;
        this.evento = new AEvento();
        this.servicos = new Servicos();
        this.mesa = true;
        this.individual = true;
        this.descricao = "";
        this.categoria = null;
        this.socio = false;
    }

    public EventoServico(int id, AEvento evento, Servicos servicos, boolean isMesa, boolean isIndividual, String descricao, Categoria categoria, boolean socio) {
        this.id = id;
        this.evento = evento;
        this.servicos = servicos;
        this.mesa = isMesa;
        this.individual = isIndividual;
        this.descricao = descricao;
        this.categoria = categoria;
        this.socio = socio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public boolean isMesa() {
        return mesa;
    }

    public void setMesa(boolean mesa) {
        this.mesa = mesa;
    }

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isSocio() {
        return socio;
    }

    public void setSocio(boolean socio) {
        this.socio = socio;
    }
}
