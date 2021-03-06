package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioMovimentoDiarioDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;
    private String qryMovimentoDiario;

    public RelatorioMovimentoDiarioDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
        this.qryMovimentoDiario
                = " -- RelatorioMovimentoDiarioDao->find() \n"
                + "SELECT data,                                       \n"
                + "             operacao,                                   \n"
                + "             historico,                                  \n"
                + "             sum(entrada)         AS entrada,            \n"
                + "             sum(saida)           AS saida,              \n"
                + "             sum(saldo_acumulado) AS saldo_acumulado,    \n"
                + "             fstatus,                                    \n"
                + "             fstatus_id,                                 \n"
                + "             id_movimento,\n"
                + "             case when grupo is null then 'OUTRAS CONTAS' else grupo end as grupo, \n "
                + "             case when id_rotina IS NULL THEN 3 ELSE id_rotina END AS id_rotina, \n"
                + "             0 AS saldo \n"
                + "        FROM (                                           \n"
                + "                  SELECT baixa AS DATA,                  \n"
                + "                         CASE WHEN servico IS NOT NULL THEN UPPER(servico) ELSE UPPER(m.conta) END AS operacao, \n"
                + "                         M.ds_historico AS historico,                                \n"
                + "                         CASE WHEN es='E' THEN M.valor_baixa ELSE 0 END AS ENTRADA,  \n"
                + "                         CASE WHEN es='S' THEN M.valor_baixa ELSE 0 END AS SAIDA,    \n"
                + "                         0               AS saldo_acumulado, \n"
                + "                         M.tipo_pagamento  AS FSTATUS,         \n"
                + "                         M.id_tipo_pagamento           AS FSTATUS_ID,      \n"
                + "                         M.id_movimento,\n"
                + "                         pl.conta4||'  ['||pl.conta1||']' as grupo, \n"
                + "                         cb.id_rotina AS id_rotina \n"
                + "                    FROM movimentos_vw AS M  \n"
                + "                    LEFT JOIN plano_vw AS pl ON pl.id_p5 = m.id_caixa_banco \n"
                + "                    LEFT JOIN caixa_banco_vw AS cb ON cb.id_plano5 = pl.id_p5 ";
    }

    public RelatorioMovimentoDiarioDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(Integer plano5_id, String in_status, String data, String tipo) {
        if (relatorios == null) {
            return new ArrayList();
        }
        // CHAMADOS 1490
        try {
            String queryString = qryMovimentoDiario;

            List listWhere = new ArrayList<>();
            // CONTA OU BANCO
            if (plano5_id != null) {
                listWhere.add("M.id_caixa_banco = " + plano5_id);
            }
            // STATUS
            if (in_status != null && !in_status.isEmpty()) {
                listWhere.add("M.id_baixa_status IN (" + in_status + ") ");
            }
            // DATA CADASTRO
            if (data != null && !data.isEmpty()) {
                listWhere.add("baixa = '" + data + "'");
            }

            if (tipo.equals("caixa")) {
                listWhere.add("cb.id_rotina = 1");
            } else if (tipo.equals("banco")) {
                listWhere.add("cb.id_rotina = 2");
            } else {
                listWhere.add("cb.id_rotina IS NULL");
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
                    + "             fstatus_id,  \n"
                    + "             id_movimento,  \n"
                    + "             grupo,  \n"
                    + "             id_rotina  \n"
                    + "            \n";
            if (relatorioOrdem != null) {
                queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
            } else {
                queryString += " ORDER BY data, id_rotina, grupo, id_movimento ";
            }
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
            } else {
                queryString += " AND id_plano5 IN ( SELECT id_plano5 FROM caixa_banco_vw )";
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

    public Double findSaldoAnteriorCaixa(String data) {
        try {
            String queryString = " "
                    + "      SELECT sum(nr_saldo)    \n"
                    + "       FROM fin_conta_saldo  \n"
                    + "      WHERE dt_data=(cast('" + data + "' as date)) - 1 \n"
                    + " AND id_plano5 IN ( SELECT id_plano5 FROM caixa_banco_vw WHERE id_rotina = 1)";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                List o = (List) list.get(0);
                return new Double(o.get(0).toString());
            }
            return new Double(0);
        } catch (Exception e) {
            return new Double(0);
        }
    }

    public Double findSaldoAnteriorBanco(String data) {
        try {
            String queryString = " "
                    + "      SELECT sum(nr_saldo)    \n"
                    + "       FROM fin_conta_saldo  \n"
                    + "      WHERE dt_data=(cast('" + data + "' as date)) - 1 \n"
                    + " AND id_plano5 IN ( SELECT id_plano5 FROM caixa_banco_vw WHERE id_rotina = 2)";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                List o = (List) list.get(0);
                return new Double(o.get(0).toString());
            }
            return new Double(0);
        } catch (Exception e) {
            return new Double(0);
        }
    }

}
