package br.com.rtools.agenda.dao;

import br.com.rtools.agenda.Compromisso;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CompromissoDao extends DB {

    public List<Compromisso> find(Integer secretaria_id, String descricao) {
        List list = new ArrayList();
        try {
            String queryString = " SELECT C.* FROM age_compromisso AS C WHERE UPPER(func_translate(C.ds_descricao)) LIKE UPPER(func_translate('%" + descricao + "%')) AND S.id_secretaria = " + secretaria_id + " ORDER BY C.dt_compromisso ASC, C._ds_hora_inicial ";
            Query query = getEntityManager().createNativeQuery(queryString, Compromisso.class);
            list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public List<Compromisso> findCompromissos(Integer secretaria_id) {
        return findCompromissos(secretaria_id, "hoje_amanha", "", "", "", "ativos", null);
    }

    public List<Compromisso> findCompromissos(Integer secretaria_id, String tipoHistorico, String tipoData, String dataInicial, String dataFinal, String cancelados, Integer usuario_id) {
        List list = new ArrayList();
        List listWhere = new ArrayList();
        try {
            String queryString = "      SELECT C.*                                                          \n"
                    + "                   FROM age_compromisso  AS C                                        \n"
                    + "              LEFT JOIN age_compromisso_usuario AS CU ON CU.id_compromisso = C.id    \n"
                    + "              LEFT JOIN sis_periodo      AS P ON P.id = C.id_periodo_repeticao       \n"
                    + "              LEFT JOIN sis_semana       AS S ON S.id = C.id_semana                  \n";
            if (tipoHistorico.equals("hoje")) {
                listWhere.add("((C.id_periodo_repeticao IS NOT NULL AND C.id_semana IS NOT NULL AND S.nr_postgres = EXTRACT(ISODOW FROM CURRENT_DATE)) OR (C.dt_data IS NOT NULL AND C.dt_data = CURRENT_DATE))");
            }
            if (tipoHistorico.equals("hoje_amanha")) {
                listWhere.add("((C.id_periodo_repeticao IS NOT NULL AND C.id_semana IS NOT NULL AND (S.nr_postgres = EXTRACT(ISODOW FROM CURRENT_DATE) OR S.nr_postgres = EXTRACT(ISODOW FROM (CURRENT_DATE + 1))))) OR (C.dt_data IS NOT NULL AND C.dt_data BETWEEN CURRENT_DATE AND CURRENT_DATE + 1)");
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
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '1 MONTH') AND C.dt_data < CURRENT_DATE");
            }
            if (tipoHistorico.equals("semestre")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '180 DAYS') AND C.dt_data < CURRENT_DATE");
            }
            if (tipoHistorico.equals("ano")) {
                listWhere.add("C.dt_data IS NOT NULL AND C.dt_data > (CURRENT_DATE - INTERVAL  '1 YEAR') AND C.dt_data < CURRENT_DATE");
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
            if (secretaria_id != null) {
                listWhere.add("C.id_secretaria = " + secretaria_id + "");
            }
            if (usuario_id != null) {
                listWhere.add("CU.id_usuario = " + usuario_id + "");
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
            Query query = getEntityManager().createNativeQuery(queryString, Compromisso.class);
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
