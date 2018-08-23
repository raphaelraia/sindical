package br.com.rtools.homologacao.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.atendimento.AteMovimento;
import br.com.rtools.atendimento.AteStatus;
import br.com.rtools.atendimento.dao.AtendimentoDao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.ListaAgendamento;
import br.com.rtools.homologacao.Recepcao;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.Status;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.impressao.ParametroSenha;
import br.com.rtools.impressao.beans.SenhaHomologacao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.Profissao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SelectItemSort;
import br.com.rtools.utilitarios.Upload;
import br.com.rtools.utilitarios.WSSocket;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

@ManagedBean
@SessionScoped
public class RecepcaoBean implements Serializable {

    private Agendamento agendamento;
    private Agendamento agendamentoEdit;
    private int idStatus;
    private boolean ocultaData;
    private boolean ocultaSenha;
    private boolean ocultaStatus;
    private boolean ocultaPreposto;
    private boolean ocultaHomologador;
    private boolean ocultaDatapesquisa;
    private boolean ocultaColunaEmpresa;
    private boolean ocultaColunaPessoaFisica;
    private boolean isPesquisarPessoaFisicaFiltro;
    private boolean isPesquisarPessoaJuridicaFiltro;
    private boolean desabilitaAtualizacaoAutomatica;
    private boolean desabilitaPesquisaProtocolo;
    private boolean dataPesquisaTodas;
    private int isCaso;
    private List<DataObject> listaHorarios;
    private MacFilial macFilial;
    private String strData;
    private String strDataFinal;
    private String strEndereco;
    private String msgRecepcao;
    private String msgConfirma;
    private String statusEmpresa;
    private Date data;
    private Date dataInicial;
    private Date dataFinal;
    private Fisica fisica;
    private Juridica juridica;
    private Recepcao recepcao;
    private Registro registro;
    private Profissao profissao;
    private int idMotivoDemissao;
    private String tipoPesquisa;
    private String dataPesquisa;
    private Cancelamento cancelamento;
    private int id_protocolo;
    private String numeroProtocolo;
    private List<ListaAgendamento> listaRecepcaos;
    private List<Senha> listaAtendimentoSimples;
    private String dataInicialString;
    private String dataFinalString;
    private boolean openDialog;
    private List<SelectItem> listaStatus;
    private List<SelectItem> listaMotivoDemissao;
    private int progressUpdate;
    private int progressLabel;
    private boolean startPooling;

    private int idStatusAtendimento;
    private List<SelectItem> listaStatusAtendimento;
    private String dataPesquisaAtendimento;
    private String dataInicialAtendimento;
    private String dataFinalAtendimento;
    private int indexTab = 0;
    private String descricaoFisica;
    private String tipoPesquisaAtendimento;
    private String tipoFisicaPesquisa;
    private List listFiles;
    private List<SelectItem> listHomologadores;
    private Integer idHomologador;
    private String motivoAlteracaoHomologador;
    private ConfiguracaoArrecadacao configuracaoArrecadacao;

    public RecepcaoBean() {
        agendamento = new Agendamento();
        agendamentoEdit = new Agendamento();
        idStatus = 0;
        ocultaData = true;
        ocultaSenha = false;
        ocultaStatus = true;
        ocultaPreposto = false;
        ocultaHomologador = false;
        ocultaDatapesquisa = false;
        ocultaColunaEmpresa = false;
        ocultaColunaPessoaFisica = false;
        isPesquisarPessoaFisicaFiltro = false;
        isPesquisarPessoaJuridicaFiltro = false;
        desabilitaAtualizacaoAutomatica = false;
        desabilitaPesquisaProtocolo = false;
        dataPesquisaTodas = false;
        isCaso = 0;
        macFilial = null;
        strData = DataHoje.data();
        strDataFinal = DataHoje.data();
        strEndereco = "";
        msgRecepcao = "";
        msgConfirma = "";
        statusEmpresa = "";
        data = DataHoje.dataHoje();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        fisica = new Fisica();
        juridica = new Juridica();
        recepcao = new Recepcao();
        registro = new Registro();
        profissao = new Profissao();
        idMotivoDemissao = 0;
        tipoPesquisa = "";
        dataPesquisa = "hoje";
        cancelamento = new Cancelamento();
        id_protocolo = -1;
        numeroProtocolo = "";
        listaRecepcaos = new ArrayList();
        macFilial = (MacFilial) GenericaSessao.getObject("acessoFilial");
        dataInicialString = DataHoje.data();
        dataFinalString = DataHoje.data();
        openDialog = false;
        listaAtendimentoSimples = new ArrayList();
        listaStatus = new ArrayList();
        listaMotivoDemissao = new ArrayList();
        listaHorarios = new ArrayList();
        progressUpdate = 100;
        progressLabel = 10;
        startPooling = true;

        idStatusAtendimento = 0;
        listaStatus = new ArrayList();
        dataPesquisaAtendimento = "hoje";
        listaStatusAtendimento = new ArrayList();
        tipoPesquisaAtendimento = "juridica";

        descricaoFisica = "";
        tipoFisicaPesquisa = "cpf";

        dataInicialAtendimento = DataHoje.data();
        dataFinalAtendimento = DataHoje.data();

        listFiles = new ArrayList();
        configuracaoArrecadacao = ConfiguracaoArrecadacao.get();
        getListaStatusAtendimento();
        loadListHorarios();
        loadListaAtendimentoSimples();
        idHomologador = null;
        motivoAlteracaoHomologador = "";
        loadListHomologadores();
    }

