package br.com.rtools.utilitarios;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class Dates extends DataHoje {

    public Date current() {
        Date dateTime = new Date();
        return dateTime;
    }

    public Date addDays(Integer days) {
        DataHoje dh = new DataHoje();
        if (days >= 0) {
            return DataHoje.converte(dh.incrementarDias(days, DataHoje.data()));
        }
        return new Date();
    }

    public Date removeDays(Integer days) {
        DataHoje dh = new DataHoje();
        if (days < 0) {
            days = -days;
        }
        return DataHoje.converte(dh.decrementarDias(days, DataHoje.data()));
    }

    public String today() {
        return DataHoje.data();
    }
}
