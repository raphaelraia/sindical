package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Permissao;
import br.com.rtools.seguranca.UsuarioAcesso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PermissaoDao extends DB {

    public List pesquisaTodosAgrupados() {
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT per                        "
                    + "     FROM Permissao per              "
                    + "    WHERE per.evento.id = 1          "
                    + " ORDER BY per.modulo.descricao ASC,  "
                    + " per.rotina.rotina ASC               ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List pesquisaTodosAgrupadosPorModulo(int idModulo) {
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT per                        "
                    + "     FROM Permissao per              "
                    + "    WHERE per.evento.id = 1          "
                    + "      AND per.modulo.id = " + idModulo
                    + " ORDER BY per.modulo.descricao ASC,  "
                    + " per.rotina.rotina ASC               ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List findModuloGroup(Integer modulo_id) {
        try {
            Query qry = getEntityManager().createQuery(
                    "     SELECT P                              "
                    + "     FROM Permissao AS P                 "
                    + "    WHERE P.modulo.id = " + modulo_id
                    + " ORDER BY P.modulo.descricao ASC,        "
                    + "          P.rotina.rotina ASC            ");
            List<Permissao> listPermissao = qry.getResultList();
            List<Permissao> list = new ArrayList();
            if (!listPermissao.isEmpty()) {
                int m = 0;
                int r = 0;
                for (int i = 0; i < listPermissao.size(); i++) {
                    if (listPermissao.get(i).getRotina().getId() != r) {
                        list.add(listPermissao.get(i));
                        r = listPermissao.get(i).getRotina().getId();
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findModuloGroup(Integer modulo_id, String rotina_descricao) {
        try {
            Query qry = getEntityManager().createQuery(
                    "     SELECT P                              "
                    + "     FROM Permissao AS P                 "
                    + "    WHERE P.modulo.id = " + modulo_id
                    + "      AND UPPER(P.rotina.rotina) LIKE '%" + rotina_descricao.toUpperCase() + "%' "
                    + " ORDER BY P.modulo.descricao ASC,        "
                    + "          P.rotina.rotina ASC            ");
            List<Permissao> listPermissao = qry.getResultList();
            List<Permissao> list = new ArrayList();
            if (!listPermissao.isEmpty()) {
                int m = 0;
                int r = 0;
                for (int i = 0; i < listPermissao.size(); i++) {
                    if (listPermissao.get(i).getRotina().getId() != r) {
                        list.add(listPermissao.get(i));
                        r = listPermissao.get(i).getRotina().getId();
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPermissaoModRot(int idModulo, int idRotina) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT per                       "
                    + "   FROM Permissao per             "
                    + "  WHERE per.modulo.id = :idModulo "
                    + "    AND per.rotina.id = :idRotina ");
            qry.setParameter("idModulo", idModulo);
            qry.setParameter("idRotina", idRotina);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List listaModuloPermissaoAgrupado() {
        try {
            Query qry = getEntityManager().createQuery("SELECT per.modulo FROM Permissao per GROUP BY per.modulo ORDER BY per.modulo.descricao ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List listaRotinaPermissaoAgrupado(int idModulo) {
        try {
            Query qry = getEntityManager().createQuery("SELECT per.rotina FROM Permissao per WHERE per.modulo.id = :idModulo AND per.rotina.ativo = true GROUP BY per.rotina ORDER BY per.rotina.rotina ASC");
            qry.setParameter("idModulo", idModulo);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List listaEventoPermissaoAgrupado(int idModulo, int idRotina) {
        try {
            Query qry = getEntityManager().createQuery("SELECT per.evento FROM Permissao per WHERE per.modulo.id = :idModulo AND per.rotina.id = :idRotina AND per.rotina.ativo = true GROUP BY per.evento ORDER BY per.evento.descricao ASC");
            qry.setParameter("idModulo", idModulo);
            qry.setParameter("idRotina", idRotina);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaPermissaoModRotEve(int idModulo, int idRotina, int idEvento) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT per                       "
                    + "   FROM Permissao per             "
                    + "  WHERE per.modulo.id = :idModulo "
                    + "    AND per.rotina.id = :idRotina "
                    + "    AND per.evento.id = :idEvento ");
            qry.setParameter("idModulo", idModulo);
            qry.setParameter("idRotina", idRotina);
            qry.setParameter("idEvento", idEvento);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return qry.getResultList();
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Permissao pesquisaPermissaoModuloRotinaEvento(Integer modulo_id, Integer rotina_id, Integer evento_id) {
        Permissao permissao = new Permissao();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT P.* \n "
                    + " FROM seg_permissao AS P \n"
                    + "WHERE P.id_modulo = " + modulo_id
                    + "  AND P.id_rotina = " + rotina_id
                    + "  AND P.id_evento = " + evento_id,
                    Permissao.class
            );
            if (!qry.getResultList().isEmpty()) {
                permissao = (Permissao) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return permissao;
    }

    public List<UsuarioAcesso> listaUsuarioAcesso(int idUsuario, int idModulo, int idRotina, int idEvento) {
        String moduloString = "";
        String rotinaString = "";
        String eventoString = "";
        if (idModulo > 0) {
            moduloString = " AND ua.permissao.modulo.id = :idModulo ";
        }
        if (idRotina > 0) {
            rotinaString = " AND ua.permissao.rotina.id = :idRotina ";
        }
        if (idEvento > 0) {
            eventoString = " AND ua.permissao.evento.id = :idEvento ";
        }
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT ua                                     "
                    + "     FROM UsuarioAcesso ua                       "
                    + "    WHERE ua.usuario.id = :idUsuario             "
                    + moduloString
                    + rotinaString
                    + eventoString
                    + " ORDER BY ua.permissao.modulo.descricao ASC,         "
                    + "          ua.permissao.rotina.rotina ASC,            "
                    + "          ua.permissao.evento.descricao ASC");
            qry.setParameter("idUsuario", idUsuario);
            if (idModulo > 0) {
                qry.setParameter("idModulo", idModulo);
            }
            if (idRotina > 0) {
                qry.setParameter("idRotina", idRotina);
            }
            if (idEvento > 0) {
                qry.setParameter("idEvento", idEvento);
            }
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
