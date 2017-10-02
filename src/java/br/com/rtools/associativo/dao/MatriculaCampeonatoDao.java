package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CampeonatoEquipe;
import br.com.rtools.associativo.MatriculaCampeonato;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class MatriculaCampeonatoDao extends DB {

    public MatriculaCampeonato exists(Integer campeonato_equipe_id, Integer campeonato_id, Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.id = :campeonato_equipe_id AND MC.campeonato.id = :campeonato_id AND MC.servicoPessoa.pessoa.id = :pessoa_id AND MC.dtInativacao IS NULL");
            query.setParameter("campeonato_equipe_id", campeonato_equipe_id);
            query.setParameter("campeonato_id", campeonato_id);
            query.setParameter("pessoa_id", pessoa_id);
            return (MatriculaCampeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<MatriculaCampeonato> findByCampeonatoEquipe(Integer campeonato_equipe_id) {
        return findByCampeonatoEquipe(campeonato_equipe_id, true);
    }

    public List<MatriculaCampeonato> findByCampeonatoEquipe(Integer campeonato_equipe_id, Boolean somente_ativas) {
        try {
            Query query;
            if (somente_ativas == null) {
                query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.id = :campeonato_equipe_id ORDER BY MC.servicoPessoa.pessoa.nome ASC");
            } else if (somente_ativas) {
                query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.id = :campeonato_equipe_id AND MC.dtInativacao IS NULL ORDER BY MC.servicoPessoa.pessoa.nome ASC");
            } else {
                query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.id = :campeonato_equipe_id AND MC.dtInativacao IS NOT NULL ORDER BY MC.servicoPessoa.pessoa.nome ASC");
            }
            query.setParameter("campeonato_equipe_id", campeonato_equipe_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }


    }

    public List<CampeonatoEquipe> findByEquipe(Integer equipe_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.equipe.id = :equipe_id ORDER BY MC.servicoPessoa.pessoa.nome ASC");
            query.setParameter("equipe_id", equipe_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
