package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.locadoraFilme.Catalogo;
import br.com.rtools.locadoraFilme.Titulo;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CatalogoDao extends DB {

    public boolean verificaFilial(Filial filial, Titulo titulo) {
        boolean result = true;
        try {
            Query qry = getEntityManager().createQuery("    SELECT c                                   "
                    + "      FROM Catalogo c                          "
                    + "     WHERE c.filial.id = " + filial.getId() + "    "
                    + "       AND c.titulo.id = " + titulo.getId() + "    ");
            Object o = qry.getSingleResult();
            if (o == null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<Catalogo> pesquisaCatalogo(Filial filial, Titulo titulo) {
        List<Catalogo> result = null;
        try {
            Query qry = getEntityManager().createQuery("    SELECT c                                   "
                    + "      FROM Catalogo c                          "
                    + "     WHERE c.filial.id = " + filial.getId() + "    "
                    + "       AND c.titulo.id = " + titulo.getId() + "    "
                    + "  ORDER BY c.titulo.descricao                  ");

            result = qry.getResultList();

        } catch (Exception e) {
            e.getMessage();
            result = null;
        }
        return result;
    }

    public Catalogo find(Integer filial_id, Integer titulo_id) {
        try {
            Query qry = getEntityManager().createQuery("            "
                    + "    SELECT C                                 "
                    + "      FROM Catalogo C                        "
                    + "     WHERE C.filial.id = " + filial_id + "   "
                    + "       AND C.titulo.id = " + titulo_id + "   ");
            return (Catalogo) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByTitulo(Integer titulo_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Catalogo AS C WHERE C.titulo.id = :titulo_id ORDER BY C.titulo.descricao");
            query.setParameter("titulo_id", titulo_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

}
