package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ModeloCarteirinhaCategoriaDao extends DB {

    public List findByModeloCarteirinha(Integer modelo_carteirinha_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MCC FROM ModeloCarteirinhaCategoria AS MCC WHERE MCC.modeloCarteirinha.id = :modelo_carteirinha_id ORDER BY MCC.rotina.rotina ASC ");
            query.setParameter("modelo_carteirinha_id", modelo_carteirinha_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    /**
     * Pesquisa todas as categorias disponíveis
     *
     * @param modelo_carteirinha_id
     * @param rotina_id
     * @return
     */
    public List findNotInCategoriaByMCC(Integer modelo_carteirinha_id, Integer rotina_id) {
        try {
            String queryString = " "
                    + "     SELECT C.*                                          \n"
                    + "       FROM soc_categoria AS C                           \n"
                    + "      WHERE C.id                                         \n"
                    + "     NOT IN ( SELECT MCC.id_categoria                    \n"
                    + "                FROM soc_modelo_carteirinha_categoria AS MCC                     \n"
                    + "               WHERE MCC.id_modelo_carteirinha = " + modelo_carteirinha_id + "   \n"
                    + "                 AND MCC.id_rotina = " + rotina_id + "                           \n"
                    + "                 AND MCC.id_categoria IS NOT NULL                                \n"
                    + "            GROUP BY MCC.id_categoria                                            \n"
                    + "     ) ORDER BY C.ds_categoria ASC ";

            Query query = getEntityManager().createNativeQuery(queryString, Categoria.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public ModeloCarteirinhaCategoria findByModeloCarteirinha(Integer rotina_id, Integer categoria_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MCC FROM ModeloCarteirinhaCategoria AS MCC WHERE MCC.rotina.id = :rotina_id AND MCC.categoria.id = :categoria_id ORDER BY MCC.rotina.rotina ASC ");
            query.setParameter("rotina_id", rotina_id);
            query.setParameter("categoria_id", categoria_id);
            return (ModeloCarteirinhaCategoria) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
