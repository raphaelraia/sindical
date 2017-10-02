package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioLocadoraDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public List find(String groups, String in_filial, String in_genero, String mes_ano_lancamento, Boolean lancamento) {
        if (groups.toLowerCase().contains("titulo")) {
            groups = "titulo";
        } else if (groups.toLowerCase().contains("gênero")) {
            groups = "genero";
        } else if (groups.toLowerCase().contains("filial")) {
            groups = "filial";
        } else {
            groups = "titulo";            
        }
        List listWhere = new ArrayList();
        String queryString = "";
        queryString = " -- RelatorioLocadoraDao->find(Cadastral)                                        \n"
                + "    SELECT G.ds_descricao        AS genero,                                          \n"
                + "           trim(T.ds_descricao)||' ('||ds_legenda||'/'||ds_formato||')' AS titulo,   \n";
        if (groups.equals("filial")) {
            queryString += " CAST(nr_qtde AS bigint) AS qtde, \n";
        } else {
            queryString += " CASE WHEN sum(nr_qtde) IS NULL THEN 0 ELSE sum(nr_qtde) END AS qtde, \n";
        }
        queryString += " "
                + " dt_data::date         AS cadastro,            \n"
                + " ds_mes_ano_lancamento AS lancamento,          \n"
                + " ds_barras             AS codigo_barras,       ";
        if (groups.equals("filial")) {
            queryString += " P.ds_nome AS filial \n";
            listWhere.add("nr_qtde > 0");
        } else {
            queryString += " '' AS filial \n";
        }
        queryString += "      "
                + "      FROM loc_titulo AS T                               \n"
                + "INNER JOIN loc_genero AS G ON G.id=T.id_genero           \n"
                + " LEFT JOIN loc_titulo_filial AS F ON F.id_titulo=T.id    \n"
                + " LEFT JOIN pes_filial FIL ON FIL.id = F.id_filial        \n"
                + " LEFT JOIN pes_juridica J ON J.id = FIL.id_filial        \n"
                + " LEFT JOIN pes_pessoa P ON P.id = J.id_pessoa            \n";
        if (lancamento != null && lancamento) {
            listWhere.add("T.ds_mes_ano_lancamento <> '' ");
            listWhere.add("current_date BETWEEN  CAST('01/' ||  ds_mes_ano_lancamento AS date) AND (CAST('01/' ||  ds_mes_ano_lancamento AS date) + INTERVAL  '3 MONTH')::date");
        }
        if (mes_ano_lancamento != null && !mes_ano_lancamento.isEmpty()) {
            listWhere.add("T.ds_mes_ano_lancamento = '" + mes_ano_lancamento + "'");
        }
        if (in_filial != null && !in_filial.isEmpty()) {
            listWhere.add("F.id_filial IN (" + in_filial + ")");
        }
        if (in_genero != null && !in_genero.isEmpty()) {
            listWhere.add("G.id IN (" + in_genero + ")");
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        if (groups.equals("genero") || groups.equals("titulo") || groups.isEmpty()) {
            queryString += ""
                    + "  GROUP BY G.ds_descricao,                                   \n"
                    + "           T.ds_descricao,                                   \n"
                    + "           dt_data::date,                                    \n"
                    + "           ds_mes_ano_lancamento,                            \n"
                    + "           ds_barras,                                        \n"
                    + "           P.ds_nome,                                        \n"
                    + "           ds_legenda,                                       \n"
                    + "           ds_formato                                        \n";
        }

        switch (groups) {
            case "genero":
                queryString += " ORDER BY G.ds_descricao, T.ds_descricao ";
                break;
            case "filial":
                queryString += " ORDER BY P.ds_nome, T.ds_descricao ";
                break;
            default:
                queryString += " ORDER BY T.ds_descricao ";
                break;
        }
        try {
            Debugs.put("habilitaDebugQuery", queryString);
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

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }
}
