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
@Table(name = "sis_relatorio_join")
public class RelatorioJoin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_relatorio", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Relatorios relatorio;
    @Column(name = "ds_join", length = 100, nullable = false)
    private String join;  

    public RelatorioJoin() {
        this.id = -1;
        this.relatorio = new Relatorios();
        this.join = "";
    }
    
    public RelatorioJoin(Integer id, Relatorios relatorio, String join) {
        this.id = id;
        this.relatorio = relatorio;
        this.join = join;
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

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

}
