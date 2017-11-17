package br.com.rtools.converter;

import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.Types;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@ManagedBean
public class ToCurrency implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (Types.isDouble(value)) {
            return value.toString();
        }
        return (String) value; // Or (value != null) ? value.toString().toUpperCase() : null;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
         if (value != null) {
             value = value.trim();
            if (value.isEmpty()) {
                value = "0";
            }
            String somenteLetras = value.replaceAll("[^a-zA-Z]", "");
            if (somenteLetras.length() > 0) {
                return "0";
            }
            value = value.replace("-", "");
            if (value.isEmpty()) {
                return "0";
            }
            return Moeda.converteR$(value, 2);
        }
        return null;
    }

}
