package br.com.rtools.arrecadacao.dao;

import br.com.rtools.pessoa.Juridica;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class WebContabilidadeDao extends DB {

    public List<Juridica> listaEmpresasPertContabilidade(int idContabilidade) {
        String textQuery
                = " SELECT j.* "
                + "   FROM pes_juridica j "
                + "  INNER JOIN pes_pessoa p ON p.id = j.id_pessoa "
                + "  INNER JOIN arr_contribuintes_vw jv ON j.id = jv.id_juridica AND jv.dt_inativacao IS NULL"
                + "  WHERE j.id_contabilidade = " + idContabilidade
                + "  ORDER BY p.ds_nome";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery, Juridica.class);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    public List pesquisaMovParaWebContabilidade(int id_pessoa) {
        String textQuery;
        textQuery = "   SELECT m.ds_documento boleto, "
                + "            se.id                    AS servico_id,          \n"
                + "            tp.id                    AS tipo_servico_id,     \n"
                + "            m.ds_referencia          AS referencia,          \n"
                + "            m.dt_vencimento          AS vencimento,          \n"
                + "            func_valor_folha(m.id)   AS valor_mov,           \n"
                + "            f.nr_valor               AS valor_folha,         \n"
                + "            0         AS multa,               \n" // versão nova não calcula por aqui!!!
                + "            0         AS juros,               \n" // versão nova não calcula por aqui!!!
                + "            0         AS correcao,            \n" // versão nova não calcula por aqui!!!
                + "            null                     AS desconto,            \n"
                + "            0 AS valor_calculado,   \n" // versão nova não calcula por aqui!!!
                + "            func_intervalo_meses(CURRENT_DATE,dt_vencimento) AS meses_em_atraso,                                     \n"
                + "            (CURRENT_DATE - dt_vencimento) AS dias_em_atraso,\n"
                + "            i.ds_descricao           AS indice,              \n"
                + "            m.id                     AS id                   \n"
                + "       FROM fin_movimento    AS m                            \n"
                + " INNER JOIN fin_servicos     AS se ON se.id = m.id_servicos      \n"
                + " INNER JOIN fin_tipo_servico AS tp ON tp.id = m.id_tipo_servico  \n"
                + " INNER JOIN pes_juridica     AS j  ON j.id_pessoa=m.id_pessoa    \n"
                + "  LEFT JOIN arr_faturamento_folha_empresa AS f ON f.id_juridica = j.id AND f.ds_referencia = m.ds_referencia AND f.id_tipo_servico = m.id_tipo_servico \n"
                + "  LEFT JOIN fin_correcao     AS cr ON cr.id_servicos = m.id_servicos AND                                                                         "
                + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) AND    "
                + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2))            \n"
                + "  LEFT JOIN fin_indice       AS i on i.id = cr.id_indice                                                                                         \n"
                + "      WHERE m.id_pessoa = " + id_pessoa + "  \n"
                + "        AND m.is_ativo = true                \n"
                + "        AND m.id_baixa IS NULL               \n"
                + "        AND m.id_servicos IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4)                                                    \n"
                + "   ORDER BY m.dt_vencimento ";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List pesquisaMovParaWebContabilidadeComRef(int id_pessoa, String referencia) {
        List result;
        Query qry = null;
        String textQuery;
        textQuery = "select m.ds_documento Boleto, "
                + "       se.id as id_servico, "
                + "       tp.id as id_tipo_servico, "
                + "       m.ds_referencia as Referencia,"
                + "       m.dt_vencimento as Vencimento, "
                + "       func_valor_folha(m.id) as Valor_Mov,"
                + "       f.nr_valor as Valor_Folha,"
                + "       0 as Multa," // versão nova não calcula por aqui!!!
                + "       0 as Juros," // versão nova não calcula por aqui!!!
                + "       0 as Correcao," // versão nova não calcula por aqui!!!
                + "       null as Desconto,"
                + "       0 as Valor_calculado," // versão nova não calcula por aqui!!!
                + "       func_intervalo_meses(CURRENT_DATE,dt_vencimento) as Meses_em_Atraso,"
                + "       CURRENT_DATE-dt_vencimento as Dias_em_atraso,"
                + "       i.ds_descricao indice,"
                + "       m.id as id"
                + "  from fin_movimento as m"
                + " inner join fin_servicos as se on se.id=m.id_servicos"
                + " inner join fin_tipo_servico as tp on tp.id=m.id_tipo_servico"
                + " inner join pes_juridica as j on j.id_pessoa=m.id_pessoa"
                + "  left join arr_faturamento_folha_empresa as f on f.id_juridica=j.id and f.ds_referencia=m.ds_referencia and f.id_tipo_servico=m.id_tipo_servico"
                + "  left join fin_correcao as cr on cr.id_servicos=m.id_servicos and "
                + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and "
                + " (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2))"
                + "  left join fin_indice as i on i.id=cr.id_indice"
                + " where m.id_pessoa = " + id_pessoa
                + "   and m.is_ativo is true "
                + "   and m.id_baixa is null "
                + "   and m.ds_referencia = '" + referencia + "'"
                + " order by m.dt_vencimento";
        try {
            qry = getEntityManager().createNativeQuery(textQuery);
            result = qry.getResultList();
        } catch (Exception e) {
            result = new Vector();
        }
        return result;
    }
//    public List pesquisaMovParaWebContabilidadeComRef(int id_pessoa, String referencia){
//        List result;
//        Query qry = null;
//        String textQuery;
//        textQuery = "select m from Movimento m " +
//                    " where m.servicos.id in (select sr.servicos.id from ServicoRotina sr where sr.rotina.id = 4) " +
//                    "   and m.pessoa.id = :idPes" +
//                    "   and m.referencia = :ref" +
//                    "   and m.ativo = true " +
//                    "   and m.baixa is null";
//        try{
//            qry = getEntityManager().createQuery(textQuery);
//            qry.setParameter("idPes", id_pessoa);
//            qry.setParameter("ref", referencia);
//            result = qry.getResultList();
//        }catch(Exception e){
//            result = new Vector();
//        }
//        return result;
//    }
}
