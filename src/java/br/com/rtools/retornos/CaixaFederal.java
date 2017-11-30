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

public class CaixaFederal extends ArquivoRetorno {

    public CaixaFederal(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<ObjetoRetorno> sindical(boolean baixar, String host) {
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

                    String cod_cedente = "0000000".substring(0, 7 - super.getContaCobranca().getCodCedente().length()) + super.getContaCobranca().getCodCedente();

                    Boolean layout_antigo = true;

                    if (linhas.get(0).substring(33, 38).equals(super.getContaCobranca().getSicasSindical())) {
                        objeto_arquivo.setCodigoCedente(linhas.get(0).substring(33, 38));
                        layout_antigo = true;
                        objeto_arquivo.setArquivoComErro(false);
                    } else if (linhas.get(0).substring(58, 65).equals(cod_cedente)) {
                        objeto_arquivo.setCodigoCedente(linhas.get(0).substring(58, 65));
                        layout_antigo = false;
                        objeto_arquivo.setArquivoComErro(false);
                    } else if (linhas.get(0).substring(60, 67).equals(cod_cedente)) {
                        // ARQUIVO COM ERRO ( bug na primeira linha do arquivo retorno )
                        objeto_arquivo.setCodigoCedente(linhas.get(0).substring(60, 67));
                        layout_antigo = false;
                        objeto_arquivo.setArquivoComErro(true);
                        retorno.setObservacao("Arquivo com erro no Header de Arquivo ( Dois espaços à esquerda do cedente )");
                    }

                    // O LAYOUT É ANTIGO ?
                    
                    if (layout_antigo) {
                        // PRIMEIRA LINHA - HEADER ARQUIVO
                        objeto_arquivo.setCnpj(linhas.get(0).substring(18, 32));

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

                            // SEGMENTO T
                            if (linhas.get(i).substring(13, 14).equals("T")) {
                                linha_segmento.setNossoNumero(linhas.get(i).substring(133, 148));
                                linha_segmento.setValorTaxa(linhas.get(i).substring(198, 213));
                                linha_segmento.setDataVencimento(linhas.get(i).substring(73, 81));
                                // VERIFICA VENCIMENTO VÁLIDO
                                try {
                                    if (Integer.parseInt(linha_segmento.getDataVencimento()) == 0) {
                                        linha_segmento.setDataVencimento("11111111");
                                    }
                                } catch (NumberFormatException e) {
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
                                    case "06":
                                        sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                        break;
                                    case "09":
                                        sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6); // REGISTRO EXCLUÍDO
                                        break;
                                    case "28": // de acordo com o manual ‘28’ Débito de Tarifas/Custas 
                                        i++;
                                        continue;
                                    default:
                                        sr = null;
                                        break;
                                }
                                linha_segmento.setStatusRetorno(sr);
                                // FIM
                            }
                            // SEGMENTO U
                            // NÃO LEMBRO PORQUE FAZ ESSA CONVERSÃO Double.valueOf
                            //if (linhas.get(i).substring(13, 14).equals("U") && Double.valueOf(linha_segmento.getNossoNumero()) != 0) {
                            i++;
                            if (linhas.get(i).substring(13, 14).equals("U")) {
                                linha_segmento.setValorPago(linhas.get(i).substring(77, 92));
                                linha_segmento.setValorCredito(linhas.get(i).substring(92, 107));
                                linha_segmento.setDataPagamento(linhas.get(i).substring(137, 145));
                                linha_segmento.setDataCredito(linhas.get(i).substring(145, 153));

                                int vlPg = 0;
                                int vlCr = 0;
                                if (!linha_segmento.getValorPago().isEmpty()) {
                                    vlPg = Integer.parseInt(linha_segmento.getValorPago());
                                }
                                if (!linha_segmento.getValorCredito().isEmpty()) {
                                    vlCr = Integer.parseInt(linha_segmento.getValorCredito());
                                }

                                String valor_repasse = Integer.toString(vlPg - vlCr);

                                linha_segmento.setValorRepasse(valor_repasse);

                                lista_linha_segmento.add(linha_segmento);

                            }
                        }

                        objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                        lista_objeto_arquivo.add(objeto_arquivo);

                        // AS DUAS ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                        // FOOTER LOTE
                        // FOOTER ARQUIVO
                    // O LAYOUT É ANTIGO ?
                    } else {
                        // PRIMEIRA LINHA - HEADER ARQUIVO
                        objeto_arquivo.setCnpj(linhas.get(0).substring(18, 32));
                        
                        objeto_arquivo.setSequencialArquivo(linhas.get(0).substring(158, 164));

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

                            // SEGMENTO T
                            if (linhas.get(i).substring(13, 14).equals("T")) {
                                linha_segmento.setNossoNumero(linhas.get(i).substring(134, 149));
                                linha_segmento.setValorTaxa(linhas.get(i).substring(199, 214));
                                linha_segmento.setDataVencimento(linhas.get(i).substring(74, 82));
                                // VERIFICA VENCIMENTO VÁLIDO
                                try {
                                    if (Integer.parseInt(linha_segmento.getDataVencimento()) == 0) {
                                        linha_segmento.setDataVencimento("11111111");
                                    }
                                } catch (NumberFormatException e) {
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
                                    case "06":
                                        sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                        break;
                                    case "09":
                                        sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6); // REGISTRO EXCLUÍDO
                                        break;
                                    case "28": // de acordo com o manual ‘28’ Débito de Tarifas/Custas 
                                        i++;
                                        continue;
                                    default:
                                        sr = null;
                                        break;
                                }
                                linha_segmento.setStatusRetorno(sr);
                                // FIM
                            }
                            // SEGMENTO U
                            // NÃO LEMBRO PORQUE FAZ ESSA CONVERSÃO Double.valueOf
                            //if (linhas.get(i).substring(13, 14).equals("U") && Double.valueOf(linha_segmento.getNossoNumero()) != 0) {
                            i++;
                            if (linhas.get(i).substring(13, 14).equals("U")) {
                                linha_segmento.setValorPago(linhas.get(i).substring(86, 101));
                                linha_segmento.setValorCredito(linhas.get(i).substring(101, 116));
                                linha_segmento.setDataPagamento(linhas.get(i).substring(146, 154));
                                linha_segmento.setDataCredito(linhas.get(i).substring(154, 162));

                                int vlPg = 0;
                                int vlCr = 0;
                                if (!linha_segmento.getValorPago().isEmpty()) {
                                    vlPg = Integer.parseInt(linha_segmento.getValorPago());
                                }
                                if (!linha_segmento.getValorCredito().isEmpty()) {
                                    vlCr = Integer.parseInt(linha_segmento.getValorCredito());
                                }

                                String valor_repasse = Integer.toString(vlPg - vlCr);

                                linha_segmento.setValorRepasse(valor_repasse);

                                lista_linha_segmento.add(linha_segmento);

                            }
                        }

                        objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                        lista_objeto_arquivo.add(objeto_arquivo);

                        // AS DUAS ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                        // FOOTER LOTE
                        // FOOTER ARQUIVO
                    }
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
                    objeto_arquivo.setCodigoCedente(linhas.get(0).substring(59, 70));
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

                        // SEGMENTO T
                        if (linhas.get(i).substring(13, 14).equals("T")) {
                            linha_segmento.setNossoNumero(linhas.get(i).substring(46, 56).trim());
                            linha_segmento.setValorTaxa(linhas.get(i).substring(199, 213));
                            linha_segmento.setDataVencimento(linhas.get(i).substring(73, 81));
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
                                case "06":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                    break;
                                case "09":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6); // REGISTRO EXCLUÍDO
                                    break;
                                case "28": // de acordo com o manual ‘28’ Débito de Tarifas/Custas 
                                    i++;
                                    continue;
                                default:
                                    sr = null;
                                    break;
                            }
                            linha_segmento.setStatusRetorno(sr);
                            // FIM
                        }

                        i++;
                        if (linhas.get(i).substring(13, 14).equals("U")) {
                            linha_segmento.setValorPago(linhas.get(i).substring(77, 92));
                            linha_segmento.setDataPagamento(linhas.get(i).substring(137, 145));

                            lista_linha_segmento.add(linha_segmento);
                        }
                    }
                    objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                    lista_objeto_arquivo.add(objeto_arquivo);

                    // AS DUAS ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                    // FOOTER LOTE
                    // FOOTER ARQUIVO
                } catch (NumberFormatException e) {
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
    public List<ObjetoRetorno> sigCB(boolean baixar, String host) {
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
                    objeto_arquivo.setCodigoCedente(linhas.get(0).substring(58, 64));
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

                        // SEGMENTO T
                        if (linhas.get(i).substring(13, 14).equals("T")) {
                            linha_segmento.setNossoNumero(linhas.get(i).substring(39, 56).trim());
                            linha_segmento.setValorTaxa(linhas.get(i).substring(198, 213));
                            linha_segmento.setDataVencimento(linhas.get(i).substring(73, 81));
                            // VERIFICA VENCIMENTO VÁLIDO
                            try {
                                if (Integer.parseInt(linha_segmento.getDataVencimento()) == 0) {
                                    linha_segmento.setDataVencimento("11111111");
                                }
                            } catch (NumberFormatException e) {
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
                                case "06":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 3); // BOLETO PARA BAIXAR
                                    break;
                                case "09":
                                    sr = (StatusRetorno) new Dao().find(new StatusRetorno(), 6); // REGISTRO EXCLUÍDO
                                    break;
                                case "28": // de acordo com o manual ‘28’ Débito de Tarifas/Custas 
                                    i++;
                                    continue;
                                default:
                                    sr = null;
                                    break;
                            }
                            linha_segmento.setStatusRetorno(sr);
                            // FIM

                            i++;
                        }

                        if (linhas.get(i).substring(13, 14).equals("U")) {
                            linha_segmento.setValorPago(linhas.get(i).substring(77, 92).trim());
                            linha_segmento.setDataPagamento(linhas.get(i).substring(137, 145).trim());

                            lista_linha_segmento.add(linha_segmento);
                        }
                    }

                    objeto_arquivo.setLinhaSegmento(lista_linha_segmento);
                    lista_objeto_arquivo.add(objeto_arquivo);

                    // AS DUAS ÚLTIMAS LINHAS NÃO TEM NECESSIDADE DE LER
                    // FOOTER LOTE
                    // FOOTER ARQUIVO
                } catch (NumberFormatException e) {
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
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivo(this.sindical(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaSigCB(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivo(this.sigCB(true, caminho), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaSigCBSocial(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivoSocial(this.sigCB(true, caminho), caminho, usuario);
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
