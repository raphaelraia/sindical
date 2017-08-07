package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.ArquivoRetorno;
import br.com.rtools.utilitarios.GenericaRetorno;
import br.com.rtools.utilitarios.Moeda;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BancoBrasil extends ArquivoRetorno {

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

    private Integer statusRegistro = null;

    public BancoBrasil(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<GenericaRetorno> sicob(boolean baixar, String host) {
        host = host + "/pendentes/";
        pasta = host;

        File fl = new File(host);
        File listFile[] = fl.listFiles();
        List<GenericaRetorno> listaRetorno = new ArrayList();
        if (listFile != null) {
            int qntRetornos = listFile.length;
            String valorDescontado = "";
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
                        if (i < 1) {
                            cnpj = ((String) lista.get(i)).substring(18, 32);
                            codigoCedente = ((String) lista.get(i)).substring(58, 70);
                        }
                        if (((String) lista.get(i)).substring(13, 14).equals("T")) {
                            if (super.getContaCobranca().getBoletoInicial().length() == 17) {
                                nossoNumero = ((String) lista.get(i)).substring(37, 57).trim();
                            } else {
                                nossoNumero = ((String) lista.get(i)).substring(37, 48).trim();
                            }
                            valorTaxa = ((String) lista.get(i)).substring(198, 213);
                            dataVencimento = ((String) lista.get(i)).substring(73, 81);

                            switch (((String) lista.get(i)).substring(15, 17)) {
                                // RETORNO VEM COM A CONFIRMAÇÃO QUE FOI REGISTRADO ( REFERENTE A REMESSA GERADA )
                                case "02":
                                    statusRegistro = 1; // BOLETO REGISTRADO
                                    break;
                                case "03":
                                    statusRegistro = 2; // BOLETO REJEITADO
                                    break;
                                default:
                                    statusRegistro = 3; // BOLETO PARA BAIXAR
                                    break;
                            }
                        }
                        try {
                            int con = Integer.parseInt(dataVencimento);
                            if (con == 0) {
                                dataVencimento = "11111111";
                            }
                        } catch (Exception e) {
                        }
                        i++;
                        if (i < lista.size() && ((String) lista.get(i)).substring(13, 14).equals("U")) {
                            valorPago = ((String) lista.get(i)).substring(77, 92);
                            dataPagamento = ((String) lista.get(i)).substring(137, 145);
                            valorDescontado = ((String) lista.get(i)).substring(196, 204).trim();
                            if (!valorDescontado.isEmpty()) {
                                double valor_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valorPago)), 100);
                                double valor_descontado = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valorDescontado)), 100);
                                String calculo = Moeda.converteR$Double(Moeda.soma(valor_descontado, valor_pago));
                                String calculo_s = ("" + calculo).replace(",", "").replace(".", "");
                                valorPago = "000000000000000".substring(0, 15 - calculo_s.length()) + calculo_s;
                            }

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
                            i++;
                        }
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
