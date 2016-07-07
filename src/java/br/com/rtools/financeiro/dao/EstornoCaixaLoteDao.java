package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EstornoCaixaLoteDao extends DB {

    public List findByUsuarioCaixa(Integer usuario_caixa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ECL FROM EstornoCaixaLote AS ECL WHERE ECL.usuarioCaixa.id = :usuario_caixa_id");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByUsuarioEstorno(Integer usuario_estorno_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ECL FROM EstornoCaixaLote AS ECL WHERE ECL.usuarioEstorno.id = :usuario_estorno_id");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
