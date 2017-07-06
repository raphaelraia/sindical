package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CampeonatoEquipe;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CampeonatoEquipeDao extends DB {

    public CampeonatoEquipe exists(Integer equipe_id, Integer campeonato_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CE FROM CampeonatoEquipe CE WHERE CE.equipe.id = :equipe_id AND CE.campeonato.id = :campeonato_id");
            query.setParameter("equipe_id", equipe_id);
            query.setParameter("campeonato_id", campeonato_id);
            return (CampeonatoEquipe) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CampeonatoEquipe> findByCampeonato(Integer campeonato_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CE FROM CampeonatoEquipe CE WHERE CE.campeonato.id = :campeonato_id ORDER BY CE.equipe.modalidade.descricao ASC, CE.equipe.descricao ASC");
            query.setParameter("campeonato_id", campeonato_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<CampeonatoEquipe> findByModalidade(Integer modalidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CE FROM CampeonatoEquipe CE WHERE CE.campeonato.modalidade.id = :modalidade_id ORDER BY CE.equipe.modalidade.descricao ASC, CE.equipe.descricao ASC");
            query.setParameter("modalidade_id", modalidade_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
