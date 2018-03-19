package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.ArquivoRetorno;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Sicoob400 extends ArquivoRetorno {

    public Sicoob400(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<ObjetoRetorno> sicob(boolean baixar, String host) {
        host = host + "/pendentes/";

        File arquivos[] = ObjetoRetorno.arquivos(host);
        List<ObjetoRetorno> lista_objeto_retorno = new ArrayList();

        // SE TEM ARQUIVOS
        if (arquivos != null) {

            for (int u = 0; u < arquivos.length; u++) {
                Retorno retorno = new Retorno(-1, super.getContaCobranca(), DataHoje.dataHoje(), arquivos[u].getName(), null, "");

                List<ObjetoArquivo> lista_objeto_arquivo = new ArrayList();
                ObjetoArquivo objeto_arquivo = new ObjetoArquivo();

                try {
                    List<String> linhas = ObjetoRetorno.retornaLinhaDoArquivo(host + arquivos[u].getName());

                    objeto_arquivo.setNomePasta(host);
                    objeto_arquivo.setNomeArquivo(arquivos[u].getName());
                    objeto_arquivo.setRetorno(retorno);

                    // PRIMEIRA LINHA - HEADER ARQUIVO
                    objeto_arquivo.setCnpj(linhas.get(1).substring(3, 17));
                    objeto_arquivo.setCodigoCedente(linhas.get(0).substring(31, 40));
                    objeto_arquivo.setSequencialArquivo(linhas.get(0).substring(100, 107));

                    Integer sequencial_arquivo = Integer.parseInt(objeto_arquivo.getSequencialArquivo());

                    String m_erro = ObjetoRetorno.continuarRetorno(retorno, super.getContaCobranca().getId(), sequencial_arquivo);

                    if (!m_erro.isEmpty()) {
                        lista_objeto_retorno.add(new ObjetoRetorno(new ArrayList(), m_erro));
                        continue;
                    }
                    
                    List<LinhaSegmento> lista_linha_segmento = new ArrayList();
                    for (int i = 0; i < linhas.size(); i++) {
                        LinhaSegmento linha_segmento = new LinhaSegmento();

                        if (linhas.get(i).substring(0, 1).equals("1")) {
                            
                            if (linhas.get(i).substring(82, 84).equals("OU")) {
                                linha_segmento.setNossoNumero(linhas.get(i).substring(62, 73).trim());
                            } else {
                                linha_segmento.setNossoNumero(linhas.get(i).substring(62, 74).trim());
                            }

                            // VERIFICA O STATUS DO MOVIMENTO RETORNADO
                            linha_segmento.setCodigoMovimento(linhas.get(i).substring(108, 110));

                            StatusRetorno sr;
                            switch (linha_segmento.getCodigoMovimento()) {
                                // RETORNO VEM COM A CONFIRMAÇÃO QUE FOI REGISTRADO ( REFERENTE A REMESSA GERADA )
                                case "02":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 2); // BOLETO REGISTRADO
                                    break;
                                case "03":
                                    sr = null; // BOLETO REJEITADO ( NO MANUAL NÃO CONSTA ESSE RETORNO )
                                    break;
                                case "05": // BOLETO LIQUIDADO SEM REGISTRO
                                case "06": // BOLETO LIQUIDADO COM REGISTRO
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                    break;
                                case "09": // 09 - BAIXA EXECUTADA DIRETO PELO BANCO (BOLETO EXCLUIDO)
                                case "10": // 10 - BAIXA EXECUTADA PELO CLIENTE (BOLETO EXCLUIDO)
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6);
                                    break;
                                default:
                                    sr = null;
                                    break;
                            }
                            linha_segmento.setStatusRetorno(sr);
                            
                            //valorTaxa = ((String) lista.get(i)).substring(95, 100); // taxa de desconto
                            linha_segmento.setValorTaxa(linhas.get(i).substring(181, 188));
                            linha_segmento.setDataVencimento(linhas.get(i).substring(146, 152));
                            linha_segmento.setDataVencimento(linha_segmento.getDataVencimento().substring(0, linha_segmento.getDataVencimento().length() - 2) + "20" + linha_segmento.getDataVencimento().substring(linha_segmento.getDataVencimento().length() - 2, linha_segmento.getDataVencimento().length()));
                            // VERIFICA VENCIMENTO VÁLIDO
                            try {
                                if (Integer.parseInt(linha_segmento.getDataVencimento()) == 0) {
                                    linha_segmento.setDataVencimento("11111111");
                                }
                            } catch (NumberFormatException e) {
                                e.getMessage();
                            }

                            linha_segmento.setValorPago(linhas.get(i).substring(152, 165));
                            linha_segmento.setValorCredito(linhas.get(i).substring(253, 266)); // ANTES AQUI ERA O ( valor pago )
                            
                            linha_segmento.setDataPagamento(linhas.get(i).substring(110, 116));
                            linha_segmento.setDataPagamento(linha_segmento.getDataPagamento().substring(0, linha_segmento.getDataPagamento().length() - 2) + "20" + linha_segmento.getDataPagamento().substring(linha_segmento.getDataPagamento().length() - 2, linha_segmento.getDataPagamento().length()));
                            
                            linha_segmento.setDataCredito(linhas.get(i).substring(175, 181));
                            linha_segmento.setDataCredito(linha_segmento.getDataCredito().substring(0, linha_segmento.getDataCredito().length() - 2) + "20" + linha_segmento.getDataCredito().substring(linha_segmento.getDataCredito().length() - 2, linha_segmento.getDataCredito().length()));

                            // TEMPORARIO ---------- COMENTEI O CÓDIGO NA DATA 23/10/2017
//                            SicoobDao dao = new SicoobDao(); // TEMPORÁRIO
//                            List<Object> boletox = dao.xsicoob(linha_segmento.getNossoNumero());
//
//                            if (!boletox.isEmpty()) {
//                                double valor_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorPago())), 100);
//                                List linhaX = ((List) boletox.get(0));
//
//                                if ((((Double) linhaX.get(1)).doubleValue() - 0.05) < valor_pago && (((Double) linhaX.get(1)).doubleValue() + 0.05) > valor_pago) {
//
//                                } else {
//                                    // UPDATE
//                                    String data_pagamento = DataHoje.colocarBarras(linha_segmento.getDataPagamento());
//                                    dao.xupdate(data_pagamento, linha_segmento.getNossoNumero());
//                                    i++;
//                                    continue;
//                                }
//                            }

                            lista_linha_segmento.add(linha_segmento);
                        }
                    }
                    objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                    lista_objeto_arquivo.add(objeto_arquivo);

                    // ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                } catch (Exception e) {
                    ObjetoRetorno objeto_retorno = new ObjetoRetorno(new ArrayList(), e.getMessage());
                    lista_objeto_retorno.add(objeto_retorno);
                    new Dao().delete(retorno, true);
                    return lista_objeto_retorno;
                }
                ObjetoRetorno objeto_retorno = new ObjetoRetorno(lista_objeto_arquivo, "");
                lista_objeto_retorno.add(objeto_retorno);
            }
        }
        return lista_objeto_retorno;
    }

    @Override
    public List<ObjetoRetorno> sindical(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public List<ObjetoRetorno> sigCB(boolean baixar, String host) {
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
