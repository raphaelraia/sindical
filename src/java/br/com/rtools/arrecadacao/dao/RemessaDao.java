/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class RemessaDao extends DB {

    public List<Plano5> listaConta() {
        String queryString
                = "SELECT p.* \n "
                + "  FROM fin_plano5 AS p \n "
                + " WHERE p.id_conta_banco > 0 \n "
                + " ORDER BY p.ds_conta";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Remessa> listaRemessa(Integer id_conta_banco) {
        String queryString
                = "SELECT r.* \n"
                + "  FROM fin_remessa AS r \n"
                + " INNER JOIN fin_remessa_banco rb ON r.id = rb.id_remessa \n"
                + " INNER JOIN fin_boleto b ON b.id = rb.id_boleto \n"
                + " INNER JOIN fin_conta_cobranca cc ON cc.id = b.id_conta_cobranca \n"
                + " WHERE cc.id_conta_banco = " + id_conta_banco + " \n"
                + " GROUP BY r.id \n"
                + " ORDER BY r.dt_emissao, r.ds_hora_emissao DESC";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Remessa.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
    
    public List<RemessaBanco> listaRemessaBanco(Integer id_remessa) {
        String queryString
                = "SELECT rb.* \n"
                + "  FROM fin_remessa_banco AS rb \n"
                + " INNER JOIN fin_boleto b ON b.id = rb.id_boleto \n"
                + " WHERE rb.id_remessa = " + id_remessa + " \n"
                + " ORDER BY rb.id";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, RemessaBanco.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
