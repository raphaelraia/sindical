package br.com.rtools.associativo.beans;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.beans.UploadFilesBean;
import br.com.rtools.sistema.dao.ProcessoAutomaticoDao;
import br.com.rtools.thread.RegistrarBoletoThread;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class ImpressaoBoletoSocialBean {

    private List<DataObject> listaGrid = new ArrayList();
    private int de = 0;
    private int ate = 0;
    private boolean imprimeVerso = true;

    private String strResponsavel = "";
    private String strLote = "";
    private String strData = "";
    private String strDocumento = "";

    private String tipo = "fisica";
    private Integer qntFolhas = 0;

    private List<Pessoa> listaPessoaSemEndereco = new ArrayList();
    private boolean atualizaListaPessoaSemEndereco = true;
    private Integer qntPessoasSelecionadas = 0;
    private String valorTotal = "0,00";
    
    private String boletoRegistrado = "todos";

    @PostConstruct
    public void init() {
        UploadFilesBean uploadFilesBean = new UploadFilesBean("Imagens/");
        GenericaSessao.put("uploadFilesBean", uploadFilesBean);
    }

    public void registrarBoletos() {
        ProcessoAutomatico pa = new ProcessoAutomaticoDao().pesquisarProcesso("registrar_boleto", Usuario.getUsuario().getId());

        if (pa.getId() != -1) {
            GenericaMensagem.info("Atenção", "Processo já iniciado, aguarde o término!");
            return;
        }

        if (!listaPessoaSemEndereco.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }

        List<Boleto> lista = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        for (int i = 0; i < listaGrid.size(); i++) {
            if ((Boolean) listaGrid.get(i).getArgumento1()) {
                lista.add(db.pesquisaBoletos((String) ((Vector) listaGrid.get(i).getArgumento2()).get(0)));
            }
        }

        if (lista.isEmpty()) {
            GenericaMensagem.error("Atenção", "Nenhum Boleto selecionado!");
            return;
        }

        new RegistrarBoletoThread(lista, "soc_boletos_vw").runDebug();
        
        // loadLista();

        GenericaMensagem.info("Sucesso", "Registro de Boletos concluído!");
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload cu = new ConfiguracaoUpload();
        cu.setArquivo(event.getFile().getFileName());
        cu.setDiretorio("Imagens/");
        cu.setArquivo("BannerPromoBoleto.png");
        cu.setSubstituir(true);
        cu.setRenomear("BannerPromoBoleto.png");
        cu.setEvent(event);
        if (Upload.enviar(cu, false)) {

        }
    }

    public void atualizaValores() {
        float soma_valor = 0;
        qntPessoasSelecionadas = 0;
        for (DataObject ldo : listaGrid) {
            if ((Boolean) ldo.getArgumento1()) {
                soma_valor = Moeda.somaValores(soma_valor, Moeda.converteUS$(ldo.getArgumento3().toString()));
                qntPessoasSelecionadas++;
            }
        }
        valorTotal = Moeda.converteR$Float(soma_valor);
    }

    public String editarPessoaSemEndereco(Pessoa pessoa) {
        ChamadaPaginaBean cp = (ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean");
        String pagina = "";

        FisicaDao dbf = new FisicaDao();
        Fisica f = dbf.pesquisaFisicaPorPessoa(pessoa.getId());
        if (f != null) {
            pagina = cp.pessoaFisica();
            FisicaBean fb = new FisicaBean();
            fb.editarFisica(f, true);
            GenericaSessao.put("fisicaBean", fb);
            setAtualizaListaPessoaSemEndereco(true);
        } else {
            JuridicaDao jdb = new JuridicaDao();
            Juridica j = jdb.pesquisaJuridicaPorPessoa(pessoa.getId());

            pagina = cp.pessoaJuridica();

            JuridicaBean jb = new JuridicaBean();
            jb.editar(j);
            GenericaSessao.put("juridicaBean", jb);
            setAtualizaListaPessoaSemEndereco(true);
        }
        return pagina;
    }

    public String qntDeFolhas(String nrCtrBoleto) {
        FinanceiroDao dao = new FinanceiroDao();
        List<Vector> lista_socio;
        if (tipo.equals("fisica")) {
            lista_socio = dao.listaBoletoSocioFisica(nrCtrBoleto, "soc_boletos_vw"); // NR_CTR_BOLETO
        } else {
            lista_socio = dao.listaBoletoSocioJuridica(nrCtrBoleto, "soc_boletos_vw"); // NR_CTR_BOLETO
        }
        return String.valueOf(lista_socio.size());
    }

    public void loadLista() {
        listaGrid = new ArrayList();
        listaPessoaSemEndereco = new ArrayList();

        if (strResponsavel.length() == 1 && strLote.isEmpty() && strData.isEmpty() && strDocumento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Muitos resultatos na pesquisa pode gerar lentidão!");
            return;
        }

        if (!strResponsavel.isEmpty() || !strLote.isEmpty() || !strData.isEmpty() || !strDocumento.isEmpty()) {
            FinanceiroDao dao = new FinanceiroDao();
            List<Vector> lista_agrupado = dao.listaBoletoSocioAgrupado(strResponsavel, strLote, strData, tipo, strDocumento, boletoRegistrado);

            int contador = 1;
            for (int i = 0; i < lista_agrupado.size(); i++) {
                List<Vector> lista_socio;
                if (tipo.equals("fisica")) {
                    lista_socio = dao.listaQntPorFisica(lista_agrupado.get(i).get(0).toString()); // NR_CTR_BOLETO
                } else {
                    lista_socio = dao.listaQntPorJuridica(lista_agrupado.get(i).get(0).toString()); // NR_CTR_BOLETO
                }
                if (qntFolhas == 0) {
                    // TODAS
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), false));
                    contador++;
                } else if (qntFolhas == 1 && lista_socio.size() <= 21) { // 21 quantidade de linhas que cabe em um boleto sem que estore
                    // APENAS COM 1 PÁGINA    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), false));
                    contador++;
                } else if (qntFolhas == 2 && (lista_socio.size() >= 22 && lista_socio.size() <= 121)) {
                    // DE 2 A 5 PAGINAS    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), false));
                    contador++;
                } else if (qntFolhas == 3 && lista_socio.size() > 122) {
                    // ACIMA DE 5 PAGINAS    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), false));
                    contador++;
                }

                // FILTRA PESSOAS SEM ENDERECO ---
                if (lista_agrupado.get(i).get(7) == null || lista_agrupado.get(i).get(7).toString().isEmpty()) {
                    listaPessoaSemEndereco.add((Pessoa) new Dao().find(new Pessoa(), Integer.valueOf(lista_agrupado.get(i).get(8).toString())));
                }
            }
            setAtualizaListaPessoaSemEndereco(false);
            atualizaValores();
        }
    }

    public int calculoDePaginas(int quantidade) {
        float soma = Moeda.divisaoValores(quantidade, 25);
        // return ((int) Math.ceil(soma) == 0) ? 1 : (int) Math.ceil(soma); // CALCULO
        return (int) Math.ceil(soma);
    }

    public void alterarPathImagem(String path) {
        UploadFilesBean uploadFilesBean = new UploadFilesBean("Imagens/");
        GenericaSessao.put("uploadFilesBean", uploadFilesBean);
    }

