/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.dao;

import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.RetornoReprocessa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class RetornoDao extends DB {

    public List<Retorno> listaRetornoNaoPermitido(Integer id_conta_cobranca, Integer nr_sequencial) {
        try {
            // SE VIER RESULTADO NÃO PODE BAIXAR
            Query query = getEntityManager().createNativeQuery(
                    "SELECT r.* \n "
                    + "  FROM fin_retorno r \n "
                    + " WHERE r.nr_sequencial = " + nr_sequencial + " \n "
                    //+ " WHERE (SELECT MAX(nr_sequencial) FROM fin_retorno) = " + nr_sequencial + " \n "
                    + "   AND " + nr_sequencial + " NOT IN (SELECT nr_sequencial FROM fin_retorno_reprocessa WHERE id_conta_cobranca = " + id_conta_cobranca + ") \n "
                    + "   AND r.id_conta_cobranca = " + id_conta_cobranca, Retorno.class
            );
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Retorno> listaRetorno(Integer id_conta_cobranca) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT r.* \n "
                    + "  FROM fin_retorno r \n "
                    + " WHERE r.id_conta_cobranca = " + id_conta_cobranca
                    + " ORDER BY r.nr_sequencial DESC", Retorno.class
            );
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<RetornoReprocessa> listaRetornoReprocessa(Integer id_conta_cobranca) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT rr.* \n "
                    + "  FROM fin_retorno_reprocessa rr \n "
                    + " WHERE rr.id_conta_cobranca = " + id_conta_cobranca
                    + " ORDER BY rr.nr_sequencial DESC", RetornoReprocessa.class
            );
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<RetornoReprocessa> listaRetornoReprocessa(Integer id_conta_cobranca, Integer sequencial) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT rr.* \n "
                    + "  FROM fin_retorno_reprocessa rr \n "
                    + " WHERE rr.id_conta_cobranca = " + id_conta_cobranca
                    + "   AND rr.nr_sequencial = " + sequencial
                    + " ORDER BY rr.nr_sequencial DESC", RetornoReprocessa.class
            );
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    
    public void corrigeRetornoIncorreto() {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT r.* \n "
                    + "  FROM fin_retorno r \n "
                    + " WHERE r.id NOT IN (SELECT rb.id_retorno FROM fin_retorno_banco rb) \n "
                    + " ORDER BY r.nr_sequencial DESC", Retorno.class
            );
            List<Retorno> lr = query.getResultList();
            Dao dao = new Dao();
            
            lr.stream().forEach((r) -> {
                dao.delete(r, true);
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    
}
