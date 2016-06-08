package br.com.rtools.arrecadacao.dao;

import br.com.rtools.financeiro.Historico;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class AcordoDao extends DB {

    public Historico pesquisaHistorico(Integer id) {
        Historico result = null;
        try {
            Query qry = getEntityManager().createQuery("select h from Historico h where h.movimento.id = :pid");
            qry.setParameter("pid", id);
            result = (Historico) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Historico pesquisaHistoricoBaixado(int idContaCobranca, String nrBoleto, int idServico) {
        Historico result = null;
        try {
            Query qry = getEntityManager().createQuery("select h "
                    + "  from Historico h "
                    + " where h.movimento.contaCobranca.id = :idCB"
                    + "   and h.movimento.documento = :nrBo"
                    + "   and h.movimento.servicos.id = :idS"
                    + "   and h.movimento.tipoServico.id = 4");
            qry.setParameter("idCB", idContaCobranca);
            qry.setParameter("nrBo", nrBoleto);
            qry.setParameter("idS", idServico);
            result = (Historico) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

//    public Historico pesquisaHistoricoMov(int idContaCobranca, int idMovimento) {
//        Historico result = null;
//        try{
//            Query qry = getEntityManager().createQuery("select h " +
//                                                       "  from Historico h " +
//                                                       " where h.movimento.id = " +idMovimento +
//                                                       "   and h.movimento.contaCobranca.id = " + idContaCobranca);
//            result = (Historico) qry.getSingleResult();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        return result;
//    }
    public Historico pesquisaHistoricoMov(int idContaCobranca, int idMovimento) {
        Historico result = null;
        String textQuery = "";
        List vetor;
        List<Historico> list = new ArrayList();
        try {
            textQuery = "select h.id ids     "
                    + "  from fin_historico h "
                    + "  inner join fin_movimento mov on (mov.id = h.id_movimento) "
                    + "  inner join fin_boleto bol on (mov.nr_ctr_boleto = bol.nr_ctr_boleto) "
                    + " where mov.id = " + idMovimento
                    + "   and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    list.add((Historico) new Dao().find(new Historico(), (Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            if (!list.isEmpty()) {
                result = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

//    public List pesquisaTodasFolhas() {
//        try {
//            Query qry = getEntityManager().createQuery("select p from FolhaEmpresa p ");
//            return (qry.getResultList());
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public List listaHistoricoAgrupado(int id_acordo) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select h.historico from Historico h where h.movimento.id in ("
                    + " select m.id from Movimento m where m.acordo.id = " + id_acordo + " and m.ativo = true"
                    + ")group by h.historico");
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
