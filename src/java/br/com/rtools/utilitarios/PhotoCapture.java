package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisPessoa;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.CaptureEvent;

@ManagedBean
@SessionScoped
public class PhotoCapture implements Serializable {

    private Boolean renderedPhotoCapture = false;
    private String update = "";
    private String savePath = "";
    private String nameFile = "";
    private Fisica fisica = null;
    private Juridica juridica = null;
    private SisPessoa sisPessoa = null;
    private String tipo = "";

    public void open(String aSavePath, String aUpdate) {
        unload();
        savePath = aSavePath;
        update = aUpdate;
        renderedPhotoCapture = true;

        PF.openDialog("dlg_photo_capture");
        PF.update("form_photo_capture");
    }

    public void openAndSave(Pessoa aPessoa, String aUpdate) {
        unload();
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
        renderedPhotoCapture = true;

        PF.openDialog("dlg_photo_capture");
        // PF.update("form_photo_capture");
    }

    public void openAndSave(Fisica aFisica, String aUpdate) {
        unload();
        fisica = aFisica;
        update = aUpdate;
        renderedPhotoCapture = true;

        PF.openDialog("dlg_photo_capture");
        // PF.update("form_photo_capture");
    }

    public void openAndSave(Juridica aJuridica, String aUpdate) {
        unload();
        juridica = aJuridica;
        update = aUpdate;
        renderedPhotoCapture = true;

        PF.openDialog("dlg_photo_capture");
        // PF.update("form_photo_capture");
    }

    public void openAndSave(SisPessoa aSisPessoa, String aTipo, String aUpdate) {
        unload();
        sisPessoa = aSisPessoa;
        tipo = aTipo;
        update = aUpdate;
        renderedPhotoCapture = true;

        PF.openDialog("dlg_photo_capture");
        // PF.update("form_photo_capture");
    }

    public void close() {
        renderedPhotoCapture = false;
        PF.closeDialog("dlg_photo_capture");
        // PF.update("form_photo_capture");
    }

    public void unload() {
        savePath = "";
        update = "";
        nameFile = "";
        fisica = null;
        juridica = null;
        sisPessoa = null;
        tipo = "";
    }

    public void capturar(CaptureEvent captureEvent) throws FileNotFoundException {
        UUID uuidX = UUID.randomUUID();
        String nameTemp = uuidX.toString().replace("-", "_");

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        byte[] data = captureEvent.getData();

        nameFile = nameTemp;

        if (fisica == null && juridica == null && sisPessoa == null) {
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

            // FISICA
            if (fisica != null) {
                String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/" + nameTemp + ".png";
                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, data);
                } catch (IOException e) {
                    e.getMessage();
                    return;
                }

                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("")+"resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + fisica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                fisica.setFoto(nameTemp);
                new Dao().update(fisica, true);
            }

            // JURIDICA
            if (juridica != null) {
                String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + juridica.getPessoa().getId() + "/" + nameTemp + ".png";
                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, data);
                } catch (IOException e) {
                    e.getMessage();
                    return;
                }

                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                    File fotoAntiga = new File(servletContext.getRealPath("")+"resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + pessoa.getId() + "/" + juridica.getFoto() + ".png");
//                    if (fotoAntiga.exists()) {
//                        FileUtils.deleteQuietly(fotoAntiga);
//                    }
                juridica.setFoto(nameTemp);
                new Dao().update(juridica, true);
            }

            // SIS PESSOA
            if (sisPessoa != null) {
                String path;
                if (tipo.equals("perfil")) {
                    path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/sispessoa/" + sisPessoa.getId() + "/perfil/" + nameTemp + ".png";
                    sisPessoa.setFotoPerfil(nameTemp);
                } else {
                    path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/sispessoa/" + sisPessoa.getId() + "/documento/" + nameTemp + ".png";
                    sisPessoa.setFotoArquivo(nameTemp);
                }

                File file = new File(path);
                try {
                    FileUtils.writeByteArrayToFile(file, data);
                } catch (IOException e) {
                    e.getMessage();
                    return;
                }
                new Dao().update(sisPessoa, true);
            }
        }
    }

    public Boolean getRenderedPhotoCapture() {
        return renderedPhotoCapture;
    }

    public void setRenderedPhotoCapture(Boolean renderedPhotoCapture) {
        this.renderedPhotoCapture = renderedPhotoCapture;
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

    public SisPessoa getSisPessoa() {
        return sisPessoa;
    }

    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
