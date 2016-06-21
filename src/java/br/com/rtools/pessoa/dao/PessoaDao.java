package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.JuridicaReceita;
import br.com.rtools.principal.DB;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaSemCadastro;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class PessoaDao extends DB {

    public List pesquisaTodosSemLogin() {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT P.* FROM pes_pessoa P WHERE (P.ds_login IS NULL OR P.ds_login = '') AND P.id > 0 ORDER BY P.ds_nome", Pessoa.class);
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean atualizarPessoaFisica(Fisica pessoaFisica) {
        try {
            getEntityManager().merge(pessoaFisica.getPessoa());
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Pessoa ultimoId() {
        Pessoa result = null;
        try {
            Query qry = getEntityManager().createQuery("select max(pes.id) from Pessoa pes ");
            result = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;

    }

    public List pesquisarPessoa(String desc, String por, String como) {
        String field = por;
        if (por.equals("cpf") || por.equals("cnpj") || por.equals("cei")) {
            field = "documento";
        }

        String text_qry = "";
        int maxResults = 300;
        if (por.equals("codigo")) {
            text_qry = " SELECT p.* "
                    + "   FROM pes_pessoa p "
                    + "  WHERE p.id = " + Integer.valueOf(desc)
                    + "  ORDER BY p.ds_nome";
        } else {
            if (desc.length() == 1) {
                maxResults = 50;
            } else if (desc.length() == 2) {
                maxResults = 150;
            } else if (desc.length() == 3) {
                maxResults = 200;
            }

            desc = AnaliseString.normalizeLower(desc);
            desc = (como.equals("I") ? desc + "%" : "%" + desc + "%");

            text_qry = " SELECT p.* "
                    + "   FROM pes_pessoa p "
                    + "  WHERE LOWER(FUNC_TRANSLATE(p.ds_" + field + ")) LIKE '" + desc + "' "
                    + "  ORDER BY p.ds_nome";
        }

        try {
            Query qry = getEntityManager().createNativeQuery(text_qry, Pessoa.class);
            qry.setMaxResults(maxResults);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List pessoasPermitidas(
            int idGrupo,
            int idConvencao) {
        try {
            Query qry = getEntityManager().createQuery(
                    " select j.pessoa                                            "
                    + "   from Juridica j,                                         "
                    + "        Contribuintes c,                                    "
                    + "        PessoaEndereco pe,                                  "
                    + "        CnaeConvencao cc,                                   "
                    + "        GrupoCidades gc                                     "
                    + "  where c.juridica.id = j.id                                "
                    + "    and cc.cnae.id = j.cnae.id                              "
                    + "    and cc.convencao.id = :c                                "
                    + "    and pe.pessoa.id = j.pessoa.id                          "
                    + "    and pe.tipoEndereco.id = 5                              "
                    + "    and pe.endereco.cidade.id = gc.cidade.id                "
                    + "    and gc.grupoCidade.id = :g                              ");
            qry.setParameter("g", idGrupo);
            qry.setParameter("c", idConvencao);
            return qry.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List pessoasSemMovimento(
            List idPessoa,
            String idRef,
            int idTipoServ,
            int idServicos) {
        int i = 0;
        String texto = "";

        try {
            texto = " select j.pessoa                                            "
                    + "   from Juridica j                                          "
                    + "  where j.pessoa.id not in (select mov.pessoa.id            "
                    + "                              from Movimento mov            "
                    + "                             where mov.referencia = :r      "
                    + "                               and mov.tipoServico.id = :t  "
                    + "                               and mov.servicos.id = :s)    "
                    + "                               and mov.loteBaixa is null    "
                    + "                               and mov.ativo = 1            "
                    + "        and j.pessoa.id  in (";

            while (i < idPessoa.size()) {
                if ((i == idPessoa.size()) || (idPessoa.size() == 1)) {
                    texto += ((Pessoa) idPessoa.get(i)).getId();
                } else if (i < (idPessoa.size() - 1)) {
                    texto += (((Pessoa) idPessoa.get(i)).getId() + ", ");
                } else {
                    texto += (((Pessoa) idPessoa.get(i)).getId());
                }
                i++;
            }
            texto += ")";
            Query qry = getEntityManager().createQuery(texto);
            qry.setParameter("r", idRef);
            qry.setParameter("t", idTipoServ);
            qry.setParameter("s", idServicos);
            return qry.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Pessoa pessoaPermitida(int idPessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    " select j.pessoa                                            "
                    + "   from Juridica j,                                         "
                    + "        Contribuintes c,                                    "
                    + "        PessoaEndereco pe,                                  "
                    + "        CnaeConvencao cc                                    "
                    + "  where c.juridica.id = j.id                                "
                    + "    and cc.cnae.id = j.cnae.id                              "
                    + "    and pe.pessoa.id = j.pessoa.id                          "
                    + "    and pe.tipoEndereco.id = 5                              "
                    + "    and pe.pessoa.id = :p                                    ");
            qry.setParameter("p", idPessoa);
            return (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean pessoaSemMovimento(
            int idPessoa,
            String idRef,
            int idTipoServ,
            int idServicos) {
        String texto = "";
        List lista = new Vector();
        try {
            texto = "  select mov.pessoa               "
                    + "    from Movimento mov            "
                    + "   where mov.pessoa.id = :p       "
                    + "     and mov.tipoServico.id = :t  "
                    + "     and mov.servicos.id = :s     "
                    + "     and mov.referencia = :r      "
                    + "     and mov.loteBaixa is null    "
                    + "     and mov.ativo = 1            ";
            Query qry = getEntityManager().createQuery(texto);
            qry.setParameter("p", idPessoa);
            qry.setParameter("r", idRef);
            qry.setParameter("t", idTipoServ);
            qry.setParameter("s", idServicos);
            lista = qry.getResultList();
            if (!lista.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @param documento (Pode ser CPF OU RG))
     * @return
     */
    public Pessoa pessoaDocumento(String documento) {
        try {
            String queryString
                    = "    SELECT P.*                                         \n"
                    + "      FROM pes_pessoa AS P                             \n"
                    + "INNER JOIN pes_fisica AS F ON F.id_pessoa = P.id       \n"
                    + "     WHERE (P.ds_documento LIKE '" + documento + "'     \n"
                    + "        OR UPPER(translate(F.ds_rg,'./-', '')) LIKE UPPER(translate('" + documento + "','./-',''))   \n"
                    + ")";
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Pessoa) list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public JuridicaReceita pesquisaJuridicaReceita(String documento) {
        try {
            Query qry = getEntityManager().createQuery("select jr from JuridicaReceita jr where jr.documento = '" + documento + "'");
            return (JuridicaReceita) qry.getSingleResult();
        } catch (Exception e) {
            return new JuridicaReceita();
        }
    }

    public PessoaComplemento pesquisaPessoaComplementoPorPessoa(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT PC FROM PessoaComplemento AS PC WHERE PC.pessoa.id = :idPessoa");
            query.setParameter("idPessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                if (list.size() == 1) {
                    return (PessoaComplemento) query.getSingleResult();
                }
            }
        } catch (Exception e) {
        }
        return new PessoaComplemento();
    }

    public PessoaSemCadastro pesquisaPessoaSemCadastro(String documento) {
        try {
            Query query = getEntityManager().createQuery("SELECT PC FROM PessoaSemCadastro AS PC WHERE PC.documento = '" + documento + "'");
            return (PessoaSemCadastro) query.getSingleResult();
        } catch (Exception e) {
            return new PessoaSemCadastro();
        }
    }

    public Boolean updateAtualizacao(Pessoa p) {
        String queryString;
        Query query;
        try {
            getEntityManager().getTransaction().begin();
            queryString = "UPDATE pes_pessoa SET dt_atualizacao = '" + p.getDtAtualizacao() + "' WHERE id = " + p.getId();
            query = getEntityManager().createNativeQuery(queryString);
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    /**
     * Acesso web randômico (Login: contriuinte - Senha: sindical)
     *
     * @return
     */
    public Pessoa contribuinteRandon() {
        try {
            Query query = getEntityManager().createNativeQuery(""
                    + "     SELECT P.* FROM pes_pessoa AS P                     \n"
                    + "      WHERE P.id IN(                                     \n"
                    + "         SELECT id_pessoa                                \n"
                    + "           FROM arr_contribuintes_vw                     \n"
                    + "          WHERE dt_inativacao IS NULL                    \n"
                    + "     )                                                   \n"
                    + "   ORDER BY RANDOM()                                     \n"
                    + "      LIMIT 1                                            \n"
                    + "", Pessoa.class);
            return (Pessoa) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Acesso web randômico (Login: contabilidade - Senha: sindical)
     *
     * @return
     */
    public Pessoa contabilidadeRandon() {
        try {
            Query query = getEntityManager().createNativeQuery(""
                    + "     SELECT P.* FROM pes_pessoa AS P                     \n"
                    + " INNER JOIN pes_juridica AS J ON J.id_pessoa = P.id      \n"
                    + "      WHERE J.id IN(                                     \n"
                    + "         SELECT id_contabilidade                         \n"
                    + "           FROM arr_contribuintes_vw                     \n"
                    + "          WHERE dt_inativacao IS NULL                    \n"
                    + "            AND id_contabilidade IS NOT NULL             \n"
                    + "     )                                                   \n"
                    + "   ORDER BY RANDOM()                                     \n"
                    + "      LIMIT 1                                            \n"
                    + "", Pessoa.class);
            return (Pessoa) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // R28576
    public Boolean existLogin(String login) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT EXISTS(SELECT id FROM pes_pessoa WHERE func_translate(ds_login) LIKE func_translate('" + login + "'))");
            query.setMaxResults(1);
            Boolean b = (Boolean) ((List) ((List) query.getResultList()).get(0)).get(0);
            return b;
        } catch (Exception e) {
            return true;
        }
    }

    public Boolean updateRecadastro(Pessoa p) {
        String queryString;
        Query query;
        try {
            getEntityManager().getTransaction().begin();
            queryString = "UPDATE pes_pessoa SET dt_recadastro = '" + p.getRecadastroString() + "' WHERE id = " + p.getId();
            query = getEntityManager().createNativeQuery(queryString);
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }
}
