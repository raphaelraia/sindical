package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.ValidadeCartao;
import br.com.rtools.associativo.dao.ValidadeCartaoDao;
import br.com.rtools.associativo.dao.CategoriaDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.ImpressaoParaSocios;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Reports;
import br.com.rtools.utilitarios.Sessions;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;

@ManagedBean
@SessionScoped
public class CartaoSocialBean implements Serializable {

    /**
     * NOVO *
     */
    private String descricao = "";
    private List<List> listaCarteirinha = new ArrayList();
    private List<List> filteredCarteirinha = new ArrayList();
    private List<List> listaSelecionado = new ArrayList();
    private List<List> listaSelecionadoMemoria = new ArrayList();
    private List<SelectItem> listFilial = new ArrayList();
    private List listaHistorico = new ArrayList();
    private String por = "";
    private String porLabel = "";
    private String indexOrdem = "0";
    private Integer page;
    private Integer idFilial = 0;
    private Boolean toggle = false;
    private Integer firstIndex = 0;
    private Integer lastIndex = 0;
    private Boolean disabled;
    private ConfiguracaoSocial configuracaoSocial;
    private String status;
    private String filter;
    private String query;
    private Boolean printed;
    private Integer idOperador;
    private List<SelectItem> listOperador;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private Boolean paginacao;
    private Integer resultadosPorPagina;
    private Boolean somenteAutorizados;
    private Boolean disabledImpressaoExterna;
    private Boolean liberaReimpressao;
    private String pdfCartao;
    private Boolean showModalSelecteds;

    public CartaoSocialBean() {
        clearUserFolder();
        showModalSelecteds = false;
        pdfCartao = "";
        Sessions.remove("FILE_NAME_GENERATED");
        configuracaoSocial = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
        disabled = false;
        if (configuracaoSocial.getControlaCartaoFilial()) {
            disabled = true;
        }
        status = "pendentes";
        filter = "";
        query = "";
        somenteAutorizados = false;
        listOperador = new ArrayList();
        loadOperador();
        getListFilial();
        Jasper.load();
        printed = false;
        typeDate = "";
        startDate = "";
        finishDate = "";
        paginacao = true;
        resultadosPorPagina = 10;
        disabledImpressaoExterna = false;
        liberaReimpressao = null;
        GenericaSessao.remove("status");
        loadList();
    }

    public void historicoCarteirinha() {
        if (listaSelecionado.size() > 0) {
            SocioCarteirinhaDao db = new SocioCarteirinhaDao();

            listaHistorico.clear();
            for (int i = 0; i < listaSelecionado.size(); i++) {
                List<HistoricoCarteirinha> listah = db.listaHistoricoCarteirinha((Integer) listaSelecionado.get(i).get(0));
                for (HistoricoCarteirinha listah1 : listah) {

                    AutorizaImpressaoCartao ai = db.pesquisaAutorizaPorHistorico(listah1.getId());

                    listaHistorico.add(new DataObject(listah1, "", listaSelecionado.get(i).get(5) + " - " + listaSelecionado.get(i).get(7), ai));

                }
            }
        }
    }

