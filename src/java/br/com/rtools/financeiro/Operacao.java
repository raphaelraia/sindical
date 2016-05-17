package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name = "fin_operacao")
@NamedQueries({
    @NamedQuery(name = "Operacao.findAll", query = "SELECT O FROM Operacao O ORDER BY O.descricao ASC"),
    @NamedQuery(name = "Operacao.findName", query = "SELECT O FROM Operacao O WHERE UPPER(O.descricao) LIKE :pdescricao ORDER BY O.descricao ASC")
})
public class Operacao implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 100, nullable = false)
    private String descricao;
    @Column(name = "ds_es", length = 1, columnDefinition = "character varying default ''")
    private String es;
    @Column(name = "is_centro_custo", columnDefinition = "boolean default false")
    private Boolean centroCusto;

    public Operacao() {
        this.id = -1;
        this.descricao = "";
        this.es = "";
        this.centroCusto = false;
    }

    public Operacao(Integer id, String descricao, String es, Boolean centroCusto) {
        this.id = id;
        this.descricao = descricao;
        this.es = es;
        this.centroCusto = centroCusto;
    }

    public int getId() {
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

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public Boolean getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(Boolean centroCusto) {
        this.centroCusto = centroCusto;
    }
}
