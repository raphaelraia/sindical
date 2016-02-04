//package br.com.rtools.webservice;
//
//import br.com.rtools.pessoa.dao.BiometriaDao;
//import br.com.rtools.seguranca.Registro;
//import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
//import br.com.rtools.utilitarios.Dao;
//import br.com.rtools.utilitarios.GenericaRequisicao;
//import br.com.rtools.webservice.classes.WSExecutarSistema;
//import com.google.gson.Gson;
//import java.io.IOException;
//import java.io.Serializable;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.RequestScoped;
//import javax.faces.bean.ViewScoped;
//import javax.faces.context.ExternalContext;
//import javax.faces.context.FacesContext;
//import org.primefaces.json.JSONException;
//
//@ManagedBean
//@RequestScoped
//@ViewScoped
//public class HabilitarCatracaBean implements Serializable {
//
//    public void response() {
//        try {
//            Boolean habilitar = false;
//            try {
//                habilitar = Boolean.parseBoolean(GenericaRequisicao.getParametro("habilitar"));
//            } catch (Exception e) {
//
//            }
//            ControleAcessoWebService caws = new ControleAcessoWebService();
//            Registro registro = (Registro) new Dao().find(new Registro(), 1);
//            BiometriaDao biometriaDao = new BiometriaDao();
//            if(habilitar) {
//                
//            }
//            // String params = caws.get
//            if (caws.getAction().equals("reload")) {
//                biometriaDao.reloadListBiometria(device_number);
//            } else {
//                biometriaDao.listBiometria();
//            }
//            WSExecutarSistema wes = new WSExecutarSistema();
//            wes.setUrl(registro.getUrlPath() + "/Sindical/" + caws.getClient() + "/");
//            FacesContext facesContext = FacesContext.getCurrentInstance();
//            ExternalContext externalContext = facesContext.getExternalContext();
//            externalContext.setResponseContentType("application/json");
//            externalContext.setResponseCharacterEncoding("UTF-8");
//            Gson gson = new Gson();
//            externalContext.getResponseOutputWriter().write(gson.toJson(wes));
//            facesContext.responseComplete();
//        } catch (NullPointerException | JSONException | IOException e) {
//
//        }
//    }
//
//}
