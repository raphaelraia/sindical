package br.com.rtools.movimento;

import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.associativo.BoletoNaoBaixado;
import br.com.rtools.financeiro.*;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.MovimentoInativoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

public class GerarMovimento extends DB {

    public Object[] gerarBoletos(String referencia, String vencimento, Integer id_grupo_cidade, Integer id_convencao, Integer id_servico, Integer id_tipo_servico, Integer id_rotina) {
        Object[] message = new Object[2];
        String textQry = "";
        getEntityManager().getTransaction().begin();
        Query qry = null;
        NovoLog log = new NovoLog();
        Integer sizeBoleto = 0;
        List list = new ArrayList();
        try {
            textQry = "select '" + DataHoje.data() + "' as dt_emissao, 'R' as ds_pag_rec, 0 as nr_valor, '" + DataHoje.data() + "' as dt_lancamento, 1 as id_filial, cv.id_pessoa, 2 as id_tipo_documento, 4 as id_rotina, false as is_avencer_contabil " + "   from arr_contribuintes_vw cv "
                    + "   left join fin_bloqueia_servico_pessoa as sp on sp.id_pessoa = cv.id_pessoa and sp.id_servicos = 3 and '04/11/2014' >= sp.dt_inicio and '04/11/2014' <= sp.dt_fim   "
                    + "  where cv.dt_inativacao is null and cv.id_grupo_cidade = " + id_grupo_cidade + " and cv.id_convencao = " + id_convencao + " and cv.id_pessoa not in "
                    + "       (select id_pessoa from fin_movimento where ds_referencia='" + referencia + "' and id_servicos = " + id_servico + " and id_tipo_servico = " + id_tipo_servico + " and is_ativo = true) "
                    + " and (sp.is_geracao = true or sp.is_geracao is null) ";
            qry = getEntityManager().createNativeQuery(textQry);
            qry.setMaxResults(1);
            list = qry.getResultList();
            if (list.isEmpty()) {
                getEntityManager().getTransaction().rollback();
                message[0] = 0;
                message[1] = "Não existem registros a serem processados!";
                return message;
            }
            /* INSERÇÃO DE LOTE ***/
            textQry = "INSERT INTO fin_lote (dt_emissao, ds_pag_rec, nr_valor, dt_lancamento, id_filial, id_pessoa, id_tipo_documento, id_rotina, is_avencer_contabil) \n"
                    + " (  SELECT '" + DataHoje.data() + "' AS dt_emissao,      \n"
                    + "           'R' AS ds_pag_rec,                            \n"
                    + "           0  AS nr_valor,                               \n"
                    + "           '" + DataHoje.data() + "' AS dt_lancamento,   \n"
                    + "           1  AS id_filial,                              \n"
                    + "           cv.id_pessoa,                                 \n"
                    + "           2 AS id_tipo_documento,                       \n"
                    + "           4 AS id_rotina,                               \n"
                    + "           false AS is_avencer_contabil                  \n"
                    + "      FROM arr_contribuintes_vw AS cv                    \n"
                    + " LEFT JOIN fin_bloqueia_servico_pessoa AS sp ON sp.id_pessoa = cv.id_pessoa          \n"
                    + "       AND sp.id_servicos = 3                            \n"
                    + "       AND '04/11/2014' >= sp.dt_inicio                  \n"
                    + "       AND '04/11/2014' <= sp.dt_fim                     \n"
                    + "     WHERE cv.dt_inativacao IS NULL                      \n"
                    + "       AND cv.id_grupo_cidade = " + id_grupo_cidade + "  \n"
                    + "       AND cv.id_convencao = " + id_convencao + "        \n"
                    + "       AND cv.id_pessoa NOT IN                           \n"
                    + "         (SELECT id_pessoa FROM fin_movimento WHERE ds_referencia='" + referencia + "' \n"
                    + "             AND id_servicos = " + id_servico + "        \n"
                    + "             AND id_tipo_servico = " + id_tipo_servico + "\n"
                    + "             AND is_ativo = true                         \n"
                    + "         )                                               \n"
                    + "       AND (sp.is_geracao IS TRUE OR sp.is_geracao IS NULL));";
            qry = getEntityManager().createNativeQuery(textQry);
            if (qry.executeUpdate() <= 0) {
                getEntityManager().getTransaction().rollback();
                message[0] = 1;
                message[1] = "Erro ao gravar lote!";
                return message;
            }
            log.save("Geracao geral: FIN_LOTE - Data: " + DataHoje.data() + " id_grupo_cidade: " + id_grupo_cidade + " id_convencao: " + id_convencao + " id_servico: " + id_servico + " referencia: " + referencia);
            /* ---------------- ***/
 /* ---------------- ***/

 /* INSERÇÃO DE MOVIMENTO */
            textQry = "INSERT INTO fin_movimento (ds_referencia, ds_es, ds_documento, nr_valor, dt_vencimento_original, dt_vencimento, nr_ctr_boleto, id_pessoa, id_tipo_documento, id_tipo_servico, id_titular, id_servicos, id_beneficiario, id_lote, is_ativo, is_obrigacao,nr_multa,nr_desconto,nr_taxa,nr_quantidade, "
                    + "nr_valor_baixa, nr_repasse_automatico, nr_correcao, nr_desconto_ate_vencimento, nr_juros, id_plano5) \n"
                    + "(    SELECT '" + referencia + "' AS ds_referencia,           \n"
                    + "            'E' AS ds_es,                                    \n"
                    + "            NULL AS ds_documento,                            \n"
                    + "            0 AS nr_valor,                                   \n"
                    + "            '" + vencimento + "' AS dt_vencimento_original,  \n"
                    + "            '" + vencimento + "' AS dt_vencimento,           \n"
                    + "            NULL AS nr_ctr_boleto,                           \n"
                    + "            c.id_pessoa,                                     \n"
                    + "            2 AS id_tipo_documento,                          \n"
                    + "            " + id_tipo_servico + " AS id_tipo_servico,      \n"
                    + "            c.id_pessoa as id_titular,                       \n"
                    + "            " + id_servico + " AS id_servicos,               \n"
                    + "            c.id_pessoa AS id_beneficiario,                  \n"
                    + "            l.id AS id_lote,                                 \n"
                    + "            true AS is_ativo,                                \n"
                    + "            true AS is_obrigacao,                            \n"
                    + "            0 AS nr_multa,                                   \n"
                    + "            0 AS nr_desconto,                                \n"
                    + "            0 AS nr_taxa,                                    \n"
                    + "            1 AS nr_quantidade,                              \n"
                    + "            0 AS nr_valor_baixa,                             \n"
                    + "            0 AS nr_repasse_automatico,                      \n"
                    + "            0 AS nr_correcao,                                \n"
                    + "            0 AS nr_desconto_ate_vencimento,                 \n"
                    + "            0 AS nr_juros,                                   \n"
                    + "            se.id_plano5 AS id_plano5                        \n"
                    + "       FROM arr_contribuintes_vw AS c                        \n"
                    + " INNER JOIN fin_lote AS l ON l.id_pessoa = c.id_pessoa       \n"
                    + "  LEFT JOIN fin_movimento AS m on m.id_lote = l.id           \n"
                    + "  LEFT JOIN fin_bloqueia_servico_pessoa AS sp ON sp.id_pessoa = c.id_pessoa  \n"
                    + "        AND sp.id_servicos = " + id_servico + "                              \n"
                    + "        AND '" + vencimento + "' >= sp.dt_inicio                             \n"
                    + "        AND '" + vencimento + "' <= sp.dt_fim                                \n"
                    + " INNER JOIN fin_servicos AS se ON se.id = " + id_servico + "                 \n"
                    + "      WHERE m.id_lote IS NULL                                                \n"
                    + "        AND c.id_grupo_cidade = " + id_grupo_cidade + "                      \n"
                    + "        AND c.id_convencao = " + id_convencao + "                            \n"
                    + "        AND (sp.is_geracao = TRUE OR sp.is_geracao IS NULL )                 \n"
                    + "        AND c.dt_inativacao IS NULL )";
            qry = getEntityManager().createNativeQuery(textQry);
            if (qry.executeUpdate() <= 0) {
                getEntityManager().getTransaction().rollback();
                message[0] = 1;
                message[1] = "Erro ao gravar movimento!";
                return message;
            }
            String queryString = ""
                    + "     SELECT m.id                                                                 \n"
                    + "       FROM fin_movimento AS m                                                   \n"
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote                                    \n"
                    + " INNER JOIN fin_servico_conta_cobranca AS scc ON scc.id_servicos = m.id_servicos \n"
                    + "        AND scc.id_tipo_servico = m.id_tipo_servico                              \n"
                    + "      WHERE l.id_rotina = 4                                                      \n"
                    + "        AND m.nr_ctr_boleto IS NULL                                              \n"
                    + "        AND m.id_servicos > 0                                                    \n"
                    + "        AND m.id_servicos IS NOT NULL                                            \n"
                    + "        AND m.is_ativo = true";

            qry = getEntityManager().createNativeQuery(queryString);
            list = qry.getResultList();
            if (!list.isEmpty()) {
                sizeBoleto = list.size();
            }
            log.save("Geracao geral: FIN_MOVIMENTO - Data: " + DataHoje.data());
            /* ------------------------ ***/
 /* ------------------------ ***/
            Integer count = 0;
            /* INSERÇÃO DE BOLETO */
//            for (int i = 0; i < 500; i++) {
//                queryString = ""
//                        + "     SELECT m.id                                             \n"
//                        + "       FROM fin_movimento AS m                               \n"
//                        + " INNER JOIN fin_lote AS l ON l.id = m.id_lote                \n"
//                        + " INNER JOIN fin_servico_conta_cobranca AS scc ON scc.id_servicos = m.id_servicos \n"
//                        + "        AND scc.id_tipo_servico = m.id_tipo_servico                              \n"
//                        + "      WHERE l.id_rotina = 4                                                      \n"
//                        + "        AND m.nr_ctr_boleto IS NULL                                              \n"
//                        + "        AND m.id_servicos > 0                                                    \n"
//                        + "        AND m.id_servicos IS NOT NULL                                            \n"
//                        + "        AND m.is_ativo = true                                                    \n"
//                        + "      LIMIT 50";
//                qry = getEntityManager().createNativeQuery(queryString);
//                List listResult = qry.getResultList();
//
//                Integer size = listResult.size();
//                count += size;
//                if (size == 0) {
//                    break;
//                }

//                textQry = "INSERT INTO fin_boleto (nr_ctr_boleto, is_ativo, id_conta_cobranca)              \n"
//                        + "(    SELECT m.id AS nr_ctr_boleto,                                               \n"
//                        + "            true AS is_ativo,                                                    \n"
//                        + "            scc.id_conta_cobranca                                                \n"
//                        + "       FROM fin_movimento AS m                                                   \n"
//                        + " INNER JOIN fin_lote AS l ON l.id = m.id_lote                                    \n"
//                        + " INNER JOIN fin_servico_conta_cobranca AS scc ON scc.id_servicos = m.id_servicos \n"
//                        + "        AND scc.id_tipo_servico = m.id_tipo_servico                              \n"
//                        + "      WHERE l.id_rotina = 4                                                      \n"
//                        + "        AND m.nr_ctr_boleto IS NULL                                              \n"
//                        + "        AND m.id_servicos > 0                                                    \n"
//                        + "        AND m.id_servicos IS NOT NULL                                            \n"
//                        + "        AND m.is_ativo = true                                                    \n"
//                        //+ "      LIMIT " + size + "                                                                  \n"
//                        + ");";
//                qry = getEntityManager().createNativeQuery(textQry);
//                if (qry.executeUpdate() <= 0) {
//                    getEntityManager().getTransaction().rollback();
//                    message[0] = 1;
//                    message[1] = "Erro ao gravar boleto!";
//                    return message;
//                }
//                /* ---------------------- ***/
//                /* ---------------------- ***/
//
//                /* ATUALIZAÇÃO DE MOVIMENTO */
//                textQry = "  UPDATE fin_movimento                                                                    \n"
//                        + "     SET nr_ctr_boleto = text(fin_movimento.id), ds_documento = ds_boleto FROM fin_boleto \n"
//                        + "   WHERE text(fin_movimento.id) = fin_boleto.nr_ctr_boleto                                \n"
//                        + "     AND (fin_movimento.nr_ctr_boleto IS NULL OR length(fin_movimento.nr_ctr_boleto) = 0) \n";
//                qry = getEntityManager().createNativeQuery(textQry);
//                if (qry.executeUpdate() <= 0) {
//                    getEntityManager().getTransaction().rollback();
//                    message[0] = 1;
//                    message[1] = "Erro ao atualizar movimentos!";
//                    return message;
//                }
//                GenericaMensagem.info("Gerados...", sizeBoleto + " registros");
//                PF.update("form_contribuicao_poll");
//            }
            textQry = "INSERT INTO fin_boleto (nr_ctr_boleto, is_ativo, id_conta_cobranca)              \n"
                    + "(    SELECT m.id AS nr_ctr_boleto,                                               \n"
                    + "            true AS is_ativo,                                                    \n"
                    + "            scc.id_conta_cobranca                                                \n"
                    + "       FROM fin_movimento AS m                                                   \n"
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote                                    \n"
                    + " INNER JOIN fin_servico_conta_cobranca AS scc ON scc.id_servicos = m.id_servicos \n"
                    + "        AND scc.id_tipo_servico = m.id_tipo_servico                              \n"
                    + "      WHERE l.id_rotina = 4                                                      \n"
                    + "        AND m.nr_ctr_boleto IS NULL                                              \n"
                    + "        AND m.id_servicos > 0                                                    \n"
                    + "        AND m.id_servicos IS NOT NULL                                            \n"
                    + "        AND m.is_ativo = true                                                    \n"
                    + ");";
            qry = getEntityManager().createNativeQuery(textQry);
            if (qry.executeUpdate() <= 0) {
                getEntityManager().getTransaction().rollback();
                message[0] = 1;
                message[1] = "Erro ao gravar boleto!";
                return message;
            }
            /* ---------------------- ***/
 /* ---------------------- ***/

 /* ATUALIZAÇÃO DE MOVIMENTO */
            textQry = "  UPDATE fin_movimento                                                                    \n"
                    + "     SET nr_ctr_boleto = text(fin_movimento.id), ds_documento = ds_boleto FROM fin_boleto \n"
                    + "   WHERE text(fin_movimento.id) = fin_boleto.nr_ctr_boleto                                \n"
                    + "     AND (fin_movimento.nr_ctr_boleto IS NULL OR length(fin_movimento.nr_ctr_boleto) = 0) \n";
            qry = getEntityManager().createNativeQuery(textQry);
            if (qry.executeUpdate() <= 0) {
                getEntityManager().getTransaction().rollback();
                message[0] = 1;
                message[1] = "Erro ao atualizar movimentos!";
                return message;
            }

            log.save("Geracao geral: FIN_BOLETO - Data: " + DataHoje.data());
            log.save("Geracao geral: atualiza FIN_MOVIMENTO - Data: " + DataHoje.data());
            /* ---------------------- ***/
 /* ---------------------- ***/

 /* INSERÇÃO DE MENSAGEM COBRANÇA */
            textQry = "INSERT INTO fin_mensagem_cobranca (id_mensagem_convencao,id_movimento)           \n"
                    + "     (SELECT mc.id, m.id FROM fin_movimento AS m                                 \n"
                    + "  INNER JOIN arr_contribuintes_vw AS c ON c.id_pessoa = m.id_pessoa              \n"
                    + "  INNER JOIN arr_mensagem_convencao AS mc ON mc.ds_referencia = m.ds_referencia  \n"
                    + "         AND mc.id_servicos = m.id_servicos                                      \n"
                    + "         AND mc.id_tipo_servico = m.id_tipo_servico                              \n"
                    + "         AND mc.id_convencao = c.id_convencao                                    \n"
                    + "         AND mc.id_grupo_cidade = c.id_grupo_cidade                              \n"
                    + "   LEFT JOIN fin_mensagem_cobranca AS mco ON m.id = mco.id_movimento             \n"
                    + "       WHERE mco.id_movimento IS NULL                                            \n"
                    + "         AND m.is_ativo = true                                                   \n"
                    + "         AND m.id_baixa IS NULL                                                  \n"
                    + ");";
            qry = getEntityManager().createNativeQuery(textQry);
            if (qry.executeUpdate() <= 0) {
                getEntityManager().getTransaction().rollback();
                message[0] = 1;
                message[1] = "Erro ao gravar mensagem cobrança!";
                return message;
            }
            log.save("Geracao geral: FIN_MENSAGEM_COBRANCA - Data: " + DataHoje.data());
            /* ---------------------- ***/
        } catch (Exception e) {
            log.save("Geracao geral: ERRO - Data: " + DataHoje.data() + " " + e.getMessage());
            getEntityManager().getTransaction().rollback();
            message[0] = 1;
            message[1] = "Erro no processo de criação, verifique os logs! (" + sizeBoleto + " movimentos) ";
            return message;
        }
        getEntityManager().getTransaction().commit();
        message[0] = 0;
        message[1] = "Gerado com sucesso! (" + sizeBoleto + " movimentos) ";
        GenericaMensagem.info("Gerados...", sizeBoleto + " registros");
        return message;
    }

