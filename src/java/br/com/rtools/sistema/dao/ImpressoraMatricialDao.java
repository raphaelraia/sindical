package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ImpressoraMatricial;
import javax.persistence.Query;

public class ImpressoraMatricialDao extends DB {

    public ImpressoraMatricial findByMacTipo(String mac, Integer tipo_dispositivo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT IM FROM ImpressoraMatricial IM WHERE IM.dispositivo.mac = :mac AND IM.dispositivo.tipoDispositivo.id = :tipo_dispositivo_id ORDER BY IM.id ASC");
            query.setParameter("mac", mac);
            query.setParameter("tipo_dispositivo_id", tipo_dispositivo_id);
            query.setMaxResults(1);
            return (ImpressoraMatricial) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
