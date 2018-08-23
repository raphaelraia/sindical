package br.com.rtools.estoque.dao;

import br.com.rtools.estoque.Estoque;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EstoqueDao extends DB {

    public Estoque find(Integer produto_id, Integer filial_id) {
        if (produto_id == null || filial_id == null) {
            return null;
        }
        Query query = getEntityManager().createQuery("SELECT E FROM Estoque AS E WHERE E.produto.id = :produto_id AND E.filial.id = :filial_id ORDER BY E.filial.filial.pessoa.nome");
        query.setParameter("produto_id", produto_id);
        query.setParameter("filial_id", filial_id);
        List list = query.getResultList();
        if (!list.isEmpty() && list.size() == 1) {
            return (Estoque) list.get(0);
        }
        return null;
    }

    public Estoque find(Integer produto_id, Integer filial_id, Integer tipo_estoque_id) {
        if (produto_id == null || filial_id == null || tipo_estoque_id == null) {
            return null;
        }
        Query query = getEntityManager().createQuery("SELECT E FROM Estoque AS E WHERE E.produto.id = :produto_id AND E.filial.id = :filial_id AND E.estoqueTipo.id = :tipo_estoque_id");
        query.setParameter("produto_id", produto_id);
        query.setParameter("filial_id", filial_id);
        query.setParameter("tipo_estoque_id", tipo_estoque_id);
        List list = query.getResultList();
        if (!list.isEmpty() && list.size() == 1) {
            return (Estoque) list.get(0);
        }
        return null;
    }

}
