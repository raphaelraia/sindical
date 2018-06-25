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
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.movimento.TrataVencimento;
import br.com.rtools.movimento.TrataVencimentoRetorno;
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
    private String strReferencia = "";
    private String strVencimento = "";
    private String strFiltroRef = "";
    private String msgConfirma = "";
    private int idTipoServico = 0;
    private int idIndex = 0;
    private boolean impVerso = false;
    private boolean renderNovo = false;
    private final List<SelectItem> listaVencimento = new ArrayList();
    private String lblLink = "";
    private Registro registro = new Registro();

    private List<ObjectListaMovimento> listaMovimento = new ArrayList();
    private List<ObjectListaMovimento> listaMovimentoSelecionado = new ArrayList();
    private ObjectListaMovimento olmSelecionado = null;

    private int idServicos = 0;
    private final List<SelectItem> listaServicos = new ArrayList();

    public WebContribuintesBean() {
        FilialDao filDB = new FilialDao();
        registro = filDB.pesquisaRegistroPorFilial(1);
        pessoa = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb");

        loadList();
    }

    public void limparNovoBoleto() {
        strReferencia = "";
        idServicos = 0;
        idTipoServico = 0;
    }

    public final void loadList() {
        listaMovimento.clear();

        juridica = new JuridicaDao().pesquisaJuridicaPorPessoa(pessoa.getId());

        WebContribuintesDao db = new WebContribuintesDao();
        List lista;
        if (strFiltroRef.isEmpty()) {
            lista = db.pesquisaMovParaWebContribuinte(juridica.getPessoa().getId());
        } else {
            lista = db.pesquisaMovParaWebContribuinteComRef(juridica.getPessoa().getId(), strFiltroRef);
        }

        Dao dao = new Dao();

        for (Object ob : lista) {
            List linha = (List) ob;
            Movimento movimento = (Movimento) dao.find(new Movimento(), (Integer) linha.get(15));
            Boleto boleto = movimento.getBoleto();

            List<SelectItem> listVencimento = new ArrayList();

            TrataVencimentoRetorno tvr = TrataVencimento.movimentoExiste(movimento, juridica, movimento.getReferencia(), movimento.getDtVencimento());

            if (tvr.getVencido()) {
                if (tvr.getRegistrado()) {
                    listVencimento.add(new SelectItem(0, tvr.getBoleto().getVencimento(), tvr.getBoleto().getVencimento()));
                } else {
                    for (int i = 0; i < 6; i++) {
                        String newData = (new DataHoje()).incrementarDias(i, DataHoje.data());
                        listVencimento.add(new SelectItem(i, newData, newData));
                    }
                }
            } else {
                listVencimento.add(new SelectItem(0, tvr.getMovimento().getVencimento(), tvr.getMovimento().getVencimento()));
            }
            
            listaMovimento.add(new ObjectListaMovimento(
                    tvr.getBoleto(), // BOLETO
                    tvr.getMovimento(), // MOVIMENTO 
                    tvr.getValor(), // VALOR
                    tvr.getJuros(), // JUROS
                    tvr.getMulta(), // MULTA
                    tvr.getCorrecao(), // CORRECAO
                    tvr.getValor_calculado(), // VALOR CALCULADO
                    0, // INDEX VENCIMENTOS PARA O BOLETO
                    listVencimento // LISTA DE VENCIMENTOS PARA O BOLETO
            ));

        }
    }

    public List<SelectItem> getListaVencimento() {
        if (listaVencimento.isEmpty()) {
            String data = DataHoje.data();
            for (int i = 0; i < 6; i++) {
                String newData = (new DataHoje()).incrementarDias(i, data);
                listaVencimento.add(new SelectItem(i, newData, newData));
            }
        }
        return listaVencimento;
    }

    public List<SelectItem> getListaServicos() {
        if (listaServicos.isEmpty()) {

            List<Servicos> result = new ServicosDao().pesquisaTodos(4);

            for (int i = 0; i < result.size(); i++) {
                listaServicos.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }

        return listaServicos;
    }

    public List<SelectItem> getListaTipoServico() {
        List<SelectItem> listaTipoServico = new ArrayList<>();
        DataHoje data = new DataHoje();
        Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));

        if (!data.integridadeReferencia(strReferencia) || (registro == null) || (servicos == null)) {
            listaTipoServico.add(new SelectItem(0, "Digite uma referência", "0"));
            return listaTipoServico;
        }

        List<Integer> listaIds = new ArrayList();

        switch (registro.getTipoEmpresa()) {
            case "E":
                if ((Integer.parseInt(strReferencia.substring(0, 2)) == 3) && (servicos.getId() == 1)) {
                    listaIds.add(1);
                    listaIds.add(3);
                } else if ((Integer.parseInt(strReferencia.substring(0, 2)) != 3) && (servicos.getId() == 1)) {
                    listaIds.add(2);
                    listaIds.add(3);
                } else {
                    listaIds.add(1);
                    listaIds.add(2);
                    listaIds.add(3);
                }
                break;
            case "P":
                if ((Integer.parseInt(strReferencia.substring(0, 2)) == 1) && (servicos.getId() == 1)) {
                    listaIds.add(1);
                    listaIds.add(3);
                } else if ((Integer.parseInt(strReferencia.substring(0, 2)) != 1) && (servicos.getId() == 1)) {
                    listaIds.add(2);
                    listaIds.add(3);
                } else {
                    listaIds.add(1);
                    listaIds.add(2);
                    listaIds.add(3);
                }
                break;
            default:
                return listaTipoServico;
        }

        TipoServicoDao db = new TipoServicoDao();
        List<TipoServico> select = db.pesquisaTodosComIds(listaIds);

        if (!select.isEmpty()) {
            for (int x = 0; x < select.size(); x++) {
                listaTipoServico.add(new SelectItem(
                        x,
                        select.get(x).getDescricao(),
                        Integer.toString(select.get(x).getId()))
                );
            }
        } else {
            listaTipoServico.add(new SelectItem(0, "Selecionar um Tipo Serviço", "0"));
        }

        return listaTipoServico;
    }

    @Override
    public void carregarFolha(DataObject data) {

    }

    @Override
    public void carregarFolha() {

    }

    @Override
    public void carregarFolha(Object linha) {
        super.carregarFolha(((ObjectListaMovimento) linha).getMovimento());
        olmSelecionado = (ObjectListaMovimento) linha;
    }

    @Override
    public void atualizaValorGrid(String tipo) {
        olmSelecionado.setValorString(super.atualizaValor(true, tipo));

        loadList();
    }

    public void imprimirComValorCalculado() {
        List<Movimento> lista = new ArrayList();

        String data = "";

        DataHoje dh = new DataHoje();

        if (listaMovimentoSelecionado.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boleto foi selecionado!");
            return;
        }

        for (ObjectListaMovimento olm : listaMovimentoSelecionado) {
            String dataValida;
            if (registro.getDiasBloqueiaAtrasadosWeb() <= 0) {
                dataValida = olm.getMovimento().getVencimento();
            } else {
                dataValida = dh.incrementarDias(registro.getDiasBloqueiaAtrasadosWeb(), olm.getMovimento().getVencimento());
            }

            if (validaBloqueio(dataValida)) {
                GenericaMensagem.warn("Atenção", "Não é possivel imprimir boletos vencidos! " + registro.getMensagemBloqueioBoletoWeb());
                return;
            }
        }

        Dao dao = new Dao();

        dao.openTransaction();

        for (ObjectListaMovimento olm : listaMovimentoSelecionado) {
            olm.getBoleto().setValor(olm.getValor_calculado());
            olm.getBoleto().setVencimento(olm.getListaVencimentoBoleto().get(olm.getIndexVencimentoBoleto()).getDescription());

            lista.add(olm.getMovimento());

            ImpressaoWeb impressaoWeb = new ImpressaoWeb(
                    -1,
                    olm.getMovimento(),
                    pessoa,
                    DataHoje.dataHoje(),
                    DataHoje.hora(),
                    olm.getBoleto().getDtVencimento()
            );

            if (!dao.save(impressaoWeb)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Impressão Web, tente novamente!");
                dao.rollback();
                return;
            }

            if (!dao.update(olm.getBoleto())) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Boleto");
                dao.rollback();
                return;
            }
        }
        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();
        lista = imp.atualizaContaCobrancaMovimento(lista);
        imp.imprimirBoleto(lista, impVerso, false);
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

    public void adicionarBoleto() {
        try {
            Dao dao = new Dao();

            ContaCobrancaDao ctaCobraDB = new ContaCobrancaDao();

            DataHoje dh = new DataHoje();

            if (getListaServicos().isEmpty()) {
                GenericaMensagem.error("Atenção", "Lista de Serviços está vazia!");
                return;
            }

            if (getListaTipoServico().size() == 1 && getListaTipoServico().get(0).getDescription().equals("0")) {
                GenericaMensagem.error("Atenção", "Não é possível adicionar Boleto sem Tipo Serviço!");
                return;
            }

            servico = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));
            tipoServico = (TipoServico) new Dao().find(new TipoServico(), Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()));

            ContaCobranca contaCob = ctaCobraDB.pesquisaServicoCobranca(servico.getId(), tipoServico.getId());
            if (contaCob == null) {
                GenericaMensagem.warn("Atenção", "Não existe conta Cobrança para gerar, contate seu Sindicato.");
                return;
            }

            MovimentoDao dbm = new MovimentoDao();

            List<Movimento> lm = dbm.pesquisaMovimentos(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId());

            if (!lm.isEmpty() && lm.size() > 1) {
                GenericaMensagem.error("Atenção", "ATENÇÃO, MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR!");
                return;
            } else if (!lm.isEmpty()) {
                GenericaMensagem.error("Atenção", "Este boleto já existe!");
                return;
            }

            if (dbm.pesquisaMovimentosAcordado(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId()) != null) {
                GenericaMensagem.warn("Atenção", "Já foi gerado um Acordo para esta REFERÊNCIA, SERVIÇO e TIPO SERVIÇO!");
                return;
            }

            if (!(new DataHoje()).integridadeReferencia(strReferencia)) {
                msgConfirma = " Referência não esta válida!";
                GenericaMensagem.warn("Atenção", "Essa referência não é válida!");
            }

            MensagemConvencao mc = new MensagemConvencaoDao().retornaDiaString(juridica.getId(), strReferencia, tipoServico.getId(), servico.getId());
            if (mc == null) {
                GenericaMensagem.warn("Atenção", "Mensagem Convenção não existe. Entrar em contato com seu Sindicato para permitir a criação desta referência!");
                return;
            }

            String dataValida;
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

            TrataVencimentoRetorno tvr = TrataVencimento.movimentoNaoExiste(servico, tipoServico, juridica, strReferencia, DataHoje.converte(strVencimento), super.carregarValor(servico.getId(), tipoServico.getId(), strReferencia, juridica.getPessoa().getId()));

            StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimento(new Lote(), tvr.getMovimento(), tvr.getVencimentoBoleto(), tvr.getValor());

            if (sr.getStatus()) {

                loadList();
                GenericaMensagem.info("Sucesso", "Boleto Adicionado!");
                renderNovo = false;

            } else {
                GenericaMensagem.error("Atenção", sr.getMensagem());
            }

        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }

    public Movimento novoMovimento(Servicos servicos, TipoServico tipoServico, Pessoa pessoa, Double m_valor, String m_vencimento, String referencia) {
        return new Movimento(
                -1,
                null,
                servicos.getPlano5(),
                pessoa,
                servicos,
                null,
                tipoServico,
                null,
                m_valor,
                referencia,
                m_vencimento,
                1,
                true,
                "E",
                false,
                pessoa,
                pessoa,
                "",
                "",
                m_vencimento,
                0, 0, 0, 0, 0, 0, 0,
                (FTipoDocumento) new Dao().find(new FTipoDocumento(), 2),
                0,
                null
        );
    }

    public void validaReferencia() {
        DataHoje dataHoje = new DataHoje();
        if (dataHoje.integridadeReferencia(strReferencia)) {

            // A diferença é de no máximo 5 anos para geração de boletos
            String dataLimite = dataHoje.decrementarMeses(60, DataHoje.data());
            Integer[] integer = dataHoje.diferencaEntreDatas(dataLimite, "01/" + strReferencia);
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

    public List<ObjectListaMovimento> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<ObjectListaMovimento> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public List<ObjectListaMovimento> getListaMovimentoSelecionado() {
        return listaMovimentoSelecionado;
    }

    public void setListaMovimentoSelecionado(List<ObjectListaMovimento> listaMovimentoSelecionado) {
        this.listaMovimentoSelecionado = listaMovimentoSelecionado;
    }

    public class ObjectListaMovimento {

        private Boleto boleto;
        private Movimento movimento;
        private Double valor;
        private Double juros;
        private Double multa;
        private Double correcao;
        private Double valor_calculado;
        private Integer indexVencimentoBoleto;
        private List<SelectItem> listaVencimentoBoleto;

        public ObjectListaMovimento(Boleto boleto, Movimento movimento, Double valor, Double juros, Double multa, Double correcao, Double valor_calculado, Integer indexVencimentoBoleto, List<SelectItem> listaVencimentoBoleto) {
            this.boleto = boleto;
            this.movimento = movimento;
            this.valor = valor;
            this.juros = juros;
            this.multa = multa;
            this.correcao = correcao;
            this.valor_calculado = valor_calculado;
            this.indexVencimentoBoleto = indexVencimentoBoleto;
            this.listaVencimentoBoleto = listaVencimentoBoleto;
        }

        public Boleto getBoleto() {
            return boleto;
        }

        public void setBoleto(Boleto boleto) {
            this.boleto = boleto;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteDoubleToString(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteStringToDouble(valorString);
        }

        public Double getJuros() {
            return juros;
        }

        public void setJuros(Double juros) {
            this.juros = juros;
        }

        public String getJurosString() {
            return Moeda.converteDoubleToString(juros);
        }

        public void setJurosString(String jurosString) {
            this.juros = Moeda.converteStringToDouble(jurosString);
        }

        public Double getMulta() {
            return multa;
        }

        public void setMulta(Double multa) {
            this.multa = multa;
        }

        public String getMultaString() {
            return Moeda.converteDoubleToString(multa);
        }

        public void setMultaString(String multaString) {
            this.multa = Moeda.converteStringToDouble(multaString);
        }

        public Double getCorrecao() {
            return correcao;
        }

        public void setCorrecao(Double correcao) {
            this.correcao = correcao;
        }

        public String getCorrecaoString() {
            return Moeda.converteDoubleToString(correcao);
        }

        public void setCorrecaoString(String correcaoString) {
            this.correcao = Moeda.converteStringToDouble(correcaoString);
        }

        public Double getValor_calculado() {
            return valor_calculado;
        }

        public void setValor_calculado(Double valor_calculado) {
            this.valor_calculado = valor_calculado;
        }

        public String getValor_calculadoString() {
            return Moeda.converteDoubleToString(valor_calculado);
        }

        public void setValor_calculadoString(String valor_calculadoString) {
            this.valor_calculado = Moeda.converteStringToDouble(valor_calculadoString);
        }

        public Integer getIndexVencimentoBoleto() {
            return indexVencimentoBoleto;
        }

        public void setIndexVencimentoBoleto(Integer indexVencimentoBoleto) {
            this.indexVencimentoBoleto = indexVencimentoBoleto;
        }

        public List<SelectItem> getListaVencimentoBoleto() {
            return listaVencimentoBoleto;
        }

        public void setListaVencimentoBoleto(List<SelectItem> listaVencimentoBoleto) {
            this.listaVencimentoBoleto = listaVencimentoBoleto;
        }

    }
}
