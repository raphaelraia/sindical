package br.com.rtools.financeiro.beans;

import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.estoque.EstoqueTipo;
import br.com.rtools.estoque.Pedido;
import br.com.rtools.estoque.Produto;
import br.com.rtools.financeiro.*;
import br.com.rtools.financeiro.dao.CentroCustoDao;
import br.com.rtools.financeiro.dao.ContaOperacaoDao;
import br.com.rtools.financeiro.dao.EstornoCaixaDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.financeiro.dao.PedidoDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.financeiro.dao.LancamentoFinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.homologacao.dao.OperacaoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirRecibo;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.Porte;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class LancamentoFinanceiroBean implements Serializable {

    private List<Lote> listaLancamento;
    private List<Parcela> listaParcela;
    private List<Parcela> listaParcelaSelecionada;
    private Integer idFilial;
    private Integer idTipoDocumento;
    private Integer idFTipo;
    private Integer idFTipoMovimento;
    private Integer idOperacao;
    private Integer idContaOperacao;
    private Integer idCentroCusto;
    private Integer idPlano5;
    private List<SelectItem> listaFilial;
    private List<SelectItem> listaTipoDocumento;
    private List<SelectItem> listaFTipo;
    private List<SelectItem> listaFTipoMovimento;
    private List<SelectItem> listaOperacao;
    private List<SelectItem> listaContaOperacao;
    private List<SelectItem> listaCentroCusto;
    private List<SelectItem> listaPlano5;
    private String descricao;
    private String mascara;
    private String es;
    private String esLancamento;
    private String condicao;
    private String total;
    private String valor;
    private String opcaoCadastro;
    private String vencimento;
    private String strConta;
    private String documentoMovimento;
    private String porPesquisa;
    private String maskSearch;
    private String description;
    private String acrescimo;
    private String multa;
    private String juros;
    private String correcao;
    private String desconto;
    private String strVisualizando;
    private String historico;
    private boolean modalVisivel;
    private boolean chkImposto;
    private boolean disabledConta;
    private boolean telaSalva;
    private Pessoa pessoa;
    private Operacao operacao;
    private Lote lote;
    private Plano5 plano;
    private List<Usuario> listaUsuarioLancamento;
    private Usuario usuarioSelecionado;
    private Parcela parcela;
    private int indexParcela;
    private int indexAcrescimo;
    private String motivoEstorno;

    /* PRODUTOS ------------------------------------------------------------- */
    private boolean modalPedido;
    private Pedido pedido;
    private int quantidadePedido;
    private String valorUnitarioPedido;
    private String descontoUnitarioPedido;
    private List<Pedido> listaPedidos;
    private String valorTotal;
    private Boolean produtos;
    private Boolean liberaAcessaFilial;
    private List<Fisica> listFisicaSugestao;
    private List<Socios> listaSocios;
    private List<Juridica> listJuridicaSugestao;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {

        listaLancamento = new ArrayList();
        listaParcela = new ArrayList();
        listaParcelaSelecionada = new ArrayList();
        idFilial = 0;
        idTipoDocumento = 0;
        idFTipo = 0;
        idFTipoMovimento = 0;
        idOperacao = 0;
        idContaOperacao = 0;
        idCentroCusto = 0;
        idPlano5 = 0;
        listaFilial = new ArrayList();
        listaTipoDocumento = new ArrayList();
        listaFTipo = new ArrayList();
        listaFTipoMovimento = new ArrayList();
        listaOperacao = new ArrayList();
        listaContaOperacao = new ArrayList();
        listaCentroCusto = new ArrayList();
        listaPlano5 = new ArrayList();
        descricao = "";
        mascara = "";
        es = "S";
        esLancamento = "S";
        condicao = "vista";
        liberaAcessaFilial = false;
        total = "";
        valor = "";
        opcaoCadastro = "";
        vencimento = DataHoje.data();
        strConta = "";
        historico = "";
        documentoMovimento = "";
        porPesquisa = "dias";
        maskSearch = "todos";
        description = "";
        acrescimo = "0";
        multa = "0";
        juros = "0";
        correcao = "0";
        desconto = "0";
        strVisualizando = "os Meus Lançamentos";
        modalVisivel = false;
        chkImposto = false;
        disabledConta = false;
        telaSalva = true;
        pessoa = new Pessoa();
        operacao = new Operacao();
        lote = new Lote();
        plano = new Plano5();
        listaUsuarioLancamento = new ArrayList<>();
        usuarioSelecionado = new Usuario();
        parcela = new Parcela();
        indexParcela = 0;
        indexAcrescimo = -1;
        motivoEstorno = "";
        usuarioSelecionado = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        valorTotal = "0";
        listaPedidos = new ArrayList();
        descontoUnitarioPedido = "";
        quantidadePedido = 1;
        pedido = new Pedido();
        modalPedido = false;
        produtos = true;
        loadLiberaAcessaFilial();
        lote.setEmissao(DataHoje.data());
        loadListaFilial();
        loadListaTipoDocumento();
        loadListaFTipo();
        loadListaFTipoMovimento();
        loadListaOperacao();
        loadListaCentroCusto();
        loadListaContaOperacao();
        loadListaLancamento();
        listFisicaSugestao = new ArrayList();
        listJuridicaSugestao = new ArrayList();
        listaSocios = new ArrayList();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("lancamentoFinanceiroBean");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
    }
    
    public String targetImprimeRecibo(Movimento movimento) {
        if (validaImprimeRecibo(movimento)) {
            return "_blank";
        }
        return "";
    }
     
    public Boolean validaImprimeRecibo(Movimento mov) {
        if (Usuario.getUsuario().getId() != 1) {
            if (mov.getBaixa() != null && !mov.getBaixa().getImportacao().isEmpty()) {
                GenericaMensagem.fatal("ATENÇÃO", "RECIBO COM DATA DE IMPORTAÇÃO NÃO PODE SER REIMPRESSO!");
                return false;
            }
            
//            if (mov.getBaixa().getUsuario().getId() != Usuario.getUsuario().getId() && cab.verificaPermissao("reimpressao_recibo_outro_operador", 4)) {
//                GenericaMensagem.fatal("ATENÇÃO", "USUÁRIO SEM PERMISSÃO PARA REIMPRIMIR ESTE RECIBO! (BAIXADO POR: " + mov.getBaixa().getUsuario().getPessoa().getNome() + ")");
//                return false;
//            }
        }
        return true;
    }
    
    public String recibo(Movimento mov) {
        ImprimirRecibo ir = new ImprimirRecibo();

        ir.reciboGenerico(mov.getId(), null);
        
        return null;
    }

    public void addItemPedido() {
        pedido.setValorUnitario(Moeda.substituiVirgulaFloat(valorUnitarioPedido));
        pedido.setDescontoUnitario(Moeda.substituiVirgulaFloat(descontoUnitarioPedido));
        pedido.setQuantidade(quantidadePedido);
        if (pedido.getProduto().getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar um produto!");
            return;
        }
        if (pedido.getQuantidade() < 1) {
            GenericaMensagem.warn("Validação", "Adicionar quantidade!");
            return;
        }
        if (pedido.getValorUnitario() < 0) {
            GenericaMensagem.warn("Validação", "Informar valor do produto!");
            return;
        }
        Dao dao = new Dao();
        if (pedido.getId() == -1) {
            pedido.setEstoqueTipo((EstoqueTipo) dao.find(new EstoqueTipo(), 1));
            listaPedidos.add(pedido);
        } else {
            dao.openTransaction();
            dao.update(pedido);
            dao.commit();
            listaPedidos.add(pedido);
        }
        pedido = new Pedido();
        valorUnitarioPedido = "";
        descontoUnitarioPedido = "";
        quantidadePedido = 1;
    }

    public void editarItemPedido(int index) {
        Dao dao = new Dao();
        for (int i = 0; i < listaPedidos.size(); i++) {
            if (i == index) {
                if (listaPedidos.get(index).getId() == -1) {
                    pedido = listaPedidos.get(index);
                    listaPedidos.remove(index);
                } else {
                    pedido = (Pedido) dao.rebind(listaPedidos.get(index));
                    listaPedidos.remove(index);
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
        for (int i = 0; i < listaPedidos.size(); i++) {
            if (i == index) {
                if (listaPedidos.get(i).getId() != -1) {
                    if (!dao.delete(dao.find(listaPedidos.get(i)))) {
                        dao.rollback();
                        erro = true;
                        break;
                    }
                }
                listaPedidos.remove(i);
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
        listaPedidos = new ArrayList<>();
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
    }

    public String valorTotalGrid(Pedido linha) {
        if (linha != null) {
            float value = Moeda.subtracaoValores(linha.getValorUnitario(), linha.getDescontoUnitario());
            value = Moeda.multiplicarValores(value, linha.getQuantidade());
            return Moeda.converteR$Float(value);
        } else {
            return Moeda.converteR$Float(0);
        }

    }

    public String getValorTotal() {
        float valorx = 0;
        for (Pedido pedidox : listaPedidos) {
            valorx = Moeda.somaValores(valorx, Moeda.converteUS$(valorTotalGrid(pedidox)));
        }
        valorTotal = Moeda.converteR$Float(valorx);
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Pedido> getListaPedidos() {
        return listaPedidos;
    }

    public void setListaPedidos(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
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

    public String getQuantidadePedidoString() {
        return Integer.toString(quantidadePedido);
    }

    public void setQuantidadePedidoString(String quantidadePedidoString) {
        this.quantidadePedido = Integer.parseInt(quantidadePedidoString);
    }

    public Pedido getPedido() {
        if (GenericaSessao.exists("produtoPesquisa")) {
            pedido.setProduto((Produto) GenericaSessao.getObject("produtoPesquisa", true));
        }
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    /* FIM PRODUTOS --------------------------------------------------------- */
    public void delete() {
        // PARCELAS BAIXADAS
        for (Parcela p : listaParcela) {
            if (p.getMovimento().getBaixa() != null) {
                GenericaMensagem.warn("Erro", "Existem parcelas baixadas, ESTORNE elas antes de excluir este lançamento!");
                return;
            }
        }
        Dao dao = new Dao();

        Map<Integer, EstornoCaixa> hashEc = new LinkedHashMap<>();
        Map<Integer, EstornoCaixaLote> hashEcl = new LinkedHashMap<>();

        dao.openTransaction();

        EstornoCaixaDao estornoCaixaDao = new EstornoCaixaDao();

        for (Parcela p : listaParcela) {
            List<EstornoCaixa> listEc = estornoCaixaDao.findAllByMovimento(p.getMovimento().getId());
            for (int i = 0; i < listEc.size(); i++) {
                hashEc.put(listEc.get(i).getId(), listEc.get(i));
                hashEcl.put(listEc.get(i).getEstornoCaixaLote().getId(), listEc.get(i).getEstornoCaixaLote());
            }
        }

        if (!hashEc.isEmpty()) {
            for (Map.Entry<Integer, EstornoCaixa> entry : hashEc.entrySet()) {
                if (!dao.delete(entry.getValue())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Estorno caixa não pode ser excluído!");
                    return;
                }
            }

            for (Map.Entry<Integer, EstornoCaixaLote> entry : hashEcl.entrySet()) {
                if (!dao.delete(entry.getValue())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Estorno caixa lote não pode ser excluído!");
                    return;
                }
            }
        }

        for (Parcela p : listaParcela) {
            if (!dao.delete(p.getMovimento())) {
                GenericaMensagem.warn("Erro", "Movimento não pode ser Excluído!");
                dao.rollback();
                return;
            }
        }

        for (Pedido pedidox : listaPedidos) {
            if (!dao.delete(dao.find(pedidox))) {
                GenericaMensagem.warn("Erro", "PEDIDO não pode ser Excluído!");
                dao.rollback();
                return;
            }
        }

        if (!dao.delete(dao.find(lote))) {
            GenericaMensagem.warn("Erro", "Lote não pode ser Excluído!");
            dao.rollback();
            return;
        }

        GenericaMensagem.info("Sucesso", "Lançamento excluído com Sucesso!");

        limpar();
        dao.commit();
    }

    public void reverse() {
        if (listaParcelaSelecionada.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Selecione ao menos uma parcela PAGA ser estornada!");
            return;
        }

        // PARCELAS PARA SEREM ESTORNADAS
        Movimento movimento = new Movimento();

        if (motivoEstorno.isEmpty() || motivoEstorno.length() <= 5) {
            GenericaMensagem.error("Atenção", "Motivo de Estorno INVÁLIDO!");
            return;
        }

        boolean reverse = false;
        
        for (Parcela p : listaParcelaSelecionada) {
            if (p.getMovimento().getBaixa() == null) {
                continue;
            }

            movimento = p.getMovimento();
            
            if (!new LancamentoFinanceiroDao().estornarTipoConta(movimento.getBaixa().getId())) {
                GenericaMensagem.warn("Atenção", "ESTORNAR PELA ROTINA DE CONTAS A PAGAR!");
                return;
            }
            
            if (GerarMovimento.estornarMovimento(movimento, motivoEstorno)) {
                reverse = true;
            }
        }

        if (reverse) {
            listaParcela.clear();
            listaParcelaSelecionada.clear();
            GenericaMensagem.info("Sucesso", "Estorno concluído!");
        } else {
            GenericaMensagem.warn("Erro", "NENHUMA parcela para ser estornada!");

        }
    }

    public String telaBaixa() {
        if (listaParcelaSelecionada.isEmpty()) {
            GenericaMensagem.warn("Erro", "Selecione ao menos UMA parcela para ser baixada!");
            return null;
        }

        MacFilial macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");
        if (macFilial == null) {
            GenericaMensagem.warn("Erro", "Não existe FILIAL NA SESSÃO!");
            return null;
        }

        if (!macFilial.isCaixaOperador()) {
            if (macFilial.getCaixa() == null) {
                GenericaMensagem.warn("Erro", "Configurar CAIXA nesta estação de trabalho!");
                return null;
            }
        } else {
            FinanceiroDao dbf = new FinanceiroDao();
            Caixa caixax = dbf.pesquisaCaixaUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId(), macFilial.getFilial().getId());

            if (caixax == null) {
                GenericaMensagem.warn("Erro", "Configurar CAIXA para este operador!");
                return null;
            }
        }

        Movimento movimento;

        List<Movimento> lista = new ArrayList();

        // PARCELAS BAIXADAS
        for (Parcela p : listaParcelaSelecionada) {
            movimento = (Movimento) p.getMovimento();
            if (movimento.getBaixa() != null) {
                GenericaMensagem.warn("Erro", "Parcelas que JÁ FORAM BAIXADAS não podem ser selecionadas!");
                return null;
            }
        }

        // PARCELAS COM VALOR ZERADO
        for (Parcela p : listaParcelaSelecionada) {
            movimento = (Movimento) p.getMovimento();
            if (movimento.getValor() <= 0) {
                GenericaMensagem.warn("Erro", "Adicione um VALOR as parcelas adicionadas!");
                return null;
            }
        }

        for (Parcela p : listaParcelaSelecionada) {
            movimento = (Movimento) p.getMovimento();

            //movimento = db.pesquisaCodigo(Integer.parseInt(String.valueOf(listaMovimento.get(i).getArgumento1())));
            movimento.setMulta(Moeda.converteUS$(p.getMulta()));
            movimento.setJuros(Moeda.converteUS$(p.getJuros()));
            movimento.setCorrecao(Moeda.converteUS$(p.getCorrecao()));
            movimento.setDesconto(Moeda.converteUS$(p.getDesconto()));

            float valor_baixa = Moeda.somaValores(movimento.getValor(), Moeda.converteUS$(p.getAcrescimo()));
            valor_baixa = Moeda.subtracaoValores(valor_baixa, movimento.getDesconto());
            //movimento.setValor(Moeda.converteUS$(listaMovimento.get(i).getArgumento6().toString()));

            movimento.setValorBaixa(valor_baixa);

            lista.add(movimento);
        }

        if (!lista.isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("esMovimento", lista.get(0).getEs());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("caixa_banco", "caixa");
            GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 2));
            return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
        }
        return null;
    }

    public void edit(Lote l) {
        lote = new Lote();
        lote = l;
        pessoa = lote.getPessoa();
        descricao = pessoa.getDocumento();
        modalVisivel = true;
        total = Moeda.converteR$Float(lote.getValor());

        // FILIAL --
        idFilial = lote.getFilial().getId();

        if (lote.getPagRec().equals("R")) {
            es = "E";
        } else {
            es = "S";
        }
        if (lote.getCondicaoPagamento().getId() == 1) {
            condicao = "vista";
        } else {
            condicao = "prazo";
        }

        // FTIPO DOCUMENTO
        idFTipo = lote.getFtipoDocumento().getId();

        // OPERACAO
        loadListaOperacao();
        idOperacao = lote.getOperacao().getId();

        // CENTRO CUSTO
        loadListaCentroCusto();
        if (!listaCentroCusto.isEmpty() && lote.getCentroCusto() != null) {
            idCentroCusto = lote.getCentroCusto().getId();
        }

        historico = lote.getHistoricoContabil();

        // OPERACAO
        loadListaContaOperacao();
        if (lote.getPlano5() != null) {
            for (int i = 0; i < listaContaOperacao.size(); i++) {
                if (Integer.parseInt(listaContaOperacao.get(i).getDescription()) == lote.getPlano5().getId()) {
                    idContaOperacao = Integer.parseInt(listaContaOperacao.get(i).getValue().toString());
                    break;
                }
            }
        }
        // TIPO DOCUMENTO -- CNPJ -- CPF -- SEM DOCUMENTO
        idTipoDocumento = pessoa.getTipoDocumento().getId();
        listaParcela = new ArrayList();
        listaParcelaSelecionada = new ArrayList();
        MovimentoDao movimentoDao = new MovimentoDao();
        List<Movimento> selectMovimento = movimentoDao.listaMovimentosDoLote(lote.getId());

        if (!selectMovimento.isEmpty()) {
            esLancamento = selectMovimento.get(0).getEs();
        }
        float acre, valor_quitado = 0;
        for (Movimento mov : selectMovimento) {
            String data_quitacao = "";
            String caixa = "NÃO BAIXADO";
            String loteBaixa = "NÃO BAIXADO";
            acre = Moeda.somaValores(Moeda.somaValores(mov.getMulta(), mov.getJuros()), mov.getCorrecao());

            if (mov.getBaixa() != null) {
                valor_quitado = mov.getValorBaixa();
                data_quitacao = mov.getBaixa().getBaixa();
                caixa = mov.getBaixa().getCaixa() != null ? mov.getBaixa().getCaixa().getCaixa() + "" : "NÃO BAIXADO";
                loteBaixa = mov.getBaixa() != null ? mov.getBaixa().getId() + "" : "NÃO BAIXADO";
            }

            listaParcela.add(new Parcela(
                    listaParcela.size(),
                    mov,
                    DataHoje.converteData(mov.getDtVencimento()),
                    mov.getReferencia(),
                    Moeda.converteR$Float(mov.getValor()),
                    Moeda.converteR$Float(acre),
                    Moeda.converteR$Float(mov.getDesconto()),
                    Moeda.converteR$Float(valor_quitado),
                    data_quitacao,
                    lote.getUsuario().getPessoa().getNome().length() >= 30 ? lote.getUsuario().getPessoa().getNome().substring(0, 30) + "..." : lote.getUsuario().getPessoa().getNome(),
                    caixa,
                    loteBaixa,
                    false
            ));
        }

        List<Plano5> select = new Plano5Dao().find(lote.getPlano5().getId(), 2);

        if (!select.isEmpty()) {
            plano = select.get(0);
            listaPedidos = new PedidoDao().findByLote(lote.getId());
        } else {
            plano = new Plano5();
            listaPedidos.clear();
        }
        produtos = false;
        if (!listaPedidos.isEmpty()) {
            produtos = true;

        }

    }

    public void save() {
        Dao dao = new Dao();
        try {
            if (listaParcela.isEmpty()) {
                GenericaMensagem.warn("Erro", "ADICIONE UMA PARCELA para salvar este lançamento!");
                return;
            }
            float soma = 0;
            for (Parcela p : listaParcela) {

                soma = Moeda.somaValores(soma, p.getMovimento().getValor());
            }

            if (soma < Moeda.converteUS$(total)) {
                GenericaMensagem.warn("Erro", "Valor das Parcelas é MENOR que soma Total!");
                return;
            } else if (soma > Moeda.converteUS$(total)) {
                GenericaMensagem.warn("Erro", "Valor das Parcelas é MAIOR que soma Total!");
                return;
            }
            dao.openTransaction();
            ContaOperacao co = (ContaOperacao) dao.find(new ContaOperacao(), idContaOperacao);
            FTipoDocumento td = (FTipoDocumento) dao.find(new FTipoDocumento(), idFTipo);
            Filial filial = (Filial) dao.find(new Filial(), idFilial);
            CondicaoPagamento cp;
            if (condicao.equals("vista")) {
                cp = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1);
            } else {
                cp = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 2);
            }
            CentroCusto cc = null;
            if (!listaCentroCusto.isEmpty()) {
                cc = (CentroCusto) dao.find(new CentroCusto(), idCentroCusto);
            }
            Usuario us = (Usuario) dao.find((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            Operacao o = (Operacao) new Dao().find(new Operacao(), idOperacao);

            lote.setValor(Moeda.converteUS$(total));
            lote.setContaFixa(co.isContaFixa());
            lote.setPlano5(co.getPlano5());
            lote.setFTipoDocumento(td);
            lote.setFilial(filial);
            lote.setCondicaoPagamento(cp);
            lote.setDepartamento(null);
            lote.setStatus((FStatus) dao.find(new FStatus(), 1));
            lote.setRotina((Rotina) dao.find(new Rotina(), 231));
            lote.setEvt(null);
            lote.setPessoa(pessoa);
            lote.setHistoricoContabil(historico);
            lote.setPessoaSemCadastro(null);
            lote.setUsuario(us);
            lote.setOperacao(operacao);
            lote.setCentroCusto(cc);
            lote.setOperacao(o);
            if (es.equals("E")) {
                lote.setPagRec("R");
            } else {
                lote.setPagRec("P");
            }
            if (lote.getId() == -1) {
                if (!dao.save(lote)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar Lote!");
                    dao.rollback();
                    return;
                }
            } else if (!dao.update(lote)) {
                GenericaMensagem.warn("Erro", "Erro ao Atualizar Lote!");
                dao.rollback();
                return;
            }
            Boolean bloqueiaTipoCondicao = true;
            int i = 0;
            for (Parcela p : listaParcela) {
                Movimento movimento = (Movimento) p.getMovimento();
                movimento.setLote(lote);
                if (condicao.equals("prazo") && bloqueiaTipoCondicao) {
                    if (listaParcela.size() == 1) {
                        if (DataHoje.maiorData(movimento.getVencimento(), lote.getEmissao())) {
                            bloqueiaTipoCondicao = false;
                        }
                    } else if (i > 0) {
                        if (DataHoje.maiorData(movimento.getVencimento(), lote.getEmissao())) {
                            bloqueiaTipoCondicao = false;
                        }
                    }
                }
                i++;
                if (movimento.getId() == -1) {
                    if (!dao.save(movimento)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Lançamento!");
                        dao.rollback();
                        return;
                    }
                } else {
                    movimento.setPessoa(pessoa);
                    movimento.setTitular(pessoa);
                    movimento.setBeneficiario(pessoa);
                    movimento.setMulta(Moeda.converteUS$(p.getMulta()));
                    movimento.setJuros(Moeda.converteUS$(p.getJuros()));
                    movimento.setCorrecao(Moeda.converteUS$(p.getCorrecao()));
                    movimento.setDesconto(Moeda.converteUS$(p.getDesconto()));
                    if (p.getReferencia().isEmpty()) {
                        p.setReferencia(DataHoje.converteDataParaReferencia(movimento.getVencimento()));
                    }
                    movimento.setReferencia(p.getReferencia());
                    List<Plano5> select = new Plano5Dao().find(movimento.getPlano5().getId(), 1);
                    if (select.isEmpty()) {
                        movimento.setPlano5(lote.getPlano5());
                    }

                    if (!dao.update(movimento)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Lançamento!");
                        dao.rollback();
                        return;
                    }
                }
            }

            if (condicao.equals("prazo")) {
                if (bloqueiaTipoCondicao) {
                    if (lote.getId() == -1) {
                        for (Parcela p : listaParcela) {
                            Movimento m = (Movimento) dao.find(p.getMovimento());
                            if (m == null) {
                                m = p.getMovimento();
                                m.setId(-1);
                                p.getMovimento();
                            }
                        }
                    }
                    GenericaMensagem.warn("Validação", "Na condição de patgo a PRAZO é necessário ter parcelas com datas superiores nas parcelas!");
                    dao.rollback();
                    return;
                }
            }

            for (Pedido pedidox : listaPedidos) {
                if (pedidox.getId() == -1) {
                    pedidox.setLote(lote);
                    if (!dao.save(pedidox)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Pedidos");
                        dao.rollback();
                        return;
                    }
                } else if (!dao.update(pedidox)) {
                    GenericaMensagem.warn("Erro", "Erro ao Alterar Pedidos");
                    dao.rollback();
                    return;
                }
            }

            GenericaMensagem.info("OK", "Lançamento SALVO com Sucesso!");
            dao.commit();
            telaSalva = true;
            loadListaLancamento();
        } catch (Exception e) {
            GenericaMensagem.error("ERROR", e.getMessage());
            dao.rollback();
        }
    }

    public void openExcluirParcela(Parcela p, int index) {
        parcela = p;
        indexParcela = index;
    }

    public void excluirParcela() {
        if (parcela.getMovimento().getId() == -1) {
            listaParcela.remove(indexParcela);
            GenericaMensagem.info("Sucesso", "Item removido!");
        } else {
            if (parcela.getMovimento().getBaixa() != null) {
                GenericaMensagem.warn("Erro", "Não é possivel excluir parcela BAIXADA, para fazer isso ESTORNE os lançamentos!");
                return;
            }
            Dao dao = new Dao();
            if (dao.find(new Movimento(), parcela.getMovimento().getId()) == null) {
                GenericaMensagem.info("Sucesso", "Item removido!");
                listaParcela.remove(indexParcela);
                return;
            }
            dao.openTransaction();
            if (dao.delete((parcela.getMovimento()))) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Item removido!");
                listaParcela.remove(indexParcela);
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Não foi possivel excluir este Movimento!");
            }
        }
    }

    public void adicionarParcela() {

        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Erro", "PESQUISE ou CADASTRE uma pessoa para adicionar uma parcela!");
            return;
        }

        if (!listaContaOperacao.isEmpty() && idContaOperacao.equals(0)) {
            GenericaMensagem.warn("Erro", "Selecione uma CONTA PRIMÁRIA para adicionar parcela!");
            return;
        }

        if (vencimento.isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR A DATA DE VENCIMENTO!");
            return;
        }

        if (valor.equals("0,00") || valor.equals("0.00")) {
            GenericaMensagem.warn("Validação", "O VALOR NÃO PODE SER 0,00!");
            return;
        }

        Dao dao = new Dao();
        ContaOperacao co = (ContaOperacao) dao.find(new ContaOperacao(), idContaOperacao);
        if (co == null) {
            GenericaMensagem.warn("Validação", "NÃO EXISTE CONTA OPERACAÇÃO CADASTRADA (FILIAL + OPERAÇÃO)!");
            return;
        }
        FTipoDocumento td = (FTipoDocumento) dao.find(new FTipoDocumento(), idFTipoMovimento);
        Plano5 pl5;
        if (chkImposto) {
            pl5 = ((Plano5) dao.find(new Plano5(), idPlano5));
        } else {
            pl5 = co.getPlano5();
        }

        Movimento movimento = new Movimento(
                -1,
                null, // LOTE
                pl5,
                pessoa,
                null, // SERVICOS
                null, // BAIXA
                null, // TIPO SERVICO
                null, // ACORDO
                Moeda.converteUS$(valor),
                DataHoje.converteDataParaReferencia(vencimento), // REFERENCIA
                vencimento, // VENCIMENTO
                1, // QUANTIDADE
                true, // ATIVO
                esLancamento,
                false, // OBRIGACAO
                pessoa, // TITULAR
                pessoa, // BENEFICIARIO
                documentoMovimento, // DOCUMENTO
                "", // NR CTR BOLETO
                vencimento, // VENCIMENTO ORIGINAL
                0, // DESCONTO ATE VENCIMENTO
                Moeda.converteUS$(correcao), // CORRECAO
                Moeda.converteUS$(juros), // JUROS
                Moeda.converteUS$(multa), // MULTA
                Moeda.converteUS$(desconto), // DESCONTO
                0, // TAXA
                0, // VALOR BAIXA
                td, // TIPO DOCUMENTO
                0, // REPASSE AUTOMATICO    
                null // MATRICULA SÓCIO
        );

        Usuario user = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        float valor_t = Moeda.subtracaoValores(Moeda.somaValores(movimento.getValor(), Moeda.converteUS$(acrescimo)), movimento.getDesconto());
        if (condicao.equals("vista")) {
            if (listaParcela.size() == 1 && !chkImposto) {
                GenericaMensagem.warn("Validação", "Condição a vista, só é possível adicionar uma parcela!");
                return;
            }
        }
        listaParcela.add(new Parcela(
                listaParcela.size(),
                movimento,
                DataHoje.converteData(movimento.getDtVencimento()),
                movimento.getReferencia(),
                Moeda.converteR$Float(movimento.getValor()),
                Moeda.converteR$(acrescimo), // ACRESCIMO
                Moeda.converteR$Float(movimento.getDesconto()), // DESCONTO
                Moeda.converteR$Float(valor_t), // VALOR PAGAMENTO
                "", // DATA PAGAMENTO
                user.getPessoa().getNome().length() >= 30 ? user.getPessoa().getNome().substring(0, 30) + "..." : user.getPessoa().getNome(),
                "NÃO BAIXADO",
                "NÃO BAIXADO",
                Moeda.converteR$Float(movimento.getMulta()),
                Moeda.converteR$Float(movimento.getJuros()),
                Moeda.converteR$Float(movimento.getCorrecao()),
                false
        ));

        openModalAcrescimo();
        desconto = "0,00";
        chkImposto = false;
        if (condicao.equals("vista")) {
            vencimento = DataHoje.data();
        }
        GenericaMensagem.info("Sucesso", "Parcela adicionada!");
    }

    public void limpar() {
        LancamentoFinanceiroBean la = new LancamentoFinanceiroBean();
        la.load();
        la.setModalVisivel(true);
        la.setProdutos(true);
        GenericaSessao.put("lancamentoFinanceiroBean", la);
    }

    public void salvarPessoa() {
        Dao dao = new Dao();
        TipoDocumento td = (TipoDocumento) dao.find(new TipoDocumento(), idTipoDocumento);
        switch (td.getDescricao().toLowerCase()) {
            case "sem documento":
                if (opcaoCadastro.equals("juridica")) {
                    if (pessoa.getNome().isEmpty()) {
                        GenericaMensagem.warn("Erro", "Digite um NOME para EMPRESA!");
                        return;
                    }

                    dao.openTransaction();

                    pessoa.setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 4));
                    pessoa.setDocumento("0");
                    pessoa.setNome(pessoa.getNome().toUpperCase());

                    if (!dao.save(pessoa)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Pessoa!");
                        dao.rollback();
                        return;
                    }

                    Juridica juridica = new Juridica(-1, pessoa, pessoa.getNome().toUpperCase(), null, null, "", "", "", "", (Porte) dao.find(new Porte(), 1), "", "", true, false, "");
                    if (!dao.save(juridica)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Empresa!");
                        dao.rollback();
                        return;
                    }

                    dao.commit();
                    GenericaMensagem.info("Sucesso", "Cadastro concluído!");
                    opcaoCadastro = "";
                } else {
                    if (pessoa.getNome().isEmpty()) {
                        GenericaMensagem.warn("Erro", "Digite um NOME para PESSOA!");
                        return;
                    }

                    dao.openTransaction();

                    pessoa.setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 4));
                    pessoa.setDocumento("0");
                    pessoa.setNome(pessoa.getNome().toUpperCase());

                    if (!dao.save(pessoa)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Pessoa!");
                        dao.rollback();
                        return;
                    }

                    Fisica fisica = new Fisica(-1, pessoa, "", "", "", "", DataHoje.dataHoje(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    if (!dao.save(fisica)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar Cadastro!");
                        dao.rollback();
                        return;
                    }

                    dao.commit();
                    GenericaMensagem.info("Sucesso", "Cadastro concluído!");
                    opcaoCadastro = "";
                }
                break;
            case "cnpj": {
                if (!ValidaDocumentos.isValidoCNPJ(descricao.replace(".", "").replace("/", "").replace("-", ""))) {
                    GenericaMensagem.warn("Erro", "Este CNPJ não é válido!");
                    return;
                }
                JuridicaDao dbj = new JuridicaDao();
                List listDocumento = dbj.pesquisaJuridicaPorDoc(descricao);
                for (int i = 0; i < listDocumento.size(); i++) {
                    if (!listDocumento.isEmpty()) {
                        GenericaMensagem.warn("Erro", "Empresa já esta cadastrada no Sistema!");
                        return;
                    }
                }
                if (pessoa.getNome().isEmpty()) {
                    GenericaMensagem.warn("Erro", "Digite um NOME para EMPRESA!");
                    return;
                }
                dao.openTransaction();
                pessoa.setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 2));
                pessoa.setDocumento(descricao);
                pessoa.setNome(pessoa.getNome().toUpperCase());
                if (!dao.save(pessoa)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar Pessoa!");
                    dao.rollback();
                    return;
                }
                Juridica juridica = new Juridica(-1, pessoa, pessoa.getNome().toUpperCase(), null, null, "", "", "", "", (Porte) dao.find(new Porte(), 1), "", "", true, false, "");
                if (!dao.save(juridica)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar Empresa!");
                    dao.rollback();
                }
                dao.commit();
                GenericaMensagem.info("Sucesso", "Cadastro concluído!");
                opcaoCadastro = "";
                break;
            }
            default: {
                if (!ValidaDocumentos.isValidoCPF(descricao.replace(".", "").replace("/", "").replace("-", ""))) {
                    GenericaMensagem.warn("Erro", "Este CPF não é válido!");
                    return;
                }
                FisicaDao db = new FisicaDao();
                List listDocumento = db.pesquisaFisicaPorDoc(descricao);
                if (!listDocumento.isEmpty()) {
                    GenericaMensagem.warn("Erro", "CPF já esta cadastrada no Sistema!");
                    return;
                }
                if (pessoa.getNome().isEmpty()) {
                    GenericaMensagem.warn("Erro", "Digite um NOME para PESSOA!");
                    return;
                }
                dao.openTransaction();
                pessoa.setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
                pessoa.setDocumento(descricao);
                pessoa.setNome(pessoa.getNome().toUpperCase());
                if (!dao.save(pessoa)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar Pessoa!");
                    dao.rollback();
                }
                Fisica fisica = new Fisica(-1, pessoa, "", "", "", "", DataHoje.dataHoje(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                if (!dao.save(fisica)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar Cadastro!");
                    dao.rollback();
                }
                dao.commit();
                GenericaMensagem.info("Sucesso", "Cadastro concluído!");
                opcaoCadastro = "";
                break;
            }
        }
    }

    public void cadastrarPessoa(String param) {
        pessoa = new Pessoa();
        TipoDocumento td = (TipoDocumento) new Dao().find(new TipoDocumento(), idTipoDocumento);
        if (param.isEmpty()) {
            switch (td.getDescricao().toLowerCase()) {
                case "cnpj":
                    opcaoCadastro = "juridica";
                    break;
                case "cpf":
                    opcaoCadastro = "fisica";
                    break;
                default:
                    opcaoCadastro = "juridica";
                    break;
            }
        } else {
            opcaoCadastro = param;
        }
    }

    public void pesquisarPessoa() {
        if (!opcaoCadastro.isEmpty()) {
            return;
        }

        if (pessoa.getId() != -1 && pessoa.getDocumento().equals(descricao)) {
            return;
        }

        TipoDocumento td = (TipoDocumento) new Dao().find(new TipoDocumento(), idTipoDocumento);
        if (td.getDescricao().toLowerCase().equals("sem documento")) {
            return;
        }

        LancamentoFinanceiroDao db = new LancamentoFinanceiroDao();
        if (td.getDescricao().toLowerCase().equals("cnpj")) {
            Juridica juridica = db.pesquisaJuridica(descricao);
            if (juridica != null) {
                pessoa = juridica.getPessoa();
            } else {
                pessoa = new Pessoa();
            }
            telaSalva = false;
        } else {
            Fisica fisica = db.pesquisaFisica(descricao);
            if (fisica != null) {
                pessoa = fisica.getPessoa();
            } else {
                pessoa = new Pessoa();
            }
            telaSalva = false;
        }
    }

    public void cancelarCadastro() {
        opcaoCadastro = "";
        descricao = "";
        pessoa = new Pessoa();
    }

    public void abreModal() {
        LancamentoFinanceiroBean la = new LancamentoFinanceiroBean();
        la.load();
        la.setModalVisivel(true);
        la.setProdutos(true);
        GenericaSessao.put("lancamentoFinanceiroBean", la);
    }

    public void fechaModal() {
        if (lote.getId() == -1) {
            modalVisivel = false;
            telaSalva = true;
            chkImposto = false;
            loadListaLancamento();
            return;
        }

        float soma = 0;
        for (Parcela p : listaParcela) {
            soma = Moeda.somaValores(soma, p.getMovimento().getValor());
        }

        if (soma < Moeda.converteUS$(total)) {
            GenericaMensagem.warn("Erro", "Valor das Parcelas é MENOR que soma Total!");
            modalVisivel = true;
            telaSalva = false;
            return;
        } else if (soma > Moeda.converteUS$(total)) {
            if (lote.getId() == -1) {
                GenericaMensagem.warn("Erro", "Valor das Parcelas é MAIOR que soma Total!");
                modalVisivel = true;
                telaSalva = false;
                return;
            }
        }

        if (!telaSalva) {
            GenericaMensagem.fatal("Erro", "Lançamento alterado SALVE este formulário antes de FECHAR!");
            modalVisivel = true;
            return;
        }
        modalVisivel = false;
    }

    public void atualizaComboES() {
        esLancamento = es;
        loadListaOperacao();
        loadListaCentroCusto();
        loadListaContaOperacao();
        historico = "";
    }

    public void atualizaComboOperacao() {
        loadListaCentroCusto();
        loadListaContaOperacao();
    }

    public void atualizaComboCentroCusto() {
        loadListaContaOperacao();
    }

    public void atualizaComboTipoCentroCusto() {
        listaContaOperacao.clear();
        idContaOperacao = 0;
    }

    public String alterarDesconto(int index) {
        float acre = Moeda.converteUS$(listaParcela.get(index).getAcrescimo());
        float desc = Moeda.converteUS$(listaParcela.get(index).getDesconto());
        float valor_p = Moeda.converteUS$(listaParcela.get(index).getValor());
        float soma = Moeda.subtracaoValores(Moeda.somaValores(acre, valor_p), desc);

        listaParcela.get(index).setDesconto(Moeda.converteR$(String.valueOf(listaParcela.get(index).getDesconto())));
        listaParcela.get(index).setValorQuitado(Moeda.converteR$Float(soma));

        telaSalva = false;
        return null;
    }

    public String alterarAcrescimo(int index) {
        listaParcela.get(index).setValorQuitado(Moeda.converteR$(String.valueOf(listaParcela.get(index).getValorQuitado())));
        multa = Moeda.converteR$(listaParcela.get(index).getMulta());
        juros = Moeda.converteR$(listaParcela.get(index).getJuros());
        correcao = Moeda.converteR$(listaParcela.get(index).getCorrecao());
        indexAcrescimo = index;
        return null;
    }

    public String adicionarAcrescimo() {
        float acre = Moeda.somaValores(Moeda.somaValores(Moeda.converteUS$(multa), Moeda.converteUS$(juros)), Moeda.converteUS$(correcao));

        if (indexAcrescimo == -1) {
            acrescimo = Moeda.converteR$Float(acre);
        } else {
            listaParcela.get(indexAcrescimo).setAcrescimo(Moeda.converteR$Float(acre));
            listaParcela.get(indexAcrescimo).setMulta(Moeda.converteR$(multa));
            listaParcela.get(indexAcrescimo).setJuros(Moeda.converteR$(juros));
            listaParcela.get(indexAcrescimo).setCorrecao(Moeda.converteR$(correcao));

            float desc = Moeda.converteUS$(listaParcela.get(indexAcrescimo).getDesconto());
            float valor_p = Moeda.somaValores(Moeda.converteUS$(listaParcela.get(indexAcrescimo).getValor()), acre);

            listaParcela.get(indexAcrescimo).setValorQuitado(Moeda.converteR$Float(Moeda.subtracaoValores(valor_p, desc)));
        }

        telaSalva = false;
        indexAcrescimo = -1;
        return null;
    }

    public void openModalAcrescimo() {
        multa = "0,00";
        juros = "0,00";
        correcao = "0,00";
        acrescimo = "0,00";
    }

    public void alterarListaLancamento(Usuario u) {
        if (u != null) {
            usuarioSelecionado = u;

            if (((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId() == u.getId()) {
                strVisualizando = "os Meus Lançamentos";
            } else {
                strVisualizando = "os Lançamentos de " + u.getPessoa().getNome();
            }
        } else {
            usuarioSelecionado = new Usuario();
            strVisualizando = "TODOS Lançamentos";
        }

        loadListaLancamento();
    }

    public void loadListaLancamento() {
        listaLancamento = new ArrayList();
        LoteDao loteDao = new LoteDao();
        if (usuarioSelecionado.getId() == -1) {
            listaLancamento = loteDao.find(-1, porPesquisa, description);
        } else {
            listaLancamento = loteDao.find(usuarioSelecionado.getId(), porPesquisa, description);
        }
    }

    public List<Lote> getListaLancamento() {
        return listaLancamento;
    }

    public void setListaLancamento(List<Lote> listaLancamento) {
        this.listaLancamento = listaLancamento;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public void loadListaFilial() {
        Filial f = MacFilial.getAcessoFilial().getFilial();
        if (f.getId() != -1) {
            if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                liberaAcessaFilial = true;
                // ROTINA MATRÍCULA ESCOLA
                List<FilialRotina> list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
                // ID DA FILIAL
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idFilial = list.get(i).getFilial().getId();
                        }
                        if (Objects.equals(f.getId(), list.get(i).getFilial().getId())) {
                            idFilial = list.get(i).getFilial().getId();
                        }
                        listaFilial.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getFilial().getPessoa().getNome()));
                    }
                } else {
                    List<Filial> listFilial = new Dao().list(new Filial(), true);
                    if (!listFilial.isEmpty()) {
                        for (int i = 0; i < listFilial.size(); i++) {
                            if (i == 0) {
                                idFilial = listFilial.get(i).getId();
                            }
                            if (Objects.equals(f.getId(), listFilial.get(i).getId())) {
                                idFilial = listFilial.get(i).getId();
                            }
                            listaFilial.add(new SelectItem(listFilial.get(i).getId(), listFilial.get(i).getFilial().getPessoa().getDocumento() + " / " + listFilial.get(i).getFilial().getPessoa().getNome()));
                        }
                    } else {
                        idFilial = f.getId();
                        listaFilial.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getDocumento() + " / " + f.getFilial().getPessoa().getNome()));
                    }
                }
            } else {
                idFilial = f.getId();
                listaFilial.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getDocumento() + " / " + f.getFilial().getPessoa().getNome()));
            }
        }

    }

    public List<SelectItem> getListaFilial() {
        return listaFilial;
    }

    public void setListaFilial(List<SelectItem> listaFilial) {
        this.listaFilial = listaFilial;
    }

    public void loadListaTipoDocumento() {
        listaTipoDocumento = new ArrayList();
        List<TipoDocumento> list = (new LancamentoFinanceiroDao()).listaTipoDocumento();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idTipoDocumento = list.get(i).getId();
            }
            listaTipoDocumento.add(
                    new SelectItem(
                            list.get(i).getId(),
                            list.get(i).getDescricao()
                    )
            );
        }
    }

    public List<SelectItem> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public Integer getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Integer idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMascara() {
        try {
            return mascara = Mask.getMascaraPesquisa(((TipoDocumento) new Dao().find(new TipoDocumento(), idTipoDocumento)).getDescricao(), true);
        } catch (Exception e) {
            return mascara = "";
        }
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getEsLancamento() {
        return esLancamento;
    }

    public void setEsLancamento(String esLancamento) {
        this.esLancamento = esLancamento;
    }

    public int getIdFTipo() {
        return idFTipo;
    }

    public void setIdFTipo(int idFTipo) {
        this.idFTipo = idFTipo;
    }

    public int getIdFTipoMovimento() {
        return idFTipoMovimento;
    }

    public void setIdFTipoMovimento(int idFTipoMovimento) {
        this.idFTipoMovimento = idFTipoMovimento;
    }

    public void loadListaFTipo() {
        listaFTipo = new ArrayList();
        List<FTipoDocumento> list = new Dao().find("FTipoDocumento", new int[]{1, 12, 24, 25});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFTipo = list.get(i).getId();
            }
            listaFTipo.add(new SelectItem(
                    list.get(i).getId(),
                    list.get(i).getDescricao()
            ));
        }
    }

    public List<SelectItem> getListaFTipo() {
        return listaFTipo;
    }

    public void setListaFTipo(List<SelectItem> listaFTipo) {
        this.listaFTipo = listaFTipo;
    }

    public void loadListaFTipoMovimento() {
        listaFTipoMovimento = new ArrayList();
        List<FTipoDocumento> list = new Dao().find("FTipoDocumento", new int[]{1, 2, 12, 24, 25});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFTipoMovimento = list.get(i).getId();
            }
            listaFTipoMovimento.add(new SelectItem(
                    list.get(i).getId(),
                    list.get(i).getDescricao()
            ));
        }
    }

    public List<SelectItem> getListaFTipoMovimento() {
        return listaFTipoMovimento;
    }

    public void setListaFTipoMovimento(List<SelectItem> listaFTipoMovimento) {
        this.listaFTipoMovimento = listaFTipoMovimento;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
            descricao = pessoa.getDocumento();
            if (null != pessoa.getTipoDocumento().getId()) {
                switch (pessoa.getTipoDocumento().getId()) {
                    case 1:
                        idTipoDocumento = 0;
                        break;
                    case 2:
                        idTipoDocumento = 1;
                        break;
                    case 4:
                        idTipoDocumento = 2;
                        break;
                    default:
                        break;
                }
            }
            opcaoCadastro = "";
        } else if (GenericaSessao.exists("fisicaPesquisa")) {
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            descricao = pessoa.getDocumento();
            if (null != pessoa.getTipoDocumento().getId()) {
                switch (pessoa.getTipoDocumento().getId()) {
                    case 1:
                        idTipoDocumento = 0;
                        break;
                    case 2:
                        idTipoDocumento = 1;
                        break;
                    case 4:
                        idTipoDocumento = 2;
                        break;
                    default:
                        break;
                }
            }
            opcaoCadastro = "";
        } else if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            descricao = pessoa.getDocumento();
            if (null != pessoa.getTipoDocumento().getId()) {
                switch (pessoa.getTipoDocumento().getId()) {
                    case 1:
                        idTipoDocumento = 0;
                        break;
                    case 2:
                        idTipoDocumento = 1;
                        break;
                    case 4:
                        idTipoDocumento = 2;
                        break;
                    default:
                        break;
                }
            }
            opcaoCadastro = "";
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getTotal() {
        if (plano.getId() != -1 && produtos) {
            return total = getValorTotal();
        } else {
            return total = Moeda.converteR$(total, null);
        }
    }

    public void setTotal(String total) {
        this.total = Moeda.substituiVirgula(total);
    }

    public String getValor() {
        return Moeda.converteR$(valor, null);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public String getOpcaoCadastro() {
        return opcaoCadastro;
    }

    public void setOpcaoCadastro(String opcaoCadastro) {
        this.opcaoCadastro = opcaoCadastro;
    }

    public List<Parcela> getListaParcela() {
        if (listaParcela.isEmpty() && lote.getId() != -1) {
            List<Movimento> selectMovimento = new MovimentoDao().listaMovimentosDoLote(lote.getId());
            float acre, desc, valor_quitado;
            String data_quitacao;
            String caixa;
            String loteBaixa;
            for (Movimento mov : selectMovimento) {
                acre = Moeda.somaValores(Moeda.somaValores(mov.getMulta(), mov.getJuros()), mov.getCorrecao());
                desc = mov.getDesconto();

                if (mov.getBaixa() != null) {
                    valor_quitado = mov.getValorBaixa();
                    data_quitacao = mov.getBaixa().getBaixa();
                    caixa = mov.getBaixa().getCaixa() != null ? mov.getBaixa().getCaixa().getCaixa() + "" : "NÃO BAIXADO";
                    loteBaixa = mov.getBaixa() != null ? mov.getBaixa().getId() + "" : "NÃO BAIXADO";
                } else {
                    valor_quitado = Moeda.subtracaoValores(Moeda.somaValores(mov.getValor(), acre), desc);
                    data_quitacao = "";
                    caixa = "NÃO BAIXADO";
                    loteBaixa = "NÃO BAIXADO";
                }
                listaParcela.add(new Parcela(
                        listaParcela.size(),
                        mov,
                        DataHoje.converteData(mov.getDtVencimento()),
                        mov.getReferencia(),
                        Moeda.converteR$Float(mov.getValor()),
                        Moeda.converteR$Float(acre),
                        Moeda.converteR$Float(mov.getDesconto()),
                        Moeda.converteR$Float(valor_quitado),
                        data_quitacao,
                        lote.getUsuario().getPessoa().getNome().length() >= 30 ? lote.getUsuario().getPessoa().getNome().substring(0, 30) + "..." : lote.getUsuario().getPessoa().getNome(),
                        caixa,
                        loteBaixa,
                        Moeda.converteR$Float(mov.getMulta()),
                        Moeda.converteR$Float(mov.getJuros()),
                        Moeda.converteR$Float(mov.getCorrecao()),
                        false
                ));

            }
        }
        return listaParcela;
    }

    public void setListaParcela(List<Parcela> listaParcela) {
        this.listaParcela = listaParcela;
    }

    public boolean isModalVisivel() {
        return modalVisivel;
    }

    public void setModalVisivel(boolean modalVisivel) {
        this.modalVisivel = modalVisivel;
    }

    public void loadListaOperacao() {
        listaOperacao = new ArrayList();
        List<Operacao> list = new OperacaoDao().findByEs(es);
        idOperacao = null;
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() != 7 && list.get(i).getId() != 8) {
                    if (list.get(i).getId() == 2) {
                        idOperacao = list.get(i).getId();
                    }
                    listaOperacao.add(
                            new SelectItem(
                                    list.get(i).getId(),
                                    list.get(i).getDescricao()
                            )
                    );
                }
            }
            if (idOperacao == null) {
                for (int i = 0; i < listaOperacao.size(); i++) {
                    if (i == 0) {
                        idOperacao = (Integer) listaOperacao.get(i).getValue();
                        break;
                    }
                }
            }
        } else {
            idOperacao = 0;
            listaOperacao.add(new SelectItem(0, "Nenhuma Operação Encontrada"));
        }
    }

    public List<SelectItem> getListaOperacao() {
        return listaOperacao;
    }

    public void setListaOperacao(List<SelectItem> listaOperacao) {
        this.listaOperacao = listaOperacao;
    }

    public Integer getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(Integer idOperacao) {
        this.idOperacao = idOperacao;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Integer getIdContaOperacao() {
        return idContaOperacao;
    }

    public void setIdContaOperacao(Integer idContaOperacao) {
        this.idContaOperacao = idContaOperacao;
    }

    public void loadListaContaOperacao() {
        listaContaOperacao = new ArrayList();
        List<ContaOperacao> listaConta = new ContaOperacaoDao().findByFilialOperacao(idFilial, idOperacao, idCentroCusto);
        if (listaConta.isEmpty()) {
            listaConta = new ContaOperacaoDao().findByFilialOperacao(idFilial, idOperacao);
        }
        if (!listaConta.isEmpty()) {
            for (int i = 0; i < listaConta.size(); i++) {
                if (i == 0) {
                    idContaOperacao = listaConta.get(i).getId();
                }
                listaContaOperacao.add(
                        new SelectItem(
                                listaConta.get(i).getId(),
                                listaConta.get(i).getPlano5().getConta() + " (" + listaConta.get(i).getPlano5().getPlano4().getConta() + ")",
                                "" + listaConta.get(i).getPlano5().getId()
                        )
                );
            }
        } else {
            idContaOperacao = 0;
            listaContaOperacao.add(new SelectItem(0, "Nenhuma Conta Encontrada"));
        }
    }

    public List<SelectItem> getListaContaOperacao() {
        if (listaContaOperacao.isEmpty() || listaOperacao.size() == 1) {
            idContaOperacao = 0;
            listaContaOperacao = new ArrayList();
            listaContaOperacao.add(new SelectItem(0, "Nenhuma Conta Encontrada", "0"));
            return listaContaOperacao;
        }
        return listaContaOperacao;
    }

    public void setListaContaOperacao(List<SelectItem> listaContaOperacao) {
        this.listaContaOperacao = listaContaOperacao;
    }

    public Integer getIdCentroCusto() {
        return idCentroCusto;
    }

    public void setIdCentroCusto(Integer idCentroCusto) {
        this.idCentroCusto = idCentroCusto;
    }

    public void loadListaCentroCusto() {
        listaCentroCusto = new ArrayList();
        Operacao o = getOperacao();
        if (o.getCentroCusto()) {
            List<CentroCusto> listaCentro = new CentroCustoDao().findByFilial(idFilial);
            if (!listaCentro.isEmpty()) {
                for (int i = 0; i < listaCentro.size(); i++) {
                    if (i == 0) {
                        idCentroCusto = listaCentro.get(i).getId();
                    }
                    listaCentroCusto.add(
                            new SelectItem(
                                    listaCentro.get(i).getId(),
                                    listaCentro.get(i).getDescricao()
                            )
                    );
                }
            } else {
                idCentroCusto = 0;
                listaCentroCusto.add(new SelectItem(0, "Nenhum Centro Custo Encontrado"));
            }
        } else {
            idCentroCusto = 0;
            listaCentroCusto.add(new SelectItem(0, "Nenhum Centro Custo Encontrado"));
        }
    }

    public List<SelectItem> getListaCentroCusto() {
        return listaCentroCusto;
    }

    public void setListaCentroCusto(List<SelectItem> listaCentroCusto) {
        this.listaCentroCusto = listaCentroCusto;
    }

//    public List<SelectItem> getListaTipoCentroCusto() {
//        if (listaTipoCentroCusto.isEmpty()) {
//            if (idCentroCusto == 0) {
//                GenericaMensagem.error("Erro", "Cadastre um Centro de Custo para fazer um Lançamento!");
//                listaTipoCentroCusto.add(new SelectItem(0, "Nenhum Tipo Encontrado", "0"));
//                return listaTipoCentroCusto;
//            }
//
//            CentroCusto centroCusto = (CentroCusto) new Dao().find(new CentroCusto(), idCentroCusto);
////            List<CentroCustoContabilSub> listaCentroContabil = (new LancamentoFinanceiroDao()).listaTipoCentroCusto(centroCusto.getCentroCustoContabil().getId(), es);
////            if (!listaCentroContabil.isEmpty()) {
////                for (int i = 0; i < listaCentroContabil.size(); i++) {
////                    listaTipoCentroCusto.add(new SelectItem(
////                            i,
////                            listaCentroContabil.get(i).getDescricao(),
////                            Integer.toString(listaCentroContabil.get(i).getId()))
////                    );
////                }
////            } else {
////                listaTipoCentroCusto.add(new SelectItem(0, "Nenhum Tipo Encontrado", "0"));
////            }
//        }
//        return listaTipoCentroCusto;
//    }
//
//    public void setListaTipoCentroCusto(List<SelectItem> listaTipoCentroCusto) {
//        this.listaTipoCentroCusto = listaTipoCentroCusto;
//    }
    public Operacao getOperacao() {
        if (listaOperacao.size() != 1 && !idOperacao.equals(0)) {
            if (!idOperacao.equals(operacao.getId())) {
                operacao = (Operacao) new Dao().find(new Operacao(), idOperacao);
            }
        }
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public boolean isChkImposto() {
        if (chkImposto) {
            listaPlano5.clear();
        }
        return chkImposto;
    }

    public void setChkImposto(boolean chkImposto) {
        this.chkImposto = chkImposto;
    }

    public List<SelectItem> getListaPlano5() {
        if (listaPlano5.isEmpty()) {
            List<Plano5> list = new Plano5Dao().find(-1, 1);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idPlano5 = list.get(i).getId();
                    }
                    listaPlano5.add(
                            new SelectItem(
                                    list.get(i).getId(),
                                    list.get(i).getConta()
                            )
                    );
                }
            } else {
                idPlano5 = 0;
                listaPlano5.add(new SelectItem(0, "Nenhuma Conta Encontrada"));
            }
        }
        return listaPlano5;
    }

    public void setListaPlano5(List<SelectItem> listaContaTipoPlano5) {
        this.listaPlano5 = listaContaTipoPlano5;
    }

    public Integer getIdPlano5() {
        return idPlano5;
    }

    public void setIdPlano5(Integer idPlano5) {
        this.idPlano5 = idPlano5;
    }

    public String getStrConta() {
        try {
            if (!listaContaOperacao.isEmpty() && !chkImposto) {
                ContaOperacao co = (ContaOperacao) new Dao().find(new ContaOperacao(), idContaOperacao);
                strConta = co.getPlano5().getConta();
            } else if (!listaPlano5.isEmpty() && chkImposto) {
                Plano5 p = (Plano5) new Dao().find(new Plano5(), idPlano5);
                strConta = p.getConta();
            } else {
                //strConta = "SEM CONTA SELECIONADA.";
                strConta = "";
            }
        } catch (Exception e) {
            strConta = "";
        }
        return strConta;
    }

    public void setStrConta(String strConta) {
        this.strConta = strConta;
    }

    public boolean isDisabledConta() {
        return disabledConta;
    }

    public void setDisabledConta(boolean disabledConta) {
        this.disabledConta = disabledConta;
    }

    public String getDocumentoMovimento() {
        return documentoMovimento;
    }

    public void setDocumentoMovimento(String documentoMovimento) {
        this.documentoMovimento = documentoMovimento;
    }

    public Plano5 getPlano() {
        if (modalVisivel) {
            if (!listaContaOperacao.isEmpty()) {
                ContaOperacao co = (ContaOperacao) new Dao().find(new ContaOperacao(), idContaOperacao);
                if (co != null) {
                    List<Plano5> select = new Plano5Dao().find(co.getPlano5().getId(), 2);
                    if (!select.isEmpty()) {
                        plano = select.get(0);
                    } else {
                        plano = new Plano5();
                    }
                }
            }
        }
        return plano;
    }

    public void setPlano(Plano5 plano) {
        this.plano = plano;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getMaskSearch() {
        switch (porPesquisa) {
            case "emissao":
                maskSearch = "99/99/9999";
                break;
            case "documento":
                if (null != idTipoDocumento) {
                    switch (idTipoDocumento) {
                        case 0:
                            maskSearch = "999.999.999-99";
                            break;
                        case 1:
                            maskSearch = "99.999.999/9999-99";
                            break;
                        default:
                            maskSearch = "";
                            break;
                    }
                }
                break;
            default:
                maskSearch = "";
                break;
        }

        return maskSearch;
    }

    public void setMaskSearch(String maskSearch) {
        this.maskSearch = maskSearch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Parcela> getListaParcelaSelecionada() {
        return listaParcelaSelecionada;
    }

    public void setListaParcelaSelecionada(List<Parcela> listaParcelaSelecionada) {
        this.listaParcelaSelecionada = listaParcelaSelecionada;
    }

    public int getIndexParcela() {
        return indexParcela;
    }

    public void setIndexParcela(int indexParcela) {
        this.indexParcela = indexParcela;
    }

    public String getAcrescimo() {
        return Moeda.converteR$(acrescimo);
    }

    public void setAcrescimo(String acrescimo) {
        this.acrescimo = Moeda.substituiVirgula(acrescimo);
    }

    public String getMulta() {
        return Moeda.converteR$(multa);
    }

    public void setMulta(String multa) {
        this.multa = Moeda.substituiVirgula(multa);
    }

    public String getJuros() {
        return Moeda.converteR$(juros);
    }

    public void setJuros(String juros) {
        this.juros = Moeda.substituiVirgula(juros);
    }

    public String getCorrecao() {
        return Moeda.converteR$(correcao);
    }

    public void setCorrecao(String correcao) {
        this.correcao = Moeda.substituiVirgula(correcao);
    }

    public String getDesconto() {
        return Moeda.converteR$(desconto);
    }

    public void setDesconto(String desconto) {
        this.desconto = Moeda.substituiVirgula(desconto);
    }

    public String getStrVisualizando() {
        return strVisualizando;
    }

    public void setStrVisualizando(String strVisualizando) {
        this.strVisualizando = strVisualizando;
    }

    public List<Usuario> getListaUsuarioLancamento() {
        if (listaUsuarioLancamento.isEmpty()) {
            listaUsuarioLancamento.add(Usuario.getUsuario());
            listaUsuarioLancamento.addAll(new Dao().list(new Usuario(), true));
        }
        return listaUsuarioLancamento;
    }

    public void setListaUsuarioLancamento(List<Usuario> listaUsuarioLancamento) {
        this.listaUsuarioLancamento = listaUsuarioLancamento;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Parcela getParcela() {
        return parcela;
    }

    public void setParcela(Parcela parcela) {
        this.parcela = parcela;
    }

    public String getHistorico() {
        if (historico.isEmpty() && es.equals("S") && pessoa.getId() != -1) {
            Dao dao = new Dao();
            FTipoDocumento f_doc = (FTipoDocumento) dao.find(new FTipoDocumento(), idFTipo);
            String num = lote.getDocumento().isEmpty() ? "____" : lote.getDocumento();
            String pes_doc = pessoa.getTipoDocumento().getId() == 4 ? "" : pessoa.getTipoDocumento().getDescricao() + ": " + pessoa.getDocumento();
            historico = "Pagamento referente a " + f_doc.getDescricao() + " de número " + num + " a " + pessoa.getNome() + ", " + pes_doc;
        } else if (historico.isEmpty() && es.equals("E") && pessoa.getId() != -1) {
            String pes_doc = pessoa.getTipoDocumento().getId() == 4 ? "" : pessoa.getTipoDocumento().getDescricao() + ": " + pessoa.getDocumento();
            historico = "Referente ao recebimento de " + pessoa.getNome() + ", " + pes_doc;
        }
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Boolean getProdutos() {
        return produtos;
    }

    public void setProdutos(Boolean produtos) {
        this.produtos = produtos;
    }

    public void loadVencimento() {
        if (condicao.equals("vista")) {
            vencimento = DataHoje.data();
        }
    }

    public List<Fisica> getListFisicaSugestao() {
        return listFisicaSugestao;
    }

    public void setListFisicaSugestao(List<Fisica> listFisicaSugestao) {
        this.listFisicaSugestao = listFisicaSugestao;
    }

    public List<Juridica> getListJuridicaSugestao() {
        return listJuridicaSugestao;
    }

    public void setListJuridicaSugestao(List<Juridica> listJuridicaSugestao) {
        this.listJuridicaSugestao = listJuridicaSugestao;
    }

    public class Parcela {

        private Integer parcela;
        private Movimento movimento;
        private String vencimento;
        private String referencia;
        private String valor;
        private String acrescimo;
        private String desconto;
        private String valorQuitado;
        private String dataQuitacao;
        private String usuarioNome;
        private String caixa;
        private String loteBaixa;
        private String juros;
        private String multa;
        private String correcao;
        private Boolean selected;

        public Parcela() {
            this.parcela = 0;
            this.movimento = null;
            this.vencimento = "";
            this.valor = "0,00";
            this.acrescimo = "0,00";
            this.desconto = "0,00";
            this.valorQuitado = "0,00";
            this.dataQuitacao = "";
            this.usuarioNome = "";
            this.caixa = "";
            this.loteBaixa = "";
            this.selected = false;
        }

        public Parcela(Integer parcela, Movimento movimento, String vencimento, String referencia, String valor, String acrescimo, String desconto, String valorQuitado, String dataQuitacao, String usuarioNome, String caixa, String loteBaixa, Boolean selected) {
            this.parcela = parcela;
            this.movimento = movimento;
            this.vencimento = vencimento;
            this.referencia = referencia;
            this.valor = valor;
            this.acrescimo = acrescimo;
            this.desconto = desconto;
            this.valorQuitado = valorQuitado;
            this.dataQuitacao = dataQuitacao;
            this.usuarioNome = usuarioNome;
            this.caixa = caixa;
            this.loteBaixa = loteBaixa;
            this.selected = selected;
        }

        public Parcela(Integer parcela, Movimento movimento, String vencimento, String referencia, String valor, String acrescimo, String desconto, String valorQuitado, String dataQuitacao, String usuarioNome, String caixa, String loteBaixa, String juros, String multa, String correcao, Boolean selected) {
            this.parcela = parcela;
            this.movimento = movimento;
            this.vencimento = vencimento;
            this.referencia = referencia;
            this.valor = valor;
            this.acrescimo = acrescimo;
            this.desconto = desconto;
            this.valorQuitado = valorQuitado;
            this.dataQuitacao = dataQuitacao;
            this.usuarioNome = usuarioNome;
            this.caixa = caixa;
            this.loteBaixa = loteBaixa;
            this.juros = juros;
            this.multa = multa;
            this.correcao = correcao;
            this.selected = selected;
        }

        public Integer getParcela() {
            return parcela;
        }

        public void setParcela(Integer parcela) {
            this.parcela = parcela;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public String getVencimento() {
            return vencimento;
        }

        public void setVencimento(String vencimento) {
            this.vencimento = vencimento;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getAcrescimo() {
            return acrescimo;
        }

        public void setAcrescimo(String acrescimo) {
            this.acrescimo = acrescimo;
        }

        public String getDesconto() {
            return desconto;
        }

        public void setDesconto(String desconto) {
            this.desconto = desconto;
        }

        public String getValorQuitado() {
            return valorQuitado;
        }

        public void setValorQuitado(String valorQuitado) {
            this.valorQuitado = valorQuitado;
        }

        public String getDataQuitacao() {
            return dataQuitacao;
        }

        public void setDataQuitacao(String dataQuitacao) {
            this.dataQuitacao = dataQuitacao;
        }

        public String getUsuarioNome() {
            return usuarioNome;
        }

        public void setUsuarioNome(String usuarioNome) {
            this.usuarioNome = usuarioNome;
        }

        public String getCaixa() {
            return caixa;
        }

        public void setCaixa(String caixa) {
            this.caixa = caixa;
        }

        public String getLoteBaixa() {
            return loteBaixa;
        }

        public void setLoteBaixa(String loteBaixa) {
            this.loteBaixa = loteBaixa;
        }

        public String getJuros() {
            return juros;
        }

        public void setJuros(String juros) {
            this.juros = juros;
        }

        public String getMulta() {
            return multa;
        }

        public void setMulta(String multa) {
            this.multa = multa;
        }

        public String getCorrecao() {
            return correcao;
        }

        public void setCorrecao(String correcao) {
            this.correcao = correcao;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }
    }

    public void sugerirPessoaFisica() {
        if (pessoa.getId() == -1) {
            if (!pessoa.getNome().isEmpty()) {
                listFisicaSugestao = new ArrayList();
                listFisicaSugestao = new FisicaDao().findByNome(pessoa.getNome());
                if (!listFisicaSugestao.isEmpty()) {
                    PF.openDialog("dlg_sugestoes_fisica");
                    PF.update("form_lf:i_sugestoes_fisica");
                }
            }
        }
    }

    public void sugerirPessoaJuridica() {
        if (pessoa.getId() == -1) {
            if (!pessoa.getNome().isEmpty()) {
                listJuridicaSugestao = new ArrayList();
                listJuridicaSugestao = new JuridicaDao().findByNome(pessoa.getNome());
                if (!listJuridicaSugestao.isEmpty()) {
                    PF.openDialog("dlg_sugestoes_juridica");
                    PF.update("form_lf:i_sugestoes_juridica");
                }
            }
        }
    }

    public void useFisicaSugestao(Fisica f) {
        pessoa = (Pessoa) new Dao().rebind(f.getPessoa());
        descricao = pessoa.getDocumento();
        opcaoCadastro = "";
        PF.update("form_lf");
    }

    public void useJuridicaSugestao(Juridica j) {
        pessoa = (Pessoa) new Dao().rebind(j.getPessoa());
        descricao = pessoa.getDocumento();
        opcaoCadastro = "";
        PF.update("form_lf");
    }

    public List<Socios> getListaSocios() {
        return listaSocios;
    }

    public void setListaSocios(List<Socios> listaSocios) {
        this.listaSocios = listaSocios;
    }

    public void listenerSocios(Integer idPessoa) {
        listaSocios.clear();
        SociosDao sociosDao = new SociosDao();
        Socios s = sociosDao.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
            listaSocios = sociosDao.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
        }
    }

    public String pessoaEmpresaString(Fisica f) {
        String pessoaEmpresaString = "";
        PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
        PessoaEmpresa pe = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorFisica(f.getId());
        if (pe != null) {
            if (pe.getId() != -1) {
                pessoaEmpresaString = pe.getJuridica().getPessoa().getNome();
            }
        }
        return (pessoaEmpresaString.isEmpty()) ? "SEM EMPRESA" : pessoaEmpresaString;
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento);
        }
        return false;
    }

//                0 - listaParcela.size(),
//                1 - movimento,
//                2 - DataHoje.converteData(movimento.getDtVencimento()),
//                3 - Moeda.converteR$Float(movimento.getValor()),
//                4 - Moeda.converteR$(acrescimo), // ACRESCIMO
//                5 - Moeda.converteR$Float(movimento.getDesconto()), // DESCONTO
//                6 - Moeda.converteR$Float(valor_t), // VALOR PAGAMENTO
//                7 - "", // DATA PAGAMENTO
//                8 - new FiltroLancamento(),
//                9 - user.getPessoa().getNome().length() >= 30 ? user.getPessoa().getNome().substring(0, 30) + "..." : user.getPessoa().getNome(),
//                10 - "NÃO BAIXADO",
//                11 - "NÃO BAIXADO",
//                12 - Moeda.converteR$Float(movimento.getMulta()),
//                13 - Moeda.converteR$Float(movimento.getJuros()),
//                14 - Moeda.converteR$Float(movimento.getCorrecao()),
//                15 - null.   
}
