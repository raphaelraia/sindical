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
import br.com.rtools.agendamentos.dao.AgendamentoServicoDao;
import br.com.rtools.agendamentos.dao.AgendamentosDao;
import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.beans.EmissaoGuiasBean;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.beans.FisicaUtils;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
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
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GlobalSync;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SMSWS;
import br.com.rtools.utilitarios.Sessions;
import br.com.rtools.utilitarios.WSSocket;
import java.io.IOException;
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
public class AtendimentosBean implements Serializable {

    // OBJETOS
    private Agendamentos agendamento;
    private Agendamentos agendamentosEdit;
    private Fisica pessoa;
    private Filial filial;
    private ConfiguracaoSocial configuracaoSocial;
    private Servicos servico;

    // LISTAS
    private List<ObjectAgenda> listObjectAgenda;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listRealizado;
    private List<SelectItem> listStatus;

    // INTEGER    
    private Integer idFilial;
    private Integer idGrupoConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idConvenio;
    private Integer idRealizado;
    private Integer idStatus;
    private AgendaHorarioReservaDao reservaDao;
    private ObjectAgenda objectAgenda;

    // DATAS
    private Date data;
    private Date startDate;
    private Date endDate;
    private String motivoCancelamento;
    private String motivoTroca;
    private String motivoTrocaColaborador;

    // BOLEANOS
    private Boolean liberaAcessaFilial;
    private Boolean desabilitaFilial;
    private Boolean showModal;

    // DOUBLE
    private Double valor;
    private Double desconto;

    // STRINGS
    private String telefone;
    private String email;
    private String contato;

    public AtendimentosBean() {
        motivoTrocaColaborador = "";
        Sessions.remove("agendamentosChange");
        motivoCancelamento = "";
        motivoTroca = "";
        reservaDao = new AgendaHorarioReservaDao();
        configuracaoSocial = ConfiguracaoSocial.get();
        pessoa = new Fisica();
        agendamento = new Agendamentos();
        agendamentosEdit = new Agendamentos();

        listFiliais = new ArrayList();
        listGrupoConvenio = new ArrayList();
        listSubGrupoConvenio = new ArrayList();
        listConvenio = new ArrayList();
        listObjectAgenda = new ArrayList();
        listRealizado = new ArrayList();
        listStatus = new ArrayList();

        idFilial = null;
        idGrupoConvenio = null;
        idSubGrupoConvenio = null;
        idConvenio = null;
        idStatus = null;

        liberaAcessaFilial = false;
        desabilitaFilial = false;
        data = new Date();
        showModal = false;
        valor = new Double(0);
        desconto = new Double(0);
        servico = null;

        telefone = "";
        email = "";
        contato = "";
        reservaDao.begin();
        loadListFilial();
        loadListStatus();
        startDate = data;
        endDate = null;
        loadListObjectAgenda();
        loadListGrupoConvenio();
        loadListSubGrupoConvenio();
        loadListConvenio();
        loadLiberaAcessaFilial();
    }

