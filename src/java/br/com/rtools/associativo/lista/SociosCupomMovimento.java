package br.com.rtools.associativo.lista;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import javax.persistence.Transient;

public class SociosCupomMovimento {

    private Boolean disabled;
    private Object titular_id;
    private Pessoa pessoa;
    private String codigoCupom;

    @Transient
    private Boolean selected;

    public SociosCupomMovimento() {
        this.disabled = true;
        this.titular_id = null;
        this.pessoa = new Pessoa();
        this.selected = false;
        this.codigoCupom = "";
    }

    public SociosCupomMovimento(Boolean disabled, Object titular_id, Boolean selected) {
        this.disabled = disabled;
        this.titular_id = titular_id;
        if (titular_id != null) {
            this.pessoa = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(titular_id.toString()));
        }
        this.selected = selected;
    }

    public SociosCupomMovimento(Boolean disabled, Object titular_id, Boolean selected, String codigoCupom) {
        this.disabled = disabled;
        this.titular_id = titular_id;
        if (titular_id != null) {
            this.pessoa = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(titular_id.toString()));
        }
        this.selected = selected;
        this.codigoCupom = codigoCupom;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Object getTitular_id() {
        return titular_id;
    }

    public void setTitular_id(Object titular_id) {
        this.titular_id = titular_id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCodigoCupom() {
        return codigoCupom;
    }

    public void setCodigoCupom(String codigoCupom) {
        this.codigoCupom = codigoCupom;
    }

}
