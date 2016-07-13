/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class DeclaracaoPessoaDao extends DB {

    public List<Object> listaConvenio(Integer id_declaracao_tipo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.id AS id, \n"
                    + "       p.ds_documento AS documento, \n"
                    + "       p.ds_nome AS nome,\n"
                    + "       j.ds_fantasia AS fantasia \n"
                    + "  FROM pes_juridica AS j \n"
                    + " INNER JOIN pes_pessoa AS p ON p.id = j.id_pessoa \n"
                    + " INNER JOIN soc_convenio AS c ON c.id_juridica = j.id \n"
                    + " INNER JOIN soc_declaracao_grupo AS g ON g.id_subgrupo = c.id_convenio_sub_grupo \n"
                    + " WHERE g.id_declaracao_tipo = " + id_declaracao_tipo);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
