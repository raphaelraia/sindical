package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioBoletosBaixadosDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioBoletosBaixadosDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioBoletosBaixadosDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String tipoDataBaixa, String dtBS, String dtBF, String tipoDataImportacao, String dtIS, String dtIF) {
        // CHAMADOS 1400
        try {
            String queryString = "";
            queryString += " -- RelatorioBoletosBaixadosDao->find()             \n"
                    + "      SELECT Pl5.ds_conta    AS conta,                   \n"
                    + "             B.dt_ocorrencia AS data_quitacao,           \n"
                    + "             B.dt_importacao AS data_importacao,         \n"
                    + "             P.ds_nome       AS pessoa_nome,             \n"
                    + "             M.ds_documento  AS boleto,                  \n"
                    + "             sum(nr_valor_baixa) AS valor                \n"
                    + "        FROM fin_movimento       AS M                    \n"
                    + "  INNER JOIN fin_baixa           AS B    ON B.id         = M.id_baixa  \n"
                    + "  INNER JOIN fin_forma_pagamento AS f    ON F.id_baixa   = B.id        \n"
                    + "  INNER JOIN pes_pessoa          AS p    ON P.id         = M.id_pessoa \n"
                    + "  INNER JOIN fin_plano5          AS pl5  ON Pl5.id       = F.id_conciliacao_plano5 \n";
            // DATA BAIXA
            List listWhere = new ArrayList<>();
            listWhere.add("M.is_ativo = true");
            listWhere.add("F.id_tipo_pagamento IN (2,3)");
            listWhere.add("M.ds_documento <> ''");
            listWhere.add("F.id_conciliacao_plano5 IN(SELECT id FROM fin_plano5 WHERE id_plano4 IN (SELECT id_plano4 FROM fin_conta_rotina WHERE id_rotina = 2))");
            listWhere.add("M.id_servicos NOT IN ( SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4 )");
            if (!dtBS.isEmpty() || !dtBS.isEmpty()) {
                switch (tipoDataBaixa) {
                    case "igual":
                        listWhere.add("B.dt_ocorrencia = '" + dtBS + "'");
                        break;
                    case "apartir":
                        listWhere.add("B.dt_ocorrencia >= '" + dtBS + "'");
                        break;
                    case "ate":
                        listWhere.add("B.dt_ocorrencia <= '" + dtBS + "'");
                        break;
                    case "faixa":
                        listWhere.add("B.dt_ocorrencia BETWEEN '" + dtBS + "' AND '" + dtBF + "'");
                        break;
                    default:
                        break;
                }
            }
            // DATA IMPORTAÇÃO
            if (!dtIS.isEmpty() || !dtIF.isEmpty()) {
                switch (tipoDataImportacao) {
                    case "igual":
                        listWhere.add("B.dt_importacao = '" + dtIS + "'");
                        break;
                    case "apartir":
                        listWhere.add("B.dt_importacao >= '" + dtIS + "'");
                        break;
                    case "ate":
                        listWhere.add("B.dt_importacao <= '" + dtIS + "'");
                        break;
                    case "faixa":
                        listWhere.add("B.dt_importacao BETWEEN '" + dtIS + "' AND '" + dtIF + "'");
                        break;
                    default:
                        break;
                }
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += "GROUP BY Pl5.ds_conta,    \n"
                    + "              B.dt_ocorrencia,      \n"
                    + "              B.dt_importacao, \n"
                    + "              P.ds_nome,       \n"
                    + "              M.ds_documento   \n";

            queryString += "ORDER BY B.dt_ocorrencia, M.ds_documento \n";
            Debugs.put("habilitaDebugQuery", queryString);
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

}
