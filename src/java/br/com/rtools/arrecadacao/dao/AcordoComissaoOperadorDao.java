package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.AcordoComissaoOperador;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AcordoComissaoOperadorDao extends DB {

    public List<AcordoComissaoOperador> findAll() {
        try {
            Query query = getEntityManager().createQuery(" SELECT ACO FROM AcordoComissaoOperador AS ACO ORDER BY ACO.rotina.rotina ASC, ACO.usuario.pessoa.nome ASC");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<AcordoComissaoOperador> findByRotina(Integer rotina_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT ACO FROM AcordoComissaoOperador AS ACO WHERE ACO.rotina.id = :rotina_id ORDER BY ACO.usuario.pessoa.nome ASC");
            query.setParameter("rotina_id", rotina_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<AcordoComissaoOperador> findByUsuario(Integer usuario_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT ACO FROM AcordoComissaoOperador AS ACO WHERE ACO.usuario.id = :usuario_id ORDER BY ACO.rotina.rotina ASC");
            query.setParameter("usuario_id", usuario_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public AcordoComissaoOperador find(Integer usuario_id, Integer rotina_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT ACO FROM AcordoComissaoOperador AS ACO WHERE ACO.usuario.id = :usuario_id AND ACO.rotina.id = :rotina_id");
            query.setParameter("usuario_id", usuario_id);
            query.setParameter("rotina_id", rotina_id);
            return (AcordoComissaoOperador) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
