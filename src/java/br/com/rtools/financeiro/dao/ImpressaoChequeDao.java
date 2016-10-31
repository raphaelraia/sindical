/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ImpressoraCheque;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class ImpressaoChequeDao extends DB {

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

    public List<Object> listaCheques(Integer conta_id, String status) {
        String query
                = "SELECT bc.nr_num_banco AS codbanco, \n"
                + "       bc.ds_banco AS banco, \n"
                + "       f.nr_valor AS valor, \n"
                + "       p.ds_nome AS favorecido, \n"
                + "       b.dt_baixa AS baixa, \n"
                + "       ch.id, \n"
                + "       ch.dt_emissao AS emissao, \n"
                + "       ch.dt_vencimento AS vencimento, \n"
                + "       ch.dt_cancelamento AS cancelamento, \n"
                + "       ch.ds_cheque AS cheque, \n"
                + "       ch.dt_impressao AS impressao \n"
                + "  FROM fin_cheque_pag AS ch \n"
                + " INNER JOIN fin_plano5 AS p5 ON p5.id = ch.id_plano5 \n"
                + " INNER JOIN fin_conta_banco AS cb ON cb.id = p5.id_conta_banco \n"
                + " INNER JOIN fin_banco AS bc on bc.id = cb.id_banco \n"
                + " INNER JOIN fin_forma_pagamento AS f ON f.id_cheque_pag = ch.id \n"
                + " INNER JOIN fin_baixa AS b ON b.id = f.id_baixa \n"
                + " INNER JOIN fin_movimento AS m ON m.id_baixa = b.id \n"
                + " INNER JOIN pes_pessoa AS p ON p.id = m.id_pessoa \n"
                + " WHERE p5.id = " + conta_id + " \n ";

        switch (status) {
            case "emitir":
                query += " AND ch.dt_impressao IS NULL AND ch.dt_cancelamento IS NULL \n";
                break;
            case "emitidos":
                query += " AND ch.dt_impressao IS NOT NULL \n";
                break;
            case "cancelados":
                query += " AND ch.dt_cancelamento IS NOT NULL \n";
                break;
            default:
                break;
        }

        query += " GROUP BY \n"
                + "       bc.nr_num_banco, \n"
                + "       bc.ds_banco, \n"
                + "       f.nr_valor, \n"
                + "       p.ds_nome, \n"
                + "       b.dt_baixa, \n"
                + "       ch.id, \n"
                + "       ch.dt_emissao, \n"
                + "       ch.dt_vencimento, \n"
                + "       ch.dt_cancelamento, \n"
                + "       ch.ds_cheque \n";
        
        query += " ORDER BY \n"
                + "       ch.ds_cheque";
        try {
            Query qry = getEntityManager().createNativeQuery(query);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<ImpressoraCheque> listaImpressora() {
        String queryString
                = "SELECT ic.* \n "
                + "  FROM fin_impressora_cheque AS ic \n "
                + " ORDER BY ic.ds_apelido";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, ImpressoraCheque.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public ImpressoraCheque pesquisaImpressoraNumeroAtiva(Integer numero) {
        String queryString
                = "SELECT ic.* \n "
                + "  FROM fin_impressora_cheque AS ic \n "
                + " WHERE ic.nr_impressora = " + numero + " \n "
                + "   AND ic.is_ativo = TRUE \n "
                + "   AND ic.ds_favorecido IS NOT NULL AND ic.ds_favorecido <> ''";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, ImpressoraCheque.class);
            return (ImpressoraCheque) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public ImpressoraCheque pesquisaImpressoraNumero(Integer numero) {
        String queryString
                = "SELECT ic.* \n "
                + "  FROM fin_impressora_cheque AS ic \n "
                + " WHERE ic.nr_impressora = " + numero;
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, ImpressoraCheque.class);
            return (ImpressoraCheque) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
