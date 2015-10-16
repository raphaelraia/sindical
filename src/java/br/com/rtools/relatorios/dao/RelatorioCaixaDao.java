package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 * 1056 Criada por: Rogério em 08/10/2015 10:36
 *
 * @since 08/10/2015 10:36
 * @author Rogério
 */
public class RelatorioCaixaDao extends DB {

    /**
     * CHAMADO: 1056
     *
     * @param relatorio_id
     * @param inCaixa
     * @param inOperador
     * @param inTipoPagamento
     * @param dataBaixaInicial
     * @param dataBaixaFinal
     * @return
     */
    public List find(Integer relatorio_id, String inCaixa, String inOperador, String inTipoPagamento, String dataBaixaInicial, String dataBaixaFinal) {
        try {
            String queryString = " -- RelatorioCaixaDao->find() \n"
                    + " SELECT ";
            String queryAsString = "";
            String queryGroup = "";
            if (relatorio_id == 60) {
                /**
                 * Resumo de Caixa (CHAMADO 1056)
                 */
                queryAsString += ""
                        + " baixa,                      \n"
                        + " fechamento_caixa,           \n"
                        + " CX.ds_descricao  AS caixa,  \n"
                        + " sum(valor_baixa) as valor   \n";
                queryGroup += ""
                        + " baixa,              \n"
                        + " fechamento_caixa,   \n"
                        + " CX.ds_descricao    \n";
            } else if (relatorio_id == 61) {
                /**
                 * Resumo de Caixa por Operador (CHAMADO 1056)
                 */
                queryAsString += ""
                        + " baixa,                          \n"
                        + " fechamento_caixa,               \n"
                        + " CX.ds_descricao  AS caixa,      \n"
                        + " PU.ds_nome       AS operador,   \n"
                        + " sum(valor_baixa) as valor       \n";
                queryGroup += ""
                        + " baixa,              \n"
                        + " fechamento_caixa,   \n"
                        + " CX.ds_descricao,    \n"
                        + " PU.ds_nome          \n";
            }
            queryString += queryAsString;
            queryString
                    += "      FROM movimentos_vw AS M                                \n"
                    + " INNER JOIN fin_caixa     AS CX ON CX.id = M.id_caixa         \n"
                    + " INNER JOIN seg_usuario   AS U  ON U.id  = M.id_usuario_baixa \n"
                    + " INNER JOIN pes_pessoa    AS PU ON PU.id = U.id_pessoa        \n";
            List listWhere = new ArrayList<>();
            if (inCaixa != null) {
                listWhere.add("M.id_caixa IN (" + inCaixa + ") ");
            }
            if (inOperador != null) {
                listWhere.add("M.id_usuario_baixa IN (" + inOperador + ") ");
            }
            if (inTipoPagamento != null) {
                listWhere.add("M.id_tipo_pagamento IN (" + inTipoPagamento + ") ");
            }
            if (dataBaixaInicial != null && dataBaixaFinal != null && !dataBaixaInicial.isEmpty() && !dataBaixaFinal.isEmpty()) {
                listWhere.add(" baixa BETWEEN '" + dataBaixaInicial + "' AND '" + dataBaixaFinal + "'");
            } else if (dataBaixaInicial != null && (dataBaixaFinal == null || dataBaixaFinal.isEmpty()) && !dataBaixaInicial.isEmpty()) {
                listWhere.add(" baixa = '" + dataBaixaInicial + "' ");
            } else if (dataBaixaInicial == null && dataBaixaFinal != null && !dataBaixaFinal.isEmpty()) {
                listWhere.add(" baixa <= '" + dataBaixaFinal + "' ");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += " GROUP BY " + queryGroup;
            queryString += " ORDER BY " + queryGroup;
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
