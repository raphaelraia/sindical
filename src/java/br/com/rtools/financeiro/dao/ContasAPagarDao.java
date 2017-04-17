/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.beans.ContasAPagarBean;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class ContasAPagarDao extends DB {

    public List<Object> listaContasAPagar(ContasAPagarBean.Filtros f) {
        List<String> list_where = new ArrayList();

        list_where.add("m.ds_es = 'S'");

        String WHERE = "";

        switch (f.getCondicao()) {
            case "em_aberto":
                list_where.add("m.id_baixa IS NULL");
                break;
            case "pagos":
                list_where.add("m.id_baixa IS NOT NULL");
                break;
            default:
                break;
        }

        if (!f.getVencimento().isEmpty() && !f.getVencimentoFinal().isEmpty()) {
            list_where.add("m.dt_vencimento >= '" + f.getVencimento() + "'");
            list_where.add("m.dt_vencimento <= '" + f.getVencimentoFinal() + "'");
        } else if (!f.getVencimento().isEmpty() && f.getVencimentoFinal().isEmpty()) {
            list_where.add("m.dt_vencimento >= '" + f.getVencimento() + "'");
        } else if (f.getVencimento().isEmpty() && !f.getVencimentoFinal().isEmpty()) {
            list_where.add("m.dt_vencimento <= '" + f.getVencimentoFinal() + "'");
        }

        if (!f.getPagamento().isEmpty() && !f.getPagamentoFinal().isEmpty()) {
            list_where.add("b.dt_baixa >= '" + f.getPagamento() + "'");
            list_where.add("b.dt_baixa <= '" + f.getPagamentoFinal() + "'");
        } else if (!f.getPagamento().isEmpty() && f.getPagamentoFinal().isEmpty()) {
            list_where.add("b.dt_baixa >= '" + f.getPagamento() + "'");
        } else if (f.getPagamento().isEmpty() && !f.getPagamentoFinal().isEmpty()) {
            list_where.add("b.dt_baixa <= '" + f.getPagamentoFinal() + "'");
        }

        if (!f.getLancamento().isEmpty() && !f.getLancamentoFinal().isEmpty()) {
            list_where.add("l.dt_lancamento >= '" + f.getLancamento() + "'");
            list_where.add("l.dt_lancamento <= '" + f.getLancamentoFinal() + "'");
        } else if (!f.getLancamento().isEmpty() && f.getLancamentoFinal().isEmpty()) {
            list_where.add("l.dt_lancamento >= '" + f.getLancamento() + "'");
        } else if (f.getLancamento().isEmpty() && !f.getLancamentoFinal().isEmpty()) {
            list_where.add("l.dt_lancamento <= '" + f.getLancamentoFinal() + "'");
        }

        if (!f.getEmissao().isEmpty() && !f.getEmissaoFinal().isEmpty()) {
            list_where.add("l.dt_emissao >= '" + f.getEmissao() + "'");
            list_where.add("l.dt_emissao <= '" + f.getEmissaoFinal() + "'");
        } else if (!f.getEmissao().isEmpty() && f.getEmissaoFinal().isEmpty()) {
            list_where.add("l.dt_emissao >= '" + f.getEmissao() + "'");
        } else if (f.getEmissao().isEmpty() && !f.getEmissaoFinal().isEmpty()) {
            list_where.add("l.dt_emissao <= '" + f.getEmissaoFinal() + "'");
        }

        if (!f.getNumeroBaixa().isEmpty()){
            list_where.add("m.id_baixa = " + f.getNumeroBaixa());
        }
        
        for (String w : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + w + " \n ";
            } else {
                WHERE += " AND " + w + " \n ";
            }
        }

        String ORDER_BY = " ORDER BY ";

        switch (f.getOrdem()) {
            case "vencimento":
                ORDER_BY += " m.dt_vencimento, p.ds_nome ";
                break;
            case "pagamento":
                ORDER_BY += " b.dt_baixa, p.ds_nome ";
                break;
            case "nome_vencimento":
                ORDER_BY += " p.ds_nome, m.dt_vencimento ";
                break;
            case "nome_pagamento":
                ORDER_BY += " p.ds_nome, b.dt_baixa ";
                break;
        }
        String text
                = "SELECT p.ds_nome AS nome,\n "
                + "	m.dt_vencimento AS vencimento, \n "
                + "	m.ds_referencia AS referencia, \n "
                + "	m.nr_valor AS valor, \n "
                + "	m.nr_correcao AS acrescimo, \n "
                + "	m.nr_desconto AS desconto, \n "
                + "	m.nr_valor + m.nr_correcao - m.nr_desconto AS vlpagto, \n "
                + "	b.dt_baixa AS baixa, \n "
                + "	t.ds_descricao AS tipo_documento, \n "
                + "	m.ds_documento AS documento, \n "
                + "	l.dt_lancamento AS lancamento, \n "
                + "	l.dt_emissao AS emissao, \n "
                + "	p5.ds_conta AS conta, \n "
                + "	b.id AS baixa_id, \n "
                + "	m.id AS movimento_id, \n "
                + "	pu_baixa.ds_nome AS operador, \n "
                + "	c_baixa.ds_descricao AS caixa, \n "
                + "	t_documento_lote.ds_descricao AS tipo_documento_lote, \n "
                + "	l.ds_documento AS documento_lote, \n "
                + "	l.ds_historico AS descricao, \n "
                + "     l.ds_historico_contabil as historico \n"
                + "  FROM fin_movimento AS m \n "
                + " INNER JOIN fin_plano5 AS p5 ON p5.id = m.id_plano5 \n "
                + " INNER JOIN fin_lote AS l ON l.id = m.id_lote \n "
                + " INNER JOIN fin_tipo_documento AS t ON t.id = l.id_tipo_documento \n "
                + " INNER JOIN pes_pessoa AS p ON p.id = m.id_pessoa \n "
                + "  LEFT JOIN fin_baixa AS b ON b.id = m.id_baixa \n "
                + "  LEFT JOIN seg_usuario AS u_baixa ON u_baixa.id = b.id_usuario \n "
                + "  LEFT JOIN pes_pessoa AS pu_baixa ON pu_baixa.id = u_baixa.id_pessoa \n "
                + "  LEFT JOIN fin_caixa AS c_baixa ON c_baixa.id = b.id_caixa \n "
                + "  LEFT JOIN fin_tipo_documento AS t_documento_lote ON t_documento_lote.id = l.id_tipo_documento \n "
                + WHERE
                + ORDER_BY;
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
