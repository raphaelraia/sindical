package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioBalanceteDao extends DB {

    private Integer idRelatorio;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioBalanceteDao() {
        this.idRelatorio = null;
        this.relatorioOrdem = null;
    }

    public RelatorioBalanceteDao(Integer idRelatorio, RelatorioOrdem relatorioOrdem) {
        this.idRelatorio = idRelatorio;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String tipo_data, String data_inicial, String data_final) {
        // CHAMADOS 1490
        try {
            String queryString = " -- RelatorioBalanceteDao->find()                       \n",
                    SELECT,
                    WHERE = "",
                    GROUP_BY,
                    ORDER_BY;
            
            if (idRelatorio != null && idRelatorio == 100) {
                SELECT 
                        = "   SELECT B.codigo1,    \n"
                        + "          B.conta1,     \n"
                        + "          B.codigo2,    \n"
                        + "          B.conta2,     \n"
                        + "          B.codigo3,    \n"
                        + "          B.conta3,     \n"
                        + "          B.codigo4,    \n"
                        + "          B.conta4,     \n"
                        + "          B.codigo5,    \n"
                        + "          B.conta5,     \n"
                        + "          sum(func_nulldouble(C.nr_saldo)) AS saldo_anterior,\n"
                        + "          sum(B.debito)  AS debito,  \n"
                        + "          sum(B.credito) AS credito, \n"
                        + "          sum(func_nulldouble(C.nr_saldo)+func_calcula_conta(B.is_soma_debito,B.debito,B.credito)) AS saldo_atual   \n";

                GROUP_BY
                        = " GROUP BY codigo1,   \n"
                        + "          conta1,    \n"
                        + "          codigo2,   \n"
                        + "          conta2,    \n"
                        + "          codigo3,   \n"
                        + "          conta3,    \n"
                        + "          codigo4,   \n"
                        + "          conta4,    \n"
                        + "          codigo5,   \n"
                        + "          conta5 \n ";

                if (relatorioOrdem != null) {
                    ORDER_BY = " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
                } else {
                    ORDER_BY
                            = " ORDER BY codigo1,   \n"
                            + "          conta1,    \n"
                            + "          codigo2,   \n"
                            + "          conta2,    \n"
                            + "          codigo3,   \n"
                            + "          conta3,    \n"
                            + "          codigo4,   \n"
                            + "          conta4,    \n"
                            + "          codigo5,   \n"
                            + "          conta5";
                }
            } else {
                // QUERY PARA IMPLEMENTAR O RESUMO BALANCETE
                SELECT
                        = " SELECT B.codigo1, \n "
                        + "	B.conta1, \n "
                        + "	sum(func_nulldouble(C.nr_saldo)) AS saldo_anterior, \n "
                        + "	sum(B.debito)  AS debito,  \n "
                        + "	sum(B.credito) AS credito, \n "
                        + "	sum(func_nulldouble(C.nr_saldo) + func_calcula_conta(B.is_soma_debito, B.debito, B.credito)) AS saldo_atual, \n "
                        + "  CASE WHEN is_soma_debito = true THEN 'D' ELSE 'C' END AS cd \n";

                GROUP_BY
                        = " GROUP BY \n "
                        + "     B.codigo1, \n "
                        + "	B.conta1, \n "
                        + "	is_soma_debito \n";

                ORDER_BY
                        = " ORDER BY \n "
                        + "     B.codigo1, \n "
                        + "	B.conta1 \n";
            }

            List listWhere = new ArrayList();
            
            if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
                switch (tipo_data) {
                    case "igual":
                        listWhere.add("data = '" + data_inicial + "'");
                        break;
                    case "apartir":
                        listWhere.add("data >= '" + data_inicial + "'");
                        break;
                    case "ate":
                        listWhere.add("data <= '" + data_inicial + "'");
                        break;
                    case "faixa":
                        listWhere.add("data BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
                        break;
                    default:
                        break;
                }
            }

            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    WHERE += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    WHERE += " AND " + listWhere.get(i).toString() + " \n";
                }
            }

            queryString
                    += SELECT
                    + "      FROM balancete_vw AS B  \n"
                    + " LEFT JOIN fin_conta_saldo AS C ON C.id_plano5 = B.id_conta AND C.dt_data = cast('" + data_inicial + "' AS date) - 1  \n"
                    + WHERE
                    + GROUP_BY
                    + ORDER_BY;

            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

    public List findMaxDates(String data_inicial) {
        try {
            String queryString = " SELECT dt_data FROM fin_conta_saldo ";
            if (data_inicial != null && !data_inicial.isEmpty()) {
                queryString += " WHERE dt_data >= '" + data_inicial + "'";
            }
            queryString += " GROUP BY dt_data ORDER BY dt_data DESC";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

}
