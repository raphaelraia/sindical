package br.com.rtools.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONObject;

public class WebServiceCNPJ {

    public HashMap registrarMovimentos(String cnpj) {
        HashMap hash = new LinkedHashMap();
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //HttpPost httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/123hg2132sdfg3716dwsdjkas/pesquisar_contribuinte");
            HttpPost httppost = new HttpPost("http://localhost:8080/webservicereceita/cliente/123hg2132sdfg3716dwsdjkas/cnpj/" + cnpj);
            List<NameValuePair> params = new ArrayList(2);
            params.add(new BasicNameValuePair("codigo", "" + ""));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            // SE FOR IGUAL A NULL CADASTRAR CONTRIBUINTE
            if (entity == null) {
                System.out.println("Cadastrar Contribuinte!");
                httpclient.close();
                httppost.abort();

                httpclient = HttpClients.createDefault();
                //httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/123hg2132sdfg3716dwsdjkas/salvar_contribuinte");
                httppost = new HttpPost("http://localhost:8080/webservicecnpj/cliente/123hg2132sdfg3716dwsdjkas/salvar_contribuinte");
                params = new ArrayList(2);
//                params.add(new BasicNameValuePair("codigo", "" + lista.get(i).getPessoa().getId()));
//                params.add(new BasicNameValuePair("documento", lista.get(i).getPessoa().getDocumento()));
//                params.add(new BasicNameValuePair("nome", lista.get(i).getPessoa().getNome()));
//                params.add(new BasicNameValuePair("endereco", lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao() + " " + lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao()));
//                params.add(new BasicNameValuePair("bairro", lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao()));
//                params.add(new BasicNameValuePair("cidade", lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade()));
//                params.add(new BasicNameValuePair("uf", lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf()));
//                params.add(new BasicNameValuePair("cep", lista.get(i).getPessoa().getPessoaEndereco().getEndereco().getCep()));

                httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                response = httpclient.execute(httppost);
                entity = response.getEntity();

                if (entity != null) {
                    String msg = EntityUtils.toString(entity);
                    JSONObject jSONObject = new JSONObject(msg);
                    boolean result = jSONObject.getBoolean("status");

                    if (!result) {
                        String mens = jSONObject.getString("mensagem");

                        hash.put("lista", new ArrayList());
                        hash.put("mensagem", mens);
                        return hash;
                    }
                }
            }

            httpclient = HttpClients.createDefault();
            // PESQUISAR BOLETO
            httppost = new HttpPost("http://localhost:8080/webservicecnpj/cliente/123hg2132sdfg3716dwsdjkas/salvar_contribuinte");

            params = new ArrayList(2);
            // params.add(new BasicNameValuePair("nosso_numero", "" + bol.getNrBoleto()));

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            response = httpclient.execute(httppost);
            entity = response.getEntity();

            // SE NÃO EXISTIR BOLETO, CRIAR
            httpclient.close();
            httppost.abort();
            httpclient = HttpClients.createDefault();

            if (entity == null) {
                httppost = new HttpPost("http://localhost:8080/webservicecnpj/cliente/123hg2132sdfg3716dwsdjkas/criar_boleto");
            } else {
                //httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/123hg2132sdfg3716dwsdjkas/alterar_boleto");
                httppost = new HttpPost("http://localhost:8080/webservicecnpj/cliente/123hg2132sdfg3716dwsdjkas/alterar_boleto");
            }

            params = new ArrayList(2);
//            params.add(new BasicNameValuePair("codigo_contribuinte", "" + lista.get(i).getPessoa().getId()));
//            params.add(new BasicNameValuePair("nosso_numero", "" + bol.getNrBoleto()));
//            params.add(new BasicNameValuePair("numero_banco", "" + bol.getContaCobranca().getContaBanco().getBanco().getNumero()));
//            params.add(new BasicNameValuePair("conta", "" + bol.getContaCobranca().getContaBanco().getConta()));
//            params.add(new BasicNameValuePair("agencia", "" + bol.getContaCobranca().getContaBanco().getAgencia()));
//            params.add(new BasicNameValuePair("codigo_cedente", "" + bol.getContaCobranca().getCodCedente()));
//            params.add(new BasicNameValuePair("especie_documento", "DM"));
//            params.add(new BasicNameValuePair("layout", "1"));
//            //params.add(new BasicNameValuePair("data_vencimento", lista.get(i).getVencimento().substring(0, 2) +  lista.get(i).getVencimento().substring(3, 5) + lista.get(i).getVencimento().substring(6, 10)));
//            params.add(new BasicNameValuePair("data_vencimento", listaVencimentos.get(i).substring(0, 2) + listaVencimentos.get(i).substring(3, 5) + listaVencimentos.get(i).substring(6, 10)));
//            params.add(new BasicNameValuePair("referencia", lista.get(i).getReferencia().replace("/", "")));
//            params.add(new BasicNameValuePair("valor", Moeda.converteR$Float(listaValores.get(i)).replace(".", "").replace(",", ".")));

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            response = httpclient.execute(httppost);
            entity = response.getEntity();

            if (entity != null) {
                String msg = EntityUtils.toString(entity);
                JSONObject jSONObject = new JSONObject(msg);
                if (!jSONObject.getBoolean("status")) {
                    hash.put("lista", new ArrayList());
                    //hash.put("mensagem", "Erro criação ou alteração do Boleto" + bol.getNrBoleto() + ", contate o Administrador.");
                    hash.put("mensagem", jSONObject.getBoolean("mensagem"));
                    return hash;
                }
            }

            httpclient.close();
            httppost.abort();

            httpclient = HttpClients.createDefault();

            //httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/123hg2132sdfg3716dwsdjkas/imprimir_boleto");
            httppost = new HttpPost("http://localhost:8080/webservicecnpj/cliente/123hg2132sdfg3716dwsdjkas/imprimir_boleto");

            params = new ArrayList(2);
            // params.add(new BasicNameValuePair("nosso_numero", "" + bol.getNrBoleto()));

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            response = httpclient.execute(httppost);
            entity = response.getEntity();
            hash.put("lista", new ArrayList());
            // hash.put("mensagem", "Erro ao Registrar Boleto " + bol.getNrBoleto() + ", contate o Administrador.");
        } catch (IOException | UnsupportedOperationException e) {
            hash.put("lista", new ArrayList());
            hash.put("mensagem", e.getMessage());
            return hash;
        }

        // hash.put("lista", listaAdd);
        hash.put("mensagem", "");
        return hash;
    }
}
