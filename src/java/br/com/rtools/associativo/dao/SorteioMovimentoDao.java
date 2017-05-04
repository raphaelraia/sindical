package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.SorteioMovimento;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SorteioMovimentoDao extends DB {

    public List findBySorteio(Integer sorteio_id) {
        String queryString = "       "
                + "     SELECT SM.*                                             \n"
                + "       FROM sort_movimento AS SM                             \n"
                + " INNER JOIN pes_pessoa AS P ON P.id = SM.id_pessoa           \n"
                + "      WHERE SM.id_sorteio = " + sorteio_id + "               \n"
                + "   ORDER BY to_date(to_char(SM.dt_sorteio, 'YYYY/MM/DD'), 'YYYY/MM/DD') DESC, \n"
                + "            P.ds_nome ASC                                    \n";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, SorteioMovimento.class);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findBySorteio(Integer sorteio_id, Integer grupo_cidade_id) {
        String queryString = "       "
                + "     SELECT SM.*                                             \n"
                + "       FROM sort_movimento AS SM                             \n"
                + " INNER JOIN pes_pessoa AS P ON P.id = SM.id_pessoa           \n"
                + "      WHERE SM.id_sorteio = " + sorteio_id + "               \n";
        if (grupo_cidade_id == null) {
            queryString += " AND SM.id_grupo_cidade IS NULL \n";
        } else {
            queryString += " AND SM.id_grupo_cidade = " + grupo_cidade_id + " \n";
        }
        queryString += ""
                + "   ORDER BY to_date(to_char(SM.dt_sorteio, 'YYYY/MM/DD'), 'YYYY/MM/DD') DESC, \n"
                + "            P.ds_nome ASC                                    \n";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, SorteioMovimento.class);
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

    public Pessoa sort(Integer sorteio_id, Integer grupo_cidade_id) {
        return sort("1,3", "1", sorteio_id, grupo_cidade_id);
    }

    public Pessoa sort(String in_categorias, String in_parentescos, Integer sorteio_id, Integer grupo_cidade_id) {
        try {
            String queryString = ""
                    + "SELECT P.* FROM pes_pessoa AS P WHERE P.id IN (                          \n"
                    + "                                                                         \n"
                    + "    SELECT S.codsocio                                                    \n"
                    + "      FROM soc_socios_vw AS S                                            \n"
                    + "INNER JOIN sort_status AS SS ON SS.id_sorteio = " + sorteio_id + "       \n"
                    + " LEFT JOIN pes_pessoa_vw AS PVW ON PVW.codigo = S.titular                \n"
                    + "     WHERE S.id_parentesco IN (" + in_parentescos + ")                   \n"
                    + "       AND S.id_categoria IN(" + in_categorias + ")                      \n"
                    + "       AND S.filiacao <= (CURRENT_DATE - (SS.nr_filiacao_meses*30))         \n"
                    + "       AND func_inadimplente(S.codsocio, SS.nr_carencia_debito) = false  \n"
                    + "       AND S.codsocio NOT IN(SELECT id_pessoa FROM sort_movimento WHERE id_sorteio = " + sorteio_id + " )         \n"
                    + "       AND ( (PVW.dt_aposentadoria IS NOT NULL) OR ( PVW.admissao <= (CURRENT_DATE - (ss.nr_admissao_meses*30) ) AND demissao IS NULL) )    \n";
            if (grupo_cidade_id != null) {
                queryString += " AND S.codsocio NOT IN ( SELECT id_pessoa FROM sort_movimento WHERE id_sorteio = " + sorteio_id + " AND id_grupo_cidade = " + grupo_cidade_id + " AND date(dt_sorteio) = current_date )    \n";
                queryString += " "
                        + " AND PVW.codigo IN (                                                     \n"
                        + "               SELECT codigo                                             \n"
                        + "                 FROM pes_pessoa_vw      AS P                            \n"
                        + "           INNER JOIN arr_grupo_cidades  AS G ON G.id_grupo_cidade = " + grupo_cidade_id + "\n"
                        + "                                             AND G.id_cidade=e_id_cidade \n"
                        + " ) ";
            } else {
                queryString += " AND S.codsocio NOT IN ( SELECT id_pessoa FROM sort_movimento WHERE id_sorteio = " + sorteio_id + " AND id_grupo_cidade IS NULL AND date(dt_sorteio) = current_date ) \n";
            }
            queryString += ""
                    + " ORDER BY random() \n"
                    + "    LIMIT 1        \n"
                    + "                   \n"
                    + ")                  \n"
                    + "";
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            return (Pessoa) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }
}
