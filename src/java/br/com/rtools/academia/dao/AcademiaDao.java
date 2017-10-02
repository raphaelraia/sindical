package br.com.rtools.academia.dao;

import br.com.rtools.academia.AcademiaGrade;
import br.com.rtools.academia.AcademiaSemana;
import br.com.rtools.academia.AcademiaServicoValor;
import br.com.rtools.associativo.MatriculaAcademia;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class AcademiaDao extends DB {

    public AcademiaGrade existeAcademiaGrade(String horaInicio, String horaFim) {
        try {
            Query query = getEntityManager().createQuery("SELECT AG FROM AcademiaGrade AS AG WHERE AG.horaInicio = :horaInicio AND AG.horaFim = :horaFim");
            query.setParameter("horaInicio", horaInicio);
            query.setParameter("horaFim", horaFim);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (AcademiaGrade) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public AcademiaServicoValor existeAcademiaServicoValor(AcademiaServicoValor asv) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASV FROM AcademiaServicoValor AS ASV WHERE ASV.servicos.id = :servico AND ASV.periodo.id = :periodo");
            query.setParameter("servico", asv.getServicos().getId());
            query.setParameter("periodo", asv.getPeriodo().getId());

            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (AcademiaServicoValor) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<AcademiaSemana> existeAcademiaSemana(int id_grade, int id_semana, int id_servico, int id_periodo) {
        try {
            //Query query = getEntityManager().createQuery("SELECT s FROM AcademiaSemana s WHERE s.academiaGrade.id = :id_grade AND s.semana = :id_semana AND s.academiaServicoValor.id = :id_servico_valor");
            Query query = getEntityManager().createQuery("SELECT s FROM AcademiaSemana s WHERE s.academiaGrade.id = :id_grade AND s.semana.id = :id_semana AND s.academiaServicoValor.periodo.id = :id_periodo AND s.academiaServicoValor.servicos.id = :id_servico");
            query.setParameter("id_grade", id_grade);
            query.setParameter("id_semana", id_semana);
            query.setParameter("id_servico", id_servico);
            query.setParameter("id_periodo", id_periodo);

            List list = query.getResultList();
            if (!list.isEmpty()) {
                return query.getResultList();
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<AcademiaServicoValor> listaAcademiaServicoValor(int idServico) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASV FROM AcademiaServicoValor AS ASV WHERE ASV.servicos.id = :servicos ORDER BY ASV.periodo.descricao ASC, ASV.servicos.descricao ASC");
            query.setParameter("servicos", idServico);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<AcademiaServicoValor> listaServicoValorPorRotina() {
        try {
            String queryString = ""
                    + "     SELECT ASV.*                                        \n"
                    + "       FROM aca_servico_valor AS ASV                     \n"
                    + " INNER JOIN fin_servicos S ON S.id = ASV.id_servico      \n"
                    + "      WHERE ASV.id_servico IN( SELECT SR.id_servicos FROM fin_servico_rotina AS SR WHERE SR.id_rotina = 122) \n"
                    + "        AND ASV.id_periodo IN(SELECT PER.id FROM sis_periodo AS PER) \n"
                    + "        AND S.ds_situacao = 'A'  \n"
                    + "   ORDER BY S.ds_descricao ASC";
            Query query = getEntityManager().createNativeQuery(queryString, AcademiaServicoValor.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public Integer findMainId() {
        try {
            String queryString = ""
                    + "     SELECT ASV.id, count(*)                      \n"
                    + "       FROM matr_academia A                              \n"
                    + " INNER JOIN aca_servico_valor AS ASV ON ASV.id = A.id_servico_valor  \n"
                    + " INNER JOIN fin_servico_pessoa AS SP ON SP.id = A.id_servico_pessoa  \n"
                    + "      WHERE dt_emissao BETWEEN (CURRENT_DATE-7) AND CURRENT_DATE     \n"
                    + "   GROUP BY ASV.id                                \n"
                    + "   ORDER BY count(*) DESC ";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return Integer.parseInt(((List) list.get(0)).get(0).toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public List<AcademiaServicoValor> listaAcademiaServicoValorPorServico(int idServico) {
        try {
            Query query = getEntityManager().createQuery(" SELECT ASV FROM AcademiaServicoValor AS ASV WHERE ASV.servicos.id = :servicos AND ASV.id IN (SELECT ASE.academiaServicoValor.id FROM AcademiaSemana ASE ) ORDER BY ASV.servicos.descricao ASC, ASV.periodo.dias ASC");
            query.setParameter("servicos", idServico);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<AcademiaSemana> listaAcademiaSemana(int id_servico_valor) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AcademiaSemana AS ASE WHERE ASE.academiaServicoValor.id = :servicoValor ORDER BY ASE.semana.id");
            query.setParameter("servicoValor", id_servico_valor);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<AcademiaSemana> listaAcademiaSemana() {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AcademiaSemana AS ASE");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public boolean existeAcademiaSemana(int idAcademiaGrade, int idSemana) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AcademiaSemana AS ASE WHERE ASE.academiaGrade.id = :academiaGrade AND ASE.semana.id = :semana");
            query.setParameter("academiaGrade", idAcademiaGrade);
            query.setParameter("semana", idSemana);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public AcademiaSemana pesquisaAcademiaSemana(int idAcademiaGrade, int idSemana) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AcademiaSemana AS ASE WHERE ASE.academiaGrade.id = :academiaGrade AND ASE.semana.id = :semana");
            query.setParameter("academiaGrade", idAcademiaGrade);
            query.setParameter("semana", idSemana);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (AcademiaSemana) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public boolean desfazerMovimento(MatriculaAcademia ma) {
        LoteDao loteDB = new LoteDao();
        Lote lote = (Lote) loteDB.pesquisaLotePorEvt(ma.getEvt());
        if (lote == null) {
            return false;
        }
        if (lote.getId() == -1) {
            return false;
        }
        Dao dao = new Dao();
        try {
            Query queryMovimentos = getEntityManager().createQuery("SELECT M FROM Movimento AS M WHERE M.lote.evt.id = " + ma.getEvt().getId());
            List<Movimento> listMovimentos = (List<Movimento>) queryMovimentos.getResultList();
            dao.openTransaction();
            for (int i = 0; i < listMovimentos.size(); i++) {
                if (!dao.delete((Movimento) dao.find(new Movimento(), listMovimentos.get(i).getId()))) {
                    dao.rollback();
                    return false;
                }
            }
            if (lote.getId() != -1) {
                if (!dao.delete((Lote) dao.find(new Lote(), lote.getId()))) {
                    dao.rollback();
                    return false;
                }
            }
            int idEvt = ma.getEvt().getId();
            ma.setEvt(null);
            if (!dao.update(ma)) {
                dao.rollback();
                return false;
            }
            if (!dao.delete((Evt) dao.find(new Evt(), idEvt))) {
                dao.rollback();
                return false;
            }
            dao.rollback();
            return true;
        } catch (Exception e) {
            dao.rollback();
            return false;
        }
    }

    public List<Movimento> listaRefazerMovimento(MatriculaAcademia ma) {
        try {
            Query queryMovimentos = getEntityManager().createQuery("SELECT M FROM Movimento AS M WHERE M.servicos.id = " + ma.getServicoPessoa().getServicos().getId() + " AND M.baixa IS NULL AND M.ativo = TRUE AND M.pessoa.id = " + ma.getServicoPessoa().getPessoa().getId());
            List<Movimento> listMovimentos = (List<Movimento>) queryMovimentos.getResultList();

            return listMovimentos;
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<MatriculaAcademia> pesquisaMatriculaAcademia(String tipo, String por, String como, String descricao, boolean ativo, int servicos, String periodoEmissao, String startDate, String endDate) {
        String queryString = ""
                + "    SELECT MATR.*                                            \n"
                + "      FROM matr_academia AS MATR                             \n"
                + "INNER JOIN fin_servico_pessoa SP ON SP.id = MATR.id_servico_pessoa\n"
                + "INNER JOIN fin_servicos S ON S.id = SP.id_servico            \n"
                + "INNER JOIN pes_pessoa A ON A.id = SP.id_pessoa               \n";

        List listWhere = new ArrayList();
        if (!descricao.isEmpty()) {
            if (por.equals("cpf")) {
                if (como.equals("I")) {
                    listWhere.add(" UPPER(A.ds_documento) LIKE '" + descricao.toUpperCase() + "%'");
                } else {
                    listWhere.add(" UPPER(A.ds_documento) LIKE '%" + descricao.toUpperCase() + "%'");
                }
            } else if (por.equals("nome")) {
                if (como.equals("I")) {
                    listWhere.add("UPPER(func_translate(A.ds_nome)) LIKE UPPER(func_translate('" + descricao + "%'))");
                } else {
                    listWhere.add("UPPER(func_translate(A.ds_nome)) LIKE UPPER(func_translate('%" + descricao + "%'))");
                }
            }
        }
        switch (periodoEmissao) {
            case "hoje":
                listWhere.add("SP.dt_emissao = CURRENT_DATE");
                break;
            case "ontem":
                listWhere.add("SP.dt_emissao = (CURRENT_DATE - 1)");
                break;
            case "ultimos_sete_dias":
                listWhere.add("SP.dt_emissao BETWEEN (CURRENT_DATE - 7) AND CURRENT_DATE ");
                break;
            case "este_mes":
                listWhere.add("to_char(SP.dt_emissao , 'YYYY-MM') = to_char(CURRENT_DATE , 'YYYY-MM')");
                break;
            default:
                break;
        }
        listWhere.add("SP.is_ativo = " + ativo + " ");
        if (servicos > 0) {
            listWhere.add("S.id IN( " + servicos + " )");
        }
        try {
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            Query query = getEntityManager().createNativeQuery(queryString, MatriculaAcademia.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public boolean existeAlunoModalidade(Integer aluno_id, Integer modalidade_id, Date emissao) {
        try {
            Query query = getEntityManager().createQuery("SELECT MA FROM MatriculaAcademia AS MA WHERE MA.servicoPessoa.pessoa.id = :aluno_id AND MA.academiaServicoValor.servicos.id = :modalidade_id AND MA.dtInativo IS NULL AND (MA.dtValidade IS NULL OR MA.dtValidade > CURRENT_TIMESTAMP)");
            query.setMaxResults(1);
            query.setParameter("aluno", aluno_id);
            query.setParameter("modalidade", modalidade_id);
            if (!query.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public Boolean existeAlunoModalidadePeriodo(Integer aluno_id, Integer modalidade_id, Integer periodo_id, Date emissao) {
        try {
            Query query = getEntityManager().createQuery("SELECT MA FROM MatriculaAcademia AS MA WHERE MA.servicoPessoa.pessoa.id = :aluno_id AND MA.academiaServicoValor.servicos.id = :modalidade_id AND MA.academiaServicoValor.id = :servico_valor_id AND MA.dtInativo IS NULL AND (MA.dtValidade IS NULL OR MA.dtValidade > CURRENT_TIMESTAMP)");
            query.setMaxResults(1);
            query.setParameter("aluno_id", aluno_id);
            query.setParameter("modalidade_id", modalidade_id);
            query.setParameter("servico_valor_id", periodo_id);
            if (!query.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
