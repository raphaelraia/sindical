package br.com.rtools.relatorios.dao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioExameMedicoDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public List find(String type, List<DateFilters> listDateFilters, String in_departamento, String in_operador, Pessoa pessoa, SisPessoa sisPessoa) {

        List listWhere = new ArrayList();
        String queryString = " -- RelatorioExameMedicoDao()->find() \n\n"
                + "    SELECT CASE WHEN PS.id IS NOT NULL THEN 'Sócio' ELSE 'Convidado' END AS tipo, \n"
                + "           CASE WHEN PS.id IS NOT NULL THEN ps.ds_nome ELSE SP.ds_nome END AS nome, \n"
                + "           E.dt_emissao   AS emissao,                        \n"
                + "           E.dt_validade  AS validade,                       \n"
                + "           PO.ds_nome     AS operador,                       \n"
                + "           D.ds_descricao AS departamento                    \n"
                + "      FROM soc_exame_medico AS E                             \n"
                + "INNER JOIN seg_departamento AS D  ON D.id  = E.id_departamento \n"
                + "INNER JOIN seg_usuario      AS U  ON U.id  = E.id_operador   \n"
                + "INNER JOIN pes_pessoa       AS PO ON PO.id = U.id_pessoa     \n"
                + " LEFT JOIN pes_pessoa       AS PS ON PS.id = E.id_pessoa     \n"
                + " LEFT JOIN sis_pessoa       AS SP ON SP.id = E.id_sis_pessoa ";

        if (type.equals("socio")) {
            listWhere.add("E.id_pessoa IS NOT NULL");
        } else if (type.equals("convidado")) {
            listWhere.add("E.id_sis_pessoa IS NOT NULL");
        }

        if (pessoa != null && pessoa.getId() != -1) {
            listWhere.add("E.id_pessoa = " + pessoa.getId() + "");
        }
        if (sisPessoa != null && sisPessoa.getId() != -1) {
            listWhere.add("E.id_sis_pessoa = " + sisPessoa.getId() + "");
        }

        if (listDateFilters != null && !listDateFilters.isEmpty()) {

            DateFilters emissao = DateFilters.getDateFilters(listDateFilters, "emissao");
            if (emissao != null) {
                if ((emissao.getDtStart() != null && !emissao.getStart().isEmpty()) || emissao.getType().equals("com") || emissao.getType().equals("sem")) {
                    switch (emissao.getType()) {
                        case "igual":
                            listWhere.add(" E.dt_emissao = '" + emissao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" E.dt_emissao >= '" + emissao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" E.dt_emissao <= '" + emissao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!emissao.getStart().isEmpty()) {
                                listWhere.add(" E.dt_emissao BETWEEN '" + emissao.getStart() + "' AND '" + emissao.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" E.dt_emissao IS NOT NULL ");
                            break;
                        case "null":
                            listWhere.add(" E.dt_emissao IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters validade = DateFilters.getDateFilters(listDateFilters, "validade");
            if (validade != null) {
                if ((validade.getDtStart() != null && !validade.getStart().isEmpty()) || validade.getType().equals("com") || validade.getType().equals("sem")) {
                    switch (validade.getType()) {
                        case "igual":
                            listWhere.add(" E.dt_validade = '" + validade.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" E.dt_validade >= '" + validade.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" E.dt_validade <= '" + validade.getStart() + "'");
                            break;
                        case "faixa":
                            if (!validade.getStart().isEmpty()) {
                                listWhere.add(" E.dt_validade BETWEEN '" + validade.getStart() + "' AND '" + validade.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" E.dt_validade IS NOT NULL ");
                            break;
                        case "null":
                            listWhere.add(" E.dt_validade IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (in_departamento != null && !in_departamento.isEmpty()) {
            listWhere.add("E.id_departamento IN (" + in_departamento + ")");
        }
        if (in_operador != null && !in_operador.isEmpty()) {
            listWhere.add("E.id_operador IN (" + in_operador + ")");
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }

        if (relatorioOrdem != null) {
            queryString += relatorioOrdem.getQuery();
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
