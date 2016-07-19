/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.DeclaracaoPessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class DeclaracaoPessoaDao extends DB {

    public List<Object> listaConvenio(Integer id_declaracao_tipo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.id AS id, \n"
                    + "       p.ds_documento AS documento, \n"
                    + "       p.ds_nome AS nome,\n"
                    + "       j.ds_fantasia AS fantasia \n"
                    + "  FROM pes_juridica AS j \n"
                    + " INNER JOIN pes_pessoa AS p ON p.id = j.id_pessoa \n"
                    + " INNER JOIN soc_convenio AS c ON c.id_juridica = j.id \n"
                    + " INNER JOIN soc_declaracao_grupo AS g ON g.id_subgrupo = c.id_convenio_sub_grupo \n"
                    + " WHERE g.id_declaracao_tipo = " + id_declaracao_tipo);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPessoa> listaDeclaracaoPessoa() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dp.* \n "
                    + "  FROM soc_declaracao_pessoa AS dp \n", DeclaracaoPessoa.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaPessoa(String descricao_pesquisa, String inicial_parcial) {
        if (descricao_pesquisa.isEmpty()) {
            return new ArrayList();
        }

        try {
            descricao_pesquisa = AnaliseString.normalizeLower(descricao_pesquisa);
            descricao_pesquisa = inicial_parcial.equals("I") ? descricao_pesquisa + "%" : "%" + descricao_pesquisa + "%";
            String WHERE = " WHERE LOWER(TRANSLATE(nome)) LIKE '" + descricao_pesquisa.toLowerCase() + "' ";

            Query qry = getEntityManager().createNativeQuery(
                    "SELECT codsocio AS id_pessoa, \n"
                    + "       id_matricula AS id_matricula, \n"
                    + "       id_categoria AS id_categoria, \n"
                    + "       id_parentesco AS id_parentesco, \n"
                    + "       func_idade(f.dt_nascimento, CURRENT_DATE) \n"
                    + "  FROM soc_socios_vw AS s \n"
                    + " INNER JOIN pes_fisica AS f ON f.id_pessoa = s.codsocio \n"
                    + WHERE
                    + " ORDER BY nome LIMIT 30"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPessoa> listaDeclaracaoPessoaAnoVigente(Integer id_pessoa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dp.* \n"
                    + "   FROM soc_declaracao_pessoa dp \n"
                    + "  WHERE dp.id_pessoa = " + id_pessoa + "\n"
                    + "    AND DATE_PART('YEAR', dp.dt_emissao) = DATE_PART('YEAR', CURRENT_DATE)", DeclaracaoPessoa.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
