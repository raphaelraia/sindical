/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class FechamentoDiarioDao extends DB {

    public List<Object> listaFechamentoDiario() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT cs.dt_data, \n"
                    + "       sum(cs.nr_saldo) \n"
                    + "  FROM fin_conta_saldo cs \n"
                    + " WHERE cs.id_plano5 IN (SELECT cb.id_plano5 FROM caixa_banco_vw cb) \n"
                    + " GROUP BY cs.dt_data \n"
                    + " ORDER BY cs.dt_data DESC"
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Object> listaFechamentoDiarioDetalhe(String data) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT cs.id, \n"
                    + "      p.id_p5, \n"
                    + "      cs.dt_data, \n"
                    + "      p.conta5, \n"
                    + "      sum(cs.nr_saldo), \n"
                    + "      p.classificador, \n"
                    + "      cb.id_plano5, \n"
                    + "	     p.id_p1, \n"
                    + "	     p.id_p4, \n"
                    + "	     p.conta1 \n"
                    + "   FROM fin_conta_saldo AS cs \n"
                    + "  INNER JOIN plano_vw AS p ON p.id_p5 = cs.id_plano5 \n"
                    + "   LEFT JOIN caixa_banco_vw AS CB ON CB.id_plano5 = P.id_p5 \n"
                    + "  WHERE cs.dt_data = '" + data + "' \n"
                    + "    AND cs.nr_saldo <> 0 \n"
                    + "  GROUP BY cs.id, p.id_p5, cs.dt_data, p.conta5, p.classificador, CB.id_plano5, p.id_p1, p.id_p4, p.conta1 \n"
                    + "  ORDER BY cs.dt_data, p.id_p4, p.conta5, p.conta1"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<Object> listaConcluirFechamentoDiario(String data_fechamento, String ultima_data) {
        try {
//            Query qry = getEntityManager().createNativeQuery(
//                    "SELECT baixa,\n"
//                    + "     sum(valor) as valor,\n"
//                    + "     id_conta,\n"
//                    + "     1 as id_usuario,\n"
//                    + "     1 as id_filial \n"
//                    + "  FROM contabil_vw \n"
//                    + " WHERE baixa = '" + data + "' \n"
//                    + " GROUP BY baixa, id_conta, id_usuario, id_filial "
//            );
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT \n"
                    + "------------------------------------------> Dia Selecionado \n"
                    + "       cast('" + data_fechamento + "' AS date), \n"
                    + "       sum(valor) AS valor, \n"
                    + "       id_conta, \n"
                    + "       1 AS id_usuario, \n"
                    + "       1 AS id_filial \n"
                    + "  \n"
                    + "FROM \n"
                    + "( \n"
                    + "\n"
                    + "SELECT id, \n"
                    + "       nr_saldo AS valor, \n"
                    + "       id_plano5 AS id_conta, \n"
                    + "       1 AS id_usuario, \n"
                    + "       1 AS id_filial \n"
                    + "  FROM fin_conta_saldo \n"
                    + "------------------------------------------> Dia Anterior \n"
                    + " WHERE dt_data = '" + ultima_data + "' \n"
                    + "\n"
                    + "UNION ALL \n"
                    + "\n"
                    + "SELECT \n"
                    + "       id_forma_pagamento AS id, \n"
                    + "       valor * func_dc(dc, natureza_dc) AS valor, \n"
                    + "       id_conta, \n"
                    + "       1 AS id_usuario, \n"
                    + "       1 AS id_filial \n"
                    + "  FROM contabil_vw \n"
                    + "------------------------------------------> Dia Selecionado \n"
                    + " WHERE baixa = '" + data_fechamento + "' \n"
                    + "  \n"
                    + ") AS b \n"
                    + " WHERE valor <> 0  \n"
                    + "GROUP BY 1,3,4,5 "
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public Date ultimaDataContaSaldo() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT max(dt_data) \n"
                    + "  FROM fin_conta_saldo "
            );
            List result = (List) qry.getSingleResult();

            return (Date) result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContaSaldo> listaFechamentoDiarioDetalheTodos(String data) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT cs.* \n"
                    + "  FROM fin_conta_saldo AS cs \n"
                    + "  WHERE cs.dt_data = '" + data + "'", ContaSaldo.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
