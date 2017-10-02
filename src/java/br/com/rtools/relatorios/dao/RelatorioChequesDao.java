package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioChequesDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public List find(String in_plano5, String in_conta_banco, String in_pessoas, DateFilters dateFilters) {
        List listWhere = new ArrayList();
        String queryString = "";
        queryString = " -- RelatorioChquesLocadoraDao->find(Cadastral)          \n"
                + "    SELECT CH.dt_emissao      AS emissao,                    \n"
                + "           BC.nr_num_banco    AS banco_numero,               \n"
                + "           trim(BC.ds_banco)  AS banco,                      \n"
                + "           CB.ds_conta 	 AS conta,                      \n"
                + "           CB.ds_agencia 	 AS agencia,                    \n"
                + "           CH.ds_cheque 	 AS cheque,                     \n"
                + "           P.ds_documento 	 AS cpf_cnpj,                   \n"
                + "           P.ds_nome 	 AS nome,                       \n"
                + "           CH.dt_impressao    AS impressao,                  \n"
                + "           CH.dt_cancelamento AS cancelamento,               \n"
                + "           P5.ds_conta 	 AS historico,                  \n"
                + "           F.nr_valor         AS valor                       \n"
                + "      FROM fin_cheque_pag         AS CH \n"
                + "INNER JOIN fin_plano5            AS CT ON CT.id		= CH.id_plano5 \n"
                + "INNER JOIN fin_conta_banco       AS CB ON CB.id		= CT.id_conta_banco \n"
                + "INNER JOIN fin_banco             AS BC ON BC.id		= CB.id_banco \n"
                + " LEFT JOIN fin_forma_pagamento   AS F  ON F.id_cheque_pag = CH.id \n"
                + " LEFT JOIN fin_baixa             AS B  ON B.id		= F.id_baixa \n"
                + " LEFT JOIN fin_movimento         AS M  ON M.id_baixa 	= B.id AND M.is_ativo = true \n"
                + " LEFT JOIN fin_lote              AS L  ON L.id		= M.id_lote \n"
                + " LEFT JOIN pes_pessoa            AS P  ON P.id 		= M.id_pessoa \n"
                + " LEFT JOIN fin_plano5            AS P5 ON P5.id		= M.id_plano5  \n";
        if (dateFilters.getTitle() != null) {
            switch (dateFilters.getTitle()) {
                case "impressos":
                    listWhere.add(" CH.dt_impressao IS NOT NULL AND M.id IS NOT NULL ");
                    switch (dateFilters.getType()) {
                        case "igual":
                            listWhere.add("CH.dt_impressao = '" + dateFilters.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add("CH.dt_impressao >= '" + dateFilters.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add("CH.dt_impressao <= '" + dateFilters.getStart() + "'");
                            break;
                        case "faixa":
                            if (!dateFilters.getFinish().isEmpty()) {
                                listWhere.add("CH.dt_impressao BETWEEN  '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                            }
                            break;
                    }
                    break;
                case "nao_impressos":
                    listWhere.add("CH.dt_impressao IS NULL AND M.id IS NOT NULL");
                    break;
                case "cancelados":
                    listWhere.add("CH.dt_cancelamento IS NOT NULL ");
                    switch (dateFilters.getType()) {
                        case "igual":
                            listWhere.add("CH.dt_cancelamento = '" + dateFilters.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add("CH.dt_cancelamento >= '" + dateFilters.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add("CH.dt_cancelamento <= '" + dateFilters.getStart() + "'");
                            break;
                        case "faixa":
                            if (!dateFilters.getFinish().isEmpty()) {
                                listWhere.add("CH.dt_cancelamento BETWEEN  '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                            }
                            break;
                    }
                    break;
                case "emissao":
                    switch (dateFilters.getType()) {
                        case "igual":
                            listWhere.add("CH.dt_emissao = '" + dateFilters.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add("CH.dt_emissao >= '" + dateFilters.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add("CH.dt_emissao <= '" + dateFilters.getStart() + "'");
                            break;
                        case "faixa":
                            if (!dateFilters.getFinish().isEmpty()) {
                                listWhere.add("CH.dt_emissao BETWEEN  '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                            }
                            break;
                    }
                    break;
                default:
                    break;

            }
        }
        if (in_plano5 != null && !in_plano5.isEmpty()) {
            listWhere.add("P5.id IN (" + in_plano5 + ")");
        }
        if (in_conta_banco != null && !in_conta_banco.isEmpty()) {
            listWhere.add("CB.id IN (" + in_conta_banco + ")");
        }
        if (in_pessoas != null && !in_pessoas.isEmpty()) {
            listWhere.add("P.id IN (" + in_pessoas + ")");
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        queryString += ""
                + "  GROUP BY CH.dt_emissao,      \n"
                + "           BC.nr_num_banco,    \n"
                + "           BC.ds_banco,        \n"
                + "           CB.ds_conta,        \n"
                + "           CB.ds_agencia,      \n"
                + "           CH.ds_cheque,       \n"
                + "           P.ds_documento,     \n"
                + "           P.ds_nome,          \n"
                + "           CH.dt_impressao,    \n"
                + "           CH.dt_cancelamento, \n"
                + "           P5.ds_conta,        \n"
                + "           F.nr_valor          \n";

        queryString += " ORDER BY CH.dt_emissao, CB.ds_conta, CH.ds_cheque ";
        try {
            Debugs.put("habilitaDebugQuery", queryString);
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }
}
