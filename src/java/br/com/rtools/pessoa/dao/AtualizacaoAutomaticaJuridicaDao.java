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
                = "SELECT J.* FROM jur_atualizacao_automatica_vw J";
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
