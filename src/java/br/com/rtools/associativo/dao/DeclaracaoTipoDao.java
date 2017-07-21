/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.DeclaracaoPeriodo;
import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class DeclaracaoTipoDao extends DB {

    public List<DeclaracaoTipo> listaDeclaracaoTipo() {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT dt.* FROM soc_declaracao_tipo dt ORDER BY dt.ds_descricao", DeclaracaoTipo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPeriodo> listaDeclaracaoPeriodo(Integer id_declaracao_tipo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT dp.* \n "
                    + " FROM soc_declaracao_periodo dp \n "
                    + "WHERE dp.id_declaracao_tipo = " + id_declaracao_tipo + "\n "
                    + "ORDER BY dp.ds_descricao", DeclaracaoPeriodo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPeriodo> listaDeclaracaoPeriodoEmissao(Integer id_declaracao_tipo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.* \n "
                    + "  FROM soc_declaracao_tipo AS t \n "
                    + " INNER JOIN soc_declaracao_periodo AS p ON p.id_declaracao_tipo = t.id \n "
                    + " WHERE EXTRACT(YEAR FROM CURRENT_DATE) = p.nr_ano \n "
                    + "   AND t.id = " + id_declaracao_tipo + " \n "
                    + " ORDER BY p.ds_descricao", DeclaracaoPeriodo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPeriodo> listaDeclaracaoPeriodoExiste(Integer id_declaracao_tipo, String descricao, Integer ano) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT dp.* \n "
                    + " FROM soc_declaracao_periodo dp \n "
                    + "WHERE dp.id_declaracao_tipo = " + id_declaracao_tipo + "\n "
                    + "  AND TRIM(dp.ds_descricao) LIKE '" + descricao.trim() + "' \n "
                    + "  AND dp.nr_ano = " + ano + " \n "
                    + "ORDER BY dp.ds_descricao", DeclaracaoPeriodo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
