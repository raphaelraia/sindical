/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.HistoricoBancario;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class MovimentoBancarioDao extends DB {

    public List<Object> listaMovimentoBancario(Integer id_plano5, String es, String tipo_pagamento, String status, String dataInicial, String dataFinal) {
        try {
            List<String> list_where = new ArrayList();

            list_where.add("f.id_plano5 = " + id_plano5);
            list_where.add("b.dt_baixa >= CURRENT_DATE - 30");
            list_where.add("m.is_ativo = TRUE");
            list_where.add("b.dt_baixa > (SELECT MIN(dt_data) FROM fin_conta_saldo WHERE id_plano5 = " + id_plano5 + ")");

            if (!es.equals("todos")) {
                list_where.add("m.ds_es = '" + es + "'");
            }

            if (!tipo_pagamento.equals("todos")) {
                switch (tipo_pagamento) {
                    case "cheque":
                        list_where.add("(f.id_tipo_pagamento = 4 OR f.id_tipo_pagamento = 5)");
                        break;
                    case "cartao_credito":
                        list_where.add("f.id_tipo_pagamento = 6");
                        break;
                    case "cartao_debito":
                        list_where.add("f.id_tipo_pagamento = 7");
                        break;
                    case "deposito_bancario":
                        list_where.add("f.id_tipo_pagamento = 8");
                        break;
                    case "doc_bancario":
                        list_where.add("f.id_tipo_pagamento = 9");
                        break;
                }
            }

            if (!status.equals("todos")) {
                switch (status) {
                    case "a_compensar":
                        list_where.add("(chr.id_status = 8 OR chp.id_status = 8)");
                        break;
                    case "compensados":
                        list_where.add("(chr.id_status <> 8 OR chp.id_status <> 8)");
                        break;
                }
            }

            if (!dataInicial.isEmpty()) {
                list_where.add("b.dt_baixa >= '" + dataInicial + "'");
            }

            if (!dataFinal.isEmpty()) {
                list_where.add("b.dt_baixa <= '" + dataFinal + "'");
            }

            //list_where.add("f.id_status = " + id_status);
            String string_where = "";

            for (String where : list_where) {
                if (string_where.isEmpty()) {
                    string_where = " WHERE " + where + " \n ";
                } else {
                    string_where += " AND " + where + " \n ";
                }
            }

            String text
                    = "SELECT f.id AS f_id, \n"
                    + "       b.id AS b_id, \n"
                    + "	      func_documento_baixa(f.id) AS documento, \n"
                    + "	      CAST(0.0 AS DOUBLE PRECISION) AS saldo, \n"
                    + "	      f.id_tipo_pagamento AS id_tipo_pagamento, \n"
                    + "	      f.id_cheque_rec AS id_cheque_rec, \n"
                    + "	      f.id_cheque_pag AS id_cheque_pag, \n"
                    + "	      b.dt_baixa AS data_baixa, \n"
                    + "	      f.id_cartao_rec AS id_cartao_rec, \n"
                    + "	      f.id_cartao_pag AS id_cartao_pag, \n"
                    + "	      m.ds_es AS es, \n"
                    + "	      max(l.id_rotina) AS id_rotina \n"
                    + "  FROM fin_lote AS l \n"
                    + " INNER JOIN fin_movimento AS m ON m.id_lote = l.id \n"
                    + " INNER JOIN fin_baixa AS b ON b.id = m.id_baixa                  \n"
                    + " INNER JOIN fin_forma_pagamento AS f ON f.id_baixa = b.id        \n"
                    + "  LEFT JOIN fin_cheque_rec AS chr ON chr.id = f.id_cheque_rec    \n"
                    + "  LEFT JOIN fin_cheque_pag AS chp ON chp.id = f.id_cheque_pag    \n"
                    + "  LEFT JOIN fin_cartao_rec AS car_rec ON car_rec.id = f.id_cartao_rec \n"
                    + "  LEFT JOIN fin_cartao_pag AS car_pag ON car_pag.id = f.id_cartao_pag \n"
                    + string_where
                    + "  GROUP BY f.id, \n"
                    + "	   	  b.id, \n"
                    + "	   	  f.id_tipo_pagamento, \n"
                    + "	   	  f.id_cheque_rec, \n"
                    + "	   	  f.id_cheque_pag, \n"
                    + "	   	  b.dt_baixa, \n"
                    + "	   	  f.id_cartao_rec, \n"
                    + "	   	  f.id_cartao_pag, \n"
                    + "	   	  m.ds_es \n"
                    + " ORDER BY b.dt_baixa ASC ";

            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Object> listaDetalheMovimentoBancario(int id_baixa, Double valor_percentual) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT RTRIM(LTRIM(p.conta5||' '||func_nullstring(se.ds_descricao))) AS conta, \n"
                    + "     SUM(nr_valor_baixa) AS valor, \n "
                    + "     l.ds_historico_contabil AS historico, \n"
                    + "	    SUM(m.nr_valor_baixa * " + valor_percentual + " / 100) AS valor_parcial \n"
                    + "  FROM fin_movimento AS m \n"
                    + "  LEFT JOIN fin_servicos AS se ON se.id = m.id_servicos \n"
                    + " INNER JOIN plano_vw AS p ON p.id_p5 = m.id_plano5 \n"
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote \n"
                    + " WHERE m.id_baixa = " + id_baixa + " \n"
                    + " GROUP BY p.conta5, se.ds_descricao, l.ds_historico_contabil"
            );
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public ContaSaldo pesquisaContaSaldoData(String data, Integer id_conta) {
        try {
            String WHERE = "";

            if (data != null) {
                WHERE = " WHERE cs2.dt_data < '" + data + "'\n";
            } else {
                WHERE = " WHERE cs2.dt_data < CURRENT_DATE \n ";
            }

            String text = ""
                    + "SELECT cs.* \n"
                    + "  FROM fin_conta_saldo cs \n"
                    + " WHERE cs.dt_data = (\n"
                    + "SELECT MAX(cs2.dt_data) \n"
                    + "  FROM fin_conta_saldo cs2 \n"
                    + WHERE
                    + "   AND cs2.id_plano5 = " + id_conta + "\n"
                    + " ) \n"
                    + "   AND cs.id_plano5 = " + id_conta + "\n"
                    + "";

            Query qry = getEntityManager().createNativeQuery(text, ContaSaldo.class);
            return (ContaSaldo) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ContaSaldo();
    }

    public Date ultimaDataContaSaldo() {
        try {
            String text = "SELECT MAX(dt_data) FROM fin_conta_saldo";

            Query qry = getEntityManager().createNativeQuery(text);
            List<Object> result = qry.getResultList();

            List linha = (List) result.get(0);
            return (Date) linha.get(0);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<HistoricoBancario> listaHistoricoBancario(Integer id_plano5, Integer id_rotina) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT hb.* \n "
                    + " FROM fin_historico_bancario hb \n "
                    + "WHERE hb.id_plano5 = " + id_plano5 + " \n "
                    + "  AND hb.id_rotina = " + id_rotina + " \n "
                    + "ORDER BY hb.ds_descricao ",
                    HistoricoBancario.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Movimento pesquisaMovimentoPorBaixa(Integer id_baixa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT m.* \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.is_ativo = true \n "
                    + "   AND m.id_baixa = " + id_baixa + "\n"
                    + " LIMIT 1",
                    Movimento.class
            );
            return (Movimento) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<FormaPagamento> listaFormaPagamentoEstorno(Integer id_conciliacao, Boolean conciliado) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT fp.* \n "
                    + "  FROM fin_forma_pagamento fp \n "
                    + " WHERE fp.id_conciliacao = " + id_conciliacao + " AND fp.is_conciliado = " + conciliado,
                    FormaPagamento.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
//
//    public List<Movimento> listaMovimentoEstorno(Integer id_conciliacao) {
//        try {
//            Query qry = getEntityManager().createNativeQuery(
//                    " SELECT m.* FROM fin_movimento m WHERE m.id_baixa IN ( \n"
//                    + "	SELECT fp.* FROM fin_forma_pagamento fp WHERE fp.id_conciliacao = " + id_conciliacao + " \n"
//                    + ")",
//                    Movimento.class
//            );
//            return qry.getResultList();
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return new ArrayList();
//    }
}
