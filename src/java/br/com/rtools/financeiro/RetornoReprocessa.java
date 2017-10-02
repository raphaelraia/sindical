package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "fin_retorno_reprocessa")
public class RetornoReprocessa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_conta_cobranca", referencedColumnName = "id")
    @ManyToOne
    private ContaCobranca contaCobranca;
    @Column(name = "nr_sequencial")
    private Integer sequencial;

    public RetornoReprocessa() {
        this.id = -1;
        this.contaCobranca = new ContaCobranca();
        this.sequencial = 0;
    }

    public RetornoReprocessa(int id, ContaCobranca contaCobranca, Integer sequencial) {
        this.id = id;
        this.contaCobranca = contaCobranca;
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

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

}
