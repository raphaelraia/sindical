package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.CertidaoDisponivel;
import br.com.rtools.arrecadacao.CertidaoMensagem;
import br.com.rtools.arrecadacao.CertidaoTipo;
import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.Patronal;
import br.com.rtools.arrecadacao.PisoSalarial;
import br.com.rtools.arrecadacao.PisoSalarialLote;
import br.com.rtools.arrecadacao.RepisMovimento;
import br.com.rtools.arrecadacao.beans.WebREPISBean.ObjectFiltro;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Types;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class WebREPISDao extends DB {

    public RepisMovimento pesquisaCodigoRepisMovimento(int id) {
        RepisMovimento repis = new RepisMovimento();
        try {
            Query qry = getEntityManager().createQuery(
                    "select rm from RepisMovimento rm where rm.id = " + id);
            repis = (RepisMovimento) qry.getSingleResult();
        } catch (Exception e) {
        }
        return repis;
    }

    public List listaContribuinteWeb() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes "
                    + "  from Pessoa pes, "
                    + "       Juridica jur, "
                    + "       CnaeConvencao cnaeCon "
                    + " where jur.pessoa.id = pes.id "
                    + "   and cnaeCon.cnae.id = jur.cnae.id order by pes.nome ASC ");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List listaContabilidadeWeb() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select pes "
                    + "  from Pessoa pes, "
                    + "       Juridica jur "
                    + " where jur.pessoa.id = pes.id"
                    + "   and jur.id in (select j.contabilidade.id from Juridica j where j.contabilidade.id is not null) order by pes.nome ASC");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List validaPessoaRepisAno(int idPessoa, int ano) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select rm "
                    + "  from RepisMovimento rm "
                    + " where rm.pessoa.id = :idPessoa "
                    + "   and rm.ano = :ano ");
            qry.setParameter("idPessoa", idPessoa);
            qry.setParameter("ano", ano);
            result = qry.getResultList();
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public List validaPessoaRepisAnoTipoPatronal(int idPessoa, int ano, int id_tipo_certidao, int id_patronal) {
        List result = new ArrayList();
        
        try {
            Query query = getEntityManager().createNativeQuery(
                    " SELECT rm.* \n "
                    + "   FROM arr_repis_movimento rm \n "
                    + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa \n "
                    + "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal \n "
                    + "  INNER JOIN arr_certidao_tipo ct ON ct.id = rm.id_certidao_tipo \n "
                    + "  WHERE p.id = " + idPessoa + " \n "
                    + "    AND rm.nr_ano = " + ano + " \n "
                    + "	   AND pa.id = " + id_patronal + " \n "
                    + "	   AND ct.id = " + id_tipo_certidao + " \n "
                    + "    AND rm.dt_inativacao IS NULL ",
                    RepisMovimento.class
            );

            result = query.getResultList();
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public List listaProtocolosPorContabilidade(int idPessoa, int ano) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "  select rm "
                    + "    from RepisMovimento rm "
                    + "   where rm.pessoa.id in ("
                    + "select j.pessoa.id "
                    + "from Juridica j "
                    + "where j.contabilidade.pessoa.id = :idPessoa"
                    + ") and rm.ano = :ano");
            qry.setParameter("idPessoa", idPessoa);
            qry.setParameter("ano", ano);

            result = qry.getResultList();

            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public List listaProtocolosPorPatronal(int idConvencao, int idGrupoCidade) {
        List<RepisMovimento> result = new ArrayList();
        List vetor;
        String text = " select m.id "
                + "   from arr_contribuintes_vw as c "
                + "  inner join pes_pessoa          as p  on p.id=c.id_pessoa "
                + "  inner join pes_pessoa_endereco as pe on pe.id_pessoa=p.id and id_tipo_endereco = 5 "
                + "  inner join endereco_vw         as e  on e.id=pe.id_endereco "
                + "  inner join arr_patronal        as pt on pt.id_convencao = c.id_convencao and pt.id_grupo_cidade =c.id_grupo_cidade "
                + "  inner join arr_repis_movimento as m  on m.id_pessoa=c.id_pessoa "
                + "  inner join arr_repis_status    as s  on s.id=m.id_repis_status "
                + "  where c.dt_inativacao is null "
                + "    and c.id_convencao = " + idConvencao
                + "    and c.id_grupo_cidade = " + idGrupoCidade;
        try {
            Query qry = getEntityManager().createNativeQuery(text);

            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    result.add(pesquisaCodigoRepisMovimento((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }

            return result;
        } catch (Exception e) {
            return result;
        }
    }

    /**
     * <p>
     * Mudança de estrutura.</p>
     *
     * @deprecated
     * @param id_patronal
     * @return
     */
    public List listaProtocolosPorPatronalCnae(int id_patronal) {
        List<RepisMovimento> result = new ArrayList();
        List vetor;
        String text = " select m.id "
                + "   from arr_contribuintes_vw as c "
                + "  inner join pes_pessoa          as p  on p.id = c.id_pessoa "
                + "  inner join pes_juridica        as j  on j.id_pessoa = p.id "
                + "  inner join arr_patronal        as pt on pt.id = " + id_patronal + " and  pt.id_grupo_cidade = c.id_grupo_cidade"
                + "  inner join arr_repis_movimento as m  on m.id_pessoa = c.id_pessoa "
                + "  inner join arr_repis_status    as s  on s.id = m.id_repis_status "
                + "  inner join arr_patronal_cnae   as cp on cp.id_cnae = j.id_cnae and cp.id_patronal = " + id_patronal + "  "
                + "  where c.dt_inativacao is null order by m.dt_emissao desc";
        try {
            Query qry = getEntityManager().createNativeQuery(text);

            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    result.add(pesquisaCodigoRepisMovimento((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }

            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public List<RepisMovimento> listaProtocolosPorPatronal(int idPatronal) {
        try {
            Query query = getEntityManager().createQuery("SELECT RM FROM RepisMovimento AS RM WHERE RM.patronal.id = :p1 ORDER BY RM.dataEmissao DESC");
            query.setParameter("p1", idPatronal);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Patronal pesquisaPatronalPorPessoa(int idPessoa) {
        Patronal patronal = new Patronal();
        try {
            Query qry = getEntityManager().createQuery("select p from Patronal p where p.pessoa.id = " + idPessoa);
            patronal = (Patronal) qry.getSingleResult();
        } catch (Exception e) {
        }
        return patronal;
    }

    public Patronal pesquisaPatronalPorConvGrupo(int id_convencao, int id_grupo_cidade) {
        Patronal patronal = new Patronal();
        try {
            Query qry = getEntityManager().createQuery("select p from Patronal p where p.convencao.id = " + id_convencao + " and p.grupoCidade.id = " + id_grupo_cidade);
            patronal = (Patronal) qry.getSingleResult();
        } catch (Exception e) {
        }
        return patronal;
    }

    public PisoSalarialLote pesquisaPisoSalarial(int ano, int id_patronal, int id_porte) {
        PisoSalarialLote ps = new PisoSalarialLote();
        try {
            Query qry = getEntityManager().createQuery("SELECT PS FROM PisoSalarialLote AS PS WHERE PS.ano = :p1 AND PS.patronal.id = :p2 AND PS.porte.id = :p3");
            qry.setParameter("p1", ano);
            qry.setParameter("p2", id_patronal);
            qry.setParameter("p3", id_porte);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                ps = (PisoSalarialLote) qry.getSingleResult();
            }
        } catch (Exception e) {
        }
        return ps;
    }

    public List<PisoSalarial> listaPisoSalarialLote(int id_piso_lote) {
        List<PisoSalarial> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select ps from PisoSalarial ps where ps.pisoLote.id = " + id_piso_lote + " order by ps.id");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public boolean pesquisaCnaePermitido(int id_cnae, int id_grupo_cidade) {
        List vetor;
        try {
            Query qry = getEntityManager().createNativeQuery("");
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<RepisMovimento> listaRepisMovimento(String por, String descricao) {
        List result = new ArrayList();
        try {
            String text
                    = "SELECT rm "
                    + "  FROM RepisMovimento rm ";

            if (por.equals("nome")) {
                text += " WHERE rm.pessoa.nome LIKE '%" + descricao + "%'";
            }

            Query qry = getEntityManager().createQuery(text);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return result;
    }

    public Juridica pesquisaEscritorioDaEmpresa(int id_pessoa) {
        try {
            Query qry = getEntityManager().createQuery("SELECT j.contabilidade FROM Juridica j where j.pessoa.id = " + id_pessoa);

            return (Juridica) qry.getSingleResult();
        } catch (Exception e) {
            return new Juridica();
        }
    }

    public List<Movimento> listaAcordoAberto(int id_pessoa) {
        try {
            Query qry = getEntityManager().createQuery("SELECT m FROM Movimento m WHERE m.tipoServico.id = 4 AND m.baixa IS NULL AND m.pessoa.id = " + id_pessoa);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList<Movimento>();
        }
    }

    public Patronal pesquisaPatronalPorSolicitante(int id_solicitante) {
        try {
            Query queryNative = getEntityManager().createNativeQuery(""
                    + "SELECT C.id_convencao,            "
                    + "       C.id_grupo_cidade          "
                    + "  FROM arr_contribuintes_vw AS C  "
                    + " WHERE C.id_pessoa = " + id_solicitante);
            List list = queryNative.getResultList();
            if (!list.isEmpty()) {
                Query query = getEntityManager().createQuery("SELECT PC.patronal FROM PatronalConvencao AS PC WHERE PC.convencao.id = :p1 AND PC.grupoCidade.id = :p2");
                int idConvencao = Integer.parseInt(((List) list.get(0)).get(0).toString());
                int idGrupoCidade = Integer.parseInt(((List) list.get(0)).get(1).toString());
                query.setParameter("p1", idConvencao);
                query.setParameter("p2", idGrupoCidade);
                if (!query.getResultList().isEmpty()) {
                    return (Patronal) query.getSingleResult();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<CertidaoTipo> listaCertidaoTipo() {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT ct "
                    + "   FROM CertidaoTipo ct "
                    + "  WHERE ct.ativo = true "
                    + "  ORDER BY ct.id "
            );

            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List<CertidaoDisponivel> listaCertidaoDisponivel(int id_cidade, int id_convencao) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT cd "
                    + "   FROM CertidaoDisponivel cd "
                    + "  WHERE cd.cidade.id = " + id_cidade
                    + "    AND cd.convencao.id = " + id_convencao
                    + "  ORDER BY cd.certidaoTipo.descricao "
            );
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List<ConvencaoPeriodo> listaConvencaoPeriodo(int id_cidade, int id_convencao) {
        try {
            String referencia = DataHoje.DataToArray(DataHoje.data())[2] + DataHoje.DataToArray(DataHoje.data())[1];

            Query qry = getEntityManager().createNativeQuery(
                    " SELECT cp.* FROM arr_convencao_periodo cp "
                    + "  WHERE cp.id_convencao = " + id_convencao + " "
                    + "    AND cp.id_grupo_cidade IN (SELECT gc.id_grupo_cidade FROM arr_grupo_cidades gc WHERE gc.id_cidade = " + id_cidade + ")"
                    + "    AND ( " + referencia + " >= CAST(SUBSTRING(cp.ds_referencia_inicial,4,4) || SUBSTRING(cp.ds_referencia_inicial,1,2)  AS int) "
                    + "         AND " + referencia + " <= CAST(SUBSTRING(cp.ds_referencia_final  ,4,4) || SUBSTRING(cp.ds_referencia_final  ,1,2) AS int) "
                    + "    ) ", ConvencaoPeriodo.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList<>();
    }

    public List<ConvencaoPeriodo> listaConvencaoPeriodoData(int id_cidade, int id_convencao, String referencia) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT cp.* FROM arr_convencao_periodo cp "
                    + "  WHERE cp.id_convencao = " + id_convencao + " "
                    + "    AND cp.id_grupo_cidade IN (SELECT gc.id_grupo_cidade FROM arr_grupo_cidades gc WHERE gc.id_cidade = " + id_cidade + ")"
                    + "    AND ( " + referencia + " >= CAST(SUBSTRING(cp.ds_referencia_inicial,4,4) || SUBSTRING(cp.ds_referencia_inicial,1,2)  AS int) "
                    + "         AND " + referencia + " <= CAST(SUBSTRING(cp.ds_referencia_final  ,4,4) || SUBSTRING(cp.ds_referencia_final  ,1,2) AS int) "
                    + "    ) ", ConvencaoPeriodo.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList<>();
    }

    public List<RepisMovimento> pesquisarListaLiberacao(String por, String descricao, int id_patronal, String quantidade, ObjectFiltro of) {
        descricao = AnaliseString.normalizeLower(descricao);

        String inner = "", and = "", limit = "";

        String textQry
                = " SELECT rm.* \n"
                + "   FROM arr_repis_movimento rm \n";

        inner += "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal \n"
                + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa \n";
        and += "  WHERE pa.id = " + id_patronal + " \n";

        if (!descricao.isEmpty()) {
            switch (por) {
                case "nome":
                    and += "    AND TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + descricao + "%' \n";
                    break;
                case "cnpj":
                    and += "    AND p.ds_documento LIKE '%" + descricao + "' \n";
                    break;
                case "protocolo":
                    if (Types.isInteger(descricao)) {
                        and += "    AND rm.id = " + descricao + " \n";
                    }
                    break;
                case "solicitante":
                    and += "    AND TRANSLATE(LOWER(rm.ds_solicitante)) LIKE '%" + descricao + "%' \n";
                    break;
                case "email":
                    and += "    AND TRANSLATE(LOWER(p.ds_email1)) LIKE '%" + descricao + "%' \n";
                    break;
            }
        }

        // ANO
        if (of.getBooleanAno() && of.getAno() != null) {
            and += "   AND rm.nr_ano = " + of.getAno() + " \n";
        }

        // DATA EMISSÃO
        if (of.getBooleanEmissao() && !of.getEmissaoInicial().isEmpty() && !of.getEmissaoFinal().isEmpty()) {
            and += "   AND rm.dt_emissao >= '" + of.getEmissaoInicial() + "' AND rm.dt_emissao <= '" + of.getEmissaoFinal() + "' \n";
        }

        // DATA RESPOSTA
        if (of.getBooleanResposta() && !of.getRespostaInicial().isEmpty() && !of.getRespostaFinal().isEmpty()) {
            and += "   AND rm.dt_resposta >= '" + of.getRespostaInicial() + "' AND rm.dt_resposta <= '" + of.getRespostaFinal() + "' \n";
        }

        // STATUS
        if (of.getBooleanStatus() && of.getIndexStatus() != 0) {
            inner += "  INNER JOIN arr_repis_status rs ON rs.id = rm.id_repis_status \n";
            and += "    AND rs.id = " + Integer.valueOf(of.getListaStatus().get(of.getIndexStatus()).getDescription()) + " \n";
        }

        // TIPO DE CERTIDÃO
        if (of.getBooleanTipoCertidao()) {
            and += "    AND rm.id_certidao_tipo = " + Integer.valueOf(of.getListaTipoCertidao().get(of.getIndexTipoCertidao()).getDescription()) + " \n";
        }

        // CIDADE
        if (of.getBooleanCidade() && Integer.valueOf(of.getListaCidade().get(of.getIndexCidade()).getDescription()) != 0) {
            inner += " INNER JOIN pes_pessoa_endereco pe ON pe.id_pessoa = p.id AND pe.id_tipo_endereco = 5 \n"
                    + " INNER JOIN end_endereco e ON e.id = pe.id_endereco \n"
                    + " INNER JOIN end_cidade c ON c.id = e.id_cidade \n";
            and += "   AND c.id = " + Integer.valueOf(of.getListaCidade().get(of.getIndexCidade()).getDescription()) + " \n";
        }
        
        and += "   AND rm.dt_inativacao IS NULL \n";

        if (!quantidade.equals("tudo")) {
            limit = " LIMIT " + quantidade + " \n";
        }

        textQry += inner + and + " ORDER BY rm.dt_emissao DESC, rm.id DESC, p.ds_nome DESC " + limit;

        Query qry = getEntityManager().createNativeQuery(
                textQry, RepisMovimento.class
        );
        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

//    public List<RepisMovimento> pesquisarListaSolicitacao(String por, String descricao, int id_pessoa, int id_contabilidade, int ano){
//        descricao = Normalizer.normalize(descricao, Normalizer.Form.NFD);  
//        descricao = descricao.toLowerCase().replaceAll("[^\\p{ASCII}]", "");
//        
//        String inner = "", and = "";
//        
//        String textQry = 
//                " SELECT rm.* " +
//                "   FROM arr_repis_movimento rm ";
//        
//        if (id_pessoa != -1){
//            inner += "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal "
//                   + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa ";
//            and   += "  WHERE p.id = "+id_pessoa+" AND rm.nr_ano = " + ano;
//        }else{
//            inner += "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal "
//                   + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa ";
//            and   += "  WHERE rm.id_pessoa IN (SELECT j.id_pessoa FROM pes_juridica j" +
//                     "                          WHERE j.id_contabilidade = " + id_contabilidade
//                   + "                        )"
//                   + "   AND rm.nr_ano = " + ano;
//        }
//        
//        if (!descricao.isEmpty()){
//            switch (por) {
//                case "nome":
//                    and   += "    AND TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + descricao+"%'";
//                    break;
//                case "cnpj":
//                    and   += "    AND p.ds_documento LIKE '%"+descricao+"'";
//                    break;
//                case "protocolo":
//                    and   += "    AND rm.id = "+descricao;
//                    break;
//                case "status":
//                    inner += "  INNER JOIN arr_repis_status rs ON rs.id = rm.id_repis_status ";
//                    and   += "    AND TRANSLATE(LOWER(rs.ds_descricao)) LIKE '%"+descricao+"%'";
//                    break;
//                case "solicitante":
//                    and   += "    AND TRANSLATE(LOWER(rm.ds_solicitante)) LIKE '%"+descricao+"%'";
//                    break;
//            }
//        }
//        
//        textQry += inner + and + " ORDER BY rm.dt_emissao DESC, rm.id DESC, p.ds_nome DESC";
//        
//        Query qry = getEntityManager().createNativeQuery(
//            textQry, RepisMovimento.class
//        );
//        try{
//            return qry.getResultList();
//        }catch(Exception e){
//            e.getMessage();
//        }
//        return new ArrayList();
//    }
    public List<RepisMovimento> pesquisarListaSolicitacao(String por, String descricao, int id_pessoa, int id_contabilidade) {
        descricao = Normalizer.normalize(descricao, Normalizer.Form.NFD);
        descricao = descricao.toLowerCase().replaceAll("[^\\p{ASCII}]", "");

        String inner = "", and = "";

        String textQry
                = " SELECT rm.* "
                + "   FROM arr_repis_movimento rm ";

        if (id_pessoa != -1) {
            inner += "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal "
                    + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa ";
            and += "  WHERE p.id = " + id_pessoa;
        } else {
            inner += "  INNER JOIN arr_patronal pa ON pa.id = rm.id_patronal "
                    + "  INNER JOIN pes_pessoa p ON p.id = rm.id_pessoa ";
            and += "  WHERE rm.id_pessoa IN ("
                    + "	select j.id_pessoa "
                    + "	  from pes_juridica j "
                    + "	 inner join pes_juridica jc ON jc.id_pessoa = " + id_contabilidade
                    + "	 where j.id_contabilidade = jc.id"
                    + ")";
        }

        if (!descricao.isEmpty()) {
            switch (por) {
                case "nome":
                    and += "    AND TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + descricao + "%'";
                    break;
                case "cnpj":
                    and += "    AND p.ds_documento LIKE '%" + descricao + "'";
                    break;
                case "protocolo":
                    and += "    AND rm.id = " + descricao;
                    break;
                case "status":
                    inner += "  INNER JOIN arr_repis_status rs ON rs.id = rm.id_repis_status ";
                    and += "    AND TRANSLATE(LOWER(rs.ds_descricao)) LIKE '%" + descricao + "%'";
                    break;
                case "solicitante":
                    and += "    AND TRANSLATE(LOWER(rm.ds_solicitante)) LIKE '%" + descricao + "%'";
                    break;
            }
        }

        and += " AND rm.dt_inativacao IS NULL ";

        textQry += inner + and + " ORDER BY rm.dt_emissao DESC, rm.id DESC, p.ds_nome DESC";

        Query qry = getEntityManager().createNativeQuery(
                textQry, RepisMovimento.class
        );
        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public CertidaoMensagem pesquisaCertidaoMensagem(int id_cidade, int id_certidao_tipo) {
        String textQry = ("  SELECT cm "
                + "  FROM CertidaoMensagem cm "
                + " WHERE cm.cidade.id = :pcidade "
                + "   AND cm.certidaoTipo.id = :ptipo");

        try {
            Query qry = getEntityManager().createQuery(textQry);

            qry.setParameter("pcidade", id_cidade);
            qry.setParameter("ptipo", id_certidao_tipo);
            return (CertidaoMensagem) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List listRepisPorPessoa(int idPessoa) {

        try {
            Query query = getEntityManager().createQuery("SELECT RM FROM RepisMovimento AS RM WHERE RM.pessoa.id = :pessoa");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }
}
