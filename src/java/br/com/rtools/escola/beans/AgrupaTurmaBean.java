package br.com.rtools.escola.beans;

import br.com.rtools.escola.AgrupaTurma;
import br.com.rtools.escola.ListaAgrupaTurma;
import br.com.rtools.escola.Turma;
import br.com.rtools.escola.dao.AgrupaTurmaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class AgrupaTurmaBean implements Serializable {

    private AgrupaTurma agrupaTurma;
    private List<AgrupaTurma> listAgrupaTurma;
    private List<ListaAgrupaTurma> itensAgrupados;
    private Boolean integral;
    private Boolean historico;

    @PostConstruct
    public void init() {
        agrupaTurma = new AgrupaTurma();
        listAgrupaTurma = new ArrayList<>();
        itensAgrupados = new ArrayList<>();
        integral = false;
        historico = false;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("turmaPesquisa");
        clear();
    }

    public void clear() {
        GenericaSessao.remove("agrupaTurmaBean");
    }

    public void save() {
        Dao dao = new Dao();
        if (itensAgrupados.isEmpty()) {
            GenericaMensagem.warn("Validação", "Pesquisar turma e adicionar itens a lista!");
            return;
        }
        boolean erro = false;
        boolean is_agrupado = false;
        Integer count_agrupado = 0;
        for (int i = 0; i < itensAgrupados.size(); i++) {
            if (itensAgrupados.get(i).getIsIntegral()) {
                count_agrupado++;
                is_agrupado = true;
            }
        }
        if (!is_agrupado) {
            GenericaMensagem.warn("Validação", "Inserir uma turma como integral!");
            return;
        }
        if (count_agrupado > 1) {
            GenericaMensagem.warn("Validação", "Só é possível adicionar uma turma como integral!");
            return;
        }
        AgrupaTurmaDao agrupaTurmaDao = new AgrupaTurmaDao();
        dao.openTransaction();
        for (int i = 0; i < itensAgrupados.size(); i++) {
            if (itensAgrupados.get(i).getAgrupaTurma().getId() == -1) {
                if (itensAgrupados.isEmpty()) {
                    if (!((List) agrupaTurmaDao.pesquisaPorTurmaIntegral(itensAgrupados.get(i).getAgrupaTurma().getTurmaIntegral().getId())).isEmpty()) {
                        GenericaMensagem.warn("Validação", "Grupo integral já cadastrado! Realizar agrupamento com o já existente.");
                        return;
                    }
                }
                if (!dao.save(itensAgrupados.get(i).getAgrupaTurma())) {
                    erro = true;
                    break;
                }
            } else if (!dao.update(itensAgrupados.get(i).getAgrupaTurma())) {
                erro = true;
                break;
            }
        }
        if (erro) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao inserir registro(s)!");
            return;
        }
        dao.commit();
        listAgrupaTurma.clear();
        itensAgrupados.clear();
        integral = false;
        agrupaTurma = new AgrupaTurma();
        GenericaMensagem.info("Sucesso", "Registro(s) inserido(s) com sucesso");
    }

    public void edit(AgrupaTurma at) {
        AgrupaTurmaDao agrupaTurmaDao = new AgrupaTurmaDao();
        List<AgrupaTurma> list = (List<AgrupaTurma>) agrupaTurmaDao.pesquisaPorTurmaIntegral(at.getTurmaIntegral().getId());
        itensAgrupados.clear();
        boolean turmaIntegral;
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTurmaIntegral() != null && Objects.equals(list.get(i).getTurma().getId(), list.get(i).getTurmaIntegral().getId())) {
                    turmaIntegral = true;
                } else {
                    turmaIntegral = false;
                }
                itensAgrupados.add(new ListaAgrupaTurma(list.get(i), turmaIntegral));
            }
        }

    }

    public void delete(AgrupaTurma at) {
        AgrupaTurmaDao agrupaTurmaDao = new AgrupaTurmaDao();
        List<AgrupaTurma> list = (List<AgrupaTurma>) agrupaTurmaDao.pesquisaPorTurmaIntegral(at.getTurmaIntegral().getId());
        Dao dao = new Dao();
        if (!list.isEmpty()) {
            dao.openTransaction();
            for (int i = 0; i < list.size(); i++) {
                AgrupaTurma at1 = (AgrupaTurma) dao.find(new AgrupaTurma(), list.get(i).getId());
                if (!dao.delete(at1)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Ao remover registro(s)!");
                    return;
                }
            }
            dao.commit();
            NovoLog novoLog = new NovoLog();
            novoLog.delete(list.toString());
            novoLog.setTabela("esc_agrupa_turma");
            novoLog.setCodigo(at.getId());
            GenericaMensagem.info("Sucesso", "Registro(s) removido(s)");
        }
        listAgrupaTurma.clear();
        itensAgrupados.clear();
    }

    public void addItem() {
        if (agrupaTurma.getTurma().getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar uma turma!");
            return;
        }
        if (!itensAgrupados.isEmpty()) {
            for (int i = 0; i < itensAgrupados.size(); i++) {
                if (!itensAgrupados.get(i).getAgrupaTurma().getTurma().getSala().equals(agrupaTurma.getTurma().getSala())) {
                    GenericaMensagem.warn("Validação", "Não é possível agrupar esta turma, não pertence a mesma sala!");
                    return;
                }
                if (Objects.equals(itensAgrupados.get(i).getAgrupaTurma().getTurma().getId(), agrupaTurma.getTurma().getId())) {
                    GenericaMensagem.warn("Validação", "Não pode existir duas turmas para o mesmo grupo!");
                    return;
                }
            }
        }
        if (!integral) {
            if (itensAgrupados.isEmpty()) {
                integral = true;
            }
        }
        if (integral) {
            for (int i = 0; i < itensAgrupados.size(); i++) {
                itensAgrupados.get(i).getAgrupaTurma().setTurmaIntegral(agrupaTurma.getTurma());
                itensAgrupados.get(i).setIsIntegral(false);
            }
            agrupaTurma.setTurmaIntegral(agrupaTurma.getTurma());
        } else if (!itensAgrupados.isEmpty()) {
            agrupaTurma.setTurmaIntegral(itensAgrupados.get(0).getAgrupaTurma().getTurmaIntegral());
        }
        itensAgrupados.add(new ListaAgrupaTurma(agrupaTurma, integral));
        agrupaTurma = new AgrupaTurma();
        integral = false;
    }

    public void editItensList(ListaAgrupaTurma lat) {
        for (int i = 0; i < itensAgrupados.size(); i++) {
            itensAgrupados.get(i).getAgrupaTurma().setTurmaIntegral(null);
            itensAgrupados.get(i).setIsIntegral(false);
        }
        for (int i = 0; i < itensAgrupados.size(); i++) {
            if (Objects.equals(itensAgrupados.get(i).getAgrupaTurma().getTurma().getId(), lat.getAgrupaTurma().getTurma().getId())) {
                itensAgrupados.get(i).setIsIntegral(true);
            }
            itensAgrupados.get(i).getAgrupaTurma().setTurmaIntegral(lat.getAgrupaTurma().getTurma());
        }
    }

    public void removeItensList(ListaAgrupaTurma lat) {
        boolean grupoIntegral = false;
        if (lat.getAgrupaTurma().getId() == -1) {
            for (int i = 0; i < itensAgrupados.size(); i++) {
                if (Objects.equals(itensAgrupados.get(i).getAgrupaTurma().getTurma().getId(), lat.getAgrupaTurma().getTurma().getId())) {
                    if (Objects.equals(itensAgrupados.get(i).getAgrupaTurma().getTurmaIntegral().getId(), lat.getAgrupaTurma().getTurma().getId())) {
                        grupoIntegral = true;
                    }
                    itensAgrupados.remove(i);
                }
            }
            if (itensAgrupados.size() > 0) {
                int idTurma = itensAgrupados.get(0).getAgrupaTurma().getTurma().getId();
                for (int j = 0; j < itensAgrupados.size(); j++) {
                    if (grupoIntegral) {
                        itensAgrupados.get(j).setIsIntegral(grupoIntegral);
                    }
                    itensAgrupados.get(j).getAgrupaTurma().setTurmaIntegral(itensAgrupados.get(j).getAgrupaTurma().getTurma());
                    grupoIntegral = false;
                }
            }
        } else if (new Dao().delete(lat.getAgrupaTurma(), true)) {
            for (int i = 0; i < itensAgrupados.size(); i++) {
                if (itensAgrupados.get(i).getAgrupaTurma().getId() == lat.getAgrupaTurma().getId()) {
                    itensAgrupados.remove(i);
                }
            }
        }
        listAgrupaTurma.clear();
    }

    public AgrupaTurma getAgrupaTurma() {
        if (GenericaSessao.exists("turmaPesquisa")) {
            agrupaTurma.setTurma((Turma) GenericaSessao.getObject("turmaPesquisa", true));
        }
        return agrupaTurma;
    }

    public void setAgrupaTurma(AgrupaTurma agrupaTurma) {
        this.agrupaTurma = agrupaTurma;
    }

    public List<AgrupaTurma> getListAgrupaTurma() {
        try {
            if (listAgrupaTurma.isEmpty()) {
                AgrupaTurmaDao atd = new AgrupaTurmaDao();
                List<AgrupaTurma> list = atd.findIntegral();
                for (int i = 0; i < list.size(); i++) {
                    Integer rf_atual = DataHoje.converteDataParaRefInteger(DataHoje.converteDataParaReferencia(new Date()));
                    Integer rf_curso = DataHoje.converteDataParaRefInteger(DataHoje.converteDataParaReferencia(list.get(i).getTurma().getDtInicio()));
                    if (historico) {
                        if ((!Objects.equals(rf_atual, rf_curso))) {
                            listAgrupaTurma.add(list.get(i));
                        }
                    } else if ((Objects.equals(rf_atual, rf_curso))) {
                        listAgrupaTurma.add(list.get(i));
                    }
                }
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return listAgrupaTurma;
    }

    public void setListAgrupaTurma(List<AgrupaTurma> listAgrupaTurma) {
        this.listAgrupaTurma = listAgrupaTurma;
    }

    public List<ListaAgrupaTurma> getItensAgrupados() {
        return itensAgrupados;
    }

    public void setItensAgrupados(List<ListaAgrupaTurma> itensAgrupados) {
        this.itensAgrupados = itensAgrupados;
    }

    public Boolean getIntegral() {
        return integral;
    }

    public void setIntegral(Boolean integral) {
        this.integral = integral;
    }

    public Boolean getHistorico() {
        return historico;
    }

    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }
}
