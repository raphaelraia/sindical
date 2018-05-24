package br.com.rtools.arrecadacao.beans;

import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.financeiro.*;
import br.com.rtools.financeiro.beans.MovimentoValorBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.movimento.TrataVencimento;
import br.com.rtools.movimento.TrataVencimentoRetorno;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ManagedBean
@SessionScoped
public class ProcessamentoIndividualBean extends MovimentoValorBean implements Serializable {

    private String frenteVerso = "false";
    private String nImpressos = "false";
    private int nrBoletos;
    private int processados;
    private int processando;
    private int idTipoServico;
    private int idServicos;
    private int idIndex = -1;
    private Object processamento = new Object();
    private Date vencimento = DataHoje.dataHoje();

    private String strReferencia = DataHoje.dataReferencia(getVencimentoString());
    private String tipoEnvio = "empresa";
    private String linkAcesso = "";
    private Lote lote = null;
    private Juridica juridica = new Juridica();
    private boolean renVisualizar = false;
    private boolean renLimparTodos = false;
    private boolean imprimeVerso = false;
    private boolean processou = false;
    private boolean marcarTodos = true;
    private boolean marcarReferencia = true;
    private boolean marcarVencimento = true;
    private boolean outrasEmpresas = false;
    private Pessoa pessoaEnvio = new Pessoa();
    private Historico historico;
    private Integer index = null;

    private List<TrataVencimentoRetorno> listaMovimento = new ArrayList();

    private TrataVencimentoRetorno olmSelecionado = null;

    public ProcessamentoIndividualBean() {
        GenericaSessao.remove("juridicaBean");
    }

    public void calcular(TrataVencimentoRetorno olm) {
        calcular(olm, null);
    }

    public void calcular(TrataVencimentoRetorno olm, Boolean todos) {
        if (todos != null) {
            olm.setCalcular(todos);
            if (!todos) {
                olm.setValor_calculado(olm.getValor());
            } else {
                olm.setValor_calculado(Moeda.soma(Moeda.soma(Moeda.soma(olm.getMulta(), olm.getJuros()), olm.getCorrecao()), olm.getValor()));
            }
        } else {
            if (!olm.getCalcular()) {
                olm.setValor_calculado(olm.getValor());
            } else {
                olm.setValor_calculado(Moeda.soma(Moeda.soma(Moeda.soma(olm.getMulta(), olm.getJuros()), olm.getCorrecao()), olm.getValor()));
            }
        }
    }

    public void removerEmpresa() {
        juridica = new Juridica();
    }

    public void salvarEmail() {
        if (this.pessoaEnvio.getId() != -1) {
            Dao dao = new Dao();
            if (dao.update(this.pessoaEnvio, true)) {
                GenericaMensagem.info("Sucesso", "Email atualizado");
            } else {
                GenericaMensagem.warn("Erro", "Erro ao atualizar Email");
            }
        }
    }

    public void verificaEmail(Pessoa pessoaEnvio) {
        this.pessoaEnvio = pessoaEnvio;
        if (!this.pessoaEnvio.getEmail1().isEmpty()) {
        }
    }

    public String getStatusContribuinte() {
        JuridicaDao db = new JuridicaDao();
        if (juridica.getId() != -1) {
            List<List> listax = db.listaJuridicaContribuinte(juridica.getId());

            if (listax.isEmpty()) {
                return "NÃO CONTRIBUINTE";
            }
            for (int i = 0; i < listax.size(); i++) {
                if (listax.get(0).get(11) != null) {
                    return "CONTRIBUINTE INATIVO";
                } else {
                    //return  "CONTRIBUINTE ATIVO";
                }
            }
        }
        return "";
    }

