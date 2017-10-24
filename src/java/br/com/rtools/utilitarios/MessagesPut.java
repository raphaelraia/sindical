package br.com.rtools.utilitarios;

public class MessagesPut {

    private String title;
    private String details;

    public MessagesPut() {
        this.title = "";
        this.details = "";
    }

    public MessagesPut(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
