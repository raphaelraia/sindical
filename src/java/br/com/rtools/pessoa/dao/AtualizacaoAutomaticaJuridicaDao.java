/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.Juridica;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class AtualizacaoAutomaticaJuridicaDao extends DB {

    public List<Juridica> listaJuridicaParaAtualizacao(Boolean inadimplentes, Boolean cadastradosMais, Boolean servicosArrecadacao, Boolean empresasAtivas, Boolean naoPagaram) {
        String text
                = "SELECT j.* \n"
                + "  FROM pes_juridica AS j\n"
                + " INNER JOIN pes_pessoa AS p ON p.id = j.id_pessoa\n"
                + " WHERE j.id IN (\n"
                + "SELECT \n"
                + "       j.id\n"
                + "  FROM fin_movimento AS m \n"
                + " INNER JOIN arr_contribuintes_vw AS c ON c.id_pessoa = m.id_pessoa \n"
                + " INNER JOIN pes_pessoa AS p ON p.id = c.id_pessoa \n"
                + " INNER JOIN pes_juridica AS j ON j.id_pessoa = p.id\n"
                + " WHERE (p.dt_recadastro < CURRENT_DATE - 180 or p.dt_recadastro is null)\n"
                + "   AND m.is_ativo = TRUE\n"
                + "--------------------- INADIMPLENTES \n"
                + "   AND m.dt_vencimento < CURRENT_DATE AND m.id_baixa IS NULL\n"
                + "--------------------- CADASTRADAS ATÉ A MAIS DE 6 MESES \n"
                + "   AND p.dt_criacao < CURRENT_DATE - 180 ----'01/01/2016' \n"
                + "--------------------- SERVIÇOS DE ARRECADAÇÃO \n"
                + "   AND m.id_servicos IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4) \n"
                + "--------------------- EMPRESAS ATIVAS \n"
                + "   AND c.dt_inativacao IS NULL\n"
                + "--------------------- QUE NÃO PAGARAM NADA ATÉ A ÚLTIMA SINDICAL \n"
                + "   AND m.id_pessoa NOT IN\n"
                + "----- pagamentos \n"
                + "( \n"
                + "SELECT m.id_pessoa \n"
                + "  FROM fin_movimento AS m \n"
                + " INNER JOIN fin_baixa AS b ON b.id = m.id_baixa \n"
                + " INNER JOIN arr_contribuintes_vw AS c ON c.id_pessoa = m.id_pessoa \n"
                + " WHERE m.is_ativo = TRUE \n"
                + "   AND b.dt_baixa >= to_date('30/04/'||EXTRACT(YEAR FROM CURRENT_DATE) - 1,'dd/mm/yyyy') \n"
                + "   AND m.id_servicos IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4) \n"
                + "   AND c.dt_inativacao IS NULL\n"
                + " GROUP BY m.id_pessoa \n"
                + ")\n"
                + "GROUP BY j.id\n"
                + ")\n"
                + "ORDER BY rtrim(ltrim(p.ds_nome)) limit 1000";
        try {
            Query qry = getEntityManager().createNativeQuery(
                    text,
                    Juridica.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public String pesquisaUltimaDataSindical() {
        String text
                = "SELECT to_date('30/04/'||EXTRACT(YEAR FROM CURRENT_DATE) - 1,'dd/mm/yyyy') ";
        try {
            Query qry = getEntityManager().createNativeQuery(
                    text
            );
            return DataHoje.converteData((Date) ((List) qry.getResultList().get(0)).get(0));
        } catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

}
