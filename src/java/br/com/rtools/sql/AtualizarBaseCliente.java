package br.com.rtools.sql;

import br.com.rtools.sistema.Configuracao;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

    public AtualizarBaseCliente() {
        this.id = null;
        this.atualizarBase = null;
        this.cliente = null;
    }

    public AtualizarBaseCliente(Integer id, AtualizarBase atualizarBase, Configuracao cliente) {
        this.id = id;
        this.cliente = cliente;
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

}
