package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class FolhaEmpresaDao extends DB {

    public FolhaEmpresa pesquisaPorPessoa(int idPessoa, int idTipoServico, String referencia) {
        FolhaEmpresa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select f"
                    + "  from FolhaEmpresa f "
                    + " where f.referencia = :r"
                    + "   and f.tipoServico.id = :t"
                    + "   and f.juridica.pessoa.id = :p");
            qry.setParameter("p", idPessoa);
            qry.setParameter("t", idTipoServico);
            qry.setParameter("r", referencia);
            result = (FolhaEmpresa) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}
