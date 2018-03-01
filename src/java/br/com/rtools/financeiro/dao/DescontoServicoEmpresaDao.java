package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class DescontoServicoEmpresaDao extends DB {

    public Boolean existePessoaComDSE(Integer id_pessoa, Integer id_servico) {
        try {
            
            // -- SE EXISTIR NAO MOSTRAR PARCEIRO
            Query query = getEntityManager().createNativeQuery(
                    "SELECT p.codigo \n "
                    + "  FROM pes_pessoa_vw p \n "
                    + "  LEFT JOIN fin_desconto_servico_empresa dse ON dse.id_juridica = p.e_id AND dse.id_servico = " + id_servico + " \n "
                    + " WHERE p.codigo = " + id_pessoa + " \n "
                    + "   AND (dse.id IS NULL OR p.e_id IS NULL)"
            );

            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
            
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    public boolean existeDescontoServicoEmpresa(DescontoServicoEmpresa descontoServicoEmpresa) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.id = :idJuridica AND DSE.servicos.id = :idServicos ");
            query.setParameter("idJuridica", descontoServicoEmpresa.getJuridica().getId());
            query.setParameter("idServicos", descontoServicoEmpresa.getServicos().getId());
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List<DescontoServicoEmpresa> listaTodos() {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC ");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<DescontoServicoEmpresa> listaTodosPorEmpresa(int idJuridica) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.id = :idJuridica ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC ");
            query.setParameter("idJuridica", idJuridica);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<DescontoServicoEmpresa> pesquisaDescontoServicoEmpresas(String pesquisaPor, String descricao, String comoPesquisa) {
        Query query;
        if (pesquisaPor.equals("nome")) {
            String queryComoPesquisa;
            if (comoPesquisa.equals("I")) {
                queryComoPesquisa = " UPPER(DSE.juridica.pessoa.nome) LIKE '" + descricao.toUpperCase() + "%' OR UPPER(DSE.juridica.fantasia) LIKE '" + descricao.toUpperCase() + "%' ";
            } else {
                queryComoPesquisa = " UPPER(DSE.juridica.pessoa.nome) LIKE '%" + descricao.toUpperCase() + "%' OR UPPER(DSE.juridica.fantasia) LIKE '%" + descricao.toUpperCase() + "%' ";
            }
            query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE " + queryComoPesquisa + " ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC ");
        } else if (pesquisaPor.equals("cnpj")) {
            query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.pessoa.documento = '" + descricao + "'");
        } else {
            query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC LIMIT 100");
        }
        try {
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public DescontoServicoEmpresa pesquisaDescontoServicoEmpresa(DescontoServicoEmpresa descontoServicoEmpresa) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.id = :idJuridica AND DSE.servicos.id = :idServicos ");
            query.setParameter("idJuridica", descontoServicoEmpresa.getJuridica().getId());
            query.setParameter("idServicos", descontoServicoEmpresa.getServicos().getId());
            List list = query.getResultList();
            if (!list.isEmpty()) {
                descontoServicoEmpresa = (DescontoServicoEmpresa) query.getSingleResult();
            }
        } catch (Exception e) {
            return descontoServicoEmpresa;
        }
        return descontoServicoEmpresa;
    }

    public List<Servicos> listaTodosServicosDisponiveis(Integer id_empresa, Integer id_subgrupo_financeiro) {
        return listaTodosServicosDisponiveis(id_empresa, null, id_subgrupo_financeiro);
    }

    public List<Servicos> listaTodosServicosDisponiveis(Integer id_empresa, Integer id_grupo_financeiro, Integer id_subgrupo_financeiro) {
        try {
            Query query;
            if (id_grupo_financeiro != null) {
                query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.situacao = 'A' AND S.id NOT IN (SELECT DSE.servicos.id FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.id = :juridica ) AND S.subGrupoFinanceiro.grupoFinanceiro.id = :grupoFinanceiro ORDER BY S.descricao ASC");
                query.setParameter("grupoFinanceiro", id_grupo_financeiro);
            } else {
                query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.situacao = 'A' AND S.id NOT IN (SELECT DSE.servicos.id FROM DescontoServicoEmpresa AS DSE WHERE DSE.juridica.id = :juridica ) AND S.subGrupoFinanceiro.id = :subGrupoFinanceiro ORDER BY S.descricao ASC");
                query.setParameter("subGrupoFinanceiro", id_subgrupo_financeiro);
            }
            query.setParameter("juridica", id_empresa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<DescontoServicoEmpresa> findByGrupo(Integer grupo_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.servicos.id = :grupo_id ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC ");
            query.setParameter("grupo_id", grupo_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<DescontoServicoEmpresa> findByGrupo(Integer grupo_id, Integer servico_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.grupo.id = :grupo_id AND DSE.servicos.id = :servico_id ORDER BY DSE.juridica.pessoa.nome ASC, DSE.servicos.descricao ASC ");
            query.setParameter("grupo_id", grupo_id);
            query.setParameter("servico_id", servico_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public DescontoServicoEmpresa findByGrupo(Integer grupo_id, Integer servico_id, Integer empresa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT DSE FROM DescontoServicoEmpresa AS DSE WHERE DSE.grupo.id = :grupo_id AND DSE.servicos.id = :servico_id AND DSE.juridica.pessoa.id = :empresa_id ");
            query.setParameter("grupo_id", grupo_id);
            query.setParameter("servico_id", servico_id);
            query.setParameter("empresa_id", empresa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (DescontoServicoEmpresa) list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
