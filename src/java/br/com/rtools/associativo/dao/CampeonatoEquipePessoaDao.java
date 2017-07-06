package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CampeonatoEquipe;
import br.com.rtools.associativo.CampeonatoEquipePessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CampeonatoEquipePessoaDao extends DB {

    public CampeonatoEquipePessoa exists(Integer campeonato_equipe_id, Integer campeonato_id, Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CEP FROM CampeonatoEquipePessoa CEP WHERE CEP.campeonatoEquipe.id = :campeonato_equipe_id AND CEP.campeonato.id = :campeonato_id AND CEP.pessoa.id = :pessoa_id");
            query.setParameter("campeonato_equipe_id", campeonato_equipe_id);
            query.setParameter("campeonato_id", campeonato_id);
            query.setParameter("pessoa_id", pessoa_id);
            return (CampeonatoEquipePessoa) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CampeonatoEquipePessoa> findByCampeonatoEquipe(Integer campeonato_equipe_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CEP FROM CampeonatoEquipePessoa CEP WHERE CEP.campeonatoEquipe.id = :campeonato_equipe_id ORDER BY CEP.pessoa.nome ASC");
            query.setParameter("campeonato_equipe_id", campeonato_equipe_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
    
    public List<CampeonatoEquipe> findByEquipe(Integer equipe_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CEP FROM CampeonatoEquipePessoa CEP WHERE CEP.campeonatoEquipe.equipe.id = :equipe_id ORDER BY CEP.pessoa.nome ASC");
            query.setParameter("equipe_id", equipe_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
