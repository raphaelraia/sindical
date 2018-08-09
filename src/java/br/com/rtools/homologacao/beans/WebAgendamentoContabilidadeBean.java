package br.com.rtools.homologacao.beans;

import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.FilialCidadeDao;
import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.pessoa.beans.PesquisarProfissaoBean;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoDao;
import br.com.rtools.arrecadacao.dao.RelacaoEmpregadosDao;
import br.com.rtools.arrecadacao.dao.WebContabilidadeDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.dao.EnderecoDao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.ConfiguracaoHomologacao;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.Feriados;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.Status;
import br.com.rtools.homologacao.dao.FeriadosDao;
import br.com.rtools.homologacao.dao.HorarioReservaDao;
import br.com.rtools.homologacao.dao.*;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public final class WebAgendamentoContabilidadeBean extends PesquisarProfissaoBean implements Serializable {

    private List<DataObject> listaHorarios = new ArrayList();
    private List listaEmDebito = new ArrayList();
    private List<SelectItem> listaEmpresas = new ArrayList<>();
    private int idStatus = 1;
    private int idMotivoDemissao = 0;
    private int idSelectRadio = 0;
    private String statusEmpresa = "";
    private String strEndereco = "";
    private String filtraPor = "todos";
    private boolean chkFiltrar = true;
    private boolean renderBtnAgendar = true;
    private Date data = DataHoje.converte(new DataHoje().incrementarDias(1, DataHoje.data()));
    private Agendamento agendamento = new Agendamento();
    private Agendamento agendamentoProtocolo = new Agendamento();
    private Fisica fisica = new Fisica();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
    private Juridica juridica = new Juridica();
    private FilialCidade sindicatoFilial;
    private PessoaEndereco enderecoFilial = new PessoaEndereco();
    private Juridica empresa = new Juridica();
    private PessoaEndereco enderecoEmpresa = new PessoaEndereco();
    private PessoaEndereco enderecoFisica = new PessoaEndereco();
    private String cepEndereco = "";
    private List<Endereco> listaEnderecos = new ArrayList();
    private String strContribuinte = "";
    private Registro registro = new Registro();
    public List<SelectItem> listaStatus = new ArrayList<>();
    public List<SelectItem> listaMotivoDemissao = new ArrayList<>();
    private String tipoTelefone = "telefone";
    private ConfiguracaoHomologacao configuracaoHomologacao;
    private ConfiguracaoArrecadacao configuracaoArrecadacao;
    private boolean visibleModal = false;
    private String tipoAviso = null;
    private Date polling;
    private List<SelectItem> listFilial = new ArrayList<>();
    private Integer idFilial = null;
    private List listFiles;
    private String uuidDir;
    private List<SelectItem> listNrRegistro;
    private Integer nrRegistro = 1;

    public WebAgendamentoContabilidadeBean() {
        uuidDir = "";
        Dao dao = new Dao();
        registro = (Registro) dao.find(new Registro(), 1);
        configuracaoHomologacao = (ConfiguracaoHomologacao) new Dao().find(new ConfiguracaoHomologacao(), 1);
        configuracaoArrecadacao = (ConfiguracaoArrecadacao) new Dao().find(new ConfiguracaoArrecadacao(), 1);
        if (configuracaoHomologacao == null) {
            configuracaoHomologacao = new ConfiguracaoHomologacao();
            new Dao().save(configuracaoHomologacao, true);
        } else if (configuracaoHomologacao.getId() != null && configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
            DataHoje dh = new DataHoje();
            data = DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento() + 1, DataHoje.converteData(DataHoje.dataHoje())));
        }
        configuracaoArrecadacao = ConfiguracaoArrecadacao.get();
        getListaStatus();
        listFiles = new ArrayList();
        this.loadListEmpresa();
        this.loadListFiliais();
        HorarioReservaDao horarioReservaDao = new HorarioReservaDao();
        horarioReservaDao.begin();
        horarioReservaDao.clear();
        clearHorarios();
        GlobalSync.load();
        loadListNrRegistro();
    }

    public boolean validaAdmissao() {
        if (fisica.getId() != -1 && empresa.getId() != -1 && !pessoaEmpresa.getAdmissao().isEmpty() && pessoaEmpresa.getId() == -1) {
            HomologacaoDao db = new HomologacaoDao();

            PessoaEmpresa pe = db.pesquisaPessoaEmpresaAdmissao(fisica.getId(), empresa.getId(), pessoaEmpresa.getAdmissao(), nrRegistro);

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
        if (fisica.getId() != -1 && empresa.getId() != -1 && !pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getId() == -1) {
            HomologacaoDao db = new HomologacaoDao();

            PessoaEmpresa pe = db.pesquisaPessoaEmpresaDemissao(fisica.getId(), empresa.getId(), pessoaEmpresa.getDemissao(), nrRegistro);

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

    public void loadListEmpresa() {
        WebContabilidadeDao db = new WebContabilidadeDao();
        JuridicaDao dbJur = new JuridicaDao();
        if (juridica.getId() == -1) {
            juridica = dbJur.pesquisaJuridicaPorPessoa(((Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb")).getId());
        }
        List<Juridica> result = db.listaEmpresasPertContabilidade(juridica.getId());
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaEmpresas.add(new SelectItem(
                        i,
                        result.get(i).getPessoa().getDocumento() + " - " + result.get(i).getPessoa().getNome(),
                        Integer.toString(result.get(i).getId())
                ));
            }
        }
    }

    public void loadListFiliais() {
        listFilial = new ArrayList();
        Dao dao = new Dao();
        PessoaEnderecoDao dbe = new PessoaEnderecoDao();
        if (!listaEmpresas.isEmpty()) {
            empresa = (Juridica) dao.find(new Juridica(), Integer.parseInt(listaEmpresas.get(idSelectRadio).getDescription()));
            enderecoEmpresa = dbe.pesquisaEndPorPessoaTipo(empresa.getPessoa().getId(), 5);
            // sindicatoFilial = new FilialCidade();
            // FILIAL DA EMPRESA
            if (empresa.getId() != -1 && enderecoEmpresa.getId() != -1) {
                FilialCidadeDao filialCidadeDao = new FilialCidadeDao();
                List<FilialCidade> list = filialCidadeDao.findListBy(enderecoEmpresa.getEndereco().getCidade().getId());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPrincipal()) {
                        idFilial = list.get(i).getFilial().getId();
                    }
                    listFilial.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getFilial().getFantasia()));
                }
            }
        }

    }

    public final void clearHorariosStatus() {
        if (idStatus == 1 && configuracaoHomologacao.getInicioDiasAgendamento() > 0) {
            DataHoje dh = new DataHoje();
            data = DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento() + 1, DataHoje.converteData(data)));
        }
        clearHorarios(1);
    }

    public void clearHorarios() {
        clearHorarios(1);
    }

    public void clearHorarios(Integer tcase) {
        if (null != tcase) {
            switch (tcase) {
                case 1:
                    loadListFiliais();
                    if (!listFilial.isEmpty() && listFilial.size() > 1) {
                        PF.openDialog("dlg_local");
                    }
                    loadListHorarios(false);
                    lock(true);
                    break;
                case 2:
                    loadListHorarios(false);
                    lock(true);
                    break;
                default:
                    break;
            }
        }
    }

    public Boolean lock() {
        return lock(false);
    }

    public Boolean lock(Boolean message) {
        if (configuracaoHomologacao.getWebDesabilitaInicial() != null) {
            if (DataHoje.between(DataHoje.converteData(data), configuracaoHomologacao.getWebDesabilitaInicialString(), configuracaoHomologacao.getWebDesabilitaFinalString())) {
                if (message) {
                    GenericaMensagem.info("Sistema", "Agendamento web suspenso entre " + configuracaoHomologacao.getWebDesabilitaInicialString() + " até " + configuracaoHomologacao.getWebDesabilitaFinalString());
                }
                if (!configuracaoHomologacao.getWebDesabilitaObs().isEmpty()) {
                    if (message) {
                        GenericaMensagem.info("Mensagem", configuracaoHomologacao.getWebDesabilitaObs());
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final void loadListHorarios() {
        loadListHorarios(stop());
    }

    public final void loadListHorarios(Boolean stop) {
        if (stop) {
            return;
        }
        listaHorarios = new ArrayList();

        if (!getMindate().isEmpty() && idStatus == 1) {
            DataHoje dh = new DataHoje();
            if (!DataHoje.maiorData(data, DataHoje.converte(dh.incrementarDias(configuracaoHomologacao.getInicioDiasAgendamento(), DataHoje.data())))) {
                data = DataHoje.dataHoje();
                GenericaMensagem.warn("Validação", "Data não disponível para agendamento!");
                return;
            }
        }

        // ENDEREÇO DA EMPRESA SELECIONADA PARA PESQUISAR OS HORÁRIOS
        if (listaEmpresas.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhuma Empresa Encontrada!");
            return;
        }
        if (listFilial.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Filial não encontrada, não é possível visualizar horários!");
            return;
        }
        List<Agendamento> ag = new ArrayList();
        List<Horarios> horario;

        HomologacaoDao db = new HomologacaoDao();
        String agendador;
        String homologador;
        Integer status_id = idStatus;
        switch (status_id) {
            //STATUS DISPONIVEL ----------------------------------------------------------------------------------------------
            case 1:
                if (lock()) {
                    return;
                }
                int idDiaSemana = DataHoje.diaDaSemana(data);
                horario = (List<Horarios>) db.pesquisaTodosHorariosDisponiveis(idFilial, idDiaSemana, true);

                int qnt;
                for (int i = 0; i < horario.size(); i++) {
                    qnt = db.pesquisaQntdDisponivel(idFilial, horario.get(i), data);
                    if (qnt == -1) {
                        GenericaMensagem.error("Erro", "Não foi possivel encontrar horários disponíveis, contate seu Sindicato!");
                        break;
                    }
                    if (qnt > 0) {
                        listaHorarios.add(new DataObject(
                                horario.get(i), // ARG 0 HORA
                                null, // ARG 1 CNPJ
                                null, //ARG 2 NOME
                                null, //ARG 3 HOMOLOGADOR
                                null, // ARG 4 CONTATO
                                null, // ARG 5 TELEFONE
                                null, // ARG 6 USUARIO
                                null,
                                qnt, // ARG 8 QUANTIDADE DISPONÍVEL
                                null)
                        );
                    }
                }
                break;

            // STATUS AGENDADO -----------------------------------------------------------------------------------------------
            case 2:
            case 8:
            case 9:

                if (filtraPor.equals("selecionado")) {
                    ag = db.pesquisaAgendadoPorEmpresaSemHorario(idFilial, data, empresa.getPessoa().getId(), status_id);
                } else {
                    WebContabilidadeDao dbw = new WebContabilidadeDao();
                    List<Juridica> result = dbw.listaEmpresasPertContabilidade(juridica.getId());
                    for (int w = 0; w < listaEmpresas.size(); w++) {
                        ag.addAll(db.pesquisaAgendadoPorEmpresaSemHorario(idFilial, data, result.get(w).getPessoa().getId(), status_id));
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

                    listaHorarios.add(new DataObject(
                            ag.get(i).getHorarios(), // ARG 0 HORA
                            ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getDocumento(), // ARG 1 CNPJ
                            ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getNome(), //ARG 2 NOME
                            homologador, //ARG 3 HOMOLOGADOR
                            ag.get(i).getContato(), // ARG 4 CONTATO
                            ag.get(i).getTelefone(), // ARG 5 TELEFONE
                            agendador, // ARG 6 USUARIO
                            ag.get(i).getPessoaEmpresa(), // ARG 7 PESSOA EMPRESA
                            ag.get(i).getData(), // ARG 8
                            ag.get(i))// ARG 9 AGENDAMENTO
                    );
                }
                break;

        }
    }

    public String imprimirPlanilha() {
        if (listaEmDebito.isEmpty()) {
            return null;
        }
        ImprimirBoleto imp = new ImprimirBoleto();
        List<Movimento> lista = new ArrayList();
        List<Double> listaValores = new ArrayList<Double>();
        Dao dao = new Dao();
        for (int i = 0; i < listaEmDebito.size(); i++) {
            Movimento m = (Movimento) dao.find(new Movimento(), (Integer) ((List) listaEmDebito.get(i)).get(0));
            lista.add(m);
            listaValores.add(m.getValor());
        }
        imp.imprimirPlanilha(lista, listaValores, false, false);
        imp.visualizar(null);
        return null;
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            Dao dao = new Dao();
            List<Status> select = new ArrayList();
            select.add((Status) dao.find(new Status(), 1));
            select.add((Status) dao.find(new Status(), 2));
            select.add((Status) dao.find(new Status(), 8));
            select.add((Status) dao.find(new Status(), 9));
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaStatus.add(new SelectItem(select.get(i).getId(), select.get(i).getDescricao()));
                }
            }
        }
        return listaStatus;
    }

    public List<SelectItem> getListaMotivoDemissao() {
        if (listaMotivoDemissao.isEmpty()) {
            Dao dao = new Dao();
            List select = dao.list(new Demissao());
            if (!select.isEmpty()) {
                listaMotivoDemissao.add(new SelectItem(0, "", "0"));
                for (int i = 0; i < select.size(); i++) {
                    listaMotivoDemissao.add(new SelectItem(i + 1,
                            (String) ((Demissao) select.get(i)).getDescricao(),
                            Integer.toString(((Demissao) select.get(i)).getId())));
                }
            }
        }
        return listaMotivoDemissao;
    }

    public void novoProtocolo() {
        Dao dao = new Dao();
        agendamentoProtocolo = new Agendamento();
        renderBtnAgendar = true;
        empresa = (Juridica) dao.find(new Juridica(), Integer.parseInt(((SelectItem) listaEmpresas.get(idSelectRadio)).getDescription()));
        agendamento.setDtData(null);
        agendamento.setHorarios(null);
        agendamento.setFilial((Filial) dao.find(new Filial(), idFilial));
        agendamentoProtocolo = agendamento;
        if (empresa.getContabilidade() != null) {
            agendamento.setTelefone(empresa.getContabilidade().getPessoa().getTelefone1());
        }
        if (profissao.getId() == -1) {
            profissao = (Profissao) dao.find(new Profissao(), 0);
        }
    }

    public void agendar(DataObject datao) {
        nrRegistro = 1;
        UUID uuidX = UUID.randomUUID();
        uuidDir = "_" + uuidX.toString().replace("-", "_");
        // CAPITURAR ENDEREÇO DA EMPRESA
        listFiles = new ArrayList();
        empresa = new Juridica();
        enderecoEmpresa = new PessoaEndereco();
        // sindicatoFilial = new FilialCidade();
        agendamento = new Agendamento();
        pessoaEmpresa = new PessoaEmpresa();
        fisica = new Fisica();
        enderecoFisica = new PessoaEndereco();
        idMotivoDemissao = 0;
        tipoAviso = null;

        if (listaEmpresas.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhuma empresa encontrada para Agendar!");
            return;
        }
        if (listFilial.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhuma filial encontrada para Agendar!");
            return;
        }

        Dao dao = new Dao();
        PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();

        empresa = (Juridica) dao.find(new Juridica(), Integer.parseInt(listaEmpresas.get(idSelectRadio).getDescription()));
        enderecoEmpresa = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(empresa.getPessoa().getId(), 5);

        if (enderecoEmpresa.getId() != -1) {
            String strCompl;
            if (enderecoEmpresa.getComplemento().isEmpty()) {
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

        Filial f = (Filial) dao.find(new Filial(), idFilial);

        switch (idStatus) {
            case 1: {
                HomologacaoDao db = new HomologacaoDao();
                int nrDataHoje = DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()));
                HorarioReservaDao hrd = new HorarioReservaDao();
                hrd.exists(nrDataHoje);
                int quantidade_reservada = hrd.count(((Horarios) datao.getArgumento0()).getId());
                int quantidade = db.pesquisaQntdDisponivel(f.getId(), ((Horarios) datao.getArgumento0()), getData());
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
                List<Agendamento> list_a = db.pesquisaAgendadoPorEmpresaSemHorario(f.getId(), data, empresa.getPessoa().getId());
                if (list_a.size() >= f.getQuantidadeAgendamentosPorEmpresa()) {
                    GenericaMensagem.warn("Atenção", "Limite de Agendamentos para hoje é de " + f.getQuantidadeAgendamentosPorEmpresa());
                    return;
                }
                if (!configuracaoHomologacao.getWebAgendarMesmoHorarioEmpresa()) {
                    List list = db.findByDataHorarioEmpresa(data, ((Horarios) datao.getArgumento0()).getId(), empresa.getId());
                    if (list.size() == 1) {
                        GenericaMensagem.error("Sistema", "Só é possível agendar um horário por empresa, nesta data!");
                        return;
                    }
                }
                if (data.getDay() == 6 || data.getDay() == 0) {
                    GenericaMensagem.warn("Atenção", "Fins de semana não é permitido!");
                    return;
                }

                if (DataHoje.converteDataParaInteger(DataHoje.converteData(data)) == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                    GenericaMensagem.warn("Atenção", "Data deve ser apartir de hoje, caso deseje marcar para esta data contate seu Sindicato!");
                    return;
                }

                if (DataHoje.converteDataParaInteger(DataHoje.converteData(getData())) < DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
                    GenericaMensagem.warn("Atenção", "Data anterior ao dia de hoje!");
                    return;
                }
                if (registro.getHomolocaoLimiteMeses() > 0) {
                    if (DataHoje.converteDataParaInteger(((new DataHoje()).incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data()))) < DataHoje.converteDataParaInteger(DataHoje.converteData(getData()))) {
                        GenericaMensagem.warn("Atenção", "NÃO É POSSÍVEL REALIZAR AGENDAMENTO COM SUPEIOR MAIOR À " + registro.getHomolocaoLimiteMeses() + " MESES!");
                        return;
                    }
                } else if (DataHoje.converteDataParaInteger(((new DataHoje()).incrementarMeses(registro.getHomolocaoLimiteMeses(), DataHoje.data()))) < DataHoje.converteDataParaInteger(DataHoje.converteData(getData()))) {
                    GenericaMensagem.warn("Atenção", "Data maior que 3 meses!");
                    return;
                }

                if (pesquisarFeriado()) {
                    GenericaMensagem.warn("Atenção", "Esta data esta cadastrada como Feriado!");
                    return;
                }

                if (data == null) {
                    GenericaMensagem.warn("Atenção", "Selecione uma data para Agendamento!");
                    return;
                } else {
                    if (empresa.getContabilidade() != null) {
                        agendamento.setTelefone(empresa.getContabilidade().getPessoa().getTelefone1());
                    }
                    if (profissao.getId() == -1) {
                        profissao = (Profissao) dao.find(new Profissao(), 0);
                    }

                    agendamento.setData(DataHoje.converteData(data));
                    agendamento.setHorarios((Horarios) datao.getArgumento0());
                    agendamento.setFilial(f);
                    agendamentoProtocolo = agendamento;
                    visibleModal = true;
                    hrd.reserve(((Horarios) datao.getArgumento0()).getId());
                    GlobalSync.load();
                }
                if (configuracaoHomologacao.getWebDocumentoObrigatorio()) {
                    File f2 = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/validacao/" + empresa.getId() + "/"));
                    if (f2.exists()) {
                        try {
                            FileUtils.deleteDirectory(f2);
                        } catch (IOException ex) {
                            GenericaMensagem.error("Erro", "Não foi possível anexar mover os anexos para validação!");
                            dao.rollback();
                            return;
                        }
                    }
                }
                break;
            }
            case 2:
            case 8:
            case 9: {
                PessoaEnderecoDao db = new PessoaEnderecoDao();
                renderBtnAgendar = false;
                agendamento = (Agendamento) datao.getArgumento9();
                agendamentoProtocolo = agendamento;
                fisica = ((PessoaEmpresa) datao.getArgumento7()).getFisica();
                enderecoFisica = db.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
                empresa = ((PessoaEmpresa) datao.getArgumento7()).getJuridica();
                pessoaEmpresa = agendamento.getPessoaEmpresa();
                nrRegistro = pessoaEmpresa.getNrRegistro();
                profissao = ((PessoaEmpresa) datao.getArgumento7()).getFuncao();
                for (int i = 0; i < getListaMotivoDemissao().size(); i++) {
                    if (Integer.parseInt(getListaMotivoDemissao().get(i).getDescription()) == agendamento.getDemissao().getId()) {
                        idMotivoDemissao = (Integer) getListaMotivoDemissao().get(i).getValue();
                        break;
                    }
                }
                tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
                visibleModal = true;
                loadListFiles();
                break;
            }
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_agendamento').show();");
    }

    public boolean pesquisarFeriado() {
        Filial f = (Filial) new Dao().find(new Filial(), idFilial);
        FeriadosDao feriadosDao = new FeriadosDao();
        List<Feriados> listFeriados = feriadosDao.pesquisarPorDataFilialEData(DataHoje.converteData(getData()), f);
        if (!listFeriados.isEmpty()) {
            GenericaMensagem.info("Feriado", listFeriados.get(0).getNome());
            return true;
        } else {
            listFeriados = feriadosDao.pesquisarPorData(DataHoje.converteData(getData()));
            PessoaEndereco pe = ((PessoaEndereco) ((List) new PessoaEnderecoDao().pesquisaEndPorPessoa(f.getFilial().getPessoa().getId())).get(0));
            if (!listFeriados.isEmpty()) {
                for (int i = 0; i < listFeriados.size(); i++) {
                    if (listFeriados.get(i).getCidade() == null) {
                        GenericaMensagem.info("Feriado", listFeriados.get(0).getNome());
                        return true;
                    }
                    if (listFeriados.get(i).getCidade().getId() == pe.getEndereco().getCidade().getId()) {
                        GenericaMensagem.info("Feriado", listFeriados.get(0).getNome());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void salvar() {

        if (fisica.getPessoa().getDocumento().isEmpty() || fisica.getPessoa().getDocumento().equals("0")) {
            GenericaMensagem.warn("Validação", "Informar o CPF!");
            return;
        } else {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                GenericaMensagem.warn("Atenção", "Documento Inválido!");
                return;
            }
        }

        Dao dao = new Dao();
        if (agendamento.getId() != -1) {
            if (agendamento.getDtEmissao() != null && agendamento.getDtRecusa2() == null && agendamento.getDtSolicitacao2() == null) {
                Agendamento a = (Agendamento) dao.find(new Agendamento(), agendamento.getId());
                a.setDtSolicitacao2(new Date());
                dao.update(a, true);
                agendamento = a;
                GenericaMensagem.info("Sucesso", "Nova solicutação enviada, aguarde novamente a validação!");
            }
            GenericaMensagem.warn("Sistema", "Não é possível atualizar este agendamento!");
            return;
        }
        if (!validaAdmissao()) {
            return;
        }

        if (!validaDemissao()) {
            return;
        }

        if (listaMotivoDemissao.get(idMotivoDemissao).getDescription().equals("0")) {
            GenericaMensagem.warn("Validação", "Selecione um Motivo de Demissão!");
            return;
        }

        if (tipoAviso == null || tipoAviso.isEmpty()) {
            GenericaMensagem.warn("Validação", "Selecione um Tipo de Aviso!");
            return;
        }
        pessoaEmpresa.setAvisoTrabalhado(tipoAviso.equals("true"));
        pessoaEmpresa.setNrRegistro(nrRegistro);

        if (configuracaoHomologacao.getWebValidaDataNascimento()) {
            if (fisica.getNascimento().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar data de nascimento!");
                return;
            }
        }

        if (configuracaoHomologacao.getWebValidaFuncao()) {
            if (profissao.getId().equals(-1) || profissao.getId().equals(0)) {
                GenericaMensagem.warn("Validação", "Informar a função/profissão!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaCarteira()) {
            if (fisica.getCarteira().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar o número da carteira de trabalho!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaSerie()) {
            if (fisica.getSerie().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar o número de série da carteira de trabalho!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaContato()) {
            if (agendamento.getContato().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar nome do contato!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaEmail()) {
            if (agendamento.getEmail().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar email!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaTelefone()) {
            if (agendamento.getTelefone().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar telefone!");
                return;
            }
        }
        if (configuracaoHomologacao.getWebValidaAdmissao()) {
            if (pessoaEmpresa.getAdmissao().isEmpty()) {
                GenericaMensagem.warn("Validação", "Informar data de admissão!");
                return;
            }
        }

        if (fisica.getPessoa().getNome().isEmpty() || fisica.getPessoa().getNome() == null) {
            GenericaMensagem.warn("Atenção", "Digite o nome do Funcionário!");
            return;
        }
        if (fisica.getId() != -1) {
            if (dao.update(fisica.getPessoa(), true)) {
                dao.update(fisica, true);
            } else {
                GenericaMensagem.error("Erro", "Não foi possível salvar pessoa!");
                return;
            }
        }
        if (!getStrContribuinte().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não é permitido agendar para uma empresa não contribuinte!");
            return;
        }
        DataHoje dataH = new DataHoje();
        Demissao demissao = (Demissao) dao.find(new Demissao(), Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()));
        if (!demissao.getMotivoWeb()) {
            GenericaMensagem.warn("Sistema", demissao.getMensagemMotivoWeb());
            return;
        }
        if (configuracaoHomologacao.getWebDocumentoObrigatorio()) {
            if (getListFiles().isEmpty()) {
                GenericaMensagem.warn("Validação", "OBRIGATÓRIO ANEXAR DOCUMENTOS DIGITALIZADOS NECESSÁRIOS PARA HOMOLOGAÇÃO!");
                return;
            }
        }
        if (!pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getDemissao() != null) {
            if (null != demissao.getId()) {
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
            }
        } else {
            GenericaMensagem.warn("Atenção", "Data de Demissão é obrigatória!");
            return;
        }
        // RELAÇÃO DE EMPREGADOS
        List listRelacao = new RelacaoEmpregadosDao().findNotSendingByPessoa(juridica.getPessoa().getId());
        if (!listRelacao.isEmpty()) {
            GenericaMensagem.warn("Erro", "Para efetuar esse agendamento CONTATE o Sindicato (2)!");
            return;
        }
        dao.openTransaction();

        if (fisica.getId() == -1) {
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            if (dao.save(fisica.getPessoa())) {
                dao.save(fisica);
            } else {
                GenericaMensagem.error("Atenção", "Erro ao inserir Pessoa, tente novamente!");
                dao.rollback();
                return;
            }
        }

        HomologacaoDao dba = new HomologacaoDao();
        Agendamento age = dba.pesquisaFisicaAgendada(fisica.getId(), empresa.getId(), nrRegistro);
        if (age != null) {
            GenericaMensagem.warn("Atenção", "Pessoa já foi agendada para empresa " + age.getPessoaEmpresa().getJuridica().getPessoa().getNome() + " com mesmo número de registro " + nrRegistro + " !");
            dao.rollback();
            return;
        }
        int ids[] = {1, 3, 4};
        if (enderecoFisica.getId() == -1) {
            if (enderecoFisica.getEndereco().getId() != -1) {
                enderecoFisica.setPessoa(fisica.getPessoa());
                PessoaEndereco pesEnd = enderecoFisica;
                for (int i = 0; i < ids.length; i++) {
                    pesEnd.setTipoEndereco((TipoEndereco) dao.find(new TipoEndereco(), ids[i]));
                    if (!dao.save(pesEnd)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível salvar endereço da pessoa!");
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
            PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
            List<PessoaEndereco> ends = pessoaEnderecoDB.pesquisaEndPorPessoa(fisica.getPessoa().getId());
            for (int i = 0; i < ends.size(); i++) {
                ends.get(i).setComplemento(enderecoFisica.getComplemento());
                ends.get(i).setEndereco(enderecoFisica.getEndereco());
                ends.get(i).setNumero(enderecoFisica.getNumero());
                ends.get(i).setPessoa(enderecoFisica.getPessoa());
                if (!dao.update(ends.get(i))) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível atualizar endereço da pessoa!");
                    return;
                }
            }
        }

        if (pessoaEmpresa == null || pessoaEmpresa.getId() == -1) {
            if (profissao.getId() == -1) {
                profissao = (Profissao) dao.find(new Profissao(), 0);
            }
            pessoaEmpresa.setFuncao(profissao);

            pessoaEmpresa.setFisica(fisica);
            pessoaEmpresa.setJuridica(empresa);
            pessoaEmpresa.setPrincipal(false);

            if (!dao.save(pessoaEmpresa)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível salvar Pessoa Empresa!");
                return;
            }
        } else {
            if (pessoaEmpresa == null || profissao.getId() == -1) {
                profissao = (Profissao) dao.find(new Profissao(), 0);
            }

            pessoaEmpresa.setFuncao(profissao);
            pessoaEmpresa.setPrincipal(false);
            if (!dao.update(pessoaEmpresa)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível atualizar Pessoa Empresa!");
                return;
            }
        }

        if (!listaEmDebito.isEmpty() && !registro.isBloquearHomologacao()) {
            if (!updatePessoaEmpresa(dao)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Pessoa Empresa!");
                dao.rollback();
                return;
            }
            GenericaMensagem.warn("Atenção", "Para efetuar esse agendamento CONTATE o Sindicato (3)!");
            dao.commit();
            return;
        }
        if (!listaEmDebito.isEmpty() && registro.isBloquearHomologacao()) {
            if (registro.getMesesInadimplentesAgenda() > 0) {
                Integer qtdeMeses = new FunctionsDao().quantidadeMesesDebitoArr(pessoaEmpresa.getJuridica().getPessoa().getId());
                if (qtdeMeses > registro.getMesesInadimplentesAgenda()) {
                    if (!updatePessoaEmpresa(dao)) {
                        GenericaMensagem.error("Erro", "Não foi possível atualizar Pessoa Empresa!");
                        dao.rollback();
                        return;
                    }
                    GenericaMensagem.error("Atenção", "Para efetuar esse agendamento CONTATE o Sindicato (4)!");
                    dao.commit();
                    return;
                }
            }
        }
        Boolean bloqueiaOposicao = new OposicaoDao().existsPorPessoaEmpresa(fisica.getPessoa().getDocumento(), juridica.getId(), configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao());
        if (bloqueiaOposicao) {
            if (configuracaoArrecadacao != null) {
                bloqueiaOposicao = configuracaoArrecadacao.getBloqueiaOposição();
            }
        }
        if (bloqueiaOposicao) {
            if (!updatePessoaEmpresa(dao)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Pessoa Empresa!");
                dao.rollback();
                return;
            }
            dao.commit();
            GenericaMensagem.warn("Atenção", "Para efetuar esse agendamento CONTATE o Sindicato (1)!");
        } else {
            Demissao demissaox = (Demissao) dao.find(new Demissao(), Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()));
            if (agendamento.getId() == -1) {

                ConvencaoDao convencaoDao = new ConvencaoDao();
                Convencao convencao = convencaoDao.findByEmpresa(pessoaEmpresa.getJuridica().getPessoa().getId());
                if (convencao == null) {
                    GenericaMensagem.warn("Mensagem", "NENHUMA CONVENÇÃO ENCONTRADA PARA ESTA EMPRESA!");
                    dao.rollback();
                    return;
                }
                agendamento.setNoPrazo(new FunctionsDao().homologacaoPrazo(pessoaEmpresa.isAvisoTrabalhado(), enderecoEmpresa.getEndereco().getCidade().getId(), pessoaEmpresa.getDemissao(), convencao.getId(), agendamento.getData()));

                agendamento.setAgendador(null);
                agendamento.setRecepcao(null);
                agendamento.setDemissao(demissaox);
                agendamento.setHomologador(null);
                agendamento.setDtEmissao(DataHoje.dataHoje());
                agendamento.setPessoaEmpresa(pessoaEmpresa);
                if (configuracaoHomologacao.getWebValidaAgendamento()) {
                    agendamento.setStatus((Status) dao.find(new Status(), 8));
                } else {
                    agendamento.setStatus((Status) dao.find(new Status(), 2));
                }
                if (dao.save(agendamento)) {
                    agendamentoProtocolo = agendamento;
                    if (configuracaoHomologacao.getWebDocumentoObrigatorio()) {
                        String s = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/validacao/" + empresa.getId() + "/" + uuidDir + "/");
                        String d = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/" + agendamento.getId() + "/");
                        File source = new File(s);
                        if (source.exists()) {
                            File destination = new File(d);
                            if (!destination.exists()) {
                                // destination.mkdirs();
                            }
                            boolean success = source.renameTo(destination);
                            if (!success) {
                                GenericaMensagem.error("Erro", "Não foi possível anexar mover os anexos para validação!");
                                dao.rollback();
                                return;
                            }
                            try {
                                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/validacao/" + empresa.getId() + "/"));
                                if (f.exists()) {
                                    FileUtils.deleteDirectory(f);
                                }
                            } catch (IOException ex) {
                                GenericaMensagem.error("Erro", "Não foi possível anexar mover os anexos para validação!");
                                dao.rollback();
                                return;
                            }
                        }
                    }
                    if (configuracaoHomologacao.getWebValidaAgendamento()) {
                        GenericaMensagem.info("Validando, ", "aguarde confirmação do agendamento no seu e-mail. Caso não receba a confirmação em 48 horas entrar em contato com o Síndicato!");
                    } else {
                        GenericaMensagem.info("Sucesso", "Agendamento Salvo!");
                    }
                    if (agendamento.isNoPrazo() == false) {
                        GenericaMensagem.warn("Mensagem", "DE ACORDO COM AS INFORMAÇÕES ACIMA PRESTADAS SEU AGENDAMENTO ESTÁ FORA DO PRAZO PREVISTO EM CONVENÇÃO COLETIVA.");
                    }

                    listaHorarios = new ArrayList();
                    loadListHorarios();
                    PF.openDialog("dlg_protocolo");
                } else {
                    GenericaMensagem.error("Erro", "Não foi possível salvar protocolo!");
                    dao.rollback();
                    return;
                }
            } else {
                agendamento.setDemissao(demissaox);
                if (dao.update(agendamento)) {
                    GenericaMensagem.info("Sucesso", "Agendamento Atualizado!");
                    agendamentoProtocolo = agendamento;
                } else {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível Atualizar protocolo!");
                    return;
                }
            }
            dao.commit();
            GlobalSync.load();
        }
    }

    public Boolean updatePessoaEmpresa(Dao dao) {
        pessoaEmpresa.setDtDemissao(null);
        if (!dao.update(pessoaEmpresa)) {
            return false;
        }
        return true;
    }

    public void cancelar() {
        strEndereco = "";
        fisica = new Fisica();
        agendamento = new Agendamento();
        agendamentoProtocolo = agendamento;
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        empresa = new Juridica();
        enderecoFisica = new PessoaEndereco();
        visibleModal = false;
        GlobalSync.load();
    }

    public void limpar() {
        String datax = agendamento.getData();
        Horarios horario = agendamento.getHorarios();
        //strEndereco = "";
        fisica = new Fisica();
        pessoaEmpresa = new PessoaEmpresa();
        agendamento = new Agendamento();
        profissao = new Profissao();
        //empresa = new Juridica();
        enderecoFisica = new PessoaEndereco();

        agendamento.setData(datax);
        agendamento.setHorarios(horario);
        agendamento.setFilial((Filial) new Dao().find(new Filial(), idFilial));
    }

    public void pesquisarFuncionarioCPF() throws IOException {
        if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("___.___.___-__")) {
            String documento = fisica.getPessoa().getDocumento();
            Dao dao = new Dao();
            FisicaDao dbFis = new FisicaDao();
            PessoaEnderecoDao dbp = new PessoaEnderecoDao();
            HomologacaoDao db = new HomologacaoDao();
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            PessoaEmpresa pe = db.pesquisaPessoaEmpresaPertencente(documento);

            // SOLICITAÇÃO CONFORME CHAMADO #1813 
            /*
                if (pe != null && pe.getJuridica().getId() != empresa.getId()) {
                    GenericaMensagem.warn("Atenção", "Esta pessoa pertence a Empresa " + pe.getJuridica().getPessoa().getNome());
                    fisica = new Fisica();
                    enderecoFisica = new PessoaEndereco();
                    return;
                }
             */
            FisicaDao fd = new FisicaDao();
            List<Fisica> listFisica = dbFis.pesquisaFisicaPorDocSemLike(fisica.getPessoa().getDocumento());
            if (listFisica.isEmpty()) {
                if (!fisica.getPessoa().getNome().isEmpty() && fisica.getDtNascimento() != null) {
                    Fisica f = (Fisica) fd.pesquisaFisicaPorNomeNascimento(fisica.getPessoa().getNome().trim(), fisica.getDtNascimento());
                    if (f != null) {
                        listFisica.add(f);
                    }
                }
            }
            if (!listFisica.isEmpty()) {
                for (int i = 0; i < listFisica.size(); i++) {
                    if (listFisica.get(i).getId() != fisica.getId()) {
                        Fisica f = listFisica.get(i);
                        if (f.getPessoa().getDocumento().equals("0") || f.getPessoa().getDocumento().isEmpty()) {
                            if (!documento.isEmpty()) {
                                f.getPessoa().setDocumento(documento);
                            }
                        }
                        fisica = f;
                        break;
                    }
                }
                if ((enderecoFisica = dbp.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1)) == null) {
                    enderecoFisica = new PessoaEndereco();
                }
            }

            Oposicao op = db.pesquisaFisicaOposicaoAgendamento(documento, empresa.getId(), DataHoje.ArrayDataHoje()[2] + DataHoje.ArrayDataHoje()[1]);
            if (op == null) {
                //GenericaMensagem.error("Erro", "Não foi possível pesquisar Oposição");
                op = new Oposicao();
            }

            if (fisica.getId() == -1 && op.getId() != -1) {
                fisica.getPessoa().setNome(op.getOposicaoPessoa().getNome());
                fisica.setRg(op.getOposicaoPessoa().getRg());
                fisica.setSexo(op.getOposicaoPessoa().getSexo());
                fisica.getPessoa().setDocumento(documento);
            }

            // VERIFICAÇÃO DE PESSOA EMPRESA SEM DEMISSAO
            //            if (fisica.getId() != -1){
            //                PessoaEmpresaDao dbx = new PessoaEmpresaDao();
            //                List<PessoaEmpresa> list_pe = dbx.listaPessoaEmpresaPorFisicaEmpresaDemissao(fisica.getId(), empresa.getId());
            //
            //                if (!list_pe.isEmpty()){
            //                    pessoaEmpresa = list_pe.get(0);
            //                    
            //                    if (pessoaEmpresa.getFuncao() != null)
            //                        profissao = pessoaEmpresa.getFuncao();
            //                }else{
            //                    if (validaAdmissao() && validaDemissao()){
            ////                        pessoaEmpresa = new PessoaEmpresa();
            //  //                      profissao = new Profissao();
            //                    }
            //                }
            //            }
            if (op.getId() != -1) {
                //msgConfirma = "Este CPF possui carta de oposição em "+op.getEmissao();
                //return;
            }
        } else if (fisica.getId() != -1) {
            fisica = new Fisica();
            enderecoFisica = new PessoaEndereco();
        }
        //FacesContext.getCurrentInstance().getExternalContext().redirect("/Sindical/webAgendamentoContabilidade.jsf");
    }

    public String pesquisaEndereco() {
        EnderecoDao db = new EnderecoDao();
        listaEnderecos.clear();
        if (!cepEndereco.isEmpty()) {
            listaEnderecos = db.pesquisaEnderecoCep(cepEndereco);
        }
        return null;
    }

    public void editarEndereco(Endereco endereco) {
        enderecoFisica.setEndereco(endereco);
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public void loadMotivoFalecimento() {
        Demissao demissao = (Demissao) new Dao().find(new Demissao(), Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()));
        if (!demissao.getMotivoWeb()) {
            GenericaMensagem.warn("Sistema", demissao.getMensagemMotivoWeb());
        }
    }

    public int getIdMotivoDemissao() {
        return idMotivoDemissao;
    }

    public void setIdMotivoDemissao(int idMotivoDemissao) {
        this.idMotivoDemissao = idMotivoDemissao;
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

    public PessoaEmpresa getPessoaEmpresa() {
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public String getStatusEmpresa() {
        HomologacaoDao db = new HomologacaoDao();
        if (empresa.getId() != -1) {
            listaEmDebito = db.pesquisaPessoaDebito(empresa.getPessoa().getId(), DataHoje.data());
        }
        if (!listaEmDebito.isEmpty()) {
            statusEmpresa = "EM DÉBITO";
        } else {
            // statusEmpresa = "REGULAR";
            statusEmpresa = "";
        }
        return statusEmpresa;
    }

    public void setStatusEmpresa(String statusEmpresa) {
        this.statusEmpresa = statusEmpresa;
    }

    public String getStrEndereco() {
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public PessoaEndereco getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public void setEnderecoEmpresa(PessoaEndereco enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }

    public Juridica getJuridica() {
        JuridicaDao db = new JuridicaDao();
        if (juridica.getId() == -1) {
            juridica = db.pesquisaJuridicaPorPessoa(((Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb")).getId());
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Juridica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public int getIdSelectRadio() {
        return idSelectRadio;
    }

    public void setIdSelectRadio(int idSelectRadio) {
        this.idSelectRadio = idSelectRadio;
    }

    public boolean isChkFiltrar() {
        return chkFiltrar;
    }

    public void setChkFiltrar(boolean chkFiltrar) {
        this.chkFiltrar = chkFiltrar;
    }

    public boolean isRenderBtnAgendar() {
        return renderBtnAgendar;
    }

    public void setRenderBtnAgendar(boolean renderBtnAgendar) {
        this.renderBtnAgendar = renderBtnAgendar;
    }

    public String getCepEndereco() {
        return cepEndereco;
    }

    public void setCepEndereco(String cepEndereco) {
        this.cepEndereco = cepEndereco;
    }

    public List<Endereco> getListaEnderecos() {
        return listaEnderecos;
    }

    public void setListaEnderecos(List<Endereco> listaEnderecos) {
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getFiltraPor() {
        return filtraPor;
    }

    public void setFiltraPor(String filtraPor) {
        this.filtraPor = filtraPor;
    }

    public String getStrContribuinte() {
        if (empresa.getId() != -1) {
            JuridicaDao db = new JuridicaDao();
            List listax = db.listaJuridicaContribuinte(empresa.getId());
            for (int i = 0; i < listax.size(); i++) {
                if (((List) listax.get(0)).get(11) != null) {
                    return strContribuinte = "Empresa Inativa";
                } else {
                    return strContribuinte = "";
                }
            }
        }
        return strContribuinte = "Empresa não contribuinte, não poderá efetuar um agendamento!";
    }

    public FilialCidade getSindicatoFilial() {
        return sindicatoFilial;
    }

    public void setSindicatoFilial(FilialCidade sindicatoFilial) {
        this.sindicatoFilial = sindicatoFilial;
    }

    public PessoaEndereco getEnderecoFilial() {
        PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
        Filial f = (Filial) new Dao().find(new Filial(), idFilial);
        if (enderecoFilial.getId() == -1) {
            enderecoFilial = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(f.getFilial().getPessoa().getId(), 2);
        }
        return enderecoFilial;
    }

    public void setEnderecoFilial(PessoaEndereco enderecoFilial) {
        this.enderecoFilial = enderecoFilial;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Agendamento getAgendamentoProtocolo() {
        return agendamentoProtocolo;
    }

    public void setAgendamentoProtocolo(Agendamento agendamentoProtocolo) {
        this.agendamentoProtocolo = agendamentoProtocolo;
    }

    public List<SelectItem> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(List<SelectItem> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public List<DataObject> getListaHorarios() {
        return listaHorarios;
    }

    public void setListaHorarios(List<DataObject> listaHorarios) {
        this.listaHorarios = listaHorarios;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public ConfiguracaoHomologacao getConfiguracaoHomologacao() {
        return configuracaoHomologacao;
    }

    public void setConfiguracaoHomologacao(ConfiguracaoHomologacao configuracaoHomologacao) {
        this.configuracaoHomologacao = configuracaoHomologacao;
    }

    public String getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(String tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public boolean isVisibleModal() {
        return visibleModal;
    }

    public void setVisibleModal(boolean visibleModal) {
        if (!visibleModal) {
            HorarioReservaDao hrd = new HorarioReservaDao();
            hrd.clear();
            hrd.begin();
            GlobalSync.load();
            loadListHorarios();
        }
        this.visibleModal = visibleModal;
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

    public Filial getFilialLocal() {
        return (Filial) new Dao().find(new Filial(), idFilial);
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

    // ARQUIVOS
    public void loadListFiles() {
        listFiles = new ArrayList();
        if (agendamento.getId() == -1) {
            listFiles = Diretorio.listaArquivos("/Arquivos/homologacao/validacao/" + empresa.getId() + "/" + uuidDir);
        } else {
            listFiles = Diretorio.listaArquivos("Arquivos/homologacao/" + agendamento.getId());
        }
    }

    public List getListFiles() {
        return listFiles;
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        if (agendamento.getId() == -1) {
            configuracaoUpload.setDiretorio("Arquivos/homologacao/validacao/" + empresa.getId() + "/" + uuidDir);
        } else {
            configuracaoUpload.setDiretorio("Arquivos/homologacao/" + agendamento.getId());
        }
        configuracaoUpload.setEvent(event);
        if (Upload.enviar(configuracaoUpload, true)) {
            listFiles.clear();
        }
        loadListFiles();
    }

    public void deleteFiles(int index) {
        String caminho = "";
        if (agendamento.getId() == -1) {
            caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/validacao/" + empresa.getId() + "/" + uuidDir + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        } else {
            caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/homologacao/" + agendamento.getId() + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        }
        File fl = new File(caminho);
        fl.delete();
        listFiles.remove(index);
        listFiles.clear();
        loadListFiles();
        PF.update("formAgendamentoContabilidade:id_grid_uploads");
        PF.update("formAgendamentoContabilidade:id_btn_anexo");
    }

    public List getListaEmDebito() {
        return listaEmDebito;
    }

    public void setListaEmDebito(List listaEmDebito) {
        this.listaEmDebito = listaEmDebito;
    }

    public ConfiguracaoArrecadacao getConfiguracaoArrecadacao() {
        return configuracaoArrecadacao;
    }

    public void setConfiguracaoArrecadacao(ConfiguracaoArrecadacao configuracaoArrecadacao) {
        this.configuracaoArrecadacao = configuracaoArrecadacao;
    }

    public Date getPolling() {
        return polling;
    }

    public void setPolling(Date polling) {
        this.polling = polling;
    }

    public String getUuidDir() {
        return uuidDir;
    }

    public void setUuidDir(String uuidDir) {
        this.uuidDir = uuidDir;
    }

    public List<SelectItem> getListNrRegistro() {
        return listNrRegistro;
    }

    public void setListNrRegistro(List<SelectItem> listNrRegistro) {
        this.listNrRegistro = listNrRegistro;
    }

    public Integer getNrRegistro() {
        return nrRegistro;
    }

    public void setNrRegistro(Integer nrRegistro) {
        this.nrRegistro = nrRegistro;
    }

    public void loadListNrRegistro() {
        listNrRegistro = new ArrayList();
        for (int i = 1; i < 6; i++) {
            listNrRegistro.add(new SelectItem(i, (i + "")));
        }
    }

}
