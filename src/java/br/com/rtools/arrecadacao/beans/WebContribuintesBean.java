package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.arrecadacao.dao.WebContribuintesDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.ImpressaoWeb;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.MovimentoValorBean;
import br.com.rtools.financeiro.dao.BoletoDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class WebContribuintesBean extends MovimentoValorBean {

    private Juridica juridica = new Juridica();
    private Servicos servico = new Servicos();
    private TipoServico tipoServico = new TipoServico();
    private Pessoa pessoa = null;
    private List<DataObject> listaMovimento = new ArrayList();
    private List<DataObject> listaMovimentoSelecionado = new ArrayList();
    private String strReferencia = "";
    private String strVencimento = "";
    private String strFiltroRef = "";
    private String strContrib = "";
    private String msgConfirma = "";
    private int idServicos = 0;
    private int idTipoServico = 0;
    private int idIndex = 0;
    private boolean impVerso = false;
    private boolean renderNovo = false;
    private List<SelectItem> listaVencimento = new ArrayList<SelectItem>();
    private String lblLink = "";
    private Registro registro = new Registro();

    public WebContribuintesBean() {
        FilialDao filDB = new FilialDao();
        registro = filDB.pesquisaRegistroPorFilial(1);
        pessoa = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb");

        loadList();
    }

    public void loadList() {
        listaMovimento.clear();
        WebContribuintesDao db = new WebContribuintesDao();
        Dao dao = new Dao();
        JuridicaDao dbJur = new JuridicaDao();
        juridica = dbJur.pesquisaJuridicaPorPessoa(pessoa.getId());

        List lista;
        if (strFiltroRef.isEmpty()) {
            lista = db.pesquisaMovParaWebContribuinte(juridica.getPessoa().getId());
        } else {
            lista = db.pesquisaMovParaWebContribuinteComRef(juridica.getPessoa().getId(), strFiltroRef);
        }

        for (int i = 0; i < lista.size(); i++) {
            if (((List) lista.get(i)).get(5) == null) {
                ((List) lista.get(i)).set(5, 0.0);
            }
            if (((List) lista.get(i)).get(6) == null) {
                ((List) lista.get(i)).set(6, 0.0);
            }
            if (((List) lista.get(i)).get(7) == null) {
                ((List) lista.get(i)).set(7, 0.0);
            }
            if (((List) lista.get(i)).get(8) == null) {
                ((List) lista.get(i)).set(8, 0.0);
            }
            if (((List) lista.get(i)).get(9) == null) {
                ((List) lista.get(i)).set(9, 0.0);
            }
            if (((List) lista.get(i)).get(10) == null) {
                ((List) lista.get(i)).set(10, 0.0);
            }
            if (((List) lista.get(i)).get(11) == null) {
                ((List) lista.get(i)).set(11, 0.0);
            }

            if (((Integer) ((List) lista.get(i)).get(13)) < 0) {
                ((List) lista.get(i)).set(13, 0);
            }

            // data ----
            boolean hdata, hvalor;
            int data1 = DataHoje.converteDataParaInteger(DataHoje.converteData((Date) ((List) lista.get(i)).get(4)));

            if (data1 < DataHoje.converteDataParaInteger(DataHoje.data())) {
                hdata = true;
            } else {
                hdata = false;
            }

            // valor ----
            if ((Integer) ((List) lista.get(i)).get(2) == 4) {
                hvalor = false;
            } else {
                hvalor = true;
            }

            String data = DataHoje.data();
            String newData = "";
            int z = 0;
            List<SelectItem> listVencimento = new ArrayList();
            while (z < 6) {
                newData = (new DataHoje()).incrementarDias(z, data);
                listVencimento.add(new SelectItem(z, newData, newData));
                z++;
            }
            Servicos s = (Servicos) dao.find(new Servicos(), (Integer) ((List) lista.get(i)).get(1));
            TipoServico ts = (TipoServico) dao.find(new TipoServico(), (Integer) ((List) lista.get(i)).get(2));
            ContaCobranca cc = new ContaCobrancaDao().pesquisaServicoCobranca(s.getId(), ts.getId());
            // Boleto b = new BoletoDao().findByNrCtrBoleto(((List) lista.get(i)).get(0).toString());
            // if (b != null) { 
            if (cc != null) {
                if (cc.getCobrancaRegistrada() != null) {
                    if (cc.getCobrancaRegistrada().getId() == 1) {
                        listVencimento = new ArrayList();
                        listVencimento.add(new SelectItem(0, DataHoje.converteData((Date) ((List) lista.get(i)).get(4)), DataHoje.converteData((Date) ((List) lista.get(i)).get(4))));
                    }
                }
            }
            //}

            listaMovimento.add(new DataObject(
                    false,
                    ((List) lista.get(i)).get(0), // boleto
                    s, // servico
                    ts, // tipo
                    ((List) lista.get(i)).get(3), // referencia
                    DataHoje.converteData((Date) ((List) lista.get(i)).get(4)), // vencimento
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(5))), // valor_mov
                    ((List) lista.get(i)).get(6), // valor_folha
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(7))), // multa
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(8))), // juros
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(9))), // correcao
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(10))), // desconto
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(11))), // valor_calculado
                    ((List) lista.get(i)).get(12), // meses em atraso
                    ((List) lista.get(i)).get(13), // dias em atraso
                    ((List) lista.get(i)).get(14), // indice
                    ((List) lista.get(i)).get(15), // id movimento
                    Moeda.converteR$(Double.toString((Double) ((List) lista.get(i)).get(11))), // valor_calculado original
                    hdata, // null
                    hvalor, // null
                    "0", // null
                    listVencimento, // null
                    null, // null
                    null, // null
                    null // null
            ));
        }
    }

    public List<SelectItem> getListaVencimento() {
        if (listaVencimento.isEmpty()) {
            String data = DataHoje.data();
            String newData = "";
            int i = 0;
            while (i < 6) {
                newData = (new DataHoje()).incrementarDias(i, data);
                listaVencimento.add(new SelectItem(i, newData, newData));
                i++;
            }
        }
        return listaVencimento;
    }

    public List<SelectItem> getListaServicos() {
        ServicosDao db = new ServicosDao();
        List<SelectItem> result = new ArrayList();
        List servicos = db.pesquisaTodos(4);
        int i = 0;
        while (i < servicos.size()) {
            result.add(new SelectItem(i,
                    ((Servicos) servicos.get(i)).getDescricao(),
                    Integer.toString(((Servicos) servicos.get(i)).getId())));
            i++;
        }
        return result;
    }

    public List<SelectItem> getListaTipoServico() {
        List<SelectItem> listaTipoServico = new ArrayList<>();
        DataHoje data = new DataHoje();
        ServicosDao dbSer = new ServicosDao();
        Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));
        int i = 0;
        TipoServicoDao db = new TipoServicoDao();
        if ((!data.integridadeReferencia(strReferencia))
                || (registro == null)
                || (servicos == null)) {
            listaTipoServico.add(new SelectItem(0, "Digite uma referência", "0"));
            return listaTipoServico;
        }

        List<Integer> listaIds = new ArrayList<Integer>();

        if (registro.getTipoEmpresa().equals("E")) {
            if ((Integer.parseInt(strReferencia.substring(0, 2)) == 3)
                    && (servicos.getId() == 1)) {
                listaIds.add(1);
                listaIds.add(3);
            } else if ((Integer.parseInt(strReferencia.substring(0, 2)) != 3)
                    && (servicos.getId() == 1)) {
                listaIds.add(2);
                listaIds.add(3);
            } else {
                listaIds.add(1);
                listaIds.add(2);
                listaIds.add(3);
            }
        } else if (registro.getTipoEmpresa().equals("P")) {
            if ((Integer.parseInt(strReferencia.substring(0, 2)) == 1)
                    && (servicos.getId() == 1)) {
                listaIds.add(1);
                listaIds.add(3);
            } else if ((Integer.parseInt(strReferencia.substring(0, 2)) != 1)
                    && (servicos.getId() == 1)) {
                listaIds.add(2);
                listaIds.add(3);
            } else {
                listaIds.add(1);
                listaIds.add(2);
                listaIds.add(3);
            }
        } else {
            return listaTipoServico;
        }
        List<TipoServico> select = db.pesquisaTodosComIds(listaIds);
        if (!select.isEmpty()) {
            for (int x = 0; x < select.size(); x++) {
                listaTipoServico.add(new SelectItem(
                        x,
                        select.get(x).getDescricao(),
                        Integer.toString(select.get(x).getId())));
            }
        } else {
            listaTipoServico.add(new SelectItem(0, "Selecionar um Tipo Serviço", "0"));
        }
        return listaTipoServico;
    }

    @Override
    public void carregarFolha(DataObject data) {
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = db.pesquisaCodigo((Integer) data.getArgumento16());
        super.carregarFolha(movimento);
    }

    @Override
    public void carregarFolha() {

    }

    @Override
    public void carregarFolha(Object valor) {

    }

    @Override
    public void atualizaValorGrid(String tipo) {
        //listaMovimento.get(idIndex).setArgumento6(super.atualizaValor(true, tipo));
        //listMovimentos.clear();
        super.atualizaValor(true, tipo);
        loadList();
    }

    public void imprimirComValorCalculado() {
        List<Movimento> lista = new ArrayList();
        List<Double> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();

        ImpressaoWeb impressaoWeb;
        Movimento movimento;
        String data = "";

        String dataValida = "";
        DataHoje dh = new DataHoje();

        if (listaMovimentoSelecionado.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boleto foi selecionado!");
            return;
        }

        for (DataObject listMovimento : listaMovimentoSelecionado) {
            if (registro.getDiasBloqueiaAtrasadosWeb() <= 0) {
                dataValida = (String) ((DataObject) listMovimento).getArgumento5();
            } else {
                dataValida = dh.incrementarDias(registro.getDiasBloqueiaAtrasadosWeb(), (String) ((DataObject) listMovimento).getArgumento5());
            }
            if (validaBloqueio(dataValida)) {
                GenericaMensagem.warn("Atenção", "Não é possivel imprimir boletos vencidos! " + registro.getMensagemBloqueioBoletoWeb());
                return;
            }
        }

        Dao dao = new Dao();

        dao.openTransaction();

        for (DataObject listMovimento : listaMovimentoSelecionado) {
            movimento = ((Movimento) dao.find(new Movimento(), (Integer) listMovimento.getArgumento16()));
            // COM VALOR ALTERADO ---------
            if (Moeda.substituiVirgulaDouble((String) listMovimento.getArgumento12()) != 0) {
                listaValores.add(Moeda.substituiVirgulaDouble((String) listMovimento.getArgumento12()));
            } else {
                if (Moeda.substituiVirgulaDouble((String) listMovimento.getArgumento6()) <= 0) {
                    GenericaMensagem.warn("Atenção", "Valor não pode ser zerado!");
                    return;
                }
                listaValores.add(Moeda.substituiVirgulaDouble((String) listMovimento.getArgumento6()));
            }
            //COM DATA ALTERADA ---------
            if (movimento.getDtVencimento().before(DataHoje.dataHoje()) && !movimento.getVencimento().equals(DataHoje.data())) {
                data = getListaVencimento().get(Integer.parseInt((String) listMovimento.getArgumento20())).getDescription();
                listaVencimentos.add(data);
            } else {
                listaVencimentos.add(movimento.getVencimento());
            }
            lista.add(movimento);
            impressaoWeb = new ImpressaoWeb(-1,
                    movimento,
                    pessoa,
                    DataHoje.dataHoje(), DataHoje.hora(), movimento.getDtVencimento());
            if (!dao.save(impressaoWeb)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Impressão Web, tente novamente!");
                dao.rollback();
                return;
            }
        }
        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();
        lista = imp.atualizaContaCobrancaMovimento(lista);
        imp.imprimirBoleto(lista, listaValores, listaVencimentos, impVerso);
        imp.visualizar(null);

        loadList();
    }

    public boolean validaBloqueio(String data) {
        if (registro.isBloqueiaAtrasadosWeb()) {
            int data1 = DataHoje.converteDataParaInteger(data);
            int data2 = DataHoje.converteDataParaInteger(DataHoje.data());

            if (data1 < data2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String imprimirBoleto() {
        Movimento movimento;
        List<Movimento> lista = new ArrayList<Movimento>();
        List<Double> listaValores = new ArrayList<Double>();
        List<String> listaVencimentos = new ArrayList<String>();
        for (int i = 0; i < listaMovimentoSelecionado.size(); i++) {
            movimento = (Movimento) listaMovimentoSelecionado.get(i).getArgumento1();
            lista.add(movimento);
            listaValores.add(movimento.getValor());
            listaVencimentos.add(movimento.getVencimento());

        }
        ImprimirBoleto imp = new ImprimirBoleto();
        imp.imprimirBoleto(lista, listaValores, listaVencimentos, impVerso);
        imp.visualizar(null);
        return null;
    }

    public void adicionarBoleto() {
        try {
            MensagemConvencao mc = new MensagemConvencao();
            Dao dao = new Dao();
            MensagemConvencaoDao dbCon = new MensagemConvencaoDao();
            TipoServicoDao dbTipo = new TipoServicoDao();
            ContaCobrancaDao ctaCobraDB = new ContaCobrancaDao();
            ContaCobranca contaCob = new ContaCobranca();
            String dataValida = "";
            DataHoje dh = new DataHoje();

            if (getListaServicos().isEmpty()) {
                GenericaMensagem.error("Erro", "Lista de Serviços está vazia!");
                return;
            }

            if (getListaTipoServico().size() == 1 && getListaTipoServico().get(0).getDescription().equals("0")) {
                GenericaMensagem.error("Erro", "Não é possível adicionar Boleto sem Tipo Serviço!");
                return;
            }

            servico = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));
            tipoServico = dbTipo.pesquisaCodigo(Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()));
            contaCob = ctaCobraDB.pesquisaServicoCobranca(servico.getId(), tipoServico.getId());
            if (contaCob == null) {
                //msgConfirma = ;
                GenericaMensagem.warn("Atenção", "Não existe conta Cobrança para gerar, contate seu Sindicato.");
                return;
            }

            MovimentoDao dbm = new MovimentoDao();

            List<Movimento> lm = dbm.pesquisaMovimentos(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId());

            if (!lm.isEmpty() && lm.size() > 1) {
                GenericaMensagem.error("Erro", "ATENÇÃO, MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR!");
                return;
            } else if (!lm.isEmpty()) {
                GenericaMensagem.error("Atenção", "Este boleto já existe!");
                return;
            }

            if (dbm.pesquisaMovimentosAcordado(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId()) != null) {
                GenericaMensagem.warn("Atenção", "Já foi gerado um Acordo para esta REFERÊNCIA, SERVIÇO e TIPO SERVIÇO!");
                return;
            }

            if ((new DataHoje()).integridadeReferencia(strReferencia)) {
                mc = dbCon.retornaDiaString(juridica.getId(), strReferencia, tipoServico.getId(), servico.getId());
                if (mc != null) {

                    if (registro.getDiasBloqueiaAtrasadosWeb() <= 0) {
                        strVencimento = mc.getVencimento();
                        dataValida = strVencimento;
                    } else {
                        strVencimento = mc.getVencimento();
                        dataValida = dh.incrementarDias(registro.getDiasBloqueiaAtrasadosWeb(), strVencimento);
                    }

                    if (validaBloqueio(dataValida)) {
                        GenericaMensagem.warn("Atenção", "Não é permitido gerar boleto vencido! " + registro.getMensagemBloqueioBoletoWeb());
                        return;
                    }

//                    strVencimento = mc.getVencimento();
//                    if (strVencimento.equals("")) {
//                        strVencimento = DataHoje.data();
//                    }
                    Movimento movi = new Movimento(-1,
                            null,
                            servico.getPlano5(),
                            juridica.getPessoa(),
                            servico,
                            null,
                            tipoServico,
                            null,
                            Moeda.converteDoubleR$Double(super.carregarValor(servico.getId(), tipoServico.getId(), strReferencia, juridica.getPessoa().getId())),
                            strReferencia,
                            strVencimento,
                            1,
                            true,
                            "E",
                            false,
                            juridica.getPessoa(),
                            juridica.getPessoa(),
                            "",
                            "",
                            strVencimento,
                            0,
                            0, 0, 0, 0, 0, 0, (FTipoDocumento) new Dao().find(new FTipoDocumento(), 2),
                            0,
                            null
                    );

                    StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimento(new Lote(), movi);

                    if (sr.getStatus()) {

                        loadList();
                        GenericaMensagem.info("Sucesso", "Boleto Adicionado!");
                        renderNovo = false;

                    } else {
                        GenericaMensagem.error("Atenção", sr.getMensagem());
                    }
                } else {
                    GenericaMensagem.warn("Atenção", "Mensagem Convenção não existe. Entrar em contato com seu Sindicato para permitir a criação desta referência!");
                }
            } else {
                msgConfirma = " Referência não esta válida!";
                GenericaMensagem.warn("Atenção", "Essa referência não é válida!");
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }

    public String carregaUsuarioPG() {
        /*usuario = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb");
         FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);*/
        return null;
    }

    public void refreshForm() {
    }

    public void validaReferencia() {
        DataHoje dataHoje = new DataHoje();
        if (dataHoje.integridadeReferencia(strReferencia)) {
            String dataS = "01/" + strReferencia;
            // A diferença é de no máximo 5 anos para geração de boletos
            String dataLimite = dataHoje.decrementarMeses(60, DataHoje.data());
            Integer[] integer = dataHoje.diferencaEntreDatas(dataLimite, dataS);
            if (integer == null) {
                strReferencia = "";
                GenericaMensagem.warn("Atenção", "Essa referência não é válida!");
                return;
            }
            if (integer[2] < 0) {
                strReferencia = "";
                GenericaMensagem.warn("Atenção", "Não é permitido emitir boletos com período superior a 5 anos atras!");
                return;
            }
            if (integer[2] > 5) {
                strReferencia = "";
                GenericaMensagem.warn("Atenção", "Não é permitido emitir boletos com períodos futuros que excedem a faixa do ano atual! Ex. Não é possível emitir um boleto de referência superior ao ano corrente.");
            }
        } else {
            strReferencia = "";
            GenericaMensagem.warn("Atenção", "Essa referência não é válida!");
        }
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setUsuario(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public int getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(int idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public String getStrReferencia() {
        return strReferencia;
    }

    public void setStrReferencia(String strReferencia) {
        this.strReferencia = strReferencia;
    }

    public String getStrVencimento() {
        return strVencimento;
    }

    public void setStrVencimento(String strVencimento) {
        this.strVencimento = strVencimento;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public boolean isImpVerso() {
        return impVerso;
    }

    public void setImpVerso(boolean impVerso) {
        this.impVerso = impVerso;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public void setListaVencimento(List<SelectItem> listaVencimento) {
        this.setListaVencimento(listaVencimento);
    }

    public String getStrFiltroRef() {
        return strFiltroRef;
    }

    public void setStrFiltroRef(String strFiltroRef) {
        this.strFiltroRef = strFiltroRef;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public String getLblLink() {
        if (!super.getLabelLink().isEmpty()) {
            lblLink = "Não existe desconto empregado para essa referência, favor contate seu Sindicato";
        } else {
            lblLink = "";
        }
        return lblLink;
    }

    public void setLblLink(String lblLink) {
        this.lblLink = lblLink;
    }

    public boolean isRenderNovo() {
        return renderNovo;
    }

    public void setRenderNovo(boolean renderNovo) {
        this.renderNovo = renderNovo;
    }

    public List<DataObject> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<DataObject> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public List<DataObject> getListaMovimentoSelecionado() {
        return listaMovimentoSelecionado;
    }

    public void setListaMovimentoSelecionado(List<DataObject> listaMovimentoSelecionado) {
        this.listaMovimentoSelecionado = listaMovimentoSelecionado;
    }
}
