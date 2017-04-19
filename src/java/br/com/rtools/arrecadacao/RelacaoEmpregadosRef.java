package br.com.rtools.arrecadacao;

import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "arr_relacao_empregados_ref")
public class RelacaoEmpregadosRef implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_referencia", length = 7, nullable = false, unique = true)
    private String referencia;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_carencia_entrega", nullable = false)
    private Date dtCarenciaEntrega;
    @Column(name = "is_bloqueia_homologacao ", columnDefinition = "boolean default false", nullable = false)
    private Boolean bloqueiaHomologacao;

    public RelacaoEmpregadosRef() {
        this.id = null;
        this.referencia = "";
        this.dtCarenciaEntrega = null;
        this.bloqueiaHomologacao = false;
    }

    public RelacaoEmpregadosRef(Integer id, String referencia, Date dtCarenciaEntrega, Boolean bloqueiaHomologacao) {
        this.id = id;
        this.referencia = referencia;
        this.dtCarenciaEntrega = dtCarenciaEntrega;
        this.bloqueiaHomologacao = bloqueiaHomologacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtCarenciaEntrega() {
        return dtCarenciaEntrega;
    }

    public void setDtCarenciaEntrega(Date dtCarenciaEntrega) {
        this.dtCarenciaEntrega = dtCarenciaEntrega;
    }

    public String getCarenciaEntrega() {
        return DataHoje.converteData(dtCarenciaEntrega);
    }

    public void setCarenciaEntrega(String carenciaEntrega) {
        this.dtCarenciaEntrega = DataHoje.converte(carenciaEntrega);
    }

    public Boolean getBloqueiaHomologacao() {
        return bloqueiaHomologacao;
    }

    public void setBloqueiaHomologacao(Boolean bloqueiaHomologacao) {
        this.bloqueiaHomologacao = bloqueiaHomologacao;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

}
