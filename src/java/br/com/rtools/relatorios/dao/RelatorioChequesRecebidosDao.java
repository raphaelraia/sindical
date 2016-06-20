package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioChequesRecebidosDao extends DB {

    public List findAllPlanos5CaixaBanco() {
        String queryString = ""
                + "SELECT P5.*                  \n"
                + "  FROM fin_plano5 AS P5      \n"
                + " WHERE P5.id IN (            \n"
                + "     SELECT id_plano5        \n"
                + "       FROM caixa_banco_vw   \n"
                + "      WHERE id_plano5 > 1    \n"
                + ")                            \n"
                + "ORDER BY ds_conta";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
