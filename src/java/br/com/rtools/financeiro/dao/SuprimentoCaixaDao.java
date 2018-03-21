/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author claudemir
 */
public class SuprimentoCaixaDao extends DB {

    public List<Object> listaContasSaida() {
        try {
            String text
                    = "SELECT p.id_p5, \n"
                    + "       p.conta5 \n"
                    + "  FROM plano_vw AS p \n"
                    + " INNER JOIN fin_conta_rotina AS cr ON cr.id_plano4 = p.id_p4 \n"
                    + " WHERE cr.id_rotina = 2 \n"
                    + " ORDER BY p.conta5";

            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaContasEntrada() {
        try {
            String text
                    = "SELECT p.id_p5, \n"
                    + "       p.conta5 \n"
                    + "  FROM plano_vw AS p \n"
                    + " WHERE p.id_p5 = 1 \n"
                    + " ORDER BY p.conta5";

            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
