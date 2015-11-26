package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ServicosDao extends DB {

    private String situacao;
    private String order;

    public ServicosDao() {
        this.situacao = null;
        this.order = " S.ds_descricao ";
    }

    public ServicosDao(String situacao, String order) {
        this.situacao = situacao;
        this.order = order;
    }

    public List findByGrupoFinanceiro(String inIdGrupoFinanceiro) {
        try {
            String queryString;
            if (inIdGrupoFinanceiro.isEmpty()) {
                queryString = " "
                        + "     SELECT S.* FROM fin_servicos AS S                   \n";

            } else {
                queryString = " "
                        + "     SELECT S.*                                          \n"
                        + "       FROM fin_servicos AS S                            \n"
                        + " INNER JOIN fin_subgrupo AS SG ON SG.id = S.id_subgrupo  \n";

            }
            if (!inIdGrupoFinanceiro.isEmpty() || situacao != null) {
                queryString += " WHERE ";
            }
            if (!inIdGrupoFinanceiro.isEmpty()) {
                queryString += " SG.id_grupo IN (" + inIdGrupoFinanceiro + ") \n";
            }
            if (situacao != null) {
                if (situacao.equals("A")) {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                } else {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                }
            }
            if (!order.isEmpty()) {
                queryString += " ORDER BY " + order;
            }
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findBySubGrupoFinanceiro(String inIdSubGrupoFinanceiro) {
        try {
            String queryString;
            if (inIdSubGrupoFinanceiro.isEmpty()) {
                queryString = " "
                        + "     SELECT S.* FROM fin_servicos AS S \n";

            } else {
                queryString = " "
                        + "     SELECT S.* \n"
                        + "       FROM fin_servicos AS S \n";

            }
            if (!inIdSubGrupoFinanceiro.isEmpty() || situacao != null) {
                queryString += " WHERE ";
            }
            if (!inIdSubGrupoFinanceiro.isEmpty()) {
                queryString += " S.id_subgrupo IN (" + inIdSubGrupoFinanceiro + ") \n";
            }
            if (situacao != null) {
                if (situacao.equals("A")) {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                } else {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                }
            }
            if (!order.isEmpty()) {
                queryString += " ORDER BY " + order;
            }
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findBySubGrupoConvenio(String inIdSubGrupoConvenio) {
        try {
            String queryString;
            if (!inIdSubGrupoConvenio.isEmpty()) {
                queryString = " "
                        + "     SELECT S.* FROM fin_servicos AS S                                       \n"
                        + "      WHERE S.id IN (                                                        \n"
                        + "         SELECT id_servico                                                   \n"
                        + "           FROM soc_convenio_servico                                         \n"
                        + "          WHERE id_convenio_sub_grupo IN ( " + inIdSubGrupoConvenio + " )    \n"
                        + ")                                                                            \n";
            } else {
                queryString = " "
                        + "     SELECT S.* FROM fin_servicos AS S                                       \n"
                        + "      WHERE S.id IN (                                                        \n"
                        + "         SELECT id_servico                                                   \n"
                        + "           FROM soc_convenio_servico                                         \n"
                        + ")                                                                            \n";

            }
            if (situacao != null) {
                if (situacao.equals("A")) {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                } else {
                    queryString += " S.ds_situacao = " + situacao + " \n";
                }
            }
            if (!order.isEmpty()) {
                queryString += " ORDER BY " + order;
            }
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Servicos> findAll() {
        try {
            Query query;
            if (situacao != null) {
                if (situacao.equals("A")) {
                    query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.situacao = 'A' ORDER BY S.descricao ASC ");
                } else {
                    query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.situacao = 'I' ORDER BY S.descricao ASC ");
                }
            } else {
                query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S ORDER BY S.descricao ASC ");
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

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
        return findNotInByTabela(table, "id_servico", colum_filter_key, colum_filter_value, true);
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
            column = "id_servico";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        String where = "";
        if (situacao != null) {
            if (situacao.equals("A")) {
                where += " T1.ds_situacao = '" + situacao + "' \n";
            } else {
                where += " T1.ds_situacao = '" + situacao + "' \n";
            }
        }
        return new FindDao().findNotInByTabela(Servicos.class, "fin_servicos", new String[]{"id_departamento"}, table, column, colum_filter_key, colum_filter_value, where);
    }

    /**
     * Situaçao A ou I
     *
     * @return
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * Situaçao A ou I
     *
     * @param situacao
     */
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}
