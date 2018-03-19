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

public class Santander extends ArquivoRetorno {

    public Santander(ContaCobranca contaCobranca) {
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
                    objeto_arquivo.setCnpj(linhas.get(0).substring(18, 32));
                    int codc = Integer.valueOf(linhas.get(0).substring(53, 61));
                    objeto_arquivo.setCodigoCedente(String.valueOf(codc));
                    objeto_arquivo.setSequencialArquivo(linhas.get(0).substring(157, 163));
                    
                    Integer sequencial_arquivo = Integer.parseInt(objeto_arquivo.getSequencialArquivo());

                    String m_erro = ObjetoRetorno.continuarRetorno(retorno, super.getContaCobranca().getId(), sequencial_arquivo);

                    if (!m_erro.isEmpty()) {
                        lista_objeto_retorno.add(new ObjetoRetorno(new ArrayList(), m_erro));
                        continue;
                    }

                    // SEGUNDA LINHA - HEADER LOTE
                    // -- PULAR
                    
                    // SEGMENTOS T - U
                    List<LinhaSegmento> lista_linha_segmento = new ArrayList();
                    for (int i = 0; i < linhas.size(); i++) {
                        LinhaSegmento linha_segmento = new LinhaSegmento();
                        
                        if (linhas.get(i).substring(13, 14).equals("T")) {
                            linha_segmento.setNossoNumero(linhas.get(i).substring(40, 52).trim());
                            linha_segmento.setValorTaxa(linhas.get(i).substring(193, 208));
                            linha_segmento.setDataVencimento(linhas.get(i).substring(69, 77));
                            // VERIFICA VENCIMENTO VÁLIDO
                            try {
                                if (Integer.parseInt(linha_segmento.getDataVencimento()) == 0) {
                                    linha_segmento.setDataVencimento("11111111");
                                }
                            } catch (Exception e) {
                            }
                            
                            // VERIFICA O STATUS DO MOVIMENTO RETORNADO
                            linha_segmento.setCodigoMovimento(linhas.get(i).substring(15, 17));
                            
                            StatusRetorno sr;
                            switch (linha_segmento.getCodigoMovimento()) {
                                // RETORNO VEM COM A CONFIRMAÇÃO QUE FOI REGISTRADO ( REFERENTE A REMESSA GERADA )
                                case "02":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 2); // BOLETO REGISTRADO
                                    break;
                                case "03":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 1); // BOLETO REJEITADO
                                    break;
                                case "06": // LIQUIDAÇÃO
                                case "17": // LIQUIDAÇÃO QUANDO O BOLETO PAGO NÃO É REGISTRADO
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                    break;
                                case "09":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6); // REGISTRO EXCLUÍDO
                                    break;
                                default:
                                    sr = null;
                                    break;
                            }
                            linha_segmento.setStatusRetorno(sr);
                            // FIM
                            i++;
                        }
                        
                        if (linhas.get(i).substring(13, 14).equals("U")) {
                            linha_segmento.setValorPago(linhas.get(i).substring(77, 92));
                            linha_segmento.setDataPagamento(linhas.get(i).substring(137, 145));
                            linha_segmento.setValorCredito(linhas.get(i).substring(92, 107));
                            linha_segmento.setDataCredito(linhas.get(i).substring(145, 153));
                            
                            lista_linha_segmento.add(linha_segmento);
                        }
                    }
                    objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                    lista_objeto_arquivo.add(objeto_arquivo);
                    
                    // AS DUAS ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                    // FOOTER LOTE
                    // FOOTER ARQUIVO
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
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaPadrao(Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }
}
