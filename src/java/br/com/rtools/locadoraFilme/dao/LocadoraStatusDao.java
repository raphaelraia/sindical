package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.locadoraFilme.LocadoraStatus;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LocadoraStatusDao extends DB {

    public List findAllByFilial(Integer filial_id) {
        try {
            String queryString = ""
                    + "     SELECT LS.*                                         \n"
                    + "       FROM loc_status AS LS                             \n"
                    + " INNER JOIN fin_servicos AS S ON S.id = LS.id_taxa       \n"
                    + "      WHERE LS.id_filial = " + filial_id + "             \n"
                    + "        AND LS.dt_data >= CURRENT_DATE                   \n"
                    + "   ORDER BY S.ds_descricao ASC   ";
            Query query = getEntityManager().createNativeQuery(queryString, LocadoraStatus.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