    public static boolean salvarListaMovimento(List<Movimento> listaMovimento) {
        return false;
    }

    public static synchronized String salvarListaAcordo(Acordo acordo, List<Movimento> listaMovimento, List<Movimento> listaAcordados, List<String> listaHistorico) {
        Dao dao = new Dao();
        CnaeConvencaoDao dbco = new CnaeConvencaoDao();
        GrupoCidadesDao dbgc = new GrupoCidadesDao();
        ContaCobrancaDao dbc = new ContaCobrancaDao();
        NovoLog log = new NovoLog();
        Boleto boleto = new Boleto();
        MensagemConvencao mc = new MensagemConvencao();
        MensagemConvencaoDao dbm = new MensagemConvencaoDao();

        MovimentoDao db = new MovimentoDao();
        for (int i = 0; i < listaMovimento.size(); i++) {
            if (listaMovimento.get(i).getPessoa().getId() != 0) {
                Convencao convencao = dbco.pesquisarCnaeConvencaoPorPessoa(listaMovimento.get(i).getPessoa().getId());
                if (convencao == null) {
                    return "Convenção não encontrada!";
                }

                mc = dbm.verificaMensagem(convencao.getId(),
                        listaMovimento.get(i).getServicos().getId(),
                        listaMovimento.get(i).getTipoServico().getId(),
                        dbgc.grupoCidadesPorPessoa(listaMovimento.get(i).getPessoa().getId(),
                                convencao.getId()).getId(),
                        "");
                if (mc == null) {
                    return "Mensagem de cobrança não encontrada";
                }
            }
            ContaCobranca cc = dbc.pesquisaServicoCobranca(listaMovimento.get(i).getServicos().getId(), listaMovimento.get(i).getTipoServico().getId());
            int id_boleto = db.inserirBoletoNativo(cc.getId());

            if (id_boleto != -1) {
                dao.openTransaction();
                if (listaMovimento.get(i).getId() == -1) {
                    // LOTE ---
                    Lote lote = new Lote();
                    lote.setDepartamento(null);
                    lote.setStatus((FStatus) dao.find(new FStatus(), 1));
                    lote.setLancamento(DataHoje.data());
                    lote.setAvencerContabil(false);
                    lote.setEmissao(DataHoje.data());
                    lote.setFTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 2));
                    lote.setValor(listaMovimento.get(i).getValor());
                    lote.setRotina((Rotina) dao.find(new Rotina(), 4));
                    lote.setPessoa(listaMovimento.get(i).getPessoa());
                    lote.setCondicaoPagamento((CondicaoPagamento) dao.find(new CondicaoPagamento(), 1));
                    lote.setFilial((Filial) dao.find(new Filial(), 1));
                    lote.setPessoaSemCadastro(null);
                    lote.setEvt(null);
                    lote.setPlano5(null);
                    lote.setDocumento("");

                    if (cc == null) {
                        dao.rollback();
                        return "Conta cobrança não encontrada!";
                    }
                    if (dao.save(lote)) {
                        log.save("Salvar Lote - ID: " + lote.getId() + " Pessoa: " + lote.getPessoa().getNome() + " Data: " + lote.getEmissao());
                    } else {
                        dao.rollback();
                        return "Erro ao salvar Lote, verifique os logs!";
                    }

                    // ACORDO ----
                    acordo.setUsuario((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
                    if (dao.save(acordo)) {
                        log.save("Salvar Acordo - ID: " + acordo.getId() + " Usuario: " + acordo.getUsuario().getPessoa().getNome());
                    } else {
                        dao.rollback();
                        return "Erro ao salvar acordo, verifique os logs!";
                    }

                    // MOVIMENTO ----
                    listaMovimento.get(i).setLote(lote);
                    if (dao.save(listaMovimento.get(i))) {
                        // BOLETO ---
                        boleto = (Boleto) dao.find(new Boleto(), id_boleto);
                        boleto.setContaCobranca(cc);

                        // SE AGRUPA FOR TRUE** NR_CTR_BOLETO = ID_PESSOA + FATOR DE VENCIMENTO
                        if (listaMovimento.get(i).getServicos().isAgrupaBoleto()) {
                        } else {
                            boleto.setNrCtrBoleto(String.valueOf(listaMovimento.get(i).getId()));

                            listaMovimento.get(i).setDocumento(boleto.getBoletoComposto());
                            listaMovimento.get(i).setNrCtrBoleto(boleto.getNrCtrBoleto());
                            listaMovimento.get(i).setAcordo(acordo);
                            if (!dao.update(listaMovimento.get(i))) {
                                dao.rollback();
                                return "Erro ao atualizar movimento, verifique os logs!";
                            }
                            if (!dao.update(boleto)) {
                                dao.rollback();
                                return "Erro ao atualizar boleto, verifique os logs!";
                            }

                            if (listaMovimento.get(i).getPessoa().getId() != 0) {
                                if (!dao.save(new MensagemCobranca(-1, listaMovimento.get(i), mc))) {
                                    dao.rollback();
                                    return "Erro ao salvar mensagem Cobrançam, verifique os logs!";
                                }
                            }
                        }

                        log.save("Salvar Movimento - ID: " + listaMovimento.get(i).getId() + " Pessoa: " + listaMovimento.get(i).getPessoa().getNome() + " Valor: " + listaMovimento.get(i).getValor());
                    } else {
                        dao.rollback();
                        return "Erro ao salvar movimento, verifique os logs!";
                    }

                    // MOVIMENTO ACORDADOS ----
                    for (int wi = 0; wi < listaAcordados.size(); wi++) {
                        listaAcordados.get(wi).setAcordo(acordo);
                        listaAcordados.get(wi).setAtivo(false);
                        listaAcordados.get(wi).setValorBaixa(0);
                        if (!dao.update(listaAcordados.get(wi))) {
                            dao.rollback();
                            return "Erro ao salvar boletos acordados!";
                        }
                    }

                    // HISTORICO ----
                    Historico his = new Historico();

                    his.setMovimento(listaMovimento.get(i));
                    his.setComplemento("");
                    his.setHistorico(listaHistorico.get(i));
                    if (dao.save(his)) {
                        log.save("Salvar Historico - ID: " + his.getId() + " OBS: " + his.getHistorico() + " ID_MOVIMENTO: " + his.getMovimento().getId());
                    } else {
                        dao.rollback();
                        return "Erro ao salvar histórico, verifique os logs!";
                    }
                } else {
                    dao.rollback();
                    return "Id do movimento deve ser -1";
                }
                dao.commit();
                listaAcordados.clear();
            } else {
                return "Erro ao salvar boleto, verifique os logs!";
            }
        }
        return "";
    }

