package br.com.rtools.homologacao.beans;

import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.pessoa.beans.PesquisarProfissaoBean;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.dao.ConvencaoDao;
import br.com.rtools.arrecadacao.dao.RelacaoEmpregadosDao;
import br.com.rtools.atendimento.dao.AtendimentoDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.dao.EnderecoDao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.beans.PlanilhaDebitoBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.lista.ListMovimentoReceber;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.ConfiguracaoHomologacao;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.Feriados;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.ListaAgendamento;
import br.com.rtools.homologacao.Status;
import br.com.rtools.homologacao.dao.FeriadosDao;
import br.com.rtools.homologacao.dao.HorarioReservaDao;
import br.com.rtools.homologacao.dao.*;
import br.com.rtools.impressao.beans.ProtocoloAgendamento;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.utilitarios.PessoaUtilitarios;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.dao.ConfiguracaoDepartamentoDao;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.dao.FunctionsDao;
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

@ManagedBean
@SessionScoped
public class AgendamentoBean extends PesquisarProfissaoBean implements Serializable {

    private int idStatus = 1;
    private int idMotivoDemissao = 0;
    private int idHorarioTransferencia = 0;
    private int idHorarioAlternativo = 0;
    //private String tipoAviso = "true";
    private String strEndereco = "";
    private String statusEmpresa = "REGULAR";
    private String cepEndereco = "";
    private String emailEmpresa = "";
    private String styleDestaque = "";
    private List<Movimento> listaMovimento = new ArrayList();
    private final List<SelectItem> listaStatus = new ArrayList();
    private final List<SelectItem> listaDemissao = new ArrayList();
    private List<SelectItem> listaHorarioTransferencia = new ArrayList();
    private final List<ListaAgendamento> listaHorarios = new ArrayList();
    private Juridica juridica = new Juridica();
    private Fisica fisica = new Fisica();
    private Date data = DataHoje.dataHoje();
    private Date dataTransferencia = DataHoje.dataHoje();
    private Agendamento agendamento = new Agendamento();
    private PessoaEndereco enderecoEmpresa = new PessoaEndereco();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
    private boolean renderCancelarHorario = false;
    private boolean renderCancelar = true;
    private boolean renderedDisponivel = false;
    private boolean ocultarHorarioAlternativo = true;
    private MacFilial macFilial = null;
    private int protocolo = 0;
    private int idIndex = -1;
    private int idIndexEndereco = -1;
    private int id_protocolo = -1;
    private List<Endereco> listaEnderecos = new ArrayList();
    private PessoaEndereco enderecoFisica = new PessoaEndereco();
    private String enviarPara = "contabilidade";
    private PessoaEndereco enderecoFilial = new PessoaEndereco();
    private boolean imprimirPro = false;
    private ConfiguracaoHomologacao configuracaoHomologacao = new ConfiguracaoHomologacao();
    private int counter = 0;

    private Registro registro = new Registro();
    private boolean visibleModal = false;
    private boolean validacao = false;

    private String tipoTelefone = "telefone";
    private Cancelamento cancelamento = new Cancelamento();
    private String documentoFisica = "";
    private Date polling;
    private List<SelectItem> listFilial;
    private Integer idFilial;
    private Boolean renderedFilial;
    private List listFiles;
    private String motivoRecusaDireto;
    private String motivoRecusa1;
    private String motivoRecusa2;

