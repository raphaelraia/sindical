package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.Cnae;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class ConverterListShuttle implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null) {
            return null;
        } else {
            Cnae cnae = (Cnae) new Dao().find(new Cnae(), Integer.parseInt(value));
            return cnae;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object object) {
        if (object == null) {
            return null;
        } else {
            return String.valueOf(((Cnae) object).getId());
        }
    }
}
