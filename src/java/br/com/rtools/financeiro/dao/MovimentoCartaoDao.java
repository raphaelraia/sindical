/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.CartaoRec;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MovimentoCartaoDao extends DB {

    public List<Object> listaCartoesCombo() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT c.id AS id_cartao \n "
                + "  FROM movimentos_vw AS m \n "
                + " INNER JOIN fin_cartao_rec AS cr ON cr.id = m.id_cartao_rec \n "
                + " INNER JOIN fin_cartao AS c ON c.id = cr.id_cartao \n "
                + " INNER JOIN fin_plano5 AS p ON p.id = c.id_plano5 \n "
                + " WHERE m.id_cartao_rec > 0 AND m.id_baixa_status = 8 \n"
                + " GROUP BY c.id"
        );

        try {
            return (List<Object>) qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaCartoes(Integer id_cartao, Integer id_plano5) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT \n "
                + "    m.id_forma_pagamento, \n "
                + "    m.id_baixa, \n "
                + "    baixa as baixa, \n "
                + "    sum(valor_baixa) AS valor, \n "
                + "    sum(((valor_baixa * c.nr_taxa) / 100)) AS taxa, \n "
                + "    sum(valor_baixa - ((valor_baixa * c.nr_taxa) / 100)) AS liquido, \n "
                + "    extract(year from baixa) || right('0' || extract(month from baixa), 2) || right('0' || extract(day from baixa), 2) AS baixa_ordem, \n "
                + "    m. dt_credito as credito \n "
                + "  FROM movimentos_vw AS m \n "
                + " INNER JOIN fin_cartao_rec AS cr ON cr.id = m.id_cartao_rec \n "
                + " INNER JOIN fin_cartao    AS c ON c.id = cr.id_cartao \n "
                + " INNER JOIN fin_plano5    AS pl ON pl.id = c.id_plano5 \n "
                + "  LEFT JOIN fin_caixa      AS cx ON cx.id = m.id_caixa \n "
                + "  LEFT JOIN pes_pessoa     AS p ON p.id = m.id_pessoa \n "
                + "  LEFT JOIN pes_pessoa     AS t ON t.id = m.id_titular \n "
                + "  LEFT JOIN pes_pessoa     AS b ON b.id = m.id_beneficiario \n "
                + " WHERE m.id_cartao_rec > 0 \n "
                + "   AND m.id_baixa_status = 8 \n "
                + "   AND es = 'E' \n "
                + "   AND c.id = " + id_cartao + " \n "
                + "   AND c.id_plano5 = " + id_plano5 + " \n "
                + " GROUP BY m.id_forma_pagamento, baixa, m.id_baixa, m. dt_credito"
        );

        try {
            return (List<Object>) qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaCartoesDetalhe(Integer id_forma_pagamento) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT \n "
                + "    p.id as responsavel, \n "
                + "    t.id as titular, \n "
                + "    b.id as beneficiario, \n "
                + "    m.vencimento as vencimento, \n "
                + "    RTRIM(LTRIM(RTRIM(LTRIM(func_nullstring(m.servico)))||' '||m.conta_contabil)) AS operacao, \n "
                + "    valor_baixa as valor \n "
                + "  FROM movimentos_vw AS m \n "
                + "  LEFT JOIN pes_pessoa     AS p ON p.id = m.id_pessoa \n "
                + "  LEFT JOIN pes_pessoa     AS t ON t.id = m.id_titular \n "
                + "  LEFT JOIN pes_pessoa     AS b ON b.id = m.id_beneficiario \n "
                + " WHERE m.id_forma_pagamento = " + id_forma_pagamento + " \n "
                + " ORDER BY \n "
                + "    m.vencimento, \n "
                + "    p.ds_nome, \n "
                + "    t.ds_nome, \n "
                + "    b.ds_nome "
        );

        try {
            return (List<Object>) qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
