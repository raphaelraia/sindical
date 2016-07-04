package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class GrupoCidadeDao extends DB {

    public List<GrupoCidade> listaGrupoCidadePorConvencao(String listaConvencao) {
        String textQuery;
        String filtroPorConvencao = "";
        if (!listaConvencao.isEmpty()) {
            filtroPorConvencao = " WHERE c.id_convencao in (" + listaConvencao + ") ";
        }
        try {

            textQuery = "   SELECT c.id_grupo_cidade          "
                    + "     FROM arr_contribuintes_vw AS c  "
                    + filtroPorConvencao
                    + " GROUP BY c.id_grupo_cidade          "
                    + " ORDER BY c.id_grupo_cidade          ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                String idGrupoCidade = "";
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idGrupoCidade = ((List) list.get(i)).get(0).toString();
                    } else {
                        idGrupoCidade += ", " + ((List) list.get(i)).get(0).toString();
                    }
                }
                Query qryGrupoCidade = getEntityManager().createQuery(" SELECT gc FROM GrupoCidade AS gc WHERE gc.id IN(" + idGrupoCidade + ")");
                List list1 = qryGrupoCidade.getResultList();
                if (!list1.isEmpty()) {
                    return list1;
                }
            }
        } catch (EJBQLException e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

}
