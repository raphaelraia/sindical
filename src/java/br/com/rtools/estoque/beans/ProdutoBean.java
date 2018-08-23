package br.com.rtools.estoque.beans;

import br.com.rtools.estoque.Estoque;
import br.com.rtools.estoque.EstoqueTipo;
import br.com.rtools.estoque.Produto;
import br.com.rtools.estoque.ProdutoGrupo;
import br.com.rtools.estoque.ProdutoSubGrupo;
import br.com.rtools.estoque.ProdutoUnidade;
import br.com.rtools.estoque.dao.EstoqueDao;
import br.com.rtools.estoque.dao.ProdutoDao;
import br.com.rtools.financeiro.IndiceMoeda;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.sistema.Cor;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ProdutoBean implements Serializable {

    private Produto produto;
    private String descricaoPesquisa;
    private Estoque estoque;
    private ProdutoGrupo produtoGrupo;
    private ProdutoSubGrupo produtoSubGrupo;
    private ProdutoUnidade produtoUnidade;
    private Cor cor;
    private String comoPesquisa;
    private Integer filial_id;
    private List<SelectItem> listFiliais;
    private Integer idFilial;
    private List<SelectItem> listEstoqueTipos;
    private Integer idEstoqueTipo;
    private List<SelectItem> listEstoqueTiposPesquisa;
    private Integer idEstoqueTipoPesquisa;
    private List<SelectItem> listUnidades;
    private Integer idUnidade;
    private List<SelectItem> listGrupos;
    private Integer idGrupo;
    private List<SelectItem> listGruposPesquisa;
    private Integer idGrupoPesquisa;
    private List<SelectItem> listSubgrupos;
    private Integer idSubgrupo;
    private List<SelectItem> listSubgruposPesquisa;
    private Integer idSubgrupoPesquisa;
    private List<SelectItem> listCores;
    private Integer idCor;
    private List<Produto> listaProdutos;
    private List<Estoque> listaEstoque;
    private List<SelectItem> listFiliaisPesquisa;
    private String custoMedio;
    private String valor;
    private Boolean liberaAcessaFilial;
    private String type;

    private List<SelectItem> listIndiceMoeda;
    private Integer idIndiceMoeda;
    private IndiceMoeda indiceMoeda;

    @PostConstruct
    public void init() {
        produto = new Produto();
        descricaoPesquisa = "";
        estoque = new Estoque();
        produtoGrupo = new ProdutoGrupo();
        produtoSubGrupo = new ProdutoSubGrupo();
        produtoUnidade = new ProdutoUnidade();
        cor = new Cor();
        comoPesquisa = "";

        listaProdutos = new ArrayList<>();
        listFiliais = new ArrayList();
        listEstoqueTipos = new ArrayList();
        listGruposPesquisa = new ArrayList();
        listSubgruposPesquisa = new ArrayList();
        listCores = new ArrayList();
        listGrupos = new ArrayList();
        listSubgrupos = new ArrayList();
        listaEstoque = new ArrayList<>();
        listUnidades = new ArrayList<>();
        listFiliaisPesquisa = new ArrayList<>();
        custoMedio = "0";
        valor = "0";
        liberaAcessaFilial = false;
        filial_id = 0;

        idIndiceMoeda = null;
        listIndiceMoeda = new ArrayList();
        indiceMoeda = new IndiceMoeda();

        loadListIndiceMoeda();
        loadListUnidades();
        loadListCores();
        loadListFiliais();
        loadListEstoqueTipos();
        loadListEstoqueTiposPesquisa();
        loadListGrupos();
        loadListGruposPesquisa();
        loadListSubgrupos();
        loadListSubgruposPesquisa();
        loadLiberaAcessaFilial();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("produtoBean");
    }

    public void clear() {
        GenericaSessao.remove("produtoBean");
    }

    public void novoIndiceMoeda() {
        indiceMoeda = new IndiceMoeda();
    }

    public void editarIndiceMoeda() {
        indiceMoeda = (IndiceMoeda) new Dao().find(new IndiceMoeda(), idIndiceMoeda);
    }

    public void excluirIndiceMoeda() {
        Dao dao = new Dao();

        if (indiceMoeda.getId() != null) {
            dao.openTransaction();

            if (!dao.delete(indiceMoeda)) {
                GenericaMensagem.fatal("Atenção", "Não foi possível excluir Índice!");
                dao.rollback();
                return;
            }

            GenericaMensagem.info("Sucesso", "Índice Excluído!");

            dao.commit();
        }

        loadListIndiceMoeda();

        PF.update("form_produto");
    }

    public void saveIndiceMoeda() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (indiceMoeda.getId() == null) {

            if (!dao.save(indiceMoeda)) {
                GenericaMensagem.fatal("Atenção", "Não foi possível salvar Índice!");
                dao.rollback();
                return;
            }

            GenericaMensagem.info("Sucesso", "Índice Adicionado!");

        } else {

            if (!dao.update(indiceMoeda)) {
                GenericaMensagem.fatal("Atenção", "Não foi possível atualizar Índice!");
                dao.rollback();
                return;
            }

            GenericaMensagem.info("Sucesso", "Índice Atualizado!");

        }

        dao.commit();

        loadListIndiceMoeda();

        PF.update("form_produto");
    }

    public final void loadListIndiceMoeda() {
        listIndiceMoeda.clear();
        idIndiceMoeda = null;

        List<IndiceMoeda> list = new ProdutoDao().listaIndiceMoeda();

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idIndiceMoeda = list.get(i).getId();
            }
            listIndiceMoeda.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
        }
    }

    public void loadLiberaAcessaFilial() {
        if (new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public void save() {
        if (produto.getDescricao().isEmpty()) {
            Messages.warn("Informar a descrição do produto!");
            return;
        }
        if (listGrupos.isEmpty()) {
            Messages.warn("Cadastrar grupo de produtos!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        produto.setProdutoGrupo((ProdutoGrupo) dao.find(new ProdutoGrupo(), idGrupo));
        if (listSubgrupos.isEmpty()) {
            produto.setProdutoSubGrupo(null);
        } else {
            produto.setProdutoSubGrupo((ProdutoSubGrupo) dao.find(new ProdutoSubGrupo(), idSubgrupo));
        }
        if (listUnidades.isEmpty()) {
            Messages.warn("Cadastrar unidades!");
            return;
        }
        produto.setProdutoUnidade((ProdutoUnidade) dao.find(new ProdutoUnidade(), idUnidade));
        if (listCores.isEmpty()) {
            Messages.warn("Cadastrar cores!");
            return;
        }
        produto.setCor((Cor) dao.find(new Cor(), idCor));
        produto.setValor(Moeda.converteUS$(valor));

        produto.setIndiceMoeda((IndiceMoeda) dao.find(new IndiceMoeda(), idIndiceMoeda));

        if (produto.getValidadeGuiasMesVigente() || produto.getValidadeGuiasDias().equals("")) {
            produto.setValidadeGuiasDias(0);
        }

        if (produto.getId() == null) {
            if (dao.save(produto)) {
                dao.commit();
                NovoLog novoLog = new NovoLog();
                novoLog.save(produto.toString());
                Messages.info("Registro inserido com sucesso");
            } else {
                dao.rollback();
                Messages.warn("Erro ao inserir registro!");
            }
        } else {
            Produto beforeUpdate = (Produto) dao.find(produto);
            if (dao.update(produto)) {
                if (!produto.equals(beforeUpdate)) {
                    NovoLog novoLog = new NovoLog();
                    novoLog.update(beforeUpdate.toString(), produto.toString());
                }
                dao.commit();
                Messages.info("Registro atualizado com sucesso");
            } else {
                Messages.warn("Erro ao atualizar registro!");
                dao.rollback();
            }
        }
    }

    public String edit(Produto p) {
        Dao dao = new Dao();
        produto = (Produto) dao.rebind(p);
        idGrupo = produto.getProdutoGrupo().getId();
        idSubgrupo = produto.getProdutoSubGrupo().getId();
        idUnidade = produto.getProdutoUnidade().getId();
        idCor = produto.getCor().getId();
        idIndiceMoeda = produto.getIndiceMoeda().getId();
        valor = Moeda.converteR$Double(produto.getValor());
        listaEstoque.clear();
        GenericaSessao.put("linkClicado", true);
        if (GenericaSessao.exists("urlRetorno")) {
            double margem = (produto.getValor() * produto.getIndiceMoeda().getValor()) * produto.getMargem() / 100;

            produto.setValor((produto.getValor() * produto.getIndiceMoeda().getValor()) + margem);

            GenericaSessao.put("produtoPesquisa", produto);

            return GenericaSessao.getString("urlRetorno");
        } else {
            return "produto";
        }
    }

    public void delete() {
        if (produto.getId() != null) {
            Dao dao = new Dao();
            dao.openTransaction();
            for (Estoque listaEstoque1 : listaEstoque) {
                if (!dao.delete(dao.find(listaEstoque1))) {
                    dao.rollback();
                    Messages.warn("Erro ao excluir produtos do estoque!");
                    return;
                }
            }
            if (dao.delete(dao.find(produto))) {
                NovoLog novoLog = new NovoLog();
                novoLog.delete(produto.toString());
                dao.commit();
                clear();
                Messages.info("Registro excluído com sucesso");
            } else {
                dao.rollback();
                Messages.warn("Erro ao excluir registro!");
            }
        }
    }

    public void addProdutoEstoque() {
        if (listFiliais.isEmpty()) {
            GenericaMensagem.warn("Cadastrar filial!");
            return;
        }
        if (listEstoqueTipos.isEmpty()) {
            GenericaMensagem.warn("Cadastrar tipos de estoque!");
            return;
        }
        if (estoque.getControlaEstoque()) {
            if (estoque.getEstoque() < 1) {
                GenericaMensagem.warn("Informar quantidade!");
                return;
            }
            if (estoque.getEstoqueMinimo() < 1) {
                GenericaMensagem.warn("Informar mínimo!");
                return;
            }
            if (estoque.getEstoqueMaximo() < 1) {
                GenericaMensagem.warn("Informar estoque máximo!");
                return;
            }
            if (estoque.getEstoqueMaximo() < estoque.getEstoqueMinimo()) {
                GenericaMensagem.warn("Estoque máximo deve ser maior que estoque mínimo!");
                return;
            }
        }
        for (int i = 0; i < listaEstoque.size(); i++) {
            if (idEstoqueTipo == 3) {
                if (listaEstoque.get(i).getEstoqueTipo().getId() == 4) {
                    GenericaMensagem.warn("Não é possível cadastrar brinde para produtos com estoque de serviço para mesma filial!");
                    return;
                }
            }
            if (idEstoqueTipo == 4) {
                if (listaEstoque.get(i).getEstoqueTipo().getId() == 3) {
                    GenericaMensagem.warn("Não é possível cadastrar serviço para produtos com estoque de brindes para mesma filial!");
                    return;
                }
            }
        }
        estoque.setCustoMedio(Moeda.converteUS$(custoMedio));
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (estoque.getId() == null) {
            estoque.setEstoqueTipo((EstoqueTipo) di.find(new EstoqueTipo(), idEstoqueTipo));
            estoque.setFilial((Filial) di.find(new Filial(), idFilial));
            estoque.setProduto(produto);
            ProdutoDao produtoDao = new ProdutoDao();
            if (produtoDao.existeProdutoEstoqueFilialTipo(estoque)) {
                GenericaMensagem.warn("Produto e tipo já cadastrado para esta filial!!");
                return;
            }
            di.openTransaction();
            if (di.save(estoque)) {
                novoLog.save(
                        "ID: " + estoque.getId()
                        + " - Filial: (" + estoque.getFilial().getId() + ") " + estoque.getFilial().getFilial().getPessoa().getNome()
                        + " - Produto: (" + estoque.getProduto().getId() + ") " + estoque.getProduto().getDescricao()
                        + " - Estoque Tipo: (" + estoque.getEstoqueTipo().getId() + ") " + estoque.getEstoqueTipo().getDescricao()
                        + " - Estoque: " + estoque.getEstoque()
                        + " - Custo Médio: " + estoque.getCustoMedio()
                );
                di.commit();
                GenericaMensagem.info("Registro inserido com sucesso");
            } else {
                di.rollback();
                GenericaMensagem.warn("Erro ao inserir registro!");
            }
        } else {
            Estoque e = (Estoque) di.find(estoque);
            String beforeUpdate
                    = "ID: " + e.getId()
                    + " - Filial: (" + e.getFilial().getId() + ") " + e.getFilial().getFilial().getPessoa().getNome()
                    + " - Produto: (" + e.getProduto().getId() + ") " + e.getProduto().getDescricao()
                    + " - Estoque Tipo: (" + e.getEstoqueTipo().getId() + ") " + e.getEstoqueTipo().getDescricao()
                    + " - Estoque: " + e.getEstoque()
                    + " - Custo Médio: " + e.getCustoMedio();
            di.openTransaction();
            if (di.update(estoque)) {
                novoLog.update(beforeUpdate,
                        "ID: " + estoque.getId()
                        + " - Filial: (" + estoque.getFilial().getId() + ") " + estoque.getFilial().getFilial().getPessoa().getNome()
                        + " - Produto: (" + estoque.getProduto().getId() + ") " + estoque.getProduto().getDescricao()
                        + " - Estoque Tipo: (" + estoque.getEstoqueTipo().getId() + ") " + estoque.getEstoqueTipo().getDescricao()
                        + " - Estoque: " + estoque.getEstoque()
                        + " - Custo Médio: " + estoque.getCustoMedio()
                );
                di.commit();
                GenericaMensagem.info("Registro atualizado com sucesso");
            } else {
                di.rollback();
                GenericaMensagem.warn("Erro ao atualizado registro!");
            }
        }
        estoque = new Estoque();
        listaEstoque.clear();
        custoMedio = "0,00";
    }

    public void editEstoque(Estoque e) {
        Dao dao = new Dao();
        estoque = (Estoque) dao.rebind(e);
        idFilial = estoque.getFilial().getId();
        idEstoqueTipo = estoque.getEstoqueTipo().getId();
        custoMedio = Moeda.converteR$Double(estoque.getCustoMedio());
    }

    public void deleteEstoque(Estoque e) {
        if (e.getId() != null) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            if (dao.delete((Estoque) dao.find(e), true)) {
                novoLog.delete(
                        "ID: " + e.getId()
                        + " - Filial: (" + e.getFilial().getId() + ") " + e.getFilial().getFilial().getPessoa().getNome()
                        + " - Produto: (" + e.getProduto().getId() + ") " + e.getProduto().getDescricao()
                        + " - Estoque Tipo: (" + e.getEstoqueTipo().getId() + ") " + e.getEstoqueTipo().getDescricao()
                        + " - Estoque: " + e.getEstoque()
                        + " - Custo Médio: " + e.getCustoMedio()
                );
                GenericaMensagem.info("Registro removido com sucesso");
            } else {
                GenericaMensagem.warn("Erro ao remover registro!");
            }
        }
        listaEstoque.clear();
    }

    public void saveProdutoGrupo() {
        if (produtoGrupo.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Informar descrição!");
            return;
        }
        ProdutoDao produtoDao = new ProdutoDao();
        if (produtoDao.existeCor(produtoGrupo.getDescricao())) {
            GenericaMensagem.warn("Produto Grupo já cadastrado!");
            return;
        }
        saveSubItens(produtoGrupo);
        produtoGrupo = new ProdutoGrupo();
        loadListGrupos();
    }

    public void saveProdutoSubGrupo() {
        if (produtoSubGrupo.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Informar descrição!");
            return;
        }
        ProdutoDao produtoDao = new ProdutoDao();
        Dao dao = new Dao();
        produtoSubGrupo.setProdutoGrupo((ProdutoGrupo) dao.find(new ProdutoGrupo(), idGrupo));
        if (produtoDao.existeProdutoSubGrupo(produtoSubGrupo.getDescricao())) {
            GenericaMensagem.warn("Produto SubGrupo já cadastrado!");
            return;
        }
        if (dao.save(produtoSubGrupo, true)) {
            GenericaMensagem.info("Registro inserido com sucesso");
        } else {
            GenericaMensagem.warn("Erro ao inserir registro!");
        }
        produtoSubGrupo = new ProdutoSubGrupo();
        loadListSubgrupos();
    }

    public void saveProdutoUnidade() {
        if (produtoUnidade.getDescricao().isEmpty()) {
            GenericaMensagem.info("Informar descrição!");
            return;
        }
        ProdutoDao produtoDao = new ProdutoDao();
        if (produtoDao.existeProdutoUnidade(produtoUnidade.getDescricao())) {
            GenericaMensagem.warn("Unidade já cadastrada!");
            return;
        }
        saveSubItens(produtoUnidade);
        produtoUnidade = new ProdutoUnidade();
        loadListUnidades();
    }

    public void saveCor() {
        if (cor.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Informar descrição!");
            return;
        }
        ProdutoDao produtoDao = new ProdutoDao();
        if (produtoDao.existeCor(cor.getDescricao())) {
            GenericaMensagem.warn("Cor já cadastrada!");
            return;
        }
        saveSubItens(cor);
        cor = new Cor();
        loadListCores();
    }

    public void saveSubItens(Object object) {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (dao.save(object, true)) {
            novoLog.save("Adicionado via cadastro de produto: " + object.toString());
            GenericaMensagem.info("Registro inserido com sucesso");
        } else {
            GenericaMensagem.warn("Erro ao inserir registro!");
        }
    }

    public ProdutoGrupo getProdutoGrupo() {
        return produtoGrupo;
    }

    public void setProdutoGrupo(ProdutoGrupo produtoGrupo) {
        this.produtoGrupo = produtoGrupo;
    }

    public ProdutoSubGrupo getProdutoSubGrupo() {
        return produtoSubGrupo;
    }

    public void setProdutoSubGrupo(ProdutoSubGrupo produtoSubGrupo) {
        this.produtoSubGrupo = produtoSubGrupo;
    }

    public ProdutoUnidade getProdutoUnidade() {
        return produtoUnidade;
    }

    public void setProdutoUnidade(ProdutoUnidade produtoUnidade) {
        this.produtoUnidade = produtoUnidade;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public void acaoPesquisaInicial() {
        listaProdutos = new ArrayList();
        comoPesquisa = "Inicial";
        load();
    }

    public void acaoPesquisaParcial() {
        listaProdutos = new ArrayList();
        comoPesquisa = "Parcial";
        load();
    }

    public void load() {
        if (listaProdutos.isEmpty()) {
            if (!getListFiliaisPesquisa().isEmpty()) {
                ProdutoDao produtoDao = new ProdutoDao();
                Filial filial = (Filial) new Dao().find(new Filial(), Integer.parseInt(getListFiliaisPesquisa().get(filial_id).getDescription()));
                if (filial.getId() != -1) {
                    List<Produto> listap;
                    listap = (List<Produto>) produtoDao.pesquisaProduto(descricaoPesquisa, 0, comoPesquisa, idGrupoPesquisa, idSubgrupoPesquisa);
                    for (Produto prod : listap) {
                        if (getType() != null && getType().equals("tproduto")) {
                            listaProdutos.add(prod);
                        } else {
                            Estoque es;
                            if (idEstoqueTipoPesquisa == null) {
                                es = new EstoqueDao().find(prod.getId(), filial.getId());
                            } else {
                                es = new EstoqueDao().find(prod.getId(), filial.getId(), idEstoqueTipoPesquisa);
                            }
                            if (es != null) {
                                listaProdutos.add(prod);
                            }
                        }
                    }
                } else {
                    listaProdutos = (List<Produto>) produtoDao.pesquisaProduto(descricaoPesquisa, 0, comoPesquisa, idGrupoPesquisa, idSubgrupoPesquisa);
                }
            }
        }
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public List<Estoque> getListaEstoque() {
        if (listaEstoque.isEmpty()) {
            if (produto.getId() != null) {
                ProdutoDao produtoDao = new ProdutoDao();
                listaEstoque = (List<Estoque>) produtoDao.listaEstoquePorProduto(produto);
            }
        }
        return listaEstoque;
    }

    public void setListaEstoque(List<Estoque> listaEstoque) {
        this.listaEstoque = listaEstoque;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    public String getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(String custoMedio) {
        this.custoMedio = custoMedio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public List<SelectItem> getListFiliaisPesquisa() {
        if (listFiliaisPesquisa.isEmpty()) {
            Filial f = MacFilial.getAcessoFilial().getFilial();
            if (f.getId() != -1) {
                if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                    liberaAcessaFilial = true;
                    // NOME DA TABELA ONDE CONTÉM AS FILIAIS
                    List<Filial> list = new FilialDao().findByTabela("est_estoque");
                    // ID DA FILIAL
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 0) {
                                filial_id = i;
                            }
                            if (Objects.equals(f.getId(), list.get(i).getId())) {
                                filial_id = i;
                            }
                            listFiliaisPesquisa.add(new SelectItem(i, list.get(i).getFilial().getPessoa().getNome(), "" + list.get(i).getId()));
                        }
                    } else {
                        filial_id = 0;
                        listFiliaisPesquisa.add(new SelectItem(0, f.getFilial().getPessoa().getNome(), "" + f.getId()));
                    }
                } else {
                    filial_id = 0;
                    listFiliaisPesquisa.add(new SelectItem(0, f.getFilial().getPessoa().getNome(), "" + f.getId()));
                }
            }
        }
        return listFiliaisPesquisa;
    }

    public void setListFiliaisPesquisa(List<SelectItem> listFiliaisPesquisa) {
        this.listFiliaisPesquisa = listFiliaisPesquisa;
    }

    public Integer getFilial_id() {
        return filial_id;
    }

    public void setFilial_id(Integer filial_id) {
        this.filial_id = filial_id;
    }

    /**
     *
     * @param tcase (1 => listaProdutos.clear(); )
     */
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                listaProdutos.clear();
                break;
        }
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getType() {
        if (GenericaSessao.exists("type")) {
            type = GenericaSessao.getString("type", true);
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SelectItem> getListIndiceMoeda() {
        return listIndiceMoeda;
    }

    public void setListIndiceMoeda(List<SelectItem> listIndiceMoeda) {
        this.listIndiceMoeda = listIndiceMoeda;
    }

    public IndiceMoeda getIndiceMoeda() {
        return indiceMoeda;
    }

    public void setIndiceMoeda(IndiceMoeda indiceMoeda) {
        this.indiceMoeda = indiceMoeda;
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public void loadListGrupos() {
        listGrupos = new ArrayList();
        Dao dao = new Dao();
        List<ProdutoGrupo> list = (List<ProdutoGrupo>) dao.list(new ProdutoGrupo(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupo = list.get(i).getId();
            }
            listGrupos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListGruposPesquisa() {
        listGruposPesquisa = new ArrayList();
        idGrupoPesquisa = null;
        Dao dao = new Dao();
        List<ProdutoGrupo> list = (List<ProdutoGrupo>) dao.list(new ProdutoGrupo(), true);
        listGruposPesquisa.add(new SelectItem(null, "-- TODOS --"));
        for (int i = 0; i < list.size(); i++) {
            listGruposPesquisa.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListSubgrupos() {
        listSubgrupos = new ArrayList();
        Dao dao = new Dao();
        List<ProdutoSubGrupo> list = (List<ProdutoSubGrupo>) dao.listQuery(new ProdutoSubGrupo(), "findGrupo", new Object[]{idGrupo});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSubgrupo = list.get(i).getId();
            }
            listSubgrupos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListSubgruposPesquisa() {
        listSubgruposPesquisa = new ArrayList();
        idSubgrupoPesquisa = null;
        listSubgruposPesquisa.add(new SelectItem(null, "-- TODOS --"));
        Dao dao = new Dao();
        List<ProdutoSubGrupo> list = (List<ProdutoSubGrupo>) dao.listQuery(new ProdutoSubGrupo(), "findGrupo", new Object[]{idGrupoPesquisa});
        for (int i = 0; i < list.size(); i++) {
            listSubgruposPesquisa.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListFiliais() {
        listFiliais = new ArrayList();
        Dao dao = new Dao();
        List<Filial> list = (List<Filial>) dao.list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFiliais.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome(), "" + list.get(i).getId()));
        }
    }

    public void loadListEstoqueTipos() {
        listEstoqueTipos = new ArrayList();
        Dao dao = new Dao();
        List<EstoqueTipo> list = (List<EstoqueTipo>) dao.list(new EstoqueTipo(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idEstoqueTipo = list.get(i).getId();
            }
            listEstoqueTipos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListEstoqueTiposPesquisa() {
        listEstoqueTiposPesquisa = new ArrayList();
        idEstoqueTipoPesquisa = null;
        Dao dao = new Dao();
        List<EstoqueTipo> list = (List<EstoqueTipo>) dao.list(new EstoqueTipo(), true);
        listEstoqueTiposPesquisa.add(new SelectItem(null, "-- TODOS --"));
        for (int i = 0; i < list.size(); i++) {
            listEstoqueTiposPesquisa.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListUnidades() {
        listUnidades = new ArrayList();
        Dao dao = new Dao();
        List<ProdutoUnidade> list = (List<ProdutoUnidade>) dao.list(new ProdutoUnidade(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idUnidade = list.get(i).getId();
            }
            listUnidades.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public void loadListCores() {
        listCores = new ArrayList();
        Dao dao = new Dao();
        List<Cor> list = (List<Cor>) dao.list(new Cor(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCor = list.get(i).getId();
            }
            listCores.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public List<SelectItem> getListEstoqueTipos() {
        return listEstoqueTipos;
    }

    public void setListEstoqueTipos(List<SelectItem> listEstoqueTipos) {
        this.listEstoqueTipos = listEstoqueTipos;
    }

    public Integer getIdEstoqueTipo() {
        return idEstoqueTipo;
    }

    public void setIdEstoqueTipo(Integer idEstoqueTipo) {
        this.idEstoqueTipo = idEstoqueTipo;
    }

    public List<SelectItem> getListUnidades() {
        return listUnidades;
    }

    public void setListUnidades(List<SelectItem> listUnidades) {
        this.listUnidades = listUnidades;
    }

    public Integer getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Integer idUnidade) {
        this.idUnidade = idUnidade;
    }

    public List<SelectItem> getListGrupos() {
        return listGrupos;
    }

    public void setListGrupos(List<SelectItem> listGrupos) {
        this.listGrupos = listGrupos;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<SelectItem> getListGruposPesquisa() {
        return listGruposPesquisa;
    }

    public void setListGruposPesquisa(List<SelectItem> listGruposPesquisa) {
        this.listGruposPesquisa = listGruposPesquisa;
    }

    public Integer getIdGrupoPesquisa() {
        return idGrupoPesquisa;
    }

    public void setIdGrupoPesquisa(Integer idGrupoPesquisa) {
        this.idGrupoPesquisa = idGrupoPesquisa;
    }

    public List<SelectItem> getListSubgrupos() {
        return listSubgrupos;
    }

    public void setListSubgrupos(List<SelectItem> listSubgrupos) {
        this.listSubgrupos = listSubgrupos;
    }

    public Integer getIdSubgrupo() {
        return idSubgrupo;
    }

    public void setIdSubgrupo(Integer idSubgrupo) {
        this.idSubgrupo = idSubgrupo;
    }

    public List<SelectItem> getListSubgruposPesquisa() {
        return listSubgruposPesquisa;
    }

    public void setListSubgruposPesquisa(List<SelectItem> listSubgruposPesquisa) {
        this.listSubgruposPesquisa = listSubgruposPesquisa;
    }

    public Integer getIdSubgrupoPesquisa() {
        return idSubgrupoPesquisa;
    }

    public void setIdSubgrupoPesquisa(Integer idSubgrupoPesquisa) {
        this.idSubgrupoPesquisa = idSubgrupoPesquisa;
    }

    public List<SelectItem> getListCores() {
        return listCores;
    }

    public void setListCores(List<SelectItem> listCores) {
        this.listCores = listCores;
    }

    public Integer getIdCor() {
        return idCor;
    }

    public void setIdCor(Integer idCor) {
        this.idCor = idCor;
    }

    public Integer getIdIndiceMoeda() {
        return idIndiceMoeda;
    }

    public void setIdIndiceMoeda(Integer idIndiceMoeda) {
        this.idIndiceMoeda = idIndiceMoeda;
    }

    public List<SelectItem> getListEstoqueTiposPesquisa() {
        return listEstoqueTiposPesquisa;
    }

    public void setListEstoqueTiposPesquisa(List<SelectItem> listEstoqueTiposPesquisa) {
        this.listEstoqueTiposPesquisa = listEstoqueTiposPesquisa;
    }

    public Integer getIdEstoqueTipoPesquisa() {
        return idEstoqueTipoPesquisa;
    }

    public void setIdEstoqueTipoPesquisa(Integer idEstoqueTipoPesquisa) {
        this.idEstoqueTipoPesquisa = idEstoqueTipoPesquisa;
    }

}
