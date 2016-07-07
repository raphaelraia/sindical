package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.MovimentoInativo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class MovimentoInativoDao extends DB {

    public MovimentoInativo findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MI FROM MovimentoInativo AS MI WHERE MI.movimento.id = :movimento_id");
            query.setParameter("movimento_id", movimento_id);
            return (MovimentoInativo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByUsuario(Integer usuario_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MI FROM MovimentoInativo AS MI WHERE MI.usuario.id = :usuario_id ORDER BY MI.dtData DESC");
            query.setParameter("usuario_id", usuario_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
