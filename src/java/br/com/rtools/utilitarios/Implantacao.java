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

    public void importarParaHomologacao() {
        if (getCliente().equals("ComercioRP")) {
            DB db = new DB();
            List<MemoryFile> listaDiretorios = Diretorio.listMemoryFiles("/Arquivos/Todos/");
            Query query = null;
            for (int i = 0; i < listaDiretorios.size(); i++) {
                List<MemoryFile> listaArquivos = Diretorio.listMemoryFiles("/Arquivos/Todos/" + listaDiretorios.get(i).getName());
                String data = listaDiretorios.get(i).getName().replace("_", "-");
                for (int x = 0; x < listaArquivos.size(); x++) {
                    String cpf = listaArquivos.get(x).getName().replace(".pdf", "");
                    try {
                        String queryString = ""
                                + "     SELECT A.id AS agendamento_id,                                                \n"
                                + "            P.ds_nome,                                                             \n"
                                + "            P.ds_documento                                                         \n"
                                + "       FROM pes_pessoa AS P                                                        \n"
                                + " INNER JOIN pes_fisica AS F ON F.id_pessoa = P.id                                  \n"
                                + " INNER JOIN pes_pessoa_empresa AS PE ON PE.id_fisica = F.id                        \n"
                                + " INNER JOIN hom_agendamento AS A ON A.id_pessoa_empresa = PE.id                    \n"
                                + "      WHERE dt_data = '" + data + "' \n"
                                + "        AND replace(replace(ds_documento, '-', ''), '.', '') LIKE '" + cpf + "'";
                        query = db.getEntityManager().createNativeQuery(queryString);
                        List result = query.getResultList();
                        List o = (List) result.get(0);
                        Integer agendamento_id = (Integer) o.get(0);
                        Diretorio.criar("Arquivos/homologacao/" + agendamento_id);
                        String origem = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Arquivos/Todos/" + listaDiretorios.get(i).getName() + "/" + listaArquivos.get(x).getName());
                        String destino = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Arquivos/homologacao/" + agendamento_id + "/" + listaArquivos.get(x).getName());
                        File f = new File(origem);
                        boolean mv = f.renameTo(new File(destino));
                        if (mv) {
                            mv = true;
                        }
                    } catch (Exception e) {
                        System.err.println("Data: " + data + " - CPF: " + cpf);
                    }
                }
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
