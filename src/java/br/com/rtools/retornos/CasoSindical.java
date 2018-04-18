/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.DocumentoInvalido;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.DocumentoInvalidoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import static br.com.rtools.utilitarios.DataHoje.referencia;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import java.util.List;

/**
 *
 * @author Claudemir Windows
 */
public class CasoSindical {

    public RetornoCasoSindical CASO_1(Integer id_conta_cobranca, String nosso_numero, String data_pagamento, String valor_pago, TipoServico tipo_servico, String referencia, String data_vencimento, String taxa, String valor_credito, Usuario usuario, String data_credito, Integer nr_sequencial_arquivo) {
        MovimentoDao db = new MovimentoDao();

        String numeroComposto
                = nosso_numero
                + data_pagamento
                + valor_pago.substring(5, valor_pago.length());

        List<Movimento> lm = db.pesquisaMovPorNumDocumentoListBaixadoArr(nosso_numero, id_conta_cobranca);

        if (!lm.isEmpty()) {
            // EXISTE O BOLETO  MAS CONTEM VALORES DIFERENTES
            Movimento mov2 = lm.get(0);

            Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);

            lm = db.pesquisaMovPorNumPessoaListBaixado(numeroComposto, id_conta_cobranca);

            if (lm.isEmpty()) {
                Movimento movi = new Movimento(
                        -1,
                        null,
                        servicos.getPlano5(),
                        mov2.getPessoa(),
                        servicos,
                        null,
                        tipo_servico,
                        null,
                        0,
                        referencia,
                        data_vencimento,
                        1,
                        true,
                        "E",
                        false,
                        mov2.getPessoa(),
                        mov2.getPessoa(),
                        numeroComposto,
                        "",
                        data_vencimento,
                        0, 0, 0, 0, 0,
                        Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(taxa)), 100),
                        Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100),
                        (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2),
                        0, null);

                StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi);

                if (sr.getStatus()) {
                    double vl_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_credito)), 100);
                    double vl_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100);

                    GerarMovimento.baixarMovimento(
                            movi,
                            usuario,
                            DataHoje.colocarBarras(data_pagamento),
                            vl_pago,
                            vl_liquido,
                            DataHoje.converte(DataHoje.colocarBarras(data_credito)),
                            numeroComposto,
                            nr_sequencial_arquivo
                    );
                    return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(movi, 1, "Boleto Baixado pelo Número"));

                }
            } else if (lm.get(0).getBaixa().getSequenciaBaixa() == 0) {
                lm.get(0).getBaixa().setSequenciaBaixa(nr_sequencial_arquivo);
                Dao dao = new Dao();
                dao.openTransaction();
                if (dao.update(lm.get(0).getBaixa())) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
            }

            return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 2, "Boleto já Baixado"));
        }

        return new RetornoCasoSindical(false, null);
    }

    public RetornoCasoSindical CASO_2(Integer id_conta_cobranca, String nosso_numero, StatusRetorno sr, Retorno retorno, String valor_pago, String taxa, String valor_credito, Usuario usuario, String data_pagamento, String data_credito, Integer nr_sequencial_arquivo) {
        String numeroComposto
                = nosso_numero
                + data_pagamento
                + valor_pago.substring(5, valor_pago.length());

        MovimentoDao db = new MovimentoDao();

        List<Movimento> lm = db.pesquisaMovPorNumDocumentoListSindical(nosso_numero, id_conta_cobranca);

        if (!lm.isEmpty()) {
            // ENCONTROU O BOLETO PRA BAIXAR

            String retorno_continua = new ContinuaBaixa().arr(lm.get(0), sr, retorno);
            if (!retorno_continua.isEmpty()) {
                return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 3, retorno_continua));
            }

            lm.get(0).setValorBaixa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100));
            lm.get(0).setTaxa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(taxa)), 100));

            double vl_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_credito)), 100);
            double vl_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100);

            GerarMovimento.baixarMovimento(
                    lm.get(0),
                    usuario,
                    DataHoje.colocarBarras(data_pagamento),
                    vl_pago,
                    vl_liquido,
                    DataHoje.converte(DataHoje.colocarBarras(data_credito)),
                    numeroComposto,
                    nr_sequencial_arquivo
            );

            return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 3, "Boleto Baixado pelo Número"));
        }

        return new RetornoCasoSindical(false, null);
    }

    public RetornoCasoSindical CASO_3(Integer id_conta_cobranca, String cnpj_pagador, String data_pagamento, String valor_pago) {
        MovimentoDao db = new MovimentoDao();

        // obs. COLOCO "000" DEVIDO AO CASO ( 3 e 4 ) DA QUERY DO ROGÉRIO EXIGIR 16 CARACTERES NO CNPJ PARA PESQUISAR
        String cnpjComposto
                = "000" + cnpj_pagador
                + data_pagamento
                + valor_pago.substring(5, valor_pago.length());

        List<Movimento> lm = db.pesquisaMovPorNumPessoaListBaixado(cnpjComposto, id_conta_cobranca);

        if (!lm.isEmpty()) {
            // EXISTE O BOLETO PELO CNPJ DA EMPRESA + DATA DE PAGAMENTO BAIXADO --------------
            return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 4, "Boleto já Baixado"));
        }

        return new RetornoCasoSindical(false, null);
    }

    public RetornoCasoSindical CASO_4(Integer id_conta_cobranca, String cnpj_pagador, String data_pagamento, String valor_pago, String referencia, TipoServico tipo_servico, StatusRetorno status_retorno, Retorno retorno, String taxa, String valor_credito, Usuario usuario, String data_credito, Integer nr_sequencial_arquivo, String data_vencimento) {
        MovimentoDao db = new MovimentoDao();
        JuridicaDao jdao = new JuridicaDao();
        
        String cnpjComposto
                = "000" + cnpj_pagador
                + data_pagamento
                + valor_pago.substring(5, valor_pago.length());

        // obs. query modelo antigo, futuramente pode apagar quando o layout das sindicais forem todos novos ( deixando apenas a de baixo )
        List<Juridica> listJuridica = jdao.pesquisaJuridicaParaRetorno(cnpj_pagador);

        // obs. query nova para o layout sindical novo
        if (listJuridica.isEmpty()) {
            listJuridica = jdao.pesquisaJuridicaParaRetornoComMascara(cnpj_pagador);
        }

        if (!listJuridica.isEmpty()) {
            List<Movimento> lm = db.pesquisaMovimentoChaveValor(listJuridica.get(0).getPessoa().getId(), referencia, id_conta_cobranca, tipo_servico.getId());

            if (!lm.isEmpty()) {
                String retorno_continua = new ContinuaBaixa().arr(lm.get(0), status_retorno, retorno);
                if (!retorno_continua.isEmpty()) {
                    return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 5, retorno_continua));
                }

                lm.get(0).setValorBaixa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100));
                lm.get(0).setTaxa(Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(taxa)), 100));

                double vl_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_credito)), 100);
                double vl_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100);

                GerarMovimento.baixarMovimento(
                        lm.get(0),
                        usuario,
                        DataHoje.colocarBarras(data_pagamento),
                        vl_pago,
                        vl_liquido,
                        DataHoje.converte(DataHoje.colocarBarras(data_credito)),
                        cnpjComposto,
                        nr_sequencial_arquivo
                );

                return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(lm.get(0), 4, "Boleto Baixado pelo CNPJ"));
            }

            Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);
            
            Movimento movi = new Movimento(
                    -1,
                    null,
                    servicos.getPlano5(),
                    listJuridica.get(0).getPessoa(),
                    servicos,
                    null,
                    tipo_servico,
                    null,
                    0,
                    referencia,
                    data_vencimento,
                    1,
                    true,
                    "E",
                    false,
                    listJuridica.get(0).getPessoa(),
                    listJuridica.get(0).getPessoa(),
                    cnpj_pagador + data_pagamento + valor_pago.substring(5, valor_pago.length()),
                    "",
                    data_vencimento,
                    0, 0, 0, 0, 0,
                    Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(taxa)), 100),
                    Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100),
                    (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2),
                    0,
                    null
            );

            StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi);

            if (sr.getStatus()) {
                String retorno_continua = new ContinuaBaixa().arr(movi, status_retorno, retorno);
                if (!retorno_continua.isEmpty()) {
                    return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(movi, 6, retorno_continua));
                }

                double vl_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_credito)), 100);
                double vl_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100);

                GerarMovimento.baixarMovimento(
                        movi,
                        usuario,
                        DataHoje.colocarBarras(data_pagamento),
                        vl_pago,
                        vl_liquido,
                        DataHoje.converte(DataHoje.colocarBarras(data_credito)),
                        cnpjComposto,
                        nr_sequencial_arquivo
                );

                return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(movi, 6, "Boleto Baixado pelo CNPJ"));
            }
        } else {
            Dao dao = new Dao();
            Servicos servicos = (Servicos) (new Dao()).find(new Servicos(), 1);
            Movimento movi = new Movimento(
                    -1,
                    null,
                    servicos.getPlano5(),
                    (Pessoa) dao.find(new Pessoa(), 0),
                    servicos,
                    null,
                    tipo_servico,
                    null,
                    0,
                    referencia,
                    data_vencimento,
                    1,
                    true,
                    "E",
                    false,
                    (Pessoa) dao.find(new Pessoa(), 0),
                    (Pessoa) dao.find(new Pessoa(), 0),
                    cnpj_pagador + data_pagamento + valor_pago.substring(5, valor_pago.length()),
                    "",
                    data_vencimento,
                    0, 0, 0, 0, 0,
                    Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(taxa)), 100),
                    Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100),
                    (FTipoDocumento) (new Dao()).find(new FTipoDocumento(), 2),
                    0,
                    null
            );

            StatusRetornoMensagem sr = GerarMovimento.salvarUmMovimentoBaixa(new Lote(), movi);

            if (sr.getStatus()) {
                String retorno_continua = new ContinuaBaixa().arr(movi, status_retorno, retorno);
                if (!retorno_continua.isEmpty()) {
                    return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(movi, 7, retorno_continua));
                }

                double vl_liquido = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_credito)), 100);
                double vl_pago = Moeda.divisao(Moeda.substituiVirgulaDouble(Moeda.converteR$(valor_pago)), 100);

                DocumentoInvalidoDao dbDocInv = new DocumentoInvalidoDao();
                List<DocumentoInvalido> listaDI = dbDocInv.pesquisaNumeroBoleto(cnpj_pagador);

                if (listaDI.isEmpty()) {
                    DocumentoInvalido di = new DocumentoInvalido(-1, cnpj_pagador, false, DataHoje.data());
                    dao.save(di, true);
                }

                GerarMovimento.baixarMovimento(
                        movi,
                        usuario,
                        DataHoje.colocarBarras(data_pagamento),
                        vl_pago,
                        vl_liquido,
                        DataHoje.converte(DataHoje.colocarBarras(data_credito)),
                        cnpjComposto,
                        nr_sequencial_arquivo
                );

                return new RetornoCasoSindical(true, new ObjetoDetalheRetorno(movi, 7, "Boleto não encontrado criado para pessoa (zero) - " + cnpj_pagador));
            }
        }

        return new RetornoCasoSindical(false, null);

    }
}
