package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Dispositivo;
import javax.persistence.Query;

public class DispositivoDao extends DB {

    public Dispositivo findByMacTipo(String mac, Integer tipo_dispositivo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT D FROM Dispositivo D WHERE D.mac = :mac AND D.tipoDispositivo.id = :tipo_dispositivo_id");
            query.setParameter("mac", mac);
            query.setParameter("tipo_dispositivo_id", tipo_dispositivo_id);
            return (Dispositivo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Dispositivo findByMacFilial(Integer mac_filial_id, Integer tipo_dispositivo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT D FROM Dispositivo D WHERE D.macFilial.id = :mac_filial_id AND D.tipoDispositivo.id = :tipo_dispositivo_id");
            query.setParameter("mac_filial_id", mac_filial_id);
            query.setParameter("tipo_dispositivo_id", tipo_dispositivo_id);
            query.setMaxResults(1);
            return (Dispositivo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Dispositivo findByFilial(Integer filial_id, Integer tipo_dispositivo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT D FROM Dispositivo D WHERE D.filial.id = :filial_id AND D.tipoDispositivo.id = :tipo_dispositivo_id");
            query.setParameter("filial_id", filial_id);
            query.setParameter("tipo_dispositivo_id", tipo_dispositivo_id);
            query.setMaxResults(1);
            return (Dispositivo) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
