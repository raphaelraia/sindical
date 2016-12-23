package br.com.rtools.escola.dao;

import br.com.rtools.escola.Turma;
import br.com.rtools.escola.TurmaProfessor;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class TurmaDao extends DB {

    public List<TurmaProfessor> listaTurmaProfessor(int idTurma) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT TP FROM TurmaProfessor TP WHERE TP.turma.id = " + idTurma + " ORDER BY TP.componenteCurricular.descricao ASC, TP.professor.professor.nome ASC ");
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public boolean existeTurma(Turma turma) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT T FROM Turma AS T WHERE T.dtInicio = :dataInicio AND T.dtTermino = :dataTermino AND T.horaInicio = :hInicio AND T.horaTermino = :hTermino  AND T.cursos.id = :idCurso AND T.filial.id = :idFilial AND T.sala = :sala ");
            qry.setParameter("dataInicio", turma.getDtInicio());
            qry.setParameter("dataTermino", turma.getDtTermino());
            qry.setParameter("hInicio", turma.getHoraInicio());
            qry.setParameter("hTermino", turma.getHoraTermino());
            qry.setParameter("sala", turma.getSala());
            qry.setParameter("idCurso", turma.getCursos().getId());
            qry.setParameter("idFilial", turma.getFilial().getId());
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;

    }

    public boolean existeTurmaProfessor(TurmaProfessor turmaProfessor) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT TP FROM TurmaProfessor AS TP WHERE TP.turma.id = :idTurma AND TP.componenteCurricular.id = :idComponenteCurricular AND TP.professor.id = :idProfessor ");
            qry.setParameter("idTurma", turmaProfessor.getTurma().getId());
            qry.setParameter("idProfessor", turmaProfessor.getProfessor().getId());
            qry.setParameter("idComponenteCurricular", turmaProfessor.getComponenteCurricular().getId());
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List listaTurmaAtiva() {
        return listaTurmaAtivaPorFilialServico(-1, -1);
    }

    public List listaTurmaAtivaPorFilial(int idFilial) {
        return listaTurmaAtivaPorFilialServico(idFilial, -1);
    }

    public List listaTurmaAtivaPorFilialServico(int idFilial, int idServico) {
        try {
            String queryFilial = idFilial > 0 ? " AND T.filial.id = " + idFilial : "";
            String queryServico = idServico > 0 ? " AND T.cursos.id = " + idServico : "";
            Query qry = getEntityManager().createQuery("SELECT T FROM Turma AS T WHERE T.dtTermino >= CURRENT_DATE " + queryFilial + " " + queryServico + " ORDER BY T.cursos.descricao ASC, T.sala ASC, T.descricao ASC, T.dtInicio DESC, T.horaInicio ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findbyFilial(Integer filial_id, String servico_descricao, String turma_descricao, String tipo, String data_inicio, String data_termino) {
        List listWhere = new ArrayList();
        try {
            String queryString = ""
                    + "     SELECT T.*                                          \n"
                    + "       FROM esc_turma AS T                               \n"
                    + " INNER JOIN fin_servicos S ON S.id = T.id_curso          \n";
            if (filial_id != null) {
                listWhere.add("T.id_filial = " + filial_id);
            }
            if (!servico_descricao.isEmpty()) {
                listWhere.add("func_translate(UPPER(TRIM(S.ds_descricao))) LIKE func_translate(UPPER(TRIM('%" + turma_descricao + "'%)))");
            }
            if (!turma_descricao.isEmpty()) {
                listWhere.add("func_translate(UPPER(TRIM(t.ds_descricao))) LIKE func_translate(UPPER(TRIM('%" + turma_descricao + "'%)))");
            }
            switch (tipo) {
                case "ativo":
                    listWhere.add("T.dt_termino >= CURRENT_DATE");
                    break;
                case "igual":
                    listWhere.add("T.dt_termino = '" + data_inicio + "'");
                    break;
                case "entre":
                    listWhere.add("T.dt_termino BETWEEN '" + data_inicio + "' AND '" + data_termino + "'");
                    break;
                case "antes":
                    listWhere.add("T.dt_termino < '" + data_termino + "'");
                    break;
                default:
                    break;
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";
                }
            }
            queryString += " "
                    + "   ORDER BY S.ds_descricao ASC,                          \n"
                    + "            T.nr_sala ASC,                               \n"
                    + "            T.ds_descricao ASC,                          \n"
                    + "            T.dt_inicio DESC,                            \n"
                    + "            T.tm_inicio ASC";
            Query query = getEntityManager().createNativeQuery(queryString, Turma.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
