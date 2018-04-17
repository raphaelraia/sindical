package br.com.rtools.sql;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sql_events")
public class SqlEvents implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50)
    private String descricao;
    @Column(name = "nr_case")
    private Integer nrCase;

    public SqlEvents() {
        this.id = null;
        this.descricao = null;
        this.nrCase = null;
    }

    public SqlEvents(Integer id, String descricao, Integer nrCase) {
        this.id = id;
        this.descricao = descricao;
        this.nrCase = nrCase;
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

    public Integer getNrCase() {
        return nrCase;
    }

    public void setNrCase(Integer nrCase) {
        this.nrCase = nrCase;
    }

}