    public synchronized void marcarTodosLista() {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if (listaMovimento.get(i).getMovimento().getServicos().getId() != 1) {
                calcular(listaMovimento.get(i), marcarTodos);
            }
        }
    }

    public synchronized String adicionarMovimento() {
        if (juridica == null || juridica.getId() == -1) {
            GenericaMensagem.warn("Erro", "Pesquise uma Empresa!");
            return null;
        }

        if (strReferencia.length() != 7) {
            GenericaMensagem.warn("Atenção", "Digite uma referência válida!");
            return null;
        }

        Dao dao = new Dao();

        Servicos servicos = (Servicos) dao.find(new Servicos(), Integer.valueOf(getListaServico().get(idServicos).getDescription()));
        TipoServico tipoServico = (TipoServico) dao.find(new TipoServico(), Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()));

        ContaCobrancaDao ctaCobraDB = new ContaCobrancaDao();

        ContaCobranca contaCob = ctaCobraDB.pesquisaServicoCobranca(servicos.getId(), tipoServico.getId());

        if (contaCob == null) {
            GenericaMensagem.warn("Erro", "Não existe conta Cobrança para gerar!");
            return null;
        }

        MovimentoDao finDB = new MovimentoDao();

        List<Movimento> lm_acordado = finDB.listaMovimentoAcordado(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servicos.getId());

        if (!lm_acordado.isEmpty()) {
            GenericaMensagem.error("Atenção", "Esta referência já foi acordada!");
            return null;
        }

        List<Movimento> lm = finDB.pesquisaMovimentos(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servicos.getId());

        Movimento movimento = null;
        if (!lm.isEmpty()) {
            if (lm.size() > 1) {
                GenericaMensagem.error("Atenção", "ATENÇÃO, MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR!");
                return null;
            } else {
                movimento = lm.get(0);

                if (movimento.getBaixa() != null) {
                    GenericaMensagem.warn("Atenção", "MOVIMENTO JÁ FOI BAIXADO!");
                    return null;
                }
            }
        }

        MensagemConvencaoDao menDB = new MensagemConvencaoDao();

        MensagemConvencao mensagemConvencao = menDB.retornaDiaString(
                juridica.getId(),
                strReferencia,
                tipoServico.getId(),
                servicos.getId()
        );

        if (mensagemConvencao == null) {
            GenericaMensagem.warn("Erro", "Não existe mensagem para esta referência !");
            return null;
        }

        TrataVencimentoRetorno olm;

        if (movimento != null) {
            olm = TrataVencimento.movimentoExiste(movimento, juridica, strReferencia, vencimento);
        } else {
            olm = TrataVencimento.movimentoNaoExiste(servicos, tipoServico, juridica, strReferencia, vencimento, super.carregarValor(servicos.getId(), tipoServico.getId(), strReferencia, juridica.getPessoa().getId()));
        }

        if (listaMovimento.isEmpty()) {
            outrasEmpresas = false;

            listaMovimento.add(olm);
        } else {
            boolean ex = false;

            for (int i = 0; i < listaMovimento.size(); i++) {
                if (listaMovimento.get(i).getMovimento().getServicos().getId() == servicos.getId()
                        && listaMovimento.get(i).getMovimento().getTipoServico().getId() == tipoServico.getId()
                        && listaMovimento.get(i).getMovimento().getReferencia().equals(strReferencia)
                        && listaMovimento.get(i).getMovimento().getPessoa().getId() == juridica.getPessoa().getId()) {
                    ex = true;
                }
            }

            if (!ex) {

                listaMovimento.add(olm);

                for (int i = 0; i < listaMovimento.size(); i++) {
                    if (listaMovimento.get(i).getMovimento().getPessoa().getId() != juridica.getPessoa().getId()) {
                        outrasEmpresas = true;
                    }
                }

            } else {
                GenericaMensagem.warn("Erro", "Esse movimento já está adicionado abaixo!");
            }
        }

        renVisualizar = false;
        renLimparTodos = false;

        return null;
    }



    public String chamarMensagem() {
        CnaeConvencaoDao cnaeConvencaoDB = new CnaeConvencaoDao();
        ConvencaoCidadeDao convencaoCidade = new ConvencaoCidadeDao();
        Convencao convencao = cnaeConvencaoDB.pesquisarCnaeConvencao(juridica.getId());
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        GrupoCidade grupoCidade = null;
        if (convencao != null) {
            PessoaEndereco pessoaEndereco = dao.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 5);
            if (pessoaEndereco != null) {
                grupoCidade = convencaoCidade.pesquisaGrupoCidadeJuridica(convencao.getId(), pessoaEndereco.getEndereco().getCidade().getId());
                if (grupoCidade != null) {
                    MensagemConvencao mensagemConvencao = new MensagemConvencao();
                    TipoServicoDao dbTipo = new TipoServicoDao();
                    Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServico().get(idServicos).getDescription()));
                    TipoServico tipoServico = dbTipo.pesquisaCodigo(Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()));
                    if ((servicos != null) && (tipoServico != null)) {
                        mensagemConvencao.setConvencao(convencao);
                        mensagemConvencao.setGrupoCidade(grupoCidade);
                        mensagemConvencao.setTipoServico(tipoServico);
                        mensagemConvencao.setServicos(servicos);
                        mensagemConvencao.setReferencia(strReferencia);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mensagemPesquisa", mensagemConvencao);
                        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).mensagem();
                    }
                }
            }
        }
        return null;
    }

    public synchronized void filtrarReferencia() {
        MensagemConvencaoDao menDB = new MensagemConvencaoDao();
        if (!(new DataHoje()).integridadeReferencia(strReferencia)) {
            strReferencia = DataHoje.dataReferencia(getVencimentoString());
            return;
        }
        MensagemConvencao mensagemConvencao = menDB.retornaDiaString(
                juridica.getId(),
                strReferencia,
                Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()),
                Integer.valueOf(getListaServico().get(idServicos).getDescription()));

        if (mensagemConvencao != null) {
            vencimento = mensagemConvencao.getDtVencimento();
            if (vencimento == null) {
                vencimento = DataHoje.dataHoje();
            }
            if (vencimento.before(DataHoje.dataHoje())) {
                vencimento = DataHoje.dataHoje();
            }
        } else {
            GenericaMensagem.warn("Validação", "Não existe mensagem!");
            vencimento = DataHoje.dataHoje();
        }
        validaReferenciaVencimento(mensagemConvencao);
    }

    public synchronized void atualizaRef() {
        try {
            if (vencimento.before(DataHoje.dataHoje())) {
                //vencimento = DataHoje.data();
            }
        } catch (Exception e) {
            vencimento = DataHoje.dataHoje();
        }
    }

    @Override
    public synchronized void carregarFolha() {

    }

    @Override
    public synchronized void carregarFolha(DataObject linha) {

    }

    @Override
    public synchronized void carregarFolha(Object linha) {
        super.carregarFolha(((TrataVencimentoRetorno) linha).getMovimento());
        olmSelecionado = (TrataVencimentoRetorno) linha;
    }

    @Override
    public synchronized void atualizaValorGrid(String tipo) {
        olmSelecionado.setValorString(super.atualizaValor(true, tipo));

        if (olmSelecionado.getMovimento().getServicos().getId() == 1) {
            olmSelecionado.setValor_calculado(Moeda.soma(Moeda.soma(Moeda.soma(olmSelecionado.getMulta(), olmSelecionado.getJuros()), olmSelecionado.getCorrecao()), olmSelecionado.getValor()));
        } else {
            calcular(olmSelecionado);
        }
    }

    public String btnExcluirMov(int index) {
        listaMovimento.remove(index);
        processou = false;
        return null;
    }

    public void excluirMov(int idMov) {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if (idMov == listaMovimento.get(i).getMovimento().getId()) {
                listaMovimento.remove(i);
            }
        }
    }

    public String limparTodos() {
        listaMovimento.clear();
        renLimparTodos = false;
        renVisualizar = false;
        juridica = new Juridica();
        idServicos = 0;
        idTipoServico = 0;
        processou = false;
        vencimento = DataHoje.dataHoje();
        strReferencia = DataHoje.dataReferencia(getVencimentoString());
        return "processamentoIndividual";
    }

    public synchronized String gerarBoleto() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        String beforeUpdate = "";

        if (!listaMovimento.isEmpty()) {
            for (TrataVencimentoRetorno olm : listaMovimento) {
                Boolean success = true;

                if (olm.getBoleto() != null) {
                    //movimentoBefore = (Movimento) dao.find((Movimento) listMovimentos.get(i).getArgumento1());
                    Movimento movimentoBefore = (Movimento) dao.find(olm.getMovimento());

                    beforeUpdate
                            = " Movimento: (" + movimentoBefore.getId() + ") "
                            + " - Referência: (" + movimentoBefore.getReferencia()
                            + " - Tipo Serviço: (" + movimentoBefore.getTipoServico().getId() + ") " + olm.getMovimento().getTipoServico().getDescricao()
                            + " - Serviços: (" + movimentoBefore.getServicos().getId() + ") " + olm.getMovimento().getServicos().getDescricao()
                            + " - Pessoa: (" + movimentoBefore.getPessoa().getId() + ") " + olm.getMovimento().getPessoa().getNome()
                            + " - Valor: " + movimentoBefore.getValorString()
                            + " - Vencimento: " + movimentoBefore.getVencimento();
//
//                    movim.setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento3()));
//                    // SE ALTERAR O VENCIMENTO E FOR COBRANÇA REGISTRADA, ENTÃO ALTERAR A DATA DE REGISTRO PARA QUANDO IMPRIMIR REGISTRAR NOVAMENTE
//                    //if (!movimentoBefore.getVencimento().equals(movim.getVencimento())) {
//                    if (!bol.getVencimento().equals(((Movimento) listMovimentos.get(i).getArgumento1()).getVencimento())) {
//
//                        if (bol.getContaCobranca().getCobrancaRegistrada().getId() != 3) {
//                            bol.setDtCobrancaRegistrada(null);
//                            new Dao().update(bol, true);
//                        }
//
//                        bol.setVencimento(((Movimento) listMovimentos.get(i).getArgumento1()).getVencimento());
//                        new Dao().update(bol, true);
//                    }

                    olm.getBoleto().setVencimento(olm.getVencimentoBoletoString());
                    olm.getBoleto().setValor(olm.getValor_calculado());

                    new Dao().update(olm.getBoleto(), true);

                    if (GerarMovimento.alterarUmMovimento(olm.getMovimento(), olm.getVencimentoBoleto())) {
                        novoLog.update(beforeUpdate,
                                " Movimento: (" + olm.getMovimento().getId() + ") "
                                + " - Referência: (" + olm.getMovimento().getReferencia()
                                + " - Tipo Serviço: (" + olm.getMovimento().getTipoServico().getId() + ") " + olm.getMovimento().getTipoServico().getDescricao()
                                + " - Serviços: (" + olm.getMovimento().getServicos().getId() + ") " + olm.getMovimento().getServicos().getDescricao()
                                + " - Pessoa: (" + olm.getMovimento().getPessoa().getId() + ") " + olm.getMovimento().getPessoa().getNome()
                                + " - Valor: " + olm.getMovimento().getValorString()
                                + " - Vencimento: " + olm.getMovimento().getVencimento()
                        );

                        GenericaMensagem.info("Sucesso", "Boleto Alterado!");
                    } else {
                        GenericaMensagem.warn("Atenção", "Erro ao alterar boleto!");
                        success = false;
                    }

//                    movim.setVencimento(((Movimento) listMovimentos.get(i).getArgumento1()).getVencimento());
//                    ((DataObject) listMovimentos.get(i)).setArgumento1(movim);
                } else {
//                    movim = (Movimento) listMovimentos.get(i).getArgumento1();
//                    movim.setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento3()));
//                    String vencto = ((Movimento) listMovimentos.get(i).getArgumento1()).getVencimento();

                    StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimento(new Lote(), olm.getMovimento(), olm.getVencimentoBoleto(), olm.getValor_calculado());

                    if (sr.getStatus()) {

//                        movim.setVencimento(vencto);
                        novoLog.save(
                                " Movimento: (" + olm.getMovimento().getId() + ") "
                                + " - Referência: (" + olm.getMovimento().getReferencia()
                                + " - Tipo Serviço: (" + olm.getMovimento().getTipoServico().getId() + ") " + olm.getMovimento().getTipoServico().getDescricao()
                                + " - Serviços: (" + olm.getMovimento().getServicos().getId() + ") " + olm.getMovimento().getServicos().getDescricao()
                                + " - Pessoa: (" + olm.getMovimento().getPessoa().getId() + ") " + olm.getMovimento().getPessoa().getNome()
                                + " - Valor: " + olm.getMovimento().getValorString()
                                + " - Vencimento: " + olm.getMovimento().getVencimento()
                        );

                        olm.setBoleto(olm.getMovimento().getBoleto());

                        GenericaMensagem.info("Sucesso", "Boleto Gerado!");

                    } else {

                        GenericaMensagem.warn("Erro", sr.getMensagem());
                        success = false;

                    }
                }

                if (success) {
                    Historico h = olm.getMovimento().getHistorico();
                    if (h != null) {
                        if (h.getId() == -1 && !h.getComplemento().isEmpty() && !h.getHistorico().isEmpty()) {
                            h.setMovimento(olm.getMovimento());
                            new Dao().save(h, true);
                        } else {
                            new Dao().update(h, true);
                        }
                    }
                }

            }
            processou = true;
        }
        return null;
    }

    public String imprimirBoleto() {
        return imprimirBoleto(false);
    }

    public String imprimirBoleto(Boolean download) {
        List<Movimento> movs = new ArrayList();

        if (!listaMovimento.isEmpty()) {
            Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
            Dao dao = new Dao();

            dao.openTransaction();

            for (TrataVencimentoRetorno olm : listaMovimento) {
                movs.add(olm.getMovimento());

                Impressao impressao = new Impressao();

                impressao.setUsuario(usuario);
                impressao.setDtVencimento(olm.getBoleto().getDtVencimento());
                impressao.setMovimento(olm.getMovimento());

                if (!dao.save(impressao)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
                    return null;
                }
                
            }

            dao.commit();

            ImprimirBoleto imp = new ImprimirBoleto();

            movs = imp.atualizaContaCobrancaMovimento(movs);

            imp.imprimirBoleto(movs, imprimeVerso, true);

            if (download) {
                imp.baixarArquivo();
            } else {
                imp.visualizar(null);
            }
        }
        return null;
    }

    public String enviarEmail() {

        if (!listaMovimento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Lista Vazia!");
            return null;
        }

        JuridicaDao db = new JuridicaDao();

        List<Movimento> movs = new ArrayList();
        String empresasSemEmail = "";

        Registro reg = Registro.get();

        if (tipoEnvio.equals("empresa")) {
            Juridica jur = new Juridica();
            for (TrataVencimentoRetorno olm : listaMovimento) {

                if (olm.getMovimento().getPessoa().getEmail1().equals("") || olm.getMovimento().getPessoa().getEmail1() == null) {
                    if (empresasSemEmail.equals("")) {
                        empresasSemEmail = jur.getPessoa().getNome();
                    } else {
                        empresasSemEmail = empresasSemEmail + ", " + jur.getPessoa().getNome();
                    }
                }
            }

            if (!empresasSemEmail.equals("")) {
                GenericaMensagem.warn("Erro", " Empresas " + empresasSemEmail + " não contém e-mail para envio!");
                return null;
            }

            if (!outrasEmpresas) {
                enviarEmailPraUma(juridica);
                return null;
            }

            List<File> fls = new ArrayList();

            for (TrataVencimentoRetorno olm : listaMovimento) {

//                ((Movimento) listMovimentos.get(i).getArgumento1()).setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento3()));
                movs.add(olm.getMovimento());

                ImprimirBoleto imp = new ImprimirBoleto();

                movs = imp.atualizaContaCobrancaMovimento(movs);

                imp.imprimirBoleto(movs, imprimeVerso, true);

                String nome = imp.criarLink(movs.get(0).getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
                List<Pessoa> pessoas = new ArrayList();

                pessoas.add(movs.get(0).getPessoa());

                String nome_envio;
                if (listaMovimento.size() == 1) {
                    nome_envio = "Boleto " + olm.getMovimento().getServicos().getDescricao() + " N° " + olm.getMovimento().getDocumento();
                } else {
                    nome_envio = "Boleto";
                }

                String mensagem;

                if (!reg.isEnviarEmailAnexo()) {
                    mensagem = " <h5> Visualize seu boleto clicando no link abaixo </h5> <br /><br />"
                            + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir boleto</a><br />";
                } else {
                    fls.add(new File(imp.getPathPasta() + "/" + nome));
                    mensagem = "<h5>Segue boleto em anexo</h5><br /><br />";
                }

                Dao di = new Dao();
                Mail mail = new Mail();
                mail.setFiles(fls);
                mail.setEmail(
                        new Email(
                                -1,
                                DataHoje.dataHoje(),
                                DataHoje.livre(new Date(), "HH:mm"),
                                (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                                (Rotina) di.find(new Rotina(), 106),
                                null,
                                nome_envio,
                                mensagem,
                                false,
                                false
                        )
                );

                List<EmailPessoa> emailPessoas = new ArrayList();
                EmailPessoa emailPessoa = new EmailPessoa();

               for (Pessoa pe : pessoas) {
                    emailPessoa.setDestinatario(pe.getEmail1());
                    emailPessoa.setPessoa(pe);
                    emailPessoa.setRecebimento(null);
                    emailPessoas.add(emailPessoa);
                    mail.setEmailPessoas(emailPessoas);
                    emailPessoa = new EmailPessoa();
                }

                String[] retorno = mail.send("personalizado");

                if (!retorno[1].isEmpty()) {
                    GenericaMensagem.warn("Erro", retorno[1]);
                } else {
                    GenericaMensagem.info("Sucesso", retorno[0]);
                }

                movs.clear();
                pessoas.clear();
                fls.clear();
            }
        } else {

            for (TrataVencimentoRetorno olm : listaMovimento) {
                //((Movimento) listMovimentos.get(i).getArgumento1()).setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento7()));
                movs.add(olm.getMovimento());
            }

            List<Movimento> m = new ArrayList();

            List<File> fls = new ArrayList();

            for (int i = 0; i < movs.size(); i++) {
                Juridica jur = db.pesquisaJuridicaPorPessoa(movs.get(i).getPessoa().getId());

                if (jur.getContabilidade() == null) {
                    if (jur.getPessoa().getEmail1().equals("") || jur.getPessoa().getEmail1() == null) {
                        if (empresasSemEmail.equals("")) {
                            empresasSemEmail = jur.getPessoa().getNome();
                        } else {
                            empresasSemEmail = empresasSemEmail + ", " + jur.getPessoa().getNome();
                        }
                        continue;
                    }
                    m.add(movs.get(i));

                    ImprimirBoleto imp = new ImprimirBoleto();

                    m = imp.atualizaContaCobrancaMovimento(m);

                    imp.imprimirBoleto(m, imprimeVerso, true);

                    String nome = imp.criarLink(jur.getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
                    List<Pessoa> pessoas = new ArrayList();
                    pessoas.add(jur.getPessoa());

                    String nome_envio;
                    if (movs.size() == 1) {
                        nome_envio = "Boleto " + m.get(0).getServicos().getDescricao() + " N° " + m.get(0).getDocumento();
                    } else {
                        nome_envio = "Boleto";
                    }

                    String mensagem;
                    if (!reg.isEnviarEmailAnexo()) {
                        mensagem = " <h5> Visualize seu boleto clicando no link abaixo </h5> <br /><br />"
                                + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir boleto</a><br />";
                    } else {
                        fls.add(new File(imp.getPathPasta() + "/" + nome));
                        mensagem = "<h5>Segue boleto em anexo</h5><br /><br />";
                    }

                    Dao di = new Dao();
                    Mail mail = new Mail();
                    mail.setFiles(fls);
                    mail.setEmail(
                            new Email(
                                    -1,
                                    DataHoje.dataHoje(),
                                    DataHoje.livre(new Date(), "HH:mm"),
                                    (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                                    (Rotina) di.find(new Rotina(), 106),
                                    null,
                                    nome_envio,
                                    mensagem,
                                    false,
                                    false
                            )
                    );

                    List<EmailPessoa> emailPessoas = new ArrayList();
                    EmailPessoa emailPessoa = new EmailPessoa();
                    for (Pessoa pe : pessoas) {
                        emailPessoa.setDestinatario(pe.getEmail1());
                        emailPessoa.setPessoa(pe);
                        emailPessoa.setRecebimento(null);
                        emailPessoas.add(emailPessoa);
                        mail.setEmailPessoas(emailPessoas);
                        emailPessoa = new EmailPessoa();
                    }

                    String[] retorno = mail.send("personalizado");

                    if (!retorno[1].isEmpty()) {
                        GenericaMensagem.warn("Erro", retorno[1]);
                    } else {
                        GenericaMensagem.info("Sucesso", retorno[0]);
                    }

                    m.clear();

                    pessoas.clear();
                    fls.clear();
                } else {

                    if (jur.getContabilidade().getPessoa().getEmail1() == null || jur.getContabilidade().getPessoa().getEmail1().isEmpty()) {
                        if (empresasSemEmail.equals("")) {
                            empresasSemEmail = jur.getContabilidade().getPessoa().getNome() + " da empresa " + jur.getPessoa().getNome();
                        } else {
                            empresasSemEmail = empresasSemEmail + ", " + jur.getContabilidade().getPessoa().getNome() + " da empresa " + jur.getPessoa().getNome();
                        }
                        continue;
                    }

                    m.add(movs.get(i));

                    ImprimirBoleto imp = new ImprimirBoleto();

                    m = imp.atualizaContaCobrancaMovimento(m);

                    imp.imprimirBoleto(m, imprimeVerso, true);

                    String nome = imp.criarLink(jur.getContabilidade().getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
                    List<Pessoa> pessoas = new ArrayList();
                    pessoas.add(jur.getContabilidade().getPessoa());

                    String nome_envio = "";
                    if (m.size() == 1) {
                        nome_envio = "Boleto " + m.get(0).getServicos().getDescricao() + " N° " + m.get(0).getDocumento();
                    } else {
                        nome_envio = "Boleto";
                    }

                    String mensagem;
                    if (!reg.isEnviarEmailAnexo()) {
                        mensagem = " <h5> Visualize seu boleto clicando no link abaixo </h5> <br /><br />"
                                + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir boleto</a><br />";
                    } else {
                        fls.add(new File(imp.getPathPasta() + "/" + nome));
                        mensagem = "<h5>Segue boleto em anexo</h5><br /><br />";
                    }

                    Dao di = new Dao();
                    Mail mail = new Mail();
                    mail.setFiles(fls);
                    mail.setEmail(
                            new Email(
                                    -1,
                                    DataHoje.dataHoje(),
                                    DataHoje.livre(new Date(), "HH:mm"),
                                    (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                                    (Rotina) di.find(new Rotina(), 106),
                                    null,
                                    nome_envio,
                                    mensagem,
                                    false,
                                    false
                            )
                    );

                    List<EmailPessoa> emailPessoas = new ArrayList();
                    EmailPessoa emailPessoa = new EmailPessoa();
                    for (Pessoa pe : pessoas) {
                        emailPessoa.setDestinatario(pe.getEmail1());
                        emailPessoa.setPessoa(pe);
                        emailPessoa.setRecebimento(null);
                        emailPessoas.add(emailPessoa);
                        mail.setEmailPessoas(emailPessoas);
                        emailPessoa = new EmailPessoa();
                    }

                    String[] retorno = mail.send("personalizado");

                    if (!retorno[1].isEmpty()) {
                        GenericaMensagem.warn("Erro", retorno[1]);
                    } else {
                        GenericaMensagem.info("Sucesso", retorno[0]);
                    }

                    m.clear();

                    pessoas.clear();
                    fls.clear();
                }
            }
            if (!empresasSemEmail.equals("")) {
                GenericaMensagem.warn("Erro", empresasSemEmail + " não contém e-mail para envio!");
            }
            return null;
        }
        return null;
    }

    public void enviarEmailPraUma(Juridica juridica) {
        if (!listaMovimento.isEmpty()) {
            return;
        }

        List<Movimento> movs = new ArrayList();

        Registro reg = Registro.get();

        for (TrataVencimentoRetorno olm : listaMovimento) {
//            ((Movimento) listMovimentos.get(i).getArgumento1()).setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento3()));
            movs.add(olm.getMovimento());
        }

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String path = request.getQueryString();

        ImprimirBoleto imp = new ImprimirBoleto();

        movs = imp.atualizaContaCobrancaMovimento(movs);

        imp.imprimirBoleto(movs, imprimeVerso, true);

        String nome = imp.criarLink(juridica.getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        List<Pessoa> pessoas = new ArrayList();
        pessoas.add(juridica.getPessoa());

        List<File> fls = new ArrayList();

        String nome_envio;
        if (movs.size() == 1) {
            nome_envio = "Boleto " + movs.get(0).getServicos().getDescricao() + " N° " + movs.get(0).getDocumento();
        } else {
            nome_envio = "Boleto";
        }

        String mensagem;
        if (!reg.isEnviarEmailAnexo()) {
            mensagem = " <h5> Visualize seu boleto clicando no link abaixo </h5> <br /><br />"
                    + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir o boleto</a><br />";
        } else {
            fls.add(new File(imp.getPathPasta() + "/" + nome));
            mensagem = "<h5>Segue boleto em anexo</h5><br /><br />";
        }

        Dao di = new Dao();
        Mail mail = new Mail();
        mail.setFiles(fls);
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        (Rotina) di.find(new Rotina(), 106),
                        null,
                        nome_envio,
                        mensagem,
                        false,
                        false
                )
        );

        List<EmailPessoa> emailPessoas = new ArrayList();
        EmailPessoa emailPessoa = new EmailPessoa();
        for (Pessoa pe : pessoas) {
            emailPessoa.setDestinatario(pe.getEmail1());
            emailPessoa.setPessoa(pe);
            emailPessoa.setRecebimento(null);
            emailPessoas.add(emailPessoa);
            mail.setEmailPessoas(emailPessoas);
            emailPessoa = new EmailPessoa();
        }

        String[] retorno = mail.send("personalizado");

        if (!retorno[1].isEmpty()) {
            GenericaMensagem.warn("Erro", retorno[1]);
        } else {
            GenericaMensagem.info("Sucesso", retorno[0]);
        }
    }

    public String getLinks() throws IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        linkAcesso = request.getParameter("arquivo");
        if (!linkAcesso.isEmpty()) {
            LinksDao db = new LinksDao();
            Links link = db.pesquisaNomeArquivo(linkAcesso);
            if (link != null) {
                DataHoje dt = new DataHoje();

                int data1 = DataHoje.converteDataParaInteger(dt.incrementarDias(30, link.getEmissao()));
                int data2 = DataHoje.converteDataParaInteger(DataHoje.data());

                if (data1 < data2) {
                    return "Arquivo expirado!";
                } else {
                    //String pathFile = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(link.getCaminho() + "/" + link.getNomeArquivo());
                    String pathFile = link.getCaminho() + "/" + link.getNomeArquivo();
                    //if (new File(pathFile).exists()) {
                    response.sendRedirect(link.getCaminho() + "/" + link.getNomeArquivo());
                    //} else {
                    //    NovoLog log = new NovoLog();
                    //    log.novo("Arquivo não encontrado", "Arquivo não existe no caminho específicado: " + link.getCaminho() + "/" + link.getNomeArquivo());
                    // }
                }
            }
        }
        return "Nenhum arquivo encontrado!";
    }

    public String editarJuridica(DataObject linha) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        JuridicaDao db = new JuridicaDao();
        Juridica jur = db.pesquisaJuridicaPorPessoa(((Movimento) linha.getArgumento1()).getPessoa().getId());
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.editar(jur, true);
        GenericaSessao.put("juridicaBean", juridicaBean);
        // ((JuridicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("juridicaBean")).editar(jur);
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).pesquisa("pessoaJuridica");
    }

    public List<SelectItem> getListaServico() {
        List<SelectItem> servicos = new ArrayList();

        ServicosDao db = new ServicosDao();
        List<Servicos> select = db.pesquisaTodos(4);

        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                servicos.add(
                        new SelectItem(
                                i,
                                select.get(i).getDescricao(),
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
        }
        return servicos;
    }

    public List<SelectItem> getListaTipoServico() {
        List<SelectItem> tipoServico = new ArrayList();
        FilialDao filDB = new FilialDao();
        DataHoje data = new DataHoje();
        Registro registro = filDB.pesquisaRegistroPorFilial(1);
        Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServico().get(idServicos).getDescription()));

        TipoServicoDao db = new TipoServicoDao();
        if ((!data.integridadeReferencia(strReferencia))
                || (registro == null)
                || (servicos == null)) {
            return tipoServico;
        }
        List<Integer> listaIds = new ArrayList();

        listaIds.add(1);
        listaIds.add(2);
        listaIds.add(3);

        List<TipoServico> select = db.pesquisaTodosComIds(listaIds);
        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                tipoServico.add(
                        new SelectItem(
                                i,
                                select.get(i).getDescricao(),
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
        }
        return tipoServico;
    }

    public void refreshForm() {
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getNrBoletos() {
        if (!listaMovimento.isEmpty()) {
            nrBoletos = listaMovimento.size();
        } else {
            nrBoletos = 0;
        }
        return nrBoletos;
    }

    public void setNrBoletos(int nrBoletos) {
        this.nrBoletos = nrBoletos;
    }

    public int getProcessados() {
        return processados;
    }

    public void setProcessados(int processados) {
        this.processados = processados;
    }

    public int getProcessando() {
        return processando;
    }

    public void setProcessando(int processando) {
        this.processando = processando;
    }

    public int getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(int idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getVencimentoString() {
        return DataHoje.converteData(vencimento);
    }

    public void setVencimentoString(String vencimentoString) {
        this.vencimento = DataHoje.converte(vencimentoString);
    }

    public Juridica getJuridica() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
            filtrarReferencia();
        }
        return juridica;
    }

    public void validaReferenciaVencimento(MensagemConvencao mensa) {
        if (juridica.getPessoa().getId() != -1) {
            if (mensa != null) {
                marcarVencimento = false;
            } else {
                marcarVencimento = true;
            }
            marcarReferencia = false;
        } else {
            marcarVencimento = true;
            marcarReferencia = true;
        }
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public String getFrenteVerso() {
        return frenteVerso;
    }

    public void setFrenteVerso(String frenteVerso) {
        this.frenteVerso = frenteVerso;
    }

    public String getnImpressos() {
        return nImpressos;
    }

    public void setnImpressos(String nImpressos) {
        this.nImpressos = nImpressos;
    }

    public boolean isRenVisualizar() {
        return renVisualizar;
    }

    public void setRenVisualizar(boolean renVisualizar) {
        this.renVisualizar = renVisualizar;
    }

    public String getStrReferencia() {
        return strReferencia;
    }

    public void setStrReferencia(String strReferencia) {
        this.strReferencia = strReferencia;
    }

    public boolean isRenLimparTodos() {
        return renLimparTodos;
    }

    public void setRenLimparTodos(boolean renLimparTodos) {
        this.renLimparTodos = renLimparTodos;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public boolean isMarcarTodos() {
        return marcarTodos;
    }

    public void setMarcarTodos(boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public boolean isMarcarVencimento() {
        return marcarVencimento;
    }

    public void setMarcarVencimento(boolean marcarVencimento) {
        this.marcarVencimento = marcarVencimento;
    }

    public boolean isMarcarReferencia() {
        return marcarReferencia;
    }

    public void setMarcarReferencia(boolean marcarReferencia) {
        this.marcarReferencia = marcarReferencia;
    }

    public Object getProcessamento() {
        processamento = -1;
        return processamento;
    }

    public void setProcessamento(Object processamento) {
        this.processamento = processamento;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public boolean isProcessou() {
        return processou;
    }

    public void setProcessou(boolean processou) {
        this.processou = processou;
    }

    public Pessoa getPessoaEnvio() {
        return pessoaEnvio;
    }

    public void setPessoaEnvio(Pessoa pessoaEnvio) {
        this.pessoaEnvio = pessoaEnvio;
    }

    public void openHistorico(int index) {
        historico = new Historico();
        this.index = index;
        historico = listaMovimento.get(index).getMovimento().getHistorico();
        if (historico == null) {
            historico = new Historico();
        }
    }

    public void saveHistorico() {
        if (historico.getHistorico().length() < 10 || historico.getHistorico().trim().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR MENSAGEM DO CONTRIBUINTE! DEVE TER NO MÍNIMO 10 CARACTERES");
            return;
        }
        if (historico.getComplemento().length() < 10 || historico.getComplemento().trim().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR MENSAGEM DO BOLETO! DEVE TER NO MÍNIMO 10 CARACTERES");
            return;

        }
        if (historico.getId() != -1) {
            new Dao().update(historico, true);
            GenericaMensagem.info("Validação", "MENSAGEM ATUALIZADA COM SUCESSO");
        } else {
            GenericaMensagem.info("Validação", "MENSAGEM INSERIDA COM SUCESSO");
        }
        listaMovimento.get(index).getMovimento().setHistorico(historico);
        this.index = null;
        historico = null;
    }

    public void closeHistorico() {
        if (historico != null) {
            if (historico.getId() == -1 && historico.getHistorico().isEmpty() && historico.getComplemento().isEmpty()) {
                listaMovimento.get(index).getMovimento().setHistorico(null);
            }
        }
        this.index = null;
        historico = null;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public List<TrataVencimentoRetorno> getListaMovimento() {
        return listaMovimento;
    }

    public void setList(List<TrataVencimentoRetorno> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }
}
