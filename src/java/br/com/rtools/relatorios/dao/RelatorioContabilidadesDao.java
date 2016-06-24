package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class RelatorioContabilidadesDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioContabilidadesDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioContabilidadesDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List pesquisaContabilidades() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select distinct jur.contabilidade "
                    + "  from Juridica jur"
                    + " where jur.contabilidade is not null"
                    + " order by jur.contabilidade.id");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaQntEmpresas(int id_contabilidade) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select count(jur) "
                    + "  from Juridica jur"
                    + " where jur.contabilidade.id = " + id_contabilidade);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public int quantidadeEmpresas() {
        int quantidade = 0;
        try {
            Query qry = getEntityManager().createNativeQuery(""
                    + "    SELECT COUNT(*) "
                    + "      FROM arr_contribuintes_vw AS C "
                    + "     WHERE C.dt_inativacao IS NULL "
                    + "       AND C.id_contabilidade > 0 "
                    + "  GROUP BY C.id_contabilidade "
                    + "  ORDER BY COUNT(*) DESC "
                    + "     LIMIT 1");
            List list = qry.getResultList();
            quantidade = Integer.parseInt(((List) list.get(0)).get(0).toString());
        } catch (Exception e) {
            quantidade = 0;
        }
        return quantidade;
    }

    public List pesquisarCnaeContabilidade() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select distinct j.contabilidade.cnae "
                    + "  from Juridica j"
                    + " where j.contabilidade.id is not null"
                    + " order by j.contabilidade.cnae.cnae");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List listaRelatorioContabilidades(String pEmpresas, int indexEmp1, int indexEmp2, String tipoPCidade, String in_cidade, String ordem, Integer tipo_endereco_id, String email) {
        String textQueryNativa;
        List list = new ArrayList();
        try {
            String textFrom = "";
            String textFaixa = "";
            if (ordem.equals("razao")) {
                textFrom = " , P.ds_nome ";
            }
            textQueryNativa
                    = " -- RelatorioContabilidadesDao->listaRelatorioContabilidades()     \n"
                    + "     SELECT C.id_contabilidade,                                          \n"
                    + "             PE.id as id_pessoa_endereco,                                \n"
                    + "             COUNT(*) qtde                                               \n"
                    + textFrom
                    + "        FROM arr_contribuintes_vw AS C                                   \n"
                    + "  INNER JOIN pes_juridica         AS J ON J.id = C.id_contabilidade      \n"
                    + "  INNER JOIN pes_pessoa_endereco  AS PE ON PE.id_pessoa = J.id_pessoa    \n"
                    + "  INNER JOIN end_endereco         AS E ON E.id = PE.id_endereco          \n"
                    + "  INNER JOIN endereco_vw          AS ENDE ON ENDE.id = PE.id_endereco    \n"
                    + "  INNER JOIN pes_pessoa           AS P ON P.ID = J.id_pessoa             \n"
                    + "       WHERE C.dt_inativacao IS NULL AND C.id_contabilidade > 0          \n";

            if (pEmpresas.equals("comEmpresas")) {
                textFaixa = " HAVING COUNT(C.id_contabilidade) >= " + indexEmp1 + " AND COUNT(C.id_contabilidade) <= " + indexEmp2 + " \n";
            }
            // CIDADE -------------------------------------------------------
            if (tipoPCidade.equals("todas")) {
                textQueryNativa += " AND PE.id_tipo_endereco = " + tipo_endereco_id + " \n";
            } else if (tipoPCidade.equals("especificas")) {
                if(in_cidade != null) {
                    textQueryNativa += " AND PE.id_tipo_endereco = " + tipo_endereco_id + " AND E.id_cidade IN (" + in_cidade + ") \n";                    
                }
            } else if (tipoPCidade.equals("local")) {
                textQueryNativa += " AND PE.id_tipo_endereco = " + tipo_endereco_id + " AND E.id_cidade IN (" + in_cidade + ") \n";
            } else if (tipoPCidade.equals("outras")) {
                textQueryNativa += " AND PE.id_tipo_endereco = " + tipo_endereco_id + " AND ENDE.cidade <> (SELECT ds_cidade FROM end_cidade WHERE id IN (" + in_cidade + ")) \n";
            }
            // EMAIL -------------------------------------------------------
            if (email.equals("email_sem")) {
                textQueryNativa += " AND P.ds_email1 = '' \n";
            } else if (email.equals("email_com")) {
                textQueryNativa += " AND P.ds_email1 <> '' \n";
            }
            // AGRUPAR ------------------------------------------------------------------------
            if (ordem.equals("razao")) {
                textQueryNativa += " GROUP BY C.id_contabilidade, PE.id, P.ds_nome " + textFaixa + " \n";
            } else if (ordem.equals("endereco")) {
                textQueryNativa += " GROUP BY C.id_contabilidade, PE.id, ENDE.uf, ENDE.cidade, ENDE.logradouro, ENDE.endereco " + textFaixa + " \n";
            } else if (ordem.equals("cep")) {
                textQueryNativa += " GROUP BY C.id_contabilidade, PE.id, ENDE.cep, P.ds_nome,ENDE.uf, ENDE.cidade, ENDE.logradouro,ENDE.endereco " + textFaixa + " \n";
            } else if (ordem.equals("qtde")) {
                textQueryNativa += " GROUP BY C.id_contabilidade, PE.id, ENDE.cep, P.ds_nome,ENDE.uf, ENDE.cidade, ENDE.logradouro,ENDE.endereco " + textFaixa + " \n";
            }
            // ORDEM ------------------------------------------------------------------------
            if (ordem.equals("razao")) {
                textQueryNativa = textQueryNativa + " ORDER BY P.ds_nome ASC \n";
            } else if (ordem.equals("endereco")) {
                textQueryNativa += " ORDER BY ENDE.uf  ASC,                     \n"
                        + "                   ENDE.cidade  ASC,                 \n"
                        + "                   ENDE.logradouro ASC,              \n"
                        + "                   ENDE.endereco ASC,                \n"
                        + "                   PE.ds_numero ASC                  \n";
            } else if (ordem.equals("cep")) {
                textQueryNativa += " ORDER BY ENDE.cep ASC,                     \n"
                        + "                   ENDE.uf  ASC,                     \n"
                        + "                   ENDE.cidade  ASC,                 \n"
                        + "                   ENDE.logradouro ASC,              \n"
                        + "                   ENDE.endereco ASC,                \n"
                        + "                   PE.ds_numero ASC                  \n";
            } else if (ordem.equals("qtde")) {
                textQueryNativa += " ORDER BY qtde ASC                          \n";
            }
            Query queryNativa = getEntityManager().createNativeQuery(textQueryNativa);
            list = queryNativa.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (EJBQLException e) {
            return list;
        } catch (NumberFormatException e) {
            return list;
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }
}
