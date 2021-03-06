package br.com.rtools.homologacao;

import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "conf_homologacao")
public class ConfiguracaoHomologacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // PARÂMETROS
    @Column(name = "dt_habilita_correcao")
    @Temporal(TemporalType.DATE)
    private Date homolocaoHabilitaCorrecao;
    @Column(name = "nr_tempo_refresh_agendamento")
    private Integer tempoRefreshAgendamento;
    @Column(name = "nr_tempo_refresh_web_agendamento")
    private Integer tempoRefreshWebAgendamento;
    @Column(name = "nr_tempo_refresh_recepcao")
    private Integer tempoRefreshRecepcao;
    @Column(name = "nr_tempo_refresh_homologacao")
    private Integer tempoRefreshHomologacao;
    @Column(name = "nr_tempo_refresh_atendimento")
    private Integer tempoRefreshAtendimento;
    // LIMITE DE MESES PARA AGENDAMENTO - > FUTURO
    @Column(name = "nr_limite_meses", columnDefinition = "integer default 3")
    private Integer limiteMeses;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_limite_agendamento_retroativo")
    private Date limiteAgendamentoRetroativo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_atualiza_homologacao")
    private Date dataAtualizaHomologacao;

    // CAMPOS OBRIGATÓRIOS AGENDAMENTO
    @Column(name = "is_valida_cpf", columnDefinition = "boolean default true")
    private Boolean validaCpf;
    @Column(name = "is_valida_nome", columnDefinition = "boolean default true")
    private Boolean validaNome;
    @Column(name = "is_valida_endereco", columnDefinition = "boolean default true")
    private Boolean validaEndereco;
    @Column(name = "is_valida_carteira", columnDefinition = "boolean default false")
    private Boolean validaCarteira;
    @Column(name = "is_valida_serie", columnDefinition = "boolean default false")
    private Boolean validaSerie;
    @Column(name = "is_valida_funcao", columnDefinition = "boolean default false")
    private Boolean validaFuncao;
    @Column(name = "is_valida_admissao", columnDefinition = "boolean default true")
    private Boolean validaAdmissao;
    @Column(name = "is_valida_demissao", columnDefinition = "boolean default true")
    private Boolean validaDemissao;
    @Column(name = "is_valida_contato", columnDefinition = "boolean default false")
    private Boolean validaContato;
    @Column(name = "is_valida_email", columnDefinition = "boolean default false")
    private Boolean validaEmail;
    @Column(name = "is_valida_telefone", columnDefinition = "boolean default false")
    private Boolean validaTelefone;
    @Column(name = "is_data_nascimento", columnDefinition = "boolean default false")
    private Boolean validaDataNascimento;

