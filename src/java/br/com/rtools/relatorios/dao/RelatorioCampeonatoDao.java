package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCampeonatoDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioCampeonatoDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioCampeonatoDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String inModalidades, String in_campeonatos, String status, String statusPagto, String statusCampeonato, String in_equipes) {
        // CHAMADOS 1192
        if (relatorios == null || relatorios.getId() == null) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            queryString += " -- RelatorioCampeonatoDao->find()                  \n";
            if (relatorios.getId() == 124) {
                queryString += ""
                        + " SELECT inicio,                                      \n"
                        + "        fim,                                         \n"
                        + "        id_modalidade,                               \n"
                        + "        modalidade,                                  \n"
                        + "        id_campeonato,                               \n"
                        + "        id_campeonato_agenda,                        \n"
                        + "        campeonato,                                  \n"
                        + "        id_equipe,                                   \n"
                        + "        equipe,                                      \n"
                        + "        id_responsavel,                              \n"
                        + "        responsavel,                                 \n"
                        + "        id_categoria,                                \n"
                        + "        categoria,                                   \n"
                        + "        matricula,                                   \n"
                        + "        servico,                                     \n"
                        + "        valor,                                       \n"
                        + "        inativacao,                                  \n"
                        + "        inadimplente,                                \n"
                        + "        id_dependente,                               \n"
                        + "        dependente,                                  \n"
                        + "        parentesco,                                  \n"
                        + "        valor_dependente                             \n"
                        + "   FROM campeonato_vw AS C";
            }
            if (relatorios.getId() == 123) {
                queryString += ""
                        + " SELECT inicio,                                      \n"
                        + "        fim,                                         \n"
                        + "        id_modalidade,                               \n"
                        + "        modalidade,                                  \n"
                        + "        id_campeonato,                               \n"
                        + "        id_campeonato_agenda,                        \n"
                        + "        campeonato,                                  \n"
                        + "        id_equipe,                                   \n"
                        + "        equipe,                                      \n"
                        + "        id_responsavel,                              \n"
                        + "        responsavel,                                 \n"
                        + "        id_categoria,                                \n"
                        + "        categoria,                                   \n"
                        + "        matricula,                                   \n"
                        + "        servico,                                     \n"
                        + "        valor,                                       \n"
                        + "        inativacao,                                  \n"
                        + "        inadimplente,                                \n"
                        + "        null,                                        \n"
                        + "        dependente,                                  \n"
                        + "        NULL,                                        \n"
                        + "        valor_dependente                             \n"
                        + "   FROM campeonato_resumo_vw AS C                    ";
            }
            if (relatorios.getId() == 125) {
                queryString += ""
                        + " SELECT inicio,                                      \n" // 0
                        + "        fim,                                         \n" // 1
                        + "        modalidade,                                  \n" // 2
                        + "        campeonato,                                  \n" // 3
                        + "        equipe,                                      \n" // 4
                        + "        count(id_responsavel) AS membros,            \n" // 5
                        + "        sum(valor)       AS valor,                   \n" // 6
                        + "        sum(dependente)  AS dependente,              \n" // 7
                        + "        sum(func_nulldouble(valor_dependente)) AS valor_dependente, \n" // 8
                        + "        sum(valor)+sum(valor_dependente) as total    \n" // 9
                        + "   FROM campeonato_resumo_vw AS C                    ";

            }
            List listWhere = new ArrayList();
            // Status do Esportista 
            if (status != null && !status.isEmpty()) {
                if (status.equals("ativo")) {
                    listWhere.add(" C.inativacao IS NULL ");
                } else if (status.equals("inativo")) {
                    listWhere.add(" C.inativacao IS NOT NULL ");
                }
            }
            // Status de pagto Esportista 
            if (statusPagto != null && !statusPagto.isEmpty()) {
                if (statusPagto.equals("adimplente")) {
                    // Adimplente  
                    listWhere.add(" C.inadimplente = false ");
                } else if (statusPagto.equals("inadimplente")) {
                    // Inadiplente 
                    listWhere.add(" C.inadimplente = true ");
                } else {
                }
            }
            if (statusCampeonato != null && !statusCampeonato.isEmpty()) {
                if (statusCampeonato.equals("ativo")) {
                    listWhere.add(" C.fim >= current_date ");
                } else if (statusCampeonato.equals("finalizado")) {
                    listWhere.add(" C.fim < CURRENT_DATE ");
                }
            }
            // MODALIDADES
            if (inModalidades != null && !inModalidades.isEmpty()) {
                listWhere.add(" C.id_modalidade IN (" + inModalidades + ")");
            }
            // CAMPEONATOS
            if (in_campeonatos != null && !in_campeonatos.isEmpty()) {
                listWhere.add(" C.id_campeonato_agenda IN (" + in_campeonatos + ")");
            }
            // EQUIPES
            if (in_equipes != null && !in_equipes.isEmpty()) {
                listWhere.add(" C.id_equipe IN (" + in_equipes + ")");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (relatorios.getId() == 125) {
                queryString += " "
                        + " GROUP BY inicio,    \n"
                        + "          fim,       \n"
                        + "          modalidade,\n"
                        + "          campeonato,\n"
                        + "          equipe     \n";
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY " + relatorioOrdem.getQuery();
            } else {
                if (relatorios.getId() == 125) {

                    queryString += " ORDER BY inicio,                           \n"
                            + "               fim,                              \n"
                            + "               modalidade,                       \n"
                            + "               campeonato,                       \n"
                            + "               equipe";
                } else {

                    queryString += " ORDER BY modalidade,                           \n"
                            + "               campeonato,                           \n"
                            + "               equipe,                               \n"
                            + "               responsavel ";
                }
            }
            Debugs.put("habilitaDebugQuery", queryString);
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

}
