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
public class RelatorioPlanoContasDao extends DB {

    public List<Object> list(Relatorios relatorio) {
        try {
            String query_string
                    = "SELECT acesso AS acesso, \n "
                    + "       classificador AS classificador, \n "
                    + "       id_p5 AS codigo, \n "
                    + "       conta1 AS conta1, \n "
                    + "       conta2 AS conta2, \n "
                    + "       conta3 AS conta3, \n "
                    + "       conta4 AS conta4, \n  "
                    + "       conta5 AS conta5, \n "
                    + "       acesso1 AS acesso1, \n "
                    + "       acesso2 AS acesso2, \n "
                    + "       acesso3 AS acesso3, \n "
                    + "       acesso4 AS acesso4, \n "
                    + "       classificador1 AS classificador1, \n "
                    + "       classificador2 AS classificador2, \n "
                    + "       classificador3 AS classificador3, \n "
                    + "       classificador4 AS classificador4 \n "
                    + "FROM plano_vw";
            Query query = getEntityManager().createNativeQuery(query_string);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
