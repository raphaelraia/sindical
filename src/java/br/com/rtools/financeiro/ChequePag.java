package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_cheque_pag")
@NamedQuery(name = "ChequePag.pesquisaID", query = "select cp from ChequePag cp where cp.id=:pid")
public class ChequePag implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_cheque", length = 50)
    private String cheque;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento")
    private Date dtVencimento;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id")
    @ManyToOne
    private Plano5 plano5;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cancelamento")
    private Date dtCancelamento;
    @JoinColumn(name = "id_operador_impressao", referencedColumnName = "id")
    @ManyToOne
    private Usuario operadorImpressao;
    @JoinColumn(name = "id_operador_cancelamento", referencedColumnName = "id")
    @ManyToOne
    private Usuario operadorCancelamento;

    public ChequePag() {
        this.id = -1;
        this.cheque = "";
        this.dtEmissao = DataHoje.dataHoje();
        this.dtVencimento = null;
        this.plano5 = new Plano5();
        this.dtCancelamento = null;
        this.operadorImpressao = null;
        this.operadorCancelamento = null;
    }

    public ChequePag(int id, String cheque, Date dtEmissao, Date dtVencimento, Plano5 plano5, Date dtCancelamento, Usuario operadorImpressao, Usuario operadorCancelamento) {
        this.id = id;
        this.cheque = cheque;
        this.dtEmissao = dtEmissao;
        this.dtVencimento = dtVencimento;
        this.plano5 = plano5;
        this.dtCancelamento = dtCancelamento;
        this.operadorImpressao = operadorImpressao;
        this.operadorCancelamento = operadorCancelamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getDtEmissaoString() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setDtEmissaoString(String emissaoString) {
        this.dtEmissao = DataHoje.converte(emissaoString);
    }

    public Date getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(Date dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public String getDtVencimentoString() {
        return DataHoje.converteData(dtVencimento);
    }

    public void setDtVencimentoString(String vencimentoString) {
        this.dtVencimento = DataHoje.converte(vencimentoString);
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Date getDtCancelamento() {
        return dtCancelamento;
    }

    public void setDtCancelamento(Date dtCancelamento) {
        this.dtCancelamento = dtCancelamento;
    }
    
    public String getDtCancelamentoString() {
        return DataHoje.converteData(dtCancelamento);
    }

    public void setDtCancelamentoString(String cancelamentoString) {
        this.dtCancelamento = DataHoje.converte(cancelamentoString);
    }    

    public Usuario getOperadorImpressao() {
        return operadorImpressao;
    }

    public void setOperadorImpressao(Usuario operadorImpressao) {
        this.operadorImpressao = operadorImpressao;
    }

    public Usuario getOperadorCancelamento() {
        return operadorCancelamento;
    }

    public void setOperadorCancelamento(Usuario operadorCancelamento) {
        this.operadorCancelamento = operadorCancelamento;
    }

}
