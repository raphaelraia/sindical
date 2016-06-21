package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
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

    private Boolean renderedPhotoCropper = false;
    private String update = "";
    private String savePath = "";
    private String nameFile = "";
    private Fisica fisica = null;
    private Juridica juridica = null;

    private CroppedImage croppedImage;

    public void open(String aSavePath, String aUpdate) {
        savePath = aSavePath;
        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public void openAndSave(Pessoa aPessoa, String aUpdate) {
        FisicaDao fisicaDB = new FisicaDao();
        Fisica fisica_x = fisicaDB.pesquisaFisicaPorPessoa(aPessoa.getId());
        if (fisica_x != null) {
            fisica = fisica_x;
        } else {
            JuridicaDao juridicaDB = new JuridicaDao();
            Juridica juridica_x = juridicaDB.pesquisaJuridicaPorPessoa(aPessoa.getId());
            if (juridica_x != null) {
                juridica = juridica_x;
            }
        }

        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public void openAndSave(Fisica aFisica, String aUpdate) {
        fisica = aFisica;
        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public void openAndSave(Juridica aJuridica, String aUpdate) {
        juridica = aJuridica;
        update = aUpdate;
        renderedPhotoCropper = true;

        PF.openDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public void close() {
        renderedPhotoCropper = false;
        PF.closeDialog("dlg_photo_cropper");
        PF.update("form_photo_cropper");
    }

    public void unload() {
        savePath = "";
        update = "";
        nameFile = "";

        fisica = null;
        juridica = null;
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

        if (fisica == null && juridica == null) {
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

            if (fisica != null) {
                String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/" + nameTemp + ".png";
                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, data);
                } catch (IOException e) {
                    e.getMessage();
                }

                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + fisica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                fisica.setFoto(nameTemp);
                new Dao().update(fisica, true);
            } else {
                String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + juridica.getPessoa().getId() + "/" + nameTemp + ".png";
                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, data);
                } catch (IOException e) {
                    e.getMessage();
                }
                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + juridica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                juridica.setFoto(nameTemp);
                new Dao().update(juridica, true);
            }
        }

        PF.closeDialog("dlg_photo_cropper");
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public Boolean getRenderedPhotoCropper() {
        return renderedPhotoCropper;
    }

    public void setRenderedPhotoCropper(Boolean renderedPhotoCropper) {
        this.renderedPhotoCropper = renderedPhotoCropper;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }
}
