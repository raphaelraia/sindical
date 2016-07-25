package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PessoaEmpresaDao extends DB {

    public List listaPessoaEmpresaPorFisica(int id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT PE FROM PessoaEmpresa AS PE WHERE PE.fisica.id = :id AND PE.principal = false ORDER BY PE.dtAdmissao DESC ");
            qry.setParameter("id", id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listaPessoaEmpresaPorFisicaDemissao(int id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT PE FROM PessoaEmpresa AS PE WHERE PE.fisica.id = :id AND PE.principal = false AND PE.dtDemissao IS NULL ORDER BY PE.dtAdmissao DESC ");
            qry.setParameter("id", id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<PessoaEmpresa> listaPessoaEmpresaPorFisicaEmpresaDemissao(int id, int id_juridica) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT PE FROM PessoaEmpresa AS PE WHERE PE.fisica.id = :id AND PE.juridica.id = :id_empresa AND PE.principal = false AND PE.dtDemissao IS NULL ORDER BY PE.dtAdmissao DESC ");
            qry.setParameter("id", id);
            qry.setParameter("id_empresa", id_juridica);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listaPessoaEmpresaTodos(int id) {
        try {
            Query qry = getEntityManager().createQuery("select pesEmp "
                    + "  from PessoaEmpresa pesEmp"
                    + " where pesEmp.fisica.id = " + id);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public PessoaEmpresa pesquisaPessoaEmpresaPorFisica(int id) {
        try {
            Query qry = getEntityManager().createQuery(
                    "    SELECT PE                          "
                    + "    FROM PessoaEmpresa AS PE         "
                    + "   WHERE PE.fisica.id = " + id
                    + "     AND (PE.principal = true OR PE.dtDemissao IS NULL)"
            );
            List<PessoaEmpresa> list = qry.getResultList();
            PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
            if (!list.isEmpty() && list.size() > 1) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isPrincipal()) {
                        pessoaEmpresa = (PessoaEmpresa) list.get(i);
                        break;
                    }
                }
            } else if (!list.isEmpty() && list.size() == 1) {
                pessoaEmpresa = (PessoaEmpresa) list.get(0);
            }
            return pessoaEmpresa;
        } catch (Exception e) {
            return new PessoaEmpresa();
        }
    }

    public PessoaEmpresa pesquisaPessoaEmpresaPorPessoa(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery(
                    "  SELECT PE                                    "
                    + "  FROM PessoaEmpresa AS PE                   "
                    + " WHERE PE.fisica.pessoa.id = " + idPessoa
                    + "   AND (PE.principal = true OR PE.dtDemissao IS NULL)");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (PessoaEmpresa) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return new PessoaEmpresa();
    }

    public List<PessoaEmpresa> listaPessoaEmpresaPorJuridica(int id_juridica) {
        try {
            Query query = getEntityManager().createQuery(
                    "  SELECT PE                                    "
                    + "  FROM PessoaEmpresa AS PE                   "
                    + " WHERE PE.juridica.id = " + id_juridica
                    + "   AND (PE.principal = true OR PE.dtDemissao IS NULL)"
            );
            return query.getResultList();

        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findAllByFisica(Integer fisica_id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT PE FROM PessoaEmpresa AS PE WHERE PE.fisica.id = :fisica_id ORDER BY PE.dtAdmissao DESC ");
            qry.setParameter("fisica_id", fisica_id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
}
