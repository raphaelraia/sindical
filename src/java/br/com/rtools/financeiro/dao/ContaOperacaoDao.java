package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ContaOperacaoDao extends DB {

    public List listPlano4AgrupadoPlanoVwNotInContaOperacao(Integer idOperacao) {
        String queryString = " "
                + "     SELECT id_p4,                                           \n"
                + "            CONCAT(conta1 ||' - '|| conta3 ||' - '|| conta4) \n"
                + "       FROM plano_vw                                         \n";
        if (null != idOperacao) {
            switch (idOperacao) {
                case 1:
                    queryString += "WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%RECEITA%' ";
                    break;
                case 2:
                    queryString += "WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%DESPESA%' ";
                    break;
                case 3:
                    queryString += " WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%ATIVO%' OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%PASSIVO%' \n";
                    break;
                case 4:
                case 5:
                    queryString += "WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%ATIVO%' ";
                    break;
                case 6:
                    queryString += " WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%ATIVO%' OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%PASSIVO%' \n";
                    break;
                case 7:
                    queryString
                            += " WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%RECEITA%' \n "
                            + "     OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%ATIVO%' \n "
                            + "     OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%PASSIVO%' \n";
                case 8:
                    queryString
                            += " WHERE REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%DESPESA%' \n "
                            + "     OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%ATIVO%' \n "
                            + "     OR REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%PASSIVO%' \n";
                    break;
//                default:
//                    queryString += "WHERE NOT REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','') LIKE '%DESPESA%' \n AND "
//                            + "           NOT REPLACE(UPPER(LTRIM(RTRIM(conta1))), ' ','')  LIKE '%RECEITA%' \n ";
//                    break;
            }
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

    public List findPlano5ByPlano4NotInContaOperacao(Integer filial_id, Integer operacao_id, Integer plano4_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT P5 FROM Plano5 AS P5 WHERE P5.plano4.id = :plano4_id AND P5.id NOT IN(SELECT CO.plano5.id FROM ContaOperacao AS CO WHERE CO.operacao.id = :operacao_id AND CO.filial.id = :filial_id) ORDER BY P5.classificador");
            query.setParameter("filial_id", filial_id);
            query.setParameter("operacao_id", operacao_id);
            query.setParameter("plano4_id", plano4_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List findByOperacao(Integer operacao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CO FROM ContaOperacao CO WHERE CO.operacao.id = :operacao_id");
            query.setParameter("operacao_id", operacao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByFilialOperacao(Integer filial_id, Integer operacao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CO FROM ContaOperacao CO WHERE CO.operacao.id = :operacao_id AND CO.filial.id = :filial_id ORDER BY CO.plano5.conta ASC");
            query.setParameter("operacao_id", operacao_id);
            query.setParameter("filial_id", filial_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByFilialOperacao(Integer filial_id, Integer operacao_id, Integer centro_custo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CO FROM ContaOperacao CO WHERE CO.operacao.id = :operacao_id AND CO.filial.id = :filial_id AND CO.centroCusto.id = :centro_custo_id ORDER BY CO.plano5.conta ASC");
            query.setParameter("operacao_id", operacao_id);
            query.setParameter("filial_id", filial_id);
            query.setParameter("centro_custo_id", centro_custo_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByContaOperacao(Integer filial_id, Integer operacao_id, Integer plano4_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CO FROM ContaOperacao CO WHERE CO.operacao.id = :operacao_id AND CO.filial.id = :filial_id AND CO.plano5.plano4.id = :plano4_id ORDER BY CO.plano5.conta ASC");
            query.setParameter("operacao_id", operacao_id);
            query.setParameter("filial_id", filial_id);
            query.setParameter("plano4_id", plano4_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
