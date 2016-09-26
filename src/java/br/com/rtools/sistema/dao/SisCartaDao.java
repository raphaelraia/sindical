package br.com.rtools.sistema.dao;

import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisCarta;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisCartaDao extends DB {

    public List execute(Integer id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM SisCarta AS C WHERE C.id = :id");
            query.setParameter("id", id);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                SisCarta sisCarta = (SisCarta) query.getSingleResult();
                query = getEntityManager().createNativeQuery(sisCarta.getSql());
                list = query.getResultList();
                if (!list.isEmpty()) {
                    return list;
                }
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findByUser(Integer usuario) {
        try {
            Query query;
            if (usuario == 1) {
                query = getEntityManager().createQuery("SELECT C FROM SisCarta AS C ORDER BY C.dtSolicitacao DESC, C.titulo ASC");
            } else {
                query = getEntityManager().createQuery("SELECT C FROM SisCarta AS C WHERE C.solicitante.id = :usuario AND C.solicitante IS NULL ORDER BY C.dtSolicitacao DESC, C.titulo ASC");
                query.setParameter("usuario", usuario);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                if (!list.isEmpty()) {
                    return list;
                }
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    /**
     * Default 2; 0 codigo; 1 nome; 2 documento; 3 tipo_endereco; 4 logradouro;
     * 5 endereco; 6 numero; 7 complemento; 8 bairro; 9 cidade; 10 uf; 11 cep
     *
     * @param sql_id_pessoas
     * @return
     */
    public List findEnderecosByInPessoa(String sql_id_pessoas) {
        return findEnderecosByInPessoa(sql_id_pessoas, 2);
    }

    /**
     * 0 codigo; 1 nome; 2 documento; 3 tipo_endereco; 4 logradouro; 5 endereco;
     * 6 numero; 7 complemento; 8 bairro; 9 cidade; 10 uf; 11 cep
     *
     * @param sql_id_pessoas
     * @param tipo_endereco_id
     * @return
     */
    public List<PessoaEndereco> findEnderecosByInPessoa(String sql_id_pessoas, Integer tipo_endereco_id) {
        try {
            String queryString = ""
                    + "     SELECT PE.*                                                             \n"
                    + "	      FROM pes_pessoa_endereco 		AS PE                               \n"
                    + "	INNER JOIN pes_pessoa                   AS P  ON P.id = PE.id_pessoa        \n"
                    + "      WHERE PE.id_tipo_endereco = " + tipo_endereco_id + "                   \n"
                    + "        AND P.id IN (" + sql_id_pessoas + ")                                 \n"
                    + "        AND ds_nome <> ''                                                    \n"
                    + "   ORDER BY P.ds_nome                                                        \n";
            Query query = getEntityManager().createNativeQuery(queryString, PessoaEndereco.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
