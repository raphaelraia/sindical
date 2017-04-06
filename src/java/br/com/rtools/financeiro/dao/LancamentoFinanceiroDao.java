package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.CentroCusto;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ContaOperacao;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LancamentoFinanceiroDao extends DB {

    public List<TipoDocumento> listaTipoDocumento() {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT tp "
                    + "  FROM TipoDocumento tp "
                    + " WHERE tp.id in (1,2,4)"
            );
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    public Juridica pesquisaJuridica(String documento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT j "
                    + "  FROM Juridica j "
                    + " WHERE j.pessoa.documento like '" + documento + "%'"
            );
            return (Juridica) qry.getSingleResult();
        } catch (Exception e) {

        }
        return null;
    }

    public Fisica pesquisaFisica(String documento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT f "
                    + "  FROM Fisica f "
                    + " WHERE f.pessoa.documento like '" + documento + "%'"
            );
            return (Fisica) qry.getSingleResult();
        } catch (Exception e) {

        }
        return null;
    }

    public List<CentroCusto> listaCentroCusto(int id_filial) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT cc "
                    + "  FROM CentroCusto cc "
                    + " WHERE cc.filial.id = " + id_filial
            );
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    public List<Plano5> listaComboPagamentoBaixa() {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT pl5 FROM Plano5 pl5 WHERE pl5.contaBanco IS NOT NULL"
            );
            return qry.getResultList();
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public ChequePag pesquisaChequeConta(String numero, int id_plano) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT ch FROM ChequePag ch WHERE ch.plano5.id = " + id_plano + " AND ch.cheque = '" + numero + "'"
            );
            return (ChequePag) qry.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public Boolean estornarTipoConta(Integer id_baixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT count(*) \n"
                    + "  FROM ( \n"
                    + "	SELECT id_pessoa, id_baixa \n"
                    + "	  FROM fin_movimento \n"
                    + "	 WHERE is_ativo = true \n"
                    + "	   AND id_baixa IS NOT NULL\n"
                    + "	   AND ds_es = 'S' \n"
                    + "	   AND id_baixa = 1 \n"
                    + "	 GROUP BY id_pessoa, id_baixa \n"
                    + ") AS bb \n"
                    + "GROUP BY id_baixa \n"
                    + "HAVING count(*) > 1"
            );
            
            List<Object> result = qry.getResultList();
            return result.get(0) == null ? false : (Integer) result.get(0) == 1;
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

//   
//    public List<ContaOperacao> listaContaOperacaoContabil(int id_centro_custo_contabil_sub) {
//        try {
//            Query qry = getEntityManager().createQuery(
//                    "SELECT co "+
//                    "  FROM ContaOperacao co " +
//                    " WHERE co.centroCustoContabilSub.id = " + id_centro_custo_contabil_sub
//            );
//            return qry.getResultList();
//        } catch (Exception e) {
//            
//        }
//        return new ArrayList<ContaOperacao>();
//    }    
}
