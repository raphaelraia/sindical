package br.com.rtools.arrecadacao.beans;

import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.dao.AcordoDao;
import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.financeiro.*;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.RemessaBancoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Modulo;
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
    private String tipoDataPesquisa = "vencimento";
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

    private String msgConfirma = "";
    //private String valorSomado;
    private String dataAntiga = "";
    private String dataNova = "";
    public boolean imprimirVerso = false;
    private String historico = "";

    private String tipoEnvio = "empresa";
    private String valorExtenso = "";
    private List<Impressao> listaImpressao = new ArrayList();

    private boolean movimentosDasEmpresas = false;
    private List<Juridica> listaEmpresasPertencentes = new ArrayList();
    private boolean dcData = false;
    /* dc = defaultCollapsed */

    private boolean dcBoleto = false;
    private final List<SelectItem> listaTipoServico = new ArrayList();
    private final List<SelectItem> listaTipoServicoAlterar = new ArrayList();
    private final List<SelectItem> listaServico = new ArrayList();
    private Movimento movimentoVencimento = new Movimento();

    private String motivoEstorno = "";
    private Movimento movimentoAlterar = new Movimento();
    private List<Movimento> listMovimentosAcordo = new ArrayList();
    private Historico historicoMovimento;
    private Integer index;
    private String motivoReativacao;

    private ControleAcessoBean cab = new ControleAcessoBean();
    
    public ExtratoTelaBean() {
        GenericaSessao.remove("tipoPesquisaPessoaJuridica");
        
        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");
        
        cab.setModulo((Modulo) new Dao().find(new Modulo(), 3));
        
        motivoReativacao = "";

        if (cab.getListaExtratoTela(false)) {
            porPesquisa = "todos";
        } else {
            porPesquisa = "naoRecebidas";
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

        loadListaEmpresasPertencentes();
        if (tCase == 1) {
            listaMovimentos.clear();
        } else {
            return;
        }
        boolean habData = false;
        double soma = 0, somaRepasse = 0;
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

        List<Vector> listax = db.listaMovimentosExtrato(
                porPesquisa, tipoDataPesquisa, dataInicial, dataFinal, dataRefInicial, dataRefFinal, boletoInicial, boletoFinal, ic, its, pessoa.getId(), ordenacao, movimentosDasEmpresas
        );

        MovimentoDao movimentosDao = new MovimentoDao();

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
                    m //  ARG 29 MOVIMENTO
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

    public void loadList() {
        loadListaEmpresasPertencentes();
        listaMovimentos.clear();
        boolean habData = false;
        double soma = 0, somaRepasse = 0;
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

        if (!chkContribuicao && !chkNrBoletos && !chkEmpresa && !chkTipo && dataInicial.isEmpty() && dataFinal.isEmpty()
                && dataRefInicial.isEmpty() && dataRefFinal.isEmpty() && ic == 0 && its == 0 && boletoInicial.isEmpty() && boletoFinal.isEmpty() && getPessoa().getId() == -1) {
            return;
        }

        Date dtInicial, dtFinal;
        if (!dataInicial.isEmpty() && (!dataFinal.isEmpty())) {
            dtInicial = DataHoje.converte(dataInicial);
            dtFinal = DataHoje.converte(dataFinal);
        } else if (!dataInicial.isEmpty()) {
            dtInicial = DataHoje.converte(dataInicial);
            dtFinal = dtInicial;
        } else {
            dtInicial = DataHoje.dataHoje();
            dtFinal = DataHoje.dataHoje();
        }

        List<Vector> listax = new ArrayList();

        switch (porPesquisa) {
            case "todos":
                listax = db.listaTodosMovimentos(
                        chkData, chkContribuicao, chkNrBoletos, chkEmpresa, chkTipo, tipoDataPesquisa,
                        dtInicial, dtFinal, dataRefInicial, dataRefFinal, ic, its, boletoInicial,
                        boletoFinal, getPessoa().getId(), ordenacao, movimentosDasEmpresas
                );
                break;
            case "recebidas":
                listax = db.listaRecebidasMovimentos(
                        chkData, chkContribuicao, chkNrBoletos, chkEmpresa, chkTipo, tipoDataPesquisa,
                        dtInicial, dtFinal, dataRefInicial, dataRefFinal, ic, its, boletoInicial,
                        boletoFinal, getPessoa().getId(), ordenacao, movimentosDasEmpresas
                );
                break;
            case "naoRecebidas":
                listax = db.listaNaoRecebidasMovimentos(
                        chkData, chkContribuicao, chkNrBoletos, chkEmpresa, chkTipo, tipoDataPesquisa,
                        dtInicial, dtFinal, dataRefInicial, dataRefFinal, ic, its, boletoInicial,
                        boletoFinal, getPessoa().getId(), ordenacao, movimentosDasEmpresas
                );
                break;
            default:
                listax = db.listaAtrazadasMovimentos(
                        chkData, chkContribuicao, chkNrBoletos, chkEmpresa, chkTipo, tipoDataPesquisa,
                        dtInicial, dtFinal, dataRefInicial, dataRefFinal, ic, its, boletoInicial,
                        boletoFinal, getPessoa().getId(), ordenacao, movimentosDasEmpresas
                );
        }

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

            soma = Moeda.subtracao(Moeda.soma(
                    Moeda.soma(
                            Moeda.soma(
                                    Double.parseDouble(Double.toString((Double) linha_list.get(21))),//valor
                                    Double.parseDouble(Double.toString((Double) linha_list.get(14)))//juros
                            ),
                            Double.parseDouble(Double.toString((Double) linha_list.get(15)))//correcao
                    ), Double.parseDouble(Double.toString((Double) linha_list.get(13))) //multa
            ), Double.parseDouble(Double.toString((Double) linha_list.get(16))));// desconto
            somaRepasse = Moeda.multiplicar(soma,
                    Moeda.divisao(
                            Double.parseDouble(Double.toString((Double) linha_list.get(17))), 100));

            if (linha_list.get(12) == null
                    && ((String) linha_list.get(11)).equals("Acordo")) {
                habData = true;
            } else {
                habData = false;
            }

            if (linha_list.get(12) == null) {
                classTbl = "tblExtratoTelaX";
            } else {
                classTbl = "";
            }
            double valor_baixa = Double.parseDouble(Double.toString((Double) linha_list.get(21))),
                    valor = Double.parseDouble(Double.toString((Double) linha_list.get(8))),
                    taxa = Double.parseDouble(Double.toString((Double) linha_list.get(9)));

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
                    null, // ARG 27 null
                    null // ARG 28 null
            )
            );

            if (linha_list.get(12) != null) {
                vlRecebido = somarValores(valor_baixa, vlRecebido);
                vlTaxa = somarValores(taxa, vlTaxa);
            }

            vlNaoRecebido = somarValores(valor, vlNaoRecebido);
            vlTotal = somarValores(valor_baixa, vlTotal);

            double contaLiquido = Moeda.subtracao(valor_baixa, taxa);
            vlLiquido = somarValores(contaLiquido, vlLiquido);

            vlRepasse = somarValores(somaRepasse, vlRepasse);
        }
        vlRepasse = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(vlLiquido), Moeda.converteUS$(vlRepasse)));

        dataRefInicial = "";
        dataRefFinal = "";
    }

    public void limparPesquisaPessoa() {
        pessoa = new Pessoa();
        listaEmpresasPertencentes.clear();
        movimentosDasEmpresas = false;
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
                StatusRetorno sr = GerarMovimento.excluirUmMovimento(db.pesquisaCodigo((Integer) listaMovimentos.get(i).getArgumento1()));
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
                StatusRetorno sr = GerarMovimento.estornarMovimento(db.pesquisaCodigo((Integer) listaMovimento.getArgumento1()), motivoEstorno);
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
        if (listaMovimentos.isEmpty()) {
            msgConfirma = "Lista vazia!";
            GenericaMensagem.error("Atenção", "Lista Vazia");
            return false;
        }

        if (bltQuitados() == true) {
            msgConfirma = "Boletos quitados não podem ser Impressos!";
            GenericaMensagem.error("Atenção", "Boletos quitados não podem ser Impressos!");
            return false;
        }

        if (bltSelecionados() != true) {
            msgConfirma = "Nenhum Boleto Selecionado!";
            GenericaMensagem.error("Atenção", "Nenhum Boleto Selecionado!");
            return false;
        }

        return true;
    }

    public String imprimirRemessa() {
        MovimentoDao db = new MovimentoDao();
        List<Movimento> listaC = new ArrayList();

        if (!validaImprimir()) {
            return null;
        }

        for (DataObject listaMovimento : listaMovimentos) {
            if ((Boolean) listaMovimento.getArgumento0()) {
                listaC.add(db.pesquisaCodigo((Integer) listaMovimento.getArgumento1()));
            }
        }

        ImprimirBoleto imp = new ImprimirBoleto();

        listaC = imp.atualizaContaCobrancaMovimento(listaC);

        MovimentoDao dao = new MovimentoDao();
        Boleto boleto_teste = dao.pesquisaBoletos(listaC.get(0).getNrCtrBoleto());
        String ids_movimento = "";

        for (Movimento m : listaC) {
            Boleto boletox = dao.pesquisaBoletos(m.getNrCtrBoleto());
            if (boleto_teste.getContaCobranca().getId() != boletox.getContaCobranca().getId()) {
                GenericaMensagem.error("Atenção", "Para gerar a remessa os boletos devem ser da mesma Conta Cobrança!");
                return null;
            }

            if (ids_movimento.isEmpty()) {
                ids_movimento = "" + m.getId();
            } else {
                ids_movimento += ", " + m.getId();
            }
        }
        RemessaBancoDao daor = new RemessaBancoDao();

        List<RemessaBanco> l_rb = daor.listaMovimentoComRemessaBanco(ids_movimento);

        if (!l_rb.isEmpty()) {
            GenericaMensagem.error("Atenção", "Movimento já enviado para Remessa, " + l_rb.get(0).getMovimento().getDocumento());
            return null;
        }

        File fi = imp.imprimirRemessa(listaC, boleto_teste);
        if (fi == null) {
            GenericaMensagem.error("Atenção", "Erro ao gerar Remessa!");
            return null;
        }
        imp.visualizar_remessa(fi);
//        
//        List<RemessaBanco> l_rb2 = daor.listaMovimentoComRemessaBanco(ids_movimento);
//        if (l_rb2.isEmpty()) {
//            GenericaMensagem.error("Atenção", "Erro ao gerar Remessa!");
//            return null;
//        }
//        
//        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/remessa/");
//        caminho = caminho + "/" + l_rb2.get(0).getRemessa().getId()+"/"+l_rb2.get(0).getRemessa().getNomeArquivo();
//        File filex = new File(caminho);
//        

        return null;
    }

    public String imprimir() {
        MovimentoDao db = new MovimentoDao();
        List<Movimento> listaC = new ArrayList();
        List<Double> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();

        if (!validaImprimir()) {
            return null;
        }

        for (DataObject listaMovimento : listaMovimentos) {
            if ((Boolean) listaMovimento.getArgumento0()) {
                Movimento mov = db.pesquisaCodigo((Integer) listaMovimento.getArgumento1());
                listaC.add(mov);
                listaValores.add(mov.getValor());
                listaVencimentos.add(mov.getVencimento());
            }
        }

        ImprimirBoleto imp = new ImprimirBoleto();

        listaC = imp.atualizaContaCobrancaMovimento(listaC);

        imp.imprimirBoleto(listaC, listaValores, listaVencimentos, imprimirVerso);

        imp.visualizar(null);

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
        List<Double> listaValores = new ArrayList<Double>();
        List<String> listaVencimentos = new ArrayList<String>();

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
                listaValores.add(listaux.get(i).getValor());
                listaVencimentos.add(listaux.get(i).getVencimento());

                try {
                    id_compara = dbj.pesquisaJuridicaPorPessoa(listaux.get(i + 1).getPessoa().getId()).getContabilidade().getId();
                    if (id_contabil != id_compara) {
                        enviar = true;
                    }
                } catch (Exception e) {
                    enviar = true;
                }

                if (enviar) {
                    enviar(movadd, listaValores, listaVencimentos, juridica.getContabilidade());
                    enviar = false;
                    movadd.clear();
                    listaValores.clear();
                    listaVencimentos.clear();
                }
                /* ENVIO PARA EMPRESA */
            } else {
                id_empresa = juridica.getId();

                movadd.add(listaux.get(i));
                listaValores.add(listaux.get(i).getValor());
                listaVencimentos.add(listaux.get(i).getVencimento());

                try {
                    id_compara = dbj.pesquisaJuridicaPorPessoa(listaux.get(i + 1).getPessoa().getId()).getId();
                    if (id_empresa != id_compara) {
                        enviar = true;
                    }
                } catch (Exception e) {
                    enviar = true;
                }

                if (enviar) {
                    enviar(movadd, listaValores, listaVencimentos, juridica);
                    enviar = false;
                    movadd.clear();
                    listaValores.clear();
                    listaVencimentos.clear();
                }
            }

        }

        PF.update("formExtratoTela:i_msg");
        PF.closeDialog("dlg_enviar_email");
        return null;
    }

    public void enviar(List<Movimento> mov, List<Double> listaValores, List<String> listaVencimentos, Juridica jur) {
        try {

            Registro reg = new Registro();
            reg = Registro.get();

            ImprimirBoleto imp = new ImprimirBoleto();
            imp.imprimirBoleto(mov, listaValores, listaVencimentos, false);
            String nome = imp.criarLink(jur.getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            List<Pessoa> pessoas = new ArrayList();
            pessoas.add(jur.getPessoa());

            String mensagem = "";
            List<File> fls = new ArrayList<File>();
            String nome_envio = "";
            if (mov.size() == 1) {
                nome_envio = "Boleto " + mov.get(0).getServicos().getDescricao() + " N° " + mov.get(0).getDocumento();
            } else {
                nome_envio = "Boleto";
            }

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

            List<EmailPessoa> emailPessoas = new ArrayList<EmailPessoa>();
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
        List<Movimento> listaC = new ArrayList<Movimento>();
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
        //dataAntiga = ((String) dtObj.getArgumento9());
        dataAntiga = movimentoVencimento.getVencimento();
        dataNova = "";
        return null;
    }

    public String atualizarData() {
        if (!dataNova.isEmpty()) {
            if (DataHoje.converteDataParaInteger(dataNova) >= DataHoje.converteDataParaInteger(DataHoje.data())) {
                movimentoVencimento.setVencimento(dataNova);
                Dao di = new Dao();

                di.openTransaction();

                if (!di.update(movimentoVencimento)) {
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

    public ControleAcessoBean getCab() {
        return cab;
    }

    public void setCab(ControleAcessoBean cab) {
        this.cab = cab;
    }

}
