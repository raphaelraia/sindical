package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.dao.RemessaDao;
import br.com.rtools.associativo.dao.ExtratoTelaSocialDao;
import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.cobranca.BoletoRemessa;
import br.com.rtools.cobranca.RespostaArquivoRemessa;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.StatusRemessa;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.RemessaBancoDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
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
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class ExtratoTelaSocialBean implements Serializable {

    private String porPesquisa = "todos";
    private String ordenacao = "referencia";
    private String tipoDataPesquisa = "ocorrencia";
    private String tipoPessoa = "nenhum";
    private List<DataObject> listaMovimento = new ArrayList();
    private Pessoa pessoa = new Pessoa();
    private String dataInicial = "";
    private String dataFinal = "";
    private String dataRefInicial = "";
    private String dataRefFinal = "";
    private String boletoInicial = "";
    private String boletoFinal = "";
    private Integer idServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();
    private Integer idTipoServico = 0;
    private List<SelectItem> listaTipoServico = new ArrayList();
    private boolean imprimirVerso = false;
    private String tipoEnvio = "responsavel";
    private String historico = "";
    private String vlRecebido = "0,00";
    private String vlNaoRecebido = "0,00";
    private String vlTaxa = "0,00";
    private String vlTotal = "0,00";
    private String vlLiquido = "0,00";
    private ControleAcessoBean cab = new ControleAcessoBean();

    private String motivoEstorno = "";

    private Integer indexListaStatusRetorno = 0;
    private final List<SelectItem> listaStatusRetorno = new ArrayList();

    private Boolean selecionaTodos = false;
    private Boolean visibleModalRemessa = false;

    private List<BoletoRemessa> listaBoletoRemessa = new ArrayList();
    private List<SelectItem> listaConta = new ArrayList();
    private Integer indexConta = 0;
    private ContaCobranca contaSelecionada = new ContaCobranca();
    private Boolean verListaRemessa = false;
    private StatusRemessa statusRemessa = new StatusRemessa();
    private String id_boleto_adicionado_remessa = "";
    private String tipoData = "faixa";
    private List<SelectItem> listaFilial = new ArrayList();
    private Integer idFilial = null;
    private Boolean apenasBoletoSelecionado = false;

    @PostConstruct
    public void init() {
        loadListaServicos();
        loadListaTipoServico();

        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");

        loadListaStatusRetorno();

        loadListaContas();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("extratoTelaSocialBean");
    }

    public final void loadListaContas() {
        listaConta.clear();
        indexConta = 0;

        ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
        List<ContaCobranca> result = servDB.listaContaCobrancaAtivoAssociativo();

        listaConta.add(new SelectItem(0, "SELECIONAR UMA CONTA", "0"));

        Integer contador = 1;
        for (int i = 0; i < result.size(); i++) {
            listaConta.add(
                    new SelectItem(
                            contador,
                            result.get(i).getApelido() + " - " + result.get(i).getCodCedente() + " - " + result.get(i).getContaBanco().getBanco().getBanco(),
                            Integer.toString(result.get(i).getId())
                    )
            );
            contador++;
        }

        if (Integer.valueOf(listaConta.get(indexConta).getDescription()) != 0) {
            contaSelecionada = (ContaCobranca) new Dao().find(new ContaCobranca(), Integer.valueOf(listaConta.get(indexConta).getDescription()));
        } else {
            contaSelecionada = new ContaCobranca();
        }
    }

    public void alterarContaRemessa() {
        if (Integer.valueOf(listaConta.get(indexConta).getDescription()) != 0) {
            contaSelecionada = (ContaCobranca) new Dao().find(new ContaCobranca(), Integer.valueOf(listaConta.get(indexConta).getDescription()));
        } else {
            contaSelecionada = new ContaCobranca();
        }

        loadLista();
    }

    public List<Boleto> validaListaRemessa(String opcao) {
        List<Boleto> lista_boleto_validado = new ArrayList();
        if (!listaMovimento.isEmpty()) {

            List<Boleto> lista_boleto = new ArrayList();
            Map<Integer, Boleto> hash = new LinkedHashMap();

            for (DataObject datao : listaMovimento) {
                // SE ESTA SELECIONADO E NÃO FOR QUITADO
                if ((Boolean) datao.getArgumento0() && ((Vector) datao.getArgumento1()).get(15) != null) {
                    Boleto boletox = (Boleto) new Dao().find(new Boleto(), ((Vector) datao.getArgumento1()).get(15));
                    hash.put(boletox.getId(), boletox);
                }
            }

            for (Map.Entry<Integer, Boleto> entry : hash.entrySet()) {
                lista_boleto.add(entry.getValue());
            }

            for (Boleto bol : lista_boleto) {
                Pessoa pessoa_boleto = bol.getPessoa();
                // SE O TIPO DE DOCUMENTO É 1 ou 2 (CPF, CNPJ)
                if (pessoa_boleto.getTipoDocumento().getId() != 1 && pessoa_boleto.getTipoDocumento().getId() != 2) {
                    GenericaMensagem.error(pessoa_boleto.getNome(), "TIPO DE DOCUMENTO INVÁLIDO!");
                    return null;
                }

                // SE O DOCUMENTO É VÁLIDO
                // -- CPF
                if (pessoa_boleto.getTipoDocumento().getId() == 1) {
                    if (!ValidaDocumentos.isValidoCPF(pessoa_boleto.getDocumentoSomentoNumeros())) {
                        GenericaMensagem.error(pessoa_boleto.getNome(), "CPF INVÁLIDO!");
                        return null;
                    }
                }
                // -- CNPJ
                if (pessoa_boleto.getTipoDocumento().getId() == 2) {
                    if (!ValidaDocumentos.isValidoCNPJ(pessoa_boleto.getDocumentoSomentoNumeros())) {
                        GenericaMensagem.error(pessoa_boleto.getNome(), "CNPJ INVÁLIDO!");
                        return null;
                    }
                }

                // SE TEM ENDEREÇO
                PessoaEndereco pe = pessoa_boleto.getPessoaEndereco();

                if (pe == null) {
                    GenericaMensagem.error(pessoa_boleto.getNome(), "NÃO CONTÉM ENDEREÇO!");
                    return null;
                }

                String cep = pe.getEndereco().getCep().replace("-", "").replace(".", "");
                if (cep.length() < 8) {
                    GenericaMensagem.error(pessoa_boleto.getNome(), "CEP INVÁLIDO: " + cep);
                    return null;
                }

                if (bol.getStatusRetorno() != null) {
                    switch (opcao) {
                        case "registrar":
                            if (bol.getStatusRetorno().getId() == 2) {
                                GenericaMensagem.error(pessoa_boleto.getNome(), "NÃO PODE REGISTRAR UM BOLETO JÁ REGISTRADO!");
                                return null;
                            }

                            if (bol.getStatusRetorno().getId() == 3) {
                                GenericaMensagem.error(pessoa_boleto.getNome(), "NÃO PODE REGISTRAR UM BOLETO LIQUIDADO!");
                                return null;
                            }

                            break;
                        case "baixar_banco":
                            if (bol.getStatusRetorno().getId() != 2) {
                                GenericaMensagem.error(pessoa_boleto.getNome(), "NÃO PODE BAIXAR NO BANCO UM BOLETO QUE NÃO FOI REGISTRADO!");
                                return null;
                            }
                            break;
                    }
                }

                lista_boleto_validado.add(bol);

            }
            return lista_boleto_validado;
        }

        return lista_boleto_validado;
    }

    public void adicionarRemessa() {
        adicionarRemessa(null);
    }

    public void adicionarRemessa(String opcao) {
        id_boleto_adicionado_remessa = "";

        if (contaSelecionada.getId() == -1) {
            GenericaMensagem.error("ATENÇÃO", "Selecione uma Conta Cobrança para continuar!");
            PF.update("formExtratoTelaSocial");
            return;
        }

        if (opcao == null) {
            PF.openDialog("dlg_opcao_remessa");
            return;
        }

        Dao dao = new Dao();

        List<Boleto> lista_boleto_validado = validaListaRemessa(opcao);
        if (lista_boleto_validado == null) {
            PF.update("formExtratoTelaSocial");
            PF.update("formExtratoTelaSocialOpcoes");
            return;
        }

        switch (opcao) {
            case "registrar":
                statusRemessa = (StatusRemessa) dao.find(new StatusRemessa(), 1);
                break;
            case "baixar_banco":
                statusRemessa = (StatusRemessa) dao.find(new StatusRemessa(), 2);
                break;
        }

        RemessaBancoDao daor = new RemessaBancoDao();
        String ids_pesquisa = "";
        // BOLETOS SELECIONADOS ------------------------------------------------
        // ---------------------------------------------------------------------
        for (Boleto bol : lista_boleto_validado) {
            BoletoRemessa br = new BoletoRemessa(bol, statusRemessa, "tblExtratoTelaT2");
            listaBoletoRemessa.add(br);

            if (id_boleto_adicionado_remessa.isEmpty()) {
                id_boleto_adicionado_remessa = "" + bol.getId();
                ids_pesquisa = "" + bol.getId();
            } else {
                id_boleto_adicionado_remessa += ", " + bol.getId();
                ids_pesquisa = ", " + bol.getId();
            }
        }
        List<RemessaBanco> l_rb;
        if (!ids_pesquisa.isEmpty()) {
            l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 1);
            if (!l_rb.isEmpty()) {
                GenericaMensagem.error("Atenção", "Boleto STATUS: Registrar já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                return;
            }

            l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 2);
            if (!l_rb.isEmpty()) {
                GenericaMensagem.error("Atenção", "Boleto STATUS: Baixar no Banco já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                return;
            }
        }
        // ---------------------------------------------------------------------
        // ---------------------------------------------------------------------

        if (apenasBoletoSelecionado && ids_pesquisa.isEmpty()){
            GenericaMensagem.error("Atenção", "Nenhum Boleto Selecionado!");
            return;
        }
        
        if (!apenasBoletoSelecionado) {
            // REGISTRAR BOLETOS AUTOMÁTICO ----------------------------------------
            // ---------------------------------------------------------------------
            List<Boleto> lista_b = new RemessaDao().listaRegistrarAutomatico(contaSelecionada.getId(), id_boleto_adicionado_remessa);

            statusRemessa = (StatusRemessa) dao.find(new StatusRemessa(), 1);

            ids_pesquisa = "";
            for (Boleto bo : lista_b) {

                BoletoRemessa br = new BoletoRemessa(bo, statusRemessa, "tblExtratoTelaT1");
                listaBoletoRemessa.add(br);

                if (id_boleto_adicionado_remessa.isEmpty()) {
                    id_boleto_adicionado_remessa = "" + bo.getId();
                    ids_pesquisa = "" + bo.getId();
                } else {
                    id_boleto_adicionado_remessa += ", " + bo.getId();
                    ids_pesquisa = ", " + bo.getId();
                }
            }

            if (!ids_pesquisa.isEmpty()) {
                l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 1);

                if (!l_rb.isEmpty()) {
                    GenericaMensagem.error("Atenção", "Boleto STATUS: Registrar já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                    return;
                }
            }
            // ---------------------------------------------------------------------
            // ---------------------------------------------------------------------

            // REGISTRAR BOLETOS RECUSADOS -----------------------------------------
            // ---------------------------------------------------------------------
            lista_b = new RemessaDao().listaRegistrarRecusados(contaSelecionada.getId(), id_boleto_adicionado_remessa);

            ids_pesquisa = "";
            for (Boleto bo : lista_b) {

                BoletoRemessa br = new BoletoRemessa(bo, statusRemessa, "tblExtratoTelaT1");
                listaBoletoRemessa.add(br);

                if (id_boleto_adicionado_remessa.isEmpty()) {
                    id_boleto_adicionado_remessa = "" + bo.getId();
                    ids_pesquisa = "" + bo.getId();
                } else {
                    id_boleto_adicionado_remessa += ", " + bo.getId();
                    ids_pesquisa = ", " + bo.getId();
                }
            }

            if (!ids_pesquisa.isEmpty()) {
                l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 1);
                if (!l_rb.isEmpty()) {
                    GenericaMensagem.error("Atenção", "Boleto STATUS: Registrar já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                    return;
                }
            }
            // ---------------------------------------------------------------------
            // ---------------------------------------------------------------------

            // BAIXAR BOLETOS RECUSADOS --------------------------------------------
            // ---------------------------------------------------------------------
            lista_b = new RemessaDao().listaBaixarRegistrados(contaSelecionada.getId(), id_boleto_adicionado_remessa);

            statusRemessa = (StatusRemessa) dao.find(new StatusRemessa(), 2);

            ids_pesquisa = "";
            for (Boleto bo : lista_b) {

                BoletoRemessa br = new BoletoRemessa(bo, statusRemessa, "tblExtratoTelaT1");
                listaBoletoRemessa.add(br);

                if (id_boleto_adicionado_remessa.isEmpty()) {
                    id_boleto_adicionado_remessa = "" + bo.getId();
                    ids_pesquisa = "" + bo.getId();
                } else {
                    id_boleto_adicionado_remessa += ", " + bo.getId();
                    ids_pesquisa = ", " + bo.getId();
                }
            }

            if (!ids_pesquisa.isEmpty()) {
                l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 2);
                if (!l_rb.isEmpty()) {
                    GenericaMensagem.error("Atenção", "Boleto STATUS: Baixar Boleto já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                    return;
                }
            }
            // ---------------------------------------------------------------------
            // ---------------------------------------------------------------------
        }
        visibleModalRemessa = true;

        loadLista();
        PF.update("formExtratoTelaSocial");
        PF.update("formExtratoTelaSocialOpcoes");

    }

    public void marcarTodos() {
        listaMovimento.stream().forEach((da) -> {
            da.setArgumento0(selecionaTodos);
        });
    }

    public void fecharModalRemessa() {
        visibleModalRemessa = false;
        id_boleto_adicionado_remessa = "";
        listaBoletoRemessa.clear();
        statusRemessa = new StatusRemessa();

        loadLista();
    }

    public void changeStatusRetorno() {
        Integer id_status_retorno = Integer.valueOf(listaStatusRetorno.get(indexListaStatusRetorno).getDescription());
        if (id_status_retorno == -2) {
            porPesquisa = "naoRecebidas";
        }
    }

    public final void loadListaStatusRetorno() {
        listaStatusRetorno.clear();

        List<StatusRetorno> result = new Dao().list(new StatusRetorno());

        listaStatusRetorno.add(new SelectItem(0, "Todos Boletos", "-1"));
        listaStatusRetorno.add(new SelectItem(1, "Não Registrados", "-2"));

        Integer indx = 1;

        for (int i = 0; i < result.size(); i++) {
            listaStatusRetorno.add(
                    new SelectItem(
                            indx + 1,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
            indx++;
        }
    }

    public void loadLista() {
        ExtratoTelaSocialDao dao = new ExtratoTelaSocialDao();

        listaMovimento.clear();
        vlRecebido = "0,00";
        vlNaoRecebido = "0,00";
        vlTaxa = "0,00";
        vlTotal = "0,00";
        vlLiquido = "0,00";

        if (!visibleModalRemessa) {
            id_boleto_adicionado_remessa = "";
        }

        Integer id_status_retorno = Integer.valueOf(listaStatusRetorno.get(indexListaStatusRetorno).getDescription());

        // TODOS BOLETOS / BOLETO REGISTRADO / BOLETO LIQUIDADO
        if (id_status_retorno == -1 || id_status_retorno == 2 || id_status_retorno == 3) {
            if (dataInicial.isEmpty() && dataFinal.isEmpty() && dataRefInicial.isEmpty() && dataRefFinal.isEmpty() && boletoInicial.isEmpty() && boletoFinal.isEmpty() && pessoa.getId() == -1 && Integer.valueOf(listaServicos.get(idServicos).getDescription()) == 0 && Integer.valueOf(listaTipoServico.get(idTipoServico).getDescription()) == 0) {
                GenericaMensagem.warn("Atenção", "Selecione algum filtro para não travar com muitos resultados!");
                return;
            }
        }

        if (!tipoPessoa.equals("nenhum") && pessoa.getId() == -1) {
            return;
        }

        List<Vector> result = dao.listaMovimentosSocial(
                porPesquisa, ordenacao, tipoDataPesquisa, tipoData, dataInicial, dataFinal, dataRefInicial, dataRefFinal, boletoInicial, boletoFinal, tipoPessoa, pessoa.getId(), Integer.valueOf(listaServicos.get(idServicos).getDescription()), Integer.valueOf(listaTipoServico.get(idTipoServico).getDescription()), id_status_retorno, id_boleto_adicionado_remessa, contaSelecionada.getId(), idFilial
        );

        double valor = 0, valor_baixa = 0, valor_taxa = 0;
        for (Vector vector : result) {
            listaMovimento.add(
                    new DataObject(
                            false,
                            vector,
                            vector.get(0) // id_movimento
                    )
            );

            // SE id_baixa FOR DIFERENTE DE NULL
            if (vector.get(14) != null) {

                valor_baixa = Double.parseDouble(Double.toString((Double) vector.get(12)));
                valor_taxa = Double.parseDouble(Double.toString((Double) vector.get(13)));

                vlRecebido = Moeda.converteR$Double(Moeda.soma(valor_baixa, Moeda.converteUS$(vlRecebido)));
                vlTaxa = Moeda.converteR$Double(Moeda.soma(valor_taxa, Moeda.converteUS$(vlTaxa)));
            } else {
                valor = Double.parseDouble(Double.toString((Double) vector.get(10)));
                vlNaoRecebido = Moeda.converteR$Double(Moeda.soma(valor, Moeda.converteUS$(vlNaoRecebido)));
            }

            vlTotal = Moeda.converteR$Double(Moeda.soma(valor_baixa, Moeda.converteUS$(vlTotal)));

            double contaLiquido = Moeda.subtracao(valor_baixa, valor_taxa);
            vlLiquido = Moeda.converteR$Double(Moeda.soma(contaLiquido, Moeda.converteUS$(vlLiquido)));
        }

    }

    public void imprimir() {
        List<Boleto> listab = new ArrayList();
        Map<Integer, Boleto> hash = new LinkedHashMap();

        for (DataObject datao : listaMovimento) {
            if ((Boolean) datao.getArgumento0() && ((Vector) datao.getArgumento1()).get(15) != null) {
                Boleto boletox = (Boleto) new Dao().find(new Boleto(), ((Vector) datao.getArgumento1()).get(15));

                if (boletox.getNrCtrBoleto().isEmpty()) {
                    GenericaMensagem.fatal("Atenção", "Boleto " + boletox.getNrBoleto() + " sem NrCtrBoleto!, Contate o administrador!");
                    return;
                }

                hash.put(boletox.getId(), boletox);
            }
        }

        for (Map.Entry<Integer, Boleto> entry : hash.entrySet()) {
            listab.add(entry.getValue());
        }

        if (!listab.isEmpty()) {
            ImprimirBoleto ib = new ImprimirBoleto();
            ib.imprimirBoletoSocial(listab, "soc_boletos_vw", imprimirVerso);
            ib.visualizar(null);
        }
    }

    public Boolean validaImprimir() {
        if (listaBoletoRemessa.isEmpty()) {
            GenericaMensagem.error("Atenção", "NENHUM BOLETO PARA GERAR ARQUIVO");
            return false;
        }

        return true;
    }

    public String imprimirRemessa() {

        ImprimirBoleto imp = new ImprimirBoleto();

        if (!validaImprimir()) {
            return null;
        }

//        List<Boleto> listab = new ArrayList();
//        Map<Integer, Boleto> hash = new LinkedHashMap();
//        String ids_boleto = "";
//        for (DataObject datao : listaMovimento) {
//            if ((Boolean) datao.getArgumento0() && ((Vector) datao.getArgumento1()).get(15) != null) {
//                Boleto boletox = (Boleto) new Dao().find(new Boleto(), ((Vector) datao.getArgumento1()).get(15));
//
//                if (boletox.getNrCtrBoleto().isEmpty()) {
//                    GenericaMensagem.fatal("Atenção", "Boleto " + boletox.getNrBoleto() + " sem NrCtrBoleto!, Contate o administrador!");
//                    return null;
//                }
//
//                hash.put(boletox.getId(), boletox);
//            }
//        }
//
//        if (hash.isEmpty()) {
//            GenericaMensagem.fatal("Atenção", "Nenhum Boleto Selecionado!");
//            return null;
//        }
//        for (Map.Entry<Integer, Boleto> entry : hash.entrySet()) {
//            listab.add(entry.getValue());
//        }
//        RemessaBancoDao daor = new RemessaBancoDao();
//        List<RemessaBanco> l_rb = daor.listaBoletoComRemessaBanco(ids_boleto);
//        if (!l_rb.isEmpty()) {
//            GenericaMensagem.error("Atenção", "Boleto já enviado para Remessa, " + l_rb.get(0).getBoleto().getBoletoComposto());
//            return null;
//        }
        //File fi = imp.imprimirRemessa(listab, listab.get(0));
        RespostaArquivoRemessa RESP = imp.imprimirRemessa(listaBoletoRemessa);

        if (RESP.getArquivo() == null) {
            GenericaMensagem.error("ATENÇÃO", RESP.getMensagem());
            return null;
        }

        imp.visualizar_remessa(RESP.getArquivo());

        return null;
    }

    public void imprimirPlanilha() {

        Integer sel = 0;
        Movimento mov = null;

        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0()) {
                sel++;
                mov = (Movimento) new Dao().find(new Movimento(), listaMovimento1.getArgumento2());
            }
        }

        if (sel == 0) {
            GenericaMensagem.error("ATENÇÃO", "Selecione UM ACORDO!");
            return;
        }

        if (sel > 1) {
            GenericaMensagem.error("ATENÇÃO", "MAIS de um acordo selecionado!");
            return;
        }

        if (mov != null && mov.getAcordo() == null) {
            GenericaMensagem.error("ATENÇÃO", "Apenas tipo ACORDO para IMPRIMIR PLANILHA");
            return;
        }

//        for (DataObject listaMovimento1 : listaMovimento) {
//            if ((Boolean) listaMovimento1.getArgumento0() && !String.valueOf(((Vector) listaMovimento1.getArgumento1()).get(7)).equals("Acordo")) {
//            }
//        }
        ImprimirBoleto imp = new ImprimirBoleto();
        List listaImp = new ArrayList();

        MovimentoDao db = new MovimentoDao();
        listaImp.addAll(db.pesquisaAcordoTodos(mov.getAcordo().getId()));

        if (!listaImp.isEmpty()) {
            // SEM HISTORICO NÃO IMPRIME
            imp.imprimirAcordoSocial(listaImp, mov.getAcordo(), mov.getHistorico());
        }

    }

    public void imprimirPromissoria() {

    }

    public void inativarBoleto() {
        if (historico.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite um motivo para exclusão!");
            return;
        } else if (historico.length() < 6) {
            GenericaMensagem.warn("Atenção", "Motivo de exclusão inválido!");
            return;
        }

        List<Movimento> listam = new ArrayList();

        if (baixado()) {
            GenericaMensagem.warn("Atenção", "Boletos BAIXADOS não podem ser excluídos!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser estornados!");
            return;
        }

        if (acordados()) {
            GenericaMensagem.warn("Atenção", "Boletos do tipo ACORDO não podem ser excluídos!");
            return;
        }

        for (DataObject dh : listaMovimento) {
            if ((Boolean) dh.getArgumento0()) {
                int id_movimento = Integer.valueOf(((Vector) dh.getArgumento1()).get(0).toString());
                Movimento mov = (Movimento) new Dao().find(new Movimento(), id_movimento);
                listam.add(mov);
            }
        }

        if (listam.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boletos foi selecionado!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        if (!GerarMovimento.inativarArrayMovimento(listam, historico, dao).isEmpty()) {
            GenericaMensagem.error("Atenção", "Ocorreu um erro em uma das exclusões, verifique o log!");
            dao.rollback();
            return;
        } else {
            GenericaMensagem.info("Sucesso", "Boletos foram excluídos!");
        }

        dao.commit();

        loadLista();

        PF.update("formExtratoTelaSocial");
        PF.closeDialog("dlg_excluir");
    }

    public void estornarBaixa() {
        if (listaMovimento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existem boletos para serem estornados!");
            return;
        }

        int qnt = 0;

        Movimento mov = null;

        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0()) {
                qnt++;
                mov = (Movimento) new Dao().find(new Movimento(), listaMovimento1.getArgumento2());
            }
        }

        if (mov == null) {
            GenericaMensagem.warn("Atenção", "Nenhum Movimento selecionado!");
            return;
        }

        if (qnt > 1) {
            GenericaMensagem.warn("Erro", "Mais de um movimento foi selecionado!");
            return;
        }

        if (!baixado()) {
            GenericaMensagem.warn("Atenção", "Existem boletos que não foram pagos para estornar!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser estornados!");
            return;
        }

        if (!mov.isAtivo()) {
            GenericaMensagem.warn("Atenção", "Boleto ID: " + mov.getId() + " esta inativo, não é possivel concluir estorno!");
            return;
        }

        if (motivoEstorno.isEmpty() || motivoEstorno.length() <= 5) {
            GenericaMensagem.error("Atenção", "Motivo de Estorno INVÁLIDO!");
            return;
        }

        if (mov.getLote().getRotina() != null && mov.getLote().getRotina().getId() == 132) {
            mov.setAtivo(false);
        }

        StatusRetornoMensagem sr = GerarMovimento.estornarMovimento(mov, motivoEstorno);

        if (!sr.getStatus()) {
            GenericaMensagem.warn("Atenção", sr.getMensagem());
        } else {
            GenericaMensagem.info("Sucesso", "Boletos estornados com sucesso!");
        }

        loadLista();

        PF.update("formExtratoTelaSocial");
        PF.closeDialog("dlg_estornar");
    }

    public void enviarEmail() {
        /*
        REVER METODO
         */
        if (baixado()) {
            GenericaMensagem.warn("Atenção", "Boletos BAIXADOS não podem ser excluídos!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser estornados!");
            return;
        }

        List<Boleto> listab = new ArrayList();
        Map<Integer, Boleto> hash = new LinkedHashMap();

        for (DataObject datao : listaMovimento) {
            if ((Boolean) datao.getArgumento0() && ((Vector) datao.getArgumento1()).get(15) != null) {
                Boleto boletox = (Boleto) new Dao().find(new Boleto(), ((Vector) datao.getArgumento1()).get(15));

                if (boletox.getNrCtrBoleto().isEmpty()) {
                    GenericaMensagem.fatal("Atenção", "Boleto " + boletox.getNrBoleto() + " sem NrCtrBoleto!, Contate o administrador!");
                    return;
                }

                hash.put(boletox.getId(), boletox);
            }
        }

        for (Map.Entry<Integer, Boleto> entry : hash.entrySet()) {
            listab.add(entry.getValue());
        }

        for (Boleto bol : listab) {

            try {
                Registro reg = (Registro) new Dao().find(new Registro(), 1);

                ImprimirBoleto ib = new ImprimirBoleto();
                ib.imprimirBoletoSocial(bol, "soc_boletos_vw", false);
                ib.setPathPasta(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos"));
                Pessoa pessoa_envio = null;
                MovimentosReceberSocialDao dbs = new MovimentosReceberSocialDao();

                if (tipoEnvio.equals("responsavel")) {
                    pessoa_envio = dbs.responsavelBoleto(bol.getNrCtrBoleto());
                }

                String nome = ib.criarLink(pessoa_envio, reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
                List<Pessoa> pessoas = new ArrayList();
                pessoas.add(pessoa_envio);

                String mensagem = "";
                List<File> fls = new ArrayList();
                String nome_envio = "";
//                if (mov.size() == 1) {
//                    nome_envio = "Boleto " + mov.get(0).getServicos().getDescricao() + " N° " + mov.get(0).getDocumento();
//                } else {
                nome_envio = "Boleto Associativo";
//                }

                if (!reg.isEnviarEmailAnexo()) {
                    mensagem = " <div style='background:#00ccff; padding: 15px; font-size:13pt'>Enviado para <b>" + pessoa_envio.getNome() + " </b></div><br />"
                            + " <h5>Visualize seu boleto clicando no link abaixo</h5><br /><br />"
                            + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "'>Clique aqui para abrir boleto</a><br />";
                } else {
                    fls.add(new File(ib.getPathPasta() + "/" + nome));
                    mensagem = " <div style='background:#00ccff; padding: 15px; font-size:13pt'>Enviado para <b>" + pessoa_envio.getNome() + " </b></div><br />"
                            + " <h5>Segue boleto em anexo</h5><br /><br />";
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
                                (Rotina) di.find(new Rotina(), 96),
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
                //String[] retorno = new String[2];

                if (!retorno[1].isEmpty()) {
                    GenericaMensagem.warn("Erro", retorno[1]);
                } else {
                    GenericaMensagem.info("Sucesso", retorno[0]);
                }
            } catch (Exception erro) {
                System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());

            }
        }
    }

    public void excluirAcordo() {
        if (listaMovimento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existem acordos para serem excluidos!");
            return;
        }

        int qnt = 0;
        Movimento movimento = new Movimento();

        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0()) {
                qnt++;
                movimento = (Movimento) new Dao().find(new Movimento(), listaMovimento1.getArgumento2());
            }
        }

        if (qnt == 0) {
            GenericaMensagem.warn("Atenção", "Nenhum Movimento selecionado!");
            return;
        }

        if (qnt > 1) {
            GenericaMensagem.warn("Erro", "Mais de um movimento foi selecionado!");
            return;
        }

        String resposta = GerarMovimento.excluirUmAcordoSocial(movimento);
        if (resposta.isEmpty()) {
            GenericaMensagem.info("OK", "Acordo Excluído com sucesso!");

            loadLista();

            PF.update("formExtratoTelaSocial");
            PF.closeDialog("dlg_acordo");
            return;
        }

        GenericaMensagem.error("Atenção", resposta);
    }

    public void loadListaServicos() {
        listaServicos.clear();

        List<Servicos> select = new ExtratoTelaSocialDao().listaServicosAssociativo();

        listaServicos.add(new SelectItem(0, "-- Selecione um Serviço --", "0"));
        for (int i = 0; i < select.size(); i++) {
            listaServicos.add(new SelectItem(
                    i + 1,
                    select.get(i).getDescricao(),
                    Integer.toString(select.get(i).getId())
            ));
        }
    }

    public void loadListaTipoServico() {
        listaTipoServico.clear();

        TipoServicoDao db = new TipoServicoDao();
        List<TipoServico> select = db.pesquisaTodos();

        listaTipoServico.add(new SelectItem(0, "-- Selecione um Tipo --", "0"));
        for (int i = 0; i < select.size(); i++) {
            listaTipoServico.add(new SelectItem(
                    i + 1,
                    select.get(i).getDescricao(),
                    Integer.toString(select.get(i).getId())
            ));
        }
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
        loadLista();
    }

    public void limparDatas() {
        if (tipoDataPesquisa.equals("referencia")) {
            dataInicial = "";
            dataFinal = "";
        } else {
            dataRefInicial = "";
            dataRefFinal = "";
        }
    }

    public String converteData(Date data) {
        return DataHoje.converteData(data);
    }

    public String converteValor(String valor) {
        return Moeda.converteR$(valor);
    }

    public boolean baixado() {
        for (DataObject listaMovimento1 : listaMovimento) {
            if (((Boolean) listaMovimento1.getArgumento0()) && ((Vector) listaMovimento1.getArgumento1()).get(14) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean acordados() {
        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0() && String.valueOf(((Vector) listaMovimento1.getArgumento1()).get(7)).equals("Acordo")) {
                return true;
            }
        }
        return false;
    }

    public boolean fechadosCaixa() {
        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0()) {
                Movimento mov = (Movimento) new Dao().find(new Movimento(), listaMovimento1.getArgumento2());
                if (mov.getBaixa() != null && mov.getBaixa().getFechamentoCaixa() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getQntBoletos() {
        String n;
        if (!listaMovimento.isEmpty()) {
            n = Integer.toString(listaMovimento.size()) + ((listaMovimento.size() == 15000) ? " limite" : "");
        } else {
            n = "0";
        }
        return n;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public List<DataObject> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<DataObject> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            loadLista();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getTipoDataPesquisa() {
        return tipoDataPesquisa;
    }

    public void setTipoDataPesquisa(String tipoDataPesquisa) {
        this.tipoDataPesquisa = tipoDataPesquisa;
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

    public String getDataRefInicial() {
        return dataRefInicial;
    }

    public void setDataRefInicial(String dataRefInicial) {
        this.dataRefInicial = dataRefInicial;
    }

    public String getDataRefFinal() {
        return dataRefFinal;
    }

    public void setDataRefFinal(String dataRefFinal) {
        this.dataRefFinal = dataRefFinal;
    }

    public String getBoletoInicial() {
        return boletoInicial;
    }

    public void setBoletoInicial(String boletoInicial) {
        this.boletoInicial = boletoInicial;
    }

    public String getBoletoFinal() {
        return boletoFinal;
    }

    public void setBoletoFinal(String boletoFinal) {
        this.boletoFinal = boletoFinal;
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

    public Integer getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(Integer idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public List<SelectItem> getListaTipoServico() {
        return listaTipoServico;
    }

    public void setListaTipoServico(List<SelectItem> listaTipoServico) {
        this.listaTipoServico = listaTipoServico;
    }

    public boolean isImprimirVerso() {
        return imprimirVerso;
    }

    public void setImprimirVerso(boolean imprimirVerso) {
        this.imprimirVerso = imprimirVerso;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getVlRecebido() {
        return vlRecebido;
    }

    public void setVlRecebido(String vlRecebido) {
        this.vlRecebido = vlRecebido;
    }

    public String getVlNaoRecebido() {
        return vlNaoRecebido;
    }

    public void setVlNaoRecebido(String vlNaoRecebido) {
        this.vlNaoRecebido = vlNaoRecebido;
    }

    public String getVlTaxa() {
        return vlTaxa;
    }

    public void setVlTaxa(String vlTaxa) {
        this.vlTaxa = vlTaxa;
    }

    public String getVlTotal() {
        return vlTotal;
    }

    public void setVlTotal(String vlTotal) {
        this.vlTotal = vlTotal;
    }

    public String getVlLiquido() {
        return vlLiquido;
    }

    public void setVlLiquido(String vlLiquido) {
        this.vlLiquido = vlLiquido;
    }

    public ControleAcessoBean getCab() {
        return cab;
    }

    public void setCab(ControleAcessoBean cab) {
        this.cab = cab;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Integer getIndexListaStatusRetorno() {
        return indexListaStatusRetorno;
    }

    public void setIndexListaStatusRetorno(Integer indexListaStatusRetorno) {
        this.indexListaStatusRetorno = indexListaStatusRetorno;
    }

    public List<SelectItem> getListaStatusRetorno() {
        return listaStatusRetorno;
    }

    public Boolean getSelecionaTodos() {
        return selecionaTodos;
    }

    public void setSelecionaTodos(Boolean selecionaTodos) {
        this.selecionaTodos = selecionaTodos;
    }

    public Boolean getVisibleModalRemessa() {
        return visibleModalRemessa;
    }

    public void setVisibleModalRemessa(Boolean visibleModalRemessa) {
        this.visibleModalRemessa = visibleModalRemessa;
    }

    public List<BoletoRemessa> getListaBoletoRemessa() {
        return listaBoletoRemessa;
    }

    public void setListaBoletoRemessa(List<BoletoRemessa> listaBoletoRemessa) {
        this.listaBoletoRemessa = listaBoletoRemessa;
    }

    public List<SelectItem> getListaConta() {
        return listaConta;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public Integer getIndexConta() {
        return indexConta;
    }

    public void setIndexConta(Integer indexConta) {
        this.indexConta = indexConta;
    }

    public ContaCobranca getContaSelecionada() {
        return contaSelecionada;
    }

    public void setContaSelecionada(ContaCobranca contaSelecionada) {
        this.contaSelecionada = contaSelecionada;
    }

    public Boolean getVerListaRemessa() {
        return verListaRemessa;
    }

    public void setVerListaRemessa(Boolean verListaRemessa) {
        this.verListaRemessa = verListaRemessa;
    }

    public StatusRemessa getStatusRemessa() {
        return statusRemessa;
    }

    public void setStatusRemessa(StatusRemessa statusRemessa) {
        this.statusRemessa = statusRemessa;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public void limparTipoDatas() {
        dataInicial = "";
        dataFinal = "";
        dataRefInicial = "";
        dataRefFinal = "";
    }

    public List<SelectItem> getListaFilial() {
        if (listaFilial.isEmpty()) {
            idFilial = null;
            listaFilial.add(new SelectItem(null, "-- SELECIONAR -- "));
            List<Filial> list = new Dao().list(new Filial(), true);
            for (int i = 0; i < list.size(); i++) {
                listaFilial.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getPessoa().getNome()));
            }
        }
        return listaFilial;
    }

    public void setListaFilial(List<SelectItem> listaFilial) {
        this.listaFilial = listaFilial;
    }

    public String getId_boleto_adicionado_remessa() {
        return id_boleto_adicionado_remessa;
    }

    public void setId_boleto_adicionado_remessa(String id_boleto_adicionado_remessa) {
        this.id_boleto_adicionado_remessa = id_boleto_adicionado_remessa;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Boolean getApenasBoletoSelecionado() {
        return apenasBoletoSelecionado;
    }

    public void setApenasBoletoSelecionado(Boolean apenasBoletoSelecionado) {
        this.apenasBoletoSelecionado = apenasBoletoSelecionado;
    }

}
