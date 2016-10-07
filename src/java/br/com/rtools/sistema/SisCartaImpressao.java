package br.com.rtools.sistema;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_carta_impressao")
public class SisCartaImpressao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_carta", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SisCarta sisCarta;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario operador;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_impressao", nullable = true)
    private Date dtImpressao;

    public SisCartaImpressao() {
        this.id = null;
        this.sisCarta = null;
        this.pessoa = null;
        this.operador = null;
        this.rotina = null;
        this.dtImpressao = null;
    }

    public SisCartaImpressao(Integer id, SisCarta sisCarta, Pessoa pessoa, Usuario operador, Rotina rotina, Date dtImpressao) {
        this.id = id;
        this.sisCarta = sisCarta;
        this.pessoa = pessoa;
        this.operador = operador;
        this.rotina = rotina;
        this.dtImpressao = dtImpressao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SisCarta getSisCarta() {
        return sisCarta;
    }

    public void setSisCarta(SisCarta sisCarta) {
        this.sisCarta = sisCarta;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Date getDtImpressao() {
        return dtImpressao;
    }

    public void setDtImpressao(Date dtImpressao) {
        this.dtImpressao = dtImpressao;
    }

}
