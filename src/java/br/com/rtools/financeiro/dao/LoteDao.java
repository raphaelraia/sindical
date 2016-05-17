package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LoteDao extends DB {

    public List find(Integer usuario_id, String forSearch, String description) {
        String select
                = "SELECT L "
                + "  FROM Lote L "
                + " WHERE L.rotina.id = 231 ";

        String and = "";

        if (usuario_id != -1) {
            and = "   AND L.usuario.id = " + usuario_id;
        }

        String order_by = " ORDER BY L.dtEmissao desc";

        String data = "";
        if (forSearch.equals("dias")) {
            DataHoje dh = new DataHoje();
            data = dh.decrementarMeses(2, DataHoje.data());

            and += "  AND L.dtEmissao >= :data";
        } else if (forSearch.equals("emissao")) {
            data = description;
            and += "  AND L.dtEmissao = :data";
        } else if (forSearch.equals("fornecedor")) {
            and += "  AND L.pessoa.nome LIKE '%" + description.toUpperCase() + "%'";
        } else if (forSearch.equals("documento")) {
            and += "  AND L.pessoa.documento LIKE '%" + description + "%'";
        }
        try {
            Query qry = getEntityManager().createQuery(select + and + order_by);

            if (!data.isEmpty()) {
                qry.setParameter("data", DataHoje.converte(data));
            }

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
