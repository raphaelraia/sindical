package br.com.rtools.associativo;

import br.com.rtools.utilitarios.Moeda;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "eve_evento_servico_valor")
@NamedQuery(name = "EventoServicoValor.pesquisaID", query = "select es from EventoServicoValor es where es.id=:pid")
public class EventoServicoValor implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_evento_servico", referencedColumnName = "id", nullable = false)
    @OneToOne
    private EventoServico eventoServico;
    @Column(name = "nr_valor", nullable = true)
    private double valor;
    @Column(name = "nr_idade_inicial", nullable = false)
    private int idadeInicial;
    @Column(name = "nr_idade_final", nullable = false)
    private int idadeFinal;
    @Column(name = "ds_sexo", nullable = true, length = 1)
    private String sexo;
    @Column(name = "nr_desconto_primeira_venda")
    private Double descontoPrimeiraVenda;

    public EventoServicoValor() {
        this.id = -1;
        this.eventoServico = new EventoServico();
        this.valor = 0;
        this.idadeInicial = 0;
        this.idadeFinal = 150;
        this.sexo = "A";
    }

    public EventoServicoValor(int id, EventoServico eventoServico, double valor, int idadeInicial, int idadeFinal, String sexo, Double descontoPrimeiraVenda) {
        this.id = id;
        this.eventoServico = eventoServico;
        this.valor = valor;
        this.idadeInicial = idadeInicial;
        this.idadeFinal = idadeFinal;
        this.sexo = sexo;
        this.descontoPrimeiraVenda = descontoPrimeiraVenda;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventoServico getEventoServico() {
        return eventoServico;
    }

    public void setEventoServico(EventoServico eventoServico) {
        this.eventoServico = eventoServico;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Double(valor);
    }

    public void setValorString(String valor) {
        this.valor = Moeda.substituiVirgulaDouble(valor);
    }

    public int getIdadeInicial() {
        return idadeInicial;
    }

    public void setIdadeInicial(int idadeInicial) {
        this.idadeInicial = idadeInicial;
    }

    public int getIdadeFinal() {
        return idadeFinal;
    }

    public void setIdadeFinal(int idadeFinal) {
        this.idadeFinal = idadeFinal;
    }

    public String getIdadeInicialString() {
        return Integer.toString(idadeInicial);
    }

    public void setIdadeInicialString(String idadeInicialString) {
        this.idadeInicial = Integer.parseInt(idadeInicialString);
    }

    public String getIdadeFinalString() {
        return Integer.toString(idadeFinal);
    }

    public void setIdadeFinalString(String idadeFinalString) {
        this.idadeFinal = Integer.parseInt(idadeFinalString);
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Double getDescontoPrimeiraVenda() {
        return descontoPrimeiraVenda;
    }

    public void setDescontoPrimeiraVenda(Double descontoPrimeiraVenda) {
        if (descontoPrimeiraVenda > 100) {
            descontoPrimeiraVenda = new Double(0);
        }
        this.descontoPrimeiraVenda = descontoPrimeiraVenda;
    }

    public String getDescontoPrimeiraVendaString() {
        return Moeda.converteR$Double(descontoPrimeiraVenda);
    }

    public void setDescontoPrimeiraVendaString(String descontoPrimeiraVendaString) {
        if (Moeda.substituiVirgulaDouble(descontoPrimeiraVendaString) > valor) {
            this.descontoPrimeiraVenda = new Double(0);
        } else {
            this.descontoPrimeiraVenda = Moeda.substituiVirgulaDouble(descontoPrimeiraVendaString);
        }
    }

}
