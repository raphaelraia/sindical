package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.locadoraFilme.ConfiguracaoLocadora;
import br.com.rtools.locadoraFilme.LocadoraAutorizados;
import br.com.rtools.locadoraFilme.LocadoraLote;
import br.com.rtools.locadoraFilme.LocadoraMovimento;
import br.com.rtools.locadoraFilme.LocadoraStatus;
import br.com.rtools.locadoraFilme.Titulo;
import br.com.rtools.locadoraFilme.dao.LocadoraAutorizadosDao;
import br.com.rtools.locadoraFilme.dao.LocadoraLoteDao;
import br.com.rtools.locadoraFilme.dao.LocadoraMovimentoDao;
import br.com.rtools.locadoraFilme.dao.LocadoraStatusDao;
import br.com.rtools.locadoraFilme.dao.TituloDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class DevolucaoFilmeBean implements Serializable {

    private LocadoraLote locadoraLote;
    private LocadoraMovimento locadoraMovimento;
    private String codigoLocatario;
    private String codigoBarras;
    private String dataDevolucaoString;
    private Titulo titulo;
    private List<LocadoraMovimento> listLocadoraMovimento;
    private List<LocadoraMovimento> listLocadoraHistorico;
    private List<LocadoraLote> listLocadoraLote;
    private Fisica locatario;
    private List<SelectItem> listLocadoraAutorizados;
    private PessoaComplemento pessoaComplemento;
    private Integer idLocadoraAutorizado;
    private LocadoraStatus locadoraStatus;
    private String status;
    private LocadoraMovimento desfazerDevolucao;

    @PostConstruct
    public void init() {
        locadoraLote = new LocadoraLote();
        locadoraMovimento = new LocadoraMovimento();
        desfazerDevolucao = null;
        codigoLocatario = "";
        codigoBarras = "";
        titulo = new Titulo();
        listLocadoraMovimento = new ArrayList();
        listLocadoraLote = new ArrayList();
        listLocadoraAutorizados = new ArrayList();
        listLocadoraHistorico = new ArrayList();
        locatario = new Fisica();
        pessoaComplemento = null;
        idLocadoraAutorizado = null;
        locadoraStatus = new LocadoraStatusDao().findByFilialData(MacFilial.getAcessoFilial().getFilial().getId());
        if (locadoraStatus == null) {
            locadoraStatus = new LocadoraStatusDao().findByFilialSemana(MacFilial.getAcessoFilial().getFilial().getId());
        }
        dataDevolucaoString = DataHoje.data();
        status = "pendentes";
        loadLocadoraMovimento();
    }

    public void loadLocadoraAutorizados() {
        listLocadoraAutorizados.clear();
        if (locatario.getId() != -1) {
            List<LocadoraAutorizados> list = new LocadoraAutorizadosDao().findAllByTitular(locatario.getPessoa().getId());
            listLocadoraAutorizados.add(new SelectItem(null, "Próprio"));
            idLocadoraAutorizado = null;
            for (int i = 0; i < list.size(); i++) {
                listLocadoraAutorizados.add(new SelectItem(list.get(i).getId(), list.get(i).getNome() + " - Parentesco: " + list.get(i).getParentesco().getParentesco()));
            }
        }
    }

    public void load() {
        if (GenericaSessao.exists("baixa_geral_sucesso")) {
            if (GenericaSessao.getBoolean("baixa_geral_sucesso", true)) {
                loadLocadoraMovimento();
            }
        }
    }

    public void loadLocadoraMovimento() {
        listLocadoraMovimento.clear();
        listLocadoraMovimento = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa(status, locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
        if (!listLocadoraMovimento.isEmpty()) {
            if (GenericaSessao.exists("locadora_movimento")) {
                LocadoraMovimento lm = (LocadoraMovimento) GenericaSessao.getObject("locadora_movimento", true);
                for (int i = 0; i < listLocadoraMovimento.size(); i++) {
                    if (listLocadoraMovimento.get(i).getId().equals(lm.getId())) {
                        listLocadoraMovimento.get(i).setSelected(true);
                        break;
                    }
                }
            }
        }
        listLocadoraHistorico.clear();
        listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("nao_devolvidos", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("tituloPesquisa");
        GenericaSessao.remove("devolucaoFilmeBean");
        GenericaSessao.remove("titulosNotIn");
        GenericaSessao.remove("baixa_geral_sucesso");
    }

    public void save() {
        if (locadoraStatus == null) {
            locadoraStatus = new LocadoraStatusDao().findByFilialSemana(MacFilial.getAcessoFilial().getFilial().getId());
            if (locadoraStatus == null) {
                GenericaMensagem.warn("Erro", "Nenhuma regra de locação e serviço definido para esta filial! Vá no Menu Locadora > Cadastro > Status");
                return;
            }
        }
        if (listLocadoraMovimento.isEmpty()) {
            GenericaMensagem.warn("Validação", "Adicione filmes para concluir esta locação!");
            return;
        }
        ConfiguracaoLocadora cf = ConfiguracaoLocadoraBean.get();
        if (cf == null || cf.getServicos() == null) {
            GenericaMensagem.warn("Validação", "Informar serviço da locadora! Menu Principal > Segurança > Departamentos");
            return;
        }
        Servicos servicoLocadora = cf.get().getServicos();
        if (servicoLocadora == null) {
            GenericaMensagem.warn("Validação", "Informar serviço da locadora!");
            return;
        }
        Dao dao = new Dao();
        Lote lote = new Lote();
        Double valorTotal = new Double(0);
        FTipoDocumento fTipoDocumento = (FTipoDocumento) dao.find(new FTipoDocumento(), 3);
        Departamento departamento = (Departamento) dao.find(new Departamento(), 19);
        dao.openTransaction();
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getSelected()) {
                if (listLocadoraMovimento.get(i).getDtDevolucao() == null) {
                    listLocadoraMovimento.get(i).setSelected(false);
                    listLocadoraMovimento.get(i).setDtDevolucao(DataHoje.dataHoje());
                    listLocadoraMovimento.get(i).setOperadorDevolucao(Usuario.getUsuario());
                    listLocadoraMovimento.get(i).setHoraDevolucaoString(DataHoje.hora());
                    if (listLocadoraMovimento.get(i).getValorTotal() > 0) {
                        if (listLocadoraMovimento.get(i).getMovimento() == null) {
                            valorTotal += listLocadoraMovimento.get(i).getValorTotal();
                            if (lote.getId() == -1) {
                                lote = new Lote(
                                        -1,
                                        new Rotina().get(),
                                        "R",
                                        DataHoje.data(),
                                        listLocadoraMovimento.get(i).getLocadoraLote().getPessoa(),
                                        servicoLocadora.getPlano5(),
                                        false,
                                        "",
                                        0,
                                        locadoraStatus.getFilial(),
                                        departamento,
                                        null,
                                        "",
                                        fTipoDocumento,
                                        (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1),
                                        (FStatus) dao.find(new FStatus(), 1),
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
                                if (!dao.save(lote)) {
                                    dao.rollback();
                                    GenericaMensagem.warn("Erro", "Ao salvar lote!");
                                    return;
                                }
                            }
                            String vencimento = listLocadoraMovimento.get(i).getLocadoraLote().getDataLocacaoString();
                            String referencia;
                            Pessoa pessoaTitular = listLocadoraMovimento.get(i).getLocadoraLote().getPessoa();
                            try {
                                String mes = vencimento.substring(3, 5);
                                String ano = vencimento.substring(6, 10);
                                referencia = mes + "/" + ano;
                                Movimento movimento = new Movimento(
                                        -1,
                                        lote,
                                        servicoLocadora.getPlano5(),
                                        pessoaTitular,
                                        servicoLocadora,
                                        null,
                                        (TipoServico) dao.find(new TipoServico(), 1),
                                        null,
                                        listLocadoraMovimento.get(i).getValorTotal(),
                                        referencia,
                                        DataHoje.data(),
                                        1,
                                        true,
                                        "E",
                                        false,
                                        pessoaTitular, // TITULAR / RESPONSÁVEL
                                        pessoaTitular, // BENEFICIÁRIO
                                        "",
                                        null,
                                        vencimento,
                                        0,
                                        0,
                                        0,
                                        0,
                                        0,
                                        0,
                                        0,
                                        fTipoDocumento,
                                        0, new MatriculaSocios()
                                );
                                if (!dao.save(movimento)) {
                                    dao.rollback();
                                    GenericaMensagem.warn("Erro", "Ao salvar movimento!");
                                    return;
                                }
                                listLocadoraMovimento.get(i).setMovimento(movimento);
                                if (!dao.update(listLocadoraMovimento.get(i))) {
                                    dao.rollback();
                                    GenericaMensagem.warn("Erro", "Ao salvar locadora movimento!");
                                    return;
                                }
                            } catch (Exception e) {
                                dao.rollback();
                                GenericaMensagem.warn("Erro", e.getMessage());
                                return;
                            }
                        }
                    } else if (!dao.update(listLocadoraMovimento.get(i))) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "Ao salvar locadora movimento!");
                        return;
                    }
                }
            }
        }
        dao.commit();
        if (lote.getId() != -1) {
            lote.setValor(valorTotal);
            dao.update(lote, true);
        }
        GenericaSessao.remove("menuLocadoraBean");
        GenericaMensagem.info("Sucesso", "Devolução concluída!");
    }

    public void rollBack() {
        if (desfazerDevolucao == null) {
            return;
        }
        if (desfazerDevolucao.getMovimento() != null) {
            if (desfazerDevolucao.getMovimento().getBaixa() != null) {
                GenericaMensagem.warn("Validação", "Movimento já baixado, faça primeiramente o estorno em Movimentos a Receber!");
                return;
            }
        }
        Dao dao = new Dao();
        dao.openTransaction();
        desfazerDevolucao.setOperadorDevolucao(null);
        desfazerDevolucao.setDtDevolucao(null);
        Movimento m = desfazerDevolucao.getMovimento();
        desfazerDevolucao.setMovimento(null);
        if (!dao.update(desfazerDevolucao)) {
            GenericaMensagem.warn("Erro", "Ao realizar cancelamento de devolução!");
            dao.rollback();
            desfazerDevolucao = new LocadoraMovimento();
            return;
        }
        if (m != null) {
            if (!dao.delete(m)) {
                GenericaMensagem.warn("Erro", "Ao desfazer movimento!");
                dao.rollback();
                desfazerDevolucao = new LocadoraMovimento();
                return;
            }
            if (!dao.delete(m.getLote())) {
                GenericaMensagem.warn("Erro", "Ao desfazer lote!");
                dao.rollback();
                desfazerDevolucao = new LocadoraMovimento();
                return;
            }
        }
        dao.commit();
        NovoLog novoLog = new NovoLog();
        novoLog.update("", "Titulo com devolução cancelada -> Locadora Lote (" + desfazerDevolucao.getLocadoraLote().getId() + " - Titulo: " + desfazerDevolucao.getTitulo().getDescricao() + " - Locatário: (" + desfazerDevolucao.getLocadoraLote().getPessoa().getId() + ") " + desfazerDevolucao.getLocadoraLote().getPessoa().getNome());
        desfazerDevolucao = new LocadoraMovimento();
        GenericaMensagem.info("Sucesso", "Devolução cancelada");
        loadLocadoraMovimento();
    }

    /**
     * 1 - Pesquisa locatário pelo código; 2 - Pesquisa titulo pelo código; 3 -
     * Não pesquisa titulos especificados ; 4 - Pend~encias; 5 - Remove titulo
     * pesquisado; 6 - Atrasados; 7 - Histórico;
     *
     * @param tcase
     */
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                if (!codigoLocatario.isEmpty()) {
                    Pessoa p = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(codigoLocatario));
                    locatario = p.getFisica();
                    codigoLocatario = "";
                }
                break;
            case 2:
                if (!codigoBarras.isEmpty()) {
                    TituloDao tituloDao = new TituloDao();
                    String inTitulos = inTitulos();
                    if (inTitulos != null) {
                        tituloDao.setNot_in(inTitulos);
                    }
                    MacFilial mf = MacFilial.getAcessoFilial();
                    Titulo t;
                    if (mf != null && mf.getId() != -1) {
                        t = tituloDao.findBarras(mf.getFilial().getId(), codigoBarras);
                    } else {
                        t = tituloDao.findBarras(null, codigoBarras);
                    }
                    if (t != null) {
                        titulo = t;
                    }
                    codigoBarras = "";
                    GenericaMensagem.warn("Validação", "Titulo não existe / indisponível / locado!");
                }
                break;
            case 3:
                GenericaSessao.put("habilitaPesquisaFilial", true);
                String inTitulos = inTitulos();
                if (inTitulos != null) {
                    GenericaSessao.put("titulosNotIn", inTitulos);
                }
                break;
            case 4:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaPendentesPorPessoa(locatario.getPessoa().getId());
                break;
            case 5:
                titulo = new Titulo();
                break;
            case 6:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaAtrasadosPorPessoa(locatario.getPessoa().getId());
                break;
            case 7:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa(locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
        }
    }

    public void delete() {

    }

    public void confirm() {

    }

    public String inTitulos() {
        String in = null;
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (i == 0) {
                in = "" + listLocadoraMovimento.get(i).getTitulo().getBarras();
            } else {
                in = ", " + listLocadoraMovimento.get(i).getTitulo().getBarras();
            }
        }
        List<LocadoraMovimento> lms = new LocadoraMovimentoDao().pesquisaPendentesPorPessoa(locatario.getPessoa().getId());
        for (int i = 0; i < lms.size(); i++) {
            if (i == 0) {
                in = "" + lms.get(i).getTitulo().getBarras();
            } else {
                in = ", " + lms.get(i).getTitulo().getBarras();
            }
        }
        return in;
    }

    public void remove(LocadoraMovimento lm) {
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getTitulo().getId().equals(lm.getTitulo().getId())) {
                if (listLocadoraMovimento.get(i).getId() == null) {
                    listLocadoraMovimento.remove(i);
                } else if (listLocadoraMovimento.get(i).getDtDevolucao() != null) {
                    if (!new Dao().delete(listLocadoraMovimento.get(i), true)) {
                        GenericaMensagem.warn("Erro", "Ao remover titulo!");
                        return;
                    }
                } else {
                    GenericaMensagem.warn("Erro", "Locações devolvidas não podem ser removidos!");
                    return;
                }
                GenericaMensagem.info("Validação", "Titulo removido!");
                return;
            }
        }
    }

    public LocadoraLote getLocadoraLote() {
        if (locatario.getPessoa().getId() != -1) {
            if (locadoraLote.getId() == null) {
                LocadoraLote ll = new LocadoraLoteDao().findByPessoa(locatario.getPessoa().getId(), true);
                if (ll != null) {
                    locadoraLote = ll;
                }
            }
        }
        return locadoraLote;
    }

    public void setLocadoraLote(LocadoraLote locadoraLote) {
        this.locadoraLote = locadoraLote;
    }

    public LocadoraMovimento getLocadoraMovimento() {
        return locadoraMovimento;
    }

    public void setLocadoraMovimento(LocadoraMovimento locadoraMovimento) {
        this.locadoraMovimento = locadoraMovimento;
    }

    public List<LocadoraMovimento> getListLocadoraMovimento() {
        return listLocadoraMovimento;
    }

    public void setListLocadoraMovimento(List<LocadoraMovimento> listLocadoraMovimento) {
        this.listLocadoraMovimento = listLocadoraMovimento;
    }

    public List<LocadoraLote> getListLocadoraLote() {
        return listLocadoraLote;
    }

    public void setListLocadoraLote(List<LocadoraLote> listLocadoraLote) {
        this.listLocadoraLote = listLocadoraLote;
    }

    public Titulo getTitulo() {
        if (GenericaSessao.exists("tituloPesquisa")) {
            titulo = (Titulo) GenericaSessao.getObject("tituloPesquisa", true);
        }
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public String getCodigoLocatario() {
        return codigoLocatario;
    }

    public void setCodigoLocatario(String codigoLocatario) {
        this.codigoLocatario = codigoLocatario;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Fisica getLocatario() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            Fisica f = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true));
            locatario.getPessoa().getSocios();
            locatario = f;
            loadLocadoraAutorizados();
            pessoaComplemento = locatario.getPessoa().getPessoaComplemento();
            if (pessoaComplemento.getObsAviso() != null && pessoaComplemento.getObsAviso().isEmpty()) {
                pessoaComplemento = null;
            }
            if (!new LocadoraLoteDao().exists(locatario.getId())) {
                GenericaMensagem.warn("Mensagem sistema", "Fazer Atualização Cadastral!");
            }
            loadLocadoraMovimento();
        }
        return locatario;
    }

    public void setLocatario(Fisica locatario) {
        this.locatario = locatario;
    }

    public void valid(Pessoa p) {

    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public Integer getIdLocadoraAutorizado() {
        return idLocadoraAutorizado;
    }

    public void setIdLocadoraAutorizado(Integer idLocadoraAutorizado) {
        this.idLocadoraAutorizado = idLocadoraAutorizado;
    }

    public List<SelectItem> getListLocadoraAutorizados() {
        return listLocadoraAutorizados;
    }

    public void setListLocadoraAutorizados(List<SelectItem> listLocadoraAutorizados) {
        this.listLocadoraAutorizados = listLocadoraAutorizados;
    }

    public LocadoraStatus getLocadoraStatus() {
        return locadoraStatus;
    }

    public void setLocadoraStatus(LocadoraStatus locadoraStatus) {
        this.locadoraStatus = locadoraStatus;
    }

    public List<LocadoraMovimento> getListLocadoraHistorico() {
        return listLocadoraHistorico;
    }

    public void setListLocadoraHistorico(List<LocadoraMovimento> listLocadoraHistorico) {
        this.listLocadoraHistorico = listLocadoraHistorico;
    }

    public String getDataDevolucaoString() {
        return dataDevolucaoString;
    }

    public void setDataDevolucaoString(String dataDevolucaoString) {
        this.dataDevolucaoString = dataDevolucaoString;
    }

    public String getStatus() {
        if (GenericaSessao.exists("locadora_status")) {
            status = GenericaSessao.getString("locadora_status", true);
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDevolver() {
        Boolean devolver = false;
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getSelected()) {
                devolver = true;
                break;
            }
        }
        return devolver;
    }

    public Boolean getReceber() {
        Boolean receber = false;
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getMovimento() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                receber = true;
                break;
            }
        }
        return receber;
    }

    public Double getValorTotalMultaDiaria() {
        Double total = new Double(0);
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getSelected()) {
                Integer dias = DataHoje.calculoDosDiasInt(listLocadoraMovimento.get(i).getDtDevolucaoPrevisao(), new Date());
                if (dias > 0) {
                    total += new FunctionsDao().multaDiariaLocadora(listLocadoraMovimento.get(i).getLocadoraLote().getFilial().getId(), listLocadoraMovimento.get(i).getLocadoraLote().getDtLocacao()) * dias;
                }
            }
        }
        return total;
    }

    public String getValorTotalMultaDiariaString() {
        try {
            return Moeda.converteR$Double(getValorTotalMultaDiaria());
        } catch (Exception e) {
            return "0,00";
        }
    }

    public Double getValorTotalReceber() {
        Double total = new Double(0);
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getDtDevolucao() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                total += listLocadoraMovimento.get(i).getMovimento().getValor();
            }
        }
        return total;
    }

    public String getValorTotalReceberString() {
        try {
            return Moeda.converteR$Double(getValorTotalReceber());
        } catch (Exception e) {
            return "0,00";
        }
    }

    public Integer getQuantidadeDevolucoes() {
        Integer qtde = 0;
        try {
            for (int i = 0; i < listLocadoraMovimento.size(); i++) {
                if (listLocadoraMovimento.get(i).getSelected()) {
                    qtde++;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return qtde;
    }

    public Integer getQuantidadeParaDevolver() {
        Integer qtde = 0;
        try {
            for (int i = 0; i < listLocadoraMovimento.size(); i++) {
                if (listLocadoraMovimento.get(i).getDtDevolucao() == null) {
                    qtde++;
                }
            }
            qtde = qtde - getQuantidadeDevolucoes();
        } catch (Exception e) {
            return 0;
        }
        return qtde;
    }

    public List<Movimento> getListMovimentoPendente() {
        List<Movimento> listMovimento = new ArrayList<>();
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getDtDevolucao() != null && listLocadoraMovimento.get(i).getMovimento() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                Movimento m = new Movimento();
                m = listLocadoraMovimento.get(i).getMovimento();
                m.setValorBaixa(listLocadoraMovimento.get(i).getMovimento().getValor());
                listMovimento.add(m);
            }
        }
        return listMovimento;
    }

    public LocadoraMovimento getDesfazerDevolucao() {
        return desfazerDevolucao;
    }

    public void setDesfazerDevolucao(LocadoraMovimento desfazerDevolucao) {
        this.desfazerDevolucao = desfazerDevolucao;
    }
}
