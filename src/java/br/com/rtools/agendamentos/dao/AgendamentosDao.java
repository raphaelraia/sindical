package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AgendamentosDao extends DB {

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
        return findSchedules(date, filial_id, convenio_sub_grupo_id, convenio_id, is_socio, is_web, null, null, "");
    }

    public List findSchedules(String date, Integer filial_id, Integer convenio_sub_grupo_id, Integer convenio_id, Boolean is_socio, Boolean is_web, String start_time, String end_time, String hora) {
        try {
            String queryString = ""
                    + "     SELECT H.id,                                                                \n"
                    + "            H.ds_hora,                                                           \n"
                    + "            func_horarios_disponiveis_agendamento(h.id, date('" + date + "'::date)) disponivel, \n"
                    + "            H.is_encaixe                                                                        \n"
                    + "       FROM ag_horarios AS H                                                     \n"
                    + " INNER JOIN sis_semana AS S ON S.nr_postgres = extract(ISODOW FROM date('" + date + "'::date)) \n"
                    + "      WHERE H.id_semana = S.id                                                   \n"
                    + "        AND H.id_filial = " + filial_id + "                                      \n"
                    + "        AND H.id_convenio_sub_grupo = " + convenio_sub_grupo_id + "              \n"
                    + "        AND H.id_convenio = " + convenio_id + "                                  \n"
                    + "        AND H.ativo = true                                                       \n";
            if (is_web) {
                queryString += " AND H.is_web = true \n";
            }
            if (!is_socio) {
                queryString += " AND H.is_socio = false                                         \n";
            }
            if (is_web) {
                queryString += " AND func_horarios_disponiveis_agendamento(h.id, date('" + date + "'::date)) > 0 \n";
            }
            if (hora != null && !hora.isEmpty()) {
                queryString += " AND H.ds_hora = '" + hora + "'\n";
            }
            if (start_time != null && end_time != null) {
                queryString += " AND H.ds_hora::time BETWEEN '" + start_time + "'::time AND '" + end_time + "'::time \n";
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

    public List<Agendamentos> findBy(Integer filial_id, Integer convenio_sub_grupo_id, Integer convenio_id, Integer pessoa_id, String date, String hora) {
        try {
            String queryString = ""
                    + "   SELECT A.*                                                \n"
                    + "     FROM ag_agendamento A                                   \n"
                    + "    WHERE A.id IN (                                          \n"
                    + "         SELECT AH.id_agendamento                            \n"
                    + "           FROM ag_agendamento_horario AH                    \n"
                    + "     INNER JOIN ag_horarios AHOR ON AHOR.id = AH.id_horario AND AH.id_agendamento = A.id \n"
                    + "          WHERE AHOR.id_filial = " + filial_id + "           \n"
                    + "            AND AHOR.id_convenio_sub_grupo = " + convenio_sub_grupo_id + "   \n"
                    + "            AND AHOR.id_convenio = " + convenio_id + "       \n"
                    + "            AND AHOR.id_semana = " + DataHoje.diaDaSemana(DataHoje.converte(date)) + " \n"
                    + "            AND AHOR.ds_hora = '" + hora + "'            \n"
                    + "     )                                                   \n"
                    + "      AND A.dt_data = '" + date + "'\n"
                    + "      AND A.id_status IN (1,4)                           \n"
                    + " ORDER BY A.id_status, A.id                              \n";
            Query query = getEntityManager().createNativeQuery(queryString, Agendamentos.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    /**
     *
     * @param date
     * @param date_end
     * @param filial_id
     * @param convenio_grupo_id
     * @param convenio_sub_grupo_id
     * @param convenio_id
     * @param pessoa_id
     * @param status_id
     * @return
     */
    public List findBy(String date, String date_end, Integer filial_id, Integer convenio_grupo_id, Integer convenio_sub_grupo_id, Integer convenio_id, Integer pessoa_id, Integer status_id) {
        try {
            List listWhere = new ArrayList();
            String queryString = ""
                    + "      SELECT A.dt_data,                                  \n" // 0
                    + "             min(H.ds_hora) AS horario_inicial,          \n" // 1
                    + "             func_soma_minutos(min(H.ds_hora), (AGS.nr_minutos-1)) AS horario_final, \n" // 2
                    + "             AGS.nr_minutos AS tempo_servico,            \n" // 3
                    + "             STATUS.id AS id_status,                     \n" // 4
                    + "             STATUS.ds_descricao AS status,              \n" // 5
                    + "             ASE.id_agendamento AS id_agendamento,       \n" // 6
                    + "             S.id AS id_servico,                         \n" // 7
                    + "             S.ds_descricao AS servico,                  \n" // 8
                    + "             P.id AS codigo,                             \n" // 9
                    + "             P.ds_nome AS nome,                          \n" // 10
                    + "             P.ds_documento AS documento,                \n" // 11
                    + "             A.ds_telefone AS telefone,                  \n" // 12
                    + "             PU.ds_nome AS agendador,                    \n" // 13
                    + "             H.id_filial AS id_filial,                   \n" // 14
                    + "             FIL.ds_nome AS filial,                      \n" // 15
                    + "             FIL.ds_documento AS filial_documento,       \n" // 16
                    + "             CONV.id AS id_colaborador,                  \n" // 17
                    + "             CONV.ds_nome AS colaborador,                \n" // 18
                    + "             CONV.ds_documento AS colaborador_documento, \n" // 19
                    + "             CSG.id AS id_convenio_sub_grupo,            \n" // 20
                    + "             CSG.ds_descricao AS convenio_sub_grupo,     \n" // 21
                    + "             CG.ds_descricao AS convenio_grupo,          \n" // 22
                    + "             func_valor_servico(P.id, S.id, current_date, 0, 0) AS valor,\n" // 23
                    + "             CG.id AS id_grupo,                          \n" // 24
                    + "             ASE.id AS id_agendamento_servico,           \n" // 25
                    + "             ASE.id_movimento AS id_movimento,           \n" // 26
                    + "             R.id AS id_realizado,                       \n" // 27
                    + "             R.ds_nome AS realizado_nome,                \n" // 28
                    + "             R.ds_documento AS realizado_documento       \n" // 29                    
                    + "        FROM ag_agendamento_servico ASE                  \n"
                    + "  INNER JOIN ag_agendamento A ON A.id = ASE.id_agendamento\n"
                    + "  INNER JOIN ag_agendamento_horario AH ON AH.id_agendamento = ASE.id_agendamento     \n"
                    + "  INNER JOIN ag_horarios H ON H.id = AH.id_horario	\n"
                    + "  INNER JOIN fin_servicos S ON S.id = ASE.id_servico\n"
                    + "  INNER JOIN ag_servico AGS ON AGS.id_servico = S.id\n"
                    + "  INNER JOIN ag_status STATUS ON STATUS.id = A.id_status\n"
                    + "  INNER JOIN pes_pessoa P ON P.id = A.id_pessoa\n"
                    + "  INNER JOIN pes_filial F ON F.id = H.id_filial\n"
                    + "  INNER JOIN pes_juridica J ON J.id = F.id_filial\n"
                    + "  INNER JOIN pes_pessoa FIL ON FIL.id = J.id_pessoa\n"
                    + "  INNER JOIN pes_pessoa CONV ON CONV.id = H.id_convenio\n"
                    + "  INNER JOIN soc_convenio_sub_grupo CSG ON CSG.id = H.id_convenio_sub_grupo  \n"
                    + "  INNER JOIN soc_convenio_grupo CG ON CG.id = CSG.id_grupo_convenio          \n"
                    + "   LEFT JOIN pes_pessoa R ON R.id = A.id_convenio_realizado                  \n"
                    + "   LEFT JOIN seg_usuario U ON U.id = A.id_agendador                          \n"
                    + "   LEFT JOIN pes_pessoa PU ON PU.id = U.id_pessoa\n";
            queryString += "WHERE ASE.id_agendamento = A.id \n";
            if (!date.isEmpty() && date_end.isEmpty()) {
                listWhere.add("A.dt_data = '" + date + "'");
            } else if (!date.isEmpty() && !date_end.isEmpty()) {
                listWhere.add("A.dt_data BETWEEN '" + date + "' AND '" + date_end + "'");
            }
            if (status_id != null) {
                if (status_id == 1 || status_id == 4) {
                    listWhere.add("A.id_status IN (1,4) ");
                } else {
                    listWhere.add("A.id_status = " + status_id);
                }
            }
            if (filial_id != null) {
                listWhere.add("H.id_filial = " + filial_id);
            }
            if (pessoa_id != null && pessoa_id != -1) {
                listWhere.add("A.id_pessoa = " + pessoa_id);
            }
            if (convenio_id != null) {
                listWhere.add("CONV.id = " + convenio_id);
            }
            if (convenio_grupo_id != null) {
                listWhere.add("CG.id = " + convenio_grupo_id);
            }
            if (convenio_sub_grupo_id != null) {
                listWhere.add("CSG.id = " + convenio_sub_grupo_id);
            }

            for (int i = 0; i < listWhere.size(); i++) {
                queryString += "AND " + listWhere.get(i).toString() + "\n";
            }

            queryString += " GROUP BY A.dt_data,                \n"
                    + "            AGS.nr_minutos,              \n"
                    + "            STATUS.id,                   \n"
                    + "		   STATUS.ds_descricao,         \n"
                    + "		   ASE.id_agendamento,          \n"
                    + "		   S.id,                        \n"
                    + "		   S.ds_descricao,              \n"
                    + "		   P.id,                        \n"
                    + "		   P.ds_nome,                   \n"
                    + "		   P.ds_documento,              \n"
                    + "            A.ds_telefone,               \n"
                    + "		   PU.ds_nome,                  \n"
                    + "		   H.id_filial,                 \n"
                    + "		   FIL.ds_nome,                 \n"
                    + "		   FIL.ds_documento,            \n"
                    + "		   CONV.id,                     \n"
                    + "            CONV.ds_nome,                \n"
                    + "            CONV.ds_documento,           \n"
                    + "            CSG.id,                      \n"
                    + "            CSG.ds_descricao,            \n"
                    + "            CG.ds_descricao,             \n"
                    + "            func_valor_servico(P.id, S.id, current_date, 0, 0), \n"
                    + "            CG.id,                       \n"
                    + "            ASE.id,                      \n"
                    + "            ASE.id_movimento,            \n"
                    + "            R.id,                        \n"
                    + "            R.ds_nome,                   \n"
                    + "            R.ds_documento               \n";
            queryString += " ORDER BY A.dt_data, min(H.ds_hora)";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public boolean verificaNaoAtendidosSegRegistroAgendamento() {
        try {
            int result = -1;
            String queryString = "";
            queryString = " "
                    + " SELECT *                                            "
                    + "   FROM seg_registro                                 "
                    + "  WHERE (CURRENT_DATE - 1) = dt_atualiza_agendamentos ";
            Query query = getEntityManager().createNativeQuery(queryString);
            result = query.getResultList().size();
            if (result == 0) {
                getEntityManager().getTransaction().begin();
                queryString = "UPDATE ag_agendamento       \n"
                        + "   SET id_status = 5            \n"
                        + " WHERE dt_data < CURRENT_DATE   \n"
                        + "   AND id_status IN (1, 4)      \n";
                query = getEntityManager().createNativeQuery(queryString);
                result = query.executeUpdate();
                if (result < 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                queryString = " "
                        + " UPDATE seg_registro                              "
                        + "    SET dt_atualiza_agendamentos = CURRENT_DATE - 1";
                query = getEntityManager().createNativeQuery(queryString);
                result = query.executeUpdate();
                if (result < 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                getEntityManager().getTransaction().commit();
            }
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    /**
     * Retorna as filiais que tem horário...
     *
     * @param web
     * @param socios
     * @return
     */
    public List<Filial> findAllFilial(Boolean web, Boolean socios) {
        try {
            Query query;
            if (web) {
                if (socios) {
                    query = getEntityManager().createQuery("SELECT F FROM Filial F WHERE F.id IN ( SELECT AH.filial.id FROM AgendaHorarios AH WHERE AH.web = true GROUP BY AH.filial.id)");
                } else {
                    query = getEntityManager().createQuery("SELECT F FROM Filial F WHERE F.id IN ( SELECT AH.filial.id FROM AgendaHorarios AH WHERE AH.web = true AND AH.socio = false GROUP BY AH.filial.id)");
                }
            } else {
                if (socios) {
                    query = getEntityManager().createQuery("SELECT F FROM Filial F WHERE F.id IN ( SELECT AH.filial.id FROM AgendaHorarios AH GROUP BY AH.filial.id)");
                } else {
                    query = getEntityManager().createQuery("SELECT F FROM Filial F WHERE F.id IN ( SELECT AH.filial.id FROM AgendaHorarios AH WHERE AH.socio = false GROUP BY AH.filial.id) ");
                }
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public SubGrupoConvenio maxSubGrupoConvenio(Integer filial_id) {
        try {
            String queryString = ""
                    + "    SELECT SBC.* FROM soc_convenio_sub_grupo SBC WHERE SBC.id IN( \n"
                    + "    SELECT AH.id_convenio_sub_grupo                         \n"
                    + "      FROM ag_agendamento_horario AHOR                       \n"
                    + "INNER JOIN ag_agendamento AG ON AG.id = AHOR.id_agendamento  \n"
                    + "INNER JOIN ag_horarios AH ON AH.id = AHOR.id_horario         \n"
                    + "     WHERE AG.dt_emissao > (CURRENT_DATE - 30)               \n"
                    + "       AND AH.id_filial = " + filial_id + "                  \n"
                    + "  GROUP BY AH.id_convenio_sub_grupo                          \n"
                    + "  ORDER BY COUNT(*) DESC                                     \n"
                    + "     LIMIT 1                         "
                    + ") ";
            Query query = getEntityManager().createNativeQuery(queryString, SubGrupoConvenio.class);
            return (SubGrupoConvenio) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Servicos maxServico(Integer filial_id) {
        try {
            String queryString = ""
                    + "    SELECT S.* FROM fin_servicos S WHERE S.id IN( \n"
                    + "    SELECT ASE.id_servico                                    \n"
                    + "      FROM ag_agendamento_horario AHOR                       \n"
                    + "INNER JOIN ag_agendamento AG ON AG.id = AHOR.id_agendamento  \n"
                    + "INNER JOIN ag_horarios AH ON AH.id = AHOR.id_horario         \n"
                    + "INNER JOIN ag_agendamento_servico ASE ON ASE.id = AG.id      \n"
                    + "     WHERE AG.dt_emissao > (CURRENT_DATE - 30)               \n"
                    + "       AND AH.id_filial = " + filial_id + "                  \n"
                    + "  GROUP BY ASE.id_servico                                    \n"
                    + "  ORDER BY COUNT(*) DESC                                     \n"
                    + "     LIMIT 1                         "
                    + ") ";
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            return (Servicos) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Agendamentos findMaxIdByPessoa(Integer pessoa_id) {
        try {
            String queryString = ""
                    + "SELECT A.*                                               \n"
                    + "  FROM ag_agendamento A                                  \n"
                    + " WHERE A.id IN (                                         \n"
                    + "                 SELECT (max(id))                        \n"
                    + "                   FROM ag_agendamento                   \n"
                    + "                  WHERE id_pessoa = " + pessoa_id + "    \n"
                    + " )";
            Query query = getEntityManager().createNativeQuery(queryString, Agendamentos.class);
            return (Agendamentos) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Servicos> suggestions(Integer pessoa_id, Integer convenio_sub_grupo_id) {
        try {
            String queryString = ""
                    + "    SELECT                                               \n"
                    + "  DISTINCT S.*                                           \n"
                    + "      FROM fin_servicos S                                \n"
                    + "INNER JOIN soc_convenio_servico AS C ON C.id_servico = S.id      \n"
                    + "INNER JOIN ag_agendamento_servico ASE ON ASE.id_servico = S.id   \n"
                    + "INNER JOIN ag_agendamento A ON A.id = ASE.id_agendamento \n"
                    + "     WHERE A.id_pessoa = " + pessoa_id + "               \n"
                    + "       AND A.id_status IN (6)                            \n"
                    + "       AND S.ds_situacao = 'A'                           \n"
                    + "       AND C.is_agendamento = true                       \n"
                    + "       AND A.dt_data > (A.dt_data - 60)                  \n"
                    + "       AND C.id_convenio_sub_grupo = " + convenio_sub_grupo_id;
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Agendamentos> existsPessoaServicoPeriodoAtivo(Integer pessoa_id, Integer servico_id, Integer periodo_dias, Boolean socio) {
        // LISTA TODOS MOVIMENTOS ATIVOS EM QUE O BENEFICIÁRIO pessoa_id E A DATA ESTEJA ENTRE OS ULTIMOS periodo_dias
        String where;
        String queryString
                = "     SELECT A.*                                              \n"
                + "       FROM ag_agendamento A                                 \n"
                + " INNER JOIN ag_agendamento_servico ASE ON ASE.id_agendamento = A.id  \n"
                + "      WHERE A.id_pessoa = " + pessoa_id + "                  \n"
                + "        AND A.id_status IN (1,4,6)                           \n"
                + "        AND ASE.id_servico = " + servico_id + "              \n"
                + "        AND (A.dt_data >= current_date - " + periodo_dias + " AND A.dt_data <= current_date) "
                + "   ORDER BY A.dt_data ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Agendamentos.class);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Agendamentos> existsPessoaServicoMesVigente(Integer pessoa_id, Integer servico_id, Boolean socio) {
        return existsPessoaServicoMesVigente(pessoa_id, servico_id, socio, DataHoje.data());
    }

    public List<Agendamentos> existsPessoaServicoMesVigente(Integer pessoa_id, Integer servico_id, Boolean socio, String data) {
        // LISTA TODOS MOVIMENTOS ATIVOS EM QUE O BENEFICIÁRIO pessoa_id E A DATA ESTEJA ENTRE O MES ATUAL
        DataHoje dh = new DataHoje();
        String queryString
                = "     SELECT A.*                                              \n"
                + "       FROM ag_agendamento A                                 \n"
                + " INNER JOIN ag_agendamento_servico ASE ON ASE.id_agendamento = A.id  \n"
                + "      WHERE A.id_pessoa = " + pessoa_id + "                  \n"
                + "        AND A.id_status IN (1,4,6)                           \n"
                + "        AND ASE.id_servico = " + servico_id + "              \n"
                + "        AND (A.dt_data >= '" + dh.primeiroDiaDoMes(data) + "' AND A.dt_data <= '" + dh.ultimoDiaDoMes(data) + "') \n"
                + "   ORDER BY A.dt_data ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Agendamentos.class);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
