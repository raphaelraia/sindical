/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.BoletoNaoBaixado;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class BoletoNaoBaixadoDao extends DB {

    public Pessoa pesquisaPessoaPorBoletoNaoBaixado(String boleto) {
        Pessoa pessoa = null;
        String textqry
                = " SELECT pes.* \n "
                + "   FROM pes_pessoa pes \n "
                + "  INNER JOIN fin_movimento mov ON pes.id = mov.id_pessoa \n "
                + "  INNER JOIN fin_boleto bol ON mov.nr_ctr_boleto = bol.nr_ctr_boleto \n "
                + "  INNER JOIN soc_boleto_nao_baixado boln ON boln.id_boleto = bol.id \n "
                + "  WHERE mov.is_ativo is true \n "
                + "    AND mov.ds_documento = '" + boleto + "'";
        try {
            Query qry = getEntityManager().createNativeQuery(textqry, Pessoa.class);
            qry.setMaxResults(1);
            pessoa = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return pessoa;
    }

    public List<BoletoNaoBaixado> listaBoletoNaoBaixado(Integer id_pessoa) {
        try {
            String textqry
                    = "SELECT boln.* \n "
                    + "  FROM soc_boleto_nao_baixado boln \n "
                    + " INNER JOIN fin_boleto bol ON bol.id = boln.id_boleto \n "
                    + " INNER JOIN fin_movimento m ON m.nr_ctr_boleto = bol.nr_ctr_boleto \n "
                    + " INNER JOIN pes_pessoa p ON p.id = m.id_pessoa \n "
                    + " WHERE m.is_ativo is true \n "
                    + "   AND p.id = " + id_pessoa;
            Query qry = getEntityManager().createNativeQuery(textqry, BoletoNaoBaixado.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
