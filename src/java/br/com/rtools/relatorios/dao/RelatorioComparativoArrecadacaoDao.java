package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioComparativoArrecadacaoDao extends DB {

    private String order = "";

    /**
     *
     * @param relatorios
     * @param empresa
     * @param contabilidade
     * @param servico
     * @param tipoServico
     * @param referencia1
     * @param tipo1
     * @param referencia2
     * @param tipo2
     * @param inConvencao
     * @param inGrupoCidade
     * @param inCnaes
     * @param inCidadeBase
     * @param percentual
     * @return
     */
    public List find(Relatorios relatorios, Integer empresa, Integer contabilidade, Integer servico, Integer tipoServico, String referencia1, String tipo1, String referencia2, String tipo2, String inConvencao, String inGrupoCidade, String inCnaes, String inCidadeBase, Integer percentual) {
        try {
            String queryString;
            queryString = " -- RelatorioComparativoArrecadacaoDao->find()                                                                                   \n"
                    + "     SELECT p.ds_documento                      AS cnpj,                                                                             \n"
                    + "            P.ds_nome                           AS empresa,                                                                          \n"
                    + "            S.ds_descricao                      AS contribuicao,                                                                     \n"
                    + "            M1.ds_referencia                    AS referencia1,                                                                      \n"
                    + "            func_nulldouble(M1.nr_valor_baixa)  AS valor1,                                                                           \n"
                    + "            M2.ds_referencia                    AS referencia2,                                                                      \n"
                    + "            func_nulldouble(M2.nr_valor_baixa)  AS valor2,                                                                           \n"
                    + "      to_char(                                                                                                                       \n"
                    + "      (CASE WHEN func_nulldouble(M2.nr_valor_baixa) > 0 AND func_nulldouble(M1.nr_valor_baixa) = 0 THEN   100                        \n"
                    + "            WHEN func_nulldouble(M2.nr_valor_baixa) = 0 AND func_nulldouble(M1.nr_valor_baixa) > 0 THEN - 100                        \n"
                    + "            WHEN func_nulldouble(M2.nr_valor_baixa) = 0 AND func_nulldouble(M1.nr_valor_baixa) = 0 THEN     0                        \n"
                    + "            WHEN func_nulldouble(M2.nr_valor_baixa) > 0 AND func_nulldouble(M1.nr_valor_baixa) > 0 THEN                              \n"
                    + "                (func_nulldouble(M2.nr_valor_baixa) - func_nulldouble(M1.nr_valor_baixa)) / func_nulldouble(M1.nr_valor_baixa) * 100 \n"
                    + "       END), 'FM999999999.00') AS percentual                                                                                         \n"
                    + "       FROM pes_pessoa               AS P                                                                                            \n"
                    + " INNER JOIN arr_contribuintes_vw     AS C  ON C.id_pessoa  = P.id AND C.dt_inativacao IS NULL                                        \n"
                    + " INNER JOIN pes_juridica             AS J  ON J.id_pessoa  = P.id                                                                    \n"
                    + " INNER JOIN pes_pessoa_endereco      AS PE ON PE.id_pessoa = P.id AND PE.id_tipo_endereco = 2                                        \n"
                    + " INNER JOIN end_endereco             AS ENDE ON ENDE.id    = PE.id_endereco                                                          \n"
                    + "  LEFT JOIN fin_movimento            AS M1 ON                                                                                        \n"
                    + "            M1.id_pessoa             = P.id                                                                                          \n"
                    + "            AND M1.id_servicos       = " + servico + "                                                                               \n"
                    + "            AND M1.id_tipo_servico   = 1                                                                                             \n"
                    + "            AND M1.ds_referencia     = '" + referencia1 + "'                                                                         \n"
                    + "            AND M1.is_ativo          = true                                                                                          \n"
                    + "  LEFT JOIN fin_movimento            AS M2 ON                                                                                        \n"
                    + "            M2.id_pessoa             = P.id                                                                                          \n"
                    + "            AND M2.id_servicos       = " + servico + "                                                                               \n"
                    + "            AND M2.id_tipo_servico   = 1                                                                                             \n"
                    + "            AND M2.ds_referencia     = '" + referencia2 + "'                                                                         \n"
                    + "            AND M2.is_ativo          = true                                                                                          \n"
                    + "  LEFT JOIN fin_servicos             AS S  ON S.id         = " + servico + "                                                         \n"
                    + "      WHERE M1.is_ativo = true                                                                                                       \n"
                    + "            " + tipo(1, tipo1)
                    + "            " + tipo(2, tipo2)
                    + "            AND P.id > 0                                                                                                             \n";

            if (!inConvencao.isEmpty()) {
                queryString += " AND C.id_convencao IN(" + inConvencao + ") \n";
            }
            if (!inGrupoCidade.isEmpty()) {
                queryString += " AND C.id_grupo_cidade IN(" + inGrupoCidade + ") \n";
            }
            if (!inCnaes.isEmpty()) {
                queryString += " AND J.id_cnae IN(" + inCnaes + ") \n";
            }
            if (!inCidadeBase.isEmpty()) {
                queryString += " AND ENDE.id_cidade IN(" + inCidadeBase + ") \n";
            }
            if (contabilidade != null) {
                queryString += " AND C.id_contabilidade = " + contabilidade + " \n";
            }
            if (empresa != null) {
                queryString += " AND C.id_juridica = " + empresa + " \n";
            }
            if (percentual != null) {
                if (percentual > 0) {
                    queryString += " \n"
                            + " AND (                                                                                               \n"
                            + "         (                                                                                           \n"
                            + "             ( func_nulldouble(M2.nr_valor_baixa) > 0 AND func_nulldouble(M1.nr_valor_baixa) > 0     \n"
                            + "                 AND ( func_nulldouble(M2.nr_valor_baixa) - func_nulldouble(M1.nr_valor_baixa)       \n"
                            + "                 ) / func_nulldouble(M2.nr_valor_baixa) * 100 > " + percentual + "                   \n"
                            + "             )                                                                                       \n"
                            + "         ) OR func_nulldouble (                                                                      \n"
                            + "             M2.nr_valor_baixa                                                                       \n"
                            + "         ) > 0 AND func_nulldouble(M1.nr_valor_baixa) = 0                                            \n"
                            + " )                                                                                                   \n";
                } else if (percentual < 0) {
                    queryString += " \n"
                            + " AND (                                                                                               \n"
                            + "         (                                                                                           \n"
                            + "             ( func_nulldouble(M2.nr_valor_baixa) > 0 AND func_nulldouble(M1.nr_valor_baixa) > 0     \n"
                            + "                 AND ( func_nulldouble(M2.nr_valor_baixa) - func_nulldouble(M1.nr_valor_baixa)       \n"
                            + "                 ) / func_nulldouble(M2.nr_valor_baixa) * 100 < " + percentual + "                   \n"
                            + "             )                                                                                       \n"
                            + "         ) OR func_nulldouble(M2.nr_valor_baixa) = 0 AND func_nulldouble(M1.nr_valor_baixa) > 0      \n"
                            + " )                                                                                                   \n";
                } else if (percentual == 0) {
                    queryString += " AND func_nulldouble(M2.nr_valor_baixa) = 0 AND func_nulldouble(M1.nr_valor_baixa) = 0 \n";
                }
            }
            if (!relatorios.getQry().isEmpty()) {
                queryString += " " + relatorios.getQry();
            }
            if (!relatorios.getQryOrdem().isEmpty()) {
                queryString += " ORDER BY " + relatorios.getQry();
            } else {
                queryString += " ORDER BY P.ds_nome ASC ";
            }
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public String tipo(Integer tCase, String tipo) {
        switch (tipo) {
            case "baixado":
                return " AND M" + tCase + ".id_baixa IS NOT NULL \n";
            case "nao_baixado":
                return " AND M" + tCase + ".id_baixa IS NULL \n";
            default:
                return "";
        }
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}
