package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioBalanceteDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioBalanceteDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioBalanceteDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String tipo_data, String data_inicial, String data_final) {
        if (relatorios == null) {
            return new ArrayList();
        }
        // CHAMADOS 1490
        try {
            String queryString = "";
            queryString += " -- RelatorioBalanceteDao->find()                   \n"
                    + " SELECT * FROM balancete_vw \n";

            List listWhere = new ArrayList<>();
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
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
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
