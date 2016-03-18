package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CupomMovimento;
import br.com.rtools.associativo.lista.SociosCupomMovimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CupomMovimentoDao extends DB {

    public List findByCupom(Integer cupom_id) {
        String queryString = "       "
                + "     SELECT CM.*                                             \n"
                + "       FROM eve_cupom_movimento AS CM                        \n"
                + " INNER JOIN pes_pessoa AS P ON P.id = CM.id_pessoa           \n"
                + "      WHERE CM.id_cupom = " + cupom_id + "                   \n"
                + "   ORDER BY CM.dt_emissao DESC,                              \n"
                + "            P.ds_nome ASC                                    \n";

        try {
            Query query = getEntityManager().createNativeQuery(queryString, CupomMovimento.class);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CM FROM CupomMovimento AS CM WHERE CM.pessoa.id = :pessoa_id ORDER BY CM.dtEmissao DESC ");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findAllSocios(Integer cupom_id, Integer pessoa_id) {
        List<SociosCupomMovimento> scms = new ArrayList();
        try {
            Query query = getEntityManager().createNativeQuery(""
                    + "     SELECT CASE WHEN CM.id IS NOT NULL  \n"
                    + "                 THEN true               \n"
                    + "                 ELSE false              \n"
                    + "            END,                         \n"
                    + "            S.titular,                   \n"
                    + "            S.codsocio,                  \n"
                    + "            S.nome,                      \n"
                    + "            S.grau                       \n"
                    + "       FROM soc_socios_vw AS S           \n"
                    + "  LEFT JOIN eve_cupom_movimento AS CM ON CM.id_cupom = " + cupom_id + " \n"
                    + "        AND CM.id_pessoa = S.codsocio    \n"
                    + "      WHERE S.id_matricula IN (SELECT id_matricula FROM soc_socios_vw WHERE codsocio = " + pessoa_id + " ) \n"
                    + "   ORDER BY S.grau DESC,                 \n"
                    + "            S.nome                       \n");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    List o = (List) list.get(i);
                    String bool = o.get(0).toString();
                    scms.add(new SociosCupomMovimento(Boolean.parseBoolean(bool), o.get(2), Boolean.FALSE));
                }
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return scms;
    }

}
