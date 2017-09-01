package br.com.rtools.arrecadacao.beans;

import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.DocumentoInvalidoDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.pessoa.DocumentoInvalido;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.retornos.*;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.ArquivoRetorno.ObjectDetalheRetorno;
import java.io.*;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public final class ArquivoBancoBean implements Serializable {

    private Object retornoBanco = new Object();
    private ContaCobranca contaCobranca = new ContaCobranca();

    private String msgOk = "";
    private String lblPendente = "";
    private List<DataObject> listaDocumentos = new ArrayList();
    private List listaPasta = new ArrayList();
    private boolean outros = false;
    private boolean carregaPastas = false;

    private int index_contribuicao = 0;
    private List<SelectItem> listaServicos = new ArrayList();
    private List<String> listaArquivosPendentes = new ArrayList();
    private List<ObjectDetalheRetorno> listaDetalheRetornoBanco = new ArrayList();

    private String tipo = "";

    public ArquivoBancoBean() {
        this.getListaServicos();
        this.loadListaArquivosBaixar();
        this.loadListaDocumentos();

        GenericaSessao.remove("detalhes_retorno_banco");
    }

    public void loadListaArquivosBaixar() {
        listaArquivosPendentes.clear();
        Object objs[] = caminhoServicoPendente();
        String caminho = (String) objs[0];
        try {
            File filex = new File(caminho);
            File listFile[] = filex.listFiles();

            for (File linha_file : listFile) {
                listaArquivosPendentes.add(linha_file.getName());
            }
        } catch (Exception e) {
        }
    }

    public void loadListaDocumentos() {
        listaDocumentos.clear();

        DataObject dtObject;
        String documento, digito;

        DocumentoInvalidoDao dbDocInv = new DocumentoInvalidoDao();
        List<DocumentoInvalido> listaDoc = dbDocInv.pesquisaTodos();
        List<DocumentoInvalido> listaDocCadastrado;

        if (listaDoc.isEmpty()) {
            return;
        }

        listaDocCadastrado = dbDocInv.pesquisaNumeroBoletoPessoa();
        for (int i = 0; i < listaDoc.size(); i++) {
            Boolean encontrado = false;
            for (int w = 0; w < listaDocCadastrado.size(); w++) {
                if (listaDoc.get(i).getId() == listaDocCadastrado.get(w).getId()) {
                    documento = listaDoc.get(i).getDocumentoInvalido().substring(
                            listaDoc.get(i).getDocumentoInvalido().length() - 12,
                            listaDoc.get(i).getDocumentoInvalido().length()
                    );

                    List<Juridica> lj = new JuridicaDao().pesquisaJuridicaPorDocSubstring(documento);
                    String mascaraDocumento = "";
                    switch (lj.get(0).getPessoa().getTipoDocumento().getId()) {
                        case 1:
                            mascaraDocumento = AnaliseString.mascaraCPF(documento);
                            break;
                        case 2:
                            digito = ValidaDocumentos.retonarDigitoCNPJ(documento);
                            mascaraDocumento = AnaliseString.mascaraCnpj(documento + digito);
                            break;
                        case 3:
                            mascaraDocumento = AnaliseString.mascaraCEI(documento);
                            break;
                    }

                    dtObject = new DataObject(
                            false,
                            mascaraDocumento,// -- DOCUMENTO
                            "** CADASTRADO **",// -- STATUS
                            listaDoc.get(i),
                            false,
                            listaDoc.get(i).getDtImportacao()
                    );

                    listaDocumentos.add(dtObject);

                    encontrado = true;
                }
            }
            if (!encontrado) {
                documento = listaDoc.get(i).getDocumentoInvalido().substring(
                        listaDoc.get(i).getDocumentoInvalido().length() - 12,
                        listaDoc.get(i).getDocumentoInvalido().length()
                );
                digito = ValidaDocumentos.retonarDigitoCNPJ(documento);
                if (ValidaDocumentos.isValidoCNPJ(documento + digito)) {
                    dtObject = new DataObject(
                            false,
                            AnaliseString.mascaraCnpj(documento + digito),// -- DOCUMENTO
                            "** VERIFICAR **",// -- STATUS
                            listaDoc.get(i),
                            true,
                            listaDoc.get(i).getDtImportacao()
                    );
                    listaDocumentos.add(dtObject);
                } else {
                    dtObject = new DataObject(
                            false,
                            documento,// -- DOCUMENTO
                            "** INVALIDO **",// -- STATUS
                            listaDoc.get(i),
                            true,
                            listaDoc.get(i).getDtImportacao()
                    );
                    listaDocumentos.add(dtObject);
                }
            }
        }
    }

    public void fileUpload(FileUploadEvent event) {
        String cod;
        tipo = "";
        if (contaCobranca.getLayout().getId() == 2) {
            cod = contaCobranca.getSicasSindical();
        } else {
            cod = contaCobranca.getCodCedente();
        }

        Diretorio.criar("Arquivos/retorno/" + cod + "/");
        Diretorio.criar("Arquivos/retorno/" + cod + "/pendentes/");

        String caminhoB = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/retorno/" + cod + "/pendentes/");

        String file_name = event.getFile().getFileName();
        String caminho = caminhoB;
        caminho = caminho + "/" + file_name;
        try {
            File fl = new File(caminho);

            FileOutputStream out;
            InputStream in = event.getFile().getInputstream();
            out = new FileOutputStream(fl.getPath());

            byte[] buf = new byte[(int) event.getFile().getSize()];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }

            in.close();
            out.flush();
            out.close();

            if (!verificaArquivos(fl, contaCobranca)) {
                GenericaMensagem.error("Erro " + fl.getName(), "Arquivo não pode ser enviado, verifique se a CONTRIBUIÇÃO e a CONTA estão corretas!");
                fl.delete();
            }

            loadListaArquivosBaixar();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public List<SelectItem> getListaServicos() {
        if (listaServicos.isEmpty()) {
            ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
            List<ContaCobranca> result = servDB.listaContaCobrancaAtivoArrecadacao();
            if (result.isEmpty()) {
                listaServicos.add(new SelectItem(0, "Nenhuma Contribuição Encontrada", "0"));
                return listaServicos;
            }

            for (int i = 0; i < result.size(); i++) {
                // LAYOUT 2 = SINDICAL
                if (result.get(i).getLayout().getId() == 2) {
                    listaServicos.add(
                            new SelectItem(
                                    i,
                                    result.get(i).getApelido() + " - "
                                    + result.get(i).getSicasSindical(),//SICAS NO CASO DE SINDICAL
                                    Integer.toString(result.get(i).getId())));
                } else {
                    listaServicos.add(
                            new SelectItem(
                                    i,
                                    result.get(i).getApelido() + " - "
                                    + result.get(i).getCodCedente(),//CODCEDENTE NO CASO DE OUTRAS
                                    Integer.toString(result.get(i).getId())));
                }
            }

            if (!listaServicos.isEmpty()) {
                contaCobranca = (ContaCobranca) new Dao().find(new ContaCobranca(), Integer.parseInt(((SelectItem) listaServicos.get(index_contribuicao)).getDescription()));
            }
        }
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public void limparArquivosEnviados() {
        Object objs[] = caminhoServicoPendente();
        String caminho = (String) objs[0] + "/";
        try {
            File filex = new File(caminho);
            File listFile[] = filex.listFiles();

            for (File linha_file : listFile) {
                linha_file.delete();
            }
            GenericaMensagem.info("OK", "Arquivos Excluídos!");
            loadListaArquivosBaixar();
        } catch (Exception e) {
            GenericaMensagem.error("Erro", "Não foi possível excluir arquivos, tente novamente!");
        }
    }

    public void atualizaContaCobranca() {
        if (!listaServicos.isEmpty()) {
            contaCobranca = (ContaCobranca) new Dao().find(new ContaCobranca(), Integer.parseInt(((SelectItem) listaServicos.get(index_contribuicao)).getDescription()));
        }

        loadListaArquivosBaixar();
    }

    public String novoServico() {
        return null;
    }

    public boolean baixarArquivosGerados() {
        try {

            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/remessa/");
            caminho += "/" + DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1] + "-" + DataHoje.ArrayDataHoje()[0];

            Zip zip = new Zip();

            File fl = new File(caminho);
            File listFile[] = fl.listFiles();

            if (listFile.length == 0) {
                return false;
            }

            File arqzip = new File(caminho + "/arqzip.zip");
            zip.zip(listFile, arqzip);

            Download download = new Download(
                    "arqzip.zip",
                    arqzip.getParent(),
                    "zip",
                    FacesContext.getCurrentInstance());
            download.baixar();

            arqzip.delete();
            return true;
        } catch (Exception e) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + e.getMessage());
            return false;
        }

    }

    public List getListaPastas() {
        if (carregaPastas) {
            listaPasta = new ArrayList();
            DataObject dt = null;
            Object objs[] = caminhoServicoPendente();
            String caminho = (String) objs[0];
            //ServicoContaCobranca scc = (ServicoContaCobranca)objs[1];
            Object[] obj = new Object[2];
            File files = null;
            try {
                files = new File(caminho);
                File listFile[] = files.listFiles();
                int numArq = listFile.length;
                for (int i = 0; i < numArq; i++) {
                    try {
                        obj = converteDataPasta(listFile[i].getName());
                    } catch (Exception e) {
                    }
                    if ((Boolean) obj[1]) {
                        dt = new DataObject(false,
                                listFile[i].getName(),
                                obj[0],
                                "",
                                "",
                                "");
                        listaPasta.add(dt);
                    }
                }
            } catch (Exception e) {
                return new ArrayList();
            }
            carregaPastas = false;
        }
        return listaPasta;
    }

    public Object[] converteDataPasta(String pasta) {
        Object[] obj = new Object[2];
        try {
            if (pasta.equals("pendentes")) {
                obj[0] = "pendentes";
                obj[1] = true;
                return obj;
            }

            pasta = pasta.substring(8, 10) + "/" + pasta.substring(5, 7) + "/" + pasta.substring(0, 4);
            obj[0] = (Date) DataHoje.converte(pasta);
            if (obj[0] == null) {
                obj[1] = false;
            } else {
                obj[0] = DataHoje.converteData((Date) obj[0]);
                obj[1] = true;
            }
        } catch (Exception e) {
            obj[0] = null;
            obj[1] = false;
        }
        return obj;
    }

    public String relatorioComparacao() {
        if (!listaPasta.isEmpty()) {
            Object objs[] = caminhoServicoPendente();
            ContaCobranca scc = (ContaCobranca) objs[1];
            ArquivoRetorno ar = null;
            List<GenericaRetorno> genericaRetorno = new ArrayList();
            MovimentoDao db = new MovimentoDao();

            // CAIXA FEDERAL ------------------------------------------------------------------------------
            if (ArquivoRetorno.CAIXA_FEDERAL == scc.getContaBanco().getBanco().getId()) {
                if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                    ar = new CaixaFederal(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sicob(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                    ar = new CaixaFederal(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sindical(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                    ar = new CaixaFederal(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sigCB(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                }
                // BANCO DO BRASIL ------------------------------------------------------------------------------
            } else if (ArquivoRetorno.BANCO_BRASIL == scc.getContaBanco().getBanco().getId()) {
                if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                    ar = new BancoBrasil(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sicob(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                }
                // REAL ------------------------------------------------------------------------------
            } else if (ArquivoRetorno.REAL == scc.getContaBanco().getBanco().getId()) {
                if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                    ar = new Real(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sicob(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                }
                // ITAU ------------------------------------------------------------------------------
            } else if (ArquivoRetorno.ITAU == scc.getContaBanco().getBanco().getId()) {
                if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                    ar = new Itau(scc);
                    for (int i = 0; i < listaPasta.size(); i++) {
                        if ((Boolean) ((DataObject) listaPasta.get(i)).getArgumento0()) {
                            genericaRetorno.addAll(ar.sicob(false, ((String) ((DataObject) listaPasta.get(i)).getArgumento1())));
                        }
                    }
                } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                }

            }
            Collection listaComparas = new ArrayList<ComparaMovimentos>();
            ComparaMovimentos compara = null;
            List<Movimento> movs = null;
            String arqNumero = "";
            String movNumero = "";
            String vencimento = "";
            Boleto boleto = new Boleto();

            if (!genericaRetorno.isEmpty()) {
                for (int i = 0; i < genericaRetorno.size(); i++) {
                    movs = db.pesquisaMovPorNumDocumentoListBaixadoArr(genericaRetorno.get(i).getNossoNumero(), scc.getId());
                    if (!movs.isEmpty()) {
                        boleto = db.pesquisaBoletos(movs.get(0).getNrCtrBoleto());
                        arqNumero = genericaRetorno.get(i).getNossoNumero();
                        movNumero = boleto.getBoletoComposto();
                        vencimento = genericaRetorno.get(i).getDataVencimento();
                        if (vencimento.isEmpty()) {
                            vencimento = "00000000";
                        }
                        compara = new ComparaMovimentos(arqNumero.substring(arqNumero.length() - movNumero.length(), arqNumero.length()),
                                Moeda.converteUS$(String.valueOf(Moeda.divisao(Integer.parseInt(genericaRetorno.get(i).getValorPago()), 100))),
                                DataHoje.colocarBarras(genericaRetorno.get(i).getDataPagamento()),
                                DataHoje.colocarBarras(vencimento),
                                movNumero,
                                movs.get(0).getValor(),
                                movs.get(0).getBaixa().getImportacao(),
                                movs.get(0).getVencimento(),
                                movs.get(0).getPessoa().getDocumento(),
                                movs.get(0).getPessoa().getNome(),
                                movs.get(0).getServicos().getDescricao(),
                                movs.get(0).getTipoServico().getDescricao(),
                                genericaRetorno.get(i).getNomePasta(),
                                genericaRetorno.get(i).getNomeArquivo());
                    } else {
                        movs = db.pesquisaMovPorNumDocumentoList(genericaRetorno.get(i).getNossoNumero(), DataHoje.converte(DataHoje.colocarBarras(genericaRetorno.get(i).getDataVencimento())), scc.getId());
                        if (!movs.isEmpty()) {
                            boleto = db.pesquisaBoletos(movs.get(0).getNrCtrBoleto());
                            arqNumero = genericaRetorno.get(i).getNossoNumero();
                            movNumero = boleto.getBoletoComposto();
                            vencimento = genericaRetorno.get(i).getDataVencimento();
                            if (vencimento.isEmpty()) {
                                vencimento = "00000000";
                            }
                            compara = new ComparaMovimentos(arqNumero.substring(arqNumero.length() - movNumero.length(), arqNumero.length()),
                                    Moeda.converteUS$(String.valueOf(Moeda.divisao(Integer.parseInt(genericaRetorno.get(i).getValorPago()), 100))),
                                    DataHoje.colocarBarras(genericaRetorno.get(i).getDataPagamento()),
                                    DataHoje.colocarBarras(vencimento),
                                    boleto.getBoletoComposto(),
                                    movs.get(0).getValor(),
                                    "",
                                    movs.get(0).getVencimento(),
                                    movs.get(0).getPessoa().getDocumento(),
                                    movs.get(0).getPessoa().getNome(),
                                    movs.get(0).getServicos().getDescricao(),
                                    movs.get(0).getTipoServico().getDescricao(),
                                    genericaRetorno.get(i).getNomePasta(),
                                    genericaRetorno.get(i).getNomeArquivo());

                        } else {
                            vencimento = genericaRetorno.get(i).getDataVencimento();
                            if (vencimento.isEmpty()) {
                                vencimento = "00000000";
                            }
                            compara = new ComparaMovimentos(genericaRetorno.get(i).getNossoNumero(),
                                    Moeda.converteUS$(String.valueOf(Moeda.divisao(Integer.parseInt(genericaRetorno.get(i).getValorPago()), 100))),
                                    DataHoje.colocarBarras(genericaRetorno.get(i).getDataPagamento()),
                                    DataHoje.colocarBarras(vencimento),
                                    "",
                                    0,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    genericaRetorno.get(i).getNomePasta(),
                                    genericaRetorno.get(i).getNomeArquivo());
                        }
                    }
                    listaComparas.add(compara);
                    movs = null;
                }
                try {
                    FacesContext faces = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
                    byte[] arquivo = new byte[0];
                    JasperReport jasper = null;

                    File fl = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/COMPARATIVO_MOVIMENTO.jasper"));
                    jasper = (JasperReport) JRLoader.loadObject(fl);

                    JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listaComparas);
                    JasperPrint print = JasperFillManager.fillReport(
                            jasper,
                            null,
                            dtSource);
                    arquivo = JasperExportManager.exportReportToPdf(print);
                    response.setContentType("application/pdf");
                    response.setContentLength(arquivo.length);
                    ServletOutputStream saida = response.getOutputStream();
                    saida.write(arquivo, 0, arquivo.length);
                    saida.flush();
                    saida.close();

                    FacesContext.getCurrentInstance().responseComplete();
                    Download download = new Download(
                            "Movimentos Baixados.pdf",
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/retornoBanco.jsf"),
                            "pdf",
                            FacesContext.getCurrentInstance());
                    download.baixar();
                } catch (JRException | IOException e) {
                    System.err.println("O arquivo não foi gerado corretamente! Erro: " + e.getMessage());
                }

            }
        }
        return null;
    }

    public void refreshPastas() {
        carregaPastas = true;
    }

    public void atualizarBoletoCadastro() {
        if (listaDocumentos.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existe nenhum Documento para ser Atualizado!");
            return;
        }
        MovimentoDao db = new MovimentoDao();
        List<Movimento> lm = new ArrayList();
        List<Juridica> l_juridicax = new ArrayList();
        JuridicaDao dbj = new JuridicaDao();

        Dao dao = new Dao();
        for (DataObject listaDocumento : listaDocumentos) {
            DocumentoInvalido di = (DocumentoInvalido) dao.find(new DocumentoInvalido(), ((DocumentoInvalido) listaDocumento.getArgumento3()).getId());

            if (!(Boolean) listaDocumento.getArgumento4()) {
                lm = db.pesquisaMovimentoCadastrado(di.getDocumentoInvalido());
                l_juridicax = dbj.pesquisaJuridicaPorDoc(listaDocumento.getArgumento1().toString());
                if (!l_juridicax.isEmpty() && !lm.isEmpty()) {

                    dao.openTransaction();
                    for (Movimento lmovimento : lm) {

                        lmovimento.getLote().setPessoa(l_juridicax.get(0).getPessoa());

                        if (!dao.update(lmovimento.getLote())) {
                            dao.rollback();
                            GenericaMensagem.error("Erro", "Não foi possível atualizar Lista!");
                            return;
                        }

                        lmovimento.setPessoa(l_juridicax.get(0).getPessoa());
                        lmovimento.setBeneficiario(l_juridicax.get(0).getPessoa());
                        lmovimento.setTitular(l_juridicax.get(0).getPessoa());;

                        if (!dao.update(lmovimento)) {
                            dao.rollback();
                            GenericaMensagem.error("Erro", "Não foi possível atualizar Lista!");
                            return;
                        }

                    }

                    di.setChecado(true);
                    if (!dao.update(di)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível atualizar Lista!");
                        return;
                    }
                    dao.commit();

                    GenericaMensagem.info("OK", "Documento " + listaDocumento.getArgumento1() + " atualizado!");
                }
            } else if ((Boolean) listaDocumento.getArgumento0()) {
                dao.openTransaction();

                di.setChecado(true);

                if (!dao.update(di)) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível atualizar Lista!");
                    return;
                }

                dao.commit();

                GenericaMensagem.info("OK", "Documento " + listaDocumento.getArgumento1() + " atualizado!");
            }
        }

        loadListaDocumentos();
    }

    public void enviarArquivoBaixar() {
        try {
            Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
            Object objs[] = caminhoServico();
            String caminhoCompleto = (String) objs[0];
            ContaCobranca scc = (ContaCobranca) objs[1];
            String result = "";
            ArquivoRetorno arquivoRetorno;

            if (!listaArquivosPendentes.isEmpty()) {
//                if (!outros) {
                // CAIXA FEDERAL ------------------------------------------------------------------------------
                switch (scc.getContaBanco().getBanco().getId()) {
                    case ArquivoRetorno.CAIXA_FEDERAL:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                arquivoRetorno = new CaixaFederal(scc);
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                arquivoRetorno = new CaixaFederal(scc);
                                result = arquivoRetorno.darBaixaSindical(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SIGCB:
                                arquivoRetorno = new CaixaFederal(scc);
                                result = arquivoRetorno.darBaixaSigCB(caminhoCompleto, usuario);
                                break;
                            default:
                                break;
                        }
                        // BANCO DO BRASIL ------------------------------------------------------------------------------
                        break;
                    case ArquivoRetorno.BANCO_BRASIL:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                arquivoRetorno = new BancoBrasil(scc);
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                result = "NÃO EXISTE SINDICAL PARA ESTA CONTA!";
                                break;
                            case ArquivoRetorno.SIGCB:
                                result = "NÃO EXISTE SIGCB PARA ESTA CONTA!";
                                break;
                            default:
                                break;
                        }
                        // REAL ------------------------------------------------------------------------------
                        break;
                    case ArquivoRetorno.REAL:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                arquivoRetorno = new Real(scc);
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                result = "NÃO EXISTE SINDICAL PARA ESTA CONTA!";
                                break;
                            case ArquivoRetorno.SIGCB:
                                result = "NÃO EXISTE SIGCB PARA ESTA CONTA!";
                                break;
                            default:
                                break;
                        }
                        // ITAU --------------------------------------------------------------------------------
                        break;
                    case ArquivoRetorno.ITAU:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                arquivoRetorno = new Itau(scc);
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                result = "NÃO EXISTE SINDICAL PARA ESTA CONTA!";
                                break;
                            case ArquivoRetorno.SIGCB:
                                result = "NÃO EXISTE SIGCB PARA ESTA CONTA!";
                                break;
                            default:
                                break;
                        }
                        break;
                    case ArquivoRetorno.SANTANDER:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                arquivoRetorno = new Santander(scc);
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                result = "NÃO EXISTE SINDICAL PARA ESTA CONTA!";
                                break;
                            case ArquivoRetorno.SIGCB:
                                result = "NÃO EXISTE SIGCB PARA ESTA CONTA!";
                                break;
                            default:
                                break;
                        }
                        break;
                    case ArquivoRetorno.SICOOB:
                        switch (scc.getLayout().getId()) {
                            case ArquivoRetorno.SICOB:
                                if (tipo.equals("400")) {
                                    arquivoRetorno = new Sicoob400(scc);
                                } else {
                                    arquivoRetorno = new Sicoob240(scc);
                                }
                                result = arquivoRetorno.darBaixaSicob(caminhoCompleto, usuario);
                                break;
                            case ArquivoRetorno.SINDICAL:
                                result = "NÃO EXISTE SINDICAL PARA ESTA CONTA!";
                                break;
                            case ArquivoRetorno.SIGCB:
                                result = "NÃO EXISTE SIGCB PARA ESTA CONTA!";
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }

            GenericaMensagem.info("Sucesso", "Arquivos Baixados");
            loadListaArquivosBaixar();
            loadListaDocumentos();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public Object[] caminhoServico() {
        Object obj[] = new Object[2];
        if (contaCobranca.getId() != -1) {
            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/retorno");
            // LAYOUT 2 SINDICAL
            if (contaCobranca.getLayout().getId() == 2) {
                //caminho = caminho +"/"+ contaCobranca.getApelido()+"_"+contaCobranca.getSicasSindical();
                caminho = caminho + "/" + contaCobranca.getSicasSindical();
            } else {
                //caminho = caminho +"/"+ contaCobranca.getApelido()+"_"+contaCobranca.getCodCedente();
                caminho = caminho + "/" + contaCobranca.getCodCedente();
            }
            obj[0] = caminho;
            obj[1] = contaCobranca;
        }
        return obj;
    }

    public Object[] caminhoServicoPendente() {
        Object obj[] = new Object[2];
        if (contaCobranca.getId() != -1) {
            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/retorno");
            // LAYOUT 2 SINDICAL
            if (contaCobranca.getLayout().getId() == 2) {
                //caminho = caminho +"/"+ contaCobranca.getApelido()+"_"+contaCobranca.getSicasSindical();
                caminho = caminho + "/" + contaCobranca.getSicasSindical();
                caminho = caminho + "/pendentes";
            } else {
                //caminho = caminho +"/"+ contaCobranca.getApelido()+"_"+contaCobranca.getCodCedente();
                caminho = caminho + "/" + contaCobranca.getCodCedente();
                caminho = caminho + "/pendentes";
            }
            obj[0] = caminho;
            obj[1] = contaCobranca;
        }
        return obj;
    }

    public boolean verificaArquivos(File filex, ContaCobranca scc) {
        try {
            Reader reader = new FileReader(filex);
            BufferedReader buffReader = new BufferedReader(reader);
            String linha = buffReader.readLine();

            reader.close();
            buffReader.close();
            // CAIXA FEDERAL --------------------------------------------------------------------------------
            switch (scc.getContaBanco().getBanco().getId()) {
                case ArquivoRetorno.CAIXA_FEDERAL:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        //String xxx = linha.substring(59, 70);
                        if (linha.substring(59, 70).equals(scc.getCodCedente())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        if (linha.substring(33, 38).equals(scc.getSicasSindical())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        if (linha.substring(58, 64).equals(scc.getCodCedente())) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    // BANCO DO BRASIL ------------------------------------------------------------------------------
                    break;
                case ArquivoRetorno.BANCO_BRASIL:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        if (linha.substring(64, 70).equals(scc.getCodCedente()) || linha.substring(65, 70).equals(scc.getCodCedente()) || linha.substring(66, 70).equals(scc.getCodCedente())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        return false;
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        return false;
                    }
                    // REAL --------------------------------------------------------------------------------------
                    break;
                case ArquivoRetorno.REAL:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        if (linha.substring(63, 70).equals(scc.getCodCedente())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        return false;
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        return false;
                    }
                    // ITAU --------------------------------------------------------------------------------------
                    break;
                case ArquivoRetorno.ITAU:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        if (linha.substring(32, 37).equals(scc.getCodCedente())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        return false;
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        return false;
                    }
                    // SANTANDER ----------------------------------------------------------------------------------
                    break;
                case ArquivoRetorno.SANTANDER:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        int codc = Integer.valueOf(linha.toString().substring(53, 61));
                        int compara = Integer.valueOf(scc.getCodCedente());
                        if (codc == compara) {
                            return true;
                        } else {
                            return false;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        return false;
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        return false;
                    }
                    // BANCOOB SICOOB ------------------------------------------------------------------------------
                    break;
                case ArquivoRetorno.SICOOB:
                    if (ArquivoRetorno.SICOB == scc.getLayout().getId()) {
                        if (ArquivoRetorno.tipo(filex.getAbsolutePath()).equals("400")) {
                            int codc = Integer.valueOf(linha.substring(31, 40));
                            int compara = Integer.valueOf(scc.getCodCedente());
                            tipo = "400";
                            return codc == compara;
                        } else {
                            int codc = Integer.valueOf(linha.substring(59, 71));
                            int compara = Integer.valueOf(scc.getContaBanco().getConta().replace(".", "").replace("-", ""));
                            tipo = "250";
                            return codc == compara;
                        }
                    } else if (ArquivoRetorno.SINDICAL == scc.getLayout().getId()) {
                        return false;
                    } else if (ArquivoRetorno.SIGCB == scc.getLayout().getId()) {
                        return false;
                    }   break;
                default:
                    break;
            }
        } catch (IOException | NumberFormatException e) {
            e.getMessage();
        }
        return false;
    }

    public void imprimirDocumentos() {
        if (listaDocumentos.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existe nenhum Documento para ser Impresso!");
            return;
        }
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            byte[] arquivo;

            Collection listaDocs = new ArrayList();

            File fl = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/DOCUMENTOINVALIDO.jasper"));

            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);

            try {
                for (int i = 0; i < listaDocumentos.size(); i++) {
                    //listaDocs.add((DocumentoInvalido)((DataObject) listaDocumentos.get(i)).getArgumento1());
                    listaDocs.add(new DocumentoInvalido(
                            -1,
                            listaDocumentos.get(i).getArgumento1().toString(),
                            true,
                            DataHoje.converteData((Date) ((DataObject) listaDocumentos.get(i)).getArgumento5())));
                }

                JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listaDocs);
                JasperPrint print = JasperFillManager.fillReport(
                        jasper,
                        null,
                        dtSource);
                arquivo = JasperExportManager.exportReportToPdf(print);
                response.setContentType("application/pdf");
                response.setContentLength(arquivo.length);
                ServletOutputStream saida = response.getOutputStream();
                saida.write(arquivo, 0, arquivo.length);
                saida.flush();
                saida.close();
            } catch (JRException | IOException erro) {
                //System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                GenericaMensagem.error("Erro", erro.getMessage());
            }
        } catch (Exception erro) {
            //System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            GenericaMensagem.error("Erro", erro.getMessage());
        }

        FacesContext.getCurrentInstance().responseComplete();
        Download download = new Download(
                "Documentos Inválidos.pdf",
                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/retornoBanco.jsf"),
                "pdf",
                FacesContext.getCurrentInstance());
        download.baixar();
    }

    public void imprimirDetalhe() {
        if (listaDetalheRetornoBanco.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existe nenhum Detalhe para ser Impresso!");
            return;
        }
        List lista_detalhes = new ArrayList();

        for (ObjectDetalheRetorno dr : listaDetalheRetornoBanco) {
            if (dr.getMovimento() != null) {
                lista_detalhes.add(
                        new DetalheRetornoArr(
                                dr.getMovimento().getPessoa().getDocumento(),
                                dr.getMovimento().getPessoa().getNome(),
                                dr.getMovimento().getDocumento(),
                                dr.getMovimento().getBaixa() != null ? dr.getMovimento().getBaixa().getDtImportacao() : null
                        )
                );
            } else {
                lista_detalhes.add(
                        new DetalheRetornoArr(
                                "",
                                dr.getDetalhe(),
                                "",
                                null
                        )
                );
            }
        }
        Jasper.printReports("DETALHE_RETORNO_ARR.jasper", "Detalhe do Retorno", lista_detalhes);
    }

    public int getIndex_contribuicao() {
        return index_contribuicao;
    }

    public void setIndex_contribuicao(int index_contribuicao) {
        this.index_contribuicao = index_contribuicao;
    }

    public String getMsgOk() {
        return msgOk;
    }

    public void setMsgOk(String msgOk) {
        this.msgOk = msgOk;
    }

    public Object getRetornoBanco() {
        retornoBanco = new Integer(-1);
        return retornoBanco;
    }

    public void setRetornoBanco(Object retornoBanco) {
        this.retornoBanco = retornoBanco;
    }

    public boolean isOutros() {
        return outros;
    }

    public void setOutros(boolean outros) {
        this.outros = outros;
    }

    public String getLblPendente() {
        if (contaCobranca.getId() != -1 && !listaServicos.isEmpty()) {
            if (contaCobranca.getLayout().getId() == 2) {
                //caminho = caminho +"/"+ contaCobranca.getApelido()+"_"+contaCobranca.getSicasSindical();
                lblPendente = contaCobranca.getApelido() + "-" + contaCobranca.getSicasSindical();
            } else {
                lblPendente = contaCobranca.getApelido() + "-" + contaCobranca.getCodCedente();
            }
        }
        return lblPendente;
    }

    public void setLblPendente(String lblPendente) {
        this.lblPendente = lblPendente;
    }

    public ContaCobranca getContaCobranca() {
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    public List<String> getListaArquivosPendentes() {
        return listaArquivosPendentes;
    }

    public void setListaArquivosPendentes(List<String> listaArquivosPendentes) {
        this.listaArquivosPendentes = listaArquivosPendentes;
    }

    public List<DataObject> getListaDocumentos() {
        return listaDocumentos;
    }

    public void setListaDocumentos(List<DataObject> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    public List<ObjectDetalheRetorno> getListaDetalheRetornoBanco() {
        if (GenericaSessao.exists("detalhes_retorno_banco")) {
            listaDetalheRetornoBanco = GenericaSessao.getList("detalhes_retorno_banco", true);
        }
        return listaDetalheRetornoBanco;
    }

    public void setListaDetalheRetornoBanco(List<ObjectDetalheRetorno> listaDetalheRetornoBanco) {
        this.listaDetalheRetornoBanco = listaDetalheRetornoBanco;
    }

    public class DetalheRetornoArr {

        private Object cnpj;
        private Object empresa;
        private Object boleto;
        private Object importacao;

        public DetalheRetornoArr(Object cnpj, Object empresa, Object boleto, Object importacao) {
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.boleto = boleto;
            this.importacao = importacao;
        }

        public Object getCnpj() {
            return cnpj;
        }

        public void setCnpj(Object cnpj) {
            this.cnpj = cnpj;
        }

        public Object getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Object empresa) {
            this.empresa = empresa;
        }

        public Object getBoleto() {
            return boleto;
        }

        public void setBoleto(Object boleto) {
            this.boleto = boleto;
        }

        public Object getImportacao() {
            return importacao;
        }

        public void setImportacao(Object importacao) {
            this.importacao = importacao;
        }
    }
}
