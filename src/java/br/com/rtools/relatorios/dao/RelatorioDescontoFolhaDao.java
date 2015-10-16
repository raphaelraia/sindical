package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioDescontoFolhaDao extends DB {

    /**
     *
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
    public List find(Integer empresa_id, Integer titular_id, String referenciaInicial, String referenciaFinal) {
        // CHAMADO: #1066 Criada por: Rogério em 15/10/2015 16:28
        try {
            String queryString = "";
            queryString += " -- RelatorioDescontoFolhaDao->find()               \n"
                    + "     SELECT t.id              AS socio_codigo,           \n" // 00 - SÓCIO -> CÓDIGO
                    + "            pj.ds_nome        AS empresa_nome,           \n" // 01 - EMPRESA -> NOME
                    + "            t.ds_nome         AS titular_nome,           \n" // 02 - TITULAR -> NOME
                    + "            m.ds_referencia   AS movimento_referencia,   \n" // 03 - MOVIMENTO -> REFERÊNCIA
                    + "            (m.dt_vencimento - cast(extract(DAY FROM m.dt_vencimento) AS int) + pc.nr_dia_vencimento) AS movimento_vencimento,   \n" // 04 - MOVIMENTO -> VENCIMENTO 
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
                    + "            j.ds_contato      AS empresa_contato         \n" // 16 - EMPRESA -> CONTATO 
                    + "       FROM soc_socios_vw     AS so                      \n"
                    + " INNER JOIN fin_movimento     AS m  ON m.id_titular = so.titular                     \n"
                    + " INNER JOIN pes_pessoa        AS t  ON t.id         = so.titular                     \n"
                    + " INNER JOIN pes_juridica      AS j  ON j.id_pessoa  = m.id_pessoa                    \n"
                    + " INNER JOIN pes_pessoa_complemento AS pc ON pc.id_pessoa = j.id_pessoa               \n"
                    + " INNER JOIN pes_pessoa        AS pj ON pj.id        = j.id_pessoa                    \n"
                    + " INNER JOIN pes_pessoa_endereco AS pe ON pe.id_pessoa = pj.id AND id_tipo_endereco = 3  \n"
                    + " INNER JOIN endereco_vw       AS e  ON e.id         = pe.id_endereco                 \n";

            List listWhere = new ArrayList<>();
            listWhere.add(" m.id_baixa is null ");
            if (referenciaInicial != null && referenciaFinal != null && !referenciaInicial.isEmpty() && !referenciaFinal.isEmpty()) {
                listWhere.add(" m.ds_referencia BETWEEN '" + referenciaInicial + "' AND '" + referenciaFinal + "'");
            } else if (referenciaInicial != null && (referenciaFinal == null || referenciaFinal.isEmpty()) && !referenciaInicial.isEmpty()) {
                listWhere.add(" m.ds_referencia = '" + referenciaInicial + "' ");
            } else if (referenciaInicial == null && referenciaFinal != null && !referenciaFinal.isEmpty()) {
                listWhere.add(" m.ds_referencia <= '" + referenciaFinal + "' ");
            }
            if (empresa_id != null) {
                listWhere.add(" j.id = " + empresa_id);
            }
            if (titular_id != null) {
                listWhere.add(" t.id = " + titular_id);
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += "    "
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
                    + "          m.dt_vencimento-cast(extract(day from m.dt_vencimento) as int)+pc.nr_dia_vencimento \n";
            queryString += " ORDER BY pj.ds_nome,       \n"
                    + "               pj.ds_documento,  \n"
                    + "               t.ds_nome         \n";

            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
