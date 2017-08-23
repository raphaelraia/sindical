package br.com.rtools.relatorios;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_relatorio_tipo")
@NamedQueries({
    @NamedQuery(name = "RelatorioTipo.findAll", query = "SELECT RT FROM RelatorioTipo AS RT ORDER BY RT.descricao ASC")
})
public class RelatorioTipo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 100, nullable = false, unique = true)
    private String descricao;

    public RelatorioTipo() {
        this.id = null;
        this.descricao = "";
    }

    public RelatorioTipo(Integer id, String descricao) {
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