    public void alterTab(TabChangeEvent event) {
        indexTab = ((TabView) event.getComponent()).getActiveIndex();
    }

    public void startStopPolling() {
//        if (startPooling)
//            setStartPooling(false);
//        else
//            setStartPooling(true);
    }

    public void progress() {
        progressUpdate = progressUpdate - 10;
        progressLabel--;
        if (progressUpdate == 0) {
            progressUpdate = 100;
            progressLabel = 10;
            loadListHorarios();
            PF.update("formRecepcao:i_tabview:i_panel_tbl");
        }
    }

    public void fecharModal() {
        agendamentoEdit = new Agendamento();
        openDialog = false;
        recepcao = new Recepcao();
        listFiles.clear();
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public void gerarSenha() {
        Dao di = new Dao();
        di.openTransaction();

        if (registro.isSenhaHomologacao()) {
            if (recepcao.getHoraInicialFuncionario().isEmpty()) {
                GenericaMensagem.warn("Atenção", "FUNCIONÁRIO ainda não esta presente, aguarde sua chegada!");
                di.rollback();
                return;
            }

            if (recepcao.getPreposto().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar o NOME do preposto!");
                di.rollback();
                return;
            }

            if (recepcao.getHoraInicialPreposto().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar o HORÁRIO que o preposto chegou!");
                di.rollback();
                return;
            }

            if (recepcao.getId() == -1) {
                if (!di.save(recepcao)) {
                    GenericaMensagem.error("Erro", "Não foi possível SALVAR Recepção!");
                    di.rollback();
                    return;
                }
            } else if (!di.update(recepcao)) {
                GenericaMensagem.error("Erro", "Não foi possível ATUALIZAR Recepção!");
                di.rollback();
                return;
            }
            agendamentoEdit.setRecepcao(recepcao);
        }

        if (!di.update(agendamentoEdit)) {
            GenericaMensagem.error("Erro", "Não foi possível ATUALIZAR Agendamento!");
            di.rollback();
            return;
        }

        SenhaHomologacao senhaHomologacao = new SenhaHomologacao();
        Collection<ParametroSenha> list = senhaHomologacao.parametros(agendamentoEdit, di);
        if (!list.isEmpty()) {
            GenericaMensagem.info("Sucesso", "Senha Gerada!");
            //senhaHomologacao.imprimir(agendamentoEdit, list);
            senhaHomologacao.imprimir(list);
        } else {
            di.rollback();
            GenericaMensagem.error("Erro", "Não foi possível GERAR SENHA!");
            return;
        }
        di.commit();
        loadListHorarios();
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public void desfazerSenha() {
        Dao di = new Dao();
        di.openTransaction();

        if (!di.update(agendamentoEdit.getRecepcao())) {
            GenericaMensagem.error("Erro", "Não foi possível EXCLUIR Recepção Senha!");
            di.rollback();
            return;
        }

        agendamentoEdit.setRecepcao(null);
        recepcao = new Recepcao();

        if (!di.update(agendamentoEdit)) {
            GenericaMensagem.error("Erro", "Não foi possível EXCLUIR Senha!");
            di.rollback();
            return;
        }

        if (!di.delete(agendamentoEdit.getSenha())) {
            GenericaMensagem.error("Erro", "Não foi possível EXCLUIR Senha!");
            di.rollback();
            return;
        }

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("hom_cancelamento");
        novoLog.setCodigo(cancelamento.getId());
        novoLog.delete(
                "Exclusão da senha (Recepção): "
                + " - ID do cancelamento: " + cancelamento.getId()
                + " - Agendamento {ID: " + cancelamento.getAgendamento().getId() + "} "
                + " - Funcionário { " + agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
        );

        GenericaMensagem.info("Sucesso", "Senha removida");
        di.commit();
        loadListHorarios();
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public void cancelarHorario() {
        if (cancelamento.getMotivo().isEmpty() || cancelamento.getMotivo().length() <= 5) {
            GenericaMensagem.warn("Atenção", "Motivo de Cancelamento inválido");
            return;
        }

        Dao di = new Dao();
        agendamentoEdit.setStatus((Status) di.find(new Status(), 3));
        di.openTransaction();
        if (!di.update(agendamentoEdit)) {
            di.rollback();
            GenericaMensagem.error("Erro", "Erro ao atualizar Agendamento");
            return;
        }
        agendamentoEdit.getPessoaEmpresa().setDtDemissao(null);

        PessoaEmpresaDao db = new PessoaEmpresaDao();
        PessoaEmpresa pem = db.pesquisaPessoaEmpresaPorFisica(agendamentoEdit.getPessoaEmpresa().getFisica().getId());

        if (pem.getId() == -1) {
            agendamentoEdit.getPessoaEmpresa().setPrincipal(true);
        }

        if (!di.update(agendamentoEdit.getPessoaEmpresa())) {
            di.rollback();
            cancelamento = new Cancelamento();
            GenericaMensagem.error("Erro", "Erro ao atualizar Pessoa Empresa");
            return;
        }

        cancelamento.setAgendamento(agendamentoEdit);
        cancelamento.setDtData(DataHoje.dataHoje());
        cancelamento.setUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")));

        if (!di.save(cancelamento)) {
            cancelamento = new Cancelamento();
            di.rollback();
            GenericaMensagem.error("Erro", "Erro ao salvar Cancelamento");
            return;
        }

        GenericaMensagem.info("Sucesso", "Homologação Cancelada!");
        di.commit();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("hom_cancelamento");
        novoLog.setCodigo(cancelamento.getId());
        novoLog.delete(
                "Cancelamento de homologação (Recepção): "
                + " - ID do cancelamento: " + cancelamento.getId()
                + " - Agendamento {ID: " + cancelamento.getAgendamento().getId() + "} "
                + " - Funcionário { " + agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                + " - Data do cancelamento: " + cancelamento.getData()
                + " - Motivo: " + cancelamento.getMotivo());

        //cancelamento = new Cancelamento();
        loadListHorarios();
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public String pesquisarPessoa() {
        if (tipoPesquisa.equals("juridica")) {
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoaJuridica();
        } else {
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoaFisica();
        }
    }

    public String pesquisarPessoaAtendimento() {
        if (tipoPesquisaAtendimento.equals("juridica")) {
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoaJuridica();
        } else {
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoaFisica();
        }
    }

    public String limparPesquisa() {
        juridica = new Juridica();
        fisica = new Fisica();
        isPesquisarPessoaFisicaFiltro = false;
        isPesquisarPessoaJuridicaFiltro = false;
        desabilitaPesquisaProtocolo = false;
        if (indexTab == 0) {
            loadListHorarios();
        } else {
            loadListaAtendimentoSimples();
        }
        return null;
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            List<Status> list = (List<Status>) new Dao().find("Status", new int[]{2, 3, 4, 5, 7, 9});
            if (!list.isEmpty()) {
                int i = 0;
                for (i = 0; i < list.size(); i++) {
                    listaStatus.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
                }
                listaStatus.add(new SelectItem(i++, "Todos", "6"));
            }
        }
        return listaStatus;
    }

    public List<SelectItem> getListaMotivoDemissao() {
        if (listaMotivoDemissao.isEmpty()) {
            Dao di = new Dao();
            List<Demissao> list = (List<Demissao>) di.list(new Demissao(), true);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    listaMotivoDemissao.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
                }
            }
        }
        return listaMotivoDemissao;
    }

    public void salvar() {
        Dao di = new Dao();
        di.openTransaction();
        if (registro.isSenhaHomologacao()) {
            if (!recepcao.getPreposto().isEmpty()) {
                if (recepcao.getHoraInicialPreposto().isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Informar o HORÁRIO que o preposto chegou!");
                    di.rollback();
                    return;
                }
            }

            //if (!recepcao.getHoraInicialFuncionario().isEmpty()) {
            if (recepcao.getId() == -1) {
                if (!di.save(recepcao)) {
                    GenericaMensagem.error("Erro", "Erro ao salvar Recepção!");
                    di.rollback();
                    return;
                }
            } else if (!di.update(recepcao)) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Recepção!");
                di.rollback();
                return;
            }
            agendamentoEdit.setRecepcao(recepcao);
            //}
        }

        if (!di.update(agendamentoEdit)) {
            GenericaMensagem.error("Erro", "Erro ao atualizar Agendamento!");
            di.rollback();
            return;
        }
        if (!di.update(agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa())) {
            GenericaMensagem.error("Erro", "Erro ao atualizar Agendamento!");
            di.rollback();
            return;
        }
        GenericaMensagem.info("Sucesso", "Agendamento atualizado!");
        di.commit();
        loadListHorarios();
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
    }

