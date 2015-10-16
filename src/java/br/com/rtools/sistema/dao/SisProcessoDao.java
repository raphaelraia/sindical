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

    public List find(String dataInicial, String dataFinal, String horaInicial, String horaFinal, Integer idRotina, String descricao) {
        List listWhere = new ArrayList();
        String queryString = ""
                + "     SELECT id_rotina,                                       \n" // 0
                + "            ds_rotina,                                       \n" // 1
                + "            P.ds_processo,                                   \n" // 2
                + "            min(nr_tempo) AS menor,                          \n" // 3
                + "            round(avg(nr_tempo), 0) AS media,                \n" // 4
                + "            max(nr_tempo) AS maior,                          \n" // 5
                + "            min(nr_tempo) AS menor_query,                    \n" // 6
                + "            round(avg(nr_tempo), 0) AS media_query,          \n" // 7
                + "            max(nr_tempo) AS maior_query                     \n" // 8
                + "       FROM sis_processo AS P                                \n"
                + " INNER JOIN seg_rotina   AS R ON R.id = P.id_rotina          \n"
                + "   ";
        if (dataInicial != null && !dataInicial.isEmpty()) {
            listWhere.add(" P.dt_data '" + dataInicial + "'");
            if (!horaInicial.isEmpty() && !horaFinal.isEmpty()) {
                // listWhere.add(" P.hora BETWEEN '" + horaInicial + "' AND '" + horaFinal + "' ");
            } else if (!horaInicial.isEmpty()) {
                // listWhere.add(" SP.hora LIKE '" + horaInicial + "' ");
            }
        }
        listWhere.add(" P.ds_processo IS NOT NULL AND  P.ds_processo <> '' ");
        if (idRotina != 0) {
            listWhere.add(" P.id_rotina = " + idRotina);
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
            queryString += " GROUP BY 1,2,3 ";
            queryString += " ORDER BY avg(nr_tempo) ";
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
            List list = find("", "", "", "", rotina_id, "");
            return ((List) list.get(0)).get(4);
        } catch (Exception e) {
            return 0;
        }
    }
}
