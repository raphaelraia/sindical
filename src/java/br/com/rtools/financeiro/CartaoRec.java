package br.com.rtools.financeiro;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_cartao_rec")
@NamedQuery(name = "CartaoRec.pesquisaID", query = "select cr from CartaoRec cr where cr.id = :pid")
public class CartaoRec implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_liquidacao")
    private Date dtLiquidacao;

    public CartaoRec(int id, Date dtLiquidacao) {
        this.id = id;
        this.dtLiquidacao = dtLiquidacao;
    }

    public CartaoRec() {
        this.id = -1;
    }

    public CartaoRec(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDtLiquidacao() {
        return dtLiquidacao;
    }

    public void setDtLiquidacao(Date dtLiquidacao) {
        this.dtLiquidacao = dtLiquidacao;
    }
}
