package br.com.rtools.locadoraFilme;

import br.com.rtools.locadoraFilme.dao.CatalogoDao;
import br.com.rtools.locadoraFilme.dao.TituloDao;
import br.com.rtools.seguranca.MacFilial;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "loc_titulo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ds_descricao", "id_genero", "ds_mes_ano_lancamento"})
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
    @Column(name = "ds_mes_ano_lancamento")
    private String mesAnoLancamento;
    @Column(name = "ds_legenda", length = 10)
    private String legenda;
    @Column(name = "ds_formato", length = 20)
    private String formato;
    @Column(name = "is_imprime_etiqueta", columnDefinition = "boolean default false")
    private Boolean imprimeEtiqueta;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_foto")
    private Date foto;

    @Transient
    private ConfiguracaoLocadora configuracaoLocadora = null;

    @Transient
    private Integer quantidadeDisponivel;

    @Transient
    private Integer quantidadeEstoqueFilial;

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
        this.mesAnoLancamento = "";
        this.legenda = "";
        this.formato = "";
        this.imprimeEtiqueta = false;
        this.foto = null;
        this.configuracaoLocadora = null;
        this.quantidadeDisponivel = null;
        this.quantidadeEstoqueFilial = null;
    }

    public Titulo(Integer id, Date data, String descricao, String autor, String atores, Integer idadeMinima, Integer qtdePorEmbalagem, Genero genero, String barras, String duracao, String mesAnoLancamento, String legenda, String formato, Boolean imprimeEtiqueta, Date foto) {
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
        this.mesAnoLancamento = mesAnoLancamento;
        this.legenda = legenda;
        this.formato = formato;
        this.imprimeEtiqueta = imprimeEtiqueta;
        this.foto = foto;
        this.configuracaoLocadora = null;
        this.quantidadeDisponivel = null;
        this.quantidadeEstoqueFilial = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdString() {
        try {
            return Integer.toString(id);
        } catch (Exception e) {
            return "";
        }
    }

    public void setIdString(String idString) {
        this.id = Integer.parseInt(idString);
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

    public String getDataString() {
        return DataHoje.converteData(data);
    }

    public void setDataString(String data) {
        this.data = DataHoje.converte(data);
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
//        try {
//            Integer ano = 0;
//            if (Integer.parseInt(this.mesAnoLancamento) > 1985) {
//                ConfiguracaoLocadora.get().getMesesLancamento();
//                ano = Integer.parseInt(DataHoje.livre(new Date(), "YYYY")) - Integer.parseInt(this.mesAnoLancamento);
//            }
//            return ano <= 1;
//        } catch (Exception e) {
//            return false;
//        }
        try {
            if (!this.mesAnoLancamento.isEmpty()) {
                DataHoje dataHoje = new DataHoje();
                Integer ano = 0;
                String data_hoje = DataHoje.data();
                String data_lancamento = "01/" + this.mesAnoLancamento;
                String validade_lancamento = dataHoje.incrementarMeses(getConfiguracaoLocadora().getMesesLancamento(), "01/" + this.mesAnoLancamento);
                if (DataHoje.maiorData(validade_lancamento, data_hoje)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Titulo{" + "id=" + id + ", data=" + data + ", descricao=" + descricao + ", autor=" + autor + ", atores=" + atores + ", idadeMinima=" + idadeMinima + ", qtdePorEmbalagem=" + qtdePorEmbalagem + ", genero=" + genero + ", barras=" + barras + ", duracao=" + duracao + ", mesAnoLancamento=" + mesAnoLancamento + ", legenda=" + legenda + ", formato=" + formato + ", imprimeEtiqueta=" + imprimeEtiqueta + ", foto=" + foto + '}';
    }

    public String getMesAnoLancamento() {
        return mesAnoLancamento;
    }

    public void setMesAnoLancamento(String mesAnoLancamento) {
        this.mesAnoLancamento = mesAnoLancamento;
    }

    public ConfiguracaoLocadora getConfiguracaoLocadora() {
        if (configuracaoLocadora == null) {
            configuracaoLocadora = ConfiguracaoLocadora.get();
        }
        return configuracaoLocadora;
    }

    public void setConfiguracaoLocadora(ConfiguracaoLocadora configuracaoLocadora) {
        this.configuracaoLocadora = configuracaoLocadora;
    }

    public Integer getQuantidadeDisponivel() {
        if (this.id != null) {
            if (quantidadeDisponivel == null) {
                quantidadeDisponivel = new TituloDao().locadoraQuantidadeTituloDisponivel(MacFilial.getAcessoFilial().getFilial().getId(), this.id);
            }
        }
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Integer getQuantidadeEstoqueFilial() {
        if (this.id != null) {
            if (quantidadeEstoqueFilial == null) {
                Catalogo c = new CatalogoDao().find(MacFilial.getAcessoFilial().getFilial().getId(), this.id);
                if (c == null) {
                    quantidadeEstoqueFilial = 0;
                } else {
                    quantidadeEstoqueFilial = c.getQuantidade();                    
                }
            }
        }
        return quantidadeEstoqueFilial;
    }

    public void setQuantidadeEstoqueFilial(Integer quantidadeEstoqueFilial) {
        this.quantidadeEstoqueFilial = quantidadeEstoqueFilial;
    }

}
