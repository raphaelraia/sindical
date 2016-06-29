package br.com.rtools.utilitarios;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Correios {

    public void main() {

        try {

            String urlCorreios = "http://m.correios.com.br/movel/buscaCepConfirma.do";

            Document doc = Jsoup.connect(urlCorreios)
                    .data("cepEntrada", "03582060")
                    .data("tipoCep", "")
                    .data("cepTemp", "")
                    .data("metodo", "buscarCep")
                    // and other hidden fields which are being passed in post request.
                    .userAgent("Mozilla")
                    .post();

            Elements campos = doc.select(".resposta");
            Elements valores = doc.select(".respostadestaque");

            for (int i = 0; i < campos.size(); i++) {
                Element campo = campos.get(i);
                Element valor = valores.get(i);
                if (campos.hasText() && valores.hasText()) {
                    System.out.println(campo.text().trim() + ":" + valor.text().trim());
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
