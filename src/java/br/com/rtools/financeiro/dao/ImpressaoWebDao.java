package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Impressao;
import br.com.rtools.financeiro.ImpressaoWeb;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ImpressaoWebDao extends DB {

    public List<ImpressaoWeb> findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT IW FROM ImpressaoWeb AS IW WHERE IW.movimento.id = :movimento_id ORDER BY IW.data DESC");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
    
    public List<ImpressaoWeb> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT IW FROM ImpressaoWeb AS IW WHERE IW.pessoa.id = :pessoa_id ORDER BY IW.data DESC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<ImpressaoWeb> findByUsuario(Integer usuario_id, Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT IW FROM ImpressaoWeb AS IW WHERE IW.movimento.id = :movimento_id ORDER BY IW.data DESC");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
