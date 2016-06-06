/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

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
                    "SELECT cs.id,"
                    + "     p.id, \n "
                    + "     cs.dt_data, \n "
                    + "     p.ds_conta, \n "
                    + "     sum(cs.nr_saldo) \n"
                    + "  FROM fin_conta_saldo AS cs \n"
                    + "  INNER JOIN fin_plano5 AS p ON p.id = cs.id_plano5 \n"
                    + "  WHERE cs.dt_data = '" + data + "' \n"
                    + " GROUP BY cs.id, p.id, cs.dt_data, p.ds_conta\n"
                    + " ORDER BY cs.dt_data, p.ds_conta"
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Object> listaConcluirFechamentoDiario(String data) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT baixa,\n"
                    + "     sum(valor) as valor,\n"
                    + "     id_conta,\n"
                    + "     1 as id_usuario,\n"
                    + "     1 as id_filial \n"
                    + "  FROM contabil_vw \n"
                    + " WHERE baixa = '" + data + "' \n"
                    + " GROUP BY baixa, id_conta, id_usuario, id_filial "
            );
            return qry.getResultList();
        } catch (Exception e) {
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

}
