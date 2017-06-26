/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MovimentoReceberDao extends DB {

    /**
     * Id da pessoa (pes_juridica.id_pessoa)
     *
     * @param pessoa_id
     * @return
     */
    public List pesquisaListaMovimentos(Integer pessoa_id) {
        try {
            String textoQuery = "-- MovimentosReceberDao->pesquisaListaMovimentos(Integer pessoa_id:" + pessoa_id + ") \n\n"
                    + ""
                    + "     SELECT m.ds_documento Boleto,                           \n" // 0
                    + "            se.ds_descricao          AS Servico,             \n" // 1
                    + "            tp.ds_descricao          AS Tipo,                \n" // 2
                    + "            m.ds_referencia          AS Referencia,          \n" // 3
                    + "            m.dt_vencimento          AS Vencimento,          \n" // 4
                    + "            func_valor_folha(m.id)   AS Valor_Mov,           \n" // 5
                    + "            f.nr_valor               AS Valor_Folha,         \n" // 6
                    + "            func_multa(m.id)         AS Multa,               \n" // 7
                    + "            func_juros(m.id)         AS Juros,               \n" // 8
                    + "            func_correcao(m.id)      AS Correcao,            \n" // 9
                    + "            null                     AS Desconto,            \n" // 10
                    + "            func_valor_folha(m.id) + func_multa(m.id) + func_juros(m.id) + func_correcao(m.id)   AS Valor_calculado, \n" // 11
                    + "            func_intervalo_meses(CURRENT_DATE,dt_vencimento)                                     AS Meses_em_Atraso, \n" // 12
                    + "            CURRENT_DATE-dt_vencimento                                                           AS Dias_em_atraso,  \n" // 13
                    + "            i.ds_descricao indice,                                                                                   \n" // 14
                    + "            m.id                                                                                 AS id               \n" // 15
                    + "       FROM fin_movimento                    AS m                                \n"
                    + " INNER JOIN fin_servicos                     AS se ON se.id = m.id_servicos      \n"
                    + " INNER JOIN fin_tipo_servico                 AS tp ON tp.id = m.id_tipo_servico  \n"
                    + " INNER JOIN pes_juridica                     AS j ON j.id_pessoa = m.id_pessoa   \n"
                    + "  LEFT JOIN arr_faturamento_folha_empresa    AS f ON f.id_juridica = j.id AND f.ds_referencia = m.ds_referencia AND f.id_tipo_servico = m.id_tipo_servico \n"
                    + "  LEFT JOIN fin_correcao                     AS cr ON cr.id_servicos = m.id_servicos AND                                                                  "
                    + "                                                 (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) AND "
                    + "                                                 (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2))         "
                    + "  LEFT JOIN fin_indice                       AS i ON i.id = cr.id_indice \n"
                    + "      WHERE m.id_pessoa = " + pessoa_id + " \n"
                    + "        AND m.is_ativo = TRUE \n"
                    + "        AND m.id_baixa IS NULL \n"
                    + "        AND m.id_servicos IN (SELECT sr.id_servicos FROM fin_servico_rotina sr WHERE sr.id_rotina = 4) \n"
                    + "   ORDER BY m.dt_vencimento \n";

            Query qry = getEntityManager().createNativeQuery(textoQuery);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List pesquisaListaMovimentosDesconto(int id_juridica, double desconto, double total) {
        try {
            String textoQuery = "select m.ds_documento Boleto, "
                    + "       se.ds_descricao as Servico, "
                    + "       tp.ds_descricao as Tipo, "
                    + "       m.ds_referencia as Referencia,"
                    + "       m.dt_vencimento as Vencimento, "
                    + "       func_valor_folha(m.id) as Valor_Mov,"
                    + "       f.nr_valor as Valor_Folha,"
                    + "       func_multa(m.id) as Multa,"
                    + "       func_juros(m.id) as Juros,"
                    + "       func_correcao(m.id) as Correcao,"
                    + "       func_desconto(m.id, " + desconto + ", " + total + ") as Desconto,"
                    + "       func_valor_folha(m.id) + func_multa(m.id) + func_juros(m.id) + func_correcao(m.id) as Valor_calculado,"
                    + "       func_intervalo_meses(CURRENT_DATE,dt_vencimento) as Meses_em_Atraso,"
                    + "       CURRENT_DATE-dt_vencimento as Dias_em_atraso,"
                    + "       i.ds_descricao indice,"
                    + "       m.id as id"
                    + "  from fin_movimento as m"
                    + " inner join fin_servicos as se on se.id=m.id_servicos"
                    + " inner join fin_tipo_servico as tp on tp.id=m.id_tipo_servico"
                    + " inner join pes_juridica as j on j.id_pessoa=m.id_pessoa"
                    + "  left join arr_faturamento_folha_empresa as f on f.id_juridica=j.id and f.ds_referencia=m.ds_referencia and f.id_tipo_servico=m.id_tipo_servico"
                    + "  left join fin_correcao as cr on cr.id_servicos=m.id_servicos and "
                    + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and "
                    + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2))"
                    + "  left join fin_indice as i on i.id=cr.id_indice"
                    + " where m.id_pessoa = " + id_juridica
                    + "   and m.is_ativo is true "
                    + "   and m.id_baixa is null "
                    + " order by m.dt_vencimento";

            Query qry = getEntityManager().createNativeQuery(textoQuery);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
