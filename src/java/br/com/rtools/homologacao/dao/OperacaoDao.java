/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao.dao;

import br.com.rtools.atendimento.AteOperacao;
import br.com.rtools.homologacao.OperacaoDepartamento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class OperacaoDao extends DB {

    public List<AteOperacao> listaOperacao() {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT o.* FROM ate_operacao o ORDER BY o.ds_descricao", AteOperacao.class);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<OperacaoDepartamento> listaOperacaoDepartamento(Integer id_operacao) {
        try {
            Query qry = getEntityManager().createQuery("SELECT od FROM OperacaoDepartamento od WHERE od.operacao.id = " + id_operacao + " ORDER BY od.departamento.descricao, od.filial.filial.pessoa.nome");

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public OperacaoDepartamento pesquisaOperacaoDepartamento(Integer id_filial, Integer id_operacao) {
        try {
            Query qry = getEntityManager().createQuery("SELECT od FROM OperacaoDepartamento od WHERE od.filial.id = " + id_filial + " AND od.operacao.id = " + id_operacao);
            qry.setMaxResults(1);
            return (OperacaoDepartamento) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<OperacaoDepartamento> listaExisteOperacaoDepartamento(Integer id_operacao, Integer id_filial, Integer id_departamento) {
        try {
            Query qry = getEntityManager().createQuery("SELECT od FROM OperacaoDepartamento od WHERE od.departamento.id = " + id_departamento + " AND od.filial.id = " + id_filial + " AND od.operacao.id = " + id_operacao);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List findByEs(String es) {
        try {
            Query query = getEntityManager().createQuery("SELECT O FROM Operacao O WHERE O.es = :es ORDER BY O.descricao");
            query.setParameter("es", es);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
