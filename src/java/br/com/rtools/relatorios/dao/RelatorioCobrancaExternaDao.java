package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCobrancaExternaDao extends DB {

    /**
     *
     * @param relatorio_id
     * @param inTipoCobranca
     * @param titular_id
     * @return [0] tipo_cobranca_descricao; [1] socio_codigo; [2] socio_nome;
     * [3] mes; [4] ano; [5] valor; [6] cobranca;
     */
    public List find(Integer relatorio_id, String inTipoCobranca, Integer titular_id) {
        // CHAMADOS 1057
        try {
            String queryString = "";
            if (relatorio_id == 56) {
                queryString += " -- RelatorioCobrancaExternaDao->find()             \n"
                        + "      SELECT so.tipo_cobranca AS tipo_cobranca_descricao,\n"
                        + "             m.id_titular     AS socio_codigo,           \n"
                        + "             t.nome           AS socio_nome,             \n"
                        + "             0                AS mes,                    \n"
                        + "             0                AS ano,                    \n"
                        + "             sum(m.valor)     AS valor,                  \n"
                        + "             case                                        \n"
                        + "             WHEN e_endereco IS NULL THEN logradouro||' '||endereco||', '||numero||' '||complemento||' '||bairro||' '||cidade||uf||' CEP: '||left(cep,5)||'-'||right(cep,3) \n"
                        + "              ELSE e_logradouro||' '||e_endereco||', '||e_numero||' '||e_complemento||' '||e_bairro||' '||e_cidade||e_uf||' CEP: '||left(e_cep,5)||'-'||right(e_cep,3)      \n"
                        + "             END AS endereco                             \n"
                        + "        FROM movimentos_vw       AS m                    \n"
                        + "  INNER JOIN fin_tipo_documento  AS td ON td.id = m.id_tipo_documento  \n"
                        + "  INNER JOIN pes_pessoa_vw       AS t  ON t.codigo = m.id_titular      \n"
                        + "  INNER JOIN soc_socios_vw       AS so ON so.codsocio = m.id_titular   \n";
                List listWhere = new ArrayList<>();
                listWhere.add("m.id_tipo_documento <> 13");
                listWhere.add("m.es = 'E'");
                listWhere.add("m.id_baixa IS NULL");
                if (inTipoCobranca != null && !inTipoCobranca.isEmpty()) {
                    listWhere.add("td.id IN (" + inTipoCobranca + ")");
                }
                for (int i = 0; i < listWhere.size(); i++) {
                    if (i == 0) {
                        queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                    } else {
                        queryString += " AND " + listWhere.get(i).toString() + " \n";
                    }
                }
                queryString += "    "
                        + " GROUP BY 1,2,3,4,5,7        \n"
                        + " ORDER BY so.tipo_cobranca,  \n"
                        + "          t.nome,            \n"
                        + "          m.id_titular       \n";
            } else if (relatorio_id == 57) {
                queryString += "      "
                        + "      SELECT so.tipo_cobranca AS tipo_cobranca_descricao,\n"
                        + "             m.id_titular    AS socio_codigo,            \n"
                        + "             t.nome          AS socio_nome,              \n"
                        + "             extract(month FROM m.vencimento) AS mes,    \n"
                        + "             extract(year  FROM m.vencimento) AS ano,    \n"
                        + "             sum(m.valor)    AS valor,                   \n"
                        + "             case                                        \n"
                        + "             WHEN e_endereco IS NULL THEN logradouro||' '||endereco||', '||numero||' '||complemento||' '||bairro||' '||cidade||uf||' CEP: '||left(cep,5)||'-'||right(cep,3) \n"
                        + "              ELSE e_logradouro||' '||e_endereco||', '||e_numero||' '||e_complemento||' '||e_bairro||' '||e_cidade||e_uf||' CEP: '||left(e_cep,5)||'-'||right(e_cep,3)      \n"
                        + "             END AS endereco                             \n"
                        + "        FROM movimentos_vw       AS m                    \n"
                        + "  INNER JOIN fin_tipo_documento  AS td ON td.id = m.id_tipo_documento  \n"
                        + "  INNER JOIN pes_pessoa_vw       AS t  ON t.codigo = m.id_titular      \n"
                        + "  INNER JOIN soc_socios_vw       AS so ON so.codsocio = m.id_titular   \n";
                List listWhere = new ArrayList<>();
                // 08/10/2015 - CHAMADO 1057
                // listWhere.add("m.id_tipo_documento <> 13");
                listWhere.add("m.es = 'E'");
                listWhere.add("m.id_baixa IS NULL");
                if (inTipoCobranca != null && !inTipoCobranca.isEmpty()) {
                    // 08/10/2015 - CHAMADO 1057 - listWhere.add("td.id IN (" + inTipoCobranca + ")");
                    listWhere.add("so.tipo_cobranca IN (" + inTipoCobranca + ")");
                }
                for (int i = 0; i < listWhere.size(); i++) {
                    if (i == 0) {
                        queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                    } else {
                        queryString += " AND " + listWhere.get(i).toString() + " \n";
                    }
                }
                queryString += "    "
                        + " GROUP BY 1,2,3,4,5,7                        \n"
                        + " ORDER BY so.tipo_cobranca,                  \n"
                        + "          t.nome,                            \n"
                        + "          m.id_titular,                      \n"
                        + "          extract(YEAR  FROM m.vencimento),  \n"
                        + "          extract(MONTH FROM m.vencimento)   \n";

            } else {
                queryString = ""
                        + "      SELECT t.empresa,                                  \n"
                        + "             t.cnpj,                                     \n"
                        + "             categoria,                                  \n"
                        + "             matricula,                                  \n"
                        + "             so.tipo_cobranca AS tipo_cobranca_descricao,\n"
                        + "             m.id_titular     AS socio_codigo,           \n"
                        + "             t.nome           AS socio_nome,             \n"
                        + "             extract(month FROM m.vencimento) AS mes,    \n"
                        + "             extract(year  FROM m.vencimento) AS ano,    \n"
                        + "             m.servico,                                  \n"
                        + "             m.valor,                                    \n"
                        + "             b.ds_nome as beneficiario                   \n"
                        + "        FROM movimentos_vw       AS m                   \n"
                        + "  INNER JOIN fin_tipo_documento  AS td ON td.id = m.id_tipo_documento\n"
                        + "  INNER JOIN pes_pessoa_vw       AS t  ON t.codigo = m.id_titular    \n"
                        + "  INNER JOIN soc_socios_vw       AS so ON so.codsocio = m.id_titular \n"
                        + "  INNER JOIN pes_pessoa          AS b  ON b.id = m.id_beneficiario   \n";

                List listWhere = new ArrayList<>();
                // 08/10/2015 - CHAMADO 1057
                // listWhere.add("m.id_tipo_documento <> 13");
                listWhere.add("m.es = 'E'");
                listWhere.add("m.id_baixa IS NULL");
                if (titular_id != null) {
                    listWhere.add("so.codsocio = " + titular_id + " ");
                }
                if (inTipoCobranca != null && !inTipoCobranca.isEmpty()) {
                    // 08/10/2015 - CHAMADO 1057 - listWhere.add("td.id IN (" + inTipoCobranca + ")");
                    listWhere.add("so.tipo_cobranca IN (" + inTipoCobranca + ")");
                }
                for (int i = 0; i < listWhere.size(); i++) {
                    if (i == 0) {
                        queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                    } else {
                        queryString += " AND " + listWhere.get(i).toString() + " \n";
                    }
                }
                queryString += " ORDER BY so.tipo_cobranca,  \n"
                        + "               t.nome,           \n"
                        + "               m.id_titular      \n";
            }
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
