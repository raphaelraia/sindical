package br.com.rtools.associativo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "eve_campeonato_modalidade")
@NamedQueries({
    @NamedQuery(name = "CampeonatoModalidade.pesquisaID", query = "SELECT S FROM CampeonatoModalidade AS S WHERE S.id = :pid"),
    @NamedQuery(name = "CampeonatoModalidade.findAll", query = "SELECT S FROM CampeonatoModalidade AS S ORDER BY S.descricao ASC "),
    @NamedQuery(name = "CampeonatoModalidade.findName", query = "SELECT S FROM CampeonatoModalidade AS S WHERE UPPER(S.descricao) LIKE :pdescricao ORDER BY S.descricao ASC ")
})
public class CampeonatoModalidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;

    public CampeonatoModalidade() {
        this.id = -1;
        this.descricao = "";
    }

    public CampeonatoModalidade(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
