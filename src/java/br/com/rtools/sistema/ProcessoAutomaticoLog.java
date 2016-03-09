/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "sis_processo_automatico_log")
public class ProcessoAutomaticoLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_processo_automatico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ProcessoAutomatico processoAutomatico;
    @Column(name = "ds_log", length = 1000)
    private String log;

    public ProcessoAutomaticoLog() {
        this.id = -1;
        this.processoAutomatico = new ProcessoAutomatico();
        this.log = "";
    }
    
    public ProcessoAutomaticoLog(Integer id, ProcessoAutomatico processoAutomatico, String log) {
        this.id = id;
        this.processoAutomatico = processoAutomatico;
        this.log = log;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProcessoAutomatico getProcessoAutomatico() {
        return processoAutomatico;
    }

    public void setProcessoAutomatico(ProcessoAutomatico processoAutomatico) {
        this.processoAutomatico = processoAutomatico;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
    
}
