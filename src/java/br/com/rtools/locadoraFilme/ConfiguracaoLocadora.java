package br.com.rtools.locadoraFilme;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conf_locadora")
public class ConfiguracaoLocadora implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Servicos servicos;
    @Column(name = "ds_obs", nullable = false)
    private String obs;
    @Column(name = "nr_meses_lancamento", nullable = false)
    private Integer mesesLancamento;
    @Column(name = "nr_qt_relocacao", nullable = false)
    private Integer nrQtRelocacao;
    @Column(name = "nr_qt_relocacao_lancamento", nullable = false)
    private Integer nrQtRelocacaoLancamento;

    public ConfiguracaoLocadora() {
        this.id = -1;
        this.servicos = null;
        this.obs = "";
        this.mesesLancamento = 0;
        this.nrQtRelocacao = 0;
        this.nrQtRelocacaoLancamento = 0;
    }

    public ConfiguracaoLocadora(Integer id, Servicos servicos, String obs, Integer mesesLancamento, Integer nrQtRelocacao, Integer nrQtRelocacaoLancamento) {
        this.id = id;
        this.servicos = servicos;
        this.obs = obs;
        this.mesesLancamento = mesesLancamento;
        this.nrQtRelocacao = nrQtRelocacao;
        this.nrQtRelocacaoLancamento = nrQtRelocacaoLancamento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public static ConfiguracaoLocadora get() {
        return (ConfiguracaoLocadora) new Dao().find(new ConfiguracaoLocadora(), 1);
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "ConfiguracaoLocadora{" + "id=" + id + ", servicos=" + servicos + ", obs=" + obs + ", mesesLancamento=" + mesesLancamento + '}';
    }

    public Integer getMesesLancamento() {
        return mesesLancamento;
    }

    public void setMesesLancamento(Integer mesesLancamento) {
        this.mesesLancamento = mesesLancamento;
    }

    public String getMesesLancamentoString() {
        return Integer.toString(mesesLancamento);
    }

    public void setMesesLancamentoString(String mesesLancamentoString) {
        this.mesesLancamento = Integer.parseInt(mesesLancamentoString);
    }

    public Integer getNrQtRelocacaoLancamento() {
        return nrQtRelocacaoLancamento;
    }

    public void setNrQtRelocacaoLancamento(Integer ntQrRelocacaoLancamento) {
        this.nrQtRelocacaoLancamento = ntQrRelocacaoLancamento;
    }

    public Integer getNrQtRelocacao() {
        return nrQtRelocacao;
    }

    public void setNrQtRelocacao(Integer nrQtRelocacao) {
        this.nrQtRelocacao = nrQtRelocacao;
    }

}
