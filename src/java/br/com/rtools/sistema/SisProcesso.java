package br.com.rtools.sistema;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_processo")
public class SisProcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nr_tempo")
    private Long tempo;
    @Column(name = "nr_tempo_query")
    private Long tempoQuery;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data", nullable = true)
    private Date data;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @JoinColumn(name = "id_relatorio", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Relatorios relatorio;
    @Column(name = "ds_processo", length = 255)
    private String processo;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "is_web", columnDefinition = "boolean default false", nullable = false)
    private Boolean web;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_abortado", nullable = true)
    private Date abortado;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_finalizado", nullable = true)
    private Date finalizado;

    public SisProcesso() {
        this.id = null;
        this.tempo = new Long(0);
        this.tempoQuery = new Long(0);
        this.data = null;
        this.rotina = null;
        this.relatorio = null;
        this.processo = null;
        this.usuario = null;
        this.pessoa = null;
        this.web = false;
        this.abortado = null;
        this.finalizado = null;
    }

    public SisProcesso(Integer id, Long tempo, Long tempoQuery, Date data, Rotina rotina, Relatorios relatorio, String processo, Usuario usuario, Pessoa pessoa, Boolean web, Date abortado, Date finalizado) {
        this.id = id;
        this.tempo = tempo;
        this.tempoQuery = tempoQuery;
        this.data = data;
        this.rotina = rotina;
        this.relatorio = relatorio;
        this.processo = processo;
        this.usuario = usuario;
        this.pessoa = pessoa;
        this.web = web;
        this.abortado = abortado;
        this.finalizado = finalizado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public void start() {
        tempo = System.currentTimeMillis();
    }

    public void startQuery() {
        tempoQuery = System.currentTimeMillis();
    }

    public void finishQuery() {
        tempoQuery = System.currentTimeMillis() - tempoQuery;
    }

    public void finish() {
        if (this.processo != null && !this.processo.isEmpty()) {
            Usuario u = Usuario.getUsuario();
            if (u != null) {
                tempo = System.currentTimeMillis() - tempo;
                this.setRotina(new Rotina().get());
                this.setData(new Date());
                new Dao().save(this, true);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SisProcesso other = (SisProcesso) obj;
        return true;
    }

    public Long getTempoQuery() {
        return tempoQuery;
    }

    public void setTempoQuery(Long tempoQuery) {
        this.tempoQuery = tempoQuery;
    }

    public Relatorios getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorios relatorio) {
        this.relatorio = relatorio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getWeb() {
        return web;
    }

    public void setWeb(Boolean web) {
        this.web = web;
    }

    public Date getAbortado() {
        return abortado;
    }

    public void setAbortado(Date abortado) {
        this.abortado = abortado;
    }

    public Date getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Date finalizado) {
        this.finalizado = finalizado;
    }

    @Override
    public String toString() {
        return "SisProcesso{" + "id=" + id + ", tempo=" + tempo + ", tempoQuery=" + tempoQuery + ", data=" + data + ", rotina=" + rotina + ", relatorio=" + relatorio + ", processo=" + processo + ", usuario=" + usuario + ", pessoa=" + pessoa + ", web=" + web + ", abortado=" + abortado + ", finalizado=" + finalizado + '}';
    }

}
