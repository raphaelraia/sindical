package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ConfiguracaoDepartamentoDao extends DB {

    public ConfiguracaoDepartamento findBy(Integer departamento_id, Integer filial_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM ConfiguracaoDepartamento AS CD WHERE CD.departamento.id = :departamento_id AND CD.filial.id = :filial_id");
            query.setParameter("filial_id", filial_id);
            query.setParameter("departamento_id", departamento_id);
            return (ConfiguracaoDepartamento) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByFilial(Integer filial_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM ConfiguracaoDepartamento AS CD WHERE CD.filial.id = :filia_id ORDER BY CD.filial.filial.pessoa.nome ASC, CD.departamento.descricao ASC");
            query.setParameter("filial_id", filial_id);
            List list = query.getResultList();
            if (list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findByDepartamento(Integer departamento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CD FROM ConfiguracaoDepartamento AS CD WHERE CD.departamento.id = :departamento_id ORDER BY CD.departamento.descricao ASC, CD.filial.filial.pessoa.nome ASC");
            query.setParameter("departamento_id", departamento_id);
            List list = query.getResultList();
            if (list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

}
