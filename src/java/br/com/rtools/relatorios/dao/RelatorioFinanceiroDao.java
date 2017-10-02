package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioGrupo;
import br.com.rtools.relatorios.RelatorioJoin;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class RelatorioFinanceiroDao extends DB {

    public List<Object> listaRelatorioFinanceiro(String ids_contabil, Integer id_grupo, Integer id_sub_grupo, Integer id_servicos, String dataEmissao, String dataEmissaoFinal, String dataVencimento, String dataVencimentoFinal, String dataQuitacao, String dataQuitacaoFinal, String dataImportacao, String dataImportacaoFinal, String dataCredito, String dataCreditoFinal, String dataFechamentoCaixa, String dataFechamentoCaixaFinal, Integer id_caixa_banco, String tipo_caixa, Integer id_caixa, Integer id_operador, Integer id_tipo_quitacao, String tipo_departamento, String tipo_es, String tipo_pessoa, String tipo_situacao, String order, Relatorios relatorio) {
//        String select
//                = "SELECT \n "
//                + "       grupo, \n "
//                + "       subgrupo, \n "
//                + "       servico, \n "
//                + "       sum(valor_baixa) \n "
//                + "  FROM movimentos_vw \n ";
        String select = " -- RelatorioFinanceiroDao->listaRelatorioFinanceiro() \n"
                + " SELECT ";

        List<RelatorioParametros> listaRL = new RelatorioDao().listaRelatorioParametro(relatorio.getId());

        if (!listaRL.isEmpty()) {
            String s = "";
            for (RelatorioParametros rp : listaRL) {
                if (s.isEmpty()) {
                    s = rp.getParametro() + " AS " + rp.getApelido();
                } else {
                    s += ", " + " \n " + rp.getParametro() + " AS " + rp.getApelido();
                }
            }
            select += s;
        } else {
            return new ArrayList();
        }

        select += " \n " + " FROM movimentos_vw AS m \n ";

        String join = "";
        if (!tipo_pessoa.isEmpty()) {
            switch (tipo_pessoa) {
                case "fisica":
                    join += " INNER JOIN pes_fisica pfx ON pfx.id_pessoa = m.id_pessoa \n ";
                    break;
                default:
                    join += " INNER JOIN pes_juridica pjx ON pjx.id_pessoa = m.id_pessoa \n ";
                    break;
            }
        }

        List<RelatorioJoin> listaRJ = new RelatorioDao().listaRelatorioJoin(relatorio.getId());
        if (!listaRJ.isEmpty()) {
            String j = "";
            for (RelatorioJoin rj : listaRJ) {
                j += " " + rj.getJoin() + " \n ";
            }
            join += j;
        }

        List<String> list_where = new ArrayList();

        String where = "";

        // CONTA CONTABIL ---
        if (!ids_contabil.isEmpty()) {
            list_where.add(" m.id_conta IN (" + ids_contabil + ") ");
        }

        // GRUPO ---
        if (id_grupo != null) {
            list_where.add(" m.id_grupo = " + id_grupo + " ");
        }

        // SUB GRUPO ---
        if (id_sub_grupo != null) {
            list_where.add(" m.id_subgrupo = " + id_sub_grupo + " ");
        }

        // SERVICOS ---
        if (id_servicos != null) {
            list_where.add(" m.id_servico = " + id_servicos + " ");
        }

        // DATA EMISSAO ---
        if (!dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao BETWEEN '" + dataEmissao + "' AND '" + dataEmissaoFinal + "' ");
        } else if (!dataEmissao.isEmpty() && dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao >= '" + dataEmissao + "' ");
        } else if (dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao <= '" + dataEmissaoFinal + "' ");
        }

        // DATA VENCIMENTO ---
        if (!dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento BETWEEN '" + dataVencimento + "' AND '" + dataVencimentoFinal + "' ");
        } else if (!dataVencimento.isEmpty() && dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento >= '" + dataVencimento + "' ");
        } else if (dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento <= '" + dataVencimentoFinal + "' ");
        }

        // DATA QUITAÇÃO ---
        if (!dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa BETWEEN '" + dataQuitacao + "' AND '" + dataQuitacaoFinal + "' ");
        } else if (!dataQuitacao.isEmpty() && dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa >= '" + dataQuitacao + "' ");
        } else if (dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa <= '" + dataQuitacaoFinal + "' ");
        }

        // DATA IMPORTACAO ---
        if (!dataImportacao.isEmpty() && !dataImportacaoFinal.isEmpty()) {
            list_where.add(" m.importacao BETWEEN '" + dataImportacao + "' AND '" + dataImportacaoFinal + "' ");
        } else if (!dataImportacao.isEmpty() && dataImportacaoFinal.isEmpty()) {
            list_where.add(" m.importacao >= '" + dataImportacao + "' ");
        } else if (dataImportacao.isEmpty() && !dataImportacaoFinal.isEmpty()) {
            list_where.add(" m.importacao <= '" + dataImportacaoFinal + "' ");
        }

        // DATA CREDITO ---
        if (!dataCredito.isEmpty() && !dataCreditoFinal.isEmpty()) {
            list_where.add(" m.dt_credito BETWEEN '" + dataCredito + "' AND '" + dataCreditoFinal + "' ");
        } else if (!dataCredito.isEmpty() && dataCreditoFinal.isEmpty()) {
            list_where.add(" m.dt_credito >= '" + dataCredito + "' ");
        } else if (dataCredito.isEmpty() && !dataCreditoFinal.isEmpty()) {
            list_where.add(" m.dt_credito <= '" + dataCreditoFinal + "' ");
        }

        // DATA FECHAMENTO CAIXA---
        if (!dataFechamentoCaixa.isEmpty() && !dataFechamentoCaixaFinal.isEmpty()) {
            list_where.add(" m.fechamento_caixa BETWEEN '" + dataFechamentoCaixa + "' AND '" + dataFechamentoCaixaFinal + "' ");
        } else if (!dataFechamentoCaixa.isEmpty() && dataFechamentoCaixaFinal.isEmpty()) {
            list_where.add(" m.fechamento_caixa >= '" + dataFechamentoCaixa + "' ");
        } else if (dataFechamentoCaixa.isEmpty() && !dataFechamentoCaixaFinal.isEmpty()) {
            list_where.add(" m.fechamento_caixa <= '" + dataFechamentoCaixaFinal + "' ");
        }

        // CAIXA / BANCO ---
        if (id_caixa_banco != null) {
            list_where.add(" m.id_caixa_banco = " + id_caixa_banco + " ");
        }

        // CAIXA ---
        if (!tipo_caixa.isEmpty()) {
            if (tipo_caixa.equals("com") && id_caixa != null) {
                list_where.add(" m.id_caixa = " + id_caixa + " ");
            } else {
                list_where.add(" m.id_caixa IS NULL ");
            }
        }

        // OPERADOR ---
        if (id_operador != null) {
            list_where.add(" m.id_usuario_baixa = " + id_operador + " ");
        }

        // TIPO QUITAÇÃO ---
        if (id_tipo_quitacao != null) {
            list_where.add(" m.id_tipo_pagamento = " + id_tipo_quitacao + " ");
        }

        // TIPO DEPARTAMENTO ---
        if (!tipo_departamento.isEmpty()) {
            switch (tipo_departamento) {
                case "outros":
                    list_where.add(" m.id_rotina <> 4 ");
                    break;
                case "todos":
                    break;
                default:
                    // ARRECADAÇÃO
                    list_where.add(" m.id_rotina = 4  ");
                    break;
            }
        }

        // TIPO E/S ---
        if (!tipo_es.isEmpty()) {
            switch (tipo_es) {
                case "E":
                    list_where.add(" m.es = 'E' ");
                    break;
                case "S":
                    list_where.add(" m.es = 'S' ");
                    break;
            }
        }

        // PESSOA
        if (!tipo_pessoa.isEmpty()) {
            switch (tipo_pessoa) {
                case "fisica":
                    break;

                // JURIDICA
                default:
                    break;
            }
        }

        // SITUACAO
        if (!tipo_situacao.isEmpty()) {
            switch (tipo_situacao) {
                case "atrasado":
                    list_where.add(" m.baixa IS NULL AND m.vencimento < CURRENT_DATE ");
                    break;
                case "baixado":
                    list_where.add(" m.baixa IS NOT NULL ");
                    break;
                // aberto
                default:
                    list_where.add(" m.baixa IS NULL ");
                    break;
            }
        }

        if (list_where.isEmpty()) {
            return new ArrayList();
        }

        select += join;

        for (String linha : list_where) {
            if (where.isEmpty()) {
                where = " WHERE " + linha + " \n";
            } else {
                where += " AND " + linha + " \n";
            }
        }

        List<RelatorioGrupo> listaRG = new RelatorioDao().listaRelatorioGrupo(relatorio.getId());
        String group = "";
        if (!listaRG.isEmpty()) {
            String g = "";
            for (RelatorioGrupo rg : listaRG) {
                if (g.isEmpty()) {
                    g = rg.getGrupo() + " \n ";
                } else {
                    g += ", " + rg.getGrupo() + " \n ";
                }
            }
            group = " GROUP BY \n ";
            group += g;
        }

        if (!order.isEmpty()) {
            order = " ORDER BY " + order;
//                + "       grupo,\n "
//                + "       subgrupo, \n "
//                + "       servico "
        }        
        Debugs.put("habilitaDebugQuery", select + where + group + order);
        Query qry = getEntityManager().createNativeQuery(select + where + group + order);

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }

    public List<Servicos> listaServicosSubGrupo(Integer id_subgrupo) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT s.* \n "
                + "  FROM fin_servicos s \n "
                + " WHERE s.ds_situacao = 'A' \n "
                + (id_subgrupo != null ? "   AND s.id_subgrupo = " + id_subgrupo : "") + " \n "
                + " ORDER BY s.ds_descricao", Servicos.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Usuario> listaUsuario() {
        Query qry = getEntityManager().createQuery(
                "SELECT u "
                + "  FROM Usuario u "
                + " ORDER BY u.pessoa.nome"
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<TipoPagamento> listaTipoQuitacao() {
        Query qry = getEntityManager().createQuery(
                "SELECT tp "
                + "  FROM TipoPagamento tp "
                + " ORDER BY tp.descricao"
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object> listaPlanos() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT id_p1, conta1, id_p2, conta2, id_p3, conta3, id_p4, conta4 \n "
                + "  FROM plano_vw \n "
                + " GROUP BY id_p1, conta1, id_p2, conta2, id_p3, conta3, id_p4, conta4, classificador \n "
                + " ORDER BY classificador "
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Plano5> listaPlano5(String ids) {
        if (ids.isEmpty()) {
            return new ArrayList();
        }

        Query qry = getEntityManager().createNativeQuery(
                "SELECT p5.* \n"
                + "  FROM fin_plano5 p5 \n "
                + " WHERE p5.id_plano4 IN (" + ids + ") \n"
                + " ORDER BY p5.ds_classificador", Plano5.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Plano5> listaCaixaBanco() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT pl5.* \n "
                + "  FROM fin_plano5 pl5 \n "
                + " WHERE pl5.id = 1 OR pl5.id_conta_banco > 0 \n "
                + " ORDER BY pl5.id_conta_banco DESC, pl5.ds_conta", Plano5.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Vector> listaChequesRecebidos(String ids_filial, String ids_caixa, String tipo_data, String data_inicial, String data_final, Integer id_status, String in_plano5) {
        String text = " -- RelatorioFinanceiroDao->listaChequesRecebidos()    \n\n"
                + " SELECT "
                + "     j.ds_fantasia AS filial, \n "
                + "     ch.dt_emissao AS emissao, \n "
                + "     ch.dt_vencimento AS vencimento, \n "
                + "     banc.nr_num_banco AS banco, \n "
                + "     ch.ds_agencia AS agencia, \n "
                + "     ch.ds_conta AS conta, \n "
                + "     ch.ds_cheque AS cheque, \n "
                + "     f.nr_valor AS valor, \n "
                + "     b.id AS id_baixa, \n "
                + "     cx.nr_caixa||'/'||cx.ds_descricao AS caixa \n "
                + " FROM fin_cheque_rec AS ch \n "
                + "INNER JOIN fin_forma_pagamento AS f ON f.id_cheque_rec = ch.id AND f.id_baixa = func_idBaixa_cheque_rec(ch.id) \n "
                + "INNER JOIN fin_baixa AS b ON b.id = f.id_baixa \n "
                + "INNER JOIN fin_caixa AS cx ON cx.id = b.id_caixa \n "
                + "INNER JOIN pes_filial AS pf ON pf.id_filial = cx.id_filial \n "
                + "INNER JOIN pes_juridica AS j ON j.id = pf.id_filial \n "
                + "INNER JOIN fin_banco AS banc ON banc.id = ch.id_banco \n ";

        String filter = "";
        String order_by = "";

        if (!ids_filial.isEmpty()) {
            filter = filter.isEmpty() ? " WHERE pf.id_filial IN (" + ids_filial + ") " : " AND pf.id_filial IN (" + ids_filial + ") ";
        }

        if (!ids_caixa.isEmpty()) {
            filter += filter.isEmpty() ? " WHERE cx.id IN (" + ids_caixa + ") " : " AND cx.id IN (" + ids_caixa + ") ";
        }
        
        if (in_plano5 != null && !in_plano5.isEmpty()) {
            filter += filter.isEmpty() ? " WHERE f.id_plano5 IN (" + in_plano5  + ") " : " AND f.id_plano5 (" + in_plano5  + ") ";
        }

        if (!tipo_data.isEmpty() && (!data_inicial.isEmpty() || !data_final.isEmpty())) {
            if (!data_inicial.isEmpty() && !data_final.isEmpty()) { // DATA INICIAL E FINAL
                filter += filter.isEmpty() ? " WHERE ch.dt_" + tipo_data + " >= '" + data_inicial + "' " + " AND ch.dt_" + tipo_data + " <= '" + data_final + "' "
                        : " AND ch.dt_" + tipo_data + " >= '" + data_inicial + "' " + " AND ch.dt_" + tipo_data + " <= '" + data_final + "' ";
            } else if (!data_inicial.isEmpty() && data_final.isEmpty()) { // POR DATA INICIAL
                filter += filter.isEmpty() ? " WHERE ch.dt_" + tipo_data + " >= '" + data_inicial + "' "
                        : " AND ch.dt_" + tipo_data + " >= '" + data_inicial + "' ";
            } else if (data_inicial.isEmpty() && !data_final.isEmpty()) { // POR DATA FINAL
                filter += filter.isEmpty() ? " WHERE ch.dt_" + tipo_data + " <= '" + data_final + "' "
                        : " AND ch.dt_" + tipo_data + " <= '" + data_final + "' ";
            }
        }

        if (id_status != 0) {
            filter += filter.isEmpty() ? " WHERE f.id_status = " + id_status : " AND f.id_status = " + id_status;
        }

        try {
            Query qry = getEntityManager().createNativeQuery(text + filter + order_by);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<FStatus> listaStatusCheque(String ids) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "  select s"
                    + "  from FStatus s "
                    + " where s.id in (" + ids + ")"
                    + " order by s.descricao");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }
}
