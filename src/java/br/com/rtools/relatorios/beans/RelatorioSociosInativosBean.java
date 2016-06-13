package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.SMotivoInativacao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.CategoriaDao;
import br.com.rtools.impressao.ParametroSociosInativos;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioSociosDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.SelectItemSort;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioSociosInativosBean implements Serializable {

    private List<Filters> listFilters;
    private List<Socios> listaSocios;

    // DATAS
    private String dataInativacaoInicial;
    private String dataInativacaoFinal;
    private String dataFiliacaoInicial;
    private String dataFiliacaoFinal;
    private String ordernarPor;

    private Relatorios relatorios;

    private String status;

    // IDS
    private Integer idRelatorio;
    private Integer idCategoria;
    private Integer idGrupoCategoria;
    private Integer idMotivo;

    Boolean comDependentes;

    // SELECT ITENS
    private List<SelectItem> listCategoria;
    private List<SelectItem> listGrupoCategoria;
    private List<SelectItem> listTipoRelatorio;
    private List<SelectItem> listMotivo;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();
        listaSocios = new ArrayList();
        listMotivo = new ArrayList();
        listTipoRelatorio = new ArrayList();
        listCategoria = new ArrayList();
        listGrupoCategoria = new ArrayList();
        this.loadListRelatorio();
        idRelatorio = 0;
        comDependentes = false;
        dataInativacaoInicial = DataHoje.data();
        dataInativacaoFinal = DataHoje.data();
        dataFiliacaoInicial = DataHoje.data();
        dataFiliacaoFinal = DataHoje.data();
        this.loadListCategoria();
        this.loadListGrupoCategoria();
        this.loadListMotivo();
        idCategoria = 0;
        idGrupoCategoria = 0;
        relatorios = new Relatorios();
        ordernarPor = "nome";
        loadFilters();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioSociosInativosBean");
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("dependentes", "Com dependentes", false, false));
        listFilters.add(new Filters("data_inativacao", "Data Inativação", false, false));
        listFilters.add(new Filters("data_filiacao", "Data Filiação", false, true));
        listFilters.add(new Filters("categoria", "Categoria", false, false));
        listFilters.add(new Filters("grupo_categoria", "Grupo Categoria", false, false));
        listFilters.add(new Filters("status", "Status", false, false));
        listFilters.add(new Filters("motivo", "Motivo", false, false));
    }

    public void print() {
        RelatorioSociosDao db = new RelatorioSociosDao();

        Integer categoria_id = null, grupo_categoria_id = null;
        String in_motivo = null;

        String dtII = null;
        String dtIF = null;
        if (listFilters.get(1).getActive()) {
            dtII = dataInativacaoInicial;
            dtIF = dataInativacaoFinal;
        }

        String dtFI = null;
        String dtFF = null;
        if (listFilters.get(2).getActive()) {
            dtFI = dataFiliacaoInicial;
            dtFF = dataFiliacaoFinal;
        }

        if (listFilters.get(3).getActive()) {
            categoria_id = idCategoria;
        }
        if (listFilters.get(4).getActive()) {
            grupo_categoria_id = idGrupoCategoria;
        }

        String s = null;
        if (listFilters.get(5).getActive()) {
            s = status;
        }

        if (listFilters.get(6).getActive()) {
            in_motivo = "" + idMotivo;
        }

        List list = db.listaSociosInativos(listFilters.get(0).getActive(), dtII, dtIF, dtFI, dtFF, categoria_id, grupo_categoria_id, ordernarPor, s, in_motivo);

        Dao di = new Dao();

        Juridica sindicato = (Juridica) di.find(new Juridica(), 1);
        PessoaEndereco endSindicato = (new PessoaEnderecoDao()).pesquisaEndPorPessoaTipo(sindicato.getId(), 3);

        List<ParametroSociosInativos> lista = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            lista.add(new ParametroSociosInativos(
                    ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                    sindicato.getPessoa().getSite(), // sinSite
                    sindicato.getPessoa().getNome(), // sinNome
                    endSindicato.getEndereco().getDescricaoEndereco().getDescricao(),
                    endSindicato.getEndereco().getLogradouro().getDescricao(),
                    endSindicato.getNumero(),
                    endSindicato.getComplemento(),
                    endSindicato.getEndereco().getBairro().getDescricao(),
                    endSindicato.getEndereco().getCep(),
                    endSindicato.getEndereco().getCidade().getCidade(),
                    endSindicato.getEndereco().getCidade().getUf(),
                    sindicato.getPessoa().getDocumento(), // sinDocumento
                    o.get(0).toString(), // nomeTitular
                    o.get(1).toString(), // codTitular
                    o.get(2).toString(), // codSocio
                    o.get(3).toString() + " (" + o.get(4).toString() + ") ", // nome
                    o.get(4).toString(), // parentesco
                    o.get(5).toString(), // matricula
                    o.get(6).toString(), // categoria
                    DataHoje.converteData((Date) o.get(7)), // filiacao -- data
                    DataHoje.converteData((Date) o.get(8)), // inativacao -- data
                    o.get(9).toString()) // motivo_inativacao
            );
        }

        if (!lista.isEmpty()) {
            try {
                Relatorios r = (Relatorios) di.find(new Relatorios(), idRelatorio);
                String jasperName = r.getNome();
                String jasperFile = r.getJasper();
                if (comDependentes) {
                    jasperName = "relatorio sócios inativos dependente";
                    jasperFile = "/Relatorios/SOCIOINATIVODEPENDENTE.jasper";
                }

                Jasper.PART_NAME = AnaliseString.removerAcentos(jasperName.toLowerCase());
                Jasper.PATH = "downloads";
                if (r.getPorFolha()) {
                    Jasper.GROUP_NAME = r.getNomeGrupo();
                }
                Jasper.printReports(jasperFile, "relatorios", (Collection) lista);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "data_inativacao":
                dataInativacaoInicial = DataHoje.data();
                dataInativacaoFinal = DataHoje.data();
                break;
            case "data_filiacao":
                dataFiliacaoInicial = DataHoje.data();
                dataFiliacaoFinal = DataHoje.data();
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public final void loadListRelatorio() {
        listTipoRelatorio.clear();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelatorio = list.get(i).getId();
            }
            listTipoRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
    }

    public final void loadListCategoria() {
        listCategoria.clear();
        List<Categoria> list = new Dao().list(new Categoria(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCategoria = list.get(i).getId();
            }
            listCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria()));
        }
    }

    public final void loadListGrupoCategoria() {
        listGrupoCategoria.clear();
        List<GrupoCategoria> list = new Dao().list(new GrupoCategoria(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoCategoria = list.get(i).getId();
            }
            listGrupoCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getGrupoCategoria()));
        }
    }

    public final void loadListMotivo() {
        listMotivo.clear();
        List<SMotivoInativacao> list = new Dao().list(new SMotivoInativacao());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idMotivo = list.get(i).getId();
            }
            listMotivo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
        SelectItemSort.sort(listMotivo);
    }

    public List<SelectItem> getListTipoRelatorio() {
        return listTipoRelatorio;
    }

    public void setListTipoRelatorio(List<SelectItem> listTipoRelatorio) {
        this.listTipoRelatorio = listTipoRelatorio;
    }

    public boolean isComDependentes() {
        return comDependentes;
    }

    public void setComDependentes(boolean comDependentes) {
        this.comDependentes = comDependentes;
    }

    public List<Socios> getListaSocios() {
        return listaSocios;
    }

    public void setListaSocios(List<Socios> listaSocios) {
        this.listaSocios = listaSocios;
    }

    public String getDataInativacaoInicial() {
        return dataInativacaoInicial;
    }

    public void setDataInativacaoInicial(String dataInativacaoInicial) {
        this.dataInativacaoInicial = dataInativacaoInicial;
    }

    public String getDataInativacaoFinal() {
        return dataInativacaoFinal;
    }

    public void setDataInativacaoFinal(String dataInativacaoFinal) {
        this.dataInativacaoFinal = dataInativacaoFinal;
    }

    public String getDataFiliacaoInicial() {
        return dataFiliacaoInicial;
    }

    public void setDataFiliacaoInicial(String dataFiliacaoInicial) {
        this.dataFiliacaoInicial = dataFiliacaoInicial;
    }

    public String getDataFiliacaoFinal() {
        return dataFiliacaoFinal;
    }

    public void setDataFiliacaoFinal(String dataFiliacaoFinal) {
        this.dataFiliacaoFinal = dataFiliacaoFinal;
    }

    public List<SelectItem> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<SelectItem> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<SelectItem> getListGrupoCategoria() {
        return listGrupoCategoria;
    }

    public void setListGrupoCategoria(List<SelectItem> listGrupoCategoria) {
        this.listGrupoCategoria = listGrupoCategoria;
    }

    public Integer getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(Integer idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public String getOrdernarPor() {
        return ordernarPor;
    }

    public void setOrdernarPor(String ordernarPor) {
        this.ordernarPor = ordernarPor;
    }

    public Relatorios getRelatorios() {
        if (relatorios != null) {
            if (relatorios.getId() == -1) {
                relatorios = (Relatorios) new Dao().find(new Relatorios(), idRelatorio);
            }
        }
        return relatorios;
    }

    /**
     * tcase
     * <ul>
     * <li>1 - Relatório == null </li>
     * </ul>
     *
     * @param tcase
     */
    public void listener(Integer tcase) {
        relatorios = new Relatorios();
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListMotivo() {
        return listMotivo;
    }

    public void setListMotivo(List<SelectItem> listMotivo) {
        this.listMotivo = listMotivo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(Integer idMotivo) {
        this.idMotivo = idMotivo;
    }
}
