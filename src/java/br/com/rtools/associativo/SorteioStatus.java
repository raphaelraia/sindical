package br.com.rtools.associativo;

import br.com.rtools.arrecadacao.GrupoCidade;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sort_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_sorteio", "nr_carencia_debito", "id_grupo_cidade"})
)
public class SorteioStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_sorteio", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Sorteio sorteio;
    @Column(name = "nr_carencia_debito", nullable = false, length = 250)
    private Integer carenciaDebito;
    @JoinColumn(name = "id_grupo_cidade", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private GrupoCidade grupoCidade;
    @Column(name = "nr_admissao_meses")
    private Integer admissaoMeses;
    @Column(name = "nr_filiacao_meses ")
    private Integer filiacaoMeses;

    public SorteioStatus() {
        this.id = null;
        this.sorteio = null;
        this.carenciaDebito = 0;
        this.grupoCidade = null;
        this.admissaoMeses = 0;
        this.filiacaoMeses = 0;
    }

    public SorteioStatus(Integer id, Sorteio sorteio, Integer carenciaDebito, GrupoCidade grupoCidade, Integer admissaoMeses, Integer filiacaoMeses) {
        this.id = id;
        this.sorteio = sorteio;
        this.carenciaDebito = carenciaDebito;
        this.grupoCidade = grupoCidade;
        this.admissaoMeses = admissaoMeses;
        this.filiacaoMeses = filiacaoMeses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sorteio getSorteio() {
        return sorteio;
    }

    public void setSorteio(Sorteio sorteio) {
        this.sorteio = sorteio;
    }

    public Integer getCarenciaDebito() {
        return carenciaDebito;
    }

    public void setCarenciaDebito(Integer carenciaDebito) {
        this.carenciaDebito = carenciaDebito;
    }

    public GrupoCidade getGrupoCidade() {
        return grupoCidade;
    }

    public void setGrupoCidade(GrupoCidade grupoCidade) {
        this.grupoCidade = grupoCidade;
    }

    public Integer getAdmissaoMeses() {
        return admissaoMeses;
    }

    public void setAdmissaoMeses(Integer admissaoMeses) {
        this.admissaoMeses = admissaoMeses;
    }

    public Integer getFiliacaoMeses() {
        return filiacaoMeses;
    }

    public void setFiliacaoMeses(Integer filiacaoMeses) {
        this.filiacaoMeses = filiacaoMeses;
    }

    @Override
    public String toString() {
        return "SorteioStatus{" + "id=" + id + ", sorteio=" + sorteio + ", carenciaDebito=" + carenciaDebito + ", grupoCidade=" + grupoCidade + ", admissaoMeses=" + admissaoMeses + ", filiacaoMeses=" + filiacaoMeses + '}';
    }

}