//    public String imagemBannerBoletoSocial(){
//        File file_promo = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/BannerPromoBoleto.png"));
//
//        if (!file_promo.exists())
//            return null;
//        else
//            return "Imagens/BannerPromoBoleto.png";
//    } 
//    
//    public String imagemVersoBannerBoletoSocial(){
//        File file_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoBoletoVersoSocial.png"));
//
//        if (!file_verso.exists())
//            return null;
//        else
//            return file_verso.getPath();
//    } 
//    
    public void marcar() {
        for (int i = 0; i < listaGrid.size(); i++) {
            if ((i + 1) >= de && ate == 0) {
                listaGrid.get(i).setArgumento1(true);
            } else if ((i + 1) >= de && (i + 1) <= ate) {
                listaGrid.get(i).setArgumento1(true);
            } else if (de == 0 && (i + 1) <= ate) {
                listaGrid.get(i).setArgumento1(true);
            } else {
                listaGrid.get(i).setArgumento1(false);
            }
        }

        atualizaValores();
    }

    public void desmarcarTudo() {
        for (int i = 0; i < listaGrid.size(); i++) {
            listaGrid.get(i).setArgumento1(false);
        }

        atualizaValores();
    }

    /*
    METODO ANTIGO
     */
    public void imprimir() {
        if (!listaPessoaSemEndereco.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }

        List<Boleto> lista = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        for (int i = 0; i < listaGrid.size(); i++) {
            if ((Boolean) listaGrid.get(i).getArgumento1()) {
                lista.add(db.pesquisaBoletos((String) ((Vector) listaGrid.get(i).getArgumento2()).get(0)));
            }
        }

        if (lista.isEmpty()) {
            GenericaMensagem.error("Atenção", "Nenhum Boleto selecionado!");
            return;
        }

        ImprimirBoleto ib = new ImprimirBoleto();

        ib.imprimirBoletoSocial(lista, "soc_boletos_vw", imprimeVerso);
        ib.visualizar(null);
    }

    /*
    METODO ATUAL EM USO
     */
    public void imprimir2() {
        if (!listaPessoaSemEndereco.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }

        MovimentoDao db = new MovimentoDao();
        String lista = "";
        for (int i = 0; i < listaGrid.size(); i++) {
            if ((Boolean) listaGrid.get(i).getArgumento1()) {
                if (lista.isEmpty()) {
                    lista = "'" + (String) ((Vector) listaGrid.get(i).getArgumento2()).get(0) + "'";
                } else {
                    lista += ", " + "'" + (String) ((Vector) listaGrid.get(i).getArgumento2()).get(0) + "'";
                }
            }
        }

        if (lista.isEmpty()) {
            GenericaMensagem.error("Atenção", "Nenhum Boleto selecionado!");
            return;
        }

        ImprimirBoleto ib = new ImprimirBoleto();

        ib.imprimirBoletoSocial_2(lista, "soc_boletos_vw", tipo, imprimeVerso);
        ib.visualizar(null);
    }

    public void etiquetaParaContabilidade() {
        if (!listaPessoaSemEndereco.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }

        List lista = new ArrayList();

        FinanceiroDao dao = new FinanceiroDao();

        try {
            Map<Integer, PessoaEndereco> hash = new LinkedHashMap();

            PessoaEnderecoDao dbpe = new PessoaEnderecoDao();
            PessoaEndereco pe;

            JuridicaDao dbj = new JuridicaDao();

            for (DataObject linha : listaGrid) {
                if ((Boolean) linha.getArgumento1()) {

                    List<PessoaEndereco> result_list = dbpe.listaEnderecoContabilidadeDaEmpresa(
                            dbj.pesquisaJuridicaPorPessoa(
                                    (Integer) ((Vector) linha.getArgumento2()).get(8)).getId(), // id_responsavel *nesse caso da empresa
                            5 // id_tipo_endereco
                    );
                    if (!result_list.isEmpty()) {
                        hash.put(result_list.get(0).getId(), result_list.get(0));
                    }
                }
            }

            for (Map.Entry<Integer, PessoaEndereco> entry : hash.entrySet()) {
                lista.add(
                        new Etiquetas(
                                entry.getValue().getPessoa().getNome(), // NOME
                                entry.getValue().getEndereco().getLogradouro().getDescricao(), // LOGRADOURO
                                entry.getValue().getEndereco().getDescricaoEndereco().getDescricao(), // ENDERECO
                                entry.getValue().getNumero(), // NÚMERO
                                entry.getValue().getEndereco().getBairro().getDescricao(), // BAIRRO
                                entry.getValue().getEndereco().getCidade().getCidade(), // CIDADE
                                entry.getValue().getEndereco().getCidade().getUf(), // UF
                                entry.getValue().getEndereco().getCep(), // CEP
                                entry.getValue().getComplemento() // COMPLEMENTO
                        )
                );
            }

            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/ETIQUETA_SOCIO.jasper"));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);

            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);

            List<JasperPrint> lista_jasper = new ArrayList();
            lista_jasper.add(JasperFillManager.fillReport(jasperReport, null, dtSource));

            JRPdfExporter exporter = new JRPdfExporter();

            String nomeDownload = "etiqueta_" + DataHoje.livre(DataHoje.dataHoje(), "yyyyMMdd-HHmmss") + ".pdf";
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/etiquetas");

            exporter.setExporterInput(SimpleExporterInput.getInstance(lista_jasper));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pathPasta + "/" + nomeDownload));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

            configuration.setCreatingBatchModeBookmarks(true);

            exporter.setConfiguration(configuration);

            exporter.exportReport();

            File fl = new File(pathPasta);

            if (fl.exists()) {
                Download download = new Download(
                        nomeDownload,
                        pathPasta,
                        "application/pdf",
                        FacesContext.getCurrentInstance()
                );
                download.baixar();
                download.remover();
            }
        } catch (JRException e) {
            e.getMessage();
        }
    }

    public void etiqueta() {
        if (!listaPessoaSemEndereco.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }

        List lista = new ArrayList();

        FinanceiroDao dao = new FinanceiroDao();

        try {
            for (int i = 0; i < listaGrid.size(); i++) {
                if ((Boolean) listaGrid.get(i).getArgumento1()) {
                    List<Vector> lista_socio;
                    PessoaEnderecoDao dbpe = new PessoaEnderecoDao();
                    PessoaEndereco pe;

                    lista_socio = dao.listaBoletoSocioJuridicaAgrupado((String) ((Vector) listaGrid.get(i).getArgumento2()).get(0)); // NR_CTR_BOLETO

                    for (int w = 0; w < lista_socio.size(); w++) {
                        if (tipo.equals("fisica")) {
                            pe = dbpe.pesquisaEndPorPessoaTipo(Integer.valueOf(lista_socio.get(w).get(0).toString()), 1);
                        } else {
                            pe = dbpe.pesquisaEndPorPessoaTipo(Integer.valueOf(lista_socio.get(w).get(0).toString()), 5);
                        }

                        if (pe != null) {
                            lista.add(
                                    new Etiquetas(
                                            pe.getPessoa().getNome(), // NOME
                                            pe.getEndereco().getLogradouro().getDescricao(), // LOGRADOURO
                                            pe.getEndereco().getDescricaoEndereco().getDescricao(), // ENDERECO
                                            pe.getNumero(), // NÚMERO
                                            pe.getEndereco().getBairro().getDescricao(), // BAIRRO
                                            pe.getEndereco().getCidade().getCidade(), // CIDADE
                                            pe.getEndereco().getCidade().getUf(), // UF
                                            pe.getEndereco().getCep(), // CEP
                                            pe.getComplemento() // COMPLEMENTO
                                    )
                            );
                        }
                    }
                }
            }
            Jasper.printReports("ETIQUETAS.jasper", "etiquetas", lista);            
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    public List<DataObject> getListaGrid() {
        return listaGrid;
    }

    public void setListaGrid(List<DataObject> listaGrid) {
        this.listaGrid = listaGrid;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getAte() {
        return ate;
    }

    public void setAte(int ate) {
        this.ate = ate;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public String getStrResponsavel() {
        return strResponsavel;
    }

    public void setStrResponsavel(String strResponsavel) {
        this.strResponsavel = strResponsavel;
    }

    public String getStrLote() {
        return strLote;
    }

    public void setStrLote(String strLote) {
        this.strLote = strLote;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getQntFolhas() {
        return qntFolhas;
    }

    public void setQntFolhas(Integer qntFolhas) {
        this.qntFolhas = qntFolhas;
    }

    public List<Pessoa> getListaPessoaSemEndereco() {
        if (atualizaListaPessoaSemEndereco) {
            //loadLista();
        }
        return listaPessoaSemEndereco;
    }

    public void setListaPessoaSemEndereco(List<Pessoa> listaPessoaSemEndereco) {
        this.listaPessoaSemEndereco = listaPessoaSemEndereco;
    }

    public boolean isAtualizaListaPessoaSemEndereco() {
        return atualizaListaPessoaSemEndereco;
    }

    public void setAtualizaListaPessoaSemEndereco(boolean atualizaListaPessoaSemEndereco) {
        this.atualizaListaPessoaSemEndereco = atualizaListaPessoaSemEndereco;
    }

    public Integer getQntPessoasSelecionadas() {
        return qntPessoasSelecionadas;
    }

    public void setQntPessoasSelecionadas(Integer qntPessoasSelecionadas) {
        this.qntPessoasSelecionadas = qntPessoasSelecionadas;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStrDocumento() {
        return strDocumento;
    }

    public void setStrDocumento(String strDocumento) {
        this.strDocumento = strDocumento;
    }

    public String getBoletoRegistrado() {
        return boletoRegistrado;
    }

    public void setBoletoRegistrado(String boletoRegistrado) {
        this.boletoRegistrado = boletoRegistrado;
    }
}
