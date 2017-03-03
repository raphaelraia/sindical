package br.com.rtools.homologacao.dao;

import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class HomologacaoDao extends DB {

    public List pesquisaAgendamentoAtendimentoAberto(int idUsuario) {
        try {
            Query query = getEntityManager().createQuery("SELECT A FROM Agendamento AS A WHERE A.homologador.id = :usuario AND A.dtData < CURRENT_TIMESTAMP AND A.status.id = 5 ORDER BY A.dtData DESC, A.horarios.hora DESC ");
            query.setParameter("usuario", idUsuario);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public Agendamento pesquisaProtocolo(int id) {
        Agendamento result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT a "
                    + " FROM Agendamento a "
                    + "WHERE a.id = :pid "
                    + "  AND a.agendador is null "
                    + "  AND a.horarios is null "
            );

            qry.setParameter("pid", id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = (Agendamento) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public List pesquisaAgendado(int idFilial, Date data) {
        String dataCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            qry.setParameter("idFilial", idFilial);
            if (!qry.getResultList().isEmpty()) {
                List xxx = (qry.getResultList());
                return xxx;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaNaoAtendido(int idFilial, Date dataInicial, Date dataFinal) {
        List<Agendamento> agendamentos = new ArrayList<Agendamento>();
        String dataCampo = "";
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  ";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  ";
        }
        try {
            String textoQry = "     SELECT age.id                                      "
                    + "       FROM hom_agendamento age                         "
                    + " INNER JOIN hom_horarios hor ON hor.id = age.id_horario "
                    + "      WHERE age.id_horario IS NOT NULL                  "
                    + dataCampo
                    + "                             "
                    + "        AND age.id_status = 7                           "
                    + "        AND hor.ativo = true                            "
                    + "        AND age.id_filial = " + idFilial
                    + "   ORDER BY hor.ds_hora                                 ";
            Query qry = getEntityManager().createNativeQuery(textoQry);
            if (!qry.getResultList().isEmpty()) {
                List list = qry.getResultList();
                for (int i = 0; i < list.size(); i++) {
                    agendamentos.add((Agendamento) new Dao().find(new Agendamento(), (Integer) ((List) list.get(i)).get(0)));
                }
                return agendamentos;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaAgendadoDataMaior(Date data) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + "   and a.dtData >= :data"
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   order by a.dtData, a.horarios.hora");
            qry.setParameter("data", data);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaAgendadoPorEmpresa(Date data, int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + "   and a.dtData = :data"
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa"
                    + "   order by a.horarios.hora");
            qry.setParameter("data", data);
            qry.setParameter("idEmpresa", idEmpresa);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaAgendadoPorEmpresaSemHorario(int id_filial, Date data, int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where (a.dtData = :data or a.dtData is null)"
                    + "   and a.status.id = 2"
                    + "   and a.filial.id = :idFilial"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa"
                    + "   order by a.id");

            qry.setParameter("data", data);
            qry.setParameter("idEmpresa", idEmpresa);
            qry.setParameter("idFilial", id_filial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaAgendadoPorEmpresaDataMaior(int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.status.id = 2"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa");
            qry.setParameter("idEmpresa", idEmpresa);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaCancelado(int idFilial, Date data, int idUsuario) {
        String dataCampo = "";
        String homologadorCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        if (idUsuario != 0) {
            homologadorCampo = "   and a.homologador.id = :usuario ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 3"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + homologadorCampo
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            if (idUsuario != 0) {
                qry.setParameter("usuario", idUsuario);
            }
            qry.setParameter("idFilial", idFilial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaHomologado(int idFilial, Date data, int idUsuario) {
        String dataCampo = "";
        String homologadorCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        if (idUsuario != 0) {
            homologadorCampo = "   and a.homologador.id = :usuario ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 4"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + homologadorCampo
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            if (idUsuario != 0) {
                qry.setParameter("usuario", idUsuario);
            }
            qry.setParameter("idFilial", idFilial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaAgendamento(Integer idStatus, Integer idFilial, Date dataInicial, Date dataFinal, Integer idUsuario, Integer idPessoaFisica, Integer idPessoaJuridica, Boolean somenteAtivos, Boolean web) {

        String dataCampo = "";
        String homologadorCampo = "";
        String statusCampo = "";
        String somenteAtivosString = "";
        String innerPessoaEmpresa = "";
        String pessoaEmpresaCampo = "";
        if (idPessoaFisica > 0 || idPessoaJuridica > 0) {
            innerPessoaEmpresa = " INNER JOIN pes_pessoa_empresa pesemp ON pesemp.id = age.id_pessoa_empresa \n";
            if (idPessoaFisica > 0) {
                pessoaEmpresaCampo = " AND pesemp.id_fisica = " + idPessoaFisica + "\n";
            } else {
                pessoaEmpresaCampo = " AND pesemp.id_juridica = " + idPessoaJuridica + "\n";
            }
        }
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  \n";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  \n";
        }
        if (idUsuario != 0) {
            if (idUsuario != -1) {
                homologadorCampo = " and age.id_homologador = " + idUsuario + " \n";
            }
        }
        if (idStatus > 0) {
            statusCampo = " AND age.id_status = " + idStatus + "\n";
        }
        if (somenteAtivos) {
            somenteAtivosString = " AND hor.ativo = true \n";
        }
        try {
            String textQuery
                    = "       SELECT age.*                                      \n"
                    + "       FROM hom_agendamento age                          \n"
                    + "      INNER JOIN hom_horarios hor ON hor.id = age.id_horario\n "
                    + innerPessoaEmpresa
                    + "      WHERE age.id_horario IS NOT NULL                  \n"
                    + dataCampo
                    + homologadorCampo
                    + statusCampo
                    + pessoaEmpresaCampo
                    + somenteAtivosString
                    + "        AND age.id_filial = " + idFilial + " \n"
                    + "      ORDER BY age.dt_data DESC, hor.ds_hora ASC      \n"
                    + "      LIMIT 1000                                        \n";

            Query qry = getEntityManager().createNativeQuery(textQuery, Agendamento.class);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaAgendamentoPorProtocolo(int numeroProtocolo) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT A FROM Agendamento AS A WHERE A.id = :id ");
            qry.setParameter("id", numeroProtocolo);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaAtendimento(int idFilial, Date dataInicial, Date dataFinal, int idUsuario) {
        List<Agendamento> agendamentos = new ArrayList<Agendamento>();
        String dataCampo = "";
        String homologadorCampo = "";
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  ";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  ";
        }
        if (idUsuario != 0) {
            homologadorCampo = " and a.id_homologador = " + idUsuario + " ";
        }
        try {
            String textoQry = "     SELECT age.id                                      "
                    + "       FROM hom_agendamento age                         "
                    + " INNER JOIN hom_horarios hor ON hor.id = age.id_horario "
                    + "      WHERE age.id_horario IS NOT NULL                  "
                    + dataCampo
                    + homologadorCampo
                    + "        AND age.id_status = 5                           "
                    + "        AND hor.ativo = true                            "
                    + "        AND age.id_filial = " + idFilial
                    + "   ORDER BY hor.ds_hora                                 ";
            Query qry = getEntityManager().createNativeQuery(textoQry);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    agendamentos.add((Agendamento) new Dao().find(new Agendamento(), (Integer) ((List) list.get(i)).get(0)));
                }
                return agendamentos;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaTodos(int idFilial) {
        try {
            Query qry = getEntityManager().createQuery(""
                    + "   SELECT A "
                    + "     FROM Agendamento AS A "
                    + "    WHERE A.horarios IS NOT NULL"
                    + "      AND A.horarios.ativo = TRUE"
                    + "      AND A.filial.id = :idFilial"
                    + " ORDER BY A.horarios.hora, "
                    + "          A.dtData DESC"
                    + "    ");
            qry.setParameter("idFilial", idFilial);
            qry.setMaxResults(300);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public int pesquisaQntdDisponivel(Integer idFilial, Horarios horarios, Date data) {
        try {
            String text = " "
                    + "      SELECT CASE WHEN                                   \n"
                    + "      ( SELECT nr_quantidade                             \n"
                    + "          FROM hom_horarios                              \n"
                    + "         WHERE id = " + horarios.getId() + " ) -         \n"
                    + "      ( SELECT func_nullInteger (                        \n"
                    + "          ( SELECT nr_quantidade                         \n"
                    + "              FROM hom_cancelar_horario                  \n"
                    + "             WHERE id_horarios = " + horarios.getId() + "\n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "          )                                              \n"
                    + "        )                                                \n"
                    + "      ) -                                                \n"
                    + "    ( SELECT func_nullInteger (                          \n"
                    + "          ( SELECT CAST(COUNT(*) AS INT)                 \n"
                    + "              FROM hom_agendamento                       \n"
                    + "             WHERE id_horario = " + horarios.getId() + " \n"
                    + "               AND id_filial = " + idFilial + "          \n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "               AND id_status = 2                         \n"
                    + "          )                                              \n"
                    + "     )) -                                                \n"
                    + "    ( SELECT func_nullInteger (                          \n"
                    + "          ( SELECT CAST(count(*) AS INT)                 \n"
                    + "              FROM hom_horario_reserva                   \n"
                    + "             WHERE id_horario = " + horarios.getId() + " \n"
                    + "          )                                              \n"
                    + "     ) ) +                                               \n"
                    // INICIO TRECHO NOVO
                    + "      ( SELECT func_nullInteger (                        \n"
                    + "          ( SELECT nr_quantidade                         \n"
                    + "              FROM hom_acrescentar_horario               \n"
                    + "             WHERE id_horarios = " + horarios.getId() + "\n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "          )                                              \n"
                    + "        )                                                \n"
                    + "      )                                                  \n"
                    // FIM TRECHO NOVO
                    + "  IS NULL THEN 0 ELSE                                    \n"
                    + "      ( SELECT                                           \n"
                    + "      ( SELECT nr_quantidade                             \n"
                    + "          FROM hom_horarios                              \n"
                    + "         WHERE id = " + horarios.getId() + " ) -         \n"
                    + "      ( SELECT func_nullInteger (                        \n"
                    + "          ( SELECT nr_quantidade                         \n"
                    + "              FROM hom_cancelar_horario                  \n"
                    + "             WHERE id_horarios = " + horarios.getId() + "\n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "          )                                              \n"
                    + "        )                                                \n"
                    + "      ) -                                                \n"
                    + "    ( SELECT func_nullInteger (                          \n"
                    + "          ( SELECT CAST(COUNT(*) AS INT)                 \n"
                    + "              FROM hom_agendamento                       \n"
                    + "             WHERE id_horario = " + horarios.getId() + " \n"
                    + "               AND id_filial = " + idFilial + "          \n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "               AND id_status = 2                         \n"
                    + "          )                                              \n"
                    + "     )                                                   \n"
                    + "     ) -                                                 \n"
                    + "    ( SELECT func_nullInteger (                          \n"
                    + "          ( SELECT CAST(COUNT(*) AS INT)                 \n"
                    + "              FROM hom_horario_reserva                   \n"
                    + "             WHERE id_horario = " + horarios.getId() + " \n"
                    + "          )                                              \n"
                    + "     )) +                                                 \n"
                    // INICIO TRECHO NOVO
                    + "      ( SELECT func_nullInteger (                        \n"
                    + "          ( SELECT nr_quantidade                         \n"
                    + "              FROM hom_acrescentar_horario               \n"
                    + "             WHERE id_horarios = " + horarios.getId() + "\n"
                    + "               AND dt_data = '" + data + "'              \n"
                    + "          )                                              \n"
                    + "        )                                                \n"
                    + "      )                                                  \n"
                    // FIM TRECHO NOVO
                    + ") END;                                                   \n";
            Query qry = getEntityManager().createNativeQuery(text);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                try {
                    Integer quantidade = Integer.valueOf(String.valueOf(((List) list.get(0)).get(0)));
                    if (quantidade < 0) {
                        return 0;
                    }
                    return quantidade;
                } catch (Exception e) {
                    return 0;
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public int pesquisaQuantidadeAgendado(int idFilial, Horarios horarios, Date data) {
        try {
            String text = " SELECT count(*) FROM hom_agendamento WHERE id_horario = " + horarios.getId() + " AND id_filial = " + idFilial + " AND dt_data = '" + data + "' AND id_status = 2 ";
            Query qry = getEntityManager().createNativeQuery(text);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (Integer.valueOf(String.valueOf((Long) ((List) list.get(0)).get(0))));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return -1;
    }

    public List pesquisaTodosHorariosDisponiveis(Integer idFilial, Integer idDiaSemana) {
        return pesquisaTodosHorariosDisponiveis(idFilial, idDiaSemana, false);
    }

    public List pesquisaTodosHorariosDisponiveis(Integer idFilial, Integer idDiaSemana, Boolean web) {
        String whereString = "";
        if (web) {
            whereString = " AND H.web = true ";
        }
        try {
            Query query = getEntityManager().createQuery(
                    "     SELECT H                          "
                    + "     FROM Horarios AS H              "
                    + "    WHERE H.ativo = true             "
                    + "      AND H.filial.id = :idFilial    "
                    + "      AND H.semana.id = :idDiaSemana "
                    + whereString
                    + " ORDER BY H.hora");
            query.setParameter("idFilial", idFilial);
            query.setParameter("idDiaSemana", idDiaSemana);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public PessoaEmpresa pesquisaPessoaEmpresaPertencente(String doc) {
        PessoaEmpresa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT pem "
                    + "  FROM PessoaEmpresa pem "
                    + " WHERE pem.fisica.pessoa.documento like :Sdoc"
                    + "   AND(pem.principal = true OR pem.dtDemissao IS NULL)"
            );
            qry.setParameter("Sdoc", doc);
            return (PessoaEmpresa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaPessoaDebito(int id_pessoa, String vencimento) {
        try {
            String queryString = ""
                    + "     SELECT id                                                                               "
                    + "       FROM fin_movimento                                                                    "
                    + "      WHERE id_pessoa = " + id_pessoa
                    + "        AND dt_vencimento < '" + vencimento + "'                                             "
                    + "        AND is_ativo = true                                                                  "
                    + "        AND id_baixa IS NULL                                                                 "
                    + "        AND id_servicos IN(                                                                  "
                    + "             SELECT id_servicos                                                              "
                    + "               FROM fin_servico_rotina                                                       "
                    + "              WHERE id_rotina = 4                                                            "
                    + "   )                                                                                         "
                    + "   ORDER BY dt_vencimento                                                                    ";
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaAgendamentoPorPessoaEmpresa(int idPessoaEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.pessoaEmpresa.id = " + idPessoaEmpresa);
            if (!qry.getResultList().isEmpty()) {
                return (qry.getResultList());
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public Agendamento findByEmpresa(Integer pessoa_empresa_id) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT A.* FROM hom_agendamento AS A WHERE A.id_pessoa_empresa = " + pessoa_empresa_id + " ORDER BY A.id DESC LIMIT 1", Agendamento.class);
            return (Agendamento) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Oposicao pesquisaFisicaOposicao(String cpf, int id_juridica) {
        Oposicao result = null;
        try {
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' and o.juridica.id = " + id_juridica);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = (Oposicao) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public List<Oposicao> pesquisaFisicaOposicaoSemEmpresa(String cpf) {
        List<Oposicao> result = new ArrayList();
        try {
            String referencia = DataHoje.livre(new Date(), "yyyyMM");
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' "
                    + "   and '" + referencia + "' BETWEEN CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaInicial, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaInicial, 0, 3) ) "
                    + "   and                   CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaFinal, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaFinal, 0, 3) )"
                    + "   and o.dtInativacao IS NULL  order by o.id desc");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Oposicao pesquisaFisicaOposicaoAgendamento(String cpf, int id_juridica, String referencia) {
        Oposicao result = null;
        try {
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' "
                    + "   and o.juridica.id = " + id_juridica
                    + "   and '" + referencia + "' BETWEEN CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaInicial, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaInicial, 0, 3) ) "
                    + "   and                   CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaFinal, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaFinal, 0, 3) )"
                    + "   and o.dt_inativacao IS NULL");
            if (!qry.getResultList().isEmpty()) {
                result = (Oposicao) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public Agendamento pesquisaFisicaAgendada(int id_fisica, int id_juridica) {
        Agendamento result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT a "
                    + " FROM Agendamento a "
                    + "WHERE a.pessoaEmpresa.fisica.id = " + id_fisica + " "
                    + "  AND a.pessoaEmpresa.juridica.id = " + id_juridica + " "
                    + "  AND a.dtData >= :data "
                    + "  AND (a.status.id = 2 OR a.status.id = 5)"
            );
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Agendamento) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    public int pesquisaUltimaSenha(int id_filial) {
        int result = 0;
        try {
            Query qry = getEntityManager().createQuery("SELECT max(s.senha) FROM Senha s WHERE s.dtData = :data AND s.filial.id = " + id_filial);
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Integer) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaSenhaAgendamento(int id_agendamento) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery("SELECT S FROM Senha AS S WHERE S.agendamento.id = " + id_agendamento);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaSenhaAtendimento(int id_filial) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery("select s "
                    + " from Senha s "
                    + " where s.senha = ("
                    //+ "         select min(s2.senha) from Senha s2 where s2.dtData = :data and s2.mesa = 0 and s2.ateMovimento is null and s2.filial.id = " + id_filial + " and s2.horaChamada = ''"
                    + "         select min(s2.senha) from Senha s2 where s2.dtData = :data and s2.mesa = 0 and s2.ateMovimento is null and s2.filial.id = " + id_filial + " and s2.agendamento.status.id = 2"
                    + "                 ) "
                    + " and s.agendamento.status.id = 2 "
                    + " and s.ateMovimento is null "
                    + " and s.dtData = :data and s.filial.id = " + id_filial);
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaAtendimentoIniciado(int id_usuario, int nr_mesa, int id_filial, Integer id_departamento) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.mesa = :nr_mesa "
                    + "   AND S.agendamento.homologador.id = :id_usuario "
                    + "   AND S.ateMovimento IS NULL "
                    + "   AND S.dtData = :data"
                    + "   AND S.agendamento.status.id = 5 and S.filial.id = :id_filial"
                    + "   AND S.departamento.id = :id_departamento");
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("nr_mesa", nr_mesa);
            qry.setParameter("id_usuario", id_usuario);
            qry.setParameter("id_filial", id_filial);
            qry.setParameter("id_departamento", id_departamento);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaAtendimentoSimplesIniciado(Integer usuario_id, Integer nr_mesa, Integer filial_id, Integer departamento_id) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.mesa = :nr_mesa "
                    + "   AND S.ateMovimento.atendente.id = :usuario_id "
                    + "   AND S.agendamento IS NULL "
                    + "   AND S.dtData = :data"
                    + "   AND S.ateMovimento.status.id = 4 "
                    + "   AND S.filial.id = :filial_id"
                    + "   AND S.departamento.id = :departamento_id");
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("nr_mesa", nr_mesa);
            qry.setParameter("usuario_id", usuario_id);
            qry.setParameter("filial_id", filial_id);
            qry.setParameter("departamento_id", departamento_id);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaAtendimentoIniciadoSimples(int id_filial) {
        Senha result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.dtData = :data "
                    + "   AND S.ateMovimento.status.id = 1 "
                    + "   AND S.filial.id = :id_filial"
                    + "   AND S.ateMovimento.reserva IS NULL"
                    + " ORDER BY S.senha");

            qry.setParameter("data", DataHoje.dataHoje(), TemporalType.DATE);
            qry.setParameter("id_filial", id_filial);
            qry.setMaxResults(1);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        return result;
    }

    public boolean verificaNaoAtendidosSegRegistroAgendamento() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT *                                              "
                    + "   FROM seg_registro                                 "
                    + "  WHERE (CURRENT_DATE - 1) = dt_atualiza_homologacao ");
            if (qry.getResultList().isEmpty()) {
                getEntityManager().getTransaction().begin();
                Query qryUpdateAgendamento = getEntityManager().createNativeQuery(
                        "UPDATE hom_agendamento             "
                        + "   SET id_status = 7             "
                        + " WHERE dt_data < CURRENT_DATE    "
                        + "   AND id_status = 2");
                if (qryUpdateAgendamento.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                Query qryUpdateRegistro = getEntityManager().createNativeQuery(
                        " UPDATE seg_registro                              "
                        + "    SET dt_atualiza_homologacao = CURRENT_DATE - 1");
                if (qryUpdateRegistro.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                getEntityManager().getTransaction().commit();
            }
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    public boolean existeHorarioDisponivel(Date date, Horarios horarios) {
        String dateString = DataHoje.converteData(date);
        try {
            Query query = getEntityManager().createNativeQuery(" "
                    + "     SELECT id, nr_quantidade                                                "
                    + "       FROM hom_horarios                                                     "
                    + "      WHERE TEXT(id_filial) || TEXT(id_semana) || ds_hora = TEXT(" + horarios.getFilial().getId() + ") || ("
                    + "            EXTRACT(                                                         "
                    + "                     DOW FROM to_date('" + dateString + "', 'DD-MM-YYYY')) + 1   "
                    + "      ) || '" + horarios.getHora() + "'");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                int idHorario = (Integer) ((List) (list.get(0))).get(0);
                int quantidade = (Integer) ((List) (list.get(0))).get(1);
                list.clear();
                query = getEntityManager().createNativeQuery(""
                        + "     SELECT " + quantidade + " - (count(*) - (           "
                        + "             SELECT COUNT(*)                         "
                        + "               FROM hom_cancelar_horario             "
                        + "              WHERE id_horarios = " + idHorario + "      "
                        + "                AND dt_data='" + dateString + "')        "
                        + "     )                                               "
                        + "       FROM hom_agendamento                          "
                        + "      WHERE id_horario = " + idHorario + "               "
                        + "        AND dt_data = '" + dateString + "'               "
                        + "        AND id_status <> 3;");
                list = query.getResultList();
                if (!list.isEmpty()) {
                    quantidade = Integer.parseInt(((List) (list.get(0))).get(0).toString());
                    if (quantidade > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception exception) {
            return false;
        }
        return false;
    }

    public Cancelamento pesquisaCancelamentoPorAgendanto(int idAgendamento) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Cancelamento AS C WHERE C.agendamento.id = :agendamento");
            query.setParameter("agendamento", idAgendamento);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Cancelamento) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public List<Senha> listaAtendimentoIniciadoSimples(int id_filial, int id_usuario) {
        List<Senha> result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT s.* FROM ate_movimento a "
                    + " INNER JOIN hom_senha s on s.id_atendimento = a.id "
                    + " WHERE a.dt_emissao = '" + DataHoje.dataHoje() + "'"
                    + "   AND a.id_status = 1"
                    + "   AND s.id_filial = " + id_filial
                    + "   AND (a.id_reserva IS NULL OR a.id_reserva = " + id_usuario + ")"
                    + " ORDER BY s.nr_senha, s.dt_data",
                    Senha.class);
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public List<Senha> listaAtendimentoIniciadoSimplesPesquisa(int id_filial, int id_usuario, int id_status, String tipoData, String dataInicial, String dataFinal, int id_pessoa, String descricaoFisica, String tipoPesquisaFisica) {
        List<Senha> result = new ArrayList();
        try {

            String inner = "", and = "", order = " ORDER BY s.nr_senha desc, s.dt_data ";

            if (id_status != 0) {
                and += " AND a.id_status = " + id_status;
            }

            if (tipoData.equals("hoje")) {
                and += " AND a.dt_emissao = '" + DataHoje.dataHoje() + "'";
            } else {
                and += " AND a.dt_emissao BETWEEN '" + dataInicial + "' AND '" + dataFinal + "'";
            }

            if (id_pessoa != -1) {
                inner += " INNER JOIN pes_juridica j ON j.id = a.id_juridica ";
                and += " AND j.id_pessoa = " + id_pessoa;
            }

            if (!tipoPesquisaFisica.isEmpty()) {
                if (!descricaoFisica.isEmpty() && !descricaoFisica.equals("___.___.___-__")) {
                    inner += " INNER JOIN sis_pessoa p ON p.id = a.id_sis_pessoa ";
                    if (tipoPesquisaFisica.equals("nome")) {
                        descricaoFisica = AnaliseString.normalizeLower(descricaoFisica);
                        and += " AND TRANSLATE(LOWER(p.ds_nome)) like '%" + descricaoFisica + "%' ";
                    } else if (tipoPesquisaFisica.equals("cpf")) {
                        and += " AND p.ds_documento = '" + descricaoFisica + "' ";
                    } else {
                        and += " AND p.ds_rg = '" + descricaoFisica + "' ";
                    }
                }
            }

            String textQry = "SELECT s.* FROM ate_movimento a "
                    + " INNER JOIN hom_senha s on s.id_atendimento = a.id "
                    + inner
                    + " WHERE s.id_filial = " + id_filial
                    + and
                    + order;

            Query qry = getEntityManager().createNativeQuery(textQry, Senha.class);
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<Senha> listaAtendimentoIniciadoSimplesUsuario(int id_filial, int id_usuario, int id_departamento) {
        List<Senha> result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT s.* FROM ate_movimento a "
                    + " INNER JOIN hom_senha s on s.id_atendimento = a.id "
                    + " WHERE a.dt_emissao = '" + DataHoje.dataHoje() + "'"
                    + "   AND a.id_status = 4"
                    + "   AND s.id_filial = " + id_filial
                    + "   AND a.id_atendente = " + id_usuario
                    + "   AND (a.id_reserva IS NULL OR a.id_reserva = " + id_usuario + ")"
                    + "   AND s.id_departamento = " + id_departamento
                    + " ORDER BY s.nr_senha DESC, s.dt_data",
                    Senha.class);

            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public Senha pesquisaAtendimentoReserva(int id_filial, int id_usuario) {
        Senha result = null;
        Query qry = getEntityManager().createNativeQuery(
                "SELECT s.* FROM ate_movimento a "
                + " INNER JOIN hom_senha s on s.id_atendimento = a.id "
                + " WHERE a.dt_emissao = '" + DataHoje.dataHoje() + "'"
                + "   AND a.id_status = 1"
                + "   AND s.id_filial = " + id_filial
                + "   AND a.id_reserva = " + id_usuario
                + " ORDER BY s.nr_senha",
                Senha.class);
        qry.setMaxResults(1);
        try {
            result = (Senha) qry.getSingleResult();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public List<Senha> listaSequenciaSenha(int id_filial, int id_departamento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT s "
                    + "  FROM Senha s"
                    + " WHERE s.dtData = :pdata"
                    + "   AND s.filial.id = :pfilial"
                    + "   AND (s.horaChamada = '' OR s.horaChamada is null)"
                    + "   AND s.departamento.id = :pdepartamento"
                    + " ORDER BY s.senha ASC"
            );

            qry.setParameter("pdata", DataHoje.dataHoje());
            qry.setParameter("pfilial", id_filial);
            qry.setParameter("pdepartamento", id_departamento);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public PessoaEmpresa pesquisaPessoaEmpresaAdmissao(int id_fisica, int id_juridica, String dataAdmissao) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT pe "
                    + "  FROM PessoaEmpresa pe"
                    + " WHERE pe.fisica.id = :id_fisica"
                    + "   AND pe.juridica.id = :id_juridica"
                    + "   AND pe.dtAdmissao = :dt_admissao"
            );

            qry.setParameter("id_fisica", id_fisica);
            qry.setParameter("id_juridica", id_juridica);
            qry.setParameter("dt_admissao", DataHoje.converte(dataAdmissao));

            return (PessoaEmpresa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public PessoaEmpresa pesquisaPessoaEmpresaDemissao(int id_fisica, int id_juridica, String dataDemissao) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT pe "
                    + "  FROM PessoaEmpresa pe"
                    + " WHERE pe.fisica.id = :id_fisica"
                    + "   AND pe.juridica.id = :id_juridica"
                    + "   AND pe.dtDemissao = :dt_demissao"
            );

            qry.setParameter("id_fisica", id_fisica);
            qry.setParameter("id_juridica", id_juridica);
            qry.setParameter("dt_demissao", DataHoje.converte(dataDemissao));

            return (PessoaEmpresa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Agendamento pesquisaAgendamentoPorPessoaEmpresa(int id_pessoa_empresa, int[] ids_status) {
        try {
            String text_qry
                    = "SELECT A "
                    + "  FROM Agendamento a"
                    + " WHERE A.pessoaEmpresa.id = :id_pessoa_empresa ";
            String ids = "";
            for (int i = 0; i < ids_status.length; i++) {
                ids += (ids.isEmpty()) ? "" + ids_status[i] : ", " + ids_status[i];
            }
            String text_and = " AND A.status.id IN (" + ids + ")";

            Query qry = getEntityManager().createQuery(
                    text_qry + text_and
            );

            qry.setParameter("id_pessoa_empresa", id_pessoa_empresa);

            return (Agendamento) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List pesquisaPorFuncionario(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT A FROM Agendamento AS A WHERE A.pessoaEmpresa.fisica.pessoa.id = :pessoa_id ORDER BY A.dtData DESC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public List pesquisaPorEmpresa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT A FROM Agendamento AS A WHERE A.pessoaEmpresa.juridica.pessoa.id = :pessoa_id ORDER BY A.dtData DESC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public List pesquisaPorPessoaEmpresa(Integer pessoa_empresa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT A FROM Agendamento AS A WHERE A.pessoaEmpresa.id = :pessoa_empresa_id");
            query.setParameter("pessoa_empresa_id", pessoa_empresa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List findAllHomologadores() {
        try {
            Query query = getEntityManager().createQuery("SELECT A.homologador FROM Agendamento AS A WHERE A.homologador IS NOT NULL GROUP BY A.homologador");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List findByDataHorarioEmpresa(Date data, Integer horario_id, Integer empresa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT A FROM Agendamento AS A WHERE A.dtData = :data AND A.horarios.id = :horario_id AND A.pessoaEmpresa.juridica.id = :empresa_id  AND A.status.id = 2 ORDER BY A.horarios.hora ASC");
            query.setParameter("data", data);
            query.setParameter("horario_id", horario_id);
            query.setParameter("empresa_id", empresa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