    public static synchronized String salvarListaAcordoSocial(Acordo acordo, List<Movimento> listaMovimento, List<Movimento> listaAcordados, List<String> listaHistorico) {
        Dao dao = new Dao();
        ContaCobrancaDao dbc = new ContaCobrancaDao();
        NovoLog log = new NovoLog();

        dao.openTransaction();

        // ACORDO ----
        acordo.setUsuario((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
        if (dao.save(acordo)) {
            log.save("Salvar Acordo - ID: " + acordo.getId() + " Usuario: " + acordo.getUsuario().getPessoa().getNome());
        } else {
            return "Erro ao salvar acordo, verifique os logs!";
        }

        for (int i = 0; i < listaMovimento.size(); i++) {
            ContaCobranca cc = dbc.pesquisaServicoCobranca(listaMovimento.get(i).getServicos().getId(), listaMovimento.get(i).getTipoServico().getId());

            if (cc == null) {
                return "Conta cobrança não encontrada!";
            }

            if (listaMovimento.get(i).getId() == -1) {
                // LOTE ---
                Lote lote = new Lote();
                lote.setDepartamento(null);
                lote.setStatus((FStatus) dao.find(new FStatus(), 1));
                lote.setLancamento(DataHoje.data());
                lote.setAvencerContabil(false);
                lote.setEmissao(DataHoje.data());
                lote.setFTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 2));
                lote.setValor(listaMovimento.get(i).getValor());
                lote.setRotina((Rotina) dao.find(new Rotina(), 118));
                lote.setPessoa(listaMovimento.get(i).getPessoa());
                lote.setCondicaoPagamento((CondicaoPagamento) dao.find(new CondicaoPagamento(), 1));
                lote.setFilial((Filial) dao.find(new Filial(), 1));
                lote.setPessoaSemCadastro(null);
                lote.setEvt(null);
                lote.setPlano5(null);
                lote.setDocumento("");

                if (dao.save(lote)) {
                    log.save("Salvar Lote - ID: " + lote.getId() + " Pessoa: " + lote.getPessoa().getNome() + " Data: " + lote.getEmissao());
                } else {
                    dao.rollback();
                    return "Erro ao salvar Lote, verifique os logs!";
                }

                // MOVIMENTO ----
                listaMovimento.get(i).setLote(lote);
                listaMovimento.get(i).setAcordo(acordo);
                if (dao.save(listaMovimento.get(i))) {
                    log.save("Salvar Movimento - ID: " + listaMovimento.get(i).getId() + " Pessoa: " + listaMovimento.get(i).getPessoa().getNome() + " Valor: " + listaMovimento.get(i).getValor());
                } else {
                    dao.rollback();
                    return "Erro ao salvar movimento, verifique os logs!";
                }

                // HISTORICO ----
                Historico his = new Historico();

                his.setMovimento(listaMovimento.get(i));
                his.setComplemento("");
                his.setHistorico(listaHistorico.get(i));
                if (dao.save(his)) {
                    log.save("Salvar Historico - ID: " + his.getId() + " OBS: " + his.getHistorico() + " ID_MOVIMENTO: " + his.getMovimento().getId());
                } else {
                    dao.rollback();
                    return "Erro ao salvar histórico, verifique os logs!";
                }
            } else {
                dao.rollback();
                return "Id do movimento deve ser -1";
            }
        }

        // MOVIMENTO ACORDADOS ----
        for (int wi = 0; wi < listaAcordados.size(); wi++) {
            listaAcordados.get(wi).setAcordo(acordo);
            listaAcordados.get(wi).setAtivo(false);
            listaAcordados.get(wi).setValorBaixa(0);
            if (!dao.update(listaAcordados.get(wi))) {
                dao.rollback();
                return "Erro ao salvar boletos acordados!";
            }
        }

        dao.commit();
        listaAcordados.clear();
        return "";
    }

