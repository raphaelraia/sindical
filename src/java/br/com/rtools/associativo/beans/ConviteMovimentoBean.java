package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.ConviteAutorizaCortesia;
import br.com.rtools.associativo.ConviteMovimento;
import br.com.rtools.associativo.ConviteServico;
import br.com.rtools.associativo.ConviteSuspencao;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.Suspencao;
import br.com.rtools.associativo.dao.ConviteDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.SuspencaoDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.ServicoValor;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoValorDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.SpcDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.sistema.dao.SisPessoaDao;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.PhotoCapture;
import br.com.rtools.utilitarios.ValidaDocumentos;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;

@ManagedBean
@SessionScoped
public class ConviteMovimentoBean implements Serializable {

    private ConviteMovimento conviteMovimento = new ConviteMovimento();
    private Movimento movimento = new Movimento();
    private Socios socios = new Socios();
    private Usuario usuario = new Usuario();
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private List<ConviteMovimento> conviteMovimentos = new ArrayList();
    private List<SelectItem> listPessoaAutoriza = new ArrayList();
    private List<SelectItem> conviteServicos = new ArrayList();

//    private StreamedContent fotoPerfilStreamed; //CARREGAR IMAGEM UPLOAD FOTO PHOTOCAM 
//    private StreamedContent fotoArquivoStreamed;
    //private String message = "";
    private String tipoCaptura = "";
    private String descricaoPesquisa = "";
    private String comoPesquisa = "";
    private String porPesquisa = "hoje";
    private String valorString = "";
    private String cliente = "";
    private int idServico = 0;
    private int idPessoaAutoriza = 0;
    private int idadeConvidado = 0;
    private boolean visibility = false;
    private boolean disabledConviteVencido = false;

    private boolean disabledValor = true;

    private String dataInicial = "";
    private String dataFinal = "";

    private ConfiguracaoSocial configuracaoSocial;
    //private double desconto = 0;

    private Integer indexTipoDocumento = 0;
    private List<SelectItem> listTipoDocumento = new ArrayList();

    public ConviteMovimentoBean() {
        loadUsuario();
        configuracaoSocial = new ConfiguracaoSocial();
        configuracaoSocial = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
        //PhotoCapture.load("temp/convite/" + usuario.getId(), "form_convite:panel_foto");
        loadListTipoDocumento();
    }

    public final void loadListTipoDocumento() {
        listTipoDocumento.clear();

        // REFERENTE A TABELA pes_tipo_documento E OBJETO TipoDocumento
        listTipoDocumento.add(new SelectItem(0, "CPF", "1"));
        listTipoDocumento.add(new SelectItem(1, "ESTRANGEIRO", "5"));
    }

