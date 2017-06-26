/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class ConciliacaoDao extends DB {

    public List<Plano5> listaCaixaBancoVW() {
        String queryString = ""
                + "SELECT p5.*                  \n"
                + "  FROM fin_plano5 AS p5      \n"
                + " WHERE p5.id IN (            \n"
                + "     SELECT id_plano5        \n"
                + "       FROM caixa_banco_vw   \n"
                + "      WHERE id_plano5 > 0    \n"
                + ")                            \n"
                + "ORDER BY ds_conta";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Plano5 pesquisaPlano5Conciliacao() {
        String queryString = ""
                + "SELECT p5.* \n"
                + "  FROM fin_plano5 p5 \n "
                + " WHERE p5.id_conta_tipo = 4";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return (Plano5) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Object> listaConciliacao(Integer id_conta_conciliacao, String data, String filtro) {
        String query = ""
                + "SELECT m.id_pessoa AS id_pessoa, \n"
                + "       m.id_tipo_pagamento AS tipo_pagamento, \n"
                + "       (ROUND(CAST(m.valor_baixa * 100 AS numeric), 2) / 100)::double precision AS valor_baixa, \n"
                + "       m.dt_conciliacao AS data_conciliacao, \n"
                + "       m.id_forma_pagamento AS forma_pagamento, \n"
                + "       m.id_conciliado AS conciliado \n"
                + "  FROM movimentos_vw AS m \n";

        List<String> list_where = new ArrayList();

        list_where.add("m.id_conciliacao_plano5 = " + id_conta_conciliacao);
        list_where.add("m.id_tipo_pagamento IN (8, 9, 10)");

//        if (!data.isEmpty()) {
//            list_where.add("m.baixa = '" + data + "'");
//        }

        switch (filtro) {
            case "conciliar":
                list_where.add("m.id_conciliado IS NULL");
                break;
            case "conciliados":
                list_where.add("m.id_conciliado IS NOT NULL");
                break;
            default:
                break;
        }

        String where = "";
        for (int i = 0; i < list_where.size(); i++) {
            if (i == 0) {
                where = " WHERE " + list_where.get(i) + " \n ";
            } else {
                where += " AND " + list_where.get(i) + " \n ";
            }
        }

        try {
            Query qry = getEntityManager().createNativeQuery(query + where);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<Object> listaParaConciliar(Integer id_plano5_baixa, String data_conciliacao, Double valor) {
        String query
                = "SELECT m.baixa AS baixa,\n"
                + "       (ROUND(CAST(m.valor_baixa * 100 AS numeric), 2) / 100)::double precision AS valor_baixa, \n"
                + "       m.id_forma_pagamento AS forma_pagamento \n"
                + "  FROM movimentos_vw AS m \n"
                + " INNER JOIN fin_plano5 AS p ON p.id = m.id_plano5 \n"
                + "   AND dt_conciliacao IS NULL \n"
                + "   AND p.id_conta_tipo = 4 \n"
                + "   AND m.id_plano5_baixa = " + id_plano5_baixa + "\n"
                + "   AND m.baixa = '" + data_conciliacao + "'"
                + "   AND (ROUND(CAST(m.valor_baixa * 100 AS numeric), 2) / 100)::double precision = " + valor;
        try {
            Query qry = getEntityManager().createNativeQuery(query);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }
}
