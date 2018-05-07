package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoRegistros;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ProcessoAutomaticoRegistrosDao extends DB {

    public ProcessoAutomaticoRegistros find(Integer processo_automatico_id, String tabela, String coluna, Integer codigo) {
        try {
            String text
                    = " SELECT PAR.* \n "
                    + "   FROM sis_processo_automatico_registros PAR \n "
                    + "  WHERE PAR.id_processo_automatico = " + processo_automatico_id + " \n"
                    + "    AND PAR.ds_tabela = '" + tabela + "' \n"
                    + "    AND PAR.ds_coluna = '" + coluna + "' \n"
                    + "    AND PAR.nr_codigo = " + codigo + " \n";
            Query qry = getEntityManager().createNativeQuery(text, ProcessoAutomaticoRegistros.class);
            return (ProcessoAutomaticoRegistros) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProcessoAutomaticoRegistros> find(Integer processo_automatico_id) {
        try {
            String text
                    = "  SELECT PAR.* \n "
                    + "    FROM sis_processo_automatico_registros PAR                       \n "
                    + "   WHERE PAR.id_processo_automatico = " + processo_automatico_id + "   \n"
                    + " ORDER BY PAR.id                                                     \n";
            Query qry = getEntityManager().createNativeQuery(text, ProcessoAutomaticoRegistros.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
