package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Parentesco;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ParentescoDao extends DB {

    private String sexo;
    private String not_in;

    public ParentescoDao() {
        this.sexo = "";
        not_in = "";
    }

    public List pesquisaTodosSemTitular() {
        try {
            Query qry = getEntityManager().createQuery("select p from Parentesco p "
                    + " where p.ativo = true "
                    + "   and p.id <> 1 order by p.id");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Parentesco> pesquisaTodosSemTitularCategoria(int id_categoria) {
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT sc.parentesco "
                    + "  FROM ServicoCategoria sc "
                    + " WHERE sc.categoria.id = " + id_categoria + " "
                    + "   AND sc.parentesco.id <> 1 "
                    + " ORDER BY sc.parentesco.id"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<Parentesco> pesquisaTodosSemTitularCategoriaSemDesconto(int id_categoria, int id_categoria_desconto) {
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT sc.parentesco "
                    + "  FROM ServicoCategoria sc "
                    + " WHERE sc.categoria.id = " + id_categoria + " "
                    + "   AND sc.parentesco.id <> 1 "
                    + "   AND sc.parentesco.id NOT IN (SELECT cdd.parentesco.id FROM CategoriaDescontoDependente cdd WHERE cdd.categoriaDesconto.id = " + id_categoria_desconto + ")"
                    + " ORDER BY sc.parentesco.id"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<Parentesco> pesquisaTodosComTitularCategoriaSemDesconto(int id_categoria, int id_categoria_desconto) {
        try {
            Query qry = getEntityManager().createQuery(
                    "     SELECT SC.parentesco                          "
                    + "     FROM ServicoCategoria AS SC                 "
                    + "    WHERE SC.categoria.id = " + id_categoria
                    + "      AND SC.parentesco.id NOT IN (SELECT CDD.parentesco.id FROM CategoriaDescontoDependente AS CDD WHERE CDD.categoriaDesconto.id = " + id_categoria_desconto + ")"
                    + " ORDER BY SC.parentesco.id                       "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Parentesco> pesquisaTodosSemTitularCategoriaSexo(int id_categoria, String sexo) {
        try {
            Query query = getEntityManager().createQuery(
                    "     SELECT SC.parentesco                      "
                    + "     FROM ServicoCategoria AS SC             "
                    + "    WHERE SC.categoria.id = " + id_categoria
                    + "      AND SC.parentesco.id <> 1              "
                    + "      AND SC.parentesco.sexo = '" + sexo + "'"
                    + " ORDER BY SC.parentesco.parentesco ASC       "
            );
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
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
        return findNotInByTabela(table, "id_parentesco", colum_filter_key, colum_filter_value);
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findNotInByTabela('seg_filial_rotina', 'id_filial', 1);
     *
     * @param table (Use alias T+column)
     * @param column
     * @param colum_filter_key Nome da coluna do filtro
     * @return Todas as rotinas não usadas em uma chave conforme o valor
     * @param colum_filter_value Valor do filtro
     */
    public List findNotInByTabela(String table, String column, String colum_filter_key, String colum_filter_value) {
        if (column == null || column.isEmpty()) {
            column = "id_parentesco";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        return new FindDao().findNotInByTabela(Parentesco.class, "soc_parentesco", new String[]{"ds_parentesco"}, table, column, colum_filter_key, colum_filter_value, "");
    }

    public List findBySexo(String sexo) {
        try {
            String queryString = ""
                    + "     SELECT P.* "
                    + "       FROM soc_parentesco AS P"
                    + "      WHERE P.ds_sexo = '" + sexo + "'";
            Query query = getEntityManager().createNativeQuery(queryString, Parentesco.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public String getNot_in() {
        return not_in;
    }

    public void setNot_in(String not_in) {
        this.not_in = not_in;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Parentesco find(String descricao, String sexo) {
        try {
            return (Parentesco) find(descricao, sexo, true).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean exists(String parentesco, String uf) {
        return find(parentesco, uf) != null;
    }

    public List find(String parentesco, String sexo, Boolean filter) {
        try {
            Query query;
            if (filter) {
                if (sexo == null) {
                    query = getEntityManager().createNativeQuery("SELECT P.* FROM soc_parentesco AS P WHERE TRIM(UPPER(FUNC_TRANSLATE(P.ds_parentesco))) = TRIM(UPPER(FUNC_TRANSLATE('" + parentesco + "')))", Parentesco.class);
                } else {
                    query = getEntityManager().createNativeQuery("SELECT P.* FROM soc_parentesco AS P WHERE TRIM(UPPER(FUNC_TRANSLATE(P.ds_parentesco))) = TRIM(UPPER(FUNC_TRANSLATE('" + parentesco + "'))) AND P.ds_sexo = '" + sexo.toUpperCase() + "'", Parentesco.class);
                }
            } else if (sexo == null) {
                query = getEntityManager().createNativeQuery("SELECT P.* FROM soc_parentesco AS P WHERE TRIM(UPPER(P.ds_parentesco)) = TRIM(UPPER('" + parentesco + "'))", Parentesco.class);
            } else {
                query = getEntityManager().createNativeQuery("SELECT P.* FROM soc_parentesco AS P WHERE TRIM(UPPER(P.ds_parentesco)) = TRIM(UPPER('" + parentesco + "')) AND P.ds_sexo = '" + sexo.toUpperCase() + "'", Parentesco.class);

            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
