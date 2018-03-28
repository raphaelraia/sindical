/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Windows
 */
public class ImpressaoEscritorioDao extends DB {

    public List<Object> listaEmpresa(Integer id_escritorio, Integer id_servico, Integer id_tipo_servico, String referencia) {
        try {
            String text
                    = "SELECT c.id_pessoa AS id_pessoa,\n"
                    + "       c.id_juridica AS id_juridica,\n"
                    + "	      c.id_convencao AS id_convencao,\n"
                    + "	      c.id_grupo_cidade AS id_grupo_cidade\n"
                    + "  FROM arr_contribuintes_vw c\n"
                    + " WHERE c.id_contabilidade = " + id_escritorio
                    + " ORDER BY c.ds_nome";
//            String text
//                    = "SELECT c.id_pessoa AS id_pessoa, \n"
//                    + "       c.id_juridica AS id_juridica, \n"
//                    + "	      c.id_convencao AS id_convencao, \n"
//                    + "	      c.id_grupo_cidade AS id_grupo_cidade, \n"
//                    + "	      func_multa_sm(c.id_pessoa, " + id_servico + ", " + id_tipo_servico + ",'" + referencia + "') AS multa, \n"
//                    + "	      func_juros_sm(c.id_pessoa, " + id_servico + ", " + id_tipo_servico + ",'" + referencia + "') AS juros, \n"
//                    + "	      func_correcao_sm(c.id_pessoa, " + id_servico + ", " + id_tipo_servico + ",'" + referencia + "') AS correcao \n"
//                    + "  FROM arr_contribuintes_vw c\n"
//                    + " WHERE c.id_contabilidade = " + id_escritorio
//                    + " ORDER BY c.ds_nome";
            Query qry = getEntityManager().createNativeQuery(text);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
