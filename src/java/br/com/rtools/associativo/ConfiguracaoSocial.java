package br.com.rtools.associativo;

import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
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

@Entity
@Table(name = "conf_social")
public class ConfiguracaoSocial implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_dias_inativa_demissionado")
    private Integer diasInativaDemissionado;
    @Column(name = "dt_inativa_demissionado")
    @Temporal(TemporalType.DATE)
    private Date dataInativacaoDemissionado;
    @JoinColumn(name = "id_grupo_categoria_inativa_demissionado", referencedColumnName = "id")
    @ManyToOne
    private GrupoCategoria grupoCategoriaInativaDemissionado;
    @Column(name = "is_inativa_demissionado", columnDefinition = "boolean default false")
    private Boolean inativaDemissionado;
    @Column(name = "is_recebe_atrasado", columnDefinition = "boolean default false")
    private Boolean recebeAtrasado;
    @Column(name = "is_controla_cartao_filial", columnDefinition = "boolean default false")
    private Boolean controlaCartaoFilial;
    @Column(name = "nr_cartao_digitos")
    private Integer cartaoDigitos;
    @Column(name = "nr_cartao_posicao_via")
    private Integer cartaoPosicaoVia;
    @Column(name = "nr_cartao_posicao_codigo")
    private Integer cartaoPosicaoCodigo;
    @Column(name = "ds_obs_desconto_folha")
    private String obsDescontoFolha;
    @Column(name = "nr_validade_meses_cartao_academia")
    private Integer validadeMesesCartaoAcademia;
    @Column(name = "nr_meses_debito_inativacao")
    private Integer mesesDebitoInativacao;
    @Column(name = "is_inativa_oposicao", columnDefinition = "boolean default true")
    private Boolean inativaOposicao;
    @Column(name = "is_bloqueia_convite_oposicao", columnDefinition = "boolean default false")
    private Boolean bloqueiaConviteOposicao;
    @Column(name = "is_libera_convite_dia", columnDefinition = "boolean default false")
    private Boolean liberaConviteDia;
    @Column(name = "is_atualiza_via_carteirinha", columnDefinition = "boolean default false")
    private Boolean atualizaViaCarteirinha;
    @Column(name = "nr_intervalo_minuto")
    private Integer intervaloMinuto;
    @Column(name = "ds_obs_convite ", length = 8000)
    private String observacaoConvite;
    @Column(name = "is_imprime_convite_teste", columnDefinition = "boolean default false", nullable = false)
    private Boolean imprimeConviteTeste;
    @Column(name = "is_obrigatorio_email", columnDefinition = "boolean default false", nullable = false)
    private Boolean obrigatorioEmail;
    @Column(name = "is_bloqueia_cpf", columnDefinition = "boolean default false", nullable = false)
    private Boolean bloqueiaCpf;

    public ConfiguracaoSocial() {
        this.id = -1;
        this.diasInativaDemissionado = 0;
        this.dataInativacaoDemissionado = null;
        this.grupoCategoriaInativaDemissionado = null;
        this.inativaDemissionado = false;
        this.recebeAtrasado = false;
        this.controlaCartaoFilial = false;
        this.cartaoDigitos = 0;
        this.cartaoPosicaoVia = 0;
        this.cartaoPosicaoCodigo = 0;
        this.obsDescontoFolha = "";
        this.validadeMesesCartaoAcademia = 12;
        this.mesesDebitoInativacao = 6;
        this.inativaOposicao = true;
        this.bloqueiaConviteOposicao = false;
        this.liberaConviteDia = false;
        this.atualizaViaCarteirinha = false;
        this.intervaloMinuto = 0;
        this.observacaoConvite = "";
        this.imprimeConviteTeste = false;
        this.obrigatorioEmail = false;
        this.bloqueiaCpf = false;
    }

    public ConfiguracaoSocial(Integer id, Integer diasInativaDemissionado, Date dataInativacaoDemissionado, GrupoCategoria grupoCategoriaInativaDemissionado, Boolean inativaDemissionado, Boolean recebeAtrasado, Boolean controlaCartaoFilial, Integer cartaoDigitos, Integer cartaoPosicaoVia, Integer cartaoPosicaoCodigo, String obsDescontoFolha, Integer validadeMesesCartaoAcademia, Integer mesesDebitoInativacao, Boolean inativaOposicao, Boolean bloqueiaConviteOposicao, Boolean liberaConviteDia, Boolean atualizaViaCarteirinha, Integer intervaloMinuto, String observacaoConvite, Boolean imprimeConviteTeste, Boolean obrigatorioEmail, Boolean bloqueiaCpf) {
        this.id = id;
        this.diasInativaDemissionado = diasInativaDemissionado;
        this.dataInativacaoDemissionado = dataInativacaoDemissionado;
        this.grupoCategoriaInativaDemissionado = grupoCategoriaInativaDemissionado;
        this.inativaDemissionado = inativaDemissionado;
        this.recebeAtrasado = recebeAtrasado;
        this.controlaCartaoFilial = controlaCartaoFilial;
        this.cartaoDigitos = cartaoDigitos;
        this.cartaoPosicaoVia = cartaoPosicaoVia;
        this.cartaoPosicaoCodigo = cartaoPosicaoCodigo;
        this.obsDescontoFolha = obsDescontoFolha;
        this.validadeMesesCartaoAcademia = validadeMesesCartaoAcademia;
        this.mesesDebitoInativacao = mesesDebitoInativacao;
        this.inativaOposicao = inativaOposicao;
        this.bloqueiaConviteOposicao = bloqueiaConviteOposicao;
        this.liberaConviteDia = liberaConviteDia;
        this.atualizaViaCarteirinha = atualizaViaCarteirinha;
        this.intervaloMinuto = intervaloMinuto;
        this.observacaoConvite = observacaoConvite;
        this.imprimeConviteTeste = imprimeConviteTeste;
        this.obrigatorioEmail = obrigatorioEmail;
        this.bloqueiaCpf = bloqueiaCpf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiasInativaDemissionado() {
        return diasInativaDemissionado;
    }

    public void setDiasInativaDemissionado(Integer diasInativaDemissionado) {
        this.diasInativaDemissionado = diasInativaDemissionado;
    }

    public Date getDataInativacaoDemissionado() {
        return dataInativacaoDemissionado;
    }

    public void setDataInativacaoDemissionado(Date dataInativacaoDemissionado) {
        this.dataInativacaoDemissionado = dataInativacaoDemissionado;
    }

    public String getDataInativacaoDemissionadoString() {
        return DataHoje.converteData(dataInativacaoDemissionado);
    }

    public void setDataInativacaoDemissionadoString(String dataInativacaoDemissionado) {
        this.dataInativacaoDemissionado = DataHoje.converte(dataInativacaoDemissionado);
    }

    public GrupoCategoria getGrupoCategoriaInativaDemissionado() {
        return grupoCategoriaInativaDemissionado;
    }

    public void setGrupoCategoriaInativaDemissionado(GrupoCategoria grupoCategoriaInativaDemissionado) {
        this.grupoCategoriaInativaDemissionado = grupoCategoriaInativaDemissionado;
    }

    public Boolean getInativaDemissionado() {
        return inativaDemissionado;
    }

    public void setInativaDemissionado(Boolean inativaDemissionado) {
        this.inativaDemissionado = inativaDemissionado;
    }

    public Boolean getRecebeAtrasado() {
        return recebeAtrasado;
    }

    public void setRecebeAtrasado(Boolean recebeAtrasado) {
        this.recebeAtrasado = recebeAtrasado;
    }

    public Boolean getControlaCartaoFilial() {
        return controlaCartaoFilial;
    }

    public void setControlaCartaoFilial(Boolean controlaCartaoFilial) {
        this.controlaCartaoFilial = controlaCartaoFilial;
    }

    public Integer getCartaoDigitos() {
        return cartaoDigitos;
    }

    public void setCartaoDigitos(Integer cartaoDigitos) {
        this.cartaoDigitos = cartaoDigitos;
    }

    public Integer getCartaoPosicaoVia() {
        return cartaoPosicaoVia;
    }

    public void setCartaoPosicaoVia(Integer cartaoPosicaoVia) {
        this.cartaoPosicaoVia = cartaoPosicaoVia;
    }

    public Integer getCartaoPosicaoCodigo() {
        return cartaoPosicaoCodigo;
    }

    public void setCartaoPosicaoCodigo(Integer cartaoPosicaoCodigo) {
        this.cartaoPosicaoCodigo = cartaoPosicaoCodigo;
    }

    public String getObsDescontoFolha() {
        return obsDescontoFolha;
    }

    public void setObsDescontoFolha(String obsDescontoFolha) {
        this.obsDescontoFolha = obsDescontoFolha;
    }

    public Integer getValidadeMesesCartaoAcademia() {
        return validadeMesesCartaoAcademia;
    }

    public void setValidadeMesesCartaoAcademia(Integer validadeMesesCartaoAcademia) {
        this.validadeMesesCartaoAcademia = validadeMesesCartaoAcademia;
    }

    public static ConfiguracaoSocial get() {
        return (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
    }

    public Integer getMesesDebitoInativacao() {
        return mesesDebitoInativacao;
    }

    public void setMesesDebitoInativacao(Integer mesesDebitoInativacao) {
        this.mesesDebitoInativacao = mesesDebitoInativacao;
    }

    public Boolean getInativaOposicao() {
        return inativaOposicao;
    }

    public void setInativaOposicao(Boolean inativaOposicao) {
        this.inativaOposicao = inativaOposicao;
    }

    public Boolean getBloqueiaConviteOposicao() {
        return bloqueiaConviteOposicao;
    }

    public void setBloqueiaConviteOposicao(Boolean bloqueiaConviteOposicao) {
        this.bloqueiaConviteOposicao = bloqueiaConviteOposicao;
    }

    public Boolean getLiberaConviteDia() {
        return liberaConviteDia;
    }

    public void setLiberaConviteDia(Boolean liberaConviteDia) {
        this.liberaConviteDia = liberaConviteDia;
    }

    public Boolean getAtualizaViaCarteirinha() {
        return atualizaViaCarteirinha;
    }

    public void setAtualizaViaCarteirinha(Boolean atualizaViaCarteirinha) {
        this.atualizaViaCarteirinha = atualizaViaCarteirinha;
    }

    public Integer getIntervaloMinuto() {
        return intervaloMinuto;
    }

    public void setIntervaloMinuto(Integer intervaloMinuto) {
        this.intervaloMinuto = intervaloMinuto;
    }

    public String getObservacaoConvite() {
        return observacaoConvite;
    }

    public void setObservacaoConvite(String observacaoConvite) {
        this.observacaoConvite = observacaoConvite;
    }

    public Boolean getImprimeConviteTeste() {
        return imprimeConviteTeste;
    }

    public void setImprimeConviteTeste(Boolean imprimeConviteTeste) {
        this.imprimeConviteTeste = imprimeConviteTeste;
    }

    public Boolean getObrigatorioEmail() {
        return obrigatorioEmail;
    }

    public void setObrigatorioEmail(Boolean obrigatorioEmail) {
        this.obrigatorioEmail = obrigatorioEmail;
    }

    public Boolean getBloqueiaCpf() {
        return bloqueiaCpf;
    }

    public void setBloqueiaCpf(Boolean bloqueiaCpf) {
        this.bloqueiaCpf = bloqueiaCpf;
    }
}
