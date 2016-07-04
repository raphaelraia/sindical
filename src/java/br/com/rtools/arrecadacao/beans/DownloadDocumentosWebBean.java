package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.CertificadoArquivos;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.dao.CertificadoArquivosDao;
import br.com.rtools.arrecadacao.dao.ConvencaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoPeriodoDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadeDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Zip;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ManagedBean
@SessionScoped
public class DownloadDocumentosWebBean implements Serializable {

    private List<CertificadoArquivos> listCertificadoArquivos;
    private Pessoa pessoa;
    private List<SelectItem> listConvencaoPeriodo;
    private List<SelectItem> listConvencao;
    private List<SelectItem> listGrupoCidade;

    private Integer idConvencaoPeriodo;
    private Integer idConvencao;
    private Integer idGrupoCidade;

    private Boolean analisar;
    private List<CertificadoArquivos> selectedCertificadoArquivos;

    public DownloadDocumentosWebBean() {
        analisar = false;
        pessoa = null;
        loadCertificadoArquivos();
        loadListConvencao();
        loadListGrupoCidade();
        loadListConvencaoPeriodo();
        selectedCertificadoArquivos = null;
    }

    public final void loadListConvencao() {
        listConvencao = new ArrayList();
        idConvencao = null;
        List<Convencao> list = new ConvencaoDao().listaConvencao();
        listConvencao.add(new SelectItem(null, "-- TODAS --"));
        for (int i = 0; i < list.size(); i++) {
            listConvencao.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
        loadListGrupoCidade();
    }

    public final void loadListGrupoCidade() {
        listGrupoCidade = new ArrayList();
        idGrupoCidade = null;
        listGrupoCidade.add(new SelectItem(null, "-- TODAS --"));
        if (idConvencao != null) {
            List<GrupoCidade> list = new GrupoCidadeDao().listaGrupoCidadePorConvencao(idConvencao + "");
            for (int i = 0; i < list.size(); i++) {
                listGrupoCidade.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        loadListConvencaoPeriodo();
    }

    public final void loadListConvencaoPeriodo() {
        loadListConvencaoPeriodo(true);
    }

    public final void loadListConvencaoPeriodo(Boolean clear) {
        if (clear) {
            selectedCertificadoArquivos = null;
        }
        listConvencaoPeriodo = new ArrayList();
        idConvencaoPeriodo = null;
        listConvencaoPeriodo.add(new SelectItem(null, "-- TODAS --"));
        if (idGrupoCidade != null) {
            List<ConvencaoPeriodo> list = new ConvencaoPeriodoDao().listaConvencaoPeriodo(idConvencao, idGrupoCidade);
            for (int i = 0; i < list.size(); i++) {
                listConvencaoPeriodo.add(new SelectItem(list.get(i).getId(), list.get(i).getReferenciaInicial() + " - " + list.get(i).getReferenciaFinal()));
            }
        }
        loadCertificadoArquivos();
    }

    public final void loadCertificadoArquivos() {
        listCertificadoArquivos = new ArrayList();
        listCertificadoArquivos = new CertificadoArquivosDao().findAll(analisar, pessoa != null ? pessoa.getId() : null, idConvencaoPeriodo);
    }

    public List<CertificadoArquivos> getListCertificadoArquivos() {
        return listCertificadoArquivos;
    }

    public void setListCertificadoArquivos(List<CertificadoArquivos> listCertificadoArquivos) {
        this.listCertificadoArquivos = listCertificadoArquivos;
    }

    public void view(CertificadoArquivos ca) throws IOException {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + String.valueOf(request.getServerPort()) + "/";
        // url += "Sindical/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId() + "/" + URLEncoder.encode(la.getNameFile(), "UTF-8").replace("+", "%20");
        response.sendRedirect(url);
    }

    public void download(CertificadoArquivos ca) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String mimeType = servletContext.getMimeType(ca.getPath() + "/" + ca.getFileName());
        Download d = new Download(ca.getFileName(), ca.getPath(), mimeType, FacesContext.getCurrentInstance());
        d.baixar();
        if (ca.getDtDownload() == null) {
            ca.setDtDownload(new Date());
            new Dao().update(ca, true);
        }
    }

    public void download() {
        if (selectedCertificadoArquivos != null && !selectedCertificadoArquivos.isEmpty()) {
            Dao dao = new Dao();
            dao.openTransaction();
            Zip zip = new Zip();
            File[] listFiles = new File[selectedCertificadoArquivos.size() + 1];
            for (int i = 0; i < selectedCertificadoArquivos.size(); i++) {
                if (selectedCertificadoArquivos.get(i) != null) {
                    File f = new File(selectedCertificadoArquivos.get(i).getPath() + "/" + selectedCertificadoArquivos.get(i).getFileName());
                    if (f.exists()) {
                        listFiles[i] = f;
                        if (selectedCertificadoArquivos.get(i).getDtDownload() == null) {
                            selectedCertificadoArquivos.get(i).setDtDownload(new Date());
                            dao.update(selectedCertificadoArquivos.get(i));
                        }
                    }
                }
            }
            if (listFiles.length > 0) {
                UUID uuidX = UUID.randomUUID();
                String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/arquivos/downloads/zip/");
                File f = new File(caminho);
                if (!f.exists()) {
                    f.mkdirs();
                }
                String uuid = "certificado_arquivos_" + uuidX.toString().replace("-", "_");
                File arqzip = new File(caminho + "/" + uuid + ".zip");
                try {
                    zip.zip(listFiles, arqzip);
                    Download download = new Download(
                            uuid + ".zip",
                            arqzip.getParent(),
                            "zip",
                            FacesContext.getCurrentInstance());
                    download.baixar();

                    arqzip.delete();
                    dao.commit();
                } catch (IOException ex) {
                    dao.rollback();
                    Logger.getLogger(DownloadDocumentosWebBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            selectedCertificadoArquivos = new ArrayList();
        }
    }

    public void deleteFile(CertificadoArquivos ca) {
        try {
            Dao dao = new Dao();
            dao.openTransaction();
            if (dao.delete(ca)) {
                String path = ca.getPath() + "/" + ca.getFileName();
                File file = new File(path);
                if (file.exists()) {
                    if (file.delete()) {
                        dao.commit();
                        loadCertificadoArquivos();
                        GenericaMensagem.info("Sucesso", "ARQUIVO REMOVIDO!");
                        return;
                    } else {
                        // dao.commit();                        
                    }
                } else {
                    dao.commit();
                    loadCertificadoArquivos();
                    return;
                }
            }
            dao.rollback();
        } catch (Exception e) {
        }
        GenericaMensagem.warn("Erro", "AO REMOVER ARQUIVO!");
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SelectItem> getListConvencaoPeriodo() {
        return listConvencaoPeriodo;
    }

    public void setListConvencaoPeriodo(List<SelectItem> listConvencaoPeriodo) {
        this.listConvencaoPeriodo = listConvencaoPeriodo;
    }

    public List<SelectItem> getListConvencao() {
        return listConvencao;
    }

    public void setListConvencao(List<SelectItem> listConvencao) {
        this.listConvencao = listConvencao;
    }

    public List<SelectItem> getListGrupoCidade() {
        return listGrupoCidade;
    }

    public void setListGrupoCidade(List<SelectItem> listGrupoCidade) {
        this.listGrupoCidade = listGrupoCidade;
    }

    public Integer getIdConvencaoPeriodo() {
        return idConvencaoPeriodo;
    }

    public void setIdConvencaoPeriodo(Integer idConvencaoPeriodo) {
        this.idConvencaoPeriodo = idConvencaoPeriodo;
    }

    public Integer getIdConvencao() {
        return idConvencao;
    }

    public void setIdConvencao(Integer idConvencao) {
        this.idConvencao = idConvencao;
    }

    public Integer getIdGrupoCidade() {
        return idGrupoCidade;
    }

    public void setIdGrupoCidade(Integer idGrupoCidade) {
        this.idGrupoCidade = idGrupoCidade;
    }

    public Boolean getAnalisar() {
        return analisar;
    }

    public void setAnalisar(Boolean analisar) {
        this.analisar = analisar;
    }

    public List<CertificadoArquivos> getSelectedCertificadoArquivos() {
        return selectedCertificadoArquivos;
    }

    public void setSelectedCertificadoArquivos(List<CertificadoArquivos> selectedCertificadoArquivos) {
        this.selectedCertificadoArquivos = selectedCertificadoArquivos;
    }

}
