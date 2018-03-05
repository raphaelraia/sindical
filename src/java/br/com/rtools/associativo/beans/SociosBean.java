package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.MatriculaSociosDao;
import br.com.rtools.associativo.dao.ServicoCategoriaDao;
import br.com.rtools.associativo.dao.ParentescoDao;
import br.com.rtools.associativo.dao.CategoriaDao;
import br.com.rtools.arrecadacao.GrupoCidades;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CredenciadoresDao;
import br.com.rtools.associativo.dao.PeriodoMensalidadeDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.ValidadeCartaoDao;
import br.com.rtools.associativo.lista.ListaDependentes;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.ServicoPessoaBloqueio;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoPessoaBloqueioDao;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.utilitarios.PessoaUtilitarios;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import static br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean.getCliente;
import br.com.rtools.sistema.Mes;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.sistema.dao.PeriodoDao;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.primefaces.context.RequestContext;

@ManagedBean
@SessionScoped
public class SociosBean implements Serializable {

    private ServicoPessoa servicoPessoa;
    private ServicoCategoria servicoCategoria;
    private Socios socios;
    private SocioCarteirinha socCarteirinha;
    private MatriculaSocios matriculaSocios;
    private PessoaEmpresa pessoaEmpresa;
    private Fisica dependente;
    private boolean chkContaCobranca;
    private List<SelectItem> listaTipoDocumento;
    private List<SelectItem> listaServicos;
    //private List<SelectItem> listaParentesco;
    // private List<DataObject> listaDependentes;
    // private List<DataObject> listaDependentesInativos;
    private List<SelectItem> listaFilial;
    private final List<SelectItem> listaMotivoInativacao;
    private int idTipoDocumento;
    private int idServico;
    private int idGrupoCategoria;
    private int idCategoria;
    private int idIndexCombo;
    private Integer idInativacao;
    private Integer idFilial;
    private boolean renderServicos;
    private boolean desabilitaImpressao;
    private boolean imprimirVerso;
    private String lblSocio;
    private String lblSocioPergunta;
    private String tipoDestinario;
    private String dataInativacao;
    private String dataReativacao;
    private String statusSocio;
// -- 
    private List<SelectItem> listaGrupoCategoria;
    private List<SelectItem> listaCategoria;
    private List<Fisica> listaFisica;
    private Fisica fisicaPesquisa;
    private Pessoa pessoaPesquisa;
    private List<SelectItem> listaSelectFisica;

    private Fisica novoDependente;
    private int index_dependente;
    private final String[] imagensTipo;
    private List<Socios> listaSocioInativo;

    private Part filePart;
    private Usuario usuario;

    private boolean modelVisible;

    private final Registro registro;

    private String novaValidadeCartao;
    private DescontoSocial descontoSocial;
    private List<SelectItem> listaDescontoSocial;
    private DescontoSocial novoDescontoSocial;
    private String valorServico;
    private String novoValorServico;
    private String alteraValorServico;
    private Integer index_desconto;
    private List<ListaDependentes> listDependentes;
    private List<ListaDependentes> listDependentesAtivos;
    private List<ListaDependentes> listDependentesInativos;
    private Bloqueio bloqueio;
    private List<Socios> listSubSocios = new ArrayList();
    private List<Fisica> listFisicaSugestao = new ArrayList();
    private List<SelectItem> listSisPeriodos;
    private Integer idSisPeriodo;
    private Double valorTaxaInscricao;
    private Integer nrParcerlasTxInscricao;
    private List<SelectItem> listParcerlasTxInscricao;
    private Lote loteTaxaInscricao;
    private List<Movimento> listMovimentosTaxaInscricao;

    private Integer indexCredenciadores;
    private List<SelectItem> listaCredenciadores;

    public SociosBean() {
        listFisicaSugestao = new ArrayList();
        bloqueio = new Bloqueio();
        servicoPessoa = new ServicoPessoa();
        servicoCategoria = new ServicoCategoria();
        socios = new Socios();
        socCarteirinha = new SocioCarteirinha();
        matriculaSocios = new MatriculaSocios();
        pessoaEmpresa = new PessoaEmpresa();
        dependente = new Fisica();
        chkContaCobranca = false;
        listaTipoDocumento = new ArrayList();
        listParcerlasTxInscricao = new ArrayList();
        listaServicos = new ArrayList();
        // listaDependentes = new ArrayList();
        // listaDependentesInativos = new ArrayList();
        listaFilial = new ArrayList();
        idTipoDocumento = 0;
        idServico = 0;
        idGrupoCategoria = 0;
        idCategoria = 0;
        idIndexCombo = -1;
        idInativacao = 0;
        idFilial = 0;
        renderServicos = true;
        desabilitaImpressao = true;
        imprimirVerso = false;
        dataInativacao = DataHoje.data();
        dataReativacao = DataHoje.data();
        lblSocio = "";
        lblSocioPergunta = "";
        tipoDestinario = "socio";
        statusSocio = "NOVO";
        listaMotivoInativacao = new ArrayList();

        registro = (Registro) new Dao().find(new Registro(), 1);
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        listaDescontoSocial = new ArrayList();
        novoDescontoSocial = new DescontoSocial();

        listaGrupoCategoria = new ArrayList();
        listaCategoria = new ArrayList();
        listaFisica = new ArrayList();
        fisicaPesquisa = new Fisica();
        pessoaPesquisa = new Pessoa();
        listaSelectFisica = new ArrayList();
        novoDependente = new Fisica();
        index_dependente = 0;
        imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
        listaSocioInativo = new ArrayList();
        usuario = new Usuario();
        modelVisible = false;
        novaValidadeCartao = "";

        loadGrupoCategoria();
        loadCategoria();
        loadTipoDocumento();
        // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
        //loadServicos();

        valorServico = "0,00";
        novoValorServico = "0,00";
        alteraValorServico = "0,00";
        index_desconto = 0;
        matriculaSocios.setEmissao(DataHoje.data());
        Diretorio.remover("temp/foto/" + getUsuario().getId() + "/");
        listDependentes = new ArrayList();
        listDependentesInativos = new ArrayList();
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
        if (f.exists()) {
            f.delete();
        }
        String url_temp = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png");
        if (new File(url_temp).exists()) {
            new File(url_temp).delete();
        }
        listSisPeriodos = new ArrayList();
        idSisPeriodo = null;
        valorTaxaInscricao = new Double(0);
        nrParcerlasTxInscricao = 0;

        loadListaCredenciadores();
    }

    @PreDestroy
    public void destroy() {
//        GenericaSessao.remove("uploadBean");
//        GenericaSessao.remove("photoCamBean");
        Diretorio.remover("temp/foto/" + getUsuario().getId() + "/");
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
        if (f.exists()) {
            f.delete();
        }
        String url_temp = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png");
        if (new File(url_temp).exists()) {
            new File(url_temp).delete();
        }
    }

    public final void loadListaCredenciadores() {
        indexCredenciadores = 0;
        listaCredenciadores = new ArrayList();

        List<Credenciadores> result = new CredenciadoresDao().listaCredenciadores();

        for (int i = 0; i < result.size(); i++) {
            listaCredenciadores.add(
                    new SelectItem(i, result.get(i).getPessoa().getNome(), Integer.toString(result.get(i).getId()))
            );

            if (result.get(i).getPessoa().getId() == 1) {
                indexCredenciadores = i;
            }
        }
    }

    public void calculoDescontoDependente(ListaDependentes linha) {
        linha.setNrDesconto(
                calculoDescontoGenerico(
                        linha.getValor(),
                        linha.getFisica().getPessoa().getId(),
                        linha.getServicoPessoa().getServicos().getId(),
                        //linha.getFisica().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()
                        servicoCategoria.getCategoria().getId()
                )
        );
    }

    public void calculoValorDependente(ListaDependentes linha) {
        if (linha != null) {
            linha.setValor(
                    calculoValorGenerico(
                            linha.getServicoPessoa().getNrDesconto(),
                            linha.getFisica().getPessoa().getId(),
                            linha.getServicoPessoa().getServicos().getId(),
                            //linha.getFisica().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()
                            servicoCategoria.getCategoria().getId()
                    )
            );
        } else {
            for (int i = 0; i < listDependentes.size(); i++) {
                listDependentes.get(i).setValor(
                        calculoValorGenerico(
                                listDependentes.get(i).getServicoPessoa().getNrDesconto(),
                                listDependentes.get(i).getFisica().getPessoa().getId(),
                                listDependentes.get(i).getServicoPessoa().getServicos().getId(),
                                //linha.getFisica().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()
                                servicoCategoria.getCategoria().getId()
                        )
                );
            }
        }
    }

