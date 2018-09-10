package br.com.rtools.arrecadacao;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.pessoa.Filial;
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
@Table(name = "conf_arrecadacao")
public class ConfiguracaoArrecadacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Filial filial;
    @Column(name = "is_certificado_faturamento_bruto_anual", columnDefinition = "boolean default false", nullable = false)
    private Boolean certificadoFaturementoBrutoAnual;
    @Column(name = "nr_dias_acordo", nullable = false)
    private Integer nrDiasAcordo;
    @Column(name = "is_bloqueia_oposicao", columnDefinition = "boolean default true", nullable = false)
    private Boolean bloqueiaOposição;
    @Column(name = "is_upload_certificado", columnDefinition = "boolean default false", nullable = false)
    private Boolean uploadCertificado;
    @Column(name = "is_visualiza_certificado_recusado", columnDefinition = "boolean default false", nullable = false)
    private Boolean visualizaCertificadoRecusado;
    @Column(name = "ds_comunicado", length = 8000)
    private String comunicado;
    @Column(name = "is_cobranca_email", columnDefinition = "boolean default true", nullable = false)
    private Boolean cobrancaEmail;
    @Column(name = "is_ignora_periodo_convencao_oposicao", columnDefinition = "boolean default false", nullable = false)
    private Boolean ignoraPeriodoConvencaoOposicao;
    @Column(name = "is_comunicado_html", length = 8000)
    private Boolean comunicadoHtml;

    public ConfiguracaoArrecadacao() {
        this.id = null;
        this.filial = new Filial();
        this.certificadoFaturementoBrutoAnual = false;
        this.nrDiasAcordo = 0;
        this.bloqueiaOposição = false;
        this.uploadCertificado = false;
        this.visualizaCertificadoRecusado = false;
        this.comunicado = "";
        this.cobrancaEmail = false;
        this.ignoraPeriodoConvencaoOposicao = false;
        this.comunicadoHtml = false;
    }

    public ConfiguracaoArrecadacao(Integer id, Filial filial, Boolean certificadoFaturementoBrutoAnual, Integer nrDiasAcordo, Boolean bloqueiaOposição, Boolean uploadCertificado, Boolean visualizaCertificadoRecusado, String comunicado, Boolean cobrancaEmail, Boolean ignoraPeriodoConvencaoOposicao, Boolean comunicadoHtml) {
        this.id = id;
        this.filial = filial;
        this.certificadoFaturementoBrutoAnual = certificadoFaturementoBrutoAnual;
        this.nrDiasAcordo = nrDiasAcordo;
        this.bloqueiaOposição = bloqueiaOposição;
        this.uploadCertificado = uploadCertificado;
        this.visualizaCertificadoRecusado = visualizaCertificadoRecusado;
        this.comunicado = comunicado;
        this.cobrancaEmail = cobrancaEmail;
        this.ignoraPeriodoConvencaoOposicao = ignoraPeriodoConvencaoOposicao;
        this.comunicadoHtml = comunicadoHtml;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getCertificadoFaturementoBrutoAnual() {
        return certificadoFaturementoBrutoAnual;
    }

    public void setCertificadoFaturementoBrutoAnual(Boolean certificadoFaturementoBrutoAnual) {
        this.certificadoFaturementoBrutoAnual = certificadoFaturementoBrutoAnual;
    }

    public Integer getNrDiasAcordo() {
        return nrDiasAcordo;
    }

    public void setNrDiasAcordo(Integer nrDiasAcordo) {
        this.nrDiasAcordo = nrDiasAcordo;
    }

    public Boolean getBloqueiaOposição() {
        return bloqueiaOposição;
    }

    public void setBloqueiaOposição(Boolean bloqueiaOposição) {
        this.bloqueiaOposição = bloqueiaOposição;
    }

    public Boolean getUploadCertificado() {
        return uploadCertificado;
    }

    public void setUploadCertificado(Boolean uploadCertificado) {
        this.uploadCertificado = uploadCertificado;
    }

    public Boolean getVisualizaCertificadoRecusado() {
        return visualizaCertificadoRecusado;
    }

    public void setVisualizaCertificadoRecusado(Boolean visualizaCertificadoRecusado) {
        this.visualizaCertificadoRecusado = visualizaCertificadoRecusado;
    }

    public static ConfiguracaoArrecadacao get() {
        ConfiguracaoArrecadacao ca = (ConfiguracaoArrecadacao) new Dao().find(new ConfiguracaoArrecadacao(), 1);
        if (ca == null) {
            new ConfiguracaoArrecadacaoBean().load(false);
            ca = (ConfiguracaoArrecadacao) new Dao().find(new ConfiguracaoArrecadacao(), 1);
        }
        return ca;
    }

    public String getComunicado() {
        return comunicado;
    }

    public void setComunicado(String comunicado) {
        this.comunicado = comunicado;
    }

    public Boolean getCobrancaEmail() {
        return cobrancaEmail;
    }

    public void setCobrancaEmail(Boolean cobrancaEmail) {
        this.cobrancaEmail = cobrancaEmail;
    }

    public Boolean getIgnoraPeriodoConvencaoOposicao() {
        return ignoraPeriodoConvencaoOposicao;
    }

    public void setIgnoraPeriodoConvencaoOposicao(Boolean ignoraPeriodoConvencaoOposicao) {
        this.ignoraPeriodoConvencaoOposicao = ignoraPeriodoConvencaoOposicao;
    }

    public Boolean getComunicadoHtml() {
        return comunicadoHtml;
    }

    public void setComunicadoHtml(Boolean comunicadoHtml) {
        this.comunicadoHtml = comunicadoHtml;
    }

}
