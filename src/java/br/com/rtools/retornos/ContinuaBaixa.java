/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.retornos;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.RetornoBanco;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;

/**
 *
 * @author Claudemir Windows
 */
public class ContinuaBaixa {

    public String arr(Movimento m, StatusRetorno sr, Retorno retorno) {
        if (sr != null) {
            Boleto bol = m.getBoleto();

            if (bol == null) {
                return "BOLETO NÃO PODE SER ENCONTRADO!";
            }

            bol.setStatusRetorno(sr);
            bol.setDtStatusRetorno(DataHoje.dataHoje());

            Dao dao = new Dao();

            RetornoBanco rb = new RetornoBanco(-1, retorno, bol.getBoletoComposto(), sr, m);

            dao.save(rb, true);

            switch (sr.getId()) {
                // BOLETO REJEITADO
                case 1:
                    dao.update(bol, true);
                    return "Boleto foi Rejeitado";
                // BOLETO REGISTRADO
                case 2:
                    bol.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    dao.update(bol, true);
                    return "Boleto está Registrado";
                // BOLETO LIQUIDADO
                case 3:
                    bol.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    dao.update(bol, true);
                    return "";
                // BOLETO EXCLUIDO
                case 6:
                    new Dao().update(bol, true);
//                    QUANDO O BOLETO VIER BAIXADO NO BANCO TMB EXCLUIR DO SISTEMA - ( ROGÉRIO DISSE QUE NÃO PODE )
                    if (m.getBaixa() != null) {
                        return "Boleto já quitado não pode ser excluído!";
                    }

                    Movimento mov_antigo = (Movimento) dao.find(m);
                    MovimentoDao dbm = new MovimentoDao();

                    m.setVencimento(mov_antigo.getVencimento());
                    int id_boleto = dbm.inserirBoletoNativo(bol.getContaCobranca().getId());

                    dbm.insertMovimentoBoleto(bol.getContaCobranca().getId(), bol.getBoletoComposto());

                    String nr_ctr = bol.getNrCtrBoleto();

                    bol.setDtRegistroBaixa(DataHoje.dataHoje());
                    bol.setAtivo(false);
                    bol.setNrCtrBoleto("");
                    dao.update(bol, true);

                    dao.openTransaction();
                    if (id_boleto != -1) {
                        Boleto bol_novo = (Boleto) dao.find(new Boleto(), id_boleto);

                        bol_novo.setNrCtrBoleto(String.valueOf(m.getId()));
                        bol_novo.setVencimento(mov_antigo.getVencimento());
                        bol_novo.setVencimentoOriginal(mov_antigo.getVencimentoOriginal());

                        m.setDocumento(bol_novo.getBoletoComposto());
                        m.setNrCtrBoleto(bol_novo.getNrCtrBoleto());

                        if (!dao.update(m)) {
                            dao.rollback();
                            voltarBoleto(bol, nr_ctr);
                            return "Erro ao Atualizar Movimento ID " + m.getId();

                        }

                        if (!dao.update(bol_novo)) {
                            dao.rollback();
                            voltarBoleto(bol, nr_ctr);
                            return "Erro ao Atualizar Boleto ID " + bol_novo.getId();
                        }

                        dao.commit();
                        bol = bol_novo;
                    } else {
                        dao.rollback();
                        voltarBoleto(bol, nr_ctr);
                        return "Erro ao Gerar Novo Boleto";
                    }

//
//                    if (m.getAcordo() != null) {
//                        return "Boleto do tipo acordo não pode ser excluído!";
//                    }
//
//                    String motivo_exclusao = "Boleto excluído pelo arquivo retorno";
//
//                    String ret = GerarMovimento.inativarUmMovimento(m, motivo_exclusao);
//
//                    if (!ret.isEmpty()) {
//                        return "Não foi possível excluir, " + ret;
//                    }
//                    return "Boleto Excluído";
                    return "Boleto Baixado(excluído) pelo Banco";
                default:
                    return "Status do Retorno não encontrado, verificar manual";
            }
        }
        return "Status do Retorno não encontrado, verificar manual";
    }

    public String soc(Boleto b, StatusRetorno sr, Retorno retorno) {
        if (sr != null) {

            b.setStatusRetorno(sr);
            b.setDtStatusRetorno(DataHoje.dataHoje());

            RetornoBanco rb = new RetornoBanco(-1, retorno, b.getBoletoComposto(), sr, null);

            Dao dao = new Dao();

            dao.save(rb, true);

            switch (sr.getId()) {
                // BOLETO REJEITADO
                case 1:
                    dao.update(b, true);
                    return "Boleto foi Rejeitado";
                // BOLETO REGISTRADO
                case 2:
                    b.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    dao.update(b, true);
                    return "Boleto está Registrado";
                // BOLETO LIQUIDADO
                case 3:
                    b.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    dao.update(b, true);
                    return "";
                // BOLETO EXCLUIDO
                case 6:
                    dao.update(b, true);
//                    QUANDO O BOLETO VIER BAIXADO NO BANCO TMB EXCLUIR DO SISTEMA - ( ROGÉRIO DISSE QUE NÃO PODE )
//                    List<Movimento> lm = b.getListaMovimento();
//
//                    if (lm.isEmpty()) {
//                        return "Lista de Movimentos não encontrado";
//                    }
//
//                    for (Movimento m : lm) {
//                        if (m.getBaixa() != null) {
//                            return "Boleto já quitado não pode ser excluído!";
//                        }
//
//                        if (m.getBaixa() != null && m.getBaixa().getFechamentoCaixa() != null) {
//                            return "Boleto com caixa fechado não pode ser excluído!";
//                        }
//
//                        if (m.getAcordo() != null) {
//                            return "Boleto do tipo acordo não pode ser excluído!";
//                        }
//                    }
//
//                    String motivo_exclusao = "Boleto excluído pelo arquivo retorno";
//
//                    Dao dao = new Dao();
//                    dao.openTransaction();
//
//                    String ret = GerarMovimento.inativarArrayMovimento(lm, motivo_exclusao, dao);
//
//                    if (!ret.isEmpty()) {
//                        dao.rollback();
//                        return "Não foi possível excluir, " + ret;
//                    }
//                    
//                    dao.commit();
//                    
//                    return "Boleto Excluído";
                    return "Boleto Baixado(excluído) pelo Banco";
                default:
                    return "Status do Retorno não encontrado, verificar manual";
            }
        }

        return "Status do Retorno não encontrado, verificar manual";
    }

    public void voltarBoleto(Boleto boleto, String nr_ctr) {
        boleto.setDtRegistroBaixa(null);
        boleto.setAtivo(true);
        boleto.setNrCtrBoleto(nr_ctr);
        new Dao().update(boleto, true);
    }

}
