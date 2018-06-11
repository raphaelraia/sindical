package br.com.rtools.pessoa;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "pes_tipo_tratamento")
@NamedQueries({
    @NamedQuery(name = "TipoTratamento.findAll", query = "SELECT T FROM TipoTratamento AS T ORDER BY T.descricao ASC ")
    ,
    @NamedQuery(name = "TipoTratamento.findName", query = "SELECT T FROM TipoTratamento AS T WHERE UPPER(T.descricao) LIKE :pdescricao ORDER BY T.descricao ASC ")
})
public class TipoTratamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false, unique = true)
    private String descricao;

    public TipoTratamento() {
        this.id = null;
        this.descricao = "";
    }

    public TipoTratamento(Integer id, String descricao) {
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
