package br.com.rtools.pessoa.dao;

import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class CnaeDao extends DB {

    public List pesquisaCnae(String desc, String por, String como) {
        List vetor;
        List<Cnae> lista = new ArrayList();
        try {
            String textQry;
            if (desc.isEmpty()) {
                return lista;
            }
            if (como.equals("I")) {
                desc = desc.toLowerCase().toUpperCase() + "%";
            } else {
                desc = "%" + desc.toLowerCase().toUpperCase() + "%";
            }

            desc = desc.replace("-", "").replace(".", "").replace("/", "");

            if (por.equals("cnae")) {
                textQry = "select id from pes_cnae where TRANSLATE(ds_numero, '.-/', '') like '" + desc + "'";
            } else {
                textQry = "select id from pes_cnae where UPPER(ds_cnae) like '" + desc + "'";
            }

            Query qry = getEntityManager().createNativeQuery(textQry);
            vetor = qry.getResultList();
            Dao dao = new Dao();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    lista.add((Cnae) dao.find(new Cnae(), (Integer) ((List) vetor.get(i)).get(0)));
                }
            }
            return lista;
        } catch (EJBQLException e) {
            return new ArrayList();
        }
    }

    public Cnae idCnae(Cnae des_cnae) {
        Cnae result = null;
        String descricao = des_cnae.getCnae().toLowerCase().toUpperCase();
        try {
            Query qry = getEntityManager().createQuery("select cna from Cnae cna where UPPER(cna.cnae) = :d_cnae");
            qry.setParameter("d_cnae", descricao);
            result = (Cnae) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public PessoaEndereco pesquisarCnaePessoaEndereco(int id) {
        PessoaEndereco result;
        result = null;
        try {
            Query qry = getEntityManager().createQuery("  select pe from PessoaEndereco pe"
                    + "  where pe.pessoa.id = :d_pes "
                    + "    and pe.tipoEndereco.id = 5");
            qry.setParameter("d_pes", id);
            result = (PessoaEndereco) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Cidade pesquisarCnaeCidade(int id) {
        Cidade result;
        Query qry = getEntityManager().createQuery("select ec"
                + "  from Endereco ee,"
                + "       Cidade ec	"
                + " where ee.cidade.id = ec.id"
                + " and ee.id = :d_cid");
        qry.setParameter("d_cid", id);
        result = (Cidade) qry.getSingleResult();
        return result;
    }

    public GrupoCidade pesquisarGrupoCidadesJuridica(int id) {
        GrupoCidade result;
        result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select gr "
                    + "  from Juridica       pj,    "
                    + "       GrupoCidades   grs,   "
                    + "       PessoaEndereco ende,  "
                    + "       Endereco       ender, "
                    + "       Cidade         cid,   "
                    + "       GrupoCidade    gr     "
                    + " where pj.id = :id_jur       "
                    + "   and pj.pessoa.id         = ende.pessoa.id "
                    + "   and ende.tipoEndereco.id = 5              "
                    + "   and ende.endereco.id     = ender.id       "
                    + "   and cid.id               = ender.cidade.id"
                    + "   and grs.cidade.id        = cid.id         "
                    + "   and grs.grupoCidade.id   = gr.id          ");
            qry.setParameter("id_jur", id);
            result = (GrupoCidade) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaCnaeSemConvencao(String desc) {
        List result = null;
        desc = desc.toLowerCase().toUpperCase() + "%";
        try {
            Query qry = getEntityManager().createQuery(""
                    + " SELECT C FROM Cnae AS C                                 "
                    + "  WHERE C.id NOT IN (                                    "
                    + "      SELECT CC.cnae.id                                  "
                    + "        FROM CnaeConvencao AS CC                         "
                    + "      )                                                  "
                    + "    AND (C.numero) LIKE :ds_Cnae");
            if (!desc.equals("%%") && !desc.equals("%")) {
                qry.setParameter("ds_Cnae", desc);
            } else {
                desc = "";
                qry.setParameter("ds_Cnae", desc);
            }

            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Cnae pesquisaNumeroCnae(String nr_cnae) {
        Cnae result = null;
        try {
            Query qry = getEntityManager().createQuery("select cna from Cnae cna where cna.numero = '" + nr_cnae + "'");
            result = (Cnae) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Cnae pesquisaCnaeDaReceita(String cnae) {
        Cnae result = null;
        try {
            Query qry = getEntityManager().createNativeQuery("");
            result = (Cnae) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List findAllByCnaeConvencao() {
        try {
            String queryString = ""
                    + "    SELECT C.*                                           \n"
                    + "      FROM pes_cnae AS C                                 \n"
                    + "INNER JOIN arr_cnae_convencao AS CV ON CV.id_cnae = C.id \n"
                    + "  ORDER BY C.ds_cnae ";
            Query query = getEntityManager().createNativeQuery(queryString, Cnae.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
