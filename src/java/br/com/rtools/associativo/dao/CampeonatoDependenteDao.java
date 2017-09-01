package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CampeonatoDependente;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CampeonatoDependenteDao extends DB {

    public CampeonatoDependente exists(Integer matricula_campeonato_id, Integer pessoa_id) {

        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM CampeonatoDependente CD WHERE CD.matriculaCampeonato.id = :matricula_campeonato_id AND CD.servicoPessoa.pessoa.id = :pessoa_id");
            query.setParameter("matricula_campeonato_id", matricula_campeonato_id);
            query.setParameter("pessoa_id", pessoa_id);
            return (CampeonatoDependente) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public CampeonatoDependente existsInCampeonato(Integer campeonato_id, Integer pessoa_id) {

        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM CampeonatoDependente CD WHERE CD.matriculaCampeonato.campeonato.id = :campeonato_id AND CD.servicoPessoa.pessoa.id = :pessoa_id");
            query.setParameter("campeonato_id", campeonato_id);
            query.setParameter("pessoa_id", pessoa_id);
            return (CampeonatoDependente) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CampeonatoDependente> findByMatriculaCampeonato(Integer matricula_campeonato_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM CampeonatoDependente CD WHERE CD.matriculaCampeonato.id = :matricula_campeonato_id ORDER BY CD.servicoPessoa.pessoa.nome ASC");
            query.setParameter("matricula_campeonato_id", matricula_campeonato_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
