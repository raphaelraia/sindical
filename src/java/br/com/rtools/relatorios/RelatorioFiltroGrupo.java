package br.com.rtools.relatorios;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sis_relatorio_filtro_grupo", uniqueConstraints = @UniqueConstraint(columnNames = {"id_rotina", "ds_descricao"}))
public class RelatorioFiltroGrupo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "ds_descricao", length = 100, nullable = false)
    private String descricao;
    @Column(name = "nr_ordem", length = 100, nullable = false, columnDefinition = "integer default 0")
    private Integer nrOrdem;

    public RelatorioFiltroGrupo() {
        this.id = null;
        this.rotina = null;
        this.descricao = "";
        this.nrOrdem = 0;
    }

    public RelatorioFiltroGrupo(Integer id, Rotina rotina, String descricao, Integer nrOrdem) {
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

    public Integer getNrOrdem() {
        return nrOrdem;
    }

    public void setNrOrdem(Integer nrOrdem) {
        this.nrOrdem = nrOrdem;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }
}
