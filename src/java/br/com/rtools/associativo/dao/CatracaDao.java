/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Catraca;
import br.com.rtools.associativo.CatracaLiberaAcesso;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Departamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class CatracaDao extends DB {

    public List<Catraca> listaCatraca() {
        try {
            Query qry = getEntityManager().createQuery("SELECT c FROM Catraca c ORDER BY c.departamento.descricao");

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Departamento> listaDepartamentoPorCatraca() {
        try {
            Query qry = getEntityManager().createQuery("SELECT c.departamento FROM Catraca c");
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Catraca> listaCatracaDepartamento(Integer id_departamento) {
        try {
            Query qry = getEntityManager().createQuery("SELECT c FROM Catraca c WHERE c.departamento.id = :id_departamento");
            qry.setParameter("id_departamento", id_departamento);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<CatracaLiberaAcesso> listaPesquisaLiberacaoCatraca(String dt_inicial, String dt_final, Integer id_pessoa, Integer id_departamento) {
        String text_qry
                = "SELECT cla.* \n "
                + "  FROM soc_catraca_libera_acesso cla \n"
                + " INNER JOIN seg_departamento d ON d.id = cla.id_departamento \n "
                + " INNER JOIN pes_pessoa p ON p.id = cla.id_pessoa \n ";
        try {
            List<String> list_where = new ArrayList();

            if (dt_inicial != null && dt_final == null) {
                list_where.add(" cla.dt_liberacao >= '" + dt_inicial + "'");
            } else if (dt_inicial != null && dt_final != null) {
                list_where.add(" cla.dt_liberacao >= '" + dt_inicial + "' AND cla.dt_liberacao <= '" + dt_final + "'");
            } else if (dt_inicial == null && dt_final != null) {
                list_where.add(" cla.dt_liberacao <= '" + dt_final + "'");
            }

            if (id_pessoa != null) {
                list_where.add(" cla.id_pessoa = " + id_pessoa);
            }

            if (id_departamento != null) {
                list_where.add(" cla.id_departamento = " + id_departamento);
            }

            for (int i = 0; i < list_where.size(); i++) {
                String get = list_where.get(i);
                if (i == 0) {
                    text_qry += " WHERE " + get + " \n ";
                } else {
                    text_qry += " AND " + get + " \n ";
                }
            }
            
            text_qry += " ORDER BY d.ds_descricao, cla.dt_liberacao, cla.ds_hora_liberacao, p.ds_nome";

            Query qry = getEntityManager().createNativeQuery(text_qry, CatracaLiberaAcesso.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
