package br.com.rtools.sql;

import br.com.rtools.sistema.Configuracao;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sql_atualizar_base_cliente")
public class AtualizarBaseCliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_atualizar_base", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private AtualizarBase atualizarBase;
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Configuracao cliente;
    @Column(name = "dt_atualizacao")
    @Temporal(TemporalType.DATE)
    private Date dtAtualizacao;

    public AtualizarBaseCliente() {
        this.id = null;
        this.atualizarBase = null;
        this.cliente = null;
        this.dtAtualizacao = null;
    }

    public AtualizarBaseCliente(Integer id, AtualizarBase atualizarBase, Configuracao cliente, Date dtAtualizacao) {
        this.id = id;
        this.atualizarBase = atualizarBase;
        this.cliente = cliente;
        this.dtAtualizacao = dtAtualizacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AtualizarBase getAtualizarBase() {
        return atualizarBase;
    }

    public void setAtualizarBase(AtualizarBase atualizarBase) {
        this.atualizarBase = atualizarBase;
    }

    public Configuracao getCliente() {
        return cliente;
    }

    public void setCliente(Configuracao cliente) {
        this.cliente = cliente;
    }

    public Date getDtAtualizacao() {
        return dtAtualizacao;
    }

    public void setDtAtualizacao(Date dtAtualizacao) {
        this.dtAtualizacao = dtAtualizacao;
    }

}