    public void atualizaDescontoValor() {
        // loadValor() LIMPA O CAMPO DESCONTO, POR ISSO ELE É SETADO O ATUAL AQUI
        //String ds = conviteMovimento.getDescontoString();

        loadValor();
        // DEVOLVE O DESCONTO DIGITADO DEPOIS DE TER SIDO APAGADO NO loadValor()
        //conviteMovimento.setDesconto(Moeda.converteUS$(ds));

        double p_desconto = Moeda.converteUS$(Moeda.percentualDoValor(valorString, conviteMovimento.getDescontoString()));
        // DESCONTO NÃO PODE SER MAIOR QUE 80%, desconto setado igual a 20% onde 80 - 100 = 20
        // SE DESCONTO NÃO PODE SER MAIOR QUE 70%, desconto setado igual a 30% onde 70 - 100 = 30
        double p_valor = Moeda.converteUS$(Moeda.valorDoPercentual(valorString, "20"));

        if (p_desconto > 80) {
            GenericaMensagem.warn("ATENÇÃO", "VALOR DO DESCONTO NÃO PODE ULTRAPASSAR 80% ( R$ " + Moeda.converteR$Double(p_valor) + " ).");
            PF.update("form_convite:out_mensagem");
            PF.openDialog("dgl_panel_mensagem");
            conviteMovimento.setDesconto(0);
            return;
        }

        valorString = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(valorString), conviteMovimento.getDesconto()));
        // MENSAGEM COM O VALOR DO DESCONTO APLICADO
        //GenericaMensagem.info("DESCONTO DE R$ ", conviteMovimento.getDescontoString() + " APLICADO!");
    }

    public void novo() {
        boolean cc = conviteMovimento.isCortesia();
        Pessoa pe = conviteMovimento.getPessoa();
        conviteMovimento = new ConviteMovimento();
        conviteMovimento.setCortesia(cc);
        conviteMovimento.setPessoa(pe);

        conviteMovimento.setSisPessoa(new SisPessoa());
        //message = "";
        tipoCaptura = "";
        descricaoPesquisa = "";
        comoPesquisa = "";
        porPesquisa = "hoje";
        conviteMovimentos.clear();

        visibility = true;
        idadeConvidado = 0;
        movimento = new Movimento();

        atualizaDescontoValor();
        //atualizarCortesia();
    }

    public final void loadUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
    }

    public void atualizarCortesia() {
        idServico = 0;
        conviteServicos.clear();
        getConviteServicos();
        atualizaDescontoValor();
    }

    public void loadValor() {
        if (conviteMovimento.isCortesia() || conviteServicos.isEmpty() || conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
            disabledValor = true;
            valorString = "0,00";
            //conviteMovimento.setDesconto(0);
            return;
        }

        Dao dao = new Dao();
        ServicoValorDao svdb = new ServicoValorDao();
        try {
            DataHoje dh = new DataHoje();
            ServicoValor sv = (ServicoValor) svdb.pesquisaServicoValorPorIdade(((ConviteServico) dao.find(new ConviteServico(), Integer.parseInt(conviteServicos.get(idServico).getDescription()))).getServicos().getId(), dh.calcularIdade(conviteMovimento.getSisPessoa().getNascimento()));
            valorString = Moeda.converteR$(Double.toString((sv.getValor())));

            if (sv.getServicos().isAlterarValor()) {
                disabledValor = false;
            } else {
                disabledValor = true;
            }

        } catch (NumberFormatException e) {
            disabledValor = true;
            valorString = "0,00";
        }

        //conviteMovimento.setDesconto(0);
    }

    public void openDialog() {
        visibility = true;
        porPesquisa = "hoje";
        conviteMovimentos = new ArrayList();
        PF.update("form_convite");
    }

    public void close() {
        valorString = "";
        idServico = 0;
        conviteMovimento = new ConviteMovimento();
        conviteMovimento.getSisPessoa().setDtNascimento(null);
        conviteMovimento.setSisPessoa(new SisPessoa());
        pessoaEndereco = new PessoaEndereco();
        socios = new Socios();
        //message = "";
        tipoCaptura = "";
        descricaoPesquisa = "";
        comoPesquisa = "";
        porPesquisa = "hoje";
        conviteMovimentos.clear();

        idadeConvidado = 0;
        movimento = new Movimento();
        listPessoaAutoriza.clear();

        atualizaDescontoValor();
        atualizarCortesia();

        visibility = false;
        indexTipoDocumento = 0;
        PF.update("form_convite");
    }

    public boolean validaSave() {

        if (configuracaoSocial.getCartaoDigitos() <= 0) {
            GenericaMensagem.fatal("ATENÇÃO", "CARTÃO NÃO TEM CÓDIGO DE BARRAS CONFIGURADO!");
            return false;
        }

        if (MacFilial.getAcessoFilial().getId() == -1) {
            if (conviteMovimento.getId() == -1) {
                if (!conviteMovimento.isCortesia()) {
                    GenericaMensagem.warn("ATENÇÃO", "PARA SALVER CONVITES É NECESSÁRIO UMA FILIAL EM SUA ESTAÇÃO DE TRABALHO!");
                    //message = "Para salvar convites não cortesia configurar Filial em sua estação trabalho!";
                    return false;
                }
            }
        }

        if (conviteServicos.isEmpty()) {
            //message = "Cadastrar serviços!";
            GenericaMensagem.warn("ATENÇÃO", "CADASTRAR SERVIÇOS!");
            return false;
        }

        if (conviteMovimento.getPessoa().getId() == -1) {
            //message = "Pesquisar sócio!";
            GenericaMensagem.warn("ATENÇÃO", "PESQUISAR SÓCIO!");
            return false;
        }

        if (!conviteMovimento.getSisPessoa().getDocumento().isEmpty()) {
            // SE O TIPO DE DOCUMENTO FOR IGUAL A CPF
            if (listTipoDocumento.get(indexTipoDocumento).getDescription().equals("1")) {
                if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(conviteMovimento.getSisPessoa().getDocumento()))) {
                    GenericaMensagem.warn("ATENÇÃO", "CPF DIGITADO INVÁLIDO!");
                    return false;
                }
            }
        }

        if (conviteMovimento.getSisPessoa().getNome().isEmpty()) {
            //message = "Informar nome do convidado!";
            GenericaMensagem.warn("ATENÇÃO", "INFORMAR NOME DO CONVIDADO!");
            return false;
        }

        if (conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
            //message = "Informar data de nascimento do convidado!";
            GenericaMensagem.warn("ATENÇÃO", "INFORMAR DATA DE NASCIMENTO DO CONVIDADO!");
            return false;
        }

        // EXIGE UMA IDADE MÍNIMA OBRIGATÓRIA PARA O USO DO CPF
        if (conviteMovimento.getSisPessoa().getDocumento().isEmpty()) {
            if (configuracaoSocial.getIdadeBloqueioCpfConvite() > 0) {
                if (conviteMovimento.getSisPessoa().getIdade() >= configuracaoSocial.getIdadeBloqueioCpfConvite()) {
                    GenericaMensagem.warn("ATENÇÃO", "A PARTIR DOS " + conviteMovimento.getSisPessoa().getIdade() + " ANOS É OBRIGATÓRIO APRESENTA CPF!");
                    return false;
                }
            }
        }

        // EXIGE UMA IDADE MÍNIMA OBRIGATÓRIA PARA O USO DO RG
        if (conviteMovimento.getSisPessoa().getRg().isEmpty()) {
            if (configuracaoSocial.getIdadeBloqueioRgConvite() > 0) {
                if (conviteMovimento.getSisPessoa().getIdade() >= configuracaoSocial.getIdadeBloqueioRgConvite()) {
                    GenericaMensagem.warn("ATENÇÃO", "A PARTIR DOS " + conviteMovimento.getSisPessoa().getIdade() + " ANOS É OBRIGATÓRIO APRESENTAR RG!");
                    return false;
                }
            }
        }

        return true;
    }

    public boolean validaSaveConvite() {
        // VALIDAÇÃO SÓCIO
        // -------------------------------
        SpcDao spcDB = new SpcDao();
        if (spcDB.existeRegistroPessoaSPC(conviteMovimento.getPessoa())) {
            //message = "Existem débitos com o sindicato (SPC)!";
            GenericaMensagem.warn("ATENÇÃO", "EXISTEM DÉBITOS COM O SINDICATO (SPC)!");
            return false;
        }

        // SE POSSUIR DÉBITOS
        if (new FunctionsDao().inadimplente(conviteMovimento.getPessoa().getId())) {
            // E VALOR FOR <= 0 BLOQUEAR COM MENSAGEM
            if (Moeda.converteUS$(valorString) <= 0) {
                //message = "Sócio possui débitos!";
                GenericaMensagem.warn("ATENÇÃO", "SÓCIO POSSUI DÉBITOS!");
                return false;
            }
        }

        if (conviteMovimento.getPessoa().getDocumento().isEmpty() || conviteMovimento.getPessoa().getDocumento().equals("0")) {
            //message = "Sócio sem CPF para pesquisar Oposição!";
            GenericaMensagem.warn("ATENÇÃO", "SÓCIO SEM CPF PARA PESQUISAR OPOSIÇÃO!");
            return false;
        }

        OposicaoDao odbt = new OposicaoDao();
        if (configuracaoSocial.getBloqueiaConviteOposicao()) {
            Boolean temOposicao = odbt.existPessoaDocumentoPeriodo(conviteMovimento.getPessoa().getDocumento());
            if (temOposicao) {
                //message = "Sócio cadastrado em Oposição!";
                GenericaMensagem.fatal("ATENÇÃO", "SÓCIO CADASTRADO EM OPOSIÇÃO!");
                return false;
            }
        } else {
            Boolean temOposicao = odbt.existPessoaDocumentoPeriodo(conviteMovimento.getPessoa().getDocumento());
            if (temOposicao) {
                //message = "Sócio cadastrado em Oposição!";
                GenericaMensagem.fatal("ATENÇÃO", "SÓCIO CADASTRADO EM OPOSIÇÃO!");
            }
        }

        SuspencaoDao s_dao = new SuspencaoDao();
        Suspencao susp = s_dao.pesquisaSuspensao(conviteMovimento.getPessoa());
        if (conviteMovimento.isCortesia()) {
            if (susp != null) {
                GenericaMensagem.fatal("ATENÇÃO", "SÓCIO COM SUSPENSÃO CADASTRADA!");
                GenericaMensagem.warn("MOTIVO", susp.getMotivo());
                return false;
            }
        }
        // FIM VALIDAÇÃO SÓCIO -----------
        // -------------------------------
        // -------------------------------

        // VALIDAÇÃO CONVIDADO
        // -------------------------------
        Dao dao = new Dao();
        Registro r = (Registro) dao.find(new Registro(), 1);
        ConviteDao cdb = new ConviteDao();
        if (conviteMovimento.isCortesia()) {
            if (cdb.limiteConvitePorSocio(r.getConviteQuantidadeSocio(), r.getConviteDiasSocio(), conviteMovimento.getPessoa().getId())) {
                GenericaMensagem.warn("ATENÇÃO", "Limite de convites excedido para este sócio! Este sócio tem direito a disponibilizar " + r.getConviteQuantidadeSocio() + " convite(s) a cada " + r.getConviteDiasSocio() + "dia(s)");
                return false;
            }

            if (cdb.limiteConviteConvidado(r.getConviteQuantidadeConvidado(), r.getConviteDiasConvidado(), conviteMovimento.getSisPessoa().getId())) {
                GenericaMensagem.warn("ATENÇÃO", "Limite de convites excedido para convidado! Este convidado tem direito a " + r.getConviteQuantidadeConvidado() + " a cada " + r.getConviteDiasConvidado() + "dia(s)");
                return false;
            }

            SociosDao sociosDB = new SociosDao();
            Socios socio_convidado = sociosDB.pesquisaSocioPorPessoaAtivoDocumento(conviteMovimento.getSisPessoa().getDocumento());
            if (socio_convidado.getId() != -1) {
                Categoria c = cdb.pesquisaCategoriaTodosDiasClube(socio_convidado.getMatriculaSocios().getCategoria().getId());
                if (c != null) {
                    GenericaMensagem.warn("ATENÇÃO", "CONVIDADO NÃO PODE SER SÓCIO ATIVO!");
                    return false;
                }
            }

            ConviteSuspencao c_susp = new ConviteSuspencao();
            c_susp.setSisPessoa(conviteMovimento.getSisPessoa());
            if (cdb.existeSisPessoaSuspensa(c_susp)) {
                GenericaMensagem.warn("ATENÇÃO", "CONVIDADO POSSUI CADASTRO SUSPENSO!");
                return false;
            }
        } else if (Moeda.converteUS$(valorString) <= 0 && conviteMovimento.getDesconto() != 0) {
            GenericaMensagem.warn("ATENÇÃO", "INFORMAR O VALOR DO SERVIÇO, FAIXA ETÁRIA NÃO POSSUI VALOR DO SERVIÇO!");
            return false;
        }

        if (idadeConvidado >= 16) {
            if (conviteMovimento.getSisPessoa().getDocumento().isEmpty() || conviteMovimento.getSisPessoa().getDocumento().equals("0")) {
                GenericaMensagem.warn("ATENÇÃO", "CPF OBRIGATÓRIO PARA CONVIDADO MAIOR DE 16 ANOS!");
                return false;
            }
        }

        if (!conviteMovimento.getSisPessoa().getDocumento().isEmpty() && !conviteMovimento.getSisPessoa().getDocumento().equals("0")) {
            FisicaDao fdb = new FisicaDao();
            List<Fisica> f_convidado = fdb.pesquisaFisicaPorDoc(conviteMovimento.getSisPessoa().getDocumento());

            if (!f_convidado.isEmpty()) {
                // SE POSSUIR DÉBITOS
                if (new FunctionsDao().inadimplente(f_convidado.get(0).getPessoa().getId())) {
                    // E VALOR FOR <= 0, BLOQUEAR COM MENSAGEM
                    if (Moeda.converteUS$(valorString) <= 0) {
                        GenericaMensagem.warn("ATENÇÃO", "CONVIDADO POSSUI DÉBITOS!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String save() {
        if (!validaSave()) {
            return null;
        }

        Dao dao = new Dao();
        if (conviteMovimento.isCortesia()) {
            conviteMovimento.setAutorizaCortesia((ConviteAutorizaCortesia) dao.find(new ConviteAutorizaCortesia(), Integer.parseInt(listPessoaAutoriza.get(idPessoaAutoriza).getDescription())));
        } else {
            conviteMovimento.setAutorizaCortesia(null);
        }

        conviteMovimento.setConviteServico((ConviteServico) dao.find(new ConviteServico(), Integer.parseInt(conviteServicos.get(idServico).getDescription())));

        if (conviteMovimento.getSisPessoa().getEndereco() == null || conviteMovimento.getSisPessoa().getEndereco().getId() == -1) {
            conviteMovimento.getSisPessoa().setEndereco(null);
        }

        NovoLog novoLog = new NovoLog();

        dao.openTransaction();
        // SALVAR sis_pessoa ------------------------
        conviteMovimento.getSisPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), Integer.valueOf(listTipoDocumento.get(indexTipoDocumento).getDescription())));
        if (conviteMovimento.getSisPessoa().getId() == -1) {
            conviteMovimento.getSisPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            if (!dao.save(conviteMovimento.getSisPessoa())) {
                dao.rollback();
                //message = "Erro ao inserir sis pessoa!";
                GenericaMensagem.fatal("ATENÇÃO", "ERRO AO INSERIR SIS PESSOA!");
                return null;
            }

            dao.commit();
        } else {
            if (!dao.update(conviteMovimento.getSisPessoa())) {
                dao.rollback();
                //message = "Erro ao atualizar sis pessoa!";
                GenericaMensagem.fatal("ATENÇÃO", "ERRO AO ATUALIZAR SIS PESSOA!");
                return null;
            }

            dao.commit();
        }
        // FIM SALVAR sis_pessoa ------------------------

        DataHoje dh = new DataHoje();
        conviteMovimento.setValidade(dh.incrementarDias(configuracaoSocial.getValidadeDiaConvite(), DataHoje.data()));

        // SALVAR CONVITE -----------------------------
        dao.openTransaction();
        if (conviteMovimento.getId() == -1) {

            if (!validaSaveConvite()) {
                return null;
            }

            conviteMovimento.setUsuario(usuario);
            conviteMovimento.setEvt(null);
            conviteMovimento.setDepartamento(null);
            conviteMovimento.setUsuarioInativacao(null);

            if (!dao.save(conviteMovimento)) {
                dao.rollback();
                //message = "Erro ao inserir registro!";
                GenericaMensagem.fatal("ATENÇÃO", "ERRO AO INSERIR REGISTRO!");
                return null;
            }

            if (conviteMovimento.isCortesia()) {
                novoLog.save(""
                        + "ID: " + conviteMovimento.getId()
                        + " - Emissão: " + conviteMovimento.getEmissao()
                        + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                        + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                        + " - Validade: " + conviteMovimento.getValidade()
                        + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                        + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
                );
            } else {
                double valor = Moeda.substituiVirgulaDouble(valorString);
                if (valor > 0) {
                    try {
                        if (!gerarMovimento(dao)) {
                            dao.rollback();
                            //message = "Erro ao inserir registro!";
                            GenericaMensagem.fatal("ATENÇÃO", "ERRO AO INSERIR REGISTRO!");
                            return null;
                        }
                    } catch (Exception e) {
                        dao.rollback();
                        //message = "Erro ao inserir registro!";
                        GenericaMensagem.fatal("ATENÇÃO", "ERRO AO INSERIR REGISTRO!");
                        return null;
                    }
                }
                novoLog.save(""
                        + "ID: " + conviteMovimento.getId()
                        + " - Emissão: " + conviteMovimento.getEmissao()
                        + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                        + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                        + " - Validade: " + conviteMovimento.getValidade()
                        + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
                );
            }

            // LIMPAR DADOS DO CONVIDADO
            //conviteMovimento.setSisPessoa(new SisPessoa());
            //message = "Registro inserido com sucesso";
            GenericaMensagem.info("SUCESSO", "CONVITE FOI SALVO!");
        } else {
            ConviteMovimento cm = (ConviteMovimento) dao.find(new ConviteMovimento(), conviteMovimento.getId());
            String beforeUpdate = ""
                    + "ID: " + cm.getId()
                    + " - Emissão: " + cm.getEmissao()
                    + " - SisPessoa: (" + cm.getSisPessoa().getId() + ") " + cm.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + cm.getPessoa().getId() + ") " + cm.getPessoa().getNome()
                    + " - Validade: " + cm.getValidade()
                    + (cm.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + cm.getAutorizaCortesia().getId() + ") " + cm.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (cm.getConviteServico() != null ? " - Convite Serviço: (" + cm.getConviteServico().getId() + ") " + cm.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)");

            if (!dao.update(conviteMovimento)) {
                dao.rollback();
                //message = "Erro ao atualizar registro!";
                GenericaMensagem.fatal("ATENÇÃO", "ERRO AO ATUALIZAR REGISTRO!");
                return null;
            }

            novoLog.update(beforeUpdate, ""
                    + "ID: " + conviteMovimento.getId()
                    + " - Emissão: " + conviteMovimento.getEmissao()
                    + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                    + " - Validade: " + conviteMovimento.getValidade()
                    + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
            );
            //message = "Registro atualizado com sucesso";
            GenericaMensagem.info("SUCESSO", "CONVITE ATUALIZADO!!");
        }
        dao.commit();
        // FIM SALVAR CONVITE -----------------------------

        NovoLog log = new NovoLog();
        log.save(conviteMovimento.toString());
        return null;
    }

    public boolean copiarArquivos(String path_arquivo, String path_destino) {
        try {
            FileInputStream origem;
            FileOutputStream destino;

            FileChannel fcOrigem;
            FileChannel fcDestino;

            origem = new FileInputStream(path_arquivo); // ARQUIVO QUE VOCÊ QUER COPIAR
            destino = new FileOutputStream(path_destino); // ONDE A COPIA SERÁ SALVA

            fcOrigem = origem.getChannel();
            fcDestino = destino.getChannel();

            fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);

            origem.close();
            destino.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConviteMovimentoBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(ConviteMovimentoBean.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Fisica> retornaFisicaSisPessoa(String tipo_pesquisa) {
        FisicaDao db = new FisicaDao();
        List<Fisica> lf = new ArrayList();

        if (tipo_pesquisa.equals("cpf")) {
            lf = db.pesquisaFisicaPorDoc(conviteMovimento.getSisPessoa().getDocumento());
        } else if (tipo_pesquisa.equals("rg")) {
            lf = db.pesquisaFisicaPorDocRG(conviteMovimento.getSisPessoa().getRg());
        } else if (tipo_pesquisa.equals("nome_nascimento")) {
            Fisica flf = db.pesquisaFisicaPorNomeNascimento(conviteMovimento.getSisPessoa().getNome(), conviteMovimento.getSisPessoa().getDtNascimento());
            if (flf != null) {
                lf.add(flf);
            }
        }

        if (!lf.isEmpty()) {
            String doc = "";
            String rg = "";
            if (!conviteMovimento.getSisPessoa().getDocumento().isEmpty()) {
                doc = conviteMovimento.getSisPessoa().getDocumento();
            }
            if (!conviteMovimento.getSisPessoa().getRg().isEmpty()) {
                rg = conviteMovimento.getSisPessoa().getRg();
            }
            conviteMovimento.setSisPessoa(new SisPessoa());
            if (!lf.get(0).getPessoa().getDocumento().isEmpty()) {
                conviteMovimento.getSisPessoa().setDocumento(lf.get(0).getPessoa().getDocumento());
            } else {
                conviteMovimento.getSisPessoa().setDocumento(doc);
            }
            if (!lf.get(0).getRg().isEmpty()) {
                conviteMovimento.getSisPessoa().setRg(lf.get(0).getRg());
            } else {
                conviteMovimento.getSisPessoa().setRg(rg);
            }
            conviteMovimento.getSisPessoa().setNome(lf.get(0).getPessoa().getNome());
            conviteMovimento.getSisPessoa().setNascimento(lf.get(0).getNascimento());
            conviteMovimento.getSisPessoa().setCelular(lf.get(0).getPessoa().getTelefone3());
            conviteMovimento.getSisPessoa().setEmail1(lf.get(0).getPessoa().getEmail1());
            conviteMovimento.getSisPessoa().setEmail2(lf.get(0).getPessoa().getEmail2());
            conviteMovimento.getSisPessoa().setObservacao(lf.get(0).getPessoa().getObs());
            conviteMovimento.getSisPessoa().setSexo(lf.get(0).getSexo());
            conviteMovimento.getSisPessoa().setTipoDocumento(lf.get(0).getPessoa().getTipoDocumento());
            conviteMovimento.getSisPessoa().setTelefone(lf.get(0).getPessoa().getTelefone1());

            if (lf.get(0).getPessoa().getPessoaEndereco() != null) {
                conviteMovimento.getSisPessoa().setEndereco(lf.get(0).getPessoa().getPessoaEndereco().getEndereco());
                conviteMovimento.getSisPessoa().setNumero(lf.get(0).getPessoa().getPessoaEndereco().getNumero());
                conviteMovimento.getSisPessoa().setComplemento(lf.get(0).getPessoa().getPessoaEndereco().getComplemento());
            }

            conviteMovimento.getSisPessoa().setFisica(lf.get(0));
            return lf;
        }
        return lf;
    }

    public void pesquisaSisPessoaDocumento() {
        // APENAS COM CPF
        if (conviteMovimento.getSisPessoa().getId() == -1 && (conviteMovimento.getSisPessoa().getFisica() == null || conviteMovimento.getSisPessoa().getFisica().getId() == -1)) {
            SisPessoaDao sisPessoaDB = new SisPessoaDao();
            if (!conviteMovimento.getSisPessoa().getDocumento().isEmpty()) {

                // VALIDA SE CONVIDADO TEM OPOSIÇÃO --------------------------------
                OposicaoDao odbt = new OposicaoDao();
                if (configuracaoSocial.getBloqueiaConviteOposicao()) {
                    Boolean temOposicao = odbt.existPessoaDocumentoPeriodo(conviteMovimento.getSisPessoa().getDocumento());
                    // BLOQUEIA SE TIVER OPOSIÇÃO
                    if (temOposicao) {
                        GenericaMensagem.fatal("ATENÇÃO", "CONVIDADO CADASTRADO EM OPOSIÇÃO!");
                        PF.openDialog("dgl_panel_mensagem");

                        // LIMPA SE TIVER OPOSIÇÃO
                        conviteMovimento.setSisPessoa(new SisPessoa());
                        return;
                    }
                } else {
                    // APENAS MOSTRA A MENSAGEM
                    Boolean temOposicao = odbt.existPessoaDocumentoPeriodo(conviteMovimento.getSisPessoa().getDocumento());
                    if (temOposicao) {
                        GenericaMensagem.fatal("ATENÇÃO", "CONVIDADO CADASTRADO EM OPOSIÇÃO!");
                        PF.update("form_convite:out_mensagem");
                        PF.openDialog("dgl_panel_mensagem");
                    }
                }
                // -----------------------------------------------------------------

                SisPessoa sp = sisPessoaDB.sisPessoaExiste(conviteMovimento.getSisPessoa(), true);
                if (sp != null) {
                    conviteMovimento.setSisPessoa(sp);
                } else {
                    List<Fisica> lf = retornaFisicaSisPessoa("cpf");
                    // SE FOR VAZIO NÃO ENCONTROU O CPF EM PESSOA FÍSICA
                    // SE FOR CHEIO PESSOA FISICA JÁ SETADA EM SIS PESSOA
                    if (lf.isEmpty()) {
                        String d = conviteMovimento.getSisPessoa().getDocumento();

                        // LIMPA SE NÃO ENCONTROU O DOCUMENTO
                        if (!conviteMovimento.getSisPessoa().getNome().isEmpty() || !conviteMovimento.getSisPessoa().getRg().isEmpty() || !conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {

                        } else {
                            conviteMovimento.setSisPessoa(new SisPessoa());
                        }
                        conviteMovimento.getSisPessoa().setDocumento(d);
                    }
                }
            }
        }
        atualizaDescontoValor();
    }

    public void pesquisaSisPessoaRG() {
        if (conviteMovimento.getSisPessoa().getId() == -1 && (conviteMovimento.getSisPessoa().getFisica() == null || conviteMovimento.getSisPessoa().getFisica().getId() == -1)) {
            // RG
            SisPessoaDao sisPessoaDB = new SisPessoaDao();
            if (conviteMovimento.getSisPessoa().getId() == -1 && !conviteMovimento.getSisPessoa().getRg().isEmpty()) {
                SisPessoa sp = sisPessoaDB.sisPessoaExiste(conviteMovimento.getSisPessoa(), true);
                if (sp != null) {
                    conviteMovimento.setSisPessoa(sp);
                } else {
                    List<Fisica> lf = retornaFisicaSisPessoa("rg");
                    // SE FOR VAZIO NÃO ENCONTROU O RG EM PESSOA FÍSICA
                    // SE FOR CHEIO PESSOA FISICA JÁ SETADA EM SIS PESSOA
                    if (lf.isEmpty()) {
                        String d = conviteMovimento.getSisPessoa().getDocumento();
                        String r = conviteMovimento.getSisPessoa().getRg();
                        // LIMPA SE NÃO ENCONTROU O DOCUMENTO
                        conviteMovimento.setSisPessoa(new SisPessoa());
                        conviteMovimento.getSisPessoa().setDocumento(d);
                        conviteMovimento.getSisPessoa().setRg(r);
                    }
                }
            }
        }
        atualizaDescontoValor();
    }

    public void pesquisaSisPessoaNomeNascimento() {
        if (conviteMovimento.getSisPessoa().getId() == -1 && (conviteMovimento.getSisPessoa().getFisica() == null || conviteMovimento.getSisPessoa().getFisica().getId() == -1)) {
            // NOME / DATA DE NASCIMENTO    
            SisPessoaDao sisPessoaDB = new SisPessoaDao();
            if (!conviteMovimento.getSisPessoa().getNome().isEmpty() && !conviteMovimento.getSisPessoa().getNascimento().isEmpty()) {
                SisPessoa sp = sisPessoaDB.sisPessoaExiste(conviteMovimento.getSisPessoa());
                if (sp != null) {
                    conviteMovimento.setSisPessoa(sp);
                } else {
                    List<Fisica> lf = retornaFisicaSisPessoa("nome_nascimento");
                    // SE FOR VAZIO NÃO ENCONTROU O NOME E NASCIMENTO EM PESSOA FÍSICA
                    // SE FOR CHEIO PESSOA FISICA JÁ SETADA EM SIS PESSOA
                    if (lf.isEmpty()) {
                        String d = conviteMovimento.getSisPessoa().getDocumento();

                        // LIMPA SE NÃO ENCONTROU PELO NOME E NASCIMENTO
                        //conviteMovimento.setSisPessoa(new SisPessoa());
                        //conviteMovimento.getSisPessoa().setDocumento(d);
                    } else {
//                        if (conviteMovimento.getSisPessoa().getId() == -1) {
//                            Fisica f = (Fisica) new Dao().find(lf.get(0));
//                            conviteMovimento.getSisPessoa().setFisica(lf.get(0));
//                            conviteMovimento.getSisPessoa().setSexo(f.getSexo());
//                            if (!f.getPessoa().getDocumento().isEmpty()) {
//                                conviteMovimento.getSisPessoa().setDocumento(f.getPessoa().getDocumento());
//                            }
//                            if (!f.getRg().isEmpty()) {
//                                conviteMovimento.getSisPessoa().setRg(f.getRg());
//                            }
//                            if (!f.getPessoa().getTelefone1().isEmpty()) {
//                                conviteMovimento.getSisPessoa().setTelefone(f.getPessoa().getTelefone1());
//                            }
//                            if (!f.getPessoa().getTelefone3().isEmpty()) {
//                                conviteMovimento.getSisPessoa().setCelular(f.getPessoa().getTelefone3());
//                            }
//                            if (f.getPessoa().getPessoaEndereco() != null && f.getPessoa().getPessoaEndereco().getId() != -1) {
//                                conviteMovimento.getSisPessoa().setEndereco(f.getPessoa().getPessoaEndereco().getEndereco());
//                                conviteMovimento.getSisPessoa().setNumero(f.getPessoa().getPessoaEndereco().getNumero());
//                                conviteMovimento.getSisPessoa().setComplemento(f.getPessoa().getPessoaEndereco().getComplemento());
//                            }
//                        }
                    }
                }
            }
        }
        atualizaDescontoValor();
    }

    public void delete() {
        if (conviteMovimento.getId() != -1) {
            Dao dao = new Dao();
            conviteMovimento.setUsuarioInativacao((Usuario) dao.find(new Usuario(), getUsuario().getId()));
            conviteMovimento.setAtivo(false);
            dao.openTransaction();
            if (!dao.update(conviteMovimento)) {
                dao.rollback();
                //message = "Erro ao inativar registro!";
                GenericaMensagem.warn("ATENÇÃO", "ERRO AO INATIVAR CONVITE!");
                return;
            }

            if (getMovimento().getId() != -1) {
                getMovimento().setAtivo(false);
                if (!dao.update(getMovimento())) {
                    dao.rollback();
                    return;
                }
            }

            NovoLog novoLog = new NovoLog();
            novoLog.delete(""
                    + "ID: " + conviteMovimento.getId()
                    + " - Emissão: " + conviteMovimento.getEmissao()
                    + " - SisPessoa: (" + conviteMovimento.getSisPessoa().getId() + ") " + conviteMovimento.getSisPessoa().getNome()
                    + " - Responsável (Pessoa): (" + conviteMovimento.getPessoa().getId() + ") " + conviteMovimento.getPessoa().getNome()
                    + " - Validade: " + conviteMovimento.getValidade()
                    + (conviteMovimento.getAutorizaCortesia() != null ? " - Autorizado por (Pessoa): (" + conviteMovimento.getAutorizaCortesia().getId() + ") " + conviteMovimento.getAutorizaCortesia().getPessoa().getNome() : "")
                    + (conviteMovimento.getConviteServico() != null ? " - Convite Serviço: (" + conviteMovimento.getConviteServico().getId() + ") " + conviteMovimento.getConviteServico().getServicos().getDescricao() : " - Convite Serviço: (null)")
            );

            apagarImagem("perfil", dao);
            apagarImagem("documento", dao);

            dao.commit();
        }
        novo();
        GenericaMensagem.info("SUCESSO", "CONVITE INATIVADO!");
    }

    public void edit(ConviteMovimento cm) {
        conviteMovimento = (ConviteMovimento) new Dao().find(cm);
        getConviteMovimento();
        carregaSocio(conviteMovimento.getPessoa());
        carregaEndereco(conviteMovimento.getPessoa());

        if (conviteMovimento.getSisPessoa().getEndereco() == null) {
            conviteMovimento.getSisPessoa().setEndereco(new Endereco());
        }

        for (int i = 0; i < listTipoDocumento.size(); i++) {
            if (Integer.parseInt(listTipoDocumento.get(i).getDescription()) == conviteMovimento.getSisPessoa().getTipoDocumento().getId()) {
                indexTipoDocumento = i;
                break;
            }
        }

        if (conviteMovimento.getAutorizaCortesia() != null) {
            listPessoaAutoriza.clear();
            for (int i = 0; i < getListPessoaAutoriza().size(); i++) {
                if (Integer.parseInt(getListPessoaAutoriza().get(i).getDescription()) == conviteMovimento.getAutorizaCortesia().getId()) {
                    idPessoaAutoriza = i;
                    break;
                }
            }
        }

        idServico = 0;
        conviteServicos.clear();
        getConviteServicos();
        if (conviteMovimento.getConviteServico() != null) {
            for (int i = 0; i < conviteServicos.size(); i++) {
                if (Integer.parseInt(conviteServicos.get(i).getDescription()) == conviteMovimento.getConviteServico().getId()) {
                    idServico = i;
                    break;
                }
            }
        }

        atualizaDescontoValor();
        //valorString = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(valorString), conviteMovimento.getDesconto()));

        visibility = true;
    }

    public List<ConviteMovimento> getConviteMovimentos() {
        if (conviteMovimentos.isEmpty()) {
            if (porPesquisa.equals("hoje")) {
                descricaoPesquisa = "";
            }
            conviteMovimentos = (List<ConviteMovimento>) new ConviteDao().pesquisaConviteMovimento(descricaoPesquisa, porPesquisa, comoPesquisa, dataInicial, dataFinal);
        }
        return conviteMovimentos;
    }

    public void setConviteMovimentos(List<ConviteMovimento> conviteMovimentos) {
        this.conviteMovimentos = conviteMovimentos;
    }

    public ConviteMovimento getConviteMovimento() {
        if (conviteMovimento.getId() != -1) {
            visibility = true;
        }

        if (GenericaSessao.exists("fisicaPesquisa")) {
            Pessoa p = (Pessoa) ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            conviteMovimento.setPessoa(p);
            carregaSocio(p);
            visibility = true;
            carregaEndereco(p);
        }

        if (GenericaSessao.exists("sisPessoaPesquisa")) {
            conviteMovimento.setSisPessoa(((SisPessoa) GenericaSessao.getObject("sisPessoaPesquisa", true)));
            visibility = true;
        }

        if (GenericaSessao.exists("enderecoPesquisa")) {
            conviteMovimento.getSisPessoa().setEndereco((Endereco) GenericaSessao.getObject("enderecoPesquisa", true));
            visibility = true;
        }
        return conviteMovimento;
    }

    public void setConviteMovimento(ConviteMovimento conviteMovimento) {
        this.conviteMovimento = conviteMovimento;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public void carregaSocio(Pessoa p) {
        SociosDao dB = new SociosDao();
        socios = dB.pesquisaSocioPorPessoa(p.getId());
    }

    public void carregaEndereco(Pessoa p) {
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        int idEndereco[] = new int[]{1, 2, 3, 4};
        for (int i = 0; i < idEndereco.length; i++) {
            pessoaEndereco = (PessoaEndereco) dao.pesquisaEndPorPessoaTipo(p.getId(), idEndereco[i]);
            if (pessoaEndereco == null) {
                pessoaEndereco = new PessoaEndereco();
            } else {
                break;
            }
        }
    }

    public List<SelectItem> getConviteServicos() {
        if (conviteServicos.isEmpty()) {
            int diaDaSemana = DataHoje.diaDaSemana(new Date());
            List<ConviteServico> list = new ConviteDao().listaConviteServicoCortesia(conviteMovimento.isCortesia());
            int i = 0;
            boolean ok = false;
            for (ConviteServico cs : list) {
                List listSemana = new ArrayList();
                if (cs.isDomingo()) {
                    listSemana.add("Dom");
                    if (diaDaSemana == 1 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isSegunda()) {
                    listSemana.add("Seg");
                    if (diaDaSemana == 2 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isTerca()) {
                    listSemana.add("Ter");
                    if (diaDaSemana == 3 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isQuarta()) {
                    listSemana.add("Qua");
                    if (diaDaSemana == 4 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isQuinta()) {
                    listSemana.add("Qui");
                    if (diaDaSemana == 5 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isSexta()) {
                    listSemana.add("Sex");
                    if (diaDaSemana == 6 && !ok) {
                        idServico = i;
                    }
                }
                if (cs.isSabado()) {
                    if (diaDaSemana == 7 && !ok) {
                        idServico = i;
                    }
                    listSemana.add("Sáb");
                }
                if (cs.isFeriado()) {
                    listSemana.add("Feriado");
                }
                conviteServicos.add(new SelectItem(i, cs.getServicos().getDescricao() + " " + listSemana, "" + cs.getId()));
                i++;
            }
        }
        return conviteServicos;
    }

    public void setConviteServicos(List<SelectItem> conviteServicos) {
        this.conviteServicos = conviteServicos;
    }

    public PessoaEndereco getPessoaEndereco() {
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public String getTipoCaptura() {
        return tipoCaptura;
    }

    public void setTipoCaptura(String tipoCaptura) {
        this.tipoCaptura = tipoCaptura;
    }

    public void capturarTipo(String tipoCaptura) {
        this.tipoCaptura = tipoCaptura;
        if (tipoCaptura.equals("perfil")) {
            new PhotoCapture().openAndSave(conviteMovimento.getSisPessoa(), "perfil", "form_convite:panel_foto");
        } else {
            new PhotoCapture().openAndSave(conviteMovimento.getSisPessoa(), "documento", "form_convite:panel_foto");
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public void apagarImagem(String tipoCaptura, Dao dao) {
        if (tipoCaptura.equals("perfil")) {
            File fsave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + getCliente().toLowerCase() + "/imagens/sispessoa/" + conviteMovimento.getSisPessoa().getId() + "/perfil/" + conviteMovimento.getSisPessoa().getFotoPerfil() + ".png"));
            if (fsave.exists()) {
                FileUtils.deleteQuietly(fsave);

                conviteMovimento.getSisPessoa().setFotoPerfil("");
                if (dao == null) {
                    Dao daox = new Dao();
                    daox.openTransaction();
                    daox.update(conviteMovimento.getSisPessoa());
                    daox.commit();
                } else {
                    dao.update(conviteMovimento.getSisPessoa());
                }
            }
        } else {
            File fsave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + getCliente().toLowerCase() + "/imagens/sispessoa/" + conviteMovimento.getSisPessoa().getId() + "/documento/" + conviteMovimento.getSisPessoa().getFotoArquivo() + ".png"));
            if (fsave.exists()) {
                FileUtils.deleteQuietly(fsave);

                conviteMovimento.getSisPessoa().setFotoArquivo("");
                if (dao == null) {
                    Dao daox = new Dao();
                    daox.openTransaction();
                    daox.update(conviteMovimento.getSisPessoa());
                    daox.commit();
                } else {
                    dao.update(conviteMovimento.getSisPessoa());
                }
            }
        }
    }

    public int getIdPessoaAutoriza() {
        return idPessoaAutoriza;
    }

    public void setIdPessoaAutoriza(int idPessoaAutoriza) {
        this.idPessoaAutoriza = idPessoaAutoriza;
    }

    public List<SelectItem> getListPessoaAutoriza() {
        if (listPessoaAutoriza.isEmpty()) {
            ConviteDao db = new ConviteDao();
            List<ConviteAutorizaCortesia> list = db.listaConviteAutorizaCortesia(conviteMovimento.getId() == -1);

            int i = 0;
            for (ConviteAutorizaCortesia cac : list) {
                listPessoaAutoriza.add(new SelectItem(i, cac.getPessoa().getNome(), "" + cac.getId()));
                i++;
            }
        }
        return listPessoaAutoriza;
    }

    public void setListPessoaAutoriza(List<SelectItem> listPessoaAutoriza) {
        this.listPessoaAutoriza = listPessoaAutoriza;
    }
//
//    public String validadeConvite(String dataEmissao) {
//        DataHoje dh = new DataHoje();
//        dataEmissao = (String) dh.incrementarMeses(1, dataEmissao);
//        return dataEmissao;
//    }

    public String getMascara() {
        String mask = porPesquisa;
        if (porPesquisa.equals("socioCPF")) {
            mask = "cpf";
        }
        return Mask.getMascaraPesquisa(mask, true);
    }

    public void acaoPesquisaInicial() {
        setComoPesquisa("Inicial");
    }

    public void acaoPesquisaParcial() {
        setComoPesquisa("Parcial");
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public int getIdadeConvidado() {
        if (!conviteMovimento.getSisPessoa().getNascimento().equals("")) {
            //if (idadeConvidado == 0) {
            DataHoje dh = new DataHoje();
            idadeConvidado = (int) dh.calcularIdade(conviteMovimento.getSisPessoa().getNascimento());
            //}
        }
        return idadeConvidado;
    }

    public void setIdadeConvidado(int idadeConvidado) {
        this.idadeConvidado = idadeConvidado;
    }

    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    public void gerarMovimento() {
        Dao dao = new Dao();
        dao.openTransaction();
        gerarMovimento(dao);
    }

    public boolean gerarMovimento(Dao dao) {
        if (conviteMovimento.getEvt() == null) {
            String vencimento = conviteMovimento.getEmissao();
            String referencia;
            Plano5 plano5 = conviteMovimento.getConviteServico().getServicos().getPlano5();
            FTipoDocumento fTipoDocumento = (FTipoDocumento) dao.find(new FTipoDocumento(), 13);
            double valor = Moeda.substituiVirgulaDouble(valorString);
            Lote lote = new Lote(
                    -1,
                    (Rotina) dao.find(new Rotina(), 215),
                    "R",
                    DataHoje.data(),
                    conviteMovimento.getPessoa(),
                    plano5,
                    false,
                    "",
                    valor,
                    conviteMovimento.getConviteServico().getServicos().getFilial(),
                    null,
                    null,
                    "",
                    (FTipoDocumento) dao.find(new FTipoDocumento(), 13),
                    (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1),
                    (FStatus) dao.find(new FStatus(), 1),
                    null,
                    false,
                    0,
                    null,
                    null,
                    null,
                    false,
                    "",
                    null,
                    ""
            );
            try {
                String nrCtrBoletoResp = "";
                for (int x = 0; x < (Integer.toString(conviteMovimento.getPessoa().getId())).length(); x++) {
                    nrCtrBoletoResp += 0;
                }
                nrCtrBoletoResp += conviteMovimento.getPessoa().getId();
                String mes = conviteMovimento.getEmissao().substring(3, 5);
                String ano = conviteMovimento.getEmissao().substring(6, 10);
                referencia = mes + "/" + ano;
                Evt evt = new Evt();
                evt.setDescricao("CONVITE MOVIMENTO");
                if (!dao.save(evt)) {
                    return false;
                }
                lote.setEvt(evt);
                if (!dao.save(lote)) {
                    return false;
                }
                String nrCtrBoleto = nrCtrBoletoResp + Long.toString(DataHoje.calculoDosDias(DataHoje.converte("07/10/1997"), DataHoje.converte(vencimento)));
                movimento = new Movimento(
                        -1,
                        lote,
                        plano5,
                        conviteMovimento.getPessoa(),
                        conviteMovimento.getConviteServico().getServicos(),
                        null,
                        (TipoServico) dao.find(new TipoServico(), 1),
                        null,
                        valor,
                        referencia,
                        conviteMovimento.getEmissao(),
                        1,
                        true,
                        "E",
                        false,
                        conviteMovimento.getPessoa(), // TITULAR / RESPONSÁVEL
                        conviteMovimento.getPessoa(), // BENEFICIÁRIO
                        "",
                        nrCtrBoleto,
                        vencimento,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        fTipoDocumento,
                        0, new MatriculaSocios()
                );
                if (dao.save(movimento)) {
                    conviteMovimento.setEvt(evt);
                    return dao.update(conviteMovimento);
                } else {
                    return false;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public String getCliente() {
        if (cliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                return GenericaSessao.getString("sessaoCliente");
            }
        }
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String baixarMovimento() {
        if (getMovimento().getId() != -1) {
            List list = new ArrayList();
            movimento.setValorBaixa(movimento.getValor());
            list.add(movimento);
            GenericaSessao.put("listaMovimento", list);
            GenericaSessao.put("caixa_banco", "caixa");
            GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
            return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
        }
        return null;
    }

    public Movimento getMovimento() {
        if (conviteMovimento.getEvt() != null) {
            LoteDao loteDB = new LoteDao();
            Lote lote = (Lote) loteDB.pesquisaLotePorEvt(conviteMovimento.getEvt());
            MovimentoDao mdb = new MovimentoDao();
            List<Movimento> movimentos = (List<Movimento>) mdb.listaMovimentosDoLote(lote.getId());
            for (Movimento m : movimentos) {
                movimento = m;
                break;
            }
        }
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public boolean isDisabledValor() {
        return disabledValor;
    }

    public void setDisabledValor(boolean disabledValor) {
        this.disabledValor = disabledValor;
    }

    public boolean isDisabledConviteVencido() {
        if (DataHoje.menorData(conviteMovimento.getValidade(), DataHoje.data())) {
            disabledConviteVencido = true;
        } else {
            disabledConviteVencido = false;
        }
        return disabledConviteVencido;
    }

    public void setDisabledConviteVencido(boolean disabledConviteVencido) {
        this.disabledConviteVencido = disabledConviteVencido;
    }

    public boolean isRenderedImpressao() {
        if (conviteMovimento.getId() != -1) {
            if (conviteMovimento.isCortesia()) {
                return true;
            }

            if (Moeda.converteUS$(valorString) <= 0) {
                return true;
            }

            if (getMovimento().getBaixa() != null) {
                return true;
            }
        }
        return false;
    }

    public List<SelectItem> getListTipoDocumento() {
        return listTipoDocumento;
    }

    public void setListTipoDocumento(List<SelectItem> listTipoDocumento) {
        this.listTipoDocumento = listTipoDocumento;
    }

    public Integer getIndexTipoDocumento() {
        return indexTipoDocumento;
    }

    public void setIndexTipoDocumento(Integer indexTipoDocumento) {
        this.indexTipoDocumento = indexTipoDocumento;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }
}