    public String finish() throws IOException {
        GlobalSync.load();
        this.loadListObjectAgenda();
        WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        List<ObjectAgenda> list = new ArrayList();
        Pessoa p = null;
        EmissaoGuiasBean emissaoGuiasBean = new EmissaoGuiasBean();
        emissaoGuiasBean.init();
        emissaoGuiasBean.setRotinaRetorno(new Rotina().get());
        Integer id_sub_grupo_convenio = null;
        Integer id_pessoa = null;
        List<AgendamentoServico> listAS = new ArrayList();
        Sessions.remove("emissaoGuiasBean");
        for (int i = 0; i < listObjectAgenda.size(); i++) {
            if (listObjectAgenda.get(i).getSelected()) {
                if (id_sub_grupo_convenio == null) {
                    id_sub_grupo_convenio = Integer.parseInt(listObjectAgenda.get(i).getId_convenio_grupo().toString());
                } else {
                    if (!id_sub_grupo_convenio.equals(Integer.parseInt(listObjectAgenda.get(i).getId_convenio_grupo().toString()))) {
                        Messages.warn("Validação", "Não é possível concluir com grupos e subgrupos diferentes!");
                        Sessions.remove("emissaoGuiasBean");
                        return null;
                    }
                }
                if (id_pessoa == null) {
                    if (listObjectAgenda.get(i).getId_realizado() == null) {
                        id_pessoa = Integer.parseInt(listObjectAgenda.get(i).getId_colaborador().toString());
                    } else {
                        id_pessoa = Integer.parseInt(listObjectAgenda.get(i).getId_realizado().toString());
                    }
                } else {
                    if (listObjectAgenda.get(i).getId_realizado() == null) {
                        if (!id_pessoa.equals(Integer.parseInt(listObjectAgenda.get(i).getId_colaborador().toString()))) {
                            Messages.warn("Validação", "Não é possível concluir com colaboradores diferentes!");
                            Sessions.remove("emissaoGuiasBean");
                            return null;
                        }
                    } else {
                        if (!id_pessoa.equals(Integer.parseInt(listObjectAgenda.get(i).getId_realizado().toString()))) {
                            Messages.warn("Validação", "Não é possível concluir com colaboradores diferentes!");
                            Sessions.remove("emissaoGuiasBean");
                            return null;
                        }
                    }
                }
                if (p == null) {
                    Sessions.put("pessoaPesquisa", (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(listObjectAgenda.get(i).getCodigo().toString())));
                    emissaoGuiasBean.getPessoa();
                    emissaoGuiasBean.loadListGrupos();
                    emissaoGuiasBean.setIdGrupo(Integer.parseInt(listObjectAgenda.get(i).getId_convenio_grupo().toString()));
                    emissaoGuiasBean.loadListSubgrupos();
                    emissaoGuiasBean.setIdSubgrupo(Integer.parseInt(listObjectAgenda.get(i).getId_convenio_sub_grupo().toString()));
                    emissaoGuiasBean.loadListJuridicas();
                    Juridica j;
                    if (listObjectAgenda.get(i).getId_realizado() == null) {
                        j = new JuridicaDao().pesquisaJuridicaPorPessoa(Integer.parseInt(listObjectAgenda.get(i).getId_colaborador().toString()));
                    } else {
                        j = new JuridicaDao().pesquisaJuridicaPorPessoa(Integer.parseInt(listObjectAgenda.get(i).getId_realizado().toString()));
                    }
                    if (j == null) {
                        return null;
                    }
                    emissaoGuiasBean.setIdConvenio(j.getId());
                }
                emissaoGuiasBean.loadListServicos();
                emissaoGuiasBean.setIdServico(Integer.parseInt(listObjectAgenda.get(i).getId_servico().toString()));
                emissaoGuiasBean.listenerEnabledItensPedidoListener();
                if (!emissaoGuiasBean.addServico2(false, true)) {
                    PF.update(":form_atendimentos:i_messsage_eg");
                    return null;
                }
                listAS.add(new AgendamentoServicoDao().findBy(Integer.parseInt(listObjectAgenda.get(i).getId_agendamento().toString()), Integer.parseInt(listObjectAgenda.get(i).getId_servico().toString())));

            }
        }
        emissaoGuiasBean.setListAgendamentoServico(listAS);
        Sessions.put("emissaoGuiasBean", emissaoGuiasBean);
        // loadListObjectAgenda();
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).redirectPage("emissaoGuias");
    }

    public String reschedule() throws IOException {
        Sessions.remove("agendamentosChange");
        if (motivoTroca.isEmpty() || motivoTroca.length() < 5) {
            Messages.warn("Validação", "INFORMAR MOTIVO DA TROCA!");
            PF.update("form_atendimentos:i_message_change");
            return null;
        }
        AgendamentoHorario ah = new AgendamentoHorario();
        List<AgendamentoHorario> list = new AgendamentoHorarioDao().findBy(Integer.parseInt(objectAgenda.getId_agendamento().toString()));
        if (list.isEmpty()) {
            Messages.warn("Validação", "ERRO SEM HORÁRIOS!");
            PF.update("form_atendimentos:i_message_change");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            ah = list.get(i);
            break;
        }
        AgendamentoServico as = new AgendamentoServicoDao().findBy(Integer.parseInt(objectAgenda.getId_agendamento().toString()));
        if (as == null) {
            Messages.warn("Validação", "ERRO SEM SERVIÇOS!");
            PF.update("form_atendimentos:i_message_change");
            return null;
        }
        Agendamentos a = objectAgenda.getAgendamentos();
        Dao dao = new Dao();
        AgendamentosBean agendamentosBean = new AgendamentosBean();
        agendamentosBean.loadListFilial();
        agendamentosBean.setIdFilial(ah.getAgendaHorarios().getFilial().getId());
        agendamentosBean.setFilial(ah.getAgendaHorarios().getFilial());
        Fisica f = new FisicaDao().pesquisaFisicaPorPessoa(a.getPessoa().getId());
        agendamentosBean.listener("new");
        agendamentosBean.setNewSched(true);
        agendamentosBean.setEmail(a.getEmail());
        agendamentosBean.setTelefone(a.getTelefone());
        agendamentosBean.setContato(a.getContato());
        agendamentosBean.setPessoa(f);
        agendamentosBean.setShowModal(true);
        agendamentosBean.setData(a.getDtData());
        agendamentosBean.loadListGrupoConvenio();
        agendamentosBean.setIdGrupoConvenio(ah.getAgendaHorarios().getSubGrupoConvenio().getGrupoConvenio().getId());
        agendamentosBean.loadListSubGrupoConvenio();
        agendamentosBean.setIdSubGrupoConvenio(ah.getAgendaHorarios().getSubGrupoConvenio().getId());
        agendamentosBean.loadListServicos();
        agendamentosBean.setIdServico(as.getServico().getId());
        agendamentosBean.loadListConvenio();
        agendamentosBean.setIdConvenio(ah.getAgendaHorarios().getConvenio().getId());
        agendamentosBean.loadListHorarios();
        agendamentosBean.setAgendaServico(new AgendaServicoDao().findByAgendaServico(as.getServico().getId(), false));
        if (agendamentosBean.getAgendaServico() == null) {
            agendamentosBean.setAgendaServico(new AgendaServico());
        }
        agendamentosBean.calculaValorServico();
        a.setMotivoTroca(motivoTroca);
        agendamentosBean.setTrocar(true);
        Sessions.put("agendamentosChange", a);
        Sessions.put("agendamentosBean", agendamentosBean);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).redirectPage("agendamentos");
    }

    public void cancel() {
        if (motivoCancelamento.isEmpty() || motivoCancelamento.length() < 5) {
            Messages.warn("Validação", "INFORMAR MOTIVO DO CANCELAMENTO!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        List<AgendamentoHorario> list = new AgendamentoHorarioDao().findBy(Integer.parseInt(objectAgenda.getId_agendamento().toString()));
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
        listObjectAgenda = new ArrayList();
        loadListObjectAgenda();
        objectAgenda.setId_status(agendamentosEdit.getAgendaStatus().getId());
        objectAgenda.setStatus(agendamentosEdit.getAgendaStatus().getDescricao());
        GlobalSync.load();
        motivoCancelamento = "";
        WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        delete_sms();
    }

    public void remove() {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public void showSched(ObjectAgenda oa) {
        this.objectAgenda = oa;
        this.showModal = true;
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
            if (list.get(i).getId() != 2 && list.get(i).getId() != 4) {
                listStatus.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
    }

    public void loadListObjectAgenda() {
        if (getBotaoCompareceu()) {
            return;
        }
        listObjectAgenda = new ArrayList();
        Dao dao = new Dao();
        List list = new AgendamentosDao().findBy(DataHoje.converteData(startDate), DataHoje.converteData(endDate), idFilial, idGrupoConvenio, idSubGrupoConvenio, idConvenio, pessoa.getPessoa().getId(), idStatus);
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
                    o.get(26),
                    o.get(27),
                    o.get(28),
                    o.get(29)
            );
            if (Integer.parseInt(oa.getId_status().toString()) == 3 || Integer.parseInt(oa.getId_status().toString()) == 5 || Integer.parseInt(oa.getId_status().toString()) == 6) {
                oa.setRendered(false);
            }
            listObjectAgenda.add(oa);
        }
    }

    public void loadListGrupoConvenio() {
        listGrupoConvenio = new ArrayList();
        List<GrupoConvenio> list = (List<GrupoConvenio>) new GrupoConvenioDao().findAllToAgendaHorarios();
        listGrupoConvenio.add(new SelectItem(null, "SELECIONAR"));
        idGrupoConvenio = null;
        idSubGrupoConvenio = null;
        idConvenio = null;
        for (int i = 0; i < list.size(); i++) {
            listGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListConvenio() {
        listConvenio = new ArrayList();
        listConvenio.add(new SelectItem(null, "SELECIONAR"));
        idConvenio = null;
        if (idSubGrupoConvenio != null) {
            List<Pessoa> list = (List<Pessoa>) new ConvenioDao().findAllBySubGrupoConvenio(idSubGrupoConvenio);
            for (int i = 0; i < list.size(); i++) {
                listConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadListSubGrupoConvenio() {
        listSubGrupoConvenio = new ArrayList();
        idSubGrupoConvenio = null;
        listSubGrupoConvenio.add(new SelectItem(null, "SELECIONAR"));
        if (idGrupoConvenio != null) {
            List<SubGrupoConvenio> list = new SubGrupoConvenioDao().findAllByGrupoAndAgendamento(idGrupoConvenio);
            for (int i = 0; i < list.size(); i++) {
                listSubGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
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
            case "init":
                showModal = false;
                loadListGrupoConvenio();
                loadListSubGrupoConvenio();
                loadListConvenio();
                // loadListServicosAdicionados();
                break;
            case "clear":

                break;
            case "close_new_sched":
                pessoa = new Fisica();
                showModal = false;
                agendamento.setId(null);
                break;
            case "grupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                break;
            case "subgrupo_convenio":
                loadListConvenio();
                break;
            case "convenio":
                break;
            case "data":
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
                reservaDao.clear();
                reservaDao.begin();
                GlobalSync.load();
                loadListObjectAgenda();
                WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
                break;
            case "schedules":
                loadListStatus();
                startDate = data;
                endDate = null;
                loadListObjectAgenda();
                break;
            case "load_schedules":
                loadListObjectAgenda();
                break;
            case "remove_pessoa":
                pessoa = new Fisica();
                if (idStatus == 1) {
                    startDate = new Date();
                    listener("load_schedules");
                }
                break;

            default:
                break;
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

            pessoa = new Fisica();
            Fisica f;
            if (Sessions.exists("fisicaPesquisaGenerica")) {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisaGenerica", true);
            } else {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            }
//            email = f.getPessoa().getEmail1();
//            telefone = f.getPessoa().getTelefone3();
//            contato = "";
//            listener("new");
            pessoa = f;
            if (idStatus == 1) {
                startDate = null;
                listener("load_schedules");
            }
        }
        return pessoa;
    }

    public void setPessoa(Fisica pessoa) {
        this.pessoa = pessoa;
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
        Fisica f = FisicaUtils.findByCPF(pessoa);
        PF.update("form_agendamentos");
        PF.update("form_person");
        if (f != null) {
            listener("new");
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

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }

    public AgendaHorarioReservaDao getReservaDao() {
        return reservaDao;
    }

    public void setReservaDao(AgendaHorarioReservaDao reservaDao) {
        this.reservaDao = reservaDao;
    }

    public String getEndTime() {
        try {
            return "";
            // return DataHoje.incrementarHora(agendamentoHorario.getAgendaHorarios().getHora(), agendaServico.getNrMinutos() - 1);
        } catch (Exception e) {
            return "";
        }
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

    public ObjectAgenda getObjectAgenda() {
        return objectAgenda;
    }

    public void setObjectAgenda(ObjectAgenda objectAgenda) {
        this.objectAgenda = objectAgenda;
    }

    public String getMotivoTroca() {
        return motivoTroca;
    }

    public void setMotivoTroca(String motivoTroca) {
        this.motivoTroca = motivoTroca;
    }

    public void print() {
        Jasper.TYPE = "default";
        Jasper.TITLE = "ATENDIMENTOS";
        Jasper.printReports("AG_ATENDIMENTOS.jasper", "Atendimentos", listObjectAgenda);
    }

    public List<SelectItem> getListRealizado() {
        return listRealizado;
    }

    public void setListRealizado(List<SelectItem> listRealizado) {
        this.listRealizado = listRealizado;
    }

    public Integer getIdRealizado() {
        return idRealizado;
    }

    public void setIdRealizado(Integer idRealizado) {
        this.idRealizado = idRealizado;
    }

    public String getMotivoTrocaColaborador() {
        return motivoTrocaColaborador;
    }

    public void setMotivoTrocaColaborador(String motivoTrocaColaborador) {
        this.motivoTrocaColaborador = motivoTrocaColaborador;
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

    public void blockList() {
        ObjectAgenda oa = new ObjectAgenda();
        int countSelect = 0;
        for (int i = 0; i < listObjectAgenda.size(); i++) {
            if (listObjectAgenda.get(i).getSelected()) {
                countSelect++;
            }
        }
        if (countSelect == 0) {
            for (int i = 0; i < listObjectAgenda.size(); i++) {
                listObjectAgenda.get(i).setDisabled(false);
                listObjectAgenda.get(i).setSelected(false);
                if (Integer.parseInt(listObjectAgenda.get(i).getId_status().toString()) == 3 || Integer.parseInt(listObjectAgenda.get(i).getId_status().toString()) == 5 || Integer.parseInt(listObjectAgenda.get(i).getId_status().toString()) == 6) {
                    oa.setRendered(false);
                }
            }
            return;
        }
        if (countSelect > 1) {
            return;
        }
        for (int i = 0; i < listObjectAgenda.size(); i++) {
            if (listObjectAgenda.get(i).getSelected()) {
                oa = listObjectAgenda.get(i);
                break;
            }
        }
        for (int i = 0; i < listObjectAgenda.size(); i++) {
            listObjectAgenda.get(i).setDisabled(false);
            if (Integer.parseInt(oa.getId_status().toString()) == 1 || Integer.parseInt(oa.getId_status().toString()) == 4) {
                if (!oa.getCodigo().toString().equals(listObjectAgenda.get(i).getCodigo().toString())) {
                    listObjectAgenda.get(i).setDisabled(true);
                }
            }
        }
    }

    public Boolean getBotaoCompareceu() {
        for (int i = 0; i < listObjectAgenda.size(); i++) {
            if (listObjectAgenda.get(i).getSelected()) {
                return true;
            }
        }
        return false;
    }

    public void delete_sms() {
        Dao dao = new Dao();
        Sms sms = new SmsDao().findBy("ag_agendamento", "id", agendamentosEdit.getId());
        if (sms == null) {
            return;
        }
        sms.setDtCancelamento(new Date());
        if (!dao.update(sms, true)) {
            return;
        }
        SMSWS smsws = new SMSWS();
        smsws.setReferenceInteger(agendamento.getId());
        smsws.schedule_to(1, agendamentosEdit.getData(), agendamentosEdit.getPrimeiraHora());
        smsws.delete();

    }

    public void changeColaborador() {
        motivoTrocaColaborador = "";
        loadListRealizado();
        if (objectAgenda.getId_realizado() == null) {
            idRealizado = Integer.parseInt(objectAgenda.getId_colaborador().toString());
        } else {
            idRealizado = Integer.parseInt(objectAgenda.getId_realizado().toString());
        }
    }

    public void updateColaborador() {
        Dao dao = new Dao();
        if (Integer.parseInt(objectAgenda.getId_colaborador().toString()) == idRealizado) {
            Messages.info("Correção", "Colaborador foi o mesmo que realizaou");
        }
        Pessoa p = (Pessoa) dao.find(new Pessoa(), idRealizado);
        if (objectAgenda.getMovimento() != null && objectAgenda.getMovimento().getBaixa() != null) {
            Messages.warn("Validação", "Não é possível trocar colaborador com movimento baixado");
            return;
        }
        if (Integer.parseInt(objectAgenda.getId_status().toString()) != 1 && Integer.parseInt(objectAgenda.getId_status().toString()) != 4) {
            Messages.warn("Validação", "Não é possível trocar colaborador que o Status não seja Agedando ou Encaixe");
            return;
        }
        String beforeUpdate = " "
                + "Troca de Colaborador - "
                + "Motivo: " + motivoTrocaColaborador + " - "
                + "Agendamento ID: " + objectAgenda.getId_agendamento() + " - "
                + "Data: " + objectAgenda.getData() + " - "
                + "Horário: " + objectAgenda.getHorario_inicial() + " às " + objectAgenda.getHorario_final() + " - "
                + "Cliente: " + objectAgenda.getNome() + " - "
                + "Documento: " + objectAgenda.getDocumento() + " - "
                + "Colaborador: " + objectAgenda.getColaborador() + " - "
                + "Realizado por: " + (objectAgenda.getRealizado() == objectAgenda.getColaborador() ? "" : objectAgenda.getRealizado()) + " - ";
        // Agendamentos a = (Agendamentos) dao.find(new Agendamentos(), Integer.parseInt(objectAgenda.getId_agendamento().toString()));
        objectAgenda.getAgendamentos().setConvenioRealizado(p);
        objectAgenda.setId_realizado(p.getId());
        objectAgenda.setRealizado(p.getNome());
        objectAgenda.setRealizado_documento(p.getDocumento());
        if (!dao.update(objectAgenda.getAgendamentos(), true)) {
            Messages.warn("Validação", "Erro ao trocar Convênio!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("ag_agendamento");
        novoLog.setCodigo(Integer.parseInt(objectAgenda.getId_agendamento().toString()));
        novoLog.update(beforeUpdate,
                "Agendamento ID: " + objectAgenda.getId_agendamento() + " - "
                + "Data: " + objectAgenda.getData() + " - "
                + "Horário: " + objectAgenda.getHorario_inicial() + " às " + objectAgenda.getHorario_final() + " - "
                + "Cliente: " + objectAgenda.getNome() + " - "
                + "Documento: " + objectAgenda.getDocumento() + " - "
                + "Colaborador: " + objectAgenda.getColaborador() + " - "
                + "Realizado por: " + (objectAgenda.getRealizado() == objectAgenda.getColaborador() ? "" : objectAgenda.getRealizado()) + " - ");
        Messages.info("Sucesso", "Convênio realizado trocado com sucesso");
        loadListObjectAgenda();
        motivoTrocaColaborador = "";
    }

    public void loadListRealizado() {
        listRealizado = new ArrayList();
        Socios s = pessoa.getPessoa().getSocios();
        List<Pessoa> list = (List<Pessoa>) new ConvenioDao().findAllBySubGrupoConvenio(Integer.parseInt(objectAgenda.getId_convenio_sub_grupo().toString()));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRealizado = list.get(i).getId();
            }
            listRealizado.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
    }

}
