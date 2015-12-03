package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LocadoraTaxaDao extends DB {

    public List findAllByServicoDiaria(Integer servico_diaria_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LT FROM LocadoraTaxa AS LT WHERE LT.servicoDiaria.id = :servico_diaria_id ORDER BY LT.servicoDiaria.descricao ASC, LT.servicoMultaDiaria.descricao ASC");
            query.setParameter("servico_diaria_id", servico_diaria_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
