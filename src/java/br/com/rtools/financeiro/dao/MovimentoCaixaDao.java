/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MovimentoCaixaDao extends DB {

    public List<Date> listaDatas() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT dt_data \n "
                + "FROM fin_conta_saldo \n "
                + "WHERE dt_data >= CURRENT_DATE - 365 \n"
                + "GROUP BY dt_data \n "
                + "ORDER BY dt_data"
        );

        try {
            return (List<Date>) qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public String pesquisaDataSelecionada() {
        Query qry = getEntityManager().createNativeQuery(
            "SELECT dt_data FROM fin_conta_saldo WHERE dt_data >= CURRENT_DATE - 30 LIMIT 1"
        );

        try {
            return DataHoje.converteData((Date) ((List) qry.getSingleResult()).get(0));
        } catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    public List<Object> listaSaldo(String data) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT baixa AS baixa,\n"
                + "       SUM(valor_baixa) AS valor,\n"
                + "       0::double precision AS saldo_atual \n"
                + "  FROM movimentos_vw AS m \n"
                + "  LEFT JOIN fin_caixa AS c ON c.id = m.id_caixa   \n"
                + "  LEFT JOIN pes_pessoa AS p ON p.id = m.id_pessoa \n"
                + "  LEFT JOIN pes_pessoa AS t ON t.id = m.id_titular \n"
                + "  LEFT JOIN pes_pessoa AS b ON b.id = m.id_beneficiario \n"
                + " WHERE baixa >= '" + data + "' AND baixa <= CURRENT_DATE \n"
                + "   AND m.id_caixa_banco = 1 \n"
                + " GROUP BY baixa \n"
                + " ORDER BY baixa "
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaSaldoDetalhe(String data) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT \n"
                + "	------------ Grid \n"
                + "	baixa AS baixa, \n"
                + "	RTRIM(LTRIM(RTRIM(LTRIM(func_nullstring(servico)))||' '||conta_contabil)) AS operacao, \n"
                + "	es AS es, \n"
                + "	valor_baixa AS valor, \n"
                + "	------------ Detalhes \n"
                + "	ds_historico AS historico, \n"
                + "	p.id AS id_responsavel, \n"
                + "	t.id AS id_titular, \n"
                + "	b.id AS id_beneficiario \n"
                + "  FROM movimentos_vw AS m \n"
                + "  LEFT JOIN fin_caixa  AS c on c.id = m.id_caixa \n"
                + "  LEFT JOIN pes_pessoa AS p on p.id = m.id_pessoa \n"
                + "  LEFT JOIN pes_pessoa AS t on t.id = m.id_titular \n"
                + "  LEFT JOIN pes_pessoa AS b on b.id = m.id_beneficiario \n"
                + " WHERE baixa = '" + data + "' \n "
                + "   AND id_caixa_banco = 1 \n "
                + " ORDER BY id_movimento "
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}