    public static boolean salvarUmMovimentoBaixa(Lote lote, Movimento movimento) {
        Dao dao = new Dao();
        ContaCobrancaDao dbc = new ContaCobrancaDao();
        NovoLog log = new NovoLog();
        Boleto boleto = new Boleto();
        MovimentoDao db = new MovimentoDao();

        ContaCobranca cc = dbc.pesquisaServicoCobranca(movimento.getServicos().getId(), movimento.getTipoServico().getId());
        int id_boleto = db.inserirBoletoNativo(cc.getId());

        if (id_boleto != -1) {
            dao.openTransaction();
            if (movimento.getId() == -1) {
                // LOTE ---
                lote.setDepartamento(null);
                lote.setStatus((FStatus) dao.find(new FStatus(), 1));
                lote.setLancamento(DataHoje.data());
                lote.setAvencerContabil(false);
                lote.setEmissao(DataHoje.data());
                lote.setFTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 2));
                lote.setValor(movimento.getValor());
                lote.setRotina((Rotina) dao.find(new Rotina(), 4));
                lote.setPessoa(movimento.getPessoa());
                lote.setCondicaoPagamento((CondicaoPagamento) dao.find(new CondicaoPagamento(), 1));
                lote.setFilial((Filial) dao.find(new Filial(), 1));
                lote.setPessoaSemCadastro(null);
                lote.setEvt(null);
                lote.setPlano5(null);

                if (cc == null) {
                    dao.rollback();
                    return false;
                }
                if (dao.save(lote)) {
                    log.save("Salvar Lote " + " - ID: " + lote.getId() + " Pessoa: " + lote.getPessoa().getNome() + " Data: " + lote.getEmissao());
                } else {
                    dao.rollback();
                    return false;
                }

                // MOVIMENTO ----
                movimento.setLote(lote);
                if (dao.save(movimento)) {
                    // BOLETO ---

                    boleto = (Boleto) dao.find(new Boleto(), id_boleto);
                    boleto.setContaCobranca(cc);

                    // SE AGRUPA FOR TRUE** NR_CTR_BOLETO = ID_PESSOA + FATOR DE VENCIMENTO
                    if (movimento.getServicos().isAgrupaBoleto()) {
                    } else {
                        // SE AGRUPA FOR FALSE** NR_CTR_BOLETO = ID_MOVIMENTO
                        boleto.setNrCtrBoleto(String.valueOf(movimento.getId()));
                        movimento.setDocumento(boleto.getBoletoComposto());
                        movimento.setNrCtrBoleto(boleto.getNrCtrBoleto());

                        if (!dao.update(movimento)) {
                            dao.rollback();
                            return false;
                        }
                        if (!dao.update(boleto)) {
                            dao.rollback();
                            return false;
                        }
                    }

                    log.save("Salvar Movimento - ID: " + movimento.getId() + " Pessoa: " + movimento.getPessoa().getNome() + " Valor: " + movimento.getValor());
                } else {
                    dao.rollback();
                    return false;
                }
            } else {
                dao.rollback();
                return false;
            }
            dao.commit();
        } else {
            return false;
        }
        return true;
    }

    public static boolean salvarUmMovimento(Lote lote, Movimento movimento) {
        Dao dao = new Dao();
        CnaeConvencaoDao dbco = new CnaeConvencaoDao();
        GrupoCidadesDao dbgc = new GrupoCidadesDao();
        ContaCobrancaDao dbc = new ContaCobrancaDao();
        NovoLog log = new NovoLog();
        Boleto boleto = new Boleto();
        MensagemConvencao mc = new MensagemConvencao();
        MensagemConvencaoDao dbm = new MensagemConvencaoDao();

        MovimentoDao db = new MovimentoDao();

        ContaCobranca cc = dbc.pesquisaServicoCobranca(movimento.getServicos().getId(), movimento.getTipoServico().getId());
        if (movimento.getPessoa().getId() != 0) {
            Convencao convencao = dbco.pesquisarCnaeConvencaoPorPessoa(movimento.getPessoa().getId());
            if (convencao == null) {
                return false;
            }

            if (movimento.getTipoServico().getId() != 4) {
                mc = dbm.verificaMensagem(convencao.getId(),
                        movimento.getServicos().getId(),
                        movimento.getTipoServico().getId(),
                        dbgc.grupoCidadesPorPessoa(movimento.getPessoa().getId(),
                                convencao.getId()).getId(),
                        movimento.getReferencia());
                if (mc == null) {
                    return false;
                }
            }
        }
        int id_boleto = db.inserirBoletoNativo(cc.getId());

        if (id_boleto != -1) {
            dao.openTransaction();
            if (movimento.getId() == -1) {
                // LOTE ---
                lote.setDepartamento(null);
                lote.setStatus((FStatus) dao.find(new FStatus(), 1));
                lote.setLancamento(DataHoje.data());
                lote.setAvencerContabil(false);
                lote.setEmissao(DataHoje.data());
                lote.setFTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 2));
                lote.setValor(movimento.getValor());
                lote.setRotina((Rotina) dao.find(new Rotina(), 4));
                lote.setPessoa(movimento.getPessoa());
                lote.setCondicaoPagamento((CondicaoPagamento) dao.find(new CondicaoPagamento(), 1));
                lote.setFilial((Filial) dao.find(new Filial(), 1));
                lote.setPessoaSemCadastro(null);
                lote.setEvt(null);
                lote.setPlano5(null);
                lote.setDocumento("");

                if (cc == null) {
                    dao.rollback();
                    return false;
                }
                if (dao.save(lote)) {
                    log.save("Salvar Lote - ID: " + lote.getId() + " Pessoa: " + lote.getPessoa().getNome() + " Data: " + lote.getEmissao());
                } else {
                    dao.rollback();
                    return false;
                }

                // MOVIMENTO ----
                movimento.setLote(lote);
                movimento.setVencimento(mc.getVencimento());
                movimento.setVencimentoOriginal(mc.getVencimento());

                if (dao.save(movimento)) {
                    // BOLETO ---

                    boleto = (Boleto) dao.find(new Boleto(), id_boleto);
                    boleto.setContaCobranca(cc);

                    // SE AGRUPA FOR TRUE** NR_CTR_BOLETO = ID_PESSOA + FATOR DE VENCIMENTO
                    if (movimento.getServicos().isAgrupaBoleto()) {
                    } else {
                        // SE AGRUPA FOR FALSE** NR_CTR_BOLETO = ID_MOVIMENTO
                        //boleto.setNrBoleto(boleto.getContaCobranca().getId());
                        boleto.setNrCtrBoleto(String.valueOf(movimento.getId()));

                        movimento.setDocumento(boleto.getBoletoComposto());
                        movimento.setNrCtrBoleto(boleto.getNrCtrBoleto());

                        if (!dao.update(movimento)) {
                            dao.rollback();
                            return false;
                        }
                        if (!dao.update(boleto)) {
                            dao.rollback();
                            return false;
                        }

                        if (movimento.getPessoa().getId() != 0 && movimento.getTipoServico().getId() != 4) {
                            if (!dao.save(new MensagemCobranca(-1, movimento, mc))) {
                                dao.rollback();
                                return false;
                            }
                        }
                    }

                    log.save("Salvar Movimento - ID: " + movimento.getId() + " Pessoa: " + movimento.getPessoa().getNome() + " Valor: " + movimento.getValor());
                } else {
                    dao.rollback();
                    return false;
                }
            } else {
                dao.rollback();
                return false;
            }
            dao.commit();
        } else {
            return false;
        }
        return true;
    }

    public static boolean alterarUmMovimento(Movimento movimento) {
        Dao dao = new Dao();
        NovoLog log = new NovoLog();
        dao.openTransaction();
        if (movimento.getId() != -1) {
            // LOTE ---
            Lote lote = (Lote) dao.find(new Lote(), movimento.getLote().getId());
            lote.setValor(movimento.getValor());
//            
//            //ServicoContaCobranca scc = dbc.pesquisaServPorIdServIdTipoServ(movimento.getServicos().getId(), movimento.getTipoServico().getId());
            if (dao.update(lote)) {
                // log.update("", "Alterar Lote - ID: " + lote.getId() + " Pessoa: " + lote.getPessoa().getNome() + " Data: " + lote.getEmissao() + " Valor: " + lote.getValor());
            } else {
                dao.rollback();
                return false;
            }
//
//            // MOVIMENTO ----
//            movimento.setLote(lote);
            if (dao.update(movimento)) {
                // log.update("", "Alterar Movimento - ID: " + movimento.getId() + " Pessoa: " + movimento.getPessoa().getNome() + " Valor: " + movimento.getValor());
            } else {
                dao.rollback();
                return false;
            }
            dao.commit();
        } else {
            return false;
        }
        return true;
    }

    public static boolean excluirUmMovimento(Movimento movimento) {
        String mensagem = "Deletados com sucesso!";
        MovimentoDao movDB = new MovimentoDao();
        Dao dao = new Dao();
        Lote lote = null;
        MensagemCobranca mensagemCobranca = null;
        List<ImpressaoWeb> listaLogWeb = new ArrayList();
        try {
            if (movimento.isAtivo() && movimento.getBaixa() == null) {
                mensagemCobranca = movDB.pesquisaMensagemCobranca(movimento.getId());
                listaLogWeb = movDB.pesquisaLogWeb(movimento.getId());
                dao.openTransaction();

                // EXCLUI LISTA IMPRESSAO
                for (ImpressaoWeb imp : listaLogWeb) {
                    if (!dao.delete(imp)) {
                        dao.rollback();
                        mensagem = "Erro na exclusão da lista de LogWeb!";
                        return false;
                    }
                }

                // EXCLUI MENSAGEM BOLETO
                if (mensagemCobranca != null) {
                    if (!dao.delete(mensagemCobranca)) {
                        dao.rollback();
                        mensagem = "Erro na exclusão da mensagem do boleto!";
                        return false;
                    }
                }

                // EXCLUI MOVIMENTO
                if (!dao.delete(movimento)) {
                    dao.rollback();
                    mensagem = "Erro na exclusão do movimento!";
                    return false;
                }

                lote = movimento.getLote();
                // EXCLUI LOTE
                if (!dao.delete(lote)) {
                    dao.rollback();
                    mensagem = "Erro na exclusão do lote.";
                    return false;
                }

                // EXCLUI BOLETO 
                Object bols = dao.find(new Boleto(), movDB.pesquisaBoletos(String.valueOf(movimento.getId())).getId());
                if (bols != null) {
                    if (!dao.delete(bols)) {
                        dao.rollback();
                        mensagem = "Erro na exclusão do boleto!";
                        return false;
                    }
                }
                dao.commit();
                return true;
            }
        } catch (Exception e) {
            mensagem = e.getMessage();
        }
        return false;
    }

    public static String inativarUmMovimento(Movimento movimento, String historico) {
        String mensagem = "";
        MovimentoDao movDB = new MovimentoDao();
        Dao dao = new Dao();
        MovimentoInativo mi = new MovimentoInativo();
        NovoLog novoLog = new NovoLog();
        try {
            if (movimento.isAtivo() && movimento.getBaixa() == null || movimento.getBaixa().getId() == -1) {
                dao.openTransaction();
                movimento.setAtivo(false);
                mi.setData(DataHoje.data());
                mi.setMovimento(movimento);
                mi.setUsuario((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
                mi.setHistorico(historico);

                Boleto bol = movDB.pesquisaBoletos(movimento.getNrCtrBoleto());
                if (bol != null) {
                    bol.setAtivo(false);
                    if (!dao.update(bol)) {
                        dao.rollback();
                        return "Erro ao excluir Boleto, verifique os logs!";
                    }
                }

                if (!dao.update(movimento)) {
                    dao.rollback();
                    return "Erro ao excluir Movimento, verifique os logs!";
                }

                if (!dao.save(mi)) {
                    dao.rollback();
                    return "Erro ao salvar Motivo de Inativação, verifique os logs!";
                }

                dao.commit();
                String nrCtrBoleto = "";
                if (bol != null) {
                    if (bol.getNrCtrBoleto() != null) {
                        nrCtrBoleto = bol.getNrCtrBoleto();
                    }
                }
                novoLog.delete("Inativação de boleto: Documento: " + mi.getMovimento().getDocumento() + " - Valor: " + mi.getMovimento().getValorString() + " - Data inativação: " + mi.getData() + " - Pessoa: (" + mi.getMovimento().getPessoa().getId() + ") - " + mi.getMovimento().getPessoa().getNome() + " - CTR Boleto: " + nrCtrBoleto + " - Motivo: " + mi.getHistorico());
            }
        } catch (Exception e) {
            mensagem = e.getMessage();
        }
        return mensagem;
    }

    public static String inativarArrayMovimento(List<Movimento> listaMovimento, String historico, Dao dao) {
        String mensagem = "";
        MovimentoDao movDB = new MovimentoDao();

        NovoLog novoLog = new NovoLog();

        boolean new_dao = false;

        if (dao == null) {
            dao = new Dao();
            dao.openTransaction();
            new_dao = true;
        }

        for (Movimento mov : listaMovimento) {
            try {
                if (mov.isAtivo() && mov.getBaixa() == null || mov.getBaixa().getId() == -1) {
                    mov.setAtivo(false);

                    MovimentoInativo mi = new MovimentoInativo();
                    mi.setData(DataHoje.data());
                    mi.setMovimento(mov);
                    mi.setUsuario((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"));
                    mi.setHistorico(historico);

                    Boleto bol = movDB.pesquisaBoletos(mov.getNrCtrBoleto());

                    if (bol != null) {
                        bol.setAtivo(false);
                        if (!dao.update(bol)) {
                            return "Erro ao excluir Boleto, verifique os logs!";
                        }
                    }

                    if (!dao.update(mov)) {
                        return "Erro ao excluir Movimento, verifique os logs!";
                    }

                    if (!dao.save(mi)) {
                        return "Erro ao salvar Motivo de Inativação, verifique os logs!";
                    }

                    String nrCtrBoleto = "";
                    if (bol != null) {
                        if (bol.getNrCtrBoleto() != null) {
                            nrCtrBoleto = bol.getNrCtrBoleto();
                        }
                    }
                    novoLog.delete("Inativação de boleto: ID MOVIMENTO: " + mi.getMovimento().getId() + " - Documento: " + mi.getMovimento().getDocumento() + " - Valor: " + mi.getMovimento().getValorString() + " - Data inativação: " + mi.getData() + " - Pessoa: (" + mi.getMovimento().getPessoa().getId() + ") - " + mi.getMovimento().getPessoa().getNome() + " - CTR Boleto: " + nrCtrBoleto + " - Motivo: " + mi.getHistorico());
                }
            } catch (Exception e) {
                mensagem = e.getMessage();
            }
        }

        if (new_dao) {
            dao.commit();
        }
        return mensagem;
    }

    public static String reativarArrayMovimento(List<Movimento> listaMovimento, String historico, Dao dao) {
        String mensagem = "";
        MovimentoDao movDB = new MovimentoDao();

        NovoLog novoLog = new NovoLog();

        boolean new_dao = false;

        if (dao == null) {
            dao = new Dao();
            dao.openTransaction();
            new_dao = true;
        }

        MovimentoInativoDao movimentoInativoDao = new MovimentoInativoDao();
        for (Movimento mov : listaMovimento) {
            try {
                if (!mov.isAtivo() && mov.getBaixa() == null) {
                    mov.setAtivo(true);

                    MovimentoInativo movimentoInativo = movimentoInativoDao.findByMovimento(mov.getId());

                    Boleto bol = movDB.pesquisaBoletos(mov.getNrCtrBoleto());

                    if (bol != null) {
                        bol.setAtivo(true);
                        if (!dao.update(bol)) {
                            return "Erro ao excluir Boleto, verifique os logs!";
                        }
                    }

                    if (!dao.update(mov)) {
                        return "Erro ao excluir Movimento, verifique os logs!";
                    }

                    if (!dao.delete(movimentoInativo)) {
                        return "Erro ao salvar Motivo de Inativação, verifique os logs!";
                    }

                    String nrCtrBoleto = "";
                    if (bol != null) {
                        if (bol.getNrCtrBoleto() != null) {
                            nrCtrBoleto = bol.getNrCtrBoleto();
                        }
                    }
                    novoLog.setCodigo(movimentoInativo.getMovimento().getId());
                    novoLog.setTabela("fin_movimento");
                    novoLog.update("", "Inativação de boleto: ID MOVIMENTO: " + movimentoInativo.getMovimento().getId() + " - Documento: " + movimentoInativo.getMovimento().getDocumento() + " - Valor: " + movimentoInativo.getMovimento().getValorString() + " - Data que hávia sido inátivo: " + movimentoInativo.getData() + " - Pessoa: (" + movimentoInativo.getMovimento().getPessoa().getId() + ") - " + movimentoInativo.getMovimento().getPessoa().getNome() + " - CTR Boleto: " + nrCtrBoleto + " - Motivo da reativação: " + movimentoInativo.getHistorico());
                }
            } catch (Exception e) {
                mensagem = e.getMessage();
            }
        }

        if (new_dao) {
            dao.commit();
        }
        return mensagem;
    }

    public static boolean estornarMovimento(Movimento movimento, String motivoEstorno) {
        MovimentoDao db = new MovimentoDao();
        Baixa baixa;
        List<FormaPagamento> formaPagamento;
        List<Movimento> lista;
        Dao dao = new Dao();
        try {
            if (movimento == null || movimento.getBaixa() == null || (!movimento.isAtivo() && movimento.getLote().getRotina().getId() != 132)) {
                return true;
            }

            lista = db.movimentoIdbaixa(movimento.getBaixa().getId());

            dao.openTransaction();
            if (lista.isEmpty()) {
                dao.rollback();
                return false;
            } else if (lista.size() > 1) {
                formaPagamento = db.pesquisaFormaPagamento(movimento.getBaixa().getId());

                for (int i = 0; i < formaPagamento.size(); i++) {
                    if (!dao.delete(formaPagamento.get(i))) {
                        dao.rollback();
                        return false;
                    }

                    if (formaPagamento.get(i).getChequeRec() != null) {
                        if (!dao.delete(formaPagamento.get(i).getChequeRec())) {
                            dao.rollback();
                            return false;
                        }
                    }
                }

                baixa = (Baixa) dao.find(new Baixa(), movimento.getBaixa().getId());

                EstornoCaixaLote ecl = new EstornoCaixaLote(
                        -1,
                        DataHoje.dataHoje(),
                        baixa.getDtBaixa(),
                        Usuario.getUsuario(),
                        baixa.getUsuario(),
                        baixa.getCaixa(),
                        motivoEstorno,
                        baixa.getId()
                );

                if (!dao.save(ecl)) {
                    dao.rollback();
                    return false;
                }

                for (int i = 0; i < lista.size(); i++) {
                    lista.get(i).setBaixa(null);
                    lista.get(i).setJuros(0);
                    lista.get(i).setMulta(0);
                    lista.get(i).setCorrecao(0);
                    lista.get(i).setTaxa(0);
                    lista.get(i).setDesconto(0);
                    lista.get(i).setValorBaixa(0);

                    if (!dao.update(lista.get(i))) {
                        dao.rollback();
                        return false;
                    }

                    if (!dao.save(new EstornoCaixa(-1, ecl, lista.get(i)))) {
                        dao.rollback();
                        return false;
                    }
                }

                if (!dao.delete(baixa)) {
                    dao.rollback();
                    return false;
                }
            } else {
                formaPagamento = db.pesquisaFormaPagamento(movimento.getBaixa().getId());

                for (int i = 0; i < formaPagamento.size(); i++) {
                    if (!dao.delete(formaPagamento.get(i))) {
                        dao.rollback();
                        return false;
                    }

                    if (formaPagamento.get(i).getChequeRec() != null) {
                        if (!dao.delete(formaPagamento.get(i).getChequeRec())) {
                            dao.rollback();
                            return false;
                        }
                    }
                }
                baixa = (Baixa) dao.find(new Baixa(), movimento.getBaixa().getId());

                EstornoCaixaLote ecl = new EstornoCaixaLote(
                        -1,
                        DataHoje.dataHoje(),
                        baixa.getDtBaixa(),
                        Usuario.getUsuario(),
                        baixa.getUsuario(),
                        baixa.getCaixa(),
                        motivoEstorno,
                        baixa.getId()
                );

                if (!dao.save(ecl)) {
                    dao.rollback();
                    return false;
                }

                movimento.setBaixa(null);
                movimento.setJuros(0);
                movimento.setMulta(0);
                movimento.setCorrecao(0);
                movimento.setTaxa(0);
                movimento.setDesconto(0);
                movimento.setValorBaixa(0);

                if (!dao.update(movimento)) {
                    dao.rollback();
                    return false;
                }

                if (!dao.save(new EstornoCaixa(-1, ecl, movimento))) {
                    dao.rollback();
                    return false;
                }

                if (!dao.delete(baixa)) {
                    dao.rollback();
                    return false;
                }
            }

            dao.commit();
            return true;
        } catch (Exception e) {
            dao.rollback();
        }
        return false;
    }

    public static boolean baixarMovimento(Movimento movimento, Usuario usuario, String pagamento, float valor_liquido, Date dataCredito, String numeroComposto, int nrSequencia) {

        Baixa baixa = new Baixa();
        baixa.setUsuario(usuario);
        baixa.setFechamentoCaixa(null);
        baixa.setBaixa(pagamento);
        baixa.setImportacao(DataHoje.data());
        baixa.setSequenciaBaixa(nrSequencia);
        baixa.setDocumentoBaixa(numeroComposto);
        baixa.setCaixa(null);

        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.save(baixa)) {
            dao.rollback();
            return false;
        }

        // CALCULO PARA PORCENTAGEM DO VALOR PAGO -- NESSE CASO DE ARRECADACAO É 100%
        //float calc = Moeda.multiplicarValores(Moeda.divisaoValores(fp.get(i).getValor(), valorTotal), 100);
        //calc = Moeda.converteFloatR$Float(calc);
        MovimentoDao db = new MovimentoDao();

        Boleto bol = db.pesquisaBoletos(movimento.getNrCtrBoleto());

        Plano5Dao plano5DB = new Plano5Dao();
        Plano5 plano5 = plano5DB.pesquisaPlano5IDContaBanco(bol.getContaCobranca().getContaBanco().getId());

        FormaPagamento fp = new FormaPagamento(
                -1,
                baixa,
                null,
                null,
                100,
                movimento.getValorBaixa(),
                movimento.getLote().getFilial(),
                plano5,
                null,
                null,
                (TipoPagamento) dao.find(new TipoPagamento(), 3),
                valor_liquido,
                dataCredito,
                0,
                null,
                0,
                null,
                null,
                null
        );

        if (!dao.save(fp)) {
            dao.rollback();
            return false;
        }

        movimento.setBaixa(baixa);

        //movimento.setValor(movimento.getValorBaixa());
        //movimento.setAtivo(false);
        if (!dao.update(movimento)) {
            dao.rollback();
            return false;
        }
//        
//        Movimento movBaixa = movimento;
//        movBaixa.setId(-1);
//        movBaixa.setAtivo(true);
//        movBaixa.setValor(movimento.getValorBaixa());
//        
//        if (!sv.inserirObjeto(movBaixa)){
//            sv.desfazerTransacao();
//            return false;
//        }

        //Movimento movi = new Movimento(-1, null, movimento.getPlano5(), movimento.getPessoa(), movimento.getServicos(), baixa, movimento.getTipoServico(), null, movimento.getValor(), movimento.getAcordado(), movimento.getReferencia(),movimento.getDtVencimento(), 1, true, movimento.getEs(), false, movimento.getTitular(), movimento.getBeneficiario(), "", movimento.getNrCtrBoleto(), movimento.getDtVencimentoOriginal(), 0, 0, 0, 0, 0, movimento.getTaxa(), movimento.getValorBaixa(), 0);
//                Movimento movi = movimento;
//                movi.setId(-1);
//                movi.setAtivo(true);
//                
//                if (salvarUmMovimento(null, movimento))
        dao.commit();
        return true;
    }

    public static boolean baixarMovimentoManual(List<Movimento> movimento, Usuario usuario, List<FormaPagamento> fp, float valorTotal, String pagamento, Caixa caixa, float valorTroco) {
        // 15
        // 000003652580001
        // 8
        // 30042013
        // 10
        // 0000022912
        try {
            String numeroComposto = "";
            if (movimento.get(0).getServicos() != null) {

                if (movimento.get(0).getServicos().getId() == 1) {
                    //String documento = movimento.get(0).getPessoa().getDocumento().replace(".", "").replace("/", "").replace("-", "").substring(0, 12);
                    String documento = movimento.get(0).getDocumento();
                    documento = ("000000000000000").substring(0, 15 - documento.length()) + documento;
                    String d_pagamento = ("00000000").substring(0, 8 - pagamento.replace("/", "").length()) + pagamento.replace("/", "");
                    String v_pago = ("0000000000").substring(0, 10 - Moeda.converteR$Float(valorTotal).replace(".", "").replace(",", "").length()) + Moeda.converteR$Float(valorTotal).replace(".", "").replace(",", "");
                    numeroComposto = documento + d_pagamento + v_pago;
                }

            }
            Dao dao = new Dao();
            Baixa baixa = new Baixa();
            baixa.setUsuario(usuario);
            baixa.setFechamentoCaixa(null);
            baixa.setBaixa(pagamento);
            baixa.setDocumentoBaixa(numeroComposto);
            baixa.setCaixa(caixa);
            baixa.setTroco(valorTroco);

            if (GenericaSessao.getObject("usuarioAutenticado") != null) {
                baixa.setUsuarioDesconto((Usuario) GenericaSessao.getObject("usuarioAutenticado"));
                GenericaSessao.remove("usuarioAutenticado");
            }

            dao.openTransaction();
            if (!dao.save(baixa)) {
                dao.rollback();
                return false;
            }
            for (FormaPagamento fp1 : fp) {
                fp1.setBaixa(baixa);
                float calc = (fp1.getValor() == 0) ? 100 : Moeda.multiplicarValores(Moeda.divisaoValores(fp1.getValor(), valorTotal), 100);
                // 13/06/2016 ( HORÁRIO DE BRASÍLIA: 16:41 ) TIREMOS A CONVERSÃO 
                // ABAIXO PORQUE NÃO ESTAVA JOGANDO fin_forma_pagamento.nr_valorp 
                // corretamente as decimais. (Rogério, eu que fiz isso e escrevi)
                // calc = Moeda.converteFloatR$Float(calc);
                fp1.setValorP(calc);
                ChequeRec ch = new ChequeRec();
                if (fp1.getChequeRec() != null) {
                    ch.setAgencia(fp1.getChequeRec().getAgencia());
                    ch.setBanco(fp1.getChequeRec().getBanco());
                    ch.setCheque(fp1.getChequeRec().getCheque());
                    ch.setConta(fp1.getChequeRec().getConta());
                    ch.setEmissao(fp1.getChequeRec().getEmissao());
                    ch.setVencimento(fp1.getChequeRec().getVencimento());
                    if (!dao.save(ch)) {
                        dao.rollback();
                        return false;
                    }
                    fp1.setChequeRec(ch);
                }
                ChequePag ch_p = new ChequePag();
                if (fp1.getChequePag() != null) {
                    ch_p.setCheque(fp1.getChequePag().getCheque());
                    ch_p.setPlano5(fp1.getChequePag().getPlano5());
                    ch_p.setVencimento(fp1.getChequePag().getVencimento());

                    if (!dao.save(ch_p)) {
                        dao.rollback();
                        return false;
                    }
                    fp1.setChequePag(ch_p);

                    ContaBanco cb = (ContaBanco) dao.find(new ContaBanco(), ch_p.getPlano5().getContaBanco().getId());
                    cb.setUCheque(cb.getUCheque() + 1);
                    if (!dao.update(cb)) {
                        dao.rollback();
                        return false;
                    }
                }

                CartaoPag cartao_pag = new CartaoPag();
                if (fp1.getCartaoPag() != null) {
                    cartao_pag.setId(fp1.getCartaoPag().getId());
                    if (!dao.save(cartao_pag)) {
                        dao.rollback();
                        return false;
                    }
                    fp1.setCartaoPag(cartao_pag);
                }

                CartaoRec cartao_rec = new CartaoRec();
                if (fp1.getCartaoRec() != null) {
                    cartao_rec.setDtLiquidacao(fp1.getCartaoRec().getDtLiquidacao());
                    cartao_rec.setCartao(fp1.getCartaoRec().getCartao());

                    if (!dao.save(cartao_rec)) {
                        dao.rollback();
                        return false;
                    }
                    fp1.setCartaoRec(cartao_rec);
                }

                if (!dao.save(fp1)) {
                    dao.rollback();
                    return false;
                }
            }

            for (int i = 0; i < movimento.size(); i++) {
                movimento.get(i).setBaixa(baixa);

                if (!dao.update(movimento.get(i))) {
                    dao.rollback();
                    return false;
                }
            }
            dao.commit();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Object[] baixarMovimentoSocial(List<Movimento> lista_movimento, Usuario usuario, String data_pagamento, float valor_baixa, float valor_taxa) {
        Dao dao = new Dao();
        Object[] lista_log = new Object[3];
        Baixa baixa = new Baixa();
        baixa.setUsuario(usuario);
        baixa.setFechamentoCaixa(null);
        baixa.setBaixa(data_pagamento);
        baixa.setImportacao(DataHoje.data());
        baixa.setCaixa(null);

        dao.openTransaction();
        if (!dao.save(baixa)) {
            dao.rollback();
            lista_log[0] = 0; // 0 - ERRO AO INSERIR BAIXA
            lista_log[1] = baixa;
            lista_log[2] = "Erro ao inserir Baixa";
            return lista_log;
        }

        MovimentoDao db = new MovimentoDao();

        Boleto bol = db.pesquisaBoletos(lista_movimento.get(0).getNrCtrBoleto());

        Plano5Dao plano5DB = new Plano5Dao();
        Plano5 plano5 = plano5DB.pesquisaPlano5IDContaBanco(bol.getContaCobranca().getContaBanco().getId());

        FormaPagamento fp = new FormaPagamento(
                -1,
                baixa,
                null,
                null,
                100,
                valor_baixa,
                lista_movimento.get(0).getLote().getFilial(),
                plano5,
                null,
                null,
                (TipoPagamento) dao.find(new TipoPagamento(), 3),
                valor_baixa,
                null,
                0,
                null,
                0,
                null,
                null,
                null
        );

        if (!dao.save(fp)) {
            dao.rollback();
            lista_log[0] = 1; // 1 - ERRO AO INSERIR FORMA DE PAGAMENTO
            lista_log[1] = fp;
            lista_log[2] = "Erro ao inserir Forma de Pagamento";
            return lista_log;
        }

        float soma = 0;
        for (Movimento movimento : lista_movimento) {
            soma = Moeda.somaValores(soma, movimento.getValor());

            movimento.setBaixa(baixa);
            if (!dao.update(movimento)) {
                dao.rollback();
                lista_log[0] = 2; // 2 - ERRO AO ALTERAR MOVIMENTO COM A BAIXA
                lista_log[1] = movimento;
                lista_log[2] = "Erro ao alterar Movimento";
                return lista_log;
            }
        }

        if (valor_baixa == soma) {
            // valor baixado corretamente
            for (Movimento movimento : lista_movimento) {
                movimento.setValorBaixa(movimento.getValor());
                if (!dao.update(movimento)) {
                    dao.rollback();
                    lista_log[0] = 9; // 9 - ERRO AO ALTERAR MOVIMENTO VALOR BAIXA CORRETO
                    lista_log[1] = movimento;
                    lista_log[2] = "Erro ao alterar Movimento com Desconto e Valor Baixa";
                }
            }
        } else if (valor_baixa < (soma - 0.03)) {
            float acrescimo = Moeda.subtracaoValores(soma, valor_baixa);
            // valor da baixa é menor que os boletos ( O CLIENTE PAGOU MENOS )

            // ROGÉRIO PEDIU PRA NÃO BAIXAR (CHAMADO 1095)
//            for (Movimento movimento : lista_movimento) {
//                float valor = 0, percentual = 0;
//                percentual = Moeda.multiplicarValores(Moeda.divisaoValores(movimento.getValor(), soma), 100);
//                valor = Moeda.divisaoValores(Moeda.multiplicarValores(acrescimo, percentual), 100);
//
//                movimento.setDesconto(valor);
//                movimento.setValorBaixa(Moeda.subtracaoValores(movimento.getValor(), valor));
//
//                if (!sv.alterarObjeto(movimento)) {
//                    sv.desfazerTransacao();
//                    lista_log[0] = 3; // 3 - ERRO AO ALTERAR MOVIMENTO COM DESCONTO E VALOR BAIXA
//                    lista_log[1] = movimento;
//                    lista_log[2] = "Erro ao alterar Movimento com Desconto e Valor Baixa";
//                    return lista_log;
//                }
//            }
//            sv.comitarTransacao();
            dao.rollback();

            String msg = "Valor do Boleto " + lista_movimento.get(0).getDocumento() + " - vencto. " + lista_movimento.get(0).getVencimento() + " - pag. " + data_pagamento + " MENOR com défit de " + Moeda.converteR$Float(acrescimo);
            BoletoNaoBaixado bnb = new BoletoNaoBaixado(-1, bol, msg, valor_baixa, DataHoje.dataHoje(), DataHoje.dataHoje());

            dao.openTransaction();
            if (!dao.save(bnb)) {
                dao.rollback();
                return new String[3];
            }
            dao.commit();
            lista_log[0] = 6; // 6 - VALOR DO ARQUIVO MENOR
            lista_log[1] = lista_movimento;
            lista_log[2] = msg;
            return lista_log;
        } else if (valor_baixa > soma) {
            float acrescimo = Moeda.subtracaoValores(valor_baixa, soma);
            // valor da baixa é maior que os boletos ( O CLIENTE PAGOU MAIS ) 
            for (Movimento movimento : lista_movimento) {
                float valor = 0, percentual = 0;
                percentual = Moeda.multiplicarValores(Moeda.divisaoValores(movimento.getValor(), soma), 100);
                valor = Moeda.divisaoValores(Moeda.multiplicarValores(acrescimo, percentual), 100);

                movimento.setCorrecao(valor);
                movimento.setValorBaixa(Moeda.somaValores(valor, movimento.getValor()));

                if (!dao.update(movimento)) {
                    dao.rollback();
                    lista_log[0] = 4; // 4 - ERRO AO ALTERAR MOVIMENTO COM CORREÇÃO E VALOR BAIXA
                    lista_log[1] = movimento;
                    lista_log[2] = "Erro ao alterar Movimento com Correção e Valor Baixa";
                    return lista_log;
                }
            }
            dao.commit();
            //sv.desfazerTransacao();
            lista_log[0] = 7; // 7 - VALOR DO ARQUIVO MAIOR
            lista_log[1] = lista_movimento;
            lista_log[2] = "Valor do Boleto " + lista_movimento.get(0).getDocumento() + " - vencto. " + lista_movimento.get(0).getVencimento() + " - pag. " + data_pagamento + " MAIOR com acréscimo de " + Moeda.converteR$Float(acrescimo);
            return lista_log;
        }
        dao.commit();
        //sv.desfazerTransacao();
        lista_log[0] = 5; // 5 - BAIXA CONCLUÍDA COM SUCESSO
        lista_log[1] = lista_movimento;
        lista_log[2] = "Baixa concluída com Sucesso!";
        return lista_log;
    }

    public static boolean refazerMovimentos(List<Movimento> lista_movimento) {
        Dao dao = new Dao();
        dao.openTransaction();

        if (!inativarArrayMovimento(lista_movimento, "Movimento refeito por alteração nos dados cadastrais", null).isEmpty()) {
            //dao.rollback();
            return false;
        }

        boolean commit = false;
        for (Movimento m : lista_movimento) {
            String vencto = m.getVencimento().substring(3);

            if (dao.liveSingle("select func_geramensalidades(" + m.getBeneficiario().getId() + ", '" + vencto + "')", true) != null) {
                commit = true;
            } else {
                dao.rollback();
                return false;
            }
        }

        if (commit) {
            dao.commit();
            return true;
        }

        dao.rollback();
        return false;
    }

    public static String excluirUmAcordoSocial(Movimento movimento) {
        NovoLog log = new NovoLog();

        List<Movimento> lista_acordo = new ArrayList();
        MovimentoDao db = new MovimentoDao();

        if (movimento.getAcordo() != null && movimento.getAcordo().getId() != -1) {
            lista_acordo.addAll(db.pesquisaAcordoParaExclusao(movimento.getAcordo().getId()));
        } else {
            return "Não existe acordo para este boleto!";
        }

        if (lista_acordo.isEmpty()) {
            return "Nenhum Acordo encontrado!";
        }

        for (Movimento lista_acordo1 : lista_acordo) {
            if (lista_acordo1.getBaixa() != null && lista_acordo1.isAtivo()) {
                return "Acordo com parcela já paga não pode ser excluído!";
            }
        }

        String ids = "";
        for (int i = 0; i < lista_acordo.size(); i++) {
            if (ids.length() > 0 && i != lista_acordo.size()) {
                ids = ids + ", ";
            }
            ids = ids + String.valueOf(lista_acordo.get(i).getId());
        }

        if (ids.isEmpty()) {
            return "Ids não gerado!";
        }

        if (!db.excluirAcordoSocialIn(ids, lista_acordo.get(0).getAcordo().getId())) {
            return "Não foi possível excluir acordos";
        }

        String str_log
                = "Acordo ID: " + lista_acordo.get(0).getAcordo().getId() + " \n "
                + "Acordo Contato: " + lista_acordo.get(0).getAcordo().getContato() + " \n "
                + "Acordo Data: " + lista_acordo.get(0).getAcordo().getData() + " \n "
                + "Acordo Email: " + lista_acordo.get(0).getAcordo().getEmail() + " \n "
                + "Acordo Usuário: " + lista_acordo.get(0).getAcordo().getUsuario().getPessoa().getNome() + " \n "
                + "------------------------------------------------- \n "
                + "-- MOVIMENTOS EXCLUÍDOS -- \n ";

        String sl = "";
        for (Movimento lista_acordo1 : lista_acordo) {
            sl
                    += "------------------------------------------------- \n "
                    + "Movimento ID: " + lista_acordo1.getId() + " \n "
                    + "Movimento Valor: " + lista_acordo1.getValorString() + " \n "
                    + "Movimento Vencimento: " + lista_acordo1.getVencimento() + " \n ";
        }

        log.delete(str_log + sl);
        return "";
    }
}
