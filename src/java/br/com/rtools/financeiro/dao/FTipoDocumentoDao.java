package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class FTipoDocumentoDao extends DB {

    public List pesquisaListaTipoExtrato() {
        try {
            Query qry = getEntityManager().createQuery("select p"
                    + "   from FTipoDocumento p "
                    + "  where p.id in (13,14,15,16,17,18,19,20,21,22,23)");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

}
