package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.principal.DB;
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
