package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.RelacaoEmpregados;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelacaoEmpregadosDao extends DB {

    public List<RelacaoEmpregados> findByRelacao(Integer relacao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM RelacaoEmpregados R  WHERE R.relacao.id = :relacao_id ORDER BY R.dtEntrega DESC");
            query.setParameter("relacao_id", relacao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
