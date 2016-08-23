package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DateFilters;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioAtendimentoDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    /**
     *
     * @param in_filial
     * @param in_status
     * @param in_operacao
     * @param in_atendente
     * @param in_reserva
     * @param in_pessoas
     * @param in_empresas
     * @param listDateFilters
     * @return
     */
    public List find(
            /**
             * IN
             */
            String in_filial,
            String in_status,
            String in_operacao,
            String in_atendente,
            String in_reserva,
            String in_pessoas,
            String in_empresas,
            /**
             * DATAS
             */
            List<DateFilters> listDateFilters) {
        if (listDateFilters == null) {
            listDateFilters = new ArrayList();
        }

        String queryString = " -- RelatorioAtendimentoDao->find() \n\n"
                + "      SELECT pf.ds_nome      AS filial_nome,                 \n"
                + "             pf.ds_documento AS filial_documento,            \n"
                + "             c.ds_nome       AS pessoa_nome,                 \n"
                + "             c.ds_documento  AS pessoa_documento,            \n"
                + "             dt_emissao      AS emissao_data,                \n"
                + "             ds_hora         AS emissao_hora,                \n"
                + "             o.ds_descricao  AS operacao_descricao,          \n"
                + "             s.ds_descricao  AS status_descricao,            \n"
                + "             pa.ds_nome      AS atendente_nome,              \n"
                + "             pr.ds_nome      AS reserva_nome,                \n"
                + "             j.ds_fantasia   AS empresa_nome,                \n"
                + "             pj.ds_documento AS empresa_documento            \n"
                + "        FROM ate_movimento   AS m                            \n"
                + "  INNER JOIN ate_operacao    AS o  ON o.id  = m.id_operacao  \n"
                + "  INNER JOIN ate_status      AS s  ON s.id  = m.id_status    \n"
                + "  INNER JOIN sis_pessoa      AS c  ON c.id  = id_sis_pessoa  \n"
                + "   LEFT JOIN seg_usuario     AS u  ON u.id  = m.id_atendente \n"
                + "   LEFT JOIN pes_pessoa      AS pa ON pa.id = u.id_pessoa    \n"
                + "   LEFT JOIN seg_usuario     AS ur ON ur.id = m.id_reserva   \n"
                + "   LEFT JOIN pes_pessoa      AS pr ON pr.id = ur.id_pessoa   \n"
                + "   LEFT JOIN pes_juridica    AS j  ON j.id  = m.id_juridica  \n"
                + "   LEFT JOIN pes_pessoa      AS pj ON pj.id = j.id_pessoa    \n"
                + "   LEFT JOIN pes_filial      AS fl ON fl.id = m.id_filial    \n"
                + "   LEFT JOIN pes_juridica    AS fj ON fj.id = fl.id_filial   \n"
                + "   LEFT JOIN pes_pessoa      AS pf ON pf.id = fj.id_pessoa ";

        List listWhere = new ArrayList();

        if (relatorios.getQry() != null && !relatorios.getQry().isEmpty()) {
            listWhere.add(relatorios.getQry());
        }

        // FILIAL
        if (in_filial != null && !in_filial.isEmpty()) {
            listWhere.add("m.id_filial IN(" + in_filial + ")");
        }

        // OPERAÇÃO
        if (in_operacao != null && !in_operacao.isEmpty()) {
            listWhere.add("m.id_operacao IN(" + in_operacao + ")");
        }

        // STATUS
        if (in_status != null && !in_status.isEmpty()) {
            listWhere.add("m.id_status IN(" + in_status + ")");
        }

        // ATENDENTE
        if (in_atendente != null && !in_atendente.isEmpty()) {
            listWhere.add("m.id_atendente IN(" + in_atendente + ")");
        }

        // RESERVA
        if (in_reserva != null && !in_reserva.isEmpty()) {
            listWhere.add("m.id_reserva IN(" + in_reserva + ")");
        }

        // PESSOA
        if (in_pessoas != null && !in_pessoas.isEmpty()) {
            listWhere.add("m.id_sis_pessoa IN(" + in_pessoas + ")");
        }

        // EMPRESAS
        if (in_empresas != null && !in_empresas.isEmpty()) {
            listWhere.add("m.id_juridica IN(" + in_empresas + ")");
        }

        if (!listDateFilters.isEmpty()) {
            DateFilters emissao = DateFilters.getDateFilters(listDateFilters, "emissao");
            if (emissao != null) {
                if (emissao.getDtStart() != null && !emissao.getStart().isEmpty()) {
                    switch (emissao.getType()) {
                        case "igual":
                            listWhere.add(" p.dt_emissao = '" + emissao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" p.dt_emissao >= '" + emissao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" p.dt_emissao <= '" + emissao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!emissao.getStart().isEmpty()) {
                                listWhere.add(" p.dt_emissao BETWEEN '" + emissao.getStart() + "' AND '" + emissao.getFinish() + "'");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
//
        if (relatorioOrdem != null) {
            // queryString += " ORDER BY " + relatorioOrdem.getQuery();
            queryString += " ORDER BY pf.ds_nome, pf.ds_documento, c.ds_nome, c.ds_documento, dt_emissao ";
        }
        try {
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
