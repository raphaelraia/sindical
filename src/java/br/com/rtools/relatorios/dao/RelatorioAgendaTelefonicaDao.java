package br.com.rtools.relatorios.dao;

import br.com.rtools.agenda.Agenda;
import br.com.rtools.agenda.GrupoAgenda;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioAgendaTelefonicaDao extends DB {

    private String order = "";

    public List find(String in_grupos, String in_cidades) {
        // CHAMADOS 1490
        try {
            String queryString = "";
            queryString += " -- RelatorioMovimentoDiarioDao->find()             \n"
                    + "      SELECT A.*                                         \n"
                    + "        FROM age_agenda AS A                             \n"
                    + "  INNER JOIN end_endereco AS E ON E.id = A.id_endereco   \n";

            List listWhere = new ArrayList<>();
            // GRUPOS
            if (in_grupos != null && !in_grupos.isEmpty()) {
                listWhere.add("A.id_grupo_agenda IN (" + in_grupos + ") ");
            }
            // CIDADES
            if (in_cidades != null && !in_cidades.isEmpty()) {
                listWhere.add("E.id_cidade IN (" + in_cidades + ") ");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (!order.isEmpty()) {
                queryString += " ORDER BY  " + order + " \n";
            }
            Query query = getEntityManager().createNativeQuery(queryString, Agenda.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findGrupos() {
        try {
            String queryString = ""
                    + "     SELECT G.* FROM \n"
                    + "age_grupo_agenda G WHERE G.id IN (\n"
                    + "SELECT A.id_grupo_agenda\n"
                    + "FROM age_agenda AS A \n"
                    + "GROUP BY A.id_grupo_agenda\n"
                    + ") ";
            Query query = getEntityManager().createNativeQuery(queryString, GrupoAgenda.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findAllUf() {
        try {
            String queryString = ""
                    + "     SELECT C.ds_uf                                      \n"
                    + "       FROM age_agenda AS A                              \n"
                    + " INNER JOIN end_endereco AS E ON E.id = A.id_endereco    \n"
                    + " INNER JOIN end_cidade AS C ON C.id = E.id_cidade        \n"
                    + "   GROUP BY C.ds_uf                                      \n";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findAllCidades(String uf) {
        try {
            String queryString = ""
                    + "     SELECT C.*                                          \n"
                    + "       FROM end_cidade C                                 \n"
                    + "      WHERE C.id IN (                                    \n"
                    + "                     SELECT C2.id                        \n"
                    + "                       FROM age_agenda   AS A            \n"
                    + "                 INNER JOIN end_endereco AS E ON E.id = A.id_endereco    \n"
                    + "                 INNER JOIN end_cidade   AS C2 ON C2.id = E.id_cidade    \n"
                    + "                      WHERE C2.ds_uf = '" + uf + "'                      \n"
                    + "                   GROUP BY C2.id                                        \n"
                    + "     ) ORDER BY C.ds_cidade                                                 \n";
            Query query = getEntityManager().createNativeQuery(queryString, Cidade.class);
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
}
