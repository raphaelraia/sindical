package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class FormaPagamentoDao extends DB {

    public List<FormaPagamento> findByBaixa(Integer baixa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT FP FROM FormaPagamento AS FP WHERE FP.baixa.id = :baixa_id");
            query.setParameter("baixa_id", baixa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
