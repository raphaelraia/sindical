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
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class PhotoUpload implements Serializable {

    private Boolean renderedPhotoUpload = false;
    private String update = "";
    private String savePath = "";
    private String nameFile = "";
    private Fisica fisica = null;
    private Juridica juridica = null;

    public void open(String aSavePath, String aUpdate) {
        savePath = aSavePath;
        update = aUpdate;
        renderedPhotoUpload = true;

        PF.openDialog("dlg_photo_upload");
        PF.update("form_photo_upload");
    }

    public void openAndSave(Pessoa aPessoa, String aUpdate) {
        FisicaDB fisicaDB = new FisicaDBToplink();
        Fisica fisica_x = fisicaDB.pesquisaFisicaPorPessoa(aPessoa.getId());
        if (fisica_x != null) {
            fisica = fisica_x;
        } else {
            JuridicaDB juridicaDB = new JuridicaDBToplink();
            Juridica juridica_x = juridicaDB.pesquisaJuridicaPorPessoa(aPessoa.getId());
            if (juridica_x != null) {
                juridica = juridica_x;
            }
        }
        update = aUpdate;
        renderedPhotoUpload = true;

        PF.openDialog("dlg_photo_upload");
        PF.update("form_photo_upload");
    }

    public void openAndSave(Fisica aFisica, String aUpdate) {
        fisica = aFisica;
        update = aUpdate;
        renderedPhotoUpload = true;

        PF.openDialog("dlg_photo_upload");
        PF.update("form_photo_upload");
    }

    public void openAndSave(Juridica aJuridica, String aUpdate) {
        juridica = aJuridica;
        update = aUpdate;
        renderedPhotoUpload = true;

        PF.openDialog("dlg_photo_upload");
        PF.update("form_photo_upload");
    }

    public void unload() {
        savePath = "";
        update = "";
        nameFile = "";

        fisica = null;
        juridica = null;
    }

    public void close() {
        renderedPhotoUpload = false;
        PF.closeDialog("dlg_photo_upload");
        PF.update("form_photo_upload");
    }

    public void upload(FileUploadEvent event) {
        UUID uuidX = UUID.randomUUID();
        String nameTemp = uuidX.toString().replace("-", "_");
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

        nameFile = nameTemp;

        if (fisica == null && juridica == null) {
            String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/" + savePath + "/" + nameTemp + ".png";
            File file = new File(path);
            try {
                FileUtils.writeByteArrayToFile(file, event.getFile().getContents());
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
                    FileUtils.writeByteArrayToFile(file, event.getFile().getContents());
                } catch (IOException e) {
                    e.getMessage();
                }

                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("")+"resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + fisica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                fisica.setFoto(nameTemp);
                new Dao().update(fisica, true);
            } else {
                String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + juridica.getPessoa().getId() + "/" + nameTemp + ".png";
                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, event.getFile().getContents());
                } catch (IOException e) {
                    e.getMessage();
                }

                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("")+"resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + juridica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                juridica.setFoto(nameTemp);
                new Dao().update(juridica, true);
            }
        }

        PF.closeDialog("dlg_photo_upload");
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

    public Boolean getRenderedPhotoUpload() {
        return renderedPhotoUpload;
    }

    public void setRenderedPhotoUpload(Boolean renderedPhotoUpload) {
        this.renderedPhotoUpload = renderedPhotoUpload;
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

}
