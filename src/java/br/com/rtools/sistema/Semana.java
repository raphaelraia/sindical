package br.com.rtools.sistema;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_semana")
@NamedQuery(name = "Semana.pesquisaID", query = "SELECT Sem FROM Semana Sem WHERE Sem.id = :pid")
public class Semana implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 15, unique = true)
    private String descricao;
    @Column(name = "nr_postgres")
    private Integer postgres;

    public Semana() {
        this.id = -1;
        this.descricao = "";
        this.postgres = null;
    }

    public Semana(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Semana(Integer id, String descricao, Integer postgres) {
        this.id = id;
        this.descricao = descricao;
        this.postgres = postgres;
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

    public Integer getCurrentDay() {
        return new Date().getDay() + 1;
    }

    public Integer getPostgres() {
        return postgres;
    }

    public void setPostgres(Integer postgres) {
        this.postgres = postgres;
    }

}
