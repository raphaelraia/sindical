package br.com.rtools.converter;

import br.com.rtools.utilitarios.DataHoje;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@ManagedBean
public class IsDateReference implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (String) value; // Or (value != null) ? value.toString().toUpperCase() : null;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            if (DataHoje.validaReferencia(value)) {
                return value;
            } else {
                return null;
            }
        }
        return null;
    }
}
