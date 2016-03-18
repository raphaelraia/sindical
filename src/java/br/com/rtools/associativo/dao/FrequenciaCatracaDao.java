/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class FrequenciaCatracaDao extends DB {

    public List<ArrayList> listaFrequencia(Integer id_departamento, String data_inicial, String data_final, String hora_inicial, String hora_final, Integer id_pessoa, Integer id_sis_pessoa, String es, Boolean relatorio) {
        /**
         * QUERY
         */
        String QUERY
                = "SELECT cf.id,              \n"
                + "       cf.id_departamento, \n"
                + "       CASE WHEN pp.id IS NOT NULL THEN pp.ds_nome ELSE sp.ds_nome END AS nome, \n"
                + "       cf.dt_acesso,       \n"
                + "       ds_hora_acesso,     \n"
                + "       cf.ds_es,           \n"
                + "       CASE WHEN pp.id IS NOT NULL THEN pp.id ELSE sp.id END AS codigo, \n"
                + "       CASE WHEN pp.id IS NOT NULL THEN '' ELSE '(CONVITE)' END AS tipo, \n"
                + "       d.ds_descricao \n"
                + "  FROM soc_catraca_frequencia cf \n"
                + " INNER JOIN seg_departamento d ON d.id = cf.id_departamento \n"
                + "  LEFT JOIN pes_pessoa pp ON cf.id_pessoa = pp.id \n"
                + "  LEFT JOIN sis_pessoa sp ON cf.id_sis_pessoa = sp.id \n";

        /**
         * CONDIÇÕES -------------
         */
        List<String> list_and = new ArrayList();
        String WHERE_AND = " WHERE cf.id_departamento = " + id_departamento + " \n";
        // DATA DE ACESSO INICIAL E FINAL
        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
            list_and.add("cf.dt_acesso >= '" + data_inicial + "' AND cf.dt_acesso <= '" + data_final + "'");
        } else if (!data_inicial.isEmpty() && data_final.isEmpty()) {
            list_and.add("cf.dt_acesso >= '" + data_inicial + "'");
        } else if (data_inicial.isEmpty() && !data_final.isEmpty()) {
            list_and.add("cf.dt_acesso <= '" + data_final + "'");
        }
        // ---

        if (!hora_inicial.isEmpty() && !hora_final.isEmpty()) {
            list_and.add("cf.ds_hora_acesso >= '" + hora_inicial + "' AND cf.ds_hora_acesso <= '" + hora_final + "'");
        } else if (!hora_inicial.isEmpty() && hora_final.isEmpty()) {
            list_and.add("cf.ds_hora_acesso >= '" + hora_inicial + "'");
        } else if (hora_inicial.isEmpty() && !hora_final.isEmpty()) {
            list_and.add("cf.ds_hora_acesso <= '" + hora_final + "'");
        }

        if (!es.equals("ES")) {
            list_and.add("cf.ds_es = '" + es + "'");
        }
        // ---

        for (String and_string : list_and) {
            WHERE_AND += " AND " + and_string + " \n ";
        }
        QUERY += WHERE_AND;

        if (id_pessoa != null) {
            QUERY += " AND pp.id = " + id_pessoa + " \n ";
        }

        if (id_sis_pessoa != null) {
            QUERY += " AND sp.id = " + id_sis_pessoa + " \n ";
        }

        /**
         * ORDEM ------------
         */
        String ORDER_BY = "ORDER BY cf.dt_acesso, nome ";
        if (relatorio) {
            ORDER_BY = "ORDER BY cf.dt_acesso, cf.ds_hora_acesso, nome ";
        }

        QUERY += ORDER_BY;

        try {
            Query qry = getEntityManager().createNativeQuery(QUERY);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CF FROM CatracaFrequencia AS CF WHERE CF.pessoa.id = :pessoa_id ORDER BY CF.dtAcesso DESC, CF.horaAcesso ASC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    public List findSisPessoa(Integer sis_pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CF FROM CatracaFrequencia AS CF WHERE CF.sisPessoa.id = :sis_pessoa_id ORDER BY CF.dtAcesso DESC, CF.horaAcesso ASC");
            query.setParameter("sis_pessoa_id", sis_pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }
}
