package br.com.rtools.financeiro.dao;

import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.financeiro.Correcao;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.DescontoServicoEmpresaGrupo;
import br.com.rtools.financeiro.IndiceMensal;
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
        return new FindDao().findNotInByTabela(Servicos.class, "fin_servicos", new String[]{"id_departamento, ds_descricao"}, table, column, colum_filter_key, colum_filter_value, where);
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

    public Correcao pesquisaCorrecao(int idServico) {
        Correcao result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select c "
                    + "  from Correcao c"
                    + " where c.servicos.id = :pid");
            qry.setParameter("pid", idServico);
            result = (Correcao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public IndiceMensal pesquisaIndiceMensal(int mes, int ano, int idIndice) {
        IndiceMensal result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT I                   "
                    + "  FROM IndiceMensal AS I   "
                    + " WHERE I.mes = :mes        "
                    + "   AND I.ano = :ano        "
                    + "   AND I.indice.id = :i");
            qry.setParameter("mes", mes);
            qry.setParameter("ano", ano);
            qry.setParameter("i", idIndice);
            result = (IndiceMensal) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaTodos(int idRotina) {
        try {
            Query qry = getEntityManager().createQuery("SELECT S.servicos FROM ServicoRotina AS S WHERE S.rotina.id = :rotina ORDER BY S.servicos.descricao ASC");
            qry.setParameter("rotina", idRotina);
            return (qry.getResultList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList();
        }
    }

    public List pesquisaTodosPeloContaCobranca(int idRotina) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT SR.servicos "
                    + "  FROM ServicoRotina AS SR"
                    + " WHERE SR.rotina.id = :r"
                    + "   AND SR.servicos.id IN(SELECT S.servicos.id FROM ServicoContaCobranca AS S)");
            qry.setParameter("r", idRotina);
            return (qry.getResultList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List pesquisaPlano5() {
        try {
            Query qry = getEntityManager().createQuery(" SELECT P5 FROM Plano5 AS P5 ");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List pesquisaServicos(String desc, String por, String como, String situacao) {
        String textQuery = "";
        switch (como) {
            case "P":
                textQuery = "SELECT S.*                             "
                        + "    FROM fin_servicos AS S               "
                        + "  WHERE TRIM(UPPER(FUNC_TRANSLATE(S." + por + "))) LIKE TRIM(UPPER(FUNC_TRANSLATE('%" + desc + "%')))";
                break;
            case "I":
                textQuery = "SELECT S.*                             "
                        + "    FROM fin_servicos AS S               "
                        + "  WHERE TRIM(UPPER(FUNC_TRANSLATE(S." + por + "))) LIKE TRIM(UPPER(FUNC_TRANSLATE('" + desc + "%')))";
                break;
        }
        textQuery += " AND S.ds_situacao LIKE '" + situacao + "'";

        try {
            Query qry = getEntityManager().createNativeQuery(textQuery, Servicos.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Servicos idServicos(Servicos des_servicos) {
        Servicos result = null;
        // String descricao = des_servicos.getDescricao().toLowerCase().toUpperCase();
        try {
            String queryString = "SELECT S.* FROM fin_servicos AS S WHERE func_translate(UPPER(TRIM(S.ds_descricao))) LIKE func_translate(UPPER(TRIM('"+des_servicos.getDescricao()+"')))";
            Query query = getEntityManager().createNativeQuery(queryString, Servicos.class);
            // query.setParameter("d_servicos", descricao);
            result = (Servicos) query.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List<Servicos> listaServicoSituacao(int id_rotina, String situacao) {
        try {
            String queryString = ""
                    + "  SELECT S.servicos "
                    + "    FROM ServicoRotina AS S"
                    + "   WHERE S.rotina.id = " + id_rotina;

            if (situacao != null && !situacao.isEmpty()) {
                queryString += " AND S.servicos.situacao = '" + situacao + "'";
            }

            Query query = getEntityManager().createQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Servicos> listaServicoSituacaoAtivo() {
        try {
            Query qry = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.situacao = 'A' ORDER BY S.descricao ASC ");
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List<Servicos> listaServicosPorSubGrupoFinanceiro(Integer subgrupo) {
        return listaServicosPorSubGrupoFinanceiro(subgrupo, 4);
    }

    public List<Servicos> listaServicosPorSubGrupoFinanceiro(Integer subgrupo, Integer rotina) {
        try {
            Query query;
            if (rotina == 4) {
                query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.subGrupoFinanceiro.id = :subgrupo AND S.situacao = 'A' AND S.id NOT IN (SELECT SR.servicos.id FROM ServicoRotina AS SR WHERE SR.rotina.id = 4 GROUP BY SR.servicos.id) ");
            } else {
                query = getEntityManager().createQuery(" SELECT S FROM Servicos AS S WHERE S.subGrupoFinanceiro.id = :subgrupo AND S.situacao = 'A' AND S.id IN (SELECT SR.servicos.id FROM ServicoRotina AS SR WHERE SR.rotina.id = :rotina GROUP BY SR.servicos.id) ");
                query.setParameter("rotina", rotina);
            }
            query.setParameter("subgrupo", subgrupo);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }
}
