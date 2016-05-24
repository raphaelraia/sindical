package br.com.rtools.financeiro;

import br.com.rtools.sistema.Mes;
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
@Table(name = "fin_servico_pessoa_bloqueio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_mes", "id_servico_pessoa"})
)
public class ServicoPessoaBloqueio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_mes", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Mes mes;
    @JoinColumn(name = "id_servico_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ServicoPessoa servicoPessoa;

    public ServicoPessoaBloqueio() {
        this.id = null;
        this.mes = null;
        this.servicoPessoa = null;
    }

    public ServicoPessoaBloqueio(Integer id, Mes mes, ServicoPessoa servicoPessoa) {
        this.id = id;
        this.mes = mes;
        this.servicoPessoa = servicoPessoa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    @Override
    public String toString() {
        return "ServicoPessoaBloqueio{" + "id=" + id + ", mes=" + mes + ", servicoPessoa=" + servicoPessoa + '}';
    }

}
