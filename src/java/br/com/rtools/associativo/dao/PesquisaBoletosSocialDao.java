package br.com.rtools.associativo.dao;

import br.com.rtools.financeiro.MovimentoBoleto;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PesquisaBoletosSocialDao extends DB {

    public List<MovimentoBoleto> listaMovimentoBoleto(String tipo, String descricao) {
        String text
                = "SELECT mb.* \n "
                + "  FROM fin_movimento_boleto mb \n "
                + " INNER JOIN fin_boleto b ON mb.id_boleto = b.id \n "
                + " INNER JOIN fin_movimento m ON m.id = mb.id_movimento \n ";

        String inner_join = "";
        List<String> list_where = new ArrayList();

        list_where.add(" WHERE m.is_ativo = true \n ");

        if (tipo.equals("boleto")) {
            text += "   AND b.ds_boleto = '" + descricao + "' \n ";
        }

        if (tipo.equals("beneficiario")) {
            inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
            descricao = AnaliseString.normalizeLower(descricao);
            list_where.add(" AND LOWER(FUNC_TRANSLATE(px.ds_nome)) like ('%" + descricao + "%') \n ");
        }

        if (tipo.equals("codigo")) {
            inner_join += " INNER JOIN pes_pessoa px ON m.id_beneficiario = px.id \n ";
            list_where.add(" AND px.id = " + descricao + " \n ");
        }

        text += inner_join;
        for (String list_where1 : list_where) {
            text += list_where1;
        }

        text += " ORDER BY m.dt_vencimento DESC";

        try {
            Query qry = getEntityManager().createNativeQuery(text, MovimentoBoleto.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
