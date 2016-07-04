package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Profissao;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.SelectTranslate;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ProfissaoDao extends DB {

    public List<String> pesquisaProfissao(String des_tipo) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery("select prof.profissao from Profissao prof where prof.profissao like :texto");
            qry.setParameter("texto", des_tipo);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Profissao idProfissao(Profissao des_prof) {
        Profissao result = null;
        try {
            Query qry = getEntityManager().createQuery("select prof from Profissao prof where prof.profissao = :d_prof");
            qry.setParameter("d_prof", des_prof.getProfissao());
            result = (Profissao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaProfParametros(String por, String combo, String desc) {
        SelectTranslate st = new SelectTranslate();
        desc = (por.equals("I") ? desc + "%" : "%" + desc + "%");
        return st.select(new Profissao()).where("profissao", desc).find();
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findByTabela('matr_escola');
     *
     * @param table
     * @return Todas as filias da tabela específicada
     */
    public List findByTabela(String table) {
        return findByTabela(table, "id_profissao");
    }

    /**
     * Nome da tabela onde esta a lista de profissões Ex:
     * findByTabela('hom_agendamento');
     * findByTabela('hom_agendamento','id_funcao');
     *
     * @param table
     * @param id_column
     * @return Todas as filias da tabela específicada
     */
    public List findByTabela(String table, String id_column) {
        try {
            String queryString
                    = "     SELECT T1.* FROM pes_profissao AS T1                \n"
                    + "      WHERE T1.id IN (                                   \n"
                    + "	           SELECT T2." + id_column + "                  \n"
                    + "              FROM " + table + " AS T2                   \n"
                    + "          GROUP BY T2." + id_column + "                  \n"
                    + ")                                                        \n"
                    + " ORDER BY T1.ds_profissao ";
            Query query = getEntityManager().createNativeQuery(queryString, Profissao.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
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
        return findNotInByTabela(table, "id_filial", colum_filter_key, colum_filter_value, true);
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
            column = "id_profissao";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        return new FindDao().findNotInByTabela(Filial.class, "pes_profissao", new String[]{column}, table, column, colum_filter_key, colum_filter_value, "");
    }
}