    public final void loadList() {
        if (showModalSelecteds) {
            return;
        }
        if (!printed) {
            if (status.isEmpty()) {
                status = "hoje";
            }
            if ((filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf") || filter.equals("empresa") || filter.equals("cnpj")) && query.isEmpty()) {
                GenericaMensagem.warn("Validação", "Específicar um valor válido para o filtro selecionado!");
                return;
            }
            printed = true;
            // listaSelecionado = new ArrayList();
            String inPessoasImprimir = "";
            if (GenericaSessao.exists("inPessoasImprimir")) {
                inPessoasImprimir = GenericaSessao.getString("inPessoasImprimir", true);
            }
            SisProcesso sisProcesso = new SisProcesso();
            sisProcesso.start();
            sisProcesso.setProcesso("Lista Cartão Social");
            listaCarteirinha = new ArrayList();
            listaCarteirinha = new SocioCarteirinhaDao().find(status, filter, query, indexOrdem, null, idOperador, typeDate, startDate, finishDate, inPessoasImprimir);
            if (!inPessoasImprimir.isEmpty()) {
                if (!listaCarteirinha.isEmpty()) {
                    listaSelecionado = new ArrayList();
                    for (int i = 0; i < listaCarteirinha.size(); i++) {
                        listaSelecionado.add(listaCarteirinha.get(i));
                    }
                    disabledImpressaoExterna = true;
                    inPessoasImprimir = "";
                } else {
                    listaCarteirinha = new SocioCarteirinhaDao().find("pendentes", filter, query, indexOrdem, null, idOperador, typeDate, startDate, finishDate, "");
                }
            }
            sisProcesso.finish();
        }
    }

    public void show() {
        if (listaSelecionado.isEmpty() && (listaSelecionadoMemoria == null || listaSelecionadoMemoria.isEmpty())) {
            Messages.warn("Validação", "Nenhum cartão selecionado!");
            return;
        }
        pdfCartao = "";
        clearUserFolder();
        UUID uuidX = UUID.randomUUID();
        String uuid = uuidX.toString().replace("-", "_");
        print(null, true, uuid);
        String originalFile = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/cartao_social/usuario/" + Usuario.getUsuario().getId() + "/cartao_social/cartao_social_" + uuid + ".pdf";
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(originalFile));
        if (f.exists()) {
            pdfCartao = originalFile;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CartaoSocialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // listaCarteirinha = new ArrayList();
        showModalSelecteds = true;
        // listaSelecionado = new ArrayList();
        // listaSelecionadoMemoria = new ArrayList();
    }

    public void print() {
        pdfCartao = "";
        clearUserFolder();
        print(null, false);
//        listaSelecionado = new ArrayList();
//        listaCarteirinha = new ArrayList();
//        listaSelecionadoMemoria = new ArrayList();
    }

    public void print(List vector) {
        print(vector, false);
    }

    public void print(List vector, Boolean show) {
        print(vector, show, null);
    }

    public void print(List vector, Boolean show, String filename) {
        pdfCartao = "";
        Sessions.remove("FILE_NAME_GENERATED");
        printed = false;
        Dao dao = new Dao();
        List<List> list = new ArrayList();
        if (!listaSelecionado.isEmpty() && vector == null) {
            list = listaSelecionado;
        } else if (vector != null) {
            list.add(vector);
        }
        String printLog = "";
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        if (!list.isEmpty()) {
            dao.openTransaction();
            SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
            DataHoje dh = new DataHoje();
            SociosDao dbs = new SociosDao();
            for (int i = 0; i < list.size(); i++) {
                Integer nrValidadeMeses = 0;
                Integer titular_id = (Integer) ((List) list.get(i)).get(40);
                Pessoa pessoa = (Pessoa) dao.find(new Pessoa(), (Integer) ((List) list.get(i)).get(0));
                Socios socios = dbs.pesquisaSocioPorPessoa(pessoa.getId());
                ValidadeCartao validadeCartao = new ValidadeCartao();
                SocioCarteirinha carteirinha = (SocioCarteirinha) dao.find(new SocioCarteirinha(), (Integer) ((List) list.get(i)).get(19));
                if (socios.getId() != -1) {
                    validadeCartao = new ValidadeCartaoDao().findByCategoriaParentesco(socios.getMatriculaSocios().getCategoria().getId(), socios.getParentesco().getId());
                    if (validadeCartao == null) {
                        GenericaMensagem.warn("Validação", "Nenhuma validade de cartão encontrada!");
                        dao.rollback();
                        return;
                    }
                    nrValidadeMeses = validadeCartao.getNrValidadeMeses();
                } else {
                    nrValidadeMeses = configuracaoSocial.getValidadeMesesCartaoAcademia();
                }
                if (socios.getId() != -1 && socios.getMatriculaSocios().getId() != -1) {
                    Date validadeCarteirinha;
                    if (validadeCartao.getDtValidadeFixa() == null) {
                        validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(nrValidadeMeses, DataHoje.data()));
                    } else {
                        validadeCarteirinha = validadeCartao.getDtValidadeFixa();
                    }
                    carteirinha.setDtValidadeCarteirinha(validadeCarteirinha);
                } else {
                    carteirinha.setDtValidadeCarteirinha(DataHoje.converte(dh.incrementarMeses(nrValidadeMeses, DataHoje.data())));
                }
                boolean validacao = false;
                if (pessoa.getSocios().getId() != -1) {
                    Fisica f = new FisicaDao().pesquisaFisicaPorPessoa(pessoa.getId());
                    if (pessoa.getSocios().getMatriculaSocios().getCategoria().isEmpresaObrigatoria()
                            && f.getDtAposentadoria() == null
                            && titular_id == pessoa.getId()) {
                        PessoaEmpresaDao db = new PessoaEmpresaDao();
                        PessoaEmpresa pe = db.pesquisaPessoaEmpresaPorPessoa(pessoa.getId());
                        //PessoaEmpresa pe = db.pesquisaPessoaEmpresaPorPessoa(titular_id);
                        if (pe.getId() == -1) {
                            GenericaMensagem.error("Atenção", "Sócio Sem Empresa Vinculada. Fazer o RECADASTRAMENTO !" + pessoa.getNome());
                            validacao = false;
                            listaSelecionado = new ArrayList();
                        }
                    }
                    listaSelecionado.remove(vector);
                }

                if (validacao) {
                    dao.rollback();
                    return;
                }

                String descricao_historico = "Impressão de Carteirinha";
                if (configuracaoSocial.getAtualizaViaCarteirinha()) {
                    carteirinha.setVia(carteirinha.getVia() + 1);
                    descricao_historico = "Impressão de " + carteirinha.getVia() + "° via do cartão";
                    list.get(i).set(11, carteirinha.getVia());
                }

                carteirinha.setEmissao(DataHoje.data());
                if (!dao.update(carteirinha)) {
                    dao.rollback();
                    return;
                }

                list.get(i).set(6, carteirinha.getValidadeCarteirinha());
                HistoricoCarteirinha hc = new HistoricoCarteirinha();

                hc.setCarteirinha(carteirinha);
                hc.setDescricao(descricao_historico);

                if (list.get(i).get(17) != null) {
                    Movimento m = (Movimento) dao.find(new Movimento(), Integer.valueOf(list.get(i).get(17).toString()));
                    if (m != null) {
                        hc.setMovimento(m);
                    }
                }

                if (!dao.save(hc)) {
                    dao.rollback();
                    return;
                }

                //AutorizaImpressaoCartao ai = dbc.pesquisaAutorizaSemHistorico(pessoa.getId(), modeloc.getId());
                AutorizaImpressaoCartao ai = dbc.pesquisaAutorizaSemHistorico(pessoa.getId(), carteirinha.getModeloCarteirinha().getId());

                if (ai != null) {
                    ai.setHistoricoCarteirinha(hc);
                    if (!dao.update(ai)) {
                        dao.rollback();
                        return;
                    }
                }
                printLog = "ID" + hc.getId()
                        + " - Pessoa {ID: " + pessoa.getId() + " - Nome: " + pessoa.getNome() + " }"
                        + " - Impresso por {ID: " + hc.getCarteirinha().getModeloCarteirinha().getId() + " - Nome: " + hc.getCarteirinha().getModeloCarteirinha().getDescricao() + " }";
                novoLog.setTabela("soc_historico_carteirinha");
                novoLog.setCodigo(hc.getId());
            }
            Reports reports = new Reports();
            reports.setPART_NAME("");
            reports.setPATH("downloads");
            if (show) {
                reports.setUSER_PATH(true);
                // reports.setPATH("downloads" + File.separator + Usuario.getUsuario().getId());
                reports.setREMOVE_FILE(false);
                reports.setDOWNLOAD(false);
            }
            if (ImpressaoParaSocios.imprimirCarteirinha(list, reports)) {
                dao.commit();
                if (show) {
                    String fileF = reports.getCONTEXT_FILE();
                    File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + fileF);
                    if (f.exists()) {
                        pdfCartao = "/" + fileF.replace("\\", "/");
                    }
                }
                if (status.equals("pendentes")) {
                    printed = false;
                }
                if (disabledImpressaoExterna) {
                    disabledImpressaoExterna = false;
                    GenericaSessao.put("status", "hoje");
                }
                listaCarteirinha = new ArrayList();
                listaSelecionado = new ArrayList();
                listaSelecionadoMemoria = new ArrayList();
                novoLog.print(printLog);
                novoLog.saveList();
            } else {
                novoLog.cancelList();
                dao.rollback();
            }
        }
    }

    // MÉTODO EM DESUSO APAGAR DEPOIS DE 30/04/2016
    public void reImprimirCarteirinha() {
        Dao dao = new Dao();

        if (!listaSelecionado.isEmpty()) {
            CategoriaDao dbCat = new CategoriaDao();
            DataHoje dh = new DataHoje();
            SociosDao dbs = new SociosDao();

            dao.openTransaction();

            for (int i = 0; i < listaSelecionado.size(); i++) {
                Pessoa pessoa = (Pessoa) dao.find(new Pessoa(), (Integer) ((List) listaSelecionado.get(i)).get(0));
                Socios socios = dbs.pesquisaSocioPorPessoa(pessoa.getId());
                SocioCarteirinha carteirinha = (SocioCarteirinha) dao.find(new SocioCarteirinha(), (Integer) ((List) listaSelecionado.get(i)).get(19));
                ValidadeCartao validadeCartao = new ValidadeCartaoDao().findByCategoriaParentesco(socios.getMatriculaSocios().getCategoria().getId(), socios.getParentesco().getId());
                if (validadeCartao == null) {
                    GenericaMensagem.warn("Validação", "Nenhuma validade de cartão encontrada!");
                    dao.rollback();
                    return;
                }
                if (socios.getId() != -1 && socios.getMatriculaSocios().getId() != -1) {
                    GrupoCategoria gpCat = dbCat.pesquisaGrupoPorCategoria(Integer.valueOf(socios.getMatriculaSocios().getCategoria().getId()));
                    Date validadeCarteirinha;
                    if (validadeCartao.getDtValidadeFixa() == null) {
                        validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(validadeCartao.getNrValidadeMeses(), DataHoje.data()));
                    } else {
                        validadeCarteirinha = validadeCartao.getDtValidadeFixa();
                    }
                    carteirinha.setDtValidadeCarteirinha(validadeCarteirinha);
                } else {
                    carteirinha.setDtValidadeCarteirinha(null);
                }

                carteirinha.setVia(carteirinha.getVia() + 1);
                listaSelecionado.get(i).set(6, carteirinha.getValidadeCarteirinha());

                if (carteirinha.getDtEmissao() == null) {
                    carteirinha.setEmissao(DataHoje.data());

                    if (!dao.update(carteirinha)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO ATUALIZAR CARTEIRINHA!");
                        return;
                    }

                    HistoricoCarteirinha hc = new HistoricoCarteirinha();

                    hc.setCarteirinha(carteirinha);
                    hc.setDescricao("Primeira ReImpressão de Carteirinha 2º Via");

                    if (listaSelecionado.get(i).get(17) != null) {
                        Movimento m = (Movimento) dao.find(new Movimento(), Integer.valueOf(listaSelecionado.get(i).get(17).toString()));
                        if (m != null) {
                            hc.setMovimento(m);
                        }
                    }

                    if (!dao.save(hc)) {
                        dao.rollback();
                        return;
                    }
                } else {
                    HistoricoCarteirinha hc = new HistoricoCarteirinha();

                    carteirinha.setVia(carteirinha.getVia() + 1);

                    if (!dao.update(carteirinha)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO ATUALIZAR CARTEIRINHA!");
                        return;
                    }

                    hc.setCarteirinha(carteirinha);
                    hc.setDescricao("ReImpressão de Carteirinha 2º Via");

                    if (listaSelecionado.get(i).get(17) != null) {
                        Movimento m = (Movimento) dao.find(new Movimento(), Integer.valueOf(listaSelecionado.get(i).get(17).toString()));
                        if (m != null) {
                            hc.setMovimento(m);
                        }
                    }

                    if (!dao.save(hc)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO ATUALIZAR HISTÓRICO DA CARTEIRINHA!");
                        return;
                    }
                }
            }

            if (ImpressaoParaSocios.imprimirCarteirinha(listaSelecionado)) {
                dao.commit();
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR CARTEIRINHA!");
            }
        }
    }

    public String imprimirEtiqueta() {
        SocioCarteirinhaDao dbs = new SocioCarteirinhaDao();

        List<Etiquetas> listax = new ArrayList();
        for (int i = 0; i < listaSelecionado.size(); i++) {
            List l = (List) dbs.listaPesquisaEtiqueta((Integer) ((List) listaSelecionado.get(i)).get(0)).get(0);
            listax.add(new Etiquetas(String.valueOf(l.get(0)),
                    String.valueOf(l.get(1)),
                    String.valueOf(l.get(2)),
                    String.valueOf(l.get(3)),
                    String.valueOf(l.get(4)),
                    String.valueOf(l.get(5)),
                    String.valueOf(l.get(6)),
                    String.valueOf(l.get(7)),
                    String.valueOf(l.get(8))));
        }

        if (listax.isEmpty()) {
            return null;
        }
        Reports reports = new Reports();
        reports.setPART_NAME("");
        reports.setPATH("etiquetas");
        reports.print("/Relatorios/ETIQUETA_SOCIO.jasper", "etiqueta_coluna", listax);
        return null;
    }

    public String imprimirEtiquetaTermica() {
        SocioCarteirinhaDao dbs = new SocioCarteirinhaDao();

        List<Etiquetas> listax = new ArrayList();
        for (int i = 0; i < listaSelecionado.size(); i++) {
            List l = (List) dbs.listaPesquisaEtiqueta((Integer) ((List) listaSelecionado.get(i)).get(0)).get(0);
            listax.add(new Etiquetas(String.valueOf(l.get(0)),
                    String.valueOf(l.get(1)),
                    String.valueOf(l.get(2)),
                    String.valueOf(l.get(3)),
                    String.valueOf(l.get(4)),
                    String.valueOf(l.get(5)),
                    String.valueOf(l.get(6)),
                    String.valueOf(l.get(7)),
                    String.valueOf(l.get(8))));
        }

        if (listax.isEmpty()) {
            return null;
        }

        Jasper.PART_NAME = "";
        Jasper.PATH = "etiquetas";
        Jasper.printReports("/Relatorios/ETIQUETA_TERMICA_SOCIAL_RETRATO.jasper", "etiqueta_termica", listax);
        return null;
    }

    /**
     * @return
     */
    public String getIndexOrdem() {
        return indexOrdem;
    }

    public void setIndexOrdem(String indexOrdem) {
        this.indexOrdem = indexOrdem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (por.equals("iMatricula")) {
            try {
                Integer.parseInt(descricao);
            } catch (Exception e) {
                descricao = "";
            }
        }
        this.descricao = descricao;
    }

    public List<List> getListaCarteirinha() {
        return listaCarteirinha;
    }

    public void setListaCarteirinha(List<List> listaCarteirinha) {
        this.listaCarteirinha = listaCarteirinha;
    }

    public String getPor() {
        return por;
    }

    public void setPor(String por) {
        this.por = por;
    }

    public String getPorLabel() {
        return porLabel;
    }

    public void setPorLabel(String porLabel) {
        this.porLabel = porLabel;
    }

    public List<List> getListaSelecionado() {
        return listaSelecionado;
    }

    public void setListaSelecionado(List<List> listaSelecionado) {
        if (toggle != null || toggle) {
//            this.listaSelecionado = listaSelecionado;
//            toggle = true;
        }
        for (int i = 0; i < this.listaSelecionado.size(); i++) {
            for (int j = 0; j < listaSelecionadoMemoria.size(); j++) {
                if (((Integer) ((List) listaSelecionadoMemoria.get(i)).get(0)) != ((Integer) ((List) this.listaSelecionado.get(i)).get(0))) {

                }
            }
        }
    }

    public List getListaHistorico() {
        return listaHistorico;
    }

    public void setListaHistorico(List listaHistorico) {
        this.listaHistorico = listaHistorico;
    }

    public List<List> getFilteredCarteirinha() {
        return filteredCarteirinha;
    }

    public void setFilteredCarteirinha(List<List> filteredCarteirinha) {
        this.filteredCarteirinha = filteredCarteirinha;
    }

    public void selectedPage(PageEvent event) {
        page = event.getPage();
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void onRowSelect(SelectEvent event) {
        List list = ((List) event.getObject());
//         if (new Registro().isCobrancaCarteirinha()) {
//            if (!status.equals("nao_impressos")) {
//                String validadeCarteirinha = list.get(6).toString();
//                if (DataHoje.maiorData(DataHoje.data(), validadeCarteirinha)) {
//                    GenericaMensagem.warn("SISTEMA", "CARTÃO ENCONTRA-SE VENCIDO! GERAR UMA NOVA VIA");
//                    return;
//                }
//            }             
//         }
        listaSelecionado.add(((List) event.getObject()));
    }

    public void onRowUnselect(UnselectEvent event) {
        listaSelecionado.remove((List) event.getObject());
//        for (int i = 0; i < listaSelecionado.size(); i++) {
//            if (((List) listaSelecionado.get(i)).get(0) == ((List) event.getObject()).get(0)) {
//                listaSelecionado.remove(i);
//                break;
//            }
//        }
    }

    public void removeSelect(List<List> list) {
        listaSelecionado.remove(list);
    }

    public String clear() {
        listaSelecionado = new ArrayList();
        page = null;
        // return  "cartaoSocial";
        return null;
    }

    public void listernetFilter(FilterEvent filterEvent) {
        toggle = true;
    }

    public void listener(String tcase) {
        if (tcase.equals("query")) {
            query = "";
        }
        if (tcase.equals("reload")) {
            listaSelecionado = new ArrayList();
            listaSelecionadoMemoria = new ArrayList();
            startDate = "";
            finishDate = "";
            if (status.equals("impressos")) {
                typeDate = "hoje";
                loadOperador();
            } else {
                typeDate = "";
            }
        } else if (tcase.equals("close_modal_selecteds")) {
            clearUserFolder();
            try {
                String originalFile = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/cartao_social/usuario/" + Usuario.getUsuario().getId() + "/cartao_social/cartao_social.pdf";
                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(originalFile));
                if (f.exists()) {
                    f.delete();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CartaoSocialBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception e) {

            }
//            listaSelecionado = new ArrayList();
//            listaCarteirinha = new ArrayList();
//            listaSelecionadoMemoria = new ArrayList();
            showModalSelecteds = false;
            printed = false;
            loadList();
        }
    }

    public void toggleSelectedListener() {
        Integer pageNumber = listaCarteirinha.size() / 10;
        Integer indexMin = 0;
        Integer indexMax = 0;
        Integer pg = 0;
        if (page != null) {
            pg = page + 1;
        } else {
            pg = 1;
        }
        if (pageNumber == 1) {
            indexMax = listaCarteirinha.size() - 1;
            indexMin = 0;
        } else if (pageNumber > 1) {
            if (page == null || page == 0) {
                indexMin = 0;
                indexMax = 9;
            } else {
                indexMin = page;
                indexMax = page + 10;
            }
        }
        int x = 0;
        listaSelecionado.clear();
        for (int i = indexMin; i < listaCarteirinha.size(); i++) {
            if (x == 10) {
                break;
            }
            listaSelecionado.add(listaCarteirinha.get(i));
            x++;
        }
    }

    public String selectedAll() {
        if (!listaSelecionado.isEmpty()) {
            if (listaSelecionado.size() == listaCarteirinha.size()) {
                listaSelecionado.clear();
            } else {
                listaSelecionado.addAll(listaCarteirinha);
            }
        } else {
            listaSelecionado.addAll(listaCarteirinha);
        }
        return "cartaoSocial";
    }

    public Boolean getToggle() {
        return toggle;
    }

    public void setToggle(Boolean toggle) {
        this.toggle = toggle;
    }

    public Integer getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(Integer firstIndex) {
        if (this.firstIndex > listaCarteirinha.size()) {
            this.firstIndex = 0;
        }
        this.firstIndex = firstIndex;
    }

    public Integer getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(Integer lastIndex) {
        if (firstIndex > 0) {
            if (this.lastIndex == 0) {
                this.lastIndex = this.firstIndex;
            } else {
                this.lastIndex = lastIndex;
            }
        } else {
            if (this.lastIndex < this.firstIndex) {
                this.lastIndex = this.firstIndex;
            }
            if (this.firstIndex > listaCarteirinha.size()) {
                this.lastIndex = listaCarteirinha.size();
            }
            if (this.lastIndex == 0) {
                this.lastIndex = this.firstIndex;
            }
        }
    }

    public String loadSelecteds() {
        if (firstIndex == 0 && lastIndex == 0) {
            return null;
        }
        listaSelecionado.clear();
        for (int i = firstIndex - 1; i < lastIndex; i++) {
            listaSelecionado.add(listaCarteirinha.get(i));
        }
        return "cartaoSocial";
    }

    public List<SelectItem> getListFilial() {
        if (listFilial.isEmpty()) {
            MacFilial mf = MacFilial.getAcessoFilial();
            idFilial = 0;
            List<Filial> list = new Dao().list(new Filial(), true);
            int j = 0;
            listFilial.add(new SelectItem(j, "TODAS", null));
            j = 1;
            for (int i = 0; i < list.size(); i++) {
                if (disabled) {
                    if (Objects.equals(list.get(i).getId(), mf.getFilial().getId())) {
                        idFilial = j;
                    }
                }
                listFilial.add(new SelectItem(j, list.get(i).getFilial().getPessoa().getNome(), "" + list.get(i).getId()));
                j++;
            }
        }
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    /**
     * Médtodo genérico para geração de históricos de carteirinhas
     *
     * @param list (Movimentos gerados)
     * @param idModelo
     * @return
     */
    public static boolean isGerarHistoricoCarteirinhas(List<Movimento> list, Integer idModelo) {
        return !CartaoSocialBean.gerarHistoricoCarteirinhas(list, idModelo).isEmpty();
    }

    /**
     * Médtodo genérico para geração de históricos de carteirinhas
     *
     * @param list (Movimentos gerados)
     * @param idCategoria
     * @param idRotina
     * @return
     */
    public static boolean isGerarHistoricoCarteirinhas(List<Movimento> list, Integer idCategoria, Integer idRotina) {
        return !gerarHistoricoCarteirinhas(list, idCategoria, idRotina).isEmpty();
    }

    /**
     * Médtodo genérico para geração de históricos de carteirinhas se retornar
     * uma lista é que existem carteirinhas geradas.
     *
     * @param list
     * @param idCategoria
     * @param idRotina
     * @return
     */
    public static List<HistoricoCarteirinha> gerarHistoricoCarteirinhas(List<Movimento> list, Integer idCategoria, Integer idRotina) {
        SocioCarteirinhaDao socioCarteirinhaDB = new SocioCarteirinhaDao();
        ModeloCarteirinha modeloCarteirinha = socioCarteirinhaDB.pesquisaModeloCarteirinha(idCategoria, idRotina);
        if (modeloCarteirinha != null) {
            return gerarHistoricoCarteirinhas(list, modeloCarteirinha.getId());
        }
        return new ArrayList();
    }

    /**
     *
     * @param list
     * @param idModelo
     * @return
     */
    public static List<HistoricoCarteirinha> gerarHistoricoCarteirinhas(List<Movimento> list, Integer idModelo) {
        HistoricoCarteirinha historicoCarteirinha;
        SocioCarteirinhaDao socioCarteirinhaDB = new SocioCarteirinhaDao();
        Dao dao = new Dao();
        dao.openTransaction();
        List<HistoricoCarteirinha> carteirinhas = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMatriculaSocios() != null) {
                historicoCarteirinha = new HistoricoCarteirinha();
                historicoCarteirinha.setHora(DataHoje.hora());
                historicoCarteirinha.setDescricao("Impressão de Carteirinha");
                historicoCarteirinha.setEmissao(DataHoje.data());
                historicoCarteirinha.setMovimento(list.get(i));
                historicoCarteirinha.setCarteirinha(socioCarteirinhaDB.pesquisaCarteirinhaPessoa(list.get(i).getBeneficiario().getId(), idModelo));
                if (!dao.save(historicoCarteirinha)) {
                    dao.rollback();
                    return new ArrayList();
                }
                carteirinhas.add(historicoCarteirinha);
            }
        }
        if (carteirinhas.isEmpty()) {
            dao.rollback();
            return new ArrayList();
        }
        dao.commit();
        return carteirinhas;
    }

    public void imprimirSocioCarteirinha(List list) {
        if (list.isEmpty()) {
            return;
        }
        Boolean isBeneficiario = false;
        List<SocioCarteirinha> carteirinhas = new ArrayList<>();
        String type = list.get(0).getClass().getSimpleName();
        if (type.equals("HistoricoCarteirinha")) {
            List<HistoricoCarteirinha> historicoCarteirinhas = list;
            for (int i = 0; i < historicoCarteirinhas.size(); i++) {
                carteirinhas.add(historicoCarteirinhas.get(i).getCarteirinha());
                isBeneficiario = true;
            }
        } else {
            carteirinhas = (List<SocioCarteirinha>) list;
        }

        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        List listAux = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            carteirinhas.get(i).setEmissao(DataHoje.data());
            new Dao().update(carteirinhas.get(i), true);
            if (isBeneficiario) {
                listAux.addAll(dbc.filtroCartao(((HistoricoCarteirinha) list.get(i)).getMovimento().getBeneficiario().getId()));
            } else {
                HistoricoCarteirinha hc = new HistoricoCarteirinha();

                hc.setCarteirinha(carteirinhas.get(i));
                hc.setDescricao("Impressão de Carteirinha pela Geração de Cartão");

                if (!new Dao().save(hc, true)) {
                    return;
                }

                listAux.addAll(dbc.filtroCartao(carteirinhas.get(i).getPessoa().getId()));
            }
        }
        ImpressaoParaSocios.imprimirCarteirinha(listAux);
        GenericaSessao.put("cartao_social_sucesso", true);
    }

    public List<List> getListaSelecionadoMemoria() {
        return listaSelecionadoMemoria;
    }

    public void setListaSelecionadoMemoria(List<List> listaSelecionadoMemoria) {
        this.listaSelecionadoMemoria = listaSelecionadoMemoria;
    }

    public Integer getFilialInteger() {
        if (!listFilial.isEmpty()) {
            try {
                return Integer.parseInt(listFilial.get(idFilial).getDescription());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }

    public void printTest() {
        try {
            Collection<BeanWithList> coll = new ArrayList<BeanWithList>();

            BeanWithList bean = new BeanWithList(Arrays.asList("London", "Paris"), 1);

            coll.add(bean);

            bean = new BeanWithList(Arrays.asList("London", "Madrid", "Moscow"), 2);
            coll.add(bean);

            bean = new BeanWithList(Arrays.asList("Rome"), 3);
            coll.add(bean);

            Map<String, Object> params = new HashMap<>();
            Jasper.printReports("TESTE.jasper", "TESTE.jasper", coll);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private JRDataSource getDataSource() {
        Collection<BeanWithList> coll = new ArrayList<>();

        BeanWithList bean = new BeanWithList(Arrays.asList("London", "Paris"), 1);

        coll.add(bean);

        bean = new BeanWithList(Arrays.asList("London", "Madrid", "Moscow"), 2);
        coll.add(bean);

        bean = new BeanWithList(Arrays.asList("Rome"), 3);
        coll.add(bean);

        return new JRBeanCollectionDataSource(coll);
    }

    public String getStatus() {
        if (GenericaSessao.exists("status")) {
            status = GenericaSessao.getString("status", true);
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Boolean getPrinted() {
        return printed;
    }

    public void setPrinted(Boolean printed) {
        this.printed = printed;
    }

    public Integer getIdOperador() {
        return idOperador;
    }

    public void setIdOperador(Integer idOperador) {
        this.idOperador = idOperador;
    }

    public List<SelectItem> getListOperador() {
        return listOperador;
    }

    public void setListOperador(List<SelectItem> listOperador) {
        this.listOperador = listOperador;
    }

    public void loadOperador() {
        listOperador = new ArrayList();
        listOperador.add(new SelectItem(null, "TODOS"));
        idOperador = null;
        List<Usuario> list = new UsuarioDao().findByTabela("soc_historico_carteirinha");
        for (int i = 0; i < list.size(); i++) {
            listOperador.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome()));
        }
    }

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Boolean getPaginacao() {
        return paginacao;
    }

    public void setPaginacao(Boolean paginacao) {
        this.paginacao = paginacao;
    }

    public Integer getResultadosPorPagina() {
        return resultadosPorPagina;
    }

    public void setResultadosPorPagina(Integer resultadosPorPagina) {
        this.resultadosPorPagina = resultadosPorPagina;
    }

    public Boolean getSomenteAutorizados() {
        return somenteAutorizados;
    }

    public void setSomenteAutorizados(Boolean somenteAutorizados) {
        this.somenteAutorizados = somenteAutorizados;
    }

    public Boolean getDisabledImpressaoExterna() {
        return disabledImpressaoExterna;
    }

    public void setDisabledImpressaoExterna(Boolean disabledImpressaoExterna) {
        this.disabledImpressaoExterna = disabledImpressaoExterna;
    }

    public Boolean getLiberaReimpressao() {
        if (liberaReimpressao == null) {
            liberaReimpressao = new ControleAcessoBean().verificaPermissao("cartao_social_libera_reimpressao", 4);
        }
        return liberaReimpressao;
    }

    public void setLiberaReimpressao(Boolean liberaReimpressao) {
        this.liberaReimpressao = liberaReimpressao;
    }

    public Boolean liberaReimpressaoPorData(String emissao) {
        if (getLiberaReimpressao()) {
            if (!emissao.isEmpty()) {
                return !DataHoje.igualdadeData(emissao, DataHoje.data());
            }
        }
        return false;
    }

    public String getPdfCartao() {
        return pdfCartao;
    }

    public Boolean getExistPdfCartao() {
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(pdfCartao));
        if (f.exists()) {
            if (f.isFile()) {
                return true;
            }
        }
        return false;
    }

    public Boolean getShowModalSelecteds() {
        return showModalSelecteds;
    }

    public void setShowModalSelecteds(Boolean showModalSelecteds) {
        this.showModalSelecteds = showModalSelecteds;
    }

    public class BeanWithList {

        private List<String> m_cities;
        private Integer m_id;

        public BeanWithList(List<String> cities, Integer id) {
            m_cities = cities;
            m_id = id;
        }

        public List<String> getCities() {
            return m_cities;
        }

        public Integer getId() {
            return m_id;
        }
    }

    public String getMascaraAlteracao() {
        if (filter != null && !filter.isEmpty()) {
            String f = filter;
            if (filter.equals("cpf_titular")) {
                f = "cpf";
            }
            return Mask.getMascaraPesquisa(f, true);
        }
        return "";
    }

    public Integer getSize() {
        if (filter != null && !filter.isEmpty()) {
            switch (filter) {
                case "nome":
                case "nome_titular":
                case "empresa":
                    return 500;
                case "cpf":
                case "cpf_titular":
                    return 120;
                case "cnpj":
                    return 150;
                case "codigo":
                case "matricula":
                    return 80;
                case "nascimento":
                    return 100;
            }
        }
        return 50;
    }

    public final void clearUserFolder() {
        try {
            String originalFile = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/tmp/" + Usuario.getUsuario().getId() + "/cartao_social/";
            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(originalFile));
            if (f.exists()) {
                FileUtils.cleanDirectory(f);
            }
            pdfCartao = "";
        } catch (IOException e) {

        }
    }

}
