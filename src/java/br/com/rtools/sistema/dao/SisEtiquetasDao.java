package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisEtiquetas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisEtiquetasDao extends DB {

    public List execute(Integer id) {
        try {
            Query query = getEntityManager().createQuery("SELECT E FROM SisEtiquetas AS E WHERE E.id = :id");
            query.setParameter("id", id);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                SisEtiquetas sisEtiquetas = (SisEtiquetas) query.getSingleResult();
                query = getEntityManager().createNativeQuery(sisEtiquetas.getSql());
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
                query = getEntityManager().createQuery("SELECT E FROM SisEtiquetas AS E ORDER BY E.dtSolicitacao DESC, E.titulo ASC");
            } else {
                query = getEntityManager().createQuery("SELECT E FROM SisEtiquetas AS E WHERE E.solicitante.id = :usuario AND E.solicitante IS NULL ORDER BY E.dtSolicitacao DESC, E.titulo ASC");
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
     * @param in_pessoas
     * @return
     */
    public List findEnderecosByInPessoa(String in_pessoas) {
        return findEnderecosByInPessoa(in_pessoas, 2);
    }

    /**
     * 0 codigo; 1 nome; 2 documento; 3 tipo_endereco; 4 logradouro; 5 endereco;
     * 6 numero; 7 complemento; 8 bairro; 9 cidade; 10 uf; 11 cep
     *
     * @param in_pessoas
     * @param tipo_endereco_id
     * @return
     */
    public List findEnderecosByInPessoa(String in_pessoas, Integer tipo_endereco_id) {
        try {
            String queryString = ""
                    + "     SELECT P.id 			AS codigo,      \n"
                    + "            UPPER(P.ds_nome) 		AS nome,        \n"
                    + "            P.ds_documento 		AS documento,   \n"
                    + "            UPPER(TE.ds_descricao)	AS tipo_endereco,\n"
                    + "            UPPER(L.ds_descricao) 	AS logradouro,  \n"
                    + "            UPPER(DE.ds_descricao) 	AS endereco,    \n"
                    + "            UPPER(PE.ds_numero)   	AS numero,      \n"
                    + "            UPPER(PE.ds_complemento)     AS complemento, \n"
                    + "            UPPER(B.ds_descricao) 	AS bairro,      \n"
                    + "            UPPER(C.ds_cidade) 		AS cidade,      \n"
                    + "            UPPER(C.ds_uf) 		AS uf,          \n"
                    + "            UPPER(E.ds_cep)		AS cep          \n"
                    + "	      FROM pes_pessoa 		AS P                    \n"
                    + " INNER JOIN pes_pessoa_endereco 	AS PE ON P.id   = PE.id_pessoa              \n"
                    + " INNER JOIN end_endereco 	AS E  ON E.id 	= PE.id_endereco            \n"
                    + " INNER JOIN end_logradouro 	AS L  ON L.id   = E.id_logradouro           \n"
                    + " INNER JOIN end_descricao_endereco  DE ON DE.id  = E.id_descricao_endereco   \n"
                    + " INNER JOIN end_bairro 		AS B  ON B.id   = E.id_bairro               \n"
                    + " INNER JOIN end_cidade 		AS C  ON C.id   = E.id_cidade               \n"
                    + " INNER JOIN pes_tipo_endereco	AS TE ON TE.id  = PE.id_tipo_endereco       \n"
                    + "      WHERE TE.id = " + tipo_endereco_id + "                                 \n"
                    + "        AND P.id IN (" + in_pessoas + ")                                     \n"
                    + "        AND ds_nome <> ''                                                    \n"
                    + "   ORDER BY C.ds_cidade,                                                     \n"
                    + "            C.ds_uf,                                                         \n"
                    + "            P.ds_nome                                                        \n";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
