package br.com.rtools.associativo;

import br.com.rtools.financeiro.Movimento;
import java.io.Serializable;
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
@Table(name = "eve_evento_baile_mapa")
@NamedQuery(name = "EventoBaileMapa.pesquisaID", query = "select ebm from EventoBaileMapa ebm where ebm.id = :pid")
public class EventoBaileMapa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_evento_baile", referencedColumnName = "id") 
    @ManyToOne
    private EventoBaile eventoBaile;
    @Column(name = "nr_mesa")
    private int mesa;
    @Column(name = "ds_posicao")
    private String posicao;
    @JoinColumn(name = "id_venda", referencedColumnName = "id")
    @ManyToOne
    private BVenda bVenda;  
    @JoinColumn(name = "id_movimento", referencedColumnName = "id")
    @ManyToOne
    private Movimento movimento;    

    public EventoBaileMapa() {
        this.id = -1;
        this.eventoBaile = new EventoBaile();
        this.mesa = 0;
        this.posicao = "";
        this.bVenda = new BVenda();
        this.movimento = null;
    }
    
    public EventoBaileMapa(int id, EventoBaile eventoBaile, int mesa, String posicao, BVenda bVenda, Movimento movimento) {
        this.id = id;
        this.eventoBaile = eventoBaile;
        this.mesa = mesa;
        this.posicao = posicao;
        this.bVenda = bVenda;
        this.movimento = movimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventoBaile getEventoBaile() {
        return eventoBaile;
    }

    public void setEventoBaile(EventoBaile eventoBaile) {
        this.eventoBaile = eventoBaile;
    }

    public int getMesa() {
        return mesa;
    }

    public void setMesa(int mesa) {
        this.mesa = mesa;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }    
    
    public BVenda getbVenda() {
        return bVenda;
    }

    public void setbVenda(BVenda bVenda) {
        this.bVenda = bVenda;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }
}
