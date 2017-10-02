/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.beans.RelatorioMovimentoLocadoraBean;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class RelatorioMovimentoLocadoraDao extends DB {

    public List<Object> listaMovimentoLocadora(RelatorioMovimentoLocadoraBean.Filtros f, Relatorios re, RelatorioOrdem ro) {

        List<String> list_where = new ArrayList();

        String WHERE = "";

        list_where.add("id_pessoa_filial = " + Integer.valueOf(f.getListaFilial().get(f.getIndexListaFilial()).getDescription()));

        if (f.getChkCliente()) {
            if (f.getCliente().getId() != -1) {
                list_where.add("id_pessoa = " + f.getCliente().getId());
            }
        }

        if (f.getChkData()) {

            if (!f.getDtLocacaoInicial().isEmpty() || !f.getDtLocacaoFinal().isEmpty()) {
                if (!f.getDtLocacaoInicial().isEmpty() && !f.getDtLocacaoFinal().isEmpty()) {
                    list_where.add("locacao >= '" + f.getDtLocacaoInicial() + "' AND locacao <= '" + f.getDtLocacaoFinal() + "'");
                } else if (!f.getDtLocacaoInicial().isEmpty() && f.getDtLocacaoFinal().isEmpty()) {
                    list_where.add("locacao >= '" + f.getDtLocacaoInicial() + "'");
                } else {
                    list_where.add("locacao <= '" + f.getDtLocacaoFinal() + "'");
                }
            }

            if (!f.getDtPrevisaoInicial().isEmpty() || !f.getDtPrevisaoFinal().isEmpty()) {
                if (!f.getDtPrevisaoInicial().isEmpty() && !f.getDtPrevisaoFinal().isEmpty()) {
                    list_where.add("previsao >= '" + f.getDtPrevisaoInicial() + "' AND previsao <= '" + f.getDtPrevisaoFinal() + "'");
                } else if (!f.getDtPrevisaoInicial().isEmpty() && f.getDtPrevisaoFinal().isEmpty()) {
                    list_where.add("previsao >= '" + f.getDtPrevisaoInicial() + "'");
                } else {
                    list_where.add("previsao <= '" + f.getDtPrevisaoFinal() + "'");
                }
            }

            if (!f.getDtEntregaInicial().isEmpty() || !f.getDtEntregaFinal().isEmpty()) {
                if (!f.getDtEntregaInicial().isEmpty() && !f.getDtEntregaFinal().isEmpty()) {
                    list_where.add("devolucao >= '" + f.getDtEntregaInicial() + "' AND devolucao <= '" + f.getDtEntregaFinal() + "'");
                } else if (!f.getDtEntregaInicial().isEmpty() && f.getDtEntregaFinal().isEmpty()) {
                    list_where.add("devolucao >= '" + f.getDtEntregaInicial() + "'");
                } else {
                    list_where.add("devolucao <= '" + f.getDtEntregaFinal() + "'");
                }
            }

        }

        if (f.getChkFilme()) {
            if (f.getFilme().getId() != -1) {
                list_where.add("id_titulo = " + f.getFilme().getId());
            }
        }

        if (f.getChkStatus()) {
            switch (f.getStatus()) {
                case "atrasados":
                    list_where.add("devolucao IS NULL AND previsao <= CURRENT_DATE");
                    break;
                case "nao_entregue":
                    list_where.add("devolucao IS NULL");
                    break;
                case "entregue":
                    list_where.add("devolucao IS NOT NULL");
                    break;
                default:
                    break;
            }
        }

        for (String w : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + w + " \n ";
            } else {
                WHERE += " AND " + w + " \n ";
            }
        }

        String ORDER_BY
                = "ORDER BY " + ro.getQuery();

        String SELECT
                = "SELECT cnpj AS filial_cnpj, \n" // 0
                + "	  id_pessoa_filial AS filial_pessoa_id, \n" // 1
                + "	  filial AS filial_nome, \n" // 2
                + "	  id_pessoa AS cliente_id, \n" // 3
                + "	  cliente AS cliente_nome, \n" // 4
                + "	  tel1 AS cliente_tel1, \n" // 5
                + "	  tel2 AS cliente_tel2, \n" // 6
                + "	  tel3 AS cliente_tel3, \n" // 7
                + "	  email AS cliente_email, \n" // 8
                + "	  id_operador_criacao AS operador_id, \n" // 9
                + "	  operador_criacao AS operador_nome, \n" // 10
                + "	  id_lote AS lote, \n" // 11
                + "	  locacao AS data_locacao, \n" // 12
                + "	  previsao AS data_previsao, \n" // 13
                + "	  devolucao AS data_devolucao, \n" // 14
                + "	  id_operador_devolucao AS operador_devolucao_id, \n" // 15
                + "	  operador_devolucao AS operador_devolucao_nome, \n" // 16
                + "	  id_titulo AS titulo_id, \n" // 17
                + "	  titulo AS titulo_nome \n" // 18
                + "  FROM loc_movimento_vw mw \n";
        try {
            Debugs.put("habilitaDebugQuery", SELECT + WHERE + ORDER_BY);
            Query query = getEntityManager().createNativeQuery(SELECT + WHERE + ORDER_BY);

            return query.getResultList();

        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }

}
