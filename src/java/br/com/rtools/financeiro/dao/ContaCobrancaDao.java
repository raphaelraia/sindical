package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Layout;
import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class ContaCobrancaDao extends DB {

    public List pesquisaLayouts() {
        try {
            Query qry = getEntityManager().createQuery("select l from Layout l");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public Layout pesquisaLayoutId(int id) {
        Layout result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Layout.pesquisaID");
            qry.setParameter("pid", id);
            result = (Layout) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaCobranca idContaCobranca(ContaCobranca des_contaCobranca) {
        ContaCobranca result = null;
        String descricao = des_contaCobranca.getCedente().toLowerCase().toUpperCase();
        try {
            Query qry = getEntityManager().createQuery("select ced from ContaCobranca ced where UPPER(ced.contaCobranca) = :d_contaCobranca");
            qry.setParameter("d_contaCobranca", descricao);
            result = (ContaCobranca) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaCobranca pesquisaServicoCobranca(int idServicos, int idTipoServico) {
        ContaCobranca result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select c.contaCobranca "
                    + "  from ServicoContaCobranca c"
                    + " where c.tipoServico.id = :idTipo"
                    + "   and c.servicos.id = :idServicos");
            qry.setParameter("idTipo", idTipoServico);
            qry.setParameter("idServicos", idServicos);
            result = (ContaCobranca) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaCobranca pesquisaCobrancaCedente(String codCedente) {
        ContaCobranca result = new ContaCobranca();
        try {
            Query qry = getEntityManager().createQuery(
                    "select c "
                    + "  from ContaCobranca c"
                    + " where c.codCedente = :codCedente");
            qry.setParameter("codCedente", codCedente);
            result = (ContaCobranca) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaCobranca pesquisaContaCobrancaMovimento(Integer id_movimento) {
        ContaCobranca result = null;
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT cc.* "
                    + "  FROM fin_movimento AS m \n"
                    + " INNER JOIN fin_servico_conta_cobranca scc ON scc.id_servicos = m.id_servicos AND m.id_tipo_servico = scc.id_tipo_servico \n"
                    + " INNER JOIN fin_conta_cobranca AS cc ON cc.id = scc.id_conta_cobranca \n"
                    + " WHERE m.id = " + id_movimento, ContaCobranca.class
            );
            result = (ContaCobranca) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }
}
