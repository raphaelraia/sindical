package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCredenciadoresDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioCredenciadoresDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioCredenciadoresDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String inParentesco, String inCredenciadores, List<DateFilters> listDateFilters) {
        if (relatorios == null || relatorios.getId() == null) {
            return new ArrayList();
        }
        List listWhere = new ArrayList();
        String queryString = "";
        queryString += " -- RelatorioCredenciadoresDao->find()                \n";
        queryString += " SELECT S.filiacao,                                     \n"
                + "             S.id_credenciador,                              \n"
                + "             S.credenciador,                                 \n"
                + "             T.ds_nome AS socio,                             \n"
                + "             S.matricula,                                    \n"
                + "             S.categoria                                     \n"
                + "        FROM soc_socios_vw AS S                              \n"
                + "  INNER JOIN pes_pessoa AS T ON T.id = S.titular             \n";

        if (listDateFilters != null && !listDateFilters.isEmpty()) {
            DateFilters cadastro = DateFilters.getDateFilters(listDateFilters, "filiacao");
            if (cadastro != null) {
                if ((cadastro.getDtStart() != null && !cadastro.getStart().isEmpty()) || cadastro.getType().equals("com") || cadastro.getType().equals("sem")) {
                    switch (cadastro.getType()) {
                        case "igual":
                            listWhere.add(" S.filiacao = '" + cadastro.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" S.filiacao >= '" + cadastro.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" S.filiacao <= '" + cadastro.getStart() + "'");
                            break;
                        case "faixa":
                            if (!cadastro.getStart().isEmpty()) {
                                listWhere.add(" S.filiacao BETWEEN '" + cadastro.getStart() + "' AND '" + cadastro.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" S.filiacao IS NOT NULL ");
                            break;
                        case "null":
                            listWhere.add(" S.filiacao IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        // OPERADOR
        if (inCredenciadores != null && !inCredenciadores.isEmpty()) {
            listWhere.add(" S.id_credenciador IN (" + inCredenciadores + ")");
        }
        if (inParentesco != null && !inParentesco.isEmpty()) {
            listWhere.add(" S.id_parentesco IN (" + inParentesco + ")");
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        queryString += " ORDER BY S.credenciador, S.filiacao ";
        Debugs.put("habilitaDebugQuery", queryString);
        Query query = getEntityManager().createNativeQuery(queryString);
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
