package br.com.rtools.utilitarios;

public class Filters {

    private String key;
    private String value;
    private Boolean active;
    private Boolean disabled;

    public Filters() {
        this.key = null;
        this.value = null;
        this.active = false;
        this.disabled = false;
    }

    public Filters(String key, String value, Boolean active) {
        this.key = key;
        this.value = value;
        this.active = active;
    }
    
    public Filters(String key, String value, Boolean active, Boolean disabled) {
        this.key = key;
        this.value = value;
        this.active = active;
        this.disabled = disabled;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}
