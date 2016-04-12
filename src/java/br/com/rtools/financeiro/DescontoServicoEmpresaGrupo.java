package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "fin_desconto_servico_empresa_grupo")
@NamedQueries({
    @NamedQuery(name = "DescontoServicoEmpresaGrupo.findAll", query = "SELECT DSEG FROM DescontoServicoEmpresaGrupo AS DSEG ORDER BY DSEG.descricao ASC ")
})
public class DescontoServicoEmpresaGrupo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 100, nullable = false)
    private String descricao;

    public DescontoServicoEmpresaGrupo() {
        this.id = -1;
        this.descricao = "";
    }

    public DescontoServicoEmpresaGrupo(Integer id, String descricao) {
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

    @Override
    public String toString() {
        return "DescontoServicoEmpresaGrupo{" + "id=" + id + ", descricao=" + descricao + '}';
    }

}
