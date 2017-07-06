package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Campeonato;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CampeonatoDao extends DB {

    public Campeonato exists(Integer evento_id, Integer modalidade_id, String descricao) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE C.id_evento = " + evento_id + " AND C.id_modalidade = " + modalidade_id + " AND func_translate(upper(trim(C.ds_titulo_complemento))) = func_translate(upper(trim('" + descricao + "')))", Campeonato.class);
            return (Campeonato) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Campeonato> findByModalidade(Integer modalidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Campeonato C WHERE C.modalidade.id = :modalidade_id ORDER BY C.tituloComplemento ASC");
            query.setParameter("modalidade_id", modalidade_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Campeonato> findAll() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT C.* FROM eve_campeonato C WHERE C.dt_inicio > current_date ORDER BY C.dt_inicio ASC, C.ds_titulo_complemento ASC", Campeonato.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
