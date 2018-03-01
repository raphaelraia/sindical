package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ContaTipoPagamento;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author claudemir
 */
public class ContaRecebimentoDao extends DB {

    public List<ContaTipoPagamento> listaContaTipoPagamento() {

        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT ctp.* \n"
                    + "  FROM fin_conta_tipo_pagamento AS ctp \n"
                    + " INNER JOIN fin_tipo_pagamento AS tp ON ctp.id_tipo_pagamento = tp.id \n"
                    + " WHERE ctp.is_recebimento = true \n"
                    + " ORDER BY tp.ds_descricao ", ContaTipoPagamento.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaPlano5(String pesquisar) {

        String WHERE = "";

        if (!pesquisar.isEmpty()) {

            String norm = AnaliseString.normalizeLower(pesquisar);

            WHERE = "WHERE (LOWER(FUNC_TRANSLATE(conta1)) LIKE '%" + norm + "%' "
                    + " OR LOWER(FUNC_TRANSLATE(conta4)) LIKE '%" + norm + "%' "
                    + " OR LOWER(FUNC_TRANSLATE(conta5)) LIKE '%" + norm + "%'"
                    + ") \n";
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT id_p5, conta1, conta4, '['||conta5||']' FROM plano_vw " + WHERE);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<TipoPagamento> listaTipoPagamentoBaixa(String id_in) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT tp.* \n"
                    + "  FROM fin_tipo_pagamento tp \n"
                    + "  LEFT JOIN fin_conta_tipo_pagamento ctp ON ctp.id_tipo_pagamento = tp.id \n"
                    + " WHERE tp.id IN (" + id_in + ") \n"
                    + "   AND (ctp.id_tipo_pagamento IS NULL) OR (ctp.id_tipo_pagamento IS NOT NULL AND ctp.id_plano5 IS NOT NULL) \n "
                    + " ORDER BY tp.id",
                    TipoPagamento.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public ContaTipoPagamento pesquisaContaTipoPagamento(Integer id_tipo_pagamento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT ctp.* \n"
                    + "  FROM fin_conta_tipo_pagamento ctp \n"
                    + " WHERE ctp.id_tipo_pagamento = " + id_tipo_pagamento,
                    ContaTipoPagamento.class
            );

            return (ContaTipoPagamento) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
