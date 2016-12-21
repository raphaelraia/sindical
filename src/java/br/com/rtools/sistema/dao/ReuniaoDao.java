package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Reuniao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ReuniaoDao extends DB {

    public Boolean exist(String data, String titulo) {
        try {
            String queryString = ""
                    + "     SELECT R.*                                          \n"
                    + "       FROM sis_reuniao R                                \n"
                    + "      WHERE trim(func_translate(R.ds_titulo)) LIKE trim(func_translate('" + titulo + "')) \n"
                    + "        AND R.dt_reuniao = '" + data + "'                    ";
            Query query = getEntityManager().createNativeQuery(queryString);
            Reuniao r = (Reuniao) query.getSingleResult();
            if (r != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List find(String criacao, String data, String titulo, Integer operador_id) {
        try {
            String queryString = ""
                    + "     SELECT R.*                                          \n"
                    + "       FROM sis_reuniao R                                \n";
            List listWhere = new ArrayList();
            if (operador_id != null) {
                listWhere.add("R.id_operador = '" + operador_id + "' ");
            }
            if (!titulo.isEmpty()) {
                listWhere.add("trim(func_translate(R.ds_titulo)) LIKE trim(func_translate('" + titulo + "')) ");
            }
            if (!data.isEmpty()) {
                listWhere.add("R.dt_reuniao = '" + data + "' ");
            }
            if (!criacao.isEmpty()) {
                listWhere.add("R.dt_criacao = '" + criacao + "' ");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString = " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString = " AND " + listWhere.get(i).toString() + "\n";
                }
            }
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
