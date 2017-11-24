/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.atendimento.dao;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.atendimento.AteMovimento;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class AtendimentoDao extends DB {

    public AteMovimento pesquisaCodigoAteMovimento(int id) {
        AteMovimento ate = new AteMovimento();
        try {
            Query qry = getEntityManager().createQuery(
                    "select mov from AteMovimento mov where mov.id = " + id);
            ate = (AteMovimento) qry.getSingleResult();
        } catch (Exception e) {
        }
        return ate;
    }

    public boolean pessoaOposicao(String cpf) {
        return pessoaOposicao(cpf, null);
    }

    public boolean pessoaOposicao(String cpf, Boolean ignoraPeriodoConvencaoOposicao) {
        try {
            String data = DataHoje.livre(new Date(), "yyyyMM");
            String queryString = ""
                    + "     SELECT * "
                    + "       FROM arr_oposicao opo "
                    + " INNER JOIN arr_oposicao_pessoa pes on pes.id = opo.id_oposicao_pessoa "
                    + " INNER JOIN arr_convencao_periodo per on per.id = opo.id_convencao_periodo "
                    + "      WHERE pes.ds_cpf = '" + cpf + "' ";
            if (ignoraPeriodoConvencaoOposicao == null || !ignoraPeriodoConvencaoOposicao) {
                queryString += " AND '" + data + "' >= (substring(per.ds_referencia_inicial,4,4)||substring(per.ds_referencia_inicial,1,2)) ";
                queryString += " AND '" + data + "' <= (substring(per.ds_referencia_final,4,4)||substring(per.ds_referencia_final,1,2)) ";
            }
            queryString += " AND opo.dt_inativacao IS NULL";
            Query qry = getEntityManager().createNativeQuery(queryString);
            if (!qry.getResultList().isEmpty()) {
                return true; 
            }
        } catch (Exception e) {
                return false;
        }

        return false;
    }

    public SisPessoa pessoaDocumento(String valor) {
        String queryString = ""
                + "        select sp.*                                   "
                + "          from sis_pessoa sp                           "
                + "         where sp.ds_documento = '" + valor + "' or              "
                + "                translate(upper(sp.ds_rg),'./-', '') = translate(upper('" + valor + "'),'./-','')";

        try {
            Query query = getEntityManager().createNativeQuery(queryString, SisPessoa.class
            );
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (SisPessoa) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean existeAtendimento(AteMovimento ateMovimento) {
        try {
            Query query = getEntityManager().createQuery(" SELECT mov FROM AteMovimento mov WHERE mov.pessoa.id = :pessoa AND mov.dataEmissao = :dataEmissao AND mov.operacao.id = :operacao AND mov.filial.id = :filial ");
            query.setParameter("pessoa", ateMovimento.getPessoa().getId());
            query.setParameter("dataEmissao", ateMovimento.getDataEmissao());
            query.setParameter("operacao", ateMovimento.getOperacao().getId());
            query.setParameter("filial", ateMovimento.getFilial().getId());
            if (query.getResultList().size() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public List<AteMovimento> listaAteMovimento(String cpf, String por) {
        String strQuery;
        List result;
        if (por.equals("hoje")) {
            por = " and ate.dataEmissao = now() ";
        } else if (por.equals("ontem")) {
            por = " and ate.dataEmissao = now() ";
        } else if (por.equals("60")) {
            por = " and between now() ";
        }
        if (!cpf.equals("")) {
            strQuery = " select ate from AteMovimento ate where ate.documento = " + cpf + " " + por;
        } else {
            strQuery = " select ate from AteMovimento ate ";
        }
        try {
            //+ "order ate.dataEmissao desc, ate.ds_hora DESC "
            Query qry = getEntityManager().createQuery(strQuery + por);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList<AteMovimento>();
        }
    }

    public List listaAteMovimentos(String cpf, String por, int id_filial) {
        String porStr = "";
        String innerPes = "";
        List<AteMovimento> result = new ArrayList();
        boolean isWhere = false;
        boolean isTodos = false;
        if (por.equals("todos")) {
            isTodos = true;
        }
        if (!cpf.equals("") || (!por.equals("todos") && !por.equals(""))) {
            porStr = " where ";
            isWhere = true;
        } else {
            porStr = "";
        }
        if (por.equals("hoje")) {
            porStr += " mov.dt_emissao = current_date ";
        } else if (por.equals("ontem")) {
            porStr += " mov.dt_emissao = current_date - interval '1 days' ";
        } else if (por.equals("60")) {
            porStr += "  mov.dt_emissao between current_date - interval '2000 days' and current_date ";
        }
        if (!cpf.equals("")) {
            if (isWhere == true) {
                if (isTodos == false) {
                    porStr += " and ";
                }
            }
            innerPes = " inner join sis_pessoa pes on(pes.id = mov.id_sis_pessoa)";
            porStr += " pes.ds_documento = '" + cpf + "'";
        }

        porStr += " and mov.id_filial = " + id_filial + " ";

        String text = " select mov.id from ate_movimento mov " + innerPes + porStr;
        List list;
        try {
            Query qry = getEntityManager().createNativeQuery(text + " order by mov.dt_emissao desc, mov.ds_hora desc ");

            list = qry.getResultList();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    result.add(pesquisaCodigoAteMovimento((Integer) ((List) list.get(i)).get(0)));
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    public Senha pesquisaSenha(int id_atendimento) {
        String text_qry = "SELECT se"
                + "  FROM Senha se"
                + " WHERE se.ateMovimento.id = " + id_atendimento
                + "   AND se.agendamento IS NULL";
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return (Senha) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
