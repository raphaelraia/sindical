package br.com.rtools.converter;

import br.com.rtools.utilitarios.Dao;
import com.google.gson.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.primefaces.json.JSONObject;

@FacesConverter("objectConverter")
public class ObjectConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().isEmpty()) {
            return new Object();
        } else {
            JSONObject obj = new JSONObject(submittedValue);
            try {
                Object o = (Object) (new Dao()).find(obj.getString("className"), obj.getInt("id"));
                return o;
            } catch (Exception e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null || object.equals("")) {
            return "";
        } else {
            Integer id;
            try {
                Class classe = object.getClass();
                Method metodo = classe.getMethod("getId", new Class[]{});
                id = (Integer) metodo.invoke(object, (Object[]) null);
                if (id == null || id == -1) {
                    return null;
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("className", object.getClass().getSimpleName());
                return jsonObject.toString();
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            }
        }
    }

}