    // METODO GENERICO, MUDAR CLASSE
    // RETORNA O DESCONTO PASSANDO O VALOR DO SERVIÇO
    public double calculoDescontoGenerico(double valor, int id_pessoa, int id_servico, Integer id_categoria) {
        String valorx;
        if (id_categoria != null) {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(id_pessoa, id_servico, DataHoje.dataHoje(), 0, id_categoria));
        } else {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(id_pessoa, id_servico, DataHoje.dataHoje(), 0, null));
        }

        String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(id_pessoa, id_servico, DataHoje.dataHoje()));

        if (valor == Moeda.converteUS$(valorx)) {
            return 0;
        } else {
            double valorx_c = Moeda.subtracao(Moeda.converteUS$(valorx_cheio), valor);
            valorx_c = Moeda.multiplicar(Moeda.divisao(valorx_c, Moeda.converteUS$(valorx_cheio)), 100);
            return valorx_c;
        }
    }

    // METODO GENERICO, MUDAR CLASSE
    // RETORNA O VALOR PASSANDO O DESCONTO PARA O SERVIÇO
    public double calculoValorGenerico(double desconto, int id_pessoa, int id_servico, Integer id_categoria) {
        String valorx;
        if (desconto == 0) {
            if (id_categoria != null) {
                valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(id_pessoa, id_servico, DataHoje.dataHoje(), 0, id_categoria));
            } else {
                valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(id_pessoa, id_servico, DataHoje.dataHoje(), 0, null));
            }
        } else {
            double valorx_c = new FunctionsDao().valorServicoCheio(id_pessoa, id_servico, DataHoje.dataHoje());

            double calculo = Moeda.divisao(Moeda.multiplicar(desconto, valorx_c), 100);
            valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, calculo));
        }

        return Moeda.converteUS$(valorx);
    }

    public void loadPessoaComplemento(Integer id_pessoa) {
        PessoaDao db = new PessoaDao();
        PessoaComplemento pc = db.pesquisaPessoaComplementoPorPessoa(id_pessoa);

        if (pc.getId() == -1) {
            servicoPessoa.setNrDiaVencimento(getRegistro().getFinDiaVencimentoCobranca());
        } else {
            servicoPessoa.setNrDiaVencimento(pc.getNrDiaVencimento());
        }
    }

    public void loadSocio(Pessoa p, boolean reativar) {
        loadSocio(p, reativar, null);
    }

    public void loadSocio(Pessoa p, boolean reativar, Integer tcase) {
        String url_temp = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png");
        if (new File(url_temp).exists()) {
            new File(url_temp).delete();
        }
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
        if (f.exists()) {
            f.delete();
        }
        SociosDao db = new SociosDao();
        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        Socios socio_pessoa = db.pesquisaSocioPorPessoa(p.getId());
        Dao dao = new Dao();
        // SE REATIVAR == FALSE NÃO CARREGAR SOCIO

        if (GenericaSessao.exists("pessoaEmpresaPesquisa")) {
            pessoaEmpresa = (PessoaEmpresa) GenericaSessao.getObject("pessoaEmpresaPesquisa", true);
        } else {
            pessoaEmpresa = null;
        }

        if (reativar == false) {
            descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
            servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            servicoPessoa.setPessoa(p);
            // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
            loadServicos();
            loadPessoaComplemento(servicoPessoa.getPessoa().getId());
            return;
        } else {
            socios = socio_pessoa;
        }

        if (socios.getMatriculaSocios().getTitular().getId() != servicoPessoa.getPessoa().getId()) {
            if (socios.getServicoPessoa().isAtivo() || reativar) {
                socios = db.pesquisaSocioPorPessoa(socios.getMatriculaSocios().getTitular().getId());
            } else {
                socios.setMatriculaSocios(new MatriculaSocios());
                Pessoa ps = socios.getServicoPessoa().getPessoa();
                ServicoPessoa sp = new ServicoPessoa();
                sp.setPessoa(ps);
                socios.setId(-1);
                socios.setNrViaCarteirinha(1);
                socios.setParentesco((Parentesco) dao.find(new Parentesco(), 1));
                sp.setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
                socios.setServicoPessoa(sp);
                servicoPessoa.setEmissao(DataHoje.data());
                socios.getMatriculaSocios().setEmissao(DataHoje.data());
            }
        }

        servicoPessoa = socios.getServicoPessoa();
        descontoSocial = servicoPessoa.getDescontoSocial();

        //GrupoCategoria gpCat = dbca.pesquisaGrupoPorCategoria(socios.getMatriculaSocios().getCategoria().getId());
        loadGrupoCategoria();
        for (int i = 0; i < listaGrupoCategoria.size(); i++) {
            if (Integer.parseInt(listaGrupoCategoria.get(i).getDescription()) == socios.getMatriculaSocios().getCategoria().getGrupoCategoria().getId()) {
                //  if (Integer.parseInt((String) getListaGrupoCategoria().get(i).getDescription()) == gpCat.getId()) {
                idGrupoCategoria = i;
                break;
            }
        }

        loadCategoria();
        //int qntCategoria = getListaCategoria().size();
        for (int i = 0; i < listaCategoria.size(); i++) {
            if (Integer.parseInt(listaCategoria.get(i).getDescription()) == socios.getMatriculaSocios().getCategoria().getId()) {
                idCategoria = i;
                break;
            }
        }
        // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
        loadServicos();
        loadPessoaComplemento(servicoPessoa.getPessoa().getId());

        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);
        List<SocioCarteirinha> listsc;

        if (modeloc != null) {
            listsc = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
            if (!listsc.isEmpty()) {
                socCarteirinha = listsc.get(0);
            } else {
                GenericaMensagem.warn("Ateção", "Sócio sem modelo de Carteirinha!");
            }
        }

        matriculaSocios = socios.getMatriculaSocios();

        listaFilial.clear();
        idFilial = 0;
        for (int i = 0; i < getListaFilial().size(); i++) {
            if (socios.getMatriculaSocios().getFilial() != null) {
                if (Integer.parseInt(listaFilial.get(i).getDescription()) == socios.getMatriculaSocios().getFilial().getId()) {
                    idFilial = i;
                    break;
                }
            }
        }

        if (servicoPessoa.getTipoDocumento().getId() == 2) {
            chkContaCobranca = true;
        } else {
            chkContaCobranca = false;
        }

        loadTipoDocumento();
        for (int i = 0; i < listaTipoDocumento.size(); i++) {
            if (Integer.parseInt((String) listaTipoDocumento.get(i).getDescription()) == servicoPessoa.getTipoDocumento().getId()) {
                idTipoDocumento = i;
                break;
            }
        }

        listaDescontoSocial.clear();
        getListaDescontoSocial();
        if (servicoPessoa.getDescontoSocial() != null) {
            for (int i = 0; i < listaDescontoSocial.size(); i++) {
                if (Integer.parseInt(listaDescontoSocial.get(i).getDescription()) == servicoPessoa.getDescontoSocial().getId()) {
                    index_desconto = i;
                    break;
                }
            }
        }

        atualizarListaDependenteAtivo();
        atualizarListaDependenteInativo();
        listSisPeriodos = new ArrayList();
        getListSisPeriodos();
        if (socios.getServicoPessoa().getPeriodoCobranca() != null) {
            idSisPeriodo = socios.getServicoPessoa().getPeriodoCobranca().getId();
        }
        loadBloqueio();
        loadListMovimentoTaxaMatricula();

        loadListaCredenciadores();
        for (int i = 0; i < listaCredenciadores.size(); i++) {
            if (socios.getMatriculaSocios().getCredenciador().getId() == Integer.parseInt(listaCredenciadores.get(i).getDescription())) {
                indexCredenciadores = i;
            }
        }

    }

    public void loadListMovimentoTaxaMatricula() {
        if (matriculaSocios.getId() != -1) {
            listMovimentosTaxaInscricao = new MovimentoDao().listaPorRotinaMatricula(120, matriculaSocios.getId());
            if (!listMovimentosTaxaInscricao.isEmpty()) {
                loteTaxaInscricao = listMovimentosTaxaInscricao.get(0).getLote();
            }
        }
    }

    public final void loadGrupoCategoria() {
        listaGrupoCategoria = new ArrayList();
        idGrupoCategoria = 0;
        List<GrupoCategoria> grupoCategorias = new CategoriaDao().pesquisaGrupoCategoriaOrdenada();
        if (!grupoCategorias.isEmpty()) {
            for (int i = 0; i < grupoCategorias.size(); i++) {
                listaGrupoCategoria.add(new SelectItem(i, grupoCategorias.get(i).getGrupoCategoria(), "" + grupoCategorias.get(i).getId()));
            }
        } else {
            listaGrupoCategoria.add(new SelectItem(0, "Nenhum Grupo Categoria Encontrado", "0"));
        }
    }

    public final void loadCategoria() {
        listaCategoria.clear();
        idCategoria = 0;
        if (!listaGrupoCategoria.isEmpty()) {
            CategoriaDao db = new CategoriaDao();
            List<Categoria> select = db.pesquisaCategoriaPorGrupo(Integer.parseInt(listaGrupoCategoria.get(idGrupoCategoria).getDescription()));
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaCategoria.add(new SelectItem(i, select.get(i).getCategoria(), Integer.toString(select.get(i).getId())));
                }
            } else {
                listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
            }
        } else {
            listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
        }
    }

    public final void loadTipoDocumento() {
        listaTipoDocumento.clear();
        idTipoDocumento = 0;
        List<FTipoDocumento> select = new ArrayList();
        Dao dao = new Dao();
        if (isChkContaCobranca()) {
            select.add((FTipoDocumento) dao.find(new FTipoDocumento(), 2));
        } else {
            select = dao.find("FTipoDocumento", new int[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23});
        }

        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                listaTipoDocumento.add(
                        new SelectItem(i,
                                select.get(i).getDescricao(),
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
        }
    }

    public void loadServicos() {
        listaServicos.clear();
        idServico = 0;
        if (!listaGrupoCategoria.isEmpty() && !listaCategoria.isEmpty()) {
            ServicoCategoriaDao db = new ServicoCategoriaDao();
            int id_categoria = Integer.parseInt(listaCategoria.get(idCategoria).getDescription());

            servicoCategoria = db.pesquisaPorParECat(1, id_categoria);

            if (servicoCategoria != null) {
                listaServicos.add(new SelectItem(0, servicoCategoria.getServicos().getDescricao(),
                        Integer.toString(servicoCategoria.getServicos().getId()))
                );
                valorServico = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
                calculoValor();
                calculoDesconto();
            } else {
                listaServicos.add(new SelectItem(0, "Nenhum Serviço Encontrado", "0"));
            }
        }
    }

    public void calculoDesconto() {
        String valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje()));

        if (Moeda.converteUS$(valorServico) == Moeda.converteUS$(valorx)) {
            servicoPessoa.setNrDescontoString("0.0");
        } else {
            double valorx_c = Moeda.subtracao(Moeda.converteUS$(valorx_cheio), Moeda.converteUS$(valorServico));
            valorx_c = Moeda.multiplicar(Moeda.divisao(valorx_c, Moeda.converteUS$(valorx_cheio)), 100);
            servicoPessoa.setNrDescontoString(Moeda.converteR$Double(valorx_c));
        }
    }

    public void calculoValor() {
        String valorx;
        if (servicoPessoa.getNrDesconto() == 0) {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        } else {
            double valorx_c = new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje());

            double calculo = Moeda.divisao(Moeda.multiplicar(servicoPessoa.getNrDesconto(), valorx_c), 100);
            valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, calculo));
        }

        valorServico = Moeda.converteR$(valorx);
    }

    public void calculoNovoDesconto() {
        String valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje()));

        if (Moeda.converteUS$(novoValorServico) == Moeda.converteUS$(valorx)) {
            novoDescontoSocial.setNrDescontoString("0.0");
        } else {
            double valorx_c = Moeda.subtracao(Moeda.converteUS$(valorx_cheio), Moeda.converteUS$(novoValorServico));
            valorx_c = Moeda.multiplicar(Moeda.divisao(valorx_c, Moeda.converteUS$(valorx_cheio)), 100);
            novoDescontoSocial.setNrDescontoString(Moeda.converteR$Double(valorx_c));
        }
    }

    public void calculoNovoValor() {
        String valorx;
        if (novoDescontoSocial.getNrDesconto() == 0) {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        } else {
            double valorx_c = new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje());

            double calculo = Moeda.divisao(Moeda.multiplicar(novoDescontoSocial.getNrDesconto(), valorx_c), 100);
            valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, calculo));
        }

        novoValorServico = Moeda.converteR$(valorx);
    }

    public void calculoAlterarDesconto() {
        String valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje()));

        if (Moeda.converteUS$(alteraValorServico) == Moeda.converteUS$(valorx)) {
            descontoSocial.setNrDescontoString("0.0");
        } else {
            double valorx_c = Moeda.subtracao(Moeda.converteUS$(valorx_cheio), Moeda.converteUS$(alteraValorServico));
            valorx_c = Moeda.multiplicar(Moeda.divisao(valorx_c, Moeda.converteUS$(valorx_cheio)), 100);
            descontoSocial.setNrDescontoString(Moeda.converteR$Double(valorx_c));
        }
    }

    public void calculoAlterarValor() {
        String valorx;
        if (descontoSocial.getNrDesconto() == 0) {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        } else {
            double valorx_c = new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje());

            double calculo = Moeda.divisao(Moeda.multiplicar(descontoSocial.getNrDesconto(), valorx_c), 100);
            valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, calculo));
        }

        alteraValorServico = Moeda.converteR$(valorx);
    }

    public void alterarDesconto() {
        Dao di = new Dao();

        di.openTransaction();
        if (descontoSocial.getId() == -1) {

        } else {
            if (!di.update(descontoSocial)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Nao foi possivel alterar esse desconto!");
                return;
            }

            servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            // aqui
            //String valorx = Moeda.converteR$Double( new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0) );
            //valorServico = Moeda.valorDoPercentual(valorx, servicoPessoa.getNrDescontoString());
            calculoValor();
            SociosDao db = new SociosDao();

            List<ServicoPessoa> lsp = db.listaServicoPessoaPorDescontoSocial(descontoSocial.getId(), null);

            for (ServicoPessoa sp : lsp) {
                sp.setNrDesconto(descontoSocial.getNrDesconto());
                if (!di.update(sp)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Nao foi possivel alterar esse Serviço Pessoa!");
                    return;
                }
            }

            di.commit();

            atualizarListaDependenteAtivo();
            atualizarListaDependenteInativo();
            PF.closeDialog("dlg_alterar_desconto");
        }
        listaDescontoSocial.clear();
        PF.update("formSocios");
    }

    public void salvarDesconto() {
        if (novoDescontoSocial.getDescricao().isEmpty() || novoDescontoSocial.getDescricao().length() < 3) {
            GenericaMensagem.warn("Atenção", "Digite um Nome do Desconto válido!");
            PF.update("form_desconto:i_panel_desconto");
            return;
        }

        Dao di = new Dao();

        di.openTransaction();
        if (novoDescontoSocial.getId() == -1) {
            if (!di.save(novoDescontoSocial)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Não foi possível salvar esse desconto!");
                PF.update("form_desconto:i_panel_desconto");
                return;
            }
            di.commit();
            servicoPessoa.setNrDesconto(novoDescontoSocial.getNrDesconto());
            descontoSocial = novoDescontoSocial;
            PF.closeDialog("dlg_desconto");
        }
        listaDescontoSocial.clear();

        getListaDescontoSocial();

        for (int i = 0; i < listaDescontoSocial.size(); i++) {
            if (Integer.valueOf(listaDescontoSocial.get(i).getDescription()) == descontoSocial.getId()) {
                index_desconto = i;
                break;
            }
        }
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), Integer.valueOf(listaDescontoSocial.get(index_desconto).getDescription()));
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());

        calculoValor();

        PF.update("formSocios:i_panel_servicos");
    }

    public void selecionarDesconto(DescontoSocial ds) {
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), Integer.valueOf(listaDescontoSocial.get(index_desconto).getDescription()));
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());

        calculoValor();
        PF.update("formSocios:i_panel_servicos");
    }

    public void clickSalvarDesconto() {
        if (!listaCategoria.isEmpty() && !listaCategoria.get(0).getDescription().equals("0")) {

            novoDescontoSocial = new DescontoSocial();
            novoDescontoSocial.setCategoria((Categoria) new Dao().find(new Categoria(), Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
            //ds.setNrDesconto(servicoPessoa.getNrDesconto());
            calculoNovoValor();

            PF.update("form_desconto:i_panel_desconto");
            PF.openDialog("dlg_desconto");
        } else {
            GenericaMensagem.error("Atenção", "Lista de Categoria VAZIA!");
        }
    }

    public void clickAlterarDesconto() {
        if (!listaCategoria.isEmpty() && !listaCategoria.get(0).getDescription().equals("0")) {
            descontoSocial = (DescontoSocial) new Dao().find(descontoSocial);
            //        descontoSocial.setCategoria((Categoria) new Dao().find(new Categoria(), Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
            //        descontoSocial.setNrDesconto(servicoPessoa.getNrDesconto());

            calculoAlterarValor();
            calculoAlterarDesconto();

            PF.update("form_desconto:i_panel_alterar_desconto");
            PF.openDialog("dlg_alterar_desconto");
        } else {
            GenericaMensagem.error("Atenção", "Lista de Categoria VAZIA!");
        }
    }

//    public void loadingImage() throws InterruptedException {
//        Thread.sleep(5000);
//        if (novoDependente.getId() != -1) {
//            salvarImagem();
//        }
//        // PF.closeDialog("dlg_loading_image");
//    }
//    public void upload(FileUploadEvent event) {
//        String fotoTempCaminho = "foto/" + getUsuario().getId();
//        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/perfil.png"));
//        if (f.exists()) {
//            boolean delete = f.delete();
//        } else {
//            fotoTempPerfil = "";
//        }
//        ConfiguracaoUpload cu = new ConfiguracaoUpload();
//        cu.setArquivo(event.getFile().getFileName());
//        cu.setDiretorio("temp/foto/" + getUsuario().getId() + "/" + novoDependente.getPessoa().getId());
//        cu.setArquivo("perfil.png");
//        cu.setSubstituir(true);
//        cu.setRenomear("perfil.png");
//        cu.setEvent(event);
//        if (Upload.enviar(cu, true)) {
//            fotoTempPerfil = "/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/" + novoDependente.getPessoa().getId() + "/perfil.png";
//            //fotoPerfil = "";
//            try {
//                loadingImage();
//            } catch (Exception e) {
//
//            }
//        } else {
//            fotoTempPerfil = "";
//            //fotoPerfil = "";
//        }
//        RequestContext.getCurrentInstance().update(":formSocios:tab_view:i_panel_dados");
//
//    }
    public String apagarImagem() {
        boolean sucesso = false;

        String fcaminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente() + "/imagens/pessoa/" + novoDependente.getPessoa().getId() + "/" + novoDependente.getFoto();
        if (new File((fcaminho + ".png")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".png"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".jpg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".jpg"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".jpeg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".jpeg"))) {
            sucesso = true;
        } else if (new File((fcaminho + ".gif")).exists() && FileUtils.deleteQuietly(new File(fcaminho + ".gif"))) {
            sucesso = true;
        }

        if (sucesso && novoDependente.getId() != -1) {
            novoDependente.setDtFoto(null);
            novoDependente.setFoto("");
            new Dao().update(novoDependente, true);
        }

        return null;
    }

    public void salvarImagem() {
//        if (!Diretorio.criar("Imagens/Fotos/")) {
//            return;
//        }
//        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/");
//        boolean error = false;
//        if (!fotoTempPerfil.equals("")) {
//            File des = new File(arquivo + "/" + novoDependente.getPessoa().getId() + ".png");
//            if (des.exists()) {
//                des.delete();
//            }
//            des = new File(arquivo + "/" + novoDependente.getPessoa().getId() + ".jpg");
//            if (des.exists()) {
//                des.delete();
//            }
//            des = new File(arquivo + "/" + novoDependente.getPessoa().getId() + ".png");
//            File src = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(fotoTempPerfil));
//            boolean rename = src.renameTo(des);
//            //fotoPerfil = "/Cliente/" + getCliente() + "/Imagens/Fotos/" + novoDependente.getPessoa().getId() + ".png";
//            File del = new File(fotoTempPerfil);
//            if (del.exists()) {
//                del.delete();
//            }
//            fotoTempPerfil = "";
//
//            if (!rename) {
//                error = true;
//            } else {
//                Dao dao = new Dao();
//                novoDependente.setDtFoto(DataHoje.dataHoje());
//                dao.update(novoDependente, true);
//            }
//        }
//        if (!error) {
//            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/foto/" + getUsuario().getId()));
//            boolean delete = f.delete();
//
//        }
//        ((PhotoCam) GenericaSessao.getObject("photoCamBean")).setFILE_PERMANENT("/Imagens/user_undefined.png");
        // GenericaMensagem.info("Sistema", "Foto atualizada com sucesso!");
    }

//    public String getFotoPerfilDependente() {
//        if (!fotoTempPerfil.isEmpty()) {
//            return fotoTempPerfil;
//        }
//
//        String caminhoTemp = "/Cliente/" + getCliente() + "/Imagens/Fotos/";
//        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(caminhoTemp);
//        for (String imagensTipo1 : imagensTipo) {
//            File f = new File(arquivo + "/" + novoDependente.getPessoa().getId() + "." + imagensTipo1);
//            if (f.exists()) {
//                return caminhoTemp + "/" + novoDependente.getPessoa().getId() + "." + imagensTipo1;
//            }
//        }
//
//        if (novoDependente.getSexo().equals("M")) {
//            return "/Imagens/user_male.png";
//        } else {
//            return "/Imagens/user_female.png";
//        }
//    }
//    public void capturar(CaptureEvent captureEvent) {
//        String fotoTempCaminho = "foto/" + getUsuario().getId();
//        if (PhotoCam.oncapture(captureEvent, "perfil", "" + novoDependente.getPessoa().getId(), true)) {
//            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/" + novoDependente.getPessoa().getId() + "/perfil.png"));
//            if (f.exists()) {
//                fotoTempPerfil = "/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/" + novoDependente.getPessoa().getId() + "/perfil.png";
//            } else {
//                fotoTempPerfil = "";
//            }
//        }
//        RequestContext.getCurrentInstance().execute("dgl_captura.hide();");
//        try {
//            // loadingImage();
//        } catch (Exception e) {
//
//        }
//        RequestContext.getCurrentInstance().update(":formSocios:tab_view:i_panel_dados");
//
//    }
//    public String getFotoTipTitular() {
//        String caminhoTemp = "/Cliente/" + getCliente() + "/Imagens/Fotos/";
//        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(caminhoTemp);
//        for (String imagensTipo1 : imagensTipo) {
//            File f = new File(arquivo + "/" + servicoPessoa.getPessoa().getId() + "." + imagensTipo1);
//            if (f.exists()) {
//                return caminhoTemp + "/" + servicoPessoa.getPessoa().getId() + "." + imagensTipo1;
//            }
//        }
//        if (servicoPessoa.getPessoa().getId() != -1) {
//            FisicaDB db = new FisicaDao();
//            Fisica fis = db.pesquisaFisicaPorPessoa(servicoPessoa.getPessoa().getId());
//            if (fis.getSexo().equals("M")) {
//                return "/Imagens/user_male.png";
//            } else {
//                return "/Imagens/user_female.png";
//            }
//        } else {
//            return "/Imagens/user_female.png";
//        }
//    }
    public void salvarData() {
        if (servicoPessoa.getId() != -1) {
            Dao dao = new Dao();
            dao.openTransaction();
            if (!dao.update(servicoPessoa)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível alterar dia de vencimento em Serviço Pessoa");
                return;
            }

            PessoaDao db = new PessoaDao();
            PessoaComplemento pc = db.pesquisaPessoaComplementoPorPessoa(servicoPessoa.getPessoa().getId());
            pc = (PessoaComplemento) dao.find(pc);
            pc.setNrDiaVencimento(servicoPessoa.getNrDiaVencimento());
            if (!dao.update(pc)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível alterar dia de vencimento em Pessoa Complemento");
                return;
            }

            GenericaMensagem.info("Sucesso", "Dia de Vencimento alterada!");
            dao.commit();
        }
    }

    public List<Fisica> listaPesquisaDependente(String query) {
        if (query.isEmpty()) {
            listaFisica.clear();
            return null;
        }
        query = query.trim();
        FisicaDao db = new FisicaDao();
        String como = "P";
        if (query.length() <= 2) {
            como = "I";
        }
        String in = "" + matriculaSocios.getTitular().getId();
        for (int i = 0; i < listDependentes.size(); i++) {
            in += "," + listDependentes.get(i).getFisica().getPessoa().getId();
        }
        for (int i = 0; i < listDependentesInativos.size(); i++) {
            in += "," + listDependentesInativos.get(i).getFisica().getPessoa().getId();
        }
        db.setNot_in(in);
        db.setLimit(1000);
        db.setIgnore(true);
        listaFisica = db.pesquisaPessoa(query, "nome", como);
        if (listaFisica.isEmpty()) {
            if (ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(query))) {
                db.setLimit(1);
                db.setIgnore(true);
                listaFisica = db.pesquisaPessoa(query, "cpf", "");
            } else if (DataHoje.isDataValida(query)) {
                db.setIgnore(true);
                db.setLimit(100);
                listaFisica = db.pesquisaPessoa(query, "nascimento", "");
            }
            if (listaFisica.isEmpty()) {
                db.setIgnore(true);
                db.setLimit(2);
                listaFisica = db.pesquisaPessoa(query, "rg", "");
            }
        }
        return listaFisica;
    }

    public void selectDependente() {
        if (fisicaPesquisa == null) {
        } else {
            // MENSAGEM SE POSSUI DÉBITOS
            FunctionsDao functionsDao = new FunctionsDao();
            if (functionsDao.inadimplente(fisicaPesquisa.getPessoa().getId())) {
                // PF.closeDialog("dlg_dependente");
                PF.openDialog("i_dlg_c");
                GenericaMensagem.warn("Sistema", "EXISTE(m) DÉBITO(s) PARA ESTE DEPENDENTE!");
                PF.update("formSocios:i_msg_1");
                return;
            }
            PF.update("formSocios:i_panel_pesquisa");
            PF.update("formSocios:i_panel_dados");
            novoDependente = fisicaPesquisa;
        }

        loadNaturalidadeDependente();
    }

    public void inativarSocio() {
        if (socios.getId() != -1) {
            Dao dao = new Dao();
            SociosDao db = new SociosDao();
            if (dataInativacao.length() < 10) {
                GenericaMensagem.warn("Erro", "Data de inativação inválida!");
                return;
            }

            if (DataHoje.converteDataParaInteger(dataInativacao) > DataHoje.converteDataParaInteger(DataHoje.data())) {
                GenericaMensagem.warn("Erro", "Data de inativação não pode ser maior que dia de hoje!");
                return;
            }

            SMotivoInativacao smi = (SMotivoInativacao) dao.find(new SMotivoInativacao(), Integer.parseInt(listaMotivoInativacao.get(idInativacao).getDescription()));
            if (smi.getId() == 6 && (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3)) {
                if (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3) {
                    GenericaMensagem.error("Atenção", "Digite um Motivo de Inativação válido!");
                    PF.update("formSocios:i_msg_motivo");
                    return;
                }
            }

            NovoLog novoLog = new NovoLog();
            novoLog.startList();
            dao.openTransaction();

            ServicoPessoa sp = (ServicoPessoa) dao.find(new ServicoPessoa(), servicoPessoa.getId());
            sp.setAtivo(false);
            List<Socios> listaDps = db.listaDependentes(matriculaSocios.getId());
            if (!dao.update(sp)) {
                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                dao.rollback();
                return;
            }
            servicoPessoa = sp;
            for (int i = 0; i < listaDps.size(); i++) {
                ServicoPessoa sp2 = (ServicoPessoa) dao.find(new ServicoPessoa(), listaDps.get(i).getServicoPessoa().getId());
                sp2.setAtivo(false);
                sp2.setInativacao(dataInativacao);
                sp2.setMotivoInativacao(smi.getDescricao() + " " + matriculaSocios.getMotivo());
                if (!dao.update(sp2)) {
                    GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                    dao.rollback();
                    return;
                }
                novoLog = new NovoLog();
                novoLog.setTabela("fin_servico_pessoa");
                novoLog.setCodigo(sp2.getId());
                novoLog.update("Dependente inativado",
                        " Serviço Pessoa: (" + sp2.getId() + ") "
                        + " - Pessoa: (" + sp2.getPessoa().getId() + ") - " + sp2.getPessoa().getNome()
                        + " - Titular: (" + matriculaSocios.getTitular().getId() + ") - " + matriculaSocios.getTitular().getNome()
                        + " - Matrícula: " + matriculaSocios.getNrMatricula()
                        + " - Categoria: " + matriculaSocios.getCategoria().getCategoria()
                        + " - Parentesco: " + listaDps.get(i).getParentesco().getParentesco()
                        + " - Motivo Inativação: " + smi.getDescricao() + " " + matriculaSocios.getMotivo()
                );
                sp2 = new ServicoPessoa();
            }

            matriculaSocios.setMotivoInativacao(smi);
            matriculaSocios.setInativo(dataInativacao);
            if (!dao.update(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao alterar matrícula");
                dao.rollback();
                return;
            }

            GenericaMensagem.info("Concluído", "Sócio inativado com Sucesso!");
            dao.commit();
            FisicaDao dbf = new FisicaDao();
            GenericaSessao.put("fisicaBean", new FisicaBean());
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(dbf.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
            //((FisicaBean) GenericaSessao.getObject("fisicaBean")).showImagemFisica();
            //((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaSocioInativo().clear();
            PF.update("formSocios");
            listDependentes.clear();
            listDependentesInativos.clear();
            //listaDependentes.clear();
            //listaDependentesInativos.clear();
            atualizarListaDependenteAtivo();
            atualizarListaDependenteInativo();
            novoLog = new NovoLog();
            novoLog.setTabela("matr_socios");
            novoLog.setCodigo(matriculaSocios.getId());
            novoLog.update("Inativação de Sócio - Motivo: " + matriculaSocios.getMotivo(),
                    " Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                    + " - Sócio {ID: " + socios.getId() + "}"
                    + " - Titular {ID: " + socios.getMatriculaSocios().getTitular().getId() + " - Nome: " + socios.getMatriculaSocios().getTitular().getNome() + "}"
                    + " - Data da inativação: " + dataInativacao
            );
            novoLog.saveList();
        } else {
            GenericaMensagem.warn("Erro", "Não existe sócio para ser inativado!");
        }
    }

    public void validaMotivoInativacao() {
        SMotivoInativacao smi = (SMotivoInativacao) new Dao().find(new SMotivoInativacao(), Integer.parseInt(listaMotivoInativacao.get(idInativacao).getDescription()));

        if (smi.getId() == 6 && (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3)) {
            PF.openDialog("i_dlg_smi");
        } else {
            matriculaSocios.setMotivo("");
            inativarSocio();
        }
    }

    public String reativarSocio() {
        Dao dao = new Dao();
        SociosDao db = new SociosDao();

        if (db.pesquisaSocioPorPessoaAtivo(socios.getServicoPessoa().getPessoa().getId()).getId() != -1) {
            GenericaMensagem.warn("Erro", "Este sócio já esta cadastrado!");
            return null;
        }

        dao.openTransaction();

        ServicoPessoa sp = (ServicoPessoa) dao.find(new ServicoPessoa(), servicoPessoa.getId());
        if (sp == null) {
            GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa");
            dao.rollback();
            return null;
        }
        sp.setAtivo(true);
        if (!dao.update(sp)) {
            GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa");
            dao.rollback();
            return null;
        }

        servicoPessoa = sp;
//        List<Socios> listaDps = db.listaDependentesInativos(matriculaSocios.getId());
//        for (int i = 0; i < listaDps.size(); i++) {
//            ServicoPessoa sp2 = (ServicoPessoa) dao.pesquisaCodigo(listaDps.get(i).getServicoPessoa().getId(), "ServicoPessoa");
//            sp2.setAtivo(true);
//            if (!dao.update(sp2)) {
//                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa do Dependente");
//                dao.rollback();
//                return null;
//            }
//            sp2 = new ServicoPessoa();
//        }
        matriculaSocios.setMotivoInativacao(null);
        matriculaSocios.setDtInativo(null);
        if (!dao.update(matriculaSocios)) {
            GenericaMensagem.error("Erro", "Erro ao ativar matrícula");
            dao.rollback();
            return null;
        }
        GenericaMensagem.info("Concluído", "Sócio ativado com Sucesso!");
        dataInativacao = DataHoje.data();

        dao.commit();
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("matr_socios");
        novoLog.setCodigo(matriculaSocios.getId());
        novoLog.update("Reativação de sócio",
                " Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                + " - Sócio {ID: " + socios.getId() + "}"
                + " - Titular {ID: " + socios.getMatriculaSocios().getTitular().getId() + " - Nome: " + socios.getMatriculaSocios().getTitular().getNome() + "}"
                + " - Data da reativação: " + DataHoje.data()
        );
        FisicaDao dbf = new FisicaDao();
        GenericaSessao.put("fisicaBean", new FisicaBean());
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(dbf.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        //((FisicaBean) GenericaSessao.getObject("fisicaBean")).showImagemFisica();
        //((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaSocioInativo().clear();
        PF.update("formSocios");
        listDependentes.clear();
        listDependentesInativos.clear();
        // listaDependentes.clear();
        //listaDependentesInativos.clear();
        atualizarListaDependenteAtivo();
        atualizarListaDependenteInativo();
        return null;
    }

    public String salvar() {
        if (!validaSalvar()) {
            return null;
        }
        Boolean save = false;
        if (!socios.getMatriculaSocios().getCategoria().getBloqueiaMeses()) {
            getListSisPeriodos();
        }
        Dao dao = new Dao();
        MatriculaSocios msMemoria = new MatriculaSocios();
        dao.openTransaction();
        try {
            servicoPessoa.setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), Integer.parseInt(getListaTipoDocumento().get(idTipoDocumento).getDescription())));
        } catch (NumberFormatException e) {
            servicoPessoa.setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), Integer.parseInt(getListaTipoDocumento().get(0).getDescription())));
        }
        // NOVO REGISTRO -----------------------
        if (socios.getMatriculaSocios().getCategoria().getBloqueiaMeses()) {
            if (listSisPeriodos.isEmpty()) {
                servicoPessoa.setPeriodoCobranca((Periodo) dao.find(new Periodo(), 3));
            } else {
                servicoPessoa.setPeriodoCobranca((Periodo) dao.find(new Periodo(), idSisPeriodo));
            }
        } else {
            servicoPessoa.setPeriodoCobranca((Periodo) dao.find(new Periodo(), 3));
        }
        if (servicoPessoa.getId() == -1) {
            servicoPessoa.setAtivo(true);
            servicoPessoa.setCobranca(servicoPessoa.getPessoa());

            // set desconto 
            if (descontoSocial.getId() != 1) {
                servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            }

            servicoPessoa.setDescontoSocial(descontoSocial);

            if (!dao.save(servicoPessoa)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Serviço Pessoa!");
                return null;
            }
            matriculaSocios.setMotivoInativacao(null);
            save = true;

        } else {
            // ATUALIZA REGISTRO -------------------

            // set desconto
            if (descontoSocial.getId() != 1) {
                servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            }

            servicoPessoa.setDescontoSocial(descontoSocial);

            if (!dao.update(servicoPessoa)) {
                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                return null;
            }
        }
        GrupoCategoria grupoCategoria = (GrupoCategoria) dao.find(new GrupoCategoria(), Integer.parseInt(getListaGrupoCategoria().get(idGrupoCategoria).getDescription()));
