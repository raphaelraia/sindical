package br.com.rtools.estoque;

import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "est_subgrupo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_grupo", "ds_descricao"})
)
@NamedQueries({
    @NamedQuery(name = "ProdutoSubGrupo.findAll", query = "SELECT PSG FROM ProdutoSubGrupo AS PSG ORDER BY PSG.produtoGrupo.descricao ASC, PSG.descricao ASC ")
    ,
    @NamedQuery(name = "ProdutoSubGrupo.findName", query = "SELECT PSG FROM ProdutoSubGrupo AS PSG WHERE UPPER(PSG.descricao) LIKE :pdescricao ORDER BY PSG.produtoGrupo.descricao ASC, PSG.descricao ASC ")
    ,
    @NamedQuery(name = "ProdutoSubGrupo.findGrupo", query = "SELECT PSG FROM ProdutoSubGrupo AS PSG WHERE PSG.produtoGrupo.id = :p1 ORDER BY PSG.produtoGrupo.descricao ASC, PSG.descricao ASC ")
})
public class ProdutoSubGrupo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_grupo", referencedColumnName = "id", nullable = false)
    @OneToOne
    private ProdutoGrupo produtoGrupo;
    @Column(name = "ds_descricao", length = 100, nullable = false)
    private String descricao;

    public ProdutoSubGrupo() {
        this.id = null;
        this.produtoGrupo = new ProdutoGrupo();
        this.descricao = "";
    }

    public ProdutoSubGrupo(Integer id, ProdutoGrupo produtoGrupo, String descricao) {
        this.produtoGrupo = produtoGrupo;
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProdutoGrupo getProdutoGrupo() {
        return produtoGrupo;
    }

    public void setProdutoGrupo(ProdutoGrupo produtoGrupo) {
        this.produtoGrupo = produtoGrupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
