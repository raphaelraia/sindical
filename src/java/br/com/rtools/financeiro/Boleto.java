package br.com.rtools.financeiro;

import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "fin_boleto")
@NamedQuery(name = "Boleto.pesquisaID", query = "select b from Boleto b where b.id=:pid")
public class Boleto implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_conta_cobranca", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ContaCobranca contaCobranca;
    @Column(name = "nr_boleto")
    private long nrBoleto;
    @Column(name = "ds_boleto", length = 50)
    private String boletoComposto;
    @Column(name = "nr_ctr_boleto", length = 30)
    private String nrCtrBoleto;
    @Column(name = "is_ativo", columnDefinition = "boolean default true")
    private boolean ativo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento")
    private Date dtVencimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento_original")
    private Date dtVencimentoOriginal;
    @Column(name = "ds_mensagem", length = 1000)
    private String mensagem;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cobranca_registrada")
    private Date dtCobrancaRegistrada;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_registro_baixa")
    private Date dtRegistroBaixa;
    @JoinColumn(name = "id_status_retorno", referencedColumnName = "id")
    @ManyToOne
    private StatusRetorno statusRetorno;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_status_retorno")
    private Date dtStatusRetorno;

    public Boleto() {
        this.id = -1;
        this.contaCobranca = new ContaCobranca();
        this.nrBoleto = 0;
        this.boletoComposto = "";
        this.nrCtrBoleto = "";
        this.ativo = true;
        this.dtVencimento = null;
        this.dtVencimentoOriginal = null;
        this.mensagem = "";
        this.dtCobrancaRegistrada = null;
        this.dtRegistroBaixa = null;
        this.statusRetorno = null;
        this.dtStatusRetorno = null;
    }

    public Boleto(int id, ContaCobranca contaCobranca, int nrBoleto, String boletoComposto, String nrCtrBoleto, boolean ativo, String vencimento, String vencimentoOriginal, String mensagem, Date dtCobrancaRegistrada, Date dtRegistroBaixa, StatusRetorno statusRetorno, Date dtStatusRetorno) {
        this.id = id;
        this.contaCobranca = contaCobranca;
        this.nrBoleto = nrBoleto;
        this.boletoComposto = boletoComposto;
        this.nrCtrBoleto = nrCtrBoleto;
        this.ativo = ativo;
        this.dtVencimento = DataHoje.converte(vencimento);
        this.dtVencimentoOriginal = DataHoje.converte(vencimentoOriginal);
        this.mensagem = mensagem;
        this.dtCobrancaRegistrada = dtCobrancaRegistrada;
        this.dtRegistroBaixa = dtRegistroBaixa;
        this.statusRetorno = statusRetorno;
        this.dtStatusRetorno = dtStatusRetorno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContaCobranca getContaCobranca() {
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    public long getNrBoleto() {
        return nrBoleto;
    }

    public void setNrBoleto(long nrBoleto) {
        this.nrBoleto = nrBoleto;
    }

    public String getBoletoComposto() {
        return boletoComposto;
    }

    public void setBoletoComposto(String boletoComposto) {
        this.boletoComposto = boletoComposto;
    }

    public String getNrCtrBoleto() {
        return nrCtrBoleto;
    }

    public void setNrCtrBoleto(String nrCtrBoleto) {
        this.nrCtrBoleto = nrCtrBoleto;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public Date getDtVencimentoOriginal() {
        return dtVencimentoOriginal;
    }

    public void setDtVencimentoOriginal(Date dtVencimentoOriginal) {
        this.dtVencimentoOriginal = dtVencimentoOriginal;
    }

    public String getVencimentoOriginal() {
        return DataHoje.converteData(dtVencimentoOriginal);
    }

    public void setVencimentoOriginal(String vencimentoOriginal) {
        this.dtVencimentoOriginal = DataHoje.converte(vencimentoOriginal);
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getDtCobrancaRegistrada() {
        return dtCobrancaRegistrada;
    }

    public void setDtCobrancaRegistrada(Date dtCobrancaRegistrada) {
        this.dtCobrancaRegistrada = dtCobrancaRegistrada;
    }

    public String getDtCobrancaRegistradaString() {
        return DataHoje.converteData(dtCobrancaRegistrada);
    }

    public Date getDtRegistroBaixa() {
        return dtRegistroBaixa;
    }

    public void setDtRegistroBaixa(Date dtRegistroBaixa) {
        this.dtRegistroBaixa = dtRegistroBaixa;
    }

    public String getDtRegistroBaixaString() {
        return DataHoje.converteData(dtRegistroBaixa);
    }

    public StatusRetorno getStatusRetorno() {
        return statusRetorno;
    }

    public void setStatusRetorno(StatusRetorno statusRetorno) {
        this.statusRetorno = statusRetorno;
    }

    public Date getDtStatusRetorno() {
        return dtStatusRetorno;
    }

    public void setDtStatusRetorno(Date dtStatusRetorno) {
        this.dtStatusRetorno = dtStatusRetorno;
    }

    public String getDtStatusRetornoString() {
        return DataHoje.converteData(dtStatusRetorno);
    }

    public void setDtStatusRetornoString(String dtStatusRetornoString) {
        this.dtStatusRetorno = DataHoje.converte(dtStatusRetornoString);
    }

    public Pessoa getPessoa() {
        if (id != -1) {
            return new MovimentosReceberSocialDao().responsavelBoleto(nrCtrBoleto);
        } else {
            return new Pessoa();
        }
    }

    public List<Movimento> getListaMovimento() {
        if (id != -1) {
            return new MovimentosReceberSocialDao().listaMovimentosPorNrCtrBoleto(nrCtrBoleto);
        } else {
            return new ArrayList();
        }
    }

    public Double getValor() {
        if (id != -1) {
            List<Movimento> list = getListaMovimento();
            Double valor_somado = new Double(0);
            for (Movimento m : list){
                valor_somado = Moeda.soma(valor_somado, m.getValor());
            }
            return valor_somado;
        } else {
            return new Double(0);
        }
    }
    
    public String getValorString(){
        return Moeda.converteR$Double(getValor());
    }
}
