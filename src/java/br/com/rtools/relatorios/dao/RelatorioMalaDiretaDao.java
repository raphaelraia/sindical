package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioMalaDiretaDao extends DB {

    public List listaMalaDireta(Integer id_mala_direta_grupo) {

        List<String> listWhere = new ArrayList();

        String queryString
                = "SELECT \n "
                + "  g.ds_descricao AS grupo, \n "
                + "  p.ds_documento,\n "
                + "  p.ds_nome AS nome, \n "
                + "  e.logradouro, \n "
                + "  e.endereco, \n "
                + "  pe.ds_numero AS numero, \n "
                + "  pe.ds_complemento AS complemento, \n "
                + "  e.bairro, \n "
                + "  e.cidade, \n "
                + "  e.uf, \n "
                + "  e.cep, \n "
                + "  p.ds_telefone1, \n "
                + "  p.ds_telefone2, \n "
                + "  p.ds_telefone3, \n "
                + "  p.ds_email1, \n "
                + "  p.ds_email2, \n "
                + "  p.ds_email3 \n "
                + " FROM pes_pessoa AS p \n "
                + "INNER JOIN pes_mala_direta AS m ON m.id_pessoa = p.id \n "
                + "INNER JOIN pes_mala_direta_grupo AS g ON g.id = m.id_grupo \n "
                + " LEFT JOIN pes_fisica AS f ON f.id_pessoa = p.id \n "
                + " LEFT JOIN pes_juridica AS j ON j.id_pessoa = p.id \n "
                + "INNER JOIN pes_pessoa_endereco AS pe ON pe.id_pessoa = p.id AND ( (pe.id_tipo_endereco = 1 AND f.id IS NOT NULL) OR (pe.id_tipo_endereco = 2 AND j.id IS NOT NULL) ) \n "
                + "INNER JOIN endereco_vw AS e ON e.id = pe.id_endereco \n ";

        // WHERE
        if (id_mala_direta_grupo != -1) {
            listWhere.add(" g.id = " + id_mala_direta_grupo);
        }

        if (!listWhere.isEmpty()) {
            for (String listw : listWhere) {
                if (listw.isEmpty()) {
                    queryString += " WHERE " + listw + " \n ";
                } else {
                    queryString += " AND " + listw + " \n ";
                }
            }
        }
        // ---

        // ORDEM
        queryString += " ORDER BY p.ds_nome";
        // ---
        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }
}