//        if(matriculaSocios.getNrMatricula() <= grupoCategoria.getNrProximaMatricula()) {
//            msgConfirma = "Número de matrícula deve ser menor ou igual!";
//            return null;
//        }
        MatriculaSociosDao dbMat = new MatriculaSociosDao();
        ValidadeCartaoDao validadeCartaoDao = new ValidadeCartaoDao();

        matriculaSocios.setCategoria(servicoCategoria.getCategoria());
        matriculaSocios.setTitular(servicoPessoa.getPessoa());
        if (matriculaSocios.getNrMatricula()
                <= 0) {
            // MATRICULA MENOR QUE ZERO 
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula()
                > grupoCategoria.getNrProximaMatricula()) {
            // MATRICULA MAIOR QUE A PROXIMA DA CATEGORIA 
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula()
                < grupoCategoria.getNrProximaMatricula()
                && // MATRICULA MENOR QUE A PROXIMA DA CATEGORIA E NÃO EXISTIR UMA IGUAL 
                dbMat.pesquisaPorNrMatricula(matriculaSocios.getNrMatricula(), servicoCategoria.getCategoria().getId()) != null) {
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula()
                < grupoCategoria.getNrProximaMatricula()
                && dbMat.pesquisaPorNrMatricula(matriculaSocios.getNrMatricula(), servicoCategoria.getCategoria().getId()) == null) {
            // MATRICULA MENOR QUE A PROXIMA DA CATEGORIA E NÃO EXISTIR
            //////////////////////////////////// NAO FAZ NADA
        }
        if (matriculaSocios.getId()
                != -1) {
            msMemoria = (MatriculaSocios) dao.find(new MatriculaSocios(), matriculaSocios.getId());
        }

        if (msMemoria.getNrMatricula()
                != matriculaSocios.getNrMatricula() || matriculaSocios.getNrMatricula() == 0) {
            List list = dbMat.listaMatriculaPorGrupoNrMatricula(matriculaSocios.getCategoria().getGrupoCategoria().getId(), matriculaSocios.getNrMatricula());
            if (!list.isEmpty()) {
                GenericaMensagem.error("Erro", "Matrícula já existe!");
                dao.rollback();
                return null;
            }
        }

        matriculaSocios.setCredenciador((Credenciadores) dao.find(new Credenciadores(), Integer.parseInt(listaCredenciadores.get(indexCredenciadores).getDescription())));

        if (matriculaSocios.getId()
                == -1) {
            if (MacFilial.getAcessoFilial().getId() == -1) {
                matriculaSocios.setFilial((Filial) new Dao().find(new Filial(), 1));
            } else {
                matriculaSocios.setFilial((Filial) new Dao().find(new Filial(), Integer.parseInt(listaFilial.get(idFilial).getDescription())));
            }
            if (!dao.save(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao Salvar Matrícula!");
                dao.rollback();
                return null;
            }
        } else {
            matriculaSocios.setFilial((Filial) new Dao().find(new Filial(), Integer.parseInt(listaFilial.get(idFilial).getDescription())));
            if (!dao.update(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao Atualizar Matrícula!");
                dao.rollback();
                return null;
            }
        }

        if (!dao.update(grupoCategoria)) {
            GenericaMensagem.error("Erro", "Ao atualizar Grupo Categoria!");
            dao.rollback();
            return null;
        }

        socios.setMatriculaSocios(matriculaSocios);

        socios.setParentesco(
                (Parentesco) dao.find(new Parentesco(), 1));
        socios.setServicoPessoa(servicoPessoa);

        socios.setNrViaCarteirinha(
                1);

        DataHoje dh = new DataHoje();
        SociosDao db = new SociosDao();
        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), 170);

        if (modeloc
                == null) {
            GenericaMensagem.error("Erro", "Não existe modelo de carteirinha para esta categoria " + listaCategoria.get(idCategoria).getLabel() + " do sócio!");
            dao.rollback();
            return null;
        }

        List<SocioCarteirinha> list_carteirinha_socio = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        // VERIFICA SE SÓCIO TEM CARTEIRINHA -- SE TIVER NÃO ADICIONAR --

        if (list_carteirinha_socio.isEmpty()) {
            //Date validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(grupoCategoria.getNrValidadeMesCartao(), DataHoje.data()));
            ValidadeCartao vc = validadeCartaoDao.findByCategoriaParentesco(Integer.parseInt(listaCategoria.get(idCategoria).getDescription()), socios.getParentesco().getId());
            if (vc == null) {
                GenericaMensagem.error("Erro", "Validade cartão inexistente!");
                dao.rollback();
                return null;
            }
            String validadeCarteirinha;
            if (vc.getDtValidadeFixa() == null) {
                validadeCarteirinha = dh.incrementarMeses(vc.getNrValidadeMeses(), DataHoje.data());
            } else {
                validadeCarteirinha = vc.getValidadeFixaString();
            }

            SocioCarteirinha sc = new SocioCarteirinha(-1, "", servicoPessoa.getPessoa(), modeloc, null, 1, validadeCarteirinha, true);

            if ((socios.getMatriculaSocios().getCategoria().isCartaoTitular() && socios.getParentesco().getId() == 1)
                    || (socios.getMatriculaSocios().getCategoria().isCartaoDependente() && socios.getParentesco().getId() != 1)) {
                sc.setAtivo(true);

            } else {
                sc.setAtivo(false);
            }
            if (!dao.save(sc)) {
                GenericaMensagem.error("Erro", "Não foi possivel salvar Socio Carteirinha!");
                dao.rollback();
                return null;
            }
            if (!dao.update(sc)) {

                GenericaMensagem.error("Erro", "Não foi possivel atualizar Sócio Carteirinha!");
                dao.rollback();
                return null;
            }
            socCarteirinha = sc;

        }

        // 0 = Save / 1 - Update
        Socios s;
        NovoLog novoLog = new NovoLog();

        novoLog.startList();
        String beforeUpdate = "";
        String saveString = "ID: " + socios.getId()
                + " - Titular (ID: " + socios.getMatriculaSocios().getTitular().getId() + " - Nome: " + socios.getMatriculaSocios().getTitular() + ") "
                + " - Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                + " - Categoria: " + socios.getMatriculaSocios().getCategoria().getCategoria()
                + " - Filiação: " + socios.getMatriculaSocios().getEmissao()
                + " - Serviço Pessoa (Desconto em folha: " + socios.getServicoPessoa().isDescontoFolha() + " - Dia de Vencimento: " + socios.getServicoPessoa().getNrDiaVencimento() + ") "
                + " - Credenciador " + socios.getMatriculaSocios().getCredenciador().getPessoa().getNome();

        if (socios.getId()
                == -1) {
            novoLog.save(saveString);
            if (!dao.save(socios)) {
                GenericaMensagem.error("Erro", "Erro ao Salvar Sócio!");
                dao.rollback();
                return null;
            }

            if (matriculaSocios.getCategoria().getServicoTaxaMatricula() != null) {
                listMovimentosTaxaInscricao = new ArrayList();
                CondicaoPagamento cp = nrParcerlasTxInscricao == 1 ? (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1) : (CondicaoPagamento) dao.find(new CondicaoPagamento(), 2);

                loteTaxaInscricao = new Lote(
                        -1,
                        (Rotina) new Dao().find(new Rotina().get()),
                        "R",
                        DataHoje.data(),
                        matriculaSocios.getTitular(),
                        matriculaSocios.getCategoria().getServicoTaxaMatricula().getPlano5(),
                        false,
                        "",
                        valorTaxaInscricao,
                        (Filial) new Dao().find(new Filial(), 1),
                        matriculaSocios.getCategoria().getServicoTaxaMatricula().getDepartamento(),
                        null,
                        "",
                        (FTipoDocumento) dao.find(new FTipoDocumento(), 13),
                        cp,
                        (FStatus) new Dao().find(new FStatus(), 1),
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

                if (!dao.save(loteTaxaInscricao)) {
                    dao.rollback();
                    GenericaMensagem.error("Error", "Nao foi possivel salvar Lote!");
                    return null;
                }

                String vencimento = "";
                Double vlParcelaTx = valorTaxaInscricao / nrParcerlasTxInscricao;
                for (int i = 0; i < nrParcerlasTxInscricao; i++) {
                    if (i == 0) {
                        vencimento = DataHoje.data();
                    } else {
                        vencimento = new DataHoje().incrementarMeses(1, vencimento);
                    }
                    Movimento m = new Movimento(
                            -1,
                            loteTaxaInscricao,
                            loteTaxaInscricao.getPlano5(),
                            matriculaSocios.getTitular(),
                            matriculaSocios.getCategoria().getServicoTaxaMatricula(),
                            null, // ID_BAIXA
                            (TipoServico) new Dao().find(new TipoServico(), 1),
                            null,
                            vlParcelaTx,
                            DataHoje.converteDataParaReferencia(vencimento),
                            vencimento,
                            1,
                            true,
                            "E",
                            false,
                            matriculaSocios.getTitular(),
                            matriculaSocios.getTitular(),
                            "",
                            "",
                            vencimento,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            (FTipoDocumento) new Dao().find(new FTipoDocumento(), 13),
                            0,
                            matriculaSocios
                    );
                    if (i == 0) {
                        vencimento = DataHoje.alterDay(servicoPessoa.getNrDiaVencimento(), DataHoje.data());
                    }
                    if (!dao.save(m)) {
                        dao.rollback();
                        GenericaMensagem.error("Error", "Nao foi possivel salvar o movimento!");
                        return null;
                    }
                    listMovimentosTaxaInscricao.add(m);
                }

            }

            GenericaMensagem.info("Sucesso", "Pessoa Associada!");

        } else {
            if (!dao.update(socios)) {
                GenericaMensagem.error("Erro", "Erro ao Atualizar Sócio!");
                dao.rollback();
                return null;
            }
            s = (Socios) new Dao().find(socios);
            beforeUpdate = "ID: " + s.getId()
                    + " - Titular (ID: " + s.getMatriculaSocios().getTitular().getId() + " - Nome: " + s.getMatriculaSocios().getTitular() + ") "
                    + " - Matrícula: " + s.getMatriculaSocios().getNrMatricula()
                    + " - Categoria: " + s.getMatriculaSocios().getCategoria().getCategoria()
                    + " - Filiação: " + s.getMatriculaSocios().getEmissao()
                    + " - Serviço Pessoa (Desconto em folha: " + s.getServicoPessoa().isDescontoFolha() + " - Dia de Vencimento: " + s.getServicoPessoa().getNrDiaVencimento() + ") "
                    + " - Credenciador " + socios.getMatriculaSocios().getCredenciador().getPessoa().getNome();

            GenericaMensagem.info("Sucesso", "Cadastro Atualizado!");
            novoLog.update(beforeUpdate, saveString);
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        }

        /* 
         // SE TIVER UMA LISTA COM DEPENDENTES
         *
         *
         */
        if (!listDependentes.isEmpty()) {

            SocioCarteirinhaDao socioCarteirinhaDao = new SocioCarteirinhaDao();

            ServicoCategoriaDao dbSCat = new ServicoCategoriaDao();
            SocioCarteirinha sc;
            SociosDao sociosDao = new SociosDao();
            ParentescoDao dbPar = new ParentescoDao();
            boolean message = true;
            for (int i = 0; i < listDependentes.size(); i++) {
                if (listDependentes.get(i).getFisica().getId() != -1) {
                    Socios socioDependente = db.pesquisaSocioPorPessoaAtivo(listDependentes.get(i).getFisica().getPessoa().getId());
                    Fisica fisicaDependente = listDependentes.get(i).getFisica();

                    List<SelectItem> lista_si = (ArrayList<SelectItem>) listDependentes.get(i).getListParentesco();
                    Parentesco parentesco = (Parentesco) dao.find(new Parentesco(), Integer.valueOf(lista_si.get(Integer.valueOf(Integer.toString(listDependentes.get(i).getIdParentesco()))).getDescription()));
                    sc = socioCarteirinhaDao.pesquisaPorPessoaModelo(fisicaDependente.getPessoa().getId(), modeloc.getId());
                    ValidadeCartao vc = validadeCartaoDao.findByCategoriaParentesco(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), parentesco.getId());
                    if (vc == null) {
                        GenericaMensagem.warn("Validação", "Validade cartão não cadastrada!");
                        return null;
                    }
                    String validadeCarteirinha;
                    if (vc.getDtValidadeFixa() == null) {
                        validadeCarteirinha = dh.incrementarMeses(vc.getNrValidadeMeses(), DataHoje.data());
                    } else {
                        validadeCarteirinha = vc.getValidadeFixaString();
                    }
//                    if (GenericaSessao.getString("sessaoCliente").equals("ServidoresRP")) {
//                        Integer verificaHoje = DataHoje.converteDataParaInteger(DataHoje.data());
//                        Integer verificaFuturo = DataHoje.converteDataParaInteger("30/06/2016");
//                        if (verificaFuturo < verificaHoje) {
//                            GenericaMensagem.warn("Atenção", "Entrar em contato com nosso suporte técnico!");
//                            return null;
//                        }
//                        if (!(parentesco.getParentesco().toUpperCase()).equals("TITULAR")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("ESPOSA")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("ESPOSO")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("SOGRA")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("SOGRO")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("PAI")
//                                && !(parentesco.getParentesco().toUpperCase()).equals("MÃE")) {
//                            validadeCarteirinha = ("30/06/2016");
//                            if (message) {
//                                GenericaMensagem.warn("Atenção", "Esta data de validade é provisória e este critério será mantido até o dia 30/06/2016, conforme solicitação do cliente, exceto para os graus de parentesco titular, esposa, sogra e paes.");
//                                message = false;
//
//                            }
//                        }
//                    }

                    ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(parentesco.getId(), servicoCategoria.getCategoria().getId());

                    String ref_dependente = "";
                    if (listDependentes.get(i).getValidadeDependente() != null && !listDependentes.get(i).getValidadeDependente().isEmpty()) {
                        ref_dependente = listDependentes.get(i).getValidadeDependente();
                    }
                    if (servicoCategoriaDep == null) {
                        GenericaMensagem.warn("Erro", "Erro para Serviço Categoria: " + listDependentes.get(i).getFisica().getPessoa().getNome());
                        dao.rollback();
                        return null;
                    }

                    modeloc = dbc.pesquisaModeloCarteirinha(matriculaSocios.getCategoria().getId(), 170);

                    List<SocioCarteirinha> list_carteirinha_dep = new ArrayList();
                    if (modeloc == null) {
                        list_carteirinha_dep = db.pesquisaCarteirinhasPorPessoa(socioDependente.getServicoPessoa().getPessoa().getId(), modeloc.getId());
                        //GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
                        //dao.rollback();
                        //return null;
                    }
                    // VERIFICA SE SÓCIO DEPENDENTE TEM CARTEIRINHA -- SE TIVER NÃO ADICIONAR --

                    if (list_carteirinha_dep.isEmpty() && registro.isCarteirinhaDependente()) {

                        if (modeloc == null) {
                            GenericaMensagem.error("Erro", "Não existe modelo de carteirinha para categoria " + servicoCategoriaDep.getCategoria().getCategoria() + " do dependente " + socioDependente.getServicoPessoa().getPessoa().getNome());
                            dao.rollback();
                            return null;
                        }
                        listDependentes.get(i).setValidadeCarteirinha(validadeCarteirinha);
                    }

                    if (socioDependente.getId() == -1) {
                        ServicoPessoa servicoPessoaDependente = new ServicoPessoa(-1,
                                servicoPessoa.getEmissao(),
                                fisicaDependente.getPessoa(),
                                servicoPessoa.isDescontoFolha(),
                                servicoCategoriaDep.getServicos(),
                                Moeda.substituiVirgulaDouble(listDependentes.get(i).getDescontoString()),
                                servicoPessoa.getReferenciaVigoracao(),
                                ref_dependente,
                                servicoPessoa.getNrDiaVencimento(),
                                servicoPessoa.getTipoDocumento(),
                                servicoPessoa.getCobranca(),
                                servicoPessoa.isAtivo(),
                                servicoPessoa.isBanco(),
                                0,
                                servicoPessoa.getDescontoSocial(),
                                null,
                                null,
                                null,
                                null,
                                "",
                                servicoPessoa.getPeriodoCobranca()
                        );
                        if (!dao.save(servicoPessoaDependente)) {
                            GenericaMensagem.warn("Erro", "Erro ao salvar Serviço Pessoa: " + listDependentes.get(i).getFisica().getPessoa().getNome());
                            dao.rollback();
                            return null;
                        }
                        socioDependente = new Socios(-1,
                                matriculaSocios,
                                servicoPessoaDependente,
                                parentesco,
                                listDependentes.get(i).getViaCarteirinha()
                        );

                        if (!dao.save(socioDependente)) {
                            GenericaMensagem.warn("Erro", "Erro ao salvar Sócio: " + listDependentes.get(i).getFisica().getPessoa().getNome());
                            dao.rollback();
                            return null;
                        }
                        saveString = "Sócio Dependente - ID: " + socioDependente.getId()
                                + " - Pessoa - ID: " + socioDependente.getServicoPessoa().getPessoa().getId() + " - Nome: " + socioDependente.getServicoPessoa().getPessoa().getNome()
                                + " - Titular (ID: " + socioDependente.getMatriculaSocios().getTitular().getId() + " - Nome: " + socioDependente.getMatriculaSocios().getTitular() + ") "
                                + " - Parentesco : " + socioDependente.getParentesco().getParentesco()
                                + " - Serviço Pessoa (Ativo: " + socioDependente.getServicoPessoa().isAtivo() + " - Validade: " + socioDependente.getServicoPessoa().getReferenciaValidade() + ") ";
                        novoLog.save(saveString);
                    } else {
                        Socios sd = (Socios) dao.find(socioDependente);
                        beforeUpdate = "Sócio Dependente - ID: " + sd.getId()
                                + " - Pessoa - ID: " + sd.getServicoPessoa().getPessoa().getId() + " - Nome: " + sd.getServicoPessoa().getPessoa().getNome()
                                + " - Titular (ID: " + sd.getMatriculaSocios().getTitular().getId() + " - Nome: " + sd.getMatriculaSocios().getTitular() + ") "
                                + " - Parentesco : " + sd.getParentesco().getParentesco()
                                + " - Serviço Pessoa (Ativo: " + sd.getServicoPessoa().isAtivo() + " - Validade: " + sd.getServicoPessoa().getReferenciaValidade() + ") ";
                        //ServicoPessoa servicoPessoaDependente = dbSerP.pesquisaServicoPessoaPorPessoa(((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getId());
                        ServicoPessoa servicoPessoaDependente = (ServicoPessoa) dao.find(new ServicoPessoa(), socioDependente.getServicoPessoa().getId());

                        servicoPessoaDependente.setEmissao(servicoPessoa.getEmissao());
                        servicoPessoaDependente.setPessoa(fisicaDependente.getPessoa());
                        servicoPessoaDependente.setDescontoFolha(servicoPessoa.isDescontoFolha());
                        servicoPessoaDependente.setServicos(servicoCategoriaDep.getServicos());
                        servicoPessoaDependente.setNrDesconto(Moeda.substituiVirgulaDouble(listDependentes.get(i).getDescontoString()));
                        servicoPessoaDependente.setReferenciaVigoracao(servicoPessoa.getReferenciaVigoracao());
                        servicoPessoaDependente.setReferenciaValidade(ref_dependente);
                        servicoPessoaDependente.setNrDiaVencimento(servicoPessoa.getNrDiaVencimento());
                        servicoPessoaDependente.setTipoDocumento(servicoPessoa.getTipoDocumento());
                        servicoPessoaDependente.setCobranca(servicoPessoa.getCobranca());
                        servicoPessoaDependente.setAtivo(servicoPessoa.isAtivo());
                        servicoPessoaDependente.setBanco(servicoPessoa.isBanco());
                        servicoPessoaDependente.setPeriodoCobranca(servicoPessoa.getPeriodoCobranca());
                        if (!dao.update(servicoPessoaDependente)) {
                            GenericaMensagem.error("Erro", "Erro ao Alterar Serviço Pessoa: " + listDependentes.get(i).getFisica().getPessoa().getNome());
                            dao.rollback();
                            return null;
                        }
                        socioDependente.setServicoPessoa(servicoPessoaDependente);
                        socioDependente.setMatriculaSocios(matriculaSocios);
                        socioDependente.setNrViaCarteirinha(listDependentes.get(i).getViaCarteirinha());
                        socioDependente.setParentesco(parentesco);
                        if (!dao.update(socioDependente)) {
                            GenericaMensagem.error("Erro", "Erro ao Salvar Sócio: " + listDependentes.get(i).getFisica().getPessoa().getNome());
                            dao.rollback();
                            return null;
                        }
                        saveString = "Sócio Dependente - ID: " + socioDependente.getId()
                                + " - Pessoa - ID: " + +socioDependente.getServicoPessoa().getPessoa().getId() + " - Nome: " + socioDependente.getServicoPessoa().getPessoa().getNome()
                                + " - Titular (ID: " + socioDependente.getMatriculaSocios().getTitular().getId() + " - Nome: " + socioDependente.getMatriculaSocios().getTitular() + ") "
                                + " - Parentesco : " + socioDependente.getParentesco().getParentesco()
                                + " - Serviço Pessoa (Ativo: " + socioDependente.getServicoPessoa().isAtivo() + " - Validade: " + socioDependente.getServicoPessoa().getReferenciaValidade() + ") ";
                        novoLog.update(beforeUpdate, saveString);
                    }
                    // sc = socioCarteirinhaDao.pesquisaPorPessoaModelo(fisicaDependente.getPessoa().getId(), modeloc.getId());
                    if (sc == null) {
                        sc = new SocioCarteirinha(-1, "", fisicaDependente.getPessoa(), modeloc, null, 1, validadeCarteirinha, true);
                        if (socioDependente.getMatriculaSocios().getCategoria().isCartaoDependente() && socioDependente.getParentesco().getId() != 1) {
                            sc.setAtivo(true);
                        } else {
                            sc.setAtivo(false);
                        }
                        if (!dao.save(sc)) {
                            GenericaMensagem.error("Erro", "Não foi possivel salvar Sócio Carteirinha Dependente!");
                            dao.rollback();
                            return null;
                        }
                        sc.setCartao(sc.getId());
                        sc = (SocioCarteirinha) dao.find(sc);
                        if (!dao.update(sc)) {
                            GenericaMensagem.error("Erro", "Não foi possivel salvar Sócio Carteirinha Dependente!");
                            dao.rollback();
                            return null;
                        }
                        saveString = "Sócio Carteirinha: " + sc.getId()
                                + " - Sócio: (ID: " + socios.getId() + ")"
                                + " - Pessoa (ID: " + socios.getServicoPessoa().getId() + ")"
                                + " - Modelo: (ID: " + sc.getModeloCarteirinha().getId() + ")"
                                + " - Cartão: " + sc.getCartao()
                                + " - Emissão: " + sc.getEmissao()
                                + " - Validade: " + sc.getValidadeCarteirinha();

                        novoLog.save(saveString);
                    } else {
                        saveString = "Sócio Carteirinha: " + sc.getId()
                                + " - Sócio: (ID: " + socios.getId() + ")"
                                + " - Pessoa (ID: " + socios.getServicoPessoa().getId() + ")"
                                + " - Modelo: (ID: " + sc.getModeloCarteirinha().getId() + ")"
                                + " - Cartão: " + sc.getCartao()
                                + " - Emissão: " + sc.getEmissao()
                                + " - Validade: " + sc.getValidadeCarteirinha();
                        if (socioDependente.getMatriculaSocios().getCategoria().isCartaoDependente() && socioDependente.getParentesco().getId() != 1) {
                            sc.setAtivo(true);
                        } else {
                            sc.setAtivo(false);
                            sc.setValidadeCarteirinha(validadeCarteirinha);
                        }
                        beforeUpdate = "Sócio Carteirinha: " + sc.getId()
                                + " - Sócio: (ID: " + socios.getId() + ")"
                                + " - Pessoa (ID: " + socios.getServicoPessoa().getId() + ")"
                                + " - Modelo: (ID: " + sc.getModeloCarteirinha().getId() + ")"
                                + " - Cartão: " + sc.getCartao()
                                + " - Emissão: " + sc.getEmissao()
                                + " - Validade: " + sc.getValidadeCarteirinha();
                        if (!dao.update(sc)) {
                            GenericaMensagem.error("Erro", "Não foi possivel atualizar Sócio Carteirinha Dependente!");
                            dao.rollback();
                            return null;
                        }
                        novoLog.update(beforeUpdate, saveString);
                    }
                }
            }
            for (int i = 0; i < listDependentesInativos.size(); i++) {
                Socios socioDependenteInativo = sociosDao.pesquisaDependenteInativoPorMatricula(listDependentesInativos.get(i).getFisica().getPessoa().getId(), socios.getMatriculaSocios().getId());
                if (socioDependenteInativo != null) {
                    if (socioDependenteInativo.getServicoPessoa().isAtivo() != socios.getServicoPessoa().isDescontoFolha()) {
                        socioDependenteInativo.getServicoPessoa().setDescontoFolha(socios.getServicoPessoa().isDescontoFolha());
                        if (!dao.update(socioDependenteInativo)) {
                            GenericaMensagem.error("Erro", "Erro ao atualizar sócio: " + listDependentesInativos.get(i).getFisica().getPessoa().getNome());
                            dao.rollback();
                            break;
                        }
                    }
                }
            }
        }

        ((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).setSocios(socios);
        dao.commit();

        novoLog.saveList();

        atualizarListaDependenteAtivo();

        atualizarListaDependenteInativo();

        /*
        if (socios.getMatriculaSocios().getCategoria().getBloqueiaMeses()) {
            List<ServicoPessoaBloqueio> spbs = new ServicoPessoaBloqueioDao().findByServicoPessoa(socios.getServicoPessoa().getId());
            if (spbs.isEmpty()) {
                saveBloqueio();
            }
        } else if (!socios.getMatriculaSocios().getCategoria().getBloqueiaMeses()) {
            List<ServicoPessoaBloqueio> spbs = new ServicoPessoaBloqueioDao().findByServicoPessoa(socios.getServicoPessoa().getId());
            if (!spbs.isEmpty()) {
                for (int i = 0; i < spbs.size(); i++) {
                    new Dao().delete(spbs.get(i), true);
                }
                bloqueio = new Bloqueio();
            }
        }
         */
        return null;
    }

    public String editarTitular() {
        if (socios.getId() == -1) {
            GenericaSessao.put("linkClicado", true);
            return "pessoaFisica";
        }
        FisicaDao db = new FisicaDao();
        GenericaSessao.remove("photoCapture");
        GenericaSessao.put("fisicaBean", new FisicaBean());
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(db.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
        GenericaSessao.put("linkClicado", true);
        return "pessoaFisica";
    }

    public boolean validaSalvar() {

        if (servicoPessoa.getId() == -1) {
            if (servicoPessoa.getPessoa().getDocumento().isEmpty() || servicoPessoa.getPessoa().getDocumento().equals("0")) {
                ConfiguracaoSocial cs = ConfiguracaoSocial.get();
                if (cs.getBloqueiaCpf()) {
                    GenericaMensagem.warn("Erro", "Para ser titular é necessário ter número de documento (CPF) no cadastro!");
                    return false;
                }
            }
        }

        if (matriculaSocios.getEmissao().isEmpty()) {
            GenericaMensagem.warn("Erro", "Data de Emissão Inválida!");
            return false;
        }

        if (getListaGrupoCategoria().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Grupos Categoria Vazia!");
            return false;
        }

        if (getListaCategoria().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Categoria Vazia!");
            return false;
        }

        if (getListaTipoDocumento().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Tipo Documentos Vazia!");
            return false;
        }

        for (ListaDependentes ld : listDependentes) {
            if (ld.getFisica().getId() == -1) {
                GenericaMensagem.warn("Erro", "Pesquise um Dependente para Salvar!");
                return false;
            }
        }
        ServicoCategoriaDao dbSCat = new ServicoCategoriaDao();
        int idCat = Integer.parseInt(getListaCategoria().get(idCategoria).getDescription());
        servicoCategoria = dbSCat.pesquisaPorParECat(1, idCat);
        if (servicoCategoria == null) {
            GenericaMensagem.warn("Erro", "Não existe Serviço Categoria cadastrado!");
            return false;
        } else {
            servicoPessoa.setServicos(servicoCategoria.getServicos());
        }

        // SE DESCONTO FOLHA = true NAO SALVAR EM cobranca ID EMPRESA -- alterado na data 12/01/2014 (ID da tarefa 388)
        servicoPessoa.setCobranca(servicoPessoa.getPessoa());

        if (servicoPessoa.getId() == -1) {
            Fisica f = new FisicaDao().pesquisaFisicaPorPessoa(servicoPessoa.getPessoa().getId());
            if (servicoCategoria.getCategoria().isEmpresaObrigatoria() && f.getDtAposentadoria() == null && servicoCategoria.getCategoria().isVotante()) {
                JuridicaDao db = new JuridicaDao();
                if (pessoaEmpresa == null || pessoaEmpresa.getId() == -1) {
                    GenericaMensagem.warn("Atenção", "Vincular uma empresa para esta Pessoa!");
                    return false;
                }
                List listax = db.listaJuridicaContribuinte(pessoaEmpresa.getJuridica().getId());
                if (!listax.isEmpty()) {
                    for (int i = 0; i < listax.size(); i++) {
                        if (((List) listax.get(0)).get(9) != null) {
                            // CONTRIBUINTE INATIVO
                            GenericaMensagem.warn("Atenção", "Sócio com EMPRESA INATIVA não pode ser salvo!");
                            return false;
                        }
                    }
                } else {
                    GenericaMensagem.warn("Atenção", "Sócio com EMPRESA NÃO CONTRIBUINTE não pode ser salvo!");
                    return false;
                }
            }
        }

        SociosDao db = new SociosDao();
        if ((servicoPessoa.getId() == -1) && (db.pesquisaSocioPorPessoaAtivo(servicoPessoa.getPessoa().getId()).getId() != -1)) {
            GenericaMensagem.error("Erro", "Esta pessoa já um Sócio Cadastrado!");
            return false;
        }

        ServicoContaCobrancaDao dbSCB = new ServicoContaCobrancaDao();
        if (chkContaCobranca) {
            List l = dbSCB.pesquisaServPorIdServIdTipoServ(servicoCategoria.getServicos().getId(), 1);
            if (!l.isEmpty()) {
                servicoPessoa.setBanco(true);
            } else {
                servicoPessoa.setBanco(false);
                GenericaMensagem.error("Erro", "Não existe Serviço Conta Cobrança!");
                return false;
            }
        } else {
            servicoPessoa.setBanco(false);
        }
        return true;
    }

    /**
     *
     * @param f (Física)
     * @param tcase (0 - Novo cadastro / 1 - Alterar cadasrtro);
     * @return
     */
    public Boolean existDependente(Fisica f, Integer tcase) {
        if (tcase == 0) {
            for (ListaDependentes listDependente : listDependentes) {
                if (listDependente.getFisica().getPessoa().getId() == f.getPessoa().getId()) {
                    return true;
                }
            }
        } else if (tcase == 1) {
            Integer count = 0;
            for (ListaDependentes listDependente : listDependentes) {
                if (listDependente.getFisica().getPessoa().getId() == f.getPessoa().getId()) {
                    count++;
                }
            }
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    public void saveFisicaDependente() {
        saveFisicaDependente(0);
    }

    /**
     *
     * @param tcase (0 - Salva e adiciona como dependente / 1 - Apenas salvar o
     * cadastro da pessoa física);
     */
    public void saveFisicaDependente(Integer tcase) {
        if (matriculaSocios.getId() == -1) {
            if (servicoPessoa.getPessoa().getId() == novoDependente.getPessoa().getId()) {
                GenericaMensagem.warn("Validação", "O titular não pode ser dependente!");
                return;
            }
        } else if (matriculaSocios.getTitular().getId() == novoDependente.getPessoa().getId()) {
            GenericaMensagem.warn("Validação", "O titular não pode ser dependente!");
            return;
        }
        if (!validaSalvarDependente()) {
            return;
        }
        Dao dao = new Dao();

        novoDependente.getPessoa().setNome(novoDependente.getPessoa().getNome().trim());

        if (novoDependente.getId() == -1) {
            novoDependente.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            dao.openTransaction();
            if (!dao.save(novoDependente.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao salvar Pessoa!");
                dao.rollback();
                return;
            }

            if (!dao.save(novoDependente)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Cadastro!");
                dao.rollback();
                return;
            }
            dao.commit();
            NovoLog novoLog = new NovoLog();
            String saveString = "ID: " + novoDependente.getId()
                    + " - Pessoa (ID: " + novoDependente.getPessoa().getId() + " - Nome: " + novoDependente.getPessoa().getNome() + " - Documento: " + novoDependente.getPessoa().getDocumento() + ")"
                    + " - RG: " + novoDependente.getRg()
                    + " - Nascimento: " + novoDependente.getNascimento()
                    + " - Estado Cívil: " + novoDependente.getEstadoCivil();
            novoLog.save(saveString);
            if (existDependente(novoDependente, 0)) {
                GenericaMensagem.warn("Sucesso", "Dependente já cadastrado para esta matrícula!");
                return;
            }
            GenericaMensagem.info("Sucesso", "Dependente Salvo!");
        } else {
            FunctionsDao functionsDao = new FunctionsDao();
            if (functionsDao.inadimplente(novoDependente.getPessoa().getId())) {
                GenericaMensagem.warn("Sistema", "EXISTE(m) DÉBITO(s) PARA ESTE DEPENDENTE!");
                return;
            }
            dao.openTransaction();
            if (!dao.update(novoDependente.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Pessoa!");
                dao.rollback();
                return;
            }

            if (!dao.update(novoDependente)) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
                dao.rollback();
                return;
            }
            Fisica f = (Fisica) dao.find(novoDependente);
            dao.commit();
            GenericaMensagem.info("Sucesso", "Dependente Atualizado!");
            NovoLog novoLog = new NovoLog();
            String saveString = "ID: " + novoDependente.getId()
                    + " - Pessoa (ID: " + novoDependente.getPessoa().getId() + " - Nome: " + novoDependente.getPessoa().getNome() + " - Documento: " + novoDependente.getPessoa().getDocumento() + ")"
                    + " - RG: " + novoDependente.getRg()
                    + " - Nascimento: " + novoDependente.getNascimento()
                    + " - Estado Cívil: " + novoDependente.getEstadoCivil();
            GenericaMensagem.info("Sucesso", "Dependente Atualizado!");
            String beforeUpdate = "ID: " + f.getId()
                    + " - Pessoa (ID: " + f.getPessoa().getId() + " - Nome: " + f.getPessoa().getNome() + " - Documento: " + f.getPessoa().getDocumento() + ")"
                    + " - RG: " + f.getRg()
                    + " - Nascimento: " + f.getNascimento()
                    + " - Estado Cívil: " + f.getEstadoCivil();
            novoLog.update(beforeUpdate, saveString);
            if (existDependente(novoDependente, 1)) {
                GenericaMensagem.warn("Sucesso", "Dependente já cadastrado para esta matrícula!");
                return;
            }
        }

        //((DataObject) listaDependentes.get(index_dependente)).setArgumento0(novoDependente);
        listDependentes.get(index_dependente).setFisica(novoDependente);

        atualizaValidadeTela(index_dependente);
        salvarImagem();
        calculoValorDependente(listDependentes.get(index_dependente));
        modelVisible = false;
        index_dependente = 0;
        novoDependente = new Fisica();
        PF.update("formSocios");
        RequestContext.getCurrentInstance().execute("PF('dlg_dependente').hide()");
    }

    public void existePessoaNomeNascimento() {
        if (novoDependente.getId() == -1) {
            Fisica f = null;
            if (!novoDependente.getNascimento().isEmpty() && !novoDependente.getPessoa().getNome().isEmpty()) {
                FisicaDao db = new FisicaDao();
                f = db.pesquisaFisicaPorNomeNascimento(novoDependente.getPessoa().getNome(), novoDependente.getDtNascimento());
                if (f != null) {
                    novoDependente = f;
                    RequestContext.getCurrentInstance().update("formSocios");
                }
            }
            if (f == null || f.getId() == -1) {
                if (novoDependente.getId() == -1) {
                    if (!novoDependente.getPessoa().getNome().isEmpty()) {
                        listFisicaSugestao = new ArrayList();
                        listFisicaSugestao = new FisicaDao().findByNome(novoDependente.getPessoa().getNome());
                        if (!listFisicaSugestao.isEmpty()) {
                            PF.openDialog("dlg_sugestoes");
                            PF.update("formSocios:i_sugestoes");
                        }
                    }
                }
            }
        }
    }

    public boolean validaSalvarDependente() {
        FisicaDao db = new FisicaDao();
        if (novoDependente.getId() == -1) {
            if (!db.pesquisaFisicaPorNomeNascRG(novoDependente.getPessoa().getNome().trim(),
                    novoDependente.getDtNascimento(),
                    novoDependente.getRg()).isEmpty()) {
                GenericaMensagem.error("Erro", "Esta pessoa já esta Cadastrada!");
                return false;
            }

            if (novoDependente.getPessoa().getDocumento().isEmpty() || novoDependente.getPessoa().getDocumento().equals("0")) {
                novoDependente.getPessoa().setDocumento("0");
            } else {
                List listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    GenericaMensagem.error("Erro", "Documento já existente!");
                    return false;
                }
            }
        } else if (novoDependente.getPessoa().getDocumento().isEmpty() || novoDependente.getPessoa().getDocumento().equals("0")) {
            novoDependente.getPessoa().setDocumento("0");
        } else {

            List listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
            for (Object listDocumento1 : listDocumento) {
                if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != novoDependente.getId()) {
                    GenericaMensagem.error("Erro", "Documento já existente!");
                    return false;
                }
            }
        }

        if (novoDependente.getPessoa().getCriacao().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Data de Cadastro inválida!");
            return false;
        }

        if (novoDependente.getNascimento().isEmpty() || novoDependente.getNascimento().length() < 10) {
            GenericaMensagem.fatal("Validação", "Data de Nascimento inválida!");
            return false;
        }

        if (novoDependente.getPessoa().getNome().equals("")) {
            GenericaMensagem.error("Validação", "O campo nome não pode ser nulo!");
            return false;
        }

        if (!novoDependente.getPessoa().getDocumento().isEmpty() && !novoDependente.getPessoa().getDocumento().equals("0")) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(novoDependente.getPessoa().getDocumento()))) {
                GenericaMensagem.error("Validação", "Documento Inválido!");
                return false;
            }
        }

        for (ListaDependentes ld : listDependentesInativos) {
            if (ld.getFisica().getId() == novoDependente.getId()) {
                GenericaMensagem.warn("Validação", "Este dependente esta inativado nesta matrícula!");
                return false;
            }
        }

        SociosDao dbs = new SociosDao();
        Socios s = dbs.pesquisaSocioPorPessoaAtivo(novoDependente.getPessoa().getId());
        if (s.getId() != -1) {
            if (s.getServicoPessoa().isAtivo()) {
                if (s.getMatriculaSocios().getTitular().getId() == socios.getMatriculaSocios().getTitular().getId()) {
                    salvarImagem();
                } else {
                    GenericaMensagem.error("Validação", "Esta pessoa já é sócia em outra matrícula para o(a) titular " + s.getMatriculaSocios().getTitular().getNome());
                    return false;
                }
            }
        }
        List<Socios> list = dbs.listaPorPessoa(novoDependente.getPessoa().getId());
        Socios soc_dep = dbs.pesquisaSocioPorPessoaAtivo(novoDependente.getPessoa().getId());
        if (soc_dep.getId() != -1 && (soc_dep.getMatriculaSocios().getId() != socios.getMatriculaSocios().getId())) {
            for (int i = 0; i < list.size(); i++) {
                if (soc_dep.getMatriculaSocios().getNrMatricula() == list.get(i).getMatriculaSocios().getNrMatricula()) {
                    if (list.get(i).getServicoPessoa().isAtivo()) {
                        GenericaMensagem.error("Validação", "Esta pessoa já é sócia em outra matrícula para o(a) titular " + list.get(i).getMatriculaSocios().getTitular().getNome());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void pesquisaCPF() {
        if (novoDependente.getId() == -1) {
            if (!novoDependente.getPessoa().getDocumento().isEmpty() && !novoDependente.getPessoa().getDocumento().equals("___.___.___-__")) {
                FisicaDao db = new FisicaDao();
                List<Fisica> listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    novoDependente = listDocumento.get(0);
                } else if (novoDependente.getId() != -1) {
                    String doc = novoDependente.getPessoa().getDocumento();

                    novoDependente = new Fisica();

                    novoDependente.getPessoa().setDocumento(doc);
                }
            }
        }
    }

    public void adicionarDependente() {

//        if (getListaParentesco().size() == 1 && Integer.valueOf(listaParentesco.get(0).getDescription()) == 0){
//            GenericaMensagem.warn("Erro", "Nenhum Serviço adicionado para Dependentes!");
//            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
//            return;
//        }
        if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0) {
            GenericaMensagem.warn("Erro", "Nenhuma Categoria Encontrada!");
            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
            return;
        }

        Fisica fisica = new Fisica();
        DataHoje dh = new DataHoje();

        CategoriaDao dbCat = new CategoriaDao();

        Date validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(0, DataHoje.data()));
//        if (GenericaSessao.getString("sessaoCliente").equals("ServidoresRP")) {
//            Integer verificaHoje = DataHoje.converteDataParaInteger(DataHoje.data());
//            Integer verificaFuturo = DataHoje.converteDataParaInteger("30/06/2016");
//            validadeCarteirinha = null;
//            if (verificaFuturo < verificaHoje) {
//                GenericaMensagem.warn("Atenção", "Entrar em contato com nosso suporte técnico!");
//            }
//        }

        List<SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new SelectItem(0, "Selecione um Dependente", "0"));
        //ServicoCategoriaDB dbSCat = new ServicoCategoriaDao();
        //ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(Integer.valueOf(selectItems.get(0).getDescription()), servicoCategoria.getCategoria().getId());
        if (listDependentes.isEmpty()) {
            listDependentes.add(
                    new ListaDependentes(
                            fisica,
                            0,
                            1,
                            DataHoje.converteData(validadeCarteirinha),
                            null,
                            servicoPessoa.getNrDesconto(),
                            selectItems,
                            true,
                            (double) 0, // calculoValorGenerico(servicoPessoa.getNrDesconto(), fisica.getPessoa().getId(), servicoCategoriaDep.getServicos().getId(), servicoCategoriaDep.getCategoria().getId())
                            new ServicoPessoa()
                    )
            );
            fisica.getPessoa().setNome("");
        } else {
            for (int i = 0; i < listDependentes.size(); i++) {
                if (listDependentes.get(i).getFisica().getId() != -1 && (i - (listDependentes.size() - 1) == 0)) {
                    fisica.getPessoa().setNome("");
                    listDependentes.add(
                            new ListaDependentes(
                                    fisica,
                                    0,
                                    1,
                                    DataHoje.converteData(validadeCarteirinha),
                                    null,
                                    servicoPessoa.getNrDesconto(),
                                    selectItems,
                                    true,
                                    (double) 0, //calculoValorGenerico(servicoPessoa.getNrDesconto(), fisica.getPessoa().getId(), servicoCategoriaDep.getServicos().getId(), servicoCategoriaDep.getCategoria().getId()),
                                    new ServicoPessoa()
                            )
                    );
                    break;
                }
            }
        }
        dependente = new Fisica();
    }

    public String novoCadastroDependente() {
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png"));
        if (f.exists()) {
            f.delete();
        }
        novoDependente = new Fisica();
        fisicaPesquisa = new Fisica();
        loadNaturalidadeDependente();
        return null;
    }

    public void novoVoid() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sociosBean", new SociosBean());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaBean());
    }

    public String excluir() {
        if (!validaExcluir()) {
            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
            return null;
        }
        List<ServicoPessoaBloqueio> spbs = new ServicoPessoaBloqueioDao().findByServicoPessoa(socios.getServicoPessoa().getId());
        //Dao dao = new Dao();
        SociosDao db = new SociosDao();

        Dao di = new Dao();
        // DELETAR DEPENDENTES -----
        di.openTransaction();

        for (int i = 0; i < spbs.size(); i++) {
            if (!di.delete(spbs.get(i))) {
                di.rollback();
                GenericaMensagem.error("Erro", "Erro ao Excluir Serviço Pessoa Bloqueio!");
                PF.openDialog("i_dlg_c");
                return null;
            }
        }

        for (ListaDependentes ld : listDependentes) {
            Socios soc = db.pesquisaSocioPorPessoa(ld.getFisica().getPessoa().getId());
            if (soc.getId() == -1) {
                //listaDependentes.remove(idIndexDep);
            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
                di.rollback();
                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
                PF.openDialog("i_dlg_c");
                return null;
            }
        }

//        for (DataObject listaDependente : listaDependentes) {
//            Socios soc = db.pesquisaSocioPorPessoa(((Fisica) ((DataObject) listaDependente).getArgumento0()).getPessoa().getId());
//            if (soc.getId() == -1) {
//                //listaDependentes.remove(idIndexDep);
//            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
//                di.rollback();
//                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
//                PF.openDialog("i_dlg_c");
//                return null;
//            }
//        }
        for (ListaDependentes ldi : listDependentesInativos) {
            Socios soc = db.pesquisaSocioPorPessoa(ldi.getFisica().getPessoa().getId());
            if (soc.getId() == -1) {
                //listaDependentes.remove(idIndexDep);
            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
                di.rollback();
                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
                PF.openDialog("i_dlg_c");
                return null;
            }
        }
//        for (DataObject listaDependentesInativo : listaDependentesInativos) {
//            Socios soc = db.pesquisaSocioPorPessoa(((Fisica) ((DataObject) listaDependentesInativo).getArgumento0()).getPessoa().getId());
//            if (soc.getId() == -1) {
//                //listaDependentes.remove(idIndexDep);
//            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
//                di.rollback();
//                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
//                PF.openDialog("i_dlg_c");
//                return null;
//            }
//        }

        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        List<SocioCarteirinha> list = new ArrayList();
        if (modeloc != null) {
            list = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        }
        SocioCarteirinhaDao socioCarteirinhaDB = new SocioCarteirinhaDao();
        List<HistoricoCarteirinha> hcs;
        if (!list.isEmpty()) {
            for (SocioCarteirinha socioCarteirinha : list) {
                hcs = socioCarteirinhaDB.listaHistoricoCarteirinha(socios.getServicoPessoa().getPessoa().getId());
                for (HistoricoCarteirinha hc : hcs) {
                    if (!di.delete(hc)) {
                        GenericaMensagem.error("Erro", "Erro ao Excluir carteirinha do Dependente!");
                        PF.openDialog("i_dlg_c");
                        di.rollback();
                        return null;
                    }
                }
                hcs.clear();
                if (!di.delete(socioCarteirinha)) {
                    GenericaMensagem.error("Erro", "Erro ao Excluir carteirinha do Dependente!");
                    PF.openDialog("i_dlg_c");
                    di.rollback();
                    return null;
                }
            }
        }

        // DELETAR SOCIOS ------
        if (!di.delete(di.find(socios))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Sócio!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }

        if (!di.delete(di.find(matriculaSocios))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Matricula, existem movimentos para essa Pessoa!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }

        if (!di.delete(di.find(servicoPessoa))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Servico Pessoa!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.delete(
                " ID: " + socios.getId()
                + " - Pessoa: (" + socios.getServicoPessoa().getPessoa().getId() + ") - " + socios.getServicoPessoa().getPessoa().getNome()
                + " - Titular: (" + socios.getMatriculaSocios().getTitular().getId() + ") - " + socios.getMatriculaSocios().getTitular().getNome()
                + " - Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                + " - Categoria: " + socios.getMatriculaSocios().getCategoria().getCategoria()
                + " - Parentesco: " + socios.getParentesco().getParentesco()
        );
        GenericaMensagem.info("Sucesso", "Cadastro Deletado!");
        di.commit();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);

        // FUNCIONANDO --
        //FisicaJSFBean fizx = (FisicaJSFBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaJSFBean());
        //fizx.setSocios(new Socios());
        ((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).setSocios(new Socios());
        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("socioPesquisa",socios);

        return "pessoaFisica";
    }

    public boolean validaExcluir() {
        if (servicoPessoa.getId() == -1) {
            GenericaMensagem.error("Erro", "Não existe Sócio para ser Excluído!");
            return false;
        }
        return true;
    }

    public String excluirDependente(ListaDependentes list, Integer index) {
        Fisica fi = list.getFisica();
        if (fi.getId() != -1) {
            SociosDao db = new SociosDao();
            // Socios soc = db.pesquisaSocioPorPessoa(fi.getPessoa().getId());
            Socios soc = db.pesquisaSocioPorPessoaEMatriculaSocio(fi.getPessoa().getId(), socios.getMatriculaSocios().getId());
            Dao dao = new Dao();
            dao.openTransaction();
            soc.getServicoPessoa().setReferenciaValidade(list.getValidadeDependente());
            Boolean isAtivo = soc.getServicoPessoa().isAtivo();
            if (soc.getId() == -1) {
                try {
                    // listaDependentes.remove(index);
                    listDependentes.remove((int) index);
                } catch (Exception e) {

                }
                GenericaMensagem.warn("Erro", "Dependente Excluído!");
                dao.rollback();
                return null;
            } else if (!inativaDependentes(dao, soc)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao inativar dependente!");
                return null;
            } else {
                dao.commit();
                listDependentesInativos.clear();
                if (isAtivo) {
                    try {
                        GenericaMensagem.warn("Erro", "Dependente inativado!");
                        listDependentes.remove((int) index);
                        NovoLog novoLog = new NovoLog();
                        novoLog.setTabela("fin_servico_pessoa");
                        novoLog.setCodigo(list.getServicoPessoa().getId());
                        novoLog.update("Dependente inátivado",
                                " Serviço Pessoa: (" + list.getServicoPessoa().getId() + ") "
                                + " - Dependente nome: (" + list.getServicoPessoa().getPessoa().getId() + ") - " + list.getServicoPessoa().getPessoa().getNome()
                                + " - Titular: (" + socios.getMatriculaSocios().getTitular().getId() + ") - " + socios.getMatriculaSocios().getTitular().getNome()
                                + " - Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                                + " - Categoria: " + socios.getMatriculaSocios().getCategoria().getCategoria()
                                + " - Parentesco: " + list.getParentesco().getParentesco()
                        );
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        // listaDependentes.remove(index);
                        listDependentes.remove((int) index);
                    } catch (Exception ex) {
                    }
                    if (!dao.delete(soc.getServicoPessoa(), true)) {
                        GenericaMensagem.warn("Sistema", "Serviço pessoa não pode ser excluído para esse dependente!");
                    }
                    GenericaMensagem.info("Sucesso", "Dependente excluído!");
                }
                soc = null;
                atualizarListaDependenteInativo();
                return null;
            }
        } else {
            // listaDependentes.remove(index);
            listDependentes.remove((int) index);
            GenericaMensagem.warn("Erro", "Dependente Excluído!");
        }
        return null;
    }

    public boolean inativaDependentes(Dao dao, Socios soc) {
        if (soc.getServicoPessoa().isAtivo()) {
            soc.getServicoPessoa().setAtivo(false);
            soc.getServicoPessoa().setDtInativacao(new Date());
            soc.getServicoPessoa().setMotivoInativacao("DEPENDENTE FOI INATIVADO PELO OPERADOR");
            if (dao.update(soc.getServicoPessoa())) {
                NovoLog novoLog = new NovoLog();
                novoLog.setTabela("fin_servico_pessoa");
                novoLog.setCodigo(soc.getServicoPessoa().getId());
                novoLog.update("Dependente inativado",
                        " Serviço Pessoa: (" + soc.getServicoPessoa().getId() + ") "
                        + " - Pessoa: (" + soc.getServicoPessoa().getPessoa().getId() + ") - " + soc.getServicoPessoa().getPessoa().getNome()
                        + " - Titular: (" + soc.getMatriculaSocios().getTitular().getId() + ") - " + soc.getMatriculaSocios().getTitular().getNome()
                        + " - Matrícula: " + soc.getMatriculaSocios().getNrMatricula()
                        + " - Categoria: " + soc.getMatriculaSocios().getCategoria().getCategoria()
                        + " - Parentesco: " + soc.getParentesco().getParentesco()
                        + " - Motivo Inativação: " + "DEPENDENTE FOI INATIVADO PELO OPERADOR"
                );
                return true;
            }
            return false;
        } else {
            return excluirDependentes(dao, (Socios) dao.find(soc));
        }
    }

    public boolean excluirDependentes(Dao dao, Socios soc) {
        SociosDao db = new SociosDao();

        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();

        ServicoCategoriaDao dbSCat = new ServicoCategoriaDao();

        ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(soc.getParentesco().getId(), servicoCategoria.getCategoria().getId());

        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(servicoCategoriaDep.getCategoria().getId(), 170);

        List<HistoricoCarteirinha> listaHistoricoCarteirinha = dbc.listaHistoricoCarteirinha(soc.getServicoPessoa().getPessoa().getId());

        if (!listaHistoricoCarteirinha.isEmpty()) {
            for (HistoricoCarteirinha hc : listaHistoricoCarteirinha) {
                if (!dao.delete(hc)) {
                    GenericaMensagem.warn("Erro", "Ao excluir histórico carteirinha do Dependente!");
                    return false;
                }
            }
        }

        List<SocioCarteirinha> list = new ArrayList();

        if (modeloc != null) {
            list = db.pesquisaCarteirinhasPorPessoa(soc.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        }

        if (!list.isEmpty()) {
            for (SocioCarteirinha socioCarteirinha : list) {
                if (!dao.delete(socioCarteirinha)) {
                    GenericaMensagem.warn("Erro", "Ao excluir carteirinha do Dependente!");
                    return false;
                }
            }
        }

        if (!dao.delete(soc)) {
            GenericaMensagem.warn("Erro", "Ao excluir Dependente!");
            return false;
        }

        // ServicoPessoa serPessoa = dbS.pesquisaServicoPessoaPorPessoa(soc.getServicoPessoa().getPessoa().getId());
//        ServicoPessoa serPessoa = (ServicoPessoa) dao.find(soc.getServicoPessoa());
//        if (!dao.delete(serPessoa)) {
//            GenericaMensagem.info("Sucesso", "Dependente excluído!");
//            GenericaMensagem.warn("Erro", "Ao excluir serviço pessoa do Dependente!");
//            return true;
//        }
        GenericaMensagem.info("Sucesso", "Dependente excluído!");
        return true;
    }

    public void editarOUsalvar(int index) {
        // Fisica fisica = (Fisica) listaDependentes.get(index).getArgumento0();
        Fisica fisica = listDependentes.get(index).getFisica();
        if (fisica.getId() == -1) {
            novoDependente = new Fisica();
        } else {
            novoDependente = fisica;
        }

        fisicaPesquisa = new Fisica();
        index_dependente = index;

        modelVisible = true;
        Diretorio.remover("temp/foto/" + getUsuario().getId() + "/");
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
        if (f.exists()) {
            f.delete();
        }
        String url_foto = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + novoDependente.getPessoa().getId());
        if (new File(url_foto + ".gif").exists() || new File(url_foto + ".png").exists() || new File(url_foto + ".jpg").exists() || new File(url_foto + ".jpeg").exists()) {
            if (fisica.getDtFoto() == null) {
                fisica.setDataFoto(DataHoje.data());
                new Dao().update(fisica, true);
            }
        }
        loadNaturalidadeDependente();
    }

    public void loadNaturalidadeDependente() {
        if (GenericaSessao.exists("cidadePesquisa")) {
            Cidade cidade = (Cidade) GenericaSessao.getObject("cidadePesquisa", true);
            novoDependente.setNaturalidade(cidade.getCidade() + " - " + cidade.getUf());
            return;
        }

        if (novoDependente.getId() == -1 || novoDependente.getNaturalidade().isEmpty()) {
            PessoaEnderecoDao dbPes = new PessoaEnderecoDao();
            Dao dao = new Dao();
            Filial fili = (Filial) dao.find(new Filial(), 1);
            if (fili != null) {
                Pessoa pes = fili.getMatriz().getPessoa();
                if (pes.getId() != -1) {
                    Cidade cidade = ((PessoaEndereco) dbPes.pesquisaEndPorPessoa(pes.getId()).get(0)).getEndereco().getCidade();
                    novoDependente.setNaturalidade(cidade.getCidade() + " - " + cidade.getUf());
                }
            }
        }
    }

    public void fechaModal() {
        novoDependente = new Fisica();
        modelVisible = false;
    }

    public void editarDependente(Fisica f) {
        dependente = (Fisica) f;
    }

    public void reativarDependente(int index) {
        String dataRef = DataHoje.dataReferencia(DataHoje.data());
        int dataHoje = DataHoje.converteDataParaRefInteger(dataRef);
        ServicoPessoaDao spdb = new ServicoPessoaDao();
        SociosDao sociosDao = new SociosDao();
        List<ServicoPessoa> list = sociosDao.listServicoPessoaInSociosByPessoa(listDependentesInativos.get(index).getFisica().getPessoa().getId());
        Dao dao = new Dao();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isAtivo()) {
                GenericaMensagem.warn("Validação", "Pessoa já esta ativa em outra matrícula! Ativo no cadastro de " + list.get(i).getPessoa().getNome());
                return;
            }
        }
        int dataValidade = 0;
        if (!listDependentesInativos.get(index).getValidadeDependente().isEmpty()) {
            dataValidade = DataHoje.converteDataParaRefInteger(listDependentesInativos.get(index).getValidadeDependente());
        }
        if (dataValidade != 0 && dataValidade < dataHoje) {
            GenericaMensagem.warn("Validação", "Não reativa com validade vencida!");
            return;
        }
        list.clear();
        list = spdb.listByPessoaInativo(listDependentesInativos.get(index).getFisica().getPessoa().getId());
        Socios s;
        if (servicoPessoa.isAtivo()) {
            NovoLog novoLog = new NovoLog();
            for (int i = 0; i < list.size(); i++) {
                s = sociosDao.pesquisaSocioPorServicoPessoa(list.get(i).getId());
                if (s.getMatriculaSocios().getId() == socios.getMatriculaSocios().getId()) {
                    list.get(i).setAtivo(true);
                    list.get(i).setDtInativacao(null);
                    list.get(i).setMotivoInativacao("");
                    try {
                        list.get(i).setReferenciaValidade(listDependentesInativos.get(index).getValidadeDependente());
                    } catch (Exception e) {
                        list.get(i).setReferenciaValidade(servicoPessoa.getReferenciaValidade());
                    }
                    dao.update(list.get(i), true);
                    novoLog.setTabela("fin_servico_pessoa");
                    novoLog.setCodigo(list.get(i).getId());
                    novoLog.update("Dependente reativado",
                            " Serviço Pessoa: (" + list.get(i).getId() + ") "
                            + " - Pessoa: (" + s.getServicoPessoa().getPessoa().getId() + ") - " + s.getServicoPessoa().getPessoa().getNome()
                            + " - Titular: (" + s.getMatriculaSocios().getTitular().getId() + ") - " + s.getMatriculaSocios().getTitular().getNome()
                            + " - Matrícula: " + s.getMatriculaSocios().getNrMatricula()
                            + " - Categoria: " + s.getMatriculaSocios().getCategoria().getCategoria()
                            + " - Parentesco: " + s.getParentesco().getParentesco()
                    );
                }
            }
            if (listDependentesInativos.get(0).getValidadeDependente().isEmpty()) {
                listDependentesInativos.get(0).setValidadeDependente(atualizaValidade(listDependentesInativos.get(0).getParentesco(), listDependentesInativos.get(0).getFisica()));
            }
            listDependentes.add(listDependentesInativos.get(index));
            listDependentesInativos.remove(index);
        } else {
            GenericaMensagem.warn("Validação", "Não é possível reativar dependente com titular (Serviço Pessoa) inativo! Contate o administrador do sistema!");
        }
    }

    public void reativarDependente() {
        for (int i = 0; i < listDependentesInativos.size(); i++) {
            String vencimento_dep = DataHoje.data().substring(0, 2) + "/" + listDependentesInativos.get(i).getValidadeDependente();
            String data_hoje = DataHoje.data();

            if (DataHoje.igualdadeData(vencimento_dep, data_hoje) || DataHoje.maiorData(vencimento_dep, data_hoje) || listDependentesInativos.get(i).getValidadeDependente().isEmpty()) {
                listDependentes.add(listDependentesInativos.get(i));
                listDependentesInativos.remove(i);
            }
        }
    }

    public void atualizarListaDependenteAtivo() {
        int index = 0;
        SociosDao db = new SociosDao();
        FisicaDao dbf = new FisicaDao();
        Dao dao = new Dao();
        // LISTA DE DEPENDENTES ATIVOS
        List<Socios> listaDepsAtivo = db.listaDependentes(matriculaSocios.getId());
        if (!listaDepsAtivo.isEmpty()) {
            listDependentes.clear();
            for (int i = 0; i < listaDepsAtivo.size(); i++) {
                // FISICA, PARENTESCO, VIA_CARTEIRINHA, DATA VALIDADE CARTEIRINHA, DATA VAL DEP
                Fisica fisica = dbf.pesquisaFisicaPorPessoa(listaDepsAtivo.get(i).getServicoPessoa().getPessoa().getId());
                List<Parentesco> listap = getListaParentesco(fisica.getSexo());

                List<SelectItem> lista_si = new ArrayList<>();
                for (int w = 0; w < listap.size(); w++) {
                    if (listaDepsAtivo.get(i).getParentesco().getId() == listap.get(w).getId()) {
                        index = w;
                    }

                    lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
                }

                String vencimento_dep = "";

                if (listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade() != null && !listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade().isEmpty()) {
                    vencimento_dep = "01/" + listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade();
                }

                String data_hoje = DataHoje.data();

                if (vencimento_dep.isEmpty()
                        || (!vencimento_dep.isEmpty() && (DataHoje.igualdadeData(vencimento_dep, data_hoje) || DataHoje.maiorData(vencimento_dep, data_hoje)))) {
                    SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
                    ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

                    List<SocioCarteirinha> listsc = new ArrayList();
                    if (modeloc != null) {
                        listsc = db.pesquisaCarteirinhasPorPessoa(listaDepsAtivo.get(i).getServicoPessoa().getPessoa().getId(), modeloc.getId());
                        if (!listsc.isEmpty()) {
                            if (DataHoje.maiorData(listsc.get(0).getDtValidadeCarteirinha(), DataHoje.dataHoje())) {
                                vencimento_dep = listsc.get(0).getValidadeCarteirinha();
                            } else {
                                vencimento_dep = listsc.get(0).getValidadeCarteirinha();
                            }
                        }
                    }

                    double valor_dependente = calculoValorGenerico(
                            listaDepsAtivo.get(i).getServicoPessoa().getNrDesconto(),
                            fisica.getPessoa().getId(),
                            listaDepsAtivo.get(i).getServicoPessoa().getServicos().getId(),
                            socios.getMatriculaSocios().getCategoria().getId()
                    );

                    listDependentes.add(
                            new ListaDependentes(
                                    fisica,
                                    index,
                                    listaDepsAtivo.get(i).getNrViaCarteirinha(),
                                    vencimento_dep,
                                    listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade(),
                                    listaDepsAtivo.get(i).getServicoPessoa().getNrDesconto(),
                                    lista_si,
                                    true,
                                    valor_dependente,
                                    listaDepsAtivo.get(i).getServicoPessoa()
                            )
                    );
                } else {
                    // AQUI INATIVA AUTOMATICAMENTE SE O DEPENDENTE ESTIVER COM A REF VALIDADE < QUE A DATA ATUAL
                    dao.openTransaction();
                    ServicoPessoa sp2 = (ServicoPessoa) dao.find(new ServicoPessoa(), listaDepsAtivo.get(i).getServicoPessoa().getId());
                    sp2.setAtivo(false);
                    if (!dao.update(sp2)) {
                        GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                        dao.rollback();
                        return;
                    }
                    sp2 = new ServicoPessoa();
                    dao.commit();
                }
            }
        }

    }

    public void atualizarListaDependenteInativo() {
        int index = 0;
        SociosDao db = new SociosDao();
        FisicaDao dbf = new FisicaDao();
        // LISTA DE DEPENDENTES INATIVOS
        List<Socios> listaDepsInativo = db.listaDependentesInativos(matriculaSocios.getId());
        if (!listaDepsInativo.isEmpty()) {
            listDependentesInativos.clear();
            for (int i = 0; i < listaDepsInativo.size(); i++) {
                // FISICA, PARENTESCO, VIA_CARTEIRINHA, DATA VALIDADE CARTEIRINHA, DATA VAL DEP
                Fisica fisica = dbf.pesquisaFisicaPorPessoa(listaDepsInativo.get(i).getServicoPessoa().getPessoa().getId());

                List<Parentesco> listap = getListaParentesco(fisica.getSexo());
                ParentescoDao dbp = new ParentescoDao();
                List<SelectItem> lista_si = new ArrayList<>();
                for (int w = 0; w < listap.size(); w++) {
                    if (listaDepsInativo.get(i).getParentesco().getId() == listap.get(w).getId()) {
                        index = w;
                    }

                    lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
                }

                SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
                ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);
                List<SocioCarteirinha> listsc = new ArrayList();

                if (modeloc != null) {
                    listsc = db.pesquisaCarteirinhasPorPessoa(listaDepsInativo.get(i).getServicoPessoa().getPessoa().getId(), modeloc.getId());
                }

                double valor_dependente = calculoValorGenerico(
                        listaDepsInativo.get(i).getServicoPessoa().getNrDesconto(),
                        fisica.getPessoa().getId(),
                        listaDepsInativo.get(i).getServicoPessoa().getServicos().getId(),
                        socios.getMatriculaSocios().getCategoria().getId()
                );

                listDependentesInativos.add(
                        new ListaDependentes(
                                fisica,
                                index,
                                listaDepsInativo.get(i).getNrViaCarteirinha(),
                                (listsc.isEmpty()) ? "" : listsc.get(0).getValidadeCarteirinha(),
                                listaDepsInativo.get(i).getServicoPessoa().getReferenciaValidade(),
                                listaDepsInativo.get(i).getServicoPessoa().getNrDesconto(),
                                lista_si,
                                false,
                                valor_dependente,
                                listaDepsInativo.get(i).getServicoPessoa()
                        )
                );
            }
        }
    }

    public String atualizaValidade(Parentesco par, Fisica fisica) {
        if (par.getNrValidade() == 0) {
            return null;
        } else if (par.getNrValidade() > 0 && par.isValidade()) {
            if (!fisica.getNascimento().equals("") && fisica.getNascimento() != null) {
                return (new DataHoje()).incrementarAnos(par.getNrValidade(), fisica.getNascimento()).substring(3, 10);
            } else {
                return null;
            }
        } else if (par.getNrValidade() > 0 && !par.isValidade()) {
            return ((new DataHoje()).incrementarAnos(par.getNrValidade(), DataHoje.data())).substring(3, 10);
        }
        return null;
    }

