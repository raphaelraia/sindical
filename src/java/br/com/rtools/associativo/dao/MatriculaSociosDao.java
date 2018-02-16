package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class MatriculaSociosDao extends DB {

    public MatriculaSocios pesquisaPorNrMatricula(int idGpCategoria, int nrMatricula) {
        MatriculaSocios result = null;
        try {
            Query qry = getEntityManager().createQuery("select m from MatriculaSocios s"
                    + " where m.grupoCategoria.id = " + idGpCategoria
                    + "   and m.nrMatricula = " + nrMatricula);
            result = (MatriculaSocios) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List listaMatriculaPorGrupoNrMatricula(int grupoCategoria, int nrMatricula) {
        try {
            String queryString = ""
                    + "        SELECT M.nr_matricula                                      "
                    + "          FROM matr_socios AS M                                    "
                    + "    INNER JOIN soc_categoria AS C ON C.id = M.id_categoria         "
                    + "         WHERE M.nr_matricula = " + nrMatricula + "                    "
                    + "           AND C.id_grupo_categoria = " + grupoCategoria;
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public MatriculaSocios findByMatricula(Integer matricula) {
        try {
            String queryString = ""
                    + "        SELECT M.*                                       \n"
                    + "          FROM matr_socios AS M                          \n"
                    + "         WHERE M.nr_matricula = " + matricula + "        ";
            Query qry = getEntityManager().createNativeQuery(queryString, MatriculaSocios.class);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (MatriculaSocios) list.get(0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public MatriculaSocios findByMatricula(Integer matricula, Integer titular_id) {
        try {
            String queryString = ""
                    + "        SELECT M.*                                       \n"
                    + "          FROM matr_socios AS M                          \n"
                    + "         WHERE M.nr_matricula = " + matricula + "        \n"
                    + "           AND M.id_titular = " + titular_id + "        ";
            Query qry = getEntityManager().createNativeQuery(queryString, MatriculaSocios.class);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (MatriculaSocios) list.get(0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<MatriculaSocios> findAllByTitular(Integer pessoa_id) {
        try {
            String queryString = ""
                    + "        SELECT M.*                                       \n"
                    + "          FROM matr_socios AS M                          \n"
                    + "         WHERE M.id_titular = " + pessoa_id + "          \n";
            Query query = getEntityManager().createNativeQuery(queryString, MatriculaSocios.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
