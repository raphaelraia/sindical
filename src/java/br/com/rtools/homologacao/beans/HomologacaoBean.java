package br.com.rtools.homologacao.beans;

import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.atendimento.AteMovimento;
import br.com.rtools.atendimento.AteStatus;
import br.com.rtools.atendimento.dao.AtendimentoDao;
import br.com.rtools.pessoa.beans.PesquisarProfissaoBean;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.ListaAgendamento;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.Status;
import br.com.rtools.homologacao.dao.CancelamentoDao;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class HomologacaoBean extends PesquisarProfissaoBean implements Serializable {

    private String msgConfirma = "";
    private String strEndereco = "";
    private String tipoAviso = "true";
    private String strData = DataHoje.data();
    private String statusEmpresa = "REGULAR";
    private boolean renderHomologar = false;
    private boolean renderCancelarHorario = false;
    private Date data = DataHoje.dataHoje();
    private int idStatus = 0;
    private int idMotivoDemissao = 0;
    private int idIndex = -1;
    private List<ListaAgendamento> listaHomologacoes = new ArrayList();
    private final List<SelectItem> listaStatus = new ArrayList();
    private final List<SelectItem> listaDemissao = new ArrayList();
    private Agendamento agendamento = new Agendamento();
    private Juridica juridica = new Juridica();
    private PessoaEndereco enderecoEmpresa = new PessoaEndereco();
    private Fisica fisica = new Fisica();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
    private MacFilial macFilial = null;
    private Registro registro = new Registro();
    private Cancelamento cancelamento = new Cancelamento();
    private Senha senhaAtendimento = new Senha();
    private List<Senha> listaAtendimentoSimples = new ArrayList();
    private List listFiles = new ArrayList();

    private boolean visibleModal = false;
    private String tipoTelefone = "telefone";
    private Boolean manutencao;
    private Integer idStatusManutencao;

    public HomologacaoBean() {
        manutencao = false;
        macFilial = (MacFilial) GenericaSessao.getObject("acessoFilial");
        registro = (Registro) new Dao().find(new Registro(), 1);
        idStatusManutencao = 0;
        if (macFilial != null) {
            this.loadListaHomologacao();
            this.loadListaAtendimentoSimples();
        }
    }

    public void alterarTipoMascara() {
        if (tipoTelefone.equals("telefone")) {
            tipoTelefone = "celular";
        } else {
            tipoTelefone = "telefone";
        }
        agendamento.setTelefone("");
    }

    public String retornaOposicaoPessoa(String documento) {
        AtendimentoDao atendimentoDB = new AtendimentoDao();
        if (atendimentoDB.pessoaOposicao(documento)) {
            return "tblOposicaox";
        } else {
            return "";
        }
    }

    public final void loadListaHomologacao() {
        listaHomologacoes.clear();
        try {
            Polling polling = new Polling();
            polling.existeUsuarioSessao();
        } catch (IOException e) {
            return;
        }

        if (macFilial == null) {
            return;
        }

        if (DataHoje.converteDataParaInteger(DataHoje.converteData(data)) > DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
            return;
        }

        HomologacaoDao db = new HomologacaoDao();
        Usuario us = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");
        int idUsuario;
        int idCaso = Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription());
        if (idCaso <= 0) {
            return;
        }
        if (idCaso == 2 || idCaso == 7) {
            idUsuario = 0;
        } else {
            idUsuario = us.getId();
        }
        if (manutencao) {
            idUsuario = -1;
        }
        List<Agendamento> agendamentos = db.pesquisaAgendamento(idCaso, macFilial.getFilial().getId(), data, null, idUsuario, 0, 0, false, false);

        for (int i = 0; i < agendamentos.size(); i++) {
            ListaAgendamento listaAgendamento = new ListaAgendamento();
            listaAgendamento.setAgendamento(agendamentos.get(i));
            if (registro.isSenhaHomologacao()) {
                Senha senha = db.pesquisaSenhaAgendamento(agendamentos.get(i).getId());
                if (DataHoje.converteDataParaInteger(DataHoje.converteData(agendamentos.get(i).getDtData())) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                    if (agendamentos.get(i).getStatus().getId() == 2) {
                        listaAgendamento.setHabilitaAlteracao(true);
                    } else {
                        listaAgendamento.setHabilitaAlteracao(false);
                    }
                }
                if (senha.getId() == -1) {
                    if (agendamentos.get(i).getStatus().getId() != 7 && agendamentos.get(i).getStatus().getId() != 3 && agendamentos.get(i).getStatus().getId() != 4 && agendamentos.get(i).getStatus().getId() != 5) {
                        continue;
                    }
                } else {
                    listaAgendamento.setSenha(senha);
                }
            } else if (DataHoje.converteDataParaInteger(DataHoje.converteData(agendamentos.get(i).getDtData())) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                listaAgendamento.setHabilitaAlteracao(false);
            }
            if (DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje())) > DataHoje.converteDataParaInteger(DataHoje.converteData(agendamentos.get(i).getDtData()))) {
                listaAgendamento.setHabilitaAlteracao(false);
            }

            AtendimentoDao dbat = new AtendimentoDao();
            if (dbat.pessoaOposicao(agendamentos.get(i).getPessoaEmpresa().getFisica().getPessoa().getDocumento())) {
                listaAgendamento.setTblEstilo("tblAgendamentoOposicaox");
            }

            if (agendamentos.get(i).getAgendador() == null) {
                listaAgendamento.setUsuarioAgendador("** Web User **");
            } else {
                listaAgendamento.setUsuarioAgendador(agendamentos.get(i).getAgendador().getPessoa().getNome());
            }
            listaHomologacoes.add(listaAgendamento);
        }
    }

    public final void loadListaAtendimentoSimples() {
        listaAtendimentoSimples.clear();

        if (macFilial != null) {
            HomologacaoDao db = new HomologacaoDao();
            SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();

            listaAtendimentoSimples = db.listaAtendimentoIniciadoSimples(macFilial.getFilial().getId(), su.getSessaoUsuario().getId());
        }
    }

    public void reservarAtendimento(AteMovimento amov) {
        Dao dao = new Dao();
        SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();

        dao.openTransaction();
        if (amov.getReserva() == null) {
            amov.setReserva(su.getSessaoUsuario());
            if (!dao.update(amov)) {
                GenericaMensagem.error("Erro", "Não foi possível salvar reserva!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("OK", "Atendimento Reservado!");
        } else if (su.getSessaoUsuario().getId() == amov.getReserva().getId()) {
            amov.setReserva(null);
            if (!dao.update(amov)) {
                GenericaMensagem.error("Erro", "Não foi possível salvar reserva!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("OK", "Reserva desfeita!");
        } else {
            GenericaMensagem.warn("Atenção", "Atendimento não pode ser desfeito!");
            dao.rollback();
            return;
        }
        dao.commit();
        PF.openDialog("dlg_reserva_atendimento");
    }

    public String excluirSenha() {
        HomologacaoDao homologacaoDB = new HomologacaoDao();
        Senha senha = homologacaoDB.pesquisaSenhaAgendamento(agendamento.getId());
        if (senha.getId() == -1) {
            GenericaMensagem.warn("Atenção", "Não existe senha para ser excluida!");
            return null;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.delete(senha)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Erro ao excluir senha!");
            return null;
        }
        agendamento.setStatus((Status) dao.find(new Status(), 2));
        agendamento.setHomologador(null);
        if (!dao.update(agendamento)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível atualizar Agendamento!");
            return null;
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "Senha Excluída !");
        strEndereco = "";
        visibleModal = false;
        tipoAviso = "true";
        fisica = new Fisica();
        agendamento = new Agendamento();
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        loadListaHomologacao();
        return null;
    }

    public void atualizarSenhaSimples(String tipo) {
        Dao dao = new Dao();
        dao.openTransaction();
        Senha senha = (Senha) dao.find(new Senha(), senhaAtendimento.getId());
        if (tipo.equals("atendido")) {
            senha.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 2));
        } else {
            senha.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 3));
        }
        senha.setDtVerificada(new Date());

        if (dao.update(senha)) {
            dao.commit();
        } else {
            dao.rollback();
        }

        senhaAtendimento = new Senha();
        loadListaAtendimentoSimples();
        PF.closeDialog("dlg_atendimento_simples");
        WSSocket.send(getWebSocketSenha(), "update");
    }

    public void excluirSenhaAtendimento() {
        Dao dao = new Dao();

        dao.openTransaction();
        Senha senha = (Senha) dao.find(new Senha(), senhaAtendimento.getId());
        AteMovimento at = (AteMovimento) dao.find(new AteMovimento(), senhaAtendimento.getAteMovimento().getId());

        // DELETA A SENHA
        if (!dao.delete(senha)) {
            dao.rollback();
            return;
        }

        // DELETA TAMBEM O ATENDIMENTO
        if (!dao.delete(at)) {
            dao.rollback();
            return;
        }

        dao.commit();
        senhaAtendimento = new Senha();

        loadListaAtendimentoSimples();
        PF.closeDialog("dlg_atendimento_simples");
    }

    public List<Senha> getListaAtendimentoSimples() {
        return listaAtendimentoSimples;
    }

    public void novaChamadaSenha() {
        HomologacaoDao hdbt = new HomologacaoDao();
        SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();
        Senha senha = hdbt.pesquisaAtendimentoIniciado(su.getSessaoUsuario().getId(), macFilial.getMesa(), macFilial.getFilial().getId(), macFilial.getDepartamento().getId());
        if (senha.getId() == -1) {
            senha = hdbt.pesquisaAtendimentoSimplesIniciado(su.getSessaoUsuario().getId(), macFilial.getMesa(), macFilial.getFilial().getId(), macFilial.getDepartamento().getId());
            if (senha.getId() == -1) {
                senha = hdbt.pesquisaAtendimentoIniciadoSimples(macFilial.getFilial().getId());
            }
        }
        if (senha != null && senha.getId() != -1) {
            Dao dao = new Dao();
            senha.setDtVerificada(new Date());
            senha.setDtNovaChamada(new Date());
            if (senha.getHoraChamada() == null) {
                senha.setHoraChamada(DataHoje.horaMinuto());
            }
            if (senha.getMesa() == 0) {
                senha.setMesa(macFilial.getMesa());
            }
            if (!dao.update(senha, true)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                return;
            }
        } else {
//            List<Senha> listax = hdbt.listaAtendimentoIniciadoSimplesUsuario(macFilial.getFilial().getId(), su.getSessaoUsuario().getId());
//            if (!listax.isEmpty()) {
//                return;
//            }
        }
        WSSocket.send(getWebSocketSenha(), "recall");
    }

    public String retornaSequenciaSenha() {
        HomologacaoDao dbh = new HomologacaoDao();
        SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();
        Dao dao = new Dao();

        // SENHA COM HOMOLOGAÇÃO INICIADA -----
        Senha senha = dbh.pesquisaAtendimentoIniciado(su.getSessaoUsuario().getId(), macFilial.getMesa(), macFilial.getFilial().getId(), macFilial.getDepartamento().getId());
        if (senha.getId() != -1) {
            agendamento = senha.getAgendamento();
            if (agendamento.getTelefone().length() > 14) {
                tipoTelefone = "celular";
            } else {
                tipoTelefone = "telefone";
            }
            fisica = senha.getAgendamento().getPessoaEmpresa().getFisica();
            juridica = senha.getAgendamento().getPessoaEmpresa().getJuridica();
            pessoaEmpresa = senha.getAgendamento().getPessoaEmpresa();
            profissao = senha.getAgendamento().getPessoaEmpresa().getFuncao();
            renderHomologar = true;
            visibleModal = true;
            renderHomologar = true;
            renderCancelarHorario = true;

            GenericaSessao.put("juridicaPesquisa", juridica);

            for (int i = 0; i < getListaDemissao().size(); i++) {
                if (Integer.parseInt(listaDemissao.get(i).getDescription()) == agendamento.getDemissao().getId()) {
                    idMotivoDemissao = (Integer) listaDemissao.get(i).getValue();
                    break;
                }
            }

            tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
            agendamento.setStatus((Status) dao.find(new Status(), 5));
            agendamento.setHomologador(su.getSessaoUsuario());

            PF.update("formConcluirHomologacao");
            PF.openDialog("dlg_homologacao");
            WSSocket.send(getWebSocketSenha(), "call");
            return null;
        }

        List<Senha> listax = dbh.listaAtendimentoIniciadoSimplesUsuario(macFilial.getFilial().getId(), su.getSessaoUsuario().getId(), macFilial.getDepartamento().getId());

        if (!listax.isEmpty()) {
            senhaAtendimento = listax.get(0);
            senhaAtendimento.setDtVerificada(new Date());
            senhaAtendimento.setDtNovaChamada(new Date());
            if (!dao.update(senhaAtendimento, true)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                return null;
            }
            WSSocket.send(getWebSocketSenha(), "call");
            PF.update("form_cancelar_data_table:tbl_at");
            PF.openDialog("dlg_atendimento_simples");
            return null;
        }
        List<Senha> listaSenha = dbh.listaSequenciaSenha(macFilial.getFilial().getId(), macFilial.getDepartamento().getId());
        if (listaSenha.isEmpty()) {
            WSSocket.send(getWebSocketSenha(), "call");
            return null;
        }
        for (Senha senhax : listaSenha) {
            // SENHA DE HOMOLOGAÇÃO --------------------------------------------------------------------------------------------------------------------------
            if (senhax.getAgendamento() != null) {
                if (senhax.getAgendamento().getStatus().getId() != 2) {
                    continue;
                }

                agendamento = senhax.getAgendamento();
                if (agendamento.getTelefone().length() > 14) {
                    tipoTelefone = "celular";
                } else {
                    tipoTelefone = "telefone";
                }
                fisica = senhax.getAgendamento().getPessoaEmpresa().getFisica();
                juridica = senhax.getAgendamento().getPessoaEmpresa().getJuridica();
                pessoaEmpresa = senhax.getAgendamento().getPessoaEmpresa();
                profissao = senhax.getAgendamento().getPessoaEmpresa().getFuncao();
                renderHomologar = true;
                visibleModal = true;
                renderHomologar = true;
                renderCancelarHorario = true;

                GenericaSessao.put("juridicaPesquisa", juridica);

                for (int i = 0; i < getListaDemissao().size(); i++) {
                    if (Integer.parseInt(listaDemissao.get(i).getDescription()) == agendamento.getDemissao().getId()) {
                        idMotivoDemissao = (Integer) listaDemissao.get(i).getValue();
                        break;
                    }
                }

                tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
                agendamento.setStatus((Status) dao.find(new Status(), 5));
                agendamento.setHomologador(su.getSessaoUsuario());
                dao.openTransaction();
                if (!dao.update(agendamento)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível atualizar Agendamento!");
                    return null;
                }

                senhax.setMesa(macFilial.getMesa());
                senhax.setHoraChamada(DataHoje.horaMinuto());
                senhax.setDtVerificada(new Date());
                if (!dao.update(senhax)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                    return null;
                }
                dao.commit();

                PF.update("formConcluirHomologacao");
                PF.openDialog("dlg_homologacao");
                WSSocket.send(getWebSocketSenha(), "call");
                return null;
            } else if (senhax.getAteMovimento() != null) {
                // SENHA DE ATENDIMENTO --------------------------------------------------------------------------------------------------------------------------
                // STATUS 1 - AGUARDANDO
                if (senhax.getAteMovimento().getStatus().getId() == 1) {
                    // OPERAÇÃO 8 - DSR - OBRIGATORIO SENHA TER RESERVADA ----
                    if (senhax.getAteMovimento().getOperacao().getId() == 8 && senhax.getAteMovimento().getReserva() == null) {
                        continue;
                    }

                    // RESERVADO PARA O USUÁRIO ------
                    if (senhax.getAteMovimento().getReserva() != null && (senhax.getAteMovimento().getReserva().getId() == su.getSessaoUsuario().getId())) {
                        dao.openTransaction();

                        senhax.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 4));
                        senhax.getAteMovimento().setAtendente(su.getSessaoUsuario());

                        senhax.setHoraChamada(DataHoje.horaMinuto());
                        senhax.setMesa(macFilial.getMesa());
                        senhax.setDtVerificada(new Date());
                        if (!dao.update(senhax) || !dao.update(senhax.getAteMovimento())) {
                            dao.rollback();
                            GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                            return null;
                        }

                        dao.commit();

                        //listaAtendimentoSimples.clear();
                        senhaAtendimento = senhax;
                        PF.update("form_cancelar_data_table:tbl_at");
                        PF.openDialog("dlg_atendimento_simples");
                        WSSocket.send(getWebSocketSenha(), "call");
                        return null;
                    }

                    // NÃO É RESERVA ----
                    if (senhax.getAteMovimento().getReserva() == null) {
                        dao.openTransaction();

                        senhax.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 4));
                        senhax.getAteMovimento().setAtendente(su.getSessaoUsuario());

                        senhax.setHoraChamada(DataHoje.horaMinuto()); // aqui
                        senhax.setMesa(macFilial.getMesa());
                        senhax.setDtVerificada(new Date());
                        senhax.setDtNovaChamada(new Date());
                        if (!dao.update(senhax) || !dao.update(senhax.getAteMovimento())) {
                            dao.rollback();
                            GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                            return null;
                        }
                        dao.commit();

                        //listaAtendimentoSimples.clear();
                        senhaAtendimento = senhax;
                        PF.update("form_cancelar_data_table:tbl_at");
                        PF.openDialog("dlg_atendimento_simples");
                        WSSocket.send(getWebSocketSenha(), "call");
                        return null;
                    }
                }
            }
        }
        return "homologacao";
    }

    public String atendimento() {
        if (macFilial.getId() == -1) {
            //msgHomologacao = "Mac não encontrado!";
            GenericaMensagem.warn("Atenção", "MAC não foi encontrado!");
            return "homologacao";
        }

        retornaSequenciaSenha();

        if (1 == 1) {
            return null;
        }
        HomologacaoDao homologacaoDB = new HomologacaoDao();
        SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();
        Dao dao = new Dao();
        Senha senhaAtendimentoReserva = homologacaoDB.pesquisaAtendimentoReserva(macFilial.getFilial().getId(), su.getSessaoUsuario().getId());
        Senha senhaAtendimentox = homologacaoDB.pesquisaAtendimentoIniciadoSimples(macFilial.getFilial().getId());
        Senha senhaHomologacao = homologacaoDB.pesquisaSenhaAtendimento(macFilial.getFilial().getId());

        // SENHA DE ATENDIMENTO RESERVADA
        if (senhaAtendimentoReserva != null) {
            dao.openTransaction();

            senhaAtendimentoReserva.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 4));
            senhaAtendimentoReserva.getAteMovimento().setAtendente(su.getSessaoUsuario());

            senhaAtendimentoReserva.setHoraChamada(DataHoje.horaMinuto());
            senhaAtendimentoReserva.setMesa(macFilial.getMesa());

            if (!dao.update(senhaAtendimentoReserva) || !dao.update(senhaAtendimentoReserva.getAteMovimento())) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                return null;
            }
            dao.commit();

            listaAtendimentoSimples.clear();

            senhaAtendimento = senhaAtendimentoReserva;
            senhaAtendimento.setDtVerificada(new Date());
            dao.update(senhaAtendimento, true);
            PF.update("form_cancelar_data_table:tbl_at");
            PF.openDialog("dlg_atendimento_simples");
            return null;
        }

        // SENHA DE ATENDIMENTO
        if (senhaAtendimentox != null && senhaHomologacao.getId() == -1) {
            dao.openTransaction();

            senhaAtendimentox.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 4));
            senhaAtendimentox.getAteMovimento().setAtendente(su.getSessaoUsuario());

            senhaAtendimentox.setHoraChamada(DataHoje.horaMinuto());
            senhaAtendimentox.setMesa(macFilial.getMesa());

            if (!dao.update(senhaAtendimentox) || !dao.update(senhaAtendimentox.getAteMovimento())) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                return null;
            }
            dao.commit();
            listaAtendimentoSimples.clear();

            senhaAtendimento = senhaAtendimentox;
            senhaAtendimento.setDtVerificada(new Date());
            dao.update(senhaAtendimento, true);
            RequestContext.getCurrentInstance().update("form_cancelar_data_table:tbl_at");
            RequestContext.getCurrentInstance().execute("PF('dlg_atendimento_simples').show();");
            return null;
        } else if (senhaAtendimentox != null && (senhaAtendimentox.getSenha() < senhaHomologacao.getSenha())) {
            dao.openTransaction();

            senhaAtendimentox.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 4));
            senhaAtendimentox.getAteMovimento().setAtendente(su.getSessaoUsuario());

            senhaAtendimentox.setHoraChamada(DataHoje.horaMinuto());
            senhaAtendimentox.setMesa(macFilial.getMesa());

            if (!dao.update(senhaAtendimentox) || !dao.update(senhaAtendimentox.getAteMovimento())) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
                return null;
            }
            dao.commit();

            listaAtendimentoSimples.clear();

            senhaAtendimento = senhaAtendimentox;
            senhaAtendimento.setDtVerificada(new Date());
            dao.update(senhaAtendimento, true);
            RequestContext.getCurrentInstance().update("form_cancelar_data_table:tbl_at");
            RequestContext.getCurrentInstance().execute("PF('dlg_atendimento_simples').show();");
            return null;
        }

        // SENHA PADRÃO DE HOMOLOGAÇÃO
        Senha senhaHomologacaoI = homologacaoDB.pesquisaAtendimentoIniciado(su.getSessaoUsuario().getId(), macFilial.getMesa(), macFilial.getFilial().getId(), macFilial.getDepartamento().getId());
        if (senhaHomologacaoI.getId() != -1) {
            senhaHomologacao = senhaHomologacaoI;
        }

        if (senhaHomologacao.getId() == -1) {
            GenericaMensagem.warn("Atenção", "Senha não encontrada!");
            return null;
        }

        agendamento = senhaHomologacao.getAgendamento();
        fisica = senhaHomologacao.getAgendamento().getPessoaEmpresa().getFisica();
        juridica = senhaHomologacao.getAgendamento().getPessoaEmpresa().getJuridica();
        pessoaEmpresa = senhaHomologacao.getAgendamento().getPessoaEmpresa();
        profissao = senhaHomologacao.getAgendamento().getPessoaEmpresa().getFuncao();
        renderHomologar = true;
        visibleModal = true;
        renderHomologar = true;
        renderCancelarHorario = true;

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaPesquisa", juridica);

        for (int i = 0; i < getListaDemissao().size(); i++) {
            if (Integer.parseInt(listaDemissao.get(i).getDescription()) == agendamento.getDemissao().getId()) {
                idMotivoDemissao = (Integer) listaDemissao.get(i).getValue();
                break;
            }
        }
        dao = new Dao();
        tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
        agendamento.setStatus((Status) dao.find(new Status(), 5));
        agendamento.setHomologador(su.getSessaoUsuario());
        dao.openTransaction();
        if (!dao.update(agendamento)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível atualizar Agendamento!");
            return "homologacao";
        }
        senhaHomologacao.setMesa(macFilial.getMesa());
        senhaHomologacao.setHoraChamada(DataHoje.horaMinuto());
        if (!dao.update(senhaHomologacao)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível atualizar Senha!");
            return "homologacao";
        }
        dao.commit();

        return "homologacao";
    }

    public void fecharModalSenha() {
        if (senhaAtendimento.getId() != -1) {
            Dao dao = new Dao();

            dao.openTransaction();

            senhaAtendimento.getAteMovimento().setStatus((AteStatus) dao.find(new AteStatus(), 1));
            senhaAtendimento.getAteMovimento().setAtendente(null);

            senhaAtendimento.setDtVerificada(new Date());
            senhaAtendimento.setDtNovaChamada(new Date());
            senhaAtendimento.setHoraChamada("");
            senhaAtendimento.setMesa(0);

            if (dao.update(senhaAtendimento) && dao.update(senhaAtendimento.getAteMovimento())) {
                dao.commit();
            } else {
                dao.rollback();
            }

            senhaAtendimento = new Senha();
            loadListaAtendimentoSimples();
        }
        PF.closeDialog("dlg_atendimento_simples");
        listFiles.clear();
        WSSocket.send(getWebSocketSenha(), "close");
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            List<Status> list = (List<Status>) new Dao().find("Status", new int[]{2, 3, 4, 5, 7});
            for (int i = 0; i < list.size(); i++) {
                listaStatus.add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
            }
        }
        return listaStatus;
    }

    public List<SelectItem> getListaDemissao() {
        if (listaDemissao.isEmpty()) {
            List<Demissao> list = (List<Demissao>) new Dao().list(new Demissao(), true);
            for (int i = 0; i < list.size(); i++) {
                listaDemissao.add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
            }
        }
        return listaDemissao;
    }

    public String agendar(Agendamento a) {
        HomologacaoDao db = new HomologacaoDao();
        Dao dao = new Dao();
        agendamento = a;
        cancelamento = new Cancelamento();
        int nrStatus = Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription());
        boolean hc = false;
        if (registro.getHomolocaoHabilitaCorrecao() != null && DataHoje.converteData(registro.getHomolocaoHabilitaCorrecao()).equals(DataHoje.data())) {
            hc = true;
        }
        if (nrStatus == 4) {
            if (!desabilitaEdicao(agendamento.getDtData(), 30) && !hc) {
                GenericaMensagem.warn("Atenção", "Não é possível realizar alterações com datas superiores a 30 dias a data de hoje. Contate o administrador do sistema para habilitar a correção de homologações pendentes!");
                PF.update("form_cancelar_data_table:i_msg");
//                msgConfirma = "Não é possível realizar alterações com datas superiores a 30 dias a data de hoje. Contate o administrador do sistema para habilitar a correção de homologações pendentes!";
//                PF.update("form_homologacao:i_painel_mensagem");
//                PF.openDialog("dgl_painel_mensagem");
                return null;
            }
        } else if (nrStatus == 3 || nrStatus == 5) {
            if (!desabilitaEdicao(agendamento.getDtData(), 30) && !hc) {
                GenericaMensagem.warn("Atenção", "Não é possível realizar alterações com datas superiores a 30 dias a data de hoje. Contate o administrador do sistema para habilitar a correção de homologações pendentes!");
                PF.update("form_cancelar_data_table:i_msg");
//                msgConfirma = "Não é possível realizar alterações com datas superiores a 30 dias a data de hoje. Contate o administrador do sistema para habilitar a correção de homologações pendentes!";
//                PF.update("form_homologacao:i_painel_mensagem");
//                PF.openDialog("dgl_painel_mensagem");
                return null;
            }
        } else if (DataHoje.converteDataParaInteger(DataHoje.converteData(data)) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
            if (registro.isSenhaHomologacao()) {
                Senha senha = db.pesquisaSenhaAgendamento(agendamento.getId());
                if (senha.getId() == -1) {
//                        msgConfirma = "Não há senha definida!";
                    GenericaMensagem.warn("Atenção", "Não existe uma senha para essa Homologação!");
                    PF.update("form_cancelar_data_table:i_msg");
                    //PF.openDialog("dgl_painel_mensagem");
                    return null;
                }
            }
        }

        visibleModal = true;

        if (nrStatus > 0) {
            fisica = agendamento.getPessoaEmpresa().getFisica();
            juridica = agendamento.getPessoaEmpresa().getJuridica();
            pessoaEmpresa = agendamento.getPessoaEmpresa();
            profissao = agendamento.getPessoaEmpresa().getFuncao();
            tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
            for (int i = 0; i < getListaDemissao().size(); i++) {
                if (Integer.parseInt(listaDemissao.get(i).getDescription()) == agendamento.getDemissao().getId()) {
                    idMotivoDemissao = (Integer) listaDemissao.get(i).getValue();
                    break;
                }
            }
            switch (nrStatus) {
                case 2: {
                    renderHomologar = true;
                    renderCancelarHorario = true;
                    agendamento.setStatus((Status) dao.find(new Status(), 5));
                    agendamento.setHomologador((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
                    dao.openTransaction();
                    if (dao.update(agendamento)) {
                        dao.commit();
                    } else {
                        dao.rollback();
                    }
                    break;
                }
                case 3: {
                    renderHomologar = true;
                    renderCancelarHorario = false;
                    // agendamento.setStatus((Status) dao.find(new Status(), 5));
                    cancelamento = new CancelamentoDao().findByAgendamento(agendamento.getId());
                    if (cancelamento == null) {
                        cancelamento = new Cancelamento();
                    }
                    agendamento.setHomologador((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
                    dao.openTransaction();
                    if (dao.update(agendamento)) {
                        dao.commit();
                    } else {
                        dao.rollback();
                    }
                    break;
                }
                case 4: {
                    renderHomologar = false;
                    renderCancelarHorario = true;
                    visibleModal = true;
                    break;
                }
                case 5: {
                    if (((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario")).getId() == agendamento.getHomologador().getId()) {
                        renderHomologar = true;
                        renderCancelarHorario = true;
                        visibleModal = true;
                        break;
                    } else {
                        renderHomologar = false;
                        renderCancelarHorario = false;
                        visibleModal = false;
                        agendamento = new Agendamento();
                        break;
                    }
                }
                case 7: {
                    renderHomologar = true;
                    renderCancelarHorario = true;
                    break;
                }
            }
        }

        if (agendamento.getTelefone().length() > 14) {
            tipoTelefone = "celular";
        } else {
            tipoTelefone = "telefone";
        }
        return "homologacao";
    }

    public void salvar() {
        List listDocumento;
        if (fisica.getPessoa().getNome().equals("") || fisica.getPessoa().getNome() == null) {
            GenericaMensagem.error("Atenção", "Digite o nome do Funcionário!");
            return;
        }
        // SALVAR FISICA -----------------------------------------------
        Dao dao = new Dao();
        fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
        if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
            GenericaMensagem.error("Atenção", "Documento Inválido!");
            return;
        }
        FisicaDao fisicaDB = new FisicaDao();
        dao.openTransaction();
        if (fisica.getId() == -1) {
            if (!fisicaDB.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(), fisica.getDtNascimento(), fisica.getRg()).isEmpty()) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Esta pessoa já esta cadastrada!");
                return;
            }
            listDocumento = fisicaDB.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            if (!listDocumento.isEmpty()) {
                GenericaMensagem.error("Atenção", "Documento já existente!");
                return;
            }
            if (!dao.save(fisica.getPessoa())) {
                GenericaMensagem.error("Atenção", "Erro ao Inserir pessoa!");
                dao.rollback();
                return;
            }
            if (!dao.save(fisica)) {
                GenericaMensagem.error("Atenção", "Erro ao Inserir pessoa física!");
                dao.rollback();
                return;

            }
        } else {
            listDocumento = fisicaDB.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            for (Object listDocumento1 : listDocumento) {
                if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != fisica.getId()) {
                    dao.rollback();
                    GenericaMensagem.error("Atenção", "Documento já existente!");
                    return;
                }
            }
            List<Fisica> fisi = fisicaDB.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(), fisica.getDtNascimento(), fisica.getRg());
            if (!fisi.isEmpty()) {
                for (Fisica fisi1 : fisi) {
                    if (fisi1.getId() != fisica.getId()) {
                        dao.rollback();
                        GenericaMensagem.error("Atenção", "Esta pessoa já esta cadastrada!");
                        return;
                    }
                }
            }
            if (!dao.update(fisica.getPessoa())) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao alterar pessoa!");
                return;
            }
            if (!dao.update(fisica)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao alterar Física!");
                return;
            }
        }
        // -------------------------------------------------------------
        pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
        if (this.profissao.getId() == -1) {
            pessoaEmpresa.setFuncao(null);
        } else {
            pessoaEmpresa.setFuncao(this.profissao);
        }
        if (!dao.update(pessoaEmpresa)) {
            GenericaMensagem.error("Atenção", "Erro ao alterar Pessoa Empresa!");
            dao.rollback();
            return;
        }
        agendamento.setDemissao((Demissao) dao.find(new Demissao(), Integer.parseInt(((SelectItem) getListaDemissao().get(idMotivoDemissao)).getDescription())));
        agendamento.setPessoaEmpresa(pessoaEmpresa);
        HomologacaoDao homologacaoDB = new HomologacaoDao();
        int nrStatus = Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription());
        if (registro.isSenhaHomologacao()) {
            if (nrStatus != 4 && nrStatus != 7) {
                Senha senha = homologacaoDB.pesquisaSenhaAgendamento(agendamento.getId());
                senha.setMesa(0);
                senha.setHoraChamada("");
                if (!dao.update(senha)) {
                    GenericaMensagem.error("Atenção", "Erro ao atualizar Senha!");
                    return;
                }
            }
        }
        switch (nrStatus) {
            case 2:
            case 4:
                agendamento.setStatus((Status) dao.find(new Status(), nrStatus));
                if (manutencao) {
                    if (agendamento.getHomologador() != null && agendamento.getHomologador().getId() == 1) {
                        agendamento.setHomologador(null);
                    }
                }
                break;
            case 7:
                agendamento.setStatus((Status) dao.find(new Status(), nrStatus));
                agendamento.setHomologador(null);
                break;
            case 5:
                if (DataHoje.converteDataParaInteger(agendamento.getData()) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                    agendamento.setStatus((Status) dao.find(new Status(), 2));
                    agendamento.setHomologador(null);
                } else {
                    agendamento.setStatus((Status) dao.find(new Status(), 2));
                    agendamento.setHomologador(null);
                }
                break;
            default:
                break;
        }
        if (!dao.update(agendamento)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ATUALIZAR AGENDAMENTO!");
            return;
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
        loadListaHomologacao();
    }

    public void closeModal() {
        HomologacaoDao db = new HomologacaoDao();
        Dao dao = new Dao();
        int nrStatus = Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription());
        dao.openTransaction();
        if (registro.isSenhaHomologacao()) {
            if (nrStatus != 4 && nrStatus != 7) {
                Senha senha = db.pesquisaSenhaAgendamento(agendamento.getId());
                if (senha.getId() != -1) {
                    senha.setMesa(0);
                    senha.setHoraChamada("");
                    if (!dao.update(senha)) {
                        msgConfirma = "Erro ao atualizar Senha!";
                        GenericaMensagem.warn("Erro", "Erro ao atualizar Senha!");
                        dao.rollback();
                        return;
                    }
                }
            }
        }
        switch (nrStatus) {
            case 2:
            case 4:
                agendamento.setStatus((Status) dao.find(new Status(), nrStatus));
                break;
            case 7:
                agendamento.setStatus((Status) dao.find(new Status(), nrStatus));
                agendamento.setHomologador(null);
                break;
            case 5:
                if (DataHoje.converteDataParaInteger(agendamento.getData()) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                    agendamento.setStatus((Status) dao.find(new Status(), 2));
                    agendamento.setHomologador(null);
                } else {
                    agendamento.setStatus((Status) dao.find(new Status(), 2));
                    agendamento.setHomologador(null);
                }
                break;
            default:
                break;
        }

        if (dao.update(agendamento)) {
            dao.commit();
            msgConfirma = "Registro atualizado com Sucesso!";
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            limpar();
            WSSocket.send(getWebSocketSenha(), "update");
        } else {
            dao.rollback();
        }
    }

    public void homologar() {
        if (pessoaEmpresa.getAdmissao().isEmpty()) {
            GenericaMensagem.error("Atenção", "Data de Admissão não pode ser vazio!");
            return;
        }

        if (pessoaEmpresa.getDemissao().isEmpty()) {
            GenericaMensagem.error("Atenção", "Data de Demissão não pode ser vazio!");
            return;
        }
        Dao dao = new Dao();
        agendamento.setHomologador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
        agendamento.setStatus((Status) dao.find(new Status(), 4));
        new Cancelamento().getAgendamento().getId();
        Cancelamento c = (Cancelamento) dao.find("Cancelamento", agendamento.getId(), "agendamento.id");
        dao.openTransaction();
        if (c != null) {
            if (!dao.delete(c)) {
                GenericaMensagem.error("Atenção", "Erro ao homologar!");
                dao.rollback();
                return;
            }
        }
        if (dao.update(agendamento) && dao.update(pessoaEmpresa)) {
            GenericaMensagem.info("Sucesso", "Agendamento homologado!");
            dao.commit();
            HomologacaoDao hd = new HomologacaoDao();
            Senha senha = hd.pesquisaSenhaAgendamento(agendamento.getId());
            senha.setDtVerificada(DataHoje.converte("01/01/1900"));
            dao.update(senha, true);
            WSSocket.send(getWebSocketSenha(), "update");
        } else {
            GenericaMensagem.error("Atenção", "Erro ao homologar!");
            dao.rollback();
        }
        strEndereco = "";
        visibleModal = false;
        tipoAviso = "true";
        fisica = new Fisica();
        agendamento = new Agendamento();
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        loadListaHomologacao();
    }

    public String cancelarHomologacao() {
        if (cancelamento.getMotivo().isEmpty() || cancelamento.getMotivo().length() <= 5) {
            GenericaMensagem.warn("Atenção", "Motivo de cancelamento inválido");
            return null;
        }
        Dao dao = new Dao();
        Status s = agendamento.getStatus();
        dao.openTransaction();
        if (!dao.update(agendamento)) {
            GenericaMensagem.error("Atenção", "Erro ao cancelar homologagação!");
            dao.rollback();
            return null;
        }
        pessoaEmpresa.setDtDemissao(null);
        if (!dao.update(pessoaEmpresa)) {
            GenericaMensagem.error("Atenção", "Erro ao atualizar Pessoa Empresa");
            return null;
        }
        cancelamento.setAgendamento(agendamento);
        cancelamento.setDtData(DataHoje.dataHoje());
        cancelamento.setUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")));
        if (!dao.save(cancelamento)) {
            GenericaMensagem.error("Atenção", "Erro ao salvar cancelamento");
            return null;
        }
        agendamento.setStatus((Status) dao.find(new Status(), 3));
        if (!dao.update(agendamento)) {
            dao.rollback();
            agendamento.setStatus(s);
            GenericaMensagem.error("Atenção", "Erro ao cancelar homologação!");
            return null;
        }
        cancelamento = new Cancelamento();
        GenericaMensagem.info("Sucesso", "Homologação Cancelada !");
        strEndereco = "";
        visibleModal = false;
        tipoAviso = "true";
        fisica = new Fisica();
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        dao.commit();
        HomologacaoDao hd = new HomologacaoDao();
        Senha senha = hd.pesquisaSenhaAgendamento(agendamento.getId());
        senha.setDtVerificada(DataHoje.converte("01/01/1900"));
        Dao dao2 = new Dao();
        dao2.update(senha, true);
        loadListaHomologacao();
        // WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
        WSSocket.send(getWebSocketSenha(), "update");
        agendamento = new Agendamento();
        return null;
    }

    public void limpar() {
        strEndereco = "";
        visibleModal = false;
        tipoAviso = "true";
        fisica = new Fisica();
        pessoaEmpresa = new PessoaEmpresa();
        agendamento = new Agendamento();
        profissao = new Profissao();
    }

    public String pesquisarFuncionarioCPF() {
        HomologacaoDao db = new HomologacaoDao();
        FisicaDao dbFis = new FisicaDao();
        fisica.getPessoa().setTipoDocumento((TipoDocumento) new Dao().find(new TipoDocumento(), 1));
        PessoaEmpresa pe = db.pesquisaPessoaEmpresaPertencente(fisica.getPessoa().getDocumento());
        if (pe.getId() != -1) {
            pessoaEmpresa = pe;
            fisica = pessoaEmpresa.getFisica();
            profissao = pessoaEmpresa.getFuncao();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaPesquisa", pessoaEmpresa.getJuridica());
            if (pessoaEmpresa.getJuridica().getContabilidade() != null) {
                agendamento.setTelefone(pessoaEmpresa.getJuridica().getContabilidade().getPessoa().getTelefone1());
            }
            return "homologacao";
        } else {
            List<Fisica> listFisica = dbFis.pesquisaFisicaPorDocSemLike(fisica.getPessoa().getDocumento());
            for (int i = 0; i < listFisica.size(); i++) {
                if (!listFisica.isEmpty() && listFisica.get(i).getId() != fisica.getId()) {
                    fisica = listFisica.get(i);
                    pessoaEmpresa = new PessoaEmpresa();
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("juridicaPesquisa");
                    return "homologacao";
                }
            }
            if (fisica.getId() != -1) {
                fisica = new Fisica();
                pessoaEmpresa = new PessoaEmpresa();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("juridicaPesquisa");
            }
        }
        return "homologacao";
    }

    public boolean desabilitaEdicao(Date date, int periodoDias) {
        long dataL = DataHoje.calculoDosDias(date, DataHoje.dataHoje());
        if (dataL <= periodoDias) {
            return true;
        }
        return false;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public PessoaEndereco getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public void setEnderecoEmpresa(PessoaEndereco enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }

    public String getStrEndereco() {
        PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
        if (juridica.getId() != -1) {
            enderecoEmpresa = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 2);
            if (enderecoEmpresa.getId() != -1) {
                String strCompl;
                if (enderecoEmpresa.getComplemento().equals("")) {
                    strCompl = " ";
                } else {
                    strCompl = " ( " + enderecoEmpresa.getComplemento() + " ) ";
                }

                strEndereco = enderecoEmpresa.getEndereco().getLogradouro().getDescricao() + " "
                        + enderecoEmpresa.getEndereco().getDescricaoEndereco().getDescricao() + ", " + enderecoEmpresa.getNumero() + " " + enderecoEmpresa.getEndereco().getBairro().getDescricao() + ","
                        + strCompl + enderecoEmpresa.getEndereco().getCidade().getCidade() + " - " + enderecoEmpresa.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(enderecoEmpresa.getEndereco().getCep());
            } else {
                strEndereco = "";
            }
        } else {
            enderecoEmpresa = new PessoaEndereco();
            strEndereco = "";
        }
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public int getIdMotivoDemissao() {
        return idMotivoDemissao;
    }

    public void setIdMotivoDemissao(int idMotivoDemissao) {
        this.idMotivoDemissao = idMotivoDemissao;
    }

    public String getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(String tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public boolean isRenderHomologar() {
        return renderHomologar;
    }

    public void setRenderHomologar(boolean renderHomologar) {
        this.renderHomologar = renderHomologar;
    }

    public boolean isRenderCancelarHorario() {
        return renderCancelarHorario;
    }

    public void setRenderCancelarHorario(boolean renderCancelarHorario) {
        this.renderCancelarHorario = renderCancelarHorario;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public String getStatusEmpresa() {
        HomologacaoDao db = new HomologacaoDao();
        List lista = new ArrayList();
        if (juridica.getId() != -1) {
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

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public int senhaHomologacao(int id) {
        HomologacaoDao db = new HomologacaoDao();
        Senha senha = db.pesquisaSenhaAgendamento(id);
        return senha.getSenha();
    }

    public Cancelamento getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(Cancelamento cancelamento) {
        this.cancelamento = cancelamento;
    }

    public synchronized List<ListaAgendamento> getListaHomologacoes() {
        return listaHomologacoes;
    }

    public void setListaHomologacoes(List<ListaAgendamento> listaHomologacoes) {
        this.listaHomologacoes = listaHomologacoes;
    }

    public List<Agendamento> listAtendimentoAberto() {
        List<Agendamento> list = new ArrayList();
        if (GenericaSessao.exists("sessaoUsuario")) {
            HomologacaoDao dao = new HomologacaoDao();
            list = (List<Agendamento>) dao.pesquisaAgendamentoAtendimentoAberto(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
        }
        return list;
    }

    public Senha getSenhaAtendimento() {
        return senhaAtendimento;
    }

    public void setSenhaAtendimento(Senha senhaAtendimento) {
        this.senhaAtendimento = senhaAtendimento;
    }

    public boolean isVisibleModal() {
        return visibleModal;
    }

    public void setVisibleModal(boolean visibleModal) {
        this.visibleModal = visibleModal;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    // ARQUIVOS
    public List getListFiles() {
        listFiles.clear();
        listFiles = Diretorio.listaArquivos("Arquivos/homologacao/" + agendamento.getId());
        return listFiles;
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setDiretorio("Arquivos/homologacao/" + agendamento.getId());
        configuracaoUpload.setEvent(event);
        if (Upload.enviar(configuracaoUpload, true)) {
            listFiles.clear();
        }
        getListFiles();
    }

    public void deleteFiles(int index) {
        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/" + agendamento.getId() + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        File fl = new File(caminho);
        fl.delete();
        listFiles.remove(index);
        listFiles.clear();
        getListFiles();
        PF.update("formConcluirHomologacao:id_grid_uploads");
        PF.update("formConcluirHomologacao:id_btn_anexo");
    }

    public String getWebSocketSenha() {
        return "senha_homologacao_" + ControleUsuarioBean.getCliente().toLowerCase() + "_" + macFilial.getFilial().getId();
    }

    public Boolean getManutencao() {
        return manutencao;
    }

    public void setManutencao(Boolean manutencao) {
        this.manutencao = manutencao;
    }

}
