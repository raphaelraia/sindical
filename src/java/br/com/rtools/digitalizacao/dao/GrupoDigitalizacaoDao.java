package br.com.rtools.digitalizacao.dao;

import br.com.rtools.digitalizacao.GrupoDigitalizacao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class GrupoDigitalizacaoDao extends DB {

    public List<GrupoDigitalizacao> listaGrupo() {
        Query qry = getEntityManager().createQuery("SELECT g FROM GrupoDigitalizacao g ORDER BY g.descricao");

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<GrupoDigitalizacao> findByModulo(Integer modulo_id) {
        Query query = getEntityManager().createQuery("SELECT GD FROM GrupoDigitalizacao GD  WHERE GD.modulo.id = :modulo_id ORDER BY GD.descricao");
        query.setParameter("modulo_id", modulo_id);
        try {
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
