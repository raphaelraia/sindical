/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class RelatorioContabilDao extends DB {

    public List<Object> pesquisar(Relatorios relatorio, String data_inicial, String data_final) {
        try {
            String query_string
                    = "SELECT \n "
                    + "	cv.baixa AS baixa, \n "
                    + "	cv.nr_conta AS conta, \n "
                    + "	cv.conta_contabil AS conta_contabil, \n "
                    + "	cv.nr_contra_partida AS contra_partida, \n "
                    + "	cv.historico AS historico, \n "
                    + "CASE WHEN dc = 'D' THEN cv.valor ELSE 0 END AS debito, \n "
                    + "CASE WHEN dc = 'C' THEN cv.valor ELSE 0 END AS credito \n "
                    + "FROM contabil_vw cv \n ";

            List list_where = new ArrayList();

            if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                list_where.add("cv.baixa BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
            } else if (!data_inicial.isEmpty() && data_final.isEmpty()) {
                list_where.add("cv.baixa = '" + data_inicial + "'");
            } else if (data_inicial.isEmpty() && !data_final.isEmpty()) {
                list_where.add("cv.baixa = '" + data_final + "'");
            }

            String where = "";
            for (int i = 0; i < list_where.size(); i++) {
                if (i == 0) {
                    where += " WHERE " + list_where.get(i).toString() + " \n";
                } else {
                    where += " AND " + list_where.get(i).toString() + " \n";
                }
            }

            String order_by = " ORDER BY cv.baixa, cv.id_movimento ";
            
            Query query = getEntityManager().createNativeQuery(query_string + where + order_by);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
