
import br.com.rtools.utilitarios.GenericaSessao;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class TestPost {

    public static void main(String[] args) {
        try {
            DefaultHttpClient cliente = new DefaultHttpClient();
            HttpContext contexto = new BasicHttpContext();
             
            HashMap hash_test = new LinkedHashMap();
            HttpPost httpPost = new HttpPost("http://webservice.correios.com.br/service/rest/rastro/rastroMobile/");
            List<NameValuePair> nameValuePairs = new ArrayList(7);            
            nameValuePairs.add(new BasicNameValuePair("usuario", "MobileXect"));
            nameValuePairs.add(new BasicNameValuePair("senha", "DRW0#9F$@0"));
            nameValuePairs.add(new BasicNameValuePair("tipo", "L"));
            nameValuePairs.add(new BasicNameValuePair("resultado", "U"));
            nameValuePairs.add(new BasicNameValuePair("objetos", "PL506210624BR/101"));
            nameValuePairs.add(new BasicNameValuePair("lingua", "101"));
            nameValuePairs.add(new BasicNameValuePair("token", "a2_4bd607a6-aea0-4f3d-9712-776e475f6ebd"));
            httpPost.setHeader("token", "a2_4bd607a6-aea0-4f3d-9712-776e475f6ebd");
            // Encapsulando
            UrlEncodedFormEntity urlEncodedFormEntity = null;
            try {
                urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TestPost.class.getName()).log(Level.SEVERE, null, ex);
            }
            // A adição dos parâmetros
            httpPost.setEntity(urlEncodedFormEntity);
            
            HttpResponse resposta = cliente.execute(httpPost, contexto);
            HttpEntity entidade = resposta.getEntity();
            String html;
            try {
                html = EntityUtils.toString(entidade);
            } catch (IOException | ParseException e) {
                hash_test.put("status", false);
                hash_test.put("mensagem", e.getMessage());
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TestPost.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
