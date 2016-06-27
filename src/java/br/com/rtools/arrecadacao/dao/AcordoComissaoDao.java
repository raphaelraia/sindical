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
        List<AcordoComissao> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select a"
                    + "  from AcordoComissao a"
                    + " where a.dtFechamento = :data");
            qry.setParameter("data", DataHoje.converte(data));
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select a from AcordoComissao a ");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Date> pesquisaTodosFechamento() {
        try {
            Query qry = getEntityManager().createQuery(
                    "select a.dtFechamento "
                    + "  from AcordoComissao a"
                    + " group by a.dtFechamento"
                    + " order by a.dtFechamento desc");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public boolean inserirAcordoComissao() {
        try {
            getEntityManager().getTransaction().begin();
            String textQuery
                    = " insert into arr_acordo_comissao (dt_inicio,dt_fechamento,nr_num_documento,id_conta_cobranca,id_acordo) "
                    + "            ( "
                    + "                select (select case when max(dt_fechamento) is null then (select min(dt_data) from arr_acordo) else (max(dt_fechamento) + 1) end from arr_acordo_comissao ) dt_inicio, "
                    + "                       '" + DataHoje.data() + "' as dt_fechamento, "
                    + "                       m.ds_documento BOLETO, "
                    + "                       bo.id_conta_cobranca, "
                    + "                       m.id_acordo "
                    + "                  from fin_movimento              as m "
                    + "                 inner join pes_pessoa            as p on p.id = m.id_pessoa "
                    + "                 inner join fin_baixa             as lb on lb.id = m.id_baixa "
                    + "                 inner join fin_servicos          as se on se.id = m.id_servicos "
                    + "                 inner join fin_boleto            as bo on bo.nr_ctr_boleto = m.nr_ctr_boleto "
                    + "                 inner join fin_conta_cobranca    as cc on cc.id = bo.id_conta_cobranca "
                    + "                 where m.id_tipo_servico = 4 "
                    + "                   and m.id_servicos <> 8 "
                    + "                   and lb.dt_baixa > '01/08/2010'"
                    + "                   and m.is_ativo = true "
                    + "                   and m.id_acordo > 0 "
                    + "                   and 'C'|| bo.id_conta_cobranca ||'D'||m.ds_documento not in (select 'C'|| "
                    + "                                                                                      id_conta_cobranca|| "
                    + "                                                                                      'D'|| "
                    + "                                                                                      nr_num_documento "
                    + "                                                                                 from arr_acordo_comissao)"
                    + ") ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();
            new NovoLog().live("Fechamento de Acordo: " + DataHoje.data());
            return true;
        } catch (Exception e) {
            e.getMessage();
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean estornarAcordoComissao(String data) {
        try {
            getEntityManager().getTransaction().begin();
            String textQuery
                    = " DELETE FROM arr_acordo_comissao WHERE dt_fechamento= \'" + data + "\' ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.getMessage();
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public List listaAcordoComissao(String data, String in_usuarios) {
        try {
            String textQuery
                    = "     SELECT DISTINCT ON (bo.id_conta_cobranca,m.ds_documento,p.ds_nome, pu.ds_nome)\n"
                    + "            p.ds_documento   AS cnpj,                    \n" // 0
                    + "            p.ds_nome        AS empresa,                 \n" // 1
                    + "            m.id_acordo      AS acordo_id,               \n" // 2
                    + "            m.ds_documento   AS boleto,                  \n" // 3
                    + "            se.ds_descricao  AS contribuicao,            \n" // 4
                    + "            m.nr_valor_baixa AS valor_recebido,          \n" // 5
                    + "            m.nr_taxa        AS taxa,                    \n" // 6
                    + "            cc.nr_repasse    AS repasse,                 \n" // 7
                    + "            lb.dt_importacao AS data_importacao,         \n" // 8
                    + "            lb.dt_baixa      AS data_recebimento,        \n" // 9
                    + "            current_date     AS data_fechamento,         \n" // 10
                    + "            m.dt_vencimento  AS data_vencimento,         \n" // 11
                    + "            acc.dt_inicio    AS data_inicio,             \n" // 12
                    + "            l.dt_emissao     AS data_emissao,            \n" // 13
                    + "            cast(0 as float) AS comissao,                \n" // 14
                    + "            cast(0 as float) AS liquido,                 \n" // 15
                    + "            pu.ds_nome       AS usuario_nome             \n" // 16
                    + "       FROM fin_movimento AS m \n"
                    + " INNER JOIN pes_pessoa AS p ON p.id = m.id_pessoa \n"
                    + " INNER JOIN fin_baixa AS lb ON lb.id = m.id_baixa \n"
                    + " INNER JOIN fin_servicos AS se ON se.id = m.id_servicos \n"
                    + " INNER JOIN fin_boleto AS bo ON bo.nr_ctr_boleto = m.nr_ctr_boleto \n"
                    + " INNER JOIN fin_conta_cobranca AS cc ON cc.id = bo.id_conta_cobranca \n"
                    + " INNER JOIN arr_acordo_comissao AS acc ON bo.id_conta_cobranca = acc.id_conta_cobranca AND m.ds_documento = acc.nr_num_documento AND dt_fechamento = '" + data + "' \n"
                    + " INNER JOIN arr_acordo AS ac ON ac.id = acc.id_acordo    \n"
                    + " INNER JOIN seg_usuario AS U ON U.id = ac.id_usuario     \n"
                    + " INNER JOIN pes_pessoa AS PU ON PU.id = U.id_pessoa      \n"
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote            \n"
                    + "      WHERE m.id_tipo_servico = 4                        \n"
                    + "        AND m.id_servicos <> 8                           \n"
                    + "        AND lb.dt_baixa > '01/08/2010'                   \n"
                    + "        AND m.is_ativo = TRUE                            \n"
                    + "        AND m.id_acordo > 0                              \n";
            if (in_usuarios != null && !in_usuarios.isEmpty()) {
                textQuery += " AND ac.id_usuario IN (" + in_usuarios + " )" + " \n";
            }
            if (relatorioOrdem == null) {
                textQuery += " ORDER BY p.ds_nome";
            } else {
                textQuery += " ORDER BY " + relatorioOrdem.getQuery();
            }
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
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
            e.getMessage();
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
