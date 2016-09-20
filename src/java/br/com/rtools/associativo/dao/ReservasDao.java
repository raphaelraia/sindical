package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Reservas;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ReservasDao extends DB {

    private String order;
    private Integer limit;

    public ReservasDao() {
        this.limit = 0;
        this.order = " S.ds_descricao ";
    }

    public ReservasDao(Integer limit, String order) {
        this.limit = limit;
        this.order = order;
    }

    public List<Reservas> find(String description, String by, String filterMatchMode) {
        if (description.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            List listWhere = new ArrayList();
            Integer type = 0;
            if (filterMatchMode.equals("I")) {
                type = 1;
            } else if (filterMatchMode.equals("P")) {
                type = 2;
            }
            queryString = ""
                    + "     SELECT R.*                                                      \n"
                    + "       FROM car_reservas     AS R                                    \n"
                    + "  LEFT JOIN car_venda        AS V    ON V.id     = R.id_cvenda       \n"
                    + "  LEFT JOIN pes_pessoa       AS P    ON P.id     = R.id_pessoa       \n"
                    + "  LEFT JOIN pes_pessoa       AS RESP ON RESP.id  = V.id_responsavel  \n"
                    + "  LEFT JOIN eve_evento       AS E    ON E.id     = V.id_aevento      \n"
                    + "  LEFT JOIN eve_desc_evento  AS DE   ON DE.id    = E.id_descricao_evento AND DE.id_grupo_evento = 2 \n";

            switch (by) {
                case "responsavel_nome":
                    listWhere.add("UPPER(func_translate(RESP.ds_nome)) " + QueryString.typeSearch(description, type));
                    break;
                case "responsavel_documento":
                    listWhere.add("RESP.ds_documento = '" + description + "'");
                    break;
                case "nome":
                    listWhere.add("UPPER(func_translate(P.ds_nome)) " + QueryString.typeSearch(description, type));
                    break;
                case "documento":
                    listWhere.add("P.ds_documento = '" + description + "'");
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
            Query query = getEntityManager().createNativeQuery(queryString, Reservas.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Reservas> findByCVenda(Integer cvenda_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM Reservas R WHERE R.venda.id = :cvenda_id ORDER BY R.pessoa.nome");
            query.setParameter("cvenda_id", cvenda_id);
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