//    Date validadeCarteirinha;
//    if (validadeCartao == null) {
//        PF.openDialog("i_dlg_c");
//        GenericaMensagem.warn("Validação", "Cadastrar validade cartão!");
//        return;
//    }
//    if (validadeCartao.getDtValidadeFixa() == null) {
//        validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(validadeCartao.getNrValidadeMeses(), DataHoje.data()));
//    } else {
//        validadeCarteirinha = validadeCartao.getDtValidadeFixa();
//    }        
    public String atualizaValidadeCarteirinha(Parentesco par, Fisica fisica) {
        ValidadeCartao validadeCartao = new ValidadeCartaoDao().findByCategoriaParentesco(Integer.parseInt(listaCategoria.get(idCategoria).getDescription()), par.getId());
        if (validadeCartao == null) {
            PF.openDialog("i_dlg_c");
            GenericaMensagem.warn("Validação", "Cadastrar validade cartão!");
            return null;
        }
        String validadeCarteirinha;
        if (validadeCartao.getDtValidadeFixa() == null) {
            validadeCarteirinha = new DataHoje().incrementarMeses(validadeCartao.getNrValidadeMeses(), DataHoje.data());
        } else {
            validadeCarteirinha = validadeCartao.getValidadeFixaString();
        }
        return validadeCarteirinha;
    }

    public void atualizaValidadeTela(int index) {
        Fisica fisica = listDependentes.get(index).getFisica();
        List<Parentesco> listap = getListaParentesco(fisica.getSexo());
        List<SelectItem> lista_si = new ArrayList();
        for (int w = 0; w < listap.size(); w++) {
            lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
        }
        listDependentes.get(index).setListParentesco(lista_si);
        int index_pa = Integer.valueOf(listDependentes.get(index).getParentescoString());
        Parentesco par = (Parentesco) new Dao().find(new Parentesco(), Integer.valueOf(lista_si.get(index_pa).getDescription()));
        listDependentes.get(index).setValidadeCarteirinha(atualizaValidadeCarteirinha(par, fisica));
        listDependentes.get(index).setValidadeDependente(atualizaValidade(par, fisica));
        ServicoCategoriaDao dbSCat = new ServicoCategoriaDao();
        ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(par.getId(), servicoCategoria.getCategoria().getId());
        listDependentes.get(index).getServicoPessoa().setServicos(servicoCategoriaDep.getServicos());

        calculoValorDependente(listDependentes.get(index));
    }

    public void confirmaImprimirCartao() {
        if (socios.getId() == -1) {
            return;
        }

        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        SociosDao dbs = new SociosDao();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return;
        }

        List<SocioCarteirinha> listsc = dbs.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());

        if (DataHoje.menorData(listsc.get(0).getValidadeCarteirinha(), DataHoje.data())) {
            DataHoje dh = new DataHoje();
            ValidadeCartao validadeCartao = new ValidadeCartaoDao().findByCategoriaParentesco(Integer.parseInt(listaCategoria.get(idCategoria).getDescription()), socios.getParentesco().getId());
            Date validadeCarteirinha;
            if (validadeCartao == null) {
                GenericaMensagem.warn("Atenção", "Validade Cartão Inexistente!");
                return;
            }
            if (validadeCartao.getDtValidadeFixa() == null) {
                validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(validadeCartao.getNrValidadeMeses(), DataHoje.data()));;
            } else {
                validadeCarteirinha = validadeCartao.getDtValidadeFixa();
            }
            novaValidadeCartao = DataHoje.converteData(validadeCarteirinha);
            PF.openDialog("dlg_validade_carteirinha");
        } else {
            PF.openDialog("dlg_imprimir_carteirinha");
        }
    }

    public String visualizarCarteirinha(boolean alterarValidade) {
        SocioCarteirinhaDao dbc = new SocioCarteirinhaDao();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return null;
        }

        /*
         COMENTEI TODO ESSE CÓDIGO PORQUE A PRINCIPIO NA MUDANÇA QUANDO SALVAR O SÓCIO ELE SEMPRE TERÁ CARTEIRINHA
         EM FASE DE TESTES 22/05/2014 QUINTA-FEIRA -- COMÉRCIO RP -- DEPOIS EXCLUIR COMENTÁRIO
         */
        if (modeloc.getFotoCartao()) {
            if (socios.getServicoPessoa().getPessoa().getFisica().getFoto() == null || socios.getServicoPessoa().getPessoa().getFisica().getFoto().isEmpty()) {
                GenericaMensagem.warn("Validação", "Obrigatório foto do titular!");
                return null;
            }
        }

        SocioCarteirinha sctitular = dbc.pesquisaCarteirinhaPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());

        if (sctitular == null) {
            GenericaMensagem.warn("Sistema", "Nenhuma carteirinha encontrada!");
            return null;
        }

        Dao dao = new Dao();
        if (sctitular.getDtEmissao() == null) {
            dao.openTransaction();
            sctitular.setDtEmissao(DataHoje.dataHoje());
            if (!dao.update(sctitular)) {
                GenericaMensagem.error("Erro", "Não foi possivel alterar data de emissão!");
                dao.rollback();
                return null;
            }

            HistoricoCarteirinha hc = new HistoricoCarteirinha();

            hc.setCarteirinha(sctitular);
            hc.setDescricao("Impressão de Carteirinha Titular");

            if (!dao.save(hc)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar histórico da carteirinha do dependente!");
                dao.rollback();
                return null;
            }
            dao.commit();
        }

        if (alterarValidade) {
            dao.openTransaction();
            sctitular.setDtValidadeCarteirinha(DataHoje.converte(novaValidadeCartao));
            socios.setNrViaCarteirinha(socios.getNrViaCarteirinha() + 1);

            HistoricoCarteirinha hc = new HistoricoCarteirinha();
            hc.setCarteirinha(sctitular);
            hc.setDescricao("Reimpressão de Carteirinha 2º Via");

            if (!dao.save(hc) || !dao.update(sctitular)) {
                GenericaMensagem.error("Erro", "Não foi possivel salvar histórico!");
                dao.rollback();
                return null;
            }

            dao.commit();
        }
        List listaAux = dbc.filtroCartao(socios.getServicoPessoa().getPessoa().getId());
        Boolean dependentesSemFotos = false;
        SociosDao db = new SociosDao();
        if (registro.isCarteirinhaDependente() && !listDependentes.isEmpty()) {
            dao.openTransaction();
            for (ListaDependentes ld : listDependentes) {
                if (modeloc.getFotoCartao()) {
                    if (ld.getServicoPessoa().getPessoa().getFisica().getFoto() == null || ld.getServicoPessoa().getPessoa().getFisica().getFoto().isEmpty()) {
                        if (!dependentesSemFotos) {
                            dependentesSemFotos = true;
                        }
                        continue;
                    }
                }
                Socios socioDependente = db.pesquisaSocioPorPessoaAtivo(ld.getFisica().getPessoa().getId());
                if (socioDependente.getMatriculaSocios().getCategoria().isCartaoDependente()) {
                    SocioCarteirinha sc = dbc.pesquisaCarteirinhaPessoa(socioDependente.getServicoPessoa().getPessoa().getId(), modeloc.getId());

                    if (sc != null && sc.getDtEmissao() == null) {
                        sc.setDtEmissao(DataHoje.dataHoje());
                        if (!dao.update(sc)) {
                            GenericaMensagem.warn("Erro", "Não foi possivel alterar data de emissão do dependente!");
                            dao.rollback();
                            return null;
                        }

                        HistoricoCarteirinha hc = new HistoricoCarteirinha();

                        hc.setCarteirinha(sc);
                        hc.setDescricao("Impressão de Carteirinha Dependente");

                        if (!dao.save(hc)) {
                            GenericaMensagem.warn("Erro", "Não foi possivel salvar histórico da carteirinha do dependente!");
                            dao.rollback();
                            return null;
                        }
                    }
                    listaAux.addAll(dbc.filtroCartao(socioDependente.getServicoPessoa().getPessoa().getId()));
                }
            }
            dao.commit();
        }

        if (!listaAux.isEmpty()) {
            ((List) listaAux.get(0)).set(6, sctitular.getValidadeCarteirinha());
            ((List) listaAux.get(0)).set(11, socios.getNrViaCarteirinha());
            ImpressaoParaSocios.imprimirCarteirinha(listaAux);
        } else {
            GenericaMensagem.warn("Sistema", "Socio não tem carteirinha!");
        }
        if (dependentesSemFotos) {
            GenericaMensagem.warn("Sistema", "Existem dependentes sem fotos!");
        }

