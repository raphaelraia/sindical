package br.com.rtools.digitalizacao.beans;

import br.com.rtools.digitalizacao.Documento;
import br.com.rtools.digitalizacao.GrupoDigitalizacao;
import br.com.rtools.digitalizacao.dao.DigitalizacaoDao;
import br.com.rtools.digitalizacao.dao.GrupoDigitalizacaoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.LinhaArquivo;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public final class DigitalizacaoBean implements Serializable {

    private Documento documento;
    private Pessoa pessoa;
    private Integer idGrupo;
    private List<SelectItem> listaGrupo;
    private List<Documento> listaDocumentos;
    private List<LinhaArquivo> listaArquivos;
    private LinhaArquivo linhaArquivoExcluir;
    private Boolean acessoExterno;

    @PostConstruct
    public void init() {
        documento = new Documento();
        pessoa = new Pessoa();
        idGrupo = 0;
        listaGrupo = new ArrayList();
        listaDocumentos = new ArrayList();
        listaArquivos = new ArrayList();
        linhaArquivoExcluir = new LinhaArquivo();
        acessoExterno = false;
        loadListaGrupo();
        loadListaDocumentos();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("digitalizacaoBean");
    }

    public void view(LinhaArquivo la) throws IOException {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String url = request.getScheme() + "://" + request.getServerName() + ":" + String.valueOf(request.getServerPort()) + "/";
        // CORRIGE URLS COM ACENTUAÇÃO
        // URLEncoder.encode(url, "UTF-8")

        // POR CONTA DO ENCODE OS ESPAÇOS FICARAM COM UM +
        // replace("+", "%20") PARA CORRIGIR ESSE CASO
        // PORÉM SE NO NOME DO ARQUIVO CONTER UM + NÃO IRÁ ABRIR O ARQUIVO
        url += "Sindical/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId() + "/" + URLEncoder.encode(la.getNameFile(), "UTF-8").replace("+", "%20");

        //url += "Sindical/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId()+"/"+la.getNameFile();
        //url += "Sindical/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId()+"/"+URLEncoder.encode(la.getNameFile(), "UTF-8");
        response.sendRedirect(url);
    }

    public void download(LinhaArquivo la) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId();
        Download d = new Download(la.getNameFile(), path, la.getMimeType(), FacesContext.getCurrentInstance());
        d.baixar();
    }

    public void clickExcluir(LinhaArquivo la) {
        linhaArquivoExcluir = la;
    }

    public void excluirArquivo() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + linhaArquivoExcluir.getDocFile().getPessoa().getId() + "/" + linhaArquivoExcluir.getDocFile().getId() + "/" + linhaArquivoExcluir.getNameFile();
        File file = new File(path);

        if (!FileUtils.deleteQuietly(file)) {
            GenericaMensagem.fatal("Atenção", "NÃO FOI POSSÍVEL EXCLUIR ARQUIVO!");
        }

        String delete_log = "Arquivo Excluido:  " + linhaArquivoExcluir.getNameFile();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("dig_documento");
        novoLog.setCodigo(documento.getId());

        novoLog.delete(
                delete_log
        );

        verDocumentos(linhaArquivoExcluir.getDocFile());
        linhaArquivoExcluir = new LinhaArquivo();
        GenericaMensagem.info("Sucesso", "ARQUIVO EXCLUÍDO!");
    }

    public void verDocumentos(Documento linha) {
        listaArquivos = new ArrayList();

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + linha.getPessoa().getId() + "/" + linha.getId() + "/";
        File file = new File(path);

        File lista_datas[] = file.listFiles();

        if (lista_datas != null) {
            for (File lista_data : lista_datas) {
                String ext = FilenameUtils.getExtension(lista_data.getPath()).toUpperCase();
                String mimeType = servletContext.getMimeType(lista_data.getPath());
                listaArquivos.add(new LinhaArquivo("fileExtension" + ext + ".png", lista_data.getName(), mimeType, linha));
            }
        }
    }

    public void saveDigitalizacao() {
        /*
        // NÃO É OBRIGATÓRIO
         if (documento.getAssunto().getDescricao().isEmpty() || documento.getAssunto().getDescricao().length() < 3) {
         GenericaMensagem.fatal("Atenção", "DIGITE UM ASSUNTO PARA OS DOCUMENTOS!");
         return;
         }

         if (documento.getTitulo().isEmpty() || documento.getTitulo().length() < 3) {
         GenericaMensagem.fatal("Atenção", "DIGITE UM TÍTULO PARA OS DOCUMENTOS!");
         return;
         }
         */
        if (pessoa.getId() == -1) {
            GenericaMensagem.fatal("Atenção", "PESQUISE UMA PESSOA!");
            return;
        }
        if (idGrupo == null) {
            GenericaMensagem.fatal("Validação", "Informar / cadastrar grupo!");
            return;
        }

        Dao dao = new Dao();
        documento.getAssunto().setGrupo((GrupoDigitalizacao) dao.find(new GrupoDigitalizacao(), idGrupo));
        documento.setPessoa(pessoa);

        String save_log
                = "Data Emissão: " + documento.getDtEmissaoString() + " \n "
                + "Grupo: " + documento.getAssunto().getGrupo().getDescricao() + " \n "
                + "Assunto: " + documento.getAssunto().getDescricao() + " \n "
                + "Titulo: " + documento.getTitulo() + " \n "
                + "Historico: " + documento.getHistorico() + " \n "
                + "Pessoa: " + documento.getPessoa().getNome();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("dig_documento");

        dao.openTransaction();
        if (documento.getId() == -1) {
            if (!dao.save(documento.getAssunto())) {
                dao.rollback();
                GenericaMensagem.fatal("Erro", "NÃO FOI POSSÍVEL SALVAR ASSUNTO!");
                return;
            }

            if (!dao.save(documento)) {
                dao.rollback();
                GenericaMensagem.fatal("Erro", "NÃO FOI POSSÍVEL SALVAR DOCUMENTO!");
                return;
            }

            novoLog.setCodigo(documento.getId());
            novoLog.save(
                    save_log
            );
        } else {
            if (!dao.update(documento.getAssunto())) {
                dao.rollback();
                GenericaMensagem.fatal("Erro", "NÃO FOI POSSÍVEL ATUALIZAR ASSUNTO!");
                return;
            }

            if (!dao.update(documento)) {
                dao.rollback();
                GenericaMensagem.fatal("Erro", "NÃO FOI POSSÍVEL ATUALIZAR DOCUMENTO!");
                return;
            }

            Documento doc = (Documento) new Dao().find(documento);
            String updade_log
                    = "Data Emissão: " + doc.getDtEmissaoString() + " \n "
                    + "Grupo: " + doc.getAssunto().getGrupo().getDescricao() + " \n "
                    + "Assunto: " + doc.getAssunto().getDescricao() + " \n "
                    + "Titulo: " + doc.getTitulo() + " \n "
                    + "Historico: " + doc.getHistorico() + " \n "
                    + "Pessoa: " + doc.getPessoa().getNome();

            novoLog.setCodigo(documento.getId());
            novoLog.update(save_log, updade_log);
        }

        if (!Diretorio.criar("documentos/" + pessoa.getId() + "/" + documento.getId(), true)) { // PASTA ex. resources/cliente/sindical/documentos/_id_pessoa/_id_documento
            dao.rollback();
            GenericaMensagem.fatal("Erro", "NÃO FOI POSSÍVEL CRIAR PASTA DE DOCUMENTOS!");
            return;
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "DOCUMENTO SALVO!");
        loadListaDocumentos();
    }

    public void novo() {
        documento = new Documento();
        idGrupo = 0;
        loadListaDocumentos();
    }

    public void excluir() {
        if (documento.getId() == -1) {
            GenericaMensagem.fatal("Atenção", "NÃO EXISTE DOCUMENTO PARA SER EXCLUÍDO");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(dao.find(documento))) {
            dao.rollback();
            GenericaMensagem.fatal("Atenção", "NÃO FOI POSSÍVEL EXCLUIR DOCUMENTOS!");
            return;
        }

        if (!dao.delete(dao.find(documento.getAssunto()))) {
            dao.rollback();
            GenericaMensagem.fatal("Atenção", "NÃO FOI POSSÍVEL EXCLUIR ASSUNTO!");
            return;
        }

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + documento.getPessoa().getId() + "/" + documento.getId() + "/";
        File file = new File(path);

        if (!FileUtils.deleteQuietly(file)) {
            dao.rollback();
            GenericaMensagem.fatal("Atenção", "NÃO FOI POSSÍVEL EXCLUIR ARQUIVOS!");
            return;
        }

        String delete_log
                = "Data Emissão: " + documento.getDtEmissaoString() + " \n "
                + "Grupo: " + documento.getAssunto().getGrupo().getDescricao() + " \n "
                + "Assunto: " + documento.getAssunto().getDescricao() + " \n "
                + "Titulo: " + documento.getTitulo() + " \n "
                + "Historico: " + documento.getHistorico() + " \n "
                + "Pessoa: " + documento.getPessoa().getNome();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("dig_documento");
        novoLog.setCodigo(documento.getId());

        novoLog.delete(delete_log);

        dao.commit();
        GenericaMensagem.info("Sucesso", "DIGITALIZAÇÃO EXCLUÍDA!");
        documento = new Documento();
        if (!acessoExterno) {
            pessoa = new Pessoa();
        }
        loadListaDocumentos();
    }

    public void editar(Documento linha) {
        documento = linha;
        pessoa = documento.getPessoa();
        idGrupo = documento.getAssunto().getGrupo().getId();
        loadListaDocumentos();
    }

    public String novoGeneric(Pessoa pe) throws IOException {
        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pagina("digitalizacao");
        DigitalizacaoBean digitalizacaoBean = new DigitalizacaoBean();
        digitalizacaoBean.load();
        digitalizacaoBean.setPessoa(pe);
        digitalizacaoBean.loadListaDocumentos();
        digitalizacaoBean.setAcessoExterno(true);
        GenericaSessao.put("digitalizacaoBean", digitalizacaoBean);
        return retorno;
    }

    public String editGeneric(Documento linha) throws IOException {
        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pagina("digitalizacao");
        DigitalizacaoBean digitalizacaoBean = new DigitalizacaoBean();
        digitalizacaoBean.load();
        digitalizacaoBean.setAcessoExterno(true);
        digitalizacaoBean.editar(linha);
        GenericaSessao.put("digitalizacaoBean", digitalizacaoBean);
        return retorno;
    }

    public void fileUpload(FileUploadEvent event) throws UnsupportedEncodingException {
        if (!Diretorio.criar("documentos/" + pessoa.getId() + "/" + documento.getId(), true)) { // PASTA ex. resources/cliente/sindical/documentos/_id_pessoa/_id_documento
            return;
        }

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        //String ext = FilenameUtils.getExtension("path");
        String nameFile = new String(event.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8");
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + pessoa.getId() + "/" + documento.getId() + "/" + nameFile;
        File file = new File(path);
        try {
            FileUtils.writeByteArrayToFile(file, event.getFile().getContents());
            String save_log = "Arquivo Enviado:  " + nameFile;

            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("dig_documento");
            novoLog.setCodigo(documento.getId());

            novoLog.save(
                    save_log
            );

        } catch (IOException e) {
            e.getMessage();
        }

        GenericaMensagem.info("Sucesso", "ARQUIVOS ENVIADOS!");
    }

    public void limparPesquisaPessoa() {
        pessoa = new Pessoa();
        documento = new Documento();
        loadListaDocumentos();
    }

    public void loadListaGrupo() {
        listaGrupo.clear();

        List<GrupoDigitalizacao> result = new GrupoDigitalizacaoDao().listaGrupo();

        for (int i = 0; i < result.size(); i++) {
            if (i == 0) {
                idGrupo = result.get(i).getId();
            }
            listaGrupo.add(
                    new SelectItem(
                            result.get(i).getId(),
                            result.get(i).getDescricao()
                    )
            );
        }
    }

    public void loadListaDocumentos() {
        listaDocumentos.clear();

        DigitalizacaoDao dao = new DigitalizacaoDao();

        if (pessoa.getId() == -1) {
            listaDocumentos = dao.listaDocumento();
        } else {
            listaDocumentos = dao.listaDocumento(pessoa.getId());
        }

        // ATUALIZA LISTA DE DOCUMENTOS COM PESSOA FÍSICA NA SESSÃO
        if (GenericaSessao.exists("fisicaBean")) {
            // ((FisicaBean) GenericaSessao.getObject("fisicaBean")).loadListaDocumentos();
        }

        // ATUALIZA LISTA DE DOCUMENTOS COM PESSOA JURÍDICA NA SESSÃO
        if (GenericaSessao.exists("juridicaBean")) {
            ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).loadListaDocumentos();
        }
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<SelectItem> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<SelectItem> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    public List<Documento> getListaDocumentos() {
        return listaDocumentos;
    }

    public void setListaDocumentos(List<Documento> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    public List<LinhaArquivo> getListaArquivos() {
        return listaArquivos;
    }

    public void setListaArquivos(List<LinhaArquivo> listaArquivos) {
        this.listaArquivos = listaArquivos;
    }

    public LinhaArquivo getLinhaArquivoExcluir() {
        return linhaArquivoExcluir;
    }

    public void setLinhaArquivoExcluir(LinhaArquivo linhaArquivoExcluir) {
        this.linhaArquivoExcluir = linhaArquivoExcluir;
    }

    public void loadListDocumentos(Integer pessoa_id) {
        pessoa = (Pessoa) new Dao().find(new Pessoa(), pessoa_id);
        listaDocumentos = new DigitalizacaoDao().listaDocumento(pessoa_id);
    }

    public void load() {
        acessoExterno = true;
        documento = new Documento();
        idGrupo = null;
        listaGrupo = new ArrayList();
        listaDocumentos = new ArrayList();
        listaArquivos = new ArrayList();
        linhaArquivoExcluir = new LinhaArquivo();
        loadListaGrupo();
    }

    public Boolean getAcessoExterno() {
        return acessoExterno;
    }

    public void setAcessoExterno(Boolean acessoExterno) {
        this.acessoExterno = acessoExterno;
    }

}
