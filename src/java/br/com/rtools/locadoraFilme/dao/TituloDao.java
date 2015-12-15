package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.locadoraFilme.Titulo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class TituloDao extends DB {

    private String not_in;

    public TituloDao() {
        not_in = null;
    }

    public Boolean exists(String descricao) {
        try {
            String queryString = " -- TituloDao()->exists() \n\n"
                    + "     SELECT T.*              \n"
                    + "       FROM loc_titulo AS T  \n"
                    + "      WHERE func_translate(UPPER(T.ds_descricao)) LIKE func_translate(UPPER('" + descricao.trim() + "')) \n";
            Query query = getEntityManager().createNativeQuery(queryString, Titulo.class);
            List list = query.getResultList();
            return !list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Se alterar aqui alterar tambem em catalogoDao
     *
     * @param por
     * @param como
     * @param descricao
     * @param genero_id
     * @param faixa_etaria_inicial
     * @param faixa_etaria_final
     * @return
     */
    public List find(String por, String como, String descricao, Integer genero_id, Integer faixa_etaria_inicial, Integer faixa_etaria_final) {
        return find(null, por, como, descricao, genero_id, faixa_etaria_inicial, faixa_etaria_final);
    }

    /**
     * cao, Integer genero_id, Integer faixa_etaria_inicial, Integer
     * faixa_etaria_final); } /** Se alterar aqui alterar tambem em catalogoDao
     *
     * @param filial_id
     * @param por
     * @param como
     * @param descricao
     * @param genero_id
     * @param faixa_etaria_inicial
     * @param faixa_etaria_final
     * @return
     */
    public List find(Integer filial_id, String por, String como, String descricao, Integer genero_id, Integer faixa_etaria_inicial, Integer faixa_etaria_final) {
        List listQuery = new ArrayList();
        String queryString = " -- TituloDao()->find() \n\n"
                + "     SELECT T.*              \n"
                + "       FROM loc_titulo AS T  \n";
        if (filial_id != null) {
            queryString += " INNER JOIN loc_titulo_filial AS TF ON TF.id_titulo = T.id ";
            listQuery.add("TF.id_filial = " + filial_id);
        }
        switch (como) {
            case "T":
                listQuery.add("func_translate(UPPER(T.ds_" + por + ")) LIKE func_translate(UPPER('" + descricao + "'))");
                break;
            case "P":
                listQuery.add("func_translate(UPPER(T.ds_" + por + ")) LIKE func_translate(UPPER('%" + descricao + "%'))");
                break;
            case "I":
                listQuery.add("func_translate(UPPER(T.ds_" + por + ")) LIKE func_translate(UPPER('" + descricao + "%'))");
                break;
        }

        if (genero_id != null) {
            listQuery.add("T.id_genero = " + genero_id);
        }
        if (faixa_etaria_inicial > 0 && faixa_etaria_inicial == 0) {
            listQuery.add("T.nr_idade_minima = " + faixa_etaria_inicial);
        } else if (faixa_etaria_inicial > 0 && faixa_etaria_inicial > 0 && faixa_etaria_final > faixa_etaria_inicial) {
            listQuery.add("T.nr_idade_minima BETWEEN " + faixa_etaria_inicial + " AND " + faixa_etaria_final);
        }
        if (this.not_in != null) {
            listQuery.add("T.ds_barras NOT IN ('" + this.not_in + "') ");
        }
        for (int i = 0; i < listQuery.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listQuery.get(i).toString();
            } else {
                queryString += " AND " + listQuery.get(i).toString();
            }
            queryString += " \n";
        }
        queryString += " ORDER BY T.ds_descricao ASC ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Titulo.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Titulo findBarras(Integer filial_id, String barras) {
        try {
            List list = find(filial_id, "barras", "T", barras, null, 0, 0);
            if (!list.isEmpty()) {
                return (Titulo) list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public String getNot_in() {
        return not_in;
    }

    public void setNot_in(String not_in) {
        this.not_in = not_in;
    }

}
