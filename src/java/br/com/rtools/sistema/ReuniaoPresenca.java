package br.com.rtools.sistema;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_reuniao_presenca")
public class ReuniaoPresenca implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_reuniao", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Reuniao reuniao;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario operador;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cadastro", nullable = false)
    private Date dtCadastro;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_presenca")
    private Date dtPresenca;

    public ReuniaoPresenca() {
        this.id = null;
        this.reuniao = null;
        this.pessoa = null;
        this.operador = null;
        this.dtCadastro = new Date();
        this.dtPresenca = null;
    }

    public ReuniaoPresenca(Integer id, Reuniao reuniao, Pessoa pessoa, Usuario operador, Date dtCadastro, Date dtPresenca) {
        this.id = id;
        this.reuniao = reuniao;
        this.pessoa = pessoa;
        this.operador = operador;
        this.dtCadastro = dtCadastro;
        this.dtPresenca = dtPresenca;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    public void setReuniao(Reuniao reuniao) {
        this.reuniao = reuniao;
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

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public Date getDtPresenca() {
        return dtPresenca;
    }

    public void setDtPresenca(Date dtPresenca) {
        this.dtPresenca = dtPresenca;
    }

    public String getCadastro() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setCadastro(String cadastro) {
        this.dtCadastro = DataHoje.converte(cadastro);
    }

    public String getPresenca() {
        return DataHoje.converteData(dtPresenca);
    }

    public void setPresenca(String presenca) {
        this.dtPresenca = DataHoje.converte(presenca);
    }

}
