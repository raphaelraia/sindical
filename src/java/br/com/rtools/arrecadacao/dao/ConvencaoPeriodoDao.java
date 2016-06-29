package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.ConvencaoCidade;
import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ConvencaoPeriodoDao extends DB {

    public List listaGrupoCidadePorConvencao(int idConvencao) {
        List<ConvencaoCidade> lista;
        try {
            Query query = getEntityManager().createQuery("      "
                    + "     SELECT cc                           "
                    + "       FROM ConvencaoCidade cc           "
                    + "      WHERE cc.convencao.id = :id        "
                    + "   ORDER BY cc.grupoCidade.descricao ASC ");
            query.setParameter("id", idConvencao);
            if (!query.getResultList().isEmpty()) {
                lista = query.getResultList();
                return lista;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public boolean convencaoPeriodoExiste(ConvencaoPeriodo convencaoPeriodo) {
        try {
            Query query = getEntityManager().createQuery("                      "
                    + "     SELECT cp                                           "
                    + "       FROM ConvencaoPeriodo cp                          "
                    + "      WHERE cp.convencao.id = :convencao                 "
                    + "        AND cp.grupoCidade.id = :grupoCidade             "
                    + "        AND cp.referenciaInicial = :referenciaInicial    "
                    + "        AND cp.referenciaFinal = :referenciaFinal        ");
            query.setParameter("convencao", convencaoPeriodo.getConvencao().getId());
            query.setParameter("grupoCidade", convencaoPeriodo.getGrupoCidade().getId());
            query.setParameter("referenciaInicial", convencaoPeriodo.getReferenciaInicial());
            query.setParameter("referenciaFinal", convencaoPeriodo.getReferenciaFinal());
            if (!query.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public List<ConvencaoPeriodo> listaConvencaoPeriodo() {
        try {
            Query query = getEntityManager().createQuery("      "
                    + "     SELECT cp                           "
                    + "       FROM ConvencaoPeriodo cp          "
                    + "   ORDER BY cp.id DESC                   ");
            if (!query.getResultList().isEmpty()) {
                return (List<ConvencaoPeriodo>) query.getResultList();
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<ConvencaoPeriodo> listaConvencaoPeriodo(Integer convencao_id, Integer grupo_cidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CP FROM ConvencaoPeriodo AS CP WHERE CP.convencao.id = :convencao_id AND CP.grupoCidade.id = :grupo_cidade_id ORDER BY CP.referenciaFinal DESC");
            query.setParameter("convencao_id", convencao_id);
            query.setParameter("grupo_cidade_id", grupo_cidade_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public ConvencaoPeriodo convencaoPeriodoConvencaoGrupoCidade(int idConvencao, int idGrupoCidade) {
        String queryString = "";
        if (idConvencao > 0 && idGrupoCidade > 0) {
            queryString = ""
                    + " SELECT CP.*                                             "
                    + "   FROM arr_convencao_periodo CP                         "
                    + "  WHERE to_char(CURRENT_DATE, 'YYYY-MM') BETWEEN concat(SUBSTRING(TRANSLATE(CP.ds_referencia_inicial, '/', '-'), 4, 9),'-', substring(TRANSLATE(CP.ds_referencia_inicial, '/', '-'), 0,3)) "
                    + "    AND concat(substring(TRANSLATE(CP.ds_referencia_final, '/', '-'), 4, 9),'-',substring(TRANSLATE(CP.ds_referencia_final, '/', '-'), 0,3)) "
                    + "    AND CP.id_convencao = " + idConvencao + " "
                    + "    AND CP.id_grupo_cidade = " + idGrupoCidade + " "
                    + "  LIMIT 1    ";
        }
        try {
            if (idConvencao > 0 && idGrupoCidade > 0) {
                Query query = getEntityManager().createNativeQuery(queryString, ConvencaoPeriodo.class);
                List list = query.getResultList();
                if (!list.isEmpty()) {
                    return (ConvencaoPeriodo) list.get(0);
                }
            }
        } catch (Exception e) {
            return new ConvencaoPeriodo();
        }
        return new ConvencaoPeriodo();
    }

    public ConvencaoPeriodo convencaoPeriodoConvencaoGrupoCidade(Integer idConvencao, Integer idGrupoCidade, String referencia_hifen) {
        ConvencaoPeriodo convencaoPeriodo = new ConvencaoPeriodo();
        String queryString = "";
        if (idConvencao > 0 && idGrupoCidade > 0) {
            queryString = ""
                    + " SELECT id "
                    + "   FROM arr_convencao_periodo "
                    + "  WHERE '" + referencia_hifen + "' BETWEEN concat(SUBSTRING(TRANSLATE(ds_referencia_inicial, '/', '-'), 4, 9),'-', substring(TRANSLATE(ds_referencia_inicial, '/', '-'), 0,3)) "
                    + "    AND concat(substring(TRANSLATE(ds_referencia_final, '/', '-'), 4, 9),'-',substring(TRANSLATE(ds_referencia_final, '/', '-'), 0,3)) "
                    + "    AND id_convencao = " + idConvencao + " "
                    + "    AND id_grupo_cidade = " + idGrupoCidade + " LIMIT 1";
        }
        List list;
        try {
            if (idConvencao > 0 && idGrupoCidade > 0) {
                Query query = getEntityManager().createNativeQuery(queryString);
                if (!query.getResultList().isEmpty()) {
                    list = (List) query.getSingleResult();
                    convencaoPeriodo = (ConvencaoPeriodo) new Dao().find(new ConvencaoPeriodo(), (Integer) list.get(0));
                }
            }
        } catch (Exception e) {
        }
        return convencaoPeriodo;
    }

    public ConvencaoPeriodo findByPessoa(Integer pessoa_id) {
        String queryString = ""
                + "     SELECT CP.*                                             \n"
                + "       FROM arr_convencao_periodo AS CP                      \n"
                + " INNER JOIN arr_contribuintes_vw C ON C.id_convencao = CP.id_convencao AND C.id_grupo_cidade = CP.id_grupo_cidade    \n"
                + "      WHERE current_date BETWEEN cast('01/'||CP.ds_referencia_inicial AS date)                                       \n"
                + "        AND date_trunc('month',cast('01/' || CP.ds_referencia_final  AS date)) + INTERVAL'1 month' - INTERVAL'1 day' \n"
                + "        AND C.id_pessoa = " + pessoa_id;

        try {
            Query query = getEntityManager().createNativeQuery(queryString, ConvencaoPeriodo.class);
            return (ConvencaoPeriodo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
