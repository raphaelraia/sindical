package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class FolhaEmpresaDao extends DB {

    public List<FolhaEmpresa> findBy(String by, String description, String referencia, Integer tipo_servico_id) {
        try {
            QueryString qs = new QueryString();
            String queryString = " "
                    + "     SELECT FE.*                                         \n"
                    + "       FROM arr_faturamento_folha_empresa AS FE          \n"
                    + " INNER JOIN pes_juridica J ON J.id = FE.id_juridica      \n"
                    + " INNER JOIN pes_pessoa P ON P.id = J.id_pessoa           \n";
            if (!description.isEmpty()) {
                if (by.equals("nome")) {
                    qs.addWhere("UPPER(func_translate(P.ds_nome)) LIKE UPPER(func_translate('%" + description + "%'))");
                }
                if (by.equals("documento")) {
                    qs.addWhere("P.ds_documento = '" + description + "'");
                }
            }
            if (!referencia.isEmpty()) {
                qs.addWhere("FE.ds_referencia = '" + referencia + "'");
            }
            qs.addWhere("FE.id_tipo_servico = " + tipo_servico_id + "");
            queryString += qs.get();
            Query query = getEntityManager().createNativeQuery(queryString, FolhaEmpresa.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    public FolhaEmpresa pesquisaPorPessoa(int idPessoa, int idTipoServico, String referencia) {
        FolhaEmpresa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select f"
                    + "  from FolhaEmpresa f "
                    + " where f.referencia = :r"
                    + "   and f.tipoServico.id = :t"
                    + "   and f.juridica.pessoa.id = :p");
            qry.setParameter("p", idPessoa);
            qry.setParameter("t", idTipoServico);
            qry.setParameter("r", referencia);
            result = (FolhaEmpresa) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public List<FolhaEmpresa> findByJuridica(Integer juridica_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT FE FROM FolhaEmpresa AS FE WHERE FE.juridica.id = :juridica_id ORDER BY FE.tipoServico.descricao ASC ");
            query.setParameter("juridica_id", juridica_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    public FolhaEmpresa findBy(Integer juridica_id, Integer tipo_servico_id, String referencia) {
        try {
            Query query = getEntityManager().createQuery(" SELECT FE FROM FolhaEmpresa AS FE WHERE FE.juridica.id = :juridica_id AND FE.tipoServico.id = :tipo_servico_id AND FE.referencia = :referencia");
            query.setParameter("juridica_id", juridica_id);
            query.setParameter("tipo_servico_id", tipo_servico_id);
            query.setParameter("referencia", referencia);
            return (FolhaEmpresa) query.getSingleResult();
        } catch (Exception e) {
            return null;

        }
    }

    public Boolean capturaFolhaAnterior(String referencia) {
        try {
            DataHoje dh = new DataHoje();
            String referenciaOriginal = referencia;
            String dm = dh.decrementarMeses(1, DataHoje.converteData(DataHoje.converte("01/" + referencia)));
            referencia = DataHoje.converteDataParaReferencia(dm);
            Query query;
            String queryString = "SELECT * FROM arr_faturamento_folha_empresa  WHERE (nr_valor=0 or nr_valor IS NULL) AND ds_referencia='" + referenciaOriginal + "'\n";
            getEntityManager().getTransaction().begin();
            query = getEntityManager().createNativeQuery(queryString);
            if(!query.getResultList().isEmpty()) {
                query = getEntityManager().createNativeQuery("DELETE FROM arr_faturamento_folha_empresa  WHERE (nr_valor=0 or nr_valor IS NULL) AND ds_referencia='" + referenciaOriginal + "'");
                if (query.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
            }
            queryString = ""
                    + "INSERT INTO arr_faturamento_folha_empresa (ds_referencia,nr_num_funcionarios,nr_alteracoes,nr_valor,id_juridica,id_tipo_servico,dt_lancamento) \n"
                    + "( \n"
                    + "SELECT '" + referenciaOriginal + "',nr_num_funcionarios,nr_alteracoes,nr_valor,id_juridica,id_tipo_servico,dt_lancamento FROM arr_faturamento_folha_empresa \n"
                    + "WHERE ds_referencia='" + referencia + "' AND nr_valor > 0 AND id_juridica NOT IN (SELECT id_juridica FROM arr_faturamento_folha_empresa WHERE ds_referencia='" + referenciaOriginal + "') \n"
                    + ") ";
            query = getEntityManager().createNativeQuery(queryString);
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;

        }
    }

    public List findByNative(String by, String description, Integer servico_id, Integer tipo_servico_id, String referencia, String type_value) {
        QueryString qs = new QueryString();
        try {
            String queryString = ""
                    + "    SELECT ds_documento  AS cnpj,                        \n"
                    + "           ds_nome       AS nome,                        \n"
                    + "           D.nr_valor as valor_folha,                    \n"
                    + "           func_arr_calcula_valor_boleto(c.id_pessoa, " + servico_id + ",'" + referencia + "', nr_valor, D.nr_num_funcionarios) AS valor_boleto,       \n"
                    + "           DE.id_servicos,                               \n"
                    + "           D.id,                                         \n"
                    + "           D.nr_num_funcionarios,                        \n"
                    + "           C.id_juridica AS juridica_id                  \n"
                    + "      FROM arr_contribuintes_vw AS C                     \n"
                    + "INNER JOIN arr_desconto_empregado AS DE ON DE.id_grupo_cidade = C.id_grupo_cidade AND DE.id_convencao = C.id_convencao AND DE.id_servicos = " + servico_id + " \n"
                    + "       AND date('01/'||'" + referencia + "') >= date('01/'||ds_ref_inicial)  \n"
                    + "       AND date('01/'||'" + referencia + "') <= date('01/'||ds_ref_final)    \n";
            queryString += " LEFT JOIN arr_faturamento_folha_empresa AS D ON D.id_juridica = C.id_juridica AND D.ds_referencia ='" + referencia + "' \n";
            if (!description.isEmpty()) {
                if (by.equals("nome")) {
                    qs.addWhere("UPPER(func_translate(ds_nome)) LIKE UPPER(func_translate('%" + description + "%'))");
                }
                if (by.equals("cpf") || by.equals("cnpj")) {
                    qs.addWhere("ds_documento = '" + description + "'");
                }
            }
            if (type_value.equals("com")) {
                qs.addWhere("(D.nr_valor IS NOT NULL AND D.nr_valor <> 0)");
            } else if (type_value.equals("sem")) {
                qs.addWhere("(D.nr_valor IS NULL OR D.nr_valor = 0)");
            }
            qs.addWhere("C.dt_inativacao IS NULL");
            queryString += qs.get();
            queryString += " ORDER BY ds_nome ";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }
}
