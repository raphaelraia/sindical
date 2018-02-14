package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.AcordoComissao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class AcordoComissaoDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public AcordoComissaoDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public AcordoComissaoDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List<AcordoComissao> pesquisaData(String data) {
        try {
            Query query = getEntityManager().createQuery(
                    "select a"
                    + "  from AcordoComissao a"
                    + " where a.dtFechamento = :data");
            query.setParameter("data", DataHoje.converte(data));
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaTodos() {
        try {
            Query query = getEntityManager().createQuery("select a from AcordoComissao a ");
            return (query.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List<Date> pesquisaTodosFechamento() {
        try {
            Query query = getEntityManager().createQuery(
                    "select a.dtFechamento "
                    + "  from AcordoComissao a"
                    + " group by a.dtFechamento"
                    + " order by a.dtFechamento desc");
            return (query.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean inserirAcordoComissao() {
        try {
            getEntityManager().getTransaction().begin();
            String textQuery
                    = " INSERT INTO arr_acordo_comissao (dt_inicio,dt_fechamento,nr_num_documento,id_conta_cobranca,id_acordo)  \n"
                    + "            (                                                                                            \n"
                    + "                SELECT (SELECT CASE WHEN MAX(dt_fechamento) IS NULL THEN (SELECT MIN(dt_data) FROM arr_acordo) ELSE (MAX(dt_fechamento) + 1) END FROM arr_acordo_comissao ) dt_inicio, \n"
                    + "                       '" + DataHoje.data() + "' AS dt_fechamento,                                           \n"
                    + "                       M.ds_documento BOLETO,                                                                \n"
                    + "                       BO.id_conta_cobranca,                                                                 \n"
                    + "                       M.id_acordo                                                                           \n"
                    + "                  FROM fin_movimento         AS M                                                            \n"
                    + "            INNER JOIN pes_pessoa            AS P  ON P.id  = M.id_pessoa                                    \n"
                    + "            INNER JOIN fin_baixa             AS LB ON LB.id = M.id_baixa                                     \n"
                    + "            INNER JOIN fin_servicos          AS SE ON SE.id = M.id_servicos                                  \n"
                    + "            INNER JOIN fin_boleto            AS BO ON BO.nr_ctr_boleto = M.nr_ctr_boleto                     \n"
                    + "            INNER JOIN fin_conta_cobranca    AS CC ON CC.id = BO.id_conta_cobranca                           \n"
                    + "                 WHERE M.id_tipo_servico = 4                                                                 \n"
                    + "                   AND M.id_servicos <> 8                                                                    \n"
                    + "                   AND LB.dt_baixa > '01/08/2010'                                                            \n"
                    + "                   AND M.is_ativo = true                                                                     \n"
                    + "                   AND M.id_acordo > 0                                                                       \n"
                    + "                   AND 'C'|| BO.id_conta_cobranca ||'D'||M.ds_documento NOT IN (SELECT 'C'||                 \n"
                    + "                                                                                      id_conta_cobranca||    \n"
                    + "                                                                                      'D'||                  \n"
                    + "                                                                                      nr_num_documento       \n"
                    + "                                                                                 FROM arr_acordo_comissao    \n"
                    + "                  )                                                                                          \n"
                    + ") ";
            Query query = getEntityManager().createNativeQuery(textQuery);
            query.executeUpdate();
            getEntityManager().getTransaction().commit();
            new NovoLog().live("Fechamento de Acordo: " + DataHoje.data());
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean estornarAcordoComissao(String data) {
        try {
            getEntityManager().getTransaction().begin();
            String textQuery
                    = " DELETE FROM arr_acordo_comissao WHERE dt_fechamento= \'" + data + "\' ";
            Query query = getEntityManager().createNativeQuery(textQuery);
            query.executeUpdate();
            getEntityManager().getTransaction().commit();
            new NovoLog().live("Estorno de Acordo: " + DataHoje.data());
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public List listaAcordoComissao(String data, String in_usuarios) {
        try {
            String textQuery
                    = "     SELECT DISTINCT ON (BO.id_conta_cobranca, M.ds_documento, P.ds_nome, PU.ds_nome)\n"
                    + "            P.ds_documento   AS cnpj,                    \n" // 0
                    + "            P.ds_nome        AS empresa,                 \n" // 1
                    + "            M.id_acordo      AS acordo_id,               \n" // 2
                    + "            M.ds_documento   AS boleto,                  \n" // 3
                    + "            SE.ds_descricao  AS contribuicao,            \n" // 4
                    + "            M.nr_valor_baixa AS valor_recebido,          \n" // 5
                    + "            M.nr_taxa        AS taxa,                    \n" // 6
                    + "            CC.nr_repasse    AS repasse,                 \n" // 7
                    + "            LB.dt_importacao AS data_importacao,         \n" // 8
                    + "            LB.dt_baixa      AS data_recebimento,        \n" // 9
                    + "            ACC.dt_fechamento AS data_fechamento,        \n" // 10
                    + "            M.dt_vencimento  AS data_vencimento,         \n" // 11
                    + "            ACC.dt_inicio    AS data_inicio,             \n" // 12
                    + "            L.dt_emissao     AS data_emissao,            \n" // 13
                    + "            cast(0 AS double precision) AS comissao,                \n" // 14
                    + "            cast(0 AS double precision) AS liquido,                 \n" // 15
                    + "            PU.ds_nome       AS usuario_nome             \n" // 16
                    + "       FROM fin_movimento    AS M                        \n"
                    + " INNER JOIN pes_pessoa       AS P  ON P.id   = M.id_pessoa   \n"
                    + " INNER JOIN fin_baixa        AS LB ON LB.id  = M.id_baixa    \n"
                    + " INNER JOIN fin_servicos     AS SE ON SE.id  = M.id_servicos \n"
                    + " INNER JOIN fin_boleto       AS BO ON BO.nr_ctr_boleto = M.nr_ctr_boleto                 \n"
                    + " INNER JOIN fin_conta_cobranca AS CC ON CC.id = BO.id_conta_cobranca                     \n"
                    + " INNER JOIN arr_acordo_comissao AS ACC ON BO.id_conta_cobranca = ACC.id_conta_cobranca   \n"
                    + "                                      AND M.ds_documento = ACC.nr_num_documento          \n"
                    + "                                      AND dt_fechamento  = '" + data + "'                \n"
                    + " INNER JOIN arr_acordo       AS AC ON AC.id  = ACC.id_acordo \n"
                    + " INNER JOIN seg_usuario      AS U ON U.id    = AC.id_usuario \n"
                    + " INNER JOIN pes_pessoa       AS PU ON PU.id  = U.id_pessoa   \n"
                    + " INNER JOIN fin_lote         AS L ON L.id    = M.id_lote     \n"
                    + "      WHERE M.id_tipo_servico = 4                        \n"
                    + "        AND M.id_servicos <> 8                           \n"
                    + "        AND LB.dt_baixa > '01/08/2010'                   \n"
                    + "        AND M.is_ativo = TRUE                            \n"
                    + "        AND M.id_acordo > 0                              \n";
            if (in_usuarios != null && !in_usuarios.isEmpty()) {
                textQuery += " AND AC.id_usuario IN (" + in_usuarios + " )" + " \n";
            }
            if (relatorioOrdem == null) {
                textQuery += " ORDER BY P.ds_nome";
            } else {
                textQuery += " ORDER BY " + relatorioOrdem.getQuery();
            }
            Query query = getEntityManager().createNativeQuery(textQuery);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaUsuariosAgrupados(String dataFechamento) {
        try {
            String queryString = ""
                    + "SELECT U.* \n"
                    + "FROM seg_usuario AS U \n"
                    + "INNER JOIN pes_pessoa AS P ON P.id = U.id_pessoa\n"
                    + "WHERE U.id IN (\n"
                    + "   SELECT id_usuario \n"
                    + "     FROM arr_acordo a \n"
                    + " INNER JOIN arr_acordo_comissao acc ON acc.id_acordo = a.id \n";
            if (!dataFechamento.isEmpty()) {
                queryString += " WHERE acc.dt_fechamento = '" + dataFechamento + "' \n";
            }
            queryString += " GROUP BY id_usuario \n"
                    + " ) ORDER BY P.ds_nome ASC";
            Query query = getEntityManager().createNativeQuery(queryString, Usuario.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }
}
