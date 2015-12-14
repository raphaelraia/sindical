package br.com.rtools.utilitarios;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class CounterView implements Serializable {

    private Integer numberIncrement;
    private Integer numberDecrement;

    public CounterView() {
        numberIncrement = 0;
        numberDecrement = 5;
    }

    public Integer getNumberIncrement() {
        return numberIncrement;
    }

    public void setNumberIncrement(Integer numberIncrement) {
        this.numberIncrement = numberIncrement;
    }

    public Integer getNumberDecrement() {
        return numberDecrement;
    }

    public void setNumberDecrement(Integer numberDecrement) {
        this.numberDecrement = numberDecrement;
    }

    public void increment() {
        numberIncrement++;
    }

    public void decrement() {
        numberDecrement--;
    }
}
