package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.ConvencaoCidade;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ConvencaoCidadeDao extends DB {

    public List<ConvencaoCidade> pesquisaGrupoPorConvencao(int idConvencao) {
        try {
            Query qry = getEntityManager().createQuery("select cc from ConvencaoCidade cc"
                    + " where cc.convencao.id = :pid");
            qry.setParameter("pid", idConvencao);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisarGrupos(int idConvencao, int idGrupoCidade) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c.grupoCidade "
                    + "  from ConvencaoCidade c"
                    + " where c.convencao.id = :idCon "
                    + " and c.grupoCidade.id = :idGpCid");
            qry.setParameter("idCon", idConvencao);
            qry.setParameter("idGpCid", idGrupoCidade);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public ConvencaoCidade pesquisarConvencao(int idConvencao, int idGrupoCidade) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c "
                    + "  from ConvencaoCidade c"
                    + " where c.convencao.id = :idCon "
                    + " and c.grupoCidade.id = :idGpCid");
            qry.setParameter("idCon", idConvencao);
            qry.setParameter("idGpCid", idGrupoCidade);
            return (ConvencaoCidade) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List ListaCidadesConvencao(int idConvencao, int idGrupoCidade) {
        try {
            Query qry = getEntityManager().createQuery(
                    "        select c.id"
                    + "          from ConvencaoCidade cc, "
                    + "               GrupoCidades gc inner join gc.cidade c,"
                    + "               GrupoCidades gc2 inner join gc2.cidade c2 "
                    + "         where gc.grupoCidade.id = cc.grupoCidade.id"
                    + "           and gc2.cidade.id = c2.id "
                    + "           and gc2.grupoCidade.id = :idGpCid "
                    + "           and cc.convencao.id = :idCon"
                    + "           and c.id = c2.id"
                    + "      group by c.id");
            qry.setParameter("idCon", idConvencao);
            qry.setParameter("idGpCid", idGrupoCidade);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public GrupoCidade pesquisaGrupoCidadeJuridica(int idConvencao, int idCidade) {
        try {
            Query qry = getEntityManager().createQuery(
                    "        select gc"
                    + "          from ConvencaoCidade cc inner join cc.grupoCidade gc"
                    + "                                  inner join cc.convencao c,"
                    + "               GrupoCidades gcs inner join gcs.cidade cid"
                    + "         where gc.id = gcs.grupoCidade.id"
                    + "           and cid.id = :idCid "
                    + "           and cc.convencao.id = :idCon"
                    + "      group by gc");
            qry.setParameter("idCon", idConvencao);
            qry.setParameter("idCid", idCidade);
            return (GrupoCidade) qry.getSingleResult();
        } catch (Exception e) {
            return new GrupoCidade();
        }
    }

    public List<GrupoCidade> pesquisarGruposPorConvencao(int idConvencao) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c.grupoCidade "
                    + "  from ConvencaoCidade c"
                    + " where c.convencao.id = :idCon"
                    + " order by c.grupoCidade.descricao");
            qry.setParameter("idCon", idConvencao);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisarConvencaoPorGrupos(int idGrupoCidade) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c.convencao "
                    + "  from ConvencaoCidade c"
                    + " where c.grupoCidade.id = :idGpCid");
            qry.setParameter("idGpCid", idGrupoCidade);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List pesquisarConvencaoCidade(List<Integer> listaParametro) {
        try {
            String query = "select c.grupoCidade "
                    + "  from ConvencaoCidade c"
                    + " where c.convencao.id in (";
            int i = 0;
            while (i < listaParametro.size()) {
                query += listaParametro.get(i);
                if ((i + 1) < listaParametro.size()) {
                    query += ",";
                }
                i++;
            }
            query += ") group by c.grupoCidade";
            Query qry = getEntityManager().createQuery(query);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public ConvencaoCidade pesquisarConvencaoCidade(int idPessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    " select coci                                                "
                    + "   from Juridica j,                                         "
                    + "        Contribuintes c,                                    "
                    + "        PessoaEndereco pe,                                  "
                    + "        CnaeConvencao cc,                                   "
                    + "        GrupoCidades gc,                                    "
                    + "        ConvencaoCidade coci                                "
                    + "  where c.juridica.id = j.id                                "
                    + "    and cc.cnae.id = j.cnae.id                              "
                    + "    and cc.convencao.id = coci.convencao.id                 "
                    + "    and pe.pessoa.id = j.pessoa.id                          "
                    + "    and pe.tipoEndereco.id = 5                              "
                    + "    and pe.endereco.cidade.id = gc.cidade.id                "
                    + "    and gc.grupoCidade.id = coci.grupoCidade.id             "
                    + "    and j.pessoa.id = :pid");
            qry.setParameter("pid", idPessoa);
            return (ConvencaoCidade) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
