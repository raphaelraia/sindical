package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.locadoraFilme.LocadoraStatus;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class LocadoraStatusDao extends DB {

    public List findAllByFilialData(Integer filial_id) {
        try {
            String queryString = ""
                    + "     SELECT LS.*                                         \n"
                    + "       FROM loc_status AS LS                             \n"
                    + " INNER JOIN loc_taxa AS T ON T.id = LS.id_taxa           \n"
                    + " INNER JOIN fin_servicos AS S ON S.id = T.id_servico_diaria  \n"
                    + "      WHERE LS.id_filial = " + filial_id + "             \n"
                    + "        AND LS.dt_data >= CURRENT_DATE                   \n"
                    + "        AND LS.id_semana IS NULL                         \n"
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

    public List findAllByFilialSemana(Integer filial_id) {
        try {
            String queryString = ""
                    + "     SELECT LS.*                                         \n"
                    + "       FROM loc_status AS LS                             \n"
                    + " INNER JOIN loc_taxa AS T ON T.id = LS.id_taxa           \n"
                    + " INNER JOIN fin_servicos AS S ON S.id = T.id_servico_diaria  \n"
                    + "      WHERE LS.id_filial = " + filial_id + "             \n"
                    + "        AND LS.dt_data IS NULL                           \n"
                    + "        AND LS.id_semana IS NOT NULL                     \n"
                    + "   ORDER BY LS.id_semana ASC, S.ds_descricao ASC   ";
            Query query = getEntityManager().createNativeQuery(queryString, LocadoraStatus.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public LocadoraStatus findByFilialData(Integer filial_id) {
        try {
            String queryString = ""
                    + "     SELECT LS.*                                                                 \n"
                    + "       FROM loc_status AS LS                                                     \n"
                    + " INNER JOIN loc_taxa AS T ON T.id = LS.id_taxa           \n"
                    + " INNER JOIN fin_servicos AS S ON S.id = T.id_servico_diaria  \n"
                    + "      WHERE LS.id_filial = " + filial_id + "                                     \n"
                    + "        AND LS.dt_data >= CURRENT_DATE                                           \n"
                    + "   ORDER BY LS.id_semana ASC, S.ds_descricao ASC   ";
            Query query = getEntityManager().createNativeQuery(queryString, LocadoraStatus.class);
            return (LocadoraStatus) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public LocadoraStatus findByFilialSemana(Integer filial_id) {
        Integer dia_semana = new Date().getDay() + 1;
        try {
            String queryString = ""
                    + "     SELECT LS.*                                                                 \n"
                    + "       FROM loc_status AS LS                                                     \n"
                    + " INNER JOIN loc_taxa AS T ON T.id = LS.id_taxa                                   \n"
                    + " INNER JOIN fin_servicos AS S ON S.id = T.id_servico_diaria                      \n"
                    + "      WHERE LS.id_filial = " + filial_id + "                                     \n"
                    + "        AND LS.dt_data IS NULL                                                   \n"
                    + "        AND LS.id_semana = " + dia_semana + "                                    \n"
                    + "   ORDER BY S.ds_descricao ASC   ";
            Query query = getEntityManager().createNativeQuery(queryString, LocadoraStatus.class);
            return (LocadoraStatus) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
