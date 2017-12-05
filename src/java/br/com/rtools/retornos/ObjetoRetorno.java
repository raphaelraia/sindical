package br.com.rtools.retornos;

import br.com.rtools.arrecadacao.dao.RetornoDao;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.RetornoReprocessa;
import br.com.rtools.utilitarios.Dao;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Claudemir Rtools
 */
public class ObjetoRetorno {

    private List<ObjetoArquivo> listaObjetoArquivo;
    private String erro;

    public ObjetoRetorno(List<ObjetoArquivo> listaObjetoArquivo, String erro) {
        this.listaObjetoArquivo = listaObjetoArquivo;
        this.erro = erro;
    }

    public static List<String> retornaLinhaDoArquivo(String caminho_arquivo) {
        List<String> lista = new ArrayList();
        //InputStreamReader i = new FileReader(caminho_arquivo);

        try (InputStreamReader reader = new FileReader(caminho_arquivo); BufferedReader buffReader = new BufferedReader(reader)) {
            //try (FileReader reader = new FileReader(caminho_arquivo); BufferedReader buffReader = new BufferedReader(reader)) {

            String linha;
            while ((linha = buffReader.readLine()) != null) {
                if (!linha.isEmpty()) {
                    lista.add(linha);
                }
            }

        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
        return lista;
    }

    public static File[] arquivos(String caminho) {

        File fl = new File(caminho);
        return fl.listFiles();

    }

    public static String continuarRetorno(Retorno retorno, Integer id_conta_cobranca, Integer sequencial) {
        if (!new RetornoDao().listaRetornoNaoPermitido(id_conta_cobranca, sequencial).isEmpty()) {
            return "RETORNO JÁ FOI BAIXADO: " + retorno.getArquivo();
        }

        List<RetornoReprocessa> lrr = new RetornoDao().listaRetornoReprocessa(id_conta_cobranca, sequencial);

        if (!lrr.isEmpty()) {
            for (RetornoReprocessa rr : lrr) {
                new Dao().delete(rr, true);
            }
        } else {
            retorno.setSequencial(sequencial);

            new Dao().save(retorno, true);
        }

        return "";
    }

    public List<ObjetoArquivo> getListaObjetoArquivo() {
        return listaObjetoArquivo;
    }

    public void setListaObjetoArquivo(List<ObjetoArquivo> listaObjetoArquivo) {
        this.listaObjetoArquivo = listaObjetoArquivo;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

}
