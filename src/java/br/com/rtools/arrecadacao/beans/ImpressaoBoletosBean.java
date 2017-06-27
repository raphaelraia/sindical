package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.CnaeConvencao;
import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.financeiro.Impressao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoContaCobranca;
import br.com.rtools.financeiro.dao.ImpressaoDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.impressao.Carta;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.relatorios.dao.RelatorioContribuintesDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.beans.SisCartaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Linha;
import br.com.rtools.utilitarios.Mail;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ImpressaoBoletosBean implements Serializable {

    private String escritorio = "null";
    private List<String> listaData = new ArrayList();
    private List<String> listaDataSelecionada = new ArrayList();
    private List<ObjectImpressaoBoleto> listObjectImpressaoBoleto = new ArrayList();
    private List<ObjectImpressaoBoleto> selected = new ArrayList();
    List<Movimento> listaAux = new ArrayList();
    private Juridica contabilidade = new Juridica();
    private int idCombo = 0;
    private Integer quantidade = 0;
    private Integer inicio = 0;
    private Integer fim = 0;
    int idData = -2;
    private long totalBoletos = 0;
    private long totalEmpresas = 0;
    private long totalEscritorios = 0;
    private boolean imprimeVerso = true;
    private String msgImpressao = "";
    private List<Convencao> listaConvencao = new ArrayList();
    private List<Convencao> listaConvencaoSelecionada = new ArrayList();
    private List<GrupoCidade> listaGrupoCidade = new ArrayList();
    private List<GrupoCidade> listaGrupoSelecionada = new ArrayList();
    private String todasContas = "false";
    private String movimentosSemMensagem = null;
    private int quantidadeEmpresas = 0;
    private String regraEscritorios = "all";
    private String cbEmail = "todos";
    private boolean chkTodosVencimentos = false;
    private List<Impressao> listHistoricoImpressao = new ArrayList();
    private String registrado = "todos";
    // private List<Linha> listaMovGrid = new ArrayList();
    // private List<Linha> listaMovGridSelecionada = new ArrayList();
    // private int boletosSel;
    private ServicoContaCobranca servicoContaCobranca = new ServicoContaCobranca();
    private Map<String, Integer> contaCobranca = null;
    private List selectedContaCobranca = new ArrayList();
    private Boolean habilitarComunicado = false;
    private String novoVencto = "";

    public void alterVencimento() {
        if (novoVencto.isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR DATA DE VENCIMENTO!");
            return;
        }
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Validação", "SELECIONAR MOVIMENTOS!");
            return;
        }
        if (selected.size() != listObjectImpressaoBoleto.size()) {
            GenericaMensagem.warn("Validação", "SELECIONAR TODOS OS MOVIMENTOS DA GRID!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        String dataAntiga = "";
        Boolean r = false;
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i).getCobranca_registrada()) {
                r = true;
            } else {
                Movimento m = (Movimento) dao.find(new Movimento(), selected.get(i).getMovimento_id());
                if (dataAntiga.isEmpty()) {
                    dataAntiga = m.getVencimento();
                }
                if (!m.getVencimento().equals(novoVencto)) {
                    m.setDtVencimento(DataHoje.converte(novoVencto));
                    m.setDtVencimentoOriginal(DataHoje.converte(novoVencto));
                    if (!dao.update(m)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "Ao alterar movimento!");
                        return;
                    }
                }
            }
        }
        if (r) {
            GenericaMensagem.warn("Sistema", "Não é possível alterar vencimento de cobrança registrada!");
        }
        dao.commit();
        selected = new ArrayList<>();
        GenericaMensagem.info("Sucesso", "Registros atualizados com sucesso!");
        NovoLog novoLog = new NovoLog();
        novoLog.save("ALTERAÇÃO DE DATA DE VENCIMENTO DOS MOVIMENTOS DE :" + dataAntiga + " PARA " + novoVencto);
        listaData = new ArrayList();
        novoVencto = "";
        getListaData();
        loadList();
    }

    public void registrarBoletos() {
        MovimentoDao db = new MovimentoDao();
        List<Movimento> lista = new ArrayList();
        List<Double> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();
        Dao dao = new Dao();
        if (!selected.isEmpty()) {
            for (ObjectImpressaoBoleto oib : selected) {
                Movimento m = (Movimento) dao.find(new Movimento(), (Integer) oib.getMovimento_id());
                lista.add(m);
                listaValores.add(m.getValor());
                listaVencimentos.add(m.getVencimento());
            }
            ImprimirBoleto imp = new ImprimirBoleto();
            HashMap hash = imp.registrarMovimentos(lista, listaValores, listaVencimentos);

            if (((ArrayList) hash.get("lista")).isEmpty() || ((ArrayList) hash.get("lista")).size() != listaValores.size()) {
                GenericaMensagem.error("Erro", hash.get("mensagem").toString());
            } else {
                GenericaMensagem.info("Sucesso", "Boletos Registrados!");
            }
            loadList();
        } else {
            GenericaMensagem.warn("Atenção", "Selecione ao menos um Boleto para registrar!");
        }
    }

    public String removerContabilidade() {
        contabilidade = new Juridica();
        loadList();
        return "impressaoBoletos";
    }

    public String getEscritorio() {
        return escritorio;
    }

    public void setEscritorio(String escritorio) {
        this.escritorio = escritorio;
    }

    public int getIdCombo() {
        return idCombo;
    }

    public void setIdCombo(int idCombo) {
        this.idCombo = idCombo;
    }

    public boolean getDesabilitarContas() {
        if (this.todasContas.equals("false")) {
            return false;
        } else {
            return true;
        }
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getQuantidadeString() {
        return Integer.toString(quantidade);
    }

    public void setQuantidadeString(String quantidadeString) {
        this.quantidade = Integer.parseInt(quantidadeString);
        if (this.quantidade > listObjectImpressaoBoleto.size()) {
            this.quantidade = listObjectImpressaoBoleto.size();
        }
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(Integer inicio) {
        this.inicio = inicio;
    }

    public Integer getFim() {
        return fim;
    }

    public void setFim(Integer fim) {
        this.fim = fim;
    }

    public String getInicioString() {
        return Integer.toString(inicio);
    }

    public void setInicioString(String inicioString) {
        this.inicio = Integer.parseInt(inicioString);
    }

    public String getFimString() {
        return Integer.toString(fim);
    }

    public void setFimString(String fimString) {
        this.fim = Integer.parseInt(fimString);
    }

//    public int getBoletosSel() {
//        return boletosSel;
//    }
//
//    public void setBoletosSel(int boletosSel) {
//        this.boletosSel = boletosSel;
//    }
//    public List<Linha> getListaMovGrid() {
//        return listaMovGrid;
//    }
//
//    public void setListaMovGrid(List<Linha> listaMovGrid) {
//        this.listaMovGrid = listaMovGrid;
//    }
//    
    public Linha addGrid(Vector vetorAux, int i) {
        List lista = new ArrayList();
        Linha linha = new Linha();
        lista.add(false); //marcar
        lista.add(i + 1); //indice
        lista.add(vetorAux.get(0));    //boleto
        lista.add(vetorAux.get(1));    //razao
        lista.add(vetorAux.get(2));    //cnpj
        lista.add(vetorAux.get(3));    //escritorio
        lista.add(vetorAux.get(4));    //servico
        lista.add(vetorAux.get(5));    //tipo
        lista.add(DataHoje.converteData((Date) vetorAux.get(6))); //vencimento
        lista.add(vetorAux.get(7));    //referencia
        lista.add(vetorAux.get(8));    //id
        lista.add(vetorAux.get(9));    //id_contabilidade ( pessoa )
        lista.add(vetorAux.get(10));   //id_juridica

        linha = Linha.preencherLinha(
                linha,
                lista,
                0);

        return linha;
    }

    public void loadList() {
        if (selectedContaCobranca.isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR AO MENOS UMA CONTRIBUIÇÃO!");
            return;
        }
        if (listaDataSelecionada.isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR AO MENOS UMA DATA DE VENCIMENTO!");
            return;
        }
        try {
            selected = new ArrayList();
            listObjectImpressaoBoleto = new ArrayList();
            //listaMovGrid.clear();
            //listaMovGridSelecionada.clear();
            ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
            MovimentoDao movDB = new MovimentoDao();
            //List<Linha> listaSwap = new ArrayList();
            List<ObjectImpressaoBoleto> sublistOIB = new ArrayList();
            ServicoContaCobranca contaCobranca;
            // Linha linha = new Linha();

            List<Integer> listG = new ArrayList();
            List<Integer> listC = new ArrayList();
            //Object[] result = new Object[]{new ArrayList(), new Integer(0)};

            totalBoletos = 0;
            totalEmpresas = 0;
            totalEscritorios = 0;

            String contribuicoes = "";
            List<ServicoContaCobranca> servicoContaCobrancas = new ArrayList<>();
            if (selectedContaCobranca != null) {
                for (int i = 0; i < selectedContaCobranca.size(); i++) {
                    servicoContaCobrancas.add(servDB.pesquisaCodigo(Integer.parseInt(selectedContaCobranca.get(i).toString())));
                }
            }
//            try {
//                servicoContaCobranca = servDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaServicoCobranca().get(idCombo)).getDescription()));
//            } catch (Exception e) {
//                servicoContaCobranca = new ServicoContaCobranca();
//            }

            if (!listaConvencaoSelecionada.isEmpty()) {
                for (int i = 0; i < listaConvencaoSelecionada.size(); i++) {
                    listC.add(listaConvencaoSelecionada.get(i).getId());
                }
            }
            if (!listaGrupoSelecionada.isEmpty()) {
                for (int i = 0; i < listaGrupoSelecionada.size(); i++) {
                    listG.add(listaGrupoSelecionada.get(i).getId());
                }
            }

            if (!(listaDataSelecionada.isEmpty())) {
                List ids = new ArrayList();

                for (int i = 0; i < listaDataSelecionada.size(); i++) {
                    ids.add(listaDataSelecionada.get(i));
                }
                //Vector vetorAux = new Vector();

                if (this.regraEscritorios.equals("all")) {
                    this.quantidadeEmpresas = 0;
                } else if (this.quantidadeEmpresas == 0) {
                    this.quantidadeEmpresas = 1;
                }

                int id_esc = 0;

                if (contabilidade.getId() != -1) {
                    id_esc = contabilidade.getId();
                }

//                result = movDB.listaImpressaoGeral(
//                        contaCobranca.getServicos().getId(),
//                        contaCobranca.getTipoServico().getId(),
//                        contaCobranca.getContaCobranca().getId(),
//                        escritorio,
//                        ids,
//                        listC,
//                        listG,
//                        this.todasContas,
//                        cbEmail,
//                        id_esc);
                List list = movDB.listaImpressaoGeral(
                        servicoContaCobranca.getServicos().getId(),
                        servicoContaCobranca.getTipoServico().getId(),
                        servicoContaCobranca.getContaCobranca().getId(),
                        escritorio,
                        ids,
                        listC,
                        listG,
                        this.todasContas,
                        cbEmail,
                        id_esc,
                        regraEscritorios,
                        quantidadeEmpresas,
                        registrado,
                        servicoContaCobrancas
                );

                //Vector v = (Vector) list;
                Integer auxEsc = 0;
                Integer auxEmpresa = 0;
                //Integer ate = 0, apartir = 0;
//                for (x = 0; x < v.size(); x++) {
//                    vetorAux = (Vector) v.get(x);
//                    if (this.regraEscritorios.equals("ate")) {
//                        if (((Long) vetorAux.get(11)) <= this.quantidadeEmpresas) {
//                            linha = addGrid(vetorAux, ate);
//                            if (!auxEsc.equals(((Integer) vetorAux.get(9)))) {
//                                totalEscritorios++;
//                            }
//                            auxEsc = ((Integer) vetorAux.get(9));
//                            if (!auxEmpresa.equals(((Integer) vetorAux.get(10)))) {
//                                totalEmpresas++;
//                            }
//                            auxEmpresa = ((Integer) vetorAux.get(10));
//                            totalBoletos++;
//                            listaSwap.add(linha);
//                            ate++;
//                        }
//                    } else if (this.regraEscritorios.equals("apartir")) {
//                        if (((Long) vetorAux.get(11)) >= this.quantidadeEmpresas) {
//                            linha = addGrid(vetorAux, apartir);
//                            if (!auxEsc.equals(((Integer) vetorAux.get(9)))) {
//                                totalEscritorios++;
//                            }
//                            auxEsc = ((Integer) vetorAux.get(9));
//                            if (!auxEmpresa.equals(((Integer) vetorAux.get(10)))) {
//                                totalEmpresas++;
//                            }
//                            auxEmpresa = ((Integer) vetorAux.get(10));
//                            totalBoletos++;
//                            listaSwap.add(linha);
//                            apartir++;
//                        }
//                    } else {
//                        linha = addGrid((Vector) v.get(x), x);
//                        if (!auxEsc.equals(((Integer) vetorAux.get(9)))) {
//                            totalEscritorios++;
//                        }
//                        auxEsc = ((Integer) vetorAux.get(9));
//                        if (!auxEmpresa.equals(((Integer) vetorAux.get(10)))) {
//                            totalEmpresas++;
//                        }
//                        auxEmpresa = ((Integer) vetorAux.get(10));
//                        totalBoletos++;
//                        listaSwap.add(linha);
//                    }
//                }
                List<ObjectImpressaoBoleto> listOIB = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    List o = (List) list.get(i);
                    ObjectImpressaoBoleto oib = new ObjectImpressaoBoleto(
                            (i + 1),
                            o.get(0),
                            o.get(1),
                            o.get(2),
                            o.get(3),
                            o.get(4),
                            o.get(5),
                            DataHoje.converteData((Date) o.get(6)),
                            o.get(7),
                            o.get(8),
                            o.get(9),
                            o.get(10),
                            o.get(11),
                            o.get(12),
                            (Boolean) o.get(13)
                    );
                    listObjectImpressaoBoleto.add(oib);
                    if (this.regraEscritorios.equals("ate")) {
                        if (((Long) oib.getQuantidade_empresas()) <= this.quantidadeEmpresas) {
                            if (!auxEsc.equals(((Integer) oib.getContabilidade_id()))) {
                                totalEscritorios++;
                            }
                            auxEsc = (Integer) oib.getContabilidade_id();
                            if (!auxEmpresa.equals(((Integer) oib.getEmpresa_id()))) {
                                totalEmpresas++;
                            }
                            auxEmpresa = ((Integer) oib.getEmpresa_id());
                            totalBoletos++;
                            // listObjectImpressaoBoleto.add(oib);
                            //listaSwap.add(linha);
                            //ate++;
                        }
                    } else if (this.regraEscritorios.equals("apartir")) {
                        if (((Long) o.get(11)) >= this.quantidadeEmpresas) {
                            //listObjectImpressaoBoleto.add(oib);
                            if (!auxEsc.equals(((Integer) oib.getContabilidade_id()))) {
                                totalEscritorios++;
                            }
                            auxEsc = ((Integer) oib.getContabilidade_id());
                            if (!auxEmpresa.equals(((Integer) oib.getEmpresa_id()))) {
                                totalEmpresas++;
                            }
                            auxEmpresa = ((Integer) oib.getEmpresa_id());
                            totalBoletos++;
                            //listObjectImpressaoBoleto.add(oib);
                            //listaSwap.add(linha);
                            //apartir++;
                        }
                    } else {
                        //linha = addGrid((Vector) v.get(x), x);
                        if (!auxEsc.equals(((Integer) oib.getContabilidade_id()))) {
                            totalEscritorios++;
                        }
                        auxEsc = ((Integer) oib.getContabilidade_id());
                        if (!auxEmpresa.equals(((Integer) oib.getEmpresa_id()))) {
                            totalEmpresas++;
                        }
                        auxEmpresa = ((Integer) oib.getEmpresa_id());
                        totalBoletos++;
                        //listObjectImpressaoBoleto.add(oib);
                        //listaSwap.add(linha);
                        // sublistOIB.add(oib);
                    }
                }
            }
            // listaMovGrid = listaSwap;
            // listObjectImpressaoBoleto = sublistOIB;
            if (!(getListObjectImpressaoBoleto().isEmpty())) {
                if (((quantidade <= getListObjectImpressaoBoleto().size()) && (inicio <= getListObjectImpressaoBoleto().size()) && (fim <= getListObjectImpressaoBoleto().size()))
                        && ((inicio != 0) && (fim != 0))) {
                    quantidade = (fim - inicio) + 1;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    public synchronized void refreshForm() {
//    }
    public void alterarTodasDatas() {
        // listaMovGrid.clear();
        // listaMovGridSelecionada.clear();
        listObjectImpressaoBoleto = new ArrayList();
        selected = new ArrayList();
        listaData = new ArrayList();
        listaDataSelecionada = new ArrayList();
        try {
            servicoContaCobranca = (ServicoContaCobranca) new Dao().find(new ServicoContaCobranca(), Integer.parseInt(((SelectItem) getListaServicoCobranca().get(idCombo)).getDescription()));
            if (servicoContaCobranca == null) {
                servicoContaCobranca = new ServicoContaCobranca();
            }
        } catch (Exception e) {
        }
        if (this.todasContas.equals("true")) {
            idData = -2;
        } else {
            idData = -1;
        }
    }

    public boolean getDesabilitaComboQuantidadeEmpresas() {
        return regraEscritorios.equals("all");
    }

    public synchronized List<String> getListaData() {
        if (listaData.isEmpty()) {

            try {
                ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
                ServicoContaCobranca contaCobranca;
                try {
                    contaCobranca = servDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaServicoCobranca().get(idCombo)).getDescription()));
                } catch (Exception e) {
                    contaCobranca = new ServicoContaCobranca();
                }
                MovimentoDao db = new MovimentoDao();
                List lista = new ArrayList();
                int i = 0;
                if (this.todasContas.equals("false")) {
                    String contribuicoes = "";
                    List<ServicoContaCobranca> servicoContaCobrancas = new ArrayList<>();
                    if (selectedContaCobranca != null) {
                        for (int x = 0; x < selectedContaCobranca.size(); x++) {
                            servicoContaCobrancas.add((ServicoContaCobranca) new Dao().find(new ServicoContaCobranca(), Integer.parseInt(selectedContaCobranca.get(x).toString())));
                        }
                    }
                    if (contaCobranca.getId() != idData) {
                        listaData = new ArrayList();
                        idData = contaCobranca.getId();
//                        lista = db.datasMovimento(
//                                contaCobranca.getServicos().getId(),
//                                contaCobranca.getTipoServico().getId(),
//                                contaCobranca.getContaCobranca().getId());
                        lista = db.datasMovimento(servicoContaCobrancas);
                    }

                } else if (idData == -2) {
                    idData = -1;
                    listaData.clear();
                    lista = db.datasMovimento();
                }

                if (lista == null) {
                    lista = new ArrayList();
                }
                while (i < lista.size()) {
                    listaData.add(DataHoje.converteData(DataHoje.converteDateSqlToDate(lista.get(i).toString())));
                    i++;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return listaData;

    }

    public void limparSelecao() {
        // listaMovGridSelecionada.clear();
        selected = new ArrayList();
        quantidade = 0;
        inicio = 0;
        fim = 0;

    }

    public void limparTotais() {
//        listaMovGrid.clear();
//        listaMovGridSelecionada.clear();
        totalBoletos = 0;
        totalEmpresas = 0;
        totalEscritorios = 0;
        selected = new ArrayList();
        listObjectImpressaoBoleto = new ArrayList();
    }

    public synchronized void controleMovimentos() {
        int i = 0;
        // listaMovGridSelecionada.clear();
        selected = new ArrayList();
        if ((quantidade != 0) && inicio == 0 && fim == 0) {//CASO 1 SOMENTE POR QUANTIDADE
            if (quantidade > listObjectImpressaoBoleto.size()) {
                quantidade = listObjectImpressaoBoleto.size();
            }
            while (i < quantidade) {
                //listaMovGrid.get(i).setValor(new Boolean(true));
                // listaMovGridSelecionada.add(listaMovGrid.get(i));
                if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                    if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                } else {
                    selected.add(listObjectImpressaoBoleto.get(i));
                }
                i++;
            }
        } else if (quantidade == 0 && inicio != 0 && fim == 0) {//CASO 2 SOMENTE POR INICIO
            if (inicio <= listObjectImpressaoBoleto.size()) {
                i = inicio - 1;
                while (i < listObjectImpressaoBoleto.size()) {
                    quantidade++;
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                }
            }
        } else if (quantidade != 0 && inicio != 0 && fim == 0) {//CASO 3 SOMENTE POR INICIO E QUANTIDADE
            if ((quantidade <= listObjectImpressaoBoleto.size()) && (inicio <= listObjectImpressaoBoleto.size())) {
                int o = 0;
                i = inicio - 1;
                while ((o < quantidade) && (i < listObjectImpressaoBoleto.size())) {
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                    o++;
                }
            }
        } else if (quantidade == 0 && inicio != 0 && fim != 0) {//CASO 4 SOMENTE POR INICIO E FIM
            if ((inicio <= listObjectImpressaoBoleto.size()) && (fim <= listObjectImpressaoBoleto.size())) {
                i = inicio - 1;
                while (i < fim) {
                    quantidade++;
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                }
            }
        } else if (quantidade == 0 && inicio == 0 && fim != 0) {//CASO 5 SOMENTE POR FIM
            if (fim <= listObjectImpressaoBoleto.size()) {
                while (i < fim) {
                    quantidade++;
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                }
            }
        } else if (quantidade != 0 && inicio == 0 && fim != 0) {//CASO 6 SOMENTE POR FIM E QUANTIDADE
            if ((quantidade <= listObjectImpressaoBoleto.size()) && (fim <= listObjectImpressaoBoleto.size())) {
                if ((quantidade - fim) < 0) {
                    i = fim - quantidade;
                } else {
                    i = quantidade - fim;
                }
                quantidade = 0;
                while (i < fim) {
                    quantidade++;
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                }
            }
        } else if (quantidade != 0 && inicio != 0 && fim != 0) {//CASO 7 POR QUANTIDADE INICIO E FIM
            if ((quantidade <= listObjectImpressaoBoleto.size()) && (inicio <= listObjectImpressaoBoleto.size()) && (fim <= listObjectImpressaoBoleto.size())) {
                i = inicio - 1;
                if (quantidade > 1) {
                    quantidade = (fim - inicio) + 1;
                }
                quantidade = 0;
                while (i < fim) {
                    quantidade++;
                    //listaMovGrid.get(i).setValor(new Boolean(true));
                    //listaMovGridSelecionada.add(listaMovGrid.get(i));
                    if (servicoContaCobranca.getContaCobranca().isCobrancaRegistrada()) {
                        if (listObjectImpressaoBoleto.get(i).getData_registro() != null) {
                            selected.add(listObjectImpressaoBoleto.get(i));
                        }
                    } else {
                        selected.add(listObjectImpressaoBoleto.get(i));
                    }
                    i++;
                }
            }
        }
    }

    public List<SelectItem> getListaServicoCobranca() {
        List<SelectItem> servicoCobranca = new ArrayList();
        int i = 0;
        ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
        List<ServicoContaCobranca> select = servDB.pesquisaTodosTipoUm();
        if (select == null) {
            select = new ArrayList();
        }
        while (i < select.size()) {
            servicoCobranca.add(
                    new SelectItem(
                            i,
                            select.get(i).getServicos().getDescricao() + " - "
                            + select.get(i).getTipoServico().getDescricao() + " - "
                            + select.get(i).getContaCobranca().getCodCedente(),
                            Integer.toString(select.get(i).getId())));
            i++;
        }
        return servicoCobranca;
    }

//    public String criarArquivoBanco() {
//        List movs = new ArrayList();
//        MovimentoDao db = new MovimentoDao();
//        try {
//            ArquivoBancoBean arquivoBanco = new ArquivoBancoBean();
//            Movimento mov = new Movimento();
//            if (todasContas.equals("true")) {
//                msgImpressao = "Selecione específicas para gerar o Arquivo!";
//                return "impressaoBoletos";
//            }
//
//            for (int o = 0; o < listObjectImpressaoBoleto.size(); o++) {
//                if ((Boolean) listaMovGrid.get(o).getValor()) {
//                    mov = db.pesquisaCodigo(
//                            (Integer) listaMovGrid.get(o).getColuna().getColuna().getColuna().getColuna().getColuna().getColuna().getColuna().getColuna().getColuna().getColuna().getValor());
//                    movs.add(mov);
//                }
//            }
//            if (!movs.isEmpty()) {
//                if (arquivoBanco.criarArquivoTXT(movs)) {
//                    msgImpressao = "Arquivo gerado com sucesso!";
//                } else {
//                    msgImpressao = "Erro ao processar arquivos!";
//                }
//            } else {
//                msgImpressao = "Lista vazia!";
//            }
//        } catch (Exception e) {
//            System.out.println("Não foi possivel criar arquivo de envio! " + e);
//        }
//        return "impressaoBoletos";
//    }
//    public String baixarArquivosGerados() {
//        ArquivoBancoBean arquivoBanco = new ArquivoBancoBean();
//        arquivoBanco.baixarArquivosGerados();
//
//        return null;
//    }
//    public void limparIn() {
//        ArquivoBancoBean arquivoBanco = new ArquivoBancoBean();
//        arquivoBanco.limparDiretorio("");
//    }
    public String imprimirBoleto() {
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUM BOLETO SELECIONADO!");
            return null;
        }
        List<Movimento> lista = new ArrayList();
        List<Double> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();

        SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();
        Dao dao = new Dao();

        dao.openTransaction();
        for (int i = 0; i < selected.size(); i++) {
            Movimento m = (Movimento) dao.find(new Movimento(), (Integer) selected.get(i).getMovimento_id());
            lista.add(m);
            listaValores.add(m.getValor());
            listaVencimentos.add(m.getVencimento());

            Impressao impressao = new Impressao();

            impressao.setUsuario(su.getSessaoUsuario());
            impressao.setDtVencimento(m.getDtVencimento());
            impressao.setMovimento(m);

            if (!dao.save(impressao)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
                return null;
            }
        }
        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();
        imp.imprimirBoleto(lista, listaValores, listaVencimentos, imprimeVerso);
        imp.baixarArquivo();
        return null;
    }
//
//    public String getPainelMenssagem() {
//        if (movimentosSemMensagem == null) {
//            return "";
//        } else {
//            return "boletoSemMensagem";
//        }
//    }

    public String etiquetaEmpresa() {
        String cnaes = "";
        RelatorioContribuintesDao dbContri = new RelatorioContribuintesDao();
        PessoaEnderecoDao pessoaEnderecoDao = new PessoaEnderecoDao();
        List listaCnaes = new ArrayList();
        // CNAES DO RELATORIO -----------------------------------------------------------
        List<Convencao> resultConvencoes = new Dao().list(new Convencao(), true);
        String ids = "", idsJuridica = "";
        for (int i = 0; i < resultConvencoes.size(); i++) {
            if (ids.length() > 0 && i != resultConvencoes.size()) {
                ids = ids + ",";
            }
            ids = ids + String.valueOf(resultConvencoes.get(i).getId());
        }
        List<CnaeConvencao> resultCnaeConvencao = new ArrayList();
        if (!ids.isEmpty()) {
            resultCnaeConvencao = dbContri.pesquisarCnaeConvencaoPorConvencao(ids);
        }

        if (!resultConvencoes.isEmpty()) {
            for (int i = 0; i < resultCnaeConvencao.size(); i++) {
                listaCnaes.add(resultCnaeConvencao.get(i));
            }
            for (int i = 0; i < listaCnaes.size(); i++) {
                if (cnaes.length() > 0 && i != resultCnaeConvencao.size()) {
                    cnaes = cnaes + ",";
                }
                cnaes = cnaes + Integer.toString(((CnaeConvencao) listaCnaes.get(i)).getCnae().getId());
            }
        } else {
            cnaes = "";
        }

        for (int i = 0; i < selected.size(); i++) {
            if (idsJuridica.length() > 0 && i != selected.size()) {
                idsJuridica = idsJuridica + ",";
            }
            idsJuridica = idsJuridica + ((Integer) selected.get(i).getEmpresa_id());
        }
        List<Juridica> result = new ArrayList();
        if (!resultCnaeConvencao.isEmpty() && !listaCnaes.isEmpty() && !idsJuridica.isEmpty()) {
            result = dbContri.listaRelatorioContribuintesPorJuridica("ativos", "todos", "todas", "", "razao", cnaes, 2, idsJuridica);
        }
        List listEtiquetas = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            try {
                PessoaEndereco endEmpresa = pessoaEnderecoDao.pesquisaEndPorPessoaTipo(result.get(i).getPessoa().getId(), 2);
                listEtiquetas.add(
                        new Etiquetas(
                                result.get(i).getPessoa().getNome(),
                                endEmpresa.getEndereco().getLogradouro().getDescricao(),
                                endEmpresa.getEndereco().getDescricaoEndereco().getDescricao(),
                                endEmpresa.getNumero(),
                                endEmpresa.getEndereco().getBairro().getDescricao(),
                                endEmpresa.getEndereco().getCidade().getCidade(),
                                endEmpresa.getEndereco().getCidade().getUf(),
                                endEmpresa.getEndereco().getCep(),
                                endEmpresa.getComplemento()
                        ));
            } catch (Exception e) {

            }
        }
        Jasper.PART_NAME = "";
        Jasper.printReports("ETIQUETAS.jasper", "etiqueta_empresa", listEtiquetas);
        return null;
    }

    public String etiquetaEscritorio() {
        String condicao = "";
        String escritorios = "";
        String cidades = "";
        String pCidade = "";
        String ordem = "";
        String cnaes = "";

        RelatorioDao db = new RelatorioDao();
        RelatorioContribuintesDao dbContri = new RelatorioContribuintesDao();
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        PessoaEndereco endEscritorio = new PessoaEndereco();
        List listaCnaes = new ArrayList();
        // CONDICAO DO RELATORIO -----------------------------------------------------------
        condicao = "ativos";

        // ESCRITORIO DO RELATORIO -----------------------------------------------------------
        escritorios = "todos";

        // CIDADE DO RELATORIO -----------------------------------------------------------
        pCidade = "todas";

        // ORDEM DO RELATORIO -----------------------------------------------------------
        ordem = "escritorio";

        // CNAES DO RELATORIO -----------------------------------------------------------
        List<Convencao> resultConvencoes = new Dao().list(new Convencao(), true);
        String ids = "", idsJuridica = "";
        for (int i = 0; i < resultConvencoes.size(); i++) {
            if (ids.length() > 0 && i != resultConvencoes.size()) {
                ids = ids + ",";
            }
            ids = ids + String.valueOf(resultConvencoes.get(i).getId());
        }
        List<CnaeConvencao> resultCnaeConvencao = new ArrayList();
        if (!ids.isEmpty()) {
            resultCnaeConvencao = dbContri.pesquisarCnaeConvencaoPorConvencao(ids);
        }

        if (!resultConvencoes.isEmpty()) {
            for (int i = 0; i < resultCnaeConvencao.size(); i++) {
                listaCnaes.add(resultCnaeConvencao.get(i));
            }
            for (int i = 0; i < listaCnaes.size(); i++) {
                if (cnaes.length() > 0 && i != resultCnaeConvencao.size()) {
                    cnaes = cnaes + ",";
                }
                cnaes = cnaes + Integer.toString(((CnaeConvencao) listaCnaes.get(i)).getCnae().getId());
            }
        } else {
            cnaes = "";
        }
        Integer idContabil1 = 0, idContabil2 = 0;
        boolean um = true;
        for (int i = 0; i < selected.size(); i++) {
            if (um) {
                if (idsJuridica.length() > 0 && i != selected.size()) {
                    idsJuridica = idsJuridica + ",";
                }
                idsJuridica = idsJuridica + ((Integer) selected.get(i).getEmpresa_id());
                um = false;
            } else {
                idContabil1 = ((Integer) selected.get(i - 1).getContabilidade_id());
                idContabil2 = ((Integer) selected.get(i).getContabilidade_id());
                if (!Objects.equals(idContabil1, idContabil2)) {
                    if (idsJuridica.length() > 0 && i != selected.size()) {
                        idsJuridica = idsJuridica + ",";
                    }
                    idsJuridica = idsJuridica + ((Integer) selected.get(i).getEmpresa_id());
                }
            }
        }
        List<Juridica> result = new ArrayList();
        if (!resultCnaeConvencao.isEmpty() && !listaCnaes.isEmpty() && !idsJuridica.isEmpty()) {
            result = dbContri.listaRelatorioContribuintesPorJuridica(condicao, escritorios, pCidade, cidades, ordem, cnaes, 2, idsJuridica);
        }
        List listEtiquetas = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            try {
                endEscritorio = dao.pesquisaEndPorPessoaTipo(result.get(i).getContabilidade().getPessoa().getId(), 2);
                listEtiquetas.add(
                        new Etiquetas(
                                result.get(i).getContabilidade().getPessoa().getNome(),
                                endEscritorio.getEndereco().getLogradouro().getDescricao(),
                                endEscritorio.getEndereco().getDescricaoEndereco().getDescricao(),
                                endEscritorio.getNumero(),
                                endEscritorio.getEndereco().getBairro().getDescricao(),
                                endEscritorio.getEndereco().getCidade().getCidade(),
                                endEscritorio.getEndereco().getCidade().getUf(),
                                endEscritorio.getEndereco().getCep(),
                                endEscritorio.getComplemento()
                        ));
            } catch (Exception e) {

            }
        }
        if (listEtiquetas.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }
        Jasper.PART_NAME = "";
        Jasper.printReports("ETIQUETAS.jasper", "etiqueta_escritorio", listEtiquetas);
        return null;
    }

    public boolean getDesabilitaVi() {
        if (cbEmail.equals("todos")) {
            return false;
        } else if (cbEmail.equals("com")) {
            return true;
        } else if (cbEmail.equals("sem")) {
            return false;
        }
        return false;
    }

    public boolean getDesabilitaEmail() {
        if (cbEmail.equals("todos")) {
            return true;
        } else if (cbEmail.equals("com")) {
            return false;
        } else if (cbEmail.equals("sem")) {

            return true;
        }
        return true;
    }

    public void enviarEmail() {

        JuridicaDao dbj = new JuridicaDao();
        // MovimentoDao dbM = new MovimentoDao();

        List<Movimento> movadd = new ArrayList();
        List<Double> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();

        boolean enviar = false;
        int id_contabil = 0, id_empresa = 0, id_compara = 0;
        Dao dao = new Dao();
        for (int i = 0; i < selected.size(); i++) {
            try {

                id_empresa = (Integer) selected.get(i).getEmpresa_id();
                id_contabil = (Integer) selected.get(i).getContabilidade_id();

                /* ENVIO PARA CONTABILIDADE */
                Movimento movimento = (Movimento) dao.find(new Movimento(), (Integer) selected.get(i).getMovimento_id());
                Juridica juridica = (Juridica) dao.find(new Juridica(), id_empresa);

                if (id_contabil != 0 && juridica.isEmailEscritorio()) {
                    movadd.add(movimento);
                    listaValores.add(movimento.getValor());
                    listaVencimentos.add(movimento.getVencimento());

                    juridica = dbj.pesquisaJuridicaPorPessoa(id_contabil);

                    try {
                        id_compara = (Integer) selected.get(i + 1).getContabilidade_id();
                        if (id_contabil != id_compara) {
                            enviar = true;
                        }
                    } catch (Exception e) {
                        enviar = true;
                    }
                    /* ENVIO PARA EMPRESA */
                } else {
                    movadd.add(movimento);
                    listaValores.add(movimento.getValor());
                    listaVencimentos.add(movimento.getVencimento());

                    try {
                        id_compara = (Integer) selected.get(i + 1).getEmpresa_id();
                        if (id_empresa != id_compara) {
                            enviar = true;
                        }
                    } catch (Exception e) {
                        enviar = true;
                    }
                }

                if (enviar) {
                    enviar(movadd, listaValores, listaVencimentos, juridica);
                    enviar = false;
                    movadd.clear();
                    listaValores.clear();
                    listaVencimentos.clear();
                }
            } catch (Exception ex) {
            }
        }
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

            String mensagem;
            List<File> fls = new ArrayList();

            String nome_envio;
            if (mov.size() == 1) {
                nome_envio = "Boleto " + mov.get(0).getServicos().getDescricao() + " N° " + mov.get(0).getDocumento();
            } else {
                nome_envio = "Boleto";
            }

            if (!reg.isEnviarEmailAnexo()) {
                mensagem = " <div style=\"background:#00ccff; padding: 15px; font-size:13pt\">Envio cadastrado para <b>" + jur.getPessoa().getNome() + " </b></div><br />"
                        + " <h5>Visualize seu boleto clicando no link abaixo</h5><br /><br />"
                        + " <a href=\"" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "\">Clique aqui para abrir boleto</a><br />";
            } else {
                fls.add(new File(imp.getPathPasta() + "/" + nome));
                mensagem = " <div style='background:#00ccff; padding: 15px; font-size:13pt'>Envio cadastrado para <b>" + jur.getPessoa().getNome() + " </b></div><br />"
                        + " <h5>Baixe seu boleto anexado neste email</5><br /><br />";
            }

            Dao di = new Dao();
            Mail mail = new Mail();
            mail.setFiles(fls);
            mail.setEmail(
                    new Email(
                            -1,
                            DataHoje.dataHoje(),
                            DataHoje.horaMinuto(),
                            (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                            (Rotina) di.find(new Rotina(), 90),
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
            msgImpressao = "Envio Concluído!";
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());

        }
    }

    public long getTotalBoletos() {
        return totalBoletos;
    }

    public void setTotalBoletos(long totalBoletos) {
        this.totalBoletos = totalBoletos;
    }

    public long getTotalEmpresas() {
        return totalEmpresas;
    }

    public void setTotalEmpresas(long totalEmpresas) {
        this.totalEmpresas = totalEmpresas;
    }

    public long getTotalEscritorios() {
        return totalEscritorios;
    }

    public void setTotalEscritorios(long totalEscritorios) {
        this.totalEscritorios = totalEscritorios;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public List<Convencao> getListaConvencao() {
        if (listaConvencao.isEmpty()) {
            listaConvencao = new Dao().list(new Convencao(), true);
        }
        return listaConvencao;
    }

    public List<GrupoCidade> getListaGrupoCidade() {
        if (listaGrupoCidade.isEmpty()) {
            if (!listaConvencaoSelecionada.isEmpty()) {
                ConvencaoCidadeDao convencaoCidadeDB = new ConvencaoCidadeDao();
                List<Integer> listInt = new ArrayList();

                for (int i = 0; i < listaConvencaoSelecionada.size(); i++) {
                    listInt.add(listaConvencaoSelecionada.get(i).getId());
                }

                listaGrupoCidade = convencaoCidadeDB.pesquisarConvencaoCidade(listInt);
            }
        }
        return listaGrupoCidade;
    }

    public void limpaGrupoCidade() {
        listaGrupoCidade.clear();
        listaGrupoSelecionada.clear();
    }

    public void setListaConvencao(List<Convencao> listaConvencao) {
        this.listaConvencao = listaConvencao;
    }

    public void setListaGrupoCidade(List<GrupoCidade> listaGrupoCidade) {
        this.listaGrupoCidade = listaGrupoCidade;
    }

    public String getMsgImpressao() {
        return msgImpressao;
    }

    public void setMsgImpressao(String msgImpressao) {
        this.msgImpressao = msgImpressao;
    }

    public String getTodasContas() {
        return todasContas;
    }

    public void setTodasContas(String todasContas) {
        this.todasContas = todasContas;
    }

    public String getMovimentosSemMensagem() {
        return movimentosSemMensagem;
    }

    public void setMovimentosSemMensagem(String movimentosSemMensagem) {
        this.movimentosSemMensagem = movimentosSemMensagem;
    }

    public String getRegraEscritorios() {
        return regraEscritorios;
    }

    public void setRegraEscritorios(String regraEscritorios) {
        if (regraEscritorios.equals("apartir")) {
            quantidadeEmpresas = 2;
        } else {
            quantidadeEmpresas = 1;
        }
        this.regraEscritorios = regraEscritorios;
    }

    public int getQuantidadeEmpresas() {
        return quantidadeEmpresas;
    }

    public void setQuantidadeEmpresas(int quantidadeEmpresas) {
        this.quantidadeEmpresas = quantidadeEmpresas;
    }

    public boolean isChkTodosVencimentos() {
        return chkTodosVencimentos;
    }

    public void setChkTodosVencimentos(boolean chkTodosVencimentos) {
        this.chkTodosVencimentos = chkTodosVencimentos;
    }

    public String getCbEmail() {
        return cbEmail;
    }

    public void setCbEmail(String cbEmail) {
        this.cbEmail = cbEmail;
    }

    public Juridica getContabilidade() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            listObjectImpressaoBoleto = new ArrayList();
            selected = new ArrayList();
            inicio = 0;
            fim = 0;
            quantidade = 0;
            contabilidade = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
            loadList();
        }
        return contabilidade;
    }

    public void printComunicado() {
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUM REGISTRO SELECIONADO!");
            return;
        }
        String comunicado = ConfiguracaoArrecadacao.get().getComunicado();
        if (comunicado.isEmpty()) {
            comunicado = "teste";
            GenericaMensagem.warn("Validação", "NÃO HÁ COMUNICADO!");
            // return;
        }
        Dao dao = new Dao();
        List<Pessoa> listPessoas = new ArrayList();
        for (int i = 0; i < selected.size(); i++) {
            if (((Integer) selected.get(i).getContabilidade_id()) != 0) {
                listPessoas.add((Pessoa) dao.find(new Pessoa(), ((Integer) selected.get(i).getContabilidade_id())));
            } else {
                listPessoas.add(((Juridica) dao.find(new Juridica(), ((Integer) selected.get(i).getEmpresa_id()))).getPessoa());
            }
        }

        SisCartaBean.printList("COMUNICADO", comunicado, listPessoas);

    }

    public void sendComunicado() {
        if (selected.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUM REGISTRO SELECIONADO!");
            return;
        }
        String comunicado = ConfiguracaoArrecadacao.get().getComunicado();
        if (comunicado.isEmpty()) {
            comunicado = "teste";
            GenericaMensagem.warn("Validação", "NÃO HÁ COMUNICADO!");
            // return;
        }
        Dao dao = new Dao();
        int[] pid = new int[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            if (((Integer) selected.get(i).getContabilidade_id()) != 0) {
                pid[i] = (int) selected.get(i).getContabilidade_id();
            } else {
                pid[i] = (int) ((Juridica) dao.find(new Juridica(), ((Integer) selected.get(i).getEmpresa_id()))).getPessoa().getId();
            }
        }
        List<Pessoa> listPessoas = (List<Pessoa>) dao.find("Pessoa", pid);
        List<Carta> c = new ArrayList<>();
        try {
            Carta carta = new Carta(
                    "COMUNICADO", // titulo
                    comunicado, // Texto
                    "Assinatura", // Assinatura
                    "Rodapé" // Rodapé

            );
            c.add(carta);
        } catch (Exception e2) {
            e2.getMessage();
        }
        Jasper.IS_HEADER = true;
        Jasper.PART_NAME = "";
        Jasper.IS_DOWNLOAD = false;
        Jasper.printReports("/Relatorios/CARTA.jasper", "comunicado", c);
        File fileComunicado = new File(Jasper.FILE_NAME_GENERATED);
        Dao di = new Dao();
        Mail mail = new Mail();
        mail.addFile(fileComunicado);
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        new Rotina().get(),
                        null,
                        "COMUNICADO",
                        "Segue comunicado em anexo",
                        false,
                        false
                )
        );
        List<EmailPessoa> emailPessoas = new ArrayList<>();
        EmailPessoa emailPessoa = new EmailPessoa();
        for (Pessoa p : listPessoas) {
            emailPessoa.setDestinatario(p.getEmail1());
            emailPessoa.setPessoa(p);
            emailPessoa.setRecebimento(null);
            emailPessoas.add(emailPessoa);
            mail.setEmailPessoas(emailPessoas);
            emailPessoa = new EmailPessoa();
        }
        String[] retorno = mail.send("personalizado");

        if (!retorno[1].isEmpty()) {
            GenericaMensagem.warn("Falha", "Email(s) " + retorno[1]);
        } else {
            GenericaMensagem.info("Sucesso", "Email(s) " + retorno[0]);
        }
        Jasper.deleteFile();
    }

    public void setContabilidade(Juridica contabilidade) {
        this.contabilidade = contabilidade;
    }

    public List<Convencao> getListaConvencaoSelecionada() {
        return listaConvencaoSelecionada;
    }

    public void setListaConvencaoSelecionada(List<Convencao> listaConvencaoSelecionada) {
        this.listaConvencaoSelecionada = listaConvencaoSelecionada;
    }

    public List<GrupoCidade> getListaGrupoSelecionada() {
        return listaGrupoSelecionada;
    }

    public void setListaGrupoSelecionada(List<GrupoCidade> listaGrupoSelecionada) {
        this.listaGrupoSelecionada = listaGrupoSelecionada;
    }

    public List<String> getListaDataSelecionada() {
        return listaDataSelecionada;
    }

    public void setListaDataSelecionada(List<String> listaDataSelecionada) {
        this.listaDataSelecionada = listaDataSelecionada;
    }

