package br.com.rtools.associativo;

import br.com.rtools.financeiro.Servicos;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "soc_categoria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_grupo_categoria"})
)
@NamedQueries({
    @NamedQuery(name = "Categoria.pesquisaID", query = "SELECT C FROM Categoria AS C WHERE C.id=:pid")
    ,
    @NamedQuery(name = "Categoria.findAll", query = "SELECT C FROM Categoria AS C ORDER BY C.grupoCategoria.grupoCategoria ASC, C.categoria ASC")
})

public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_categoria", length = 100, nullable = true)
    private String categoria;
    @JoinColumn(name = "id_grupo_categoria", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private GrupoCategoria grupoCategoria;
    @Column(name = "nr_carencia_balcao", length = 10, nullable = true)
    private Integer nrCarenciaBalcao;
    @Column(name = "nr_carencia_desc_folha", length = 10, nullable = true)
    private Integer nrCarenciaDescFolha;
    @Column(name = "empresa_obrigatoria", nullable = true)
    private boolean empresaObrigatoria;
    @Column(name = "votante", nullable = true)
    private boolean votante;
    @Column(name = "usa_clube_segunda", nullable = true)
    private boolean usaClubeSegunda;
    @Column(name = "usa_clube_terca", nullable = true)
    private boolean usaClubeTerca;
    @Column(name = "usa_clube_quarta", nullable = true)
    private boolean usaClubeQuarta;
    @Column(name = "usa_clube_quinta", nullable = true)
    private boolean usaClubeQuinta;
    @Column(name = "usa_clube_sexta", nullable = true)
    private boolean usaClubeSexta;
    @Column(name = "usa_clube_sabado", nullable = true)
    private boolean usaClubeSabado;
    @Column(name = "usa_clube_domingo", nullable = true)
    private boolean usaClubeDomingo;
    @Column(name = "is_cartao_titular", nullable = false, columnDefinition = "boolean default true")
    private boolean cartaoTitular;
    @Column(name = "is_cartao_dependente", nullable = false, columnDefinition = "boolean default true")
    private boolean cartaoDependente;
    @Column(name = "is_bloqueia_meses", nullable = false, columnDefinition = "boolean default false")
    private Boolean bloqueiaMeses;
    @JoinColumn(name = "id_servico_taxa_matricula", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Servicos servicoTaxaMatricula;
    @Column(name = "nr_taxa_matricula_parcelas", nullable = true)
    private Integer nrTaxaMatriculaParcelas;
    @Column(name = "is_ficha_social", nullable = false, columnDefinition = "boolean default true")
    private Boolean fichaSocial;
    @Column(name = "is_ficha_filiacao", nullable = false, columnDefinition = "boolean default false")
    private Boolean fichaFiliacao;
    @Column(name = "is_cobranca_carteirinha", nullable = false, columnDefinition = "boolean default false")
    private Boolean cobrancaCarteirinha;
    @Column(name = "nr_dias_reativacao", nullable = false, columnDefinition = "integer default 30")
    private Integer nrDiasReativacao;
    
    
    @Transient
    private Boolean selected;

    public Categoria() {
        this.id = -1;
        this.categoria = "";
        this.grupoCategoria = new GrupoCategoria();
        this.nrCarenciaBalcao = 0;
        this.nrCarenciaDescFolha = 0;
        this.empresaObrigatoria = false;
        this.votante = false;
        this.usaClubeSegunda = false;
        this.usaClubeTerca = false;
        this.usaClubeQuarta = false;
        this.usaClubeQuinta = false;
        this.usaClubeSexta = false;
        this.usaClubeSabado = false;
        this.usaClubeDomingo = false;
        this.cartaoTitular = true;
        this.cartaoDependente = true;
        this.bloqueiaMeses = false;
        this.servicoTaxaMatricula = null;
        this.nrTaxaMatriculaParcelas = 1;
        this.fichaSocial = true;
        this.fichaFiliacao = false;
        this.cobrancaCarteirinha = false;
        this.selected = false;
        this.nrDiasReativacao = 30;
    }

    public Categoria(Integer id, String categoria, GrupoCategoria grupoCategoria, Integer nrCarenciaBalcao, Integer nrCarenciaDescFolha,
            boolean empresaObrigatoria, boolean votante, boolean usaClubeSegunda, boolean usaClubeTerca, boolean usaClubeQuarta,
            boolean usaClubeQuinta, boolean usaClubeSexta, boolean usaClubeSabado, boolean usaClubeDomingo, boolean cartaoTitular, boolean cartaoDependente, Boolean bloqueiaMeses,
            Servicos servicoTaxaMatricula,Integer nrTaxaMatriculaParcelas, boolean fichaSocial, boolean fichaFiliacao, boolean cobrancaCarteirinha ,
            Integer nrDiasReativacao) {
        this.id = id;
        this.categoria = categoria;
        this.grupoCategoria = grupoCategoria;
        this.nrCarenciaBalcao = nrCarenciaBalcao;
        this.nrCarenciaDescFolha = nrCarenciaDescFolha;
        this.empresaObrigatoria = empresaObrigatoria;
        this.votante = votante;
        this.usaClubeSegunda = usaClubeSegunda;
        this.usaClubeTerca = usaClubeTerca;
        this.usaClubeQuarta = usaClubeQuarta;
        this.usaClubeQuinta = usaClubeQuinta;
        this.usaClubeSexta = usaClubeSexta;
        this.usaClubeSabado = usaClubeSabado;
        this.usaClubeDomingo = usaClubeDomingo;
        this.cartaoTitular = cartaoTitular;
        this.cartaoDependente = cartaoDependente;
        this.bloqueiaMeses = bloqueiaMeses;
        this.servicoTaxaMatricula = servicoTaxaMatricula;
        this.nrTaxaMatriculaParcelas = nrTaxaMatriculaParcelas;
        this.fichaSocial = fichaSocial;
        this.fichaFiliacao = fichaFiliacao; 
        this.cobrancaCarteirinha = cobrancaCarteirinha;
        this.selected = false;
        this.nrDiasReativacao = nrDiasReativacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GrupoCategoria getGrupoCategoria() {
        return grupoCategoria;
    }

    public void setGrupoCategoria(GrupoCategoria grupoCategoria) {
        this.grupoCategoria = grupoCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getNrCarenciaBalcao() {
        return nrCarenciaBalcao;
    }

    public void setNrCarenciaBalcao(Integer nrCarenciaBalcao) {
        this.nrCarenciaBalcao = nrCarenciaBalcao;
    }

    public Integer getNrCarenciaDescFolha() {
        return nrCarenciaDescFolha;
    }

    public void setNrCarenciaDescFolha(Integer nrCarenciaDescFolha) {
        this.nrCarenciaDescFolha = nrCarenciaDescFolha;
    }

    public boolean isEmpresaObrigatoria() {
        return empresaObrigatoria;
    }

    public void setEmpresaObrigatoria(boolean empresaObrigatoria) {
        this.empresaObrigatoria = empresaObrigatoria;
    }

    public boolean isUsaClubeSegunda() {
        return usaClubeSegunda;
    }

    public void setUsaClubeSegunda(boolean usaClubeSegunda) {
        this.usaClubeSegunda = usaClubeSegunda;
    }

    public boolean isUsaClubeTerca() {
        return usaClubeTerca;
    }

    public void setUsaClubeTerca(boolean usaClubeTerca) {
        this.usaClubeTerca = usaClubeTerca;
    }

    public boolean isUsaClubeQuarta() {
        return usaClubeQuarta;
    }

    public void setUsaClubeQuarta(boolean usaClubeQuarta) {
        this.usaClubeQuarta = usaClubeQuarta;
    }

    public boolean isUsaClubeQuinta() {
        return usaClubeQuinta;
    }

    public void setUsaClubeQuinta(boolean usaClubeQuinta) {
        this.usaClubeQuinta = usaClubeQuinta;
    }

    public boolean isUsaClubeSexta() {
        return usaClubeSexta;
    }

    public void setUsaClubeSexta(boolean usaClubeSexta) {
        this.usaClubeSexta = usaClubeSexta;
    }

    public boolean isUsaClubeSabado() {
        return usaClubeSabado;
    }

    public void setUsaClubeSabado(boolean usaClubeSabado) {
        this.usaClubeSabado = usaClubeSabado;
    }

    public boolean isUsaClubeDomingo() {
        return usaClubeDomingo;
    }

    public void setUsaClubeDomingo(boolean usaClubeDomingo) {
        this.usaClubeDomingo = usaClubeDomingo;
    }

    public boolean isVotante() {
        return votante;
    }

    public void setVotante(boolean votante) {
        this.votante = votante;
    }

    public String getNrCarenciaBalcaoString() {
        return "" + nrCarenciaBalcao;
    }

    public void setNrCarenciaBalcaoString(String nrCarenciaBalcaoString) {
        this.nrCarenciaBalcao = Integer.parseInt(nrCarenciaBalcaoString);
    }

    public String getNrCarenciaDescFolhaString() {
        return "" + nrCarenciaDescFolha;
    }

    public void setNrCarenciaDescFolha(String nrCarenciaDescFolhaString) {
        this.nrCarenciaDescFolha = Integer.parseInt(nrCarenciaDescFolhaString);
    }

    public boolean isCartaoTitular() {
        return cartaoTitular;
    }

    public void setCartaoTitular(boolean cartaoTitular) {
        if (!cartaoTitular) {
            cartaoDependente = false;
        }
        this.cartaoTitular = cartaoTitular;
    }

    public boolean isCartaoDependente() {
        return cartaoDependente;
    }

    public void setCartaoDependente(boolean cartaoDependente) {
        if (cartaoDependente) {
            if (!this.cartaoTitular) {
                cartaoDependente = false;
            }
        }
        this.cartaoDependente = cartaoDependente;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getBloqueiaMeses() {
        return bloqueiaMeses;
    }

    public void setBloqueiaMeses(Boolean bloqueiaMeses) {
        this.bloqueiaMeses = bloqueiaMeses;
    }

    public Servicos getServicoTaxaMatricula() {
        return servicoTaxaMatricula;
    }

    public void setServicoTaxaMatricula(Servicos servicoTaxaMatricula) {
        this.servicoTaxaMatricula = servicoTaxaMatricula;
    }

    public Integer getNrTaxaMatriculaParcelas() {
        return nrTaxaMatriculaParcelas;
    }

    public void setNrTaxaMatriculaParcelas(Integer nrTaxaMatriculaParcelas) {
        this.nrTaxaMatriculaParcelas = nrTaxaMatriculaParcelas;
    }
    
    public Boolean getFichaSocial() {
        return fichaSocial;
    }

    public void setFichaSocial(Boolean fichaSocial) {
        this.fichaSocial = fichaSocial;
    }

    public Boolean getFichaFiliacao() {
        return fichaFiliacao;
    }

    public void setFichaFiliacao(Boolean fichaFiliacao) {
        this.fichaFiliacao = fichaFiliacao;
    }
    

    @Override
    public String toString() {
        return "Categoria{" + "id=" + id + ", categoria=" + categoria + ", grupoCategoria=" + grupoCategoria + ", nrCarenciaBalcao=" + nrCarenciaBalcao + ", nrCarenciaDescFolha=" + nrCarenciaDescFolha + ", empresaObrigatoria=" + empresaObrigatoria + ", votante=" + votante + ", usaClubeSegunda=" + usaClubeSegunda + ", usaClubeTerca=" + usaClubeTerca + ", usaClubeQuarta=" + usaClubeQuarta + ", usaClubeQuinta=" + usaClubeQuinta + ", usaClubeSexta=" + usaClubeSexta + ", usaClubeSabado=" + usaClubeSabado + ", usaClubeDomingo=" + usaClubeDomingo + ", cartaoTitular=" + cartaoTitular + ", cartaoDependente=" + cartaoDependente + ", bloqueiaMeses=" + bloqueiaMeses + ", servicoTaxaMatricula=" + servicoTaxaMatricula + ", nrTaxaMatriculaParcelas=" + nrTaxaMatriculaParcelas + ", selected=" + selected + '}';
    }

    public Boolean getCobrancaCarteirinha() {
        return cobrancaCarteirinha;
    }

    public void setCobrancaCarteirinha(Boolean cobrancaCarteirinha) {
        this.cobrancaCarteirinha = cobrancaCarteirinha;
    }

    public Integer getNrDiasReativacao() {
        return nrDiasReativacao;
    }

    public void setNrDiasReativacao(Integer nrDiasReativacao) {
        this.nrDiasReativacao = nrDiasReativacao;
    }

}
