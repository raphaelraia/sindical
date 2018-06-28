package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.arrecadacao.dao.WebContabilidadeDao;
import br.com.rtools.financeiro.Boleto;
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
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class WebContabilidadeBean extends MovimentoValorBean {

    private Pessoa pessoa = null;
    private Juridica juridica = new Juridica();
    private Servicos servico = new Servicos();
    private TipoServico tipoServico = new TipoServico();
    private List<Juridica> listaEmpresa = new ArrayList();
    private List<Juridica> listaEmpresaSelecionada = new ArrayList();
    private int idTipoServico = 0;
    private String strReferencia = "";
    private String strVencimento = "";
    private String strFiltroRef = "";
    private String vlFolha = "0";
    private boolean impVerso = false;
    private final List<SelectItem> listaVencimento = new ArrayList();
    private Set<Integer> keys = null;
    private String lblLink = "";
    private Registro registro = new Registro();

    private List<ObjectListaMovimento> listaMovimento = new ArrayList();
    private List<ObjectListaMovimento> listaMovimentoSelecionado = new ArrayList();
    private ObjectListaMovimento olmSelecionado = null;

    private int idServicos = 0;
    private final List<SelectItem> listaServicos = new ArrayList();

    public WebContabilidadeBean() {
        FilialDao filDB = new FilialDao();
        registro = filDB.pesquisaRegistroPorFilial(1);
        pessoa = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb");
        juridica = new JuridicaDao().pesquisaJuridicaPorPessoa(pessoa.getId());

        WebContabilidadeDao db = new WebContabilidadeDao();
        listaEmpresa = db.listaEmpresasPertContabilidade(juridica.getId());

    }

    public void limparNovoBoleto() {
        strReferencia = "";
        idServicos = 0;
        idTipoServico = 0;
    }

    public void loadList() {
        listaMovimento.clear();

        if (listaEmpresaSelecionada.isEmpty()) {
            return;
        }

        WebContabilidadeDao db = new WebContabilidadeDao();
        Dao dao = new Dao();

        for (Juridica jur : listaEmpresaSelecionada) {
            List lista_result;
            if (strFiltroRef.isEmpty()) {
                lista_result = db.pesquisaMovParaWebContabilidade(jur.getPessoa().getId());
            } else {
                lista_result = db.pesquisaMovParaWebContabilidadeComRef(jur.getPessoa().getId(), strFiltroRef);
            }

            for (Object ob : lista_result) {
                List linha = (List) ob;

                Movimento movimento = (Movimento) dao.find(new Movimento(), (Integer) linha.get(15));

                if (movimento.getBoleto() == null){
                    GenericaMensagem.error("Atenção", "Movimento [ " + movimento.getId() +" ] sem BOLETO");
                    listaMovimento.clear();
                    return;
                }
                
                List<SelectItem> listVencimento = new ArrayList();

                TrataVencimentoRetorno tvr = TrataVencimento.movimentoExiste(movimento, jur, movimento.getReferencia(), movimento.getDtVencimento());

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

                tvr.getBoleto().setVencimento(tvr.getVencimentoBoletoString());
                tvr.getBoleto().setValor(tvr.getValor_calculado());

                new Dao().update(tvr.getBoleto(), true);

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

    }

    public List<SelectItem> getListaVencimento() {
        if (listaVencimento.isEmpty()) {
            String data = DataHoje.data();
            String newData = "";

            for (int i = 0; i < 6; i++) {
                newData = (new DataHoje()).incrementarDias(i, data);
                listaVencimento.add(new SelectItem(i, newData, newData));
            }
        }
        return listaVencimento;
    }

    public void adicionarBoleto() {
        if (listaEmpresaSelecionada.isEmpty()) {
            GenericaMensagem.warn("Atenção", "SELECIONE UMA EMPRESA PARA GERAR BOLETOS!");
            return;
        }

        Dao dao = new Dao();

        if (getListaServicos().isEmpty()) {
            GenericaMensagem.warn("Atenção", "LISTA DE SERVIÇOS VAZIA!");
            return;
        }

        if (getListaTipoServico().isEmpty()) {
            GenericaMensagem.warn("Atenção", "LISTA DE TIPO SERVIÇO VAZIA!");
            return;
        }

        servico = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));
        tipoServico = (TipoServico) new Dao().find(new TipoServico(), Integer.valueOf(getListaTipoServico().get(idTipoServico).getDescription()));

        MovimentoDao dbm = new MovimentoDao();

        if (new ContaCobrancaDao().pesquisaServicoCobranca(servico.getId(), tipoServico.getId()) == null) {
            GenericaMensagem.warn("Atenção", "NÃO EXISTE CONTA COBRANÇA PARAR GERAR!");
            return;
        }

        if (dbm.pesquisaMovimentosAcordado(juridica.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId()) != null) {
            GenericaMensagem.warn("Atenção", "JÁ FOI GERADO UM ACORDO PARA ESTA REFERÊNCIA, SERVIÇO E TIPO SERVIÇO!");
            return;
        }

        if (!(new DataHoje()).integridadeReferencia(strReferencia)) {
            GenericaMensagem.warn("Atenção", "REFEÊNCIA NÃO É VÁLIDA!");
            return;
        }

        for (Juridica jur : listaEmpresaSelecionada) {
            List<Movimento> lm = dbm.pesquisaMovimentos(jur.getPessoa().getId(), strReferencia, tipoServico.getId(), servico.getId());

            if (!lm.isEmpty() && lm.size() > 1) {
                GenericaMensagem.error("Atenção", "ATENÇÃO, MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR!");
                continue;
            } else if (!lm.isEmpty()) {
                GenericaMensagem.warn("Atenção", "ESTE BOLETO JÁ EXISTE PARA " + jur.getPessoa().getNome());
                continue;
            }

            String dataValida;
            DataHoje dh = new DataHoje();
            MensagemConvencao mc = new MensagemConvencaoDao().retornaDiaString(jur.getId(), strReferencia, tipoServico.getId(), servico.getId());

            if (mc == null) {
                GenericaMensagem.warn("Atenção", "MENSAGEM CONVENÇÃO NÃO EXISTE. ENTRAR EM CONTATO COM O SEU SINDICATO PARA PERMITIR A CRIAÇÃO DESTA REFERÊNCIA!");
                return;
            }

            if (registro.getDiasBloqueiaAtrasadosWeb() <= 0) {
                strVencimento = mc.getVencimento();
                dataValida = strVencimento;
            } else {
                strVencimento = mc.getVencimento();
                dataValida = dh.incrementarDias(registro.getDiasBloqueiaAtrasadosWeb(), strVencimento);
            }

            if (getValidaBloqueio(dataValida)) {
                GenericaMensagem.warn("Atenção", "NÃO É PERMITIDO GERAR BOLETO VENCIDO! " + registro.getMensagemBloqueioBoletoWeb());
                return;
            }

            TrataVencimentoRetorno tvr = TrataVencimento.movimentoNaoExiste(servico, tipoServico, jur, strReferencia, DataHoje.converte(strVencimento), super.carregarValor(servico.getId(), tipoServico.getId(), strReferencia, jur.getPessoa().getId()));

            StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimento(new Lote(), tvr.getMovimento(), tvr.getVencimentoBoleto(), tvr.getValor());

            if (!sr.getStatus()) {
                GenericaMensagem.error("Atenção", sr.getMensagem());
            }

        }

        GenericaMensagem.info("Sucesso", "BOLETOS ADICIONADOS!");
        loadList();

    }

    public boolean getValidaBloqueio(String data) {
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

    public String imprimirComValorCalculado() {
        List<Movimento> lista = new ArrayList();

        Dao dao = new Dao();
        String dataValida = "";
        DataHoje dh = new DataHoje();

        if (listaMovimentoSelecionado.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boleto foi selecionado!");
            return null;
        }

        for (ObjectListaMovimento olm : listaMovimentoSelecionado) {
            if (registro.getDiasBloqueiaAtrasadosWeb() <= 0) {
                dataValida = olm.getMovimento().getVencimento();
            } else {
                dataValida = dh.incrementarDias(registro.getDiasBloqueiaAtrasadosWeb(), olm.getMovimento().getVencimento());
            }

            if (getValidaBloqueio(dataValida)) {
                GenericaMensagem.warn("Atenção", "Não é possivel imprimir boletos vencidos! " + registro.getMensagemBloqueioBoletoWeb());
                return null;
            }
        }

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
                GenericaMensagem.error("Erro", "Não foi possível salvar impressão web");
                dao.rollback();
                return null;
            }

            if (!dao.update(olm.getBoleto())) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Boleto");
                dao.rollback();
                return null;
            }
        }
        dao.commit();

        ImprimirBoleto imp = new ImprimirBoleto();
        lista = imp.atualizaContaCobrancaMovimento(lista);

        imp.imprimirBoleto(lista, impVerso, false);
        imp.visualizar(null);

        loadList();
        return "webContabilidade";
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
        List<SelectItem> listaTipoServico = new ArrayList();
        DataHoje data = new DataHoje();
        Servicos servicos = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(getListaServicos().get(idServicos).getDescription()));
        int i = 0;
        TipoServicoDao db = new TipoServicoDao();
        if ((!data.integridadeReferencia(strReferencia)) || (registro == null) || (servicos == null)) {
            listaTipoServico.add(new SelectItem(0, "Digite uma referência", "0"));
            return listaTipoServico;
        }
        List<Integer> listaIds = new ArrayList();

        if (registro.getTipoEmpresa().equals("E")) {
            if ((Integer.parseInt(strReferencia.substring(0, 2)) == 3) && (servicos.getId() == 1)) {
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
        } else {
            return listaTipoServico;
        }

        List<TipoServico> select = db.pesquisaTodosComIds(listaIds);
        if (!select.isEmpty()) {
            while (i < select.size()) {
                listaTipoServico.add(new SelectItem(
                        i,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId())));
                i++;
            }
        } else {
            listaTipoServico.add(new SelectItem(0, "Selecionar um Tipo Serviço", "0"));
        }
        return listaTipoServico;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
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

    public String getVlFolha() {
        return Moeda.converteR$(vlFolha);
    }

    public void setVlFolha(String vlFolha) {
        this.vlFolha = Moeda.substituiVirgula(vlFolha);
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public String getStrVencimento() {
        return strVencimento;
    }

    public void setStrVencimento(String strVencimento) {
        this.strVencimento = strVencimento;
    }

    public boolean isImpVerso() {
        return impVerso;
    }

    public void setImpVerso(boolean impVerso) {
        this.impVerso = impVerso;
    }

    public void setListaVencimento(List<SelectItem> listaVencimento) {
        this.setListaVencimento(listaVencimento);
    }

    public synchronized Set getKeys() {
        return keys;
    }

    public synchronized void setKeys(Set keys) {
        this.keys = keys;
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
        olmSelecionado.setValor_calculado(Moeda.soma(Moeda.soma(Moeda.soma(olmSelecionado.getMulta(), olmSelecionado.getJuros()), olmSelecionado.getCorrecao()), olmSelecionado.getValor()));
        loadList();
    }

    public String getStrFiltroRef() {
        return strFiltroRef;
    }

    public void setStrFiltroRef(String strFiltroRef) {
        this.strFiltroRef = strFiltroRef;
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

    public List<Juridica> getListaEmpresa() {
        return listaEmpresa;
    }

    public void setListaEmpresa(List<Juridica> listaEmpresa) {
        this.listaEmpresa = listaEmpresa;
    }

    public List<Juridica> getListaEmpresaSelecionada() {
        return listaEmpresaSelecionada;
    }

    public void setListaEmpresaSelecionada(List<Juridica> listaEmpresaSelecionada) {
        this.listaEmpresaSelecionada = listaEmpresaSelecionada;
    }

    public List<ObjectListaMovimento> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimentos(List<ObjectListaMovimento> listaMovimento) {
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
