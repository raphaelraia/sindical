package br.com.rtools.utilitarios;

import br.com.rtools.seguranca.Rotina;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class GlobalSync implements Serializable {

    private static final Map<String, Date> date = new LinkedHashMap<>();

    public static Date getStaticDate() {
        try {
            Date d = new Date();
            if (GenericaSessao.exists("sessaoCliente")) {
                for (Map.Entry<String, Date> entry : date.entrySet()) {
                    if (entry.getKey().equals(getKey())) {
                        d = entry.getValue();
                    }
                            
                        }
                    }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static void load() {
        if (GenericaSessao.exists("sessaoCliente")) {
            String key = getKey();
            date.put(key, new Date());
        }
    }

    private static String getKey() {
        try {
            String cliente = GenericaSessao.getString("sessaoCliente");
            cliente = cliente.replace("/", "");
            String rotina = new Rotina().get().getCurrentPage().toLowerCase();
            if(rotina.contains("agendamento")) {
                rotina = "agendamento";
            }
            String key = (cliente + "_" + rotina).toLowerCase();
            return key;
        } catch (Exception e) {
            return "";
        }
    }

}
