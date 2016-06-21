package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.FilialCidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class FilialCidadeDao extends DB {

    public FilialCidade pesquisaFilialCidade(int idFilial, int idCidade) {
        FilialCidade result = new FilialCidade();
        try {
            Query qry = getEntityManager().createQuery("select fc from FilialCidade fc"
                    + " where fc.cidade.id = " + idCidade
                    + "   and fc.filial.id = " + idFilial);
            if (!qry.getResultList().isEmpty()) {
                result = (FilialCidade) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public FilialCidade pesquisaFilialPorCidade(int idCidade) {
        FilialCidade result = new FilialCidade();
        try {
            Query qry = getEntityManager().createQuery("select fc from FilialCidade fc"
                    + " where fc.cidade.id = " + idCidade);
            if (!qry.getResultList().isEmpty()) {
                result = (FilialCidade) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List findListBy(Integer cidade_id) {
        return findListBy(cidade_id, null);
    }

    public List findListBy(Integer cidade_id, Boolean principal) {
        try {
            Query query;
            if (principal == null) {
                query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.cidade.id = :cidade_id ORDER BY FC.cidade.cidade ASC");
            } else {
                query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.cidade.id = :cidade_id AND FC.principal = :principal ORDER BY FC.cidade.cidade ASC");
                query.setParameter("principal", principal);
            }
            query.setParameter("cidade_id", cidade_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List findListByJuridica(Integer juridica_id, Boolean principal) {
        try {
            Query query;
            if (principal == null) {
                query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.filial.id = :juridica_id");
            } else {
                query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.filial.id = :juridica_id AND FC.principal = :principal");
                query.setParameter("principal", principal);
            }
            query.setParameter("juridica_id", juridica_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public FilialCidade findPrincipal(Integer cidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.cidade.id = :cidade_id AND FC.principal = :principal");
            query.setParameter("principal", true);
            query.setParameter("cidade_id", cidade_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (FilialCidade) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public FilialCidade find(Integer juridica_id, Integer cidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT FC FROM FilialCidade AS FC WHERE FC.cidade.id = :cidade_id AND FC.filial.filial.id = :juridica_id");
            query.setParameter("juridica_id", juridica_id);
            query.setParameter("cidade_id", cidade_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (FilialCidade) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
