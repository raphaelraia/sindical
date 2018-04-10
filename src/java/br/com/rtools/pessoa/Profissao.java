package br.com.rtools.pessoa;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_profissao")
@NamedQueries({
    @NamedQuery(name = "Profissao.pesquisaID", query = "select prof from Profissao prof where prof.id=:pid")
    ,
    @NamedQuery(name = "Profissao.findAll", query = "SELECT P FROM Profissao AS P ORDER BY P.profissao ASC")
})
public class Profissao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_profissao", length = 200, nullable = false)
    private String profissao;
    @Column(name = "ds_cbo", length = 10, nullable = true)
    private String cbo;

    @Transient
    private String descricaoPesquisa;

    public Profissao() {
        this.id = -1;
        this.profissao = "";
        this.cbo = "";
        this.descricaoPesquisa = "";
    }

    public Profissao(Integer id, String profissao, String cbo) {
        this.id = id;
        this.profissao = profissao;
        this.cbo = cbo;
        this.descricaoPesquisa = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getCbo() {
        return cbo;
    }

    public void setCbo(String cbo) {
        this.cbo = cbo;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }
}
