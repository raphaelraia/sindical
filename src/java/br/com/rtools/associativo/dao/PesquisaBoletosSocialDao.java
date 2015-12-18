package br.com.rtools.associativo.dao;

import br.com.rtools.financeiro.MovimentoBoleto;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PesquisaBoletosSocialDao extends DB {

    public List<Object> listaMovimentoBoleto(String tipo, String descricao) {
        String text
                = "SELECT \n "
                + "  pt.id AS id_titular,\n "
                + "  pt.ds_nome AS titular,\n "
                + "  pb.id AS id_beneficiario,\n "
                + "  pb.ds_nome AS beneficiario,\n "
                + "  s.id AS id_servico,\n "
                + "  s.ds_descricao AS servico,\n "
                + "  m.nr_valor AS valor,\n "
                + "  m.dt_vencimento AS vencimento,\n "
                + "  m.ds_documento AS boleto_atual,\n "
                + "  ba.ds_boleto AS boleto_anterior, \n "
                + "  bai.dt_baixa AS quitacao, \n "
                + "  m.id AS id_movimento, \n "
                + "  ba.id AS id_boleto, \n "
                + "  ba.id_conta_cobranca AS id_conta_cobranca \n "
                + "  FROM fin_movimento m \n "
                + " INNER JOIN pes_pessoa pt ON pt.id = m.id_pessoa \n "
                + " INNER JOIN pes_pessoa pb ON pb.id = m.id_beneficiario \n "
                + " INNER JOIN fin_servicos s ON s.id = m.id_servicos \n "
                + "  LEFT JOIN fin_baixa bai ON bai.id = m.id_baixa \n "
                + " INNER JOIN fin_movimento_boleto mb ON mb.id_movimento = m.id \n "
                + " INNER JOIN fin_boleto b ON mb.id_boleto = b.id \n "
                + "  LEFT JOIN fin_boleto ba ON ba.id = b.id \n ";

        List<String> list_where = new ArrayList();

        list_where.add(" WHERE m.is_ativo = true \n ");

        if (tipo.equals("boleto_atual")) {
            list_where.add("   AND m.ds_documento = '" + descricao + "' \n ");
        }

        if (tipo.equals("boleto_anterior")) {
            list_where.add("   AND ba.ds_boleto = '" + descricao + "' \n ");
        }

        if (tipo.equals("beneficiario")) {
            //inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
            descricao = AnaliseString.normalizeLower(descricao);
            list_where.add(" AND LOWER(FUNC_TRANSLATE(pb.ds_nome)) like ('%" + descricao + "%') \n ");
        }

        if (tipo.equals("codigo")) {
            //inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
            list_where.add(" AND pb.id = " + descricao + " \n ");
        }

        for (String list_where1 : list_where) {
            text += list_where1;
        }

        text += " ORDER BY m.dt_vencimento DESC, m.id";

        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
//    public List<MovimentoBoleto> listaMovimentoBoleto(String tipo, String descricao) {
//        String text
//                = "SELECT mb.* \n "
//                + "  FROM fin_movimento_boleto mb \n "
//                + " INNER JOIN fin_boleto b ON mb.id_boleto = b.id \n "
//                + " INNER JOIN fin_movimento m ON m.id = mb.id_movimento \n ";
//
//        String inner_join = "";
//        List<String> list_where = new ArrayList();
//
//        list_where.add(" WHERE m.is_ativo = true \n ");
//
//        if (tipo.equals("boleto")) {
//            text += "   AND b.ds_boleto = '" + descricao + "' \n ";
//        }
//
//        if (tipo.equals("beneficiario")) {
//            inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
//            descricao = AnaliseString.normalizeLower(descricao);
//            list_where.add(" AND LOWER(FUNC_TRANSLATE(px.ds_nome)) like ('%" + descricao + "%') \n ");
//        }
//
//        if (tipo.equals("codigo")) {
//            inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
//            list_where.add(" AND px.id = " + descricao + " \n ");
//        }
//
//        text += inner_join;
//        for (String list_where1 : list_where) {
//            text += list_where1;
//        }
//
//        text += " ORDER BY m.dt_vencimento DESC";
//
//        try {
//            Query qry = getEntityManager().createNativeQuery(text, MovimentoBoleto.class
//            );
//            return qry.getResultList();
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return new ArrayList();
//    }
}
