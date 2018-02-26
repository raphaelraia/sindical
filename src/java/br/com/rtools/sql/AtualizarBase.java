/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.sql;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sql_atualizar_base")
public class AtualizarBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 5000, nullable = false)
    private String descricao;
    @Column(name = "nr_chamado")
    private Integer chamado;
    @Lob
    @Column(name = "ds_script", length = 5000, nullable = false)
    private String script;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_agendamento")
    private Date dtAgendamento;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_cadastro", nullable = false)
    private Date dtCadastro;
    @Column(name = "is_reiniciar", columnDefinition = "boolean default false")
    private Boolean reiniciar;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_processamento")
    private Date dtProcessamento;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;

    public AtualizarBase() {
        this.id = null;
        this.descricao = "";
        this.chamado = 0;
        this.script = "";
        this.dtAgendamento = null;
        this.dtCadastro = new Date();
        this.reiniciar = false;
        this.dtProcessamento = null;
        this.usuario = null;
    }

    public AtualizarBase(Integer id, String descricao, Integer chamado, String script, Date dtAgendamento, Date dtCadastro, Boolean reiniciar, Date dtProcessamento, Usuario usuario) {
        this.id = id;
        this.descricao = descricao;
        this.chamado = chamado;
        this.script = script;
        this.dtAgendamento = dtAgendamento;
        this.dtCadastro = dtCadastro;
        this.reiniciar = reiniciar;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getChamado() {
        return chamado;
    }

    public void setChamado(Integer chamado) {
        this.chamado = chamado;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Date getDtAgendamento() {
        return dtAgendamento;
    }

    public void setDtAgendamento(Date dtAgendamento) {
        this.dtAgendamento = dtAgendamento;
    }

    public String getDataAgendamento() {
        return DataHoje.converteData(dtAgendamento);
    }

    public void setDataAgendamento(String dataAgendamento) {
        dtAgendamento = DataHoje.converte(dataAgendamento);
    }

    public String getHoraAgendamento() {
        return DataHoje.converteHora(dtAgendamento);
    }

    public void setHoraAgendamento(String horaAgendamento) {
        this.dtAgendamento = DataHoje.converteDataHora(getDataAgendamento(), horaAgendamento);
    }

    public Date getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(Date dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getDataCadastro() {
        return DataHoje.converteData(dtCadastro);
    }

    public void setDataCadastro(String dataLancamento) {
        dtCadastro = DataHoje.converte(dataLancamento);
    }

    public String getHoraCadastro() {
        return DataHoje.converteHora(dtCadastro);
    }

    public void setHoraCadastro(String horaCadastro) {
        this.dtCadastro = DataHoje.converteDataHora(getDataCadastro(), horaCadastro);
    }

    public Boolean getReiniciar() {
        return reiniciar;
    }

    public void setReiniciar(Boolean reiniciar) {
        this.reiniciar = reiniciar;
    }

    public Date getDtProcessamento() {
        return dtProcessamento;
    }

    public void setDtProcessamento(Date dtProcessamento) {
        this.dtProcessamento = dtProcessamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
