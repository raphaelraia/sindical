/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.ExameMedico;
import br.com.rtools.associativo.ValidadeExameMedico;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class ExameMedicoDao extends DB {

    public ValidadeExameMedico pesquisaValidadeExameMedico(Integer id_departamento) {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT emv.* FROM soc_exame_medico_validade emv WHERE emv.id_departamento = " + id_departamento, ValidadeExameMedico.class);
            return (ValidadeExameMedico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<ValidadeExameMedico> listaValidadeExameMedico(String id_departamento) {
        try {
            String text_query
                    = " SELECT emv.* \n "
                    + " FROM soc_exame_medico_validade emv \n "
                    + "INNER JOIN seg_departamento d ON d.id = emv.id_departamento \n"
                    + "WHERE d.id in (" + id_departamento + ") \n"
                    + "ORDER BY d.ds_descricao";
            Query qry = getEntityManager().createNativeQuery(
                    text_query,
                    ValidadeExameMedico.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<ValidadeExameMedico> listaValidadeExameMedico() {
        try {
            String text_query
                    = " SELECT emv.* \n "
                    + " FROM soc_exame_medico_validade emv \n "
                    + "INNER JOIN seg_departamento d ON d.id = emv.id_departamento \n"
                    + "ORDER BY d.ds_descricao";
            Query qry = getEntityManager().createNativeQuery(
                    text_query,
                    ValidadeExameMedico.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<ExameMedico> listaExameMedico(Integer id_pessoa, String id_departamento) {
        try {
            String text_query
                    = "     SELECT em.*                                                 \n"
                    + "       FROM soc_exame_medico AS EM                               \n"
                    + " INNER JOIN seg_departamento AS D  ON D.id  = EM.id_departamento \n"
                    + "  LEFT JOIN pes_pessoa       AS P  ON P.id  = EM.id_pessoa       \n"
                    + "  LEFT JOIN sis_pessoa       AS SP ON SP.id = EM.id_sis_pessoa   \n"
                    + " WHERE d.id IN (" + id_departamento + ") \n";

            if (id_pessoa != null) {
                text_query += " AND p.id = " + id_pessoa;
            }

            text_query += " ORDER BY em.id DESC ";

            Query qry = getEntityManager().createNativeQuery(
                    text_query,
                    ExameMedico.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Date pesquisaDataUltimoExame(Integer id_pessoa, Integer id_departamento) {
        String text_query
                = "SELECT MAX(dt_validade) \n"
                + "  FROM soc_exame_medico \n"
                + " WHERE id_pessoa = " + id_pessoa + " \n"
                + "   AND id_departamento = " + id_departamento;

        try {
            Query qry = getEntityManager().createNativeQuery(text_query);
            Date data = (Date) ((List) ((List) qry.getResultList()).get(0)).get(0);
            return data;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

}
