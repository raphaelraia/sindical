package br.com.rtools.pessoa.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CentroComercialDao extends DB {

    public List listaCentroComercial(int idTipoCentroComercial, int idJuridica) {
        try {
            Query qry = getEntityManager().createQuery(""
                    + "   SELECT CC FROM CentroComercial AS CC                          "
                    + "    WHERE CC.tipoCentroComercial.id = " + idTipoCentroComercial
                    + "      AND CC.juridica.id = " + idJuridica
                    + " ORDER BY CC.tipoCentroComercial.descricao ASC,                  "
                    + "          CC.juridica.pessoa.nome ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findEnderecoNumeroByJuridicas(String in_juridicas) {
        List result = new ArrayList();
        String textQuery = ""
                + "     SELECT pe2.id_endereco,                                 \n"
                + "            pe2.ds_numero                                    \n"
                + "       FROM pes_pessoa_endereco pe2                          \n"
                + " INNER JOIN pes_pessoa p2   ON p2.id = pe2.id_pessoa         \n"
                + " INNER JOIN pes_juridica j2 ON j2.id_pessoa = p2.id          \n"
                + " INNER JOIN pes_centro_comercial cc2 ON cc2.id_juridica = j2.id \n"
                + "      WHERE pe2.id_tipo_endereco = 5                         \n"
                + "        AND j2.id IN (" + in_juridicas + ")                  \n";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }
    
    public List findEnderecoNumeroByPessoas(String in_pessoas) {
        List result = new ArrayList();
        String textQuery = ""
                + "     SELECT pe2.id_endereco,                                 \n"
                + "            pe2.ds_numero                                    \n"
                + "       FROM pes_pessoa_endereco pe2                          \n"
                + " INNER JOIN pes_pessoa p2   ON p2.id = pe2.id_pessoa         \n"
                + " INNER JOIN pes_juridica j2 ON j2.id_pessoa = p2.id          \n"
                + " INNER JOIN pes_centro_comercial cc2 ON cc2.id_juridica = j2.id \n"
                + "      WHERE pe2.id_tipo_endereco = 5                         \n"
                + "        AND j2.id_pessoa IN (" + in_pessoas + ")                  \n";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }
}
