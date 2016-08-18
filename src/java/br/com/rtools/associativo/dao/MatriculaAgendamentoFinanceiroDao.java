/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.MatriculaAgendamentoFinanceiro;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MatriculaAgendamentoFinanceiroDao extends DB {

    public List<Object> listaServicoPessoaMatricula(Integer id_pessoa) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT sp.id AS id_servico_pessoa, \n"
                + "       ma.id AS id_matricula_agendamento\n"
                + "  FROM fin_servico_pessoa sp\n"
                + "  LEFT JOIN matr_agendamento ma ON ma.id_servico_pessoa = sp.id\n"
                + " WHERE sp.id_pessoa = " + id_pessoa
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }

    public MatriculaAgendamentoFinanceiro pesquisaMatriculaAgendamentoPessoaAtiva(Integer id_pessoa, Integer id_servico) {
        Query qry = getEntityManager().createNativeQuery(
                "  SELECT ma.* \n "
                + "  FROM matr_agendamento ma \n "
                + " INNER JOIN fin_servico_pessoa sp ON sp.id = ma.id_servico_pessoa \n "
                + " WHERE sp.is_ativo = true \n "
                + "   AND sp.id_pessoa = " + id_pessoa
                + "   AND sp.id_servico = " + id_servico, MatriculaAgendamentoFinanceiro.class
        );

        try {
            return  (MatriculaAgendamentoFinanceiro) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        
        return null;
    }
}
