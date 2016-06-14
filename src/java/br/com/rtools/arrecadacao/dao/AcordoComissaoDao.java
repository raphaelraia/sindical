package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.AcordoComissao;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class AcordoComissaoDao extends DB {

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

    public List<Vector> listaAcordoComissao(String data) {
        try {
            String textQuery
                    = "SELECT \n"
                    + "   DISTINCT ON (bo.id_conta_cobranca,m.ds_documento,p.ds_nome) \n"
                    + "   p.ds_documento AS CNPJ, \n" // 0
                    + "   p.ds_nome AS EMPRESA, \n" // 1
                    + "   m.id_acordo AS ACORDO, \n" // 2
                    + "   m.ds_documento AS BOLETO, \n" // 3
                    + "   se.ds_descricao AS CONTRIBUICAO, \n" // 4
                    + "   lb.dt_importacao AS IMPORTACAO, \n" // 5
                    + "   lb.dt_baixa AS RECEBTO, \n" // 6
                    + "   m.dt_vencimento AS VENCIMENTO, \n" // 7
                    + "   acc.dt_inicio AS DT_INICIO, \n" // 8
                    + "   m.nr_valor_baixa AS VLRECEB, \n" // 9
                    + "   m.nr_taxa AS TAXA, \n" // 10
                    + "   cc.nr_repasse AS REPASSE, \n" // 11
                    + "   l.dt_emissao AS EMISSAO \n" // 12
                    + "  FROM fin_movimento AS m \n"
                    + " INNER JOIN pes_pessoa AS p ON p.id = m.id_pessoa \n"
                    + " INNER JOIN fin_baixa AS lb ON lb.id = m.id_baixa \n"
                    + " INNER JOIN fin_servicos AS se ON se.id = m.id_servicos \n"
                    + " INNER JOIN fin_boleto AS bo ON bo.nr_ctr_boleto = m.nr_ctr_boleto \n"
                    + " INNER JOIN fin_conta_cobranca AS cc ON cc.id = bo.id_conta_cobranca \n"
                    + " INNER JOIN arr_acordo_comissao AS acc ON bo.id_conta_cobranca = acc.id_conta_cobranca AND m.ds_documento = acc.nr_num_documento AND dt_fechamento = '" + data + "' \n"
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote \n"
                    + " WHERE m.id_tipo_servico = 4 \n"
                    + "   AND m.id_servicos <> 8 \n"
                    + "   AND lb.dt_baixa > '01/08/2010' \n"
                    + "   AND m.is_ativo = TRUE \n"
                    + "   AND m.id_acordo > 0 \n"
                    + "   ORDER BY p.ds_nome";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return (Vector) qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }
}
