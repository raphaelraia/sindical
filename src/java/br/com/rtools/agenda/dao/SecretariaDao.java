package br.com.rtools.agenda.dao;

import br.com.rtools.agenda.Secretaria;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SecretariaDao extends DB {

    public List<Secretaria> findBySecretaria(Integer secretaria_id) {
        List list = new ArrayList();
        try {
            Query query = getEntityManager().createQuery(" SELECT S FROM Secretaria AS S WHERE S.secretaria.id = :secretaria_id AND S.usuario.ativo = true ORDER BY S.usuario.pessoa.nome ASC");
            query.setParameter("secretaria_id", secretaria_id);
            list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

}
