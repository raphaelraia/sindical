package br.com.rtools.relatorios;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sis_relatorio_grupo")
public class RelatorioGrupo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_relatorio", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Relatorios relatorio;
    @Column(name = "ds_grupo", length = 100, nullable = false)
    private String grupo;  

    public RelatorioGrupo() {
        this.id = -1;
        this.relatorio = new Relatorios();
        this.grupo = "";
    }
    
    public RelatorioGrupo(Integer id, Relatorios relatorio, String grupo) {
        this.id = id;
        this.relatorio = relatorio;
        this.grupo = grupo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Relatorios getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorios relatorio) {
        this.relatorio = relatorio;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

}
