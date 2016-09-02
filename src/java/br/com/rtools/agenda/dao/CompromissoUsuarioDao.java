package br.com.rtools.agenda.dao;

import br.com.rtools.agenda.Compromisso;
import br.com.rtools.agenda.CompromissoUsuario;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CompromissoUsuarioDao extends DB {

    public List<CompromissoUsuario> findByUsuario(Integer usuario_id) {
        List list = new ArrayList();
        try {
            String queryString = "      SELECT CU.*                                                     \n"
                    + "                   FROM age_compromisso_usuario  AS CU                           \n"
                    + "             INNER JOIN age_compromisso  AS C ON C.id = CU.id_compromisso        \n"
                    + "              LEFT JOIN sis_periodo      AS P ON P.id = C.id_periodo_repeticao   \n"
                    + "              LEFT JOIN sis_semana       AS S ON S.id = C.id_semana              \n"
                    + "                  WHERE (                                                        \n"
                    + "                             (                                                   \n"
                    + "                                 C.id_periodo_repeticao IS NOT NULL              \n"
                    + "                             ) OR (                                              \n"
                    + "                                 C.dt_data IS NOT NULL                           \n"
                    + "                                 AND                                             \n"
                    + "                                 C.dt_data >= CURRENT_DATE                       \n"
                    + "                             )                                                   \n"
                    + "                        )                                                        \n"
                    + "                    AND CU.id_usuario = " + usuario_id + "                       \n"
                    + "                    AND C.dt_cancelamento IS NULL                                \n"
                    + "               ORDER BY (S.id IS NOT NULL),                                      \n"
                    + "                        (C.dt_data IS NOT NULL) DESC,                            \n"
                    + "                         C.ds_hora_inicial ASC                                   \n";
            Query query = getEntityManager().createNativeQuery(queryString, CompromissoUsuario.class);
            list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public List<CompromissoUsuario> findByCompromisso(Integer compromisso_id) {
        List list = new ArrayList();
        try {
            Query query = getEntityManager().createQuery("SELECT CU FROM CompromissoUsuario AS CU WHERE CU.compromisso.id = :compromisso_id ORDER BY CU.usuario.pessoa.nome ASC");
            query.setParameter("compromisso_id", compromisso_id);
            list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public List<CompromissoUsuario> findCompromissos(Integer usuario_id) {
        return findCompromissos(null, "hoje_amanha", "", "", "", "ativos", usuario_id);
    }

    public List<CompromissoUsuario> findCompromissos(Integer secretaria_id, String tipoHistorico, String tipoData, String dataInicial, String dataFinal, String cancelados, Integer usuario_id) {
        List list = new ArrayList();
        List listWhere = new ArrayList();
        try {
            String queryString = "      SELECT CU.*                                                          \n"
                    + "                   FROM age_compromisso_usuario AS CU                                \n"
                    + "             INNER JOIN age_compromisso  AS C ON C.id = CU.id_compromisso            \n"
                    + "              LEFT JOIN sis_periodo      AS P ON P.id = C.id_periodo_repeticao       \n"
                    + "              LEFT JOIN sis_semana       AS S ON S.id = C.id_semana                  \n";
            if (tipoHistorico.equals("hoje")) {
                listWhere.add("((C.id_periodo_repeticao IS NOT NULL AND C.id_semana IS NOT NULL AND S.nr_postgres = EXTRACT(ISODOW FROM CURRENT_DATE)) OR (C.dt_data IS NOT NULL AND C.dt_data = CURRENT_DATE))");
            }
            if (tipoHistorico.equals("hoje_amanha")) {
                listWhere.add("(((C.id_periodo_repeticao IS NOT NULL AND C.id_semana IS NOT NULL AND (S.nr_postgres = EXTRACT(ISODOW FROM CURRENT_DATE) OR S.nr_postgres = EXTRACT(ISODOW FROM (CURRENT_DATE + 1))))) OR (C.dt_data IS NOT NULL AND C.dt_data BETWEEN CURRENT_DATE AND CURRENT_DATE + 1))");
            }
            if (tipoHistorico.equals("agendados")) {
                listWhere.add("((C.id_periodo_repeticao IS NOT NULL) OR (C.dt_data IS NOT NULL AND C.dt_data >= CURRENT_DATE))");
            }
            if (tipoHistorico.equals("permanentes")) {
                listWhere.add("C.id_periodo_repeticao IS NOT NULL");
            }
            if (tipoHistorico.equals("essa_semana")) {
                listWhere.add("C.dt_data IS NOT NULL AND CURRENT_DATE BETWEEN date_trunc('week', C.dt_data::timestamp)::date AND (date_trunc('week', C.dt_data::timestamp)+ '6 days'::interval)::date");
            }
            if (tipoHistorico.equals("semana_que_vem")) {
                listWhere.add("C.dt_data IS NOT NULL AND (CURRENT_DATE + INTERVAL '7 DAY') BETWEEN date_trunc('week', C.dt_data::timestamp)::date AND (date_trunc('week', C.dt_data::timestamp)+ '6 days'::interval)::date");
            }
            if (tipoHistorico.equals("ontem")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data = (CURRENT_DATE - INTERVAL  '1 DAY')");
            }
            if (tipoHistorico.equals("mes")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '1 MONTH')");
            }
            if (tipoHistorico.equals("semestre")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '180 DAYS')");
            }
            if (tipoHistorico.equals("ano")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '1 YEAR')");
            }
            if (cancelados.equals("ativos")) {
                listWhere.add("C.dt_cancelamento IS NULL");
            } else if (cancelados.equals("cancelados")) {
                listWhere.add("C.dt_cancelamento IS NOT NULL");
            }
            if (tipoHistorico.equals("especifico")) {
                switch (tipoData) {
                    case "igual":
                        if (!dataInicial.isEmpty()) {
                            listWhere.add("C.dt_data = '" + dataInicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!dataInicial.isEmpty()) {
                            listWhere.add("C.dt_data >= '" + dataInicial + "'");
                        }
                        break;
                    case "ate":
                        if (!dataInicial.isEmpty()) {
                            listWhere.add("C.dt_data <= '" + dataInicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
                            listWhere.add("C.dt_data IS NOT NULL AND C.dt_data BETWEEN '" + dataInicial + "' AND '" + dataFinal + "'");
                        }
                        break;
                    default:
                        break;
                }
            }
            if (secretaria_id != null && usuario_id != null) {
                listWhere.add("( C.id_secretaria = " + secretaria_id + " OR CU.id_usuario = " + usuario_id + " )");
            } else {
                if (secretaria_id != null) {
                    listWhere.add("CU.id_secretaria = " + secretaria_id + "");
                }
                if (usuario_id != null) {
                    listWhere.add("CU.id_usuario = " + usuario_id + "");
                }
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE ";
                } else {
                    queryString += " AND ";
                }
                queryString += " " + listWhere.get(i).toString() + " \n";
            }
            queryString += "  ORDER BY C.id_semana,      \n"
                    + "                C.dt_data,        \n"
                    + "                C.ds_hora_inicial \n";
            Query query = getEntityManager().createNativeQuery(queryString, CompromissoUsuario.class);
            list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

}
