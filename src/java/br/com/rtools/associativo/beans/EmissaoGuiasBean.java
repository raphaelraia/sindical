package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioServicoDao;
import br.com.rtools.associativo.dao.LancamentoIndividualDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.estoque.Estoque;
import br.com.rtools.estoque.EstoqueTipo;
import br.com.rtools.estoque.Pedido;
import br.com.rtools.estoque.Produto;
import br.com.rtools.estoque.dao.ProdutoDao;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.DescontoServicoEmpresaDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.lista.ListMovimentoEmissaoGuias;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.utilitarios.AutenticaUsuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.StatusRetorno;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class EmissaoGuiasBean implements Serializable {

    private Filial filial;
    private Pessoa pessoa;
    private Fisica fisica;
    private Lote lote;
    private int quantidade;
    private List<Movimento> listaMovimentoAuxiliar;
    private List<HistoricoEmissaoGuias> listHistoricoEmissaoGuias;
    private String valor;
    private String desconto;
    private String total;
    private String message;

    // MODIFICAÇÕES BRUNO
    private boolean modalPedido;
    private int quantidadePedido;
    private String valorUnitarioPedido;
    private String descontoUnitarioPedido;
    private List<Pedido> listPedidos;
    private String valorTotal;
    private List<ListMovimentoEmissaoGuias> listaMovimento;
    private Juridica juridica;
    private Pedido pedido;
    private Estoque estoque;
    //private boolean enabledItensPedido;
    /**
     * <ul>
     * <li> Indices </li>
     * <li>[0] Grupo </li>
     * <li>[1] Subgrupo </li>
     * <li>[2] Serviços </li>
     * <li>[3] Jurídica </li>
     * </ul>
     */
    private List<SelectItem>[] listSelectItem;
    private Integer[] index;

    /**
     * <ul>
     * <li> Variáveis </li>
     * <li>[0] Valor </li>
     * <li>[1] Desconto </li>
     * <li>[2] Total </li>
     * <li>[3] Mensagem </li>
     * <li>[4] Mensagem Filial</li>
     * </ul>
     */
    private String[] var;

    private Socios socios;
    private List<Movimento> listaMovimentosEmitidos;
    private Servicos servicox = new Servicos();
    private Fisica fisicaNovoCadastro = new Fisica();

    private ControleAcessoBean cab = new ControleAcessoBean();

    private String novoDesconto = "0,00";
    private String observacao = "";

    private List<SelectItem> listParceiro;
    private Integer idParceiro;

    @PostConstruct
    public void init() {
        estoque = new Estoque();
        filial = MacFilial.getAcessoFilial().getFilial();
        pessoa = new Pessoa();
        fisica = new Fisica();
        lote = new Lote();
        quantidade = 1;
        listaMovimentoAuxiliar = new ArrayList();
        listHistoricoEmissaoGuias = new ArrayList();
        valor = "";
        desconto = "";
        total = "";
        listaMovimento = new ArrayList();
        //validaPessoa = false;
        // MODIFICAÇÕES BRUNO
        modalPedido = false;
        quantidadePedido = 1;
        valorUnitarioPedido = "";
        descontoUnitarioPedido = "";
        listPedidos = new ArrayList();
        valorTotal = "0";
        message = "";
        juridica = new Juridica();
        pedido = new Pedido();
        listSelectItem = new ArrayList[]{
            new ArrayList(),
            new ArrayList(),
            new ArrayList(),
            new ArrayList()
        };
        listParceiro = new ArrayList();
        //enabledItensPedido = false;
        index = new Integer[]{0, 0, 0, 0};
        var = new String[]{"", "", "", "", ""};
        idParceiro = -1;

        getListGrupo();
        getListSubGrupo();
        if (!listSelectItem[0].isEmpty() && !listSelectItem[1].isEmpty()) {
            SubGrupoConvenio sgc = (SubGrupoConvenio) (new Dao()).find(new SubGrupoConvenio(), Integer.parseInt(getListSubGrupo().get(index[1]).getDescription()));
            //lote.setHistorico(sgc.getObservacao());
            observacao = sgc.getObservacao();
        }

        socios = new Socios();
        listaMovimentosEmitidos = new ArrayList();
        servicox = (Servicos) new Dao().find(new Servicos(), Integer.parseInt(getListServicos().get(index[2]).getDescription()));

        atualizarHistorico();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("emissaoGuiasBean");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("pessoaPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("produtoBean");
        GenericaSessao.remove("produtoPesquisa");
        GenericaSessao.remove("listaMovimento");
        GenericaSessao.remove("usuarioAutenticado");
        GenericaSessao.remove("sessaoSisAutorizacao");
    }

    public void autorizarDesconto() {
        GenericaSessao.put("AutenticaUsuario", new AutenticaUsuario("dlg_desconto", "autorizaDescontos", 3));
    }

    public void adicionarDesconto() {
        desconto = novoDesconto;
        PF.closeDialog("dlg_autentica_usuario");
        PF.closeDialog("dlg_desconto");
        PF.update("form_eg:i_panel_servicos");
    }

    public void autenticaUsuario() {
        // se TRUE NÃO tem permissao
        if (cab.verificarPermissao("autorizaDescontos", 3)) {
            PF.openDialog("dlg_autentica_usuario");
        }
    }

    public void pesquisaPessoaSindicato() {
        Juridica jur = (Juridica) new Dao().find(new Juridica(), 1);
        GenericaSessao.put("juridicaPesquisa", jur);
        getPessoa();
    }

    public String calculoIdade() {
        if (fisica != null && fisica.getId() != -1 && !fisica.getNascimento().isEmpty()) {
            DataHoje dh = new DataHoje();
            return "" + dh.calcularIdade(fisica.getNascimento());
        } else {
            return "NAO ENCONTRADA";
        }
    }

    public String verMovimentosEmitidos(Movimento linha) {
        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).movimentosReceberSocial();

        GenericaSessao.put("pessoaPesquisa", linha.getPessoa());
        return retorno;
    }

    public Guia pesquisaGuia(int id_lote) {
        MovimentosReceberSocialBean mr = new MovimentosReceberSocialBean();
        Guia gu = mr.pesquisaGuia(id_lote);
        return gu;
    }

    public void imprimirEncaminhamento() {
        MovimentosReceberSocialBean mr = new MovimentosReceberSocialBean();

        mr.encaminhamento(lote.getId());
    }

    public String baixarErrado(HistoricoEmissaoGuias heg) {
        if (!listHistoricoEmissaoGuias.isEmpty()) {
            listaMovimentoAuxiliar.clear();

            for (HistoricoEmissaoGuias listHistoricoEmissaoGuia : listHistoricoEmissaoGuias) {
                if (heg.getMovimento().getLote().getId() == listHistoricoEmissaoGuia.getMovimento().getLote().getId() && listHistoricoEmissaoGuia.getMovimento().getBaixa() == null) {
                    listaMovimentoAuxiliar.add(listHistoricoEmissaoGuia.getMovimento());
                }
            }

            if (!listaMovimentoAuxiliar.isEmpty()) {
                listHistoricoEmissaoGuias.clear();
                GenericaSessao.put("listaMovimento", listaMovimentoAuxiliar);
                GenericaSessao.put("caixa_banco", "caixa");
                GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
                return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
            }
        }
        return null;
    }

    public String excluirErrado(HistoricoEmissaoGuias heg) {
        if (!listHistoricoEmissaoGuias.isEmpty()) {
            listaMovimentoAuxiliar.clear();

            Dao di = new Dao();
            MovimentoDao db = new MovimentoDao();
            di.openTransaction();

            for (HistoricoEmissaoGuias listHistoricoEmissaoGuia : listHistoricoEmissaoGuias) {
                if (heg.getMovimento().getLote().getId() == listHistoricoEmissaoGuia.getMovimento().getLote().getId()) {
                    listaMovimentoAuxiliar.add(listHistoricoEmissaoGuia.getMovimento());
                    if (!di.delete(listHistoricoEmissaoGuia)) {
                        di.rollback();
                        return null;
                    }
                }
            }

            // GUIA
            Guia guias = db.pesquisaGuias(heg.getMovimento().getLote().getId());
            if (guias.getId() != -1) {
                if (!di.delete(guias)) {
                    di.rollback();
                    return null;
                }
            }

            // MOVIMENTO
            for (Movimento listaMovimentoAuxiliar1 : listaMovimentoAuxiliar) {
                if (listaMovimentoAuxiliar1.getId() != -1) {
                    if (!di.delete(listaMovimentoAuxiliar1)) {
                        di.rollback();
                        return null;
                    }
                }
            }

            // LOTE
            if (heg.getMovimento().getLote().getId() != -1) {
                if (!di.delete(heg.getMovimento().getLote())) {
                    di.rollback();
                    return null;
                }
            }
            di.commit();
            listHistoricoEmissaoGuias.clear();
        }
        return "menuPrincipal";
    }

    public void atualizarHistorico() {
        if (!GenericaSessao.exists("sessaoUsuario")) {
            return;
        }
        Dao di = new Dao();
        MovimentoDao db = new MovimentoDao();
        Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        List<HistoricoEmissaoGuias> listHEGuias = db.pesquisaHistoricoEmissaoGuias(usuario.getId());

        if (listHEGuias.isEmpty()) {
            return;
        }

        di.openTransaction();
        for (HistoricoEmissaoGuias list_h : listHEGuias) {
            if (list_h.getMovimento().getBaixa() != null) {
                list_h.setBaixado(true);
            }
            if (!di.update(list_h)) {
                di.rollback();
                return;
            }
        }

        di.commit();
    }

    public List<HistoricoEmissaoGuias> getListHistoricoEmissaoGuias() {
        MovimentoDao db = new MovimentoDao();
        Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        if (usuario != null) {
            listHistoricoEmissaoGuias = db.pesquisaHistoricoEmissaoGuias(usuario.getId());
        }
        return listHistoricoEmissaoGuias;
    }

    public void setListHistoricoEmissaoGuias(List<HistoricoEmissaoGuias> listHistoricoEmissaoGuias) {
        this.listHistoricoEmissaoGuias = listHistoricoEmissaoGuias;
    }

    /**
     * <ul>
     * <li>tcase = 3 => Nova pessoa </li>
     * </ul>
     *
     * @param tcase
     */
    public void clear(int tcase) {
        switch (tcase) {
            case 1:
                //listSelectItem[0] = new ArrayList();
                listSelectItem[1] = new ArrayList();
                listSelectItem[2] = new ArrayList();
                listSelectItem[3] = new ArrayList();
                //index[0] = 0;
                index[1] = 0;
                index[2] = 0;
                index[3] = 0;
                getListSubGrupo();
                getListServicos();
                getListJuridica();
                listenerEnabledItensPedido();
                if (!listSelectItem[0].isEmpty() && !listSelectItem[1].isEmpty()) {
                    SubGrupoConvenio sgc = (SubGrupoConvenio) (new Dao()).find(new SubGrupoConvenio(), Integer.parseInt(getListSubGrupo().get(index[1]).getDescription()));
                    //lote.setHistorico(sgc.getObservacao());
                    observacao = sgc.getObservacao();
                }
                break;
            case 2:
                //listSelectItem[0] = new ArrayList();
                listSelectItem[2] = new ArrayList();
                listSelectItem[3] = new ArrayList();
                index[2] = 0;
                index[3] = 0;
                getListSubGrupo();
                getListServicos();
                getListJuridica();
                listenerEnabledItensPedido();
                if (!listSelectItem[0].isEmpty() && !listSelectItem[1].isEmpty()) {
                    SubGrupoConvenio sgc = (SubGrupoConvenio) (new Dao()).find(new SubGrupoConvenio(), Integer.parseInt(getListSubGrupo().get(index[1]).getDescription()));
                    //lote.setHistorico(sgc.getObservacao());
                    observacao = sgc.getObservacao();
                }
                break;
            case 3:
                pessoa = new Pessoa();
                juridica = new Juridica();
                fisica = new Fisica();
                fisicaNovoCadastro = new Fisica();
                break;
        }
    }

    public void pesquisaSemCadastro(String por) {
        FisicaDao dbf = new FisicaDao();
        if (por.equals("cpf") && fisicaNovoCadastro.getId() == -1) {
            if (!fisicaNovoCadastro.getPessoa().getDocumento().isEmpty()) {
                List<Fisica> lista = dbf.pesquisaFisicaPorDoc(fisicaNovoCadastro.getPessoa().getDocumento());
                if (!lista.isEmpty()) {
                    message = "Este CPF já esta cadastrado!";
                    GenericaMensagem.warn("Validação", message);
                    fisicaNovoCadastro = lista.get(0);
                    return;
                }
            }
        }

        if (por.equals("rg") && fisicaNovoCadastro.getId() == -1) {
            if (!fisicaNovoCadastro.getRg().isEmpty()) {
                List<Fisica> lista = dbf.pesquisaFisicaPorNomeNascRG("", null, fisicaNovoCadastro.getRg());
                if (!lista.isEmpty()) {
                    message = "Este RG já esta cadastrado!";
                    GenericaMensagem.warn("Validação", message);
                    fisicaNovoCadastro = lista.get(0);
                    return;
                }
            }
        }

        if (por.equals("nome") || por.equals("nascimento") && fisicaNovoCadastro.getId() == -1) {
            if (!fisicaNovoCadastro.getPessoa().getNome().isEmpty() && !fisicaNovoCadastro.getNascimento().isEmpty()) {
                List<Fisica> lista = dbf.pesquisaFisicaPorNomeNascRG(fisicaNovoCadastro.getPessoa().getNome(), fisicaNovoCadastro.getDtNascimento(), fisicaNovoCadastro.getRg());
                if (!lista.isEmpty()) {
                    message = "Esta pessoa já esta cadastrada em nosso sistema!";
                    GenericaMensagem.warn("Validação", message);
                    fisicaNovoCadastro = lista.get(0);
                }
            }
        }
    }

    public void clear() {
        GenericaSessao.remove("emissaoGuiasBean");
        GenericaSessao.exists("sessaoSisAutorizacao");
    }

    public void selecionarPessoaCadastro() {
        if (fisicaNovoCadastro.getId() != -1) {
            if (new FunctionsDao().inadimplente(fisicaNovoCadastro.getPessoa().getId())) {
                GenericaMensagem.error("Atenção", "Esta pessoa possui débitos com o Sindicato!");
                return;
            }

            fisica = fisicaNovoCadastro;
            pessoa = fisicaNovoCadastro.getPessoa();
        }
    }

    public void saveSemCadastro() {
        if (fisicaNovoCadastro.getPessoa().getId() != -1) {
            GenericaMensagem.fatal("Erro", "Verifique os dados antes de salvar!");
            return;
        }
        if (fisicaNovoCadastro.getPessoa().getNome().isEmpty()) {
            message = "Nome não pode estar vazio!";
            return;
        }
        Dao di = new Dao();

        //fisicaNovoCadastro.setPessoa(pessoa);
        fisicaNovoCadastro.getPessoa().setTipoDocumento((TipoDocumento) di.find(new TipoDocumento(), 1));

        if (fisicaNovoCadastro.getPessoa().getId() == -1 && fisicaNovoCadastro.getId() == -1) {
            List<String> list_log = new ArrayList();
            di.openTransaction();
            if (!di.save(fisicaNovoCadastro.getPessoa())) {
                message = "Falha ao salvar Pessoa!";
                di.rollback();
                return;
            }

            if (!di.save(fisicaNovoCadastro)) {
                message = "Falha ao salvar Física!";
                di.rollback();
                return;
            }

            Registro reg = (Registro) di.find(new Registro(), 1);
            PessoaComplemento pc = new PessoaComplemento(
                    -1,
                    fisicaNovoCadastro.getPessoa(),
                    reg.getFinDiaVencimentoCobranca(),
                    false,
                    null,
                    false,
                    ""
            );

            if (!di.save(pc)) {
                message = "Falha ao salvar Pessoa Complemento!";
                di.rollback();
                return;
            }
            message = "Pessoa salva com Sucesso!";
            di.commit();

            list_log.add("** SALVAR NOVA PESSOA FÍSICA **");
            list_log.add("Pessoa ID: " + fisicaNovoCadastro.getPessoa().getId());
            list_log.add("Pessoa Nome: " + fisicaNovoCadastro.getPessoa().getNome());
            list_log.add("Pessoa CPF: " + fisicaNovoCadastro.getPessoa().getDocumento());
            list_log.add("Pessoa Telefone: " + fisicaNovoCadastro.getPessoa().getTelefone1());
            list_log.add("-------------------------------------------------------------------");
            list_log.add("Física ID: " + fisicaNovoCadastro.getId());
            list_log.add("Física RG: " + fisicaNovoCadastro.getRg());
            list_log.add("Física Nascimento: " + fisicaNovoCadastro.getNascimento());

            String save_log = "";

            for (String string_x : list_log) {
                save_log += string_x + " \n";
            }

            NovoLog novoLog = new NovoLog();

            novoLog.save(
                    save_log
            );

            selecionarPessoaCadastro();

            fisicaNovoCadastro = new Fisica();
        }
    }

    public void addServico() {
        if (!listaMovimento.isEmpty()) {
            if (listaMovimento.get(0).getMovimento().getId() != -1) {
                GenericaMensagem.warn("Validação", "Emissão concluída!");
                return;
            }
        }
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquise uma pessoa para gerar Parcelas");
            return;
        }
        if (getListServicos().isEmpty() || listSelectItem[2].get(index[2]).getDescription().equals("0")) {
            GenericaMensagem.warn("Validação", "A lista de serviços não pode estar vazia!");
            return;
        }

        String vencto_ini = DataHoje.data();
        Dao di = new Dao();
        FTipoDocumento fTipoDocumento = (FTipoDocumento) di.find(new FTipoDocumento(), 2); // FTipo_documento 13 - CARTEIRA, 2 - BOLETO
        float valorx = Moeda.converteUS$(valor);
        Servicos servicos = (Servicos) di.find(new Servicos(), Integer.parseInt(getListServicos().get(index[2]).getDescription()));
        MovimentoDao db = new MovimentoDao();
        //SociosDB dbs = new SociosDao();
        listaMovimentosEmitidos.clear();
        if (servicos.getPeriodo() != null) {
            DataHoje dh = new DataHoje();
            // SEM CONTROLE FAMILIAR ---
            if (!servicos.isFamiliarPeriodo()) {

                if (!servicos.isValidadeGuiasVigente()) {
                    listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoPeriodoAtivo(pessoa.getId(), servicos.getId(), servicos.getPeriodo().getDias(), false);
                    if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                        GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!listaMovimentosEmitidos.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), listaMovimentosEmitidos.get(0).getLote().getEmissao()) : ""));
                        return;
                    }
                    listaMovimentosEmitidos.clear();
                } else {
                    listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoMesVigente(pessoa.getId(), servicos.getId(), false);
                    if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                        GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                        return;
                    }
                    listaMovimentosEmitidos.clear();
                }

            }

            // COM CONTROLE FAMILIAR --- 
            if (servicos.isFamiliarPeriodo()) {
                //Socios socios = dbs.pesquisaSocioPorPessoaAtivo(pessoa.getId());

                // NÃO SÓCIO ---
                if (socios.getId() == -1) {
                    if (!servicos.isValidadeGuiasVigente()) {
                        listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoPeriodoAtivo(pessoa.getId(), servicos.getId(), servicos.getPeriodo().getDias(), false);
                        if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!listaMovimentosEmitidos.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), listaMovimentosEmitidos.get(0).getLote().getEmissao()) : ""));
                            return;
                        }
                        listaMovimentosEmitidos.clear();
                    } else {
                        listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoMesVigente(pessoa.getId(), servicos.getId(), false);
                        if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                            GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                            return;
                        }
                        listaMovimentosEmitidos.clear();
                    }
                    // SOCIO ---
                } else if (!servicos.isValidadeGuiasVigente()) {
                    listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoPeriodoAtivo(socios.getMatriculaSocios().getId(), servicos.getId(), servicos.getPeriodo().getDias(), true);

                    if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                        GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! " + ((!listaMovimentosEmitidos.isEmpty()) ? " Liberação a partir de " + dh.incrementarDias(servicos.getPeriodo().getDias(), listaMovimentosEmitidos.get(0).getLote().getEmissao()) : ""));
                        return;
                    }
                    listaMovimentosEmitidos.clear();
                } else {
                    listaMovimentosEmitidos = db.listaMovimentoBeneficiarioServicoMesVigente(socios.getMatriculaSocios().getId(), servicos.getId(), true);
                    if (listaMovimentosEmitidos.size() >= servicos.getQuantidadePeriodo() && valorx == 0) {
                        GenericaMensagem.error("Atenção", "Excedido o limite de utilização deste serviço no periodo determinado! Liberação a partir de " + DataHoje.alterDay(1, dh.incrementarMeses(1, DataHoje.data())));
                        return;
                    }
                    listaMovimentosEmitidos.clear();
                }
            }
        }

        if (!servicos.isValorZerado()) {
            if (valorx == 0) {
                GenericaMensagem.warn("Validação", "Informar valor do serviço!");
                return;
            }
        }

        for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
            if (listaMovimento1.getMovimento().getServicos().getId() == servicos.getId()) {
                GenericaMensagem.warn("Validação", "Este serviço já foi adicionado!");
                return;
            }
        }
        if (servicos.isProduto()) {
            for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
                if (listaMovimento1.getMovimento().getServicos().isProduto()) {
                    GenericaMensagem.warn("Validação", "Serviço com produto(s) já cadastrado!");
                    return;
                }
            }
        }
        float descontox = Moeda.converteUS$(desconto);
        Pessoa pessoa_movimento = null;

        // Se o beneficiário for sócio então id_titular e id_pessoa do movimento será o titular.
        // Se o beneficiário NÃO for sócio então id_titular e  id_pessoa será o próprio beneficiário.
        // CHAMADO 775
        FunctionsDao dbfunc = new FunctionsDao();
        if (socios.getId() != -1) {
            pessoa_movimento = dbfunc.titularDaPessoa(pessoa.getId());
        } else {
            pessoa_movimento = pessoa;
        }
        listaMovimento.add(
                new ListMovimentoEmissaoGuias(
                        new Movimento(
                                -1,
                                new Lote(),
                                servicos.getPlano5(),
                                pessoa_movimento,
                                servicos,
                                null, // BAIXA
                                (TipoServico) di.find(new TipoServico(), 1), // TIPO SERVICO
                                null, // ACORDO
                                valorx, // VALOR
                                DataHoje.data().substring(3), // REFERENCIA
                                vencto_ini, // VENCIMENTO
                                quantidade, // QUANTIDADE
                                true, // ATIVO
                                "E", // ES
                                false, // OBRIGACAO
                                pessoa_movimento, // PESSOA TITULAR
                                pessoa, // PESSOA BENEFICIARIO
                                "", // DOCUMENTO
                                "", // NR_CTR_BOLETO
                                vencto_ini, // VENCIMENTO ORIGINAL
                                0, // DESCONTO ATE VENCIMENTO
                                0, // CORRECAO
                                0, // JUROS
                                0, // MULTA
                                0,//descontox, // DESCONTO
                                0, // TAXA
                                0,//Moeda.multiplicarValores(quantidade, Moeda.subtracaoValores(valorx, descontox)), // VALOR BAIXA
                                fTipoDocumento, // FTipo_documento 13 - CARTEIRA, 2 - BOLETO
                                0, // REPASSE AUTOMATICO
                                new MatriculaSocios()
                        ),
                        Moeda.converteR$Float(Moeda.converteFloatR$Float(valorx)), // VALOR COM MASCARA
                        Moeda.converteR$Float(Moeda.converteFloatR$Float(descontox)), // DESCONTO COM MASCARA
                        Moeda.converteR$Float(Moeda.multiplicarValores(quantidade, Moeda.subtracaoValores(valorx, descontox))) // VALOR TOTAL QUANTIDADE * (VALOR-DESCONTO)
                ));

        total = "0";
        for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
            String total_desconto = listaMovimento1.getTotal();
            total = Moeda.converteR$Float(Moeda.somaValores(Moeda.converteUS$(total), Moeda.converteUS$(total_desconto)));
        }
        desconto = "0";

    }

    public void removeServico(ListMovimentoEmissaoGuias lmeg) {
        for (int i = 0; i < listaMovimento.size(); i++) {

            if (lmeg.getMovimento().getServicos().getId() == listaMovimento.get(i).getMovimento().getServicos().getId()) {
                if (lmeg.getMovimento().getServicos().getId() != -1) {

                }
                listaMovimento.remove(i);
                PF.update("form_eg:i_tbl_eg");
                break;
            }
        }

        total = "0";
        for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
            String total_desconto = listaMovimento1.getTotal();
            total = Moeda.converteR$Float(Moeda.somaValores(Moeda.converteUS$(total), Moeda.converteUS$(total_desconto)));
        }
    }

    public String save() {
        if (pessoa.getId() == -1) {
            message = "Pesquise uma pessoa para gerar!";
            return null;
        }
        if (pessoa.getId() == -1) {
            message = "Pesquise uma pessoa para gerar!";
            return null;
        }
        if (getListServicos().isEmpty() || listSelectItem[2].get(index[2]).getDescription().equals("0")) {
            message = "A lista de serviços não pode estar vazia!";
            return null;
        }
        if (getListJuridica().isEmpty() || listSelectItem[3].get(index[3]).getDescription().equals("0")) {
            message = "Empresa Conveniada não encontrada!";
            return null;
        }
        if (listaMovimento.isEmpty()) {
            message = "A lista de lançamento não pode estar vazia!";
            return null;
        }
        Dao di = new Dao();
        Servicos serv = (Servicos) di.find(new Servicos(), Integer.parseInt(getListServicos().get(index[2]).getDescription()));

        List<String> list_log = new ArrayList();
        // CODICAO DE PAGAMENTO
        CondicaoPagamento cp = null;
        if (DataHoje.converteDataParaInteger(listaMovimento.get(0).getMovimento().getVencimento()) > DataHoje.converteDataParaInteger(DataHoje.data())) {
            cp = (CondicaoPagamento) di.find(new CondicaoPagamento(), 2);
        } else {
            cp = (CondicaoPagamento) di.find(new CondicaoPagamento(), 1);
        }

        list_log.add("Condição de Pagamento: " + cp.getDescricao());
        // TIPO DE DOCUMENTO  FTipo_documento 13 - CARTEIRA, 2 - BOLETO
        FTipoDocumento td = (FTipoDocumento) di.find(new FTipoDocumento(), 2);
        lote.setEmissao(DataHoje.data());
        lote.setAvencerContabil(false);
        lote.setPagRec("R");
        lote.setValor(Moeda.converteUS$(total));
        lote.setFilial(MacFilial.getAcessoFilial().getFilial());
        lote.setEvt(null);
        lote.setPessoa(pessoa);
        lote.setFTipoDocumento(td);
        lote.setRotina((Rotina) di.find(new Rotina(), 132));
        lote.setStatus((FStatus) di.find(new FStatus(), 1));
        lote.setPessoaSemCadastro(null);
        lote.setDepartamento(serv.getDepartamento());
        lote.setCondicaoPagamento(cp);
        lote.setPlano5(serv.getPlano5());
        lote.setDescontoFolha(false);

        di.openTransaction();
        if (!di.save(lote)) {
            message = " Erro ao salvar Lote!";
            di.rollback();
            return null;
        }

        list_log.add("Lote ID: " + lote.getId());
        list_log.add("Pessoa ID: " + listaMovimento.get(0).getMovimento().getPessoa().getId());
        list_log.add("Pessoa Nome: " + listaMovimento.get(0).getMovimento().getPessoa().getNome());

        for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
            listaMovimento1.getMovimento().setLote(lote);
            if (!di.save(listaMovimento1.getMovimento())) {
                message = " Erro ao salvar Movimento!";
                di.rollback();
                return null;
            }

            list_log.add("-------------------------------------------------------------------");
            list_log.add("Serviço ID: " + listaMovimento1.getMovimento().getServicos().getId());
            list_log.add("Serviço Nome: " + listaMovimento1.getMovimento().getServicos().getDescricao());
            list_log.add("Movimento ID: " + listaMovimento1.getMovimento().getId());
            list_log.add("Movimento Quantidade: " + listaMovimento1.getMovimento().getQuantidade());
            list_log.add("Movimento Valor Unitário: " + listaMovimento1.getValor());
            list_log.add("Movimento Desconto Unitário: " + listaMovimento1.getDesconto());
            list_log.add("Movimento Total: " + listaMovimento1.getTotal());
        }

        Estoque e;
        ProdutoDao produtoDao = new ProdutoDao();
        for (Pedido listPedido : listPedidos) {
            listPedido.setLote(lote);
            if (!di.save(listPedido)) {
                message = " Erro ao salvar Pedido!";
                di.rollback();
                return null;
            }
            int qtde = 0;
            e = produtoDao.listaEstoquePorProdutoFilial(listPedido.getProduto(), filial);
            e.setEstoque(e.getEstoque() - listPedido.getQuantidade());
            if (!di.update(e)) {
                message = " Erro ao salvar Pedido!";
                di.rollback();
                return null;
            }
            e = new Estoque();
            list_log.add("-------------------------------------------------------------------");
            list_log.add("Produto ID: " + listPedido.getProduto().getId());
            list_log.add("Produto Nome: " + listPedido.getProduto().getDescricao());
            list_log.add("Produto Quantidade: " + listPedido.getQuantidade());
            list_log.add("Pedido ID: " + listPedido.getId());
            list_log.add("Pedido Valor Unitário: " + listPedido.getValorUnitarioString());
            list_log.add("Pedido Desconto Unitário: " + listPedido.getDescontoUnitarioString());
        }

        Juridica empresaConvenio = (Juridica) di.find(new Juridica(), Integer.valueOf(listSelectItem[3].get(index[3]).getDescription()));
        Pessoa parceiro = null;
        if (idParceiro != null && idParceiro != -1) {
            parceiro = (Pessoa) di.find(new Pessoa(), idParceiro);
        }
        Guia guias = new Guia(
                -1,
                lote,
                empresaConvenio.getPessoa(),
                (SubGrupoConvenio) di.find(new SubGrupoConvenio(), Integer.parseInt(getListSubGrupo().get(index[1]).getDescription())),
                false,
                observacao,
                parceiro
        );

        if (!di.save(guias)) {
            message = " Erro ao salvar Guias!";
            di.rollback();
            return null;
        }
        list_log.add("-------------------------------------------------------------------");
        list_log.add("Guia ID: " + guias.getId());
        list_log.add("Guia Empresa Convênio: " + guias.getPessoa().getNome());
        list_log.add("Guia Observação: " + guias.getObservacao());

        di.commit();

        //List<Movimento> listaaux = new ArrayList();
        float valorx = 0, descontox = 0, valor_soma = 0;
        //Moeda.converteUS$(desconto);

        Movimento movimento = new Movimento();
        List<HistoricoEmissaoGuias> list_heg = new ArrayList();
        for (int i = 0; i < listaMovimento.size(); i++) {
            HistoricoEmissaoGuias heg = new HistoricoEmissaoGuias();
            movimento = (Movimento) di.find(new Movimento(), listaMovimento.get(i).getMovimento().getId());

            descontox = Moeda.multiplicarValores(listaMovimento.get(i).getMovimento().getQuantidade(), Moeda.converteUS$(listaMovimento.get(i).getDesconto()));
            valorx = Moeda.multiplicarValores(listaMovimento.get(i).getMovimento().getQuantidade(), Moeda.converteUS$(listaMovimento.get(i).getValor()));

            valor_soma = Moeda.somaValores(valor_soma, valorx);
            //valor_soma = Moeda.subtracaoValores(valor_soma, descontox);

            movimento.setMulta(listaMovimento.get(i).getMovimento().getMulta());
            movimento.setJuros(listaMovimento.get(i).getMovimento().getJuros());
            movimento.setCorrecao((listaMovimento.get(i).getMovimento()).getCorrecao());
            movimento.setDesconto(descontox);

            //movimento.setValor( Float.valueOf( listaMovimento.get(i).getArgumento9().toString() ) );
            movimento.setValor(Moeda.multiplicarValores(listaMovimento.get(i).getMovimento().getQuantidade(), listaMovimento.get(i).getMovimento().getValor()));

            movimento.setValorBaixa(Moeda.subtracaoValores(movimento.getValor(), movimento.getDesconto()));
            //movimento.setValorBaixa(valorx);

            listaMovimentoAuxiliar.add(movimento);
            heg.setMovimento(movimento);
            heg.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));

            di.openTransaction();
            if (!di.save(heg)) {
                di.rollback();
            } else {
                list_heg.add(heg);
                di.commit();
            }
        }

        String save_log = "";

        for (String string_x : list_log) {
            save_log += string_x + " \n";
        }

        NovoLog novoLog = new NovoLog();

        novoLog.save(
                save_log
        );

        GenericaSessao.put("caixa_banco", "caixa");
        if (valor_soma == 0) {
            di.openTransaction();
            guias.setEncaminhamento(true);
            di.update(guias);

            for (HistoricoEmissaoGuias heg : list_heg) {
                heg.setBaixado(true);

                if (!di.update(heg)) {
                    di.rollback();
                    message = "Erro atualizar Histórico de Impressão!";
                    return null;
                }
            }

            di.commit();

            List<FormaPagamento> lf = new ArrayList();

            lf.add(
                    new FormaPagamento(
                            -1, null, null, null, 100, 0, filial, (Plano5) new Dao().find(new Plano5(), 1), null, null, (TipoPagamento) new Dao().find(new TipoPagamento(), 3), 0, DataHoje.dataHoje(), 0, null, 0, null, null, null
                    )
            );

            Caixa caixa;//MacFilial.getAcessoFilial().getCaixa();
            MacFilial mf = MacFilial.getAcessoFilial();
            if (mf == null) {
                message = "Mac Filial não configurado!";
                return null;
            }

            if (!mf.isCaixaOperador()) {
                caixa = mf.getCaixa();

                if (caixa == null) {
                    message = "Caixa não configurado!";
                    return null;
                }
            } else {
                FinanceiroDao dao = new FinanceiroDao();
                caixa = dao.pesquisaCaixaUsuario(Usuario.getUsuario().getId(), mf.getFilial().getId());

                if (caixa == null) {
                    message = "Caixa POR OPERADOR não configurado!";
                    return null;
                }
            }

            StatusRetorno sr = GerarMovimento.baixarMovimentoManual(listaMovimentoAuxiliar, new SegurancaUtilitariosBean().getSessaoUsuario(), lf, valor_soma, DataHoje.data(), caixa, 0);
            
            if (!sr.getStatus()) {
                message = "Erro ao baixar Guias! " + sr.getMensagem();
                return null;
            }
        } else if (!listaMovimentoAuxiliar.isEmpty()) {

            if (GenericaSessao.exists("sessaoSisAutorizacao")) {
                SisAutorizacoes sa = (SisAutorizacoes) GenericaSessao.getObject("sessaoSisAutorizacao", true);
                sa.setDtConcluido(new Date());
                new Dao().update(sa, true);
            }

            GenericaSessao.put("listaMovimento", listaMovimentoAuxiliar);
            GenericaSessao.put("mensagem_recibo", observacao);
            GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
        }
        message = " Lançamento efetuado com Sucesso!";
        return null;
    }

    public String getValor() {

        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            fisica = new Fisica();
            FisicaDao db = new FisicaDao();
            fisica = db.pesquisaFisicaPorPessoa(pessoa.getId());

            SociosDao dbs = new SociosDao();
            socios = dbs.pesquisaSocioPorPessoaAtivo(pessoa.getId());
            listenerEnabledItensPedido();
            if (new FunctionsDao().inadimplente(pessoa.getId())) {
                GenericaMensagem.error("Atenção", "Esta pessoa possui débitos com o Sindicato, não poderá ser responsável!");
                return null;
            }
        }
        boolean isFisica = false;
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            pessoa = new Pessoa();
            pessoa = fisica.getPessoa();
            isFisica = true;

            SociosDao dbs = new SociosDao();
            socios = dbs.pesquisaSocioPorPessoaAtivo(pessoa.getId());
            listenerEnabledItensPedido();
            listSelectItem[2].clear();
            getListServicos();
            listenerEnabledItensPedido();
            if (new FunctionsDao().inadimplente(pessoa.getId())) {
                GenericaMensagem.error("Atenção", "Esta pessoa possui débitos com o Sindicato, não poderá ser responsável!");
                return null;
            }
        }
        if (!isFisica) {
            if (GenericaSessao.exists("juridicaPesquisa")) {
                juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
                pessoa = new Pessoa();
                pessoa = juridica.getPessoa();
                socios = new Socios();
                listenerEnabledItensPedido();
                if (new FunctionsDao().inadimplente(pessoa.getId())) {
                    GenericaMensagem.error("Atenção", "Esta pessoa possui débitos com o Sindicato, não poderá ser responsável!");
                    return null;
                }
            }
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SelectItem> getListGrupo() {
        if (listSelectItem[0].isEmpty()) {
            Dao di = new Dao();
            List<GrupoConvenio> list = (List<GrupoConvenio>) di.list(new GrupoConvenio());
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[0].add(new SelectItem(i, (String) list.get(i).getDescricao(), Integer.toString(list.get(i).getId())
                ));
            }
            if (listSelectItem[0].isEmpty()) {
                listSelectItem[0] = new ArrayList();
            }
        }
        return listSelectItem[0];
    }

    public List<SelectItem> getListSubGrupo() {
        if (listSelectItem[1].isEmpty()) {
            if (!listSelectItem[0].isEmpty()) {
                SubGrupoConvenioDao db = new SubGrupoConvenioDao();
                List<SubGrupoConvenio> list = (List<SubGrupoConvenio>) db.listaSubGrupoConvenioPorGrupo(Integer.parseInt(listSelectItem[0].get(index[0]).getDescription()));
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPrincipal()) {
                        index[1] = i;
                    }
                    listSelectItem[1].add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
                }
                if (listSelectItem[1].isEmpty()) {
                    listSelectItem[1] = new ArrayList();
                }
            }

        }
        return listSelectItem[1];
    }

    public List<SelectItem> getListServicos() {
        if (listSelectItem[2].isEmpty()) {
            ConvenioServicoDao db = new ConvenioServicoDao();

            listSelectItem[2].add(new SelectItem(0, "-- Selecione um Serviço --", "0"));
            int b = 0;
            index[2] = 0;
            SisAutorizacoes sa = ((SisAutorizacoes) GenericaSessao.getObject("sessaoSisAutorizacao"));
            if (!getListGrupo().isEmpty() && !getListSubGrupo().isEmpty()) {
                List<Servicos> list = (List<Servicos>) db.pesquisaServicosSubGrupoConvenio(Integer.parseInt(getListSubGrupo().get(index[1]).getDescription()));
                for (int i = 0; i < list.size(); i++) {
                    if (GenericaSessao.exists("sessaoSisAutorizacao")) {
                        if (list.get(i).getId() == sa.getServicos().getId()) {
                            if (b == 0) {
                                index[2] = b + 1;
                            }
                            listSelectItem[2].add(new SelectItem(b + 1, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
                            b++;
                        }
                    } else {
                        listSelectItem[2].add(new SelectItem(i + 1, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
                    }
                }
            }
        }
        return listSelectItem[2];
    }

    public List<SelectItem> getListJuridica() {
        if (listSelectItem[3].isEmpty()) {
            LancamentoIndividualDao db = new LancamentoIndividualDao();
            if (getListSubGrupo().isEmpty()) {
                return listSelectItem[3];
            }
            List<Juridica> list = (List<Juridica>) db.listaEmpresaConveniadaPorSubGrupo(Integer.parseInt(getListSubGrupo().get(index[1]).getDescription()));
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[3].add(new SelectItem(i, list.get(i).getPessoa().getNome(), Integer.toString(list.get(i).getId())));
            }
        }
        return listSelectItem[3];
    }

    public String getDesconto() {
        if (desconto.isEmpty()) {
            desconto = "0";
        }
        return Moeda.converteR$(desconto);
    }

    public void setDesconto(String desconto) {
        if (desconto.isEmpty()) {
            desconto = "0";
        }
        this.desconto = Moeda.substituiVirgula(desconto);
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public List<ListMovimentoEmissaoGuias> getListaMovimento() {
        for (ListMovimentoEmissaoGuias listaMovimento1 : listaMovimento) {
            listaMovimento1.setValor(Moeda.converteR$(listaMovimento1.getValor()));
            listaMovimento1.getMovimento().setValor(Moeda.converteUS$(Moeda.converteR$(listaMovimento1.getValor())));
        }
        return listaMovimento;
    }

    public void setListaMovimento(List<ListMovimentoEmissaoGuias> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public String getTotal() {
        if (total.isEmpty()) {
            total = "0";
        }
        return Moeda.converteR$(total);
    }

    public void setTotal(String total) {
        if (total.isEmpty()) {
            total = "0";
        }
        this.total = Moeda.substituiVirgula(total);
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    public void addItemPedido() {
        pedido.setValorUnitario(Moeda.substituiVirgulaFloat(valorUnitarioPedido));
        //pedido.setDescontoUnitario(Moeda.substituiVirgulaFloat(descontoUnitarioPedido));
        pedido.setQuantidade(quantidadePedido);
        if (pedido.getProduto().getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar um produto!");
            return;
        }
        if (pedido.getQuantidade() < 1) {
            GenericaMensagem.warn("Validação", "Adicionar quantidade!");
            return;
        }
        if (pedido.getValorUnitario() < 1) {
            GenericaMensagem.warn("Validação", "Informar valor do produto!");
            return;
        }
        for (Pedido listPedido : listPedidos) {
            if (listPedido.getProduto().getId() == pedido.getProduto().getId()) {
                GenericaMensagem.warn("Validação", "Produto já adicionado!");
                return;
            }
        }
        Dao dao = new Dao();
        if (pedido.getId() == -1) {
            pedido.setEstoqueTipo((EstoqueTipo) dao.find(new EstoqueTipo(), 3));
            listPedidos.add(pedido);
        } else {
            dao.openTransaction();
            dao.update(pedido);
            dao.commit();
            listPedidos.add(pedido);
        }
        pedido = new Pedido();
        estoque = new Estoque();
        valorUnitarioPedido = "";
        descontoUnitarioPedido = "";
        quantidadePedido = 1;
    }

    public void editarItemPedido(int index) {
        Dao dao = new Dao();
        for (int i = 0; i < listPedidos.size(); i++) {
            if (i == index) {
                if (listPedidos.get(index).getId() == -1) {
                    pedido = listPedidos.get(index);
                    listPedidos.remove(index);
                } else {
                    pedido = (Pedido) dao.rebind(listPedidos.get(index));
                    listPedidos.remove(index);
                }
                valorUnitarioPedido = pedido.getValorUnitarioString();
                descontoUnitarioPedido = pedido.getDescontoUnitarioString();
                quantidadePedido = pedido.getQuantidade();
                break;
            }
        }
        modalPedido = true;
    }

    public void removeItemPedido(int index) {
        boolean erro = false;
        Dao dao = new Dao();
        dao.openTransaction();
        for (int i = 0; i < listPedidos.size(); i++) {
            if (i == index) {
                if (listPedidos.get(i).getId() != -1) {
                    if (!dao.delete(dao.find(listPedidos.get(i)))) {
                        dao.rollback();
                        erro = true;
                        break;
                    }
                }
                listPedidos.remove(i);
                break;
            }
        }
        if (erro) {
            dao.rollback();
        } else {
            dao.commit();
        }
    }

    public void novoPedido() {
        pedido = new Pedido();
        listPedidos = new ArrayList();
        descontoUnitarioPedido = "0,00";
        valorUnitarioPedido = "0,00";
        quantidadePedido = 1;

    }

    public void openModalPedido() {
        modalPedido = true;
        pedido = new Pedido();
        descontoUnitarioPedido = "0,00";
        valorUnitarioPedido = "0,00";
        quantidadePedido = 1;
    }

    public void closeModalPedido() {
        modalPedido = false;

        listenerEnabledItensPedido();
    }

    public String valorTotalPedido(Pedido p) {
        if (p != null) {
            float value = Moeda.subtracaoValores(p.getValorUnitario(), p.getDescontoUnitario());
            value = Moeda.multiplicarValores(value, p.getQuantidade());
            return Moeda.converteR$Float(value);
        } else {
            return Moeda.converteR$Float(0);
        }

    }

    public String getValorTotal() {
        float v = 0;
        for (Pedido p : listPedidos) {
            v = Moeda.somaValores(v, Moeda.converteUS$(valorTotalPedido(p)));
        }
        valorTotal = Moeda.converteR$Float(v);
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Pedido> getListPedidos() {
        return listPedidos;
    }

    public void setListPedidos(List<Pedido> listPedidos) {
        this.listPedidos = listPedidos;
    }

    public String getDescontoUnitarioPedido() {
        return descontoUnitarioPedido;
    }

    public void setDescontoUnitarioPedido(String descontoUnitarioPedido) {
        this.descontoUnitarioPedido = descontoUnitarioPedido;
    }

    public String getValorUnitarioPedido() {
        return valorUnitarioPedido;
    }

    public void setValorUnitarioPedido(String valorUnitarioPedido) {
        this.valorUnitarioPedido = valorUnitarioPedido;
    }

    public boolean isModalPedido() {
        return modalPedido;
    }

    public void setModalPedido(boolean modalPedido) {
        this.modalPedido = modalPedido;
    }

    public int getQuantidadePedido() {
        return quantidadePedido;
    }

    public void setQuantidadePedido(int quantidadePedido) {
        this.quantidadePedido = quantidadePedido;
    }

    public void validaProdutoPesquisa() {
        if (GenericaSessao.exists("produtoPesquisa")) {
            Produto p = (Produto) GenericaSessao.getObject("produtoPesquisa", true);
            for (Pedido listPedido : listPedidos) {
                if (listPedido.getProduto().getId() == p.getId()) {
                    GenericaMensagem.warn("Validação", "Produto já adicionado!");
                    PF.update(":form_eg:i_message_pedido");
                    pedido = new Pedido();
                }
            }
            ProdutoDao produtoDao = new ProdutoDao();
            estoque = new Estoque();
            estoque = produtoDao.listaEstoquePorProdutoFilial(p, filial);
            if (estoque == null) {
                GenericaMensagem.warn("Validação", "Produto indisponível para esta filial!");
                PF.update(":form_eg:i_message_pedido");
            } else if (estoque.getEstoqueTipo().getId() == 3) {
                pedido.setProduto(estoque.getProduto());
                if (estoque.getEstoque() == 0) {
                    GenericaMensagem.warn("Validação", "Quantidade indisponível!");
                }
                valorUnitarioPedido = pedido.getProduto().getValorString();
            } else {
                GenericaMensagem.warn("Validação", "Tipo de produto estoque " + estoque.getEstoqueTipo().getDescricao() + " não é permitido!");
            }
        }
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

//    public boolean isEnabledItensPedido() {
//        return enabledItensPedido;
//    }
//
//    public void setEnabledItensPedido(boolean enabledItensPedido) {
//        this.enabledItensPedido = enabledItensPedido;
//    }
    public void listenerEnabledItensPedidoListener() {
        loadListParceiro();
        listenerEnabledItensPedido();

    }

    public void listenerEnabledItensPedido() {
        if (!getListServicos().isEmpty() && !listSelectItem[2].get(index[2]).getDescription().equals("0")) {
            Dao di = new Dao();
            servicox = (Servicos) di.find(new Servicos(), Integer.parseInt(getListServicos().get(index[2]).getDescription()));

            if (pessoa.getId() != -1) {
                //if (!enabledItensPedido) {
                if (!servicox.isProduto()) {
                    LancamentoIndividualDao db = new LancamentoIndividualDao();
                    List<Vector> valorx = db.pesquisaServicoValor(pessoa.getId(), Integer.parseInt(getListServicos().get(index[2]).getDescription()));
                    if (!valorx.isEmpty()) {
                        float vl = Float.valueOf(((Double) valorx.get(0).get(0)).toString());
                        valor = Moeda.converteR$Float(vl);
                        if (idParceiro != null && idParceiro != -1) {
                            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
                            DescontoServicoEmpresa dse = dsed.findByGrupo(2, Integer.parseInt(getListServicos().get(index[2]).getDescription()), idParceiro);
                            if (dse != null) {
                                valor = Moeda.converteR$Float(Moeda.converteUS$(Moeda.valorDoPercentual(valor, dse.getDescontoString())));
                            }
                        }
                    } else {
                        valor = "0,00";
                        GenericaMensagem.fatal("Atenção", "Valor do Serviço não encontrado");
                    }
                } else {
                    float v = 0;
                    if (!listPedidos.isEmpty()) {
                        for (Pedido p : listPedidos) {
                            v = Moeda.somaValores(v, Moeda.converteUS$(valorTotalPedido(p)));
                        }
                    }
                    valor = "" + v;
                }
            } else {
                valor = "0,00";
                GenericaMensagem.fatal("Atenção", "Pesquise uma pessoa para que o valor seja calculado!");
            }
//            if (servicos.isProduto() && servicos.isAlterarValor()){
//                enabledItensPedido = true;
//            }else
//                enabledItensPedido = false;
        } else {
            valor = "0,00";
            servicox = new Servicos();
        }
    }

    /* FIM PRODUTOS --------------------------------------------------------- */
    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public String[] getVar() {
        return var;
    }

    public void setVar(String[] var) {
        this.var = var;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public List<Movimento> getListaMovimentosEmitidos() {
        return listaMovimentosEmitidos;
    }

    public void setListaMovimentosEmitidos(List<Movimento> listaMovimentosEmitidos) {
        this.listaMovimentosEmitidos = listaMovimentosEmitidos;
    }

    public Servicos getServicox() {
        return servicox;
    }

    public void setServicox(Servicos servicox) {
        this.servicox = servicox;
    }

    public Fisica getFisicaNovoCadastro() {
        return fisicaNovoCadastro;
    }

    public void setFisicaNovoCadastro(Fisica fisicaNovoCadastro) {
        this.fisicaNovoCadastro = fisicaNovoCadastro;
    }

    public ControleAcessoBean getCab() {
        return cab;
    }

    public void setCab(ControleAcessoBean cab) {
        this.cab = cab;
    }

    public String getNovoDesconto() {
        return novoDesconto;
    }

    public void setNovoDesconto(String novoDesconto) {
        this.novoDesconto = novoDesconto;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    private void loadListParceiro() {
        if (socios.getId() == -1 && pessoa.getId() != -1) {
            Integer servido_id = Integer.parseInt(getListServicos().get(index[2]).getDescription());
            listParceiro = new ArrayList();
            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
            List<DescontoServicoEmpresa> list = dsed.findByGrupo(2, servido_id);
            listParceiro.add(new SelectItem(-1, "NENHUM"));
            idParceiro = -1;
            Integer pessoa_id = null;
            for (int i = 0; i < list.size(); i++) {
                if (pessoa_id == null || pessoa_id != list.get(i).getJuridica().getPessoa().getId()) {
                    listParceiro.add(new SelectItem(list.get(i).getJuridica().getPessoa().getId(), list.get(i).getJuridica().getPessoa().getNome()));
                    pessoa_id = list.get(i).getJuridica().getPessoa().getId();
                }
            }
        }
    }

    public List<SelectItem> getListParceiro() {
        return listParceiro;
    }

    public void setListParceiro(List<SelectItem> listParceiro) {
        this.listParceiro = listParceiro;
    }

    public Integer getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(Integer idParceiro) {
        this.idParceiro = idParceiro;
    }

}
