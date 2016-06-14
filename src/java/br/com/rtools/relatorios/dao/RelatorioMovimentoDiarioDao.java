package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
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

    public List find(Integer plano5_id, String in_status, String data) {
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
                    + "             sum(saldo_acumulado) AS saldo_acumulado,    \n"
                    + "             fstatus,                                    \n"
                    + "             fstatus_id                                  \n"
                    + "        FROM (                                           \n"
                    + "                  SELECT baixa AS DATA,                  \n"
                    + "                         CASE WHEN servico IS NOT NULL THEN UPPER(servico) ELSE UPPER(conta) END AS operacao, \n"
                    + "                         M.ds_historico AS historico,                                \n"
                    + "                         CASE WHEN es='E' THEN M.valor_baixa ELSE 0 END AS ENTRADA,  \n"
                    + "                         CASE WHEN es='S' THEN M.valor_baixa ELSE 0 END AS SAIDA,    \n"
                    + "                         0               AS saldo_acumulado, \n"
                    + "                         ST.ds_descricao AS FSTATUS,         \n"
                    + "                         ST.id           AS FSTATUS_ID       \n"
                    + "                    FROM movimentos_vw AS M                  \n"
                    + "               LEFT JOIN fin_status    AS ST ON ST.id = m.id_baixa_status \n";

            List listWhere = new ArrayList<>();
            // CONTA OU BANCO
            if (plano5_id != null) {
                listWhere.add("M.id_caixa_banco = " + plano5_id);
            } else {
                listWhere.add("M.id_plano5 IN ( SELECT id_plano5 FROM caixa_banco_vw )");
            }
            // STATUS
            if (in_status != null && !in_status.isEmpty()) {
                listWhere.add("M.id_baixa_status IN (" + in_status + ") ");
            }
            // DATA CADASTRO
            if (data != null && !data.isEmpty()) {
                listWhere.add("baixa = '" + data + "'");
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
                    + "             historico,  \n"
                    + "             fstatus,    \n"
                    + "             fstatus_id  \n"
                    + "            \n";
            if (relatorioOrdem != null) {
                queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
            } else {
                queryString += " ORDER BY data, operacao ";
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
                    + "   ORDER BY P.ds_conta ";
            Query query = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findMaxDates(Integer plano5_id) {
        try {
            String queryString = " SELECT dt_data FROM fin_conta_saldo ";
            if (plano5_id != null) {
                queryString += " WHERE id_plano5 = " + plano5_id;
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

    public Double findSaldoAnterior(String date, Integer plano5_id) {
        if (date == null || date.isEmpty()) {
            return new Double(0);
        }
        try {
            String queryString = " "
                    + "     SELECT sum(nr_saldo)    \n"
                    + "       FROM fin_conta_saldo  \n"
                    + "      WHERE dt_data=(cast('" + date + "' as date)) - 1 \n";
            if (plano5_id != null) {
                queryString += " AND id_plano5 = " + plano5_id;
            }
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                List o = (List) list.get(0);
                return new Double(o.get(0).toString());
            }
        } catch (Exception e) {
            return new Double(0);
        }
        return new Double(0);
    }
}
