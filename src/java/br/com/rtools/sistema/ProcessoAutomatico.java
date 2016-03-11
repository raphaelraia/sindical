/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "sis_processo_automatico")
public class ProcessoAutomatico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @Column(name = "ds_processo", length = 255)
    private String processo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inicio")
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_final")
    private Date dataFinal;
    @Column(name = "ds_hora_inicio", length = 10)
    private String horaInicio;
    @Column(name = "ds_hora_final", length = 10)
    private String horaFinal;
    @Column(name = "nr_progresso")
    private Integer nrProgresso;
    @Column(name = "nr_progresso_final")
    private Integer nrProgressoFinal;
    @Column(name = "is_todos_usuarios")
    private Boolean todosUsuarios;
    @Column(name = "is_visualizado_fim_processo")
    private Boolean visualizadoFimProcesso;
    @Column(name = "is_cancelar_processo")
    private Boolean cancelarProcesso;

    public ProcessoAutomatico() {
        this.id = -1;
        this.usuario = new Usuario();
        this.processo = "";
        this.dataInicio = null;
        this.dataFinal = null;
        this.horaInicio = "";
        this.horaFinal = "";
        this.nrProgresso = 0;
        this.nrProgressoFinal = 0;
        this.todosUsuarios = false;
        this.visualizadoFimProcesso = false;
        this.cancelarProcesso = false;
    }

    public ProcessoAutomatico(Integer id, Usuario usuario, String processo, Date dataInicio, Date dataFinal, String horaInicio, String horaFinal, Integer nrProgresso, Integer nrProgressoFinal, Boolean todosUsuarios, Boolean visualizadoFimProcesso, Boolean cancelarProcesso) {
        this.id = id;
        this.usuario = usuario;
        this.processo = processo;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.nrProgresso = nrProgresso;
        this.nrProgressoFinal = nrProgressoFinal;
        this.todosUsuarios = todosUsuarios;
        this.visualizadoFimProcesso = visualizadoFimProcesso;
        this.cancelarProcesso = cancelarProcesso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public String getDataInicioString() {
        return DataHoje.converteData(dataInicio);
    }

    public void setDataInicioString(String dataInicioString) {
        this.dataInicio = DataHoje.converte(dataInicioString);
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataFinalString() {
        return DataHoje.converteData(dataFinal);
    }

    public void setDataFinalString(String dataFinalString) {
        this.dataFinal = DataHoje.converte(dataFinalString);
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Integer getNrProgresso() {
        return nrProgresso;
    }

    public void setNrProgresso(Integer nrProgresso) {
        this.nrProgresso = nrProgresso;
    }

    public Boolean getTodosUsuarios() {
        return todosUsuarios;
    }

    public void setTodosUsuarios(Boolean todosUsuarios) {
        this.todosUsuarios = todosUsuarios;
    }

    public Integer getNrProgressoFinal() {
        return nrProgressoFinal;
    }

    public void setNrProgressoFinal(Integer nrProgressoFinal) {
        this.nrProgressoFinal = nrProgressoFinal;
    }

    public Boolean getVisualizadoFimProcesso() {
        return visualizadoFimProcesso;
    }

    public void setVisualizadoFimProcesso(Boolean visualizadoFimProcesso) {
        this.visualizadoFimProcesso = visualizadoFimProcesso;
    }

    public Boolean getCancelarProcesso() {
        return cancelarProcesso;
    }

    public void setCancelarProcesso(Boolean cancelarProcesso) {
        this.cancelarProcesso = cancelarProcesso;
    }

}