// CAMPOS OBRIGATÓRIOS WEB AGENDAMENTO
    @Column(name = "is_web_valida_cpf", columnDefinition = "boolean default true")
    private Boolean webValidaCpf;
    @Column(name = "is_web_valida_nome", columnDefinition = "boolean default true")
    private Boolean webValidaNome;
    @Column(name = "is_web_valida_endereco", columnDefinition = "boolean default true")
    private Boolean webValidaEndereco;
    @Column(name = "is_web_valida_carteira", columnDefinition = "boolean default false")
    private Boolean webValidaCarteira;
    @Column(name = "is_web_valida_serie", columnDefinition = "boolean default false")
    private Boolean webValidaSerie;
    @Column(name = "is_web_valida_funcao", columnDefinition = "boolean default false")
    private Boolean webValidaFuncao;
    @Column(name = "is_web_valida_admissao", columnDefinition = "boolean default true")
    private Boolean webValidaAdmissao;
    @Column(name = "is_web_valida_demissao", columnDefinition = "boolean default true")
    private Boolean webValidaDemissao;
    @Column(name = "is_web_valida_contato", columnDefinition = "boolean default false")
    private Boolean webValidaContato;
    @Column(name = "is_web_valida_email", columnDefinition = "boolean default false")
    private Boolean webValidaEmail;
    @Column(name = "is_web_valida_telefone", columnDefinition = "boolean default false")
    private Boolean webValidaTelefone;
    @Column(name = "is_web_data_nascimento", columnDefinition = "boolean default false")
    private Boolean webValidaDataNascimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_web_desabilita_inicial")
    private Date webDesabilitaInicial;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_web_desabilita_final")
    private Date webDesabilitaFinal;
    @Column(name = "web_desabilita_obs", length = 1000)
    private String webDesabilitaObs;

    @Column(name = "nr_inicio_dias_agendamento", columnDefinition = "integer default 0", nullable = false)
    private Integer inicioDiasAgendamento;

    @Column(name = "is_web_imprimir_planilha_debito", columnDefinition = "boolean default false")
    private Boolean webImprimirPlanilhaDebito;

    @Column(name = "is_imprime_senha_matricial", columnDefinition = "boolean default false", nullable = false)
    private Boolean imprimeSenhaMatricial;

    @Column(name = "is_agendar_mesmo_horario_empresa", columnDefinition = "boolean default false", nullable = false)
    private Boolean agendarMesmoHorarioEmpresa;

    @Column(name = "is_web_agendar_mesmo_horario_empresa", columnDefinition = "boolean default false", nullable = false)
    private Boolean webAgendarMesmoHorarioEmpresa;

    @Column(name = "is_web_valida_agendamento", columnDefinition = "boolean default false", nullable = false)
    private Boolean webValidaAgendamento;

    @Column(name = "is_web_documento_obrigatorio", columnDefinition = "boolean default false", nullable = false)
    private Boolean webDocumentoObrigatorio;

    @Lob
    @Column(name = "ds_web_documentos_homologacao")
    private String webDocumentosHomologacao;

