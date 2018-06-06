package br.com.rtools.utilitarios;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "comparator")
@ViewScoped
public class Comparator implements Serializable {

    /**
     *
     * @param compare (Valor a ser comparado)
     * @param conditions (Condições Ex: a,b,c,d,e ou 1 2 3 a d 3 5)
     * @return
     */
    public Boolean test(String compare, String conditions) {
        try {
            String[] c = null;
            if (conditions.contains(" ")) {
                c = conditions.split(" ");
            } else {
                c = conditions.split(",");
            }
            for (int i = 0; i < c.length; i++) {
                if ((c[i].toString().replace(" ", "")).equals(compare)) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     *
     * @param compare (Valor a ser comparado)
     * @param conditions (Condições Ex: a,b,c,d,e ou 1 2 3 a d 3 5)
     * @return
     */
    public Boolean test(Integer compare, String conditions) {
        try {
            String[] c = null;
            if (conditions.contains(" ")) {
                c = conditions.split(" ");
            } else {
                c = conditions.split(",");
            }
            for (int i = 0; i < c.length; i++) {
                if (Integer.parseInt(c[i].toString().replace(" ", "")) == compare) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }
}
