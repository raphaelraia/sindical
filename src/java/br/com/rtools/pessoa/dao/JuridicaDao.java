package br.com.rtools.pessoa.dao;

import br.com.rtools.arrecadacao.CnaeConvencao;
import br.com.rtools.arrecadacao.Empregados;
import br.com.rtools.arrecadacao.MotivoInativacao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class JuridicaDao extends DB {

    private Boolean contabilidade = false;

    public List<PessoaEndereco> pesquisarPessoaEnderecoJuridica(int id) {
        List<PessoaEndereco> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select pe "
                    + "  from  PessoaEndereco pe"
                    + " where pe.pessoa.id = :id");
            qry.setParameter("id", id);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPessoa(String desc, String por, String como) {
        return pesquisaPessoa(desc, por, como, false, false, null, null);
    }

    public List pesquisaPessoa(String desc, String por, String como, boolean isContabilidades, boolean isAtivas, Integer limit, Integer offset) {
        if (desc.isEmpty()) {
            return new ArrayList();
        }
        String textQuery = "";

        if (por.equals("codigo")) {

        } else {
            desc = AnaliseString.normalizeLower(desc).replace("'", "");
            desc = (como.equals("I") ? desc + "%" : "%" + desc + "%");
        }

        String field = "";

        if (por.equals("codigo")) {
            field = "p.id";
        }
        if (por.equals("nome")) {
            field = "p.ds_nome";
        }
        if (por.equals("contabilidade")) {
            field = "pc.ds_nome";
        }
        if (por.equals("fantasia")) {
            field = "j.ds_fantasia";
        }
        if (por.equals("email1")) {
            field = "p.ds_email1";
        }
        if (por.equals("email2")) {
            field = "p.ds_email2";
        }
        if (por.equals("cpf") || por.equals("cnpj") || por.equals("cei")) {
            field = "p.ds_documento";
        }
        if (offset == null && !contabilidade) {
            textQuery = " SELECT COUNT(*) \n ";
        } else {
            textQuery = " SELECT j.* \n ";
        }
        textQuery = textQuery
                + "       FROM pes_juridica j                                    \n "
                + " INNER JOIN pes_pessoa p ON p.id = j.id_pessoa                \n "
                + " LEFT JOIN pes_juridica cont ON cont.id = j.id_contabilidade  \n "
                + " LEFT JOIN pes_pessoa pc ON pc.id = cont.id_pessoa            \n ";

        switch (por) {
            case "codigo":
                textQuery = textQuery + " WHERE " + field + " = " + desc + "  \n ";
                break;
            case "cnpj":
            case "cpf":
            case "cei":
                textQuery = textQuery + " WHERE " + field + " LIKE ('" + desc + "') \n ";
                break;
            default:
                textQuery = textQuery + " WHERE LOWER(TRANSLATE(" + field + ")) LIKE ('" + desc + "') \n ";
                break;
        }

        if (isAtivas) {
            textQuery += " AND j.id IN (SELECT id_juridica FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL)  \n ";
            // textQuery += " AND j.id != id_contabilidade ";
        }
        if (isContabilidades) {
            textQuery += " AND j.id IN (SELECT id_contabilidade FROM pes_juridica WHERE id_contabilidade IS NOT NULL)  \n ";
        }
        if (offset != null) {
            textQuery += "  ORDER BY p.ds_nome  \n ";
        }

        if (por.equals("endereco")) {
            if (offset == null && !contabilidade) {
                textQuery = " SELECT COUNT(*) \n ";
            } else {
                textQuery = " SELECT jur.* \n ";
            }
            textQuery = textQuery
                    + "        FROM pes_pessoa_endereco pesend                                                                                                                                \n "
                    + "  INNER JOIN pes_pessoa pes ON (pes.id = pesend.id_pessoa)                                                                                                             \n "
                    + "  INNER JOIN end_endereco ende ON (ende.id = pesend.id_endereco)                                                                                                       \n "
                    + "  INNER JOIN end_cidade cid ON (cid.id = ende.id_cidade)                                                                                                               \n "
                    + "  INNER JOIN end_descricao_endereco enddes ON (enddes.id = ende.id_descricao_endereco)                                                                                 \n "
                    + "  INNER JOIN end_bairro bai ON (bai.id = ende.id_bairro)                                                                                                               \n "
                    + "  INNER JOIN end_logradouro logr ON (logr.id = ende.id_logradouro)                                                                                                     \n "
                    + "  INNER JOIN pes_juridica jur ON (jur.id_pessoa = pes.id)                                                                                                              \n "
                    + "  WHERE (LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || pesend.ds_numero || ', ' || bai.ds_descricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf )) LIKE '%" + desc + "%'  \n "
                    + "     OR LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || bai.ds_descricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf )) LIKE '%" + desc + "%'  \n "
                    + "     OR LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  || ', ' || cid.ds_uf )) LIKE '%" + desc + "%'                                \n "
                    + "     OR LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  )) LIKE '%" + desc + "%'                                                     \n "
                    + "     OR LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao)) LIKE '%" + desc + "%' || ', ' || pesend.ds_numero                                                    \n "
                    + "     OR LOWER(TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                                \n "
                    + "     OR LOWER(TRANSLATE(enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                                                            \n "
                    + "     OR LOWER(TRANSLATE(cid.ds_cidade)) LIKE '%" + desc + "%'                                                                                                                  \n "
                    + "     OR LOWER(TRANSLATE(ende.ds_cep)) = '" + desc + "')  \n "
                    + "    AND pesend.id_tipo_endereco = 2  \n ";
            if (isAtivas) {
                textQuery += " AND jur.id IN (SELECT id_juridica FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL)  \n ";
                // textQuery += " AND j.id != id_contabilidade ";
            }
            if (isContabilidades) {
                textQuery += " AND jur.id IN (SELECT id_contabilidade FROM pes_juridica WHERE id_contabilidade IS NOT NULL)  \n ";
            }
            if (offset != null) {
                textQuery += " ORDER BY pes.ds_nome  \n ";
            }
        }
        if (offset != null && limit != null) {
            textQuery += " LIMIT " + limit + " OFFSET " + offset;
        }
        Query query;
        if (offset == null && !contabilidade) {
            query = getEntityManager().createNativeQuery(textQuery);
        } else {
            query = getEntityManager().createNativeQuery(textQuery, Juridica.class);
        }
        try {
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (EJBQLException e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public CnaeConvencao pesquisaCnaeParaContribuicao(int id) {
        CnaeConvencao result = null;
        try {
            Query qry = getEntityManager().createQuery("select cc from Cnae c,      "
                    + "	      CnaeConvencao   cc"
                    + " where c.id  = cc.cnae.id   "
                    + "   and cc.id = :id_cnae");
            qry.setParameter("id_cnae", id);
            result = (CnaeConvencao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List listaMotivoInativacao() {
        List result;
        try {
            Query qry = getEntityManager().createQuery("select mi from MotivoInativacao mi");
            result = qry.getResultList();
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public Juridica pesquisaJuridicaPorPessoa(int id) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT j.* \n "
                    + " FROM pes_juridica j \n "
                    + "WHERE j.id_pessoa = " + id, Juridica.class
            );
            return (Juridica) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public MotivoInativacao pesquisaCodigoMotivoInativacao(int id) {
        MotivoInativacao result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("MotivoInativacao.pesquisaID");
            qry.setParameter("pid", id);
            result = (MotivoInativacao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaJuridicaPorDoc(String doc) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select jur from Juridica jur"
                    + "  where jur.pessoa.documento = :doc "
                    + " order by jur.pessoa.nome ");
            qry.setParameter("doc", doc);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaJuridicaPorDocSubstring(String doc) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT j.* \n "
                    + "  FROM pes_pessoa p \n "
                    + " INNER JOIN pes_juridica j ON j.id_pessoa = p.id \n "
                    + " WHERE '" + doc + "' = REPLACE(REPLACE(REPLACE(p.ds_documento,'/',''),'-',''),'.','')", Juridica.class
            );
            qry.setParameter("doc", doc);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaPesEndEmpresaComContabil(int idJurCon) {
        try {
            Query qry = getEntityManager().createQuery("select pe"
                    + "  from PessoaEndereco pe, "
                    + "       Juridica jur "
                    + " where pe.pessoa.id = jur.pessoa.id "
                    + "   and pe.tipoEndereco.id = 3 "
                    + "   and jur.contabilidade.id = :idJurCon");
            qry.setParameter("idJurCon", idJurCon);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaJuridicaComEmail() {
        try {
            Query qry = getEntityManager().createQuery("select jur"
                    + "  from Juridica jur "
                    + " where jur.id <> 1 and jur.pessoa.email1 <> ''");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Juridica> pesquisaJuridicaParaRetorno(String documento) {
        List vetor;
        List<Juridica> listJur = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select jur.id "
                    + "  from pes_juridica jur,"
                    + "       pes_pessoa pes "
                    + " where pes.id = jur.id_pessoa "
                    + "   and substring('00'||substring(replace( "
                    + " replace( "
                    + "    replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''),length( "
                    + " replace( "
                    + "    replace( "
                    + "          replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))-14,length( "
                    + " replace( "
                    + "          replace( "
                    + " replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))),0,16) = '" + documento + "'";

            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listJur.add((Juridica) new Dao().find(new Juridica(), (Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listJur;
        } catch (EJBQLException e) {
            return listJur;
        }
    }

    public List<Juridica> pesquisaJuridicaParaRetornoComMascara(String documento) {
        List vetor;
        List<Juridica> listJur = new ArrayList();
        String textQuery = "";
        try {
            textQuery
                    = "SELECT jur.id \n "
                    + "  FROM pes_juridica jur \n "
                    + " INNER JOIN pes_pessoa pes ON pes.id = jur.id_pessoa \n "
                    + " WHERE pes.ds_documento = '" + AnaliseString.mascaraCnpj(documento) + "'";

            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listJur.add((Juridica) new Dao().find(new Juridica(), (Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listJur;
        } catch (EJBQLException e) {
            return listJur;
        }
    }

    public int quantidadeEmpresas(int idContabilidade) {
        try {
            Query qry = getEntityManager().createQuery("select count(j) from Juridica j"
                    + " where j.contabilidade.id = " + idContabilidade);
            return Integer.parseInt(String.valueOf(qry.getSingleResult()));
        } catch (EJBQLException e) {
            return -1;
        }
    }

    public List listaJuridicaContribuinte(Integer id_juridica) {
        try {
            String textQuery = "select * from arr_contribuintes_vw where id_juridica = " + id_juridica;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaJuridicaContribuinteID() {
        try {
            String textQuery = "select id_juridica from arr_contribuintes_vw";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return (Vector) qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Juridica> listaContabilidadePertencente(int id_juridica) {
        List vetor;
        List<Juridica> listJur = new ArrayList();
        try {
            String textQuery = "select id_juridica from arr_contribuintes_vw where id_contabilidade = " + id_juridica + " and dt_inativacao is null order by ds_nome";

            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listJur.add((Juridica) new Dao().find(new Juridica(), (Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listJur;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaContabilidade() {
        try {
            Query qry = getEntityManager().createQuery("select jur.contabilidade "
                    + "  from Juridica jur "
                    + " group by jur.contabilidade, jur.contabilidade.pessoa.nome "
                    + " order by jur.contabilidade.pessoa.nome asc");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public int[] listaInadimplencia(int id_juridica) {
        List vetor;
        int[] result = new int[2];

        try {
            // QUERY QUANTIDADE DE MESES INADIMPLENTES -------------------
            String textQry = "SELECT extract(month FROM dt_vencimento),         "
                    + "              extract(year  FROM dt_vencimento)          "
                    + "         FROM fin_movimento                              "
                    + "        WHERE is_ativo = true                            "
                    + "          AND id_baixa IS NULL                           "
                    + "          AND id_pessoa = " + id_juridica + "            "
                    + "          AND dt_vencimento < '" + DataHoje.data() + "'  "
                    + "          AND id_servicos IN(                            "
                    + "              SELECT id_servicos                         "
                    + "                FROM fin_servico_rotina                  "
                    + "               WHERE id_rotina = 4                       "
                    + "   )                                                     "
                    + "     GROUP BY extract(month FROM dt_vencimento),         "
                    + "              extract(year  FROM dt_vencimento)          ";

            Query qry = getEntityManager().createNativeQuery(textQry);
            vetor = qry.getResultList();

            if (vetor != null && !vetor.isEmpty()) {
//                for (int i = 0; i < vetor.size(); i++){
                result[0] = vetor.size();
//                }
            } else {
                result[0] = 0;
            }
            // ----------------------------------------------------------

            // QUANTIDADE DE PARCELAS INADIMPLENTES ---------------------
            textQry = "     SELECT count(*)                                     "
                    + "       FROM fin_movimento                                "
                    + "      WHERE is_ativo = true                              "
                    + "        AND id_baixa IS NULL                             "
                    + "        AND id_pessoa = " + id_juridica + "              "
                    + "        AND dt_vencimento < '" + DataHoje.data() + "'    "
                    + "        AND id_servicos IN(                              "
                    + "             SELECT id_servicos                          "
                    + "               FROM fin_servico_rotina                   "
                    + "              WHERE id_rotina = 4                        "
                    + "   )                                                     ";

            qry = getEntityManager().createNativeQuery(textQry);
            vetor = qry.getResultList();

//          for (int i = 0; i < vetor.size(); i++){
            result[1] = Integer.parseInt(String.valueOf((Long) ((Vector) vetor.get(0)).get(0)));
//           }
            // ----------------------------------------------------------
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public boolean empresaInativa(Integer pessoa) {
        Pessoa p = new Pessoa();
        p.setId(pessoa);
        return empresaInativa(p, "");
    }

    public boolean empresaInativa(Pessoa pessoa, String motivo) {
        String stringMotivo = "";
        if (!motivo.equals("")) {
            stringMotivo = " AND motivo = '" + motivo + "' ";
        }
        Query query = getEntityManager().createNativeQuery(" SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NOT NULL AND id_pessoa = " + pessoa.getId() + stringMotivo);
        try {
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (EJBQLException e) {
        }
        return false;
    }

    public Empregados pesquisaEmpregados(int id_juridica) {
        Query qry = getEntityManager().createQuery(
                " SELECT em FROM Empregados em"
                + "  WHERE em.referencia = :p_referencia "
                + "   AND em.juridica.id = :p_juridica"
        );

        try {
            qry.setParameter("p_referencia", DataHoje.data().substring(3));
            qry.setParameter("p_juridica", id_juridica);

            return (Empregados) qry.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public Juridica pesquisaContabilidadePorEmail(String email) {
        String text_qry = "select j.* from pes_juridica j where j.id in ( "
                + "select jc.id_contabilidade "
                + "  from pes_pessoa p "
                + " inner join pes_juridica j ON j.id_pessoa = p.id "
                + " inner join pes_juridica jc ON j.id = jc.id_contabilidade "
                + " where p.ds_email1 like '" + email.toLowerCase() + "' "
                + " group by jc.id_contabilidade "
                + ") limit 1";

        Query qry = getEntityManager().createNativeQuery(text_qry, Juridica.class);

        try {
            return (Juridica) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List findByNome(String nome) {
        try {
            String queryString = "SELECT J.* FROM pes_juridica AS J \n"
                    + "INNER JOIN pes_pessoa AS P ON P.id = J.id_pessoa\n"
                    + "WHERE ( TRIM(UPPER(func_translate(ds_nome))) LIKE TRIM(UPPER(('" + nome + "'))) OR TRIM(UPPER(func_translate(ds_fantasia))) LIKE TRIM(UPPER(('" + nome + "'))) )";
            Query query = getEntityManager().createNativeQuery(queryString, Juridica.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Juridica findByDocumento(String documento) {
        try {
            String queryString = "SELECT J.* FROM pes_juridica AS J \n"
                    + "INNER JOIN pes_pessoa AS P ON P.id = J.id_pessoa\n"
                    + "WHERE P.ds_documento = '" + documento + "'";
            Query query = getEntityManager().createNativeQuery(queryString, Juridica.class);
            return (Juridica) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Juridica> findByContabilidade(Integer contabilidade_id) {
        try {
            String queryString = ""
                    + "     SELECT J.*                                          \n"
                    + "       FROM pes_juridica AS J                            \n"
                    + " INNER JOIN pes_pessoa AS P ON P.id = J.id_pessoa        \n"
                    + "      WHERE J.id_contabilidade = " + contabilidade_id + "\n"
                    + "   ORDER BY P.ds_nome ";
            Query query = getEntityManager().createNativeQuery(queryString, Juridica.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Boolean getContabilidade() {
        return contabilidade;
    }

    public void setContabilidade(Boolean contabilidade) {
        this.contabilidade = contabilidade;
    }
}
