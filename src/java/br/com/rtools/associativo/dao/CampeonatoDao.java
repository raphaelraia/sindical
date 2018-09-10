package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Campeonato;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.beans.PessoaBean;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CampeonatoDao extends DB {

    public Campeonato exists(Integer evento_id, Integer modalidade_id, String descricao) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE C.id_evento = " + evento_id + " AND C.id_modalidade = " + modalidade_id + " AND func_translate(upper(trim(C.ds_titulo_complemento))) = func_translate(upper(trim('" + descricao + "')))", Campeonato.class);
            return (Campeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Campeonato> findByModalidade(Integer modalidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.modalidade.id = :modalidade_id ORDER BY C.tituloComplemento ASC");
            query.setParameter("modalidade_id", modalidade_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Campeonato> findBy(String in_modalidades, String situacao) {
        try {
            Query query;
            if (in_modalidades == null || in_modalidades.isEmpty()) {
                switch (situacao) {
                    case "ativo":
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.dtFim >= CURRENT_TIMESTAMP ORDER BY C.tituloComplemento ASC");
                        break;
                    case "finalizado":
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.dtFim < CURRENT_TIMESTAMP ORDER BY C.tituloComplemento ASC");
                        break;
                    default:
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C ORDER BY C.tituloComplemento ASC");
                        break;
                }
            } else {
                switch (situacao) {
                    case "ativo":
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.modalidade.id IN( " + in_modalidades + " ) AND C.dtFim >= CURRENT_TIMESTAMP ORDER BY C.tituloComplemento ASC");
                        break;
                    case "finalizado":
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.modalidade.id IN( " + in_modalidades + " ) AND C.dtFim < CURRENT_TIMESTAMP ORDER BY C.tituloComplemento ASC");
                        break;
                    default:
                        query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.modalidade.id IN( " + in_modalidades + " ) ORDER BY C.tituloComplemento ASC");
                        break;
                }
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Campeonato> findAll() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE C.dt_inicio > current_date ORDER BY C.dt_inicio ASC, C.ds_titulo_complemento ASC", Campeonato.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Campeonato> findAll(Boolean current) {
        try {
            Query query;
            if (current == null || current) {
                query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE current_date BETWEEN C.dt_inicio AND C.dt_fim ORDER BY C.dt_inicio DESC, C.ds_titulo_complemento ASC", Campeonato.class);
            } else {
                query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE C.dt_fim < current_date ORDER BY C.dt_inicio DESC, C.ds_titulo_complemento ASC", Campeonato.class);
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<ServicoPessoa> findToUpdate(Integer campeonato_id) {
        try {
            String queryString = ""
                    + "SELECT SP.* FROM fin_servico_pessoa  SP\n"
                    + "WHERE SP.id IN (\n"
                    + "SELECT id_servico_pessoa \n"
                    + "FROM matr_campeonato AS MC\n"
                    + "WHERE id_campeonato = " + campeonato_id + " \n"
                    + "UNION ALL\n"
                    + "SELECT ECD.id_servico_pessoa \n"
                    + "FROM eve_campeonato_dependente AS ECD\n"
                    + "INNER JOIN matr_campeonato AS MC ON MC.id = ECD.id_matricula_campeonato\n"
                    + "WHERE id_campeonato = " + campeonato_id + " \n"
                    + ")\n"
                    + "AND SP.is_ativo = true";
            Query query = getEntityManager().createNativeQuery(queryString, ServicoPessoa.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByPessoaDetalhes(Integer pessoa_id, Boolean ativas) {
        try {
            String queryString = ""
                    + "  SELECT modalidade,     \n "
                    + "         campeonato,     \n "
                    + "         equipe,         \n "
                    + "         inicio,         \n "
                    + "         fim,            \n "
                    + "         responsavel,    \n "
                    + "         valor,          \n "
                    + "         dependente,     \n "
                    + "         parentesco,     \n "
                    + "         valor_dependente \n"
                    + "    FROM campeonato_vw    \n"
                    + "    WHERE ( id_responsavel = " + pessoa_id + "  OR id_dependente =" + pessoa_id + " ) \n";
            if (ativas) {
                queryString += "AND inativacao  IS NULL";
            } else {
                queryString += "AND inativacao  IS NOT NULL";
            }
            queryString += " ORDER BY inicio DESC,  \n "
                    + "               modalidade,   \n "
                    + "               campeonato,   \n "
                    + "               equipe,       \n "
                    + "               inicio,       \n "
                    + "               fim,          \n "
                    + "               responsavel,  \n "
                    + "               dependente       ";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
