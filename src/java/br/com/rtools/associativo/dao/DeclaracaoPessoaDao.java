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

    public List<DeclaracaoPessoa> listaDeclaracaoPessoa(Integer id_pessoa_convenio) {
        try {
            String query = "SELECT dp.* \n "
                    + "  FROM soc_declaracao_pessoa AS dp \n";

            if (id_pessoa_convenio != null) {
                query += " WHERE dp.id_convenio = " + id_pessoa_convenio;
            }

            Query qry = getEntityManager().createNativeQuery(query, DeclaracaoPessoa.class
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

    public List<DeclaracaoPessoa> listaDeclaracaoPessoaAnoVigente(Integer id_pessoa, Integer id_declaracao_periodo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dp.* \n"
                    + "   FROM soc_declaracao_pessoa dp \n"
                    + "  WHERE dp.id_pessoa = " + id_pessoa + "\n"
                    + "    AND dp.id_declaracao_periodo = " + id_declaracao_periodo, DeclaracaoPessoa.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPessoa> listaDeclaracaoPessoaJuridica(Integer id_pessoa, String tipo) {
        try {
            String text
                    = "SELECT dp.* \n "
                    + "  FROM soc_declaracao_pessoa AS dp \n";

            if (tipo.equals("empresa_conveniada")) {
                text += " WHERE dp.id_convenio = " + id_pessoa;
            } else {
                text += "   INNER JOIN matr_socios AS m ON m.id = dp.id_matricula \n"
                        + " INNER JOIN pes_fisica AS pt ON pt.id_pessoa = m.id_titular \n"
                        + " INNER JOIN pes_fisica AS p ON p.id_pessoa = dp.id_pessoa \n"
                        + " INNER JOIN pes_pessoa_empresa AS pe ON (pe.id_fisica = pt.id OR pe.id_fisica = p.id) AND pe.is_principal = TRUE AND pe.dt_demissao IS NULL \n"
                        + "      WHERE pe.id_juridica =" + id_pessoa + "";
            }
            Query qry = getEntityManager().createNativeQuery(text, DeclaracaoPessoa.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<DeclaracaoPessoa> listaDeclaracaoPessoaFisica(Integer id_pessoa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dp.* \n "
                    + "  FROM soc_declaracao_pessoa AS dp \n"
                    + " WHERE dp.id_pessoa = " + id_pessoa, DeclaracaoPessoa.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List find(String tcase, Integer pessoa_id) {
        String queryString = "-- DeclaracaoPesoaDao().find( " + tcase + ").... \n\n"
                + "      SELECT F.ds_nome       AS funcionario, -- Cadastro de Pessoa Juridica (Emitidas Para os Funcionários Desta Empresa) ----> Trocar nome da coluna Titular para Funcionário\n"
                + "             PTT.ds_nome     AS titular, -- Somente pessoa física e  (Emitidas para esta Empresa) \n"
                + "             PP.ds_nome      AS beneficiario,                \n"
                + "             C.ds_categoria  AS categoria,                   \n"
                + "             M.nr_matricula  AS matricula,                   \n"
                + "             DT.ds_descricao AS tipo,                        \n"
                + "             DR.ds_descricao||' '||dr.nr_ano AS periodo,     \n"
                + "             CO.ds_documento AS cnpj,                        \n"
                + "             CO.ds_nome      AS convenio,                    \n"
                + "             DP.id           AS id_declaracao                \n"
                + "        FROM soc_declaracao_pessoa AS DP                     \n"
                + "  INNER JOIN pes_pessoa             CO  ON CO.id  = DP.id_convenio \n"
                + "  INNER JOIN soc_declaracao_periodo DR  ON DR.id  = DP.id_declaracao_periodo \n"
                + "  INNER JOIN soc_declaracao_tipo    DT  ON DT.id  = DR.id_declaracao_tipo \n"
                + "  INNER JOIN matr_socios            M   ON M.id   = DP.id_matricula \n"
                + "  INNER JOIN soc_categoria          C   ON C.id   = M.id_categoria \n"
                + "  INNER JOIN pes_fisica             PT  ON PT.id_pessoa = M.id_titular \n"
                + "  INNER JOIN pes_pessoa             PTT ON PTT.id = PT.id_pessoa \n"
                + "  INNER JOIN pes_fisica             P   ON P.id_pessoa = DP.id_pessoa \n"
                + "  INNER JOIN pes_pessoa             PP  ON PP.id  = P.id_pessoa \n"
                + "  INNER JOIN pes_pessoa_empresa     PE  ON (PE.id_fisica = PT.id OR PE.id_fisica = P.id) AND PE.is_principal = true AND PE.dt_demissao IS NULL \n"
                + "  INNER JOIN pes_juridica           JE  ON JE.id  = PE.id_juridica \n"
                + "  INNER JOIN pes_fisica             FF  ON FF.id  = PE.id_fisica \n"
                + "  INNER JOIN pes_pessoa             F   ON F.id   = FF.id_pessoa \n";
//              Cadastro de Pessoa Física
        if (tcase.equals("pessoa_fisica")) {
            queryString += " WHERE PTT.id = " + pessoa_id + " OR PP.id = " + pessoa_id + " ORDER BY ptt.ds_nome,pp.ds_nome";
//              Cadastro de Pessoa Juridica (Emitidas para esta Empresa)
        } else if (tcase.equals("empresa_pessoa")) {
            queryString += "WHERE CO.id = " + pessoa_id + "  ORDER BY PTT.ds_nome, PP.ds_nome ";
        } else if (tcase.equals("empresa_conveniada")) {
//              Cadastro de Pessoa Juridica (Emitidas Para os Funcionários Desta Empresa)                    
            queryString += "WHERE JE.id_pessoa =  " + pessoa_id + " ORDER BY F.ds_nome, PTT.ds_nome, PP.ds_nome";
        }
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
