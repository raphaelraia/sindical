package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.AgendaServico;
import br.com.rtools.agendamentos.AgendaStatus;
import br.com.rtools.agendamentos.AgendamentoCancelamento;
import br.com.rtools.agendamentos.AgendamentoHorario;
import br.com.rtools.agendamentos.AgendamentoServico;
import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.agendamentos.dao.AgendaHorarioReservaDao;
import br.com.rtools.agendamentos.dao.AgendaServicoDao;
import br.com.rtools.agendamentos.dao.AgendamentoHorarioDao;
import br.com.rtools.agendamentos.dao.AgendamentosDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.LancamentoIndividualDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.DescontoServicoEmpresaDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.beans.FisicaUtils;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GlobalSync;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.Sessions;
import br.com.rtools.utilitarios.WSSocket;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class AgendamentosBean implements Serializable {

    // OBJETOS
    private AgendaHorarios acrescentarHorario;
    private Agendamentos agendamento;
    private Agendamentos agendamentosEdit;
    private AgendaServico agendaServico;
    private AgendamentoHorario agendamentoHorario;
    private AgendamentoServico agendamentoServico;
    private Fisica pessoa;
    private Filial filial;
    private ConfiguracaoSocial configuracaoSocial;
    private ObjectAgendamentos objectAgendamentos;
    private Servicos servico;

    // LISTAS
    private List<ObjectAgendamentos> agendamentos;
    private List<ObjectAgenda> listObjectAgenda;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listServicos;
    private List<AgendaServico> listServicosAdicionados;
    private List<SelectItem> listStatus;
    private List<AgendamentoServico> listAgendamentoServico;
    private List<AgendamentoHorario> listAgendamentoHorario;

    // INTEGER
    private Integer idFilial;
    private Integer idGrupoConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idConvenio;
    private Integer idServico;
    private Integer idStatus;
    private AgendaHorarioReservaDao reservaDao;

    // DATAS
    private Date data;
    private String startDate;
    private String endDate;
    private String motivoCancelamento;

    // BOLEANOS
    private Boolean liberaAcessaFilial;
    private Boolean desabilitaFilial;
    private Boolean newSched;
    private Boolean showModal;
    private Boolean lockScheduler;
    private Boolean newRegister;
    private Boolean schedulesStatus;

    // DOUBLE
    private Double valor;
    private Double desconto;

    // STRINGS
    private String telefone;
    private String email;
    private String contato;

    public AgendamentosBean() {
        motivoCancelamento = "";
        reservaDao = new AgendaHorarioReservaDao();
        configuracaoSocial = ConfiguracaoSocial.get();
        pessoa = new Fisica();
        acrescentarHorario = new AgendaHorarios();
        agendamento = new Agendamentos();
        agendamentoHorario = new AgendamentoHorario();
        agendamentosEdit = new Agendamentos();
        agendamentoServico = new AgendamentoServico();

        agendamentos = new ArrayList();
        listAgendamentoServico = new ArrayList();
        listFiliais = new ArrayList();
        listGrupoConvenio = new ArrayList();
        listSubGrupoConvenio = new ArrayList();
        listConvenio = new ArrayList();
        listServicos = new ArrayList();
        listServicosAdicionados = new ArrayList();
        listAgendamentoHorario = new ArrayList();
        listStatus = new ArrayList();

        idFilial = null;
        idGrupoConvenio = null;
        idSubGrupoConvenio = null;
        idConvenio = null;
        idStatus = null;

        liberaAcessaFilial = false;
        desabilitaFilial = false;
        data = new Date();
        newSched = false;
        showModal = false;
        lockScheduler = false;
        newRegister = false;
        schedulesStatus = false;
        valor = new Double(0);
        desconto = new Double(0);
        servico = null;

        telefone = "";
        email = "";
        contato = "";
        reservaDao.begin();
        loadLiberaAcessaFilial();
        loadListFilial();
    }

    public void scheduler(ObjectAgendamentos oa) {
        scheduler(oa, true);
    }

    public void scheduler(ObjectAgendamentos oa, Boolean confirm) {
        newRegister = false;
        Dao dao = new Dao();
        if (oa.getAgendamento() == null) {
            int amoutTime = 0;
            AgendaHorarios ah = (AgendaHorarios) dao.find(new AgendaHorarios(), oa.horario_id);
            int totalTime = agendaServico.getNrMinutos();
            String lastHour = "";
//            if (agendamento.getId() != null) {
//                agendamento.setId(null);
//            }
            agendamento = new Agendamentos();
            agendamentoHorario = new AgendamentoHorario();
            agendamentoServico = new AgendamentoServico();
            listAgendamentoServico = new ArrayList();
            listAgendamentoHorario = new ArrayList();
            agendamentoHorario = new AgendamentoHorario();
            agendamentoServico = new AgendamentoServico();
            agendamento.setTelefone(telefone);
            agendamento.setEmail(email);
            agendamento.setContato(contato);
            if (Integer.parseInt(oa.getQuantidade()) == 0) {
                agendamento.setAgendaStatus((AgendaStatus) dao.find(new AgendaStatus(), 4));
            } else {
                agendamento.setAgendaStatus((AgendaStatus) dao.find(new AgendaStatus(), 1));
            }
            amoutTime = 0;
            if (confirm != null && confirm) {
                if (agendamento.getAgendaStatus().getId() == 1) {
                    for (int i = 0; i < agendamentos.size(); i++) {
                        if (DataHoje.convertTimeToInteger(agendamentos.get(i).getHora()) > DataHoje.convertTimeToInteger(ah.getHora())) {
                            if (amoutTime < totalTime) {
                                amoutTime = DataHoje.diffHour(ah.getHora(), agendamentos.get(i).getHora());
                                if (Integer.parseInt(agendamentos.get(i).getQuantidade()) == 0) {
                                    objectAgendamentos = oa;
                                    lockScheduler = true;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            objectAgendamentos = new ObjectAgendamentos();
            lockScheduler = false;
            agendamento.setDtData(data);
            agendamento.setDtEmissao(new Date());
            agendamento.setPessoa(pessoa.getPessoa());
            agendamento.setAgendador(Usuario.getUsuario());
            if (agendamento.getEmail().trim().isEmpty()) {
                agendamento.setEmail(pessoa.getPessoa().getEmail1());
            }
            if (agendamento.getTelefone().trim().isEmpty()) {
                agendamento.setTelefone(pessoa.getPessoa().getTelefone3());
            }
            if (agendamento.getEmail().trim().isEmpty()) {
                Messages.warn("Validação", "INFORMAR E-MAIL");
                return;
            }
            if (agendamento.getTelefone().trim().isEmpty()) {
                Messages.warn("Validação", "INFORMAR TELEFONE");
                return;
            }
            amoutTime = 0;
            agendamentoServico.setServico(agendaServico.getServico());
            agendamentoHorario.setAgendaHorarios((AgendaHorarios) dao.find(new AgendaHorarios(), oa.horario_id));
            listAgendamentoHorario.add(agendamentoHorario);
            if (Integer.parseInt(oa.getQuantidade()) > 0) {
                reservaDao.reserveMultiplesBegin(oa.horario_id);
            }
            AgendamentoServico as = new AgendamentoServico();
            as.setServico(agendaServico.getServico());
            listAgendamentoServico.add(as);
            for (int i = 0; i < agendamentos.size(); i++) {
                AgendamentoHorario ah1 = new AgendamentoHorario();
                if (DataHoje.convertTimeToInteger(agendamentos.get(i).getHora()) > DataHoje.convertTimeToInteger(agendamentoHorario.getAgendaHorarios().getHora())) {
                    if (amoutTime < totalTime) {
                        amoutTime = DataHoje.diffHour(agendamentoHorario.getAgendaHorarios().getHora(), agendamentos.get(i).getHora());
                        if (amoutTime >= totalTime) {
                            break;
                        }
                        ah1.setAgendaHorarios((AgendaHorarios) dao.find(new AgendaHorarios(), agendamentos.get(i).getHorario_id()));
                        if (Integer.parseInt(agendamentos.get(i).getQuantidade()) > 0) {
                            reservaDao.reserveMultiples(agendamentos.get(i).getHorario_id());
                        }
                        listAgendamentoHorario.add(ah1);
                    } else {
                        break;
                    }
                }
            }
            showModal = true;
            GlobalSync.load();
            this.loadListHorarios();
            WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        }
        if (oa.getAgendamento() == null) {
            // agendamento = new Agendamentos();
        }

    }

    public void save() {
        Dao dao = new Dao();
        newRegister = false;
        dao.openTransaction();
        if (agendamento.getId() == null) {
            newRegister = true;
            if (!dao.save(agendamento)) {
                agendamento.setId(null);
                dao.rollback();
                Messages.warn("Erro", "AO SALVAR AGENDAMENTO!");
                return;
            }
        } else {
            if (!dao.update(agendamento)) {
                dao.rollback();
                Messages.warn("Erro", "AO ATUALIZAR AGENDAMENTO!");
                return;
            }
        }
        for (int i = 0; i < listAgendamentoServico.size(); i++) {
            listAgendamentoServico.get(i).setAgendamento(agendamento);
            if (!dao.save(listAgendamentoServico.get(i))) {
                agendamento.setId(null);
                listAgendamentoServico.get(i).setId(null);
                listAgendamentoHorario.get(i).setAgendamento(null);
                dao.rollback();
                Messages.warn("Erro", "SERVIÇO JÁ CADASTRADO PARA ESSA AGENDA!");
                return;
            }
        }
        for (int i = 0; i < listAgendamentoHorario.size(); i++) {
            listAgendamentoHorario.get(i).setAgendamento(agendamento);
            if (!dao.save(listAgendamentoHorario.get(i))) {
                agendamento.setId(null);
                listAgendamentoHorario.get(i).setId(null);
                listAgendamentoHorario.get(i).setAgendamento(null);
                dao.rollback();
                Messages.warn("Erro", "AO SALVAR HORÁRIO DA AGENDA!");
                return;
            }
        }
        Messages.info("Sucesso", "AGENDA CRIADA!");
        dao.commit();
        agendamento.setId(null);
        reservaDao.commit();
        newRegister = true;
    }

    public void cancel() {
        if (motivoCancelamento.isEmpty() || motivoCancelamento.length() < 5) {
            Messages.warn("Validação", "INFORMAR MOTIVO DO CANCELAMENTO!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        List<AgendamentoHorario> list = new AgendamentoHorarioDao().findBy(agendamentosEdit.getId());
        for (int i = 0; i < list.size(); i++) {
            AgendamentoCancelamento ac = new AgendamentoCancelamento();
            ac.setDtData(new Date());
            ac.setMotivo(motivoCancelamento);
            ac.setUsuario(Usuario.getUsuario());
            ac.setAgendamentoHorario(list.get(i));
            if (!dao.save(ac)) {
                dao.rollback();
                Messages.warn("Erro", "AO CANCELAR AGENDA!");
                return;
            }
        }
        agendamentosEdit.setAgendaStatus((AgendaStatus) dao.find(new AgendaStatus(), 3));
        if (!dao.update(agendamentosEdit)) {
            dao.rollback();
            Messages.warn("Erro", "AO CANCELAR AGENDA!");
            return;
        }
        dao.commit();
        Messages.info("Sucesso", "AGENDAMENTO CANCELADO!");
        loadListObjectAgenda();
        GlobalSync.load();
        this.loadListHorarios();
        WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public void remove() {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public void removeSched(Integer agendamento_id) {
        motivoCancelamento = "";
        agendamentosEdit = new Agendamentos();
        agendamentosEdit = (Agendamentos) new Dao().find(new Agendamentos(), agendamento_id);
    }

    public void transfer(Agendamentos agendamentos) {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void dataListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.data = DataHoje.converte(format.format(event.getObject()));
        listener("data");
    }

    public Filial getFilial() {
        if (Sessions.exists("acessoFilial") && filial == null) {
            desabilitaFilial = true;
            filial = ((MacFilial) Sessions.getObject("acessoFilial")).getFilial();
        }
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getDesabilitaFilial() {
        return desabilitaFilial;
    }

    public void setDesabilitaFilial(Boolean desabilitaFilial) {
        this.desabilitaFilial = desabilitaFilial;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public final void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public final void loadListFilial() {
        listFiliais = new ArrayList();
        Filial f = MacFilial.getAcessoFilial().getFilial();
        if (f.getId() != -1) {
            if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                liberaAcessaFilial = true;
                // ROTINA MATRÍCULA ESCOLA
                Rotina r = new Rotina().get();
                List<FilialRotina> list = new ArrayList();
                if (r != null) {
                    list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
                }
                // ID DA FILIAL
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idFilial = list.get(i).getFilial().getId();
                            filial = f;
                        }
                        if (Objects.equals(f.getId(), list.get(i).getFilial().getId())) {
                            idFilial = f.getId();
                            filial = f;
                        }
                        listFiliais.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getFilial().getPessoa().getNome()));
                    }
                } else {
                    listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome() + " / " + f.getFilial().getPessoa().getDocumento()));
                }
            } else {
                idFilial = f.getId();
                listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome() + " / " + f.getFilial().getPessoa().getDocumento()));
            }
        }
    }

    public void loadListStatus() {
        listStatus = new ArrayList();
        List<AgendaStatus> list = (List<AgendaStatus>) new Dao().list(new AgendaStatus());
        idStatus = null;
        listStatus.add(new SelectItem(null, "TODOS"));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idStatus = list.get(i).getId();
            }
            if (list.get(i).getId() != 2) {
                listStatus.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
    }

    public void loadListObjectAgenda() {
        listObjectAgenda = new ArrayList();
        Dao dao = new Dao();
        List list = new AgendamentosDao().findBy(startDate, endDate, idFilial, null, idGrupoConvenio, idSubGrupoConvenio, pessoa.getPessoa().getId(), idStatus);
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            ObjectAgenda oa = new ObjectAgenda(
                    o.get(0),
                    o.get(1),
                    o.get(2),
                    o.get(3),
                    o.get(4),
                    o.get(5),
                    o.get(6),
                    o.get(7),
                    o.get(8),
                    o.get(9),
                    o.get(10),
                    o.get(11),
                    o.get(12),
                    o.get(13),
                    o.get(14),
                    o.get(15),
                    o.get(16),
                    o.get(17),
                    o.get(18),
                    o.get(19),
                    o.get(20),
                    (Agendamentos) dao.find(new Agendamentos(), Integer.parseInt(o.get(6).toString())),
                    o.get(21),
                    o.get(22),
                    o.get(23),
                    o.get(24)
            );
            listObjectAgenda.add(oa);
        }
    }

    public void loadListGrupoConvenio() {
        listGrupoConvenio = new ArrayList();
        List<GrupoConvenio> list = (List<GrupoConvenio>) new GrupoConvenioDao().findAllToAgendaHorarios();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoConvenio = list.get(i).getId();
            }
            listGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListConvenio() {
        listConvenio = new ArrayList();
        List<Pessoa> list = (List<Pessoa>) new ConvenioDao().findAllBySubGrupoConvenio(idSubGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idConvenio = list.get(i).getId();
            }
            listConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
    }

    public void loadListServicos() {
        valor = new Double(0);
        desconto = new Double(0);
        servico = null;
        listServicos = new ArrayList();
        List<Servicos> list = (List<Servicos>) new ServicosDao().findBySubgrupoConvenioAgendamentos(idSubGrupoConvenio);
        listServicos.add(new SelectItem(null, "SELECIONAR"));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                // idServico = list.get(i).getId();
            }
            listServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListServicosAdicionados() {
        listServicosAdicionados = new ArrayList();
    }

    public void loadListSubGrupoConvenio() {
        listSubGrupoConvenio = new ArrayList();
        List<SubGrupoConvenio> list = new SubGrupoConvenioDao().findAllByGrupoAndAgendamento(idGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSubGrupoConvenio = list.get(i).getId();
            }
            listSubGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListHorarios() {
        agendamentos = new ArrayList();
        if (idServico != null) {
            if (DataHoje.converteData(data).isEmpty()) {
                return;
            }
            agendaServico = new AgendaServicoDao().findByAgendaServico(idServico, false);
            Integer tempoServico = 0;
            if (agendaServico == null) {
                Messages.warn("Sistema", "CADASTRAR SERVIÇOS!");
                return;
            }
            tempoServico = agendaServico.getNrMinutos();
            Socios s = pessoa.getPessoa().getSocios();
            List list = new AgendamentosDao().findSchedules(DataHoje.converteData(data), filial.getId(), idSubGrupoConvenio, idConvenio, (s.getId() != -1));
            Dao dao = new Dao();
            // AgendaServico as = (AgendaServico) new Dao().find(new Servicos(), idServico);
            // as.getNrMinutos();
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                ObjectAgendamentos oa = new ObjectAgendamentos();
//                try {
//                    for (int j = i + 1; j < list.size(); j++) {
//                        List o2 = (List) list.get(j);
//                        Integer diff = DataHoje.diffHour(o.get(1).toString(), o2.get(1).toString());
//                        if (diff > agendaServico.getNrMinutos()) {
//                            Integer qtde = Integer.parseInt(o.get(2).toString());
//                            if(qtde > 0) {
//                                qtde = qtde-1;
//                                oa = new ObjectAgendamentos((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(o.get(0).toString())), Integer.parseInt(o.get(0).toString()), o.get(1).toString(), qtde.toString());
//                            }
//                        } else {
//                            oa = new ObjectAgendamentos((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(o.get(0).toString())), Integer.parseInt(o.get(0).toString()), o.get(1).toString(), o.get(2).toString());
//                        }
//                    }
//                } catch (Exception e) {
//                }
                oa = new ObjectAgendamentos((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(o.get(0).toString())), Integer.parseInt(o.get(0).toString()), o.get(1).toString(), o.get(2).toString());
                agendamentos.add(oa);
            }
        }

    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public List<SelectItem> getListConvenio() {
        return listConvenio;
    }

    public void setListConvenio(List<SelectItem> listConvenio) {
        this.listConvenio = listConvenio;
    }

    public List<SelectItem> getListGrupoConvenio() {
        return listGrupoConvenio;
    }

    public void setListGrupoConvenio(List<SelectItem> listGrupoConvenio) {
        this.listGrupoConvenio = listGrupoConvenio;
    }

    public List<SelectItem> getListSubGrupoConvenio() {
        return listSubGrupoConvenio;
    }

    public void setListSubGrupoConvenio(List<SelectItem> listSubGrupoConvenio) {
        this.listSubGrupoConvenio = listSubGrupoConvenio;
    }

    public Integer getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Integer idConvenio) {
        this.idConvenio = idConvenio;
    }

    public Integer getIdSubGrupoConvenio() {
        return idSubGrupoConvenio;
    }

    public void setIdSubGrupoConvenio(Integer idSubGrupoConvenio) {
        this.idSubGrupoConvenio = idSubGrupoConvenio;
    }

    public Integer getIdGrupoConvenio() {
        return idGrupoConvenio;
    }

    public void setIdGrupoConvenio(Integer idGrupoConvenio) {
        this.idGrupoConvenio = idGrupoConvenio;
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "new":
                agendamento = new Agendamentos();
                telefone = "";
                email = "";
                contato = "";
                agendamentoHorario = new AgendamentoHorario();
                agendamentoServico = new AgendamentoServico();
                listener("init");
                break;
            case "init":
                showModal = false;
                lockScheduler = false;
                loadListGrupoConvenio();
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                // loadListServicosAdicionados();
                break;
            case "clear":

                break;
            case "close_new_sched":
                pessoa = new Fisica();
                newSched = false;
                showModal = false;
                lockScheduler = false;
                agendamento.setId(null);
                break;
            case "grupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                break;
            case "subgrupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                break;
            case "convenio":
                listener("servicos");
                break;
            case "servicos":
                data = new Date();
                loadListHorarios();
                agendaServico = new AgendaServicoDao().findByAgendaServico(idServico, false);
                if (agendaServico == null) {
                    agendaServico = new AgendaServico();
                }
                calculaValorServico();
            case "data":
                loadListHorarios();
                String email = agendamento.getEmail();
                String telefone = agendamento.getTelefone();
                String obs = agendamento.getObs();
                agendamento = new Agendamentos();
                agendamento = new AgendamentosDao().findBy(DataHoje.converteData(data), filial.getId(), pessoa.getPessoa().getId());
                if (agendamento == null) {
                    agendamento = new Agendamentos();
                    agendamento.setEmail(email);
                    agendamento.setTelefone(telefone);
                    agendamento.setObs(obs);
                }
                break;
            case "filial":
                filial = (Filial) new Dao().find(new Filial(), idFilial);
                break;
            case "close_sched":
                agendamento.setAgendaStatus(null);
                agendamento.setData("");
                agendamento.setEmissao("");
                agendamento.setId(null);
                showModal = false;
                lockScheduler = false;
                reservaDao.clear();
                reservaDao.begin();
                GlobalSync.load();
                loadListHorarios();
                WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
                break;
            case "schedules":
                schedulesStatus = true;
                loadListStatus();
                startDate = DataHoje.converteData(data);
                endDate = "";
                loadListObjectAgenda();
                break;
            case "load_schedules":
                loadListObjectAgenda();
                break;
            case "close_schedules":
                schedulesStatus = false;
                break;

            default:
                break;
        }
    }

    public void calculaValorServico() {
        valor = new Double(0);
        desconto = new Double(0);
        servico = null;
        if (listServicos.isEmpty() && idServico == null && pessoa.getId() != -1 && agendaServico != null && agendaServico.getId() != null) {
            return;
        }
        Dao dao = new Dao();
        servico = (Servicos) dao.find(new Servicos(), idServico);

        //if (!enabledItensPedido) {
        if (!servico.isProduto()) {
            LancamentoIndividualDao db = new LancamentoIndividualDao();
            List<List> valorx = (List) db.pesquisaServicoValor(pessoa.getPessoa().getId(), idServico);
            if (!valorx.isEmpty()) {
                double vl = Double.valueOf(((Double) valorx.get(0).get(0)).toString());
                valor = vl;
                if (idConvenio != null && idConvenio != -1) {
                    DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
                    DescontoServicoEmpresa dse = dsed.findByGrupo(2, idServico, idConvenio);
                    if (dse != null) {
                        valor = Moeda.converteUS$(Moeda.valorDoPercentual(Moeda.converteR$Double(valor), dse.getDescontoString()));
                    }
                }
            } else {
                valor = new Double(0);
                GenericaMensagem.fatal("Atenção", "Valor do Serviço não encontrado");
            }
        }
    }

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public AgendaHorarios getAcrescentarHorario() {
        return acrescentarHorario;
    }

    public void setAcrescentarHorario(AgendaHorarios acrescentarHorario) {
        this.acrescentarHorario = acrescentarHorario;
    }

    public Agendamentos getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamentos agendamento) {
        this.agendamento = agendamento;
    }

    public Agendamentos getAgendamentosEdit() {
        return agendamentosEdit;
    }

    public void setAgendamentosEdit(Agendamentos agendamentosEdit) {
        this.agendamentosEdit = agendamentosEdit;
    }

    public Fisica getPessoa() {
        if (Sessions.exists("fisicaPesquisa") || Sessions.exists("fisicaPesquisaGenerica")) {
            newSched = false;
            agendamento = new Agendamentos();
            agendamentoHorario = new AgendamentoHorario();
            pessoa = new Fisica();
            Fisica f;
            if (Sessions.exists("fisicaPesquisaGenerica")) {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisaGenerica", true);
            } else {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            }
            email = f.getPessoa().getEmail1();
            telefone = f.getPessoa().getTelefone3();
            contato = "";
            listener("new");
            newSched = true;
            pessoa = f;
        }
        return pessoa;
    }

    public void setPessoa(Fisica pessoa) {
        this.pessoa = pessoa;
    }

    public List<ObjectAgendamentos> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<ObjectAgendamentos> agendamentos) {
        this.agendamentos = agendamentos;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Boolean getShowModal() {
        return showModal;
    }

    public void setShowModal(Boolean showModal) {
        // this.showModal = showModal;
    }

    public void findPessoaFisica() {
        newSched = false;
        Fisica f = FisicaUtils.findByCPF(pessoa);
        PF.update("form_agendamentos");
        PF.update("form_person");
        if (f != null) {
            listener("new");
            newSched = true;
            pessoa = f;
            email = pessoa.getPessoa().getEmail1();
            telefone = pessoa.getPessoa().getTelefone3();
            contato = "";
        }
//        if (pessoa.getPessoa().getDocumento().equals("___.___.___-__")) {
//            pessoa.getPessoa().setDocumento("");
//            return;
//        }
//        if (!pessoa.getPessoa().getDocumento().isEmpty() && !pessoa.getPessoa().getDocumento().equals("___.___.___-__")) {
//            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(pessoa.getPessoa().getDocumento()))) {
//                GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + pessoa.getPessoa().getDocumento());
//                PF.update("form_agendamentos");
//                return;
//            }
//        }
//        if (pessoa.getPessoa().getId() == -1) {
//            FisicaDao db = new FisicaDao();
//            List<Fisica> list = db.pesquisaFisicaPorDoc(pessoa.getPessoa().getDocumento());
//            Boolean success = false;
//            if (!list.isEmpty()) {
//                success = true;
//                if (!FisicaUtils.validation(list.get(0), "agendamentos")) {
//                    return;
//                }
//                listener("new");
//                showModal = true;
//                pessoa = list.get(0);
//
//            }
//
//            if (success) {
//                PF.update("form_person");
//                PF.update("form_agendamentos");
//            }
//        }

    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public List<AgendaServico> getListServicosAdicionados() {
        return listServicosAdicionados;
    }

    public void setListServicosAdicionados(List<AgendaServico> listServicosAdicionados) {
        this.listServicosAdicionados = listServicosAdicionados;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public AgendaServico getAgendaServico() {
        return agendaServico;
    }

    public void setAgendaServico(AgendaServico agendaServico) {
        this.agendaServico = agendaServico;
    }

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }

    public Boolean getNewSched() {
        return newSched;
    }

    public void setNewSched(Boolean newSched) {
        this.newSched = newSched;
    }

    public AgendamentoHorario getAgendamentoHorario() {
        return agendamentoHorario;
    }

    public void setAgendamentoHorario(AgendamentoHorario agendamentoHorario) {
        this.agendamentoHorario = agendamentoHorario;
    }

    public AgendamentoServico getAgendamentoServico() {
        return agendamentoServico;
    }

    public void setAgendamentoServico(AgendamentoServico agendamentoServico) {
        this.agendamentoServico = agendamentoServico;
    }

    public AgendaHorarioReservaDao getReservaDao() {
        return reservaDao;
    }

    public void setReservaDao(AgendaHorarioReservaDao reservaDao) {
        this.reservaDao = reservaDao;
    }

    public List<AgendamentoServico> getListAgendamentoServico() {
        return listAgendamentoServico;
    }

    public void setListAgendamentoServico(List<AgendamentoServico> listAgendamentoServico) {
        this.listAgendamentoServico = listAgendamentoServico;
    }

    public Boolean getLockScheduler() {
        return lockScheduler;
    }

    public void setLockScheduler(Boolean lockScheduler) {
        this.lockScheduler = lockScheduler;
    }

    public String getEndTime() {
        try {
            return DataHoje.incrementarHora(agendamentoHorario.getAgendaHorarios().getHora(), agendaServico.getNrMinutos() - 1);
        } catch (Exception e) {
            return "";
        }
    }

    public List<AgendamentoHorario> getListAgendamentoHorario() {
        return listAgendamentoHorario;
    }

    public void setListAgendamentoHorario(List<AgendamentoHorario> listAgendamentoHorario) {
        this.listAgendamentoHorario = listAgendamentoHorario;
    }

    public ObjectAgendamentos getObjectAgendamentos() {
        return objectAgendamentos;
    }

    public void setObjectAgendamentos(ObjectAgendamentos objectAgendamentos) {
        this.objectAgendamentos = objectAgendamentos;
    }

    public Boolean getNewRegister() {
        return newRegister;
    }

    public void setNewRegister(Boolean newRegister) {
        this.newRegister = newRegister;
    }

    public List<SelectItem> getListStatus() {
        return listStatus;
    }

    public void setListStatus(List<SelectItem> listStatus) {
        this.listStatus = listStatus;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public List<ObjectAgenda> getListObjectAgenda() {
        return listObjectAgenda;
    }

    public void setListObjectAgenda(List<ObjectAgenda> listObjectAgenda) {
        this.listObjectAgenda = listObjectAgenda;
    }

    public Boolean getSchedulesStatus() {
        return schedulesStatus;
    }

    public void setSchedulesStatus(Boolean schedulesStatus) {
        this.schedulesStatus = schedulesStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;

    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public class ObjectAgendamentos {

        private AgendaHorarios horario;
        private Agendamentos agendamento;
        private AgendamentoCancelamento cancelamento;
        private Integer horario_id;
        private String hora;
        private String quantidade;

        public ObjectAgendamentos() {
            this.horario = null;
            this.agendamento = null;
            this.cancelamento = null;
        }

        public ObjectAgendamentos(AgendaHorarios horario, Agendamentos agendamento, AgendamentoCancelamento cancelamento) {
            this.horario = horario;
            this.agendamento = agendamento;
            this.cancelamento = cancelamento;
        }

        public ObjectAgendamentos(AgendaHorarios horario, Agendamentos agendamento, AgendamentoCancelamento cancelamento, Integer horario_id, String hora, String quantidade) {
            this.horario = horario;
            this.agendamento = agendamento;
            this.cancelamento = cancelamento;
            this.horario_id = horario_id;
            this.hora = hora;
            this.quantidade = quantidade;
        }

        public ObjectAgendamentos(AgendaHorarios horario, Integer horario_id, String hora, String quantidade) {
            this.horario = horario;
            this.horario_id = horario_id;
            this.hora = hora;
            this.quantidade = quantidade;
        }

        public AgendaHorarios getHorario() {
            return horario;
        }

        public void setHorario(AgendaHorarios horario) {
            this.horario = horario;
        }

        public Agendamentos getAgendamento() {
            return agendamento;
        }

        public void setAgendamento(Agendamentos agendamento) {
            this.agendamento = agendamento;
        }

        public AgendamentoCancelamento getCancelamento() {
            return cancelamento;
        }

        public void setCancelamento(AgendamentoCancelamento cancelamento) {
            this.cancelamento = cancelamento;
        }

        public Integer getHorario_id() {
            return horario_id;
        }

        public void setHorario_id(Integer horario_id) {
            this.horario_id = horario_id;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }

        public String getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(String quantidade) {
            this.quantidade = quantidade;
        }

    }

    public void verificaNaoAtendidos() {
        new AgendamentosDao().verificaNaoAtendidosSegRegistroAgendamento();
    }

}
