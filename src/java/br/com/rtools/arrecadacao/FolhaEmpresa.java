package br.com.rtools.arrecadacao;

import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "arr_faturamento_folha_empresa")
@NamedQueries({
    @NamedQuery(name = "FolhaEmpresa.pesquisaID", query = "SELECT FE FROM FolhaEmpresa AS FE WHERE FE.id = :pid")
    ,
    @NamedQuery(name = "FolhaEmpresa.findAll", query = "SELECT FE FROM FolhaEmpresa AS FE "),})
public class FolhaEmpresa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Juridica juridica;
    @JoinColumn(name = "id_tipo_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TipoServico tipoServico;
    @Column(name = "ds_referencia", length = 7, nullable = true)
    private String referencia;
    @Column(name = "nr_valor", nullable = true)
    private double valorMes;
    @Column(name = "nr_num_funcionarios", nullable = true)
    private int numFuncionarios;
    @Column(name = "nr_alteracoes", nullable = false)
    private int alteracoes;
    @Column(name = "dt_lancamento")
    @Temporal(TemporalType.DATE)
    private Date dtLancamento;

    @Transient
    private Double valorFolha;
    @Transient
    private Double valorBoleto;

    public FolhaEmpresa() {
        this.id = -1;
        this.juridica = new Juridica();
        this.tipoServico = new TipoServico();
        this.referencia = "";
        this.valorMes = 0;
        this.numFuncionarios = 0;
        this.alteracoes = 1;
        this.dtLancamento = new Date();
        this.valorFolha = null;
        this.valorBoleto = null;
    }

    public FolhaEmpresa(int id, Juridica juridica, TipoServico tipoServico, String referencia, double valorMes, int numFuncionarios, int alteracoes, Date dtLancamento) {
        this.id = id;
        this.juridica = juridica;
        this.tipoServico = tipoServico;
        this.referencia = referencia;
        this.valorMes = valorMes;
        this.numFuncionarios = numFuncionarios;
        this.alteracoes = alteracoes;
        this.dtLancamento = dtLancamento;
        this.valorFolha = null;
        this.valorBoleto = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public double getValorMes() {
        return valorMes;
    }

    public void setValorMes(double valorMes) {
        this.valorMes = valorMes;
    }

    public String getValorMesString() {
        return Moeda.converteR$Float(valorMes);
    }

    public void setValorMesString(String valorMesString) {
        this.valorMes = Moeda.converteUS$(valorMesString);
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public int getNumFuncionarios() {
        return numFuncionarios;
    }

    public void setNumFuncionarios(int numFuncionarios) {
        this.numFuncionarios = numFuncionarios;
    }

    public String getNumFuncionariosString() {
        return numFuncionarios + "";
    }

    public void setNumFuncionariosString(String numFuncionariosString) {
        this.numFuncionarios = Integer.parseInt(numFuncionariosString);
    }

    public int getAlteracoes() {
        return alteracoes;
    }

    public void setAlteracoes(int alteracoes) {
        this.alteracoes = alteracoes;
    }

    public String getAlteracoesString() {
        return alteracoes + "";
    }

    public void setAlteracoesString(String alteracoesString) {
        this.alteracoes = Integer.parseInt(alteracoesString);
    }

    public Date getDtLancamento() {
        return dtLancamento;
    }

    public void setDtLancamento(Date dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public String getLancamento() {
        return DataHoje.converteData(dtLancamento);
    }

    public void setLancamento(String lancamento) {
        this.dtLancamento = DataHoje.converte(lancamento);
    }

}
