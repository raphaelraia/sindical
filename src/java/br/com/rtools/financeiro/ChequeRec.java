package br.com.rtools.financeiro;

import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_cheque_rec")
@NamedQuery(name = "ChequeRec.pesquisaID", query = "select cr from ChequeRec cr where cr.id=:pid")
public class ChequeRec implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_banco", referencedColumnName = "id")
    @ManyToOne
    private Banco banco;
    @Column(name = "ds_emitente", length = 100)
    private String emitente;
    @Column(name = "ds_agencia", length = 50)
    private String agencia;
    @Column(name = "ds_conta", length = 50)
    private String conta;
    @Column(name = "ds_cheque", length = 50)
    private String cheque;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dtEmissao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento")
    private Date dtVencimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_liquidacao")
    private Date dtLiquidacao;

    public ChequeRec() {
        this.id = -1;
        this.emitente = "";
        this.banco = new Banco();
        this.agencia = "";
        this.conta = "";
        this.cheque = "";
        this.dtEmissao = null;
        this.dtVencimento = null;
        this.dtLiquidacao = DataHoje.dataHoje();
    }

    public ChequeRec(int id, String emitente, Banco banco, String agencia, String conta, String cheque, Date dtEmissao, Date dtVencimento, Date dtLiquidacao) {
        this.id = id;
        this.emitente = emitente;
        this.banco = banco;
        this.agencia = agencia;
        this.conta = conta;
        this.cheque = cheque;
        this.dtEmissao = dtEmissao;
        this.dtVencimento = dtVencimento;
        this.dtLiquidacao = dtLiquidacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmitente() {
        return emitente;
    }

    public void setEmitente(String emitente) {
        this.emitente = emitente;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
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

    public String getEmissao() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setEmissao(String emissao) {
        this.dtEmissao = DataHoje.converte(emissao);
    }

    public Date getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(Date dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public String getVencimento() {
        return DataHoje.converteData(dtVencimento);
    }

    public void setVencimento(String vencimento) {
        this.dtVencimento = DataHoje.converte(vencimento);
    }

    public Date getDtLiquidacao() {
        return dtLiquidacao;
    }

    public void setDtLiquidacao(Date dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
    }
}
