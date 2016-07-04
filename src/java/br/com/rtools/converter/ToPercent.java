package br.com.rtools.converter;

import br.com.rtools.utilitarios.Moeda;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@ManagedBean
public class ToPercent implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (String) value; // Or (value != null) ? value.toString().toUpperCase() : null;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            if (value.isEmpty()) {
                value = "0";
            }
            String somenteLetras = value.replaceAll("[^a-zA-Z]", "");
            if (somenteLetras.length() > 0) {
                return null;
            }
            value = value.replace("-", "");
            if (value.isEmpty()) {
                return null;
            }
            Float f = Moeda.substituiVirgulaFloat(Moeda.converteR$(value, 4));
            if (f <= 0 || f > 100) {
                f = new Float(0);
            }
            return "" + f;
        }
        return null;
    }

}