//    @Transient
//    // @Temporal(TemporalType.DATE)
//    // @Column(name = "dt_atualiza")
//    private Date dtAtualiza;
//    @Transient
//    // @Column(name = "dt_habilita_correcao")
//    // @Temporal(TemporalType.DATE)
//    private Date dtHabilitaCorrecao;
//    // @Column(name = "is_agendamento_web", columnDefinition = "boolean default false")
//    private Boolean agendamentoWeb;
//    @Transient
//    // @Column(name = "is_senha", columnDefinition = "boolean default false")
//    private Boolean habilitaSenha;
//    @Transient
//    // @Column(name = "ds_documentos", length = 8000)
//    private String documentos;
//    @Transient
//    // @Column(name = "ds_forma_pagamento", length = 8000)
//    private String formaPagamento;
//    @Transient
//    // @Column(name = "is_agendar_sem_horario_web", columnDefinition = "boolean default false")
//    private Boolean agendarSemHorarioWeb;
//    @Transient
//    // @Column(name = "is_bloquear_homologacao", columnDefinition = "boolean default false")
//    private Boolean bloquearHomologacao;
//    @Transient
//    // @Column(name = "nr_meses_inadimplentes")
//    private Integer mesesInadimplentesAgenda;
    public ConfiguracaoHomologacao() {
        this.id = null;
        this.homolocaoHabilitaCorrecao = null;
        this.tempoRefreshAgendamento = 5;
        this.tempoRefreshWebAgendamento = 5;
        this.tempoRefreshRecepcao = 5;
        this.tempoRefreshHomologacao = 5;
        this.tempoRefreshAtendimento = 5;
        this.limiteMeses = 3;
        this.limiteAgendamentoRetroativo = null;
        this.dataAtualizaHomologacao = null;
        this.validaCpf = false;
        this.validaNome = false;
        this.validaEndereco = false;
        this.validaCarteira = false;
        this.validaSerie = false;
        this.validaFuncao = false;
        this.validaAdmissao = false;
        this.validaDemissao = false;
        this.validaContato = false;
        this.validaEmail = false;
        this.validaTelefone = false;
        this.validaDataNascimento = false;
        this.webValidaCpf = false;
        this.webValidaNome = false;
        this.webValidaEndereco = false;
        this.webValidaCarteira = false;
        this.webValidaSerie = false;
        this.webValidaFuncao = false;
        this.webValidaAdmissao = false;
        this.webValidaDemissao = false;
        this.webValidaContato = false;
        this.webValidaEmail = false;
        this.webValidaTelefone = false;
        this.webValidaDataNascimento = false;
        this.webDesabilitaInicial = null;
        this.webDesabilitaFinal = null;
        this.webDesabilitaObs = "";
        this.inicioDiasAgendamento = 0;
        this.webImprimirPlanilhaDebito = false;
        this.imprimeSenhaMatricial = false;
        this.agendarMesmoHorarioEmpresa = false;
        this.webAgendarMesmoHorarioEmpresa = false;
        this.webDocumentosHomologacao = "";
//        this.dtAtualiza = null;
//        this.dtHabilitaCorrecao = null;
//        this.agendamentoWeb = false;
//        this.habilitaSenha = false;
//        this.documentos = "";
//        this.formaPagamento = "";
//        this.agendarSemHorarioWeb = false;
//        this.bloquearHomologacao = false;
//        this.mesesInadimplentesAgenda = 0;
    }

    public ConfiguracaoHomologacao(Integer id, Date homolocaoHabilitaCorrecao, Integer tempoRefreshAgendamento, Integer tempoRefreshWebAgendamento, Integer tempoRefreshRecepcao, Integer tempoRefreshHomologacao, Integer tempoRefreshAtendimento, Integer limiteMeses, Date limiteAgendamentoRetroativo, Date dataAtualizaHomologacao, Boolean validaCpf, Boolean validaNome, Boolean validaEndereco, Boolean validaCarteira, Boolean validaSerie, Boolean validaFuncao, Boolean validaAdmissao, Boolean validaDemissao, Boolean validaContato, Boolean validaEmail, Boolean validaTelefone, Boolean validaDataNascimento, Boolean webValidaCpf, Boolean webValidaNome, Boolean webValidaEndereco, Boolean webValidaCarteira, Boolean webValidaSerie, Boolean webValidaFuncao, Boolean webValidaAdmissao, Boolean webValidaDemissao, Boolean webValidaContato, Boolean webValidaEmail, Boolean webValidaTelefone, Boolean webValidaDataNascimento, Date webDesabilitaInicial, Date webDesabilitaFinal, String webDesabilitaObs, Integer inicioDiasAgendamento, Boolean webImprimirPlanilhaDebito, Boolean imprimeSenhaMatricial, Boolean agendarMesmoHorarioEmpresa, Boolean webAgendarMesmoHorarioEmpresa, String webDocumentosHomologacao) {
        this.id = id;
        this.homolocaoHabilitaCorrecao = homolocaoHabilitaCorrecao;
        this.tempoRefreshAgendamento = tempoRefreshAgendamento;
        this.tempoRefreshWebAgendamento = tempoRefreshWebAgendamento;
        this.tempoRefreshRecepcao = tempoRefreshRecepcao;
        this.tempoRefreshHomologacao = tempoRefreshHomologacao;
        this.tempoRefreshAtendimento = tempoRefreshAtendimento;
        this.limiteMeses = limiteMeses;
        this.limiteAgendamentoRetroativo = limiteAgendamentoRetroativo;
        this.dataAtualizaHomologacao = dataAtualizaHomologacao;
        this.validaCpf = validaCpf;
        this.validaNome = validaNome;
        this.validaEndereco = validaEndereco;
        this.validaCarteira = validaCarteira;
        this.validaSerie = validaSerie;
        this.validaFuncao = validaFuncao;
        this.validaAdmissao = validaAdmissao;
        this.validaDemissao = validaDemissao;
        this.validaContato = validaContato;
        this.validaEmail = validaEmail;
        this.validaTelefone = validaTelefone;
        this.validaDataNascimento = validaDataNascimento;
        this.webValidaCpf = webValidaCpf;
        this.webValidaNome = webValidaNome;
        this.webValidaEndereco = webValidaEndereco;
        this.webValidaCarteira = webValidaCarteira;
        this.webValidaSerie = webValidaSerie;
        this.webValidaFuncao = webValidaFuncao;
        this.webValidaAdmissao = webValidaAdmissao;
        this.webValidaDemissao = webValidaDemissao;
        this.webValidaContato = webValidaContato;
        this.webValidaEmail = webValidaEmail;
        this.webValidaTelefone = webValidaTelefone;
        this.webValidaDataNascimento = webValidaDataNascimento;
        this.webDesabilitaInicial = webDesabilitaInicial;
        this.webDesabilitaFinal = webDesabilitaFinal;
        this.webDesabilitaObs = webDesabilitaObs;
        this.inicioDiasAgendamento = inicioDiasAgendamento;
        this.webImprimirPlanilhaDebito = webImprimirPlanilhaDebito;
        this.imprimeSenhaMatricial = imprimeSenhaMatricial;
        this.agendarMesmoHorarioEmpresa = agendarMesmoHorarioEmpresa;
        this.webAgendarMesmoHorarioEmpresa = webAgendarMesmoHorarioEmpresa;
        this.webDocumentosHomologacao = webDocumentosHomologacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getHomolocaoHabilitaCorrecao() {
        return homolocaoHabilitaCorrecao;
    }

    public void setHomolocaoHabilitaCorrecao(Date homolocaoHabilitaCorrecao) {
        this.homolocaoHabilitaCorrecao = homolocaoHabilitaCorrecao;
    }

    public Integer getTempoRefreshAgendamento() {
        return tempoRefreshAgendamento;
    }

    public void setTempoRefreshAgendamento(Integer tempoRefreshAgendamento) {
        this.tempoRefreshAgendamento = tempoRefreshAgendamento;
    }

    public Integer getTempoRefreshWebAgendamento() {
        return tempoRefreshWebAgendamento;
    }

    public void setTempoRefreshWebAgendamento(Integer tempoRefreshWebAgendamento) {
        this.tempoRefreshWebAgendamento = tempoRefreshWebAgendamento;
    }

    public Integer getTempoRefreshRecepcao() {
        return tempoRefreshRecepcao;
    }

    public void setTempoRefreshRecepcao(Integer tempoRefreshRecepcao) {
        this.tempoRefreshRecepcao = tempoRefreshRecepcao;
    }

    public Integer getTempoRefreshHomologacao() {
        return tempoRefreshHomologacao;
    }

    public void setTempoRefreshHomologacao(Integer tempoRefreshHomologacao) {
        this.tempoRefreshHomologacao = tempoRefreshHomologacao;
    }

    public Integer getTempoRefreshAtendimento() {
        return tempoRefreshAtendimento;
    }

    public void setTempoRefreshAtendimento(Integer tempoRefreshAtendimento) {
        this.tempoRefreshAtendimento = tempoRefreshAtendimento;
    }

    public Integer getLimiteMeses() {
        return limiteMeses;
    }

    public void setLimiteMeses(Integer limiteMeses) {
        this.limiteMeses = limiteMeses;
    }

    public Date getLimiteAgendamentoRetroativo() {
        return limiteAgendamentoRetroativo;
    }

    public void setLimiteAgendamentoRetroativo(Date limiteAgendamentoRetroativo) {
        this.limiteAgendamentoRetroativo = limiteAgendamentoRetroativo;
    }

    public Date getDataAtualizaHomologacao() {
        return dataAtualizaHomologacao;
    }

    public void setDataAtualizaHomologacao(Date dataAtualizaHomologacao) {
        this.dataAtualizaHomologacao = dataAtualizaHomologacao;
    }

    public Boolean getValidaCpf() {
        return validaCpf;
    }

    public void setValidaCpf(Boolean validaCpf) {
        this.validaCpf = validaCpf;
    }

    public Boolean getValidaNome() {
        return validaNome;
    }

    public void setValidaNome(Boolean validaNome) {
        this.validaNome = validaNome;
    }

    public Boolean getValidaEndereco() {
        return validaEndereco;
    }

    public void setValidaEndereco(Boolean validaEndereco) {
        this.validaEndereco = validaEndereco;
    }

    public Boolean getValidaCarteira() {
        return validaCarteira;
    }

    public void setValidaCarteira(Boolean validaCarteira) {
        this.validaCarteira = validaCarteira;
    }

    public Boolean getValidaSerie() {
        return validaSerie;
    }

    public void setValidaSerie(Boolean validaSerie) {
        this.validaSerie = validaSerie;
    }

    public Boolean getValidaFuncao() {
        return validaFuncao;
    }

    public void setValidaFuncao(Boolean validaFuncao) {
        this.validaFuncao = validaFuncao;
    }

    public Boolean getValidaAdmissao() {
        return validaAdmissao;
    }

    public void setValidaAdmissao(Boolean validaAdmissao) {
        this.validaAdmissao = validaAdmissao;
    }

    public Boolean getValidaDemissao() {
        return validaDemissao;
    }

    public void setValidaDemissao(Boolean validaDemissao) {
        this.validaDemissao = validaDemissao;
    }

    public Boolean getValidaContato() {
        return validaContato;
    }

    public void setValidaContato(Boolean validaContato) {
        this.validaContato = validaContato;
    }

    public Boolean getValidaEmail() {
        return validaEmail;
    }

    public void setValidaEmail(Boolean validaEmail) {
        this.validaEmail = validaEmail;
    }

    public Boolean getValidaTelefone() {
        return validaTelefone;
    }

    public void setValidaTelefone(Boolean validaTelefone) {
        this.validaTelefone = validaTelefone;
    }

    public Boolean getWebValidaCpf() {
        return webValidaCpf;
    }

    public void setWebValidaCpf(Boolean webValidaCpf) {
        this.webValidaCpf = webValidaCpf;
    }

    public Boolean getWebValidaNome() {
        return webValidaNome;
    }

    public void setWebValidaNome(Boolean webValidaNome) {
        this.webValidaNome = webValidaNome;
    }

    public Boolean getWebValidaEndereco() {
        return webValidaEndereco;
    }

    public void setWebValidaEndereco(Boolean webValidaEndereco) {
        this.webValidaEndereco = webValidaEndereco;
    }

    public Boolean getWebValidaCarteira() {
        return webValidaCarteira;
    }

    public void setWebValidaCarteira(Boolean webValidaCarteira) {
        this.webValidaCarteira = webValidaCarteira;
    }

    public Boolean getWebValidaSerie() {
        return webValidaSerie;
    }

    public void setWebValidaSerie(Boolean webValidaSerie) {
        this.webValidaSerie = webValidaSerie;
    }

    public Boolean getWebValidaFuncao() {
        return webValidaFuncao;
    }

    public void setWebValidaFuncao(Boolean webValidaFuncao) {
        this.webValidaFuncao = webValidaFuncao;
    }

    public Boolean getWebValidaAdmissao() {
        return webValidaAdmissao;
    }

    public void setWebValidaAdmissao(Boolean webValidaAdmissao) {
        this.webValidaAdmissao = webValidaAdmissao;
    }

    public Boolean getWebValidaDemissao() {
        return webValidaDemissao;
    }

    public void setWebValidaDemissao(Boolean webValidaDemissao) {
        this.webValidaDemissao = webValidaDemissao;
    }

    public Boolean getWebValidaContato() {
        return webValidaContato;
    }

    public void setWebValidaContato(Boolean webValidaContato) {
        this.webValidaContato = webValidaContato;
    }

    public Boolean getWebValidaEmail() {
        return webValidaEmail;
    }

    public void setWebValidaEmail(Boolean webValidaEmail) {
        this.webValidaEmail = webValidaEmail;
    }

    public Boolean getWebValidaTelefone() {
        return webValidaTelefone;
    }

    public void setWebValidaTelefone(Boolean webValidaTelefone) {
        this.webValidaTelefone = webValidaTelefone;
    }

    public Boolean getWebValidaDataNascimento() {
        return webValidaDataNascimento;
    }

    public void setWebValidaDataNascimento(Boolean webValidaDataNascimento) {
        this.webValidaDataNascimento = webValidaDataNascimento;
    }

    public Boolean getValidaDataNascimento() {
        return validaDataNascimento;
    }

    public void setValidaDataNascimento(Boolean validaDataNascimento) {
        this.validaDataNascimento = validaDataNascimento;
    }

    public Date getWebDesabilitaInicial() {
        return webDesabilitaInicial;
    }

    public void setWebDesabilitaInicial(Date webDesabilitaInicial) {
        this.webDesabilitaInicial = webDesabilitaInicial;
    }

    public Date getWebDesabilitaFinal() {
        return webDesabilitaFinal;
    }

    public void setWebDesabilitaFinal(Date webDesabilitaFinal) {
        this.webDesabilitaFinal = webDesabilitaFinal;
    }

    public String getWebDesabilitaInicialString() {
        Integer x = DataHoje.converteDataParaInteger(DataHoje.data());
        Integer y = DataHoje.converteDataParaInteger(getWebDesabilitaFinalString());
        if (x > y) {
            webDesabilitaInicial = null;
            webDesabilitaFinal = null;
            webDesabilitaObs = "";
        }
        return DataHoje.converteData(webDesabilitaInicial);
    }

    public void setWebDesabilitaInicialString(String webDesabilitaInicialString) {
        this.webDesabilitaInicial = DataHoje.converte(webDesabilitaInicialString);
    }

    public String getWebDesabilitaFinalString() {
        if (webDesabilitaInicial != null) {
            if (webDesabilitaFinal == null) {
                webDesabilitaFinal = webDesabilitaInicial;
            }
            if (DataHoje.maiorData(webDesabilitaInicial, webDesabilitaFinal)) {
                webDesabilitaFinal = webDesabilitaInicial;
            }
        }
        return DataHoje.converteData(webDesabilitaFinal);
    }

    public void setWebDesabilitaFinalString(String webDesabilitaFinalString) {
        this.webDesabilitaFinal = DataHoje.converte(webDesabilitaFinalString);
    }

    public String getWebDesabilitaObs() {
        return webDesabilitaObs;
    }

    public void setWebDesabilitaObs(String webDesabilitaObs) {
        this.webDesabilitaObs = webDesabilitaObs;
    }

    public Integer getInicioDiasAgendamento() {
        return inicioDiasAgendamento;
    }

    public void setInicioDiasAgendamento(Integer inicioDiasAgendamento) {
        this.inicioDiasAgendamento = inicioDiasAgendamento;
    }
//
//    public Date getDtAtualiza() {
//        return dtAtualiza;
//    }
//
//    public void setDtAtualiza(Date dtAtualiza) {
//        this.dtAtualiza = dtAtualiza;
//    }
//
//    public Date getDtHabilitaCorrecao() {
//        return dtHabilitaCorrecao;
//    }
//
//    public void setDtHabilitaCorrecao(Date dtHabilitaCorrecao) {
//        this.dtHabilitaCorrecao = dtHabilitaCorrecao;
//    }
//
//    public Boolean getAgendamentoWeb() {
//        return agendamentoWeb;
//    }
//
//    public void setAgendamentoWeb(Boolean agendamentoWeb) {
//        this.agendamentoWeb = agendamentoWeb;
//    }
//
//    public Boolean getHabilitaSenha() {
//        return habilitaSenha;
//    }
//
//    public void setHabilitaSenha(Boolean habilitaSenha) {
//        this.habilitaSenha = habilitaSenha;
//    }
//
//    public String getDocumentos() {
//        return documentos;
//    }
//
//    public void setDocumentos(String documentos) {
//        this.documentos = documentos;
//    }
//
//    public String getFormaPagamento() {
//        return formaPagamento;
//    }
//
//    public void setFormaPagamento(String formaPagamento) {
//        this.formaPagamento = formaPagamento;
//    }
//
//    public Boolean getAgendarSemHorarioWeb() {
//        return agendarSemHorarioWeb;
//    }
//
//    public void setAgendarSemHorarioWeb(Boolean agendarSemHorarioWeb) {
//        this.agendarSemHorarioWeb = agendarSemHorarioWeb;
//    }
//
//    public Boolean getBloquearHomologacao() {
//        return bloquearHomologacao;
//    }
//
//    public void setBloquearHomologacao(Boolean bloquearHomologacao) {
//        this.bloquearHomologacao = bloquearHomologacao;
//    }
//
//    public Integer getMesesInadimplentesAgenda() {
//        return mesesInadimplentesAgenda;
//    }
//
//    public void setMesesInadimplentesAgenda(Integer mesesInadimplentesAgenda) {
//        this.mesesInadimplentesAgenda = mesesInadimplentesAgenda;
//    }

    public Boolean getWebImprimirPlanilhaDebito() {
        return webImprimirPlanilhaDebito;
    }

    public void setWebImprimirPlanilhaDebito(Boolean webImprimirPlanilhaDebito) {
        this.webImprimirPlanilhaDebito = webImprimirPlanilhaDebito;
    }

    public Boolean getImprimeSenhaMatricial() {
        return imprimeSenhaMatricial;
    }

    public void setImprimeSenhaMatricial(Boolean imprimeSenhaMatricial) {
        this.imprimeSenhaMatricial = imprimeSenhaMatricial;
    }

    public static ConfiguracaoHomologacao get() {
        return (ConfiguracaoHomologacao) new Dao().find(new ConfiguracaoHomologacao(), 1);
    }

    public Boolean getAgendarMesmoHorarioEmpresa() {
        return agendarMesmoHorarioEmpresa;
    }

    public void setAgendarMesmoHorarioEmpresa(Boolean agendarMesmoHorarioEmpresa) {
        this.agendarMesmoHorarioEmpresa = agendarMesmoHorarioEmpresa;
    }

    public Boolean getWebAgendarMesmoHorarioEmpresa() {
        return webAgendarMesmoHorarioEmpresa;
    }

    public void setWebAgendarMesmoHorarioEmpresa(Boolean webAgendarMesmoHorarioEmpresa) {
        this.webAgendarMesmoHorarioEmpresa = webAgendarMesmoHorarioEmpresa;
    }

    public Boolean getWebValidaAgendamento() {
        return webValidaAgendamento;
    }

    public void setWebValidaAgendamento(Boolean webValidaAgendamento) {
        this.webValidaAgendamento = webValidaAgendamento;
    }

    public Boolean getWebDocumentoObrigatorio() {
        return webDocumentoObrigatorio;
    }

    public void setWebDocumentoObrigatorio(Boolean webDocumentoObrigatorio) {
        this.webDocumentoObrigatorio = webDocumentoObrigatorio;
    }

    public String getWebDocumentosHomologacao() {
        return webDocumentosHomologacao;
    }

    public void setWebDocumentosHomologacao(String webDocumentosHomologacao) {
        this.webDocumentosHomologacao = webDocumentosHomologacao;
    }

}
