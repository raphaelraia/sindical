package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CaravanaVenda;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CaravanaVendaDao extends DB {

    private String order;
    private Integer limit;

    public CaravanaVendaDao() {
        this.limit = 0;
        this.order = " S.ds_descricao ";
    }

    public CaravanaVendaDao(Integer limit, String order) {
        this.limit = limit;
        this.order = order;
    }

    public List<CaravanaVenda> find(String description, String by, String filterMatchMode) {
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
                    + "  INNER JOIN eve_evento       AS E    ON E.id     = V.id_evento       \n"
                    + "  INNER JOIN eve_desc_evento  AS DE   ON DE.id    = E.id_descricao_evento AND DE.id_grupo_evento = 2 \n"
                    + "  INNER JOIN car_caravana     AS C    ON C.id_evento = E.id  \n";

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
            queryString += " ORDER BY C.dt_embarque_ida, RESP.ds_nome";
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
            Query query = getEntityManager().createNativeQuery(queryString, CaravanaVenda.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public CaravanaVenda findByResponsavel(Integer evento_id, Integer responsavel_id) {
        List<CaravanaVenda> list = findByResponsavel(evento_id, responsavel_id, true);
        return list.isEmpty() ? null : (CaravanaVenda) list.get(0);
    }

    /**
     *
     * @param evento_id
     * @param responsavel_id
     * @param status (Ativo = true / Cancelado = false, Todos = null)
     * @return
     */
    public List<CaravanaVenda> findByResponsavel(Integer evento_id, Integer responsavel_id, Boolean status) {
        try {
            Query query;
            if (status == null) {
                query = getEntityManager().createQuery("SELECT V FROM CaravanaVenda V WHERE V.evento.id = :evento_id AND V.responsavel.id = :responsavel_id ORDER BY V.dtEmissao DESC");
            } else if (status) {
                query = getEntityManager().createQuery("SELECT V FROM CaravanaVenda V WHERE V.evento.id = :evento_id AND V.responsavel.id = :responsavel_id AND V.dtCancelamento IS NULL ORDER BY V.dtEmissao DESC");
            } else {
                query = getEntityManager().createQuery("SELECT V FROM CaravanaVenda V WHERE V.evento.id = :evento_id AND V.responsavel.id = :responsavel_id AND V.dtCancelamento IS NOT NULL ORDER BY V.dtCancelamento DESC");
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    public List<CaravanaVenda> findByReponsavel(Integer responsavel_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CV FROM CaravanaVenda AS CV WHERE CV.responsavel.id = :responsavel_id ORDER BY CV.dtEmissao DESC ");
            query.setParameter("responsavel_id", responsavel_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
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
