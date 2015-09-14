package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Operacao;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DaoInterface;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ContaOperacaoDao extends DB {

    public List listPlano4AgrupadoPlanoVwNotInContaOperacao(Integer idOperacao) {
        DaoInterface di = new Dao();
        Operacao o = (Operacao) di.find(new Operacao(), idOperacao);        
        String queryString = " "
                + "     SELECT id_p4,                                           \n"
                + "            CONCAT(conta1 ||' - '|| conta3 ||' - '|| conta4) \n"
                + "       FROM plano_vw                                         \n";
        if(idOperacao == 1) {
            queryString += " WHERE replace(upper(ltrim(rtrim(conta1))), ' ','') LIKE '%RECEITA%' \n";
        } else if(idOperacao == 2) {
            queryString += " WHERE replace(upper(ltrim(rtrim(conta1))), ' ','') LIKE '%DESPESA%' \n";
        } else {
            queryString += "WHERE NOT REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%DESPESA%' \n AND "
                    + "           NOT REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','')  LIKE '%RECEITA%' \n ";
        }
        queryString += " GROUP BY conta1,           \n"
                + "               conta3,           \n"
                + "               conta4,           \n"
                + "               id_p1,            \n"
                + "               id_p3,            \n"
                + "               id_p4             \n"
                + "      ORDER BY id_p1, id_p3, id_p4      ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List listContaOperacaoPorOperacao(Integer idOperacao, Integer idPlano4) {
        try {
            Query query = getEntityManager().createQuery("SELECT CO FROM ContaOperacao AS CO WHERE CO.operacao.id = :p1 AND CO.plano5.plano4.id = :p2");
            query.setParameter("p1", idOperacao);
            query.setParameter("p2", idPlano4);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findPlano5ByPlano4NotInContaOperacao(Integer idPlano4, Integer idOperacao) {
        try {
            Query query = getEntityManager().createQuery("SELECT P5 FROM Plano5 AS P5 WHERE P5.plano4.id = :p1 AND P5.id NOT IN(SELECT CO.plano5.id FROM ContaOperacao AS CO WHERE CO.operacao.id = :p2) ORDER BY P5.classificador");
            query.setParameter("p1", idPlano4);
            query.setParameter("p2", idOperacao);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }
}
