package br.com.rtools.endereco.dao;

import br.com.rtools.endereco.Logradouro;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LogradouroDao extends DB {

    public Logradouro pesquisaLogradouroPorDescricao(String descricao) {
        descricao = descricao.toLowerCase().toUpperCase();
        try {
            Query query = getEntityManager().createNativeQuery("SELECT L.* FROM end_logradouro AS L WHERE UPPER(TRANSLATE(L.ds_descricao)) = '" + AnaliseString.removerAcentos(descricao) + "'", Logradouro.class);
            List list = query.getResultList();
            if (!list.isEmpty() || list.size() == 1) {
                return (Logradouro) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Logradouro find(String descricao) {
        try {
            return (Logradouro) find(descricao, true).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean exists(String descricao) {
        return find(descricao) != null;
    }

    public List find(String descricao, Boolean filter) {
        try {
            Query query;
            if (filter) {
                query = getEntityManager().createNativeQuery("SELECT L.* FROM end_logradouro AS L WHERE TRIM(UPPER(FUNC_TRANSLATE(L.ds_descricao))) = TRIM(UPPER(FUNC_TRANSLATE('" + descricao + "'))) ", Logradouro.class);
            } else {
                query = getEntityManager().createNativeQuery("SELECT L.* FROM end_logradouro AS L WHERE TRIM(UPPER(L.ds_descricao)) = TRIM(UPPER('" + descricao + "')) ", Logradouro.class);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
