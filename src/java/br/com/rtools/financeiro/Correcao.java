package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name = "fin_correcao")
@NamedQueries({
    @NamedQuery(name = "Correcao.pesquisaID", query = "SELECT C From Correcao AS C WHERE C.id = :pid"),
    @NamedQuery(name = "Correcao.findAll", query = "SELECT C FROM Correcao AS C ORDER BY C.servicos.descricao ASC, C.indice.descricao ASC ")
})
public class Correcao implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "nr_multa_primeiro_mes", nullable = false)
    private double multaPriMes;
    @Column(name = "nr_multa_apartir_2mes", nullable = false)
    private double multaApartir2Mes;
    @Column(name = "nr_juros_pri_mes", nullable = false)
    private double jurosPriMes;
    @Column(name = "nr_juros_apartir_2mes", nullable = false)
    private double jurosApartir2Mes;
    @Column(name = "nr_multa_por_funcionario", nullable = false)
    private double multaPorFuncionario;
    @Column(name = "nr_juros_diarios", nullable = false)
    private double jurosDiarios;
    @JoinColumn(name = "id_indice", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Indice indice;
    @JoinColumn(name = "id_servicos", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Servicos servicos;
    @Column(name = "ds_ref_inicial", nullable = false)
    private String referenciaInicial;
    @Column(name = "ds_ref_final", nullable = false)
    private String referenciaFinal;

    public Correcao() {
        this.id = -1;
        this.multaPriMes = 0;
        this.multaApartir2Mes = 0;
        this.jurosPriMes = 0;
        this.jurosApartir2Mes = 0;
        this.multaPorFuncionario = 0;
        this.indice = new Indice();
        this.servicos = new Servicos();
        this.jurosDiarios = 0;
        this.referenciaInicial = "";
        this.referenciaFinal = "";
    }

    public Correcao(int id, double multaPriMes, double multaApartir2Mes, double jurosPriMes, double jurosApartir2Mes, double multaPorFuncionario, double jurosDiarios, Indice indice, Servicos servicos, String referenciaInicial, String referenciaFinal) {
        this.id = id;
        this.multaPriMes = multaPriMes;
        this.multaApartir2Mes = multaApartir2Mes;
        this.jurosPriMes = jurosPriMes;
        this.jurosApartir2Mes = jurosApartir2Mes;
        this.multaPorFuncionario = multaPorFuncionario;
        this.jurosDiarios = jurosDiarios;
        this.indice = indice;
        this.servicos = servicos;
        this.referenciaInicial = referenciaInicial;
        this.referenciaFinal = referenciaFinal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMultaPriMes() {
        return multaPriMes;
    }

    public void setMultaPriMes(double multaPriMes) {
        this.multaPriMes = multaPriMes;
    }

    public double getMultaApartir2Mes() {
        return multaApartir2Mes;
    }

    public void setMultaApartir2Mes(double multaApartir2Mes) {
        this.multaApartir2Mes = multaApartir2Mes;
    }

    public double getJurosPriMes() {
        return jurosPriMes;
    }

    public void setJurosPriMes(double jurosPriMes) {
        this.jurosPriMes = jurosPriMes;
    }

    public double getJurosApartir2Mes() {
        return jurosApartir2Mes;
    }

    public void setJurosApartir2Mes(double jurosApartir2Mes) {
        this.jurosApartir2Mes = jurosApartir2Mes;
    }

    public double getMultaPorFuncionario() {
        return multaPorFuncionario;
    }

    public void setMultaPorFuncionario(double multaPorFuncionario) {
        this.multaPorFuncionario = multaPorFuncionario;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public double getJurosDiarios() {
        return jurosDiarios;
    }

    public void setJurosDiarios(double jurosDiarios) {
        this.jurosDiarios = jurosDiarios;
    }

    public Indice getIndice() {
        return indice;
    }

    public void setIndice(Indice indice) {
        this.indice = indice;
    }

    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }
}
