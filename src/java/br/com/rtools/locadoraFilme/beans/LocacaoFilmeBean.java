package br.com.rtools.locadoraFilme.beans;

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
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.db.FunctionsDao;
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
public class LocacaoFilmeBean implements Serializable {

    private LocadoraLote locadoraLote;
    private LocadoraMovimento locadoraMovimento;
    private String codigoLocatario;
    private String codigoBarras;
    private Titulo titulo;
    private List<LocadoraMovimento> listLocadoraMovimento;
    private List<LocadoraMovimento> listLocadoraHistorico;
    private List<LocadoraLote> listLocadoraLote;
    private Fisica locatario;
    private List<SelectItem> listLocadoraAutorizados;
    private PessoaComplemento pessoaComplemento;
    private Integer idLocadoraAutorizado;
    private LocadoraStatus locadoraStatus;

    @PostConstruct
    public void init() {
        locadoraLote = new LocadoraLote();
        locadoraMovimento = new LocadoraMovimento();
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

    public void loadLocadoraMovimento() {
        new LocadoraMovimentoDao().pesquisaPendentesPorPessoa(locatario.getPessoa().getId());

    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("tituloPesquisa");
        GenericaSessao.remove("locacaoFilmeBean");
        GenericaSessao.remove("titulosNotIn");
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
        Dao dao = new Dao();
        locadoraLote.setPessoa((Pessoa) dao.find(locatario.getPessoa()));
        locadoraLote.setDtLocacao(new Date());
        if (idLocadoraAutorizado != null) {
            locadoraLote.setLocadoraAutorizados((LocadoraAutorizados) dao.find(new LocadoraAutorizados(), idLocadoraAutorizado));
        }
        dao.openTransaction();
        if (locadoraLote.getId() == null) {
            locadoraLote.setFilial(MacFilial.getAcessoFilial().getFilial());
            if (!dao.save(locadoraLote)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao realizar esta locação!");
                return;
            } else {

            }
        }
        Integer quantidade = 0;
        Integer quantidadeLancamentos = 0;
//        String valorx;
//        Servicos se = locadoraStatus.getTaxa().getServicoDiaria();
//        valorx = Moeda.converteR$Float(new FunctionsDao().valorServico(locatario.getPessoa().getId(), locadoraStatus.getTaxa().getServicoDiaria().getId(), DataHoje.dataHoje(), 0, locatario.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
//        String valor = Moeda.converteR$(valorx);
//        List<LocadoraMovimento> lms = new LocadoraMovimentoDao().findAllByPessoa(DataHoje.data(), locatario.getPessoa().getId());
//        Evt evt = new Evt();
//        Lote lote = new Lote();
//        if (!lms.isEmpty()) {
//            lote = new LoteDBToplink().pesquisaLotePorEvt(lms.get(0).getEvt().getId());
//            evt = lote.getEvt();
//        }
//        if (evt.getId() == -1) {
//            if (!dao.save(evt)) {
//                dao.rollback();
//                GenericaMensagem.warn("Erro", "Ao salvar Evt!");
//                return;
//            }
//        }
//        Socios s = locatario.getPessoa().getSocios();
//        if (lote.getId() == -1) {
//            lote = new Lote(
//                    -1,
//                    (Rotina) dao.find(new Rotina().get()),
//                    "R",
//                    DataHoje.data(),
//                    s.getMatriculaSocios().getTitular(),
//                    locadoraStatus.getTaxa().getServicoDiaria().getPlano5(),
//                    false,
//                    "",
//                    0,
//                    MacFilial.getAcessoFilial().getFilial(),
//                    null,
//                    null,
//                    "",
//                    (FTipoDocumento) dao.find(new FTipoDocumento(), 3),
//                    (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1),
//                    (FStatus) dao.find(new FStatus(), 1),
//                    null,
//                    false,
//                    0
//            );
//            if (!dao.save(lote)) {
//                dao.rollback();
//                GenericaMensagem.warn("Erro", "Ao salvar Evt!");
//                return;
//            }
//        }
        // Movimento movimento = new Movimento();
        // movimento.setIdInteger(null);
        DataHoje dataHoje = new DataHoje();
        if (locadoraLote.getId() != null) {
            for (int i = 0; i < listLocadoraMovimento.size(); i++) {
                listLocadoraMovimento.get(i).setDataDevolucaoPrevisaoString(dataHoje.incrementarDias(locadoraStatus.getDiasDevolucao(), DataHoje.data()));
                if (listLocadoraMovimento.get(i).getLocadoraLote() == null) {
                    listLocadoraMovimento.get(i).setLocadoraLote(locadoraLote);
                    quantidade++;
                } else {
                    quantidade++;
                }
                if (listLocadoraMovimento.get(i).getTitulo().isLancamento()) {
                    quantidadeLancamentos++;
                }
                if (listLocadoraMovimento.get(i).getId() == null) {
                    if (!dao.save(listLocadoraMovimento.get(i))) {
                        GenericaMensagem.warn("Erro", "Ao inserir locadora movimento!");
                        return;
                    }
                } else if (!dao.update(listLocadoraMovimento.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Ao atualizar locadora movimento!");
                    return;
                }
            }
        } else {

        }
        if (quantidade > locadoraStatus.getQtdeLocacao()) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "Não é possível locar mais de " + locadoraStatus.getQtdeLocacao() + "!");
            return;
        }
        if (quantidadeLancamentos > locadoraStatus.getQtdeLancamentos()) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "Não é possível locar mais de " + locadoraStatus.getQtdeLancamentos() + " lançamentos!");
            return;
        }
        GenericaMensagem.info("Sucesso", "Locação concluída!");
        dao.commit();
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
                    } else {
                        GenericaMensagem.warn("Validação", "Titulo não existe / indisponível / locado!");
                    }
                    codigoBarras = "";
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
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("todos", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
            case 5:
                titulo = new Titulo();
                break;
            case 6:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("nao_devolvidos", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
            case 7:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("todos", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
            case 8:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("hoje", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
            case 9:
                listLocadoraHistorico.clear();
                listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("pendentes", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
                break;
        }
    }

    public void delete() {

    }

    public void confirm() {

    }

    public String inTitulos() {
        String in = "";
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
                in += "" + lms.get(i).getTitulo().getBarras();
            } else {
                in += ", " + lms.get(i).getTitulo().getBarras();
            }
        }
        return in;
    }

    public void add() {
        if (titulo.getId() == null) {
            GenericaMensagem.warn("Validação", "Pesquisar um titulo!");
            return;
        }
        if (new TituloDao().locadoraQuantidadeTituloDisponivel(MacFilial.getAcessoFilial().getFilial().getId(), titulo.getId()) <= 0) {
            GenericaMensagem.warn("Validação", "Não tem quantidade disponíveis para locação!");
            return;
        }
        locatario.getPessoa().getSocios();
        if (locadoraStatus.getLocacaoDependente()) {
            if (!locatario.getPessoa().getIsTitular()) {
                if (locatario.getIdade() < 18) {
                    GenericaMensagem.warn("Validação", "Locação somente para dependentes acima de 18 anos!");
                    return;
                }
            }
        } else if (!locatario.getPessoa().getIsTitular()) {
            GenericaMensagem.warn("Validação", "Não permitida a locação para dependentes, somente para pessoas autorizadas pelo titular!");
            return;
        }
        if (locatario.getIdade() > 0) {
            if (locatario.getIdade() < titulo.getIdadeMinima()) {
                GenericaMensagem.warn("Validação", "Esse filme só pode ser alugado para maiores de " + locatario.getIdade() + " 18 anos!");
                return;
            }
        }
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getTitulo().getId().equals(titulo.getId())) {
                GenericaMensagem.warn("Validação", "Titulo já adicionado!");
                return;
            }
        }
        List<LocadoraMovimento> listLM = new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("pendentes", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
        for (int i = 0; i < listLM.size(); i++) {
            if (listLM.get(i).getTitulo().getId().equals(titulo.getId())) {
                GenericaMensagem.warn("Validação", "Titulo já locado / pendente de devolução!");
                return;
            }
        }
        LocadoraMovimentoDao locadoraMovimentoDao = new LocadoraMovimentoDao();
        List<LocadoraMovimento> lms = locadoraMovimentoDao.pesquisaPendentesPorPessoa(locatario.getPessoa().getId());
        if (lms.size() > locadoraStatus.getQtdeLocacao()) {
            GenericaMensagem.warn("Validação", "Quantidade de locações excedidas, existem locações em aberto!");
            return;
        }
        LocadoraMovimento lm = new LocadoraMovimento();
        lm.setTitulo(titulo);
        lm.setDataDevolucaoPrevisaoString(new DataHoje().incrementarDias(locadoraStatus.getDiasDevolucao(), locadoraLote.getDataLocacaoString()));
        if (locadoraLote.getId() != null) {
            lm.setLocadoraLote(locadoraLote);
        }
        listLocadoraMovimento.add(lm);
        titulo = new Titulo();
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
            listLocadoraMovimento.clear();
            listLocadoraMovimento = new LocadoraMovimentoDao().findAllByPessoa(DataHoje.data(), locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId());
            listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaPendentesPorPessoa(locatario.getPessoa().getId());
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

    public Integer getPendentes() {
        return new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("pendentes", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId()).size();
    }

    public Integer getPendentesAtrasados() {
        return new LocadoraMovimentoDao().pesquisaHistoricoPorPessoa("nao_devolvidos", locatario.getPessoa().getId(), MacFilial.getAcessoFilial().getFilial().getId()).size();
    }
}
