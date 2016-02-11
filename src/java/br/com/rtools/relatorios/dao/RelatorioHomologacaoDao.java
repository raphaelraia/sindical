package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioHomologacaoDao extends DB {

    private String order;
    private Relatorios relatorios;

    public RelatorioHomologacaoDao() {
        this.order = "";
        this.relatorios = new Relatorios();
    }

    public RelatorioHomologacaoDao(Relatorios relatorios) {
        this.order = "";
        this.relatorios = relatorios;
    }

    /* <ul>
     * <li> [00] - Data Inicial Ignorar) </li>
     * <li> [01] - Data Final </li>
     * <li> [02] - Data </li>
     * <li> [03] - Hora </li>
     * <li> [04] - Empresa - CNPJ </li>
     * <li> [05] - Empresa - Nome </li>
     * <li> [06] - Funcionário - Nome </li>
     * <li> [07] - Contato </li>
     * <li> [08] - Telefone </li>
     * <li> [09] - Usuário agendador (Operador) </li>
     * <li> [10] - Homologação - Observcações </li>
     * <li> [12] - Status </li>
     * <li> [13] - Cancelamento - Usuário - Nome </li>
     * <li> [14] - Cancelamento - Motivo </li>
     * </ul>
     *
     *<strong> Parâmetros</>
     * @param relatorio
     * @param inIdEmpresas
     * @param inIdFuncionarios
     * @param tipoUsuarioOperacional
     * @param usuarioOperacional
     * @param status
     * @param filial
     * @param tCase
     * @param dateStart
     * @param dateFinish
     * @param motivoDemissao
     * @param tipoAviso
     * @param tipoAgendador
     * @param sexo
     * @param webAgendamento
     * @param idConvencao
     * @return 
     */
    public List find(String inIdEmpresas, String inIdFuncionarios, String tipoUsuarioOperacional, Integer usuarioOperacional, String inIdStatus, Integer filial, Integer tCase, String dateStart, String dateFinish, Integer motivoDemissao, Boolean tipoAviso, String tipoAgendador, String sexo, Boolean webAgendamento, Integer idConvencao) {
        List listQuery = new ArrayList();
        String queryString = ""
                + " -- RelatorioHomologacaoDao->find() \n\n "
                + " SELECT ";
        if (relatorios.getId() == 70) {

            queryString += " PPE.ds_documento AS cnpj,                          \n" /*  00 - Empresa - CNPJ                 */
                    + "      PPE.ds_nome      AS empresa,                       \n" /*  01 - Empresa  Nome                  */
                    + "      S.ds_descricao   AS status,                        \n" /*  02 - Status                         */
                    + "      count(*)         AS quantidade_status              \n";
            /* 03 - Quantidade por status          */
        } else if (relatorios.getId() == 81) {
            queryString += " A.id               AS codigo,      \n" // 00 - ID Agendamento
                    + "      A.dt_data          AS data,        \n" // 01 - Data Agendamento
                    + "      CONTR.ds_descricao AS convencao,   \n" // 02 - Convenção
                    + "      PPE.ds_documento   AS cnpj,        \n" // 03 - CNPJ Empresa
                    + "      PPE.ds_nome        AS empresa,     \n" // 04 - Nome Empresa
                    + "      FUNC.ds_documento  AS cpf,         \n" // 05 - CPF Funcionário
                    + "      FUNC.ds_nome       AS funcionario, \n" // 06 - Nome Funcionário
                    + "      PE.dt_admissao     AS admissao,    \n" // 07 - Data de Admissão
                    + "      PE.dt_demissao     AS demissao,    \n" // 08 - Data de Demissão
                    + "      D.ds_descricao     AS dispensa,    \n" // 09 - Motivo Demissão
                    + "      PROF.ds_profissao  AS funcao,      \n" // 10 - Profissão
                    + "      S.ds_descricao     AS status       \n"; // 11 - Status
        } else {
            queryString += "A.dt_data        AS data_inicial,                   \n" /*  00 - Data Inicial                   */
                    + "     A.dt_data        AS data_final,                     \n" /*  01 - Data Final                     */
                    + "     A.dt_data        AS data,                           \n" /*  02 - Data                           */
                    + "     H.ds_hora        AS hora,                           \n" /*  03 - Hora                           */
                    + "     PPE.ds_documento AS cnpj,                           \n" /*  04 - Empresa - CNPJ                 */
                    + "     PPE.ds_nome      AS empresa,                        \n" /*  05 - Empresa  Nome                  */
                    + "     FUNC.ds_nome     AS funcionario,                    \n" /*  06 - Funcionário - Nome             */
                    + "     A.ds_contato     AS contato,                        \n" /*  07 - Contato                        */
                    + "     A.ds_telefone    AS telefone,                       \n";/*  08 - Telefone                       */

            if (tipoUsuarioOperacional != null) {
                queryString += " UO.ds_nome AS usuario_operacional, \n " /*                 09 - OPERADOR */;
            } else {
                queryString += " '' AS usuario_operacional, \n " /*                         09 - OPERADOR */;
            }

            queryString += "       A.ds_obs         AS obs,                         \n" /*  10 - Observações                    */
                    + "            S.ds_descricao   AS status,                      \n" /*  11 - Status                         */
                    + "            C.dt_data        AS cancelamento_data,           \n" /*  12 - Cancelamento - Data            */
                    + "            CPU.ds_nome      AS cancelamento_usuario_nome,   \n" /*  13 - Cancelamento - Usuário - Nome  */
                    + "            C.ds_motivo      AS cancelamento_motivo          \n" /*  14 - Cancelamento - Motivo          */;
        }
        queryString += "       FROM hom_agendamento                  AS A                    \n"
                + " INNER JOIN hom_horarios       AS H       ON H.id    = A.id_horario  \n"
                + " INNER JOIN hom_status         AS S       ON S.id    = A.id_status   \n";
        if (tipoUsuarioOperacional != null) {
            queryString += "  LEFT JOIN seg_usuario        AS U       ON U.id    = A." + tipoUsuarioOperacional
                    + "  LEFT JOIN pes_pessoa         AS UO      ON UO.id   = U.id_pessoa   \n";

        }
        queryString += " INNER JOIN pes_pessoa_empresa AS PE      ON PE.id   = A.id_pessoa_empresa   \n"
                + " INNER JOIN pes_juridica       AS J       ON J.id    = PE.id_juridica \n"
                + " INNER JOIN pes_fisica         AS F       ON F.id    = PE.id_fisica   \n"
                + " INNER JOIN pes_pessoa         AS FUNC    ON FUNC.id = F.id_pessoa    \n"
                + " INNER JOIN pes_pessoa         AS PPE     ON PPE.id  = J.id_pessoa    \n"
                + "  LEFT JOIN hom_cancelamento   AS C       ON C.id_agendamento  = A.id \n"
                + "  LEFT JOIN seg_usuario        AS CU      ON CU.id   = C.id_usuario   \n"
                + "  LEFT JOIN pes_pessoa         AS CPU     ON CPU.id  = CU.id_pessoa   \n";

        if (idConvencao != null) {
            queryString += " INNER JOIN arr_contribuintes_vw AS CONTR      ON CONTR.id_juridica   = J.id AND CONTR.dt_inativacao IS NULL \n";
        } else {
            queryString += "  LEFT JOIN arr_contribuintes_vw AS CONTR      ON CONTR.id_juridica   = J.id AND CONTR.dt_inativacao IS NULL \n";
        }

        queryString += "  LEFT JOIN hom_demissao AS D ON D.id = A.id_demissao \n";
        queryString += "  LEFT JOIN pes_profissao AS PROF ON PROF.id = PE.id_funcao \n";

        if (relatorios.getQry() == null || relatorios.getQry().isEmpty()) {
            if (tCase != null) {
                if (tCase == 1) {
                    // DATA DE AGENDAMENTO ---------------
                    if (!dateStart.isEmpty() && !dateFinish.isEmpty()) {
                        listQuery.add(" A.dt_data >= '" + dateStart + "' AND A.dt_data <= '" + dateFinish + "'");
                    } else if (!dateStart.isEmpty()) {
                        listQuery.add(" A.dt_data = '" + dateStart + "'");
                    } else {
                        listQuery.add(" A.dt_data >= '01/01/1900' AND A.dt_data <= '01/01/2030'");
                    }
                } else // DATA DE DEMISSÃO ---------------
                {
                    if (!dateStart.isEmpty() && !dateFinish.isEmpty()) {
                        listQuery.add(" PE.dt_demissao >= '" + dateStart + "' AND PE.dt_demissao <= '" + dateFinish + "'");
                    } else if (!dateStart.isEmpty()) {
                        listQuery.add(" PE.dt_demissao = '" + dateStart + "'");
                    } else {
                        listQuery.add(" PE.dt_demissao >= '01/01/1900' AND PE.dt_demissao <= '01/01/2030'");
                    }
                }
            }
            if (inIdEmpresas != null) {
                listQuery.add("J.id IN ( " + inIdEmpresas + " )");
            }
            if (inIdFuncionarios != null) {
                listQuery.add("F.id IN ( " + inIdFuncionarios + ") ");
            }
            if (usuarioOperacional != null) {
                if (tipoUsuarioOperacional != null && tipoUsuarioOperacional.equals("id_agendador")) {
                    if (webAgendamento) {
                        listQuery.add("A." + tipoUsuarioOperacional + " IS NULL");
                    } else {
                        listQuery.add("A." + tipoUsuarioOperacional + " = " + usuarioOperacional);
                    }
                } else {
                    listQuery.add("A." + tipoUsuarioOperacional + " = " + usuarioOperacional);
                }
            } else if (tipoUsuarioOperacional != null && tipoUsuarioOperacional.equals("id_agendador")) {
                if (webAgendamento) {
                    listQuery.add("A." + tipoUsuarioOperacional + " IS NULL");
                } else if (usuarioOperacional != null) {
                    listQuery.add("A." + tipoUsuarioOperacional + " = " + usuarioOperacional);
                }
            }
            if (filial != null) {
                listQuery.add("A.id_filial = " + filial);
            }
            if (inIdStatus != null) {
                listQuery.add("A.id_status IN ( " + inIdStatus + ") ");
            }
            if (motivoDemissao != null) {
                listQuery.add("A.id_demissao = " + motivoDemissao);
            }
            if (sexo != null && !sexo.isEmpty()) {
                listQuery.add("F.ds_sexo = '" + sexo + "'");
            }
            if (tipoAviso != null) {
                if (tipoAviso) {
                    listQuery.add("PE.aviso_trabalhado = true");
                } else {
                    listQuery.add("PE.aviso_trabalhado = false");
                }
            }
            if (idConvencao != null) {
                listQuery.add("CONTR.id_convencao = " + idConvencao);
            }
            for (int i = 0; i < listQuery.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE ";
                } else {
                    queryString += " AND ";
                }
                queryString += " " + listQuery.get(i).toString() + " \n";
            }
        } else {
            queryString += " WHERE" + relatorios.getQry() + " \n";
        }

        // ORDEM DA QRY
        if (relatorios.getId() == 70) {
            queryString += ""
                    + " GROUP BY PPE.ds_documento,  \n"
                    + "          PPE.ds_nome,       \n"
                    + "          S.ds_descricao     \n";
            relatorios.setQryOrdem("PPE.ds_nome, PPE.ds_documento, S.ds_descricao");
        }
        if (relatorios.getQryOrdem()
                == null || relatorios.getQryOrdem().isEmpty()) {
            if (!order.isEmpty()) {
                switch (order) {
                    case "data":
                        queryString += " ORDER BY A.dt_data DESC, H.ds_hora, PPE.ds_nome";
                        break;
                    case "empresa":
                        queryString += " ORDER BY PPE.ds_nome ";
                        break;
                    case "funcionario":
                        queryString += " ORDER BY FUNC.ds_nome ";
                        break;
                    case "homologador":
                        queryString += " ORDER BY UO.ds_nome ";
                        break;
                }
            }
        } else {
            queryString += " ORDER BY " + relatorios.getQryOrdem();
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }

        return new ArrayList();
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }
}
