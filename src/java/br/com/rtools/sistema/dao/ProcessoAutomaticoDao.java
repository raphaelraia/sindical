/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoLog;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class ProcessoAutomaticoDao extends DB {

    public ProcessoAutomatico pesquisarProcesso(String nome_processo, Integer id_usuario) {
        try {
            String text
                    = " SELECT pa.* \n "
                    + "   FROM sis_processo_automatico pa \n "
                    + "  WHERE pa.dt_final IS NULL \n "
                    + "    AND pa.ds_processo = '"+nome_processo+"' \n";
            
            if (id_usuario != null){
                text += " AND pa.id_usuario = " +id_usuario;
            }
                
            Query qry = getEntityManager().createNativeQuery(text, ProcessoAutomatico.class);
            return (ProcessoAutomatico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ProcessoAutomatico();
    }
    
    public ProcessoAutomatico pesquisarProcessoConcluidoNaoVisto(String nome_processo, Integer id_usuario) {
        try {
            String text
                    = " SELECT pa.* \n "
                    + "   FROM sis_processo_automatico pa \n "
                    + "  WHERE pa.dt_final IS NOT NULL \n "
                    + "    AND pa.ds_processo = '"+nome_processo+"' \n"
                    + "    AND pa.is_visualizado_fim_processo = false";
            
            if (id_usuario != null){
                text += " AND pa.id_usuario = " +id_usuario;
            }
                
            Query qry = getEntityManager().createNativeQuery(text + " LIMIT 1", ProcessoAutomatico.class);
            return (ProcessoAutomatico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ProcessoAutomatico();
    }
    
    public List<ProcessoAutomaticoLog> listaProcessoAutomaticoLog(Integer id_processo_automatico) {
        try {
            String text
                    = " SELECT pal.* \n "
                    + "   FROM sis_processo_automatico_log pal \n "
                    + "  WHERE pal.id_processo_automatico = " + id_processo_automatico
                    + "  ORDER BY pal.id DESC";
            
            Query qry = getEntityManager().createNativeQuery(text, ProcessoAutomaticoLog.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
