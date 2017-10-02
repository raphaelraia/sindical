package br.com.rtools.agendamentos.benas;

import br.com.rtools.agendamentos.AgendaServico;
import br.com.rtools.agendamentos.dao.AgendaServicoDao;
import br.com.rtools.financeiro.ServicoRotina;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class AgendaServicoBean {

    private List<AgendaServico> listAgendaServico;
    private AgendaServico agendaServico;
    private List<SelectItem> listServicos;
    private Integer idServico;

    public AgendaServicoBean() {
        agendaServico = new AgendaServico();
        loadListServicos();
        loadListAgendaServicos();
    }

    public void store() {
        if (listServicos.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR SERVIÇOS DA ROTINA!");
            return;
        }
        Dao dao = new Dao();
        if (agendaServico.getId() == null) {
            if (new AgendaServicoDao().existByAgendaServico(idServico, null)) {
                GenericaMensagem.warn("Validação", "SERVIÇO JÁ CADASTRADO!");
                return;
            }
            agendaServico.setServico((Servicos) dao.find(new Servicos(), idServico));
            if (!dao.save(agendaServico, true)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        } else {
            if (!dao.update(agendaServico, true)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
        }
        loadListAgendaServicos();
        agendaServico = new AgendaServico();
    }

    public void edit(AgendaServico as) {
        agendaServico = (AgendaServico) new Dao().rebind(as);
        idServico = agendaServico.getServico().getId();
    }

    public void delete() {
        if (agendaServico.getId() != null) {
            delete(agendaServico);
        }
    }

    public void delete(AgendaServico as) {
        if (!new Dao().delete(as, true)) {
            GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO!");
            return;
        }
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        agendaServico = new AgendaServico();
        loadListAgendaServicos();
    }

    public final void loadListAgendaServicos() {
        listAgendaServico = new ArrayList();
        listAgendaServico = new Dao().list(new AgendaServico(), true);
    }

    public final void loadListServicos() {
        List<ServicoRotina> list = new ServicoRotinaDao().findAllByRotina(new Rotina().get().getId());
        listServicos = new ArrayList();
        idServico = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getServicos().getSituacao().equals("A")) {
                if (i == 0) {
                    idServico = list.get(i).getServicos().getId();
                }
                listServicos.add(new SelectItem(list.get(i).getServicos().getId(), list.get(i).getServicos().getDescricao()));
            }
        }
    }

    public void clear() {
        GenericaSessao.remove("agendaServicoBean");
    }

    public List<AgendaServico> getListAgendaServico() {
        return listAgendaServico;
    }

    public void setListAgendaServico(List<AgendaServico> listAgendaServico) {
        this.listAgendaServico = listAgendaServico;
    }

    public AgendaServico getAgendaServico() {
        return agendaServico;
    }

    public void setAgendaServico(AgendaServico agendaServico) {
        this.agendaServico = agendaServico;
    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

}