    public void agendar(DataObject datao) {
        cancelamento = new Cancelamento();
        if (getData() != null) {
            if (DataHoje.converteDataParaInteger(DataHoje.converteData(getData()))
                    < DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                return;
            }
        }
        Dao di = new Dao();
        int idCaso = Integer.parseInt(datao.getArgumento12().toString());
        switch (idCaso) {
            case 2:
            case 3:
            case 4:
            case 7: 
            case 9: {
                agendamentoEdit = (Agendamento) di.find(datao.getArgumento9());
                profissao = ((PessoaEmpresa) datao.getArgumento7()).getFuncao();
                for (int i = 0; i < getListaMotivoDemissao().size(); i++) {
                    if (Integer.parseInt(getListaMotivoDemissao().get(i).getDescription()) == agendamentoEdit.getDemissao().getId()) {
                        idMotivoDemissao = (Integer) getListaMotivoDemissao().get(i).getValue();
                        break;
                    }
                }
                break;
            }
            case 5: {
                agendamentoEdit = (Agendamento) di.find(datao.getArgumento9());
                if (((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")).getId() == agendamentoEdit.getHomologador().getId()) {
                    profissao = ((PessoaEmpresa) datao.getArgumento7()).getFuncao();
                    for (int i = 0; i < getListaMotivoDemissao().size(); i++) {
                        if (Integer.parseInt(getListaMotivoDemissao().get(i).getDescription()) == agendamentoEdit.getDemissao().getId()) {
                            idMotivoDemissao = (Integer) getListaMotivoDemissao().get(i).getValue();
                            break;
                        }
                    }
                    break;
                } else {
                    if(agendamentoEdit.getStatus().getId() == 4) {
                        GenericaMensagem.warn("Alerta", "Pessoa em atendimento!");
                        PF.closeDialog("dlg_recepcao");
                    }
                    agendamentoEdit = new Agendamento();
                    break;
                }
            }
        }

        HomologacaoDao dB = new HomologacaoDao();
        cancelamento = (Cancelamento) dB.pesquisaCancelamentoPorAgendanto(agendamentoEdit.getId());
        if (cancelamento == null) {
            cancelamento = new Cancelamento();
        }

        if (agendamentoEdit.getRecepcao() != null) {
            recepcao = agendamentoEdit.getRecepcao();
        }

        getStrEndereco();
        openDialog = true;
    }

    public List<DataObject> getListaHorarios() {
        return listaHorarios;
    }

    public String voltar() {
        if (!isPesquisarPessoaFisicaFiltro && !isPesquisarPessoaJuridicaFiltro) {
            juridica = new Juridica();
            fisica = new Fisica();
        }
        agendamento = new Agendamento();
        profissao = new Profissao();
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("fisicaPesquisa");
        return "recepcao";
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public int getIdMotivoDemissao() {
        return idMotivoDemissao;
    }

    public void setIdMotivoDemissao(int idMotivoDemissao) {
        this.idMotivoDemissao = idMotivoDemissao;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public String getMsgRecepcao() {
        if (!GenericaSessao.exists("acessoFilial")) {
            msgRecepcao = "Não existe filial definida!";
        } else {
            macFilial = (MacFilial) GenericaSessao.getObject("acessoFilial");
        }
        return msgRecepcao;
    }

    public void setMsgRecepcao(String msgRecepcao) {
        this.msgRecepcao = msgRecepcao;
    }

    public Juridica getJuridica() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            idStatus = 5;
            numeroProtocolo = "";
            isPesquisarPessoaJuridicaFiltro = true;
            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
            GenericaSessao.remove("fisicaPesquisa");
            GenericaSessao.remove("juridicaPesquisa");
            dataPesquisaTodas = true;
            if (indexTab == 0) {
                loadListHorarios();
            } else {
                loadListaAtendimentoSimples();
            }
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public String getStatusEmpresa() {
        List lista = new ArrayList();
        if (juridica.getId() != -1) {
            HomologacaoDao db = new HomologacaoDao();
            lista = db.pesquisaPessoaDebito(juridica.getPessoa().getId(), DataHoje.data());
        }
        if (!lista.isEmpty()) {
            statusEmpresa = "EM DÉBITO";
        } else {
            statusEmpresa = "REGULAR";
        }
        return statusEmpresa;
    }

    public void setStatusEmpresa(String statusEmpresa) {
        this.statusEmpresa = statusEmpresa;
    }

    public String getStrEndereco() {
        if (agendamentoEdit.getPessoaEmpresa().getJuridica().getId() != -1) {
            PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
            PessoaEndereco enderecoEmpresa = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(agendamentoEdit.getPessoaEmpresa().getJuridica().getPessoa().getId(), 2);
            if (enderecoEmpresa.getId() != -1) {
                String strCompl;
                try {
                    if (enderecoEmpresa.getComplemento().equals("")) {
                        strCompl = " ";
                    } else {
                        strCompl = " ( " + enderecoEmpresa.getComplemento() + " ) ";
                    }
                } catch (Exception e) {
                    strCompl = "";
                }

                strEndereco = enderecoEmpresa.getEndereco().getLogradouro().getDescricao() + " "
                        + enderecoEmpresa.getEndereco().getDescricaoEndereco().getDescricao() + ", " + enderecoEmpresa.getNumero() + " " + enderecoEmpresa.getEndereco().getBairro().getDescricao() + ","
                        + strCompl + enderecoEmpresa.getEndereco().getCidade().getCidade() + " - " + enderecoEmpresa.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(enderecoEmpresa.getEndereco().getCep());
            } else {
                strEndereco = "";
            }
        } else {
            strEndereco = "";
        }
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            isPesquisarPessoaFisicaFiltro = true;
            numeroProtocolo = "";
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            GenericaSessao.remove("juridicaPesquisa");
            dataPesquisaTodas = true;
            idStatus = 5;
            if (indexTab == 0) {
                loadListHorarios();
            } else {
                loadListaAtendimentoSimples();
            }
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

//    public Recepcao getRecepcao() {
//        return recepcao;
//    }
//
//    public void setRecepcao(Recepcao recepcao) {
//        this.recepcao = recepcao;
//    }
    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public Cancelamento getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(Cancelamento cancelamento) {
        this.cancelamento = cancelamento;
    }

    public Registro getRegistro() {
        if (registro == null) {
            registro = new Registro();
        }
        if (registro.getId() == -1) {
            Dao di = new Dao();
            registro = (Registro) di.find(new Registro(), 1);
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public boolean isOcultaData() {
        return ocultaData;
    }

    public void setOcultaData(boolean ocultaData) {
        this.ocultaData = ocultaData;
    }

    public boolean isOcultaStatus() {
        return ocultaStatus;
    }

    public void setOcultaStatus(boolean ocultaStatus) {
        this.ocultaStatus = ocultaStatus;
    }

    public int getId_protocolo() {
        return id_protocolo;
    }

    public void setId_protocolo(int id_protocolo) {
        this.id_protocolo = id_protocolo;
    }

    public String getDataPesquisa() {
        if (dataPesquisa.equals("hoje")) {
            if (Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription()) != 6) {
                data = DataHoje.dataHoje();
            }
            ocultaDatapesquisa = false;
        } else {
            ocultaDatapesquisa = true;
        }
        return dataPesquisa;
    }

    public void setDataPesquisa(String dataPesquisa) {
        this.dataPesquisa = dataPesquisa;
    }

    public String getStrDataFinal() {
        return strDataFinal;
    }

    public void setStrDataFinal(String strDataFinal) {
        this.strDataFinal = strDataFinal;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataInicialString() {
        return dataInicialString;
    }

    public void setDataInicialString(String dataInicialString) {
        this.dataInicialString = dataInicialString;
        this.dataInicial = DataHoje.converte(dataInicialString);
    }

    public String getDataFinalString() {
        return dataFinalString;
    }

    public void setDataFinalString(String dataFinalString) {
        this.dataFinalString = dataFinalString;
        this.dataFinal = DataHoje.converte(dataFinalString);
    }

    public void setData(String dataFinal) {
        this.dataFinal = DataHoje.converte(dataFinal);
    }

    public boolean isOcultaDatapesquisa() {
        return ocultaDatapesquisa;
    }

    public void setOcultaDatapesquisa(boolean ocultaDatapesquisa) {
        this.ocultaDatapesquisa = ocultaDatapesquisa;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public int getIsCaso() {
        return isCaso;
    }

    public void setIsCaso(int isCaso) {
        this.isCaso = isCaso;
    }

    public boolean isOcultaPreposto() {
        return ocultaPreposto;
    }

    public void setOcultaPreposto(boolean ocultaPreposto) {
        this.ocultaPreposto = ocultaPreposto;
    }

    public boolean isOcultaHomologador() {
        return ocultaHomologador;
    }

    public void setOcultaHomologador(boolean ocultaHomologador) {
        this.ocultaHomologador = ocultaHomologador;
    }

    public boolean isOcultaColunaEmpresa() {
        return ocultaColunaEmpresa;
    }

    public void setOcultaColunaEmpresa(boolean ocultaColunaEmpresa) {
        this.ocultaColunaEmpresa = ocultaColunaEmpresa;
    }

    public boolean isOcultaColunaPessoaFisica() {
        return ocultaColunaPessoaFisica;
    }

    public void setOcultaColunaPessoaFisica(boolean ocultaColunaPessoaFisica) {
        this.ocultaColunaPessoaFisica = ocultaColunaPessoaFisica;
    }

    public boolean isOcultaSenha() {
        return ocultaSenha;
    }

    public void setOcultaSenha(boolean ocultaSenha) {
        this.ocultaSenha = ocultaSenha;
    }

    public boolean isIsPesquisarPessoaFisicaFiltro() {
        return isPesquisarPessoaFisicaFiltro;
    }

    public void setIsPesquisarPessoaFisicaFiltro(boolean isPesquisarPessoaFisicaFiltro) {
        this.isPesquisarPessoaFisicaFiltro = isPesquisarPessoaFisicaFiltro;
    }

    public boolean isIsPesquisarPessoaJuridicaFiltro() {
        return isPesquisarPessoaJuridicaFiltro;
    }

    public void setIsPesquisarPessoaJuridicaFiltro(boolean isPesquisarPessoaJuridicaFiltro) {
        this.isPesquisarPessoaJuridicaFiltro = isPesquisarPessoaJuridicaFiltro;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public boolean isDesabilitaAtualizacaoAutomatica() {
        return desabilitaAtualizacaoAutomatica;
    }

    public void setDesabilitaAtualizacaoAutomatica(boolean desabilitaAtualizacaoAutomatica) {
        this.desabilitaAtualizacaoAutomatica = desabilitaAtualizacaoAutomatica;
    }

    public void limparPesquisaProtocolo() {
        numeroProtocolo = "";
        //listaRecepcaos.clear();
        //getListaRecepcaos();
    }

    public boolean isDesabilitaPesquisaProtocolo() {
        return desabilitaPesquisaProtocolo;
    }

    public void setDesabilitaPesquisaProtocolo(boolean desabilitaPesquisaProtocolo) {
        this.desabilitaPesquisaProtocolo = desabilitaPesquisaProtocolo;
    }

    public boolean isDataPesquisaTodas() {
        return dataPesquisaTodas;
    }

    public void setDataPesquisaTodas(boolean dataPesquisaTodas) {
        this.dataPesquisaTodas = dataPesquisaTodas;
    }

    public void setListaRecepcaos(List<ListaAgendamento> listaRecepcaos) {
        this.listaRecepcaos = listaRecepcaos;
    }

    public void selecionaDataInicial(SelectEvent selectEvent) {
        ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        setDataInicial(event.getStartDate());
    }

    public void selecionaDataFinal(SelectEvent selectEvent) {
        ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        setDataFinal(event.getStartDate());
    }

    public void loadListHorarios() {
        listaHorarios = new ArrayList();

        if (macFilial == null) {
            return;
        }
        if (isPesquisarPessoaFisicaFiltro == true) {
            juridica = new Juridica();
        }
        if (isPesquisarPessoaJuridicaFiltro == true) {
            fisica = new Fisica();
        }

        if (dataPesquisa.equals("hoje")) {
            dataInicial = DataHoje.dataHoje();
            dataInicialString = DataHoje.data();
        }

        if (juridica.getId() != -1 || fisica.getId() != -1) {
            desabilitaPesquisaProtocolo = true;
            numeroProtocolo = "";
        } else {
            desabilitaPesquisaProtocolo = false;
        }

        HomologacaoDao db = new HomologacaoDao();
        List<Agendamento> ag;
        setData(DataHoje.dataHoje());
        String agendador;
        String homologador;
        String contabilidade;
        ocultaData = false;
        ocultaHomologador = false;
        ocultaPreposto = false;
        ocultaStatus = false;
        ocultaColunaPessoaFisica = false;
        ocultaColunaEmpresa = false;
        ocultaSenha = false;
        int idCaso = Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription());
        int idCasoStatus;
        Date dataInicialA;
        Date dataFinalA;
        desabilitaAtualizacaoAutomatica = false;
        if (!numeroProtocolo.equals("")) {
            if (Integer.parseInt(numeroProtocolo) > 0) {
                ag = db.pesquisaAgendamentoPorProtocolo(Integer.parseInt(numeroProtocolo));
                if (!ag.isEmpty()) {
                    desabilitaAtualizacaoAutomatica = true;
                }
            } else {
                ag = new ArrayList();
            }
        } else {
            ag = new ArrayList();
        }
        if (idCaso > 0 && ag.isEmpty()) {
            if (idCaso == 6) {
                idCasoStatus = 0;
                ocultaStatus = false;
                if (dataPesquisa.equals("hoje")) {
                    ocultaData = true;
                    dataInicial = DataHoje.dataHoje();
                    dataFinal = DataHoje.dataHoje();
                    strData = getDataInicialString();
                    if (fisica.getId() != -1 || juridica.getId() != -1) {
                        dataInicialA = dataInicial;
                        dataFinalA = null;
                    } else {
                        dataInicialA = dataInicial;
                        dataFinalA = null;
                    }
                } else if (dataPesquisa.equals("periodo") && !tipoPesquisa.equals("todos") && juridica.getId() == -1 && fisica.getId() == -1) {
                    ocultaData = false;
                    dataInicialA = dataInicial;
                    dataFinalA = dataFinal;
                } else {
                    ocultaData = false;
                    if (fisica.getId() != -1 || juridica.getId() != -1) {
                        dataInicialA = dataInicial;
                        dataFinalA = dataFinal;
                    } else {
                        dataInicialA = dataInicial;
                        dataFinalA = null;
                    }
                }
            } else {
                idCasoStatus = idCaso;
                ocultaStatus = true;
                if (dataPesquisa.equals("hoje")) {
                    ocultaData = true;
                    dataInicial = DataHoje.dataHoje();
                    dataFinal = DataHoje.dataHoje();
                    strData = getDataInicialString();
                    dataInicialA = dataInicial;
                    dataFinalA = null;
                } else {
                    ocultaData = false;
                    dataInicialA = dataInicial;
                    dataFinalA = dataFinal;
                }
            }

            if (dataPesquisaTodas) {
                if (juridica.getId() != -1 || fisica.getId() != -1) {
                    dataInicialA = null;
                    dataFinalA = null;
                }
            }

            if (numeroProtocolo.equals("")) {
                ag = db.pesquisaAgendamento(idCasoStatus, macFilial.getFilial().getId(), dataInicialA, dataFinalA, 0, fisica.getId(), juridica.getId(), false, false);
            } else {
                ag = new ArrayList();
            }

            if (idCaso == 7) {
                this.ocultaPreposto = true;
                this.ocultaHomologador = true;
                this.ocultaSenha = true;
            }

            if (juridica.getId() != -1) {
                ocultaColunaEmpresa = true;
            }

            if (fisica.getId() != -1) {
                ocultaColunaPessoaFisica = true;
            }

        }

        for (int i = 0; i < ag.size(); i++) {
            if (ag.get(i).getAgendador() != null) {
                agendador = ag.get(i).getAgendador().getPessoa().getNome();
            } else {
                agendador = "** Web User **";
            }
            if (ag.get(i).getHomologador() != null) {
                homologador = ag.get(i).getHomologador().getPessoa().getNome();
            } else {
                homologador = "";
            }
            if (ag.get(i).getPessoaEmpresa().getJuridica().getContabilidade() != null) {
                contabilidade = ag.get(i).getPessoaEmpresa().getJuridica().getContabilidade().getPessoa().getNome();
            } else {
                contabilidade = "";
            }
            Senha senha = db.pesquisaSenhaAgendamento(ag.get(i).getId());
            String senhaId = "";
            String senhaString = "";
            String oposicaoString = "";
            boolean isOposicao = false;
            AtendimentoDao dbat = new AtendimentoDao();
            if (dbat.pessoaOposicao(ag.get(i).getPessoaEmpresa().getFisica().getPessoa().getDocumento(), configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao())) {
                isOposicao = true;
                oposicaoString = "tblAgendamentoOposicaox";
            }

            if (senha != null && senha.getId() != -1) {
                senhaString = ((Integer) senha.getSenha()).toString();
                senhaString = (senhaString.length() == 1) ? "0" + senhaString : senhaString;
            }

            Cancelamento can = (Cancelamento) db.pesquisaCancelamentoPorAgendanto(ag.get(i).getId());
            if (senha != null && senha.getId() != -1 && can == null) {
                senhaId = "tblListaRecepcaox";
            }
            if (!isOposicao) {
            } else {

            }

            listaHorarios.add(new DataObject(
                    ag.get(i).getHorarios(), // ARG 0 HORA
                    ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getDocumento(), // ARG 1 CNPJ
                    ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getNome(), //ARG 2 NOME
                    homologador, //ARG 3 HOMOLOGADOR
                    ag.get(i).getContato(), // ARG 4 CONTATO
                    ag.get(i).getTelefone(), // ARG 5 TELEFONE
                    agendador, // ARG 6 USUARIO
                    ag.get(i).getPessoaEmpresa(), // ARG 7 PESSOA EMPRESA
                    senhaId, // senha.getId() == -1 ? "" : "tblListaRecepcao", // ARG 8 SE TIVER SENHA COR VERDE
                    ag.get(i), // ARG 9 AGENDAMENTO
                    senhaString, // senha.getId() == -1 ? null : senha.getSenha(), // ARG 10 SENHA PARA ATENDIMENTO
                    ag.get(i).getData(), // ARG 11 DATA DO AGENDAMENTO
                    ag.get(i).getStatus().getId(), // ARG 12 STATUS ID
                    ag.get(i).getStatus().getDescricao(), // ARG 13 STATUS DESCRIÇÃO
                    contabilidade, //ARG 14 CONTABILIDADE
                    oposicaoString)
            );
        }
    }

    public void loadListaAtendimentoSimples() {
        listaAtendimentoSimples.clear();
        if (macFilial != null) {
            HomologacaoDao db = new HomologacaoDao();
            SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();

            listaAtendimentoSimples = db.listaAtendimentoIniciadoSimplesPesquisa(
                    macFilial.getFilial().getId(),
                    su.getSessaoUsuario().getId(),
                    Integer.valueOf(listaStatusAtendimento.get(idStatusAtendimento).getDescription()),
                    dataPesquisaAtendimento,
                    dataInicialAtendimento,
                    dataFinalAtendimento,
                    juridica.getId(),
                    descricaoFisica,
                    (tipoPesquisaAtendimento.equals("fisica")) ? tipoFisicaPesquisa : ""
            );
        }
    }

    public void clearDescricaoFisica() {
        descricaoFisica = "";
        loadListaAtendimentoSimples();
    }

    public void cleanTipoFisica() {
        getTipoFisicaPesquisa();
        loadListaAtendimentoSimples();
    }

    public boolean renderedInput(String tipo) {
        return (tipoFisicaPesquisa.equals(tipo));
    }

    public String estiloLinha(AteMovimento atm) {
        if (atm.getStatus() != null) {
            if (atm.getStatus().getId() == 1) {
                return "tblListaRecepcaox";
            } else {
                return "";
            }
        }
        return "";
    }

    public List<Senha> getListaAtendimentoSimples() {
        return listaAtendimentoSimples;
    }

    public void setListaAtendimentoSimples(List<Senha> listaAtendimentoSimples) {
        this.listaAtendimentoSimples = listaAtendimentoSimples;
    }

    public Agendamento getAgendamentoEdit() {
        return agendamentoEdit;
    }

    public void setAgendamentoEdit(Agendamento agendamentoEdit) {
        this.agendamentoEdit = agendamentoEdit;
    }

    public boolean isOpenDialog() {
        return openDialog;
    }

    public void setOpenDialog(boolean openDialog) {
        this.openDialog = openDialog;
    }

    public Recepcao getRecepcao() {
        return recepcao;
    }

    public void setRecepcao(Recepcao recepcao) {
        this.recepcao = recepcao;
    }

    public int getProgressUpdate() {
        return progressUpdate;
    }

    public void setProgressUpdate(int progressUpdate) {
        this.progressUpdate = progressUpdate;
    }

    public int getProgressLabel() {
        return progressLabel;
    }

    public void setProgressLabel(int progressLabel) {
        this.progressLabel = progressLabel;
    }

    public boolean isStartPooling() {
        return startPooling;
    }

    public void setStartPooling(boolean startPooling) {
        this.startPooling = startPooling;
    }

    public int getIdStatusAtendimento() {
        return idStatusAtendimento;
    }

    public void setIdStatusAtendimento(int idStatusAtendimento) {
        this.idStatusAtendimento = idStatusAtendimento;
    }

    public List<SelectItem> getListaStatusAtendimento() {
        if (listaStatusAtendimento.isEmpty()) {
            List<AteStatus> result = new Dao().list("AteStatus");
            listaStatusAtendimento.add(new SelectItem(0, "Todos", "0"));
            for (int i = 0; i < result.size(); i++) {
                listaStatusAtendimento.add(new SelectItem(i + 1, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())));
            }
        }
        return listaStatusAtendimento;
    }

    public void setListaStatusAtendimento(List<SelectItem> listaStatusAtendimento) {
        this.listaStatusAtendimento = listaStatusAtendimento;
    }

    public String getDataPesquisaAtendimento() {
        return dataPesquisaAtendimento;
    }

    public void setDataPesquisaAtendimento(String dataPesquisaAtendimento) {
        this.dataPesquisaAtendimento = dataPesquisaAtendimento;
    }

    public String getDataInicialAtendimento() {
        return dataInicialAtendimento;
    }

    public void setDataInicialAtendimento(String dataInicialAtendimento) {
        this.dataInicialAtendimento = dataInicialAtendimento;
    }

    public String getDataFinalAtendimento() {
        return dataFinalAtendimento;
    }

    public void setDataFinalAtendimento(String dataFinalAtendimento) {
        this.dataFinalAtendimento = dataFinalAtendimento;
    }

    public int getIndexTab() {
        return indexTab;
    }

    public void setIndexTab(int indexTab) {
        this.indexTab = indexTab;
    }

    public String getDescricaoFisica() {
        return descricaoFisica;
    }

    public void setDescricaoFisica(String descricaoFisica) {
        this.descricaoFisica = descricaoFisica;
    }

    public String getTipoPesquisaAtendimento() {
        return tipoPesquisaAtendimento;
    }

    public void setTipoPesquisaAtendimento(String tipoPesquisaAtendimento) {
        this.tipoPesquisaAtendimento = tipoPesquisaAtendimento;
    }

    public String getTipoFisicaPesquisa() {
        return tipoFisicaPesquisa;
    }

    public void setTipoFisicaPesquisa(String tipoFisicaPesquisa) {
        this.tipoFisicaPesquisa = tipoFisicaPesquisa;
    }

    // ARQUIVOS
    public List getListFiles() {
        listFiles.clear();
        if (agendamentoEdit.getId() != -1) {
            listFiles = Diretorio.listaArquivos("Arquivos/homologacao/" + agendamentoEdit.getId());
        }
        return listFiles;
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setDiretorio("Arquivos/homologacao/" + agendamentoEdit.getId());
        configuracaoUpload.setEvent(event);
        if (Upload.enviar(configuracaoUpload, true)) {
            listFiles.clear();
        }
        getListFiles();
    }

    public void deleteFiles(int index) {
        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/" + agendamentoEdit.getId() + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        File fl = new File(caminho);
        fl.delete();
        listFiles.remove(index);
        listFiles.clear();
        getListFiles();
        PF.update("form_recepcao_upload:id_grid_uploads");
        PF.update("formRecepcao:id_btn_anexo");
    }

    public Boolean getExisteOposicao() {
        if (agendamentoEdit.getId() != -1) {
            OposicaoDao oposicaoDao = new OposicaoDao();
            return oposicaoDao.existPessoaOposicao(agendamentoEdit.getPessoaEmpresa().getFisica().getPessoa().getDocumento(), configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao());
        }
        return false;
    }

    public void updateHomologador() {
        if (motivoAlteracaoHomologador.isEmpty() || motivoAlteracaoHomologador.length() < 10) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MOTIVO VÁLIDO, COM MAIS DE 10 CARACTERES!");
            return;
        }
        Dao dao = new Dao();
        agendamentoEdit.setHomologador((Usuario) dao.find(new Usuario(), idHomologador));
        Agendamento a = agendamentoEdit;
        if (dao.update(agendamentoEdit, true)) {
            motivoAlteracaoHomologador = "";
            loadListHomologadores();
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("hom_agendamento");
            novoLog.setCodigo(agendamentoEdit.getId());
            novoLog.update("Agendamento (Protocolo): " + a.getId() + " - Homologador: " + a.getHomologador().getId(), "Agendamento (Protocolo): " + agendamentoEdit.getId() + " - Homologador: " + agendamentoEdit.getHomologador().getId() + " - Motivo da alteração: " + motivoAlteracaoHomologador);
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
        } else {
            GenericaMensagem.warn("Erro", "AO ATUALIZAR!");

        }
    }

    public void loadListHomologadores() {
        listHomologadores = new ArrayList();
        HomologacaoDao homologacaoDao = new HomologacaoDao();
        List<Usuario> list = homologacaoDao.findAllHomologadores();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idHomologador = list.get(i).getId();
            }
            listHomologadores.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
            SelectItemSort.sort(listHomologadores);
        }

    }

    public List<SelectItem> getListHomologadores() {
        return listHomologadores;
    }

    public void setListHomologadores(List<SelectItem> listHomologadores) {
        this.listHomologadores = listHomologadores;
    }

    public Integer getIdHomologador() {
        return idHomologador;
    }

    public void setIdHomologador(Integer idHomologador) {
        this.idHomologador = idHomologador;
    }

    public String getMotivoAlteracaoHomologador() {
        return motivoAlteracaoHomologador;
    }

    public void setMotivoAlteracaoHomologador(String motivoAlteracaoHomologador) {
        this.motivoAlteracaoHomologador = motivoAlteracaoHomologador;
    }
}
