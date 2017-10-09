package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class GrupoConvenioDao extends DB {

    // PARA AGENDA DE HORÁRIOS
    public List<GrupoConvenio> findAllToAgendaHorarios() {
        String queryString = "   "
                + "  SELECT GC.* FROM soc_convenio_grupo AS GC                    \n"
                + "   WHERE id IN (                                               \n"
                + "	SELECT id_grupo_convenio FROM soc_convenio_sub_grupo    \n"
                + "      WHERE id IN (                                          \n"
                + "        SELECT id_convenio_sub_grupo            \n"
                + "          FROM soc_convenio_servico             \n"
                + "         WHERE is_agendamento = true                         \n"
                + "	 )                                                      \n"
                + "	)                                                       \n"
                + "ORDER BY ds_descricao ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, GrupoConvenio.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }
}
