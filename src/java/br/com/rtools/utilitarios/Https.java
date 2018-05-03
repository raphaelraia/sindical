package br.com.rtools.utilitarios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class Https {

    public static String get(String uri, Properties paramns) {
        // os parametros a serem enviados
//            Properties parameters = new Properties();
//            parameters.setProperty("cep", cep);
//            parameters.setProperty("formato", "xml");
        if (paramns != null) {
            Iterator i = paramns.keySet().iterator();
            int counter = 0;
            while (i.hasNext()) {
                String name = (String) i.next();
                String value = paramns.getProperty(name);
                uri += (++counter == 1 ? "?" : "&") + name + "=" + value;
            }
        }
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Request-Method", "GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder newData = new StringBuilder();
            String s = "";
            while (null != ((s = br.readLine()))) {
                newData.append(s);
            }
            connection.disconnect();
            br.close();
            return newData.toString();
        } catch (IOException e) {
        }
        return null;
    }
}
