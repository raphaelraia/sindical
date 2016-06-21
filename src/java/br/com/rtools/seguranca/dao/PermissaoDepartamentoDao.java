package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Permissao;
import br.com.rtools.seguranca.PermissaoDepartamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PermissaoDepartamentoDao extends DB {

    public List pesquisaPermissaoDptoIdEvento(int id) {
        try {
            Query qry = getEntityManager().createQuery("select pd from PermissaoDepartamento pd "
                    + " where pd.id = " + id);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List pesquisaPermissaDisponivel(String ids) {
        String textQuery = "";
        try {
            if (ids.length() == 0) {
                textQuery = "select p "
                        + "  from Permissao p "
                        + " order by p.modulo.descricao";
            } else {
                textQuery = "select p "
                        + "  from Permissao p"
                        + " where p.id not in (select pd.permissao.id "
                        + "                      from PermissaoDepartamento pd"
                        + "                     where pd.id in (" + ids + "))"
                        + " order by p.modulo.descricao,"
                        + "          p.rotina.rotina";
            }

            Query qry = getEntityManager().createQuery(textQuery);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPermissaoAdc(int idDepto, int idNivel) {
        try {
            Query qry = getEntityManager().createQuery("select pd "
                    + "  from PermissaoDepartamento pd "
                    + " where pd.departamento.id = :idDepto "
                    + "   and pd.nivel.id = :idNivel "
                    + " order by pd.permissao.modulo.descricao,"
                    + "          pd.permissao.rotina.rotina");
            qry.setParameter("idDepto", idDepto);
            qry.setParameter("idNivel", idNivel);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPermissaDepto(String ids) {
        String textQuery = "";
        try {
            textQuery = "select pd "
                    + " from PermissaoDepartamento pd "
                    + "where pd.permissao.id in (" + ids + ")";
            Query qry = getEntityManager().createQuery(textQuery);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Permissao> listaPermissaoDepartamentoDisponivel(Integer idDepartamento, Integer idNivel, Integer evento_id, String descricaoPesquisa) {
        String queryFiltro = "";
        String[] descArray = null;
        if (!descricaoPesquisa.equals("")) {
            try {
                descArray = descricaoPesquisa.split(",");
            } catch (Exception e) {

            }
            if (descArray != null) {
                queryFiltro += " AND (";
                for (int i = 0; i < descArray.length; i++) {
                    if (i == 0) {
                        queryFiltro += " UPPER(P.rotina.rotina) LIKE '%" + descArray[i].toUpperCase().trim() + "%'";
                    } else {
                        queryFiltro += " OR UPPER(P.rotina.rotina) LIKE '%" + descArray[i].toUpperCase().trim() + "%'";
                    }
                }
                queryFiltro += " ) ";
            } else {
                queryFiltro = " AND UPPER(P.rotina.rotina) LIKE '%" + descricaoPesquisa.toUpperCase().trim() + "%'";
            }
        }
        if(evento_id != null) {
            queryFiltro += " AND P.evento.id = " + evento_id;
        }        
        try {
            Query query = getEntityManager().createQuery(" SELECT P FROM Permissao AS P WHERE P.id NOT IN ( SELECT PD.permissao.id FROM PermissaoDepartamento AS PD WHERE PD.departamento.id = " + idDepartamento + " AND PD.nivel.id = " + idNivel + ") " + queryFiltro + " ORDER BY P.modulo.descricao ASC, P.rotina.rotina ASC ");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();

    }

    public List<PermissaoDepartamento> listaPermissaoDepartamentoAdicionada(Integer idDepartamento, Integer idNivel, Integer evento_id, String descricaoPesquisa) {
        String queryFiltro = "";
        String[] descArray = null;
        if (!descricaoPesquisa.equals("")) {
            try {
                descArray = descricaoPesquisa.split(",");
            } catch (Exception e) {

            }
            if (descArray != null) {
                queryFiltro += " AND (";
                for (int i = 0; i < descArray.length; i++) {
                    if (i == 0) {
                        queryFiltro += " UPPER(PD.permissao.rotina.rotina) LIKE '%" + descArray[i].toUpperCase().trim() + "%'";
                    } else {
                        queryFiltro += " OR UPPER(PD.permissao.rotina.rotina) LIKE '%" + descArray[i].toUpperCase().trim() + "%'";
                    }
                }
                queryFiltro += " ) ";
            } else if (!descricaoPesquisa.equals("")) {
                queryFiltro = " AND UPPER(PD.permissao.rotina.rotina) LIKE '%" + descricaoPesquisa.toUpperCase() + "%'";
            }
        }
        if(evento_id != null) {
            queryFiltro += " AND PD.permissao.evento.id = " + evento_id;
        }
        try {
            Query query = getEntityManager().createQuery(" SELECT PD FROM PermissaoDepartamento AS PD WHERE PD.departamento.id = :idDepartamento AND PD.nivel.id = :idNivel " + queryFiltro + " ORDER BY PD.permissao.modulo.descricao ASC, PD.permissao.rotina.rotina ASC ");
            query.setParameter("idDepartamento", idDepartamento);
            query.setParameter("idNivel", idNivel);
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
