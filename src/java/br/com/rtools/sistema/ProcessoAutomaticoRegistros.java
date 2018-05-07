package br.com.rtools.sistema;

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

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "sis_processo_automatico_registros")
public class ProcessoAutomaticoRegistros implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_processo_automatico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ProcessoAutomatico processoAutomatico;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_solicitacao", nullable = false)
    private Date dtSolicitacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_conclusao")
    private Date dtConclusao;
    @Column(name = "ds_tabela", length = 255, nullable = false)
    private String tabela;
    @Column(name = "ds_coluna", length = 255, nullable = false)
    private String coluna;
    @Column(name = "nr_codigo", nullable = false)
    private Integer codigo;

    public ProcessoAutomaticoRegistros() {
        this.id = null;
        this.processoAutomatico = null;
        this.dtSolicitacao = new Date();
        this.dtConclusao = null;
        this.tabela = null;
        this.coluna = null;
        this.codigo = null;
    }

    public ProcessoAutomaticoRegistros(Integer id, ProcessoAutomatico processoAutomatico, Date dtSolicitacao, Date dtConclusao, String tabela, String coluna, Integer codigo) {
        this.id = id;
        this.processoAutomatico = processoAutomatico;
        this.dtSolicitacao = dtSolicitacao;
        this.dtConclusao = dtConclusao;
        this.tabela = tabela;
        this.coluna = coluna;
        this.codigo = codigo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProcessoAutomatico getProcessoAutomatico() {
        return processoAutomatico;
    }

    public void setProcessoAutomatico(ProcessoAutomatico processoAutomatico) {
        this.processoAutomatico = processoAutomatico;
    }

    public Date getDtSolicitacao() {
        return dtSolicitacao;
    }

    public void setDtSolicitacao(Date dtSolicitacao) {
        this.dtSolicitacao = dtSolicitacao;
    }

    public Date getDtConclusao() {
        return dtConclusao;
    }

    public void setDtConclusao(Date dtConclusao) {
        this.dtConclusao = dtConclusao;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

}
