package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CategoriaDao extends DB {

    public List pesquisaCategoriaPorGrupo(int idGrupoCategoria) {
        try {
            Query qry = getEntityManager().createQuery("select c "
                    + "  from Categoria c "
                    + " where c.grupoCategoria.id = " + idGrupoCategoria
                    + " order by c.categoria");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List pesquisaCategoriaPorGrupoIds(String ids) {
        try {
            Query qry = getEntityManager().createQuery("select c "
                    + "  from Categoria c "
                    + " where c.grupoCategoria.id in (" + ids + ")"
                    + " order by c.categoria");
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public GrupoCategoria pesquisaGrupoPorCategoria(int idCategoria) {
        try {
            Query qry = getEntityManager().createQuery("select gc "
                    + "  from GrupoCategoria gc "
                    + " where gc.id in (select c.grupoCategoria.id  from Categoria c where c.id = " + idCategoria + ")");
            return ((GrupoCategoria) qry.getSingleResult());
        } catch (Exception e) {
            return null;
        }
    }

    public List<GrupoCategoria> pesquisaGrupoCategoriaOrdenada() {
        try {
            Query query = getEntityManager().createQuery("SELECT GC FROM GrupoCategoria AS GC ORDER BY GC.grupoCategoria");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<?> findCategoriaByGrupoCategoria(String in) {
        if (in == null || in.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString = "SELECT C.* FROM soc_categoria AS C WHERE id_grupo_categoria IN (" + in + ") ORDER BY ds_categoria ";
            Query query = getEntityManager().createNativeQuery(queryString, Categoria.class);
            return query.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }
}