//        if (!comita) {
//            dao.rollback();
//        }      
        return null;
    }

    public String imprimirFichaSocial() {
        String foto = "";

        for (String imagensTipo1 : imagensTipo) {
            File test = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + socios.getServicoPessoa().getPessoa().getId() + "/" + socios.getServicoPessoa().getPessoa().getFisica().getFoto() + "." + imagensTipo1);
            if (test.exists()) {
                foto = test.getAbsolutePath();
                break;
            }
        }

        String path = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper";
        String pathVerso = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTROVERSO.jasper";

        Diretorio.criar("Arquivos/downloads/fichas");
        //String caminhoDiretorio = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/fichas");
        ImpressaoParaSocios.comDependente(
                "ficha_" + socios.getId() + "_" + socios.getServicoPessoa().getPessoa().getId() + ".pdf",
                path,
                pathVerso,
                socios,
                pessoaEmpresa,
                matriculaSocios,
                imprimirVerso,
                foto
        );
        return null;
    }

    public String imprimirFichaSocialVazia() {
        ImpressaoParaSocios.branco();
        return "menuSocial";
    }

    public void imprimirFichaSocialBranco() {
        ImpressaoParaSocios.branco();
    }

    public void atualizarCategoria() {
        listaDescontoSocial.clear();
        listParcerlasTxInscricao = new ArrayList();
        index_desconto = 0;
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
        loadCategoria();
        loadServicos();
        Categoria c = getCategoria();
        nrParcerlasTxInscricao = 0;
        valorTaxaInscricao = new Double(0);
        if (c.getServicoTaxaMatricula() != null) {
            valorTaxaInscricao = new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), c.getServicoTaxaMatricula().getId(), new Date(), 0, 0);
            for (int i = 0; i < c.getNrTaxaMatriculaParcelas(); i++) {
                nrParcerlasTxInscricao = i + 1;
                listParcerlasTxInscricao.add(new SelectItem(i + 1, "" + (i + 1)));
                gerarParcelas();
            }
        }
    }

    public void atualizarListaCategoria() {
        listaDescontoSocial.clear();
        listParcerlasTxInscricao = new ArrayList();
        index_desconto = 0;
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
        idSisPeriodo = null;
        loadServicos();
        calculoValorDependente(null);
        Categoria c = getCategoria();
        nrParcerlasTxInscricao = 0;
        valorTaxaInscricao = new Double(0);
        if (c.getServicoTaxaMatricula() != null) {
            valorTaxaInscricao = new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), c.getServicoTaxaMatricula().getId(), new Date(), 0, 0);
            for (int i = 0; i < c.getNrTaxaMatriculaParcelas(); i++) {
                nrParcerlasTxInscricao = i + 1;
                listParcerlasTxInscricao.add(new SelectItem(i + 1, "" + (i + 1)));
                gerarParcelas();
            }
        }
    }

    public List<SelectItem> getListaGrupoCategoria() {
        return listaGrupoCategoria;
    }

    public List<SelectItem> getListaCategoria() {
        return listaCategoria;
    }

    public ServicoPessoa getServicoPessoa() {
//        if (GenericaSessao.getObject("fisicaPesquisa") != null && GenericaSessao.getObject("reativarSocio") != null) {
//            servicoPessoa.setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa")).getPessoa());
//            editarGenerico(((Fisica) GenericaSessao.getObject("fisicaPesquisa")).getPessoa(),
//                    GenericaSessao.getBoolean("reativarSocio"));
//            pessoaEmpresa = (PessoaEmpresa) GenericaSessao.getObject("pessoaEmpresaPesquisa");
//            GenericaSessao.remove("fisicaPesquisa");
//            GenericaSessao.remove("pessoaEmpresaPesquisa");
//            GenericaSessao.remove("reativarSocio");
//        }
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    public boolean isChkContaCobranca() {
        return chkContaCobranca;
    }

    public void setChkContaCobranca(boolean chkContaCobranca) {
//        if (this.chkContaCobranca != chkContaCobranca) {
//            listaTipoDocumento.clear();
//        }
        this.chkContaCobranca = chkContaCobranca;
    }

    public List<SelectItem> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public boolean isRenderServicos() {
        return renderServicos;
    }

    public void setRenderServicos(boolean renderServicos) {
        this.renderServicos = renderServicos;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public int getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(int idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public MatriculaSocios getMatriculaSocios() {
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        Dao dao = new Dao();
        List<GrupoCidades> cids = (List<GrupoCidades>) dao.list(new GrupoCidades(), true);
        if (socios.getId() == -1 && matriculaSocios.getId() == -1) {
            PessoaEndereco ende = db.pesquisaEndPorPessoaTipo(servicoPessoa.getPessoa().getId(), 3);
            if (GenericaSessao.exists("cidadePesquisa")) {
                matriculaSocios.setCidade((Cidade) GenericaSessao.getObject("cidadePesquisa"));
            } else if (ende != null && ende.getId() != -1) {
                for (int i = 0; i < cids.size(); i++) {
                    if (cids.get(i).getCidade().getId() == ende.getEndereco().getCidade().getId()) {
                        matriculaSocios.setCidade(ende.getEndereco().getCidade());
                        return matriculaSocios;
                    }
                }
                matriculaSocios.setCidade(((PessoaEndereco) db.pesquisaEndPorPessoaTipo(1, 3)).getEndereco().getCidade());
            } else {
                matriculaSocios.setCidade(((PessoaEndereco) db.pesquisaEndPorPessoaTipo(1, 3)).getEndereco().getCidade());
            }
        } else if (GenericaSessao.exists("cidadePesquisa")) {
            matriculaSocios.setCidade((Cidade) GenericaSessao.getObject("cidadePesquisa", !modelVisible));
            //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("cidadePesquisa");
        }
        return matriculaSocios;
    }

    public void setMatriculaSocios(MatriculaSocios matriculaSocios) {
        this.matriculaSocios = matriculaSocios;
    }

//    public List<DataObject> getListaDependentes() {
//        return listaDependentes;
//    }
//
//    public void setListaDependentes(List<DataObject> listaDependentes) {
//        this.listaDependentes = listaDependentes;
//    }
// AQUI LISTA DE PARENTESCO
//    public List<SelectItem> getListaParentesco() {
//        if (listaParentesco.isEmpty() && !listaCategoria.isEmpty()) {
//            ParentescoDB db = new ParentescoDao();
//            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0){
//                listaParentesco.add(new SelectItem(0, "Sem Categoria", "0"));
//                return listaParentesco;
//            }
//            
//            List<Parentesco> select = db.pesquisaTodosSemTitularCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
//            //List<Parentesco> select = db.pesquisaTodosSemTitular();
//            
//            if (!select.isEmpty()){
//                for (int i = 0; i < select.size(); i++) {
//                    listaParentesco.add(new SelectItem(i,
//                            select.get(i).getParentesco(),
//                            Integer.toString(select.get(i).getId()))
//                    );
//                }
//            }else
//                listaParentesco.add(new SelectItem(0, "Sem Categoria", "0"));
//        }
//        return listaParentesco;
//    }
    public List<Parentesco> getListaParentesco(String sexo) {
        if (!listaCategoria.isEmpty()) {
            ParentescoDao db = new ParentescoDao();
            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0) {
                return new ArrayList<>();
            }

            //List<Parentesco> select = db.pesquisaTodosSemTitularCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
            List<Parentesco> select = db.pesquisaTodosSemTitularCategoriaSexo(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), sexo);
            //List<Parentesco> select = db.pesquisaTodosSemTitular();

            if (!select.isEmpty()) {
                return select;
            } else {
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }
//
//    public void setListaParentesco(List<SelectItem> listaParentesco) {
//        this.listaParentesco = listaParentesco;
//    }

//    public String getPessoaImagem() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        File files = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/"));
//        File fExiste = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/fotoTemp.jpg"));
//        File listFile[] = files.listFiles();
//        String nome;
//        //temFoto = false;
//        if (fExiste.exists() && dependente.getDataFoto().isEmpty()) {
//            fotoTemp = true;
//        }
//        if (fotoTemp) {
//            nome = "fotoTemp";
//        } else {
//            nome = "semFoto";
//        }
//        int numArq = listFile.length;
//        for (int i = 0; i < numArq; i++) {
//            String n = listFile[i].getName();
//            for (int o = 0; o < n.length(); o++) {
//                if (n.substring(o, o + 1).equals(".")) {
//                    n = listFile[i].getName().substring(0, o);
//                }
//            }
//            try {
//                if (!fotoTemp) {
//                    if (Integer.parseInt(n) == dependente.getPessoa().getId()) {
//                        nome = n;
//                        fotoTemp = false;
//                        String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/fotoTemp.jpg");
//                        File fl = new File(caminho);
//                        fl.delete();
//                        break;
//                    }
//                } else {
//                    fotoTemp = false;
//                    break;
//                }
//            } catch (NumberFormatException e) {
//
//            }
//        }
//        return nome + ".jpg";
//    }
    public Fisica getDependente() {
        return dependente;
    }

    public void setDependente(Fisica dependente) {
        this.dependente = dependente;
    }

    public int getIdIndexCombo() {
        return idIndexCombo;
    }

    public void setIdIndexCombo(int idIndexCombo) {
        this.idIndexCombo = idIndexCombo;
    }

    public String getLblSocio() {
        if (socios.getMatriculaSocios().getId() == -1) {
            lblSocio = "SALVAR ";
        } else {
            lblSocio = "ALTERAR";
        }
        return lblSocio;
    }

    public void setLblSocio(String lblSocio) {
        this.lblSocio = lblSocio;
    }

    public String getLblSocioPergunta() {
        if (socios.getId() == -1) {
            lblSocioPergunta = "Deseja associar esse cadastro? ";
        } else {
            lblSocioPergunta = "Deseja alterar esse cadastro?";
        }
        return lblSocioPergunta;
    }

    public void setLblSocioPergunta(String lblSocioPergunta) {
        this.lblSocioPergunta = lblSocioPergunta;
    }

    public boolean isDesabilitaImpressao() {
        if (socios.getId() != -1 && matriculaSocios.getId() != -1) {
            desabilitaImpressao = false;
        }
        return desabilitaImpressao;
    }

    public void setDesabilitaImpressao(boolean desabilitaImpressao) {
        this.desabilitaImpressao = desabilitaImpressao;
    }

    public String getTipoDestinario() {
        return tipoDestinario;
    }

    public void setTipoDestinario(String tipoDestinario) {
        this.tipoDestinario = tipoDestinario;
    }

    public boolean isImprimirVerso() {
        return imprimirVerso;
    }

    public void setImprimirVerso(boolean imprimirVerso) {
        this.imprimirVerso = imprimirVerso;
    }

    public String getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(String dataInativacao) {
        this.dataInativacao = dataInativacao;
    }

    public Integer getIdInativacao() {
        return idInativacao;
    }

    public void setIdInativacao(Integer idInativacao) {
        this.idInativacao = idInativacao;
    }

    public List<SelectItem> getListaMotivoInativacao() {
        if (listaMotivoInativacao.isEmpty()) {
            SociosDao db = new SociosDao();
            List<SMotivoInativacao> select = db.pesquisaMotivoInativacao();
            for (int i = 0; i < select.size(); i++) {
                listaMotivoInativacao.add(new SelectItem(i, select.get(i).getDescricao(), Integer.toString(select.get(i).getId())));
            }
        }
        return listaMotivoInativacao;
    }

    public String getDataReativacao() {
        return dataReativacao;
    }

    public void setDataReativacao(String dataReativacao) {
        this.dataReativacao = dataReativacao;
    }

    public String getStatusSocio() {
        if (socios.getId() == -1) {
            statusSocio = "STATUS";
        } else if (matriculaSocios.getMotivoInativacao() != null) {
            statusSocio = "INATIVO / " + matriculaSocios.getMotivoInativacao().getDescricao() + " - " + matriculaSocios.getInativo();
        } else {
            statusSocio = "ATIVO";
        }
        return statusSocio;
    }

    public void setStatusSocio(String statusSocio) {
        this.statusSocio = statusSocio;
    }

    public List<Fisica> getListaFisica() {
        return listaFisica;
    }

    public void setListaFisica(List<Fisica> listaFisica) {
        this.listaFisica = listaFisica;
    }

    public List<SelectItem> getListaSelectFisica() {
        return listaSelectFisica;
    }

    public void setListaSelectFisica(List<SelectItem> listaSelectFisica) {
        this.listaSelectFisica = listaSelectFisica;
    }

    public Fisica getFisicaPesquisa() {
        return fisicaPesquisa;
    }

    public void setFisicaPesquisa(Fisica fisicaPesquisa) {
        this.fisicaPesquisa = fisicaPesquisa;
    }

    public Pessoa getPessoaPesquisa() {
        return pessoaPesquisa;
    }

    public void setPessoaPesquisa(Pessoa pessoaPesquisa) {
        this.pessoaPesquisa = pessoaPesquisa;
    }

    public Fisica getNovoDependente() {
        if (GenericaSessao.exists("cidadePesquisa")) {
            loadNaturalidadeDependente();
        }
        return novoDependente;
    }

    public void setNovoDependente(Fisica novoDependente) {
        this.novoDependente = novoDependente;
    }

    public int getIndex_dependente() {
        return index_dependente;
    }

    public void setIndex_dependente(int index_dependente) {
        this.index_dependente = index_dependente;
    }

    public List<Socios> getListaSocioInativo() {
        return listaSocioInativo;
    }

    public void setListaSocioInativo(List<Socios> listaSocioInativo) {
        this.listaSocioInativo = listaSocioInativo;
    }
//
//    public List<DataObject> getListaDependentesInativos() {
//        return listaDependentesInativos;
//    }
//
//    public void setListaDependentesInativos(List<DataObject> listaDependentesInativos) {
//        this.listaDependentesInativos = listaDependentesInativos;
//    }

    public Part getFilePart() {
        return filePart;
    }

    public void setFilePart(Part filePart) {
        this.filePart = filePart;
    }

    public final Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
//
//    public String getFotoTempPerfil() {
//        if (novoDependente.getId() == -1) {
//            if (fotoTempPerfil.isEmpty()) {
//                String urlTemp = "/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId() + "/perfil.png";
//                String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(urlTemp);
//                for (String imagensTipo1 : imagensTipo) {
//                    File f = new File(arquivo);
//                    if (f.exists()) {
//                        fotoTempPerfil = urlTemp;
//                        break;
//                    } else {
//                        fotoTempPerfil = "";
//                    }
//                }
//            }
//        } else {
//            fotoTempPerfil = "";
//        }
//        return fotoTempPerfil;
//    }
//
//    public void setFotoTempPerfil(String fotoTempPerfil) {
//        this.fotoTempPerfil = fotoTempPerfil;
//    }

    public boolean isModelVisible() {
        return modelVisible;
    }

    public void setModelVisible(boolean modelVisible) {
        this.modelVisible = modelVisible;
    }

    public String getNovaValidadeCartao() {
        return novaValidadeCartao;
    }

    public void setNovaValidadeCartao(String novaValidadeCartao) {
        this.novaValidadeCartao = novaValidadeCartao;
    }

    public SocioCarteirinha getSocCarteirinha() {
        return socCarteirinha;
    }

    public void setSocCarteirinha(SocioCarteirinha socCarteirinha) {
        this.socCarteirinha = socCarteirinha;
    }

    public Registro getRegistro() {
        return registro;
    }

    public DescontoSocial getDescontoSocial() {
        return descontoSocial;
    }

    public void setDescontoSocial(DescontoSocial descontoSocial) {
        this.descontoSocial = descontoSocial;
    }

    public List<SelectItem> getListaDescontoSocial() {
        if (listaDescontoSocial.isEmpty()) {
            SociosDao db = new SociosDao();
            //listaDescontoSocial.add((DescontoSocial) new Dao().find(new DescontoSocial(), 1));
            DescontoSocial ds = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
            listaDescontoSocial.add(new SelectItem(0, ds.getDescricao(), "" + ds.getId()));
            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) != 0) {
                //listaDescontoSocial.addAll(db.listaDescontoSocial(Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
                List<DescontoSocial> lds = db.listaDescontoSocial(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
                for (int i = 0; i < lds.size(); i++) {
                    listaDescontoSocial.add(
                            new SelectItem(i + 1, lds.get(i).getDescricao(), "" + lds.get(i).getId())
                    );
                }
            }
        }
        return listaDescontoSocial;
    }

    public void setListaDescontoSocial(List<SelectItem> listaDescontoSocial) {
        this.listaDescontoSocial = listaDescontoSocial;
    }

    public DescontoSocial getNovoDescontoSocial() {
        return novoDescontoSocial;
    }

    public void setNovoDescontoSocial(DescontoSocial novoDescontoSocial) {
        this.novoDescontoSocial = novoDescontoSocial;
    }

    public String getValorServico() {
        return valorServico;
    }

    public void setValorServico(String valorServico) {
        this.valorServico = Moeda.converteR$(valorServico);
    }

    public Integer getIndex_desconto() {
        return index_desconto;
    }

    public void setIndex_desconto(Integer index_desconto) {
        this.index_desconto = index_desconto;
    }

    public String getNovoValorServico() {
        return novoValorServico;
    }

    public void setNovoValorServico(String novoValorServico) {
        this.novoValorServico = Moeda.converteR$(novoValorServico);
    }

    public String getAlteraValorServico() {
        return alteraValorServico;
    }

    public void setAlteraValorServico(String alteraValorServico) {
        this.alteraValorServico = Moeda.converteR$(alteraValorServico);
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public List<SelectItem> getListaFilial() {
        if (listaFilial.isEmpty()) {
            List<Filial> list = new Dao().list(new Filial(), true);
            MacFilial mc = MacFilial.getAcessoFilial();
            if (mc.getId() == -1) {
                mc.setFilial((Filial) new Dao().find(new Filial(), 1));
            }
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getId(), mc.getFilial().getId())) {
                    idFilial = i;
                }
                listaFilial.add(new SelectItem(i, list.get(i).getFilial().getPessoa().getNome(), "" + list.get(i).getId()));
            }
        }
        return listaFilial;
    }

    public void setListaFilial(List<SelectItem> listaFilial) {
        this.listaFilial = listaFilial;
    }

    public List<ListaDependentes> getListDependentes() {
        return listDependentes;
    }

    public void setListDependentes(List<ListaDependentes> listDependentes) {
        this.listDependentes = listDependentes;
    }

    public List<ListaDependentes> getListDependentesInativos() {
        return listDependentesInativos;
    }

    public void setListDependentesInativos(List<ListaDependentes> listDependentesInativos) {
        this.listDependentesInativos = listDependentesInativos;
    }

    public String getPath() {
        if (novoDependente.getId() == -1) {
            return "temp/foto/" + new PessoaUtilitarios().getUsuarioSessao().getId();
        } else {
            return "Imagens/Fotos";
        }
    }

    public Categoria getCategoria() {
        return (Categoria) new Dao().find(new Categoria(), Integer.parseInt(listaCategoria.get(idCategoria).getDescription()));
    }

    public void loadBloqueio() {
        List<Mes> list = new Dao().list(new Mes(), true);
        bloqueio = new Bloqueio();
        listenerPeriodo();
        /*
        List<ServicoPessoaBloqueio> spbs = new ServicoPessoaBloqueioDao().findByServicoPessoa(socios.getServicoPessoa().getId());
        if (!spbs.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                Boolean status = true;
                for (int y = 0; y < spbs.size(); y++) {
                    if (list.get(i).getId() == spbs.get(y).getMes().getId()) {
                        status = false;
                        break;
                    }
                }
                switch (list.get(i).getId()) {
                    case 1:
                        bloqueio.setJan(status);
                        break;
                    case 2:
                        bloqueio.setFev(status);
                        break;
                    case 3:
                        bloqueio.setMar(status);
                        break;
                    case 4:
                        bloqueio.setAbr(status);
                        break;
                    case 5:
                        bloqueio.setMai(status);
                        break;
                    case 6:
                        bloqueio.setJun(status);
                        break;
                    case 7:
                        bloqueio.setJul(status);
                        break;
                    case 8:
                        bloqueio.setAgo(status);
                        break;
                    case 9:
                        bloqueio.setSet(status);
                        break;
                    case 10:
                        bloqueio.setOut(status);
                        break;
                    case 11:
                        bloqueio.setNov(status);
                        break;
                    case 12:
                        bloqueio.setDez(status);
                        break;
                }
            }
        }
         */
    }

    public Bloqueio getBloqueio() {
        return bloqueio;
    }

    public void setBloqueio(Bloqueio bloqueio) {
        this.bloqueio = bloqueio;
    }

    public void saveBloqueio() {
        listenerBloqueio(true, null, null);
    }

    public void listenerBloqueio(Integer servico_pessoa_id, Integer mes_id) {
        listenerBloqueio(null, servico_pessoa_id, mes_id);
    }

    public void listenerBloqueio(Boolean save, Integer servico_pessoa_id, Integer mes_id) {
        try {
            Dao dao = new Dao();
            if (save != null && save) {
                ServicoPessoaBloqueio spb;
                if (!bloqueio.getJan()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 1));
                    dao.save(spb, true);
                }
                if (!bloqueio.getFev()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 2));
                    dao.save(spb, true);
                }
                if (!bloqueio.getMar()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 3));
                    dao.save(spb, true);
                }
                if (!bloqueio.getAbr()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 4));
                    dao.save(spb, true);
                }
                if (!bloqueio.getMai()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 5));
                    dao.save(spb, true);
                }
                if (!bloqueio.getJun()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 6));
                    dao.save(spb, true);
                }
                if (!bloqueio.getJul()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 7));
                    dao.save(spb, true);
                }
                if (!bloqueio.getAgo()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 8));
                    dao.save(spb, true);
                }
                if (!bloqueio.getSet()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 9));
                    dao.save(spb, true);
                }
                if (!bloqueio.getOut()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 10));
                    dao.save(spb, true);
                }
                if (!bloqueio.getNov()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 11));
                    dao.save(spb, true);
                }
                if (!bloqueio.getDez()) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa(servicoPessoa);
                    spb.setMes((Mes) dao.find(new Mes(), 12));
                    dao.save(spb, true);
                }
            } else if (socios.getId() != -1) {
                ServicoPessoaBloqueio spb = new ServicoPessoaBloqueioDao().find(servico_pessoa_id, mes_id);
                if (spb == null) {
                    spb = new ServicoPessoaBloqueio();
                    spb.setServicoPessoa((ServicoPessoa) dao.find(new ServicoPessoa(), socios.getServicoPessoa().getId()));
                    spb.setMes((Mes) dao.find(new Mes(), mes_id));
                    dao.save(spb, true);
                } else {
                    dao.delete(spb, true);
                }
                GenericaMensagem.info("Sucesso", "Bloqueio atualizado com sucesso!");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public List<Socios> getListSubSocios() {
        return listSubSocios;
    }

    public void setListSubSocios(List<Socios> listSubSocios) {
        this.listSubSocios = listSubSocios;
    }

    public void listenerPeriodo() {
        bloqueio = new Bloqueio();
//        if (socios.getId() != -1) {
//            List<ServicoPessoaBloqueio> list = new ServicoPessoaBloqueioDao().findByServicoPessoa(socios.getServicoPessoa().getId());
//            for (int i = 0; i < list.size(); i++) {
//                new Dao().delete(list.get(i), true);
//            }
//        }
        PeriodoMensalidade pm = new PeriodoMensalidadeDao().findByPeriodo(idSisPeriodo);
        Integer z = null;
        if (pm == null) {
            z = Integer.parseInt(DataHoje.livre(new Date(), "MM"));
        } else {
            if (pm.getMes() == null) {
                z = Integer.parseInt(DataHoje.livre(new Date(), "MM"));
            } else {
                z = pm.getMes().getId();
            }
        }
        if (idSisPeriodo == 3) {
            bloqueio = new Bloqueio();
        } else {
            bloqueio = new Bloqueio(false, false, false, false, false, false, false, false, false, false, false, false);

            int j = z;
            switch (idSisPeriodo) {
                case 4:
                    loopPeriodsMonths(j, 2);
                    break;
                case 5:
                    loopPeriodsMonths(j, 3);
                    break;
                case 8:
                    loopPeriodsMonths(j, 4);
                    break;
                case 6:
                    loopPeriodsMonths(j, 6);
                    break;
                case 7:
                    setMesBloqueio(z);
                    break;
                default:
                    break;
            }
            if (socios.getId() != -1) {
                // listenerBloqueio(true, socios.getServicoPessoa().getId(), null);
                // GenericaMensagem.info("Sucesso", "Bloqueio atualizado com sucesso!");
            }
        }
    }

    public void listenerSubSocios(Integer idPessoa) {
        listSubSocios.clear();
        SociosDao sociosDao = new SociosDao();
        Socios s = sociosDao.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
            listSubSocios = sociosDao.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
        }
    }

    public List<Fisica> getListFisicaSugestao() {
        return listFisicaSugestao;
    }

    public void setListFisicaSugestao(List<Fisica> listFisicaSugestao) {
        this.listFisicaSugestao = listFisicaSugestao;
    }

    public void useFisicaSugestao(Fisica f) {
        novoDependente = (Fisica) new Dao().rebind(f);
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento, ConfiguracaoArrecadacao.get().getIgnoraPeriodoConvencaoOposicao());
        }
        return false;
    }

    public List<SelectItem> getListSisPeriodos() {
        if (listSisPeriodos.isEmpty()) {
            List<Periodo> list = new PeriodoDao().findByPeriodoMensalidade();
            if (list.isEmpty()) {
                list = new Dao().find("Periodo", new int[]{3}, "", "dias");
            }
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idSisPeriodo = list.get(i).getId();
                }
                listSisPeriodos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        return listSisPeriodos;
    }

    public void setListSisPeriodos(List<SelectItem> listSisPeriodos) {
        this.listSisPeriodos = listSisPeriodos;
    }

    public Integer getIdSisPeriodo() {
        return idSisPeriodo;
    }

    public void setIdSisPeriodo(Integer idSisPeriodo) {
        this.idSisPeriodo = idSisPeriodo;
    }

    public void loopPeriodsMonths(Integer initial, Integer loop) {
        setMesBloqueio(initial);
        for (int x = 0; x < 12; x++) {
            Boolean n = true;
            for (int i = 0; i < 12; i++) {
                if (n) {
                    initial = initial + loop;
                    if (initial > 12) {
                        initial = initial - 12;
                        setMesBloqueio(initial);
                    } else {
                        setMesBloqueio(initial);
                    }
                    n = false;
                }
            }
        }
    }

    public void setMesBloqueio(Integer mes) {
        if (null != mes) {
            switch (mes) {
                case 1:
                    bloqueio.setJan(true);
                    break;
                case 2:
                    bloqueio.setFev(true);
                    break;
                case 3:
                    bloqueio.setMar(true);
                    break;
                case 4:
                    bloqueio.setAbr(true);
                    break;
                case 5:
                    bloqueio.setMai(true);
                    break;
                case 6:
                    bloqueio.setJun(true);
                    break;
                case 7:
                    bloqueio.setJul(true);
                    break;
                case 8:
                    bloqueio.setAgo(true);
                    break;
                case 9:
                    bloqueio.setSet(true);
                    break;
                case 10:
                    bloqueio.setOut(true);
                    break;
                case 11:
                    bloqueio.setNov(true);
                    break;
                case 12:
                    bloqueio.setDez(true);
                    break;
                default:
                    break;
            }
        }
    }

    public Double getValorTaxaInscricao() {
        return valorTaxaInscricao;
    }

    public void setValorTaxaInscricao(Double valorTaxaInscricao) {
        this.valorTaxaInscricao = valorTaxaInscricao;
    }

    public Integer getNrParcerlasTxInscricao() {
        return nrParcerlasTxInscricao;
    }

    public void setNrParcerlasTxInscricao(Integer nrParcerlasTxInscricao) {
        this.nrParcerlasTxInscricao = nrParcerlasTxInscricao;
    }

    public List<SelectItem> getListParcerlasTxInscricao() {
        return listParcerlasTxInscricao;
    }

    public void setListParcerlasTxInscricao(List<SelectItem> listParcerlasTxInscricao) {
        this.listParcerlasTxInscricao = listParcerlasTxInscricao;
    }

    public void gerarParcelas() {
        if (socios.getId() == -1) {

        }
    }

    public Lote getLoteTaxaInscricao() {
        return loteTaxaInscricao;
    }

    public void setLoteTaxaInscricao(Lote loteTaxaInscricao) {
        this.loteTaxaInscricao = loteTaxaInscricao;
    }

    public List<Movimento> getListMovimentosTaxaInscricao() {
        return listMovimentosTaxaInscricao;
    }

    public void setListMovimentosTaxaInscricao(List<Movimento> listMovimentosTaxaInscricao) {
        this.listMovimentosTaxaInscricao = listMovimentosTaxaInscricao;
    }

    public class Bloqueio {

        private Boolean jan;
        private Boolean fev;
        private Boolean mar;
        private Boolean abr;
        private Boolean mai;
        private Boolean jun;
        private Boolean jul;
        private Boolean ago;
        private Boolean set;
        private Boolean out;
        private Boolean nov;
        private Boolean dez;

        public Bloqueio() {
            this.jan = true;
            this.fev = true;
            this.mar = true;
            this.abr = true;
            this.mai = true;
            this.jun = true;
            this.jul = true;
            this.ago = true;
            this.set = true;
            this.out = true;
            this.nov = true;
            this.dez = true;
        }

        public Bloqueio(Boolean jan, Boolean fev, Boolean mar, Boolean abr, Boolean mai, Boolean jun, Boolean jul, Boolean ago, Boolean set, Boolean out, Boolean nov, Boolean dez) {
            this.jan = jan;
            this.fev = fev;
            this.mar = mar;
            this.abr = abr;
            this.mai = mai;
            this.jun = jun;
            this.jul = jul;
            this.ago = ago;
            this.set = set;
            this.out = out;
            this.nov = nov;
            this.dez = dez;
        }

        public Boolean getJan() {
            return jan;
        }

        public void setJan(Boolean jan) {
            this.jan = jan;
        }

        public Boolean getFev() {
            return fev;
        }

        public void setFev(Boolean fev) {
            this.fev = fev;
        }

        public Boolean getMar() {
            return mar;
        }

        public void setMar(Boolean mar) {
            this.mar = mar;
        }

        public Boolean getAbr() {
            return abr;
        }

        public void setAbr(Boolean abr) {
            this.abr = abr;
        }

        public Boolean getMai() {
            return mai;
        }

        public void setMai(Boolean mai) {
            this.mai = mai;
        }

        public Boolean getJun() {
            return jun;
        }

        public void setJun(Boolean jun) {
            this.jun = jun;
        }

        public Boolean getJul() {
            return jul;
        }

        public void setJul(Boolean jul) {
            this.jul = jul;
        }

        public Boolean getAgo() {
            return ago;
        }

        public void setAgo(Boolean ago) {
            this.ago = ago;
        }

        public Boolean getSet() {
            return set;
        }

        public void setSet(Boolean set) {
            this.set = set;
        }

        public Boolean getOut() {
            return out;
        }

        public void setOut(Boolean out) {
            this.out = out;
        }

        public Boolean getNov() {
            return nov;
        }

        public void setNov(Boolean nov) {
            this.nov = nov;
        }

        public Boolean getDez() {
            return dez;
        }

        public void setDez(Boolean dez) {
            this.dez = dez;
        }

    }

    public Integer getIndexCredenciadores() {
        return indexCredenciadores;
    }

    public void setIndexCredenciadores(Integer indexCredenciadores) {
        this.indexCredenciadores = indexCredenciadores;
    }

    public List<SelectItem> getListaCredenciadores() {
        return listaCredenciadores;
    }

    public void setListaCredenciadores(List<SelectItem> listaCredenciadores) {
        this.listaCredenciadores = listaCredenciadores;
    }

}
