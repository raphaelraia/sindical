package br.com.rtools.sistema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.event.FileUploadEvent;

public class ConfiguracaoUpload implements Serializable {

    private String diretorio;
    private String arquivo;
    private String renomear;
    private FileUploadEvent event;
    private Integer tamanhoMaximo;
    private Integer alturaMaxima;
    private Integer larguraMaxima;
    private List tiposPermitidos;
    private boolean substituir;
    private boolean resourceFolder;
    private String cliente;
    private String folder;

    public ConfiguracaoUpload() {
        this.diretorio = "";
        this.arquivo = "";
        this.event = null;
        this.tamanhoMaximo = 0;
        this.alturaMaxima = 0;
        this.larguraMaxima = 0;
        this.tiposPermitidos = new ArrayList();
        this.substituir = false;
        this.renomear = "";
        this.resourceFolder = false;
        this.cliente = null;
        this.folder = null;
    }

    public ConfiguracaoUpload(String diretorio, String arquivo, String renomear, FileUploadEvent event, Integer tamanhoMaximo, Integer alturaMaxima, Integer larguraMaxima, List tiposPermitidos, boolean substituir, boolean resourceFolder, String cliente, String folder) {
        this.diretorio = diretorio;
        this.arquivo = arquivo;
        this.renomear = renomear;
        this.event = event;
        this.tamanhoMaximo = tamanhoMaximo;
        this.alturaMaxima = alturaMaxima;
        this.larguraMaxima = larguraMaxima;
        this.tiposPermitidos = tiposPermitidos;
        this.substituir = substituir;
        this.resourceFolder = resourceFolder;
        this.cliente = cliente;
        this.folder = folder;
    }

    public String getDiretorio() {
        return diretorio;
    }

    public void setDiretorio(String diretorio) {
        this.diretorio = diretorio;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public FileUploadEvent getEvent() {
        return event;
    }

    public void setEvent(FileUploadEvent event) {
        this.event = event;
    }

    public Integer getTamanhoMaximo() {
        return tamanhoMaximo;
    }

    public void setTamanhoMaximo(Integer tamanhoMaximo) {
        this.tamanhoMaximo = tamanhoMaximo;
    }

    public Integer getAlturaMaxima() {
        return alturaMaxima;
    }

    public void setAlturaMaxima(Integer alturaMaxima) {
        this.alturaMaxima = alturaMaxima;
    }

    public Integer getLarguraMaxima() {
        return larguraMaxima;
    }

    public void setLarguraMaxima(Integer larguraMaxima) {
        this.larguraMaxima = larguraMaxima;
    }

    public List getTiposPermitidos() {
        return tiposPermitidos;
    }

    public void setTiposPermitidos(List tiposPermitidos) {
        this.tiposPermitidos = tiposPermitidos;
    }

    public boolean isSubstituir() {
        return substituir;
    }

    public void setSubstituir(boolean substituir) {
        this.substituir = substituir;
    }

    public String getRenomear() {
        return renomear;
    }

    public void setRenomear(String renomear) {
        this.renomear = renomear;
    }

    public boolean isResourceFolder() {
        return resourceFolder;
    }

    public void setResourceFolder(boolean resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

}
