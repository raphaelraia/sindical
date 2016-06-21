package br.com.rtools.converter;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.utilitarios.Dao;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("fisicaConverter")
public class FisicaConverter implements Converter {

    public static List<Fisica> fisicaDB;

    static {

    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("-1")) {
            return new Fisica();
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                Fisica fisica = (Fisica) (new Dao()).find(new Fisica(), number);
                return fisica;
            } catch (NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Fisica) value).getId());
        }
    }

}
