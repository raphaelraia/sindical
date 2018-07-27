package br.com.rtools.arrecadacao.beans;

import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.dao.AcordoDao;
import br.com.rtools.arrecadacao.dao.RemessaDao;
import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.cobranca.BoletoRemessa;
import br.com.rtools.cobranca.RespostaArquivoRemessa;
import br.com.rtools.financeiro.*;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.RemessaBancoDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ExtratoTelaBean implements Serializable {

    private int idContribuicao = 0;
    private int idTipoServico = 0;
    private int idTipoServicoAlterar = 0;
    private int idIndex = -1;
    private Pessoa pessoa = new Pessoa();
    private Movimento mov = new Movimento();
    private List<DataObject> listaMovimentos = new ArrayList();
    private List<ExtratoTelaObject> listMovimentos;
    //private List listMov = new Vector();
    private boolean chkData = false;
    private boolean chkContribuicao = false;
    private boolean chkNrBoletos = false;
    private boolean chkEmpresa = false;
    private boolean chkTipo = false;
    //private boolean recarregaPag = false;
    private boolean chkExcluirBol = false;
    private String tipoPesquisa = "data";
    private String porPesquisa = "";
    private String geraPesquisa = "naoVerificar";
    private String tipoData = "faixa";
    private String tipoDataPesquisa = "ocorrencia";
    private String ordenacao = "referencia";
    private String dataInicial = "";
    private String dataFinal = "";
    private String dataRefInicial = "";
    private String dataRefFinal = "";
    private String boletoInicial = "";
    private String boletoFinal = "";

    private String vlTotal = "0,00";
    private String vlRecebido = "0,00";
    private String vlNaoRecebido = "0,00";
    private String vlTaxa = "0,00";
    private String vlLiquido = "0,00";
    private String vlRepasse = "0,00";
    private String pessoa_documento = "";

    private String msgConfirma = "";
    //private String valorSomado;
    private String dataAntiga = "";
    private String dataNova = "";
    public boolean imprimirVerso = false;
    private String historico = "";

    private String tipoEnvio = "empresa";
    private String valorExtenso = "";
    private List<Impressao> listaImpressao = new ArrayList();
    private List<ImpressaoWeb> listaImpressaoWeb = new ArrayList();

    private boolean movimentosDasEmpresas = false;
    private List<Juridica> listaEmpresasPertencentes = new ArrayList();
    private boolean dcData = false;
    /* dc = defaultCollapsed */

    private boolean dcBoleto = false;
    private final List<SelectItem> listaTipoServico = new ArrayList();
    private final List<SelectItem> listaTipoServicoAlterar = new ArrayList();
    private final List<SelectItem> listaServico = new ArrayList();
    private List<SelectItem> listaFilial = new ArrayList();
    private Movimento movimentoVencimento = new Movimento();

    private String motivoEstorno = "";
    private Movimento movimentoAlterar = new Movimento();
    private List<Movimento> listMovimentosAcordo = new ArrayList();
    private Historico historicoMovimento;
    private Integer index;
    private String motivoReativacao;
    Integer idFilial;

    private ControleAcessoBean cab = new ControleAcessoBean();

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
    private Boolean apenasBoletoSelecionado = false;

    public ExtratoTelaBean() {
        GenericaSessao.remove("tipoPesquisaPessoaJuridica");

        // NÃO PRECISA PEGAR DA SESSÃO
        //cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");
        // NUNCA SETAR O MÓDULO DENTRO DESTA PÁGINA, POIS ESTA ALTERANDO O MÓDULO PADRÃO DA TELA ( ex. quando vem de social o módulo é 2, e aqui estava colocando 3 - ERRO FEITO POR CLAUDEMIR
        //cab.setModulo((Modulo) new Dao().find(new Modulo(), 3));
        motivoReativacao = "";

        if (cab.getListaExtratoTela(false)) {
            porPesquisa = "todos";
        } else {
            porPesquisa = "nao_recebidas";
        }
        listMovimentos = new ArrayList();

        loadListaStatusRetorno();

        loadListaContas();
    }

    public final void loadListaContas() {
        listaConta.clear();
        indexConta = 0;

        ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
        List<ContaCobranca> result = servDB.listaContaCobrancaAtivoArrecadacao();

        listaConta.add(new SelectItem(0, "SELECIONAR UMA CONTA", "0"));

        Integer contador = 1;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getLayout().getId() == 2) {
                listaConta.add(
                        new SelectItem(
                                contador,
                                result.get(i).getApelido() + " - " + result.get(i).getSicasSindical() + " - " + result.get(i).getContaBanco().getBanco().getBanco(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            } else {
                listaConta.add(
                        new SelectItem(
                                contador,
                                result.get(i).getApelido() + " - " + result.get(i).getCodCedente() + " - " + result.get(i).getContaBanco().getBanco().getBanco(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }

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

        loadListBeta();
    }

    public List<Movimento> validaListaRemessa(String opcao) {
        List<Movimento> lista_movimento_validado = new ArrayList();
        if (!listaMovimentos.isEmpty()) {
            Dao dao = new Dao();
            for (DataObject dob : listaMovimentos) {
                // BOLETOS QUE ESTÃO SELECIONADO
                if ((Boolean) dob.getArgumento0()) {

                    Movimento m = ((Movimento) dob.getArgumento29());
                    Boleto b = (Boleto) dao.find(m.getBoleto());

                    // SE O BOLETO ESTA QUITADO
                    if (dob.getArgumento15() != null) {
                        if (!dob.getArgumento15().equals("")) {
                            GenericaMensagem.error("Atenção", "BOLETOS QUITADOS NÃO PODEM SER REGISTRADOS!");
                            return null;
                        }
                    }

                    String data_calculo = new DataHoje().decrementarDias(b.getContaCobranca().getRegistrosDiasVencidos(), DataHoje.data());

                    if (b.getVencimento().isEmpty()) {

                        b.setVencimento(m.getVencimento());
                        b.setVencimentoOriginal(m.getVencimentoOriginal());

                        dao.save(b, true);

                        //GenericaMensagem.error("Atenção", "BOLETO: " + m.getDocumento() + " NÃO TEM DATA DE VENCIMENTO");
                        //return null;
                    }

                    if (DataHoje.menorData(b.getVencimento(), data_calculo)) {
                        GenericaMensagem.error("Atenção", "BOLETO: " + m.getDocumento() + " ESTÁ VENCIDO A MAIS TEMPO QUE O PERMITIDO ( " + b.getContaCobranca().getRegistrosDiasVencidos() + " DIAS )");
                        return null;
                    }

                    // SE O TIPO DE DOCUMENTO É 1 ou 2 (CPF, CNPJ)
                    if (m.getPessoa().getTipoDocumento().getId() != 1 && m.getPessoa().getTipoDocumento().getId() != 2) {
                        GenericaMensagem.error(m.getPessoa().getNome(), "TIPO DE DOCUMENTO INVÁLIDO!");
                        return null;
                    }

                    // SE O DOCUMENTO É VÁLIDO
                    // -- CPF
                    if (m.getPessoa().getTipoDocumento().getId() == 1) {
                        if (!ValidaDocumentos.isValidoCPF(m.getPessoa().getDocumentoSomentoNumeros())) {
                            GenericaMensagem.error(m.getPessoa().getNome(), "CPF INVÁLIDO!");
                            return null;
                        }
                    }
                    // -- CNPJ
                    if (m.getPessoa().getTipoDocumento().getId() == 2) {
                        if (!ValidaDocumentos.isValidoCNPJ(m.getPessoa().getDocumentoSomentoNumeros())) {
                            GenericaMensagem.error(m.getPessoa().getNome(), "CNPJ INVÁLIDO!");
                            return null;
                        }
                    }

                    // SE TEM ENDEREÇO
                    PessoaEndereco pe = m.getPessoa().getPessoaEndereco();

                    if (pe == null) {
                        GenericaMensagem.error(m.getPessoa().getNome(), "NÃO CONTÉM ENDEREÇO!");
                        return null;
                    }

                    String cep = pe.getEndereco().getCep().replace("-", "").replace(".", "");
                    if (cep.length() < 8) {
                        GenericaMensagem.error(m.getPessoa().getNome(), "CEP INVÁLIDO: " + cep);
                        return null;
                    }

                    if (b.getStatusRetorno() != null) {
                        switch (opcao) {
                            case "registrar":
                                if (m.getBoleto().getStatusRetorno().getId() == 2) {
                                    GenericaMensagem.error(m.getPessoa().getNome(), "NÃO PODE REGISTRAR UM BOLETO JÁ REGISTRADO!");
                                    return null;
                                }

                                if (m.getBoleto().getStatusRetorno().getId() == 3) {
                                    GenericaMensagem.error(m.getPessoa().getNome(), "NÃO PODE REGISTRAR UM BOLETO LIQUIDADO!");
                                    return null;
                                }

                                break;
                            case "baixar_banco":
                                if (m.getBoleto().getStatusRetorno().getId() != 2) {
                                    GenericaMensagem.error(m.getPessoa().getNome(), "NÃO PODE BAIXAR NO BANCO UM BOLETO QUE NÃO FOI REGISTRADO!");
                                    return null;
                                }
                                break;
                        }
                    }

                    lista_movimento_validado.add(m);
                }

            }
            return lista_movimento_validado;
        }
        return lista_movimento_validado;
    }

    public void adicionarRemessa() {
        adicionarRemessa(null);
    }

    public void adicionarRemessa(String opcao) {
        id_boleto_adicionado_remessa = "";

        if (contaSelecionada.getId() == -1) {
            GenericaMensagem.error("ATENÇÃO", "Selecione uma Conta Cobrança para continuar!");
            PF.update("formExtratoTela");
            return;
        }

        if (opcao == null) {
            PF.openDialog("dlg_opcao_remessa");
            return;
        }

        Dao dao = new Dao();

        List<Movimento> lista_movimento_validado = validaListaRemessa(opcao);
        if (lista_movimento_validado == null) {
            PF.update("formExtratoTela");
            PF.update("formExtratoTelaOpcoes");
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
        for (Movimento mov : lista_movimento_validado) {

            Boleto bo = mov.getBoleto();
            BoletoRemessa br = new BoletoRemessa(bo, statusRemessa, "tblExtratoTelaT2");
            listaBoletoRemessa.add(br);

            if (id_boleto_adicionado_remessa.isEmpty()) {
                id_boleto_adicionado_remessa = "" + bo.getId();
                ids_pesquisa = "" + bo.getId();
            } else {
                id_boleto_adicionado_remessa += ", " + bo.getId();
                ids_pesquisa += ", " + bo.getId();
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
            // ADICIONA BOLETO PARA REGISTRO AUTOMÁTICO
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
                    ids_pesquisa += ", " + bo.getId();
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
            // ADICIONA BOLETO PARA REGISTRO DE RECUSADOS
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
                    ids_pesquisa += ", " + bo.getId();
                }
            }

            if (!ids_pesquisa.isEmpty()) {
                l_rb = daor.listaBoletoComRemessaBanco(ids_pesquisa, 1);
                if (!l_rb.isEmpty()) {
                    GenericaMensagem.error("Atenção", "Boleto STATUS: Registrar já foi enviado: " + l_rb.get(0).getBoleto().getBoletoComposto());
                    return;
                }
            }

            // ADICIONA BOLETO PARA BAIXAR REGISTRADOS
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
        }
        visibleModalRemessa = true;

        loadListBeta();
        PF.update("formExtratoTela");
        PF.update("formExtratoTelaOpcoes");

    }

    public void marcarTodos() {
        listaMovimentos.stream().forEach((da) -> {
            da.setArgumento0(selecionaTodos);
        });
    }

    public void fecharModalRemessa() {
        visibleModalRemessa = false;
        id_boleto_adicionado_remessa = "";
        listaBoletoRemessa.clear();
        statusRemessa = new StatusRemessa();
        loadListBeta();
    }

    public void changeStatusRetorno() {
        Integer id_status_retorno = Integer.valueOf(listaStatusRetorno.get(indexListaStatusRetorno).getDescription());
        if (id_status_retorno == -2) {
            porPesquisa = "nao_recebidas";
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

    public void alterarMovimento(Integer id) {
        movimentoAlterar = (Movimento) new Dao().find(new Movimento(), id);

        for (int i = 0; i < listaTipoServicoAlterar.size(); i++) {
            if (movimentoAlterar.getTipoServico().getId() == Integer.valueOf(listaTipoServicoAlterar.get(i).getDescription())) {
                idTipoServicoAlterar = i;
            }
        }
        PF.openDialog("dlg_alterar_movimento");
        PF.update("formExtratoTela:panel_alterar_movimento");
    }

    public void salvarAlterarMovimento() {
        if (movimentoAlterar.getId() != -1) {
            Dao dao = new Dao();

            dao.openTransaction();
            movimentoAlterar.setTipoServico((TipoServico) dao.find(new TipoServico(), Integer.valueOf(listaTipoServicoAlterar.get(idTipoServicoAlterar).getDescription())));
            if (!dao.update(movimentoAlterar)) {
                GenericaMensagem.error("Erro", "Não foi possível alterar Movimento");
                return;
            }

            dao.commit();
            movimentoAlterar = new Movimento();
            GenericaMensagem.info("Sucesso", "Movimento Alterado com Sucesso!");
            loadListBeta();
        }
    }

    public List<Juridica> loadListaEmpresasPertencentes() {
        listaEmpresasPertencentes.clear();
        JuridicaDao db = new JuridicaDao();
        if (pessoa.getId() != -1) {
            Juridica j = db.pesquisaJuridicaPorPessoa(pessoa.getId());
            if (j != null && j.getId() != -1) {
                List lista_x = db.listaContabilidadePertencente(j.getId());
                for (Object lista_x1 : lista_x) {
                    // pe = dbPe.pesquisaEndPorPessoaTipo(((Juridica) (listaX.get(i))).getPessoa().getId(), 2); // endereco da empresa pertencente
                    listaEmpresasPertencentes.add((Juridica) lista_x1);
                }
            }
        }

        if (listaEmpresasPertencentes.isEmpty()) {
            movimentosDasEmpresas = false;
        }

        return listaEmpresasPertencentes;
    }

    public void loadListBeta() {
        loadListBeta(1);
    }

    public void loadListBeta(Integer tCase) {

        if (tCase == 1) {
            listaMovimentos.clear();
            listMovimentos = new ArrayList();
        } else {
            return;
        }

        if (!visibleModalRemessa) {
            id_boleto_adicionado_remessa = "";
        }

        Integer id_status_retorno = Integer.valueOf(listaStatusRetorno.get(indexListaStatusRetorno).getDescription());

        // TODOS BOLETOS / BOLETO REGISTRADO / BOLETO LIQUIDADO
        if (id_status_retorno == -1 || id_status_retorno == 2 || id_status_retorno == 3) {

            if ((tipoData.equals("ate") || tipoData.equals("apartir")) && pessoa.getId() == -1) {
                GenericaMensagem.warn("Validação", "Pesquisar uma pessoa caso o tipo de filtro de data seja até ou a partir!");
                return;
            }

            if (pessoa.getId() == -1) {

                if ((boletoInicial.equals("0") && boletoFinal.equals("0")) || (boletoInicial.isEmpty() && boletoFinal.isEmpty())) {
                    if (tipoDataPesquisa.equals("referencia")) {
                        if (dataRefInicial.isEmpty() && dataRefFinal.isEmpty()) {
                            GenericaMensagem.warn("Validação", "Informar referência uma referência, boleto ou uma pessoa para filtrar!");
                            return;
                        }
                        if (dataRefInicial.isEmpty()) {
                            GenericaMensagem.warn("Validação", "Informar a referência inicial!");
                            return;
                        }
                        if (tipoData.equals("faixa")) {
                            if (dataRefFinal.isEmpty()) {
                                GenericaMensagem.warn("Validação", "Informar a referência final!");
                                return;
                            }
                            if (!dataRefInicial.equals(dataRefFinal)) {
                                if (DataHoje.maiorData("01/" + dataRefInicial, "01/" + dataRefFinal)) {
                                    GenericaMensagem.warn("Validação", "Referência final deve ser maior ou igual que data inicial!");
                                    return;
                                }

                            }
                        }
                    } else {
                        if ((dataInicial.isEmpty() && dataFinal.isEmpty())) {
                            GenericaMensagem.warn("Validação", "Informar data, boleto ou uma pessoa para filtrar!");
                            return;
                        }
                        if (dataInicial.isEmpty()) {
                            GenericaMensagem.warn("Validação", "Informar a data inicial!");
                            return;
                        }
                        if (tipoData.equals("faixa")) {
                            if (dataFinal.isEmpty()) {
                                GenericaMensagem.warn("Validação", "Informar a data final!");
                                return;
                            }
                            if (!dataInicial.equals(dataFinal)) {
                                if (DataHoje.maiorData(dataInicial, dataFinal)) {
                                    GenericaMensagem.warn("Validação", "Data final deve ser maior ou igual que data inicial!");
                                    return;
                                }
                            }
                        }
                    }
                }

            }
        }

        loadListaEmpresasPertencentes();
        if (tCase == 1) {
            listaMovimentos.clear();
            listMovimentos = new ArrayList();
        } else {
            return;
        }
        boolean habData = false;
        double soma = 0, somaRepasse = 0;
        double somaNew = 0, somaRepasseNew = 0;
        String classTbl = "";

        vlRecebido = "0,00";
        vlNaoRecebido = "0,00";
        vlTotal = "0,00";
        vlTaxa = "0,00";
        vlLiquido = "0,00";
        vlRepasse = "0,00";

        MovimentoDao db = new MovimentoDao();

        int ic, its;

        if (chkData && !tipoDataPesquisa.equals("referencia")) {
            if (dataInicial.isEmpty() || dataFinal.isEmpty()) {
                chkData = false;
            }
        } else if (dataRefInicial.isEmpty() || dataRefFinal.isEmpty()) {
            chkData = false;
        }
        if (pessoa == null) {
            pessoa = new Pessoa();
        }

        if (!getListaServico().isEmpty()) {
            ic = Integer.parseInt(getListaServico().get(idContribuicao).getDescription());
        } else {
            ic = 0;
        }

        if (!getListaTipoServico().isEmpty()) {
            its = Integer.parseInt(getListaTipoServico().get(idTipoServico).getDescription());
        } else {
            its = 0;
        }

        if (!boletoInicial.isEmpty() && boletoFinal.isEmpty()) {
            boletoFinal = boletoInicial;
        }

        if (boletoInicial.isEmpty() && !boletoFinal.isEmpty()) {
            boletoInicial = boletoFinal;
        }

        //  BLOQUEIA QUE O USUÁRIO GERE UMA PESQUISA SEM FILTRO, TRAZENDO (N) REGISTROS
        if (dataInicial.isEmpty() && dataFinal.isEmpty() && dataRefInicial.isEmpty() && dataRefFinal.isEmpty() && ic == 0 && its == 0 && boletoInicial.isEmpty() && boletoFinal.isEmpty() && getPessoa().getId() == -1) {
            return;
        }

        // USAR PARA DEPURAR COM LISTA VAZIA
        // List<Vector> listax = new ArrayList<Vector>();
        List<Vector> listax = db.listaMovimentosExtrato(
                porPesquisa, tipoDataPesquisa, tipoData, dataInicial, dataFinal, dataRefInicial, dataRefFinal, boletoInicial, boletoFinal, ic, its, pessoa.getId(), ordenacao, movimentosDasEmpresas, idFilial, id_status_retorno, id_boleto_adicionado_remessa, contaSelecionada.getId()
        );

        MovimentoDao movimentosDao = new MovimentoDao();

        // NOVA LISTA (DESENVOLVIMENTO)
//        for (List list : listax) {
//
//            ExtratoTelaObject eto
//                    = new ExtratoTelaObject(
//                            false,
//                            list.get(0),
//                            list.get(1),
//                            list.get(2),
//                            list.get(3),
//                            list.get(4),
//                            list.get(5),
//                            list.get(6),
//                            list.get(7),
//                            list.get(8),
//                            list.get(9),
//                            list.get(10),
//                            list.get(11),
//                            list.get(12),
//                            list.get(13),
//                            list.get(14),
//                            list.get(15),
//                            list.get(16),
//                            list.get(17),
//                            list.get(18),
//                            list.get(19),
//                            list.get(20),
//                            list.get(21),
//                            list.get(22),
//                            new ArrayList(),
//                            new Double(0),
//                            new Double(0),
//                            false,
//                            "",
//                            null
//                    );
//            if (eto.getServico_tipo().toUpperCase().equals("ACORDO")) {
//                eto.setListMovimentoAcordo(movimentosDao.pesquisaAcordoPorMovimento(eto.getId()));
//            }
//
//            // SOMA
//            eto.setSoma(Moeda.subtracao(
//                    Moeda.soma(
//                            Moeda.soma(
//                                    Moeda.soma(
//                                            eto.getValor_baixa(), eto.getJuros()
//                                    ),
//                                    eto.getCorrecao()
//                            ), eto.getMulta()
//                    ), eto.getDesconto())
//            );
//            // HABILITA DATA
//            eto.setHabilita_data((eto.getQuitacao() == null && eto.getServico_tipo().toUpperCase().equals("ACORDO")));
//
//            // REPASSE
//            eto.setSoma_repasse(Moeda.multiplicar(eto.getValor_baixa(), Moeda.divisao(eto.getRepasse(), 100)));
//
//            // ROW STYLE CLASS
//            eto.setRowStyleClass((eto.getQuitacao() == null && !porPesquisa.equals("excluidos")) ? "tblExtratoTelaX" : "");
//
//            // MOVIMENTO
//            eto.setMovimento((Movimento) new Dao().find(new Movimento(), eto.getId()));
//
//            listMovimentos.add(eto);
//        }
        // LISTA ATUALMENTE USADA
        for (Vector linha_list : listax) {
            if ((linha_list.get(21)) == null) {
                linha_list.set(21, 0.0);
            }
            if ((linha_list.get(9)) == null) {
                linha_list.set(9, 0.0);
            }
            if ((linha_list.get(13)) == null) {
                linha_list.set(13, 0.0);
            }
            if ((linha_list.get(14)) == null) {
                linha_list.set(14, 0.0);
            }
            if ((linha_list.get(15)) == null) {
                linha_list.set(15, 0.0);
            }
            if ((linha_list.get(16)) == null) {
                linha_list.set(16, 0.0);
            }
            if ((linha_list.get(17)) == null) {
                linha_list.set(17, 0.0);
            }
            double valor_baixa = Double.parseDouble(Double.toString((Double) linha_list.get(21))),
                    valor = Double.parseDouble(Double.toString((Double) linha_list.get(8))),
                    taxa = Double.parseDouble(Double.toString((Double) linha_list.get(9)));

            soma = Moeda.subtracao(Moeda.soma(
                    Moeda.soma(
                            Moeda.soma(
                                    Double.parseDouble(Double.toString((Double) linha_list.get(21))),//valor
                                    Double.parseDouble(Double.toString((Double) linha_list.get(14)))//juros
                            ),
                            Double.parseDouble(Double.toString((Double) linha_list.get(15)))//correcao
                    ), Double.parseDouble(Double.toString((Double) linha_list.get(13))) //multa
            ), Double.parseDouble(Double.toString((Double) linha_list.get(16))));// desconto

            // ROGÉRIO PEDIU PARA ESSE CALCULO SER PELO VALOR BAIXA, SENDO QUE EM UM CASO DE TESTE NÃO BATEU, APENAS COM O VALOR (soma)
            somaRepasse = Moeda.multiplicar(valor_baixa,
                    Moeda.divisao(
                            Double.parseDouble(Double.toString((Double) linha_list.get(17))), 100));

// ALTERADO PARA BATER COM O RELATÓRIO RESUMO DE CONTRIBUIÇÕES > Menu Financeiro > Relatório > Movimento
//            somaRepasse = Moeda.multiplicar(
//                    soma,
//                    Moeda.divisao(
//                            Double.parseDouble(Double.toString((Double) linha_list.get(17))), 100)
//            );
            if (linha_list.get(12) == null
                    && ((String) linha_list.get(11)).equals("Acordo")) {
                habData = true;
            } else {
                habData = false;
            }

            if (linha_list.get(12) == null) {
                if (!porPesquisa.equals("excluidos")) {
                    classTbl = "tblExtratoTelaX";
                }
            } else {
                classTbl = "";
            }

            List<Movimento> listMovimentoAcordo = new ArrayList();

            if (linha_list.get(11).toString().toUpperCase().equals("ACORDO")) {
                listMovimentoAcordo = movimentosDao.pesquisaAcordoPorMovimento(((Integer) linha_list.get(0)));
            }
            Movimento m = (Movimento) new Dao().find(new Movimento(), ((Integer) linha_list.get(0)));
            listaMovimentos.add(new DataObject(
                    false,
                    ((Integer) linha_list.get(0)), //ARG 1 id
                    linha_list.get(13), // ARG 2 multa
                    soma, // ARG 3 soma
                    linha_list.get(1), // ARG 4 documento
                    linha_list.get(2), // ARG 5 nome
                    linha_list.get(3), // ARG 6 boleto
                    linha_list.get(4), // ARG 7 contribuicao
                    linha_list.get(5), // ARG 8 referencia
                    DataHoje.converteData((Date) linha_list.get(6)), // ARG 9 vencimento
                    DataHoje.converteData((Date) linha_list.get(7)), // ARG 10 importacao
                    Moeda.converteR$Double(valor), // ARG 11 valor
                    Moeda.converteR$Double(taxa), // ARG 12 taxa
                    linha_list.get(10), // ARG 13 nomeUsuario
                    linha_list.get(11), // ARG 14 tipo
                    DataHoje.converteData((Date) linha_list.get(12)),// ARG 15 quitacao
                    linha_list.get(14), // ARG 16 juros
                    linha_list.get(15), // ARG 17 correcao
                    linha_list.get(16),// ARG 18 desconto
                    linha_list.get(17), // ARG 19 repasse
                    somaRepasse,// ARG 20 somaRepasse
                    habData, // ARG 21 boolean habilita data
                    linha_list.get(18), // ARG 22 lote baixa
                    linha_list.get(19), // ARG 23 beneficiario
                    linha_list.get(20), // ARG 24 filial
                    Moeda.converteR$Double(valor_baixa), // ARG 25 valor_baixa
                    classTbl, // ARG 26 null
                    listMovimentoAcordo, // ARG 27 MOVIMENTOS ACORDO
                    linha_list.get(22), // ARG 28 null
                    m, //  ARG 29 MOVIMENTO
                    linha_list.get(24), //  ARG 30 id_boleto
                    linha_list.get(25), //  ARG 31 Data ocorrência
                    null,
                    null,
                    null
            )
            );

            if (linha_list.get(12) != null) {
                vlRecebido = somarValores(valor_baixa, vlRecebido);
                vlTaxa = somarValores(taxa, vlTaxa);
            } else {
                vlNaoRecebido = somarValores(valor, vlNaoRecebido);
            }

            vlTotal = somarValores(valor_baixa, vlTotal);

            double contaLiquido = Moeda.subtracao(valor_baixa, taxa);
            vlLiquido = somarValores(contaLiquido, vlLiquido);

            vlRepasse = somarValores(somaRepasse, vlRepasse);
        }
        vlRepasse = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(vlLiquido), Moeda.converteUS$(vlRepasse)));
    }

    public void limparPesquisaPessoa() {
        pessoa = new Pessoa();
        listaEmpresasPertencentes = new ArrayList();
        movimentosDasEmpresas = false;
        listaMovimentos = new ArrayList();
        listMovimentos = new ArrayList();
        pessoa_documento = "";
    }

    public void limparDatas() {
        tipoData = "faixa";
        if (tipoDataPesquisa.equals("referencia")) {
            dataInicial = "";
            dataFinal = "";
        } else {
            dataRefInicial = "";
            dataRefFinal = "";
        }
    }

    public void limparTipoDatas() {
        dataInicial = "";
        dataFinal = "";
        dataRefInicial = "";
        dataRefFinal = "";
    }

    // SOMA DOS VALORES //
    public String somarValores(double valor, String valorString) {
        Double somaFloat = Moeda.soma(valor, Moeda.converteUS$(valorString));
        return Moeda.converteR$Double(somaFloat);
    }

    public String getVlRecebido() {
        return vlRecebido;
    }

    public String getVlNaoRecebido() {
        return vlNaoRecebido;
    }

    public String getVlTotal() {
        return vlTotal;
    }

    public String getVlTaxa() {
        return vlTaxa;
    }

    public String getVlLiquido() {
        return vlLiquido;
    }

    public String getVlRepasse() {
        return vlRepasse;
    }

    public List<SelectItem> getListaServico() {
        if (listaServico.isEmpty()) {
            ServicosDao db = new ServicosDao();
            List<Servicos> select = db.pesquisaTodos(4);

            listaServico.add(new SelectItem(0, "-- Selecione um Serviço --", "0"));
            for (int i = 0; i < select.size(); i++) {
                listaServico.add(new SelectItem(i + 1,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId())
                ));
            }
        }
        return listaServico;
    }

    public List<SelectItem> getListaTipoServico() {
        if (listaTipoServico.isEmpty()) {
            TipoServicoDao db = new TipoServicoDao();
            List<TipoServico> select = db.pesquisaTodos();

            listaTipoServico.add(new SelectItem(0, "-- Selecione um Tipo --", "0"));
            for (int i = 0; i < select.size(); i++) {
                listaTipoServico.add(new SelectItem(i + 1,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId())
                ));
            }
        }
        return listaTipoServico;
    }

    public String novo() {
        pessoa = new Pessoa();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
        return "extratoTela";
    }

    public String novoDelete() {
        return "extratoTela";
    }

    public boolean getUltimaImpressao(int id_movimento) {
        // ESTA CAUSANDO LENTIDÃO NA ROTINA
//        MovimentoDao db = new MovimentoDao();
//
//        List<Impressao> lista_result = db.listaImpressao(id_movimento);
//
//        if (!lista_result.isEmpty()) {
//            return true;//lista_result.get(0).getImpressao();
//        } else {
//
//        }
        return true;
    }

    public String verUltimaImpressão(int id_movimento) {
        MovimentoDao db = new MovimentoDao();
        listaImpressao = db.listaImpressao(id_movimento);
        listaImpressaoWeb = db.listaImpressaoWeb(id_movimento);
        return null;
    }

    public List<Impressao> getListaImpressao() {
        return listaImpressao;
    }

    public List<DataObject> getListaMovimentos() {
        return listaMovimentos;
    }

    public String getQntBoletos() {
        String n;
        if (!listaMovimentos.isEmpty()) {
            n = Integer.toString(listaMovimentos.size());
        } else {
            n = "0";
        }
        return n;
    }

    public String baixaBoletos() {

        return null;
    }

    public String excluirBoleto() {
        MovimentoDao db = new MovimentoDao();
        if (listaMovimentos.isEmpty()) {
            return null;
        }
        if (bltQuitados() == true) {
            msgConfirma = "Boletos quitados não podem ser Excluídos!";
            return null;
        }
        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            return null;
        }
        if (bltAcordo() == true) {
            msgConfirma = "Boletos do tipo acordo não podem ser Excluídos";
            return null;
        }

        boolean exc = true;

        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())) {
                StatusRetornoMensagem sr = GerarMovimento.excluirUmMovimento(db.pesquisaCodigo((Integer) listaMovimentos.get(i).getArgumento1()));
                if (!sr.getStatus()) {
                    exc = false;
                }
            }
        }

        if (!exc) {
            msgConfirma = "Ocorreu um erro em uma das exclusões, verifique o log!";
        } else {
            msgConfirma = "Boleto excluído com sucesso!";
        }

        loadListBeta();

        return null;
    }

    public String inativarBoleto() {
        MovimentoDao db = new MovimentoDao();
        if (listaMovimentos.isEmpty()) {
            msgConfirma = "Lista Vazia!";
            GenericaMensagem.warn("Atenção", "Lista Vazia!");
            return null;
        }
        if (bltQuitados() == true) {
            msgConfirma = "Boletos quitados não podem ser excluídos!";
            GenericaMensagem.error("Atenção", "Boletos quitados não podem ser excluídos!");
            return null;
        }
        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            GenericaMensagem.warn("Atenção", "Nenhum Boleto Selecionado!");
            return null;
        }
        if (bltAcordo() == true) {
            msgConfirma = "Boletos do tipo acordo não podem ser Excluídos";
            GenericaMensagem.error("Atenção", "Boletos do tipo acordo não podem ser Excluídos");
            return null;
        }

        if (historico.isEmpty()) {
            msgConfirma = "Digite um motivo para exclusão!";
            GenericaMensagem.error("Atenção", "Digite um motivo para exclusão!");
            return null;
        } else if (historico.length() < 6) {
            msgConfirma = "Motivo de exclusão inválido!";
            GenericaMensagem.error("Atenção", "Motivo de exclusão inválido!");
            return null;
        }

        boolean exc = true;
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())) {
                if (!GerarMovimento.inativarUmMovimento(db.pesquisaCodigo((Integer) listaMovimentos.get(i).getArgumento1()), historico).isEmpty()) {
                    exc = false;
                }
            }
        }
        if (!exc) {
            msgConfirma = "Ocorreu um erro em uma das exclusões, verifique o log!";
            GenericaMensagem.error("ERRO", "Ocorreu um erro em uma das exclusões, verifique o log!");
        } else {
            msgConfirma = "Boleto excluído com sucesso!";
            GenericaMensagem.info("OK", "Boleto excluído com sucesso!");
        }

        loadListBeta();

        PF.update("formExtratoTela:i_msg");
        PF.update("formExtratoTela:tbl");

        PF.closeDialog("dlg_excluir");
        return null;
    }

    public boolean bltQuitados() {
        boolean result = false;
        if (!listaMovimentos.isEmpty()) {
            int i = 0;
            while (i < listaMovimentos.size()) {
                if ((Boolean) listaMovimentos.get(i).getArgumento0()) {
                    if (listaMovimentos.get(i).getArgumento15() != null) {
                        if (!listaMovimentos.get(i).getArgumento15().equals("")) {
                            result = true;
                            break;
                        }
                    } else {
                        result = false;
                    }
                }
                i++;
            }
        }
        return result;
    }

    public boolean bltSelecionados() {
        for (int i = 0; i < listaMovimentos.size(); i++) {
            boolean b = (Boolean) listaMovimentos.get(i).getArgumento0();
            if (b) {
                return true;
            }
        }
        return false;
    }

    public boolean bltAcordo() {
        boolean result = false;
        if (!listaMovimentos.isEmpty()) {
            int i = 0;
            while (i < listaMovimentos.size()) {
                if ((Boolean) listaMovimentos.get(i).getArgumento0()) {
                    if (listaMovimentos.get(i).getArgumento14().equals("Acordo")) {
                        result = true;
                        break;
                    } else {
                        result = false;
                    }
                }
                i++;
            }
        }
        return result;
    }

