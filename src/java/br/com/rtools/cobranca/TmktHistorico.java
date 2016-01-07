package br.com.rtools.cobranca;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Usuario;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "tlm_historico")
public class TmktHistorico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_lancamento")
    private Date lancamento;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario operador;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Departamento departamento;
    @JoinColumn(name = "id_contato", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TmktContato contato;
    @JoinColumn(name = "id_natureza", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TmktNatureza natureza;
    @Column(name = "ds_contato", length = 100)
    private String contatoDescricao;
    @Column(name = "ds_historico", length = 8000)
    private String historico;

    public TmktHistorico() {
        this.id = null;
        this.lancamento = new Date();
        this.pessoa = null;
        this.operador = null;
        this.departamento = null;
        this.contato = null;
        this.natureza = null;
        this.contatoDescricao = "";
        this.historico = "";
    }

    public TmktHistorico(Integer id, Date lancamento, Pessoa pessoa, Usuario operador, Departamento departamento, TmktContato contato, TmktNatureza natureza, String contatoDescricao, String historico) {
        this.id = id;
        this.lancamento = lancamento;
        this.pessoa = pessoa;
        this.operador = operador;
        this.departamento = departamento;
        this.contato = contato;
        this.natureza = natureza;
        this.contatoDescricao = contatoDescricao;
        this.historico = historico;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
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

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public TmktContato getContato() {
        return contato;
    }

    public void setContato(TmktContato contato) {
        this.contato = contato;
    }

    public TmktNatureza getNatureza() {
        return natureza;
    }

    public void setNatureza(TmktNatureza natureza) {
        this.natureza = natureza;
    }

    public String getContatoDescricao() {
        return contatoDescricao;
    }

    public void setContatoDescricao(String contatoDescricao) {
        this.contatoDescricao = contatoDescricao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    @Override
    public String toString() {
        return "TmktHistorico{" + "id=" + id + ", lancamento=" + lancamento + ", pessoa=" + pessoa + ", operador=" + operador + ", departamento=" + departamento + ", contato=" + contato + ", natureza=" + natureza + ", contatoDescricao=" + contatoDescricao + ", historico=" + historico + '}';
    }

}
