package br.com.rtools.homologacao.dao;

import br.com.rtools.homologacao.HorarioReserva;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class HorarioReservaDao extends DB {

    private Integer getIdPessoaReserva() {
        try {
            return ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa().getId();
        } catch (Exception e1) {
            try {
                return ((Pessoa) GenericaSessao.getObject("sessaoUsuarioAcessoWeb")).getId();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public void begin() {
        try {
            getEntityManager().getTransaction().begin();
            Query query = getEntityManager().createNativeQuery("DELETE FROM hom_horario_reserva WHERE CURRENT_TIMESTAMP > dt_expiracao");
            query.executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
    }

    public Boolean exists(Integer horario_id) {
        return exists(horario_id, getIdPessoaReserva());
    }

    public Boolean exists(Integer horario_id, Integer pessoa_id) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT * FROM hom_horario_reserva WHERE id_pessoa_reserva = " + pessoa_id + " AND id_horario <> " + horario_id);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }

    }

    public Integer count(Integer horario_id) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM hom_horario_reserva WHERE id_horario = " + horario_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return Integer.parseInt(((List) (list.get(0))).get(0).toString());
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public void reserve(Integer horario_id) {
        reserve(horario_id, getIdPessoaReserva());
    }

    public void reserve(Integer horario_id, Integer pessoa_id) {
        begin();
        clear();
        Dao dao = new Dao();
        HorarioReserva horarioReserva = new HorarioReserva();
        horarioReserva.setDtExpiracao(DataHoje.incrementarMinuto(new Date(), 15));
        horarioReserva.setHorario((Horarios) dao.find(new Horarios(), horario_id));
        dao.save(horarioReserva, true);
    }

    public void clear() {
        commit(getIdPessoaReserva());
    }

    public void commit() {
        commit(getIdPessoaReserva());
    }

    public void commit(Integer pessoa_id) {
        try {
            getEntityManager().getTransaction().begin();
            Query query = getEntityManager().createNativeQuery("DELETE FROM hom_horario_reserva WHERE id_pessoa_reserva = " + pessoa_id);
            query.executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
    }

}
