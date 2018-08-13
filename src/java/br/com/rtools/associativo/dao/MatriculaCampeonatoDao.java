package br.com.rtools.associativo.dao;

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

    public List<MatriculaCampeonato> findByEquipe(Integer equipe_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.equipe.id = :equipe_id ORDER BY MC.servicoPessoa.pessoa.nome ASC");
            query.setParameter("equipe_id", equipe_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public MatriculaCampeonato existsInCampeonato(Integer campeonato_id, Integer pessoa_id) {
        return existsInCampeonato(campeonato_id, pessoa_id, Boolean.TRUE);
    }

    public MatriculaCampeonato existsInCampeonato(Integer campeonato_id, Integer pessoa_id, Boolean ativo) {
        try {
            Query query;
            if (ativo == null) {
                query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonato.id = :campeonato_id AND MC.servicoPessoa.pessoa.id = :pessoa_id");
            } else {
                if (ativo) {
                    query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonato.id = :campeonato_id AND MC.servicoPessoa.pessoa.id = :pessoa_id AND MC.servicoPessoa.ativo = true");
                } else {
                    query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonato.id = :campeonato_id AND MC.servicoPessoa.pessoa.id = :pessoa_id AND MC.servicoPessoa.ativo = false");
                }
            }
            query.setParameter("campeonato_id", campeonato_id);
            query.setParameter("pessoa_id", pessoa_id);
            return (MatriculaCampeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public MatriculaCampeonato findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MC FROM MatriculaCampeonato MC WHERE MC.campeonatoEquipe.id = :campeonato_equipe_id AND MC.campeonato.id = :campeonato_id AND MC.servicoPessoa.pessoa.id = :pessoa_id AND MC.dtInativacao IS NULL");
            query.setParameter("pessoa_id", pessoa_id);
            return (MatriculaCampeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public MatriculaCampeonato findByPessoaSuspensa(Integer pessoa_id) {
        try {
            String queryString = ""
                    + "    SELECT *                                             \n"
                    + "      FROM matr_campeonato AS MC                         \n"
                    + "INNER JOIN fin_servico_pessoa AS SP ON SP.id = MC.id_servico_pessoa \n"
                    + "     WHERE MC.dt_suspensao_fim > current_date AND MC.dt_suspensao_fim IS NOT NULL \n"
                    + "       AND SP.id_pessoa = " + pessoa_id;
            Query query = getEntityManager().createNativeQuery(queryString, MatriculaCampeonato.class);
            query.setParameter("pessoa_id", pessoa_id);
            return (MatriculaCampeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<MatriculaCampeonato> findAllSuspensos() {
        String queryString = ""
                + "     SELECT *                                                \n"
                + "      FROM matr_campeonato AS MC                             \n"
                + "INNER JOIN fin_servico_pessoa AS SP ON SP.id = MC.id_servico_pessoa \n"
                + "INNER JOIN pes_pessoa AS P ON P.id = SP.id_pessoa            \n"
                + "     WHERE MC.dt_suspensao_fim > current_date                \n"
                + "       AND MC.dt_suspensao_fim IS NOT NULL                   \n"
                + "  ORDER BY P.ds_nome                                         \n";
        Query query = getEntityManager().createNativeQuery(queryString, MatriculaCampeonato.class);
        return query.getResultList();
    }

}