    public AgendamentoBean() {
        if (configuracaoHomologacao.getId() == null) {
            configuracaoHomologacao = (ConfiguracaoHomologacao) new Dao().find(new ConfiguracaoHomologacao(), 1);
        }
        if (configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
            DataHoje dh = new DataHoje();
            data = DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento() + 1, DataHoje.converteData(data)));
        }
        if (configuracaoHomologacao.getWebValidaAgendamento()) {
            idStatus = 8;
        }
        motivoRecusaDireto = "";
        motivoRecusa1 = "";
        motivoRecusa2 = "";
        listFiles = new ArrayList();
        macFilial = (MacFilial) GenericaSessao.getObject("acessoFilial");
        Dao dao = new Dao();
        registro = (Registro) dao.find(new Registro(), 1);
        HorarioReservaDao horarioReservaDao = new HorarioReservaDao();
        horarioReservaDao.begin();
        horarioReservaDao.clear();
        if (macFilial != null) {
            this.loadListaHorarios();
            if (listaHorarios.isEmpty()) {
                data = DataHoje.dataHoje();
                idStatus = 1;
                this.loadListaHorarios();
            }
            this.loadListaHorariosTransferencia();
        }
        renderedFilial = false;
        GlobalSync.load();
    }

    public boolean validaAdmissao() {
        if (fisica.getId() != -1 && juridica.getId() != -1 && !pessoaEmpresa.getAdmissao().isEmpty() && pessoaEmpresa.getId() == -1) {
            HomologacaoDao db = new HomologacaoDao();

            PessoaEmpresa pe = db.pesquisaPessoaEmpresaAdmissao(fisica.getId(), juridica.getId(), pessoaEmpresa.getAdmissao());

            if (pe != null) {
                int[] ids = new int[2];
                ids[0] = 2;
                ids[1] = 4;
                Agendamento a = db.pesquisaAgendamentoPorPessoaEmpresa(pe.getId(), ids);

                if (a != null) {
                    GenericaMensagem.fatal("Atenção", "Esse agendamento já foi " + a.getStatus().getDescricao() + "!");
                    return false;
                }

                pessoaEmpresa = pe;
            }
        }
        return true;
    }

    public void actionValidaAdmissao() {
        validaAdmissao();
    }

    public boolean validaDemissao() {
        if (fisica.getId() != -1 && juridica.getId() != -1 && !pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getId() == -1) {
            HomologacaoDao db = new HomologacaoDao();

            PessoaEmpresa pe = db.pesquisaPessoaEmpresaDemissao(fisica.getId(), juridica.getId(), pessoaEmpresa.getDemissao());

            if (pe != null) {
                int[] ids = new int[2];
                ids[0] = 2;
                ids[1] = 4;
                Agendamento a = db.pesquisaAgendamentoPorPessoaEmpresa(pe.getId(), ids);

                if (a != null) {
                    GenericaMensagem.fatal("Atenção", "Esse agendamento já foi " + a.getStatus().getDescricao() + "!");
                    return false;
                }
                pessoaEmpresa = pe;
            }
        }
        return true;
    }

    public void actionValidaDemissao() {
        validaDemissao();
    }

    public void alterarTipoMascara() {
        if (tipoTelefone.equals("telefone")) {
            tipoTelefone = "celular";
        } else {
            tipoTelefone = "telefone";
        }
        agendamento.setTelefone("");
    }

    public final void alterFilial(Boolean renderedFilial) {
        this.renderedFilial = renderedFilial;
        loadListFilial();
    }

    public void loadListFilial() {
        listFilial = new ArrayList();
        List<Filial> list = new FilialDao().findByHorarios();
        idFilial = macFilial.getFilial().getId();
        listFilial = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            listFilial.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public final void loadListaHorariosTransferencia() {
        loadListaHorariosTransferencia(macFilial.getFilial().getId());
    }

    public final void loadListaHorariosTransferencia(Integer filial_id) {
        listaHorarioTransferencia = new ArrayList();
        idHorarioTransferencia = 0;

        HomologacaoDao db = new HomologacaoDao();
        int idDiaSemana = DataHoje.diaDaSemana(dataTransferencia);
        List<Horarios> select = db.pesquisaTodosHorariosDisponiveis(filial_id, idDiaSemana);
        if (select.isEmpty()) {
            listaHorarioTransferencia.add(new SelectItem(0, "Nenhum horário encontrado", "0"));
            return;
        }

        int qnt;
        int j = 0;
        for (Horarios listh : select) {
            qnt = db.pesquisaQntdDisponivel(macFilial.getFilial().getId(), listh, getDataTransferencia());
            if (qnt == -1) {
                listaHorarioTransferencia.add(new SelectItem(0, "Nenhum horário disponível", "0"));
                return;
            }
            if (qnt > 0) {
                listaHorarioTransferencia.add(new SelectItem(j, listh.getHora() + " (" + qnt + ")", String.valueOf(listh.getId())));
                j++;
            }
        }

        if (listaHorarioTransferencia.isEmpty()) {
            listaHorarioTransferencia.add(new SelectItem(0, "Nenhum horário encontrado", "0"));
        }
    }

    public final void loadListaHorarios() {
        loadListaHorarios(stop());
    }

    public final void loadListaHorariosStatus(Boolean stop) {
        if (configuracaoHomologacao.getInicioDiasAgendamento() > 0 && idStatus == 1) {
            DataHoje dh = new DataHoje();
            data = DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento() + 1, DataHoje.data()));
        }
        loadListaHorarios(stop);
    }

    public final void loadListaHorarios(Boolean stop) {

        if (stop) {
            return;
        }

        listaHorarios.clear();

        if (macFilial == null) {
            return;
        }

        if (!getMindate().isEmpty() && idStatus == 1) {
            if (configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
                DataHoje dh = new DataHoje();
                if (!DataHoje.maiorData(data, DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento(), DataHoje.data())))) {
                    // data = DataHoje.dataHoje();
                    GenericaMensagem.warn("Validação", "Data não disponível para agendamento!");
                    return;
                }
            }
        }

        int idNrStatus = idStatus;
        int diaDaSemana = DataHoje.diaDaSemana(data);
        renderedDisponivel = idNrStatus != 2 && idNrStatus != 3 && idNrStatus != 4 && idNrStatus != 5 && idNrStatus != 6 && idNrStatus != 7 && idNrStatus != 8 && idNrStatus != 9;
        HomologacaoDao homologacaoDB = new HomologacaoDao();
        List<Agendamento> agendamentos;
        List<Horarios> horarios;
        if (idNrStatus == 1 || idNrStatus == 6) {
            horarios = homologacaoDB.pesquisaTodosHorariosDisponiveis(macFilial.getFilial().getId(), diaDaSemana);
            for (int j = 0; j < horarios.size(); j++) {
                ListaAgendamento listaAgendamento = new ListaAgendamento();
                if (idNrStatus == 1) {
                    int quantidade = homologacaoDB.pesquisaQntdDisponivel(macFilial.getFilial().getId(), horarios.get(j), getData());
                    if (quantidade == -1) {
                        GenericaMensagem.error("Erro", "Não foi possível pesquisar horários disponíveis!");
                        break;
                    }
                    if (quantidade > 0) {
                        listaAgendamento.setQuantidade(quantidade);
                        listaAgendamento.getAgendamento().setHorarios(horarios.get(j));
                        listaHorarios.add(listaAgendamento);
                    }
                } else {
                    listaAgendamento.getAgendamento().setHorarios(horarios.get(j));
                    listaHorarios.add(listaAgendamento);
                }
            }
        } else {
            Date d = getData();
            if (idNrStatus == 8) {
                d = null;
            }
            agendamentos = homologacaoDB.pesquisaAgendamento(idNrStatus, macFilial.getFilial().getId(), d, null, 0, 0, 0, false, false);
            for (Agendamento agenda : agendamentos) {
                ListaAgendamento listaAgendamento = new ListaAgendamento();
                Usuario u = new Usuario();
                listaAgendamento.setAgendamento(agenda);

                if (agenda.getAgendador() == null) {
                    listaAgendamento.setUsuarioAgendador("** Web User **");
                } else {
                    listaAgendamento.setUsuarioAgendador(agenda.getAgendador().getPessoa().getNome());
                }

                AtendimentoDao dbat = new AtendimentoDao();
                if (dbat.pessoaOposicao(listaAgendamento.getAgendamento().getPessoaEmpresa().getFisica().getPessoa().getDocumento())) {
                    listaAgendamento.setTblEstilo("tblAgendamentoOposicaox");
                }

                listaHorarios.add(listaAgendamento);
            }
        }
    }

    public void salvarTransferencia() {
        Agendamento a = (Agendamento) new Dao().find(agendamento);
        String beforeUpdate = ""
                + " ID: " + a.getId()
                + " - Funcionário { " + a.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + a.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                + " - Empresa { " + a.getPessoaEmpresa().getJuridica().getPessoa().getId() + " - Nome: " + a.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " } "
                + " - Admissão: " + a.getPessoaEmpresa().getAdmissao()
                + " - Demissão: " + a.getPessoaEmpresa().getDemissao()
                + " - Data da homologação: " + a.getData()
                + " - Horário: " + a.getHorarios().getHora();
        if (getDataTransferencia().getDay() == 6 || getDataTransferencia().getDay() == 0) {
            GenericaMensagem.warn("Atenção", "Fins de semana não permitido!");
            return;
        }
        if (DataHoje.converteDataParaInteger(DataHoje.converteData(getDataTransferencia()))
                < DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
            GenericaMensagem.warn("Atenção", "Data anterior ao dia de hoje!");
            return;
        }

        if (DataHoje.converteDataParaInteger(((new DataHoje()).incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data())))
                < DataHoje.converteDataParaInteger(DataHoje.converteData(getDataTransferencia()))) {
            GenericaMensagem.warn("Atenção", "Data maior que " + registro.getHomolocaoLimiteMeses() + " meses!");
            return;
        }

        Dao dao = new Dao();
        agendamento.setDtData(dataTransferencia);
        if (listaHorarioTransferencia.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existem horários para esse dia da semana!");
            return;
        }

        NovoLog novoLog = new NovoLog();
        Horarios h = (Horarios) dao.find(new Horarios(), Integer.valueOf(listaHorarioTransferencia.get(idHorarioTransferencia).getDescription()));
        agendamento.setHorarios(h);

        if (renderedFilial) {
            agendamento.setFilial(h.getFilial());
        }

        dao.openTransaction();
        agendamento.setAgendador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
        if (!dao.update(agendamento)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível transferir horário, tente novamente!");
            return;
        }
        dao.commit();
        novoLog.setTabela("hom_agendamento");
        novoLog.setCodigo(agendamento.getId());
        novoLog.update(
                beforeUpdate,
                " ID: " + agendamento.getId()
                + " - Funcionário { " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                + " - Empresa { " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " } "
                + " - Admissão: " + agendamento.getPessoaEmpresa().getAdmissao()
                + " - Demissão: " + agendamento.getPessoaEmpresa().getDemissao()
                + " - Data da homologação: " + agendamento.getData()
                + " - Horário: " + agendamento.getHorarios().getHora());
        GenericaMensagem.info("Sucesso", "Horário transferido!");
        GlobalSync.load();
        loadListaHorarios();
        PF.closeDialog("dlg_transferir_horario");
    }

    public String imprimirPlanilha() {
        if (listaMovimento.isEmpty()) {
            return null;
        }

        List<ListMovimentoReceber> listMovimentoReceber = PlanilhaDebitoBean.find(juridica.getPessoa().getId());
        for (int i = 0; i < listaMovimento.size(); i++) {
            for (int x = 0; x < listMovimentoReceber.size(); x++) {
                if (listaMovimento.get(i).getId() == Integer.parseInt(listMovimentoReceber.get(x).getIdMovimento())) {
                    listMovimentoReceber.get(x).setSelected(true);
                }
            }
        }
        PlanilhaDebitoBean.printNoNStatic(listMovimentoReceber);
        return null;
    }

    public String enviarPlanilha() {
        if (listaMovimento.isEmpty()) {
            return null;
        }
        ImprimirBoleto imp = new ImprimirBoleto();
        List<Movimento> lista = new ArrayList();
        List<Double> listaValores = new ArrayList<>();
        Dao dao = new Dao();
        for (Movimento listaMovimento1 : listaMovimento) {
            lista.add(listaMovimento1);
            listaValores.add(listaMovimento1.getValor());
        }

        if (!lista.isEmpty()) {
            imp.imprimirPlanilha(lista, listaValores, false, false);
        }

        try {
            Pessoa pessoa;
            if (enviarPara.equals("contabilidade")) {
                if (juridica.getContabilidade() == null) {
                    GenericaMensagem.warn("Atenção", "Empresa sem Contabilidade!");
                    return null;
                }

                pessoa = (Pessoa) dao.find(juridica.getContabilidade().getPessoa());
                if (emailEmpresa.isEmpty()) {
                    if (pessoa.getEmail1().isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Contabilidade sem Email para envio!");
                        return null;
                    }
                } else {
                    pessoa.setEmail1(emailEmpresa);
                }
            } else {
                pessoa = (Pessoa) dao.find(juridica.getPessoa());
                if (emailEmpresa.isEmpty()) {
                    if (pessoa.getEmail1().isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Empresa sem Email para envio!");
                        return null;
                    }
                } else {
                    pessoa.setEmail1(emailEmpresa);
                }
            }

            String nome = imp.criarLink(pessoa, registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            List<Pessoa> p = new ArrayList();
            p.add(pessoa);
            Mail mail = new Mail();
            String assinatura = "";
            ConfiguracaoDepartamento configuracaoDepartamento = new ConfiguracaoDepartamentoDao().findBy(8, MacFilial.getAcessoFilial().getFilial().getId());
            if (configuracaoDepartamento != null) {
                mail.setConfiguracaoDepartamento(configuracaoDepartamento);
            }
            Email email = new Email(
                    -1,
                    DataHoje.dataHoje(),
                    DataHoje.livre(new Date(), "HH:mm"),
                    (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                    (Rotina) dao.find(new Rotina(), 113),
                    null,
                    "Envio de Débitos",
                    "",
                    false,
                    false
            );

            if (!registro.isEnviarEmailAnexo()) {
                email.setMensagem(""
                        + " <div style='background:#00ccff; padding: 15px; font-size:13pt'>"
                        + " Envio cadastrado para <b>" + pessoa.getNome() + " </b>"
                        + " </div>"
                        + " <br />"
                        + " <h5>Visualize sua planilha de débitos clicando no link abaixo</h5>"
                        + " <br /><br />"
                        + " <a href='" + registro.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "'>Clique aqui para abrir Planilha de Débitos</a><br />"
                        + assinatura
                );
            } else {
                List<File> fls = new ArrayList<>();
                fls.add(new File(imp.getPathPasta() + "/" + nome));
                mail.setFiles(fls);
                email.setMensagem(""
                        + " <div style='background:#00ccff; padding: 15px; font-size:13pt'>         "
                        + "     Envio cadastrado para <b>" + pessoa.getNome() + " </b>              "
                        + " </div><br />                                                            "
                        + " <h5>Baixe sua planilha de débitos anexado neste email</h5><br /><br />   "
                        + assinatura
                );
            }
            mail.setEmail(email);
            List<EmailPessoa> emailPessoas = new ArrayList<>();
            EmailPessoa emailPessoa = new EmailPessoa();
            List<Pessoa> pessoas = (List<Pessoa>) p;
            for (Pessoa p1 : pessoas) {
                emailPessoa.setDestinatario(p1.getEmail1());
                emailPessoa.setPessoa(p1);
                emailPessoa.setRecebimento(null);
                emailPessoas.add(emailPessoa);
                mail.setEmailPessoas(emailPessoas);
                emailPessoa = new EmailPessoa();
            }
            String[] retorno = mail.send("personalizado");

            if (!retorno[1].isEmpty()) {
                GenericaMensagem.error("Erro", retorno[1]);
            } else {
                GenericaMensagem.info("OK", retorno[0]);
            }
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());

        }
        return null;
    }

    public String atualizar() {
        return "agendamento";
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            Dao dao = new Dao();
            List<Status> list = (List<Status>) dao.list(new Status());
            for (int i = 0; i < list.size(); i++) {
                listaStatus.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        return listaStatus;
    }

    public List<SelectItem> getListaDemissao() {
        if (listaDemissao.isEmpty()) {
            List<Demissao> list = (List<Demissao>) new Dao().list(new Demissao());
            for (int i = 0; i < list.size(); i++) {
                listaDemissao.add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
            }
        }
        return listaDemissao;
    }

    public List<SelectItem> getListaHorarioTransferencia() {
        return listaHorarioTransferencia;
    }

    public boolean pesquisarFeriado() {
        FeriadosDao feriadosDao = new FeriadosDao();
        if (macFilial.getFilial().getFilial().getId() != -1) {
            List<Feriados> feriados = feriadosDao.pesquisarPorDataFilialEData(DataHoje.converteData(data), macFilial.getFilial());
            if (!feriados.isEmpty()) {
                return true;
            } else {
                List<Feriados> listFeriados = (List<Feriados>) feriadosDao.pesquisarPorData(DataHoje.converteData(getData()));
                if (!listFeriados.isEmpty()) {
                    for (Feriados f : listFeriados) {
                        if (f.getCidade() == null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    public void agendar(Agendamento a) {
        listFiles = new ArrayList();
        if (a.getId() != -1) {
            getListFiles();
        }
        limpar();
        loadListaHorariosTransferencia();

        if (idStatus != 8) {
            if (getData().getDay() == 6 || getData().getDay() == 0) {
                GenericaMensagem.warn("Atenção", "Fins de semana não permitido!");
                return;
            }
        }

        emailEmpresa = "";
        idMotivoDemissao = 0;
        int nrAgendamentoRetroativo = DataHoje.converteDataParaInteger(DataHoje.converteData(registro.getAgendamentoRetroativo()));
        int nrData = DataHoje.converteDataParaInteger(DataHoje.converteData(getData()));
        int nrDataHoje = DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()));
        if (nrAgendamentoRetroativo < nrDataHoje) {
            if (nrData < nrDataHoje) {
                GenericaMensagem.warn("Atenção", "Data anterior ao dia de hoje!");
                return;
            }
        } else {
            GenericaMensagem.info("Informação", "Agendamento retroativo liberado até dia " + registro.getAgendamentoRetroativoString());
        }

        if (DataHoje.converteDataParaInteger(((new DataHoje()).incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data()))) < nrData) {
            GenericaMensagem.warn("Atenção", "Data maior que " + registro.getHomolocaoLimiteMeses() + " meses!");
            return;
        }
        protocolo = 0;
        if (profissao.getId() == -1) {
            profissao = (Profissao) new Dao().find(new Profissao(), 0);
            profissao.setProfissao("");
        }

        switch (idStatus) {
            case 1: {
                HorarioReservaDao hrd = new HorarioReservaDao();
                HomologacaoDao dba = new HomologacaoDao();
                hrd.exists(nrDataHoje);
                int quantidade_reservada = hrd.count(a.getHorarios().getId());
                int quantidade = dba.pesquisaQntdDisponivel(macFilial.getFilial().getId(), a.getHorarios(), getData());
                int quantidade_resultado = quantidade - quantidade_reservada;
                if (quantidade == -1) {
                    GenericaMensagem.error("Sistema", "Este horário não esta mais disponivel! (reservado ou já agendado)");
                    return;
                }
                if (quantidade_resultado < 0 && quantidade != 1) {
                    GenericaMensagem.error("Sistema", "Este horário não esta mais disponivel! (reservado ou já agendado)");
                    return;
                }
                hrd.begin();
                if (getData() == null) {
                    GenericaMensagem.warn("Atenção", "Selecione uma data para Agendamento!");
                    return;
                } else {
                    renderCancelarHorario = false;
                    renderCancelar = true;
                    if (pesquisarFeriado()) {
                        GenericaMensagem.warn("Atenção", "Esta data esta cadastrada como Feriado!");
                        return;
                    } else {
                        agendamento.setData(DataHoje.converteData(getData()));
                        Horarios horarios = a.getHorarios();
                        agendamento.setHorarios(horarios);
                    }
                }
                visibleModal = true;
                hrd.reserve(a.getHorarios().getId());
                GlobalSync.load();
                this.loadListaHorarios();
                WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                PF.openDialog("dlg_agendamento");
                break;
            }
            case 2:
            case 8:
            case 9:
                PessoaEnderecoDao db = new PessoaEnderecoDao();
                agendamento = a;
                fisica = a.getPessoaEmpresa().getFisica();
                documentoFisica = fisica.getPessoa().getDocumento();
                enderecoFisica = db.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
                juridica = a.getPessoaEmpresa().getJuridica();
                loadStatusEmpresa();
                loadEmpresaEndereco();
                profissao = a.getPessoaEmpresa().getFuncao();
                pessoaEmpresa = a.getPessoaEmpresa();
                renderCancelarHorario = true;
                renderCancelar = false;
                for (int i = 0; i < getListaDemissao().size(); i++) {
                    if (Integer.parseInt(getListaDemissao().get(i).getDescription()) == a.getDemissao().getId()) {
                        idMotivoDemissao = (Integer) getListaDemissao().get(i).getValue();
                        break;
                    }
                }
                //tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());

                WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                if (idStatus == 8 || idStatus == 9) {
                    validacao = true;
                }
                visibleModal = true;
                PF.openDialog("dlg_agendamento");
                PF.update(":formConcluirAgendamento:");
                break;
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
            case 6: {
                if (getData() == null) {
                    GenericaMensagem.warn("Atenção", "Selecione uma data para Agendamento!");
                    return;
                } else {
                    renderCancelarHorario = false;
                    renderCancelar = true;
                    if (pesquisarFeriado()) {
                        GenericaMensagem.warn("Atenção", "Esta data esta cadastrada como Feriado!");
                        return;
                    } else {
                        agendamento.setData(DataHoje.converteData(getData()));
                        agendamento.setHorarios(a.getHorarios());
                    }
                }

                visibleModal = true;
                PF.openDialog("dlg_agendamento");
                break;
            }
        }

        if (agendamento.getTelefone().length() > 14) {
            tipoTelefone = "celular";
        } else {
            tipoTelefone = "telefone";
        }
    }

    public void save() {
        save("");
    }

    public void save(String tcase) {
        NovoLog novoLog = new NovoLog();

        if (tcase.equals("validar")) {
            validar();
            WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
            GenericaSessao.remove("menuPrincipalBean");
            return;
        }
        if (tcase.equals("solicitar_pendencias") || tcase.equals("recusar") || tcase.equals("segunda_recusa")) {
            recusar(tcase);
            GenericaSessao.remove("menuPrincipalBean");
            WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
            motivoRecusaDireto = "";
            motivoRecusa1 = "";
            motivoRecusa2 = "";
            return;
        }

        if (!validaAdmissao()) {
            return;
        }

        if (!validaDemissao()) {
            return;
        }
        styleDestaque = "";
        Dao dao = new Dao();
        if (configuracaoHomologacao.getValidaNome()) {
            if (fisica.getPessoa().getNome().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Digite o nome do Funcionário!");
                return;
            }
        }

        if (configuracaoHomologacao.getValidaDataNascimento()) {
            if (fisica.getNascimento().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar data de nascimento!");
                return;
            }
        }

        if (configuracaoHomologacao.getValidaFuncao()) {
            if (profissao.getId().equals(-1) || profissao.getId().equals(0)) {
                GenericaMensagem.warn("Atenção", "Informar a Função!");
                return;
            }
        }

        if (configuracaoHomologacao.getValidaCarteira()) {
            if (fisica.getCarteira().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar a Carteira!");
                return;
            }
        }

        if (configuracaoHomologacao.getValidaSerie()) {
            if (fisica.getSerie().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar a Série!");
                return;
            }
        }

        if (isContribuinte()) {
            GenericaMensagem.error("Atenção", getContribuinte());
            return;
        }

        FisicaDao dbFis = new FisicaDao();
        List listDocumento;
        imprimirPro = false;
        DataHoje dataH = new DataHoje();
        Demissao demissao = (Demissao) dao.find(new Demissao(), Integer.parseInt(((SelectItem) getListaDemissao().get(idMotivoDemissao)).getDescription()));

        if (!pessoaEmpresa.getAdmissao().isEmpty() && pessoaEmpresa.getAdmissao() != null) {
            if (DataHoje.converteDataParaInteger(pessoaEmpresa.getAdmissao())
                    > DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())) {
                GenericaMensagem.warn("Atenção", "Data de Admissão é maior que data de Demissão!");
                return;
            }
        } else {
            GenericaMensagem.warn("Atenção", "Data de Admissão é obrigatória!");
            return;
        }

        if (!pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getDemissao() != null) {
            switch (demissao.getId()) {
                case 1:
                    if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                            > DataHoje.converteDataParaInteger(dataH.incrementarMeses(1, DataHoje.data()))) {
                        GenericaMensagem.warn("Atenção", "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 30 dias!");
                        return;
                    }
                    break;
                case 2:
                    if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                            > DataHoje.converteDataParaInteger(dataH.incrementarMeses(3, DataHoje.data()))) {
                        GenericaMensagem.warn("Atenção", "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 90 dias!");
                        return;
                    }
                    break;
                case 3:
                    if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                            > DataHoje.converteDataParaInteger(dataH.incrementarDias(10, DataHoje.data()))) {
                        GenericaMensagem.warn("Atenção", "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 10 dias!");
                        return;
                    }
                    break;
                default:
                    break;
            }
        } else {
            GenericaMensagem.warn("Atenção", "Data de Demissão é obrigatória!");
            return;
        }

        // SALVAR FISICA -----------------------------------------------
        fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
        if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
            GenericaMensagem.warn("Atenção", "Documento Inválido!");
            return;
        }

        dao.openTransaction();
        Fisica f = (Fisica) dao.find(new Fisica(), fisica.getId());
        if (f == null) {
            fisica.setId(-1);
        }
        if (fisica.getId() == -1) {
            if (!dbFis.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(),
                    fisica.getDtNascimento(),
                    fisica.getRg()).isEmpty()) {
                GenericaMensagem.warn("Atenção", "Esta pessoa já esta cadastrada! Cadastro duplicado.");
                dao.rollback();
                return;
            }
            listDocumento = dbFis.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            if (!listDocumento.isEmpty()) {
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Documento já existente!");
                return;
            }

            if (dao.save(fisica.getPessoa())) {
                dao.save(fisica);
            } else {
                dao.rollback();
                GenericaMensagem.error("Erro", "Erro ao inserir Pessoa!");
                return;
            }
        } else {
            listDocumento = dbFis.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            for (Object listDocumento1 : listDocumento) {
                if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != fisica.getId()) {
                    dao.rollback();
                    GenericaMensagem.warn("Atenção", "Documento já existente!");
                    return;
                }
            }
            List<Fisica> fisi = dbFis.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(),
                    fisica.getDtNascimento(),
                    fisica.getRg());
            if (!fisi.isEmpty()) {
                for (Fisica fisi1 : fisi) {
                    if (fisi1.getId() != fisica.getId()) {
                        dao.rollback();
                        GenericaMensagem.warn("Atenção", "Esta pessoa já esta cadastrada! Cadastro duplicado.");
                        return;
                    }
                }
            }
            if (dao.update(fisica.getPessoa())) {
                if (dao.update(fisica)) {
                } else {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível inserir Pessoa Física, tente novamente!");
                    return;
                }
            } else {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível inserir Pessoa, tente novamente!");
                return;
            }
        }
        HomologacaoDao dba = new HomologacaoDao();
        Agendamento age = dba.pesquisaFisicaAgendada(fisica.getId(), juridica.getId());
        if (age != null && agendamento.getId() == -1) {
            dao.rollback();
            GenericaMensagem.warn("Atenção", "Pessoa já foi agendada, na data " + age.getData());
            return;
        }
        if (agendamento.getId() == -1) {
            if (!configuracaoHomologacao.getAgendarMesmoHorarioEmpresa()) {
                List list = dba.findByDataHorarioEmpresa(agendamento.getDtData(), agendamento.getHorarios().getId(), juridica.getId());
                if (list.size() == 1) {
                    dao.rollback();
                    GenericaMensagem.error("Sistema", "Só é possível agendar um horário por empresa, nesta data!");
                    return;
                }
            }
        }
        boolean isOposicao = false;
        AtendimentoDao dbat = new AtendimentoDao();
        if (dbat.pessoaOposicao(fisica.getPessoa().getDocumento())) {
            isOposicao = true;
        }

        PessoaEnderecoDao dbp = new PessoaEnderecoDao();

        if (enderecoFisica.getId() == -1) {
            if (enderecoFisica.getEndereco().getId() != -1) {
                enderecoFisica.setPessoa(fisica.getPessoa());
                PessoaEndereco pesEnd = enderecoFisica;
                Object ids[] = {1, 3, 4};
                for (int i = 0; i < ids.length; i++) {
                    pesEnd.setTipoEndereco((TipoEndereco) dao.find(new TipoEndereco(), ids[i]));
                    if (!dao.save(pesEnd)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível inserir Pessoa Endereço, tente novamente!");
                        return;
                    }
                    pesEnd = new PessoaEndereco();
                    pesEnd.setComplemento(enderecoFisica.getComplemento());
                    pesEnd.setEndereco(enderecoFisica.getEndereco());
                    pesEnd.setNumero(enderecoFisica.getNumero());
                    pesEnd.setPessoa(enderecoFisica.getPessoa());
                }
            }
        } else {
            List<PessoaEndereco> ends = dbp.pesquisaEndPorPessoa(fisica.getPessoa().getId());
            for (PessoaEndereco end : ends) {
                end.setComplemento(enderecoFisica.getComplemento());
                end.setEndereco(enderecoFisica.getEndereco());
                end.setNumero(enderecoFisica.getNumero());
                end.setPessoa(enderecoFisica.getPessoa());
                if (!dao.update(end)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível atualizar Pessoa Endereço, tente novamente!");
                    return;
                }
            }
        }
        // -------------------------------------------------------------
        int idStatusI = idStatus;
        if (profissao.getId() == -1) {
            profissao = (Profissao) new Dao().find(new Profissao(), 0);
            profissao.setProfissao("");
        }

        switch (idStatusI) {
            case 1: {
                pessoaEmpresa.setFisica(fisica);
                pessoaEmpresa.setJuridica(juridica);
                pessoaEmpresa.setFuncao(profissao);
                pessoaEmpresa.setPrincipal(false);
                //pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
                agendamento.setDemissao(demissao);
                agendamento.setHomologador(null);
                agendamento.setPessoaEmpresa(pessoaEmpresa);
                agendamento.setStatus((Status) dao.find(new Status(), 2));
                break;
            }
            case 2: {
                pessoaEmpresa.setFisica(fisica);
                pessoaEmpresa.setJuridica(juridica);
                //pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
                pessoaEmpresa.setFuncao(profissao);
                agendamento.setDemissao(demissao);
                agendamento.setPessoaEmpresa(pessoaEmpresa);
                break;
            }
            case 6: {
                pessoaEmpresa.setFisica(fisica);
                pessoaEmpresa.setJuridica(juridica);
                pessoaEmpresa.setFuncao(profissao);
                //pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
                agendamento.setDemissao(demissao);
                agendamento.setHomologador(null);
                agendamento.setPessoaEmpresa(pessoaEmpresa);
                agendamento.setStatus((Status) dao.find(new Status(), 2));
                break;
            }
        }
        if (pessoaEmpresa.getId() == -1) {
            if (!dao.save(pessoaEmpresa)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível inserir Pessoa Empresa!");
                return;
            }
        } else if (!dao.update(pessoaEmpresa)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível alterar Pessoa Empresa!");
            return;
        }
        List listRelacao = new RelacaoEmpregadosDao().findNotSendingByPessoa(agendamento.getPessoaEmpresa().getJuridica().getId());
        if (!listRelacao.isEmpty()) {
            GenericaMensagem.warn("Erro", "Empresa não entregou relação de empregados no período específicado!");
            return;
        }
        if (configuracaoHomologacao.getValidaContato()) {
            if (agendamento.getContato().isEmpty()) {
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Informar o nome do Contato!");
                return;
            }
        }
        if (configuracaoHomologacao.getValidaTelefone()) {
            if (agendamento.getTelefone().isEmpty()) {
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Informar o telefone para contato!");
                return;
            }
        }
        if (configuracaoHomologacao.getValidaEmail()) {
            if (agendamento.getEmail().isEmpty()) {
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Informar o email!");
                return;
            }
        }

        if (agendamento.getId() == -1) {

            ConvencaoDao convencaoDao = new ConvencaoDao();
            Convencao convencao = convencaoDao.findByEmpresa(pessoaEmpresa.getJuridica().getPessoa().getId());
            if (convencao == null) {
                GenericaMensagem.warn("Mensagem", "NENHUMA CONVENÇÃO ENCONTRADA PARA ESTA EMPRESA!");
                dao.rollback();
                return;
            }

            agendamento.setNoPrazo(new FunctionsDao().homologacaoPrazo(pessoaEmpresa.isAvisoTrabalhado(), enderecoEmpresa.getEndereco().getCidade().getId(), pessoaEmpresa.getDemissao(), convencao.getId()));
            if (idStatusI == 2) {
                if (!dba.existeHorarioDisponivel(agendamento.getDtData(), agendamento.getHorarios())) {
                    dao.rollback();
                    loadListaHorariosTransferencia();
                    GenericaMensagem.fatal("Atenção", "Não existe mais disponibilidade para o horário agendado!");
                    ocultarHorarioAlternativo = false;
                    return;
                }
            }
            agendamento.setFilial(macFilial.getFilial());
            agendamento.setAgendador((Usuario) GenericaSessao.getObject("sessaoUsuario")); // USUARIO SESSAO
            agendamento.setRecepcao(null);
            agendamento.setDtEmissao(DataHoje.dataHoje());
            if (dao.save(agendamento)) {
                dao.commit();
//                msgConfirma = "Para imprimir Protocolo clique aqui! ";
                GenericaMensagem.info("Sucesso", "Agendamento Concluído!");
                if (agendamento.isNoPrazo() == false) {
                    GenericaMensagem.info("Mensagem", "DE ACORDO COM AS INFORMAÇÕES ACIMA PRESTADAS SEU AGENDAMENTO ESTÁ FORA DO PRAZO PREVISTO EM CONVENÇÃO COLETIVA.");
                }
                novoLog.setTabela("hom_agendamento");
                novoLog.setCodigo(agendamento.getId());
                novoLog.save(
                        " ID: " + agendamento.getId()
                        + " - Funcionário { " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                        + " - Empresa { " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " } "
                        + " - Admissão: " + agendamento.getPessoaEmpresa().getAdmissao()
                        + " - Demissão: " + agendamento.getPessoaEmpresa().getDemissao()
                        + " - Data da homologação: " + agendamento.getData()
                        + " - Horário: " + agendamento.getHorarios().getHora());
                GlobalSync.load();
                WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
            } else {
                GenericaMensagem.fatal("Atenção", "Erro ao realizar este Agendamento!");
                dao.rollback();
            }
        } else {
            Agendamento a = (Agendamento) new Dao().find(agendamento);
            String beforeUpdate = ""
                    + " ID: " + a.getId()
                    + " - Funcionário { " + a.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + a.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                    + " - Empresa { " + a.getPessoaEmpresa().getJuridica().getPessoa().getId() + " - Nome: " + a.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " } "
                    + " - Admissão: " + a.getPessoaEmpresa().getAdmissao()
                    + " - Demissão: " + a.getPessoaEmpresa().getDemissao()
                    + " - Data da homologação: " + a.getData()
                    + " - Horário: " + a.getHorarios().getHora();

            if (dao.update(agendamento)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Agendamento atualizado!");
                novoLog.setTabela("hom_agendamento");
                novoLog.setCodigo(agendamento.getId());
                novoLog.update(
                        beforeUpdate,
                        " ID: " + agendamento.getId()
                        + " - Funcionário { " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                        + " - Empresa { " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " } "
                        + " - Admissão: " + agendamento.getPessoaEmpresa().getAdmissao()
                        + " - Demissão: " + agendamento.getPessoaEmpresa().getDemissao()
                        + " - Data da homologação: " + agendamento.getData()
                        + " - Horário: " + agendamento.getHorarios().getHora());
                if (isOposicao) {
                    //msgConfirma = "Agendamento atualizado com Sucesso! imprimir Protocolo clicando aqui! Pessoa cadastrada em oposição. ";
                    styleDestaque = "color: red; font-size: 14pt; font-weight:bold";
                } else {
                    //msgConfirma = "Agendamento atualizado com Sucesso! imprimir Protocolo clicando aqui! ";
                    styleDestaque = "";
                }
            } else {
                GenericaMensagem.fatal("Atenção", "Erro ao atualizar Agendamento!");
                dao.rollback();
            }
        }

        renderCancelarHorario = true;
        loadListaHorariosTransferencia();
        GlobalSync.load();
        loadListaHorarios();
        id_protocolo = agendamento.getId();
        ocultarHorarioAlternativo = true;
        imprimirPro = true;
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
        renderedFilial = false;
    }

    public String salvarMais() {

        return null;
    }

    public String cancelar() {
        strEndereco = "";
        //tipoAviso = "true";
        fisica = new Fisica();
        documentoFisica = "";
        cepEndereco = "";
        listaEnderecos.clear();
        agendamento = new Agendamento();
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        juridica = new Juridica();
        listaMovimento.clear();
        enderecoFisica = new PessoaEndereco();
        imprimirPro = false;
        loadListaHorariosTransferencia();
        emailEmpresa = "";
        return "agendamento";
    }

    public String cancelarHorario() {
        PessoaEmpresaDao dbPesEmp = new PessoaEmpresaDao();
        Dao dao = new Dao();
        agendamento.setStatus((Status) dao.find(new Status(), 3));
        dao.openTransaction();
        if (!dao.update(agendamento)) {
            GenericaMensagem.warn("Erro", "Ao cancelar horário!");
            dao.rollback();
            return "agendamento";
        }
        cancelamento.setData(DataHoje.data());
        cancelamento.setUsuario(new PessoaUtilitarios().getUsuarioSessao());
        cancelamento.setAgendamento(agendamento);
        if (!dao.save(cancelamento)) {
            GenericaMensagem.warn("Erro", "Ao cancelar horário!");
            dao.rollback();
            return "agendamento";
        }
        GenericaMensagem.info("Sucesso", "Agendamento Cancelado!");
        dao.commit();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("hom_cancelamento");
        novoLog.setCodigo(cancelamento.getId());
        novoLog.delete(
                "Cancelamento de homologação : "
                + " - ID do cancelamento: " + cancelamento.getId()
                + " - Agendamento {ID: " + cancelamento.getAgendamento().getId() + "} "
                + " - Funcionário { " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getId() + " - Nome: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getNome() + " } "
                + " - Data do cancelamento: " + cancelamento.getData()
                + " - Motivo: " + cancelamento.getMotivo());

        cancelamento = new Cancelamento();
        pessoaEmpresa.setDtDemissao(null);

        PessoaEmpresa pem = dbPesEmp.pesquisaPessoaEmpresaPorFisica(pessoaEmpresa.getFisica().getId());

        if (pem.getId() == -1) {
            pessoaEmpresa.setPrincipal(true);
        }
        dao.update(pessoaEmpresa, true);
        strEndereco = "";
        renderCancelarHorario = false;
        renderCancelar = true;
        //tipoAviso = "true";
        fisica = new Fisica();
        documentoFisica = "";
        agendamento = new Agendamento();
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        juridica = new Juridica();
        listaMovimento.clear();
        enderecoFisica = new PessoaEndereco();

        setVisibleModal(false);
        GlobalSync.load();
        loadListaHorarios();
        loadListaHorariosTransferencia();
        return "agendamento";
    }

    public void limpar() {
        strEndereco = "";
        //tipoAviso = "true";
        fisica = new Fisica();
        documentoFisica = "";
        pessoaEmpresa = new PessoaEmpresa();
        agendamento = new Agendamento();
        profissao = new Profissao();
        juridica = new Juridica();
        listaMovimento.clear();
        protocolo = 0;
        enderecoFisica = new PessoaEndereco();
    }

    public void limparMais() {
        strEndereco = "";
        //tipoAviso = "true";
        fisica = new Fisica();
        documentoFisica = "";
        pessoaEmpresa = new PessoaEmpresa();
        agendamento = new Agendamento();
        profissao = new Profissao();
        protocolo = 0;
        enderecoFisica = new PessoaEndereco();
    }

    public void pesquisarFuncionarioCPF() throws IOException {
        if (agendamento.getId() == -1) {
            styleDestaque = "";

            if (documentoFisica.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar CPF!");
                return;
            }

            if (!documentoFisica.isEmpty() && documentoFisica.equals("___.___.___-__")) {
                GenericaMensagem.warn("Atenção", "Informar CPF!");
                return;
            }

            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(documentoFisica))) {
                GenericaMensagem.warn("Atenção", "Documento Inválido!");
                return;
            }

            if (fisica.getId() != -1 && fisica.getPessoa().getDocumento().equals(documentoFisica)) {
                return;
            }

            fisica.getPessoa().setDocumento(documentoFisica);
            //fisica = new Fisica();
            FisicaDao dbFis = new FisicaDao();
            HomologacaoDao db = new HomologacaoDao();
            PessoaEnderecoDao dbe = new PessoaEnderecoDao();

            String documento = documentoFisica;

            List<Fisica> listFisica = dbFis.pesquisaFisicaPorDocSemLike(documento);

            if (listFisica.isEmpty()) {
                if (!fisica.getPessoa().getNome().isEmpty() && !fisica.getNascimento().isEmpty()) {
                    Fisica f = (Fisica) dbFis.pesquisaFisicaPorNomeNascimento(fisica.getPessoa().getNome().trim(), fisica.getDtNascimento());
                    if (f != null) {
                        listFisica.add(f);
                    }
                }
            }

            List<Oposicao> listao = db.pesquisaFisicaOposicaoSemEmpresa(documento);
            PessoaEmpresa pem = db.pesquisaPessoaEmpresaPertencente(documento);

            if (!listFisica.isEmpty()) {
                // AQUI
                //                Agendamento age = db.pesquisaFisicaAgendada(listFisica.get(0).getId());
                //                if (age != null) {
                //                    msgConfirma = "CPF já foi agendado:" + age.getData() + " às " + age.getHorarios().getHora() + " h(s) ";
                //                    return;
                //                }
            }

            // SEM PESSOA FISICA E SEM OPOSICAO
            if (listFisica.isEmpty() && listao.isEmpty()) {
                //FacesContext.getCurrentInstance().getExternalContext().redirect("/Sindical/agendamento.jsf");
                //msgConfirma = "CPF verificado com sucesso";
                // SEM PESSOA FISICA E COM OPOSICAO    
            } else if (listFisica.isEmpty() && !listao.isEmpty()) {
                GenericaMensagem.warn("Atenção", "CPF cadastrado em oposição data: " + listao.get(0).getEmissao());

                styleDestaque = "color: red; font-size: 14pt; font-weight:bold";
                fisica.getPessoa().setNome(listao.get(0).getOposicaoPessoa().getNome());
                fisica.setRg(listao.get(0).getOposicaoPessoa().getRg());
                fisica.setSexo(listao.get(0).getOposicaoPessoa().getSexo());
                fisica.getPessoa().setDocumento(documento);
                juridica = listao.get(0).getJuridica();

                PF.openDialog("dlg_oposicao");
                // COM FISICA, COM PESSOA EMPRESA E SEM OPOSICAO    
            } else if (!listFisica.isEmpty() && pem != null && listao.isEmpty()) {
                //msgConfirma = "CPF verificado com sucesso";
                pessoaEmpresa = pem;
                fisica = pessoaEmpresa.getFisica();
                profissao = (pessoaEmpresa.getFuncao() == null) ? new Profissao() : pessoaEmpresa.getFuncao();
                GenericaSessao.put("juridicaPesquisa", pessoaEmpresa.getJuridica());
                enderecoFisica = dbe.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 3);
                protocolo = 0;
                //FacesContext.getCurrentInstance().getExternalContext().redirect("/Sindical/agendamento.jsf");
                // COM FISICA, SEM PESSOA EMPRESA E SEM OPOSICAO    
            } else if (!listFisica.isEmpty() && pem == null && listao.isEmpty()) {
                //msgConfirma = "CPF verificado com sucesso";
                fisica = listFisica.get(0);
                fisica.getPessoa().setDocumento(documento);
                pessoaEmpresa = new PessoaEmpresa();
                juridica = new Juridica();
                enderecoFisica = dbe.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 3);
                //FacesContext.getCurrentInstance().getExternalContext().redirect("/Sindical/agendamento.jsf");
                // COM FISICA, COM PESSOA EMPRESA COM OPOSICAO
            } else if (!listFisica.isEmpty() && pem != null && !listao.isEmpty()) {
                GenericaMensagem.warn("Atenção", "CPF cadastrado em oposição data: " + listao.get(0).getEmissao());

                styleDestaque = "color: red; font-size: 14pt; font-weight:bold";
                pessoaEmpresa = pem;
                fisica = pessoaEmpresa.getFisica();
                profissao = (pessoaEmpresa.getFuncao() == null) ? new Profissao() : pessoaEmpresa.getFuncao();
                enderecoFisica = dbe.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 3);
                protocolo = 0;
                // FUNCIONARIO JA FOI AGENDADO
                //                if (db.pesquisaFisicaAgendada(fisica.getId()) != null){
                //
                //                }
                // EMPRESA É A MESMA DA OPOSICAO
                if (pessoaEmpresa.getJuridica().getId() == listao.get(0).getJuridica().getId()) {
                    juridica = pessoaEmpresa.getJuridica();
                } else {
                    juridica = listao.get(0).getJuridica();
                    // FUNCIONARIO SEM DATA DE DEMISSAO
                    //                    if (pessoaEmpresa.getDemissao().isEmpty()){
                    //                        if ((DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao()) < DataHoje.converteDataParaInteger(listao.get(0).getEmissao()) )){
                    //
                    //                        }
                    //                    }
                }
                GenericaSessao.put("juridicaPesquisa", juridica);
                PF.openDialog("dlg_oposicao");
                // COM FISICA, SEM PESSOA EMPRESA COM OPOSICAO
            } else if (!listFisica.isEmpty() && pem == null && !listao.isEmpty()) {
                GenericaMensagem.warn("Atenção", "CPF cadastrado em oposição data: " + listao.get(0).getEmissao());
                styleDestaque = "color: red; font-size: 14pt; font-weight:bold";
                fisica = listFisica.get(0);
                juridica = listao.get(0).getJuridica();
                enderecoFisica = dbe.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 3);
                PF.openDialog("dlg_oposicao");
            }
        }
    }

    public String pesquisarProtocolo() {
        HomologacaoDao db = new HomologacaoDao();
        if (protocolo > 0) {
            Agendamento age = new Agendamento();
            age.setData(agendamento.getData());
            age.setHorarios(agendamento.getHorarios());
            Agendamento age2 = db.pesquisaProtocolo(protocolo);
            if (age2 != null) {
                agendamento = age2;
                agendamento.setData(age.getData());
                agendamento.setHorarios(age.getHorarios());
                agendamento.setAgendador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
                agendamento.setFilial(macFilial.getFilial());
                fisica = agendamento.getPessoaEmpresa().getFisica();
                PessoaEnderecoDao dbe = new PessoaEnderecoDao();
                enderecoFisica = dbe.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 3);
                pessoaEmpresa = agendamento.getPessoaEmpresa();
                profissao = pessoaEmpresa.getFuncao();
                GenericaSessao.put("juridicaPesquisa", pessoaEmpresa.getJuridica());
            } else if (agendamento.getId() != -1) {
                limpar();
            }
        } else if (agendamento.getId() != -1) {
            limpar();
        }
        return "agendamento";
    }

    public String enviarEmailProtocolo() {
        ProtocoloAgendamento protocoloAgendamento = new ProtocoloAgendamento();
        protocoloAgendamento.enviar(agendamento);
        return null;
    }

    public void pesquisaEndereco() {
        EnderecoDao db = new EnderecoDao();
        listaEnderecos.clear();
        if (!cepEndereco.isEmpty()) {
            listaEnderecos = db.pesquisaEnderecoCep(cepEndereco);
        }
    }

    public void limparPesquisaEndereco() {
        listaEnderecos.clear();
    }

    public void editarEndereco(Endereco e) {
        enderecoFisica.setEndereco(e);
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public void refreshForm() {

    }

    public Juridica getJuridica() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
            listaMovimento.clear();
            GenericaSessao.put("pessoaPesquisa", juridica.getPessoa());
            if (juridica.getContabilidade() != null && agendamento.getId() == -1) {
                agendamento.setTelefone(juridica.getContabilidade().getPessoa().getTelefone1());
            }

            if (fisica.getId() != -1) {
                PessoaEmpresaDao db = new PessoaEmpresaDao();
                List<PessoaEmpresa> list_pe = db.listaPessoaEmpresaPorFisicaEmpresaDemissao(fisica.getId(), juridica.getId());

                if (!list_pe.isEmpty()) {
                    pessoaEmpresa = list_pe.get(0);

                    if (pessoaEmpresa.getFuncao() != null) {
                        profissao = pessoaEmpresa.getFuncao();
                    }
                } else if (validaAdmissao() && validaDemissao()) {
//                        pessoaEmpresa = new PessoaEmpresa();
                    //                      profissao = new Profissao();
                }
            }
            loadStatusEmpresa();
            loadEmpresaEndereco();
        }
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
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public void loadEmpresaEndereco() {
        enderecoEmpresa = new PessoaEndereco();
        strEndereco = "";
        PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
        enderecoEmpresa = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 5);
        String strCompl;
        if (enderecoEmpresa.getComplemento().equals("")) {
            strCompl = " ";
        } else {
            strCompl = " ( " + enderecoEmpresa.getComplemento() + " ) ";
        }

        strEndereco = enderecoEmpresa.getEndereco().getLogradouro().getDescricao() + " "
                + enderecoEmpresa.getEndereco().getDescricaoEndereco().getDescricao() + ", " + enderecoEmpresa.getNumero() + " " + enderecoEmpresa.getEndereco().getBairro().getDescricao() + ","
                + strCompl + enderecoEmpresa.getEndereco().getCidade().getCidade() + " - " + enderecoEmpresa.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(enderecoEmpresa.getEndereco().getCep());
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public int getIdMotivoDemissao() {
        return idMotivoDemissao;
    }

    public void setIdMotivoDemissao(int idMotivoDemissao) {
        this.idMotivoDemissao = idMotivoDemissao;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public boolean isRenderCancelarHorario() {
        return renderCancelarHorario;
    }

    public void setRenderCancelarHorario(boolean renderCancelarHorario) {
        this.renderCancelarHorario = renderCancelarHorario;
    }

    public boolean isRenderCancelar() {
        return renderCancelar;
    }

    public void setRenderCancelar(boolean renderCancelar) {
        this.renderCancelar = renderCancelar;
    }

    public String getStatusEmpresa() {
        return statusEmpresa;
    }

    public void setStatusEmpresa(String statusEmpresa) {
        this.statusEmpresa = statusEmpresa;
    }

    public void loadStatusEmpresa() {
        statusEmpresa = "";
        listaMovimento = new ArrayList();
        listaMovimento = new MovimentoDao().findDebitoPessoa(juridica.getPessoa().getId());
        if (!listaMovimento.isEmpty()) {
            statusEmpresa = "EM DÉBITO";
        } else {
            statusEmpresa = "REGULAR";
        }
    }

    public int getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(int protocolo) {
        this.protocolo = protocolo;
    }

    public String getCepEndereco() {
        return cepEndereco;
    }

    public void setCepEndereco(String cepEndereco) {
        this.cepEndereco = cepEndereco;
    }

    public List<Endereco> getListaEnderecos() {
//        if (listaEnderecos.isEmpty()) {
//            if (!cepEndereco.equals("")) {
//                EnderecoDao db = new EnderecoDao();
//                listaEnderecos = db.pesquisaEnderecoCep(cepEndereco);
//            }
//        }
        return listaEnderecos;
    }

    public void setListaEnderecos(List listaEnderecos) {
        this.listaEnderecos = listaEnderecos;
    }

    public PessoaEndereco getEnderecoFisica() {
        if (enderecoFisica == null) {
            enderecoFisica = new PessoaEndereco();
        }
        return enderecoFisica;
    }

    public void setEnderecoFisica(PessoaEndereco enderecoFisica) {
        this.enderecoFisica = enderecoFisica;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getIdIndexEndereco() {
        return idIndexEndereco;
    }

    public void setIdIndexEndereco(int idIndexEndereco) {
        this.idIndexEndereco = idIndexEndereco;
    }

    public String extratoTela() {
        GenericaSessao.put("pessoaPesquisa", juridica.getPessoa());
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).extratoTela();
    }

    public Boolean isContribuinte() {
        return !getContribuinte().isEmpty();
    }

    public String getContribuinte() {
        if (juridica.getId() != -1) {
            JuridicaDao db = new JuridicaDao();
            List<ArrayList> listax = db.listaJuridicaContribuinte(juridica.getId());
            if (!listax.isEmpty()) {
                if (((List) (listax.get(0))).get(11) != null) {
                    return "Empresa Inativa";
                } else {
                    return "";
                }
            }
        }
        return "Empresa não contribuinte, não poderá efetuar um agendamento!";
    }

    public int getIdHorarioTransferencia() {
        return idHorarioTransferencia;
    }

    public void setIdHorarioTransferencia(int idHorarioTransferencia) {
        this.idHorarioTransferencia = idHorarioTransferencia;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public String getEnviarPara() {
        return enviarPara;
    }

    public void setEnviarPara(String enviarPara) {
        this.enviarPara = enviarPara;
    }

    public PessoaEndereco getEnderecoFilial() {
        PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
        if (enderecoFilial.getId() == -1) {
            enderecoFilial = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(macFilial.getFilial().getFilial().getPessoa().getId(), 2);
        }
        return enderecoFilial;
    }

    public void setEnderecoFilial(PessoaEndereco enderecoFilial) {
        this.enderecoFilial = enderecoFilial;
    }

    public boolean isImprimirPro() {
        return imprimirPro;
    }

    public void setImprimirPro(boolean imprimirPro) {
        this.imprimirPro = imprimirPro;
    }

    public boolean isRenderedDisponivel() {
        return renderedDisponivel;
    }

    public void setRenderedDisponivel(boolean renderedDisponivel) {
        this.renderedDisponivel = renderedDisponivel;
    }

    public void mensagemAgendamento() {
        FeriadosDao feriadosDao = new FeriadosDao();
        if (macFilial.getFilial().getFilial().getId() != -1) {
            List<Feriados> feriados = feriadosDao.pesquisarPorDataFilialEData(DataHoje.converteData(data), macFilial.getFilial());
            if (!feriados.isEmpty()) {
                String msg = "";
                for (int i = 0; i < feriados.size(); i++) {
                    if (i == 0) {
                        msg += feriados.get(i).getNome();
                    } else {
                        msg += ", " + feriados.get(i).getNome();
                    }
                }
                if (!msg.isEmpty()) {
                    GenericaMensagem.warn("Sistema", "Nesta data existem feriados/agenda: " + msg);
                }
            }
        }
    }

    public int getIdHorarioAlternativo() {
        return idHorarioAlternativo;
    }

    public void setIdHorarioAlternativo(int idHorarioAlternativo) {
        this.idHorarioAlternativo = idHorarioAlternativo;
    }

    public void adicionarHorarioAlternativo() {
        agendamento.setHorarios((Horarios) new Dao().find(new Horarios(), Integer.parseInt(listaHorarioTransferencia.get(idHorarioAlternativo).getDescription())));
        setOcultarHorarioAlternativo(true);
        loadListaHorariosTransferencia();
    }

    public boolean isOcultarHorarioAlternativo() {
        return ocultarHorarioAlternativo;
    }

    public void setOcultarHorarioAlternativo(boolean ocultarHorarioAlternativo) {
        this.ocultarHorarioAlternativo = ocultarHorarioAlternativo;
    }

    // Verifica os agendamentos que não foram atendidos no menu principal;
    public void verificaNaoAtendidos() {
        HomologacaoDao homologacaoDB = new HomologacaoDao();
        homologacaoDB.verificaNaoAtendidosSegRegistroAgendamento();
    }

    public String getEstiloTabela() {
        return "";
    }

    /**
     * -- STATUS 1 - DISPONIVEL; 2 - AGENDADO; 3 - CANCELADO; 4 - HOMOLOGADO; 5
     * - ATENDIMENTO; 6 - ENCAIXE; 7 - NÃO COMPARACEU
     *
     * @return
     */
    public List<ListaAgendamento> getListaHorarios() {
        return listaHorarios;
    }

    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    public String getStyleDestaque() {
        return styleDestaque;
    }

    public void setStyleDestaque(String styleDestaque) {
        this.styleDestaque = styleDestaque;
    }

    public ConfiguracaoHomologacao getConfiguracaoHomologacao() {
        return configuracaoHomologacao;
    }

    public void setConfiguracaoHomologacao(ConfiguracaoHomologacao configuracaoHomologacao) {
        this.configuracaoHomologacao = configuracaoHomologacao;
    }

    public int getCounter() {
        return counter;
    }

    public void increment() {
        counter--;
        if (counter == -1) {
            counter = configuracaoHomologacao.getTempoRefreshAgendamento();
        }
    }

    public boolean isVisibleModal() {
        return visibleModal;
    }

    public void setVisibleModal(boolean visibleModal) {
        if (!visibleModal) {
            renderedFilial = false;
            HorarioReservaDao hrd = new HorarioReservaDao();
            hrd.clear();
            hrd.begin();
            GlobalSync.load();
            WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
        }
        this.visibleModal = visibleModal;
        this.validacao = false;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public Cancelamento getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(Cancelamento cancelamento) {
        this.cancelamento = cancelamento;
    }

    public String getDocumentoFisica() {
        return documentoFisica;
    }

    public void setDocumentoFisica(String documentoFisica) {
        this.documentoFisica = documentoFisica;
    }

    public boolean stop() {
        if (GlobalSync.getStaticDate() != null) {
            if (polling != null) {
                if (GlobalSync.getStaticDate() == polling) {
                    return true;
                } else {
                    polling = GlobalSync.getStaticDate();
                }
            } else {
                polling = GlobalSync.getStaticDate();
            }
        }
        return false;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public String getMindate() {
        if (idStatus == 1) {
            if (registro.getAgendamentoRetroativo() != null && (DataHoje.maiorData(registro.getAgendamentoRetroativo(), DataHoje.dataHoje()) || DataHoje.converteData(registro.getAgendamentoRetroativo()).equals(DataHoje.data()))) {
                return "";
            }
            DataHoje dh = new DataHoje();
            if (configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
                return DataHoje.converteData(DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento() + 1, DataHoje.data())));
            } else {
                return DataHoje.data();

            }
        }
        return "";
    }

    public String getMaxdate() {
        if (idStatus == 1) {
            if (DataHoje.maiorData(data, DataHoje.dataHoje())) {
                if (registro.getHomolocaoLimiteMeses() <= 0) {
                    return new DataHoje().incrementarMeses(3, DataHoje.data());
                } else {
                    DataHoje dh = new DataHoje();
                    if (configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
                        return new DataHoje().incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data());
                    } else {
                        return new DataHoje().incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data());
                    }

                }
            } else if (registro.getHomolocaoLimiteMeses() <= 0) {
                return new DataHoje().incrementarMeses(3, DataHoje.data());
            } else {
                return new DataHoje().incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data());
            }
        }
        return "";
    }

    public List<Movimento> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<Movimento> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public List<SelectItem> getListFilial() {
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Boolean getRenderedFilial() {
        return renderedFilial;
    }

    public void setRenderedFilial(Boolean renderedFilial) {
        this.renderedFilial = renderedFilial;
    }

    // ARQUIVOS
    public List getListFiles() {
        listFiles.clear();
        listFiles = Diretorio.listaArquivos("Arquivos/homologacao/" + agendamento.getId());
        return listFiles;
    }

    private void validar() {
        Dao dao = new Dao();
        agendamento.setStatus((Status) dao.find(new Status(), 2));
        if (agendamento.getId() != -1) {
            if (dao.update(agendamento, true)) {
                validacao = false;
                GenericaMensagem.info("Sucesso", "Autorização de Agendamento Concluída!");
                ProtocoloAgendamento protocoloAgendamento = new ProtocoloAgendamento();
                protocoloAgendamento.enviar(agendamento, 8);
            } else {
                GenericaMensagem.warn("Erro", "Erro ao validar agendamento!");
            }
        }
    }

    private void recusar(String tcase) {
        Dao dao = new Dao();
        if (agendamento.getEmail().isEmpty()) {
            GenericaMensagem.info("Validação", "Informar e-mail");
            return;
        }
        String titulo = "";
        if (tcase.equals("recusar")) {
            titulo = "Recusa de solicitação de agendamento de homologação referente aos dados abaixo";
            if (motivoRecusaDireto.isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar motivo da recusa!");
                return;
            }
            agendamento.setMotivoRecusa(motivoRecusaDireto);
            agendamento.setStatus((Status) dao.find(new Status(), 9));
            agendamento.setDtRecusa1(new Date());
            agendamento.setOperadorRecusa(new Usuario().getUsuario());
        } else if (tcase.equals("solicitar_pendencias")) {
            titulo = "Solicitação de documentos pendentes!";
            if (motivoRecusa1.isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar motivo da recusa!");
                return;
            }
            agendamento.setMotivoRecusa(motivoRecusa1);
            agendamento.setStatus((Status) dao.find(new Status(), 8));
            agendamento.setDtRecusa1(new Date());
            agendamento.setOperadorRecusa(new Usuario().getUsuario());
        } else if (tcase.equals("segunda_recusa")) {
            titulo = "2ª Recusa de solicitação de agendamento de homologação referente aos dados abaixo";
            if (motivoRecusa2.isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar motivo da recusa!");
                return;
            }
            agendamento.setMotivoRecusa2(motivoRecusa2);
            agendamento.setStatus((Status) dao.find(new Status(), 9));
            agendamento.setDtRecusa2(new Date());
            agendamento.setOperadorRecusa2(new Usuario().getUsuario());
        }
        if (agendamento.getId() != -1) {
            if (dao.update(agendamento, true)) {
                validacao = false;
                try {
                    List<Pessoa> p = new ArrayList();
                    agendamento.getPessoaEmpresa().getFisica().getPessoa().setEmail1(agendamento.getEmail());
                    p.add(agendamento.getPessoaEmpresa().getFisica().getPessoa());
                    Mail mail = new Mail();
                    ConfiguracaoDepartamento configuracaoDepartamento = new ConfiguracaoDepartamentoDao().findBy(8, MacFilial.getAcessoFilial().getFilial().getId());
                    Email email = new Email(
                            -1,
                            DataHoje.dataHoje(),
                            DataHoje.livre(new Date(), "HH:mm"),
                            (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                            (Rotina) dao.find(new Rotina(), 113),
                            null,
                            "Documentação incompleta protocolo n°" + agendamento.getId(),
                            "",
                            false,
                            false
                    );
                    mail.setEmail(email);
                    String htmlString = "";
                    htmlString = ""
                            + "<html>"
                            + "     <body style='background-color: white'>"
                            + "         <p> <b> " + titulo + "</b> </p>"
                            + "         <p> <b>Razão Social</b>: " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " </p>"
                            + "         <p> <b>CNPJ</b>: " + agendamento.getPessoaEmpresa().getJuridica().getPessoa().getDocumento() + " </p>"
                            + "         <p> <b>Funcionário</b>: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getNome() + " </p>"
                            + "         <p> <b>CPF</b>: " + agendamento.getPessoaEmpresa().getFisica().getPessoa().getDocumento() + " </p>"
                            + "         <p> <b>Protocolo nº " + agendamento.getId() + "</b> </p>"
                            + "         <p> <b>Motivo</b>: " + agendamento.getMotivoRecusa() + " </p>"
                            + "         <p> <b>A/C: </b>: " + agendamento.getContato() + " </p>"
                            + "         <p></p>"
                            + "         <br /><br />"
                            + "     </body>"
                            + "</html>";
                    mail.setHtml(htmlString);
                    List<EmailPessoa> emailPessoas = new ArrayList<>();
                    EmailPessoa emailPessoa = new EmailPessoa();
                    List<Pessoa> pessoas = (List<Pessoa>) p;
                    for (Pessoa p1 : pessoas) {
                        emailPessoa.setDestinatario(p1.getEmail1());
                        emailPessoa.setPessoa(p1);
                        emailPessoa.setRecebimento(null);
                        emailPessoas.add(emailPessoa);
                        mail.setEmailPessoas(emailPessoas);
                        emailPessoa = new EmailPessoa();
                    }
                    if (configuracaoDepartamento != null) {
                        mail.setConfiguracaoDepartamento(configuracaoDepartamento);
                    }
                    String[] retorno = mail.send("personalizado");
                    if (!retorno[1].isEmpty()) {
                        GenericaMensagem.warn("E-mail", retorno[1]);
                    } else {
                        GenericaMensagem.info("E-mail", retorno[0]);
                    }
                } catch (Exception e) {
                    NovoLog log = new NovoLog();
                    log.live("Erro de envio de protocolo por e-mail: Mensagem: " + e.getMessage() + " - Causa: " + e.getCause() + " - Caminho: " + e.getStackTrace().toString());
                }
                GenericaMensagem.info("Sucesso", "Agendamento recusado!");

            } else {
                GenericaMensagem.warn("Erro", "Erro ao validar agendamento!");
            }
        }
    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public int getId_protocolo() {
        return id_protocolo;
    }

    public void setId_protocolo(int id_protocolo) {
        this.id_protocolo = id_protocolo;
    }

    public boolean isValidacao() {
        return validacao;
    }

    public void setValidacao(boolean validacao) {
        this.validacao = validacao;
    }

    public Date getPolling() {
        return polling;
    }

    public void setPolling(Date polling) {
        this.polling = polling;
    }

    public String getMotivoRecusaDireto() {
        return motivoRecusaDireto;
    }

    public void setMotivoRecusaDireto(String motivoRecusaDireto) {
        this.motivoRecusaDireto = motivoRecusaDireto;
    }

    public String getMotivoRecusa1() {
        return motivoRecusa1;
    }

    public void setMotivoRecusa1(String motivoRecusa1) {
        this.motivoRecusa1 = motivoRecusa1;
    }

    public String getMotivoRecusa2() {
        return motivoRecusa2;
    }

    public void setMotivoRecusa2(String motivoRecusa2) {
        this.motivoRecusa2 = motivoRecusa2;
    }

}
