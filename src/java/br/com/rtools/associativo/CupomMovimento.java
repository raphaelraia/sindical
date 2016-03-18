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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_cupom_movimento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_cupom", "id_pessoa", "dt_emissao"})
)
public class CupomMovimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_cupom", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Cupom cupom;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_emissao", nullable = false)
    private Date dtEmissao;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario operador;
    @Column(name = "ds_codigo", length = 50)
    private String codigo;

    @Transient
    private Boolean selected;

    public CupomMovimento() {
        this.id = null;
        this.cupom = null;
        this.pessoa = null;
        this.dtEmissao = null;
        this.operador = null;
        this.codigo = "";
        this.selected = false;
    }

    public CupomMovimento(Integer id, Cupom cupom, Pessoa pessoa, Date dtEmissao, Usuario operador, String codigo) {
        this.id = id;
        this.cupom = cupom;
        this.pessoa = pessoa;
        this.dtEmissao = dtEmissao;
        this.operador = operador;
        this.codigo = codigo;
        this.selected = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getEmissao() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setEmissao(String emissao) {
        this.dtEmissao = DataHoje.converte(emissao);
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "CupomMovimento{" + "id=" + id + ", cupom=" + cupom + ", pessoa=" + pessoa + ", dtEmissao=" + dtEmissao + ", operador=" + operador + ", codigo=" + codigo + ", selected=" + selected + '}';
    }

}
