package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "soc_credenciadores")
@NamedQueries({
    @NamedQuery(name = "Credenciadores.findAll", query = "SELECT C FROM Credenciadores AS C ORDER BY C.pessoa.nome ASC ")
})
public class Credenciadores implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inativacao")
    private Date dtInativacao;

    public Credenciadores() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.dtInativacao = null; //DataHoje.dataHoje();
    }

    public Credenciadores(Integer id, Pessoa pessoa, Date dtInativacao) {
        this.id = id;
        this.pessoa = pessoa;
        this.dtInativacao = dtInativacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDtInativacaoString() {
        return DataHoje.converteData(dtInativacao);
    }

    public void setDtInativacaoString(String dtInativacaoString) {
        this.dtInativacao = DataHoje.converte(dtInativacaoString);
    }

    public Date getDtInativacao() {
        return dtInativacao;
    }

    public void setDtInativacao(Date dtInativacao) {
        this.dtInativacao = dtInativacao;
    }

}
