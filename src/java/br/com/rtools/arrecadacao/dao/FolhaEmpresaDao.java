package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
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

    public List<FolhaEmpresa> findByJuridica(Integer juridica_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT FE FROM FolhaEmpresa AS FE WHERE FE.juridica.id = :juridica_id ORDER BY FE.tipoServico.descricao ASC ");
            query.setParameter("juridica_id", juridica_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }
}
