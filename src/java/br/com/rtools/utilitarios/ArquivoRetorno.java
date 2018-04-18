package br.com.rtools.utilitarios;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.DocumentoInvalidoDao;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.RetornoBanco;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.FechamentoDiarioDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.DocumentoInvalido;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.retornos.CasoSindical;
import br.com.rtools.retornos.ContinuaBaixa;
import br.com.rtools.retornos.LinhaSegmento;
import br.com.rtools.retornos.ObjetoArquivo;
import br.com.rtools.retornos.ObjetoDetalheRetorno;
import br.com.rtools.retornos.ObjetoRetorno;
import br.com.rtools.retornos.RetornoCasoSindical;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class ArquivoRetorno {

    private ContaCobranca contaCobranca;

    public final static int SICOB = 1;
    public final static int SINDICAL = 2;
    public final static int SIGCB = 3;
    public final static int CAIXA_FEDERAL = 109;
    public final static int REAL = 82;
    //public final static int BANESPA = 82;
    public final static int BANCO_BRASIL = 36;
    public final static int ITAU = 63;
    public final static int SANTANDER = 88;
    public final static int SICOOB = 26;

    public abstract List<ObjetoRetorno> sicob(boolean baixar, String host);

    public abstract List<ObjetoRetorno> sindical(boolean baixar, String host);

    public abstract List<ObjetoRetorno> sigCB(boolean baixar, String host);

    public abstract String darBaixaSicob(String caminho, Usuario usuario);

    public abstract String darBaixaSigCB(String caminho, Usuario usuario);

    public abstract String darBaixaSindical(String caminho, Usuario usuario);

    public abstract String darBaixaPadrao(Usuario usuario);

    public abstract String darBaixaSicobSocial(String caminho, Usuario usuario);

    public abstract String darBaixaSigCBSocial(String caminho, Usuario usuario);

    public static final String tipo(String arquivo) {
        File file = new File(arquivo);
        try {
            BufferedReader buffReader;
            try (FileReader reader = new FileReader(file)) {
                buffReader = new BufferedReader(reader);
                String linha = buffReader.readLine();
                buffReader.close();
                String test = linha.substring(399);
                return "400";
            } catch (Exception x) {
                return "240";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    protected ArquivoRetorno(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    protected String baixarArquivo(List<ObjetoRetorno> lista_objeto_retorno, String caminho, Usuario usuario) {
        GenericaSessao.remove("detalhes_retorno_banco");

        String referencia = "";
        String dataVencto = "";
        String result = "";
        String destino = caminho + "/" + DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1] + "-" + DataHoje.ArrayDataHoje()[0];

        boolean moverArquivo = true;
        List<String> errors = new ArrayList();

        MovimentoDao db = new MovimentoDao();
        JuridicaDao dbJur = new JuridicaDao();
        List<Movimento> movimento;
        Dao dao = new Dao();
        File fl = new File(caminho + "/pendentes/");
        File listFls[] = fl.listFiles();
        File flDes = new File(destino); // 0 DIA, 1 MES, 2 ANO
        flDes.mkdir();
        TipoServico tipoServico;
        HashMap hash_detalhes = new LinkedHashMap();
        List<ObjetoDetalheRetorno> lista_detalhe = new ArrayList();

        String data_fechamento_diario = DataHoje.converteData(new FechamentoDiarioDao().ultimaDataContaSaldo());

        for (ObjetoRetorno ob : lista_objeto_retorno) {

            if (!ob.getErro().isEmpty()) {
                lista_detalhe.add(new ObjetoDetalheRetorno(null, 7, ob.getErro()));
                continue;
            }

//            for (ObjetoArquivo oa : ob.getListaObjetoArquivo()) {
//
//                for (LinhaSegmento ls : oa.getLinhaSegmento()) {
//                    String data_pag = DataHoje.colocarBarras(ls.getDataPagamento());
//                    if (DataHoje.maiorData(data_pag, data_fechamento_diario) || data_pag.equals(data_fechamento_diario)) {
//                        return "Fechamento diário já concluído hoje, ARQUIVO NÃO PODERÁ SER BAIXADO!";
//                    }
//                }
//
//            }
        }

        // LAYOUT 2 = SINDICAL
        if (this.getContaCobranca().getLayout().getId() == 2) {
            for (ObjetoRetorno objeto_retorno : lista_objeto_retorno) {

                for (ObjetoArquivo objeto_arquivo : objeto_retorno.getListaObjetoArquivo()) {

                    for (LinhaSegmento linha_segmento : objeto_arquivo.getLinhaSegmento()) {
                        // VERIFICA O TIPO DA EMPRESA -------------------------------------------------------------------------------------------------
                        // ----------------------------------------------------------------------------------------------------------------------------
                        if (((Registro) dao.find(new Registro(), 1)).getTipoEmpresa().equals("E")) {
                            // VERIFICA O ANO QUE VEIO NO ARQUIVO MENOR QUE ANO 2000 -------------------------------------------------------
                            // -------------------------------------------------------------------------------------------------------------
                            if (Integer.parseInt(linha_segmento.getDataVencimento().substring(4, 8)) < 2000) {
                                referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(linha_segmento.getDataPagamento()));
                                dataVencto = DataHoje.colocarBarras(linha_segmento.getDataPagamento());
                                if (referencia.substring(0, 2).equals("03")) {
                                    tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                                } else {
                                    tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                                }
                            } else {
                                referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(linha_segmento.getDataVencimento()));
                                dataVencto = DataHoje.colocarBarras(linha_segmento.getDataVencimento());
                                if (referencia.substring(0, 2).equals("03")) {
                                    tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                                } else {
                                    tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                                }
                            }
                        } else if (Integer.parseInt(linha_segmento.getDataVencimento().substring(4, 8)) < 2000) {
                            referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(linha_segmento.getDataPagamento()));
                            dataVencto = DataHoje.colocarBarras(linha_segmento.getDataPagamento());
                            if (referencia.substring(0, 2).equals("01")) {
                                tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                            } else {
                                tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                            }
                        } else {
                            referencia = DataHoje.dataReferencia(DataHoje.colocarBarras(linha_segmento.getDataVencimento()));
                            dataVencto = DataHoje.colocarBarras(linha_segmento.getDataVencimento());
                            if (referencia.substring(0, 2).equals("01")) {
                                tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
                            } else {
                                tipoServico = (TipoServico) dao.find(new TipoServico(), 2);
                            }
                        }
                        // ----------------------------------------------------------------------------------------------------------------------------
                        // ----------------------------------------------------------------------------------------------------------------------------

                        // 1 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO JA BAIXADO -------------------------------------------------
                        // -------------------------------------------------------------------------------------------------------------------
                        RetornoCasoSindical rcs = new CasoSindical().CASO_1(
                                this.getContaCobranca().getId(),
                                linha_segmento.getNossoNumero(),
                                linha_segmento.getDataPagamento(),
                                linha_segmento.getValorPago(),
                                tipoServico,
                                referencia,
                                dataVencto,
                                linha_segmento.getValorTaxa(),
                                linha_segmento.getValorCredito(),
                                usuario,
                                linha_segmento.getDataCredito(),
                                Integer.valueOf(objeto_arquivo.getSequencialArquivo())
                        );

                        if (rcs.getRetorno() == true) {

                            lista_detalhe.add(rcs.getOdr());

                            continue;
                        }

                        // 2 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO AINDA NÃO BAIXADO -------------------------------------------------------------------
                        rcs = new CasoSindical().CASO_2(
                                this.getContaCobranca().getId(),
                                linha_segmento.getNossoNumero(),
                                linha_segmento.getStatusRetorno(),
                                objeto_arquivo.getRetorno(),
                                linha_segmento.getValorPago(),
                                linha_segmento.getValorTaxa(),
                                linha_segmento.getValorCredito(),
                                usuario,
                                linha_segmento.getDataPagamento(),
                                linha_segmento.getDataCredito(),
                                Integer.valueOf(objeto_arquivo.getSequencialArquivo())
                        );

                        if (rcs.getRetorno() == true) {

                            lista_detalhe.add(rcs.getOdr());

                            continue;
                        }

                        String VALIDA = validaCNPJ(linha_segmento.getCnpjPagador(), objeto_arquivo.getCnpj());
                        switch (VALIDA) {
                            case "nosso_numero":

                                // 1 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO JA BAIXADO -------------------------------------------------
                                // -------------------------------------------------------------------------------------------------------------------
                                rcs = new CasoSindical().CASO_1(
                                        this.getContaCobranca().getId(),
                                        linha_segmento.getCnpjPagador(),
                                        linha_segmento.getDataPagamento(),
                                        linha_segmento.getValorPago(),
                                        tipoServico,
                                        referencia,
                                        dataVencto,
                                        linha_segmento.getValorTaxa(),
                                        linha_segmento.getValorCredito(),
                                        usuario,
                                        linha_segmento.getDataCredito(),
                                        Integer.valueOf(objeto_arquivo.getSequencialArquivo())
                                );

                                if (rcs.getRetorno() == true) {

                                    lista_detalhe.add(rcs.getOdr());

                                    continue;
                                }

                                // 2 caso VERIFICA SE EXISTE BOLETO PELO NUMERO DO BOLETO AINDA NÃO BAIXADO -------------------------------------------------------------------
                                rcs = new CasoSindical().CASO_2(
                                        this.getContaCobranca().getId(),
                                        linha_segmento.getCnpjPagador(),
                                        linha_segmento.getStatusRetorno(),
                                        objeto_arquivo.getRetorno(),
                                        linha_segmento.getValorPago(),
                                        linha_segmento.getValorTaxa(),
                                        linha_segmento.getValorCredito(),
                                        usuario,
                                        linha_segmento.getDataPagamento(),
                                        linha_segmento.getDataCredito(),
                                        Integer.valueOf(objeto_arquivo.getSequencialArquivo())
                                );

                                if (rcs.getRetorno() == true) {

                                    lista_detalhe.add(rcs.getOdr());
                                }

                                break;
                            case "cnpj":
                            case "cnpj_sem_dv":

                                if (VALIDA.equals("cnpj_sem_dv")) {
                                    String cnpj_sem_dv = linha_segmento.getCnpjPagador().substring(linha_segmento.getCnpjPagador().length() - 12, linha_segmento.getCnpjPagador().length());
                                    String digito = ValidaDocumentos.retonarDigitoCNPJ(cnpj_sem_dv);

                                    linha_segmento.setCnpjPagador(cnpj_sem_dv + digito);
                                }
                                
                                // 3 caso VERIFICA SE EXISTE BOLETO PELO CNPJ DA EMPRESA + DATA DE PAGAMENTO + VALOR PAGO BAIXADO ---------------------------
                                // ------------------------------------------------------------------------------------------------------
                                rcs = new CasoSindical().CASO_3(
                                        this.getContaCobranca().getId(),
                                        linha_segmento.getCnpjPagador(),
                                        linha_segmento.getDataPagamento(),
                                        linha_segmento.getValorPago()
                                );

                                if (rcs.getRetorno() == true) {

                                    lista_detalhe.add(rcs.getOdr());

                                    continue;
                                }

                                // 4 caso VERIFICA SE EXISTE BOLETO PELO CNPJ DA EMPRESA ------------------------------------------------
                                // ------------------------------------------------------------------------------------------------------
                                rcs = new CasoSindical().CASO_4(
                                        this.getContaCobranca().getId(),
                                        linha_segmento.getCnpjPagador(),
                                        linha_segmento.getDataPagamento(),
                                        linha_segmento.getValorPago(),
                                        referencia,
                                        tipoServico,
                                        linha_segmento.getStatusRetorno(),
                                        objeto_arquivo.getRetorno(),
                                        linha_segmento.getValorTaxa(),
                                        linha_segmento.getValorCredito(),
                                        usuario,
                                        linha_segmento.getDataCredito(),
                                        Integer.valueOf(objeto_arquivo.getSequencialArquivo()),
                                        dataVencto
                                );

                                if (rcs.getRetorno() == true) {

                                    lista_detalhe.add(rcs.getOdr());

                                    continue;
                                }
                                break;
                            case "cpf":
                                lista_detalhe.add(new ObjetoDetalheRetorno(null, 7, "Baixa para CPF não existe"));
                                continue;
                            default:
                                lista_detalhe.add(new ObjetoDetalheRetorno(null, 7, "Campo CNPJ Pagante inválido"));
                        }

                    }

                }

            }
        } else {
            // SE NÃO FOR SINDICAL ---------------------------
            for (ObjetoRetorno objeto_retorno : lista_objeto_retorno) {

                for (ObjetoArquivo objeto_arquivo : objeto_retorno.getListaObjetoArquivo()) {

                    String cnpj = AnaliseString.mascaraCnpj(objeto_arquivo.getCnpj());
                    if (dbJur.pesquisaJuridicaPorDoc(cnpj).isEmpty()) {
                        errors.add(" Documento não Existe no Sistema! " + objeto_arquivo.getCnpj());
                    }

                    for (LinhaSegmento linha_segmento : objeto_arquivo.getLinhaSegmento()) {

                        movimento = db.pesquisaMovPorNumDocumentoListBaixadoArr(linha_segmento.getNossoNumero(), this.getContaCobranca().getId());

                        if (movimento.isEmpty()) {
                            movimento = db.pesquisaMovPorNumDocumentoList(linha_segmento.getNossoNumero(), DataHoje.converte(DataHoje.colocarBarras(linha_segmento.getDataVencimento())), this.getContaCobranca().getId());

                            if (!movimento.isEmpty()) {
                                if (movimento.size() == 1) {
                                    String retorno_continua = new ContinuaBaixa().arr(movimento.get(0), linha_segmento.getStatusRetorno(), objeto_arquivo.getRetorno());
                                    if (!retorno_continua.isEmpty()) {
                                        lista_detalhe.add(new ObjetoDetalheRetorno(movimento.get(0), 8, retorno_continua));
                                        continue;
                                    }

                                    movimento.get(0).setValorBaixa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorPago())), 100));
                                    movimento.get(0).setTaxa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorTaxa())), 100));

                                    double valor_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorCredito())), 100);
                                    double valor_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorPago())), 100);

                                    GerarMovimento.baixarMovimento(movimento.get(0), usuario, DataHoje.colocarBarras(linha_segmento.getDataPagamento()), valor_pago, valor_liquido, DataHoje.converte(DataHoje.colocarBarras(linha_segmento.getDataCredito())), "", Integer.valueOf(objeto_arquivo.getSequencialArquivo()));

                                    lista_detalhe.add(new ObjetoDetalheRetorno(movimento.get(0), 8, "Boleto Baixado"));
                                } else {
                                    String detalhe = "";

                                    String retorno_continua = new ContinuaBaixa().arr(movimento.get(0), linha_segmento.getStatusRetorno(), objeto_arquivo.getRetorno());

                                    if (!retorno_continua.isEmpty()) {
                                        detalhe = retorno_continua;
                                    }

                                    String xt = "";

                                    for (Movimento m : movimento) {
                                        if (xt.isEmpty()) {
                                            xt = "<b>||BOLETO - DUPLICADO||</b>";
                                        }
                                        xt += "</br></br>";
                                        xt += "ID Movimento: " + m.getId() + " </br> ";
                                        xt += "Data de Vencimento: " + m.getVencimento();
                                    }

                                    if (!detalhe.isEmpty()) {
                                        detalhe += "</br></br>" + xt;
                                    } else {
                                        detalhe = xt;
                                    }

                                    lista_detalhe.add(new ObjetoDetalheRetorno(movimento.get(0), 7, detalhe));
                                }
                            } else {
                                String xt = "Boleto não Encontrado - " + linha_segmento.getNossoNumero()
                                        + " - Data de Vencimento: " + DataHoje.colocarBarras(linha_segmento.getDataVencimento())
                                        + " - Data de Pagamento: " + DataHoje.colocarBarras(linha_segmento.getDataPagamento())
                                        + " - Valor Pago: " + Moeda.converteR$Double(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorPago())), 100));
                                lista_detalhe.add(new ObjetoDetalheRetorno(null, 7, xt));
                            }
                        } else {
                            lista_detalhe.add(new ObjetoDetalheRetorno(movimento.get(0), 2, "Boleto Já Baixado"));
                        }
                        movimento.clear();
                    }
                }
            }

        }

        GenericaSessao.put("detalhes_retorno_banco", lista_detalhe);

        // TERMINAR O CASO DE PENDENTES OU NÃO --------------------------------
        if (listFls != null) {
            if (moverArquivo) {
                for (int i = 0; i < listFls.length; i++) {
                    flDes = new File(caminho + "/pendentes/" + listFls[i].getName());

                    fl = new File(destino + "/" + listFls[i].getName());
                    if (fl.exists()) {
                        fl.delete();
                    }

                    if (!flDes.renameTo(fl)) {
                        result = " Erro ao mover arquivo!";
                    }
                }
            }
        }
        return result;
    }

    public String validaCNPJ(String cnpj, String cnpj_sindicato) {
        if (cnpj.trim().isEmpty()) {
            return "";
        }

        try {
            if (Integer.valueOf(cnpj) == 0) {
                return "";
            } else {
                return "nosso_numero";
            }
        } catch (NumberFormatException e) {
            if (ValidaDocumentos.isValidoCNPJ(cnpj)) {
                if (cnpj.equals(cnpj_sindicato)) {
                    return "";
                }

                return "cnpj";
            }

            String cnpj_sem_dv = cnpj.substring(cnpj.length() - 12, cnpj.length());
            String digito = ValidaDocumentos.retonarDigitoCNPJ(cnpj_sem_dv);

            if (ValidaDocumentos.isValidoCNPJ(cnpj_sem_dv + digito)) {
                return "cnpj_sem_dv";
            }

            if (ValidaDocumentos.isValidoCPF(cnpj.substring(4))) {
                return "cpf";
            }
        }
        return "";
    }

    protected String baixarArquivoPadrao(List<ObjetoRetorno> listaParametros, Usuario usuario) {
        return "Processo Concluido []";
    }

    protected String baixarArquivoSocial(List<ObjetoRetorno> lista_objeto_retorno, String caminho, Usuario usuario) {

        String result = "";
        String destino = caminho + "/" + DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1] + "-" + DataHoje.ArrayDataHoje()[0];

        boolean moverArquivo = true;
        List<String> errors = new ArrayList();

        MovimentoDao db = new MovimentoDao();
        JuridicaDao dbJur = new JuridicaDao();
        File fl = new File(caminho + "/pendentes/");
        File listFls[] = fl.listFiles();
        File flDes = new File(destino); // 0 DIA, 1 MES, 2 ANO
        flDes.mkdir();

        List<Object[]> lista_logs = new ArrayList();

        String data_fechamento_diario = DataHoje.converteData(new FechamentoDiarioDao().ultimaDataContaSaldo());

        for (ObjetoRetorno ob : lista_objeto_retorno) {

            if (!ob.getErro().isEmpty()) {
                return ob.getErro();
            }

//            for (ObjetoArquivo oa : ob.getListaObjetoArquivo()) {
//
//                for (LinhaSegmento ls : oa.getLinhaSegmento()) {
//                    String data_pag = DataHoje.colocarBarras(ls.getDataPagamento());
//                    if (DataHoje.maiorData(data_pag, data_fechamento_diario) || data_pag.equals(data_fechamento_diario)) {
//                        return "Fechamento diário já concluído hoje, ARQUIVO NÃO PODERÁ SER BAIXADO!";
//                    }
//                }
//
//            }
        }

        // LAYOUT 2 = SINDICAL
        if (this.getContaCobranca().getLayout().getId() != 2) {
            for (ObjetoRetorno objeto_retorno : lista_objeto_retorno) {

                for (ObjetoArquivo objeto_arquivo : objeto_retorno.getListaObjetoArquivo()) {

                    String cnpj = AnaliseString.mascaraCnpj(objeto_arquivo.getCnpj());

                    if (dbJur.pesquisaJuridicaPorDoc(cnpj).isEmpty()) {
                        errors.add(" Documento não Existe no Sistema! " + objeto_arquivo.getCnpj());
                    }

                    for (LinhaSegmento linha_segmento : objeto_arquivo.getLinhaSegmento()) {
                        List<Movimento> lista_movimento = db.pesquisaMovPorNumDocumentoListBaixadoAss(linha_segmento.getNossoNumero(), this.getContaCobranca().getId());
                        if (lista_movimento.isEmpty()) {
                            lista_movimento = db.pesquisaMovPorNumDocumentoListAss(linha_segmento.getNossoNumero(), this.getContaCobranca().getId());
                            if (!lista_movimento.isEmpty()) {
                                //movimento.get(0).setValorBaixa(Moeda.divisaoValores(Moeda.substituiVirgulaDouble(Moeda.converteR$(listaParametros.get(u).getValorPago())), 100));
                                //movimento.get(0).setTaxa(Moeda.divisaoValores(Moeda.substituiVirgulaDouble(Moeda.converteR$(listaParametros.get(u).getValorTaxa())), 100));

                                Boolean continua = true;
                                for (Movimento mx : lista_movimento) {
                                    if (mx.getBaixa() != null) {
                                        Object[] log = new Object[3];

                                        log[0] = 8;
                                        log[1] = mx.getDocumento();
                                        log[2] = "Boleto com Movimento Pago não pode ser baixado - "
                                                + " - Data de Vencimento: " + mx.getVencimento()
                                                + " - Data de Pagamento: " + mx.getBaixa().getBaixa()
                                                + " - Valor Pago: " + mx.getValorBaixaString()
                                                + " - Usuário: " + mx.getBaixa().getUsuario().getPessoa().getNome();

                                        lista_logs.add(log);
                                        continua = false;

                                        break;
                                    }
                                }

                                if (!continua) {
                                    continue;
                                }

                                String retorno_continua = new ContinuaBaixa().soc(lista_movimento.get(0).getBoleto(), linha_segmento.getStatusRetorno(), objeto_arquivo.getRetorno());

                                if (!retorno_continua.isEmpty()) {
                                    Object[] log = new Object[3];

                                    log[0] = 8;
                                    log[1] = linha_segmento.getNossoNumero();
                                    log[2] = retorno_continua + " - " + linha_segmento.getNossoNumero()
                                            + " - Data de Vencimento: " + DataHoje.colocarBarras(linha_segmento.getDataVencimento())
                                            + " - Data de Pagamento: " + DataHoje.colocarBarras(linha_segmento.getDataPagamento())
                                            + " - Valor Pago: " + Moeda.converteR$Double(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorCredito())), 100));
                                    lista_logs.add(log);
                                    continue;
                                }

                                // logs de mensagens ---
                                // 0 - ERRO AO INSERIR BAIXA - [1] obj Baixa
                                // 1 - ERRO AO INSERIR FORMA DE PAGAMENTO - [1] obj FormaPagamento
                                // 2 - ERRO AO ALTERAR MOVIMENTO COM A BAIXA - [1] obj Movimento
                                // 3 - ERRO AO ALTERAR MOVIMENTO COM DESCONTO E VALOR BAIXA - [1] obj Movimento
                                // 4 - ERRO AO ALTERAR MOVIMENTO COM CORREÇÃO E VALOR BAIXA - [1] obj Movimento
                                // 5 - BAIXA CONCLUÍDA COM SUCESSO
                                // 6 - VALOR DA BAIXA MENOR - [1] obj Lista Movimento
                                // 7 - VALOR DA BAIXA MAIOR - [1] obj Lista Movimento
                                // 8 - BOLETO NÃO ENCONTRADO - [1] string Número do Boleto
                                // 9 - ERRO AO ALTERAR MOVIMENTO COM VALOR BAIXA CORRETO - [1] obj Movimento
                                Double valor_credito = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorCredito())), 100);
                                Double valor_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorPago())), 100);

                                Object[] log = GerarMovimento.baixarMovimentoSocial(
                                        lista_movimento, // lista de movimentos
                                        usuario, // usuario que esta baixando
                                        DataHoje.colocarBarras(linha_segmento.getDataPagamento()), // data do pagamento
                                        valor_pago, // valor pago ( total pago )
                                        valor_credito, // valor liquido ( total que entrou na conta ) - taxas
                                        DataHoje.colocarBarras(linha_segmento.getDataCredito()), // data do credito
                                        Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorTaxa())), 100), // valor taxa
                                        Integer.valueOf(objeto_arquivo.getSequencialArquivo())
                                );

                                lista_logs.add(log);
                            } else {
                                Object[] log = new Object[3];

                                log[0] = 8;
                                log[1] = linha_segmento.getNossoNumero();
                                log[2] = "Boleto não Encontrado - " + linha_segmento.getNossoNumero()
                                        + " - Data de Vencimento: " + DataHoje.colocarBarras(linha_segmento.getDataVencimento())
                                        + " - Data de Pagamento: " + DataHoje.colocarBarras(linha_segmento.getDataPagamento())
                                        + " - Valor Pago: " + Moeda.converteR$Double(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorCredito())), 100));
                                lista_logs.add(log);
                            }
                        } else {
                            Object[] log = new Object[3];

                            log[0] = 5;
                            log[1] = linha_segmento.getNossoNumero();
                            log[2] = "Boleto já Baixado - " + linha_segmento.getNossoNumero()
                                    + " - Data de Importação: " + lista_movimento.get(0).getBaixa().getImportacao()
                                    + " - Data de Pagamento: " + DataHoje.colocarBarras(linha_segmento.getDataPagamento())
                                    + " - Valor Pago: " + Moeda.converteR$Double(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(linha_segmento.getValorCredito())), 100));
                            lista_logs.add(log);
                        }

                        lista_movimento.clear();
                    }
                }
            }
        }

        GenericaSessao.put("logsRetornoSocial", lista_logs);

        if (listFls != null) {
            if (moverArquivo) {
                for (File listFl : listFls) {
                    flDes = new File(caminho + "/pendentes/" + listFl.getName());
                    fl = new File(destino + "/" + listFl.getName());
                    if (fl.exists()) {
                        fl.delete();
                    }
                    if (!flDes.renameTo(fl)) {
                        result = " Erro ao mover arquivo!";
                    }
                }
            }
        }
        return result;
    }

    public ContaCobranca getContaCobranca() {
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

}
