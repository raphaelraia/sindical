package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioGrupo;
import br.com.rtools.relatorios.RelatorioJoin;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Rotina;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioDao extends DB {

    public List findByRotina(Integer rotina_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM Relatorios AS R WHERE R.rotina.id = :rotina_id ORDER BY R.nome");
            query.setParameter("rotina_id", rotina_id);
            return query.getResultList();
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Relatorios findDefaultByRotina(Integer rotina_id, Boolean principal) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM Relatorios AS R WHERE R.rotina.id = :rotina_id AND R.principal = :principal");
            query.setParameter("rotina_id", rotina_id);
            query.setParameter("principal", principal);
            query.setMaxResults(1);
            return (Relatorios) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public Relatorios findByJasper(String relatorio_jasper) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM Relatorios AS R WHERE R.jasper LIKE :relatorio_jasper");
            query.setParameter("relatorio_jasper", relatorio_jasper);
            return (Relatorios) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean defineDefault(Relatorios r) {
        if (r.getId() == -1) {
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            Query query = getEntityManager().createNativeQuery("UPDATE sis_relatorios SET is_default = false WHERE id_rotina = " + r.getRotina().getId());
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            if (r.getPrincipal()) {
                query = getEntityManager().createNativeQuery("UPDATE sis_relatorios SET is_default = true WHERE id = " + r.getId());
                if (query.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
            }
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    public List<Relatorios> pesquisaTipoRelatorio(Integer idRotina) {
        List<Relatorios> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.rotina.id = :idRotina"
                    + " order by rel.nome");
            qry.setParameter("idRotina", idRotina);
            result = qry.getResultList();
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public List pesquisaCidadesRelatorio() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select pes.endereco.cidade "
                    + "  from PessoaEndereco pes"
                    + " where pes.tipoEndereco.id = 2"
                    + " group by pes.endereco.cidade"
                    + " order by pes.endereco.cidade.cidade");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Relatorios pesquisaRelatorios(int idRelatorio) {
        Relatorios result = null;
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.id = :idRelatorio");
            qry.setParameter("idRelatorio", idRelatorio);
            result = (Relatorios) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Relatorios pesquisaRelatoriosPorJasper(String dsJasper) {
        Relatorios result = null;
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.jasper = :jasper");
            qry.setParameter("jasper", dsJasper);
            result = (Relatorios) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List<Rotina> pesquisaRotina() {
        List<Rotina> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rot from Rotina rot where rot.rotina like 'RELATÓRIO%'");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<Relatorios> pesquisaTodosRelatorios() {
        List<Relatorios> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT R FROM Relatorios AS R ORDER BY R.nome ASC ");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<RelatorioParametros> listaRelatorioParametro(Integer id_relatorio) {
        List<RelatorioParametros> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT r FROM RelatorioParametros AS r WHERE r.relatorio.id = " + id_relatorio + " ORDER BY r.id");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<RelatorioGrupo> listaRelatorioGrupo(Integer id_relatorio) {
        List<RelatorioGrupo> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT r FROM RelatorioGrupo AS r WHERE r.relatorio.id = " + id_relatorio + " ORDER BY r.id");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<RelatorioJoin> listaRelatorioJoin(Integer id_relatorio) {
        List<RelatorioJoin> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT r FROM RelatorioJoin AS r WHERE r.relatorio.id = " + id_relatorio + " ORDER BY r.id");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<Relatorios> find(Integer rotina_id, String description) {
        try {
            String queryString = " -- RelatorioDao().find()                   \n\n"
                    + "     SELECT R.*                                          \n"
                    + "       FROM sis_relatorios AS R                          \n";
            List listWhere = new ArrayList();
            if (rotina_id != null) {
                listWhere.add("R.id_rotina = " + rotina_id);
            }
            if (!description.isEmpty()) {
                listWhere.add("func_translate(UPPER(R.ds_nome)) LIKE func_translate(UPPER('%" + description + "%'))");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";

                }
            }
            queryString += " ORDER BY R.ds_nome ASC ";
            Query query = getEntityManager().createNativeQuery(queryString, Relatorios.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
