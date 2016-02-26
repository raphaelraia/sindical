package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sort_movimento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_sorteio", "id_pessoa"})
)
public class SorteioMovimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_sorteio", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Sorteio sorteio;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_sorteio", nullable = false)
    private Date dtSorteio;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario operador;

    public SorteioMovimento() {
        this.id = null;
        this.sorteio = null;
        this.pessoa = null;
        this.dtSorteio = null;
        this.operador = null;
    }

    public SorteioMovimento(Integer id, Sorteio sorteio, Pessoa pessoa, Date dtSorteio, Usuario operador) {
        this.id = id;
        this.sorteio = sorteio;
        this.pessoa = pessoa;
        this.dtSorteio = dtSorteio;
        this.operador = operador;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtSorteio() {
        return dtSorteio;
    }

    public void setDtSorteio(Date dtSorteio) {
        this.dtSorteio = dtSorteio;
    }

    public String getSorteioString() {
        return DataHoje.converteData(dtSorteio);
    }
    
    public String getHorario() {
        return DataHoje.converteHora(dtSorteio);
    }

    public void setSorteioString(String sorteioString) {
        this.dtSorteio = DataHoje.converte(sorteioString);
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    @Override
    public String toString() {
        return "SorteioMovimento{" + "id=" + id + ", sorteio=" + sorteio + ", pessoa=" + pessoa + ", dtSorteio=" + dtSorteio + ", operador=" + operador + '}';
    }

}
