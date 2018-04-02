/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.CertidaoDisponivel;
import br.com.rtools.arrecadacao.CertidaoDisponivelMensagem;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class CertidaoDisponivelDao extends DB {

    public List<CertidaoDisponivel> listaCertidaoDisponivel() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT cd.* \n "
                    + " FROM arr_certidao_disponivel cd \n "
                    + " ORDER BY cd.id", CertidaoDisponivel.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public CertidaoDisponivelMensagem pesquisaCertidaoDisponivelMensagem(Integer id_certidao_disponivel, Integer id_convencao_periodo) {
        try {
            
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT cdm.* \n "
                    + " FROM arr_certidao_disponivel_mensagem cdm \n "
                    + "WHERE cdm.id_certidao_disponivel = " + id_certidao_disponivel
                    + "  AND cdm.id_convencao_periodo = " + id_convencao_periodo,
                     CertidaoDisponivelMensagem.class
            );

            return (CertidaoDisponivelMensagem) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public CertidaoDisponivel pesquisaCertidaoDisponivel(Integer id_cidade, Integer id_convencao, Integer id_certidao_tipo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT cd.* \n "
                    + " FROM arr_certidao_disponivel cd \n "
                    + " WHERE cd.id_cidade = " + id_cidade
                    + "   AND cd.id_convencao = " + id_convencao
                    + "   AND cd.id_certidao_tipo = " + id_certidao_tipo,
                    CertidaoDisponivel.class
            );

            return (CertidaoDisponivel) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
