package br.com.rtools.utilitarios;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RelogioBean implements Serializable {

    private Date dataAtual = new Date();

    public void relogio() {
        dataAtual = Calendar.getInstance(new Locale("BR")).getTime();
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }
}
