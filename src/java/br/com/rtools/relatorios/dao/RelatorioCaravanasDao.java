package br.com.rtools.relatorios.dao;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCaravanasDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioCaravanasDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioCaravanasDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String inCaravanas) {
        if (relatorios == null || relatorios.getId() == null) {
            return new ArrayList();
        }
        List listWhere = new ArrayList();
        String queryString = "";
        queryString += " -- RelatorioCaravanas->find()                  \n";
        queryString += ""
                + " SELECT                                                      \n"
                + "        descricao_caravana,                                  \n" // 0
                + "        data,                                                \n" // 1
                + "        emissao,                                             \n" // 2
                + "        operador,                                            \n" // 3
                + "        id_responsavel,                                      \n" // 4
                + "        responsavel,                                         \n" // 5
                + "        id_beneficiario,                                     \n" // 6
                + "        beneficiario,                                        \n" // 7
                + "        baixa AS pagamento,                                  \n" // 8
                + "        vencimento,                                          \n" // 9
                + "        valor,                                               \n" // 10
                + "        valor_baixa,                                         \n" // 11
                + "        caixa,                                               \n" // 12
                + "        ds_observacao AS observacao                          \n" // 13
                + "   FROM caravana_vw                                          \n";

        // CARAVANAS
        if (inCaravanas != null && !inCaravanas.isEmpty()) {
            listWhere.add(" id_caravana IN (" + inCaravanas + ")");
        }
        listWhere.add(" dt_cancelamento IS NULL ");
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        if (relatorioOrdem != null && relatorioOrdem.getId() != null) {
            queryString += " ORDER BY " + relatorioOrdem.getQuery();
        } else {
            queryString += " ORDER BY responsavel, vencimento";
        }
        Debugs.put("habilitaDebugQuery", queryString);
        Query query = getEntityManager().createNativeQuery(queryString);
        return query.getResultList();
    }

    public List findAll() {
        List listWhere = new ArrayList();
        String queryString = "";
        queryString += " -- RelatorioCaravanas->find()                  \n";
        queryString += "     SELECT C.* \n"
                + "      FROM car_caravana AS C \n"
                + "INNER JOIN eve_evento_vw AS E ON E.id_evento = C.id_evento \n"
                + "  ORDER BY dt_embarque_ida DESC \n";
        Debugs.put("habilitaDebugQuery", queryString);
        Query query = getEntityManager().createNativeQuery(queryString, Caravana.class);
        return query.getResultList();
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
