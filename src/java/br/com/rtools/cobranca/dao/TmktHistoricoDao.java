package br.com.rtools.cobranca.dao;

import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class TmktHistoricoDao extends DB {

    private String order;
    private Integer limit;

    public TmktHistoricoDao() {
        this.limit = 0;
        this.order = " S.ds_descricao ";
    }

    public TmktHistoricoDao(Integer limit, String order) {
        this.limit = limit;
        this.order = order;
    }

    public List<TmktHistorico> find(String description, String by, String filterMatchMode) {
        if (description.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            String queryWhere = "";
            String field = "";
            Integer type = 0;
            if (filterMatchMode.equals("I")) {
                type = 1;
            } else if (filterMatchMode.equals("P")) {
                type = 2;
            }
            if (by.equals("nome") || by.equals("cpf") || by.equals("cnpj")) {
                if (by.equals("nome")) {
                    queryWhere = "P.ds_nome " + QueryString.typeSearch(description, type);
                } else if (by.equals("cpf") || by.equals("cnpj")) {
                    queryWhere = "P.ds_documento = '" + description + "'";
                }
                queryString
                        = "      SELECT T.* "
                        + "        FROM tlm_historico AS T                            "
                        + "  INNER JOIN pes_pessoa AS P ON P.id = T.id_pessoa         "
                        + "       WHERE " + queryWhere
                        + "     ORDER BY P.ds_nome ASC ";
            } else if (by.equals("ds_") || by.equals("ds_documento")) {
                if (by.equals("operador")) {
                    queryWhere = "P.ds_nome " + QueryString.typeSearch(description, type);
                } else if (by.equals("cpf") || by.equals("cnpj")) {
                    queryWhere = "P.ds_documento = '" + description + "'";
                }
                queryString
                        = "      SELECT T.* "
                        + "        FROM tlm_historico AS T                       "
                        + "  INNER JOIN seg_usuario AS U ON U.id = T.id_operador "
                        + "  INNER JOIN pes_pessoa  AS P ON P.id = U.id_pessoa   "
                        + "       WHERE " + queryWhere
                        + "     ORDER BY P.ds_nome ASC ";
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
            Query query = getEntityManager().createNativeQuery(queryString, TmktHistorico.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<TmktHistorico> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT H FROM TmktHistorico AS H WHERE H.pessoa.id = :pessoa_id ORDER BY H.lancamento DESC ");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
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
