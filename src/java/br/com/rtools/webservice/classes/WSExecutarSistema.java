package br.com.rtools.webservice.classes;

public class WSExecutarSistema {

    private String url;

    public WSExecutarSistema() {
        this.url = "";
    }

    public WSExecutarSistema(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}