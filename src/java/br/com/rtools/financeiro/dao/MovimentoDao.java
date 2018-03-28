/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Impressao;
import br.com.rtools.financeiro.ImpressaoWeb;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.MensagemCobranca;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoContaCobranca;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Debugs;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

/**
 *
 * @author Claudemir Rtools
 */
public class MovimentoDao extends DB {

    private Integer limit = null;

    public List<Movimento> findByEvt(Integer evt_id) {
        Lote l = new LoteDao().pesquisaLotePorEvt(evt_id);
        return findByLote(l.getId(), true);
    }

    public List<Movimento> findByLote(Integer lote_id) {
        return findByLote(lote_id, true);
    }

    public List<Movimento> findByLote(Integer lote_id, Boolean ativos) {
        try {
            Query query;
            if (ativos == null) {
                query = getEntityManager().createQuery(" SELECT M FROM Movimento AS M WHERE M.lote.id = :lote_id ORDER BY M.dtVencimento ASC, M.id ASC");
            } else if (ativos) {
                query = getEntityManager().createQuery(" SELECT M FROM Movimento AS M WHERE M.lote.id = :lote_id AND M.ativo = true ORDER BY M.dtVencimento ASC, M.id ASC");
                query.setParameter("lote_id", lote_id);
            } else {
                query = getEntityManager().createQuery(" SELECT M FROM Movimento AS M WHERE M.lote.id = :lote_id AND M.ativo = false ORDER BY M.dtVencimento ASC, M.id ASC");
                query.setParameter("lote_id", lote_id);
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Movimento findByNrCtrBoletoPessoa(String nr_ctr_boleto, Integer pessoa_id) {
        return findByNrCtrBoletoAll(nr_ctr_boleto, pessoa_id, null);
    }

    public Movimento findByNrCtrBoletoTitular(String nr_ctr_boleto, Integer titular_id) {
        return findByNrCtrBoletoAll(nr_ctr_boleto, null, titular_id);
    }

    public Movimento findByNrCtrBoletoAll(String nr_ctr_boleto, Integer pessoa_id, Integer titular_id) {
        try {
            Query query = null;
            if (pessoa_id != null) {
                query = getEntityManager().createQuery("SELECT M FROM Movimento AS M WHERE M.nrCtrBoleto = :nr_ctr_boleto AND M.pessoa.id = :pessoa_id AND M.ativo = true");
                query.setParameter("pessoa_id", pessoa_id);

            } else if (titular_id != null) {
                query = getEntityManager().createQuery("SELECT M FROM Movimento AS M WHERE M.nrCtrBoleto = :nr_ctr_boleto AND M.titular.id = :titular_id  AND M.ativo = true");
                query.setParameter("titular_id", titular_id);
            }
            query.setParameter("nr_ctr_boleto", nr_ctr_boleto);
            return (Movimento) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Movimento pesquisaCodigo(int id) {
        Movimento result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Movimento.pesquisaID");
            qry.setParameter("pid", id);
            result = (Movimento) qry.getSingleResult();
        } catch (EJBQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public MensagemCobranca pesquisaMensagemCobranca(int idMovimento) {
        MensagemCobranca result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select m "
                    + "  from MensagemCobranca m"
                    + " where m.movimento.id = :pid");
            qry.setParameter("pid", idMovimento);
            result = (MensagemCobranca) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public String pesquisaDescMensagem(int id_tipo_servico, int id_servicos, int id_convencao, int id_grupo_cidade) {
        String result = "";
        try {
            String textQry = "select ds_mensagem_compensacao "
                    + "  from arr_mensagem_convencao "
                    + " where id_tipo_servico = " + id_tipo_servico
                    + "   and id_servicos = " + id_servicos
                    + "   and id_convencao = " + id_convencao
                    + "   and id_grupo_cidade = " + id_grupo_cidade;
            Query qry = getEntityManager().createNativeQuery(textQry);
            result = qry.getResultList().get(0).toString();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public Boleto pesquisaBoletos(String nrCtrBoleto) {
        Boleto result = null;
        try {
//            Query qry = getEntityManager().createQuery(
//                    "select b "
//                    + "  from Boleto b"
//                    + " where b.nrCtrBoleto = '" + nrCtrBoleto+"'");
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT b.* "
                    + "  FROM fin_boleto b"
                    + " WHERE b.nr_ctr_boleto = '" + nrCtrBoleto + "'", Boleto.class
            );
            result = (Boleto) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<Movimento> listaMovimentoPorNrCtrBoleto(String nrCtrBoleto) {
        List<Movimento> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select m from Movimento m where m.nrCtrBoleto = '" + nrCtrBoleto + "' and m.ativo = true");
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public Movimento pesquisaPorId(int id) {
        Movimento result = null;
        try {
            Query qry = getEntityManager().createQuery("select m from Movimento m"
                    + "where m.id = :pid");
            qry.setParameter("pid", id);
            result = (Movimento) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaPartidas(int idLote) {
        try {
            Query qry = getEntityManager().createQuery("SELECT M FROM Movimento M WHERE M.lote.id = :pid AND M.baixa <> -1");
            qry.setParameter("pid", idLote);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List pesquisaLogWeb(int idMovimento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select i "
                    + "  from ImpressaoWeb i "
                    + " where i.movimento.id = :pid");
            qry.setParameter("pid", idMovimento);
            return (qry.getResultList());
        } catch (Exception e) {

            return null;
        }
    }

    public Movimento pesquisaContraPartida(int idLote) {
        Movimento result = null;
        try {
            Query qry = getEntityManager().createQuery("select m from Movimento m where m.lote.id = :pid and m.baixa = -1");
            qry.setParameter("pid", idLote);
            result = (Movimento) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Historico pesquisaHistorico(int id) {
        Historico result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select h "
                    + "  from Historico h"
                    + " where h.movimento.id =" + id);
            result = (Historico) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public boolean verificaMovimentoArrecadacao(int idPessoa, String referencia, int idServico, int idTipoServico) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select m"
                    + "  from Movimento m"
                    + " where m.pessoa.id  = :pessoa"
                    + "   and m.referencia = :ref"
                    + "   and m.servico.id = :serv"
                    + "   and m.tipoServico.id = :tserv");
            qry.setParameter("pessoa", idPessoa);
            qry.setParameter("ref", referencia);
            qry.setParameter("serv", idServico);
            qry.setParameter("tserv", idTipoServico);
            if (qry.getResultList().isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {

            return false;
        }
    }

    public List datasMovimento(int idServico, int idTipoServico, int idContaCobranca) {
        Date dataAnterior = DataHoje.converte(DataHoje.data().substring(0, 6) + Integer.toString(Integer.parseInt(DataHoje.data().substring(6, 10)) - 1));
        return datasMovimento(idServico, idTipoServico, idContaCobranca, DataHoje.data().substring(0, 6) + Integer.toString(Integer.parseInt(DataHoje.data().substring(6, 10)) - 1));
    }

    public List datasMovimento(Integer servico_id, Integer tipo_servico_id, Integer conta_cobranca_id, String data_anterior) {
        Date dataAnterior = DataHoje.converte(data_anterior);
        try {
            Query qry = getEntityManager().createQuery(
                    "select m.dtVencimento "
                    + "  from Movimento m, Boleto b "
                    + " where b.nrCtrBoleto = m.nrCtrBoleto "
                    + "   and m.dtVencimento > :data "
                    + "   and m.baixa is null"
                    + "   and m.servicos.id = " + servico_id
                    + "   and m.tipoServico.id = " + tipo_servico_id
                    + "   and b.contaCobranca.id = " + conta_cobranca_id
                    + " group by m.dtVencimento"
                    + " order by m.dtVencimento desc");
            qry.setParameter("data", dataAnterior);
            qry.setMaxResults(20);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List datasMovimento(List<ServicoContaCobranca> list) {
        if (list.isEmpty()) {
            return new ArrayList();
        }

        String dataAnterior = DataHoje.data().substring(0, 6) + Integer.toString(Integer.parseInt(DataHoje.data().substring(6, 10)) - 1);
        try {
            String queryString = ""
                    + "     SELECT M.dt_vencimento                              \n"
                    + "       FROM fin_movimento M                              \n"
                    + " INNER JOIN fin_boleto B ON B.nr_ctr_boleto = M.nr_ctr_boleto\n"
                    + "       WHERE M.dt_vencimento > '" + dataAnterior + "'        \n"
                    + "         AND M.id_baixa IS NULL                          \n"
                    + "         AND (";

            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    queryString += " OR ";
                }
                queryString += " ( M.id_servicos = " + list.get(i).getServicos().getId() + " AND M.id_tipo_servico = " + list.get(i).getTipoServico().getId() + " AND B.id_conta_cobranca = " + list.get(i).getContaCobranca().getId() + " )";
            }
            queryString += " "
                    + ")                                \n"
                    + " GROUP BY M.dt_vencimento        \n"
                    + " ORDER BY M.dt_vencimento DESC  ";
            Query qry = getEntityManager().createNativeQuery(queryString);
            qry.setMaxResults(20);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List datasMovimento() {
        Date dataAnterior = DataHoje.converte(DataHoje.data().substring(0, 6) + Integer.toString(Integer.parseInt(DataHoje.data().substring(6, 10)) - 1));
        try {
            Query qry = getEntityManager().createQuery(
                    "select m.dtVencimento   "
                    + "  from Movimento m                "
                    + " where m.dtVencimento > :data "
                    + "   and m.baixa is null"
                    + "   and m.tipoServico.id = " + 1
                    + " group by m.dtVencimento"
                    + " order by m.dtVencimento desc");
            qry.setParameter("data", dataAnterior);
            qry.setMaxResults(20);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List pesquisaPorVencimento(Date vencimento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select m    "
                    + "  from Movimento m                "
                    + " where m.dtVencimento = :data");
            qry.setParameter("data", vencimento);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }

    }

    public List pesquisaMovPorJuriData(int idJuri, Date vencimento) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select mov from Movimento mov, "
                    + "                Juridica jur, "
                    + "                Pessoa pes "
                    + "          where pes.id = jur.pessoa.id"
                    + "            and pes.id = mov.pessoa.id"
                    + "            and jur.id = :id_juri"
                    + "            and mov.dtVencimento = :data");
            qry.setParameter("id_juri", idJuri);
            qry.setParameter("data", vencimento);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List movimentosAbertoComVencimentoOriginal(int idPessoa) {
        String texto = "";
        try {
            Query qry;
            texto = " select mov,                                 "
                    + "        m.dtVencimento                       "
                    + "   from Movimento mov,                       "
                    + "        Juridica j,                          "
                    + "        Contribuintes c,                     "
                    + "        PessoaEndereco pe,                   "
                    + "        CnaeConvencao cc,                    "
                    + "        GrupoCidades gc,                     "
                    + "        MensagemConvencao m                  "
                    + "  where c.juridica.id = j.id                 "
                    + "    and cc.cnae.id = j.cnae.id               "
                    + "    and cc.convencao.id = m.convencao.id     "
                    + "    and pe.pessoa.id = j.pessoa.id             "
                    + "    and pe.tipoEndereco.id = 5                 "
                    + "    and pe.endereco.cidade.id = gc.cidade.id   "
                    + "    and gc.grupoCidade.id = m.grupoCidade.id   "
                    + "    and (m.referencia = mov.referencia "
                    + "       or m.referencia = \"\")"
                    + "    and m.tipoServico.id = mov.tipoServico.id  "
                    + "    and m.servicos.id = mov.servicos.id        "
                    + "    and j.pessoa.id = mov.pessoa.id            "
                    + "    and mov.loteBaixa is null "
                    + "    and mov.debitoCredito = \"D\""
                    + "    and mov.lote.rotina.id = 4 "
                    + "    and mov.pessoa.id = " + idPessoa
                    + "    order by mov.dtVencimento";

            qry = getEntityManager().createQuery(texto);
            return qry.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List movimentosAberto(int idPessoa, boolean sindical) {
        String texto = "";
        try {
            Query qry;
            String and_sindical = "";
            if (!sindical) {
                and_sindical = " and m.servicos.id <> 1";
            }

            texto = "select m    "
                    + "  from Movimento m "
                    + " where m.baixa is null "
                    + "   and m.ativo = true "
                    + "   and m.lote.rotina.id = 4 "
                    + "   and m.pessoa.id = " + idPessoa + and_sindical
                    + " order by m.dtVencimento";
            qry = getEntityManager().createQuery(texto);
            return qry.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List movimentosBaixados(int idLoteBaixa) {
        String texto = "";
        try {
            Query qry;
            texto = "select m    "
                    + "  from Movimento m "
                    + " where m.loteBaixa.id = " + idLoteBaixa
                    + "   and m.debitoCredito = \"D\""
                    + " and m.ativo = 1";
            qry = getEntityManager().createQuery(texto);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List listaMovimentosExtrato(String tipo, String faixa_data, String tipo_data, String data_inicial, String data_final, String referencia_inicial, String referencia_final, String boleto_inicial, String boleto_final, int id_servico, int id_tipo_servico, int id_pessoa, String ordenacao, boolean movimentoDaEmpresa, Integer filial_id, Integer id_status_retorno, String id_boleto_adicionado_remessa, Integer id_conta_cobranca) {
        String qry_data = "", qry_servico = "", qry_tipo_servico = "", qry_condicao = "", qry_boleto = "", qry_pessoa = "", qry_filial = "", ordem = "";

        String textQuery;
        switch (faixa_data) {
            case "recebimento":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and ba.dt_baixa = '" + data_inicial + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and ba.dt_baixa >= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            qry_data = "' and ba.dt_baixa <= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            qry_data = " and ba.dt_baixa >= '" + data_inicial + "' and ba.dt_baixa <= '" + data_final + "' \n ";
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "importacao":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and ba.dt_importacao = '" + data_inicial + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and ba.dt_importacao >= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            qry_data = "' and ba.dt_importacao <= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            qry_data = " and ba.dt_importacao >= '" + data_inicial + "' and ba.dt_importacao <= '" + data_final + "' \n ";
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "vencimento":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and m.dt_vencimento = '" + data_inicial + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and m.dt_vencimento >= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            qry_data = "' and m.dt_vencimento <= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            qry_data = " and m.dt_vencimento >= '" + data_inicial + "' and m.dt_vencimento <= '" + data_final + "' \n ";
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "lancamento":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and l.dt_lancamento = '" + data_inicial + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and l.dt_lancamento >= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            qry_data = "' and l.dt_lancamento <= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            qry_data = " and l.dt_lancamento >= '" + data_inicial + "' and l.dt_lancamento <= '" + data_final + "' \n ";
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "registro":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and b.dt_cobranca_registrada = '" + data_inicial + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            qry_data = " and b.dt_cobranca_registrada >= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            qry_data = "' and b.dt_cobranca_registrada <= '" + data_inicial + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            qry_data = " and b.dt_cobranca_registrada >= '" + data_inicial + "' and b.dt_cobranca_registrada <= '" + data_final + "' \n ";
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "referencia":

                switch (tipo_data) {
                    case "igual":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
                            qry_data = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) = '" + ini + "' \n ";
                        }
                        break;
                    case "apartir":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            qry_data = "' substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' \n ";
                        }
                        break;
                    case "ate":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            qry_data = "' substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + ini + "' \n ";
                        }
                        break;
                    case "faixa":
                        if (!referencia_inicial.isEmpty() && !referencia_final.isEmpty()) {
                            String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
                            String fin = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            qry_data = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' \n ";
                        }
                        break;
                    default:
                        break;
                }

//                if (!referencia_inicial.isEmpty() && referencia_final.isEmpty()) {
//                    String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
//                    qry_data = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' \n ";
//                } else if (!referencia_inicial.isEmpty() && !referencia_final.isEmpty()) {
//                    String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
//                    String fin = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
//                    qry_data = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' \n ";
//                } else if (referencia_inicial.isEmpty() && !referencia_final.isEmpty()) {
//                    String fin = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
//                    qry_data = "' substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' \n ";
//                }
                break;
        }

        if (filial_id != null) {
            qry_servico = " and l.id_filial = " + filial_id + " \n ";
        }

        if (id_servico != 0) {
            qry_servico = " and m.id_servicos = " + id_servico + " \n ";
        }

        if (id_tipo_servico != 0) {
            qry_tipo_servico = " and m.id_tipo_servico = " + id_tipo_servico + " \n ";
        }

        if (id_pessoa != -1) {
            if (movimentoDaEmpresa) {
                qry_pessoa = " and m.id_pessoa in ( \n "
                        + "select c.id_pessoa \n "
                        + "  from arr_contribuintes_vw c \n "
                        + " inner join pes_juridica jc on jc.id = c.id_contabilidade \n "
                        + " inner join pes_pessoa p on p.id = jc.id_pessoa \n "
                        + " where p.id = " + id_pessoa + " \n "
                        + "   and c.dt_inativacao is null \n "
                        + " order by c.ds_nome) \n ";
                ordem = "nome, ";
            } else {
                qry_pessoa = " and m.id_pessoa = " + id_pessoa + " \n ";
            }
        }

        if (!boleto_inicial.isEmpty() || !boleto_final.isEmpty()) {
            if (boleto_inicial.equals("0")) {
                boleto_inicial = "";
            }
            if (boleto_final.equals("0")) {
                boleto_final = "";
            }
            if (!boleto_inicial.isEmpty() && boleto_final.isEmpty()) {
                qry_boleto = " and b.ds_boleto >= '" + boleto_inicial + "' \n ";
            } else if (!boleto_inicial.isEmpty() && !boleto_final.isEmpty()) {
                qry_boleto = " and b.ds_boleto >= '" + boleto_inicial + "' \n "
                        + " and b.ds_boleto <= '" + boleto_final + "' \n ";
            } else if (boleto_inicial.isEmpty() && !boleto_final.isEmpty()) {
                qry_boleto = " and b.ds_boleto <= '" + boleto_final + "' \n ";
            }
        }

        String ativo = "true";
        switch (tipo) {
            case "todos":
                break;
            case "recebidas":
                qry_condicao = "   and m.id_baixa is not null \n ";
                break;
            case "naoRecebidas":
                qry_condicao = "   and m.id_baixa is null \n ";
                break;
            case "atrasadas":
                qry_condicao = "   and m.id_baixa is null \n "
                        + "   and m.dt_vencimento < '" + DataHoje.data() + "' \n ";
                break;
            case "excluidos":
                ativo = "false";
                break;
        }

//        if (nrBoletos == true) {
//            cntNrBoletos = " and b.ds_boleto >= '" + descNrBoletoIni + "'"
//                    + " and b.ds_boleto <= '" + descNrBoletoFin + "'";
//        } else {
//            cntNrBoletos = "";
//        }
//        if (empresa == true) {
//            if (!movimentoDaEmpresa)
//                cntEmpresa = " and m.id_pessoa = " + descEmpresa;
//            else{
//                cntEmpresa = " and m.id_pessoa in (select id_pessoa from arr_contribuintes_vw where id_contabilidade = " + descEmpresa + " and dt_inativacao is null order by ds_nome)";
//                ordem = "nome, ";
//            }
//        } else {
//            cntEmpresa = "";
//        }
        if (ordenacao.equals("referencia")) {
            ordem += "substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc, nome";
        } else if (ordenacao.equals("vencimento")) {
            ordem += "m.dt_vencimento desc, nome";
        } else if (ordenacao.equals("quitacao")) {
            ordem += "ba.dt_baixa desc, nome";
        } else if (ordenacao.equals("lancamento")) {
            ordem += "l.dt_lancamento desc, nome";
        } else if (ordenacao.equals("boleto")) {
            ordem += "m.ds_documento desc, nome";
        } else if (ordenacao.equals("empresa")) {
            ordem += "p.ds_nome, p.ds_documento ";
        } else if (ordenacao.equals("contribuicao")) {
            ordem += "s.ds_descricao desc, t.ds_descricao, substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc, nome";
        } else {
            ordem += "ba.dt_importacao desc, nome";
        }

        String qry_status_boleto = "";

        switch (id_status_retorno) {
            case -1:
                // TODOS BOLETOS
                break;
            case -2:
                // NÃO REGISTRADOS
                qry_status_boleto += " AND (b.id_status_retorno IS NULL OR bo.id_status_retorno = 4) AND b.dt_vencimento > CURRENT_DATE \n ";
                qry_status_boleto += " AND m.id_pessoa NOT IN ( \n "
                        + " SELECT id_pessoa FROM fin_bloqueia_servico_pessoa \n "
                        + "  WHERE id_servicos = m.id_servicos \n "
                        + "    AND func_compara_intervalo_ref( \n "
                        + "     '" + DataHoje.referencia() + "','12/2050', \n"
                        + "     RIGHT('0'||EXTRACT(MONTH FROM dt_inicio),2)||'/'||EXTRACT(YEAR FROM dt_inicio), \n"
                        + "     RIGHT('0'||EXTRACT(MONTH FROM dt_fim),2)||'/'||EXTRACT(YEAR FROM dt_fim) \n"
                        + ") = TRUE) \n ";
                break;
            default:
                // STATUS
                qry_status_boleto += " AND b.id_status_retorno = " + id_status_retorno;
                break;
        }

        String qry_adicionado_remessa = "";
        if (!id_boleto_adicionado_remessa.isEmpty()) {
            qry_adicionado_remessa = " AND b.id NOT IN (" + id_boleto_adicionado_remessa + ")";
        }

        String qry_conta_cobranca = "";
        if (id_conta_cobranca != -1) {
            qry_conta_cobranca = " AND b.id_conta_cobranca = " + id_conta_cobranca;
        }

        textQuery = "select m.id            as id, \n " // 0
                + "       p.ds_documento  as documento, \n " // 1
                + "       p.ds_nome       as nome, \n " //2 
                + "       m.ds_documento  as boleto, \n "// 3
                + "       s.ds_descricao  as contribuicao, \n " //4
                + "       m.ds_referencia as referencia, \n "//5
                + "       m.dt_vencimento as vencimento, \n "//6
                + "       ba.dt_importacao as importacao, \n "
                + "       m.nr_valor      as valor, \n "
                + "       m.nr_taxa       as taxa, \n "
                + "       pu.ds_nome      as nomeUsuario, \n "//10
                + "       t.ds_descricao  as tipo, \n "//11
                + "       ba.dt_baixa     as quitacao, \n "
                + "       m.nr_multa      as multa, \n "
                + "       m.nr_juros      as juros, \n "
                + "       m.nr_correcao   as correcao, \n "//15
                + "       m.nr_desconto   as desconto, \n "//16
                + "       cc.nr_repasse   as repasse, \n "//17
                + "       case when ba.id = null              then 0 else ba.id end   as id_baixa,  \n "
                + "       case when pb.ds_nome = null then '' else pb.ds_nome end  as beneficiario, \n "
                + "       case when pf.ds_nome = null       then '' else pf.ds_nome end  as filial, \n "
                + "       m.nr_valor_baixa as valor_baixa, \n "
                + "       UPPER(P5.ds_conta) AS conta,                          \n "//22,
                + "       l.dt_lancamento  AS lancamento, \n "// 23                
                + "       b.id as boleto_id \n "// 24                
                + "  from fin_movimento m \n "
                + "  left join fin_baixa ba on (m.id_baixa = ba.id) \n "
                + "  left join fin_lote l on (m.id_lote = l.id) \n "
                + "  left join seg_usuario u    on (u.id = ba.id_usuario) \n "
                + "  left join pes_pessoa pu    on (pu.id = u.id_pessoa) \n "
                + "  left join fin_boleto b     on (b.nr_ctr_boleto = cast(m.id as text)) \n "
                + "  left join fin_conta_cobranca cc on (cc.id = b.id_conta_cobranca) \n "
                + "  left join pes_filial f on (f.id = l.id_filial)             \n "
                + "  left join pes_juridica pj on (pj.id = f.id_filial)         \n "
                + "  left join pes_pessoa pf on (pf.id = pj.id_pessoa)          \n "
                + "  left join pes_pessoa pb on (pb.id = m.id_beneficiario)     \n "
                + "  left join fin_forma_pagamento FP ON FP.id_baixa = BA.id    \n "
                + "  left join fin_plano5 P5 ON P5.id = FP.id_plano5,           \n "
                + "       pes_pessoa p, \n "
                + "       fin_servicos s, \n "
                + "       fin_tipo_servico t \n "
                + " where m.id_servicos in (select sr.id_servicos from fin_servico_rotina sr where sr.id_rotina = 4) \n "
                + "   and m.is_ativo = " + ativo + " \n "
                + "   and m.id_pessoa = p.id \n "
                + "   and m.id_servicos = s.id \n "
                + "   and m.id_tipo_servico = t.id " + qry_data + qry_boleto + qry_servico + qry_tipo_servico + qry_pessoa + qry_condicao + qry_filial + qry_status_boleto + qry_adicionado_remessa + qry_conta_cobranca
                + "     group by m.id, \n"
                + "        p.ds_documento, \n"
                + "        p.ds_nome, \n"
                + "        m.ds_documento, \n"
                + "        s.ds_descricao, \n"
                + "        m.ds_referencia, \n"
                + "        m.dt_vencimento, \n"
                + "        ba.dt_importacao, \n"
                + "        m.nr_valor, \n"
                + "        m.nr_taxa, \n"
                + "        pu.ds_nome, \n"
                + "        t.ds_descricao, \n"
                + "        ba.dt_baixa, \n"
                + "        m.nr_multa, \n"
                + "        m.nr_juros, \n"
                + "        m.nr_correcao, \n"
                + "        m.nr_desconto, \n"
                + "        cc.nr_repasse, \n"
                + "        ba.id, \n"
                + "        pb.ds_nome, \n"
                + "        pf.ds_nome, \n"
                + "        m.nr_valor_baixa, \n"
                + "        P5.ds_conta, "
                + "        l.dt_lancamento, "
                + "        b.id "
                + " order by " + ordem + "";

        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaTodosMovimentos(boolean data, boolean contrib, boolean nrBoletos, boolean empresa, boolean tipo, String faixaData,
            Date descDataIni, Date descDataFin, String dtRefInicial, String dtRefFinal, int idContribuicao, int idTipoServico, String descNrBoletoIni,
            String descNrBoletoFin, int descEmpresa, String ordenacao, boolean movimentoDaEmpresa) {
        String cntData = "", cntContrib = "", cntNrBoletos = "", cntEmpresa = "", cntTipo = "", ordem = "";

        String textQuery;
        if (data == true) {
            if (faixaData.equals("recebimento")) {
                cntData = " and ba.dt_baixa >= '" + descDataIni + "'"
                        + " and ba.dt_baixa <= '" + descDataFin + "'";
            }
            if (faixaData.equals("importacao")) {
                cntData = " and ba.dt_importacao >= '" + descDataIni + "'"
                        + " and ba.dt_importacao <= '" + descDataFin + "'";
            }
            if (faixaData.equals("vencimento")) {
                cntData = " and m.dt_vencimento >= '" + descDataIni + "'"
                        + " and m.dt_vencimento <= '" + descDataFin + "'";
            }
            if (faixaData.equals("referencia")) {
                String ini = dtRefInicial.substring(3, 7) + dtRefInicial.substring(0, 2);
                String fin = dtRefFinal.substring(3, 7) + dtRefFinal.substring(0, 2);
                cntData = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' "
                        + " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' ";
            }
        } else {
            cntData = "";
        }

        if (contrib == true) {
            cntContrib = " and m.id_servicos = " + idContribuicao;
        } else {
            cntContrib = "";
        }
        if (nrBoletos == true) {
            cntNrBoletos = " and b.ds_boleto >= '" + descNrBoletoIni + "'"
                    + " and b.ds_boleto <= '" + descNrBoletoFin + "'";
        } else {
            cntNrBoletos = "";
        }
        if (empresa == true) {
            if (!movimentoDaEmpresa) {
                cntEmpresa = " and m.id_pessoa = " + descEmpresa;
            } else {
                cntEmpresa = " and m.id_pessoa in (select id_pessoa from arr_contribuintes_vw where id_contabilidade = " + descEmpresa + " and dt_inativacao is null order by ds_nome)";
                ordem = "nome, ";
            }
        } else {
            cntEmpresa = "";
        }
        if (tipo == true) {
            cntTipo = " and m.id_tipo_servico = " + idTipoServico;
        } else {
            cntTipo = "";
        }

        if (ordenacao.equals("referencia")) {
            ordem += "substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc";
        } else if (ordenacao.equals("vencimento")) {
            ordem += "m.dt_vencimento desc";
        } else if (ordenacao.equals("quitacao")) {
            ordem += "ba.dt_baixa desc";
        } else {
            ordem += "ba.dt_importacao desc";
        }

        textQuery = "select m.id            as id, "
                + "       p.ds_documento  as documento, "
                + "       p.ds_nome       as nome, "
                + "       m.ds_documento  as boleto, "
                + "       s.ds_descricao  as contribuicao, "
                + "       m.ds_referencia as referencia, "
                + "       m.dt_vencimento as vencimento, "
                + "       ba.dt_importacao as importacao, "
                + "       m.nr_valor      as valor, "
                + "       m.nr_taxa       as taxa, "
                + "       pu.ds_nome      as nomeUsuario, "
                + "       t.ds_descricao  as tipo,"
                + "       ba.dt_baixa     as quitacao, "
                + "       m.nr_multa      as multa, "
                + "       m.nr_juros      as juros, "
                + "       m.nr_correcao   as correcao, "
                + "       m.nr_desconto   as desconto, "
                + "       cc.nr_repasse   as repasse, "
                + "       case when l.id = null              then 0 else l.id end   as id_baixa, "
                + "       case when pb.ds_nome = null then '' else pb.ds_nome end  as beneficiario, "
                + "       case when pf.ds_nome = null       then '' else pf.ds_nome end  as filial, "
                + "       m.nr_valor_baixa as valor_baixa, \n "
                + "       b.id as boleto_id \n "
                + "  from fin_movimento m "
                + "  left join fin_baixa ba on (m.id_baixa = ba.id) "
                + "  left join fin_lote l on (m.id_lote = l.id) "
                + "  left join seg_usuario u    on (u.id = ba.id_usuario) "
                + "  left join pes_pessoa pu    on (pu.id = u.id_pessoa)"
                + "  left join fin_boleto b     on (b.nr_ctr_boleto = cast(m.id as text)) "
                + "  left join fin_conta_cobranca cc on (cc.id = b.id_conta_cobranca) "
                + "  left join pes_filial f on (f.id = l.id_filial) "
                + "  left join pes_juridica pj on (pj.id = f.id_filial) "
                + "  left join pes_pessoa pf on (pf.id = pj.id_pessoa) "
                + "  left join pes_pessoa pb on (pb.id = m.id_beneficiario), "
                + "       pes_pessoa p, "
                + "       fin_servicos s, "
                + "       fin_tipo_servico t "
                + " where m.id_servicos in (select sr.id_servicos from fin_servico_rotina sr where sr.id_rotina = 4) "
                + "   and m.is_ativo = true "
                + "   and m.id_pessoa = p.id "
                + "   and m.id_servicos = s.id "
                + "   and m.id_tipo_servico = t.id " + cntData + cntContrib + cntNrBoletos + cntEmpresa + cntTipo
                + " order by " + ordem + ", nome";

        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new Vector();
    }

    public List listaRecebidasMovimentos(boolean data, boolean contrib, boolean nrBoletos, boolean empresa, boolean tipo, String faixaData,
            Date descDataIni, Date descDataFin, String dtRefInicial, String dtRefFinal, int idContribuicao, int idTipoServico, String descNrBoletoIni,
            String descNrBoletoFin, int descEmpresa, String ordenacao, boolean movimentoDaEmpresa) {
        String cntData = "", cntContrib = "", cntNrBoletos = "", cntEmpresa = "", cntTipo = "", ordem = "";

        if (data == true) {
            if (faixaData.equals("recebimento")) {
                cntData = " and ba.dt_baixa >= '" + descDataIni + "'"
                        + " and ba.dt_baixa <= '" + descDataFin + "'";
            }
            if (faixaData.equals("importacao")) {
                cntData = " and ba.dt_importacao >= '" + descDataIni + "'"
                        + " and ba.dt_importacao <= '" + descDataFin + "'";
            }
            if (faixaData.equals("vencimento")) {
                cntData = " and m.dt_vencimento >= '" + descDataIni + "'"
                        + " and m.dt_vencimento <= '" + descDataFin + "'";
            }
            if (faixaData.equals("referencia")) {
                String ini = dtRefInicial.substring(3, 7) + dtRefInicial.substring(0, 2);
                String fin = dtRefFinal.substring(3, 7) + dtRefFinal.substring(0, 2);
                cntData = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' "
                        + " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' ";
            }
        } else {
            cntData = "";
        }

        if (contrib == true) {
            cntContrib = " and m.id_servicos = " + idContribuicao;
        } else {
            cntContrib = "";
        }
        if (nrBoletos == true) {
            cntNrBoletos = " and b.ds_boleto >= '" + descNrBoletoIni + "'"
                    + " and b.ds_boleto <= '" + descNrBoletoFin + "'";
        } else {
            cntNrBoletos = "";
        }
        if (empresa == true) {
            if (!movimentoDaEmpresa) {
                cntEmpresa = " and m.id_pessoa = " + descEmpresa;
            } else {
                cntEmpresa = " and m.id_pessoa in (select id_pessoa from arr_contribuintes_vw where id_contabilidade = " + descEmpresa + " and dt_inativacao is null order by ds_nome)";
                ordem = "nome, ";
            }
        } else {
            cntEmpresa = "";
        }
        if (tipo == true) {
            cntTipo = " and m.id_tipo_servico = " + idTipoServico;
        } else {
            cntTipo = "";
        }

        if (ordenacao.equals("referencia")) {
            ordem += "substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc";
        } else if (ordenacao.equals("vencimento")) {
            ordem += "m.dt_vencimento desc";
        } else if (ordenacao.equals("quitacao")) {
            ordem += "ba.dt_baixa desc";
        } else {
            ordem += "ba.dt_importacao desc";
        }

        String textQuery = "select m.id            as id, "
                + "       p.ds_documento  as documento, "
                + "       p.ds_nome       as nome, "
                + "       m.ds_documento  as boleto, "
                + "       s.ds_descricao  as contribuicao, "
                + "       m.ds_referencia as referencia, "
                + "       m.dt_vencimento as vencimento, "
                + "       ba.dt_importacao as importacao, "
                + "       m.nr_valor      as valor, "
                + "       m.nr_taxa       as taxa, "
                + "       pu.ds_nome      as nomeUsuario, "
                + "       t.ds_descricao  as tipo,"
                + "       ba.dt_baixa     as quitacao, "
                + "       m.nr_multa      as multa, "
                + "       m.nr_juros      as juros, "
                + "       m.nr_correcao   as correcao, "
                + "       m.nr_desconto   as desconto, "
                + "       cc.nr_repasse   as repasse, "
                + "       case when l.id = null              then 0 else l.id end   as id_baixa, "
                + "       case when pb.ds_nome = null then '' else pb.ds_nome end  as beneficiario, "
                + "       case when pf.ds_nome = null       then '' else pf.ds_nome end  as filial, "
                + "       m.nr_valor_baixa as valor_baixa, \n "
                + "       b.id as boleto_id \n "
                + "  from fin_movimento m "
                + "  right join fin_baixa ba on (m.id_baixa = ba.id) "
                + "  left join fin_lote l on (m.id_lote = l.id) "
                + "  left join seg_usuario u    on (u.id = ba.id_usuario) "
                + "  left join pes_pessoa pu    on (pu.id = u.id_pessoa)"
                + "  left join fin_boleto b     on (b.nr_ctr_boleto = cast(m.id as text)) "
                + "  left join fin_conta_cobranca cc on (cc.id = b.id_conta_cobranca) "
                + "  left join pes_filial f on (f.id = l.id_filial) "
                + "  left join pes_juridica pj on (pj.id = f.id_filial) "
                + "  left join pes_pessoa pf on (pf.id = pj.id_pessoa) "
                + "  left join pes_pessoa pb on (pb.id = m.id_beneficiario), "
                + "       pes_pessoa p, "
                + "       fin_servicos s, "
                + "       fin_tipo_servico t "
                + " where m.id_servicos in (select sr.id_servicos from fin_servico_rotina sr where sr.id_rotina = 4) "
                + "   and m.is_ativo = true "
                + "   and m.id_pessoa = p.id "
                + "   and m.id_servicos = s.id "
                + "   and m.id_tipo_servico = t.id " + cntData + cntContrib + cntNrBoletos + cntEmpresa + cntTipo
                + " order by " + ordem + ", nome";

        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List listaNaoRecebidasMovimentos(boolean data, boolean contrib, boolean nrBoletos, boolean empresa, boolean tipo, String faixaData,
            Date descDataIni, Date descDataFin, String dtRefInicial, String dtRefFinal, int idContribuicao, int idTipoServico, String descNrBoletoIni,
            String descNrBoletoFin, int descEmpresa, String ordenacao, boolean movimentoDaEmpresa) {
        String cntData = "", cntContrib = "", cntNrBoletos = "", cntEmpresa = "", cntTipo = "", ordem = "";

        if (data == true) {
            if (faixaData.equals("recebimento")) {
                cntData = " and ba.dt_baixa >= '" + descDataIni + "'"
                        + " and ba.dt_baixa <= '" + descDataFin + "'";
            }
            if (faixaData.equals("importacao")) {
                cntData = " and ba.dt_importacao >= '" + descDataIni + "'"
                        + " and ba.dt_importacao <= '" + descDataFin + "'";
            }
            if (faixaData.equals("vencimento")) {
                cntData = " and m.dt_vencimento >= '" + descDataIni + "'"
                        + " and m.dt_vencimento <= '" + descDataFin + "'";
            }
            if (faixaData.equals("referencia")) {
                String ini = dtRefInicial.substring(3, 7) + dtRefInicial.substring(0, 2);
                String fin = dtRefFinal.substring(3, 7) + dtRefFinal.substring(0, 2);
                cntData = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' "
                        + " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' ";
            }
        } else {
            cntData = "";
        }

        if (contrib == true) {
            cntContrib = " and m.id_servicos = " + idContribuicao;
        } else {
            cntContrib = "";
        }
        if (nrBoletos == true) {
            cntNrBoletos = " and b.ds_boleto >= '" + descNrBoletoIni + "'"
                    + " and b.ds_boleto <= '" + descNrBoletoFin + "'";
        } else {
            cntNrBoletos = "";
        }
        if (empresa == true) {
            if (!movimentoDaEmpresa) {
                cntEmpresa = " and m.id_pessoa = " + descEmpresa;
            } else {
                cntEmpresa = " and m.id_pessoa in (select id_pessoa from arr_contribuintes_vw where id_contabilidade = " + descEmpresa + " and dt_inativacao is null order by ds_nome)";
                ordem = "nome, ";
            }
        } else {
            cntEmpresa = "";
        }
        if (tipo == true) {
            cntTipo = " and m.id_tipo_servico = " + idTipoServico;
        } else {
            cntTipo = "";
        }

        if (ordenacao.equals("referencia")) {
            ordem += "substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc";
        } else if (ordenacao.equals("vencimento")) {
            ordem += "m.dt_vencimento desc";
        } else if (ordenacao.equals("quitacao")) {
            ordem += "ba.dt_baixa desc";
        } else {
            ordem += "ba.dt_importacao desc";
        }

        String textQuery = "select m.id            as id, "
                + "       p.ds_documento  as documento, "
                + "       p.ds_nome       as nome, "
                + "       m.ds_documento  as boleto, "
                + "       s.ds_descricao  as contribuicao, "
                + "       m.ds_referencia as referencia, "
                + "       m.dt_vencimento as vencimento, "
                + "       ba.dt_importacao as importacao, "
                + "       m.nr_valor      as valor, "
                + "       m.nr_taxa       as taxa, "
                + "       pu.ds_nome      as nomeUsuario, "
                + "       t.ds_descricao  as tipo,"
                + "       ba.dt_baixa     as quitacao, "
                + "       m.nr_multa      as multa, "
                + "       m.nr_juros      as juros, "
                + "       m.nr_correcao   as correcao, "
                + "       m.nr_desconto   as desconto, "
                + "       cc.nr_repasse   as repasse, "
                + "       0              as id_baixa, "
                + "       case when pb.ds_nome = null then '' else pb.ds_nome end  as beneficiario, "
                + "       ''              as filial, "
                + "       m.nr_valor_baixa as valor_baixa, \n "
                + "       b.id as boleto_id \n "
                + "  from fin_movimento m "
                + "  left join fin_baixa ba on (m.id_baixa = ba.id) "
                + "  left join fin_lote l on (m.id_lote = l.id) "
                + "  left join seg_usuario u    on (u.id = ba.id_usuario) "
                + "  left join pes_pessoa pu    on (pu.id = u.id_pessoa)"
                + "  left join fin_boleto b     on (b.nr_ctr_boleto = cast(m.id as text)) "
                + "  left join fin_conta_cobranca cc on (cc.id = b.id_conta_cobranca) "
                + "  left join pes_pessoa pb on (pb.id = m.id_beneficiario), "
                + "       pes_pessoa p, "
                + "       fin_servicos s, "
                + "       fin_tipo_servico t "
                + " where m.id_servicos in (select sr.id_servicos from fin_servico_rotina sr where sr.id_rotina = 4) "
                + "   and m.is_ativo is true "
                + "   and m.id_pessoa = p.id "
                + "   and m.id_servicos = s.id "
                + "   and m.id_tipo_servico = t.id "
                + "   and m.id_baixa is null " + cntData + cntContrib + cntNrBoletos + cntEmpresa + cntTipo
                + " order by " + ordem + ", nome";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List listaAtrazadasMovimentos(boolean data, boolean contrib, boolean nrBoletos, boolean empresa, boolean tipo, String faixaData,
            Date descDataIni, Date descDataFin, String dtRefInicial, String dtRefFinal, int idContribuicao, int idTipoServico, String descNrBoletoIni,
            String descNrBoletoFin, int descEmpresa, String ordenacao, boolean movimentoDaEmpresa) {
        String cntData = "", cntContrib = "", cntNrBoletos = "", cntEmpresa = "", cntTipo = "", ordem = "";

        if (data == true) {
            if (faixaData.equals("recebimento")) {
                cntData = " and ba.dt_baixa >= '" + descDataIni + "'"
                        + " and ba.dt_baixa <= '" + descDataFin + "'";
            }
            if (faixaData.equals("importacao")) {
                cntData = " and ba.dt_importacao >= '" + descDataIni + "'"
                        + " and ba.dt_importacao <= '" + descDataFin + "'";
            }
            if (faixaData.equals("vencimento")) {
                cntData = " and m.dt_vencimento >= '" + descDataIni + "'"
                        + " and m.dt_vencimento <= '" + descDataFin + "'";
            }
            if (faixaData.equals("referencia")) {
                String ini = dtRefInicial.substring(3, 7) + dtRefInicial.substring(0, 2);
                String fin = dtRefFinal.substring(3, 7) + dtRefFinal.substring(0, 2);
                cntData = " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' "
                        + " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "' ";
            }
        } else {
            cntData = "";
        }

        if (contrib == true) {
            cntContrib = " and m.id_servicos = " + idContribuicao;
        } else {
            cntContrib = "";
        }
        if (nrBoletos == true) {
            cntNrBoletos = " and b.ds_boleto >= '" + descNrBoletoIni + "'"
                    + " and b.ds_boleto <= '" + descNrBoletoFin + "'";
        } else {
            cntNrBoletos = "";
        }
        if (empresa == true) {
            if (!movimentoDaEmpresa) {
                cntEmpresa = " and m.id_pessoa = " + descEmpresa;
            } else {
                cntEmpresa = " and m.id_pessoa in (select id_pessoa from arr_contribuintes_vw where id_contabilidade = " + descEmpresa + " and dt_inativacao is null order by ds_nome)";
                ordem = "nome, ";
            }
        } else {
            cntEmpresa = "";
        }
        if (tipo == true) {
            cntTipo = " and m.id_tipo_servico = " + idTipoServico;
        } else {
            cntTipo = "";
        }

        if (ordenacao.equals("referencia")) {
            ordem += "substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) desc";
        } else if (ordenacao.equals("vencimento")) {
            ordem += "m.dt_vencimento desc";
        } else if (ordenacao.equals("quitacao")) {
            ordem += "ba.dt_baixa desc";
        } else {
            ordem += "ba.dt_importacao desc";
        }

        String textQuery = "select m.id            as id, "
                + "       p.ds_documento  as documento, "
                + "       p.ds_nome       as nome, "
                + "       m.ds_documento  as boleto, "
                + "       s.ds_descricao  as contribuicao, "
                + "       m.ds_referencia as referencia, "
                + "       m.dt_vencimento as vencimento, "
                + "       ba.dt_importacao as importacao, "
                + "       m.nr_valor      as valor, "
                + "       m.nr_taxa       as taxa, "
                + "       pu.ds_nome      as nomeUsuario, "
                + "       t.ds_descricao  as tipo,"
                + "       ba.dt_baixa     as quitacao, "
                + "       m.nr_multa      as multa, "
                + "       m.nr_juros      as juros, "
                + "       m.nr_correcao   as correcao, "
                + "       m.nr_desconto   as desconto, "
                + "       cc.nr_repasse   as repasse, "
                + "       0              as id_baixa, "
                + "       case when pb.ds_nome = null then '' else pb.ds_nome end  as beneficiario, "
                + "       ''              as filial, "
                + "       m.nr_valor_baixa as valor_baixa, \n "
                + "       b.id as boleto_id \n "
                + "  from fin_movimento m "
                + "  left join fin_baixa ba on (m.id_baixa = ba.id) "
                + "  left join fin_lote l on (m.id_lote = l.id) "
                + "  left join seg_usuario u    on (u.id = ba.id_usuario) "
                + "  left join pes_pessoa pu    on (pu.id = u.id_pessoa)"
                + "  left join fin_boleto b     on (b.nr_ctr_boleto = cast(m.id as text)) "
                + "  left join fin_conta_cobranca cc on (cc.id = b.id_conta_cobranca) "
                + "  left join pes_pessoa pb on (pb.id = m.id_beneficiario), "
                + "       pes_pessoa p, "
                + "       fin_servicos s, "
                + "       fin_tipo_servico t "
                + " where m.id_servicos in (select sr.id_servicos from fin_servico_rotina sr where sr.id_rotina = 4) "
                + "   and m.is_ativo is true "
                + "   and m.id_pessoa = p.id "
                + "   and m.id_servicos = s.id "
                + "   and m.id_tipo_servico = t.id "
                + "   and m.id_baixa is null "
                + "   and m.dt_vencimento < '" + DataHoje.data() + "'" + cntData + cntContrib + cntNrBoletos + cntEmpresa + cntTipo
                + " order by " + ordem + ", nome";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List pesquisaMovimentoPorJuridica(int id) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select mov from Movimento mov, "
                    + "                Juridica jur, "
                    + "                Pessoa pes "
                    + "          where pes.id = jur.pessoa.id"
                    + "            and pes.id = mov.pessoa.id"
                    + "            and jur.id = :id_jur");
            qry.setParameter("id_jur", id);
            return qry.getResultList();
        } catch (Exception e) {

            return null;
        }
    }

    public List pesquisaServicoCobranca(String id[]) {
        try {
            String query = "select sc"
                    + "  from ServicoContaCobranca sc"
                    + "       Movimento m"
                    + " where m.servicos.id = sc.servico.id "
                    + "   and m.tipoServico.id = sc.tipoServico.id "
                    + "   and m.contaCobranca.id = sc.contaCobranca.id"
                    + "   and m.id in (";
            int i = 0;
            while (i < id.length) {
                query += id[i];
                if ((i + 1) < id.length) {
                    query += ",";
                }
                i++;
            }
            query += ")";
            Query qry = getEntityManager().createQuery(query);
            return (qry.getResultList());
        } catch (Exception e) {

            return null;
        }
    }

    public List pesquisaOrdenarServicoMovimento(List<String> id) {
        try {
            String query = "select distinct m.servicos.id"
                    + "  from Movimento m"
                    + " where m.id in (";
            int i = 0;
            while (i < id.size()) {
                query += id.get(i);
                if ((i + 1) < id.size()) {
                    query += ",";
                }
                i++;
            }
            query += ") ";
            query += "order by m.servicos.descricao";
            Query qry = getEntityManager().createQuery(query);
            return (qry.getResultList());
        } catch (Exception e) {

            return null;
        }
    }

    public List listaImpressaoGeral(String isEscritorio, List<String> id, List<Integer> listaConvencao, List<Integer> listaGrupoCidade, String todasContas, String email, int id_esc, String type, Integer qtde, String registrado, List<ServicoContaCobranca> listServicoContaCobranca) {
        try {

            String datas = " ( ", filtros = "";
            for (int i = 0; i < id.size(); i++) {
                if (id.get(i) != null) {
                    datas += " \'" + id.get(i).replace("/", "-") + "\' ";
                    if ((i + 1) < id.size()) {
                        datas += ",";
                    }
                    if (i == (id.size() - 1)) {
                        datas += " )";
                    }
                }
            }

            String inTipoServico = "";
            String inServico = "";
            if (todasContas.equals("false") || !listServicoContaCobranca.isEmpty()) {
                String in = " AND (";
                for (int i = 0; i < listServicoContaCobranca.size(); i++) {
                    if (i == 0) {
                        inTipoServico = "" + listServicoContaCobranca.get(i).getTipoServico().getId();
                        inServico = "" + listServicoContaCobranca.get(i).getServicos().getId();
                        in += " (m.id_servicos = " + listServicoContaCobranca.get(i).getServicos().getId() + " AND bo.id_conta_cobranca = " + listServicoContaCobranca.get(i).getContaCobranca().getId() + " ) \n";
                    } else {
                        inTipoServico += "," + listServicoContaCobranca.get(i).getTipoServico().getId();
                        inServico = ", " + listServicoContaCobranca.get(i).getServicos().getId();
                        in += " OR (m.id_servicos = " + listServicoContaCobranca.get(i).getServicos().getId() + " AND bo.id_conta_cobranca = " + listServicoContaCobranca.get(i).getContaCobranca().getId() + " ) \n";
                    }
                }
                in += ") \n";
                filtros += in;
            } else {
                inTipoServico = "1";
            }

            String grupoCidadeConvencao = "";
            if (!listaConvencao.isEmpty()) {
                grupoCidadeConvencao = " AND contr.id_convencao IN (";

                for (int i = 0; i < listaConvencao.size(); i++) {
                    if (i == 0) {
                        grupoCidadeConvencao += listaConvencao.get(i);
                    } else {
                        grupoCidadeConvencao += "," + listaConvencao.get(i);
                    }
                }
                grupoCidadeConvencao += ") \n";
            }

            if (!listaGrupoCidade.isEmpty()) {
                grupoCidadeConvencao += " AND contr.id_grupo_cidade IN (";
                for (int i = 0; i < listaGrupoCidade.size(); i++) {
                    if (i == 0) {
                        grupoCidadeConvencao += listaGrupoCidade.get(i);
                    } else {
                        grupoCidadeConvencao += "," + listaGrupoCidade.get(i);
                    }
                }
                grupoCidadeConvencao += ") \n ";
            }

            filtros += grupoCidadeConvencao;

            switch (email) { // EMAIL                
                case "com":
                    email = " AND ( ( ( pj.id_contabilidade IS NULL or pj.is_cobranca_escritorio = false ) AND pcomp.id_status_cobranca = 2 ) OR ( pj.id_contabilidade > 0 AND pj.is_cobranca_escritorio = true AND pcompe.id_status_cobranca = 2 ) ) \n";
                    break; // CORREIOS                
                case "sem":
                    email = " AND ( ( ( pj.id_contabilidade IS NULL OR pj.is_cobranca_escritorio = false ) AND pcomp.id_status_cobranca = 1 ) OR ( pj.id_contabilidade > 0 AND pj.is_cobranca_escritorio = true AND pcompe.id_status_cobranca = 1 ) ) \n";
                    break;
                default:
                    email = " ";
                    break;
            }

            if (id_esc != 0) {
                filtros += " AND j_contabil.id = " + id_esc + " \n";
            }

            if (type.equals("ate")) {
                filtros += " AND x.qtde <= " + qtde + " \n";
            } else if (type.equals("apartir")) {
                filtros += " AND x.qtde >= " + qtde + "\n";
            }

            if (registrado.equals("registrado")) {
                filtros += " AND bo.dt_cobranca_registrada IS NOT NULL \n";
            } else if (registrado.equals("sem_registro")) {
                filtros += " AND bo.dt_cobranca_registrada IS NULL \n";
            }

            String textQry = "SELECT m.ds_documento AS boleto,                                                              \n" // 0 BOLETO
                    + "              contr.ds_nome  AS razao,                                                               \n" // 1 EMPRESA NOME
                    + "              contr.ds_documento AS cnpj,                                                            \n" // 2 EMPRESA DOCUMENTO
                    + "    CASE WHEN ( x.idcontabilidade > 0 AND pj.is_cobranca_escritorio = true)  THEN p_contabil.ds_nome ELSE '' END AS escritorio,          \n" // 3 CONTABILIDADE NOME
                    + "              s.ds_descricao  AS servico,                                                            \n" // 4 SERVIÇO (CONTRIBUIÇÃO)
                    + "              t.ds_descricao  AS tipo_servico,                                                       \n" // 5 TIPO DE SERVIÇO
                    + "              m.dt_vencimento AS vencimento,                                                         \n" // 6 VENCIMENTO
                    + "              m.ds_referencia AS referencia,                                                         \n" // 7 REFERÊNCIA
                    + "              m.id AS id,                                                                            \n" // 8 MOVIMENTO ID
                    + "    CASE WHEN ( x.idcontabilidade > 0 AND pj.is_cobranca_escritorio = true) THEN p_contabil.id ELSE 0 END AS idContabilidade,            \n" // 9 CONTABILIDADE id_pessoa
                    + "              contr.id_juridica AS idJuridica,                                                       \n" // 10 EMPRESA id_juridica
                    + "    CASE WHEN ( x.idcontabilidade > 0 AND pj.is_cobranca_escritorio = true) THEN x.qtde ELSE 0 END AS qtde,                              \n" // 11 QUANTIDADE
                    + "              bo.dt_cobranca_registrada AS data_cobranca_registrada,                                 \n" // 12 DATA COBRANÇA REGISTRADA
                    + "    CASE WHEN (cc.id_cobranca_registrada = 3) THEN false ELSE true END AS cobranca_registrada    \n" // 13 COBRANÇA REGISTRADA
                    + "         FROM fin_movimento              AS m                                                    \n"
                    + " INNER JOIN arr_contribuintes_vw         AS contr      ON m.id_pessoa = contr.id_pessoa          \n"
                    + " INNER JOIN pes_pessoa                   AS p          ON p.id = contr.id_pessoa                 \n"
                    + " INNER JOIN pes_juridica                 AS pj         ON pj.id=contr.id_juridica                \n"
                    + "  LEFT JOIN pes_juridica                 AS j_contabil ON j_contabil.id = contr.id_contabilidade \n"
                    + "  LEFT JOIN pes_pessoa                   AS p_contabil ON p_contabil.id = j_contabil.id_pessoa   \n"
                    + " INNER JOIN fin_boleto                   AS bo         ON bo.nr_ctr_boleto = m.nr_ctr_boleto     \n"
                    + " INNER JOIN fin_conta_cobranca           AS cc	      ON cc.id = bo.id_conta_cobranca          \n"
                    + " INNER JOIN fin_servicos                 AS s          ON s.id = m.id_servicos                   \n"
                    + " INNER JOIN fin_tipo_servico             AS t          ON t.id = m.id_tipo_servico               \n"
                    + " INNER JOIN pes_pessoa_endereco          AS pce        ON pce.id_pessoa = contr.id_pessoa AND pce.id_tipo_endereco = 3 \n"
                    + "  LEFT JOIN pes_pessoa_endereco          AS pce2       ON pce2.id_pessoa = p_contabil.id AND pce2.id_tipo_endereco = 3 \n"
                    + "  LEFT JOIN fin_bloqueia_servico_pessoa  AS sp         ON sp.id_pessoa = m.id_pessoa AND sp.id_servicos = m.id_servicos AND m.dt_vencimento >= sp.dt_inicio AND m.dt_vencimento <= sp.dt_fim \n"
                    + "  LEFT JOIN pes_pessoa_complemento       AS pcomp      ON pcomp.id_pessoa = p.id                                         \n"
                    + "  LEFT JOIN pes_pessoa_complemento       AS pcompe     ON pcompe.id_pessoa = p_contabil.id                               \n"
                    // + "  30/09/2015 - LEFT JOIN ( SELECT CASE WHEN pj.is_cobranca_escritorio = true THEN p_contabil.id ELSE 0 END AS idContabilidade, count(*) qtde n"
                    + "  LEFT JOIN ( SELECT p_contabil.id AS idContabilidade, count(*) qtde                                         \n"
                    + "                FROM fin_movimento               AS m                                                        \n"
                    + "          INNER JOIN arr_contribuintes_vw        AS contr        ON m.id_pessoa = contr.id_pessoa            \n"
                    + "          INNER JOIN pes_juridica                AS pj           ON pj.id = contr.id_juridica                \n"
                    + "          INNER JOIN pes_pessoa                  AS p            ON p.id = contr.id_pessoa                   \n"
                    //                    + "         30/09/2015 - LEFT JOIN pes_juridica                AS j_contabil   ON j_contabil.id = contr.id_contabilidade   \n"
                    //                    + "         30/09/2015 - LEFT JOIN pes_pessoa                  AS p_contabil   ON p_contabil.id = j_contabil.id_pessoa     \n"
                    + "          INNER JOIN pes_juridica                AS j_contabil   ON j_contabil.id = contr.id_contabilidade   \n"
                    + "          INNER JOIN pes_pessoa                  AS p_contabil   ON p_contabil.id = j_contabil.id_pessoa     \n"
                    + "          INNER JOIN fin_boleto                  AS bo           ON bo.nr_ctr_boleto = m.nr_ctr_boleto       \n"
                    + "          INNER JOIN fin_servicos                AS s            ON s.id = m.id_servicos                     \n"
                    + "          INNER JOIN fin_tipo_servico            AS t            ON t.id = m.id_tipo_servico                 \n"
                    + "          INNER JOIN pes_pessoa_endereco         AS pce          ON pce.id_pessoa  = contr.id_pessoa AND pce.id_tipo_endereco = 3  \n"
                    + "           LEFT JOIN pes_pessoa_endereco         AS pce2         ON pce2.id_pessoa = p_contabil.id AND pce2.id_tipo_endereco = 3   \n"
                    + "           LEFT JOIN fin_bloqueia_servico_pessoa AS sp           ON sp.id_pessoa = m.id_pessoa AND sp.id_servicos = m.id_servicos AND m.dt_vencimento >= sp.dt_inicio AND m.dt_vencimento <= sp.dt_fim \n"
                    + "               WHERE (sp.is_impressao = true OR sp.is_impressao IS NULL) \n"
                    + "                 AND m.is_ativo = true                                   \n"
                    + "                 AND m.id_Baixa IS NULL                                  \n"
                    + "                 AND m.ds_es = 'E'                                       \n"
                    + "                 AND m.id_tipo_Servico IN (" + inTipoServico + ")        \n"
                    + "                 AND m.dt_Vencimento IN  " + datas + "                   \n"
                    // 30/09/2015 - ADICIONADO AS LINHAS ABAIXO
                    + "                 AND j_contabil.id > 0                                   \n"
                    + "                 AND pj.is_cobranca_escritorio = true                    \n"
                    + "            GROUP BY 1                                                   \n"
                    + "            ) AS x ON x.idcontabilidade = p_contabil.id                  \n"
                    + "      WHERE (sp.is_impressao = true OR sp.is_impressao IS NULL)          \n"
                    + email
                    + filtros
                    + "      AND m.id_Baixa IS NULL                                             \n"
                    + "      AND m.is_ativo = true                                              \n"
                    + "      AND contr.id_pessoa NOT IN(SELECT bl.id_pessoa FROM fin_bloqueia_servico_pessoa AS bl WHERE bl.is_impressao = false AND bl.id_servicos IN (" + inServico + ") AND '15/09/2013' >= bl.dt_inicio AND '15/09/2013' <= bl.dt_fim) \n"
                    + "      AND m.ds_es = 'E'                                                  \n"
                    + "      AND m.id_tipo_Servico IN (" + inTipoServico + ")                   \n"
                    + "      AND m.dt_Vencimento IN " + datas + "                               \n"
                    + " ORDER BY escritorio, razao              \n";

            Debugs.put("habilitaDebugQuery", textQry);
            Query query = getEntityManager().createNativeQuery(textQry);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Object[] pesquisaValorFolha(Movimento movimento) {
        Object[] valor = new Object[2];
        String referencia = movimento.getReferencia().substring(3, 7) + movimento.getReferencia().substring(0, 2);
        try {
            Query qry = getEntityManager().createQuery(
                    "select f.valorMes, d.percentual "
                    + "  from DescontoEmpregado d, "
                    + "         FolhaEmpresa f, "
                    + "         PessoaEndereco pe,"
                    + "         Endereco e,"
                    + "         GrupoCidades gcs, "
                    + "         CnaeConvencao cnc, "
                    + "         ConvencaoCidade cc "
                    + "where d.servicos.id = " + movimento.getServicos().getId()
                    + "   and f.tipoServico.id = " + movimento.getTipoServico().getId()
                    + "   and f.referencia = \"" + movimento.getReferencia() + "\""
                    + "   and f.juridica.pessoa.id = " + movimento.getPessoa().getId()
                    + "   and f.juridica.cnae.id = cnc.cnae.id"
                    + "   and pe.endereco.id = e.id"
                    + "   and pe.tipoEndereco.id = 5"
                    + "   and pe.pessoa.id = f.juridica.pessoa.id"
                    + "   and e.cidade.id = gcs.cidade.id"
                    + "   and cc.grupoCidade.id = gcs.grupoCidade.id"
                    + "   and cc.convencao.id = cnc.convencao.id"
                    + "   and d.convencao.id = cc.convencao.id"
                    + "   and d.grupoCidade.id = cc.grupoCidade.id"
                    + "   and \"" + referencia + "\" between CONCAT( SUBSTRING(d.referenciaInicial,4,8) ,"
                    + "                                           SUBSTRING(d.referenciaInicial,0,3) )"
                    + "                               and                                             "
                    + "                                   CONCAT( SUBSTRING(d.referenciaFinal,4,8)  , "
                    + "                                           SUBSTRING(d.referenciaFinal,0,3)   )");

            Query qry2 = getEntityManager().createNativeQuery(
                    "select fol.nr_valor  as valor,                                                                     "
                    + "       nr_percentual as percentual                                                                 "
                    + "  from arr_desconto_empregado des,                                                                 "
                    + "       arr_faturamento_folha_empresa fol,                                                          "
                    + "       pes_juridica j,                                                                             "
                    + "       pes_pessoa_endereco pe,                                                                     "
                    + "       end_endereco e,                                                                             "
                    + "       arr_grupo_cidades gcs,                                                                      "
                    + "       arr_cnae_convencao cnaec,                                                                   "
                    + "       arr_convencao_cidade cc                                                                     "
                    + "   where des.id_servicos =   " + movimento.getServicos().getId()
                    + "   and fol.id_tipo_servico = " + movimento.getTipoServico().getId()
                    + "   and fol.ds_referencia = \'" + movimento.getReferencia() + "\' "
                    + "   and fol.id_juridica     = j.id                                                                  "
                    + "   and j.id_pessoa         = " + movimento.getPessoa().getId()
                    + "   and j.id_cnae           = cnaec.id_cnae                                                         "
                    + "   and pe.id_endereco      = e.id                                                                  "
                    + "   and pe.id_tipo_endereco = 5                                                                     "
                    + "   and pe.id_pessoa        = j.id_pessoa                                                           "
                    + "   and e.id_cidade         = gcs.id_cidade                                                         "
                    + "   and cc.id_grupo_cidade  = gcs.id_grupo_cidade                                                   "
                    + "   and cc.id_convencao     = cnaec.id_convencao                                                    "
                    + "   and des.id_convencao    = cc.id_convencao                                                       "
                    + "   and des.id_grupo_cidade = cc.id_grupo_cidade                                                    "
                    + "   and \'" + referencia + "\'                                                                                    "
                    + "       between                                                                                     "
                    + "       SUBSTRING(des.ds_ref_inicial,4,7) || SUBSTRING(des.ds_ref_inicial,1,2)                      "
                    + "       and                                                                                         "
                    + "       SUBSTRING(des.ds_ref_final,4,7)   || SUBSTRING  (des.ds_ref_final,1,2)  ");
            //valor = (Object[]) qry.getSingleResult();
            List resultado = (Vector) qry2.getSingleResult();

            return new Object[]{(new BigDecimal((Double) resultado.get(0))).doubleValue(), (new BigDecimal((Double) resultado.get(1))).doubleValue()};
        } catch (Exception e) {

            return null;
        }
    }

    public Object[] pesquisaValorFolha(int idServico, int idTipo, String ref, int idPessoa) {
        Object[] valor = new Object[2];

        String text
                = "SELECT d.nr_valor_por_empregado * f.nr_num_funcionarios AS valor_empregado, \n"
                + "       f.nr_valor * d.nr_percentual / 100 AS valor \n"
                + "  FROM arr_faturamento_folha_empresa f \n"
                + " INNER JOIN pes_juridica AS j ON j.id = f.id_juridica \n"
                + " INNER JOIN pes_pessoa_endereco pe ON pe.id_pessoa = j.id_pessoa AND pe.id_tipo_Endereco = 5 \n"
                + " INNER JOIN end_endereco AS e ON e.id = pe.id_endereco \n"
                + " INNER JOIN arr_grupo_cidades AS gcs ON gcs.id_cidade = e.id_cidade \n"
                + " INNER JOIN arr_cnae_convencao AS cnc ON cnc.id_cnae = j.id_cnae \n"
                + " INNER JOIN arr_desconto_empregado AS d ON d.id_convencao = cnc.id_convencao AND d.id_grupo_cidade = gcs.id_grupo_cidade\n"
                + "   				AND RIGHT(f.ds_referencia, 4) || LEFT(f.ds_referencia, 2) BETWEEN RIGHT(d.ds_ref_Inicial, 4) || LEFT(d.ds_ref_Inicial, 2) AND RIGHT(d.ds_ref_final, 4) || LEFT(d.ds_ref_final, 2) \n"
                + "  WHERE d.id_servicos = " + idServico + " \n"
                + "    AND f.id_tipo_Servico = " + idTipo + " \n"
                + "    AND f.ds_referencia = '" + ref + "' \n"
                + "    AND j.id_pessoa = " + idPessoa;

        try {
            Query qry = getEntityManager().createNativeQuery(text);

            List vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    valor[0] = (Double) ((Vector) vetor.get(i)).get(0);
                    valor[1] = (Double) ((Vector) vetor.get(i)).get(1);
                }
                return valor;
            } 
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Movimento pesquisaMovPorNumDocumento(String numero) {
        Movimento mov = new Movimento();
        try {
            Query qry = getEntityManager().createQuery("select mov "
                    + "  from Movimento mov "
                    + " where mov.documento = '" + numero + "'"
                    + " and mov.baixa is null "
                    + " and mov.ativo = true");
            mov = (Movimento) qry.getSingleResult();
            return mov;
        } catch (Exception e) {
        }
        return mov;
    }

    public List<Movimento> pesquisaMovPorTipoDocumentoList(String tipoDocumento, String ref, int idContaCobranca, TipoServico tipoServico) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id as idi "
                    + "  from fin_movimento mov, "
                    + "       pes_pessoa pes "
                    + " where mov.id_pessoa = pes.id "
                    + "   and substring('00'||substring(replace( "
                    + " replace( "
                    + "      replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''),length( "
                    + " replace( "
                    + "       replace( "
                    + "             replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))-14,length( "
                    + " replace( "
                    + "       replace( "
                    + "             replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))),0,16) = '" + tipoDocumento + "' "
                    + "   and mov.id_baixa is null "
                    + //"   and mov.id_conta_cobranca = " + idContaCobranca +
                    "   and mov.id_servicos = 1 "
                    + // ------- FIXO
                    "   and mov.ds_referencia = '" + ref + "' "
                    + "   and mov.id_tipo_servico = " + tipoServico.getId();
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {

            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorTipoDocumentoBaixado(String tipoDocumento, String ref, int idContaCobranca, TipoServico tipoServico) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id as idi "
                    + "  from fin_movimento mov, "
                    + "       pes_pessoa pes, "
                    + "       fin_boleto bol "
                    + " where mov.id_pessoa = pes.id "
                    + "   and substring('00'||substring(replace( "
                    + " replace( "
                    + "      replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''),length( "
                    + " replace( "
                    + "       replace( "
                    + "             replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))-14,length( "
                    + " replace( "
                    + "       replace( "
                    + "             replace('0000000000'||pes.ds_documento,'/',''),'-',''),'.',''))),0,16) = '" + tipoDocumento + "' "
                    + "   and mov.id_baixa is not null "
                    + //"   and mov.id_conta_cobranca = " + idContaCobranca +
                    "   and bol.id_conta_cobranca = " + idContaCobranca
                    + "   and mov.id_servicos = 1 "
                    + // ------- FIXO
                    "   and mov.ds_referencia = '" + ref + "' "
                    + "   and mov.is_ativo is true "
                    + "   and mov.id_tipo_servico = " + tipoServico.getId();
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {

            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumDocumentoListSindical(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids "
                    + "  from fin_movimento mov, fin_boleto bol "
                    + "  where mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                    + "    and substring('000000000000000'||'" + numero + "', "
                    + "                  length('000000000000000'||'" + numero + "') - 16, "
                    + "                  length('000000000000000'||'" + numero + "')) = "
                    + "        substring('000000000000000'||bol.ds_boleto, "
                    + "                  length('000000000000000'||bol.ds_boleto) - 16, "
                    + "                  length('000000000000000'||bol.ds_boleto))"
                    + "    and mov.is_ativo is true "
                    + "    and mov.id_baixa is null "
                    + "    AND mov.nr_ctr_boleto IS NOT NULL AND mov.nr_ctr_boleto <> '' " // nova verificação 30/08/2017
                    + "    and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {
            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumDocumentoList(String numero, Date vencimento, int idContaCobranca) {
        List<Movimento> listMov = new ArrayList();
        String textQuery;
        FilialDao db = new FilialDao();
        // PESQUISANDO PELA FILIAL... DEPOIS ADICIONAR UMA COMBO COM ELAS
        Registro reg = db.pesquisaRegistroPorFilial(1);
        textQuery
                = "SELECT m.* \n"
                + "  FROM fin_movimento m \n "
                + " INNER JOIN fin_boleto b ON b.nr_ctr_boleto = m.nr_ctr_boleto \n "
                + " WHERE m.id_baixa IS NULL \n "
                + "   AND m.is_ativo = TRUE \n "
                + "   AND m.nr_ctr_boleto IS NOT NULL \n "
                + "   AND m.nr_ctr_boleto <> '' \n "
                + "   AND SUBSTRING('000000000000000'||'" + numero + "', LENGTH('000000000000000'||'" + numero + "') - 16, LENGTH('000000000000000'||'" + numero + "')) \n"
                + "                = \n"
                + "       SUBSTRING('000000000000000'||b.ds_boleto, LENGTH('000000000000000'||b.ds_boleto) - 16, LENGTH('000000000000000'||b.ds_boleto))";
        if (!reg.isBaixaVencimento()) {
            textQuery += " AND b.id_conta_cobranca = " + idContaCobranca;
        } else if (DataHoje.converteData(vencimento).equals("11/11/1111")) {
            textQuery
                    += " AND b.id_conta_cobranca = " + idContaCobranca;
        } else {
            textQuery
                    += " AND b.id_conta_cobranca  = " + idContaCobranca
                    + "  AND b.dt_vencimento = '" + DataHoje.converteData(vencimento) + "'";
        }
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery, Movimento.class);
            listMov = qry.getResultList();
            return listMov;
        } catch (EJBQLException e) {
            e.getMessage();
            return listMov;
        }

    }

    public List<Movimento> pesquisaMovPorNumDocumentoListBaixadoArr(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids "
                    + "  from fin_movimento mov, fin_boleto bol "
                    + "  where mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                    + "    and substring('000000000000000'||'" + numero + "', "
                    + "                  length('000000000000000'||'" + numero + "') - 16, "
                    + "                  length('000000000000000'||'" + numero + "')) = "
                    + "        substring('000000000000000'||bol.ds_boleto, "
                    + "                  length('000000000000000'||bol.ds_boleto) - 16, "
                    + "                  length('000000000000000'||bol.ds_boleto))"
                    + "    and mov.is_ativo is true "
                    + "    and mov.id_baixa is not null "
                    + "    AND mov.nr_ctr_boleto IS NOT NULL AND mov.nr_ctr_boleto <> '' " // nova linha de verificação 30/08/2017
                    + "    and mov.id_servicos IN (select id_servicos from fin_servico_rotina where id_rotina = 4) "
                    + "    and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {
            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumDocumentoListArr(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids "
                    + "  from fin_movimento mov, fin_boleto bol "
                    + "  where mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                    + "    and substring('000000000000000'||'" + numero + "', "
                    + "                  length('000000000000000'||'" + numero + "') - 16, "
                    + "                  length('000000000000000'||'" + numero + "')) = "
                    + "        substring('000000000000000'||bol.ds_boleto, "
                    + "                  length('000000000000000'||bol.ds_boleto) - 16, "
                    + "                  length('000000000000000'||bol.ds_boleto))"
                    + "    and mov.is_ativo is true "
                    + "    and mov.id_baixa is null "
                    + "    and mov.id_servicos IN (select id_servicos from fin_servico_rotina where id_rotina = 4) "
                    + "    and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {

            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumDocumentoListBaixadoAss(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids "
                    + "  from fin_movimento mov, fin_boleto bol "
                    + "  where mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                    + "    and substring('000000000000000'||'" + numero + "', "
                    + "                  length('000000000000000'||'" + numero + "') - 16, "
                    + "                  length('000000000000000'||'" + numero + "')) = "
                    + "        substring('000000000000000'||bol.ds_boleto, "
                    + "                  length('000000000000000'||bol.ds_boleto) - 16, "
                    + "                  length('000000000000000'||bol.ds_boleto))"
                    + "    and mov.is_ativo is true "
                    + "    and mov.id_baixa is not null "
                    + "    AND mov.nr_ctr_boleto IS NOT NULL AND mov.nr_ctr_boleto <> '' "
                    + "    and mov.id_servicos NOT IN (select id_servicos from fin_servico_rotina where id_rotina = 4) "
                    + "    and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {
            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumDocumentoListAss(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids "
                    + "  from fin_movimento mov, fin_boleto bol "
                    + "  where mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                    + "    and substring('000000000000000'||'" + numero + "', "
                    + "                  length('000000000000000'||'" + numero + "') - 16, "
                    + "                  length('000000000000000'||'" + numero + "')) = "
                    + "        substring('000000000000000'||bol.ds_boleto, "
                    + "                  length('000000000000000'||bol.ds_boleto) - 16, "
                    + "                  length('000000000000000'||bol.ds_boleto))"
                    + "    and mov.is_ativo is true "
                    //+ "    and mov.id_baixa is null "
                    + "    AND mov.nr_ctr_boleto IS NOT NULL AND mov.nr_ctr_boleto <> '' "
                    + "    and mov.id_servicos NOT IN (select id_servicos from fin_servico_rotina where id_rotina = 4) "
                    + "    and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {

            return listMov;
        }
    }

    public List<Movimento> pesquisaMovPorNumPessoaListBaixado(String numero, int idContaCobranca) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select mov.id ids     "
                    + "  from fin_movimento mov "
                    + "  inner join fin_baixa ba on (ba.id = mov.id_baixa) "
                    + "  inner join fin_boleto bol on (mov.nr_ctr_boleto = bol.nr_ctr_boleto) "
                    + " where ba.ds_documento_baixa = '" + numero + "'"
                    + "   and mov.is_ativo is true     "
                    + "   AND mov.nr_ctr_boleto IS NOT NULL AND mov.nr_ctr_boleto <> ''" // nova linha de verificação 30/08/2017
                    + "   and bol.id_conta_cobranca = " + idContaCobranca;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {
            return listMov;
        }
    }

    public List<Movimento> listaMovimentosDoLote(int idLote) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT MOV FROM Movimento AS MOV WHERE MOV.lote.id = :pLote ORDER BY MOV.dtVencimento ASC, MOV.id ASC");
            qry.setParameter("pLote", idLote);
            List<Movimento> listMovimentos = qry.getResultList();
            if (!listMovimentos.isEmpty()) {
                return listMovimentos;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public int movimentosDoLote(int idLote) {
        int qntMovimento = 0;
        try {
            Query qry = getEntityManager().createQuery(
                    " select count(mov) "
                    + "   from Movimento mov "
                    + "  where mov.lote.id = :pLote");
            qry.setParameter("pLote", idLote);
            qntMovimento = Integer.parseInt(String.valueOf((Long) qry.getSingleResult()));
            return qntMovimento;
        } catch (Exception e) {

            return qntMovimento;
        }
    }

    public List<Movimento> pesquisaMovimentos(int id_pessoa, String ds_ref, int id_tipo_servico, int id_servicos) {
        try {
            String texto
                    = "SELECT m.* \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.ds_referencia = '" + ds_ref + "' \n "
                    + "   AND m.id_pessoa = " + id_pessoa + " \n "
                    + "   AND m.id_servicos = " + id_servicos + " \n "
                    + "   AND ( \n "
                    + "         (m.id_tipo_servico = " + id_tipo_servico + " AND m.is_ativo = TRUE) OR (m.id_tipo_servico = " + id_tipo_servico + " AND m.id_acordo > 0 AND m.is_ativo = FALSE) \n "
                    + "       )";

            Query qry = getEntityManager().createNativeQuery(texto, Movimento.class);
            // DEVE RETORNAR APENAS UM RESULTADO OU ZERO, SE TRAZER MAIS DE UM ESTA COM ERRO POIS NÃO PODE DUPLICAR A CHAVE
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public boolean extornarMovimento(List<Integer> listaLoteBaixa) {
        String vetor = "";
        vetor = listaLoteBaixa.toString();
        vetor = vetor.replace("[", "(");
        vetor = vetor.replace("]", ")");
        String textQuery = "";
        try {
            textQuery = "update fin_movimento m                                                   "
                    + "   set nr_ativo = 1,                                                     "
                    + "       id_lote_baixa = null                                              "
                    + " where id_lote_baixa in " + vetor + "                                      "
                    + "   and nr_ativo = 0;                                                     "
                    + "                                                                         "
                    + "delete from fin_complemento_valor c                                      "
                    + " where c.id_movimento in ( select m.id                                   "
                    + "                             from fin_movimento m                        "
                    + "                            where id_lote_baixa in " + vetor + "           "
                    + "                              and nr_ativo = 1 );                        "
                    + "delete from fin_movimento m                                              "
                    + " where id_lote_baixa in " + vetor + "                                      "
                    + "   and nr_ativo = 1;                                                     "
                    + "                                                                         "
                    + "delete from fin_lote l                                                   "
                    + " where l.id not in (select id_lote                                       "
                    + "                      from fin_movimento);                               "
                    + "                                                                         "
                    + "delete from fin_baixa where id in " + vetor + ";                      ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            qry.executeUpdate();
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public List pesquisaGuia(String campo, String valor) {
        List result = null;
        try {
            String textoQuery
                    = "  select distinct g.id, g, m"
                    + "  from Guia g,"
                    + "       MovimentoResponsavel m"
                    + " where g.lote.id = m.movimento.lote.id"
                    + "   and m.movimento.ativo = 1";
            if (campo.equals("nomeBen")) {
                textoQuery += "and upper(m.beneficiario.nome) like :valor";
            } else if (campo.equals("nomeEmp")) {
                textoQuery += "and upper(g.pessoa.nome) like :valor";
            } else if (campo.equals("data")) {
                textoQuery += "and upper(g.lote.data) = :valor";
            }
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("valor", valor.toUpperCase());
            result = qry.getResultList();
        } catch (Exception e) {
            result = new ArrayList();
        }
        return result;
    }

    public List<Movimento> pesquisaGuia(Guia guia) {
        List<Movimento> result;
        try {
            String textoQuery
                    = "  select m"
                    + "  from Movimento m"
                    + " where m.lote.id = :pid"
                    + "   and m.ativo = 1 order by m.servicos.validade";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", guia.getLote().getId());
            result = qry.getResultList();
        } catch (Exception e) {
            result = new ArrayList();
        }
        return result;
    }

    public List<Object> pesquisaGuiaParaEncaminhamento(Integer id_lote) {
        try {
            String textoQuery
                    = "SELECT mov.id, \n "
                    + "       lot.id, \n "
                    + "       gui.id, \n "
                    + "       ben.id, \n "
                    + "       ben.ds_nome, \n "
                    + "       bai.id, \n "
                    + "       pes_usu.id, \n "
                    + "       pes_usu.ds_nome, \n "
                    + "       ser.id, \n "
                    + "       ser.ds_descricao, \n "
                    + "       ser.is_validade_guias_vigente,                    \n " // 10
                    + "       ser.nr_validade_guia_dias,                        \n " // 11
                    + "       pro.id,                                           \n " // 12
                    + "       pro.ds_descricao,                                 \n " // 13
                    + "       pro.is_validade_guias_mes_vigente,                \n " // 14
                    + "       pro.nr_validade_guias_dias,                       \n " // 15
                    + "       ser.is_validade_guias                             \n " // 16 
                    + "  FROM fin_movimento mov \n "
                    + " INNER JOIN fin_lote lot ON lot.id = mov.id_lote \n "
                    + " INNER JOIN fin_guia gui ON gui.id_lote = lot.id \n "
                    + " INNER JOIN pes_pessoa ben ON ben.id = mov.id_beneficiario \n "
                    + " INNER JOIN fin_servicos ser ON ser.id = mov.id_servicos \n "
                    + " INNER JOIN fin_baixa bai ON bai.id = mov.id_baixa \n "
                    + " INNER JOIN seg_usuario usu_bai ON usu_bai.id = bai.id_usuario \n "
                    + " INNER JOIN pes_pessoa pes_usu ON pes_usu.id = usu_bai.id_pessoa \n "
                    + "  LEFT JOIN est_pedido ped ON ped.id_lote = mov.id_lote AND ped.id_servico = mov.id_servicos\n "
                    + "  LEFT JOIN est_produto pro ON pro.id = ped.id_produto \n "
                    + " WHERE mov.is_ativo = TRUE \n "
                    + "   AND mov.id_lote = " + id_lote + " \n "
                    + " ORDER BY ser.nr_validade_guia_dias ";

            Query qry = getEntityManager().createNativeQuery(textoQuery);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Movimento> pesquisaAcordoAberto(int idAcordo) {
        List<Movimento> result = new ArrayList();
        try {
            String textoQuery
                    = "  select m"
                    + "  from Movimento m"
                    + " where m.acordo.id = :pid"
                    + "   and m.ativo = true"
                    + "   order by m.dtVencimento asc";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", idAcordo);
            result = qry.getResultList();
        } catch (Exception e) {
            result = new ArrayList();
        }
        return result;
    }

    public List<Movimento> pesquisaAcordoPorMovimento(Integer movimento_id) {
        try {
            String queryString
                    = "     SELECT M.*                                          \n"
                    //                    + "            M.id_acordo AS acordo_id,\n"
                    //                    + "            M.ds_referencia AS referencia,\n"
                    //                    + "            SE.ds_descricao AS servico_descricao,\n"
                    //                    + "            M.nr_valor AS valor,\n"
                    //                    + "            M.nr_multa AS multa,\n"
                    //                    + "            M.nr_juros AS juros,\n"
                    //                    + "            M.nr_correcao AS correcao,\n"
                    //                    + "            M.nr_desconto AS desconto,\n"
                    //                    + "            M.nr_valor + M.nr_multa + M.nr_juros + M.nr_correcao - M.nr_desconto AS total\n"
                    + "       FROM fin_movimento AS M                           \n"
                    + "INNER JOIN fin_servicos SE ON SE.id = M.id_servicos      \n"
                    + "     WHERE M.id_acordo IN (SELECT id_acordo              \n"
                    + "                             FROM fin_movimento          \n"
                    + "                            WHERE id = " + movimento_id + ")   \n"
                    + "       AND M.id_tipo_servico <> 4                        \n"
                    + "  ORDER BY SE.ds_descricao,                              \n"
                    + "           RIGHT (M.ds_referencia, 4),                   \n"
                    + "           LEFT(M.ds_referencia, 2);                     \n";
            Query query = getEntityManager().createNativeQuery(queryString, Movimento.class);
            List list = query.getResultList();
            return list;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Movimento> pesquisaAcordoTodos(int idAcordo) {
        // TANTO PARA ACORDO DE ARRECADAÇÃO QUANTO SOCIAL
        try {
            String textoQuery
                    = "  select m "
                    + "    from Movimento m "
                    + "   where m.acordo.id = :pid "
                    + "   order by m.dtVencimento asc";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", idAcordo);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Movimento> pesquisaAcordoParaExclusao(int idAcordo) {
        List<Movimento> result = new ArrayList();
        try {
            String textoQuery
                    = "  select m"
                    + "  from Movimento m"
                    + " where m.acordo.id = :pid"
                    + "   and m.ativo = true";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", idAcordo);
            result = qry.getResultList();
        } catch (Exception e) {
            result = new ArrayList();
        }
        return result;
    }

    public boolean excluirAcordoIn(String ids, int idAcordo) {
        try {

            Query qry = null;
            qry = getEntityManager().createQuery("select m.nrCtrBoleto from Movimento m where m.acordo.id = " + idAcordo + " and m.ativo = true");

            List bole = qry.getResultList();

            String ids_boleto = "";

            for (int i = 0; i < bole.size(); i++) {
                if (ids_boleto.length() > 0 && i != bole.size()) {
                    ids_boleto += ",'" + bole.get(i).toString() + "'";
                } else {
                    ids_boleto = "'" + bole.get(i).toString() + "'";
                }
            }

            if (bole.isEmpty()) {
                return false;
            }

            List lista = new ArrayList();
            lista.add("delete from Historico h where h.movimento.id in ( " + ids + " )");
            lista.add("delete from Cobranca C where C.movimento.id IN ( " + ids + " )");
            lista.add("delete from MensagemCobranca mc where mc.movimento.id in ( " + ids + " ) and mc.movimento.acordo.id = " + idAcordo + " and mc.movimento.ativo = true");
            lista.add("delete from MovimentoBoleto mb where mb.movimento.id in ( " + ids + " )");
            lista.add("delete from Boleto b where b.nrCtrBoleto in (" + ids_boleto + ")");
            lista.add("delete from ImpressaoWeb i where i.movimento.id in (" + ids + ")");
            lista.add("delete from Impressao i where i.movimento.id in (" + ids + ")");
            lista.add("delete from Movimento m where m.acordo.id = " + idAcordo + " and m.ativo = true");
            lista.add("update Movimento m set m.ativo = true, m.acordo = null, m.multa = 0, m.juros = 0, m.correcao = 0 where m.acordo.id = " + idAcordo + "");
            lista.add("delete from Acordo a where a.id = " + idAcordo);

            getEntityManager().getTransaction().begin();

            for (Object query : lista) {
                qry = getEntityManager().createQuery(query.toString());
                qry.executeUpdate();
            }

            qry = getEntityManager().createNativeQuery("delete from fin_lote where id in (select l.id from fin_lote as l left join fin_movimento as m on m.id_lote = l.id where m.id_lote is null);");
            qry.executeUpdate();

            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean excluirAcordoSocialIn(String ids, int idAcordo) {
        try {
            Query qry = getEntityManager().createQuery("select m.nrCtrBoleto from Movimento m where m.acordo.id = " + idAcordo + " and m.ativo = true and m.nrCtrBoleto <> ''");

            List bole = qry.getResultList();

            String ids_boleto = "";

            for (int i = 0; i < bole.size(); i++) {
                if (ids_boleto.length() > 0 && i != bole.size()) {
                    ids_boleto += ",'" + bole.get(i).toString() + "'";
                } else {
                    ids_boleto = "'" + bole.get(i).toString() + "'";
                }
            }

            // SE NÃO TER BOLETO
//            if (bole.isEmpty()) {
//                return false;
//            }
            List lista = new ArrayList();
            lista.add("delete from Historico h where h.movimento.id in ( " + ids + " )");
            if (!bole.isEmpty()) {
                lista.add("delete from Boleto b where b.nrCtrBoleto in (" + ids_boleto + ")");
            }
            lista.add("delete from ImpressaoWeb i where i.movimento.id in (" + ids + ")");
            lista.add("delete from Impressao i where i.movimento.id in (" + ids + ")");
            lista.add("delete from Movimento m where m.acordo.id = " + idAcordo + " and m.ativo = true");
            lista.add("update Movimento m set m.ativo = true, m.acordo = null, m.multa = 0, m.juros = 0, m.correcao = 0 where m.acordo.id = " + idAcordo + "");
            lista.add("delete from Acordo a where a.id = " + idAcordo);

            getEntityManager().getTransaction().begin();

            for (Object query : lista) {
                qry = getEntityManager().createQuery(query.toString());
                qry.executeUpdate();
            }

            qry = getEntityManager().createNativeQuery("delete from fin_lote where id in (select l.id from fin_lote as l left join fin_movimento as m on m.id_lote = l.id where m.id_lote is null);");
            qry.executeUpdate();

            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }
//    public boolean excluirAcordoIn(String ids, int idAcordo){
//        try{
//            List lista = new ArrayList();
//            lista.add("delete from Historico h where h.movimento.id in ( "+ids+" )");
//            lista.add("delete from MensagemCobranca mc where mc.movimento.id in ( "+ids+" ) and mc.movimento.acordo.id = "+idAcordo+" and mc.movimento.ativo = true");
//            lista.add("delete from Boleto b where b.nrCtrBoleto in ( select m.nrCtrBoleto from Movimento m where m.acordo.id = "+idAcordo+" and m.ativo = true )");
//            lista.add("delete from Movimento m where m.acordo.id = "+idAcordo+" and m.ativo = true");
//            lista.add("update Movimento m set m.ativo = true, m.acordo = null, m.multa = 0, m.juros = 0, m.correcao = 0 where m.acordo.id = "+idAcordo+"");
//            lista.add("delete from Acordo a where a.id = "+idAcordo);
//            
//            getEntityManager().getTransaction().begin();
//            
//            Query qry = null;
//            
//            for (int i = 0; i < lista.size(); i++){
//                qry = getEntityManager().createQuery(lista.get(i).toString());
//                qry.executeUpdate();
//            }
//            
//            qry = getEntityManager().createNativeQuery("delete from fin_lote where id in (select l.id from fin_lote as l left join fin_movimento as m on m.id_lote = l.id where m.id_lote is null);");
//            qry.executeUpdate();
//            
//            getEntityManager().getTransaction().commit();
//            
////            Query qry = null;
////            qry = getEntityManager().createNativeQuery("select l.id from fin_lote as l left join fin_movimento as m on m.id_lote = l.id where m.id_lote is null");
////            
////            List l = qry.getResultList();
////            String ids_lote = "";
////            for (int i = 0; i < l.size(); i++){
////                if (ids_lote.length() > 0 && i != l.size())
////                    ids_lote = ids_lote+",";
////                ids_lote = ids_lote + String.valueOf(l.get(i).toString());
////            }
////            
////            lista.add("delete from Lote where id in ("+ids_lote+")");
////            lista.add("delete from Acordo a where a.id = "+idAcordo);            
//            
////            textQuery = "delete from fin_historico where id_movimento in ( select id from fin_movimento where id in ("+ids+")); "
////                        + "" +
////                        "delete from fin_mensagem_cobranca where id_movimento in ( select id from fin_movimento where id in ("+ids+") and id_acordo = "+idAcordo+" and is_ativo is true );"
////                        + "" +
////                        "delete from fin_boleto where nr_ctr_boleto in (select nr_ctr_boleto from fin_movimento where id_acordo = "+idAcordo+" and is_ativo is true);"
////                        + "" +
////                        "delete from fin_movimento where id_acordo = "+idAcordo+" and is_ativo is true;"
////                        + "" +
////                        "update fin_movimento set is_ativo = true, id_acordo = null, nr_multa = 0, nr_juros = 0, nr_correcao = 0 where id_acordo = "+idAcordo+";"
////                        + "" +
////                        "delete from fin_lote where id in (select l.id from fin_lote as l left join fin_movimento as m on m.id_lote = l.id where m.id_lote is null);"
////                        + "" +
////                        "delete from arr_acordo where id = "+idAcordo+";";
//            return true;
//        }catch(Exception e){
//            getEntityManager().getTransaction().rollback();
//            return false;
//        }
//    }

    public boolean pesquisaMatriculaBaixa(int idMatricula) {
        String textQuery = "";
        boolean result = true;
        try {
            textQuery = "SELECT me.id "
                    + "  FROM matr_escola me "
                    + " INNER JOIN fin_evt fe ON (fe.id = me.id_evt) "
                    + " INNER JOIN fin_movimento m ON (m.id_evt = fe.id) "
                    + " INNER JOIN fin_lote l ON (l.id = m.id_lote)"
                    + " WHERE me.id = " + idMatricula + " "
                    + "   AND m.id_lote_baixa is not null ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            Vector vetor = (Vector) qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    result = false;
                }
            }
            return result;
        } catch (Exception e) {
            return result;
        }
    }

    public int inserirBoletoNativo(int id_conta_cobranca) {
        try {
            String textQuery = "INSERT INTO fin_boleto (id_conta_cobranca, is_ativo) values (" + id_conta_cobranca + ", true)";
            Query qry = getEntityManager().createNativeQuery(textQuery);

            getEntityManager().getTransaction().begin();
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();

            textQuery = "select max(id) from fin_boleto";
            qry = getEntityManager().createNativeQuery(textQuery);
            Vector vetor = (Vector) qry.getResultList();
            return (Integer) ((Vector) vetor.get(0)).get(0);
        } catch (Exception e) {
            return -1;
        }
    }

    public List<FormaPagamento> pesquisaFormaPagamento(int id_baixa) {
        try {
            String textoQuery = "  select f "
                    + "  from FormaPagamento f "
                    + " where f.baixa.id = :pid";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", id_baixa);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Movimento> movimentoIdbaixa(int id_baixa) {
        try {
            String textoQuery = " select m "
                    + "   from Movimento m "
                    + "  where m.baixa.id = :pid";
            Query qry = getEntityManager().createQuery(textoQuery);
            qry.setParameter("pid", id_baixa);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Movimento> listaMovimentoBaixaOrder(int id_baixa) {
        try {
            String textoQuery
                    = "   SELECT m.* "
                    + "  FROM fin_movimento m "
                    + " WHERE m.id_baixa = " + id_baixa
                    + " ORDER BY m.id_pessoa, m.id_titular, m.dt_vencimento, m.id_beneficiario";
            Query qry = getEntityManager().createNativeQuery(textoQuery, Movimento.class);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> pesquisaAcrescimo(int id_movimento) {
        // TANTO DE ARRECADAÇÃO QUANTO DE SOCIAL
        try {
            String textQuery
                    = "SELECT m.nr_valor valor,"
                    + "       m.nr_multa multa, "
                    + "       m.nr_juros juros, "
                    + "       m.nr_correcao correcao, "
                    + "       m.nr_desconto desconto, "
                    + "       (m.nr_valor + m.nr_multa + m.nr_juros + m.nr_correcao - m.nr_desconto) vlrpagar "
                    + "  FROM fin_movimento as m "
                    + " WHERE m.id = " + id_movimento;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Movimento> pesquisaMovimentoChaveValor(int id_pessoa, String ref, int id_conta_cobranca, int id_tipo_servico) {
        List vetor;
        List<Movimento> listMov = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select m.id from fin_movimento m "
                    + " inner join pes_pessoa p on (m.id_pessoa = p.id) "
                    + " inner join pes_juridica j on (p.id = j.id_pessoa) "
                    + " inner join fin_boleto b on (b.nr_ctr_boleto = m.nr_ctr_boleto) "
                    + " where j.id_pessoa = " + id_pessoa
                    + "   and m.id_servicos = 1 "
                    + "   and m.is_ativo is true"
                    + "   and m.id_baixa is null"
                    + "   AND m.nr_ctr_boleto IS NOT NULL AND m.nr_ctr_boleto <> ''" // nova verificação 30/08/2017
                    + "   and m.ds_referencia = '" + ref + "'"
                    + "   and b.id_conta_cobranca = " + id_conta_cobranca
                    + "   and m.id_tipo_servico = " + id_tipo_servico;
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    listMov.add(pesquisaCodigo((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }
            return listMov;
        } catch (EJBQLException e) {

            return listMov;
        }
    }

    public Double funcaoJuros(int id_pessoa, int id_servico, int id_tipo_servico, String referencia) {
        List vetor;
        double result = 0;
        String textQuery = "";
        try {
            textQuery = "select func_juros_sm(" + id_pessoa + "," + id_servico + "," + id_tipo_servico + ",'" + referencia + "')";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    if (((Vector) vetor.get(i)).get(0) != null) {
                        result = (Double) ((Vector) vetor.get(i)).get(0);
                    }
                }
            }
            return result;
        } catch (EJBQLException e) {
            return result;
        }
    }

    public Double funcaoMulta(int id_pessoa, int id_servico, int id_tipo_servico, String referencia) {
        List vetor;
        double result = 0;
        String textQuery = "";
        try {
            textQuery = "select func_multa_sm(" + id_pessoa + "," + id_servico + "," + id_tipo_servico + ",'" + referencia + "')";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    if (((Vector) vetor.get(i)).get(0) != null) {
                        result = (Double) ((Vector) vetor.get(i)).get(0);
                    }
                }
            }
            return result;
        } catch (EJBQLException e) {
            return result;
        }
    }

    public Double funcaoCorrecao(int id_pessoa, int id_servico, int id_tipo_servico, String referencia) {
        List vetor;
        double result = 0;
        String textQuery = "";
        try {
            textQuery = "select func_correcao_sm(" + id_pessoa + "," + id_servico + "," + id_tipo_servico + ",'" + referencia + "')";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                for (int i = 0; i < vetor.size(); i++) {
                    if (((Vector) vetor.get(i)).get(0) != null) {
                        result = (Double) ((Vector) vetor.get(i)).get(0);
                    }
                }
            }
            return result;
        } catch (EJBQLException e) {
            return result;
        }
    }

    public List movimentosBaixadosPorEvt(int idEvt) {
        try {
            Query query = getEntityManager().createQuery(" SELECT M FROM Movimento AS M WHERE M.lote.evt.id = :idEvt AND M.baixa.id > 0");
            query.setParameter("idEvt", idEvt);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Movimento pesquisaMovimentosAcordado(int idPessoa, String idRef, int idTipoServ, int idServicos) {
        int i = 0;
        String texto = "";

        try {
            texto = "select mov "
                    + "  from Movimento mov            "
                    + " where mov.referencia = :r      "
                    + "   and mov.tipoServico.id = :t  "
                    + "   and mov.servicos.id = :s     "
                    + "   and mov.pessoa.id  = :p      "
                    + "   and mov.ativo = false "
                    + "   and mov.acordo is not null";
            Query qry = getEntityManager().createQuery(texto);
            qry.setParameter("r", idRef);
            qry.setParameter("t", idTipoServ);
            qry.setParameter("s", idServicos);
            qry.setParameter("p", idPessoa);
            return (Movimento) qry.getSingleResult();
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * @param pessoa
     * @param vencimento ( Caso a data vencimento seja definida como null, será
     * passado o parâmetro )
     * @return List<Movimento> (Lista movimentos em débito da pessoa)
     */
    public List<Movimento> listaDebitoPessoa(Pessoa pessoa, Date vencimento) {
        String queryString;
        if (vencimento == null) {
            queryString = " current_date ";
        } else {
            queryString = "'" + vencimento + "'";
        }
        try {
            Query qry = getEntityManager().createQuery("SELECT MOV FROM Movimento AS MOV WHERE MOV.pessoa.id = :idPessoa AND MOV.dtVencimento < :vencimento AND MOV.ativo = TRUE AND MOV.baixa IS NULL");
            qry.setParameter("idPessoa", pessoa.getId());
            qry.setParameter("vencimento", queryString);
            if (!qry.getResultList().isEmpty()) {
                return qry.getResultList();
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * @param pessoa
     * @param vencimento ( Caso a data vencimento seja definida como null, será
     * passado o parâmetro )
     * @return boolean (true -> se existe débito / false -> caso não exista)
     */
    public boolean existeDebitoPessoa(Pessoa pessoa, Date vencimento) {
        String queryString;
        if (vencimento == null) {
            queryString = " current_date ";
        } else {
            queryString = "'" + vencimento + "'";
        }
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "          SELECT id                                         "
                    + "          FROM fin_movimento                             "
                    + "         WHERE id_pessoa = " + pessoa.getId() + "        "
                    + "   AND dt_vencimento < " + queryString + "           "
                    + "   AND is_ativo = TRUE                           "
                    + "   AND id_baixa IS NULL                          "
                    + " LIMIT 1                                         ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public List<HistoricoEmissaoGuias> pesquisaHistoricoEmissaoGuias(int id_usuario) {
        try {
            //Query qry = getEntityManager().createQuery("select heg from HistoricoEmissaoGuias heg where heg.usuario.id = "+id_usuario+" and heg.baixado = false");
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT heg.* "
                    + "  FROM soc_historico_emissao_guias heg "
                    + " INNER JOIN fin_movimento m ON m.id = heg.id_movimento "
                    + " WHERE heg.id_usuario = " + id_usuario
                    + "   AND heg.is_baixado = false "
                    + "   AND m.is_ativo = TRUE", HistoricoEmissaoGuias.class
            );
            List<HistoricoEmissaoGuias> list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public HistoricoEmissaoGuias pesquisaHistoricoEmissaoGuiasPorMovimento(int id_usuario, int id_movimento) {
        try {
            Query qry = getEntityManager().createQuery("select heg from HistoricoEmissaoGuias heg where heg.movimento.id = " + id_movimento + " and heg.usuario.id = " + id_usuario + " and heg.baixado = false");
            HistoricoEmissaoGuias result = (HistoricoEmissaoGuias) qry.getSingleResult();
            return result;
        } catch (Exception e) {
            return new HistoricoEmissaoGuias();
        }
    }

    public Guia pesquisaGuias(int id_lote) {
        try {
            Query qry = getEntityManager().createQuery("select g from Guia g where g.lote.id = " + id_lote);
            qry.setMaxResults(1);
            Guia result = (Guia) qry.getSingleResult();
            return result;
        } catch (Exception e) {
            return new Guia();
        }
    }

    public List<Movimento> pesquisaMovimentoCadastrado(String documento) {
        String text_qry
                = "SELECT m "
                + "  FROM Movimento m "
                + " WHERE m.baixa.documentoBaixa LIKE '%" + documento + "%'";
        try {
            Query qry = getEntityManager().createQuery(text_qry);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Impressao> listaImpressao(int id_movimento) {
        try {
            Query qry = getEntityManager().createQuery("SELECT I FROM Impressao I WHERE I.movimento.id = :id_movimento ORDER BY I.dtImpressao DESC, I.dtVencimento ASC");
            qry.setParameter("id_movimento", id_movimento);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<ImpressaoWeb> listaImpressaoWeb(int id_movimento) {
        try {
            Query qry = getEntityManager().createQuery("SELECT IW FROM ImpressaoWeb IW WHERE IW.movimento.id = :id_movimento ORDER BY IW.data DESC, IW.hora ASC");
            qry.setParameter("id_movimento", id_movimento);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Movimento> listaMovimentoBeneficiarioServicoPeriodoAtivo(int id_beneficiario, int id_servico, int periodo_dias, boolean socio) {
        // LISTA TODOS MOVIMENTOS ATIVOS EM QUE O BENEFICIÁRIO id_beneficiario E A DATA ESTEJA ENTRE OS ULTIMOS periodo_dias
        String where;

        if (socio) {
            where = " WHERE m.id_matricula_socios = " + id_beneficiario;
        } else {
            where = " WHERE m.id_beneficiario = " + id_beneficiario;
        }

        String text_qry
                = "SELECT m.* "
                + "  FROM fin_movimento m "
                + " INNER JOIN fin_lote l ON l.id = m.id_lote "
                + where
                + "   AND m.is_ativo = true "
                + "   AND m.id_servicos = " + id_servico
                + "   AND (l.dt_emissao >= current_date - " + periodo_dias + " AND l.dt_emissao <= current_date) "
                + " ORDER BY l.dt_emissao ";
        try {
            Query qry = getEntityManager().createNativeQuery(text_qry, Movimento.class);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Movimento> listaMovimentoBeneficiarioServicoMesVigente(int id_beneficiario, int id_servico, boolean socio) {
        return listaMovimentoBeneficiarioServicoMesVigente(id_beneficiario, id_servico, socio, DataHoje.data());
    }

    public List<Movimento> listaMovimentoBeneficiarioServicoMesVigente(int id_beneficiario, int id_servico, boolean socio, String data) {
        // LISTA TODOS MOVIMENTOS ATIVOS EM QUE O BENEFICIÁRIO id_beneficiario E A DATA ESTEJA ENTRE O MES ATUAL
        String where;
        DataHoje dh = new DataHoje();
        if (socio) {
            where = " WHERE m.id_matricula_socios = " + id_beneficiario;
        } else {
            where = " WHERE m.id_beneficiario = " + id_beneficiario;
        }

        String text_qry
                = "SELECT m.* "
                + "  FROM fin_movimento m "
                + " INNER JOIN fin_lote l ON l.id = m.id_lote "
                + where
                + "   AND m.is_ativo = true "
                + "   AND m.id_servicos = " + id_servico
                + "   AND (l.dt_emissao >= '" + dh.primeiroDiaDoMes(data) + "' AND l.dt_emissao <= '" + dh.ultimoDiaDoMes(data) + "') "
                + " ORDER BY l.dt_emissao ";
        try {
            Query qry = getEntityManager().createNativeQuery(text_qry, Movimento.class);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    /**
     * Retorna movimentos gerados no período
     *
     * @param id_pessoa
     * @param id_servico
     * @param dias
     * @return
     */
    public List<Movimento> listaMovimentosUltimosDias(Integer id_pessoa, Integer id_servico, Integer dias) {
        return listaMovimentosUltimosDias(id_pessoa, id_servico, null, dias, true, null);

    }

    /**
     * Retorna movimentos gerados no período
     *
     * @param id_pessoa
     * @param id_servico
     * @param id_rotina
     * @param dias
     * @param tipo_pessoa (null ou 0 = id_pessoa; 1 = id_titular; 2
     * id_beneficiario)
     * @return
     */
    public List<Movimento> listaMovimentosUltimosDias(Integer id_pessoa, Integer id_servico, Integer id_rotina, Integer dias, Integer tipo_pessoa) {
        return listaMovimentosUltimosDias(id_pessoa, id_servico, id_rotina, dias, true, tipo_pessoa);

    }

    /**
     * Retorna movimentos gerados no período
     *
     * @param id_pessoa
     * @param id_servico
     * @param id_rotina
     * @param tipo_pessoa (null ou 0 = id_pessoa; 1 = id_titular; 2
     * id_beneficiario)
     * @param dias (dias retroativos) Exemplo 10, trará uma lista de movimentos
     * de acordo com específicado nos ultimos 10 dias
     * @param ativo
     * @return
     */
    public List<Movimento> listaMovimentosUltimosDias(Integer id_pessoa, Integer id_servico, Integer id_rotina, Integer dias, Boolean ativo, Integer tipo_pessoa) {
        String tipo_pessoa_string = "id_pessoa";
        if (tipo_pessoa == null || tipo_pessoa == 0) {
            tipo_pessoa_string = "id_pessoa";
        } else if (tipo_pessoa == 1) {
            tipo_pessoa_string = "id_titular";
        } else if (tipo_pessoa == 2) {
            tipo_pessoa_string = "id_beneficiario";
        }
        String queryString = ""
                + "     SELECT M.*                                                                      "
                + "       FROM fin_movimento AS M                                                       "
                + " INNER JOIN fin_lote      AS L ON L.id = M.id_lote                                   "
                + "      WHERE M.dt_vencimento BETWEEN current_date - " + dias + " AND current_date     "
                + "        AND M." + tipo_pessoa_string + " = " + id_pessoa
                + "        AND M.id_servicos = " + id_servico;
        if (id_rotina != null) {
            queryString += " AND M.is_ativo = " + ativo;
        }
        if (ativo != null) {
            queryString += " AND L.id_rotina = " + id_rotina;
        }
        queryString += " ORDER BY M.dt_vencimento DESC ";
        if (limit != null) {
            queryString += " LIMIT " + limit;
        }
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Movimento.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }

    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Movimentos da pessoa
     *
     * @param pessoa_id
     * @return
     */
    public List<Movimento> findByPessoa(Integer pessoa_id) {
        return findBy("pessoa", pessoa_id, false);
    }

    /**
     * Movimentos do titular
     *
     * @param pessoa_id
     * @return
     */
    public List<Movimento> findByTitular(Integer pessoa_id) {
        return findBy("titular", pessoa_id, false);
    }

    /**
     * Movimentos do beneficiário
     *
     * @param pessoa_id
     * @return
     */
    public List<Movimento> findByBeneficiario(Integer pessoa_id) {
        return findBy("beneficiario", pessoa_id, false);
    }

    /**
     * (força a pesquisa do movimento nas colunas (pessoa)
     *
     * @param pessoa_id
     * @return
     */
    public List<Movimento> findByAllColumnsByPessoa(Integer pessoa_id) {
        return findBy("", pessoa_id, true);
    }

    /**
     *
     * @param column (pessoa, titular, beneficiario)
     * @param pessoa_id
     * @return
     */
    public List<Movimento> findBy(String column, Integer pessoa_id) {
        return findBy(column, pessoa_id, false);
    }

    public List<Movimento> findBy(String column, Integer pessoa_id, Boolean allColumns) {
        return findBy(column, pessoa_id, null, allColumns, null, null);
    }

    /**
     *
     * @param column (pessoa, titular, beneficiario)
     * @param pessoa_id
     * @param servicos_id (caso for trazer movimento por serviço específico)
     * @return
     */
    public List<Movimento> findBy(String column, Integer pessoa_id, Integer servicos_id) {
        return findBy(column, pessoa_id, servicos_id, false, null, null);
    }

    /**
     *
     * @param column (pessoa, titular, beneficiario)
     * @param pessoa_id
     * @param servicos_id (caso for trazer movimento por serviço específico)
     * @param allColumns (força a pesquisa do movimento nas colunas (pessoa)
     * titular, beneficiario)
     * @param ativo (situação do movimento)
     * @param baixado (se movimento foi baixado = true, não false, ou null nada)
     * @return
     */
    public List<Movimento> findBy(String column, Integer pessoa_id, Integer servicos_id, Boolean allColumns, Boolean ativo, Boolean baixado) {
        try {
            List listWhere = new ArrayList();
            Query query;
            String queryString = " "
                    + "-- MovimentoDao()->findBy(...) \n\n"
                    + "SELECT M.*                   \n"
                    + "  FROM fin_movimento AS M    \n";

            if (allColumns != null && allColumns) {
                listWhere.add(
                        "      M.id IN (                                        \n"
                        + "  SELECT M2.id                                       \n "
                        + "    FROM fin_movimento AS M2                         \n "
                        + "   WHERE (M2.id_pessoa = " + pessoa_id + "           \n"
                        + "      OR M2.id_titular = " + pessoa_id + "           \n"
                        + "      OR M2.id_beneficiario = " + pessoa_id + ")     \n"
                        + "GROUP BY M2.id                                       \n"
                        + ")                                                    \n");
            } else {
                listWhere.add("M.id_" + column + " = " + pessoa_id);
            }
            if (servicos_id != null) {
                listWhere.add("M.id_servicos = " + servicos_id);
            }
            if (ativo != null && ativo) {
                listWhere.add("M.is_ativo = " + ativo);
            }
            if (baixado != null) {
                if (baixado) {
                    listWhere.add("M.id_baixa IS NOT NULL");
                } else {
                    listWhere.add("M.id_baixa IS NULL");
                }
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            query = getEntityManager().createNativeQuery(queryString, Movimento.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    /**
     * Data de vencimento: hoje
     *
     * @param pessoa_id
     * @return
     */
    public List findDebitoPessoa(Integer pessoa_id) {
        return findDebitoPessoa(pessoa_id, DataHoje.data());
    }

    /**
     * *
     * Selecionar data vencimento: ??/??/????
     *
     * @param pessoa_id
     * @param vencimento
     * @return
     */
    public List findDebitoPessoa(Integer pessoa_id, String vencimento) {
        try {
            String queryString = ""
                    + "     SELECT M.*                                          \n"
                    + "       FROM fin_movimento M                              \n"
                    + "      WHERE M.id_pessoa = " + pessoa_id + "              \n"
                    + "        AND M.dt_vencimento < '" + vencimento + "'       \n"
                    + "        AND M.is_ativo = true                            \n"
                    + "        AND M.id_baixa IS NULL                           \n"
                    + "        AND M.id_servicos IN(                            \n"
                    + "             SELECT id_servicos                          \n"
                    + "               FROM fin_servico_rotina                   \n"
                    + "              WHERE id_rotina = 4                        \n"
                    + "   )                                                     \n"
                    + "   ORDER BY M.dt_vencimento                              ";
            Query query = getEntityManager().createNativeQuery(queryString, Movimento.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public void insertMovimentoBoleto(Integer id_conta_cobranca, String boleto) {
        String text
                = "INSERT INTO fin_movimento_boleto (id_movimento, id_boleto) \n"
                + "( \n"
                + "SELECT m.id, b.id \n"
                + "  FROM fin_boleto as b \n"
                + " INNER JOIN fin_movimento AS m ON m.nr_ctr_boleto = b.nr_ctr_boleto \n"
                + "  LEFT JOIN fin_movimento_boleto AS mb ON mb.id_boleto = b.id AND mb.id_movimento = m.id \n"
                + " WHERE m.is_ativo = true \n"
                + "   AND mb.id IS NULL \n"
                + "   AND m.id_baixa IS NULL \n"
                + "   AND b.id_conta_cobranca = " + id_conta_cobranca + " \n"
                + "   AND m.ds_documento = '" + boleto + "' \n"
                + " GROUP BY m.id, b.id \n"
                + ");";
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            qry.executeUpdate();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /**
     *
     * @return
     */
    public List existsInconsistenciaBaixa() {
        try {
            String queryString = ""
                    + "SELECT \n"
                    + "P.ds_nome       AS pessoa_nome,\n" // 0
                    + "SE.ds_descricao AS servico_descricao,\n" // 1
                    + "MM.nr_valor     AS movimento_valor,\n" // 2
                    + "BL.id_movimento AS movimento_id,\n" // 3
                    + "B.dt_baixa      AS baixa_data,\n" // 4
                    + "B.id            AS baixa_id,\n" // 5
                    + "C.ds_descricao  AS caixa\n" // 6
                    + "FROM fin_baixa  AS B \n"
                    + "INNER JOIN fin_baixa_log AS BL ON BL.id_baixa = B.id \n"
                    + "INNER JOIN fin_caixa     AS C  ON C.id        = B.id_caixa \n"
                    + "LEFT JOIN fin_movimento  AS MM ON MM.id       = BL.id_movimento \n"
                    + "LEFT JOIN pes_pessoa     AS P  ON P.id        = MM.id_pessoa \n"
                    + "LEFT JOIN fin_servicos   AS SE ON SE.id       = MM.id_servicos \n"
                    + "LEFT JOIN fin_movimento  AS M  ON M.id_baixa  = B.id \n"
                    + "WHERE M.id IS NULL\n"
                    + "AND B.dt_baixa >= '01/01/2017'";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Object> listaMovimentoAgrupadoOrdemBaixa(String movimento_ids) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "  SELECT p.ds_nome AS nome, \n "
                    + "       p.id AS pessoa_id, \n "
                    + "       b.id AS baixa_id,\n "
                    + "       b.dt_baixa AS data_baixa, \n "
                    + "       trim(l.ds_historico_contabil_padrao) || trim(l.ds_historico_contabil) AS historico_contabil, \n "
                    + "       m.ds_es AS es, \n "
                    + "       sum(m.nr_valor_baixa) AS valor_baixa, \n "
                    + "       l.id AS lote_id, \n "
                    + "       ptd.id AS tipo_documento_id, \n "
                    + "       ptd.ds_descricao AS tipo_documento_pessoa, \n "
                    + "       p.ds_documento AS documento_pessoa \n "
                    + "  FROM fin_movimento m \n "
                    + " INNER JOIN pes_pessoa p ON p.id = m.id_pessoa\n "
                    + " INNER JOIN fin_baixa b ON b.id = m.id_baixa \n "
                    + " INNER JOIN fin_lote l ON l.id = m.id_lote \n "
                    + " INNER JOIN pes_tipo_documento ptd ON ptd.id = p.id_tipo_documento \n "
                    + " WHERE m.id IN (" + movimento_ids + ") \n "
                    + "   AND m.id_plano5 NOT IN (SELECT id_plano5 FROM fin_conta_tipo_plano5 WHERE id_conta_tipo = 1) \n "
                    + " GROUP BY p.ds_nome, \n "
                    + "          p.id, \n "
                    + "          b.id,\n "
                    + "          b.dt_baixa, \n "
                    + "          m.ds_es, \n "
                    + "          l.id,"
                    + "          ptd.id, \n "
                    + "          ptd.ds_descricao, \n "
                    + "          p.ds_documento \n "
                    + " ORDER BY p.ds_nome, b.id, l.id"
            );
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Boolean imprimeEncaminhamentoJunto(Integer baixa_id) {

        Query qry = getEntityManager().createNativeQuery(
                "  SELECT count(*) \n "
                + "  FROM fin_guia AS g \n "
                + " INNER JOIN soc_convenio_sub_grupo AS s ON s.id = g.id_convenio_sub_grupo \n "
                + " INNER JOIN fin_movimento AS m ON m.id_lote = g.id_lote \n "
                + " WHERE m.id_baixa = " + baixa_id + " \n "
                + "   AND s.is_encaminhamento = true"
        );

        return ((Long) ((List) qry.getSingleResult()).get(0)).intValue() > 0;
    }

    public List<Movimento> listaMovimentoAcordado(Integer id_pessoa, String referencia, Integer id_tipo_servico, Integer id_servico) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT m.* \n"
                    + "  FROM fin_movimento m \n"
                    + " WHERE m.id_pessoa = " + id_pessoa + " \n"
                    + "   AND m.id_servicos = " + id_servico + " \n"
                    + "   AND m.ds_referencia = '" + referencia + "' \n"
                    + "   AND m.id_tipo_servico = " + id_tipo_servico + " \n"
                    + "   AND m.is_ativo = FALSE \n"
                    + "   AND m.id_acordo > 0", Movimento.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Movimento> listaPorRotinaMatricula(Integer rotina_id, Integer matricula_socios_id) {
        String queryString = " "
                + "    SELECT M.* \n"
                + "      FROM fin_movimento M \n"
                + "INNER JOIN fin_lote L ON L.id = M.id_lote \n"
                + "     WHERE L.id_rotina = " + rotina_id + " \n"
                + "       AND M.id_matricula_socios = " + matricula_socios_id + " \n"
                + "  ORDER BY M.dt_vencimento ASC \n";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString, Movimento.class);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
