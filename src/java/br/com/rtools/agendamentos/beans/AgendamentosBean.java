package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.AgendaServico;
import br.com.rtools.agendamentos.AgendaStatus;
import br.com.rtools.agendamentos.AgendamentoCancelamento;
import br.com.rtools.agendamentos.AgendamentoHorario;
import br.com.rtools.agendamentos.AgendamentoServico;
import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.agendamentos.dao.AgendaHorarioReservaDao;
import br.com.rtools.agendamentos.dao.AgendaHorariosDao;
import br.com.rtools.agendamentos.dao.AgendaServicoDao;
import br.com.rtools.agendamentos.dao.AgendamentoHorarioDao;
import br.com.rtools.agendamentos.dao.AgendamentoServicoDao;
import br.com.rtools.agendamentos.dao.AgendamentosDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.Convenio;
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
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.beans.FisicaUtils;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.sistema.Sms;
import br.com.rtools.sistema.dao.SmsDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GlobalSync;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SMSWS;
import br.com.rtools.utilitarios.Sessions;
import br.com.rtools.utilitarios.WSSocket;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private List<Servicos> listServicosSuggestions;
    private List<SelectItem> listHora;
    private List<String> listHoras;
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
    private Date dataPre;
    private Date startDate;
    private Date endDate;
    private String motivoCancelamento;
    private String queryHoras;

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
    private Boolean trocar;
    private String hora;
    private String horaPre;

    public AgendamentosBean() {
        horaPre = "";
        trocar = false;
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
        listHora = new ArrayList();
        listHoras = new ArrayList();
        listAgendamentoServico = new ArrayList();
        listFiliais = new ArrayList();
        listGrupoConvenio = new ArrayList();
        listSubGrupoConvenio = new ArrayList();
        listConvenio = new ArrayList();
        listServicos = new ArrayList();
        listServicosSuggestions = new ArrayList();
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
        dataPre = new Date();
        newSched = false;
        showModal = false;
        lockScheduler = false;
        newRegister = false;
        schedulesStatus = false;
        valor = new Double(0);
        desconto = new Double(0);
        servico = null;

        queryHoras = "";
        telefone = "";
        email = "";
        contato = "";
        hora = "";
        reservaDao.begin();
        loadLiberaAcessaFilial();
        loadListFilial();
    }

    public void scheduler() {
        Socios s = pessoa.getPessoa().getSocios();
        List list = new AgendamentosDao().findSchedules(DataHoje.converteData(data), filial.getId(), idSubGrupoConvenio, idConvenio, (s.getId() != -1), false, null, null, hora.equals("TODOS") ? "" : hora);
        Dao dao = new Dao();
        ObjectAgendamentos oa = null;
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oa = new ObjectAgendamentos((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(o.get(0).toString())), Integer.parseInt(o.get(0).toString()), o.get(1).toString(), o.get(2).toString(), Boolean.parseBoolean(o.get(3).toString()), null);
            break;
        }
        if (oa != null) {
            scheduler(oa);
        }
    }

    public void scheduler(ObjectAgendamentos oa) {
        scheduler(oa, true);
    }

    public void scheduler(ObjectAgendamentos oa, Boolean confirm) {
        newRegister = false;
        Dao dao = new Dao();
        if (oa.getNrQuantidade() == 0 && !agendaServico.getEncaixe()) {
            if (!oa.getEncaixe()) {
                GenericaMensagem.warn("Validação", "Não há disponibilidade de horários para o colaborador/serviço e não permite encaixe!");
                PF.update("form_agendamentos:i_message_sched");
                PF.update("form_agendamentos:growl_ag");
                return;
            }
        } else {
            if (oa.getNrQuantidade() == 0 && !oa.getEncaixe()) {
                GenericaMensagem.warn("Validação", "Não há disponibilidade de horários e não é permitido encaixe para este colaborador! " + (agendaServico.getEncaixe() ? "O serviço permite, mas o colaborador não possuí essa condição." : ""));
                PF.update("form_agendamentos:i_message_sched");
                PF.update("form_agendamentos:growl_ag");
                return;
            }
        }
        Boolean encaixe = false;
        if (oa.getNrQuantidade() == 0 && agendaServico.getEncaixe() && oa.getEncaixe()) {
            encaixe = true;
        }
        if (oa.getAgendamento() == null || oa.getAgendamento().getId() == null || encaixe) {
            int amoutTime = 0;
            AgendaHorarios ah = (AgendaHorarios) dao.find(new AgendaHorarios(), oa.horario_id);
            int totalTime = agendaServico.getNrMinutos();
            String lastHour = "";
//            if (agendamento.getId() != null) {
//                agendamento.setId(null);
//            }
            Servicos servicos = agendaServico.getServico();
            List<Agendamentos> resultList;
            MovimentoDao db = new MovimentoDao();
            AgendamentosDao ad = new AgendamentosDao();
            Socios s = pessoa.getPessoa().getSocios();
            if (servicos.getPeriodo() != null) {
                DataHoje dh = new DataHoje();
                // SEM CONTROLE FAMILIAR ---
                if (!servicos.isFamiliarPeriodo()) {

                    if (!servicos.isValidadeGuiasVigente()) {
                        resultList = ad.existsPessoaServicoPeriodoAtivo(pessoa.getPessoa().getId(), servicos.getId(), servicos.getPeriodo().getDias(), false);
                        if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!resultList.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), resultList.get(0).getEmissao()) : ""));
                            PF.update("form_agendamentos:i_message_sched");
                            PF.update("form_agendamentos:growl_ag");
                            if (!trocar) {
                                return;
                            }
                        }
                        resultList.clear();
                    } else {
                        resultList = ad.existsPessoaServicoMesVigente(pessoa.getPessoa().getId(), servicos.getId(), false, DataHoje.converteData(data));
                        if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                            PF.update("form_agendamentos:i_message_sched");
                            PF.update("form_agendamentos:growl_ag");
                            if (!trocar) {
                                return;
                            }
                        }
                        resultList.clear();
                    }

                }

                // COM CONTROLE FAMILIAR --- 
                if (servicos.isFamiliarPeriodo()) {
                    //Socios socios = dbs.pesquisaSocioPorPessoaAtivo(pessoa.getId());

                    // NÃO SÓCIO ---
                    if (s.getId() == -1) {
                        if (!servicos.isValidadeGuiasVigente()) {
                            resultList = ad.existsPessoaServicoPeriodoAtivo(pessoa.getPessoa().getId(), servicos.getId(), servicos.getPeriodo().getDias(), false);
                            if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                                GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!resultList.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), resultList.get(0).getEmissao()) : ""));
                                PF.update("form_agendamentos:i_message_sched");
                                PF.update("form_agendamentos:growl_ag");
                                if (!trocar) {
                                    return;
                                }
                            }
                            resultList.clear();
                        } else {
                            resultList = ad.existsPessoaServicoMesVigente(pessoa.getPessoa().getId(), servicos.getId(), false, DataHoje.converteData(data));
                            if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                                GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                                PF.update("form_agendamentos:i_message_sched");
                                PF.update("form_agendamentos:growl_ag");
                                if (!trocar) {
                                    return;
                                }
                            }
                            resultList.clear();
                        }
                        // SOCIO ---
                    } else if (!servicos.isValidadeGuiasVigente()) {
                        resultList = ad.existsPessoaServicoPeriodoAtivo(s.getMatriculaSocios().getId(), servicos.getId(), servicos.getPeriodo().getDias(), true);
                        if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!resultList.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), resultList.get(0).getEmissao()) : ""));
                            PF.update("form_agendamentos:i_message_sched");
                            PF.update("form_agendamentos:growl_ag");
                            if (!trocar) {
                                return;
                            }
                        }
                        resultList.clear();
                    } else {
                        resultList = ad.existsPessoaServicoMesVigente(s.getMatriculaSocios().getId(), servicos.getId(), true);
                        if (resultList.size() >= servicos.getQuantidadePeriodo() && valor == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                            PF.update("form_agendamentos:i_message_sched");
                            PF.update("form_agendamentos:growl_ag");
                            if (!trocar) {
                                return;
                            }
                        }
                        resultList.clear();
                    }
                }
            }
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
                                    PF.update("form_agendamentos");
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
            if (agendamento.getTelefone().trim().isEmpty() && agendamento.getEmail().trim().isEmpty()) {
                Messages.warn("Validação", "INFORMAR E-MAIL OU TELEFONE");
                PF.update("form_agendamentos");
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
        if (trocar) {
            Messages.info("IMPORTANTE", "TROCA DE HORÁRIO");
        }
        PF.update("form_agendamentos");

    }

    public void save() {
        Dao dao = new Dao();
        newRegister = false;
        dao.openTransaction();
        Convenio c = null;
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
        String horario = "";
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
            if (horario.isEmpty()) {
                horario = listAgendamentoHorario.get(i).getAgendaHorarios().getHora();
            }
            if (c == null) {
                Pessoa colaborador = listAgendamentoHorario.get(i).getAgendaHorarios().getConvenio();
                c = new ConvenioDao().find(idSubGrupoConvenio, colaborador.getJuridica().getId());
            }
        }
        if (trocar) {
            if (Sessions.exists("agendamentosChange")) {
                Agendamentos a = (Agendamentos) Sessions.getObject("agendamentosChange", true);
                List<AgendamentoHorario> list = new AgendamentoHorarioDao().findBy(a.getId());
                if (list.isEmpty()) {
                    dao.rollback();
                    Messages.warn("Validação", "ERRO SEM HORÁRIOS!");
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    AgendamentoCancelamento ac = new AgendamentoCancelamento();
                    ac.setDtData(new Date());
                    ac.setMotivo(a.getMotivoTroca());
                    ac.setUsuario(Usuario.getUsuario());
                    ac.setAgendamentoHorario(list.get(i));
                    if (!dao.save(ac)) {
                        dao.rollback();
                        Messages.warn("Erro", "AO CANCELAR AGENDA!");
                        return;
                    }
                }
                AgendamentoServico as = new AgendamentoServicoDao().findBy(a.getId());
                if (as == null) {
                    dao.rollback();
                    Messages.warn("Validação", "ERRO SEM SERVIÇOS!");
                    return;
                }
                if (as.getMovimento() != null) {
                    dao.rollback();
                    Messages.warn("Validação", "NÃO É POSSÍVEL TRANSFERIR AGENDA COM MOVIMENTO!");
                    return;
                }
                a.setAgendaStatus((AgendaStatus) dao.find(new AgendaStatus(), 3));
                if (!dao.update(a)) {
                    dao.rollback();
                    Messages.warn("Erro", "AO CANCELAR AGENDA!");
                    return;
                }
                ((AtendimentosBean) Sessions.getObject("atendimentosBean")).listener("close_sched");
                ((AtendimentosBean) Sessions.getObject("atendimentosBean")).loadListObjectAgenda();

            }
        }
        Messages.info("Sucesso", "AGENDA CRIADA!");
        dao.commit();
        send_sms(c, horario);
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
        delete_sms();
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
        listHoras = new ArrayList<>();
        listHora = new ArrayList<>();
        String h = hora;
        listener("data");
        hora = "";
        queryHoras = "";
        listener("hora");
        for (int i = 0; i < listHora.size(); i++) {
            if (listHoras.get(i).equals(h)) {
                hora = h;
                break;
            }
        }
        listener("subgrupo_convenio");
    }

    public void dataPreListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataPre = DataHoje.converte(format.format(event.getObject()));
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
        if (idFilial == null) {
            if (f.getId() != -1) {
                idFilial = f.getId();
            }
        }
        listener("filial");
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
        List list = new AgendamentosDao().findBy(DataHoje.converteData(startDate), DataHoje.converteData(endDate), idFilial, null, null, null, pessoa.getPessoa().getId(), idStatus);
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
                    o.get(21),
                    (Agendamentos) dao.find(new Agendamentos(), Integer.parseInt(o.get(6).toString())),
                    o.get(22),
                    o.get(23),
                    o.get(24),
                    o.get(26)
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
        Socios s = pessoa.getPessoa().getSocios();
        List<Pessoa> list = (List<Pessoa>) new ConvenioDao().findAllBySubGrupoConvenio(idSubGrupoConvenio, null, data, false, (s.getId() != -1), hora.equals("TODOS") ? "" : hora);
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
        Servicos s = new AgendamentosDao().maxServico(MacFilial.getAcessoFilial().getFilial().getId());
        for (int i = 0; i < list.size(); i++) {
            if (s != null) {
                if (s.getId() == list.get(i).getId()) {
                    idServico = list.get(i).getId();
                }
            }
            listServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListServicosSuggestion() {
        listServicosSuggestions = new ArrayList();
        if (pessoa != null && pessoa.getId() != -1 && idSubGrupoConvenio != null) {
            listServicosSuggestions = (List<Servicos>) new AgendamentosDao().suggestions(pessoa.getPessoa().getId(), idSubGrupoConvenio);
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
            Integer agendamento_id = null;
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
                List<Agendamentos> a = new ArrayList();
                try {
                    if (Integer.parseInt(o.get(2).toString()) == 0) {
                        a = new AgendamentosDao().findBy(idFilial, idSubGrupoConvenio, idConvenio, null, DataHoje.converteData(data), o.get(1).toString());
                    }
                } catch (Exception e) {

                }
                oa = new ObjectAgendamentos((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(o.get(0).toString())), Integer.parseInt(o.get(0).toString()), o.get(1).toString(), o.get(2).toString(), Boolean.parseBoolean(o.get(3).toString()), a);
                oa.setFirstHora(hora);
                agendamentos.add(oa);
            }
        }

    }

    public void loadListHora() {
        listHora = new ArrayList();
        listHora.add(new SelectItem("", ""));
        listHoras.add("TODOS");
        Socios s = pessoa.getPessoa().getSocios();
        List list = new AgendaHorariosDao().listHora(idFilial, idSubGrupoConvenio, data, false, (s.getId() != -1));
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listHora.add(new SelectItem((String) o.get(0), (String) o.get(0)));
            listHoras.add((String) o.get(0));
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
                SubGrupoConvenio sgc = new AgendamentosDao().maxSubGrupoConvenio(MacFilial.getAcessoFilial().getFilial().getId());
                loadListGrupoConvenio();
                if (sgc != null && !listGrupoConvenio.isEmpty()) {
                    idGrupoConvenio = sgc.getGrupoConvenio().getId();
                }
                loadListSubGrupoConvenio();
                if (sgc != null && !listSubGrupoConvenio.isEmpty()) {
                    idSubGrupoConvenio = sgc.getId();
                }
                loadListConvenio();
                loadListServicos();
                loadListServicosSuggestion();
                loadListHora();
                if (idServico != null) {
                    loadListHorarios();
                }
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
                dataPre = null;
                horaPre = "";
                break;
            case "grupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                loadListServicosSuggestion();
                loadListHorarios();
                break;
            case "subgrupo_convenio":
                loadListConvenio();
                loadListServicos();
                loadListServicosSuggestion();
                loadListHora();
                loadListHorarios();
                break;
            case "convenio":
                listener("servicos");
                break;
            case "servicos":
                loadListHorarios();
                agendaServico = new AgendaServicoDao().findByAgendaServico(idServico, false);
                if (agendaServico == null) {
                    agendaServico = new AgendaServico();
                }
                calculaValorServico();
            case "data":
                dataPre = null;
                horaPre = "";
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
            case "hora":
                loadListConvenio();
                loadListHorarios();
                break;
            case "close_sched":
                dataPre = null;
                horaPre = "";
                startDate = new Date();
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
                startDate = data;
                endDate = null;
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
        if (servico != null) {
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
            if (dataPre != null) {
                data = dataPre;
                dataPre = null;
            }
            if (!horaPre.isEmpty()) {
                hora = horaPre;
                queryHoras = horaPre;
            }
            pessoa = f;
            listener("new");
            Agendamentos a = new AgendamentosDao().findMaxIdByPessoa(f.getPessoa().getId());
            if (a == null) {
                telefone = f.getPessoa().getTelefone3();
                email = f.getPessoa().getEmail1();
                contato = "";
            } else {
                if (a.getTelefone().isEmpty()) {
                    telefone = f.getPessoa().getTelefone3();
                } else {
                    telefone = a.getTelefone();
                }
                if (a.getEmail().isEmpty()) {
                    email = f.getPessoa().getEmail1();
                } else {
                    email = a.getEmail();
                }
                if (!a.getContato().isEmpty()) {
                    contato = a.getContato();
                }
            }
            if (!horaPre.isEmpty()) {
                hora = horaPre;
                queryHoras = horaPre;
                horaPre = "";
            }
            newSched = true;
//            loadListConvenio();
//            loadListHora();
//            if (idServico != null) {
//                loadListHorarios();
//            }
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
            if (dataPre != null) {
                data = dataPre;
                dataPre = null;
            }
            if (!horaPre.isEmpty()) {
                hora = horaPre;
                queryHoras = horaPre;
            }
            pessoa = f;
            listener("new");
            Agendamentos a = new AgendamentosDao().findMaxIdByPessoa(f.getPessoa().getId());
            if (a == null) {
                telefone = f.getPessoa().getTelefone3();
                email = f.getPessoa().getEmail1();
                contato = "";
            } else {
                if (a.getTelefone().isEmpty()) {
                    telefone = f.getPessoa().getTelefone3();
                } else {
                    telefone = a.getTelefone();
                }
                if (a.getEmail().isEmpty()) {
                    email = f.getPessoa().getEmail1();
                } else {
                    email = a.getTelefone();
                }
                if (!a.getContato().isEmpty()) {
                    contato = a.getContato();
                }
            }
            if (!horaPre.isEmpty()) {
                hora = horaPre;
                queryHoras = horaPre;
                horaPre = "";
            }
            newSched = true;
//            loadListConvenio();
//            loadListHora();
//            if (idServico != null) {
//                loadListHorarios();
//            }
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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

    public Boolean getTrocar() {
        return trocar;
    }

    public void setTrocar(Boolean trocar) {
        this.trocar = trocar;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public List<SelectItem> getListHora() {
        return listHora;
    }

    public void setListHora(List<SelectItem> listHora) {
        this.listHora = listHora;
    }

    public List<String> getListHoras() {
        return listHoras;
    }

    public void setListHoras(List<String> listHoras) {
        this.listHoras = listHoras;
    }

    public String getQueryHoras() {
        return queryHoras;
    }

    public void setQueryHoras(String queryHoras) {
        this.queryHoras = queryHoras;
    }

    public Date getDataPre() {
        return dataPre;
    }

    public void setDataPre(Date dataPre) {
        this.dataPre = dataPre;
    }

    public String getHoraPre() {
        return horaPre;
    }

    public void setHoraPre(String horaPre) {
        this.horaPre = horaPre;
    }

    public List<Servicos> getListServicosSuggestions() {
        return listServicosSuggestions;
    }

    public void setListServicosSuggestions(List<Servicos> listServicosSuggestions) {
        this.listServicosSuggestions = listServicosSuggestions;
    }

    public class ObjectAgendamentos {

        private AgendaHorarios horario;
        private Agendamentos agendamento;
        private AgendamentoCancelamento cancelamento;
        private Integer horario_id;
        private String hora;
        private String quantidade;
        private Boolean encaixe;
        private List<Agendamentos> listAgendamentos;
        private String firstHora;

        public ObjectAgendamentos() {
            this.horario = null;
            this.agendamento = null;
            this.cancelamento = null;
            this.encaixe = false;
            this.listAgendamentos = new ArrayList();
            this.firstHora = "";
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

        public ObjectAgendamentos(AgendaHorarios horario, Integer horario_id, String hora, String quantidade, Boolean encaixe, List<Agendamentos> listAgendamentos) {
            this.horario = horario;
            this.horario_id = horario_id;
            this.hora = hora;
            this.quantidade = quantidade;
            this.encaixe = encaixe;
            this.listAgendamentos = listAgendamentos;
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

        public int getNrQuantidade() {
            try {
                return Integer.parseInt(quantidade);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public Boolean getEncaixe() {
            return encaixe;
        }

        public void setEncaixe(Boolean encaixe) {
            this.encaixe = encaixe;
        }

        public List<Agendamentos> getListAgendamentos() {
            return listAgendamentos;
        }

        public void setListAgendamentos(List<Agendamentos> listAgendamentos) {
            this.listAgendamentos = listAgendamentos;
        }

        public String getFirstHora() {
            return firstHora;
        }

        public void setFirstHora(String firstHora) {
            this.firstHora = firstHora;
        }

    }

    public void verificaNaoAtendidos() {
        new AgendamentosDao().verificaNaoAtendidosSegRegistroAgendamento();
    }

    public String back() {
        ((AtendimentosBean) Sessions.getObject("atendimentosBean")).listener("load_schedules");
        return ((ChamadaPaginaBean) Sessions.getObject("chamadaPaginaBean")).back();
    }

    public List<String> findHorarios(String query) {
        List<String> results = new ArrayList<>();
        for (int i = 0; i < listHoras.size(); i++) {
            if (listHoras.get(i).startsWith(query)) {
                results.add(listHoras.get(i));
            }
        }
        return results;
    }

    public void selectedHora(SelectEvent event) {
        hora = event.getObject().toString();
        listener("hora");

    }

    public void selectedServico(Servicos s) {
        Boolean ok = false;
        for (int i = 0; i < listServicos.size(); i++) {
            try {
                if (Integer.parseInt(listServicos.get(i).getValue().toString()) == s.getId()) {
                    idServico = s.getId();
                    ok = true;
                    break;
                }
            } catch (Exception e) {

            }
        }
        if (ok) {
            listener("servicos");
        } else {
            Messages.warn("Sistema", "Serviço não disponível atualmente!");
        }
    }

    public void send_sms(Convenio c, String horario) {
        Dao dao = new Dao();
        Registro r = Registro.get();
        if (r.getEnviaSms() && configuracaoSocial.getEnviaSmsAgendamento()) {
            try {
                if (agendamento.getData() == null ? DataHoje.data() != null : !agendamento.getData().equals(DataHoje.data()) && !agendamento.getTelefone().isEmpty()) {
                    SMSWS smsws = new SMSWS();
                    if (smsws.getConfiguracaoSms() != null) {
                        smsws.setMobile_phone(agendamento.getTelefone());
                        smsws.setReferenceInteger(agendamento.getId());
                        smsws.setFilial(filial);
                        smsws.schedule_to(1, agendamento.getData(), horario);
                        String resp = "";
                        if (c != null) {
                            if (c.getTipoTratamento() != null && !c.getAbreviacao().isEmpty()) {
                                resp = c.getTipoTratamento().getDescricao() + " " + c.getAbreviacao();
                            } else {
                                String respSplit[] = c.getJuridica().getPessoa().getNome().split(" ");
                                for (int i = 0; i < respSplit.length; i++) {
                                    resp = respSplit[i];
                                    break;
                                }
                            }
                        }
                        String message = "Agendamento p/ " + agendamento.getData() + " as " + horario + " com " + resp;
                        smsws.setMessage(message);
                        smsws.send();
                        Sms sms = new Sms();
                        sms.setRotina(new Rotina().get());
                        sms.setConfiguracaoSms(smsws.getConfiguracaoSms());
                        sms.setMensagem(smsws.getMessage());
                        sms.setNumero(smsws.getMobile_phone());
                        sms.setReferencia(smsws.getReference());
                        sms.setDtEnvio(new Date());
                        try {
                            sms.setDtAgendamento(smsws.getDate());
                        } catch (Exception e) {

                        }
                        sms.setUsuario(Usuario.getUsuario());
                        sms.setDestinatario(agendamento.getPessoa());
                        sms.setTabela("ag_agendamento");
                        sms.setChave("id");
                        sms.setCodigo(agendamento.getId());
                        dao.save(sms, true);
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public void delete_sms() {
        Dao dao = new Dao();
        Sms sms = new SmsDao().findBy("ag_agendamento", "id", agendamento.getId());
        if (sms == null) {
            return;
        }
        sms.setDtCancelamento(new Date());
        if (!dao.update(sms, true)) {
            return;
        }
        SMSWS smsws = new SMSWS();
        smsws.setReferenceInteger(agendamento.getId());
        smsws.schedule_to(1, agendamento.getData(), agendamento.getPrimeiraHora());
        smsws.delete();

    }
}
