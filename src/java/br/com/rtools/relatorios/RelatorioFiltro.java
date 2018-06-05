package br.com.rtools.relatorios;

import br.com.rtools.html.HtmlTag;
import br.com.rtools.seguranca.Rotina;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sis_relatorio_filtro", uniqueConstraints = @UniqueConstraint(columnNames = {"id_rotina", "id_relatorio", "id_grupo", "ds_chave", "ds_valor"}))
public class RelatorioFiltro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @JoinColumn(name = "id_relatorio", referencedColumnName = "id")
    @ManyToOne
    private Relatorios relatorio;
    @JoinColumn(name = "id_grupo", referencedColumnName = "id")
    @ManyToOne
    private RelatorioFiltroGrupo relatorioFiltroGrupo;
    @JoinColumn(name = "id_html_tag", referencedColumnName = "id")
    @ManyToOne
    private HtmlTag htmlTag;
    @Column(name = "ds_chave", length = 100, nullable = false)
    private String chave;
    @Column(name = "ds_valor", length = 100, nullable = false)
    private String valor;
    @Column(name = "ds_obs", length = 255, nullable = false)
    private String obs;
    @Column(name = "is_renderizar", nullable = false, columnDefinition = "boolean default false")
    private Boolean renderizar;
    @Column(name = "is_desabilitar", nullable = false, columnDefinition = "boolean default false")
    private Boolean desabilitar;
    @Column(name = "is_ativo", nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo;
    @Column(name = "nr_ordem", nullable = false, columnDefinition = "integer default 0")
    private Integer ordem;

    @Transient
    private Boolean selected;

    public RelatorioFiltro() {
        this.id = null;
        this.rotina = null;
        this.relatorio = null;
        this.relatorioFiltroGrupo = null;
        this.htmlTag = null;
        this.chave = "";
        this.valor = "";
        this.obs = "";
        this.renderizar = false;
        this.desabilitar = false;
        this.ativo = true;
        this.ordem = 0;
        this.selected = false;
    }

    public RelatorioFiltro(Integer id, Rotina rotina, Relatorios relatorio, RelatorioFiltroGrupo relatorioFiltroGrupo, HtmlTag htmlTag, String chave, String valor, String obs, Boolean renderizar, Boolean desabilitar, Boolean ativo, Integer ordem) {
        this.id = id;
        this.rotina = rotina;
        this.relatorio = relatorio;
        this.relatorioFiltroGrupo = relatorioFiltroGrupo;
        this.htmlTag = htmlTag;
        this.chave = chave;
        this.valor = valor;
        this.obs = obs;
        this.renderizar = renderizar;
        this.desabilitar = desabilitar;
        this.ativo = ativo;
        this.ordem = ordem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Relatorios getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorios relatorio) {
        this.relatorio = relatorio;
    }

    public RelatorioFiltroGrupo getRelatorioFiltroGrupo() {
        return relatorioFiltroGrupo;
    }

    public void setRelatorioFiltroGrupo(RelatorioFiltroGrupo relatorioFiltroGrupo) {
        this.relatorioFiltroGrupo = relatorioFiltroGrupo;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Boolean getRenderizar() {
        return renderizar;
    }

    public void setRenderizar(Boolean renderizar) {
        this.renderizar = renderizar;
    }

    public Boolean getDesabilitar() {
        return desabilitar;
    }

    public void setDesabilitar(Boolean desabilitar) {
        this.desabilitar = desabilitar;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public HtmlTag getHtmlTag() {
        return htmlTag;
    }

    public void setHtmlTag(HtmlTag htmlTag) {
        this.htmlTag = htmlTag;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
