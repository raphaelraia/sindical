package br.com.rtools.locadoraFilme;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_titulo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ds_descricao", "id_genero", "ano_lancamento"})
)
public class Titulo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data")
    private Date data;
    @Column(name = "ds_descricao", length = 50, nullable = false)
    private String descricao;
    @Column(name = "ds_autor", length = 100)
    private String autor;
    @Column(name = "ds_atores", length = 1000)
    private String atores;
    @Column(name = "nr_idade_minima", columnDefinition = "integer default 0")
    private Integer idadeMinima;
    @Column(name = "nr_qtde_por_embalagem", columnDefinition = "integer default 0")
    private Integer qtdePorEmbalagem;
    @JoinColumn(name = "id_genero", referencedColumnName = "id")
    @ManyToOne
    private Genero genero;
    @Column(name = "ds_barras", length = 100, unique = true)
    private String barras;
    @Column(name = "nr_duracao_minutos", length = 5)
    private String duracao;
    @Column(name = "ano_lancamento")
    private Integer anoLancamento;
    @Column(name = "ds_legenda", length = 10)
    private String legenda;
    @Column(name = "ds_formato", length = 20)
    private String formato;
    @Column(name = "is_imprime_etiqueta", columnDefinition = "boolean default false")
    private Boolean imprimeEtiqueta;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_foto")
    private Date foto;

    public Titulo() {
        this.id = null;
        this.data = new Date();
        this.descricao = "";
        this.autor = "";
        this.atores = "";
        this.idadeMinima = 0;
        this.qtdePorEmbalagem = 1;
        this.genero = new Genero();
        this.barras = "";
        this.duracao = "";
        this.anoLancamento = 0;
        this.legenda = "";
        this.formato = "";
        this.imprimeEtiqueta = false;
        this.foto = null;
    }

    public Titulo(Integer id, Date data, String descricao, String autor, String atores, Integer idadeMinima, Integer qtdePorEmbalagem, Genero genero, String barras, String duracao, Integer anoLancamento, String legenda, String formato, Boolean imprimeEtiqueta, Date foto) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.autor = autor;
        this.atores = atores;
        this.idadeMinima = idadeMinima;
        this.qtdePorEmbalagem = qtdePorEmbalagem;
        this.genero = genero;
        this.barras = barras;
        this.duracao = duracao;
        this.anoLancamento = anoLancamento;
        this.legenda = legenda;
        this.formato = formato;
        this.imprimeEtiqueta = imprimeEtiqueta;
        this.foto = foto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getBarras() {
        return barras;
    }

    public void setBarras(String barras) {
        this.barras = barras;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public String getLegenda() {
        return legenda;
    }

    public void setLegenda(String legenda) {
        this.legenda = legenda;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Boolean getImprimeEtiqueta() {
        return imprimeEtiqueta;
    }

    public void setImprimeEtiqueta(Boolean imprimeEtiqueta) {
        this.imprimeEtiqueta = imprimeEtiqueta;
    }

    public void setDataString(String data) {
        this.data = DataHoje.converte(data);
    }

    public String getDataString() {
        return DataHoje.converteData(data);
    }

    public void setAnoLancamentoString(String anoLancamento) {
        try {
            this.anoLancamento = Integer.parseInt(anoLancamento);
        } catch (Exception e) {
            this.anoLancamento = 0;
        }
    }

    public String getAnoLancamentoString() {
        if (anoLancamento == 0) {
            return "";
        } else {
            return Integer.toString(anoLancamento);
        }
    }

    public Date getFoto() {
        return foto;
    }

    public void setFoto(Date foto) {
        this.foto = foto;
    }

    public String getAtores() {
        return atores;
    }

    public Integer getQtdePorEmbalagem() {
        return qtdePorEmbalagem;
    }

    public void setQtdePorEmbalagem(Integer qtdePorEmbalagem) {
        this.qtdePorEmbalagem = qtdePorEmbalagem;

    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public Integer getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(Integer idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public String getIdadeMinimaString() {
        return Integer.toString(idadeMinima);
    }

    public void setIdadeMinimaString(String idadeMinimaString) {
        try {
            this.idadeMinima = Integer.parseInt(idadeMinimaString);
            if (this.idadeMinima < 0) {
                this.idadeMinima = 0;
            }
        } catch (Exception e) {
        }
    }

    public String getQtdePorEmbalagemString() {
        return Integer.toString(qtdePorEmbalagem);
    }

    public void setQtdePorEmbalagemString(String qtdePorEmbalagemString) {
        try {
            this.qtdePorEmbalagem = Integer.parseInt(qtdePorEmbalagemString);
            if (this.qtdePorEmbalagem < 1) {
                this.qtdePorEmbalagem = 1;
            }
        } catch (Exception e) {
        }

    }

    public boolean isLancamento() {
        try {
            Integer ano = 0;
            if (this.anoLancamento > 1985) {
                ano = Integer.parseInt(DataHoje.livre(new Date(), "YYYY")) - this.anoLancamento;
            }
            return ano <= 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Titulo other = (Titulo) obj;
        return true;
    }

    @Override
    public String toString() {
        return "Titulo{" + "id=" + id + ", data=" + data + ", descricao=" + descricao + ", autor=" + autor + ", atores=" + atores + ", idadeMinima=" + idadeMinima + ", qtdePorEmbalagem=" + qtdePorEmbalagem + ", genero=" + genero + ", barras=" + barras + ", duracao=" + duracao + ", anoLancamento=" + anoLancamento + ", legenda=" + legenda + ", formato=" + formato + ", imprimeEtiqueta=" + imprimeEtiqueta + ", foto=" + foto + '}';
    }

}
