/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.MatriculaSeguro;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MatriculaSeguroDao extends DB {

    public List<MatriculaSeguro> listaMatriculaSeguro(String descricao_pesquisa, Integer id_servico, String tipo, String por) {
        if (descricao_pesquisa.isEmpty() && id_servico == null) {
            return new ArrayList();
        }

        String text
                = "SELECT ms.* \n "
                + "  FROM matr_seguro ms \n "
                + " INNER JOIN fin_servico_pessoa sp ON sp.id = ms.id_servico_pessoa \n "
                + " INNER JOIN pes_pessoa p ON p.id = sp.id_pessoa \n ";

        descricao_pesquisa = AnaliseString.normalizeLower(descricao_pesquisa);
        descricao_pesquisa = por.equals("I") ? descricao_pesquisa + "%" : "%" + descricao_pesquisa + "%";

        List<String> list_where = new ArrayList();

        switch (tipo) {
            case "nome":
                list_where.add(" TRANSLATE(LOWER(p.ds_nome)) LIKE '" + descricao_pesquisa + "' ");
                break;
            case "cpf":
                list_where.add(" p.ds_documento LIKE '" + descricao_pesquisa + "' ");
                break;
            case "servico":
                list_where.add(" sp.id_servico = " + id_servico + " ");
                break;
        }

        String where = "";
        for (Integer i = 0; i < list_where.size(); i++) {
            if (where.isEmpty()) {
                where = " WHERE " + list_where.get(i) + " \n ";
            } else {
                where += " AND " + list_where.get(i) + " \n ";
            }
        }

        String order_by = " ORDER BY sp.dt_emissao, p.ds_nome";

        Query qry = getEntityManager().createNativeQuery(text + where + order_by, MatriculaSeguro.class);

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }

    public MatriculaSeguro pesquisaMatriculaSeguroPessoaAtiva(Integer id_pessoa, Integer id_servico) {
        Query qry = getEntityManager().createNativeQuery(
                "  SELECT ms.* \n "
                + "  FROM matr_seguro ms \n "
                + " INNER JOIN fin_servico_pessoa sp ON sp.id = ms.id_servico_pessoa \n "
                + " WHERE sp.is_ativo = true \n "
                + "   AND sp.id_pessoa = " + id_pessoa
                + "   AND sp.id_servico = " + id_servico, MatriculaSeguro.class
        );

        try {
            return  (MatriculaSeguro) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        
        return null;
    }
}
