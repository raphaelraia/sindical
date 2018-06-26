package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arr_repis_movimento")
@NamedQuery(name = "RepisMovimento.pesquisaID", query = "select m from RepisMovimento m where m.id = :pid")
public class RepisMovimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao")
    private Date dataEmissao;
    @Column(name = "ds_solicitante", length = 100, nullable = true)
    private String contato;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_resposta")
    private Date dataResposta;
    @Column(name = "nr_ano", length = 4, nullable = false)
    private Integer ano;
    @JoinColumn(name = "id_repis_status", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private RepisStatus repisStatus;
    @JoinColumn(name = "id_patronal", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Patronal patronal;
    @JoinColumn(name = "id_certidao_tipo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CertidaoTipo certidaoTipo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_impressao")
    private Date dataImpressao;
    @Column(name = "nr_faturamento_bruto_anual", columnDefinition = "double precision 0")
    private Double faturamentoBrutoAnual;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inativacao")
    private Date dtInativacao;
    @JoinColumn(name = "id_operador_inativacao", referencedColumnName = "id")
    @ManyToOne
    private Pessoa operadorInativacao;
    @Column(name = "ds_retirado_por", length = 150)
    private String retiradoPor;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_retirada")
    private Date dtRetirada;
        
    public RepisMovimento() {
        this.id = -1;
        this.dataEmissao = DataHoje.dataHoje();
        this.contato = "";
        this.pessoa = new Pessoa();
        this.dataResposta = new Date();
        this.ano = 0;
        this.repisStatus = new RepisStatus();
        this.patronal = new Patronal();
        this.certidaoTipo = new CertidaoTipo();
        this.dataImpressao = null;
        this.faturamentoBrutoAnual = new Double(0);
        this.dtInativacao = null;
        this.operadorInativacao = null;
        this.retiradoPor = "";
        this.dtRetirada = null;
    }

    public RepisMovimento(Integer id, Date dataEmissao, String contato, Pessoa pessoa, Date dataResposta, Integer ano, RepisStatus repisStatus, Patronal patronal, CertidaoTipo certidaoTipo, Date dataImpressao, Double faturamentoBrutoAnual, Date dtInativacao, Pessoa operadorInativacao, String retiradoPor, Date dtRetirada) {
        this.id = id;
        this.dataEmissao = dataEmissao;
        this.contato = contato;
        this.pessoa = pessoa;
        this.dataResposta = dataResposta;
        this.ano = ano;
        this.repisStatus = repisStatus;
        this.patronal = patronal;
        this.certidaoTipo = certidaoTipo;
        this.dataImpressao = dataResposta;
        this.faturamentoBrutoAnual = faturamentoBrutoAnual;
        this.dtInativacao = dtInativacao;
        this.operadorInativacao = operadorInativacao;
        this.retiradoPor = retiradoPor;
        this.dtRetirada = dtRetirada;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataEmissaoString() {
        return DataHoje.converteData(dataEmissao);
    }

    public void setDataEmissaoString(String dataEmissaoString) {
        this.dataEmissao = DataHoje.converte(dataEmissaoString);
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }

    public String getDataRespostaString() {
        return DataHoje.converteData(dataResposta);
    }

    public void setDataRespostaString(String dataRespostaString) {
        this.dataResposta = DataHoje.converte(dataRespostaString);
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public RepisStatus getRepisStatus() {
        return repisStatus;
    }

    public void setRepisStatus(RepisStatus repisStatus) {
        this.repisStatus = repisStatus;
    }

    public Patronal getPatronal() {
        return patronal;
    }

    public void setPatronal(Patronal patronal) {
        this.patronal = patronal;
    }

    public CertidaoTipo getCertidaoTipo() {
        return certidaoTipo;
    }

    public void setCertidaoTipo(CertidaoTipo certidaoTipo) {
        this.certidaoTipo = certidaoTipo;
    }

    public Date getDataImpressao() {
        return dataImpressao;
    }

    public void setDataImpressao(Date dataImpressao) {
        this.dataImpressao = dataImpressao;
    }

    public String getDataImpressaoString() {
        return DataHoje.converteData(dataImpressao);
    }

    public void setDataImpressaoString(String dataImpressaoString) {
        this.dataImpressao = DataHoje.converte(dataImpressaoString);
    }

    public Double getFaturamentoBrutoAnual() {
        return faturamentoBrutoAnual;
    }

    public void setFaturamentoBrutoAnual(Double faturamentoBrutoAnual) {
        this.faturamentoBrutoAnual = faturamentoBrutoAnual;
    }

    public String getFaturamentoBrutoAnualString() {
        return Moeda.converteR$Double(faturamentoBrutoAnual);
    }

    public void setFaturamentoBrutoAnualString(String faturamentoBrutoAnualString) {
        this.faturamentoBrutoAnual = Moeda.converteUS$(faturamentoBrutoAnualString);
    }

    public Date getDtInativacao() {
        return dtInativacao;
    }

    public void setDtInativacao(Date dtInativacao) {
        this.dtInativacao = dtInativacao;
    }
    
    public String getDtInativacaoString() {
        return DataHoje.converteData(dtInativacao);
    }

    public void setDtInativacaoString(String dtInativacaoString) {
        this.dtInativacao = DataHoje.converte(dtInativacaoString);
    }
    
    public Pessoa getOperadorInativacao() {
        return operadorInativacao;
    }

    public void setOperadorInativacao(Pessoa operadorInativacao) {
        this.operadorInativacao = operadorInativacao;
    }

    public String getRetiradoPor() {
        return retiradoPor;
    }

    public void setRetiradoPor(String retiradoPor) {
        this.retiradoPor = retiradoPor;
    }

    public Date getDtRetirada() {
        return dtRetirada;
    }

    public void setDtRetirada(Date dtRetirada) {
        this.dtRetirada = dtRetirada;
    }
    
    public String getDtRetiradaString() {
        return DataHoje.converteData(dtRetirada);
    }

    public void setDtRetiradaString(String dtRetiradaString) {
        this.dtRetirada = DataHoje.converte(dtRetiradaString);
    }
}
