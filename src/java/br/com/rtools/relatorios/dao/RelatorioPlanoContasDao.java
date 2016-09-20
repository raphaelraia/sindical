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
            String query_string = "SELECT acesso, classificador, id_p5 AS codigo, conta1, conta2, conta3, conta4, conta5 FROM plano_vw";
            Query query = getEntityManager().createNativeQuery(query_string);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
