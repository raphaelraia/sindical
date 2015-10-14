package br.com.rtools.utilitarios;

import br.com.rtools.principal.DB;
import java.io.File;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;

public class Implantacao {

    private final static String cliente = "";

    // http://localhost:8080/Sindical/indexLogin.jsf?cliente=Sindical&export=true
    public void run() {
        if (getCliente().equals("ComercioItapetininga")
                || getCliente().equals("ComercioSorocaba")
                || getCliente().equals("HoteleiroRP")
                || getCliente().equals("ComercioRP")
                || getCliente().equals("Sindical")
                || getCliente().equals("SeaacRP")
                || getCliente().equals("ServidoresRP")
                || getCliente().equals("ComercioLimeira")) {

            DB db = new DB();
            try {
                Query query = db.getEntityManager().createNativeQuery("SELECT id_pessoa FROM pes_fisica WHERE dt_foto IS NOT NULL");
                List list = query.getResultList();
                for (int i = 0; i < list.size(); i++) {
                    List o = (List) list.get(i);
                    String pt = o.get(0).toString();
                    try {
                        Diretorio.criar("imagens/pessoa/" + pt, true);
                    } catch (Exception e) {

                    }
                }
                String ext = "";
                for (int i = 0; i < list.size(); i++) {
                    List o = (List) list.get(i);
                    String pt = o.get(0).toString();
                    String origem = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + pt);
                    String destino = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + getCliente().toLowerCase() + "/imagens/pessoa/" + pt);
                    File file = new File(origem + ".png");
                    if (!file.exists()) {
                        file = new File(origem + ".jpg");
                        if (file.exists()) {
                            ext = ".jpg";
                            boolean mv = file.renameTo(new File(destino + "/" + pt + ext));
                        }
                    } else {
                        ext = ".png";
                        boolean mv = file.renameTo(new File(destino + "/" + pt + ext));
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public static String getCliente() {
        if (cliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                return GenericaSessao.getString("sessaoCliente");
            }
        }
        return cliente;
    }
}
