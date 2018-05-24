/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.arrecadacao.dao.ImpressaoEscritorioDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Impressao;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.MovimentoValorBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Windows
 */
@ManagedBean
@SessionScoped
public class ImpressaoEscritorioBean extends MovimentoValorBean implements Serializable {

    private List<SelectItem> listaServico = new ArrayList();
    private Integer indexServico = 0;
    private List<SelectItem> listaTipoServico = new ArrayList();
    private Integer indexTipoServico = 0;
    private String referencia = "";
    private String vencimento = "";
    private Juridica escritorio = new Juridica();

    private Boolean podeListarEmpresa = false;
    private List<ListaEmpresaEscritorio> listaEmpresa = new ArrayList();
    private List<ListaEmpresaEscritorio> listaEmpresaSelecionada = new ArrayList();

    private Boolean modalBoletosVisible = false;

    ListaEmpresaEscritorio obListaEmpresa = null;

    private Boolean processou = false;
    private Boolean todosAcrescimos = true;

    public ImpressaoEscritorioBean() {
        loadListaServico();
        loadListaTipoServico();

        GenericaSessao.remove("juridicaPesquisa");
    }

    public String cadastrarMensagem(ListaEmpresaEscritorio lee) {
        CnaeConvencaoDao cnaeConvencaoDB = new CnaeConvencaoDao();
        ConvencaoCidadeDao convencaoCidade = new ConvencaoCidadeDao();
        Convencao convencao = cnaeConvencaoDB.pesquisarCnaeConvencao(lee.getEmpresa().getId());
        PessoaEnderecoDao dao = new PessoaEnderecoDao();

        if (convencao != null) {
            PessoaEndereco pessoaEndereco = dao.pesquisaEndPorPessoaTipo(lee.getEmpresa().getPessoa().getId(), 5);
            if (pessoaEndereco != null) {
                GrupoCidade grupoCidade = convencaoCidade.pesquisaGrupoCidadeJuridica(convencao.getId(), pessoaEndereco.getEndereco().getCidade().getId());
                if (grupoCidade != null) {
                    MensagemConvencao mensagemConvencao = new MensagemConvencao();
                    TipoServicoDao dbTipo = new TipoServicoDao();

                    Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(listaServico.get(indexServico).getDescription()));
                    TipoServico tipoServico = dbTipo.pesquisaCodigo(Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()));

                    if ((servicos != null) && (tipoServico != null)) {
                        mensagemConvencao.setConvencao(convencao);
                        mensagemConvencao.setGrupoCidade(grupoCidade);
                        mensagemConvencao.setTipoServico(tipoServico);
                        mensagemConvencao.setServicos(servicos);
                        mensagemConvencao.setReferencia(referencia);
                        mensagemConvencao.setVencimento(vencimento);
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mensagemPesquisa", mensagemConvencao);

                        atualizaGeracao();

                        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).mensagem();
                    }
                }
            }
        }
        return null;
    }

    public void imprimir() {
        this.imprimir(false);
    }

    public void imprimir(Boolean download) {
        List<Movimento> movs = new ArrayList();

        Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        Dao dao = new Dao();

        dao.openTransaction();
        for (ListaEmpresaEscritorio lee : listaEmpresaSelecionada) {

            movs.add(lee.getMovimento());

            Impressao impressao = new Impressao();

            impressao.setUsuario(usuario);
            impressao.setDtVencimento(lee.getMovimento().getDtVencimento());
            impressao.setMovimento(lee.getMovimento());

            if (!dao.save(impressao)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
                return;
            }
        }

        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();

        movs = imp.atualizaContaCobrancaMovimento(movs);

        imp.imprimirBoleto(movs, false, true);
        if (download) {
            imp.baixarArquivo();
        } else {
            imp.visualizar(null);
        }
    }

    public void enviarEmail() {
        Registro reg = Registro.get();
//
//        for (ListaEmpresaEscritorio lee : listaEmpresaSelecionada) {
//            // SET VALOR CALCULADO
//            // ((Movimento) listMovimentos.get(i).getArgumento1()).setValor(Moeda.substituiVirgulaDouble((String) listMovimentos.get(i).getArgumento7()));
//            movs.add(lee.getMovimento());
//        }

        List<Movimento> m = new ArrayList();
        
        List<File> fls = new ArrayList();

        if (escritorio.getPessoa().getEmail1() == null || escritorio.getPessoa().getEmail1().isEmpty()) {
            GenericaMensagem.error("Atenção", "Escritório sem email!");
            return;
        }

        List<Pessoa> pessoas = new ArrayList();
        pessoas.add(escritorio.getPessoa());

        String nome_envio = "";
        String mensagem = "";

        for (ListaEmpresaEscritorio lee : listaEmpresaSelecionada) {

            m.add(lee.getMovimento());

        }

        ImprimirBoleto imp = new ImprimirBoleto();

        m = imp.atualizaContaCobrancaMovimento(m);

        imp.imprimirBoleto(m, false, true);

        String nome = imp.criarLink(escritorio.getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");

        nome_envio = "Boleto: " + m.get(0).getServicos().getDescricao() + " Vencimento: " + vencimento;

        if (!reg.isEnviarEmailAnexo()) {
            mensagem = " <h5> Visualize seu boleto clicando no link abaixo </h5> <br /><br />"
                    + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir boleto</a><br />";
        } else {
            fls.add(new File(imp.getPathPasta() + "/" + nome));
            mensagem = "<h5>Segue boleto em anexo</h5><br /><br />";
        }

        if (nome_envio.isEmpty() || mensagem.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Email gerado INCORRETAMENTE!");
            return;
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
                        (Rotina) di.find(new Rotina(), 488),
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

    @Override
    public void carregarFolha() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void carregarFolha(DataObject valor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void carregarFolha(Object valor) {
        obListaEmpresa = (ListaEmpresaEscritorio) valor;

        super.carregarFolha(obListaEmpresa.getMovimento());
    }

    @Override
    public void atualizaValorGrid(String tipo) {
        obListaEmpresa.getMovimento().setValorString(super.atualizaValor(true, tipo));
        obListaEmpresa.setValorCalculado(valor_calculado(obListaEmpresa.getMovimento().getPessoa().getId(), obListaEmpresa.getMovimento().getServicos().getId(), obListaEmpresa.getMovimento().getTipoServico().getId(), obListaEmpresa.getMovimento().getReferencia(), obListaEmpresa.getMovimento().getValor()));
    }

    public void modalGerarBoleto() {
        if (listaEmpresaSelecionada.isEmpty()) {
            GenericaMensagem.warn("Atenção", "SELECIONE ALGUMA EMPRESA PARA GERAR BOLETOS");
            return;
        }
//          QUANDO QUISER ALTERAR O VENCIMENTO JUNTO COM A IMPRESSÃO
//        for (ListaEmpresaEscritorio lee : listaEmpresaSelecionada) {
//            lee.getMovimento().setVencimento(vencimento);
//            lee.getMovimento().setValor(lee.getValorCalculado());
//        }

        modalBoletosVisible = true;
        processou = false;
    }

    public void gerarBoletos() {

        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();

        for (ListaEmpresaEscritorio lee : listaEmpresaSelecionada) {
            Boolean success = true;

            if (lee.getMovimento().getId() != -1) {

                Movimento movimentoBefore = (Movimento) dao.find(lee.getMovimento());

                String beforeUpdate
                        = " Movimento: (" + movimentoBefore.getId() + ") "
                        + " - Referência: (" + movimentoBefore.getReferencia()
                        + " - Tipo Serviço: (" + movimentoBefore.getTipoServico().getId() + ") " + lee.getMovimento().getTipoServico().getDescricao()
                        + " - Serviços: (" + movimentoBefore.getServicos().getId() + ") " + lee.getMovimento().getServicos().getDescricao()
                        + " - Pessoa: (" + movimentoBefore.getPessoa().getId() + ") " + lee.getMovimento().getPessoa().getNome()
                        + " - Valor: " + movimentoBefore.getValorString()
                        + " - Vencimento: " + movimentoBefore.getVencimento();

//                // SE ALTERAR O VENCIMENTO E FOR COBRANÇA REGISTRADA, ENTÃO ALTERAR A DATA DE REGISTRO PARA QUANDO IMPRIMIR REGISTRAR NOVAMENTE
//                if (!movimentoBefore.getVencimento().equals(lee.getMovimento().getVencimento())) {
//                    Boleto bol = finDB.pesquisaBoletos(lee.getMovimento().getNrCtrBoleto());
//                    if (bol != null) {
//                        if (bol.getContaCobranca().getCobrancaRegistrada().getId() != 3) {
//                            bol.setDtCobrancaRegistrada(null);
//                            new Dao().update(bol, true);
//                        }
//                    }
//                }
                if (GerarMovimento.alterarUmMovimento(lee.getMovimento(), lee.getMovimento().getDtVencimento())) {
                    novoLog.update(beforeUpdate,
                            " Movimento: (" + lee.getMovimento().getId() + ") "
                            + " - Referência: (" + lee.getMovimento().getReferencia()
                            + " - Tipo Serviço: (" + lee.getMovimento().getTipoServico().getId() + ") " + lee.getMovimento().getTipoServico().getDescricao()
                            + " - Serviços: (" + lee.getMovimento().getServicos().getId() + ") " + lee.getMovimento().getServicos().getDescricao()
                            + " - Pessoa: (" + lee.getMovimento().getPessoa().getId() + ") " + lee.getMovimento().getPessoa().getNome()
                            + " - Valor: " + lee.getMovimento().getValorString()
                            + " - Vencimento: " + lee.getMovimento().getVencimento()
                    );

                } else {
                    GenericaMensagem.warn("Erro", "Não foi possível alterar boletos!");
                    success = false;
                }
            } else {

                StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimento(new Lote(), lee.getMovimento(), lee.getMovimento().getDtVencimento(), lee.getValorCalculado());

                if (sr.getStatus()) {
                    novoLog.save(
                            " Movimento: (" + lee.getMovimento().getId() + ") "
                            + " - Referência: (" + lee.getMovimento().getReferencia()
                            + " - Tipo Serviço: (" + lee.getMovimento().getTipoServico().getId() + ") " + lee.getMovimento().getTipoServico().getDescricao()
                            + " - Serviços: (" + lee.getMovimento().getServicos().getId() + ") " + lee.getMovimento().getServicos().getDescricao()
                            + " - Pessoa: (" + lee.getMovimento().getPessoa().getId() + ") " + lee.getMovimento().getPessoa().getNome()
                            + " - Valor: " + lee.getMovimento().getValorString()
                            + " - Vencimento: " + lee.getMovimento().getVencimento()
                    );

                } else {

                    GenericaMensagem.warn("Erro", sr.getMensagem());
                    success = false;

                }
            }

            if (success) {
                Historico h = lee.getMovimento().getHistorico();
                if (h != null) {
                    if (h.getId() == -1 && !h.getComplemento().isEmpty() && !h.getHistorico().isEmpty()) {
                        h.setMovimento(lee.getMovimento());
                        new Dao().save(h, true);
                    } else {
                        new Dao().update(h, true);
                    }
                }
            }
        }

        GenericaMensagem.info("Sucesso", "Boletos foram gerados!");
        processou = true;
    }

    public void fecharModalGerarBoleto() {
        modalBoletosVisible = false;

        if (processou) {
            loadListaEmpresa();
        }
    }

    public void atualizaGeracao() {
        podeListarEmpresa = false;

        listaEmpresa.clear();
        listaEmpresaSelecionada.clear();
    }

    public final void loadListaEmpresa() {
        listaEmpresa.clear();
        listaEmpresaSelecionada.clear();

        if (referencia.isEmpty() || vencimento.isEmpty()) {
            GenericaMensagem.error("Atenção", "DIGITE UMA REFERÊNCIA E VENCIMENTO!");
            return;
        }

        if (!new DataHoje().integridadeReferencia(referencia)) {
            GenericaMensagem.error("Atenção", "REFERÊNCIA INVÁLIDA!");
            return;
        }

        Dao dao = new Dao();

        Servicos servico = (Servicos) dao.find(new Servicos(), Integer.valueOf(listaServico.get(indexServico).getDescription()));
        TipoServico tipoServico = (TipoServico) dao.find(new TipoServico(), Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()));

        ImpressaoEscritorioDao idao = new ImpressaoEscritorioDao();

        List<Object> result = idao.listaEmpresa(escritorio.getId(), servico.getId(), tipoServico.getId(), referencia);

        if (result.isEmpty()) {
            GenericaMensagem.error("Atenção", "ESCRITÓRIO SEM EMPRESA VINCULADA!");
            return;
        }

        MensagemConvencaoDao menDB = new MensagemConvencaoDao();
        MovimentoDao finDB = new MovimentoDao();

        for (Object ob : result) {
            List linha = ((List) ob);
            // MensagemConvencao mc = null;
            MensagemConvencao mc = menDB.retornaDiaString(
                    (Integer) linha.get(1),
                    referencia,
                    tipoServico.getId(),
                    servico.getId()
            );

            Boolean podeGerar = true;
            String erros = "";

            // VALIDA MENSAGEM -------------------------------------------------
            Boolean temMensagem = true;
            if (mc == null) {
                temMensagem = false;
                podeGerar = false;
                erros = "| NÃO TEM MENSAGEM |";
            }

            // VALIDA ACORDO ---------------------------------------------------
            //List<Movimento> lm_acordado  = new ArrayList();
            List<Movimento> lm_acordado = finDB.listaMovimentoAcordado(
                    (Integer) linha.get(0),
                    referencia,
                    tipoServico.getId(),
                    servico.getId()
            );

            if (!lm_acordado.isEmpty()) {
                erros += "| REFERÊNCIA JÁ FOI ACORDADA |";
                podeGerar = false;
            }

            // VALIDA MOVIMENTO ----------------------------------------------------
            //List<Movimento> lm = new ArrayList();
            List<Movimento> lm = finDB.pesquisaMovimentos(
                    (Integer) linha.get(0),
                    referencia,
                    tipoServico.getId(),
                    servico.getId()
            );

            Movimento movimento_gerado;

            if (!lm.isEmpty()) {

                if (lm.size() > 1) {
                    erros += "| MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR |";
                    podeGerar = false;
                }

                if (lm.get(0).getBaixa() != null && lm.get(0).getBaixa().getId() != -1) {
                    erros += "| MOVIMENTO JÁ FOI BAIXADO |";
                    podeGerar = false;
                }

                movimento_gerado = lm.get(0);

            } else {
                //Double valor_boleto = new Double(0);
                Double valor_boleto = Moeda.converteDoubleR$Double(
                        super.carregarValor(
                                servico.getId(),
                                tipoServico.getId(),
                                referencia,
                                (Integer) linha.get(0)
                        )
                );

                Pessoa pessoa = (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(0));

                movimento_gerado = new Movimento(
                        -1,
                        null,
                        servico.getPlano5(),
                        pessoa,
                        servico,
                        null,
                        tipoServico,
                        null,
                        valor_boleto,
                        referencia,
                        vencimento,
                        1,
                        true,
                        "E",
                        false,
                        pessoa,
                        pessoa,
                        "",
                        "",
                        vencimento,
                        0, 0, 0, 0, 0, 0, 0, (FTipoDocumento) dao.find(new FTipoDocumento(), 2), 0, null
                );
            }

            Double valor_calculado;
            if (todosAcrescimos) {
                valor_calculado = valor_calculado((Integer) linha.get(0), servico.getId(), tipoServico.getId(), referencia, movimento_gerado.getValor());
            } else {
                valor_calculado = movimento_gerado.getValor();
            }

//            RESULTADO DIRETO DA QUERY
//            Double juros = (Double) linha.get(4);
//            Double multa = (Double) linha.get(5);
//            Double correcao = (Double) linha.get(6);
//        Double valor_calculado = Moeda.soma(
//                Moeda.soma(Moeda.soma(juros, multa), correcao), movimento_gerado.getValor()
//        );
//        
            listaEmpresa.add(
                    new ListaEmpresaEscritorio(
                            (Juridica) dao.find(new Juridica(), (Integer) linha.get(1)),
                            temMensagem,
                            podeGerar,
                            erros,
                            movimento_gerado,
                            valor_calculado,
                            todosAcrescimos
                    )
            );

        }

        podeListarEmpresa = true;
    }

    public Double valor_calculado(Integer id_pessoa, Integer id_servico, Integer id_tipo_servico, String referencia, Double valorx) {

        MovimentoDao db = new MovimentoDao();
        Double juros = db.funcaoJuros(id_pessoa, id_servico, id_tipo_servico, referencia);
        Double multa = db.funcaoMulta(id_pessoa, id_servico, id_tipo_servico, referencia);
        Double correcao = db.funcaoCorrecao(id_pessoa, id_servico, id_tipo_servico, referencia);
        Double valor_calculado = Moeda.soma(
                Moeda.soma(Moeda.soma(juros, multa), correcao), valorx
        );

        return valor_calculado;
    }

    public void atualiza_valor_acrescimo(ListaEmpresaEscritorio lee) {
        if (lee.getAdicionaAcrescimo()) {
            lee.setValorCalculado(valor_calculado(lee.getMovimento().getPessoa().getId(), lee.getMovimento().getServicos().getId(), lee.getMovimento().getTipoServico().getId(), referencia, lee.getMovimento().getValor()));
        } else {
            lee.setValorCalculado(lee.getMovimento().getValor());
        }
    }

    public void atualiza_todos_acrescimo() {

        for (ListaEmpresaEscritorio lee : listaEmpresa) {
            if (todosAcrescimos) {
                lee.setValorCalculado(valor_calculado(lee.getMovimento().getPessoa().getId(), lee.getMovimento().getServicos().getId(), lee.getMovimento().getTipoServico().getId(), referencia, lee.getMovimento().getValor()));
                lee.setAdicionaAcrescimo(true);
            } else {
                lee.setValorCalculado(lee.getMovimento().getValor());
                lee.setAdicionaAcrescimo(false);
            }
        }

        todosAcrescimos = !todosAcrescimos;
    }

    public final void loadListaServico() {
        listaServico.clear();
        listaEmpresaSelecionada.clear();

        ServicosDao db = new ServicosDao();
        List<Servicos> result = db.pesquisaTodos(4);
        for (int i = 0; i < result.size(); i++) {
            listaServico.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }

    }

    public final void loadListaTipoServico() {
        listaTipoServico.clear();

        List<Integer> listaIds = new ArrayList();

        listaIds.add(1);
        listaIds.add(2);
        listaIds.add(3);

        TipoServicoDao db = new TipoServicoDao();
        List<TipoServico> result = db.pesquisaTodosComIds(listaIds);

        for (int i = 0; i < result.size(); i++) {
            listaTipoServico.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public List<SelectItem> getListaServico() {
        return listaServico;
    }

    public void setListaServico(List<SelectItem> listaServico) {
        this.listaServico = listaServico;
    }

    public Integer getIndexServico() {
        return indexServico;
    }

    public void setIndexServico(Integer indexServico) {
        this.indexServico = indexServico;
    }

    public List<SelectItem> getListaTipoServico() {
        return listaTipoServico;
    }

    public void setListaTipoServico(List<SelectItem> listaTipoServico) {
        this.listaTipoServico = listaTipoServico;
    }

    public Integer getIndexTipoServico() {
        return indexTipoServico;
    }

    public void setIndexTipoServico(Integer indexTipoServico) {
        this.indexTipoServico = indexTipoServico;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public Juridica getEscritorio() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            // CASO TROQUE DE ESCRITÓRIO E A LISTA JÁ ESTA CARREGADA
            if (escritorio.getId() != ((Juridica) GenericaSessao.getObject("juridicaPesquisa")).getId() ){
                podeListarEmpresa = false;
            }
            
            escritorio = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return escritorio;
    }

    public void setEscritorio(Juridica escritorio) {
        this.escritorio = escritorio;
    }

    public class ListaEmpresaEscritorio {

        private Juridica empresa;
        private Boolean temMensagem;
        private Boolean podeGerarBoleto;
        private String mensagemErro;
        private Movimento movimento;
        private Double valorCalculado;
        private Boolean adicionaAcrescimo;

        public ListaEmpresaEscritorio(Juridica empresa, Boolean temMensagem, Boolean podeGerarBoleto, String mensagemErro, Movimento movimento, Double valorCalculado, Boolean adicionaAcrescimo) {
            this.empresa = empresa;
            this.temMensagem = temMensagem;
            this.podeGerarBoleto = podeGerarBoleto;
            this.mensagemErro = mensagemErro;
            this.movimento = movimento;
            this.valorCalculado = valorCalculado;
            this.adicionaAcrescimo = adicionaAcrescimo;
        }

        public Juridica getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Juridica empresa) {
            this.empresa = empresa;
        }

        public Boolean getTemMensagem() {
            return temMensagem;
        }

        public void setTemMensagem(Boolean temMensagem) {
            this.temMensagem = temMensagem;
        }

        public Boolean getPodeGerarBoleto() {
            return podeGerarBoleto;
        }

        public void setPodeGerarBoleto(Boolean podeGerarBoleto) {
            this.podeGerarBoleto = podeGerarBoleto;
        }

        public String getMensagemErro() {
            return mensagemErro;
        }

        public void setMensagemErro(String mensagemErro) {
            this.mensagemErro = mensagemErro;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Double getValorCalculado() {
            return valorCalculado;
        }

        public void setValorCalculado(Double valorCalculado) {
            this.valorCalculado = valorCalculado;
        }

        public String getValorCalculadoString() {
            return Moeda.converteDoubleToString(valorCalculado);
        }

        public void setValorCalculadoString(String valorCalculadoString) {
            this.valorCalculado = Moeda.converteStringToDouble(valorCalculadoString);
        }

        public Boolean getAdicionaAcrescimo() {
            return adicionaAcrescimo;
        }

        public void setAdicionaAcrescimo(Boolean adicionaAcrescimo) {
            this.adicionaAcrescimo = adicionaAcrescimo;
        }

    }

    public List<ListaEmpresaEscritorio> getListaEmpresa() {
        return listaEmpresa;
    }

    public void setListaEmpresa(List<ListaEmpresaEscritorio> listaEmpresa) {
        this.listaEmpresa = listaEmpresa;
    }

    public List<ListaEmpresaEscritorio> getListaEmpresaSelecionada() {
        return listaEmpresaSelecionada;
    }

    public void setListaEmpresaSelecionada(List<ListaEmpresaEscritorio> listaEmpresaSelecionada) {
        this.listaEmpresaSelecionada = listaEmpresaSelecionada;
    }

    public Boolean getModalBoletosVisible() {
        return modalBoletosVisible;
    }

    public void setModalBoletosVisible(Boolean modalBoletosVisible) {
        this.modalBoletosVisible = modalBoletosVisible;
    }

    public Boolean getPodeListarEmpresa() {
        return podeListarEmpresa;
    }

    public void setPodeListarEmpresa(Boolean podeListarEmpresa) {
        this.podeListarEmpresa = podeListarEmpresa;
    }

    public Boolean getProcessou() {
        return processou;
    }

    public void setProcessou(Boolean processou) {
        this.processou = processou;
    }

    public Boolean getTodosAcrescimos() {
        return todosAcrescimos;
    }

    public void setTodosAcrescimos(Boolean todosAcrescimos) {
        this.todosAcrescimos = todosAcrescimos;
    }
}
