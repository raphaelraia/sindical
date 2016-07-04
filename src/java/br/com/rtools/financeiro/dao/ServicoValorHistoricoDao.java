package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class ServicoValorHistoricoDao extends DB {

    public List findByServicoValor(Integer servico_valor_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SVH FROM ServicoValorHistorico SVH WHERE SVH.servicoValor.id = :servico_valor_id ORDER BY SVH.dtData DESC");
            query.setParameter("servico_valor_id", servico_valor_id);
            return (query.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List findByServico(Integer servico_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SVH FROM ServicoValorHistorico SVH WHERE SVH.servico.id = :servico_id ORDER BY SVH.dtData DESC");
            query.setParameter("servico_id", servico_id);
            return (query.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

}
