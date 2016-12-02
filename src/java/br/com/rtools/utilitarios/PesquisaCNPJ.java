package br.com.rtools.utilitarios;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class PesquisaCNPJ implements Serializable {

    private String cnpj = "";
    private String captcha = "";
    private String nameTemp = "";
    private DefaultHttpClient cliente = new DefaultHttpClient();
    private HttpContext contexto = new BasicHttpContext();

    public HashMap pesquisar() throws IOException {
        HashMap result = new LinkedHashMap();
        result.put("status", true);
        result.put("mensagem", "");
        // Adicionando um sistema de redireção
        cliente = new DefaultHttpClient();
        cliente.setRedirectStrategy(new LaxRedirectStrategy());
        // Mantendo a conexão sempre ativa
        cliente.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        // Criando o container de cookies
        CookieStore cookie = new BasicCookieStore();
        // Adicionando o coockie store no contexto de conexão
        contexto = new BasicHttpContext();
        contexto.setAttribute(ClientContext.COOKIE_STORE, cookie);

        HttpGet requisição1 = new HttpGet("http://www.receita.fazenda.gov.br/PessoaJuridica/CNPJ/cnpjreva/Cnpjreva_Solicitacao2.asp");
        // Resposta
        HttpResponse resposta = cliente.execute(requisição1, contexto);
        // Buscando a entidade
        HttpEntity entidade = resposta.getEntity();
        // Transformando o conteúdo em uma string
        try {
            EntityUtils.toString(entidade);
        } catch (IOException | ParseException e) {
            result.put("status", false);
            result.put("mensagem", e.getMessage());
            return result;
        }
        HttpGet requisição2 = new HttpGet("http://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/captcha/gerarCaptcha.asp");
        // HttpGet requisição2 = new HttpGet("http://www.receita.fazenda.gov.br/scripts/srf/intercepta/captcha.aspx?opt=image");
        // Resposta
        resposta = cliente.execute(requisição2, contexto);

        entidade = resposta.getEntity();

        UUID uuidX = UUID.randomUUID();
        nameTemp = uuidX.toString().replace("-", "_");

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/images/captcha/" + nameTemp + ".png";
        File file = new File(path);

        try {
            FileUtils.writeByteArrayToFile(file, EntityUtils.toByteArray(entidade));
        } catch (IOException e) {
            result.put("status", false);
            result.put("mensagem", e.getMessage());
            return result;
        }
        return result;
    }

    public HashMap webServiceRtools() throws IOException {
        HashMap result = new LinkedHashMap();
        result.put("status", true);
        result.put("mensagem", "");
        HashMap hash_test = new LinkedHashMap();
        HttpPost httpPost = new HttpPost("http://webservicerf.rtools.com.br:7090/webservicereceita/consulta/cnpj/client/" + GenericaSessao.getString("sessaoCliente") + "/document/" + cnpj + "/");
        // HttpPost httpPost = new HttpPost("http://localhost:8080/webservicereceita/consulta/cnpj/");
        hash_test.put("status", false);
        hash_test.put("mensagem", "Consulta não realizada");
        List<NameValuePair> nameValuePairs = new ArrayList(7);

        nameValuePairs.add(new BasicNameValuePair("client", GenericaSessao.getString("sessaoCliente")));
        nameValuePairs.add(new BasicNameValuePair("document", cnpj));
        // Encapsulando
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF8");
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
            return hash_test;
        }

        if (html.isEmpty()) {
            hash_test.put("status", false);
            hash_test.put("mensagem", "Erro na Consulta - Retorna vázio!");
            return hash_test;
        }

        if (html.contains("<b>Erro na Consulta</b>")) {
            hash_test.put("status", false);
            hash_test.put("mensagem", "Erro na Consulta");
            return hash_test;
        }

        if (html.contains("No existe no Cadastro de Pessoas Jurdicas o nmero de CNPJ informado. Verifique se o mesmo foi digitado corretamente.")) {
            hash_test.put("status", false);
            hash_test.put("mensagem", "CNPJ não existe no cadastro de Pessoas Jurídicas da Receita");
            return hash_test;
        }

        // Busco o documento estruturado
        HTMLDocument document = getHTMLDocument(html);
        // Busco todos os elementos em forma de iterador
        ElementIterator elementIterator = new ElementIterator(document);
        // Enquanto existir próximo elemento
        Element element;
        while ((element = elementIterator.next()) != null) {
            // Dentro dos elementos estão as informações para se pegar, porém não tenho autorização de divulgá-las.
            // Use a criatividade que você irá recuperar todos os dados necessários dentro deste While.
            HashMap hash = quebra_parametros(element);
            if (hash != null) {
                return hash;
            }
        }
        return result;
    }

    public HashMap confirmar() throws UnsupportedEncodingException, IOException {
        HashMap hash_test = new LinkedHashMap();
        hash_test.put("status", false);
        hash_test.put("mensagem", "Consulta não realizada");
        HttpPost requisição3 = new HttpPost("http://www.receita.fazenda.gov.br/pessoajuridica/cnpj/cnpjreva/valida.asp");
        //HttpResponse resposta = cliente.execute(requisição3, contexto);
        // Lista de parâmetros
        List<NameValuePair> nameValuePairs = new ArrayList(7);

        nameValuePairs.add(new BasicNameValuePair("origem", "comprovante"));
        nameValuePairs.add(new BasicNameValuePair("search_type", "cnpj"));
        nameValuePairs.add(new BasicNameValuePair("cnpj", cnpj.replace(".", "").replace("-", "").replace("/", "")));
        nameValuePairs.add(new BasicNameValuePair("txtTexto_captcha_serpro_gov_br", captcha));
        nameValuePairs.add(new BasicNameValuePair("submit1", "Consultar"));
        // Encapsulando
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF8");
        // A adição dos parâmetros
        requisição3.setEntity(urlEncodedFormEntity);
        // Resposta
        HttpResponse resposta = cliente.execute(requisição3, contexto);
        HttpEntity entidade = resposta.getEntity();
        String html;
        try {
            html = EntityUtils.toString(entidade);
        } catch (IOException | ParseException e) {
            hash_test.put("status", false);
            hash_test.put("mensagem", e.getMessage());
            return hash_test;
        }

        if (html.contains("<b>Erro na Consulta</b>")) {
            hash_test.put("status", false);
            hash_test.put("mensagem", "Erro na Consulta");
            return hash_test;
        }

        if (html.contains("No existe no Cadastro de Pessoas Jurdicas o nmero de CNPJ informado. Verifique se o mesmo foi digitado corretamente.")) {
            hash_test.put("status", false);
            hash_test.put("mensagem", "CNPJ não existe no cadastro de Pessoas Jurídicas da Receita");
            return hash_test;
        }

        // Busco o documento estruturado
        HTMLDocument document = getHTMLDocument(html);
        // Busco todos os elementos em forma de iterador
        ElementIterator elementIterator = new ElementIterator(document);
        // Enquanto existir próximo elemento
        Element element;
        while ((element = elementIterator.next()) != null) {
            // Dentro dos elementos estão as informações para se pegar, porém não tenho autorização de divulgá-las.
            // Use a criatividade que você irá recuperar todos os dados necessários dentro deste While.
            HashMap hash = quebra_parametros(element);
            if (hash != null) {
                return hash;
            }
        }
        return hash_test;
    }

    public HashMap quebra_parametros(Element element) {
        if (element.getName().equals(HTML.Tag.TD.toString())) {
            String conteudo;
            try {
                conteudo = (String) element.getDocument().getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
            } catch (BadLocationException e) {
                return null;
            }

            if (!valida_conteudo_html(conteudo)) {
                return null;
            }

            // ESTA COMENTADO NO SITE DA RECEITA
            /*
            if (!conteudo.contains("PORTE DA EMPRESA")) {
                return conteudo;
            }
             */
            // Esses IF's continuam até terminar todos os "Nomes" que quero dentro da página (VEJA o Cartão CNPJ para entender).
            // Este é um objeto criado apenas para guardar os dados capturados
            //ElementoCnpj elementoCnpj = new ElementoCnpj();
            HashMap params = new LinkedHashMap();
            params.put("status", true);
            params.put("mensagem", "");
            // Aqui capturo o CNPJ
            int index = 0;
            int pos1 = conteudo.indexOf("NÚMERO DE INSCRIÇÃO", index);
            index = pos1;
            int pos2 = conteudo.indexOf("COMPROVANTE DE INSCRIÇÃO E DE SITUAÇÃO CADASTRAL", index);
            index = pos2;
            params.put("cnpj", limparString(conteudo.substring(pos1 + "NÚMERO DE INSCRIÇÃO".length(), pos2)).substring(0, 18));

            // Aqui capturo a Data de Abertura
            pos1 = conteudo.indexOf("DATA DE ABERTURA", index);
            index = pos1;
            pos2 = conteudo.indexOf("NOME EMPRESARIAL", index);
            index = pos2;
            params.put("data_abertura", limparString(conteudo.substring(pos1 + "DATA DE ABERTURA".length(), pos2)));

            // Aqui capturo Nome Empresarial
            pos1 = conteudo.indexOf("NOME EMPRESARIAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("TÍTULO DO ESTABELECIMENTO (NOME DE FANTASIA)", index);
            index = pos2;
            params.put("nome_empresarial", limparString(conteudo.substring(pos1 + "NOME EMPRESARIAL".length(), pos2)));

            // Aqui capturo Fantasia
            pos1 = conteudo.indexOf("TÍTULO DO ESTABELECIMENTO (NOME DE FANTASIA)", index);
            index = pos1;
            pos2 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DA ATIVIDADE ECONÔMICA PRINCIPAL", index);
            index = pos2;
            params.put("fantasia", limparString(conteudo.substring(pos1 + "TÍTULO DO ESTABELECIMENTO (NOME DE FANTASIA)".length(), pos2)));

            // Aqui capturo Cnae Primário
            pos1 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DA ATIVIDADE ECONÔMICA PRINCIPAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DAS ATIVIDADES ECONÔMICAS SECUNDÁRIAS", index);
            index = pos2;
            params.put("cnae", limparString(conteudo.substring(pos1 + "CÓDIGO E DESCRIÇÃO DA ATIVIDADE ECONÔMICA PRINCIPAL".length(), pos2)));

            // Aqui capturo Cnae Secundário
            pos1 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DAS ATIVIDADES ECONÔMICAS SECUNDÁRIAS", index);
            index = pos1;
            pos2 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DA NATUREZA JURÍDICA", index);
            index = pos2;
            params.put("cnae_secundario", limparString(conteudo.substring(pos1 + "CÓDIGO E DESCRIÇÃO DAS ATIVIDADES ECONÔMICAS SECUNDÁRIAS".length(), pos2)));
            params.put("cnae_secundario_list", extrairCnaes(params.get("cnae_secundario").toString()));

            // Aqui capturo Natureza Jurídica
            pos1 = conteudo.indexOf("CÓDIGO E DESCRIÇÃO DA NATUREZA JURÍDICA", index);
            index = pos1;
            pos2 = conteudo.indexOf("LOGRADOURO", index);
            index = pos2;
            params.put("natureza_juridica", limparString(conteudo.substring(pos1 + "CÓDIGO E DESCRIÇÃO DA NATUREZA JURÍDICA".length(), pos2)));

            // Aqui capturo Logradouro
            pos1 = conteudo.indexOf("LOGRADOURO", index);
            index = pos1;
            pos2 = conteudo.indexOf("NÚMERO", index);
            index = pos2;
            params.put("logradouro", limparString(conteudo.substring(pos1 + "LOGRADOURO".length(), pos2)));

            // Aqui capturo Número
            pos1 = conteudo.indexOf("NÚMERO", index);
            index = pos1;
            pos2 = conteudo.indexOf("COMPLEMENTO", index);
            index = pos2;
            params.put("numero", limparString(conteudo.substring(pos1 + "NÚMERO".length(), pos2)));

            // Aqui capturo Complemento
            pos1 = conteudo.indexOf("COMPLEMENTO", index);
            index = pos1;
            pos2 = conteudo.indexOf("CEP", index);
            index = pos2;
            params.put("complemento", limparString(conteudo.substring(pos1 + "COMPLEMENTO".length(), pos2)));

            // Aqui capturo Cep
            pos1 = conteudo.indexOf("CEP", index);
            index = pos1;
            pos2 = conteudo.indexOf("BAIRRO/DISTRITO", index);
            index = pos2;
            params.put("cep", limparString(conteudo.substring(pos1 + "CEP".length(), pos2)));

            // Aqui capturo Bairro / Distrito
            pos1 = conteudo.indexOf("BAIRRO/DISTRITO", index);
            index = pos1;
            pos2 = conteudo.indexOf("MUNICÍPIO", index);
            index = pos2;
            params.put("bairro", limparString(conteudo.substring(pos1 + "BAIRRO/DISTRITO".length(), pos2)));

            // Aqui capturo Município
            pos1 = conteudo.indexOf("MUNICÍPIO", index);
            index = pos1;
            pos2 = conteudo.indexOf("UF", index);
            index = pos2;
            params.put("municipio", limparString(conteudo.substring(pos1 + "MUNICÍPIO".length(), pos2)));

            // Aqui capturo UF
            pos1 = conteudo.indexOf("UF", index);
            index = pos1;
            pos2 = conteudo.indexOf("ENDEREÇO ELETRÔNICO", index);
            index = pos2;
            params.put("uf", limparString(conteudo.substring(pos1 + "UF".length(), pos2)));

            // Aqui capturo Endereço Eletrônico
            pos1 = conteudo.indexOf("ENDEREÇO ELETRÔNICO", index);
            index = pos1;
            pos2 = conteudo.indexOf("TELEFONE", index);
            index = pos2;
            params.put("endereco_eletronico", limparString(conteudo.substring(pos1 + "ENDEREÇO ELETRÔNICO".length(), pos2)));

            // Aqui capturo Telefone
            pos1 = conteudo.indexOf("TELEFONE", index);
            index = pos1;
            pos2 = conteudo.indexOf("ENTE FEDERATIVO RESPONSÁVEL (EFR)", index);
            index = pos2;
            params.put("telefone", limparString(conteudo.substring(pos1 + "TELEFONE".length(), pos2)));

            // Aqui capturo Ente Federativo Responsável
            pos1 = conteudo.indexOf("ENTE FEDERATIVO RESPONSÁVEL (EFR)", index);
            index = pos1;
            pos2 = conteudo.indexOf("SITUAÇÃO CADASTRAL", index);
            index = pos2;
            params.put("efr", limparString(conteudo.substring(pos1 + "ENTE FEDERATIVO RESPONSÁVEL (EFR)".length(), pos2)));

            // Aqui capturo Situação Cadastral
            pos1 = conteudo.indexOf("SITUAÇÃO CADASTRAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("DATA DA SITUAÇÃO CADASTRAL", index);
            index = pos2;
            params.put("situacao_cadastral", limparString(conteudo.substring(pos1 + "SITUAÇÃO CADASTRAL".length(), pos2)));

            // Aqui capturo Data Situação Cadastral
            pos1 = conteudo.indexOf("DATA DA SITUAÇÃO CADASTRAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("MOTIVO DE SITUAÇÃO CADASTRAL", index);
            index = pos2;
            params.put("data_situacao_cadastral", limparString(conteudo.substring(pos1 + "DATA DA SITUAÇÃO CADASTRAL".length(), pos2)));

            // Aqui capturo Data Motivo de Situação Cadastral
            pos1 = conteudo.indexOf("MOTIVO DE SITUAÇÃO CADASTRAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("SITUAÇÃO ESPECIAL", index);
            index = pos2;
            params.put("motivo_situacao_cadastral", limparString(conteudo.substring(pos1 + "MOTIVO DE SITUAÇÃO CADASTRAL".length(), pos2)));

            // Aqui capturo Situação Especial
            pos1 = conteudo.indexOf("SITUAÇÃO ESPECIAL", index);
            index = pos1;
            pos2 = conteudo.indexOf("DATA DA SITUAÇÃO ESPECIAL", index);
            index = pos2;
            params.put("situacao_especial", limparString(conteudo.substring(pos1 + "SITUAÇÃO ESPECIAL".length(), pos2)));

            // Aqui capturo Data Situação Especial
            pos1 = conteudo.indexOf("DATA DA SITUAÇÃO ESPECIAL", index);
            index = pos1;
            pos2 = conteudo.length();
            index = pos2;
            params.put("data_situacao_especial", limparString(conteudo.substring(pos1 + "DATA DA SITUAÇÃO ESPECIAL".length(), pos2)));
            // Faço isto para tudo que eu quiser capturar.
            return params;
        }
        return null;
    }

    public boolean valida_conteudo_html(String conteudo) {

        if (!conteudo.contains("NÚMERO DE INSCRIÇÃO")) {
            return false;
        }
        if (!conteudo.contains("DATA DE ABERTURA")) {
            return false;
        }
        if (!conteudo.contains("NOME EMPRESARIAL")) {
            return false;
        }
        if (!conteudo.contains("TÍTULO DO ESTABELECIMENTO (NOME DE FANTASIA)")) {
            return false;
        }
        if (!conteudo.contains("CÓDIGO E DESCRIÇÃO DA ATIVIDADE ECONÔMICA PRINCIPAL")) {
            return false;
        }
        if (!conteudo.contains("CÓDIGO E DESCRIÇÃO DAS ATIVIDADES ECONÔMICAS SECUNDÁRIAS")) {
            return false;
        }
        if (!conteudo.contains("CÓDIGO E DESCRIÇÃO DA NATUREZA JURÍDICA")) {
            return false;
        }
        if (!conteudo.contains("LOGRADOURO")) {
            return false;
        }
        if (!conteudo.contains("NÚMERO")) {
            return false;
        }
        if (!conteudo.contains("COMPLEMENTO")) {
            return false;
        }
        if (!conteudo.contains("CEP")) {
            return false;
        }
        if (!conteudo.contains("BAIRRO/DISTRITO")) {
            return false;
        }
        if (!conteudo.contains("MUNICÍPIO")) {
            return false;
        }
        if (!conteudo.contains("UF")) {
            return false;
        }
        if (!conteudo.contains("ENDEREÇO ELETRÔNICO")) {
            return false;
        }
        if (!conteudo.contains("ENDEREÇO ELETRÔNICO")) {
            return false;
        }
        if (!conteudo.contains("TELEFONE")) {
            return false;
        }
        if (!conteudo.contains("ENTE FEDERATIVO RESPONSÁVEL (EFR)")) {
            return false;
        }
        if (!conteudo.contains("SITUAÇÃO CADASTRAL")) {
            return false;
        }
        if (!conteudo.contains("DATA DA SITUAÇÃO CADASTRAL")) {
            return false;
        }
        if (!conteudo.contains("MOTIVO DE SITUAÇÃO CADASTRAL")) {
            return false;
        }
        if (!conteudo.contains("SITUAÇÃO ESPECIAL")) {
            return false;
        }
        if (!conteudo.contains("SITUAÇÃO ESPECIAL")) {
            return false;
        }
        if (!conteudo.contains("DATA DA SITUAÇÃO ESPECIAL")) {
            return false;
        }

        return true;
    }

    private String limparString(String string) {
        // Retirar caracter misterioso da página da Receita Federal :D.
        string = string.replace((char) 10, ' ');
        // Retirar caracter misterioso da página da Receita Federal :D.
        string = string.replace((char) 160, ' ');
        // Retirar os preenchimentos com '*' nos cadastros não preenchidos.
        string = string.replaceAll("\\*", "");
        return string.trim();
    }

    public List<String> extrairCnaes(String cnae) {
        if (cnae.equals("Não informada")) {
            return new ArrayList();
        }

        StringBuilder numero_cnae = new StringBuilder();

        List<String> lista_cnae = new ArrayList();

        for (int i = 0; i < cnae.length(); i++) {
            char c = cnae.charAt(i);
            if (Character.isDigit(c)) {
                numero_cnae.append(c);
                continue;
            } else if (cnae.substring(i, i + 1).equals(".") && numero_cnae.length() > 1) {
                numero_cnae.append(c);
                continue;
            } else if (cnae.substring(i, i + 1).equals("-") && numero_cnae.length() > 1) {
                numero_cnae.append(c);
                continue;
            }

            if (numero_cnae.length() == 10) {
                //hash_cnae.put(numero_cnae.toString(), "");
                HashMap hash = new LinkedHashMap();
                hash.put(numero_cnae.toString(), "");

                lista_cnae.add(numero_cnae.toString());

                numero_cnae = new StringBuilder();
            }

        }

        int index = 0;
        for (int i = 0; i < lista_cnae.size(); i++) {
            // POSICAO UM ------------------------------------------------------
            int pos1 = cnae.indexOf(lista_cnae.get(i), index);
            index = pos1;

            // POSICAO DOIS ----------------------------------------------------
            int pos2;
            try {
                pos2 = cnae.indexOf(lista_cnae.get(i + 1), index);
            } catch (Exception e) {
                pos2 = cnae.length();
            }

            String descricao_cnae = "[" + lista_cnae.get(i) + "] " + cnae.substring(pos1 + lista_cnae.get(i).length(), pos2).replace("-", "").trim();
            lista_cnae.set(i, descricao_cnae);
        }

        return lista_cnae;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaminhoImagem() {
        ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        String path_imagem = servletContext.getRealPath("/resources/images/captcha/" + nameTemp + ".png");
        if (new File(path_imagem).exists()) {
            return "/resources/images/captcha/" + nameTemp + ".png";
        } else {
            return "";
        }
    }

//    public void setCaminhoImagem(String caminhoImagem) {
//        this.caminhoImagem = caminhoImagem;
//    }
    public HTMLDocument getHTMLDocument(String html) {
        HTMLEditorKit editorKit = new HTMLEditorKit();
        HTMLDocument document = (HTMLDocument) editorKit.createDefaultDocument();
        document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        InputStream inputStream = new ByteArrayInputStream(html.getBytes());
        try {
            editorKit.read(inputStream, document, 0);
        } catch (IOException | BadLocationException ex) {
            return null;
        }
        return document;
    }
}
