package br.com.rtools.financeiro;

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

@Entity
@Table(name = "fin_cartao")
@NamedQueries({
    @NamedQuery(name = "Cartao.pesquisaID", query = "SELECT C FROM Cartao AS C WHERE C.id = :pid")
    ,
    @NamedQuery(name = "Cartao.findAll", query = "SELECT C FROM Cartao AS C ORDER BY C.plano5.conta ASC, C.descricao ASC, C.dias ASC")
})
public class Cartao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao")
    private String descricao;
    @Column(name = "nr_dias")
    private int dias;
    @Column(name = "nr_taxa")
    private double taxa;
    @Column(name = "ds_debito_credito")
    private String debitoCredito;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "id_plano5_baixa", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5Baixa;
    @JoinColumn(name = "id_plano5_despesa", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5Despesa;
    @Column(name = "is_ativo")
    private Boolean ativo;

    public Cartao() {
        this.id = -1;
        this.descricao = "";
        this.dias = 0;
        this.taxa = 0;
        this.debitoCredito = "";
        this.plano5 = new Plano5();
        this.plano5Baixa = new Plano5();
        this.plano5Despesa = new Plano5();
        this.ativo = true;
    }

    public Cartao(int id, String descricao, int dias, double taxa, String debitoCredito, Plano5 plano5, Plano5 plano5Baixa, Plano5 plano5Despesa, Boolean ativo) {
        this.id = id;
        this.descricao = descricao;
        this.dias = dias;
        this.taxa = taxa;
        this.debitoCredito = debitoCredito;
        this.plano5 = plano5;
        this.plano5Baixa = plano5Baixa;
        this.plano5Despesa = plano5Despesa;
        this.ativo = ativo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public double getTaxa() {
        return taxa;
    }

    public void setTaxa(double taxa) {
        this.taxa = taxa;
    }

    public String getDebitoCredito() {
        return debitoCredito;
    }

    public void setDebitoCredito(String debitoCredito) {
        this.debitoCredito = debitoCredito;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Plano5 getPlano5Baixa() {
        return plano5Baixa;
    }

    public void setPlano5Baixa(Plano5 plano5Baixa) {
        this.plano5Baixa = plano5Baixa;
    }

    public Plano5 getPlano5Despesa() {
        return plano5Despesa;
    }

    public void setPlano5Despesa(Plano5 plano5Despesa) {
        this.plano5Despesa = plano5Despesa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}
