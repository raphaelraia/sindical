package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class ConvencaoDao extends DB {

    public Convencao pesquisaConvencaoDesc(String descricao) {
        Convencao result = null;
        try {
            Query qry = getEntityManager().createQuery("select con from Convencao con where con.descricao like :d_convencao");
            qry.setParameter("d_convencao", descricao);
            result = (Convencao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Convencao findByEmpresa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createNativeQuery("select c.* from arr_convencao c where c.id = (select id_convencao from arr_contribuintes_vw where id_pessoa = " + pessoa_id + ")", Convencao.class);
            return (Convencao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
