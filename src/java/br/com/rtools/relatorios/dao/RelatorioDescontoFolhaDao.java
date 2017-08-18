package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioDescontoFolhaDao extends DB {

    /**
     *
     * @param relatorio_id
     * @param empresa_id
     * @param titular_id
     * @param referenciaInicial
     * @param referenciaFinal
     * @return 0 Object socio_codigo, 1 Object empresa_nome, 2 Object
     * titular_nome, 3 Object movimento_referencia, 4 Object
     * movimento_vencimento, 5 Object movimento_valor, 6 Object
     * empresa_documento, 7 Object empresa_telefone1, 8 Object
     * e_endereco_logradouro, 9 Object e_endereco_descricao, 10 Object
     * e_endereco_numero, 11 Object e_endereco_complemento, 12 Object
     * e_endereco_bairro, 13 Object e_endereco_cidade, 14 Object e_endereco_uf,
     * 15 Object e_endereco_cep, 16 Object empresa_contato
     */
    public List find(Integer relatorio_id, Integer empresa_id, Integer titular_id, String referencia_atual) {
        // CHAMADO: #1066 Criada por: Rogério em 15/10/2015 16:28
        try {

            List listWhere = new ArrayList<>();

            //listWhere.add(" m.id_baixa is null ");
            listWhere.add(" m.is_ativo = true ");

            //if (referenciaInicial != null && referenciaFinal != null && !referenciaInicial.isEmpty() && !referenciaFinal.isEmpty()) {
            //    WHERE_REFERENCIA = " m.ds_referencia BETWEEN '" + referenciaInicial + "' AND '" + referenciaFinal + "'";
            //} else if (referenciaInicial != null && (referenciaFinal == null || referenciaFinal.isEmpty()) && !referenciaInicial.isEmpty()) {
            listWhere.add(" m.ds_referencia = '" + referencia_atual + "' ");
            //} else if (referenciaInicial == null && referenciaFinal != null && !referenciaFinal.isEmpty()) {
            //    WHERE_REFERENCIA = " m.ds_referencia <= '" + referenciaFinal + "' ";
            //}

            if (empresa_id != null) {
                listWhere.add(" j.id = " + empresa_id);
            }
            if (titular_id != null) {
                listWhere.add(" t.id = " + titular_id);
            }

            String SELECT_STRING = "", WHERE_STRING = "";

            DataHoje dh = new DataHoje();
            String referencia_anterior = DataHoje.converteDataParaReferencia(dh.decrementarMeses(1, "01/" + referencia_atual));

            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    WHERE_STRING += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    WHERE_STRING += " AND " + listWhere.get(i).toString() + " \n";
                }
            }

            if (relatorio_id.equals(62) || relatorio_id.equals(77)) {
                SELECT_STRING += " -- RelatorioDescontoFolhaDao->find(62)           \n"
                        + "(     SELECT t.id              AS socio_codigo,          \n" // 00 - SÓCIO -> CÓDIGO
                        + "            pj.ds_nome        AS empresa_nome,           \n" // 01 - EMPRESA -> NOME
                        + "            t.ds_nome         AS titular_nome,           \n" // 02 - TITULAR -> NOME
                        + "            m.ds_referencia   AS movimento_referencia,   \n" // 03 - MOVIMENTO -> REFERÊNCIA
                        + "            (m.dt_vencimento - cast(extract(DAY FROM m.dt_vencimento) AS int) + pc.nr_dia_vencimento) AS movimento_vencimento, \n" // 04 - MOVIMENTO -> VENCIMENTO 
                        + "            sum(m.nr_valor)   AS movimento_valor,        \n" // 05 - MOVIMENTO -> VALOR
                        + "            pj.ds_documento   AS empresa_documento,      \n" // 06 - EMPRESA -> DOCUMENTO
                        + "            pj.ds_telefone1   AS empresa_telefone1,      \n" // 07 - EMPRESA -> TELEFONE1
                        + "            e.logradouro      AS e_endereco_logradouro,  \n" // 08 - ENDEREÇO DA EMPRESA -> LOGRADOURO
                        + "            e.endereco        AS e_endereco_descricao,   \n" // 09 - ENDEREÇO DA EMPRESA -> DESCRIÇÃO 
                        + "            pe.ds_numero      AS e_endereco_numero,      \n" // 10 - ENDEREÇO DA EMPRESA -> NÚMERO
                        + "            pe.ds_complemento AS e_endereco_complemento, \n" // 11 - ENDEREÇO DA EMPRESA -> COMPLEMENTO 
                        + "            e.bairro          AS e_endereco_bairro,      \n" // 12 - ENDEREÇO DA EMPRESA -> BAIRRO 
                        + "            e.cidade          AS e_endereco_cidade,      \n" // 13 - ENDEREÇO DA EMPRESA -> CIDADE 
                        + "            e.uf              AS e_endereco_uf,          \n" // 14 - ENDEREÇO DA EMPRESA -> UF 
                        + "            e.cep             AS e_endereco_cep,         \n" // 15 - ENDEREÇO DA EMPRESA -> CEP 
                        + "            j.ds_contato      AS empresa_contato,        \n" // 16 - EMPRESA -> CONTATO 
                        + "            1                 AS grupo,                  \n" // 17 - GRUPO
                        + "            j.id              AS empresa_id,             \n" // 18 - EMPRESA ID
                        + "            ba.dt_baixa       AS dt_baixa,               \n" // 20 - DATA DA BAIXA
                        + "            sum(m.nr_valor_baixa)  AS valor_baixa,       \n" // 21 VALOR DA BAIXA
                        + "            ''                AS nome_socio              \n" // 22 NOME DO SÓCIO
                        + "       FROM fin_movimento     AS m                       \n"
                        + " INNER JOIN pes_fisica AS F ON f.id_pessoa = m.id_titular \n"                         
                        + "  LEFT JOIN fin_baixa         AS ba ON ba.id = m.id_baixa \n"
                        + " INNER JOIN pes_pessoa        AS t  ON t.id         = m.id_titular                   \n"
                        + " INNER JOIN pes_juridica      AS j  ON j.id_pessoa  = m.id_pessoa                    \n"
                        + " INNER JOIN pes_pessoa_complemento AS pc ON pc.id_pessoa = j.id_pessoa               \n"
                        + " INNER JOIN pes_pessoa        AS pj ON pj.id        = j.id_pessoa                    \n"
                        + " INNER JOIN pes_pessoa_endereco AS pe ON pe.id_pessoa = pj.id AND id_tipo_endereco = 3 \n"
                        + " INNER JOIN endereco_vw       AS e  ON e.id         = pe.id_endereco                 \n"
                        + WHERE_STRING
                        + " GROUP BY t.id,              \n"
                        + "          pj.ds_nome,        \n"
                        + "          t.ds_nome,         \n"
                        + "          m.ds_referencia,   \n"
                        + "          m.dt_vencimento - cast(extract(DAY FROM m.dt_vencimento) AS int) + pc.nr_dia_vencimento,   \n"
                        + "          pj.ds_documento,   \n"
                        + "          pj.ds_telefone1,   \n"
                        + "          e.logradouro,      \n"
                        + "          e.endereco,        \n"
                        + "          pe.ds_numero,      \n"
                        + "          pe.ds_complemento, \n"
                        + "          e.bairro,          \n"
                        + "          e.cidade,          \n"
                        + "          e.uf,              \n"
                        + "          e.cep,             \n"
                        + "          j.ds_contato,      \n"
                        + "          m.dt_vencimento - CAST(EXTRACT(day FROM m.dt_vencimento) AS int) + pc.nr_dia_vencimento, \n"
                        + "          j.id, \n"
                        + "          ba.dt_baixa \n"
                        + ") \n";

                SELECT_STRING
                        += "-- NOVOS \n"
                        + "UNION \n"
                        + "("
                        + "SELECT  \n"
                        + "        t.id              AS socio_codigo,       --- 1 \n"
                        + "        pj.ds_nome        AS empresa_nome,       --- 2 -- 1 \n"
                        + "        t.ds_nome         AS titular_nome,       --- 3 -- 4 \n"
                        + "        '' AS movimento_referencia,              --- 4 \n"
                        + "        '01/01/1900' AS movimento_vencimento,    --- 5 \n"
                        + "        0  AS movimento_valor,                   --- 6 \n"
                        + "        pj.ds_documento   AS empresa_documento,  --- 7 -- 2 \n"
                        + "        '' AS empresa_telefone1,                 --- 8 \n"
                        + "        '' AS e_endereco_logradouro,             --- 9 \n"
                        + "        '' AS e_endereco_descricao,              --- 10 \n"
                        + "        '' AS e_endereco_numero,                 --- 11 \n"
                        + "        '' AS e_endereco_complemento,            --- 12 \n"
                        + "        '' AS e_endereco_bairro,                 --- 13 \n"
                        + "        '' AS e_endereco_cidade,                 --- 14 \n"
                        + "        '' AS e_endereco_uf,                     --- 15 \n"
                        + "        '' AS e_endereco_cep,                    --- 16 \n"
                        + "        '' AS empresa_contato,                   --- 17 \n"
                        + "        2 AS grupo,                              --- 18 \n"
                        + "        j.id AS empresa_id,                      --- 19 \n"
                        + "        null AS dt_baixa,                        --- 20 \n"
                        + "        0 AS valor_baixa,                        --- 21 \n"
                        + "        be.ds_nome AS nome_socio                 --- 22 \n"
                        + "  FROM fin_movimento          AS m                       \n"
                        + " INNER JOIN pes_fisica as f on f.id_pessoa = m.id_titular \n"                        
                        + " INNER JOIN pes_pessoa        AS t  ON t.id         = m.id_titular                   \n"
                        + " INNER JOIN pes_pessoa        AS be  ON be.id        = m.id_beneficiario             \n"
                        + " INNER JOIN pes_juridica      AS j  ON j.id_pessoa  = m.id_pessoa                    \n"
                        + " INNER JOIN pes_pessoa_complemento AS pc ON pc.id_pessoa = j.id_pessoa \n"
                        + " INNER JOIN pes_pessoa             AS pj ON pj.id        = j.id_pessoa \n"
                        + " INNER JOIN pes_pessoa_endereco    AS pe ON pe.id_pessoa = pj.id AND id_tipo_endereco = 3 \n"
                        + " INNER JOIN endereco_vw            AS e  ON e.id         = pe.id_endereco \n"
                        + "  LEFT JOIN \n"
                        + "( \n "
                        + "     SELECT m.id_pessoa, \n"
                        + "            id_titular \n"
                        + "       FROM fin_movimento AS m\n"
                        + "      INNER JOIN pes_juridica AS j ON j.id_pessoa = m.id_pessoa \n"
                        + "      WHERE is_ativo = true \n"
                        + "        AND ds_referencia = '" + referencia_anterior + "' --- REF.ANTERIOR A INFORMADA \n"
                        + "      GROUP BY m.id_pessoa, id_titular \n "
                        + ") as x ON x.id_pessoa = m.id_pessoa AND x.id_titular = m.id_titular \n"
                        + "\n"
                        + WHERE_STRING
                        + " AND x.id_pessoa IS NULL \n"
                        + (empresa_id != null ? "        AND j.id = " + empresa_id : "") + " \n"
                        + (titular_id != null ? "        AND t.id = " + titular_id : "") + " \n"
                        + "\n"
                        + "     GROUP BY \n"
                        + "        t.id, \n"
                        + "        pj.ds_nome, \n"
                        + "        t.ds_nome, \n"
                        + "        pj.ds_documento, \n"
                        + "        j.id, \n"
                        + "        be.ds_nome, \n"
                        + "        be.id \n"
                        + "\n"
                        + ") ";

                SELECT_STRING
                        += "-- INATIVOS \n"
                        + "UNION \n"
                        + "("
                        + "SELECT   \n"
                        + "        t.id              AS socio_codigo, \n"
                        + "        pj.ds_nome        AS empresa_nome, \n"
                        + "        t.ds_nome         AS titular_nome, \n"
                        + "        '' as movimento_referencia, \n"
                        + "        '01/01/1900' AS movimento_vencimento, \n"
                        + "        0  AS movimento_valor, \n"
                        + "        pj.ds_documento AS empresa_documento, \n"
                        + "        '' AS empresa_telefone1, \n"
                        + "        '' AS e_endereco_logradouro, \n"
                        + "        '' AS e_endereco_descricao, \n"
                        + "        '' AS e_endereco_numero, \n"
                        + "        '' AS e_endereco_complemento, \n"
                        + "        '' AS e_endereco_bairro, \n"
                        + "        '' AS e_endereco_cidade, \n"
                        + "        '' AS e_endereco_uf, \n"
                        + "        '' AS e_endereco_cep, \n"
                        + "        '' AS empresa_contato, \n"
                        + "        3 as grupo, \n"
                        + "        j.id AS empresa_id, \n"
                        + "        null AS dt_baixa, \n"
                        + "        0 AS valor_baixa,  \n"
                        + "        be.ds_nome AS nome_socio  \n"
                        + "       FROM fin_movimento     AS m \n"
                        + " INNER JOIN pes_fisica as f on f.id_pessoa = m.id_titular \n"                        
                        + " INNER JOIN pes_pessoa   AS t ON t.id  = m.id_titular \n"
                        + " INNER JOIN pes_pessoa   AS be ON be.id = m.id_beneficiario \n"
                        + " INNER JOIN pes_pessoa   AS pj ON pj.id = m.id_pessoa \n"
                        + " INNER JOIN pes_juridica AS j ON j.id_pessoa = m.id_pessoa \n"
                        + " INNER JOIN pes_fisica AS f ON f.id_pessoa=t.id \n"
                        + "  LEFT JOIN \n"
                        + "( \n "
                        + "     SELECT m.id_pessoa, \n"
                        + "            id_titular \n"
                        + "       FROM fin_movimento AS m \n"
                        + "      INNER JOIN pes_juridica AS j ON j.id_pessoa = m.id_pessoa \n"
                        + "      WHERE is_ativo = true \n"
                        + "        AND ds_referencia = '" + referencia_atual + "' --- REF.ANTERIOR A INFORMADA \n"
                        + "      GROUP BY m.id_pessoa, id_titular \n "
                        + ") as x ON x.id_pessoa = m.id_pessoa AND x.id_titular = m.id_titular \n"
                        + "WHERE x.id_titular IS NULL \n"
                        + "  AND m.is_ativo = true "
                        + "  AND m.ds_referencia = '" + referencia_anterior + "'  --- REF.ANTERIOR A INFORMADA \n"
                        + (empresa_id != null ? "        AND j.id = " + empresa_id : "") + " \n"
                        + (titular_id != null ? "        AND t.id = " + titular_id : "") + " \n"
                        + "  AND m.id_servicos NOT IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4) \n"
                        + "  AND x.id_pessoa IS NULL \n"
                        + "GROUP BY \n" 
                        + "        t.id,            \n"
                        + "        pj.ds_nome,      \n"
                        + "        t.ds_nome,       \n"
                        + "        pj.ds_documento, \n"
                        + "        j.id, \n"
                        + "        be.ds_nome, \n"
                        + "        be.id \n"
                        + ") ";
                SELECT_STRING += " ORDER BY 2, 7, 18, 3";
            } else if (relatorio_id.equals(63)) {
                // # CHAMADO 1074 Criada por: Rogério em 19/10/2015 09:52
                SELECT_STRING += " -- RelatorioDescontoFolhaDao->find(63)             \n"
                        + "     SELECT t.id              AS socio_codigo,           \n" // 0
                        + "            pj.ds_nome        AS empresa_nome,           \n" // 1
                        + "            t.ds_nome         AS titular_nome,           \n" // 2
                        + "            m.ds_referencia   AS movimento_referencia,   \n" // 3
                        + "            m.dt_vencimento-cast(extract(day from m.dt_vencimento) as int)+pc.nr_dia_vencimento   AS movimento_vencimento,  \n" // 4
                        + "            m.nr_valor   AS valor,                       \n" // 5
                        + "            pj.ds_documento   AS empresa_documento,      \n" // 6
                        + "            pj.ds_telefone1   AS empresa_telefone1,      \n" // 7
                        + "            e.logradouro      AS e_endereco_logradouro,  \n" // 8
                        + "            e.endereco        AS e_endereco_descricao,   \n" // 9
                        + "            pe.ds_numero      AS e_endereco_numero,      \n" // 10
                        + "            pe.ds_complemento AS e_endereco_complemento, \n" // 11
                        + "            e.bairro          AS e_endereco_bairro,      \n" // 12
                        + "            e.cidade          AS e_endereco_cidade,      \n" // 13
                        + "            e.uf              AS e_endereco_uf,          \n" // 14
                        + "            e.cep             AS e_endereco_cep,         \n" // 15
                        + "            j.ds_contato      AS empresa_contato,        \n" // 16
                        + "            so.categoria      AS categoria_descricao,    \n" // 17
                        + "            so.matricula      AS matricula,              \n" // 18
                        + "            se.ds_descricao   AS servico_descricao,      \n" // 19
                        + "            b.ds_nome         AS beneficiario_nome,      \n" // 20 
                        + "            right('0'||text(extract(month FROM m.dt_vencimento)),2) AS mes, \n" // 21
                        + "            text(extract(year FROM m.dt_vencimento))                AS ano  \n" // 22
                        + "       FROM fin_movimento     AS m                                          \n"
                        + " INNER JOIN pes_fisica as f on f.id_pessoa = m.id_titular                   \n"                        
                        + "  LEFT JOIN soc_socios_vw     AS so  ON m.id_titular = so.codsocio          \n"
                        + " INNER JOIN fin_servicos      AS se on se.id = m.id_servicos                \n"
                        + " INNER JOIN pes_pessoa        AS t  ON t.id  = m.id_titular                 \n"
                        + " INNER JOIN pes_pessoa        AS b  ON b.id  = m.id_beneficiario            \n"
                        + " INNER JOIN pes_juridica      AS j  ON j.id_pessoa  = m.id_pessoa           \n"
                        + " INNER JOIN pes_pessoa_complemento AS pc ON pc.id_pessoa = j.id_pessoa      \n"
                        + " INNER JOIN pes_pessoa        AS pj ON pj.id        = j.id_pessoa           \n"
                        + " INNER JOIN pes_pessoa_endereco AS pe ON pe.id_pessoa = pj.id AND id_tipo_endereco = 3 \n"
                        + " INNER JOIN endereco_vw       AS e  ON e.id         = pe.id_endereco "
                        + WHERE_STRING;

                SELECT_STRING += " ORDER BY pj.ds_nome,    \n"
                        + "               pj.ds_documento, \n"
                        + "               t.ds_nome        \n";
            }

            Query query = getEntityManager().createNativeQuery(SELECT_STRING);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
