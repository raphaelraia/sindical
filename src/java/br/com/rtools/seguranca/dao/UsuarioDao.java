package br.com.rtools.seguranca.dao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class UsuarioDao extends DB {

    public List<Usuario> pesquisaTodosPorDescricao(String descricaoPesquisa) {
        try {
            Query qry = getEntityManager().createQuery("SELECT usu FROM Usuario AS USU WHERE UPPER(USU.pessoa.nome) LIKE '%" + descricaoPesquisa.toUpperCase() + "%' OR UPPER(USU.login) LIKE '%" + descricaoPesquisa.toUpperCase() + "%' ORDER BY USU.pessoa.nome ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Pessoa ValidaUsuarioWeb(String login, String senha) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes"
                    + "  from Pessoa pes"
                    + " where pes.login = :log"
                    + "   and pes.senha = :sen");
            qry.setParameter("log", login);
            qry.setParameter("sen", senha);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (Pessoa) qry.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Pessoa validaUsuarioWebCpfNascimento(String cpf, String nascimento) {
        try {
            Query query = getEntityManager().createQuery("SELECT F.pessoa FROM Fisica F WHERE F.pessoa.documento = :cpf AND F.dtNascimento = :nascimento");
            query.setParameter("cpf", cpf);
            Date d = DataHoje.converte(nascimento);
            query.setParameter("nascimento", d, TemporalType.DATE);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Pessoa) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Usuario ValidaUsuario(String login, String senha) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT usu"
                    + "   FROM Usuario usu"
                    + "  WHERE usu.login = :log"
                    + "    AND usu.senha = :sen");
            qry.setParameter("log", login);
            qry.setParameter("sen", senha);
            List list = qry.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                Usuario usuario = (Usuario) qry.getSingleResult();
                if (usuario.getAtivo()) {
                    return usuario;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Usuario ValidaUsuarioSuporteWeb(String login, String senha) {
        Usuario result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select usu"
                    + "  from Usuario usu"
                    + " where usu.login = :log"
                    + "   and usu.senha = :sen");
            qry.setParameter("log", login);
            qry.setParameter("sen", senha);
            result = (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaLogin(String login, int idPessoa) {
        List result = new ArrayList();
        String descricao = login.toLowerCase().toUpperCase();
        try {
            Query qry = getEntityManager().createQuery("select usu from Usuario usu where UPPER(usu.login) = :d_usuario"
                    + "    or usu.pessoa.id = :idPessoa");
            qry.setParameter("d_usuario", descricao);
            qry.setParameter("idPessoa", idPessoa);
            result = qry.getResultList();
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public Pessoa ValidaUsuarioContribuinteWeb(int idPessoa) {
        Pessoa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes "
                    + "  from Pessoa pes, "
                    + "       Juridica jur, "
                    + "       CnaeConvencao cnaeCon "
                    + " where pes.id = :idPes"
                    + "   and jur.pessoa.id = pes.id "
                    + "   and cnaeCon.cnae.id = jur.cnae.id");
            qry.setParameter("idPes", idPessoa);
            result = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Pessoa ValidaUsuarioContabilidadeWeb(int idPessoa) {
        Pessoa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes "
                    + "  from Pessoa pes, "
                    + "       Juridica jur "
                    + " where pes.id = :idPes"
                    + "   and jur.pessoa.id = pes.id"
                    + "   and jur.id in (select j.contabilidade.id from Juridica j where j.contabilidade.id is not null)");
            qry.setParameter("idPes", idPessoa);
            result = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Pessoa ValidaUsuarioPatronalWeb(int idPessoa) {
        Pessoa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes "
                    + "  from Pessoa pes "
                    + " where pes.id in (select patro.pessoa.id from Patronal patro where patro.pessoa.id = :idPes )");
            qry.setParameter("idPes", idPessoa);
            result = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Usuario ValidaUsuarioContribuinte(int idUsuario) {
        Usuario result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select usu "
                    + "  from Usuario usu, "
                    + "       Pessoa pes, "
                    + "       Juridica jur, "
                    + "       Contribuintes con "
                    + " where usu.id = :idUser "
                    + "   and usu.pessoa.id = pes.id "
                    + "   and jur.pessoa.id = pes.id "
                    + "   and con.juridica.id = jur.id");
            qry.setParameter("idUser", idUsuario);
            result = (Usuario) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Usuario ValidaUsuarioContabilidade(int idUsuario) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select usu "
                    + "  from Usuario usu, "
                    + "       Pessoa pes, "
                    + "       Juridica jur "
                    + " where usu.id = :idUser "
                    + "   and usu.pessoa.id = pes.id "
                    + "   and jur.pessoa.id = pes.id "
                    + "   and jur.id in ( select j.contabilidade.id "
                    + "                     from Juridica j "
                    + "                    where j.contabilidade.id is not null )");
            qry.setParameter("idUser", idUsuario);
            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAcordoMovimento() {
        try {
            Query qry = getEntityManager().createNativeQuery("update fin_movimento set nr_ativo=0, nr_acordado=1 where nr_ativo = 1 and id_acordo > 0 and id_tipo_servico <> 4");
            getEntityManager().getTransaction().begin();
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
    }

    public Usuario pesquisaUsuarioPorPessoa(int id_pessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select usu from Usuario usu where USU.pessoa.id = " + id_pessoa);
            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findNotInByTabela('matr_escola');
     *
     * @param table (Use alias T+colum
     * @param colum_filter_key Nome da coluna do filtro
     * @return Todas as rotinas da tabela específicada
     * @param colum_filter_value Valor do filtro
     */
    public List findNotInByTabela(String table, String colum_filter_key, String colum_filter_value) {
        return findNotInByTabela(table, "id", colum_filter_key, colum_filter_value, true);
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findNotInByTabela('seg_filial_rotina', 'id_filial', 1);
     *
     * @param table (Use alias T+colum)
     * @param column
     * @param colum_filter_key Nome da coluna do filtro
     * @return Todas as rotinas não usadas em uma chave conforme o valor
     * @param colum_filter_value Valor do filtro
     * @param is_ativo default null
     */
    public List findNotInByTabela(String table, String column, String colum_filter_key, String colum_filter_value, Boolean is_ativo) {
        if (column == null || column.isEmpty()) {
            column = "id_usuario";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        return new FindDao().findNotInByTabela(Usuario.class, "seg_usuario", new String[]{"id"}, table, column, colum_filter_key, colum_filter_value, "");
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findByTabela('matr_escola');
     *
     * @param table
     * @return Todas as filias da tabela específicada
     */
    public List findByTabela(String table) {
        return findByTabela(table, "id_usuario");
    }

    public List findByTabela(String table, String column) {
        if (column == null || column.isEmpty()) {
            column = "id_usuario";
        }
        try {
            String queryString
                    = "     SELECT T1.* FROM seg_usuario AS T1                  \n"
                    + " INNER JOIN pes_pessoa AS T2 ON T2.id = T1.id_pessoa     \n"
                    + "      WHERE T1.id IN (                                   \n"
                    + "	           SELECT T4." + column + "                     \n"
                    + "              FROM " + table + " AS T4                   \n"
                    + "          GROUP BY T4." + column + "                     \n"
                    + ")                                                        \n"
                    + " ORDER BY T2.ds_nome ";
            Query query = getEntityManager().createNativeQuery(queryString, Usuario.class);
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
