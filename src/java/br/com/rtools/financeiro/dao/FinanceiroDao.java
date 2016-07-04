/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.associativo.LoteBoleto;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Banco;
import br.com.rtools.financeiro.BloqueiaServicoPessoa;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class FinanceiroDao extends DB {

    public boolean executarQuery(String textoQuery) {
        try {
            Query qry = getEntityManager().createNativeQuery(textoQuery);
            int result = qry.executeUpdate();
            if (result == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insert(Object objeto) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(objeto);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public String insertHist(Object objeto) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(objeto);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return "true";
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return e.getMessage() + "  Resto";
        }
    }

    public Movimento pesquisaCodigo(Movimento movimento) {
        int id = movimento.getId();
        Movimento result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Movimento.pesquisaID");
            qry.setParameter("pid", id);
            result = (Movimento) qry.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public Baixa pesquisaCodigo(Baixa loteBaixa) {
        int id = loteBaixa.getId();
        Baixa result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("LoteBaixa.pesquisaID");
            qry.setParameter("pid", id);
            result = (Baixa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Lote pesquisaCodigo(Lote lote) {
        int id = lote.getId();
        Lote result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Lote.pesquisaID");
            qry.setParameter("pid", id);
            result = (Lote) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Usuario pesquisaUsuario(int idUsuario) {
        Usuario result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select u "
                    + "  from Usuario u "
                    + " where u.id = :pid");
            qry.setParameter("pid", idUsuario);
            result = (Usuario) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Historico pesquisaHistorico(int idHistorico) {
        Historico result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select u "
                    + "  from Historico u "
                    + " where u.id = :pid");
            qry.setParameter("pid", idHistorico);
            result = (Historico) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public int contarMovimentosPara(int idLote) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select count (m) "
                    + "  from Movimento m "
                    + " where m.lote.id = " + idLote);
            List vetor = qry.getResultList();
            Long longI = (Long) vetor.get(0);
            return Integer.parseInt(Long.toString(longI));
        } catch (Exception e) {
            return -1;
        }
    }

    public List<Movimento> pesquisaMovimentoOriginal(int idLoteBaixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select m "
                    + "  from Movimento m left join m.baixa l"
                    + " where l.id = " + idLoteBaixa
                    + "   and m.ativo is false");
            return (List<Movimento>) qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public boolean update(Object objeto) {
        try {
            getEntityManager().merge(objeto);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(Object objeto) {
        try {
            getEntityManager().remove(objeto);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean delete(Movimento objeto) {
        try {
            getEntityManager().remove(objeto);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean acumularObjeto(Object objeto) {
        try {
            getEntityManager().persist(objeto);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void abrirTransacao() {
        getEntityManager().getTransaction().begin();
    }

    public void comitarTransacao() {
        getEntityManager().getTransaction().commit();
    }

    public void desfazerTransacao() {
        getEntityManager().getTransaction().rollback();
    }

    public List<BloqueiaServicoPessoa> listaBloqueiaServicoPessoas(int id_pessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select bl from BloqueiaServicoPessoa bl where bl.pessoa.id = " + id_pessoa + " order by bl.servicos.descricao");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public BloqueiaServicoPessoa pesquisaBloqueiaServicoPessoa(int id_pessoa, int id_servico, Date dt_inicial, Date dt_final) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select bl from BloqueiaServicoPessoa bl where bl.pessoa.id = " + id_pessoa + " and bl.servicos.id = " + id_servico + " and bl.dtInicio = :dtInicial and bl.dtFim = :dtFinal");
            qry.setParameter("dtInicial", dt_inicial);
            qry.setParameter("dtFinal", dt_final);
            return (BloqueiaServicoPessoa) qry.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public List<Movimento> pesquisaMovimentoPorLote(int id_lote) {
        try {
            Query qry = getEntityManager().createQuery("select m from Movimento m where m.lote.id = " + id_lote);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public ContaSaldo pesquisaSaldoInicial(int id_caixa) {
        try {
            Query qry = getEntityManager().createQuery("select cs from ContaSaldo cs where cs.caixa.id = " + id_caixa + " and cs.dtData = (select MAX(csx.dtData) from ContaSaldo csx)");
            return (ContaSaldo) qry.getSingleResult();
        } catch (Exception e) {
            return new ContaSaldo();
        }
    }

    public List<Caixa> listaCaixa() {
        try {
            //Query qry = getEntityManager().createQuery("select c from Caixa c where c.caixa <> 1 order by c.caixa");
            // ROGÉRIO QUER O CAIXA 01 PARA O FECHAMENTO
            Query qry = getEntityManager().createQuery("select c from Caixa c order by c.caixa");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaMovimentoCaixa(int id_caixa, String es, Integer id_usuario, String data) {
        try {
            String and = (id_usuario == null) ? "" : " AND u.id = " + id_usuario;
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT distinct(f.id), \n "
                    + "       m.ds_es, \n "
                    + "	b.dt_baixa, \n "
                    + "	b.id_caixa, \n "
                    + "	p.ds_nome, \n "
                    + "	tp.ds_descricao, \n "
                    + "	f.nr_valor, \n "
                    + "       cx.id_filial, \n "
                    + "       b.id \n "
                    + "  FROM fin_forma_pagamento AS f \n "
                    + " INNER JOIN fin_baixa AS b ON b.id = f.id_baixa \n "
                    + " INNER JOIN seg_usuario AS u ON u.id = b.id_usuario \n "
                    + " INNER JOIN pes_pessoa AS p ON p.id = u.id_pessoa \n "
                    + " INNER JOIN fin_movimento AS m ON m.id_baixa = b.id \n "
                    + " INNER JOIN fin_tipo_pagamento AS tp ON tp.id = f.id_tipo_pagamento \n "
                    + " INNER JOIN fin_caixa AS cx ON cx.id = b.id_caixa \n "
                    + " WHERE b.id_caixa = " + id_caixa + " \n "
                    + "   AND b.id_fechamento_caixa IS NULL \n "
                    + "   AND m.is_ativo = TRUE \n "
                    + "   AND b.dt_baixa = '" + data + "' \n "
                    + "   AND m.ds_es = '" + es + "' \n " + and);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferenciaEntrada(int id_caixa, Integer id_usuario, String data) {
        String and = (id_usuario == null) ? "" : " and tc.usuario.id = " + id_usuario;
        try {
            Query qry = getEntityManager().createQuery("SELECT tc FROM TransferenciaCaixa tc WHERE tc.caixaEntrada.id = " + id_caixa + " AND tc.fechamentoEntrada is null AND tc.dtLancamento = '" + data + "' " + and);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferenciaSaida(int id_caixa, Integer id_usuario, String data) {
        String and = (id_usuario == null) ? "" : " and tc.usuario.id = " + id_usuario;
        try {
            Query qry = getEntityManager().createQuery("SELECT tc FROM TransferenciaCaixa tc WHERE tc.caixaSaida.id = " + id_caixa + " AND (tc.caixaEntrada.caixa <> 1) AND tc.fechamentoSaida is null AND tc.dtLancamento = '" + data + "' " + and);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Baixa> listaBaixa(int id_fechamento_caixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT b "
                    + "  FROM Baixa b "
                    + " WHERE b.fechamentoCaixa.id = " + id_fechamento_caixa
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaFechamentoCaixaTransferencia(int id_caixa) {
        try {
            String text
                    = "SELECT 	tc.id_caixa_entrada, "
                    + "        tc.id_fechamento_entrada, "
                    + "        fc.nr_valor_fechamento, "
                    + "        fc.nr_valor_informado, "
                    + "        fc.dt_data, "
                    + "        fc.ds_hora  "
                    + "  FROM fin_fechamento_caixa fc  "
                    + " INNER JOIN fin_transferencia_caixa tc ON tc.id_fechamento_entrada = fc.id AND tc.id_caixa_entrada = " + id_caixa
                    + //" WHERE tc.id_fechamento_entrada NOT IN (SELECT id_fechamento_entrada FROM fin_transferencia_caixa WHERE id_caixa_saida = "+id_caixa+" AND id_status = 12) " +
                    " WHERE tc.id_fechamento_entrada NOT IN (SELECT id_fechamento_saida FROM fin_transferencia_caixa WHERE id_caixa_saida = " + id_caixa + " AND id_status = 12) "
                    + " GROUP BY tc.id_caixa_entrada, "
                    + "          tc.id_fechamento_entrada, "
                    + "          fc.nr_valor_fechamento, "
                    + "          fc.nr_valor_informado, "
                    + "          fc.dt_data, "
                    + "          fc.ds_hora "
                    + "UNION "
                    + "SELECT 	b.id_caixa, "
                    + "        b.id_fechamento_caixa, "
                    + "        fc.nr_valor_fechamento, "
                    + "        fc.nr_valor_informado, "
                    + "        fc.dt_data, "
                    + "        fc.ds_hora   "
                    + "  FROM fin_fechamento_caixa fc  "
                    + " INNER JOIN fin_baixa b ON b.id_caixa = " + id_caixa + " AND b.id_fechamento_caixa = fc.id "
                    + //" WHERE b.id_fechamento_caixa NOT IN (SELECT id_fechamento_entrada FROM fin_transferencia_caixa WHERE id_caixa_saida = "+id_caixa+" AND id_status = 12) " +
                    " WHERE b.id_fechamento_caixa NOT IN (SELECT id_fechamento_saida FROM fin_transferencia_caixa WHERE id_caixa_saida = " + id_caixa + " AND id_status = 12) "
                    + " GROUP BY b.id_caixa, "
                    + "          b.id_fechamento_caixa, "
                    + "          fc.nr_valor_fechamento, "
                    + "          fc.nr_valor_informado, "
                    + "          fc.dt_data, "
                    + "          fc.ds_hora";
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaFechamentoCaixa(int id_caixa) {
        String text
                = "SELECT  tc.id_caixa_entrada, "
                + "        tc.id_fechamento_entrada, "
                + "        fc.nr_valor_fechamento, "
                + "        fc.nr_valor_informado, "
                + "        fc.dt_data, "
                + "        fc.ds_hora  "
                + "  FROM fin_fechamento_caixa fc  "
                + " INNER JOIN fin_transferencia_caixa tc ON tc.id_fechamento_entrada = fc.id AND tc.id_caixa_entrada = " + id_caixa
                + " GROUP BY tc.id_caixa_entrada, "
                + "          tc.id_fechamento_entrada, "
                + "          fc.nr_valor_fechamento, "
                + "          fc.nr_valor_informado, "
                + "          fc.dt_data, "
                + "          fc.ds_hora "
                + "UNION "
                + "SELECT 	b.id_caixa, "
                + "        b.id_fechamento_caixa, "
                + "        fc.nr_valor_fechamento, "
                + "        fc.nr_valor_informado, "
                + "        fc.dt_data, "
                + "        fc.ds_hora   "
                + "  FROM fin_fechamento_caixa fc  "
                + " INNER JOIN fin_baixa b ON b.id_caixa = " + id_caixa + " AND b.id_fechamento_caixa = fc.id "
                + " GROUP BY b.id_caixa, "
                + "          b.id_fechamento_caixa, "
                + "          fc.nr_valor_fechamento, "
                + "          fc.nr_valor_informado, "
                + "          fc.dt_data, "
                + "          fc.ds_hora "
                + " ORDER BY 5 desc, 6 desc";

        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferencia(int id_fechamento_caixa) {
        try {

            Query qry = getEntityManager().createNativeQuery(
                    "SELECT tc.id "
                    + "  FROM fin_transferencia_caixa tc "
                    + " WHERE (tc.id_fechamento_entrada = " + id_fechamento_caixa + " OR tc.id_fechamento_saida = " + id_fechamento_caixa + ")"
            );

            List<Vector> lista = qry.getResultList();
            List<TransferenciaCaixa> result = new ArrayList();

            for (int i = 0; i < lista.size(); i++) {
                result.add((TransferenciaCaixa) new Dao().find(new TransferenciaCaixa(), (Integer) lista.get(i).get(0)));
            }
            return result;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Caixa pesquisaCaixaUm() {
        try {
            Query qry = getEntityManager().createQuery("select c from Caixa c where c.caixa = 1");
            qry.setMaxResults(1);
            return (Caixa) qry.getSingleResult();
        } catch (Exception e) {
            return new Caixa();
        }
    }

    public List<Object> listaDeCheques(Integer id_status) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT c.id, \n"
                    + "     f.id_baixa AS id_baixa, \n"
                    + "     banc.nr_num_banco, \n"
                    + "     ds_agencia, \n "
                    + "     ds_conta, \n "
                    + "     ds_cheque, \n "
                    + "     dt_emissao, \n "
                    + "     dt_vencimento, \n "
                    + "     f.nr_valor, \n"
                    + "     f.id \n "
                    + "  FROM fin_cheque_rec AS c \n "
                    + " INNER JOIN fin_forma_pagamento AS f ON f.id_cheque_rec = c.id AND f.id_status = " + id_status + " \n "
                    + " INNER JOIN fin_banco AS banc ON banc.id = c.id_banco \n "
                    + " WHERE dt_vencimento <= CURRENT_DATE "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferenciaDinheiro(int id_fechamento_caixa, int id_caixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT tc "
                    + "  FROM TransferenciaCaixa tc "
                    + " WHERE tc.fechamentoEntrada.id = " + id_fechamento_caixa
                    + "   AND tc.caixaEntrada.id = " + id_caixa
            );
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferenciaDinheiroEntrada(int id_fechamento_caixa, int id_caixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT tc "
                    + "  FROM TransferenciaCaixa tc "
                    + " WHERE tc.fechamentoEntrada.id = " + id_fechamento_caixa
                    + "   AND tc.caixaEntrada.id = " + id_caixa
            );
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<TransferenciaCaixa> listaTransferenciaDinheiroSaida(int id_fechamento_caixa, int id_caixa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT tc "
                    + "  FROM TransferenciaCaixa tc "
                    + " WHERE tc.fechamentoSaida.id = " + id_fechamento_caixa
                    + "   AND tc.caixaSaida.id = " + id_caixa
            );
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<FormaPagamento> listaTransferenciaFormaPagamento(int id_fechamento_caixa, int id_caixa, String es) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT fp "
                    + "  FROM FormaPagamento fp "
                    + " WHERE fp.baixa.id IN ( "
                    + "   SELECT m.baixa.id FROM Movimento m WHERE m.baixa.caixa.id = " + id_caixa + " AND m.baixa.fechamentoCaixa.id = " + id_fechamento_caixa + " AND m.es = '" + es + "'"
                    //+ "   SELECT b.id FROM Baixa b WHERE b.caixa.id = "+id_caixa+" AND b.fechamentoCaixa.id = "+id_fechamento_caixa
                    + " ) "
            );
            return qry.getResultList();

        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> pesquisaSaldoAtual(int id_caixa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor, fc.dt_data as data	"
                    + "	  FROM fin_fechamento_caixa fc "
                    + "	 INNER JOIN fin_baixa b ON b.id_fechamento_caixa = fc.id "
                    + "	 WHERE b.id_caixa = " + id_caixa
                    + "	 GROUP BY fc.id "
                    + " UNION "
                    + "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor, fc.dt_data as data "
                    + "	  FROM fin_fechamento_caixa fc "
                    + "	 INNER JOIN fin_transferencia_caixa tc ON tc.id_fechamento_entrada = fc.id "
                    + "	 WHERE tc.id_caixa_entrada = " + id_caixa
                    + "	 GROUP BY fc.id "
                    + "	 ORDER BY 1 DESC LIMIT 1"
            //                    "select max(x.id), sum(x.valor) from " +
            //                    "	( " +
            //                    "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor" +
            //                    "	  FROM fin_fechamento_caixa fc" +
            //                    "	 INNER JOIN fin_baixa b ON b.id_fechamento_caixa = fc.id" +
            //                    "	 WHERE b.id_caixa = " + id_caixa +
            //                    "	 GROUP BY fc.id " +
            //                    " UNION " +
            //                    "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor" +
            //                    "	  FROM fin_fechamento_caixa fc" +
            //                    "	 INNER JOIN fin_transferencia_caixa tc ON tc.id_fechamento_entrada = fc.id" +
            //                    "	 WHERE tc.id_caixa_entrada = " + id_caixa +
            //                    "	 GROUP BY fc.id" +
            //                    "	) as x"
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> pesquisaSaldoAtualRelatorio(int id_caixa, int id_fechamento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor	"
                    + "	  FROM fin_fechamento_caixa fc "
                    + "	 INNER JOIN fin_baixa b ON b.id_fechamento_caixa = fc.id "
                    + "	 WHERE b.id_caixa = " + id_caixa + " AND fc.id < " + id_fechamento
                    + "	 GROUP BY fc.id "
                    + " UNION "
                    + "	SELECT max(fc.id) as id, fc.nr_saldo_atual as valor "
                    + "	  FROM fin_fechamento_caixa fc "
                    + "	 INNER JOIN fin_transferencia_caixa tc ON tc.id_fechamento_entrada = fc.id "
                    + "	 WHERE tc.id_caixa_entrada = " + id_caixa + " AND fc.id < " + id_fechamento
                    + "	 GROUP BY fc.id "
                    + "	 ORDER BY 1 DESC LIMIT 1"
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> pesquisaUsuarioFechamento(int id_fechamento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.ds_nome "
                    + "  FROM fin_baixa b "
                    + " INNER JOIN seg_usuario u on u.id = b.id_usuario "
                    + " INNER JOIN pes_pessoa p on p.id = u.id_pessoa "
                    + " WHERE id_fechamento_caixa  = " + id_fechamento
                    + " GROUP BY p.ds_nome "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SubGrupoFinanceiro> listaSubGrupo(String id_grupo) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT SGF "
                    + "  FROM SubGrupoFinanceiro AS SGF "
                    + (id_grupo != null ? " WHERE SGF.grupoFinanceiro.id IN ( " + id_grupo + ") " : "")
                    + " ORDER BY SGF.descricao "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SubGrupoFinanceiro> listaSubGrupo(Integer id_grupo) {
        try {
            return listaSubGrupo("" + id_grupo);
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaBoletoSocioAgrupado(String responsavel, String lote, String data, String tipo, String documento) {

        String text_qry = "", where = "", inner_join = "";

        if (tipo.equals("fisica")) {
            inner_join = " INNER JOIN pes_fisica f ON f.id_pessoa = b.codigo \n ";
        } else if (tipo.equals("juridica")) {
            inner_join = " INNER JOIN pes_juridica j ON j.id_pessoa = b.codigo \n ";
        }

        where = " WHERE b.ativo = true \n ";

//        // IF temporário... excluir
//        if (tipo.equals("fisica")){
//            where += " AND b.codigo IN (SELECT id FROM xextrato) ";
//        }
        // DOCUMENTO --
        if (!documento.isEmpty()) {
//            if (tipo.equals("fisica")){
//                inner_join = " INNER JOIN pes_pessoa p ON p.id = f.id_pessoa ";
//            }else if (tipo.equals("juridica")){
//                inner_join = " INNER JOIN pes_pessoa p ON p.id = j.id_pessoa ";
//            }

            inner_join += " INNER JOIN pes_pessoa p ON p.id = b.codigo \n ";
            where += " AND p.ds_documento = '" + documento + "' \n ";
        }

        // RESPONSAVEL --
        if (!responsavel.isEmpty()) {
            responsavel = AnaliseString.normalizeLower(responsavel);
            where += " AND TRANSLATE(LOWER(b.responsavel)) like '%" + responsavel + "%' \n ";
        }

        // LOTE --
        if (!lote.isEmpty()) {
            where += " AND b.id_lote_boleto = " + Integer.valueOf(lote) + " \n ";

        }

        // DATA --
        if (!data.isEmpty()) {
            where += " AND b.processamento = '" + data + "' \n ";
        }

        text_qry
                = " SELECT b.nr_ctr_boleto, \n "
                + "      b.id_lote_boleto, \n "
                + "      b.responsavel, \n "
                + "      b.boleto, \n "
                + "      to_char(b.vencimento,'dd/MM/yyyy') as vencimento, \n "
                + "      to_char(b.processamento,'dd/MM/yyyy') as processamento, \n "
                + "      sum(b.valor) as valor, \n "
                + "      b.endereco_responsavel, \n "
                + "      b.codigo \n "
                + "   FROM soc_boletos_vw b " + inner_join + where
                + "  GROUP BY \n "
                + "      b.nr_ctr_boleto, \n "
                + "      b.id_lote_boleto, \n "
                + "      b.responsavel, \n "
                + "      b.boleto, \n "
                + "      b.vencimento, \n "
                + "      b.processamento, \n "
                + "      b.endereco_responsavel, \n "
                + "      b.codigo \n "
                + "  ORDER BY \n "
                + "      b.responsavel, \n "
                + "      b.vencimento desc";

        try {
            Query qry = getEntityManager().createNativeQuery(text_qry);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }

    }

    public List<Vector> listaBoletoSocioFisica(String nr_ctr_boleto, String view) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT "
                    + "     id_fin_lote, \n "
                    + // 0
                    "       id_fin_movimento, \n "
                    + // 1
                    "       nr_ctr_boleto, \n "
                    + // 2
                    "       id_lote_boleto, \n "
                    + // 3
                    "       processamento, \n "
                    + // 4
                    "       codigo, \n "
                    + // 5
                    "       responsavel, \n "
                    + // 6
                    "       vencimento, \n "
                    + // 7
                    "       matricula, \n "
                    + // 8
                    "       grupo_categoria, \n "
                    + // 9
                    "       categoria, \n "
                    + // 10
                    "       servico, \n "
                    + // 11
                    "       id_beneficiario, \n "
                    + // 12
                    "       nome_beneficiario, \n "
                    + // 13
                    "       valor, \n "
                    + // 14
                    "       mensalidades_corrigidas, \n "
                    + // 15
                    "       mensagem_boleto, \n "
                    + // 16
                    "       banco, \n "
                    + // 17
                    "       agencia, \n "
                    + // 18
                    "       cedente, \n "
                    + // 19
                    "       boleto, \n "
                    + // 20
                    "       email, \n "
                    + // 21
                    "       nome_filial, \n "
                    + // 22
                    "       site_filial, \n "
                    + // 23
                    "       cnpj_filial, \n "
                    + // 24
                    "       tel_filial, \n "
                    + // 25
                    "       endereco_filial, \n "
                    + // 26
                    "       bairro_filial, \n "
                    + // 27
                    "       cidade_filial, \n "
                    + // 28
                    "       uf_filial, \n "
                    + // 29
                    "       cep_filial, \n "
                    + // 30
                    "       logradouro_responsavel, \n "
                    + // 31
                    "       endereco_responsavel, \n "
                    + // 32
                    "       cep_responsavel, \n "
                    + // 33
                    "       uf_responsavel, \n "
                    + // 34
                    "       cidade_responsavel, \n "
                    + // 35
                    "       informativo, \n "
                    + // 36
                    "       local_pagamento, \n "
                    + // 37
                    "       vencimento_movimento, \n "
                    + // 38
                    "       vencimento_boleto, \n "
                    + // 39
                    "       vencimento_original_boleto \n  "
                    + // 40
                    "   FROM " + view + " \n "
                    + "  WHERE nr_ctr_boleto IN ('" + nr_ctr_boleto + "') \n "
                    + "  ORDER BY responsavel, nome_titular, vencimento_movimento, codigo, nome_beneficiario "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaBoletoSocioJuridica(String nr_ctr_boleto, String view) {
        try {
            String text_qry
                    = " SELECT "
                    + "       0 as id_lotex, \n "
                    + "       0 as id_movimentox, \n "
                    + "       nr_ctr_boleto, \n  "
                    + "       id_lote_boleto, \n  "
                    + "       processamento, \n  "
                    + "       codigo, \n "
                    + "       responsavel, \n "
                    + "       vencimento, \n "
                    + "       matricula, \n "
                    + "       grupo_categoria, \n "
                    + "       categoria, \n "
                    + "       '', \n "
                    + "       codigo_titular, \n "
                    + "       nome_titular, \n "
                    + "       SUM(valor_sem_acrescimo), \n "
                    + "       mensalidades_corrigidas, \n "
                    + "       mensagem_boleto, \n "
                    + "       banco, \n "
                    + "       agencia, \n "
                    + "       cedente, \n "
                    + "       boleto, \n "
                    + "       email, \n "
                    + "       nome_filial, \n "
                    + "       site_filial, \n "
                    + "       cnpj_filial, \n "
                    + "       tel_filial, \n "
                    + "       endereco_filial, \n "
                    + "       bairro_filial, \n "
                    + "       cidade_filial, \n "
                    + "       uf_filial, \n "
                    + "       cep_filial, \n "
                    + "       logradouro_responsavel, \n "
                    + "       endereco_responsavel, \n "
                    + "       cep_responsavel, \n "
                    + "       uf_responsavel, \n "
                    + "       cidade_responsavel, \n "
                    + "       informativo, \n "
                    + "       local_pagamento,  \n "
                    + "       vencimento_movimento, \n  "
                    + "       vencimento_boleto,  \n "
                    + "       vencimento_original_boleto  \n "
                    + "   FROM " + view + " \n "
                    + "  WHERE nr_ctr_boleto IN ('" + nr_ctr_boleto + "')  \n "
                    + "  GROUP BY  \n "
                    + //"       id_fin_lote, " +
                    //"       id_fin_movimento, " +
                    "       nr_ctr_boleto,  \n "
                    + "       id_lote_boleto,  \n "
                    + "       processamento,  \n "
                    + "       codigo, \n "
                    + "       responsavel, \n "
                    + "       vencimento, \n "
                    + "       matricula, \n "
                    + "       grupo_categoria, \n "
                    + "       categoria, \n "
                    + //"       servico," +
                    "       codigo_titular, \n "
                    + "       nome_titular, \n "
                    + "       mensalidades_corrigidas, \n "
                    + "       mensagem_boleto, \n "
                    + "       banco, \n "
                    + "       agencia, \n "
                    + "       cedente, \n "
                    + "       boleto, \n "
                    + "       email, \n "
                    + "       nome_filial, \n "
                    + "       site_filial, \n "
                    + "       cnpj_filial, \n "
                    + "       tel_filial, \n "
                    + "       endereco_filial, \n "
                    + "       bairro_filial, \n "
                    + "       cidade_filial, \n "
                    + "       uf_filial, \n "
                    + "       cep_filial, \n "
                    + "       logradouro_responsavel, \n "
                    + "       endereco_responsavel, \n "
                    + "       cep_responsavel, \n "
                    + "       uf_responsavel, \n "
                    + "       cidade_responsavel, \n "
                    + "       informativo, \n "
                    + "       local_pagamento,  \n "
                    + "       vencimento_movimento, \n  "
                    + "       vencimento_boleto,  \n "
                    + "       vencimento_original_boleto  \n "
                    + "  ORDER BY responsavel, nome_titular, vencimento_movimento, codigo, nome_titular ";

            Query qry = getEntityManager().createNativeQuery(text_qry);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaBoletoSocioJuridicaAgrupado(String nr_ctr_boleto) {
        try {
            String text_qry
                    = " SELECT codigo "
                    + "   FROM soc_boletos_vw "
                    + "  WHERE nr_ctr_boleto IN ('" + nr_ctr_boleto + "') "
                    + "  GROUP BY codigo";

            Query qry = getEntityManager().createNativeQuery(text_qry);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaQntPorJuridica(String nrCtrBoleto) {
        Query qry = getEntityManager().createNativeQuery(
                "select j.id_pessoa, m.id_titular "
                + "  from fin_movimento m "
                + " inner join pes_juridica j on j.id_pessoa = m.id_pessoa "
                + " where m.nr_ctr_boleto = '" + nrCtrBoleto + "' and m.is_ativo = true"
                + "  group by j.id_pessoa, m.id_titular"
        );
        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Vector> listaQntPorFisica(String nrCtrBoleto) {
        Query qry = getEntityManager().createNativeQuery(
                "select f.id_pessoa, m.id_titular "
                + "  from fin_movimento m "
                + " inner join pes_fisica f on f.id_pessoa = m.id_pessoa "
                + " where m.nr_ctr_boleto = '" + nrCtrBoleto + "' and m.is_ativo = true"
                + "  group by f.id_pessoa, m.id_titular"
        );
        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Vector> listaServicosSemCobranca() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT "
                    + "  se.id AS id_servico, "
                    + "  se.ds_descricao AS servico, "
                    + "  t.id AS id_tipo, "
                    + "  t.ds_descricao AS tipo "
                    + "  FROM fin_servicos AS se "
                    + " INNER JOIN fin_tipo_servico AS t ON t.id > 0 "
                    + " WHERE 's'||se.id||'t'||t.id NOT IN (SELECT 's'||id_servicos||'t'||id_tipo_servico FROM fin_servico_conta_cobranca) "
                    + "   AND se.id NOT IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4) "
                    + "   AND se.id NOT IN (6,7,8,10,11) "
                    + " ORDER BY se.id, se.ds_descricao, t.id, t.ds_descricao "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaPessoaSemComplemento(String referenciaVigoracao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.id, p.ds_nome "
                    + "  FROM pes_pessoa AS p "
                    + "  LEFT JOIN pes_pessoa_complemento AS c ON c.id_pessoa = p.id "
                    + "  LEFT JOIN fin_servico_pessoa AS sp ON sp.id_cobranca = p.id "
                    + " WHERE c.id IS NULL "
                    + "   AND (ds_ref_vigoracao = '' OR( ds_ref_vigoracao != '' AND "
                    + "                CAST(RIGHT(ds_ref_vigoracao, 4) || LEFT(ds_ref_vigoracao, 2) AS INT) <= "
                    + "                CAST(RIGHT('" + referenciaVigoracao + "', 4) || LEFT('" + referenciaVigoracao + "',2) AS INT) "
                    + "      ) "
                    + "   ) "
                    + "   AND (ds_ref_validade = '' OR( ds_ref_validade != '' AND "
                    + "                CAST(RIGHT(ds_ref_validade, 4) || LEFT(ds_ref_validade, 2) AS INT) > "
                    + "                CAST(RIGHT('" + referenciaVigoracao + "', 4) || LEFT('" + referenciaVigoracao + "', 2) AS INT) "
                    + "   ) "
                    + ") "
                    + " GROUP BY p.id, p.ds_nome "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaRelatorioAnalitico(Integer id_fechamento_caixa) {
        try {
            String text = "SELECT "
                    + "cx.ds_descricao caixa, \n"
                    + "b.dt_baixa, \n"
                    + "b.id as lote_baixa, \n"
                    + "pu.ds_nome as operador, \n"
                    + "pr.ds_nome as responsavel, \n"
                    + "pt.ds_nome as titular, \n"
                    + "pb.ds_nome as beneficiario, \n"
                    + "se.ds_descricao as servico, \n"
                    + "m.ds_es AS operacao, \n"
                    + "func_es(m.ds_es,m.nr_valor) as valor, \n"
                    + "func_es(m.ds_es,m.nr_valor_baixa) valor_baixa, \n"
                    + "m.id, \n "
                    + "f.dt_data as fechamento \n "
                    + "from fin_movimento as m \n"
                    + "inner join fin_baixa as b on b.id=m.id_baixa \n"
                    + "inner join fin_caixa as cx on cx.id=b.id_caixa \n"
                    + "inner join fin_servicos as se on se.id=m.id_servicos \n"
                    + "inner join pes_pessoa as pr on pr.id=m.id_pessoa \n"
                    + "inner join pes_pessoa as pt on pt.id=m.id_titular \n"
                    + "inner join pes_pessoa as pb on pb.id=m.id_beneficiario \n"
                    + "inner join seg_usuario as u on u.id = b.id_usuario \n"
                    + "inner join pes_pessoa as pu on pu.id=u.id_pessoa \n "
                    + "inner join fin_fechamento_caixa as f on f.id = b.id_fechamento_caixa  \n "
                    + "where b.id_fechamento_caixa= " + id_fechamento_caixa + " \n"
                    + "\n"
                    + "---transferencia entrada \n"
                    + "union \n"
                    + "\n"
                    + "select \n"
                    + "cxe.ds_descricao as caixa, \n"
                    + "t.dt_lancamento as dt_baixa, \n"
                    + "null as lote_baixa, \n"
                    + "pu.ds_nome as operador, \n"
                    + "'' as responsavel, \n"
                    + "'' as titular, \n"
                    + "cxs.ds_descricao||' para '||cxe.ds_descricao  as beneficiario, \n"
                    + "'TRANSFERÊNCIA ENTRE CAIXAS' as servico, \n"
                    + "'E' AS operacao, \n"
                    + "t.nr_valor as valor, \n"
                    + "t.nr_valor as valor_baixa, \n"
                    + "0, \n"
                    + "f.dt_data as fechamento \n"
                    + "from fin_transferencia_caixa as t \n"
                    + "inner join fin_caixa as cxs on cxs.id=id_caixa_saida \n"
                    + "inner join fin_caixa as cxe on cxe.id=id_caixa_entrada \n"
                    + "inner join seg_usuario as u on u.id=t.id_usuario \n"
                    + "inner join pes_pessoa as pu on pu.id=u.id_pessoa \n"
                    + "inner join fin_fechamento_caixa as f on f.id = t.id_fechamento_entrada \n"
                    + "where t.id_fechamento_entrada=" + id_fechamento_caixa + " \n"
                    + "\n"
                    + "---transferencia saida \n"
                    + "\n"
                    + "union \n"
                    + "\n"
                    + "select \n"
                    + "cxs.ds_descricao as caixa, \n"
                    + "t.dt_lancamento as dt_baixa, \n"
                    + "null as lote_baixa, \n"
                    + "pu.ds_nome as operador, \n"
                    + "'' as responsavel, \n"
                    + "'' as titular, \n"
                    + "cxs.ds_descricao||' para '||cxe.ds_descricao  as beneficiario, \n"
                    + "'TRANSFERÊNCIA ENTRE CAIXAS' as servico, \n"
                    + "'S' AS operacao, \n"
                    + "func_es('S',t.nr_valor) as valor, \n"
                    + "func_es('S',t.nr_valor) as valor_baixa, \n"
                    + "0, \n"
                    + "f.dt_data as fechamento \n"
                    + "from fin_transferencia_caixa as t \n"
                    + "inner join fin_caixa as cxs on cxs.id=id_caixa_saida \n"
                    + "inner join fin_caixa as cxe on cxe.id=id_caixa_entrada \n"
                    + "inner join seg_usuario as u on u.id=t.id_usuario \n"
                    + "inner join pes_pessoa as pu on pu.id=u.id_pessoa \n"
                    + "inner join fin_fechamento_caixa as f on f.id = t.id_fechamento_saida \n"
                    + "where t.id_fechamento_saida=" + id_fechamento_caixa + " \n"
                    + "order by 3,4,5,6 ";

            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaResumoFechamentoCaixa(String data) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT \n "
                    + "  f.dt_data, \n "
                    + "  m.ds_es AS operacao, \n "
                    + "  g.ds_descricao AS grupo, \n "
                    + "  sg.ds_descricao AS subgrupo, \n "
                    + "  se.ds_descricao AS servico, \n "
                    + "  SUM(func_es(m.ds_es, m.nr_valor_baixa)) valor_baixa, \n "
                    + "  SUM(m.nr_quantidade) AS quantidade_total, \n"
                    + "  SUM(func_quantidade_zero(m.nr_quantidade, m.nr_valor_baixa, 0)) AS quantidade_valor_zero, \n"
                    + "  SUM(func_quantidade_zero(m.nr_quantidade, m.nr_valor_baixa, 1)) AS quantidade_valor_pago \n"
                    + " FROM fin_movimento AS m \n "
                    + "INNER JOIN fin_baixa AS b ON b.id = m.id_baixa \n "
                    + "INNER JOIN fin_caixa AS cx ON cx.id = b.id_caixa \n "
                    + "INNER JOIN fin_servicos AS se ON se.id = m.id_servicos \n "
                    + " LEFT JOIN fin_subgrupo AS sg ON sg.id = se.id_subgrupo \n "
                    + " LEFT JOIN fin_grupo AS g ON g.id = sg.id_grupo \n "
                    + "INNER JOIN pes_pessoa AS pr ON pr.id = m.id_pessoa \n "
                    + "INNER JOIN pes_pessoa AS pt ON pt.id = m.id_titular \n "
                    + "INNER JOIN pes_pessoa AS pb ON pb.id = m.id_beneficiario \n "
                    + "INNER JOIN seg_usuario AS u ON u.id = b.id_usuario \n "
                    + "INNER JOIN pes_pessoa AS pu ON pu.id = u.id_pessoa \n "
                    + "INNER JOIN fin_fechamento_caixa AS f ON f.id = b.id_fechamento_caixa \n "
                    + "WHERE f.dt_data='" + data + "' \n "
                    + "GROUP BY \n "
                    + "  f.dt_data, \n "
                    + "  m.ds_es, \n "
                    + "  g.ds_descricao, \n "
                    + "  sg.ds_descricao, \n "
                    + "  se.ds_descricao \n "
                    + " \n "
                    + " ---transferencia entrada \n "
                    + "UNION \n"
                    + " \n "
                    + "SELECT \n "
                    + "  f.dt_data, \n "
                    + "  'E', \n "
                    + "  '', \n "
                    + "  '', \n "
                    + "  'TRANSFERÊNCIA ENTRE CAIXAS', \n "
                    + "  SUM(func_es('E', t.nr_valor)) AS valor_baixa, \n "
                    + "  0, \n "
                    + "  0, \n "
                    + "  0 \n "
                    + " FROM fin_transferencia_caixa AS t \n "
                    + "INNER JOIN fin_caixa AS cxs ON cxs.id = id_caixa_saida \n "
                    + "INNER JOIN fin_caixa AS cxe ON cxe.id = id_caixa_entrada \n "
                    + "INNER JOIN seg_usuario AS u ON u.id = t.id_usuario \n "
                    + "INNER JOIN pes_pessoa AS pu ON pu.id = u.id_pessoa \n "
                    + "INNER JOIN fin_fechamento_caixa AS f ON f.id = t.id_fechamento_entrada \n "
                    + "WHERE f.dt_data='" + data + "' \n "
                    + "GROUP BY f.dt_data \n "
                    + " \n "
                    + "---transferencia saida \n "
                    + " \n "
                    + "UNION \n "
                    + " \n "
                    + "SELECT \n "
                    + " \n "
                    + "  f.dt_data, \n "
                    + "  'S', \n "
                    + "  '', \n "
                    + "  '', \n "
                    + "  'TRANSFERÊNCIA ENTRE CAIXAS', \n "
                    + "  SUM(func_es('S', t.nr_valor)) AS valor_baixa, \n "
                    + "  0, \n "
                    + "  0, \n "
                    + "  0 \n "
                    + " FROM fin_transferencia_caixa AS t \n "
                    + "INNER JOIN fin_caixa AS cxs ON cxs.id = id_caixa_saida \n "
                    + "INNER JOIN fin_caixa AS cxe ON cxe.id = id_caixa_entrada \n "
                    + "INNER JOIN seg_usuario AS u ON u.id = t.id_usuario \n"
                    + "INNER JOIN pes_pessoa AS pu ON pu.id = u.id_pessoa \n"
                    + "INNER JOIN fin_fechamento_caixa AS f ON f.id = t.id_fechamento_saida \n"
                    + "WHERE f.dt_data='" + data + "' AND f.id <> 1 \n"
                    + "GROUP BY f.dt_data \n "
                    + "ORDER BY 1,2,3,4,5 "
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

//        NAO USA --- EXCLUIR DEPOIS DE 01/04/2015
//    
//    public List<Vector> listaPessoaFisicaSemEndereco(int mes, int ano) {
//        try {
//            Query qry = getEntityManager().createNativeQuery(
//                    "SELECT m.id_pessoa, p.ds_nome " +
//                    "  FROM fin_movimento m " +
//                    " INNER JOIN pes_fisica f ON f.id_pessoa = m.id_pessoa " +
//                    " INNER JOIN pes_pessoa p ON p.id = f.id_pessoa " +
//                    " WHERE EXTRACT(MONTH FROM dt_vencimento) = " + mes +
//                    "   AND EXTRACT(YEAR FROM dt_vencimento) = " + ano +
//                    "   AND m.is_ativo = true " +
//                    "   AND m.id_pessoa NOT IN ( " +
//                    "       SELECT f.id_pessoa FROM soc_boletos_vw b " +
//                    "       INNER JOIN pes_fisica f ON f.id_pessoa = b.codigo " +
//                    "   )"
//            );
//            return qry.getResultList();
//        } catch (Exception e) {
//            return new ArrayList();
//        }
//    }
//    
//    
//    public List<Vector> listaPessoaJuridicaSemEndereco(int mes, int ano) {
//        try {
//            Query qry = getEntityManager().createNativeQuery(
//                    "SELECT m.id_pessoa, p.ds_nome " +
//                    "  FROM fin_movimento m " +
//                    " INNER JOIN pes_juridica j ON j.id_pessoa = m.id_pessoa " +
//                    " INNER JOIN pes_pessoa p ON p.id = j.id_pessoa " +
//                    " WHERE EXTRACT(MONTH FROM m.dt_vencimento) = " + mes +
//                    "   AND EXTRACT(YEAR FROM m.dt_vencimento) = " + ano +
//                    "   AND m.is_ativo = true " +
//                    "   AND m.id_pessoa NOT IN ( " +
//                    "       SELECT f.id_pessoa FROM soc_boletos_vw b " +
//                    "	INNER JOIN pes_juridica f ON f.id_pessoa = b.codigo " +
//                    "   ) " +
//                    " GROUP BY m.id_pessoa, p.ds_nome "
//            );
//            return qry.getResultList();
//        } catch (Exception e) {
//            return new ArrayList();
//        }
//    }
    public List<LoteBoleto> listaLoteBoleto() {
        String text = "SELECT lb.* \n"
                + "  FROM soc_lote_boleto lb \n"
                + " -- INNER JOIN soc_boletos_vw b ON b.id_lote_boleto = lb.id \n"
                + " -- WHERE b.ativo = true \n"
                + " GROUP BY lb.id, lb.dt_processamento \n"
                + " ORDER BY lb.id DESC LIMIT 50";

        Query qry = getEntityManager().createNativeQuery(text, LoteBoleto.class);

        try {
            List<LoteBoleto> result = qry.getResultList();
            return result;
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Caixa pesquisaCaixaUsuario(int id_usuario, int id_filial) {
        String text = "SELECT c "
                + "  FROM Caixa c "
                + " WHERE c.usuario.id = " + id_usuario
                + "   AND c.filial.id = " + id_filial;

        Query qry = getEntityManager().createQuery(text);

        try {
            Caixa result = (Caixa) qry.getSingleResult();
            return result;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Vector> listaFechamentoCaixaGeral() {
        String text
                = "SELECT \n"
                + " caixa, \n"
                + " dt_fechamento, \n"
                + " hora_fechamento, \n"
                + " dt_transferencia, \n"
                + " sum(valor) as valor, \n"
                + " id_fechamento_caixa, \n"
                + " id_caixa \n"
                + "  FROM  fin_fecha_caixa_geral_vw \n"
                + " GROUP BY \n"
                + " caixa, \n"
                + " dt_fechamento, \n"
                + " hora_fechamento, \n"
                + " dt_transferencia, \n"
                + " id_fechamento_caixa, \n"
                + " id_caixa \n"
                + " ORDER BY dt_fechamento, caixa ";

        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Vector> listaDetalhesFechamentoCaixaGeral(Integer id_caixa, Integer id_fechamento) {
        String text;

        if (id_fechamento != null) {
            text = "SELECT operador, sum(valor) FROM fin_fecha_caixa_geral_vw WHERE id_caixa = " + id_caixa + " AND id_fechamento_caixa = " + id_fechamento + " GROUP BY operador ";
        } else {
            text = "SELECT operador, sum(valor) FROM fin_fecha_caixa_geral_vw WHERE id_caixa = " + id_caixa + " AND id_fechamento_caixa IS NULL GROUP BY operador ";
        }

        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public String dataFechamentoCaixa(Integer id_caixa) {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT MIN(dt_baixa) FROM fin_baixa WHERE id_caixa = " + id_caixa + " AND id_fechamento_caixa IS NULL AND dt_baixa >= '01/04/2015'");
            List<Object> result = qry.getResultList();
            if (!result.isEmpty() && ((List) result.get(0)).get(0) != null) {
                return DataHoje.converteData((Date) ((List) result.get(0)).get(0));
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    public List<FStatus> listaFStatusIn(String ids) {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT s FROM FStatus s WHERE s.id IN (" + ids + ")"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Object listaQuantidadeCaixasAberto(String data_fechamento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT CAST(COUNT(*) AS INT) \n"
                    + "  FROM fin_baixa AS b \n"
                    + " INNER JOIN fin_caixa c ON c.id = b.id_caixa \n"
                    + " WHERE b.id_caixa IS NOT NULL \n"
                    + "   AND b.id_fechamento_caixa IS NULL \n"
                    + "   AND b.dt_baixa <= '" + data_fechamento + "'  \n"
                    + "   AND c.nr_caixa <> 1"
            );
            return qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Object> listaContasParaTransferencia(Integer id_conta) {
        try {
            String text
                    = "SELECT p.id_p5, \n"
                    + "       p.conta5 \n"
                    + "  FROM plano_vw AS p \n"
                    + " INNER JOIN fin_conta_rotina AS cr ON cr.id_plano4 = p.id_p4 \n"
                    + " WHERE cr.id_rotina = 2 \n"
                    + (id_conta != null ? " AND p.id_p5 <> " + id_conta : "")
                    + " ORDER BY p.conta5";

            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Banco> listaDeBancos() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT b.* \n"
                + "  FROM fin_banco b \n"
                + " WHERE b.id > 0 \n"
                + " ORDER BY b.nr_num_banco", Banco.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Plano5> listaContas() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT id_plano5, \n"
                + "       conta \n"
                + "  FROM caixa_banco_vw \n"
                + " WHERE id_plano5 <> 1 \n"
                + " ORDER BY conta"
        );

        try {
            List<Object> result = qry.getResultList();
            List<Plano5> lista = new ArrayList();
            Dao dao = new Dao();

            for (Object ob : result) {
                List linha = (List) ob;
                lista.add((Plano5) dao.find(new Plano5(), linha.get(0)));
            }
            return lista;
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Plano5> listaContasBaixa() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT id_p5, \n"
                + "       conta5 \n"
                + "  FROM plano_vw \n"
                + " WHERE id_p5 NOT IN (SELECT id_plano5 FROM caixa_banco_vw WHERE id_plano5 <> 1 ORDER BY conta) \n"
                + "   AND REPLACE(UPPER(conta1),' ','') LIKE '%ATIVO%' \n"
                + "   AND REPLACE(UPPER(conta3),' ','') NOT LIKE '%IMOBILIZADO%' ORDER BY classificador "
        );

        try {
            List<Object> result = qry.getResultList();
            List<Plano5> lista = new ArrayList();
            Dao dao = new Dao();

            for (Object ob : result) {
                List linha = (List) ob;
                lista.add((Plano5) dao.find(new Plano5(), linha.get(0)));
            }
            return lista;
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaContasDespesa() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT id_p5, \n "
                + "     LEFT(conta4||LPAD(' ', 40), 40)||conta5 AS conta \n "
                + "  FROM plano_vw \n"
                + " WHERE REPLACE(UPPER(conta1),' ','') LIKE '%DESPESA%' \n "
                + " ORDER BY conta4, conta5"
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
