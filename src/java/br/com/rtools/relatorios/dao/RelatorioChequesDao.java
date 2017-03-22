package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioChequesDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public List find(String in_plano5, String in_conta_banco, String selectedStatus) {
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
                + "           P5.ds_conta 	   AS historico                 \n"
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
        if (selectedStatus.equals("impressos")) {
            listWhere.add(" ch.dt_impressao is not null and m.id is not null ");
        } else if (selectedStatus.equals("nao_impressos")) {
            listWhere.add("ch.dt_impressao is not null and m.id is not null and m.id is not null");
        } else if (selectedStatus.equals("cancelados")) {
            listWhere.add("ch.dt_cancelamento is not null or m.id is null ");
        }
        if (in_plano5 != null && !in_plano5.isEmpty()) {
            listWhere.add("CT.id IN (" + in_plano5 + ")");
        }
        if (in_conta_banco != null && !in_conta_banco.isEmpty()) {
            listWhere.add("CB.id IN (" + in_conta_banco + ")");
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
                + "           P5.ds_conta           ";

        queryString += " ORDER BY CH.dt_emissao, CB.ds_conta, CH.ds_cheque ";
        try {
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
