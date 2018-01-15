package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendamentoServico;
import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AgendamentoServicoDao extends DB {

    /**
     * 0 id_horario, 1 hora, 2 quantidade
     *
     * @param date
     * @param filial_id
     * @param convenio_sub_grupo_id
     * @param convenio_id
     * @param is_socio
     * @return
     */
    public List findSchedules(String date, Integer filial_id, Integer convenio_sub_grupo_id, Integer convenio_id, Boolean is_socio) {
        return findSchedules(date, filial_id, convenio_sub_grupo_id, convenio_id, is_socio, false);
    }

    /**
     *
     * @param date
     * @param filial_id
     * @param convenio_sub_grupo_id
     * @param convenio_id
     * @param is_socio
     * @param is_web
     * @return
     */
    public List findSchedules(String date, Integer filial_id, Integer convenio_sub_grupo_id, Integer convenio_id, Boolean is_socio, Boolean is_web) {
        try {
            String queryString = ""
                    + "     SELECT H.id,                                                                \n"
                    + "            H.ds_hora,                                                           \n"
                    + "            func_horarios_disponiveis_agendamento(h.id, date('" + date + "'::date)) disponivel  \n"
                    + "       FROM ag_horarios AS H                                                     \n"
                    + " INNER JOIN sis_semana AS S ON S.nr_postgres = extract(ISODOW FROM date('" + date + "'::date)) \n"
                    + "      WHERE H.id_semana = S.id                                                   \n"
                    + "        AND H.id_filial = " + filial_id + "                                      \n"
                    + "        AND H.id_convenio_sub_grupo = " + convenio_sub_grupo_id + "              \n"
                    + "        AND H.id_convenio = " + convenio_id + "                                  \n";
            if (is_web) {
                queryString += " AND H.is_web = true \n";
            }
            if (!is_socio) {
                queryString += " AND H.is_socio = false                                         \n";
            }
            queryString += " ORDER BY H.ds_hora";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Agendamentos findBy(String date, Integer filial_id, Integer pessoa_id) {
        try {
            String queryString = ""
                    + "   SELECT A.*                                            \n"
                    + "     FROM ag_agendamento A                               \n"
                    + "    WHERE A.id IN (                                      \n"
                    + "         SELECT AH.id_agendamento                        \n"
                    + "           FROM ag_agendamento_horario AH                \n"
                    + "     INNER JOIN ag_horarios AHOR ON AHOR.id = AH.id_horario	\n"
                    + "          WHERE AH.id_agendamento = A.id                 \n"
                    + "            AND AHOR.id_filial = " + filial_id + "       \n"
                    + "     )                                                   \n"
                    + "      AND A.dt_data = '" + date + "'\n"
                    + "      AND A.id_pessoa = " + pessoa_id + "\n";
            Query query = getEntityManager().createNativeQuery(queryString, Agendamentos.class);
            return (Agendamentos) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public AgendamentoServico findBy(Integer agendamento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AgendamentoServico AS ASE WHERE ASE.agendamento.id = :agendamento_id");
            query.setParameter("agendamento_id", agendamento_id);
            return (AgendamentoServico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public AgendamentoServico findBy(Integer agendamento_id, Integer servico_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AgendamentoServico AS ASE WHERE ASE.agendamento.id = :agendamento_id AND ASE.servico.id = :servico_id");
            query.setParameter("agendamento_id", agendamento_id);
            query.setParameter("servico_id", servico_id);
            return (AgendamentoServico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
