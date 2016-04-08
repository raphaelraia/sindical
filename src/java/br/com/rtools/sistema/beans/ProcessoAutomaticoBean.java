/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoLog;
import br.com.rtools.sistema.dao.ProcessoAutomaticoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class ProcessoAutomaticoBean implements Serializable {

    private ProcessoAutomatico processoAutomatico = new ProcessoAutomatico();
    private ProcessoAutomatico processoAutomaticoConcluido = new ProcessoAutomatico();
    private ProcessoAutomatico paDetalhe = new ProcessoAutomatico();
    private List<ProcessoAutomaticoLog> listaProcessoAutomaticoLog = new ArrayList();
    private Integer progressValue = 0;
    private Integer progressLabel = 0;
    private Boolean dialogLogOpened = false;
    private Integer tentativas = 0;
    private String thread_name = "";

    // SEMPRE MANTER NO preRenderView DA TELA QUE TEM O PROCESSO
    public void find_progress(String nome_do_processo) {
        try {
            // METODO EXECUTADO TODA VEZ QUE INICIA A TELA
            // VERIFICA SE A REQUISIÇÃO É AJAX OU POST, PARA O CASO DE RECARREGAR A PÁGINA
            // ex. fonte http://stackoverflow.com/questions/14153895/view-scoped-bean-prerenderview-method-being-called-multiple-times
            if (!FacesContext.getCurrentInstance().isPostback()) {
                // LIMPA TODO O bean PARA PESQUISAR UM NOVO PROCESSO
                clearBean();

                thread_name = nome_do_processo;
                ProcessoAutomaticoDao dao = new ProcessoAutomaticoDao();
                processoAutomatico = dao.pesquisarProcesso(thread_name, Usuario.getUsuario().getId());
                if (processoAutomatico.getId() != -1) {
                    // CONCLUIU O PROCESSAMENTO
                    //concluiuProcessamento();

                    // INCREMENTA A PROGRESSÃO DA BARRA DE STATUS
                    Integer progress = Math.round((processoAutomatico.getNrProgresso().floatValue() / processoAutomatico.getNrProgressoFinal().floatValue()) * 100);
                    progressValue = progress;
                    progressLabel = progressValue;
                } else {
                    processoAutomaticoConcluido = dao.pesquisarProcessoConcluidoNaoVisto(thread_name, Usuario.getUsuario().getId());
                }
            }            
        } catch (Exception e) {
            
        }
    }

    public void cancelarProcesso(){
        processoAutomatico = new ProcessoAutomaticoDao().pesquisarProcesso(thread_name, Usuario.getUsuario().getId());
        
        if (processoAutomatico.getId() != -1){
            processoAutomatico.setCancelarProcesso(true);
            
            new Dao().update(processoAutomatico, true);
        }
    }
    
    public void clearBean() {
        processoAutomatico = new ProcessoAutomatico();
        processoAutomaticoConcluido = new ProcessoAutomatico();
        paDetalhe = new ProcessoAutomatico();
        listaProcessoAutomaticoLog = new ArrayList();
        progressValue = 0;
        progressLabel = 0;
        dialogLogOpened = false;
        tentativas = 0;
        thread_name = "";
    }
//
//    public void concluiuProcessamento() {
//        if (processoAutomatico.getDataFinal() != null) {
//            processoAutomaticoConcluido = processoAutomatico;
//            progressValue = 100;
//            progressLabel = 100;
//            processoAutomatico = new ProcessoAutomatico();
//            //try{Thread.sleep(2000);}catch(Exception e){}
//        }
//    }

    public void verLog() {
        loadListaProcessoAutomaticoLog();
        dialogLogOpened = true;
//        
//        PF.update("formMenuPrincipal:panel_processo_automatico_log");
//        PF.openDialog("dlg_processo_automatico_log");
    }

    public void ok() {
        processoAutomaticoConcluido.setVisualizadoFimProcesso(true);
        new Dao().update(processoAutomaticoConcluido, true);
        processoAutomaticoConcluido = new ProcessoAutomatico();
    }

    public void progress() {
        if (!thread_name.isEmpty()) {
            ProcessoAutomaticoDao dao = new ProcessoAutomaticoDao();
            processoAutomatico = dao.pesquisarProcesso(thread_name, Usuario.getUsuario().getId());

        // CONCLUIU O PROCESSAMENTO
            //concluiuProcessamento();
            if (processoAutomatico.getId() != -1) {
//            //processoAutomaticoConcluido = processoAutomatico;

                Integer progress = Math.round((processoAutomatico.getNrProgresso().floatValue() / processoAutomatico.getNrProgressoFinal().floatValue()) * 100);
                if (progressValue.equals(progress)) {
                    tentativas++;
                } else {
                    tentativas = 0;
                }

                progressValue = progress;
                progressLabel = progressValue;

            // SE VERIFICAR A PROCESSO E ESTE ESTIVER PARADO, FINALIZAR
                // APENAS NO BANCO DE DADOS
                // NÃO FUNCIONA PARA STOP A THREAD
                if (tentativas == 80) {
//                    Dao daox = new Dao();
//
//                    processoAutomatico.setDataFinal(DataHoje.dataHoje());
//                    processoAutomatico.setHoraFinal(DataHoje.hora());
//
//                    daox.update(processoAutomatico, true);
//                    processoAutomatico = new ProcessoAutomatico();
                }
            } else {
                processoAutomaticoConcluido = dao.pesquisarProcessoConcluidoNaoVisto(thread_name, Usuario.getUsuario().getId());
            }

            if (dialogLogOpened) {
                loadListaProcessoAutomaticoLog();
            }
        }
    }

    public void closeDialogLog() {
        dialogLogOpened = false;
    }

    public void loadListaProcessoAutomaticoLog() {
        listaProcessoAutomaticoLog.clear();

        if (processoAutomatico.getId() != -1) {
            ProcessoAutomaticoDao dao = new ProcessoAutomaticoDao();
            listaProcessoAutomaticoLog = dao.listaProcessoAutomaticoLog(processoAutomatico.getId());
            paDetalhe = processoAutomatico;
        } else if (processoAutomaticoConcluido.getId() != -1) {
            ProcessoAutomaticoDao dao = new ProcessoAutomaticoDao();
            listaProcessoAutomaticoLog = dao.listaProcessoAutomaticoLog(processoAutomaticoConcluido.getId());
            paDetalhe = processoAutomaticoConcluido;
        }
    }

    public ProcessoAutomatico getProcessoAutomatico() {
        return processoAutomatico;
    }

    public void setProcessoAutomatico(ProcessoAutomatico processoAutomatico) {
        this.processoAutomatico = processoAutomatico;
    }

    public List<ProcessoAutomaticoLog> getListaProcessoAutomaticoLog() {
        return listaProcessoAutomaticoLog;
    }

    public void setListaProcessoAutomaticoLog(List<ProcessoAutomaticoLog> listaProcessoAutomaticoLog) {
        this.listaProcessoAutomaticoLog = listaProcessoAutomaticoLog;
    }

    public Integer getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(Integer progressValue) {
        this.progressValue = progressValue;
    }

    public Integer getProgressLabel() {
        return progressLabel;
    }

    public void setProgressLabel(Integer progressLabel) {
        this.progressLabel = progressLabel;
    }

    public Boolean getDialogLogOpened() {
        return dialogLogOpened;
    }

    public void setDialogLogOpened(Boolean dialogLogOpened) {
        this.dialogLogOpened = dialogLogOpened;
    }

    public ProcessoAutomatico getProcessoAutomaticoConcluido() {
        return processoAutomaticoConcluido;
    }

    public void setProcessoAutomaticoConcluido(ProcessoAutomatico processoAutomaticoConcluido) {
        this.processoAutomaticoConcluido = processoAutomaticoConcluido;
    }

    public ProcessoAutomatico getPaDetalhe() {
        return paDetalhe;
    }

    public void setPaDetalhe(ProcessoAutomatico paDetalhe) {
        this.paDetalhe = paDetalhe;
    }
}
