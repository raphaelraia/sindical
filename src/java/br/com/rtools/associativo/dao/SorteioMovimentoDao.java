package br.com.rtools.associativo.dao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SorteioMovimentoDao extends DB {

    public List findBySorteio(Integer sorteio_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SM FROM SorteioMovimento AS SM WHERE SM.sorteio.id = :sorteio_id ORDER BY SM.dtSorteio DESC, SM.pessoa.nome ASC ");
            query.setParameter("sorteio_id", sorteio_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SM FROM SorteioMovimento AS SM WHERE SM.pessoa.id = :pessoa_id ORDER BY SM.dtSorteio DESC ");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Pessoa sort(Integer sorteio_id) {
        return sort("1", "1,3", sorteio_id);
    }

    public Pessoa sort(String in_categorias, String in_parentescos, Integer sorteio_id) {
        try {
            String queryString = ""
                    + "SELECT P.* FROM pes_pessoa AS P WHERE P.id IN (                           \n"
                    + "                                                                          \n"
                    + "    SELECT S.codsocio                                                     \n"
                    + "      FROM soc_socios_vw AS S                                             \n"
                    + "INNER JOIN sort_status AS SS ON SS.id_sorteio = " + sorteio_id + "        \n"
                    + "     WHERE S.id_parentesco IN (" + in_parentescos + ")                    \n"
                    + "       AND S.id_categoria IN(" + in_categorias + ")                       \n"
                    + "       AND func_inadimplente(S.codsocio, SS.nr_carencia_debito) = true\n"
                    + "  ORDER BY random()                                                       \n"
                    + "     LIMIT 1                                                               \n"
                    + "                                                                          \n"
                    + ")                                                                         \n"
                    + "";
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            return (Pessoa) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }
}