//    public List<Linha> getListaMovGridSelecionada() {
//        return listaMovGridSelecionada;
//    }
//
//    public void setListaMovGridSelecionada(List<Linha> listaMovGridSelecionada) {
//        this.listaMovGridSelecionada = listaMovGridSelecionada;
//    }
    public List<ObjectImpressaoBoleto> getListObjectImpressaoBoleto() {
        return listObjectImpressaoBoleto;
    }

    public void setListObjectImpressaoBoleto(List<ObjectImpressaoBoleto> listObjectImpressaoBoleto) {
        this.listObjectImpressaoBoleto = listObjectImpressaoBoleto;
    }

    public List<ObjectImpressaoBoleto> getSelected() {
        return selected;
    }

    public void setSelected(List<ObjectImpressaoBoleto> selected) {
        this.selected = selected;
    }

    public List<Impressao> getListHistoricoImpressao() {
        return listHistoricoImpressao;
    }

    public void setListHistoricoImpressao(List<Impressao> listHistoricoImpressao) {
        this.listHistoricoImpressao = listHistoricoImpressao;
    }

    public void loadHistorico(Integer movimento_id) {
        listHistoricoImpressao = new ArrayList();
        ImpressaoDao impressaoDao = new ImpressaoDao();
        listHistoricoImpressao = impressaoDao.findByMovimento(movimento_id);
    }

    public String getRegistrado() {
        return registrado;
    }

    public void setRegistrado(String registrado) {
        this.registrado = registrado;
    }

    public ServicoContaCobranca getServicoContaCobranca() {
        return servicoContaCobranca;
    }

    public void setServicoContaCobranca(ServicoContaCobranca servicoContaCobranca) {
        this.servicoContaCobranca = servicoContaCobranca;
    }

    public Map<String, Integer> getContaCobranca() {
        if (contaCobranca == null) {
            selectedContaCobranca = new ArrayList();
            contaCobranca = new LinkedHashMap<>();
            ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
            List<ServicoContaCobranca> list = servDB.pesquisaTodosTipoUm();
            if (list == null) {
                list = new ArrayList();
            }
            contaCobranca = new LinkedHashMap<>();
            for (int i = 0; i < list.size(); i++) {
                if (selectedContaCobranca.isEmpty()) {
                    String data_anterior = new DataHoje().decrementarMeses(1, DataHoje.data());
                    List lista = new MovimentoDao().datasMovimento(list.get(i).getServicos().getId(), list.get(i).getTipoServico().getId(), list.get(i).getContaCobranca().getId(), data_anterior);
                    if (lista != null && !lista.isEmpty()) {
                        selectedContaCobranca.add(list.get(i).getId());
                    }
                }
                contaCobranca.put(
                        (list.get(i).getServicos().getDescricao() + " - "
                        + list.get(i).getTipoServico().getDescricao() + " - "
                        + list.get(i).getContaCobranca().getCodCedente() + "" + (list.get(i).getContaCobranca().isCobrancaRegistrada() ? "( REGISTRADA )" : "") + "").toUpperCase(),
                        list.get(i).getId()
                );
            }
        }
        return contaCobranca;
    }

    public void setContaCobranca(Map<String, Integer> contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    public List getSelectedContaCobranca() {
        return selectedContaCobranca;
    }

    public void setSelectedContaCobranca(List selectedContaCobranca) {
        this.selectedContaCobranca = selectedContaCobranca;
    }

    public Boolean getHabilitarComunicado() {
        return habilitarComunicado;
    }

    public void setHabilitarComunicado(Boolean habilitarComunicado) {
        selected = new ArrayList();
        this.habilitarComunicado = habilitarComunicado;
    }

    public String getNovoVencto() {
        return novoVencto;
    }

    public void setNovoVencto(String novoVencto) {
        this.novoVencto = novoVencto;
    }

    public class ObjectImpressaoBoleto {

        private Integer indice;
        private Object boleto;
        private Object empresa_nome;
        private Object empresa_documento;
        private Object contabilidade_nome;
        private Object contribuicao;
        private Object tipo;
        private Object vencimento;
        private Object referencia;
        private Object movimento_id;
        private Object contabilidade_id;
        private Object empresa_id;
        private Object quantidade_empresas;
        private Object data_registro;
        private Boolean cobranca_registrada;

        public ObjectImpressaoBoleto() {
            this.indice = null;
            this.boleto = null;
            this.empresa_nome = null;
            this.empresa_documento = null;
            this.contabilidade_nome = null;
            this.contribuicao = null;
            this.tipo = null;
            this.vencimento = null;
            this.referencia = null;
            this.movimento_id = null;
            this.contabilidade_id = null;
            this.empresa_id = null;
            this.quantidade_empresas = null;
            this.data_registro = null;
            this.cobranca_registrada = null;
        }

        public ObjectImpressaoBoleto(Integer indice, Object boleto, Object empresa_nome, Object empresa_documento, Object contabilidade_nome, Object contribuicao, Object tipo, Object vencimento, Object referencia, Object movimento_id, Object contabilidade_id, Object empresa_id, Object quantidade_empresas, Object data_registro, Boolean cobranca_registrada) {
            this.indice = indice;
            this.boleto = boleto;
            this.empresa_nome = empresa_nome;
            this.empresa_documento = empresa_documento;
            this.contabilidade_nome = contabilidade_nome;
            this.contribuicao = contribuicao;
            this.tipo = tipo;
            this.vencimento = vencimento;
            this.referencia = referencia;
            this.movimento_id = movimento_id;
            this.contabilidade_id = contabilidade_id;
            this.empresa_id = empresa_id;
            this.quantidade_empresas = quantidade_empresas;
            this.data_registro = data_registro;
            this.cobranca_registrada = cobranca_registrada;
        }

        public Object getBoleto() {
            return boleto;
        }

        public void setBoleto(Object boleto) {
            this.boleto = boleto;
        }

        public Object getEmpresa_nome() {
            return empresa_nome;
        }

        public void setEmpresa_nome(Object empresa_nome) {
            this.empresa_nome = empresa_nome;
        }

        public Object getEmpresa_documento() {
            return empresa_documento;
        }

        public void setEmpresa_documento(Object empresa_documento) {
            this.empresa_documento = empresa_documento;
        }

        public Object getContabilidade_nome() {
            return contabilidade_nome;
        }

        public void setContabilidade_nome(Object contabilidade_nome) {
            this.contabilidade_nome = contabilidade_nome;
        }

        public Object getContribuicao() {
            return contribuicao;
        }

        public void setContribuicao(Object contribuicao) {
            this.contribuicao = contribuicao;
        }

        public Object getTipo() {
            return tipo;
        }

        public void setTipo(Object tipo) {
            this.tipo = tipo;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
        }

        public Object getReferencia() {
            return referencia;
        }

        public void setReferencia(Object referencia) {
            this.referencia = referencia;
        }

        public Object getMovimento_id() {
            return movimento_id;
        }

        public void setMovimento_id(Object movimento_id) {
            this.movimento_id = movimento_id;
        }

        public Object getContabilidade_id() {
            return contabilidade_id;
        }

        public void setContabilidade_id(Object contabilidade_id) {
            this.contabilidade_id = contabilidade_id;
        }

        public Object getEmpresa_id() {
            return empresa_id;
        }

        public void setEmpresa_id(Object empresa_id) {
            this.empresa_id = empresa_id;
        }

        public Object getQuantidade_empresas() {
            return quantidade_empresas;
        }

        public void setQuantidade_empresas(Object quantidade_empresas) {
            this.quantidade_empresas = quantidade_empresas;
        }

        public Object getData_registro() {
            return data_registro;
        }

        public void setData_registro(Object data_registro) {
            this.data_registro = data_registro;
        }

        public Integer getIndice() {
            return indice;
        }

        public void setIndice(Integer indice) {
            this.indice = indice;
        }

        public Boolean getCobranca_registrada() {
            return cobranca_registrada;
        }

        public void setCobranca_registrada(Boolean cobranca_registrada) {
            this.cobranca_registrada = cobranca_registrada;
        }

    }
}
