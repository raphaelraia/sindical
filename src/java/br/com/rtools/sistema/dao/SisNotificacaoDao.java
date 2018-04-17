package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisNotificacao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisNotificacaoDao extends DB {

    public List<SisNotificacao> findAll() {
        try {
            String queryString = "SELECT SN.* \n"
                    + "FROM sis_notificacao SN \n"
                    + "WHERE SN.dt_cadastro::date BETWEEN (SN.dt_cadastro::date - 60) AND CURRENT_DATE\n"
                    + "ORDER BY SN.dt_inicial DESC, SN.dt_final DESC";

            Query query = getEntityManager().createNativeQuery(queryString, SisNotificacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
