package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "arr_relacao_empregados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_relacao", "id_pessoa"})
)
public class RelacaoEmpregados implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_relacao", referencedColumnName = "id", nullable = false)
    @OneToOne
    private RelacaoEmpregadosRef relacao;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_entrega", nullable = false)
    private Date dtEntrega;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Pessoa operador;

    public RelacaoEmpregados() {
        this.id = null;
        this.relacao = null;
        this.pessoa = null;
        this.dtEntrega = null;
        this.operador = null;
    }

    public RelacaoEmpregados(Integer id, RelacaoEmpregadosRef relacao, Pessoa pessoa, Date dtEntrega, Pessoa operador) {
        this.id = id;
        this.relacao = relacao;
        this.pessoa = pessoa;
        this.dtEntrega = dtEntrega;
        this.operador = operador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RelacaoEmpregadosRef getRelacao() {
        return relacao;
    }

    public void setRelacao(RelacaoEmpregadosRef relacao) {
        this.relacao = relacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtEntrega() {
        return dtEntrega;
    }

    public void setDtEntrega(Date dtEntrega) {
        this.dtEntrega = dtEntrega;
    }

    public Pessoa getOperador() {
        return operador;
    }

    public void setOperador(Pessoa operador) {
        this.operador = operador;
    }

}
