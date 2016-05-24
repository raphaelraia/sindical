package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.ConvencaoCidade;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ConvencaoCidadeDao extends DB {

    public List<ConvencaoCidade> pesquisaGrupoPorConvencao(int idConvencao) {
        try {
            Query qry = getEntityManager().createQuery("select cc from ConvencaoCidade cc"
                    + " where cc.convencao.id = :pid");
            qry.setParameter("pid", idConvencao);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
