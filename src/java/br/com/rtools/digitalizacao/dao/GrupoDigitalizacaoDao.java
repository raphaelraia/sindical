package br.com.rtools.digitalizacao.dao;

import br.com.rtools.digitalizacao.GrupoDigitalizacao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class GrupoDigitalizacaoDao extends DB {

    public List<GrupoDigitalizacao> listaGrupo() {
        Query qry = getEntityManager().createQuery("SELECT g FROM GrupoDigitalizacao g ORDER BY g.descricao");

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<GrupoDigitalizacao> findByModulo(Integer modulo_id) {
        Query query;
        // PORQUE NA PESSOA JURÍDICA ESTA SETANDO MODULO 9 - CADASTRO AUXILIAR e não tem grupo para ele
        if (modulo_id == 9) {
            query = getEntityManager().createNativeQuery("SELECT gd.* FROM dig_grupo gd ORDER BY gd.ds_descricao", GrupoDigitalizacao.class);
        } else {
            query = getEntityManager().createNativeQuery("SELECT gd.* FROM dig_grupo gd WHERE gd.id_modulo = " + modulo_id + " ORDER BY gd.ds_descricao", GrupoDigitalizacao.class);
        }

        try {
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
