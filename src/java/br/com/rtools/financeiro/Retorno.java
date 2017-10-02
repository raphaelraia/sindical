package br.com.rtools.financeiro;

import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "fin_retorno")
public class Retorno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_conta_cobranca", referencedColumnName = "id")
    @ManyToOne
    private ContaCobranca contaCobranca;
    @Column(name = "dt_retorno")
    @Temporal(TemporalType.DATE)
    private Date dtRetorno;
    @Column(name = "ds_arquivo")
    private String arquivo;
    @Column(name = "nr_sequencial")
    private Integer sequencial;

    public Retorno() {
        this.id = -1;
        this.contaCobranca = new ContaCobranca();
        this.dtRetorno = null;
        this.arquivo = "";
        this.sequencial = 0;
    }

    public Retorno(int id, ContaCobranca contaCobranca, Date dtRetorno, String arquivo, Integer sequencial) {
        this.id = id;
        this.contaCobranca = contaCobranca;
        this.dtRetorno = dtRetorno;
        this.arquivo = arquivo;
        this.sequencial = sequencial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContaCobranca getContaCobranca() {
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    public Date getDtRetorno() {
        return dtRetorno;
    }

    public void setDtRetorno(Date dtRetorno) {
        this.dtRetorno = dtRetorno;
    }

    public String getDtRetornoString() {
        return DataHoje.converteData(dtRetorno);
    }

    public void setDtRetornoString(String dtRetornoString) {
        this.dtRetorno = DataHoje.converte(dtRetornoString);
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

}
