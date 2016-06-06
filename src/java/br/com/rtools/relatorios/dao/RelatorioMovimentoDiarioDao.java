package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioMovimentoDiarioDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioMovimentoDiarioDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioMovimentoDiarioDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String in_plano5, String tipoData, String data_inicial, String data_final) {
        if (relatorios == null) {
            return new ArrayList();
        }
        // CHAMADOS 1490
        try {
            String queryString = "";
            queryString += " -- RelatorioMovimentoDiarioDao->find()             \n"
                    + "      SELECT data,                                       \n"
                    + "             operacao,                                   \n"
                    + "             historico,                                  \n"
                    + "             sum(entrada)         AS entrada,            \n"
                    + "             sum(saida)           AS saida,              \n"
                    + "             sum(saldo_acumulado) AS saldo_acumulado     \n"
                    + "        FROM (                                           \n"
                    + "                  SELECT baixa AS DATA,                  \n"
                    + "                         CASE WHEN servico IS NOT NULL THEN UPPER(servico) ELSE UPPER(conta) END AS operacao, \n"
                    + "                         ds_historico AS historico,                                  \n"
                    + "                         CASE WHEN es='E' THEN M.valor_baixa ELSE 0 END AS ENTRADA,  \n"
                    + "                         CASE WHEN es='S' THEN M.valor_baixa ELSE 0 END AS SAIDA,    \n"
                    + "                         0 AS saldo_acumulado            \n"
                    + "                    FROM movimentos_vw AS M              \n";

            List listWhere = new ArrayList<>();
            // SITUAÇÃO
            // UNIDADE
            if (in_plano5 != null) {
                listWhere.add("M.id_plano5 IN (" + in_plano5 + ")");
            }
            // DATA CADASTRO
            if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
                switch (tipoData) {
                    case "igual":
                        listWhere.add("baixa = '" + data_inicial + "'");
                        break;
                    case "apartir":
                        listWhere.add("baixa >= '" + data_inicial + "'");
                        break;
                    case "ate":
                        listWhere.add("baixa <= '" + data_inicial + "'");
                        break;
                    case "faixa":
                        listWhere.add("baixa BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
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
            queryString
                    += "         ) AS X \n"
                    + "    GROUP BY data,       \n"
                    + "             operacao,   \n"
                    + "             historico   \n"
                    + "            \n";
            if (relatorioOrdem != null) {
                queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
            } else {
                queryString += " ORDER BY data, operacao";
            }
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

    public List findPlano5() {
        try {
            String queryString = ""
                    + "     SELECT P.*                                                  \n"
                    + "       FROM fin_plano5 AS P                                      \n"
                    + " INNER JOIN fin_conta_rotina AS CR ON CR.id_plano4 = P.id_plano4 \n"
                    + "      WHERE id_rotina IN(1,2)                                    \n"
                    + "   ORDER BY P.id,                                                \n"
                    + "            P.ds_conta ";
            Query query = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
