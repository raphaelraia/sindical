package br.com.rtools.financeiro;

import br.com.rtools.pessoa.Filial;
import javax.persistence.*;

@Entity
@Table(name = "fin_centro_custo")
@NamedQuery(name = "CentroCusto.pesquisaID", query = "select cc from CentroCusto cc where cc.id = :pid")
public class CentroCusto implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao")
    private String descricao;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne
    private Filial filial;

    public CentroCusto() {
        this.id = -1;
        this.descricao = "";
        this.filial = new Filial();
    }

    public CentroCusto(Integer id, String descricao, Filial filial) {
        this.id = id;
        this.descricao = descricao;
        this.filial = filial;
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

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

}
