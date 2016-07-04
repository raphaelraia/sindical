package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class ConvencaoDao extends DB {

    public Convencao pesquisaConvencaoDesc(String descricao) {
        Convencao result = null;
        try {
            Query qry = getEntityManager().createQuery("select con from Convencao con where con.descricao like :d_convencao");
            qry.setParameter("d_convencao", descricao);
            result = (Convencao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Convencao findByEmpresa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createNativeQuery("select c.* from arr_convencao c where c.id = (select id_convencao from arr_contribuintes_vw where id_pessoa = " + pessoa_id + ")", Convencao.class);
            return (Convencao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Convencao> listaConvencao() {
        return listaConvencao(false);
    }

    public List<Convencao> listaConvencao(boolean isContabilidade) {
        String textQuery;
        try {

            textQuery = "   SELECT c.id_convencao               "
                    + "     FROM arr_contribuintes_vw AS c      ";

            if (isContabilidade) {
                textQuery += " WHERE id_contabilidade IS NOT NULL ";
            }

            textQuery += ""
                    + " GROUP BY c.id_convencao                 "
                    + " ORDER BY c.id_convencao                 ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                if (!list.isEmpty()) {
                    String idConvencao = "";
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idConvencao = ((List) list.get(i)).get(0).toString();
                        } else {
                            idConvencao += ", " + ((List) list.get(i)).get(0).toString();
                        }
                    }
                    Query qryConvencoes = getEntityManager().createQuery(" SELECT con FROM Convencao AS con WHERE con.id IN (" + idConvencao + ")");
                    List list1 = qryConvencoes.getResultList();
                    if (!list1.isEmpty()) {
                        return list1;
                    }
                }
                return list;
            }
        } catch (EJBQLException e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
}
