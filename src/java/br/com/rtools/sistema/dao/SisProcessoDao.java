package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisProcessoDao extends DB {

    public List findRotinasGroup() {
        try {
            Query query = getEntityManager().createQuery("SELECT SP.rotina FROM SisProcesso AS SP GROUP BY SP.rotina ORDER BY SP.rotina.rotina ASC");
            List list = query.getResultList();
            if (list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List find(String dataInicial, String dataFinal, String horaInicial, String horaFinal, Integer rotina_id, Integer relatorio_id, Integer usuario_id, String descricao, Boolean agrupado) {
        List listWhere = new ArrayList();
        String queryString = "";
        if (agrupado) {
            queryString = ""
                    + "     SELECT P.id_rotina,                                     \n" // 0
                    + "            ds_rotina,                                       \n" // 1
                    + "            P.ds_processo,                                   \n" // 2
                    + "            min(nr_tempo) AS menor,                          \n" // 3
                    + "            round(avg(nr_tempo), 0) AS media,                \n" // 4
                    + "            max(nr_tempo) AS maior,                          \n" // 5
                    + "            min(nr_tempo_query) AS menor_query,              \n" // 6
                    + "            round(avg(nr_tempo_query), 0) AS media_query,    \n" // 7
                    + "            max(nr_tempo_query) AS maior_query               \n" // 8
                    + "   ";
        } else {
            queryString = ""
                    + "     SELECT ds_rotina,                                       \n" // 0
                    + "            P.ds_processo,                                   \n" // 1
                    + "            REL.ds_nome,                                     \n" // 2
                    + "            PU.ds_nome,                                      \n" // 3
                    + "            nr_tempo,                                        \n" // 4
                    + "            nr_tempo_query,                                  \n" // 5
                    + "            dt_data,                                         \n" // 6
                    + "            dt_finalizado,                                   \n" // 7
                    + "            dt_abortado,                                     \n" // 8
                    + "            P.id                                             \n" // 9
                    + "   ";

        }
        queryString
                += "      FROM sis_processo AS P                                \n"
                + " INNER JOIN seg_rotina   AS R ON R.id = P.id_rotina          \n"
                + "  LEFT JOIN sis_relatorios AS REL ON REL.id = P.id_relatorio \n"
                + "  LEFT JOIN seg_usuario AS U ON U.id = P.id_usuario          \n"
                + "  LEFT JOIN pes_pessoa AS PU ON PU.id = U.id_pessoa          \n";
        if (!dataInicial.isEmpty() && dataFinal.isEmpty()) {
            listWhere.add(" P.dt_data::date = '" + dataInicial + "'");
        } else if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
            listWhere.add(" P.dt_data::date BETWEEN '" + dataInicial + "' AND '" + dataFinal + "'");
        }
        // listWhere.add(" P.ds_processo IS NOT NULL AND  P.ds_processo <> '' ");
        if (rotina_id != 0) {
            listWhere.add(" P.id_rotina = " + rotina_id);
        }
        if (relatorio_id != null) {
            listWhere.add(" P.id_relaotio = " + relatorio_id);
        }
        if (usuario_id != null) {
            listWhere.add(" P.id_usuario = " + usuario_id);
        }
        if (!descricao.isEmpty()) {
            listWhere.add(" func_translate(UPPER(P.ds_processo)) LIKE func_translate('%" + descricao.toUpperCase() + "%') OR func_translate(UPPER(P.ds_processo)) LIKE func_translate('%" + descricao.toUpperCase() + "%') ");
        }
        try {
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " ";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " ";
                }
            }
            if (agrupado) {
                queryString += " GROUP BY 1,2,3 ";
                queryString += " ORDER BY avg(nr_tempo) ";
            } else {
                queryString += " ORDER BY dt_data DESC";
            }
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public Object avgByRotina(Integer rotina_id) {
        try {
            List list = find("", "", "", "", rotina_id, null, null, "", true);
            return ((List) list.get(0)).get(4);
        } catch (Exception e) {
            return 0;
        }
    }
}
