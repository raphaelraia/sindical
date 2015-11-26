package br.com.rtools.associativo;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "soc_catraca_frequencia",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_pessoa", "id_departamento", "dt_entrada", "dt_saida"})
)

public class CatracaFrequencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servicos;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Departamento departamento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_entrada", nullable = false)
    private Date dtEntrada;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_saida", nullable = false)
    private Date dtSaida;

    public CatracaFrequencia() {
        this.id = null;
        this.servicos = null;
        this.departamento = null;
        this.dtEntrada = null;
        this.dtSaida = null;
    }

    public CatracaFrequencia(Integer id, Servicos servicos, Departamento departamento, Date dtEntrada, Date dtSaida) {
        this.id = id;
        this.servicos = servicos;
        this.departamento = departamento;
        this.dtEntrada = dtEntrada;
        this.dtSaida = dtSaida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Date getDtEntrada() {
        return dtEntrada;
    }

    public void setDtEntrada(Date dtEntrada) {
        this.dtEntrada = dtEntrada;
    }

    public Date getDtSaida() {
        return dtSaida;
    }

    public void setDtSaida(Date dtSaida) {
        this.dtSaida = dtSaida;
    }

    public String getDataEntradaString() {
        if (dtEntrada != null) {
            return DataHoje.livre(dtEntrada, "dd/MM/yyyy");
        }
        return null;
    }

    public String getHoraEntradaString() {
        if (dtEntrada != null) {
            return DataHoje.livre(dtEntrada, "HH:mm");
        }
        return null;
    }

    public String getDataSaidaString() {
        if (dtSaida != null) {
            return DataHoje.livre(dtSaida, "dd/MM/yyyy");
        }
        return null;
    }

    public String getHoraSaidaString() {
        if (dtSaida != null) {
            return DataHoje.livre(dtSaida, "HH:mm");
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CatracaFrequencia other = (CatracaFrequencia) obj;
        return true;
    }

    @Override
    public String toString() {
        return "CatracaFrequencia{" + "id=" + id + ", servicos=" + servicos + ", departamento=" + departamento + ", dtEntrada=" + dtEntrada + ", dtSaida=" + dtSaida + '}';
    }

}
