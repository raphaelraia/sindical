package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.ArquivoRetorno;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaRetorno;
import br.com.rtools.utilitarios.Moeda;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Sicoob400 extends ArquivoRetorno {

    private String linha = "",
            pasta = "",
            cnpj = "",
            codigoCedente = "",
            nossoNumero = "",
            dataVencimento = "",
            valorTaxa = "",
            valorPago = "",
            valorCredito = "",
            valorRepasse = "",
            dataPagamento = "",
            dataCredito = "";

    public Sicoob400(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<GenericaRetorno> sicob(boolean baixar, String host) {
        host = host + "/pendentes/";
        pasta = host;

        File fl = new File(host);
        File listFile[] = fl.listFiles();
        List<GenericaRetorno> listaRetorno = new ArrayList();

        SicoobDao dao = new SicoobDao(); // TEMPORÁRIO
        if (listFile != null) {
            int qntRetornos = listFile.length;
            for (int u = 0; u < qntRetornos; u++) {
                try {
                    FileReader reader = new FileReader(host + listFile[u].getName());
                    BufferedReader buffReader = new BufferedReader(reader);
                    List lista = new Vector();
                    while ((linha = buffReader.readLine()) != null) {
                        lista.add(linha);
                    }
                    reader.close();
                    buffReader.close();
                    int i = 0;
                    while (i < lista.size()) {
                        // HEADER
                        if (i < 1) {
                            codigoCedente = ((String) lista.get(i)).substring(31, 40);
                            i++;
                            continue;
                        }

                        if ((i + 1) != lista.size()) {
                            cnpj = ((String) lista.get(i)).substring(3, 17);

                            if ((((String) lista.get(i)).substring(82, 84)).equals("OU")) {
                                nossoNumero = ((String) lista.get(i)).substring(62, 73).trim();
                            } else {
                                nossoNumero = ((String) lista.get(i)).substring(62, 74).trim();
                            }
                            
                            //valorTaxa = ((String) lista.get(i)).substring(95, 100); // taxa de desconto
                            valorTaxa = ((String) lista.get(i)).substring(181, 188); // valor da tarifa

                            dataVencimento = ((String) lista.get(i)).substring(146, 150) + "20" + ((String) lista.get(i)).substring(150, 152);
                            try {
                                int con = Integer.parseInt(dataVencimento);
                                if (con == 0) {
                                    dataVencimento = "11111111";
                                }
                            } catch (Exception e) {
                            }

                            // valorPago = ((String) lista.get(i)).substring(152, 165); // (Valor Titulo) -- CHAMADO 1061
                            valorPago = ((String) lista.get(i)).substring(253, 266); // (valor recebido parcial)
                            dataPagamento = ((String) lista.get(i)).substring(110, 114) + "20" + ((String) lista.get(i)).substring(114, 116);

                            // TEMPORARIO ----------
                            List<Object> boletox = dao.xsicoob(nossoNumero);
                            if (!boletox.isEmpty()) {
                                float valor_pago = Moeda.divisaoValores(Moeda.substituiVirgulaFloat(Moeda.converteR$(valorPago)), 100);
                                List linhaX = ((List) boletox.get(0));

                                if ((((Double) linhaX.get(1)).floatValue() - 0.05) < valor_pago && (((Double) linhaX.get(1)).floatValue() + 0.05) > valor_pago) {

                                } else {
                                    // UPDATE
                                    String data_pagamento = DataHoje.colocarBarras(dataPagamento);
                                    dao.xupdate(data_pagamento, nossoNumero);
                                    i++;
                                    continue;
                                }
                            }
                            // -------------

                            listaRetorno.add(new GenericaRetorno(
                                    cnpj, //1 ENTIDADE
                                    codigoCedente, //2 NESTE CASO SICAS
                                    nossoNumero, //3
                                    valorPago, //4
                                    valorTaxa, //5
                                    "",//valorCredito,   //6
                                    dataPagamento, //7
                                    dataVencimento,//dataVencimento, //8
                                    "", //9 ACRESCIMO
                                    "", //10 VALOR DESCONTO
                                    "", //11 VALOR ABATIMENTO
                                    "", //12 VALOR REPASSE ...(valorPago - valorCredito)
                                    pasta, // 13 NOME DA PASTA
                                    listFile[u].getName(), //14 NOME DO ARQUIVO
                                    "", //15 DATA CREDITO
                                    "") // 16 SEQUENCIAL DO ARQUIVO
                            );
                        }
                        i++;
                    }
                } catch (Exception e) {

                }
            }
        }
        return listaRetorno;
    }

    @Override
    public List<GenericaRetorno> sindical(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public List<GenericaRetorno> sigCB(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCB(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCBSocial(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSicob(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivo(this.sicob(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaSicobSocial(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivoSocial(this.sicob(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaPadrao(Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }
}
