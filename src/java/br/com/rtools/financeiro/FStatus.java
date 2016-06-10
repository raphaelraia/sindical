package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "fin_status")
@NamedQueries({
    @NamedQuery(name = "FStatus.findAll", query = "SELECT FS FROM FStatus AS FS ORDER BY FS.descricao ASC ")
})
public class FStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 50, unique = true, nullable = false)
    private String descricao;
    @Column(name = "ds_historico", length = 500)
    private String historico;

    public FStatus() {
        this.id = -1;
        this.descricao = "";
        this.historico = "";
    }

    public FStatus(int id, String descricao, String historico) {
        this.id = id;
        this.descricao = descricao;
        this.historico = historico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}
