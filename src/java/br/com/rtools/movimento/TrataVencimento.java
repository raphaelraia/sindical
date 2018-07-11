/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.movimento;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;
import java.util.List;

/**
 *
 * @author rtools2
 */
public class TrataVencimento {

    public static TrataVencimentoRetorno boletoExiste(Boleto boleto) {
        // TIPO COBRANÇA NÃO REGISTRADA 
        if (boleto.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
            // VENCIDO
            if (DataHoje.menorData(boleto.getDtVencimento(), DataHoje.dataHoje())) {

                MovimentoDao db = new MovimentoDao();

                List<Movimento> lm = boleto.getListaMovimento();

                Double valor = new Double(0);
                Double juros = new Double(0);
                Double multa = new Double(0);
                Double correcao = new Double(0);

                for (Movimento m : lm) {

                    valor = Moeda.soma(m.getValor(), valor);
                    juros = Moeda.soma(db.funcaoJurosAss(m.getId()), juros);
                    multa = Moeda.soma(db.funcaoMultaAss(m.getId()), multa);
                    correcao = Moeda.soma(db.funcaoCorrecaoAss(m.getId()), correcao);

                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), valor);

                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), true, true, false);

            } else {
                // NÃO VENCIDO
                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), new Double(0), new Double(0), new Double(0), boleto.getValor(), boleto.getDtProcessamento(), false, false, false);
            }
        }

        // TIPO COBRANÇA REGISTRADA 
        // MOVIMENTO VENCIDO
        if (DataHoje.menorData(boleto.getDtVencimento(), DataHoje.dataHoje())) {
            // REGISTRADO
            if (boleto.getStatusRetorno() != null && boleto.getStatusRetorno().getId() == 2) {
                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), new Double(0), new Double(0), new Double(0), boleto.getValor(), boleto.getDtProcessamento(), false, true, true);
            } else {
                // NÃO REGISTRADO

                MovimentoDao db = new MovimentoDao();

                List<Movimento> lm = boleto.getListaMovimento();

                Double valor = new Double(0);
                Double juros = new Double(0);
                Double multa = new Double(0);
                Double correcao = new Double(0);

                for (Movimento m : lm) {

                    valor = Moeda.soma(m.getValor(), valor);
                    juros = Moeda.soma(db.funcaoJurosAss(m.getId()), juros);
                    multa = Moeda.soma(db.funcaoMultaAss(m.getId()), multa);
                    correcao = Moeda.soma(db.funcaoCorrecaoAss(m.getId()), correcao);

                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), valor);

                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), true, true, false);

            }
        } else {
            // NÃO VENCIDO
            if (boleto.getStatusRetorno() != null && boleto.getStatusRetorno().getId() == 2) {
                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), new Double(0), new Double(0), new Double(0), boleto.getValor(), boleto.getDtProcessamento(), false, false, true);
            } else {
                // NÃO REGISTRADO

                MovimentoDao db = new MovimentoDao();

                List<Movimento> lm = boleto.getListaMovimento();

                Double valor = new Double(0);
                Double juros = new Double(0);
                Double multa = new Double(0);
                Double correcao = new Double(0);

                for (Movimento m : lm) {

                    valor = Moeda.soma(m.getValor(), valor);
                    juros = Moeda.soma(db.funcaoJurosAss(m.getId()), juros);
                    multa = Moeda.soma(db.funcaoMultaAss(m.getId()), multa);
                    correcao = Moeda.soma(db.funcaoCorrecaoAss(m.getId()), correcao);

                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), valor);

                return new TrataVencimentoRetorno(boleto, boleto.getDtVencimento(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), false, false, false);

            }
        }
    }

    public static TrataVencimentoRetorno movimentoExiste(Movimento movimento, Juridica juridica, String referencia, Date vencimento) {
        Boleto b = movimento.getBoleto();

        // TIPO COBRANÇA NÃO REGISTRADA 
        if (b.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
            // VENCIDO
            if (DataHoje.menorData(movimento.getDtVencimento(), DataHoje.dataHoje())) {
                MovimentoDao db = new MovimentoDao();

                Double juros;
                Double multa;
                Double correcao;

                // SE NÃO FOR ACORDO
                if (movimento.getTipoServico().getId() != 4) {
                    juros = db.funcaoJuros(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    multa = db.funcaoMulta(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    correcao = db.funcaoCorrecao(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                } else {
                    juros = db.funcaoJurosAcordo(movimento.getId());
                    multa = db.funcaoMultaAcordo(movimento.getId());
                    correcao = db.funcaoCorrecaoAcordo(movimento.getId());
                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), movimento.getValor());

                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), vencimento, movimento.getValor(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), true, true, false);
            } else {
                // NÃO VENCIDO
                if (movimento.getValor() > 0 && b.getValor() == 0) {
                    b.setValor(movimento.getValor());
                }
                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), vencimento, movimento.getValor(), new Double(0), new Double(0), new Double(0), b.getValor(), b.getDtProcessamento(), false, false, false);
            }
        }

        // TIPO DE COBRANCA REGISTRADA
        // MOVIMENTO VENCIDO
        if (DataHoje.menorData(movimento.getDtVencimento(), DataHoje.dataHoje())) {
            // REGISTRADO
            if (b.getStatusRetorno() != null && b.getStatusRetorno().getId() == 2) {
                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), b.getDtVencimento(), movimento.getValor(), new Double(0), new Double(0), new Double(0), b.getValor(), b.getDtProcessamento(), false, true, true);
            } else {
                // NÃO REGISTRADO

                MovimentoDao db = new MovimentoDao();

                Double juros;
                Double multa;
                Double correcao;

                // SE NÃO FOR ACORDO
                if (movimento.getTipoServico().getId() != 4) {
                    juros = db.funcaoJuros(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    multa = db.funcaoMulta(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    correcao = db.funcaoCorrecao(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                } else {
                    juros = db.funcaoJurosAcordo(movimento.getId());
                    multa = db.funcaoMultaAcordo(movimento.getId());
                    correcao = db.funcaoCorrecaoAcordo(movimento.getId());
                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), movimento.getValor());

                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), vencimento, movimento.getValor(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), true, true, false);

            }
        } else {
            // NÃO VENCIDO
            if (b.getStatusRetorno() != null && b.getStatusRetorno().getId() == 2) {
                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), b.getDtVencimento(), movimento.getValor(), new Double(0), new Double(0), new Double(0), b.getValor(), b.getDtProcessamento(), false, false, true);
            } else {
                // NÃO REGISTRADO

                MovimentoDao db = new MovimentoDao();

                Double juros;
                Double multa;
                Double correcao;

                // SE NÃO FOR ACORDO
                if (movimento.getTipoServico().getId() != 4) {
                    juros = db.funcaoJuros(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    multa = db.funcaoMulta(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                    correcao = db.funcaoCorrecao(juridica.getPessoa().getId(), movimento.getServicos().getId(), movimento.getTipoServico().getId(), referencia);
                } else {
                    juros = db.funcaoJurosAcordo(movimento.getId());
                    multa = db.funcaoMultaAcordo(movimento.getId());
                    correcao = db.funcaoCorrecaoAcordo(movimento.getId());
                }

                Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), movimento.getValor());

                return new TrataVencimentoRetorno(b, movimento, juridica.getContabilidade(), movimento.getDtVencimento(), vencimento, movimento.getValor(), juros, multa, correcao, valor_calculado, DataHoje.dataHoje(), false, false, false);

            }
        }
    }

    public static TrataVencimentoRetorno movimentoNaoExiste(Servicos servico, TipoServico tipoServico, Juridica juridica, String referencia, Date vencimento, Double valor) {

        // MOVIMENTO VENCIDO
        // NÃO REGISTRADO POIS NÃO EXISTE
        Date vencimentoMensagem = vencimentoMensagem(juridica.getPessoa().getId(), servico.getId(), tipoServico.getId(), referencia);

        if (vencimentoMensagem == null) {
            vencimentoMensagem = vencimento;
        }

        if (DataHoje.menorData(vencimentoMensagem, DataHoje.dataHoje())) {

            MovimentoDao db = new MovimentoDao();

            Double juros = db.funcaoJuros(juridica.getPessoa().getId(), servico.getId(), tipoServico.getId(), referencia);
            Double multa = db.funcaoMulta(juridica.getPessoa().getId(), servico.getId(), tipoServico.getId(), referencia);
            Double correcao = db.funcaoCorrecao(juridica.getPessoa().getId(), servico.getId(), tipoServico.getId(), referencia);

            Double mvalor = valor;

            Double valor_calculado = Moeda.soma(Moeda.soma(Moeda.soma(juros, multa), correcao), mvalor);

            Movimento novo_movimento = novoMovimento(
                    servico,
                    tipoServico,
                    juridica.getPessoa(),
                    mvalor,
                    DataHoje.converteData(vencimentoMensagem),
                    referencia
            );

            return new TrataVencimentoRetorno(null, novo_movimento, juridica.getContabilidade(), vencimentoMensagem, vencimento, novo_movimento.getValor(), juros, multa, correcao, valor_calculado, null, true, true, false);

        } else {
            // NÃO VENCIDO
            Movimento novo_movimento = novoMovimento(
                    servico,
                    tipoServico,
                    juridica.getPessoa(),
                    valor,
                    DataHoje.converteData(vencimento),
                    referencia
            );

            return new TrataVencimentoRetorno(null, novo_movimento, juridica.getContabilidade(), vencimentoMensagem, vencimento, novo_movimento.getValor(), new Double(0), new Double(0), new Double(0), novo_movimento.getValor(), null, false, true, false);

        }
    }

    public static Date vencimentoMensagem(Integer id_pessoa, Integer id_servico, Integer id_tipo_servico, String referencia) {
        CnaeConvencaoDao dbco = new CnaeConvencaoDao();
        Convencao convencao = dbco.pesquisarCnaeConvencaoPorPessoa(id_pessoa);
        if (convencao == null) {
            return null;
        }

        if (id_tipo_servico != 4) {
            MensagemConvencaoDao dbm = new MensagemConvencaoDao();
            GrupoCidadesDao dbgc = new GrupoCidadesDao();

            MensagemConvencao mc = dbm.verificaMensagem(
                    convencao.getId(),
                    id_servico,
                    id_tipo_servico,
                    dbgc.grupoCidadesPorPessoa(id_pessoa, convencao.getId()).getId(),
                    referencia
            );

            if (mc == null) {
                return null;
            }

            return mc.getDtVencimento();
        }

        return null;
    }

    public static Movimento novoMovimento(Servicos servicos, TipoServico tipoServico, Pessoa pessoa, Double m_valor, String m_vencimento, String referencia) {
        return new Movimento(
                -1,
                null,
                servicos.getPlano5(),
                pessoa,
                servicos,
                null,
                tipoServico,
                null,
                m_valor,
                referencia,
                m_vencimento,
                1,
                true,
                "E",
                false,
                pessoa,
                pessoa,
                "",
                "",
                m_vencimento,
                0, 0, 0, 0, 0, 0, 0,
                (FTipoDocumento) new Dao().find(new FTipoDocumento(), 2),
                0,
                null
        );
    }

}
