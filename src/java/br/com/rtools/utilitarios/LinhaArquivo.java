package br.com.rtools.utilitarios;

import br.com.rtools.digitalizacao.Documento;

public class LinhaArquivo {

    private String extensionFile = ".doc";
    private String nameFile = "";
    private String mimeType = "";
    private Documento docFile = new Documento();

    public LinhaArquivo() {
        this.extensionFile = ".doc";
        this.nameFile = "";
        this.mimeType = "";
        this.docFile = new Documento();
    }

    public LinhaArquivo(String extensionFile, String nameFile, String mimeType, Documento docFile) {
        this.extensionFile = extensionFile;
        this.nameFile = nameFile;
        this.mimeType = mimeType;
        this.docFile = docFile;
    }

    public String getExtensionFile() {
        return extensionFile;
    }

    public void setExtensionFile(String extensionFile) {
        this.extensionFile = extensionFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Documento getDocFile() {
        return docFile;
    }

    public void setDocFile(Documento docFile) {
        this.docFile = docFile;
    }
}
