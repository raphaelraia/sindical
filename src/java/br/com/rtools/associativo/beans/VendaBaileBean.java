package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AEndereco;
import br.com.rtools.associativo.AStatus;
import br.com.rtools.associativo.BVenda;
import br.com.rtools.associativo.EventoBaile;
import br.com.rtools.associativo.EventoBaileConvite;
import br.com.rtools.associativo.EventoBaileImpressaoConvite;
import br.com.rtools.associativo.EventoBaileMapa;
import br.com.rtools.associativo.EventoBanda;
import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.BVendaDao;
import br.com.rtools.associativo.dao.VendaBaileDao;
import br.com.rtools.associativo.dao.EventoBaileDao;
import br.com.rtools.associativo.dao.EventoBandaDao;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.impressao.ParametroConviteBaile;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.utilitarios.AutenticaUsuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class VendaBaileBean implements Serializable {

    private List<SelectItem> listaEventoBaile = new ArrayList();
    private List<EventoBaileMapa> listaMesasBaile = new ArrayList();
    private List<EventoBaileMapa> listaMesasBaileSelecionada = new ArrayList();
    private List<EventoBaileConvite> listaConviteBaile = new ArrayList();
    private List<EventoBaileConvite> listaConviteBaileSelecionado = new ArrayList();
    private List<EventoBaileImpressaoConvite> listaConviteBaileImpressaoConvites = new ArrayList();
    private List<SelectItem> listaTipoVenda = new ArrayList();
    //private int idMesaBaile = 0;
    private int indexEventoBaile = 0;
    private Pessoa pessoa = new Pessoa();
    private BVenda venda = new BVenda();
    private Boolean todos = false;

    private String mesaConvite = "mesa";
    private String tipoVenda = "venda";

    private Integer idServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();

    private String valor = "0,00";
    private String desconto = "0,00";
    private double descontoPrimeiraVenda = 0;
    private String valorLiquido = "0,00";
    private String total = "0,00";
    private Integer quantidade = 0;

    private List<List> listaVendasMesa = new ArrayList();
    private List<List> listaVendasConvite = new ArrayList();
    private ControleAcessoBean cab = new ControleAcessoBean();
    private String novoDesconto = "0,00";
    private String tipoPagamento = "avista";

    private Pessoa responsavel = new Pessoa();
    private EventoServicoValor eventoServicoValor = new EventoServicoValor();
    private String descricaoServico = "NENHUM SERVIÇO CORRESPONDENTE";
    private boolean chkCortesia = false;
    private EventoBaile eventoBaile = new EventoBaile();

    private List<Movimento> listaMovimento = new ArrayList();

    private EventoBaileMapa ebmTroca = new EventoBaileMapa();
    private EventoBaileMapa ebmTrocaSelecionada = new EventoBaileMapa();

    private EventoBaileConvite ebcTroca = new EventoBaileConvite();
    private EventoBaileConvite ebcTrocaSelecionada = new EventoBaileConvite();
    private Boolean editarVenda = false;
    private String startDate = "";
    private String endDate = "";

    @PostConstruct
    public void init() {
        loadListaEventoBaile();
        loadListaMesa();
        loadListaServicos();
        loadListaConvite();
        loadListaTipoVenda();

        GenericaSessao.remove("fisicaPesquisa");

        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");
    }

    public void load() {
        if (GenericaSessao.exists("baixa_geral_sucesso", true)) {
            VendaBaileDao dao = new VendaBaileDao();
            // LISTA EVENTO BAILE
            List<EventoBaileMapa> lm = dao.listaEventoBaileMapaPorVenda(venda.getId());
            List<EventoBaileConvite> lc = dao.listaEventoBaileConvitePorVenda(venda.getId());
            if (!lm.isEmpty()) {
                loadListaTipoVenda();
                loadListaMesa();
            } else if (!lc.isEmpty()) {
                loadListaTipoVenda();
                loadListaConvite();
            }
        }
    }

    public void imprimirConviteIndividual(EventoBaileConvite ebc) {

        if (ebc.getMovimento() != null) {
            if (ebc.getMovimento().getLote().getCondicaoPagamento().getId() == 1) {
                Movimento m = (Movimento) new Dao().find(ebc.getMovimento());
                if (m.getBaixa() == null) {
                    GenericaMensagem.warn("Atenção", "Não é possível imprimir convites com movimentos não baixados!");
                    return;
                }
            }
        }

        EventoBaileDao ebdb = new EventoBaileDao();

        // FALSE = tem permissão
        // TRUE = não tem permissão
        // cab.verificaPermissao("reimprimir_convite_baile", 4)
        Boolean reimpressao = cab.verificaPermissao("reimprimir_convite_baile", 4);
        List<EventoBaileImpressaoConvite> listaEventoBaileImpressaoConvite = ebdb.listaEventoBaileImpressaoConvite(ebc.getId(), "convite");
        if (!listaEventoBaileImpressaoConvite.isEmpty()) {
            if (reimpressao) {
                GenericaMensagem.error("Atenção", "Convite já impresso! Sem permissão para reimpressão.");
                return;
            }
            GenericaMensagem.warn("Importante", "Convite já impresso! Reimpressão com persmissão de acesso!!!");
        }

        String tipo = "Individual", tipo_descricao, numero = "" + ebc.getConvite();

        Boolean chk_c = (ebc.getbVenda().getEventoServico().getServicos().getId() == 13 || ebc.getbVenda().getEventoServico().getServicos().getId() == 13);
        if (chk_c) {
            tipo_descricao = "CONVITE CORTESIA INDIVIDUAL";
        } else {
            tipo_descricao = "CONVITE INDIVIDUAL";
        }

        String descricao = ebc.getEventoBaile().getEvento().getDescricaoEvento().getDescricao();
        Date data = ebc.getEventoBaile().getData();
        String hora_inicial = ebc.getEventoBaile().getHoraInicio();
        String hora_final = ebc.getEventoBaile().getHoraFim();

        EventoBandaDao db = new EventoBandaDao();
        List<EventoBanda> listaEventoBanda = db.pesquisaBandasDoEvento(ebc.getbVenda().getEvento().getId());

        String banda = "";
        for (EventoBanda eb : listaEventoBanda) {
            banda += eb.getBanda().getDescricao() + " \n ";
        }

        Pessoa p = ebc.getbVenda().getPessoa();
        Pessoa resp = new FunctionsDao().titularDaPessoa(p.getId());

        Socios s = resp.getSocios();

        String nome_socio = "", categoria = "";
        if (s != null) {
            nome_socio = resp.getNome();
            categoria = s.getMatriculaSocios().getCategoria().getCategoria();
        }

        AEndereco endereco = ebdb.pesquisaEnderecoEvento(ebc.getEventoBaile().getEvento().getId());
        String local = ebc.getEventoBaile().getLocal(), endereco_string = endereco.getEndereco().getEnderecoToString() + ", " + endereco.getNumero() + endereco.getComplemento();

        EventoBaileImpressaoConvite ebic = new EventoBaileImpressaoConvite(
                -1,
                ebc,
                null,
                Usuario.getUsuario(),
                DataHoje.dataHoje(),
                DataHoje.horaMinuto()
        );

        if (!new Dao().save(ebic, true)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Registro de Impressão!");
            return;
        }

        imprimirConvite(tipo, tipo_descricao, numero, descricao, data, hora_inicial, hora_final, banda, nome_socio, categoria, local, endereco_string);
    }

    public void imprimirConviteMesa(EventoBaileMapa ebm) {
        EventoBaileDao ebdb = new EventoBaileDao();
        // FALSE = tem permissão
        // TRUE = não tem permissão
        if (ebm.getbVenda().getEventoServico().getServicos().getId() != 13 && ebm.getbVenda().getEventoServico().getServicos().getId() != 15) {
            if (ebm.getMovimento() != null) {
                if (ebm.getMovimento().getLote().getCondicaoPagamento().getId() == 1) {
                    Movimento m = (Movimento) new Dao().find(ebm.getMovimento());
                    if (m.getBaixa() == null) {
                        GenericaMensagem.warn("Atenção", "Não é possível imprimir convites com movimentos não baixados!");
                        return;
                    }
                }
            }
        }
        Boolean reimpressao = cab.verificaPermissao("reimprimir_convite_baile", 4);
        List<EventoBaileImpressaoConvite> listaEventoBaileImpressaoConvite = ebdb.listaEventoBaileImpressaoConvite(ebm.getId(), "mesa");
        if (!listaEventoBaileImpressaoConvite.isEmpty()) {
            if (reimpressao) {
                GenericaMensagem.error("Atenção", "Convite já impresso! Sem permissão para reimpressão.");
                return;
            }
            GenericaMensagem.warn("Importante", "Convite já impresso! Reimpressão com persmissão de acesso!!!");
        }

        String tipo = "Mesa", tipo_descricao, numero = "" + ebm.getMesa();

        Boolean chk_c = (ebm.getbVenda().getEventoServico().getServicos().getId() == 13 || ebm.getbVenda().getEventoServico().getServicos().getId() == 13);
        if (chk_c) {
            tipo_descricao = "CONVITE CORTESIA MESA";
        } else {
            tipo_descricao = "CONVITE MESA";
        }

        String descricao = ebm.getEventoBaile().getEvento().getDescricaoEvento().getDescricao();
        Date data = ebm.getEventoBaile().getData();
        String hora_inicial = ebm.getEventoBaile().getHoraInicio();
        String hora_final = ebm.getEventoBaile().getHoraFim();

        EventoBandaDao db = new EventoBandaDao();
        List<EventoBanda> listaEventoBanda = db.pesquisaBandasDoEvento(ebm.getbVenda().getEvento().getId());

        String banda = "";
        for (EventoBanda eb : listaEventoBanda) {
            banda += eb.getBanda().getDescricao() + " \n ";
        }

        Pessoa p = ebm.getbVenda().getPessoa();
        Pessoa resp = new FunctionsDao().titularDaPessoa(p.getId());

        Socios s = resp.getSocios();

        String nome_socio = "", categoria = "";
        if (s != null) {
            nome_socio = resp.getNome();
            categoria = s.getMatriculaSocios().getCategoria().getCategoria();
        }

        EventoBaileDao eventoBaileDB = new EventoBaileDao();
        AEndereco endereco = eventoBaileDB.pesquisaEnderecoEvento(ebm.getEventoBaile().getEvento().getId());
        String local = ebm.getEventoBaile().getLocal(), endereco_string = endereco.getEndereco().getEnderecoToString() + ", " + endereco.getNumero() + endereco.getComplemento();

        EventoBaileImpressaoConvite ebic = new EventoBaileImpressaoConvite(
                -1,
                null,
                ebm,
                Usuario.getUsuario(),
                DataHoje.dataHoje(),
                DataHoje.horaMinuto()
        );

        if (!new Dao().save(ebic, true)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Registro de Impressão!");
            return;
        }

        imprimirConvite(tipo, tipo_descricao, numero, descricao, data, hora_inicial, hora_final, banda, nome_socio, categoria, local, endereco_string);
    }

    public void imprimirConvite(String tipo, String tipo_descricao, String numero, String descricao, Date data, String hora_inicial, String hora_final, String banda, String nome_socio, String categoria, String local, String endereco_string) {
        HashMap params = new LinkedHashMap();
        params.put("tipo", tipo);
        params.put("tipo_descricao", tipo_descricao);
        params.put("descricao", descricao);
        params.put("numero", numero);
        params.put("data", data);
        params.put("hora_inicial", hora_inicial);
        params.put("hora_final", hora_final);
        params.put("banda", banda);
        params.put("nome_socio", nome_socio);
        params.put("categoria", categoria);
        params.put("local", local);
        params.put("endereco", endereco_string);
        params.put("emissao", DataHoje.dataHoje());
        params.put("hora_emissao", DataHoje.horaMinuto());

        List<ParametroConviteBaile> lista = new ArrayList();

        // PARA MESA - QUANTIDADE DE CADEIRAS
        if (mesaConvite.equals("mesa")) {
            for (int i = 0; i < 4; i++) {
                lista.add(
                        new ParametroConviteBaile(
                                i + 1
                        )
                );
            }
        } else {
            lista.add(
                    new ParametroConviteBaile(
                            -1
                    )
            );
        }
        Jasper.PART_NAME = "";
        Jasper.printReports("CONVITE_BAILE.jasper", tipo_descricao + " n" + numero + "__", lista, params);
    }

    public void trocarMesa(Vector linha) {
        ebmTroca = (EventoBaileMapa) new Dao().find(new EventoBaileMapa(), (Integer) linha.get(9));
        if (Usuario.getUsuario().getId() != 1) {
            if (ebmTroca.getbVenda().getUsuario().getId() != Usuario.getUsuario().getId() && cab.verificaPermissao("trocar_mesa_convite", 3)) {
                GenericaMensagem.fatal("ATENÇÃO", "USUÁRIO SEM PERMISSÃO PARA TROCAR ESTA MESA!");
                PF.update("formPesquisaVendasBaile:message_mesa");
                return;
            }
        }
        ebmTrocaSelecionada = new EventoBaileMapa();
        //Pessoa pv = bv.getPessoa();
        //Pessoa rv = new FunctionsDao().titularDaPessoa(pv.getId());

        loadListaMesa();
        PF.openDialog("dlg_trocar_mesa");
    }

    public void showHistoricoImpressao(Object o, String tcase) {
        if (eventoBaile.getId() == -1) {
            return;
        }
        listaConviteBaileImpressaoConvites = new ArrayList();
        EventoBaileDao ebdb = new EventoBaileDao();
        if (tcase.equals("mesa")) {
            EventoBaileMapa ebm = (EventoBaileMapa) o;
            listaConviteBaileImpressaoConvites = ebdb.listaEventoBaileImpressaoConvite(ebm.getId(), tcase);
        } else {
            EventoBaileConvite ebc = (EventoBaileConvite) o;
            listaConviteBaileImpressaoConvites = ebdb.listaEventoBaileImpressaoConvite(ebc.getId(), tcase);
        }
    }

    public void selecionarTrocarMesa(EventoBaileMapa ebm) {
        ebmTrocaSelecionada = ebm;
    }

    public void salvarTrocarMesa() {
        if (ebmTrocaSelecionada.getId() == -1) {
            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UMA MESA PARA TROCA!");
            PF.update("formPesquisaVendasBaile:panel_trocar_mesa");
            return;
        }
        if (ebmTroca.getId() != -1) {
            Movimento m = ebmTroca.getMovimento();
            BVenda v = ebmTroca.getbVenda();

            Dao dao = new Dao();

            dao.openTransaction();

            ebmTroca.setMovimento(null);
            ebmTroca.setbVenda(null);
            if (!dao.update(ebmTroca)) {
                dao.rollback();
                ebmTroca = new EventoBaileMapa();
                ebmTrocaSelecionada = new EventoBaileMapa();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO TROCAR MESA, TENTE NOVAMENTE!");
                PF.update("formPesquisaVendasBaile:panel_trocar_mesa");
                return;
            }

            ebmTrocaSelecionada.setMovimento(m);
            ebmTrocaSelecionada.setbVenda(v);

            if (!dao.update(ebmTrocaSelecionada)) {
                dao.rollback();
                ebmTroca = new EventoBaileMapa();
                ebmTrocaSelecionada = new EventoBaileMapa();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR MESA, TENTE NOVAMENTE!");
                PF.update("formPesquisaVendasBaile:panel_trocar_mesa");
                return;
            }

            dao.commit();

            GenericaMensagem.info("SUCESSO", "MESA TROCADA DE: " + (ebmTroca.getMesa() < 10 ? "0" + ebmTroca.getMesa() : ebmTroca.getMesa()) + " PARA: " + (ebmTrocaSelecionada.getMesa() < 10 ? "0" + ebmTrocaSelecionada.getMesa() : ebmTrocaSelecionada.getMesa()));
            ebmTroca = new EventoBaileMapa();
            ebmTrocaSelecionada = new EventoBaileMapa();

            loadListaVendasMesa();
            PF.closeDialog("dlg_trocar_mesa");
            PF.update("formPesquisaVendasBaile:message_mesa");
            PF.update("formPesquisaVendasBaile:table_mesa");
        }
    }

    public void trocarConvite(Vector linha) {
        ebcTroca = (EventoBaileConvite) new Dao().find(new EventoBaileConvite(), (Integer) linha.get(9));
        if (Usuario.getUsuario().getId() != 1) {
            if (ebcTroca.getbVenda().getUsuario().getId() != Usuario.getUsuario().getId() && cab.verificaPermissao("trocar_mesa_convite", 3)) {
                GenericaMensagem.fatal("ATENÇÃO", "USUÁRIO SEM PERMISSÃO PARA TROCAR ESTE CONVITE!");
                PF.update("formPesquisaVendasBaile:message_convite");
                return;
            }
        }

        ebcTrocaSelecionada = new EventoBaileConvite();
        //Pessoa pv = bv.getPessoa();
        //Pessoa rv = new FunctionsDao().titularDaPessoa(pv.getId());

        loadListaConvite();
        PF.openDialog("dlg_trocar_convite");
    }

    public void selecionarTrocarConvite(EventoBaileConvite ebc) {
        ebcTrocaSelecionada = ebc;
    }

    public void salvarTrocarConvite() {
        if (ebcTrocaSelecionada.getId() == -1) {
            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UM CONVITE PARA TROCA!");
            PF.update("formPesquisaVendasBaile:panel_trocar_convite");
            return;
        }

        if (ebcTroca.getId() != -1) {
            Movimento m = ebcTroca.getMovimento();
            BVenda v = ebcTroca.getbVenda();

            Dao dao = new Dao();

            dao.openTransaction();

            ebcTroca.setMovimento(null);
            ebcTroca.setbVenda(null);
            if (!dao.update(ebcTroca)) {
                dao.rollback();
                ebcTroca = new EventoBaileConvite();
                ebcTrocaSelecionada = new EventoBaileConvite();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO TROCAR CONVITE, TENTE NOVAMENTE!");
                PF.update("formPesquisaVendasBaile:panel_trocar_convite");
                return;
            }

            ebcTrocaSelecionada.setMovimento(m);
            ebcTrocaSelecionada.setbVenda(v);

            if (!dao.update(ebcTrocaSelecionada)) {
                dao.rollback();
                ebcTroca = new EventoBaileConvite();
                ebcTrocaSelecionada = new EventoBaileConvite();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR CONVITE, TENTE NOVAMENTE!");
                PF.update("formPesquisaVendasBaile:panel_trocar_convite");
                return;
            }

            dao.commit();

            GenericaMensagem.info("SUCESSO", "CONVITE TROCADO DE: " + (ebcTroca.getConvite() < 10 ? "0" + ebcTroca.getConvite() : ebcTroca.getConvite()) + " PARA: " + (ebcTrocaSelecionada.getConvite() < 10 ? "0" + ebcTrocaSelecionada.getConvite() : ebcTrocaSelecionada.getConvite()));
            ebcTroca = new EventoBaileConvite();
            ebcTrocaSelecionada = new EventoBaileConvite();

            loadListaVendasConvite();
            PF.closeDialog("dlg_trocar_convite");
            PF.update("formPesquisaVendasBaile:message_convite");
            PF.update("formPesquisaVendasBaile:table_convite");
        }
    }

    public void gravarVendaLog(BVenda v, Boolean update_log) {
        NovoLog logs = new NovoLog();
        if (!update_log) {
            logs.setTabela("eve_venda");
            logs.setCodigo(v.getId());
            logs.save(
                    "ID: " + v.getId() + " \n "
                    + " - Pessoa ID: " + v.getPessoa().getId() + " \n "
                    + " - Pessoa Nome: " + v.getPessoa().getNome() + " \n "
                    + " - Responsável ID: " + v.getResponsavel().getId() + " \n "
                    + " - Responsável Nome: " + v.getResponsavel().getNome() + " \n "
                    + " - Evento: " + v.getEvento().getDescricaoEvento().getDescricao() + " \n "
                    + " - Serviço: " + v.getEventoServico().getServicos().getDescricao() + " \n "
                    + " - Status: " + v.getStatus().getDescricao() + " \n "
                    + " - Valor Unitário: " + v.getValorUnitarioString() + " \n "
                    + " - Desconto Unitário: " + v.getDescontoUnitarioString() + " \n "
                    + " - Observação: " + v.getObs()
            );
        } else {
            Dao dao = new Dao();
            BVenda v_update = (BVenda) dao.find(new BVenda(), v.getId());
            String antes
                    = "ID: " + v_update.getId() + " \n "
                    + " - Pessoa ID: " + v_update.getPessoa().getId() + " \n "
                    + " - Pessoa Nome: " + v_update.getPessoa().getNome() + " \n "
                    + " - Responsável ID: " + v_update.getResponsavel().getId() + " \n "
                    + " - Responsável Nome: " + v_update.getResponsavel().getNome() + " \n "
                    + " - Evento: " + v_update.getEvento().getDescricaoEvento().getDescricao() + " \n "
                    + " - Serviço: " + v_update.getEventoServico().getServicos().getDescricao() + " \n "
                    + " - Status: " + v_update.getStatus().getDescricao() + " \n "
                    + " - Valor Unitário: " + v_update.getValorUnitarioString() + " \n "
                    + " - Desconto Unitário: " + v_update.getDescontoUnitarioString() + " \n "
                    + " - Observação: " + v_update.getObs();

            logs.setTabela("eve_venda");
            logs.setCodigo(v.getId());
            logs.update(
                    antes,
                    "ID: " + v.getId() + " \n "
                    + " - Pessoa ID: " + v.getPessoa().getId() + " \n "
                    + " - Pessoa Nome: " + v.getPessoa().getNome() + " \n "
                    + " - Responsável ID: " + v.getResponsavel().getId() + " \n "
                    + " - Responsável Nome: " + v.getResponsavel().getNome() + " \n "
                    + " - Evento: " + v.getEvento().getDescricaoEvento().getDescricao() + " \n "
                    + " - Serviço: " + v.getEventoServico().getServicos().getDescricao() + " \n "
                    + " - Status: " + v.getStatus().getDescricao() + " \n "
                    + " - Valor Unitário: " + v.getValorUnitarioString() + " \n "
                    + " - Desconto Unitário: " + v.getDescontoUnitarioString() + " \n "
                    + " - Observação: " + v.getObs()
            );
        }
    }

    public String pesquisaVendas() throws IOException {
        loadListaVendasMesa();
        loadListaVendasConvite();

        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pagina("pesquisaVendasBaile");
    }

    public void adicionarDesconto() {
        desconto = novoDesconto;
        PF.closeDialog("dlg_autentica_usuario");
        PF.closeDialog("dlg_desconto");
        novoDesconto = "0,00";

        calculoDesconto();

        PF.update("formVendasBaile");
        PF.update("formVendasBaileTbl");
    }

    public void autorizarDesconto() {
        GenericaSessao.put("AutenticaUsuario", new AutenticaUsuario("dlg_desconto", "descontoVendasBaile", 3));
    }

    public void loadListaVendasMesa() {
        listaVendasMesa = new ArrayList();
        VendaBaileDao dao = new VendaBaileDao();
        listaVendasMesa = dao.listaVendasMesa(eventoBaile.getEvento().getId());
    }

    public void loadListaVendasConvite() {
        listaVendasConvite = new ArrayList();
        VendaBaileDao dao = new VendaBaileDao();
        listaVendasConvite = dao.listaVendasConvite(eventoBaile.getEvento().getId());
    }

    public void somaValor() {
        if (eventoServicoValor != null) {
            valor = Moeda.converteR$Double(eventoServicoValor.getValor());
            if (pessoa.getId() != -1) {

                if (eventoServicoValor.getDescontoPrimeiraVenda() != null && eventoServicoValor.getDescontoPrimeiraVenda() != 0) {
                    if (pessoa.getSocios().getId() != -1) {
                        List list = new BVendaDao().findBy(pessoa.getId(), eventoServicoValor.getEventoServico().getEvento().getId());
                        if (list.isEmpty()) {
                            descontoPrimeiraVenda = eventoServicoValor.getDescontoPrimeiraVenda();
                        }
                    }
                }
            }
            if (venda.getId() != -1) {
                desconto = venda.getDescontoUnitarioString();
            }
        }
        calculoDesconto();
    }

    public void calculoDesconto() {
        if (Moeda.converteUS$(valor) <= 0) {
            desconto = "0,00";
        }
        valorLiquido = Moeda.converteR$Double(Moeda.converteUS$(valor) - Moeda.converteUS$(desconto));
        total = Moeda.converteR$Double(Moeda.converteUS$(valorLiquido) * getQuantidade());
        if (descontoPrimeiraVenda != 0 && Moeda.converteUS$(total) > 0) {
            total = Moeda.converteR$Double(Moeda.converteUS$(total) - descontoPrimeiraVenda);
            if (!tipoVenda.equals("reserva")) {
                GenericaMensagem.info("Valor promocional", "APLICADO DESCONTO PROMOCIONAL DE R$" + eventoServicoValor.getDescontoPrimeiraVendaString() + " PARA PRIMEIRA COMPRA NESTE EVENTO!");
            }
        }
    }

    public void loadListaServicos() {
        listaServicos = new ArrayList();
        VendaBaileDao dao = new VendaBaileDao();
        if (!listaEventoBaile.isEmpty()) {
            EventoBaile eb = (EventoBaile) new Dao().find(new EventoBaile(), Integer.valueOf(listaEventoBaile.get(indexEventoBaile).getDescription()));
            List<EventoServicoValor> result = new ArrayList();
            if (pessoa.getId() == -1) {
                eventoServicoValor = new EventoServicoValor();
                descricaoServico = "NENHUM SERVIÇO CORRESPONDENTE";
            } else {
                String sx = "A";
                Integer idade = 0, id_categoria = null;

                if (pessoa.getFisica() != null && pessoa.getFisica().getId() != -1) {
                    DataHoje dh = new DataHoje();
                    sx = pessoa.getFisica().getSexo();
                    idade = dh.calcularIdade(pessoa.getFisica().getDtNascimento());
                }

                if (pessoa.getSocios() != null && pessoa.getSocios().getId() != -1) {
                    id_categoria = pessoa.getSocios().getMatriculaSocios().getCategoria().getId();
                }

                Integer id_servicos = 0;
                if (mesaConvite.equals("mesa")) {
                    if (chkCortesia) {
                        id_servicos = 13;
                        id_categoria = null;
                    } else {
                        id_servicos = 12;
                    }
                } else if (chkCortesia) {
                    id_servicos = 15;
                    id_categoria = null;
                } else {
                    id_servicos = 14;
                }

                eventoServicoValor = dao.pesquisaEventoServicoValor(
                        eb.getEvento().getId(),
                        id_categoria,
                        sx,
                        idade,
                        id_servicos,
                        mesaConvite.equals("mesa")
                );

                if (eventoServicoValor != null) {
                    String dc = eventoServicoValor.getEventoServico().isSocio() ? eventoServicoValor.getEventoServico().getCategoria().getCategoria() + " - " : "";
                    String ds = eventoServicoValor.getEventoServico().getServicos().getDescricao();// + " - " + descricaoCategoria + result.get(i).getIdadeInicial() + " à " + result.get(i).getIdadeFinal() + "(" + result.get(i).getSexo() + ") R$ " + result.get(i).getValorString();
                    descricaoServico = dc + ds;
                } else {
                    eventoServicoValor = new EventoServicoValor();
                    descricaoServico = "SERVIÇO VALOR NÃO ENCONTRADO";
                }
            }
        }

        zeraValores();
        somaValor();
    }

    public void updateTipoVenda() {
        loadListaTipoVenda();
        loadListaServicos();
        loadListaMesa();
        loadListaConvite();
        zeraValores();
        somaValor();
        eventoBaile = (EventoBaile) new Dao().find(new EventoBaile(), Integer.valueOf(listaEventoBaile.get(indexEventoBaile).getDescription()));
        mesaConvite = "mesa";
        if (eventoBaile.getQuantidadeMesas() > 0) {
            mesaConvite = "mesa";
        } else if (eventoBaile.getQuantidadeMesas() == 0 && eventoBaile.getQuantidadeConvites() > 0) {
            mesaConvite = "convite";
        }
    }

    public void zeraValores() {
        valor = "0,00";
        valorLiquido = "0,00";
        descontoPrimeiraVenda = 0;
        desconto = "0,00";
        total = "0,00";
    }

    public void loadListaMesa() {
        listaMesasBaile = new ArrayList();
        listaMesasBaileSelecionada = (listaMesasBaileSelecionada == null ? new ArrayList() : new ArrayList());
        if (!listaEventoBaile.isEmpty()) {
            EventoBaileDao eventoBaileDB = new EventoBaileDao();
            if ((tipoVenda.equals("venda") || tipoVenda.equals("reserva")) && venda.getId() == -1) {
                listaMesasBaile = (List<EventoBaileMapa>) eventoBaileDB.listaBaileMapaDisponiveis(Integer.parseInt(listaEventoBaile.get(indexEventoBaile).getDescription()), 1, null, null);
            } else if (tipoVenda.equals("reservado")) {
                listaMesasBaile = (List<EventoBaileMapa>) eventoBaileDB.listaBaileMapaDisponiveis(Integer.parseInt(listaEventoBaile.get(indexEventoBaile).getDescription()), 2, pessoa.getId(), venda.getId());
                listaMesasBaileSelecionada.addAll(listaMesasBaile);
            } else if (tipoVenda.equals("vendido")) {
                listaMesasBaile = (List<EventoBaileMapa>) eventoBaileDB.listaBaileMapaDisponiveis(Integer.parseInt(listaEventoBaile.get(indexEventoBaile).getDescription()), 3, pessoa.getId(), venda.getId());
                listaMesasBaileSelecionada.addAll(listaMesasBaile);
            }
        }
    }

    public void loadListaConvite() {
        listaConviteBaile = new ArrayList();
        listaConviteBaileSelecionado = (listaConviteBaileSelecionado == null ? new ArrayList() : new ArrayList());
        if (!listaEventoBaile.isEmpty()) {
            EventoBaileDao eventoBaileDB = new EventoBaileDao();
            if (venda.getId() == -1) {
                listaConviteBaile = (List<EventoBaileConvite>) eventoBaileDB.listaBaileConviteDisponiveis(Integer.parseInt(listaEventoBaile.get(indexEventoBaile).getDescription()), 1, null, null);
            } else {
                listaConviteBaile = (List<EventoBaileConvite>) eventoBaileDB.listaBaileConviteDisponiveis(Integer.parseInt(listaEventoBaile.get(indexEventoBaile).getDescription()), 3, pessoa.getId(), venda.getId());
            }
        }
    }

    public void loadListaEventoBaile() {
        listaEventoBaile = new ArrayList();
        VendaBaileDao dao = new VendaBaileDao();
        if (!todos) {
            startDate = "";
            endDate = "";
        }
        List<EventoBaile> result = dao.listaBaile(todos, DataHoje.converte(startDate), DataHoje.converte(endDate));
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaEventoBaile.add(new SelectItem(
                        i,
                        result.get(i).getEvento().getDescricaoEvento().getDescricao() + " -  "
                        + result.get(i).getDataString() + " - ("
                        + result.get(i).getHoraInicio() + " às  "
                        + result.get(i).getHoraFim() + ")   "
                        + result.get(i).getQuantidadeMesas() + " mesas  / " + result.get(i).getQuantidadeConvites() + " convites",
                        Integer.toString((result.get(i)).getId())
                )
                );
            }
            eventoBaile = (EventoBaile) new Dao().find(new EventoBaile(), Integer.valueOf(listaEventoBaile.get(indexEventoBaile).getDescription())); //result.get(0);
            if (eventoBaile.getQuantidadeMesas() > 0) {
                mesaConvite = "mesa";
            } else if (eventoBaile.getQuantidadeMesas() == 0 && eventoBaile.getQuantidadeConvites() > 0) {
                mesaConvite = "convite";
            }
        }
        loadListaServicos();
    }

    public void loadListaTipoVenda() {
        listaTipoVenda = new ArrayList();
        tipoVenda = "venda";

        if (mesaConvite.equals("mesa")) {
            if (venda.getId() == -1) {
                listaTipoVenda.add(new SelectItem("venda", "VENDA"));
                listaTipoVenda.add(new SelectItem("reserva", "RESERVA"));
            } else if (venda.getStatus().getId() == 2) {
                listaTipoVenda.add(new SelectItem("reservado", "RESERVADO"));
                tipoVenda = "reservado";
            } else {
                listaTipoVenda.add(new SelectItem("vendido", "VENDIDO"));
                tipoVenda = "vendido";
            }
        } else if (venda.getId() == -1) {
            listaTipoVenda.add(new SelectItem("venda", "VENDA"));
        } else {
            listaTipoVenda.add(new SelectItem("vendido", "VENDIDO"));
            tipoVenda = "vendido";
        }
    }

    public void concluir() {
        concluir(false);
    }

    public void concluir(Boolean updateBVenda) {
        if (updateBVenda) {
            if (venda.getId() != -1 && tipoVenda.equals("vendido")) {
                new Dao().update(venda, true);
                GenericaMensagem.info("Sucesso", "OBSERVAÇÃO ATUALIZADA!");
                PF.update("formVendasBaile");
                return;
            }
        }
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Atenção", "Pesquise uma pessoa para salvar esta venda!");
            PF.update("formVendasBaile");
            return;
        }

        if (eventoServicoValor == null || eventoServicoValor.getEventoServico().getId() == -1) {
            GenericaMensagem.error("Erro", "Nenhum serviço encontrado! Para este tipo de venda!!");
            return;
        }

        if (mesaConvite.equals("mesa")) {
            if (listaMesasBaileSelecionada.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Selecione pelo menos uma MESA para reservar!");
                PF.update("formVendasBaile");
                return;
            }

            for (EventoBaileMapa m : listaMesasBaileSelecionada) {
                EventoBaileMapa ebm = new VendaBaileDao().pesquisaMesaDisponivel(m.getId());
                if (ebm.getbVenda() != null && ebm.getbVenda().getId() != venda.getId()) {
                    GenericaMensagem.warn("Atenção", "Mesa " + ebm.getMesa() + " acabou de ser vendida, selecione outra!");
                    loadListaMesa();
                    PF.update("formVendasBaile");
                    return;
                }
            }
        } else {
            if (listaConviteBaileSelecionado.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Selecione pelo menos um CONVITE para vender!");
                PF.update("formVendasBaile");
                return;
            }

            for (EventoBaileConvite c : listaConviteBaileSelecionado) {
                EventoBaileConvite ebc = new VendaBaileDao().pesquisaConviteDisponivel(c.getId());
                if (ebc.getbVenda() != null && ebc.getbVenda().getId() != venda.getId()) {
                    GenericaMensagem.warn("Atenção", "Convite " + ebc.getConvite() + " acabou de ser vendido, selecione outro!");
                    loadListaConvite();
                    PF.update("formVendasBaile");
                    return;
                }
            }
        }

        if (eventoServicoValor.getEventoServico().getServicos().getId() == 13 || eventoServicoValor.getEventoServico().getServicos().getId() == 15) {
            if (pessoa.getSocios() == null || pessoa.getSocios().getId() == -1) {
                if (venda.getObs().isEmpty()) {
                    GenericaMensagem.warn("Atenção", "O campo Observação para cortesia não sócio é obrigatório!");
                    PF.update("formVendasBaile");
                    return;
                } else if (venda.getObs().length() <= 4) {
                    GenericaMensagem.warn("Atenção", "Observação inválida!");
                    PF.update("formVendasBaile");
                    return;
                } else if (venda.getObs().equals("TESTE") || venda.getObs().equals("Teste") || venda.getObs().equals("teste")) {
                    GenericaMensagem.warn("Atenção", "Observação inválida!");
                    PF.update("formVendasBaile");
                    return;
                }
            }
        }

        if (tipoPagamento.equals("debitar")) {
            if (pessoa.getSocios() == null || pessoa.getSocios().getId() == -1) {
                GenericaMensagem.warn("Atenção", "Para fazer um débito a pessoa precisa ser sócia!");
                PF.update("formVendasBaile");
                return;
            }

            if (new FunctionsDao().inadimplente(pessoa.getId())) {
                GenericaMensagem.warn("Atenção", "Pessoas com débitos pendentes!");
                PF.update("formVendasBaile");
                return;
            }
        }

        switch (tipoVenda) {
            case "venda":
            case "reservado":
                PF.openDialog("dlg_venda");
                break;
            case "reserva":
                PF.openDialog("dlg_reserva");
                break;
            case "vendido":
                PF.openDialog("dlg_vendido");
                break;
        }
    }

    public String salvar() {
        Dao dao = new Dao();

        if (eventoServicoValor.getEventoServico() == null || eventoServicoValor.getEventoServico().getId() == -1) {
            GenericaMensagem.error("Erro", "Nenhum serviço encontrado!");
            return null;
        }

        dao.openTransaction();
        EventoBaile eb = (EventoBaile) new Dao().find(new EventoBaile(), Integer.valueOf(listaEventoBaile.get(indexEventoBaile).getDescription()));

        Boolean update_log = false;
        if (venda.getId() == -1) {
            if (chkCortesia && pessoa.getExistOposicao()) {
                GenericaMensagem.warn("Validação", "Não foi possível salvar Venda Cortesia, para cadastro com oposição!");
                dao.rollback();
                return null;
            }
            venda.setEvento(eb.getEvento());
            venda.setPessoa(pessoa);
            venda.setResponsavel(responsavel);
            venda.setUsuario(Usuario.getUsuario());
            venda.setEventoServico(eventoServicoValor.getEventoServico());
            venda.setValorUnitarioString(valor);
            venda.setDescontoUnitarioString(desconto);
            venda.setStatus(null);

            if (!dao.save(venda)) {
                GenericaMensagem.error("Erro", "Não foi possível salvar Venda!");
                dao.rollback();
                return null;
            }
            update_log = false;
        } else {

            venda.setEventoServico(eventoServicoValor.getEventoServico());
            venda.setValorUnitarioString(valor);
            venda.setDescontoUnitarioString(desconto);

            if (!dao.update(venda)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Venda!");
                dao.rollback();
                return null;
            }
            update_log = true;
        }

        switch (tipoVenda) {
            case "venda":
            case "reservado":
                venda.setStatus((AStatus) dao.find(new AStatus(), 3));
                dao.update(venda);

                if (Moeda.converteUS$(valorLiquido) > 0) {
                    if (!gerarMovimento(dao)) {
                        GenericaMensagem.error("Erro", "Não foi possível salvar Movimento!");
                        dao.rollback();
                        return null;
                    }
                }

                if (mesaConvite.equals("mesa")) {
                    for (EventoBaileMapa ebm : listaMesasBaileSelecionada) {
                        //ebm.setStatus((AStatus) dao.find(new AStatus(), 3));
                        ebm.setbVenda(venda);

                        if (!dao.update(ebm)) {
                            GenericaMensagem.error("Erro", "Não foi possível salvar Evento Baile Mapa!");
                            dao.rollback();
                            return null;
                        }
                    }
                } else {
                    for (EventoBaileConvite ebc : listaConviteBaileSelecionado) {
                        ebc.setbVenda(venda);

                        if (!dao.update(ebc)) {
                            GenericaMensagem.error("Erro", "Não foi possível salvar Evento Baile Convite!");
                            dao.rollback();
                            return null;
                        }
                    }
                }

                gravarVendaLog(venda, update_log);

                dao.commit();
                if (tipoPagamento.equals("avista") && !chkCortesia) {
                    return telaBaixa();
                } else {
                    GenericaMensagem.info("Sucesso", "Venda Concluída!");
                    novo(false);
                    editar(venda, false);
                }
                break;
            case "reserva":
                venda.setStatus((AStatus) dao.find(new AStatus(), 2));
                dao.update(venda);

                for (EventoBaileMapa ebm : listaMesasBaileSelecionada) {
                    ebm.setbVenda(venda);

                    if (!dao.update(ebm)) {
                        GenericaMensagem.error("Erro", "Não foi possível salvar Evento Baile Mapa!");
                        dao.rollback();
                        return null;
                    }
                }

                gravarVendaLog(venda, update_log);

                dao.commit();
                GenericaMensagem.info("Sucesso", "Reserva Concluída!");
                novo();
                break;
            case "vendido":
                gravarVendaLog(venda, update_log);
                dao.commit();
                GenericaMensagem.info("Sucesso", "Venda Concluída!");
                novo();
                break;
        }
        return null;
    }

    public String editar(Vector linha) {
        Dao dao = new Dao();
        venda = (BVenda) dao.find(new BVenda(), (Integer) linha.get(0));
        editar(venda);
        GenericaSessao.put("linkClicado", true);
        return (String) GenericaSessao.getString("urlRetorno");
    }

    public void editar(BVenda bv) {
        editar(bv, true);
    }

    public void editar(BVenda bv, Boolean ev) {
        Dao dao = new Dao();
        venda = (BVenda) dao.rebind(venda);
        venda = bv;
        pessoa = venda.getPessoa();
        responsavel = new FunctionsDao().titularDaPessoa(pessoa.getId());
        editarVenda = ev;
        VendaBaileDao vbd = new VendaBaileDao();
        // LISTA EVENTO BAILE
        List<EventoBaileMapa> lm = vbd.listaEventoBaileMapaPorVenda(venda.getId());
        List<EventoBaileConvite> lc = vbd.listaEventoBaileConvitePorVenda(venda.getId());
        EventoBaile eb = new EventoBaile();

        if (!lm.isEmpty()) {
            eb = lm.get(0).getEventoBaile();
            mesaConvite = "mesa";
            loadListaTipoVenda();
            quantidade = lm.size();
            loadListaMesa();
        } else if (!lc.isEmpty()) {
            eb = lc.get(0).getEventoBaile();
            mesaConvite = "convite";
            loadListaTipoVenda();
            quantidade = lc.size();
            loadListaConvite();
        }

        chkCortesia = (venda.getEventoServico().getServicos().getId() == 13 || venda.getEventoServico().getServicos().getId() == 13);

        loadListaEventoBaile();
        for (int i = 0; i < listaEventoBaile.size(); i++) {
            if (Integer.valueOf(listaEventoBaile.get(i).getDescription()) == eb.getId()) {
                indexEventoBaile = i;
                eventoBaile = eb;
                break;
            }
        }

    }

    public void excluir() {
        VendaBaileDao vdao = new VendaBaileDao();
        // LISTA EVENTO BAILE
        List<EventoBaileMapa> lm = vdao.listaEventoBaileMapaPorVenda(venda.getId());
        List<EventoBaileConvite> lc = vdao.listaEventoBaileConvitePorVenda(venda.getId());
        editarVenda = false;
        Dao dao = new Dao();
        dao.openTransaction();
        List<Movimento> listm = new ArrayList();
        String ids_convite = "", ids_mesa = "";
        if (!lm.isEmpty()) {
            for (EventoBaileMapa ebm : lm) {
                //ebm.setStatus((AStatus) dao.find(new AStatus(), 1));
                ebm.setbVenda(null);

                if (ebm.getMovimento() != null) {
                    listm.add(ebm.getMovimento());
                    ebm.setMovimento(null);
                }

                if (!dao.update(ebm)) {
                    GenericaMensagem.error("Erro", "Não foi possível excluir Evento Baile Mapa!");
                    dao.rollback();
                    return;
                }

                if (ids_mesa.isEmpty()) {
                    ids_mesa = "" + ebm.getMesa();
                } else {
                    ids_mesa += ", " + ebm.getMesa();
                }
            }
            if (!listm.isEmpty()) {
                if (!excluirMovimento(listm, dao)) {
                    GenericaMensagem.error("Erro", "Não foi possível excluir Movimentos!");
                    dao.rollback();
                    return;
                }
            }
        } else if (!lc.isEmpty()) {
            for (EventoBaileConvite ebc : lc) {
                //ebc.setStatus((AStatus) dao.find(new AStatus(), 1));
                ebc.setbVenda(null);

                if (ebc.getMovimento() != null) {
                    listm.add(ebc.getMovimento());
                    ebc.setMovimento(null);
                }

                if (!dao.update(ebc)) {
                    GenericaMensagem.error("Erro", "Não foi possível excluir Evento Baile Convite!");
                    dao.rollback();
                    return;
                }

                if (ids_convite.isEmpty()) {
                    ids_convite = "" + ebc.getConvite();
                } else {
                    ids_convite += ", " + ebc.getConvite();
                }
            }

            if (!listm.isEmpty()) {
                if (!excluirMovimento(listm, dao)) {
                    GenericaMensagem.error("Erro", "Não foi possível excluir Movimentos!");
                    dao.rollback();
                    return;
                }
            }
        }

        if (!dao.delete(dao.find(venda))) {
            GenericaMensagem.error("Erro", "Não foi possível excluir Venda!");
            dao.rollback();
            return;
        }

        String ids_movimento = "";
        for (Movimento m : listm) {
            if (ids_movimento.isEmpty()) {
                ids_movimento = "" + m.getId();
            } else {
                ids_movimento += ", " + m.getId();
            }
        }
        NovoLog logs = new NovoLog();

        logs.setTabela("eve_venda");
        logs.setCodigo(venda.getId());
        logs.delete(
                "ID: " + venda.getId()
                + " - Pessoa ID: " + venda.getPessoa().getId()
                + " - Pessoa Nome: " + venda.getPessoa().getNome()
                + " - Responsável ID: " + venda.getResponsavel().getId()
                + " - Responsável Nome: " + venda.getResponsavel().getNome()
                + " - Evento: " + venda.getEvento().getDescricaoEvento().getDescricao()
                + " - Serviço: " + venda.getEventoServico().getServicos().getDescricao()
                + " - Status: " + venda.getStatus().getDescricao()
                + " - Valor Unitário: " + venda.getValorUnitarioString()
                + " - Desconto Unitário: " + venda.getDescontoUnitarioString()
                + " - Observação: " + venda.getObs()
                + " - CONVITES: { " + ids_convite + " }"
                + " - MESAS: { " + ids_mesa + " }"
                + " - MOVIMENTOS: { " + ids_movimento + " }"
        );

        dao.commit();
        GenericaSessao.remove("vendaBaileBean");
        GenericaMensagem.info("Sucesso", "Venda Excluida!");
    }

    public boolean excluirMovimento(List<Movimento> listm, Dao dao) {
        for (Movimento m : listm) {
            if (m.getBaixa() != null) {
                GenericaMensagem.error("Erro", "Existem movimentos pagos, venda não pode ser excluída!");
                return false;
            }
        }

        return GerarMovimento.inativarArrayMovimento(listm, "EXCLUSÃO DE VENDA BAILE", dao).isEmpty();
    }

    public String novo() {
        novo(true);
        return "vendasBaile";
    }

    public void novo(Boolean newVenda) {
        if (newVenda) {
            venda = new BVenda();
        }
        listaMesasBaile = new ArrayList();
        listaMesasBaileSelecionada = new ArrayList();
        listaConviteBaile = new ArrayList();
        listaConviteBaileSelecionado = new ArrayList();
        listaTipoVenda = new ArrayList();

        pessoa = new Pessoa();
        todos = false;

        mesaConvite = "mesa";
        tipoVenda = "venda";
        editarVenda = false;
        idServicos = 0;
        listaServicos = new ArrayList();

        valor = "0,00";
        desconto = "0,00";
        valorLiquido = "0,00";
        total = "0,00";
        quantidade = 0;

        listaVendasMesa = new ArrayList();
        listaVendasConvite = new ArrayList();
        cab = new ControleAcessoBean();
        novoDesconto = "0,00";
        tipoPagamento = "avista";

        responsavel = new Pessoa();
        eventoServicoValor = new EventoServicoValor();
        descricaoServico = "NENHUM SERVIÇO CORRESPONDENTE";
        chkCortesia = false;

        listaMovimento = new ArrayList();

        if (newVenda) {
            init();
        }

    }

    public String telaBaixa() {
        List lista = new ArrayList();
        Movimento movimento;
        MacFilial macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");

        if (macFilial == null) {
            GenericaMensagem.warn("Atenção", "Não existe filial na sessão!");
            return null;
        }

        if (!macFilial.getCaixaOperador()) {
            if (macFilial.getCaixa() == null) {
                GenericaMensagem.warn("Atenção", "Configurar Caixa nesta estação de trabalho!");
                return null;
            }
        } else {
            FinanceiroDao dao = new FinanceiroDao();
            Caixa caixax = dao.pesquisaCaixaUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId(), macFilial.getFilial().getId());

            if (caixax == null) {
                GenericaMensagem.warn("Erro", "Configurar Caixa para este Operador!");
                return null;
            }
        }

        if (!chkCortesia) {
            if (!listaMovimento.isEmpty()) {
                for (int i = 0; i < listaMovimento.size(); i++) {
                    movimento = (Movimento) listaMovimento.get(i);

                    movimento.setMulta(listaMovimento.get(i).getMulta());
                    movimento.setJuros(listaMovimento.get(i).getJuros());
                    movimento.setCorrecao(listaMovimento.get(i).getCorrecao());
                    movimento.setDesconto(listaMovimento.get(i).getDesconto());

                    movimento.setValor(listaMovimento.get(i).getValor());
                    movimento.setValorBaixa(listaMovimento.get(i).getValor());

                    lista.add(movimento);
                }

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);

                GenericaSessao.put("caixa_banco", "caixa");
                GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
                return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();

            } else {
                GenericaMensagem.warn("Atenção", "Lista de Movimentos Vazia!");
            }
        }
        return null;
    }

    public boolean gerarMovimento(Dao dao) {
        PessoaComplemento pc = responsavel.getPessoaComplemento();
        DataHoje dh = new DataHoje();
        CondicaoPagamento cp = tipoPagamento.equals("avista") ? (CondicaoPagamento) new Dao().find(new CondicaoPagamento(), 1) : (CondicaoPagamento) new Dao().find(new CondicaoPagamento(), 2);
        String vencimento = tipoPagamento.equals("avista") ? DataHoje.data() : (pc.getNrDiaVencimento() < 10) ? "0" + pc.getNrDiaVencimento() : "" + pc.getNrDiaVencimento() + "/" + dh.incrementarMeses(1, DataHoje.data()).substring(3);
        listaMovimento = new ArrayList();

//        Evt evt = new Evt();
//        if (!dao.save(evt)) {
//            GenericaMensagem.error("Error", "Nao foi possivel salvar EVT!");
//            return false;
//        }
        EventoBaile eb = (EventoBaile) new Dao().find(new EventoBaile(), Integer.valueOf(listaEventoBaile.get(indexEventoBaile).getDescription()));

        Lote l = new Lote(
                -1,
                (Rotina) new Dao().find(new Rotina(), 141),
                "R",
                DataHoje.data(),
                responsavel,
                venda.getEventoServico().getServicos().getPlano5(),
                false,
                "",
                Moeda.converteUS$(total),
                (Filial) new Dao().find(new Filial(), 1),
                venda.getEventoServico().getServicos().getDepartamento(),
                eb.getEvt(),
                "",
                (FTipoDocumento) new Dao().find(new FTipoDocumento(), 13),
                cp,
                (FStatus) new Dao().find(new FStatus(), 1),
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

        if (!dao.save(l)) {
            GenericaMensagem.error("Error", "Nao foi possivel salvar Lote!");
            return false;
        }

        int index = 0;
        if (mesaConvite.equals("mesa")) {
            for (EventoBaileMapa mb : listaMesasBaileSelecionada) {
                Movimento m = movimento(l, vencimento, eb, index);
                if (!dao.save(m)) {
                    GenericaMensagem.error("Error", "Nao foi possivel salvar Movimento!");
                    listaMovimento = new ArrayList();
                    return false;
                }
                listaMovimento.add(m);

                mb.setMovimento(m);
                if (!dao.update(mb)) {
                    GenericaMensagem.error("Error", "Nao foi possivel alterar Evento Mesa Baile!");
                    listaMovimento = new ArrayList();
                    return false;
                }
                index++;

            }
        } else {
            for (EventoBaileConvite cb : listaConviteBaileSelecionado) {
                Movimento m = movimento(l, vencimento, eb, index);
                if (!dao.save(m)) {
                    GenericaMensagem.error("Error", "Nao foi possivel salvar Movimento!");
                    listaMovimento = new ArrayList();
                    return false;
                }
                listaMovimento.add(m);

                cb.setMovimento(m);
                if (!dao.update(cb)) {
                    GenericaMensagem.error("Error", "Nao foi possivel alterar Evento Mesa Convite!");
                    listaMovimento = new ArrayList();
                    return false;
                }
                index++;
            }
        }
        return true;
    }

    public Movimento movimento(Lote l, String vencimento, EventoBaile eb, int index) {
        double vl = Moeda.converteUS$(valorLiquido);
        if (descontoPrimeiraVenda > 0) {
            if (index == 0) {
                vl = vl - descontoPrimeiraVenda;
            }
        }
        return new Movimento(
                -1,
                l,
                l.getPlano5(),
                pessoa,
                venda.getEventoServico().getServicos(),
                null, // ID_BAIXA
                (TipoServico) new Dao().find(new TipoServico(), 1),
                null,
                vl,
                DataHoje.converteDataParaReferencia(eb.getDataString()),
                vencimento,
                1,
                true,
                "E",
                false,
                responsavel,
                pessoa,
                "",
                "",
                vencimento,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                (FTipoDocumento) new Dao().find(new FTipoDocumento(), 13),
                0,
                null
        );
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
        responsavel = new Pessoa();
        loadListaServicos();
        loadListaMesa();
        loadListaConvite();
        zeraValores();
    }

    public void pesquisaNaoSocio() {
        pessoa = (Pessoa) new Dao().find(new Pessoa(), 1);
        responsavel = pessoa;
        loadListaServicos();
    }

    public String converteMoeda(double valorx) {
        return Moeda.converteR$Double(valorx);
    }

    public String converteData(Date datax) {
        return DataHoje.converteData(datax);
    }

    public List<SelectItem> getListaEventoBaile() {
        return listaEventoBaile;
    }

    public int getIndexEventoBaile() {
        return indexEventoBaile;
    }

    public void setIndexEventoBaile(int indexEventoBaile) {
        this.indexEventoBaile = indexEventoBaile;
    }

    public BVenda getVenda() {
        return venda;
    }

    public void setVenda(BVenda venda) {
        this.venda = venda;
    }

    public Boolean getTodos() {
        return todos;
    }

    public void setTodos(Boolean todos) {
        if (!todos) {
            startDate = "";
            endDate = "";
        }
        this.todos = todos;
    }

    public String getMesaConvite() {
        return mesaConvite;
    }

    public void setMesaConvite(String mesaConvite) {
        this.mesaConvite = mesaConvite;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            responsavel = new FunctionsDao().titularDaPessoa(pessoa.getId());
            loadListaServicos();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(String tipoVenda) {
        this.tipoVenda = tipoVenda;
    }

    public List<SelectItem> getListaTipoVenda() {
        return listaTipoVenda;
    }

    public void setListaTipoVenda(List<SelectItem> listaTipoVenda) {
        this.listaTipoVenda = listaTipoVenda;
    }

    public List<EventoBaileMapa> getListaMesasBaileSelecionada() {
        return listaMesasBaileSelecionada;
    }

    public void setListaMesasBaileSelecionada(List<EventoBaileMapa> listaMesasBaileSelecionada) {
        this.listaMesasBaileSelecionada = listaMesasBaileSelecionada;
    }

    public List<EventoBaileMapa> getListaMesasBaile() {
        return listaMesasBaile;
    }

    public void setListaMesasBaile(List<EventoBaileMapa> listaMesasBaile) {
        this.listaMesasBaile = listaMesasBaile;
    }

    public Integer getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(Integer idServicos) {
        this.idServicos = idServicos;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDesconto() {
        return Moeda.converteR$(desconto);
    }

    public void setDesconto(String desconto) {
        this.desconto = Moeda.converteR$(desconto);
    }

    public String getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(String valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Integer getQuantidade() {
        if (mesaConvite.equals("mesa")) {
            if (listaMesasBaileSelecionada != null) {
                quantidade = listaMesasBaileSelecionada.size();
            } else {
                quantidade = 0;
            }
        } else if (listaConviteBaileSelecionado != null) {
            quantidade = listaConviteBaileSelecionado.size();
        } else {
            quantidade = 0;
        }
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public List<List> getListaVendasMesa() {
        return listaVendasMesa;
    }

    public void setListaVendasMesa(List<List> listaVendasMesa) {
        this.listaVendasMesa = listaVendasMesa;
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

    public List<EventoBaileConvite> getListaConviteBaile() {
        return listaConviteBaile;
    }

    public void setListaConviteBaile(List<EventoBaileConvite> listaConviteBaile) {
        this.listaConviteBaile = listaConviteBaile;
    }

    public List<EventoBaileConvite> getListaConviteBaileSelecionado() {
        return listaConviteBaileSelecionado;
    }

    public void setListaConviteBaileSelecionado(List<EventoBaileConvite> listaConviteBaileSelecionado) {
        this.listaConviteBaileSelecionado = listaConviteBaileSelecionado;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public EventoServicoValor getEventoServicoValor() {
        return eventoServicoValor;
    }

    public void setEventoServicoValor(EventoServicoValor eventoServicoValor) {
        this.eventoServicoValor = eventoServicoValor;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public boolean isChkCortesia() {
        return chkCortesia;
    }

    public void setChkCortesia(boolean chkCortesia) {
        this.chkCortesia = chkCortesia;
    }

    public EventoBaile getEventoBaile() {
        return eventoBaile;
    }

    public void setEventoBaile(EventoBaile eventoBaile) {
        this.eventoBaile = eventoBaile;
    }

    public List<List> getListaVendasConvite() {
        return listaVendasConvite;
    }

    public void setListaVendasConvite(List<List> listaVendasConvite) {
        this.listaVendasConvite = listaVendasConvite;
    }

    public EventoBaileMapa getEbmTroca() {
        return ebmTroca;
    }

    public void setEbmTroca(EventoBaileMapa ebmTroca) {
        this.ebmTroca = ebmTroca;
    }

    public EventoBaileMapa getEbmTrocaSelecionada() {
        return ebmTrocaSelecionada;
    }

    public void setEbmTrocaSelecionada(EventoBaileMapa ebmTrocaSelecionada) {
        this.ebmTrocaSelecionada = ebmTrocaSelecionada;
    }

    public EventoBaileConvite getEbcTroca() {
        return ebcTroca;
    }

    public void setEbcTroca(EventoBaileConvite ebcTroca) {
        this.ebcTroca = ebcTroca;
    }

    public EventoBaileConvite getEbcTrocaSelecionada() {
        return ebcTrocaSelecionada;
    }

    public void setEbcTrocaSelecionada(EventoBaileConvite ebcTrocaSelecionada) {
        this.ebcTrocaSelecionada = ebcTrocaSelecionada;
    }

    public double getDescontoPrimeiraVenda() {
        return descontoPrimeiraVenda;
    }

    public void setDescontoPrimeiraVenda(double descontoPrimeiraVenda) {
        this.descontoPrimeiraVenda = descontoPrimeiraVenda;
    }

    public Boolean getEditarVenda() {
        return editarVenda;
    }

    public void setEditarVenda(Boolean editarVenda) {
        this.editarVenda = editarVenda;
    }

    public List<EventoBaileImpressaoConvite> getListaConviteBaileImpressaoConvites() {
        return listaConviteBaileImpressaoConvites;
    }

    public void setListaConviteBaileImpressaoConvites(List<EventoBaileImpressaoConvite> listaConviteBaileImpressaoConvites) {
        this.listaConviteBaileImpressaoConvites = listaConviteBaileImpressaoConvites;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
