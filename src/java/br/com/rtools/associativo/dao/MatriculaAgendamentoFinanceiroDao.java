/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.MatriculaSeguro;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MatriculaAgendamentoFinanceiroDao extends DB {

    public List<ServicoPessoa> listaServicoPessoa(Integer id_pessoa) {
        Query qry = getEntityManager().createNativeQuery("", ServicoPessoa.class);

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }
    
}
