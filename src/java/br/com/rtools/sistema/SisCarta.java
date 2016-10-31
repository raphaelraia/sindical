package br.com.rtools.sistema;

import br.com.rtools.seguranca.Usuario;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sis_carta")
public class SisCarta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_tipo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private SisCartaTipo tipo;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario operador;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cadastro", nullable = true)
    private Date dtCadastro;
    @Column(name = "ds_titulo", length = 100)
    private String titulo;
    @Column(name = "ds_texto", length = 5000)
    private String texto;
    @Column(name = "ds_sql", length = 2000)
    private String sql;

    public SisCarta() {
        this.id = null;
        this.operador = null;
        this.tipo = null;
        this.dtCadastro = DataHoje.dataHoje();
        this.titulo = "";
        this.texto = "";
        this.sql = "";
    }

    public SisCarta(Integer id, SisCartaTipo tipo, Usuario operador, Date dtCadastro, String titulo, String texto, String sql) {
        this.id = id;
        this.tipo = tipo;
        this.operador = operador;
        this.dtCadastro = dtCadastro;
        this.titulo = titulo;
        this.texto = texto;
        this.sql = sql;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SisCartaTipo getTipo() {
        return tipo;
    }

    public void setTipo(SisCartaTipo tipo) {
        this.tipo = tipo;
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

    public String getDtCadastroString() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setDtCadastroString(String dtCadastroString) {
        this.dtCadastro = DataHoje.converte(dtCadastroString);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
