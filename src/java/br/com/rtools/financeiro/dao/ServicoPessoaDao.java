package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class ServicoPessoaDao extends DB {

    /**
     * Pesquisa Serviço Pessoa por id da Pessoa e id do Serviço
     *
     * @param id_pessoa
     * @param id_servico
     * @param is_ativo
     * @return
     */
    public ServicoPessoa pesquisaServicoPessoa(int id_pessoa, int id_servico, boolean is_ativo) {
        try {
            Query query = getEntityManager().createQuery("SELECT sp FROM ServicoPessoa sp WHERE sp.pessoa.id = " + id_pessoa + " AND sp.servicos.id = " + id_servico + " AND sp.ativo = " + is_ativo);
            query.setMaxResults(1);
            return (ServicoPessoa) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public List<ServicoPessoa> listaTodosServicoPessoaPorTitular(int id_titular_matricula) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT sp.* \n "
                    + "  FROM fin_servico_pessoa sp \n "
                    + " INNER JOIN soc_socios s ON s.id_servico_pessoa = sp.id \n "
                    + " INNER JOIN matr_socios m ON m.id = s.id_matricula_socios \n "
                    + " WHERE m.id_titular = " + id_titular_matricula, ServicoPessoa.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public ServicoPessoa pesquisaCodigo(int id) {
        ServicoPessoa result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("ServicoPessoa.pesquisaID");
            qry.setParameter("pid", id);
            result = (ServicoPessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select sp from ServicoPessoa sp");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public ServicoPessoa pesquisaServicoPessoaPorPessoa(int idPessoa) {
        ServicoPessoa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp "
                    + "  from ServicoPessoa sp"
                    + " where sp.pessoa.id = :pid"
                    + "   and sp.ativo = true");
            qry.setParameter("pid", idPessoa);
            result = (ServicoPessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List listByPessoa(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.pessoa.id = :pessoa AND SP.ativo = true");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listByPessoaInativo(int idPessoa) {
        try {
            //Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.pessoa.id = :pessoa AND SP.ativo = false ");
            Query query = getEntityManager().createQuery(" SELECT S.servicoPessoa FROM Socios S WHERE S.servicoPessoa.pessoa.id = :pessoa AND S.servicoPessoa.ativo = false ");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listByCobranca(int idCobranca) {
        try {
            Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.cobranca.id = :pessoa AND SP.ativo = true");
            query.setParameter("pessoa", idCobranca);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaTodosParaGeracao(String referencia) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp"
                    + "  from ServicoPessoa sp"
                    + " where sp.id in (select s.servicoPessoa.id from Socios s where s.matriculaSocios.dtInativo is null)"
                    + "  and CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) <= :ref"
                    + "  and ((CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) > :ref) or "
                    + "       (sp.referenciaValidade is null))");
            qry.setParameter("ref", referencia.substring(3, 7) + referencia.substring(0, 2));
            return (qry.getResultList());
        } catch (EJBQLException e) {
            e.getMessage();
            return null;
        }
    }

    public List pesquisaTodosParaGeracao(String referencia, int idPessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp"
                    + "  from Socios s join s.servicoPessoa sp  "
                    + " where s.matriculaSocios.dtInativo is null"
                    + "  and CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) <= :ref"
                    + "  and ((CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) > :ref) or "
                    + "       (sp.referenciaValidade is null))"
                    + "  and (   (sp.pessoa.id = :idPessoa)"
                    + "       or (s.matriculaSocios.pessoa.id = :idPessoa))");
            qry.setParameter("ref", referencia.substring(3, 7) + referencia.substring(0, 2));
            qry.setParameter("idPessoa", idPessoa);
            return (qry.getResultList());
        } catch (EJBQLException e) {
            e.getMessage();
            return null;
        }
    }

    public List listAllByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listAllByCobranca(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.cobranca.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listAllByCobrancaMovimento(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT SP FROM ServicoPessoa AS SP WHERE SP.cobrancaMovimento.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
}
