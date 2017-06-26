package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.beans.LancamentoFinanceiroBean.Filtro;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LoteDao extends DB {

    public List<Lote> find(Integer usuario_id, Filtro f) {

        List<String> list_where = new ArrayList();

        list_where.add("l.id_rotina = 231");

        list_where.add(
                "l.id IN ( \n "
                + "SELECT m.id_lote \n "
                + "  FROM fin_movimento m \n "
                // + " INNER JOIN fin_movimento_inativo mi ON mi.id_movimento = m.id \n "
                + " WHERE m.is_ativo = true \n "
                + "   AND m.id_lote = l.id \n "
                + " ) "
        );

        if (usuario_id != -1) {
            list_where.add("l.id_usuario = " + usuario_id);
        }

        switch (f.getPesquisaPor()) {
            case "ultimos60dias":
                String data = new DataHoje().decrementarMeses(2, DataHoje.data());
                list_where.add("l.dt_emissao >= '" + data + "'");
                break;
            case "emissao":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }

                list_where.add("l.dt_emissao = '" + f.getDescricao() + "'");
                break;
            case "lancamento":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }

                list_where.add("l.dt_lancamento = '" + f.getDescricao() + "'");
                break;
            case "documentoNF":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }

                list_where.add("l.ds_documento = '" + f.getDescricao() + "'");
                break;
            case "fornecedor":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }

                list_where.add("LOWER(TRANSLATE(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(f.getDescricao()) + "%'");
                break;
            case "cpf":
            case "cnpj":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }
                list_where.add("p.ds_documento = '" + f.getDescricao() + "'");
                break;
            case "valorNF":
                if (f.getDescricao().isEmpty()) {
                    return new ArrayList();
                }

                list_where.add("CAST((l.nr_valor * 100) AS int) = " + f.getDescricao().replace(",", "").replace(".", ""));
                break;
            default:
                break;
        }

        String WHERE = "";

        for (String w : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + w + " \n ";
            } else {
                WHERE += " AND " + w + " \n ";
            }
        }

        String ORDER_BY = " ORDER BY l.dt_emissao DESC";

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT l.* \n "
                    + "  FROM fin_lote l \n "
                    + " INNER JOIN pes_pessoa p ON p.id = l.id_pessoa \n "
                    + WHERE
                    + ORDER_BY, Lote.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Lote pesquisaCodigo(int id) {
        Lote result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Lote.pesquisaID");
            qry.setParameter("pid", id);
            result = (Lote) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public boolean insert(Lote lote) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(lote);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void commit() {
        getEntityManager().getTransaction().commit();
    }

    public void roolback() {
        getEntityManager().getTransaction().rollback();
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select p from Lote p ");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public int ultimoCodigo() {
        int result = 0;
        try {
            Query qry = getEntityManager().createQuery("select max(p.id) from Lote p ");
            result = (Integer) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List dependentesTransferencia(int idLote) {
        List lista = null;
        try {
            Query qry = getEntityManager().createQuery("select m from Movimento m where m.lote.id =" + idLote);
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List pesquisarLoteEsp(String desc, String por, String como) {
        List lista = null;
        String textQuery = null;
        if ((como.equals("T")) || (desc.equals(""))) {
            textQuery = "select lote from Lote lote where lote.rotina.id in (1,2)";
        } else if (como.equals("D")) {
            if (por.equals("q")) {
                textQuery = "select lote from Lote lote where lote.qtde = " + desc + " and lote.rotina.id in (1,2)";
            } else if (por.equals("t")) {
                textQuery = "select lote from Lote lote where lote.total = " + desc + " and lote.rotina.id in (1,2)";
            } else if (por.equals("d")) {
                textQuery = "select lote from Lote lote where lote.data = :desc and lote.rotina.id in (1,2)";
            }
        }
        try {
            Query qry = getEntityManager().createQuery(textQuery);
            if ((por.equals("d")) && (!(como.equals("T")))) {
                qry.setParameter("desc", desc);
            }
            lista = qry.getResultList();
        } catch (Exception e) {
            lista = null;
        }
        return lista;
    }

    public List pesquisarLoteTransferência(String desc, String por, String como) {
        List lista = null;
        String textQuery = null;
        if ((como.equals("T")) || (desc.equals(""))) {
            textQuery = "select lote from Lote lote where lote.rotina.id = 3";
        } else if (como.equals("D")) {
            if (por.equals("q")) {
                textQuery = "select lote from Lote lote where lote.qtde = " + desc + " and lote.rotina.id = 3";
            } else if (por.equals("t")) {
                textQuery = "select lote from Lote lote where lote.total = " + desc + " and lote.rotina.id = 3";
            } else if (por.equals("d")) {
                textQuery = "select lote from Lote lote where lote.data = :desc and lote.rotina.id = 3";
            }
        }
        try {
            Query qry = getEntityManager().createQuery(textQuery);
            if ((por.equals("d")) && (!(como.equals("T")))) {
                qry.setParameter("desc", desc);
            }
            lista = qry.getResultList();
        } catch (Exception e) {
            lista = null;
        }
        return lista;
    }

    public boolean delete(Lote lote) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(lote);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public Lote pesquisaLotePorEvt(Evt evt) {
        try {
            Query query = getEntityManager().createQuery(" SELECT L FROM Lote AS L WHERE L.evt.id = :idEvt");
            query.setParameter("idEvt", evt.getId());
            query.setMaxResults(1);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Lote) query.getSingleResult();
            }
        } catch (Exception e) {
            return new Lote();
        }
        return new Lote();

    }

    public Lote pesquisaLotePorEvt(int evt) {
        return pesquisaLotePorEvt(new Evt(evt));
    }

    public List<Lote> pesquisaLotesPorEvt(Evt evt) {
        try {
            Query query = getEntityManager().createQuery(" SELECT L FROM Lote AS L WHERE L.evt.id = :idEvt");
            query.setParameter("idEvt", evt.getId());
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();

    }

    public List<Lote> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT L FROM Lote AS L WHERE L.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }

    }

    public List<Lote> pesquisaLotesPorEvt(int evt) {
        return pesquisaLotesPorEvt(new Evt(evt));
    }

    public List<Lote> pesquisaLoteDocumento(Integer pessoa_id, Integer tipo_documento_id, String documento, Double valor) {

        if (documento.equals("S/N")) {
            return new ArrayList();
        }

        /*
                        "l.id IN ( \n "
                + "SELECT m.id_lote \n "
                + "  FROM fin_movimento m \n "
               // + " INNER JOIN fin_movimento_inativo mi ON mi.id_movimento = m.id \n "
                + " WHERE m.is_ativo = true \n "
                + "   AND m.id_lote = l.id \n "
                + " ) "
         */
        try {
            Query query = getEntityManager().createNativeQuery(
                    " SELECT l.* \n "
                    + "   FROM fin_lote l \n"
                    + "  WHERE l.id_pessoa = " + pessoa_id + " \n"
                    + "    AND l.ds_documento = '" + documento + "' \n"
                    + "    AND l.id_tipo_documento = " + tipo_documento_id + " \n"
                    + "    AND CAST((l.nr_valor * 100) AS int) = " + Integer.valueOf(valor.toString().replaceAll(",", "").replace(".", ""))
                    + "    AND l.id IN ( \n "
                        + "SELECT m.id_lote \n "
                        + "  FROM fin_movimento m \n "
                        + " WHERE m.is_ativo = true \n "
                        + "   AND m.id_lote = l.id \n "
                    + " ) ",
                    Lote.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }
}
