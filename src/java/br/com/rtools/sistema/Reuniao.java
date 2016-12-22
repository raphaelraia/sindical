package br.com.rtools.sistema;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_reuniao")
public class Reuniao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_titulo", length = 100, nullable = false)
    private String titulo;
    @Column(name = "ds_descricao")
    @Lob
    private String descricao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_criacao", nullable = false)
    private Date dtCriacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_reuniao", nullable = false)
    private Date dtReuniao;
    @Column(name = "ds_horario", length = 6)
    private String horario;
    @JoinColumn(name = "id_operador", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario operador;
    @Column(name = "ds_pauta")
    @Lob
    private String pauta;

    public Reuniao() {
        this.id = null;
        this.titulo = "";
        this.descricao = "";
        this.dtCriacao = new Date();
        this.dtReuniao = null;
        this.horario = "";
        this.operador = null;
        this.pauta = "";
    }

    public Reuniao(Integer id, String titulo, String descricao, Date dtCriacao, Date dtReuniao, String horario, Usuario operador, String pauta) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dtCriacao = dtCriacao;
        this.dtReuniao = dtReuniao;
        this.horario = horario;
        this.operador = operador;
        this.pauta = pauta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public Date getDtReuniao() {
        return dtReuniao;
    }

    public void setDtReuniao(Date dtReuniao) {
        this.dtReuniao = dtReuniao;
    }

    public String getCriacao() {
        return DataHoje.converteData(dtCriacao);
    }

    public void setCriacao(String criacao) {
        this.dtCriacao = DataHoje.converte(criacao);
    }

    public String getReuniao() {
        return DataHoje.converteData(dtReuniao);
    }

    public void setReuniao(String reuniao) {
        this.dtReuniao = DataHoje.converte(reuniao);
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Usuario getOperador() {
        return operador;
    }

    public void setOperador(Usuario operador) {
        this.operador = operador;
    }

    public String getPauta() {
        return pauta;
    }

    public void setPauta(String pauta) {
        this.pauta = pauta;
    }

}
