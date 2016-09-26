package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CaravanaReservas;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class CaravanaReservasDao extends DB {

    private String order;
    private Integer limit;

    public CaravanaReservasDao() {
        this.limit = 0;
        this.order = " S.ds_descricao ";
    }

    public CaravanaReservasDao(Integer limit, String order) {
        this.limit = limit;
        this.order = order;
    }

    public List<CaravanaReservas> find(String description, String by, String filterMatchMode) {
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
                    + "     SELECT R.*                                                          \n"
                    + "       FROM car_reservas     AS R                                        \n"
                    + "  LEFT JOIN car_venda        AS V    ON V.id     = R.id_caravana_venda   \n"
                    + "  LEFT JOIN pes_pessoa       AS P    ON P.id     = R.id_pessoa           \n"
                    + "  LEFT JOIN pes_pessoa       AS RESP ON RESP.id  = V.id_responsavel      \n"
                    + "  LEFT JOIN eve_evento       AS E    ON E.id     = V.id_evento           \n"
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
            Query query = getEntityManager().createNativeQuery(queryString, CaravanaReservas.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<CaravanaReservas> findByCaravanaVenda(Integer caravana_venda_id) {
        return findByCaravanaVenda(caravana_venda_id, true);
    }

    public List<CaravanaReservas> findByCaravanaVenda(Integer caravana_venda_id, Boolean ativas) {
        try {
            Query query = null;
            if (ativas == null) {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.id = :caravana_venda_id ORDER BY CR.pessoa.nome");
            } else if (ativas) {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.id = :caravana_venda_id AND CR.dtCancelamento IS NULL ORDER BY CR.pessoa.nome");
            } else {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.id = :caravana_venda_id AND CR.dtCancelamento IS NOT NULL ORDER BY CR.pessoa.nome");
            }
            query.setParameter("caravana_venda_id", caravana_venda_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<CaravanaReservas> findPoltronaDisponivel(Integer caravana_id, Integer poltrona) {
        return findByCaravanaVenda(caravana_id, true);
    }

    public List<CaravanaReservas> findPoltronaDisponivel(Integer caravana_id, Integer poltrona, Boolean ativas) {
        try {
            Query query = null;
            if (ativas == null) {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.poltrona = :poltrona ORDER BY CR.pessoa.nome");
            } else if (ativas) {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.poltrona = :poltrona AND CR.dtCancelamento IS NULL ORDER BY CR.pessoa.nome");
            } else {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.poltrona = :poltrona AND CR.dtCancelamento IS NOT NULL ORDER BY CR.pessoa.nome");
            }
            query.setParameter("caravana_id", caravana_id);
            query.setParameter("poltrona", poltrona);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<CaravanaReservas> findPassageiroCaravana(Integer caravana_id, Integer passageiro_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.pessoa.id = :passageiro_id AND CR.dtCancelamento IS NULL ORDER BY CR.pessoa.nome");
            query.setParameter("caravana_id", caravana_id);
            query.setParameter("passageiro_id", passageiro_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public int qntReservas(int idEvento, int idGrupoEvento) {
        int qnt = -1;
        try {
            Query qry = getEntityManager().createQuery("SELECT count(res) "
                    + "  from CaravanaReservas res"
                    + " where res.venda.evento.id =" + idEvento
                    + "   and res.venda.evento.descricaoEvento.grupoEvento.id = " + idGrupoEvento);
            qnt = Integer.parseInt(String.valueOf((Long) qry.getSingleResult()));
            return qnt;
        } catch (EJBQLException e) {
            e.getMessage();
            return qnt;
        }
    }

    public List<CaravanaReservas> listaReservasVenda(int idVenda) {
        List<CaravanaReservas> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select res"
                    + "  from CaravanaReservas res"
                    + " where res.venda.id = " + idVenda);
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    public List<CaravanaReservas> listaReservasVendaPessoa(int idVenda, int idPessoa) {
        List<CaravanaReservas> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select res"
                    + "  from CaravanaReservas res"
                    + " where res.venda.id = " + idVenda
                    + "   and res.pessoa.id = " + idPessoa);
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    public List<CaravanaReservas> findByCaravana(Integer caravana_id, Boolean ativas) {
        try {
            Query query;
            if (ativas) {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.dtCancelamento IS NULL ORDER BY CR.pessoa.nome");
            } else {
                query = getEntityManager().createQuery("SELECT CR FROM CaravanaReservas CR WHERE CR.venda.caravana.id = :caravana_id AND CR.dtCancelamento IS NOT NULL ORDER BY CR.pessoa.nome");
            }
            query.setParameter("caravana_id", caravana_id);
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
