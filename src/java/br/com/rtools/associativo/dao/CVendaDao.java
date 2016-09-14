package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CVenda;
import br.com.rtools.associativo.Reservas;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CVendaDao extends DB {

    private String order;
    private Integer limit;

    public CVendaDao() {
        this.limit = 0;
        this.order = " S.ds_descricao ";
    }

    public CVendaDao(Integer limit, String order) {
        this.limit = limit;
        this.order = order;
    }

        public List<CVenda> find(String description, String by, String filterMatchMode) {
        if (description.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            List listWhere = new ArrayList();
            String field = "";
            Integer type = 0;
            if (filterMatchMode.equals("I")) {
                type = 1;
            } else if (filterMatchMode.equals("P")) {
                type = 2;
            }

            queryString = "" 
                    + "      SELECT V.*                                        \n"
                    + "        FROM car_venda        AS V                       \n"
                    + "  INNER JOIN pes_pessoa       AS RESP ON RESP.id  = V.id_responsavel  \n"
                    + "  INNER JOIN eve_evento       AS E    ON E.id     = V.id_aevento      \n"
                    + "  INNER JOIN eve_desc_evento  AS DE   ON DE.id    = E.id_descricao_evento AND DE.id_grupo_evento = 2 \n"
                    + "  INNER JOIN car_caravana     AS C    ON C.id_aevento = E.id \n";

            switch (by) {
                case "responsavel_nome":
                    listWhere.add("UPPER(func_translate(RESP.ds_nome)) " + QueryString.typeSearch(description, type));
                    break;
                case "responsavel_documento":
                    listWhere.add("RESP.ds_documento = '" + description + "'");
                    break;
                case "descricao_evento":
                    listWhere.add("UPPER(func_translate(DE.ds_descricao)) " + QueryString.typeSearch(description, type));
                    break;
                default:
                    break;
            }

            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += " ORDER BY C.dt_saida, RESP.ds_nome";
            int maxResults = 1000;
            if (limit == 0) {
                switch (description.length()) {
                    case 1:
                        maxResults = 50;
                        break;
                    case 2:
                        maxResults = 150;
                        break;
                    case 3:
                        maxResults = 500;
                        break;
                    default:
                        break;
                }
            } else {
                maxResults = limit;
            }
            Query query = getEntityManager().createNativeQuery(queryString, CVenda.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
