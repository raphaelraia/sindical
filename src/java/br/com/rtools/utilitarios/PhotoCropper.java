package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.db.FisicaDB;
import br.com.rtools.pessoa.db.FisicaDBToplink;
import br.com.rtools.pessoa.db.JuridicaDB;
import br.com.rtools.pessoa.db.JuridicaDBToplink;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.CroppedImage;

@ManagedBean
@SessionScoped
public class PhotoCropper implements Serializable {

    private static Boolean renderedPhotoCropper = false;
    private static String update = "";
    private static String savePath = "";
    private static String nameFile = "";
    private static Pessoa pessoa = null;

    private CroppedImage croppedImage;

    public static void open(String aSavePath, String aUpdate) {
        savePath = aSavePath;
        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public static void openAndSave(Pessoa aPessoa, String aUpdate) {
        pessoa = aPessoa;
        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public static String getSavePath() {
        return savePath;
    }

    public static void setSavePath(String aSavePath) {
        savePath = aSavePath;
    }

    public static String getNameFile() {
        return nameFile;
    }

    public static void setNameFile(String aNameFile) {
        nameFile = aNameFile;
    }

    public static Pessoa getPessoa() {
        return pessoa;
    }

    public static void setPessoa(Pessoa aPessoa) {
        pessoa = aPessoa;
    }

    public Pessoa getPessoaView() {
        return pessoa;
    }

    public void setPessoaView(Pessoa aPessoa) {
        pessoa = aPessoa;
    }

    public void close() {
        renderedPhotoCropper = false;
        PF.closeDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public static void unload() {
        savePath = "";
        update = "";
        nameFile = "";
        pessoa = null;
    }

    public static Boolean getRenderedPhotoCropper() {
        return renderedPhotoCropper;
    }

    public static void setRenderedPhotoCropper(Boolean aRenderedPhotoCropper) {
        renderedPhotoCropper = aRenderedPhotoCropper;
    }

    public static String getUpdate() {
        return update;
    }

    public static void setUpdate(String aUpdate) {
        update = aUpdate;
    }

    public Boolean getRenderedPhotoCropperView() {
        return renderedPhotoCropper;
    }

    public void setRenderedPhotoCropperView(Boolean aRenderedPhotoCropperView) {
        renderedPhotoCropper = aRenderedPhotoCropperView;
    }

    public String getUpdateView() {
        return update;
    }

    public void setUpdateView(String aUpdateView) {
        update = aUpdateView;
    }

    public String getNameFileView() {
        return nameFile;
    }

    public void setNameFileView(String aNameFileView) {
        nameFile = aNameFileView;
    }

    public void crop() {
        if (croppedImage == null) {
            return;
        }

        UUID uuidX = UUID.randomUUID();
        String nameTemp = uuidX.toString().replace("-", "_");

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        byte[] data = croppedImage.getBytes();

        nameFile = nameTemp;

        if (pessoa == null) {
            String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/" + savePath + "/" + nameTemp + ".png";
            File file = new File(path);
            try {
                FileUtils.writeByteArrayToFile(file, data);
            } catch (IOException e) {
                e.getMessage();
            }
        } else {
            if (!Diretorio.criar("imagens/pessoa", true)) { // PASTA ex. resources/cliente/sindical/imagens/pessoa
                return;
            }
            String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + nameTemp + ".png";
            File file = new File(path);
            try {
                FileUtils.writeByteArrayToFile(file, data);
            } catch (IOException e) {
                e.getMessage();
            }

            if (file.exists()) {
                FisicaDB fisicaDB = new FisicaDBToplink();
                Fisica fisica = fisicaDB.pesquisaFisicaPorPessoa(pessoa.getId());

                if (fisica != null) {
                    // CASO QUEIRA REMOVER A FOTO ANTERIOR
                    File fotoAntiga = new File(servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + fisica.getFoto() + ".png");
                    if (fotoAntiga.exists()) {
                        FileUtils.deleteQuietly(fotoAntiga);
                    }

                    fisica.setFoto(nameTemp);
                    new Dao().update(fisica, true);
                } else {
                    JuridicaDB juridicaDB = new JuridicaDBToplink();
                    Juridica juridica = juridicaDB.pesquisaJuridicaPorPessoa(pessoa.getId());

                    // CASO QUEIRA REMOVER A FOTO ANTERIOR
                    File fotoAntiga = new File(servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + juridica.getFoto() + ".png");
                    if (fotoAntiga.exists()) {
                        FileUtils.deleteQuietly(fotoAntiga);
                    }

                    juridica.setFoto(nameTemp);
                    new Dao().update(juridica, true);
                }
            }
        }
        
        PhotoUpload.unload();
        PhotoCapture.unload();
        
        PF.closeDialog("dlg_photo_cropper");
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }
}
