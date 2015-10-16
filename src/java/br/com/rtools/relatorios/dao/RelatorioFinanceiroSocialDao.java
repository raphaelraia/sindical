package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioGrupo;
import br.com.rtools.relatorios.RelatorioJoin;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioFinanceiroSocialDao extends DB {

    public List<Object> listaRelatorioFinanceiroSocial(Integer id_grupo_categoria, Integer id_categoria, Integer id_parentesco, Integer id_cidade_socio, Integer id_cidade_empresa, Boolean is_votante, String dataCadastro, String dataCadastroFinal, String dataRecadastro, String dataRecadastroFinal, String dataAdmissao, String dataAdmissaoFinal, String dataDemissao, String dataDemissaoFinal, String dataFiliacao, String dataFiliacaoFinal, String dataAposentadoria, String dataAposentadoriaFinal, String dataAtualizacao, String dataAtualizacaoFinal, String tipo_situacao, String tipo_pessoa, Integer id_pessoa, Integer id_grupo_financeiro, Integer id_sub_grupo, Integer id_servicos, Integer id_tipo_cobranca, String dataEmissao, String dataEmissaoFinal, String dataVencimento, String dataVencimentoFinal, String dataQuitacao, String dataQuitacaoFinal, String tipo_es, String tipo_situacao_financeiro, String tipo_departamento, String tipo_pessoa_financeiro, String desconto_folha_socio, String desconto_folha_financeiro, String order, Relatorios relatorio) {
        String select = " -- RelatorioFinanceiroSocialDao->listaRelatorioFinanceiroSocial() \n"
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

        // GRUPO CATEGORIA
        if (id_grupo_categoria != null) {
            list_where.add(" so.id_grupo_categoria = " + id_grupo_categoria);
        }

        // CATEGORIA
        if (id_categoria != null) {
            list_where.add(" so.id_categoria = " + id_categoria);
        }

        // PARENTESCO
        if (id_parentesco != null) {
            list_where.add(" so.id_parentesco = " + id_parentesco);
        }

        // CIDADE DO SÓCIO
        if (id_cidade_socio != null) {
            list_where.add(" p.id_cidade = " + id_cidade_socio);
        }

        // CIDADE DA EMPRESA
        if (id_cidade_empresa != null) {
            list_where.add(" p.e_id_cidade = " + id_cidade_empresa);
        }

        // VOTANTE
        if (is_votante != null) {
            list_where.add(" so.votante = " + is_votante);
        }

        // DATA CADASTRO ---
        if (!dataCadastro.isEmpty() && !dataCadastroFinal.isEmpty()) {
            list_where.add(" p.cadastro BETWEEN '" + dataCadastro + "' and '" + dataCadastroFinal + "' \n ");
        } else if (!dataCadastro.isEmpty() && dataCadastroFinal.isEmpty()) {
            list_where.add(" p.cadastro >= '" + dataCadastro + "' \n ");
        } else if (dataCadastro.isEmpty() && !dataCadastroFinal.isEmpty()) {
            list_where.add(" p.cadastro <= '" + dataCadastroFinal + "' \n ");
        }

        // DATA RECADASTRO ---
        if (!dataRecadastro.isEmpty() && !dataRecadastroFinal.isEmpty()) {
            list_where.add(" p.recadastro BETWEEN '" + dataRecadastro + "' and '" + dataRecadastroFinal + "' \n ");
        } else if (!dataRecadastro.isEmpty() && dataRecadastroFinal.isEmpty()) {
            list_where.add(" p.recadastro >= '" + dataRecadastro + "' \n ");
        } else if (dataRecadastro.isEmpty() && !dataRecadastroFinal.isEmpty()) {
            list_where.add(" p.recadastro <= '" + dataRecadastroFinal + "' \n ");
        }

        // DATA ADMISSAO ---
        if (!dataAdmissao.isEmpty() && !dataAdmissaoFinal.isEmpty()) {
            list_where.add(" p.admissao BETWEEN '" + dataAdmissao + "' and '" + dataAdmissaoFinal + "' \n ");
        } else if (!dataAdmissao.isEmpty() && dataAdmissaoFinal.isEmpty()) {
            list_where.add(" p.admissao >= '" + dataAdmissao + "' \n ");
        } else if (dataAdmissao.isEmpty() && !dataAdmissaoFinal.isEmpty()) {
            list_where.add(" p.admissao <= '" + dataAdmissaoFinal + "' \n ");
        }

        // DATA DEMISSAO ---
        if (!dataDemissao.isEmpty() && !dataDemissaoFinal.isEmpty()) {
            list_where.add(" p.demissao BETWEEN '" + dataDemissao + "' and '" + dataDemissaoFinal + "' \n ");
        } else if (!dataDemissao.isEmpty() && dataDemissaoFinal.isEmpty()) {
            list_where.add(" p.demissao >= '" + dataDemissao + "' \n ");
        } else if (dataDemissao.isEmpty() && !dataDemissaoFinal.isEmpty()) {
            list_where.add(" p.demissao <= '" + dataDemissaoFinal + "' \n ");
        }

        // DATA FILIACAO ---
        if (!dataFiliacao.isEmpty() && !dataFiliacaoFinal.isEmpty()) {
            list_where.add(" so.filiacao BETWEEN '" + dataFiliacao + "' and '" + dataFiliacaoFinal + "' \n ");
        } else if (!dataFiliacao.isEmpty() && dataFiliacaoFinal.isEmpty()) {
            list_where.add(" so.filiacao >= '" + dataFiliacao + "' \n ");
        } else if (dataFiliacao.isEmpty() && !dataFiliacaoFinal.isEmpty()) {
            list_where.add(" so.filiacao <= '" + dataFiliacaoFinal + "' \n ");
        }

        // DATA APOSENTADORIA ---
        if (!dataAposentadoria.isEmpty() && !dataAposentadoriaFinal.isEmpty()) {
            list_where.add(" p.dt_aposentadoria BETWEEN '" + dataAposentadoria + "' and '" + dataAposentadoriaFinal + "' \n ");
        } else if (!dataAposentadoria.isEmpty() && dataAposentadoriaFinal.isEmpty()) {
            list_where.add(" p.dt_aposentadoria >= '" + dataAposentadoria + "' \n ");
        } else if (dataAposentadoria.isEmpty() && !dataAposentadoriaFinal.isEmpty()) {
            list_where.add(" p.dt_aposentadoria <= '" + dataAposentadoriaFinal + "' \n ");
        }

        // DATA ATUALIZAÇÃO ---
        if (!dataAtualizacao.isEmpty() && !dataAtualizacaoFinal.isEmpty()) {
            list_where.add(" p.dt_atualizacao BETWEEN '" + dataAtualizacao + "' and '" + dataAtualizacaoFinal + "' \n ");
        } else if (!dataAtualizacao.isEmpty() && dataAtualizacaoFinal.isEmpty()) {
            list_where.add(" p.dt_atualizacao >= '" + dataAtualizacao + "' \n ");
        } else if (dataAtualizacao.isEmpty() && !dataAtualizacaoFinal.isEmpty()) {
            list_where.add(" p.dt_atualizacao <= '" + dataAtualizacaoFinal + "' \n ");
        }

        // TIPO SITUAÇÃO
        if (!tipo_situacao.isEmpty()) {
            if (tipo_situacao.equals("ativo")) {
                list_where.add(" m.id_titular IN (SELECT titular FROM soc_socios_vw) \n ");
            } else {
                list_where.add(" m.id_titular NOT IN (SELECT titular FROM soc_socios_vw) \n ");
            }
        }

        // PESSOA SÓCIO
        if (id_pessoa != null) {
            switch (tipo_pessoa) {
                case "responsavel":
                    list_where.add(" m.id_pessoa = " + id_pessoa + " \n ");
                    break;
                case "titular":
                    list_where.add(" m.id_titular = " + id_pessoa + " \n ");
                    break;
                case "beneficiario":
                    list_where.add(" m.id_beneficiario = " + id_pessoa + " \n ");
                    break;
            }
        }

        // DESCONTO FOLHA SÓCIO ---
        if (desconto_folha_socio != null) {
            if (desconto_folha_socio.equals("SIM")) {
                list_where.add(" so.desconto_folha = true ");
            } else {
                list_where.add(" so.desconto_folha = false ");
            }
        }

        // GRUPO ---
        if (id_grupo_financeiro != null) {
            list_where.add(" m.id_grupo = " + id_grupo_financeiro + " \n ");
        }

        // SUB GRUPO ---
        if (id_sub_grupo != null) {
            list_where.add(" m.id_subgrupo = " + id_sub_grupo + " \n ");
        }

        // SERVICOS ---
        if (id_servicos != null) {
            list_where.add(" m.id_servico = " + id_servicos + " \n ");
        }

        // TIPO COBRANÇA ---
        if (id_tipo_cobranca != null) {
            list_where.add(" so.cod_tipo_cobranca = " + id_tipo_cobranca + " \n ");
        }

        // DATA EMISSAO ---
        if (!dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao BETWEEN '" + dataEmissao + "' and '" + dataEmissaoFinal + "' \n ");
        } else if (!dataEmissao.isEmpty() && dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao >= '" + dataEmissao + "' \n ");
        } else if (dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
            list_where.add(" m.emissao <= '" + dataEmissaoFinal + "' \n ");
        }

        // DATA VENCIMENTO ---
        if (!dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento BETWEEN '" + dataVencimento + "' and '" + dataVencimentoFinal + "' \n ");
        } else if (!dataVencimento.isEmpty() && dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento >= '" + dataVencimento + "' \n ");
        } else if (dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
            list_where.add(" m.vencimento <= '" + dataVencimentoFinal + "' \n ");
        }

        // DATA QUITAÇÃO ---
        if (!dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa BETWEEN '" + dataQuitacao + "' and '" + dataQuitacaoFinal + "' \n ");
        } else if (!dataQuitacao.isEmpty() && dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa >= '" + dataQuitacao + "' \n ");
        } else if (dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
            list_where.add(" m.baixa <= '" + dataQuitacaoFinal + "' \n ");
        }

        // TIPO E/S ---
        if (!tipo_es.isEmpty()) {
            switch (tipo_es) {
                case "E":
                    list_where.add(" m.es = 'E' \n ");
                    break;
                case "S":
                    list_where.add(" m.es = 'S' \n ");
                    break;
            }
        }

        // SITUACAO FINANCEIRO
        if (!tipo_situacao_financeiro.isEmpty()) {
            switch (tipo_situacao_financeiro) {
                case "atrasado":
                    list_where.add(" m.baixa IS NULL AND m.vencimento < CURRENT_DATE \n ");
                    break;
                case "baixado":
                    list_where.add(" m.baixa IS NOT NULL \n ");
                    break;
                // aberto
                default:
                    list_where.add(" m.baixa IS NULL \n ");
                    break;
            }
        }

        // TIPO DEPARTAMENTO ---
        if (!tipo_departamento.isEmpty()) {
            switch (tipo_departamento) {
                case "outros":
                    list_where.add(" m.id_rotina <> 4 \n ");
                    break;
                case "todos":
                    break;
                default:
                    // ARRECADAÇÃO
                    list_where.add(" m.id_rotina = 4 \n ");
                    break;
            }
        }

        // DESCONTO FOLHA FINANCEIRO ---
        if (desconto_folha_financeiro != null) {
            if (desconto_folha_financeiro.equals("SIM")) {
                list_where.add(" j.id > 0 ");
            } else {
                list_where.add(" j.id IS NULL ");
            }
        }

        if (!tipo_pessoa_financeiro.isEmpty()) {
            switch (tipo_pessoa_financeiro) {
                case "fisica":
                    join += " INNER JOIN pes_fisica pfx ON pfx.id_pessoa = m.id_pessoa \n ";
                    break;
                default:
                    join += " INNER JOIN pes_juridica pjx ON pjx.id_pessoa = m.id_pessoa \n ";
                    break;
            }
        } else {
            // join += " INNER JOIN pes_fisica pfx ON pfx.id_pessoa = m.id_pessoa \n ";
        }

        if (list_where.isEmpty()) {
            return new ArrayList();
        }

        select += join;

        for (String linha : list_where) {
            if (where.isEmpty()) {
                where = " WHERE " + linha + " \n ";
            } else {
                where += " AND " + linha + " \n ";
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
        }

        Query qry = getEntityManager().createNativeQuery(select + where + group + order + " LIMIT 4000");

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }
}