//    public String linkVoltarBaixaMovimento() {
//        linkVoltar = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
//        if (linkVoltar == null) {
//            return "menuPrincipal";
//        } else {
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("urlRetorno");
//        }
//        return linkVoltar;
//    }
//    public String getValorTotal() {
//        getSomarBoletoSelecionados();
//        String v = "";
//        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("valorTotalExtrato") != null) {
//            v = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("valorTotalExtrato");
//        } else {
//            v = "R$ ";
//        }
//        return v;
//    }
    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String estornarBaixa() {
        MovimentoDao db = new MovimentoDao();
        if (listaMovimentos.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Lista vazia!");
            return null;
        }

        if (bltSelecionados() != true) {
            GenericaMensagem.warn("Atenção", "Nenhum Boleto Selecionado!");
            return null;
        }

        if (motivoEstorno.isEmpty() || motivoEstorno.length() <= 5) {
            GenericaMensagem.error("Atenção", "Motivo de Estorno INVÁLIDO!");
            return null;
        }

        for (DataObject listaMovimento : listaMovimentos) {
            if ((Boolean) listaMovimento.getArgumento0()) {
                StatusRetornoMensagem sr = GerarMovimento.estornarMovimento(db.pesquisaCodigo((Integer) listaMovimento.getArgumento1()), motivoEstorno);
                if (!sr.getStatus()) {
                    msgConfirma = sr.getMensagem();
                    GenericaMensagem.error("ERRO", sr.getMensagem());
                    return null;
                }
            }
        }

        GenericaMensagem.info("OK", "Boletos estornados com sucesso!");

        loadListBeta();

        PF.update("formExtratoTela:i_msg");
        PF.update("formExtratoTela:tbl");

        PF.closeDialog("dlg_estornar");
        return null;
    }

    public String imprimirPromissoria() {
        List<Movimento> listaC = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = new Movimento();
        Acordo acordo = new Acordo();
        AcordoDao dbAc = new AcordoDao();
        Historico historico = new Historico();
        Dao dao = new Dao();
        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            GenericaMensagem.warn("Atenção", "Nenhum Boleto Selecionado!");
            return null;
        }

        int qnt = 0;
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())) {
                qnt++;
                if (qnt > 1) {
                    msgConfirma = "Somente um acordo pode ser selecionado!";
                    GenericaMensagem.warn("Atenção", "Somente um acordo pode ser selecionado!");
                    return null;
                }
                movimento = db.pesquisaCodigo((Integer) listaMovimentos.get(i).getArgumento1());
                if (movimento.getAcordo() != null) {
                    acordo = (Acordo) dao.find(new Acordo(), movimento.getAcordo().getId());
                    if (acordo != null) {
                        listaC.addAll(db.pesquisaAcordoAberto(acordo.getId()));
                    }
                } else {
                    msgConfirma = "Não existe acordo para este boleto!";
                    GenericaMensagem.warn("Atenção", "Não existe acordo para este boleto!");
                    return null;
                }
            }
        }

        if (!listaC.isEmpty()) {
            historico = dbAc.pesquisaHistorico(listaC.get(0).getId());
            Boleto boleto = db.pesquisaBoletos(listaC.get(0).getNrCtrBoleto());
            if (historico == null && boleto != null) {
                historico = dbAc.pesquisaHistoricoBaixado(boleto.getContaCobranca().getId(),
                        boleto.getBoletoComposto(),
                        listaC.get(0).getServicos().getId());
            }
            if (historico != null) {
                ImprimirBoleto imp = new ImprimirBoleto();
                imp.imprimirPromissoria(listaC, false);
                imp.visualizar(null);
            } else {
                msgConfirma = "Não existe histórico para este acordo!";
                GenericaMensagem.warn("Atenção", "Não existe histórico para este acordo!");
            }
        } else {
            msgConfirma = "Nenhum Acordo encontrado!";
            GenericaMensagem.warn("Atenção", "Nenhum Acordo encontrado!");
        }
        return null;
    }

    public Boolean validaImprimir() {
        if (listaBoletoRemessa.isEmpty()) {
            GenericaMensagem.error("Atenção", "NENHUM BOLETO PARA GERAR ARQUIVO");
            return false;
        }

        return true;
    }

    public String imprimirRemessa() {
//        MovimentoDao db = new MovimentoDao();
//        List<Movimento> lista_m = new ArrayList();

        if (!validaImprimir()) {
            return null;
        }

//        for (BoletoRemessa br : listaBoletoRemessa) {
//            lista_m.addAll(br.getBoleto().getListaMovimento());
//        }
        ImprimirBoleto imp = new ImprimirBoleto();

//        lista_m = imp.atualizaContaCobrancaMovimento(lista_m);
        RespostaArquivoRemessa RESP = imp.imprimirRemessa(listaBoletoRemessa);

        if (RESP.getArquivo() == null) {
            GenericaMensagem.error("ATENÇÃO", RESP.getMensagem());
            return null;
        }

        imp.visualizar_remessa(RESP.getArquivo());

        return null;
    }

    public String imprimir() {
        return imprimir(false);
    }

    public String imprimir(Boolean download) {
        MovimentoDao db = new MovimentoDao();
        List<Movimento> listaC = new ArrayList();

        if (listaMovimentos.isEmpty()) {
            GenericaMensagem.error("Atenção", "NENHUM BOLETO PARA IMPRIMIR");
            return null;
        }

        Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        Dao dao = new Dao();
        dao.openTransaction();
        for (DataObject listaMovimento : listaMovimentos) {
            if ((Boolean) listaMovimento.getArgumento0()) {
                Movimento m = db.pesquisaCodigo((Integer) listaMovimento.getArgumento1());
                Boleto b = m.getBoleto();

                if (b.getContaCobranca().getCobrancaRegistrada().getId() != 3) {
                    if (DataHoje.menorData(b.getDtVencimento(), DataHoje.dataHoje()) && (b.getStatusRetorno() == null || b.getStatusRetorno().getId() != 2)) {
                        GenericaMensagem.error("Atenção", "Imprimir pela Rotina de Impressão Individual");
                        return null;
                    }
                }

                listaC.add(m);

                Impressao impressao = new Impressao();

                impressao.setUsuario(usuario);
                impressao.setDtVencimento(m.getDtVencimento());
                impressao.setMovimento(m);

                if (!dao.save(impressao)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
                    return null;
                }

            }
        }
        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();

        listaC = imp.atualizaContaCobrancaMovimento(listaC);

        imp.imprimirBoleto(listaC, imprimirVerso, false);

        if (download) {
            imp.baixarArquivo();
        } else {
            imp.visualizar(null);
        }

        loadListBeta(0);
        return null;
    }

    public boolean isImprimirVerso() {
        return imprimirVerso;
    }

    public void setImprimirVerso(boolean imprimirVerso) {
        this.imprimirVerso = imprimirVerso;
    }

    public String enviarEmail() {
        //Movimento movimento = new Movimento();
        Juridica juridica = new Juridica();

        JuridicaDao dbj = new JuridicaDao();
        MovimentoDao dbM = new MovimentoDao();

        List<Movimento> movadd = new ArrayList();

        //List<Linha> select  = new ArrayList();
        List<Movimento> listaux = new ArrayList();
        boolean enviar = false;
        int id_contabil = 0, id_empresa = 0, id_compara = 0;

        if (listaMovimentos.isEmpty()) {
            msgConfirma = "Lista vazia!";
            GenericaMensagem.warn("Atenção", "Lista vazia!");
            return null;
        }

        for (int i = 0; i < listaMovimentos.size(); i++) {
            if ((Boolean) listaMovimentos.get(i).getArgumento0()) {
                Movimento mo = (Movimento) new Dao().find(new Movimento(), (Integer) listaMovimentos.get(i).getArgumento1());
                if (mo.getBaixa() != null) {
                    msgConfirma = "Não pode enviar email de boletos quitados!";
                    GenericaMensagem.error("Atenção", "Não pode enviar email de boletos quitados! - Boleto: " + mo.getDocumento());
                    return null;
                } else {
                    listaux.add(mo);
                }
            }
        }

        if (listaux.isEmpty()) {
            msgConfirma = "Nenhum boleto selecionado";
            GenericaMensagem.warn("Atenção", "Nenhum boleto selecionado");
            return null;
        }

        for (int i = 0; i < listaux.size(); i++) {
            juridica = dbj.pesquisaJuridicaPorPessoa(listaux.get(i).getPessoa().getId());

            /* ENVIO PARA CONTABILIDADE */
            if (tipoEnvio.equals("contabilidade")) {
                if (juridica.getContabilidade() != null) {
                    id_contabil = juridica.getContabilidade().getId();
                } else {
                    msgConfirma = "Empresa " + juridica.getPessoa().getNome() + " não pertence a nenhuma contabilidade";
                    GenericaMensagem.error("Atenção", "Empresa " + juridica.getPessoa().getNome() + " não pertence a nenhuma contabilidade");
                    return null;
                }
                movadd.add(listaux.get(i));

                try {
                    id_compara = dbj.pesquisaJuridicaPorPessoa(listaux.get(i + 1).getPessoa().getId()).getContabilidade().getId();
                    if (id_contabil != id_compara) {
                        enviar = true;
                    }
                } catch (Exception e) {
                    enviar = true;
                }

                if (enviar) {
                    enviar(movadd, juridica.getContabilidade());
                    enviar = false;
                    movadd.clear();
                }
                /* ENVIO PARA EMPRESA */
            } else {
                id_empresa = juridica.getId();

                movadd.add(listaux.get(i));

                try {
                    id_compara = dbj.pesquisaJuridicaPorPessoa(listaux.get(i + 1).getPessoa().getId()).getId();
                    if (id_empresa != id_compara) {
                        enviar = true;
                    }
                } catch (Exception e) {
                    enviar = true;
                }

                if (enviar) {
                    enviar(movadd, juridica);
                    enviar = false;
                    movadd.clear();
                }
            }

        }

        PF.update("formExtratoTela:i_msg");
        PF.closeDialog("dlg_enviar_email");
        return null;
    }

    public void enviar(List<Movimento> mov, Juridica jur) {
        try {

            Registro reg = Registro.get();

            ImprimirBoleto imp = new ImprimirBoleto();
            imp.imprimirBoleto(mov, false, false);
            String nome = imp.criarLink(jur.getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            List<Pessoa> pessoas = new ArrayList();
            pessoas.add(jur.getPessoa());

            List<File> fls = new ArrayList();
            String nome_envio;
            if (mov.size() == 1) {
                nome_envio = "Boleto " + mov.get(0).getServicos().getDescricao() + " N° " + mov.get(0).getDocumento();
            } else {
                nome_envio = "Boleto";
            }

            String mensagem;
            if (!reg.isEnviarEmailAnexo()) {
                mensagem = " <div style='background:#00ccff; padding: 15px; font-size:13pt'>Enviado para <b>" + jur.getPessoa().getNome() + " </b></div><br />"
                        + " <h5>Visualize seu boleto clicando no link abaixo</h5><br /><br />"
                        + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "'>Clique aqui para abrir boleto</a><br />";
            } else {
                fls.add(new File(imp.getPathPasta() + "/" + nome));
                mensagem = " <div style='background:#00ccff; padding: 15px; font-size:13pt'>Enviado para <b>" + jur.getPessoa().getNome() + " </b></div><br />"
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

            if (!retorno[1].isEmpty()) {
                msgConfirma = retorno[1];
                GenericaMensagem.warn("Erro", msgConfirma);
            } else {
                msgConfirma = retorno[0];
                GenericaMensagem.info("Sucesso", msgConfirma);
            }
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());

        }
    }

    public String imprimirPlanilha() {
        List<Movimento> listaC = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = new Movimento();
        Acordo acordo = new Acordo();
        Dao dao = new Dao();
        AcordoDao dbAc = new AcordoDao();
        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            GenericaMensagem.warn("Atenção", "Nenhum Boleto Selecionado!");
            return null;
        }
        int qnt = 0;
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())) {
                qnt++;
                if (qnt > 1) {
                    msgConfirma = "Somente um acordo pode ser selecionado!";
                    GenericaMensagem.warn("Atenção", "Somente um acordo pode ser selecionado!");
                    return null;
                }
                movimento = db.pesquisaCodigo((Integer) listaMovimentos.get(i).getArgumento1());
                if (movimento.getAcordo() != null) {
                    acordo = (Acordo) dao.find(new Acordo(), movimento.getAcordo().getId());
                    if (acordo != null) {
                        listaC.addAll(db.pesquisaAcordoTodos(acordo.getId()));
                    }
                } else {
                    msgConfirma = "Não existe acordo para este boleto!";
                    GenericaMensagem.warn("Atenção", "Não existe acordo para este boleto!");
                    return null;
                }
            }
        }

        if (!listaC.isEmpty()) {
            int ind = 0;
            for (int i = 0; i < listaC.size(); i++) {
                if (listaC.get(i).isAtivo()) {
                    ind = i;
                }
            }

            List hist = dbAc.listaHistoricoAgrupado(listaC.get(ind).getAcordo().getId());
            Boleto boleto = db.pesquisaBoletos(listaC.get(ind).getNrCtrBoleto());
            if (hist.isEmpty() && boleto != null) {
                Historico h = dbAc.pesquisaHistoricoBaixado(boleto.getContaCobranca().getId(),
                        boleto.getBoletoComposto(),
                        listaC.get(ind).getServicos().getId());
                if (h != null) {
                    hist.add(h.getHistorico());
                }
            }
            if (!hist.isEmpty()) {
                String s_hist = "Acordo correspondente a: ";
                for (int i = 0; i < hist.size(); i++) {
                    s_hist += " " + hist.get(i);
                }
                ImprimirBoleto imp = new ImprimirBoleto();
                imp.imprimirAcordoAcordado(listaC, acordo, s_hist, imprimirVerso);
                imp.visualizar(null);
            } else {
                msgConfirma = "Não existe histórico para este acordo!";
                GenericaMensagem.warn("Atenção", "Não existe histórico para este acordo!");
            }
        } else {
            msgConfirma = "Nenhum Acordo encontrado!";
            GenericaMensagem.warn("Atenção", "Nenhum Acordo encontrado!");
        }
        return null;
    }

    public String excluirAcordo() {

        List<Movimento> listaC = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        Dao dao = new Dao();
        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            GenericaMensagem.warn("Atenção", "Nenhum Boleto Selecionado!");
            return null;
        }
        int qnt = 0;
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())) {
                qnt++;
                if (qnt > 1) {
                    msgConfirma = "Somente um acordo pode ser selecionado!";
                    GenericaMensagem.warn("Atenção", "Somente um acordo pode ser selecionado!");
                    return null;
                }
                Movimento m = (Movimento) dao.find(new Movimento(), (Integer) listaMovimentos.get(i).getArgumento1());
                if (m != null && m.getAcordo() != null) {
                    if (m.getAcordo().getId() != -1) {
                        listaC.addAll(db.pesquisaAcordoParaExclusao(m.getAcordo().getId()));
                    }
                } else {
                    msgConfirma = "Não existe acordo para este boleto!";
                    GenericaMensagem.warn("Atenção", "Não existe acordo para este boleto!");
                    return null;
                }
                for (int w = 0; w < listaC.size(); w++) {
                    if (listaC.get(w).getBaixa() != null && listaC.get(w).isAtivo()) {
                        msgConfirma = "Acordo com parcela já paga não pode ser excluído!";
                        GenericaMensagem.warn("Atenção", "Acordo com parcela já paga não pode ser excluído!");
                        return null;
                    }
                }
            }
        }

        if (!listaC.isEmpty()) {
            String ids = "";
            for (int i = 0; i < listaC.size(); i++) {
                if (ids.length() > 0 && i != listaC.size()) {
                    ids = ids + ",";
                }
                ids = ids + String.valueOf(listaC.get(i).getId());
            }

            if (ids.isEmpty()) {
                return null;
            } else {
                db.excluirAcordoIn(ids, listaC.get(0).getAcordo().getId());
            }

            loadListBeta();
            msgConfirma = "Acordo Excluído com sucesso!";
            GenericaMensagem.info("OK", "Acordo Excluído com sucesso!");

            PF.update("formExtratoTela:i_msg");
            PF.update("formExtratoTela:tbl");

            PF.closeDialog("dlg_acordo");
        } else {
            msgConfirma = "Nenhum Acordo encontrado!";
            GenericaMensagem.warn("Atenção", "Nenhum Acordo encontrado!");
        }
        return null;
    }

    public String carregaDataAntiga(int id_movimento) {
        movimentoVencimento = (Movimento) new Dao().find(new Movimento(), id_movimento);
        dataAntiga = movimentoVencimento.getVencimento();
        dataNova = "";
        return null;
    }

    public String atualizarData() {
        // APENAS ALTERA O VENCIMENTO DE TIPO SERVICO ACORDO
        if (!dataNova.isEmpty()) {
            if (DataHoje.converteDataParaInteger(dataNova) >= DataHoje.converteDataParaInteger(DataHoje.data())) {

                Boleto b = movimentoVencimento.getBoleto();

                Dao di = new Dao();

                di.openTransaction();

                // COBRANCA REGISTRADA
                if (b.getContaCobranca().getCobrancaRegistrada().getId() != 3) {
                    // registrado
                    if (b.getStatusRetorno() != null && b.getStatusRetorno().getId() == 2) {
                        b.setDtCobrancaRegistrada(DataHoje.dataHoje());
                        b.setDtStatusRetorno(DataHoje.dataHoje());
                        b.setStatusRetorno((StatusRetorno) di.find(new StatusRetorno(), 6));
                    }
                }

                movimentoVencimento.setVencimento(dataNova);
                b.setVencimento(dataNova);

                if (!di.update(movimentoVencimento) || !di.update(b)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível alterar o movimento, tente novamente!");
                    return null;
                }

                di.commit();

                movimentoVencimento = new Movimento();
                loadListBeta();
                GenericaMensagem.info("OK", "Data alterada com sucesso!");

                PF.update("formExtratoTela:i_msg");
                PF.update("formExtratoTela:tbl");

                PF.closeDialog("dlg_alterar_vencimento");
            } else {
                GenericaMensagem.warn("Atenção", "A nova data deve ser MAIOR ou IGUAL a data de hoje!");
            }
        }

        return null;
    }

    public String getDataAntiga() {
        return dataAntiga;
    }

    public void setDataAntiga(String dataAntiga) {
        this.dataAntiga = dataAntiga;
    }

    public String getDataNova() {
        return dataNova;
    }

    public void setDataNova(String dataNova) {
        this.dataNova = dataNova;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            GenericaSessao.remove("pessoaPesquisa");
            loadListBeta();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public String getValorExtenso() {
        if (!listaMovimentos.isEmpty() && !vlLiquido.isEmpty()) {
            ValorExtenso ve = new ValorExtenso();
            ve.setNumber(Double.valueOf(Double.toString(Moeda.substituiVirgulaDouble(vlLiquido))));
            return valorExtenso = ve.toString();
        } else {
            return valorExtenso = "";
        }
    }

    public void setValorExtenso(String valorExtenso) {
        this.valorExtenso = valorExtenso;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public void refreshForm() {
    }

    public String refreshTela() {
        return "extratoTela";
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getGeraPesquisa() {
        return geraPesquisa;
    }

    public void setGeraPesquisa(String geraPesquisa) {
        this.geraPesquisa = geraPesquisa;
    }

    public String getTipoDataPesquisa() {
        return tipoDataPesquisa;
    }

    public void setTipoDataPesquisa(String tipoDataPesquisa) {
        this.tipoDataPesquisa = tipoDataPesquisa;
    }

    public int getIdContribuicao() {
        return idContribuicao;
    }

    public void setIdContribuicao(int idContribuicao) {
        this.idContribuicao = idContribuicao;
    }

    private Pessoa getPesquisaPessoa() {
        Pessoa p = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pessoaPesquisa");
        if (p == null) {
            p = new Pessoa();
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
        }
        return p;
    }

    public boolean isChkData() {
        return chkData;
    }

    public void setChkData(boolean chkData) {
        this.chkData = chkData;
    }

    public boolean isChkContribuicao() {
        return chkContribuicao;
    }

    public void setChkContribuicao(boolean chkContribuicao) {
        this.chkContribuicao = chkContribuicao;
    }

    public boolean isChkNrBoletos() {
        return chkNrBoletos;
    }

    public void setChkNrBoletos(boolean chkNrBoletos) {
        this.chkNrBoletos = chkNrBoletos;
    }

    public boolean isChkEmpresa() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pessoaPesquisa") != null) {
            chkEmpresa = true;
        }
        return chkEmpresa;
    }

    public void setChkEmpresa(boolean chkEmpresa) {
        this.chkEmpresa = chkEmpresa;
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

    public Movimento getMov() {
        return mov;
    }

    public void setMov(Movimento mov) {
        this.mov = mov;
    }

    public int getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(int idTipoServico) {
        this.idTipoServico = idTipoServico;
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

    public boolean isChkTipo() {
        return chkTipo;
    }

    public void setChkTipo(boolean chkTipo) {
        this.chkTipo = chkTipo;
    }

    public boolean isChkExcluirBol() {
        return chkExcluirBol;
    }

    public void setChkExcluirBol(boolean chkExcluirBol) {
        this.chkExcluirBol = chkExcluirBol;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

//    public String getValorSomado() {
//        valorSomado = getValorTotal();
//        return valorSomado;
//    }
//    public void setValorSomado(String valorSomado) {
//        this.valorSomado = valorSomado;
//    }
    public boolean isMovimentosDasEmpresas() {
        return movimentosDasEmpresas;
    }

    public void setMovimentosDasEmpresas(boolean movimentosDasEmpresas) {
        this.movimentosDasEmpresas = movimentosDasEmpresas;
    }

    public List<Juridica> getListaEmpresasPertencentes() {
        return listaEmpresasPertencentes;
    }

    public void setListaEmpresasPertencentes(List<Juridica> listaEmpresasPertencentes) {
        this.listaEmpresasPertencentes = listaEmpresasPertencentes;
    }

    public boolean isDcData() {
        return dcData;
    }

    public void setDcData(boolean dcData) {
        this.dcData = dcData;
    }

    public boolean isDcBoleto() {
        return dcBoleto;
    }

    public void setDcBoleto(boolean dcBoleto) {
        this.dcBoleto = dcBoleto;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Movimento getMovimentoAlterar() {
        return movimentoAlterar;
    }

    public void setMovimentoAlterar(Movimento movimentoAlterar) {
        this.movimentoAlterar = movimentoAlterar;
    }

    public int getIdTipoServicoAlterar() {
        return idTipoServicoAlterar;
    }

    public void setIdTipoServicoAlterar(int idTipoServicoAlterar) {
        this.idTipoServicoAlterar = idTipoServicoAlterar;
    }

    public List<SelectItem> getListaTipoServicoAlterar() {
        if (listaTipoServicoAlterar.isEmpty()) {
            TipoServicoDao db = new TipoServicoDao();
            List<TipoServico> select = db.pesquisaTodos();

            for (int i = 0; i < select.size(); i++) {
                listaTipoServicoAlterar.add(new SelectItem(i,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId())
                ));
            }
        }
        return listaTipoServicoAlterar;
    }

    public String vencimentoOritinal() {
        movimentoVencimento.setVencimento(movimentoVencimento.getVencimentoOriginal());
        Dao di = new Dao();

        di.openTransaction();

        if (!di.update(movimentoVencimento)) {
            di.rollback();
            GenericaMensagem.error("Erro", "Não foi possível alterar o movimento, tente novamente!");
            return null;
        }

        di.commit();

        loadListBeta();
        GenericaMensagem.info("OK", "Data alterada com sucesso!");

        movimentoVencimento = new Movimento();

        PF.update("formExtratoTela:i_msg");
        PF.update("formExtratoTela:tbl");
        PF.update("formExtratoTelaAlteraVencimento:i_msg_vencimento");
        PF.update("formExtratoTelaAlteraVencimento:i_vo");

        PF.closeDialog("dlg_alterar_vencimento");
        return null;
    }

    public Movimento getMovimentoVencimento() {
        return movimentoVencimento;
    }

    public void setMovimentoVencimento(Movimento movimentoVencimento) {
        this.movimentoVencimento = movimentoVencimento;
    }

    public List<Movimento> getListMovimentosAcordo() {
        return listMovimentosAcordo;
    }

    public void setListMovimentosAcordo(List<Movimento> listMovimentosAcordo) {
        this.listMovimentosAcordo = listMovimentosAcordo;
    }

    public void openHistorico(int index) {
        historicoMovimento = new Historico();
        this.index = index;
        historicoMovimento = ((Movimento) listaMovimentos.get(index).getArgumento29()).getHistorico();
        if (historicoMovimento == null) {
            historicoMovimento = new Historico();
        }
    }

    public void saveHistorico() {
        if (historicoMovimento.getHistorico().length() < 10 || historicoMovimento.getHistorico().trim().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR MENSAGEM DO CONTRIBUINTE! DEVE TER NO MÍNIMO 10 CARACTERES");
            return;
        }
        if (historicoMovimento.getComplemento().length() < 10 || historicoMovimento.getComplemento().trim().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR MENSAGEM DO BOLETO! DEVE TER NO MÍNIMO 10 CARACTERES");
            return;

        }
        if (historicoMovimento.getId() != -1) {
            new Dao().update(historicoMovimento, true);
            GenericaMensagem.info("Validação", "MENSAGEM ATUALIZADA COM SUCESSO");
        } else {
            GenericaMensagem.info("Validação", "MENSAGEM INSERIDA COM SUCESSO");
        }
        ((Movimento) listaMovimentos.get(index).getArgumento29()).setHistorico(historicoMovimento);
        this.index = null;
        historicoMovimento = null;
    }

    public void closeHistorico() {
        if (historicoMovimento != null) {
            if (historicoMovimento.getId() == -1 && historicoMovimento.getHistorico().isEmpty() && historicoMovimento.getComplemento().isEmpty()) {
                ((Movimento) listaMovimentos.get(index).getArgumento29()).setHistorico(null);
            }
        }
        this.index = null;
        historicoMovimento = null;
    }

    public Historico getHistoricoMovimento() {
        return historicoMovimento;
    }

    public void setHistoricoMovimento(Historico historicoMovimento) {
        this.historicoMovimento = historicoMovimento;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getMotivoReativacao() {
        return motivoReativacao;
    }

    public void setMotivoReativacao(String motivoReativacao) {
        this.motivoReativacao = motivoReativacao;
    }

    public void reativarMovimentos() {
        if (motivoReativacao.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite um motivo para reativação!");
            return;
        } else if (motivoReativacao.length() < 6) {
            GenericaMensagem.warn("Atenção", "Motivo de válido para reativação! Com mais de 6 caracteres.");
            return;
        }

        List<Movimento> listam = new ArrayList();

        if (baixado()) {
            GenericaMensagem.warn("Atenção", "Boletos BAIXADOS não podem ser reativados!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser reativados!");
            return;
        }

        if (acordados()) {
            GenericaMensagem.warn("Atenção", "Boletos do tipo ACORDO não podem ser reativados!");
            return;
        }

        for (DataObject dh : listaMovimentos) {
            if ((Boolean) dh.getArgumento0()) {
                listam.add(((Movimento) dh.getArgumento29()));
            }
        }

        if (listam.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boletos foi selecionado!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        if (!GerarMovimento.reativarArrayMovimento(listam, motivoReativacao, dao).isEmpty()) {
            GenericaMensagem.error("Erro", "Ao reativar movimento(s), verifique o log!");
            dao.rollback();
            return;
        } else {
            GenericaMensagem.info("Sucesso", "Boletos foram reativados!");
        }

        listaMovimentos.clear();
        dao.commit();
        motivoReativacao = "";
        loadListBeta();
    }

    public boolean baixado() {
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0()) && ((Movimento) (listaMovimentos.get(i).getArgumento29())).getBaixa() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean fechadosCaixa() {
        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if (((Boolean) listaMovimentos.get(i).getArgumento0())
                    && (((Movimento) (listaMovimentos.get(i).getArgumento29())).getBaixa() != null) && (((Movimento) (listaMovimentos.get(i).getArgumento29())).getBaixa().getFechamentoCaixa() != null)) {
                return true;
            }
        }
        return false;
    }

    public boolean acordados() {
        for (int i = 0; i < listaMovimentos.size(); i++) {
            if ((Boolean) listaMovimentos.get(i).getArgumento0() && String.valueOf(listaMovimentos.get(i).getArgumento14()).equals("Acordo")) {
                return true;
            }
        }
        return false;
    }

    public List<ExtratoTelaObject> getListMovimentos() {
        return listMovimentos;
    }

    public void setListMovimentos(List<ExtratoTelaObject> listMovimentos) {
        this.listMovimentos = listMovimentos;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
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

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public String getPessoa_documento() {
        return pessoa_documento;
    }

    public void setPessoa_documento(String pessoa_documento) {
        this.pessoa_documento = pessoa_documento;
    }

    public void findPessoaBy() {
        pessoa = new Pessoa();
        Juridica j = new Juridica();
        if (!pessoa_documento.isEmpty()) {
            pessoa_documento = AnaliseString.somenteNumero(pessoa_documento);
            if (pessoa_documento.length() == 11 && ValidaDocumentos.isValidoCPF(pessoa_documento)) {
                pessoa_documento = Mask.cpf(pessoa_documento);
                j = new JuridicaDao().findByDocumento(pessoa_documento);
                pessoa_documento = "";
                if (j == null) {
                    return;
                }
                pessoa = j.getPessoa();
                loadListBeta();
            }
            if (pessoa_documento.length() == 14 || ValidaDocumentos.isValidoCNPJ(pessoa_documento)) {
                pessoa_documento = Mask.cnpj(pessoa_documento);
                j = new JuridicaDao().findByDocumento(pessoa_documento);
                pessoa_documento = "";
                if (j == null) {
                    return;
                }
                pessoa = j.getPessoa();
                loadListBeta();
            }
        }
    }

    public List<SelectItem> getListaStatusRetorno() {
        return listaStatusRetorno;
    }

    public Integer getIndexListaStatusRetorno() {
        return indexListaStatusRetorno;
    }

    public void setIndexListaStatusRetorno(Integer indexListaStatusRetorno) {
        this.indexListaStatusRetorno = indexListaStatusRetorno;
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

    public Boolean getVerListaRemessa() {
        return verListaRemessa;
    }

    public void setVerListaRemessa(Boolean verListaRemessa) {
        this.verListaRemessa = verListaRemessa;
    }

    public ContaCobranca getContaSelecionada() {
        return contaSelecionada;
    }

    public void setContaSelecionada(ContaCobranca contaSelecionada) {
        this.contaSelecionada = contaSelecionada;
    }

    public StatusRemessa getStatusRemessa() {
        return statusRemessa;
    }

    public void setStatusRemessa(StatusRemessa statusRemessa) {
        this.statusRemessa = statusRemessa;
    }

    public List<ImpressaoWeb> getListaImpressaoWeb() {
        return listaImpressaoWeb;
    }

    public void setListaImpressaoWeb(List<ImpressaoWeb> listaImpressaoWeb) {
        this.listaImpressaoWeb = listaImpressaoWeb;

    }

    public class ExtratoTelaObject {

        private Boolean selected;
        private Integer id;
        private String documento;
        private String nome;
        private String boleto;
        private String contribuicao;
        private String referencia;
        private Date vencimento;
        private Date importacao;
        private Double valor;
        private Double taxa;
        private String usuario_nome;
        private String servico_tipo;
        private Date quitacao;
        private Double multa;
        private Double juros;
        private Double correcao;
        private Double desconto;
        private Double repasse;
        private Integer baixa_id;
        private String beneficiario;
        private String filial;
        private Double valor_baixa;
        private String conta;
        private List<Movimento> listMovimentoAcordo;
        private Double soma;
        private Double soma_repasse;
        private Boolean habilita_data;
        private String rowStyleClass;
        private Movimento movimento;

        public ExtratoTelaObject() {
            this.selected = false;
            this.id = -1;
            this.documento = "";
            this.nome = "";
            this.boleto = "";
            this.contribuicao = "";
            this.referencia = "";
            this.vencimento = null;
            this.importacao = null;
            this.valor = new Double(0);
            this.taxa = new Double(0);
            this.usuario_nome = "";
            this.servico_tipo = "";
            this.quitacao = null;
            this.multa = new Double(0);
            this.juros = new Double(0);
            this.correcao = new Double(0);
            this.desconto = new Double(0);
            this.repasse = new Double(0);
            this.baixa_id = -1;
            this.beneficiario = "";
            this.filial = "";
            this.valor_baixa = new Double(0);
            this.conta = "";
            this.listMovimentoAcordo = new ArrayList();
            this.soma = new Double(0);
            this.soma_repasse = new Double(0);
            this.habilita_data = false;
            this.rowStyleClass = "";
            this.movimento = null;
        }

        /**
         *
         * @param selected
         * @param id
         * @param documento
         * @param nome
         * @param boleto
         * @param contribuicao
         * @param referencia
         * @param vencimento
         * @param importacao
         * @param valor
         * @param taxa
         * @param usuario_nome
         * @param servico_tipo
         * @param quitacao
         * @param multa
         * @param juros
         * @param correcao
         * @param desconto
         * @param repasse
         * @param baixa_id
         * @param beneficiario
         * @param filial
         * @param valor_baixa
         * @param conta
         * @param listMovimentoAcordo
         * @param soma
         * @param soma_repasse
         * @param habilita_data
         */
        public ExtratoTelaObject(Boolean selected, Object id, Object documento, Object nome, Object boleto, Object contribuicao, Object referencia, Object vencimento, Object importacao, Object valor, Object taxa, Object usuario_nome, Object servico_tipo, Object quitacao, Object multa, Object juros, Object correcao, Object desconto, Object repasse, Object baixa_id, Object beneficiario, Object filial, Object valor_baixa, Object conta, List<Movimento> listMovimentoAcordo, Double soma, Double soma_repasse, Boolean habilita_data, String rowStyleClass, Movimento movimento) {
            this.selected = selected;
            this.id = (Integer) id;
            this.documento = (documento == null) ? "" : (String) documento;
            this.nome = (nome == null) ? "" : (String) nome;
            this.boleto = (boleto == null) ? "" : (String) boleto;
            this.contribuicao = (contribuicao == null) ? "" : (String) contribuicao;
            this.referencia = (referencia == null) ? "" : (String) referencia;
            this.vencimento = (Date) vencimento;
            this.importacao = (Date) importacao;
            this.valor = (valor == null) ? new Double(0) : (Double) valor;
            this.taxa = (taxa == null) ? new Double(0) : (Double) taxa;
            this.usuario_nome = (usuario_nome == null) ? "" : (String) usuario_nome;
            this.servico_tipo = (servico_tipo == null) ? "" : (String) servico_tipo;
            this.quitacao = (Date) quitacao;
            this.multa = (multa == null) ? new Double(0) : (Double) multa;
            this.juros = (juros == null) ? new Double(0) : (Double) juros;
            this.correcao = (correcao == null) ? new Double(0) : (Double) correcao;
            this.desconto = (desconto == null) ? new Double(0) : (Double) desconto;
            this.repasse = (repasse == null) ? new Double(0) : (Double) repasse;
            this.baixa_id = (Integer) baixa_id;
            this.beneficiario = (beneficiario == null) ? "" : (String) beneficiario;
            this.filial = (filial == null) ? "" : (String) filial;
            this.valor_baixa = (valor_baixa == null) ? new Double(0) : (Double) valor_baixa;
            this.conta = (conta == null) ? "" : (String) conta;
            this.soma = soma;
            this.soma_repasse = soma_repasse;
            this.habilita_data = habilita_data;
            this.rowStyleClass = rowStyleClass;
            this.movimento = movimento;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getBoleto() {
            return boleto;
        }

        public void setBoleto(String boleto) {
            this.boleto = boleto;
        }

        public String getContribuicao() {
            return contribuicao;
        }

        public void setContribuicao(String contribuicao) {
            this.contribuicao = contribuicao;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public Date getVencimento() {
            return vencimento;
        }

        public void setVencimento(Date vencimento) {
            this.vencimento = vencimento;
        }

        public Date getImportacao() {
            return importacao;
        }

        public void setImportacao(Date importacao) {
            this.importacao = importacao;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public Double getTaxa() {
            return taxa;
        }

        public void setTaxa(Double taxa) {
            this.taxa = taxa;
        }

        public String getUsuario_nome() {
            return usuario_nome;
        }

        public void setUsuario_nome(String usuario_nome) {
            this.usuario_nome = usuario_nome;
        }

        public String getServico_tipo() {
            return servico_tipo;
        }

        public void setServico_tipo(String servico_tipo) {
            this.servico_tipo = servico_tipo;
        }

        public Date getQuitacao() {
            return quitacao;
        }

        public void setQuitacao(Date quitacao) {
            this.quitacao = quitacao;
        }

        public Double getMulta() {
            return multa;
        }

        public void setMulta(Double multa) {
            this.multa = multa;
        }

        public Double getJuros() {
            return juros;
        }

        public void setJuros(Double juros) {
            this.juros = juros;
        }

        public Double getCorrecao() {
            return correcao;
        }

        public void setCorrecao(Double correcao) {
            this.correcao = correcao;
        }

        public Double getDesconto() {
            return desconto;
        }

        public void setDesconto(Double desconto) {
            this.desconto = desconto;
        }

        public Double getRepasse() {
            return repasse;
        }

        public void setRepasse(Double repasse) {
            this.repasse = repasse;
        }

        public Integer getBaixa_id() {
            return baixa_id;
        }

        public void setBaixa_id(Integer baixa_id) {
            this.baixa_id = baixa_id;
        }

        public String getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(String beneficiario) {
            this.beneficiario = beneficiario;
        }

        public String getFilial() {
            return filial;
        }

        public void setFilial(String filial) {
            this.filial = filial;
        }

        public Double getValor_baixa() {
            return valor_baixa;
        }

        public void setValor_baixa(Double valor_baixa) {
            this.valor_baixa = valor_baixa;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public List<Movimento> getListMovimentoAcordo() {
            return listMovimentoAcordo;
        }

        public void setListMovimentoAcordo(List<Movimento> listMovimentoAcordo) {
            this.listMovimentoAcordo = listMovimentoAcordo;
        }

        public Double getSoma() {
            return soma;
        }

        public void setSoma(Double soma) {
            this.soma = soma;
        }

        public Double getSoma_repasse() {
            return soma_repasse;
        }

        public void setSoma_repasse(Double soma_repasse) {
            this.soma_repasse = soma_repasse;
        }

        public Boolean getHabilita_data() {
            return habilita_data;
        }

        public void setHabilita_data(Boolean habilita_data) {
            this.habilita_data = habilita_data;
        }

        public String getRowStyleClass() {
            return rowStyleClass;
        }

        public void setRowStyleClass(String rowStyleClass) {
            this.rowStyleClass = rowStyleClass;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

    }

    public Boolean getApenasBoletoSelecionado() {
        return apenasBoletoSelecionado;
    }

    public void setApenasBoletoSelecionado(Boolean apenasBoletoSelecionado) {
        this.apenasBoletoSelecionado = apenasBoletoSelecionado;
    }

}
