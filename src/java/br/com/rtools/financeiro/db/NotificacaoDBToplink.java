package br.com.rtools.financeiro.db;

import br.com.rtools.financeiro.Cobranca;
import br.com.rtools.financeiro.CobrancaEnvio;
import br.com.rtools.financeiro.CobrancaLote;
import br.com.rtools.financeiro.CobrancaTipo;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class NotificacaoDBToplink extends DB implements NotificacaoDB {

    @Override
    public Object[] listaParaNotificacao(int id_lote, String data, String id_empresa, String id_contabil, String id_cidade, boolean comContabil, boolean semContabil, String ids_servicos, String ids_tipo_servico) {
        Object[] obj = new Object[2];
        List<Vector> result = null;
        String data_inicial = new DataHoje().decrementarMeses(60, DataHoje.data());

        try {
            String filtro_empresa = "", filtro_contabil = "", filtro_cidade = "", filtro_lote = "", filtro_com_sem = "", filtro_servicos = "", filtro_tipo_servico = "";
            if (!id_empresa.isEmpty()) {
                filtro_empresa = " AND c.id_juridica IN (" + id_empresa + ") \n ";
            }

            if (!id_contabil.isEmpty()) {
                filtro_contabil = " AND c.id_contabilidade IN (" + id_contabil + ") \n ";
            }

            if (!id_cidade.isEmpty()) {
                filtro_cidade = " AND en.id_cidade IN (" + id_cidade + ") \n ";
            }

            if (id_lote != -1) {
                filtro_lote = " AND fc.id_lote = " + id_lote + " \n ";
            }

            if (comContabil) {
                filtro_com_sem = " AND c.id_contabilidade IS NOT NULL \n ";
            }

            if (semContabil) {
                filtro_com_sem = " AND c.id_contabilidade IS NULL \n ";
            }

            if (!ids_servicos.isEmpty()) {
                filtro_servicos = " AND m.id_servicos IN (" + ids_servicos + ") \n ";
            }

            if (!ids_tipo_servico.isEmpty()) {
                filtro_tipo_servico = " AND m.id_tipo_servico IN (" + ids_tipo_servico + ") \n ";
            }

            String text_select0 = "SELECT t.id_pessoa, t.ds_nome, count(*), null AS lote_cobranca, null AS lote_envio, MAX(t.dt_emissao) FROM \n (";
            String text_select = 
                    "SELECT c.id_pessoa, \n "
                    + "     c.ds_nome, \n "
                    + "     m.id AS movimento, \n "
                    + "     max(ce.dt_emissao) as dt_emissao \n ";

            String text_from = 
                    " FROM arr_contribuintes_vw AS c \n "
                    + " INNER JOIN fin_movimento AS m ON m.id_pessoa = c.id_pessoa \n"
                    + "  LEFT JOIN fin_cobranca  AS fc ON fc.id_movimento = m.id \n"
                    + "  LEFT JOIN fin_cobranca_envio AS ce ON ce.id_lote = fc.id_lote \n"
                    + "  LEFT JOIN pes_juridica AS je ON je.id = c.id_contabilidade \n"
                    + "  LEFT JOIN pes_pessoa AS pe ON pe.id = je.id_pessoa \n"
                    + "  LEFT JOIN pes_pessoa_endereco AS pee ON pee.id_pessoa = c.id_pessoa \n"
                    + "  LEFT JOIN end_endereco AS en ON en.id = pee.id_endereco  \n "
                    + " WHERE c.dt_inativacao IS NULL \n "
                    + "   AND m.is_ativo = true \n "
                    + "   AND m.id_baixa IS NULL \n "
                    + "   AND m.dt_vencimento >= '" + data_inicial + "' \n "
                    + "   AND m.dt_vencimento < '" + data + "' \n "
                    + "   AND pee.id_tipo_endereco = 5 \n "
                    + filtro_empresa + filtro_contabil + filtro_lote + filtro_cidade + filtro_com_sem + filtro_servicos + filtro_tipo_servico;

            String text_from0 = 
                    ") AS t \n "
                    + " GROUP BY t.id_pessoa, t.ds_nome \n "
                    + " ORDER BY ds_nome, id_pessoa ";
            
            String text_group_by = " GROUP BY c.id_pessoa, c.ds_nome, m.id \n ";
            //String text_order_by = " ORDER BY c.ds_nome, c.id_pessoa \n ";

            //Query qry = getEntityManager().createNativeQuery(text_select + text_from + text_group_by + text_order_by);
            Query qry = getEntityManager().createNativeQuery(text_select0 + text_select + text_from + text_group_by + text_from0);
            result = qry.getResultList();

            obj[0] = text_from;
            obj[1] = result;
        } catch (Exception e) {
        }
        return obj;
    }

    @Override
    public List listaNotificado(int id_movimento) {
        List<Vector> result = null;
        try {
            String textQry = 
                    "  SELECT fc.id, \n "
                    + "       cl.dt_emissao \n "
                    + "  FROM fin_cobranca AS fc "
                    + " INNER JOIN fin_cobranca_lote AS cl ON cl.id = fc.id_lote "
                    + " WHERE fc.id_movimento = " + id_movimento;
            Query qry = getEntityManager().createNativeQuery(textQry);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public List<CobrancaLote> listaCobrancaLote() {
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT cl "
                    + "  FROM CobrancaLote cl ORDER BY cl.dtEmissao DESC, cl.hora DESC");
            qry.setMaxResults(15);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List<CobrancaTipo> listaCobrancaTipoEnvio() {
        try {
            Query qry = getEntityManager().createQuery(
                    "select ct "
                    + "  from CobrancaTipo ct");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List<Cobranca> listaCobranca(int id_lote_cobranca) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c "
                    + "  from Cobranca c where c.lote.id = " + id_lote_cobranca);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public CobrancaLote pesquisaCobrancaLote(int id_usuario, Date dataEmissao) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select cl "
                    + "  from CobrancaLote cl where cl.usuario.id = " + id_usuario + " and cl.dtEmissao = :data");
            qry.setParameter("data", dataEmissao);
            return (CobrancaLote) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List listaNotificacaoEnvio(int tipo_envio, int id_lote) {
        try {
            String textQry
                    = " SELECT \n "
                    + "       pj.escNome, \n "
                    + "       pj.escTelefone, \n "
                    + "       pj.escLogradouro, \n "
                    + "       pj.escEndereco, \n "
                    + "       pj.escNumero, \n "
                    + "       pj.escComplemento, \n "
                    + "       pj.escBairro, \n "
                    + "       substring(pj.escCep,1,5)||'-'||substring(pj.escCep,6,3) as escCep, \n "
                    + "       pj.escCidade, \n "
                    + "       pj.escUf, \n "
                    + "       pj.jurNome, \n "
                    + "       pj.jurDocumento, \n "
                    + "       pj.jurTelefone, \n "
                    + "       pj.jurcidade, \n "
                    + "       pj.juruf, \n "
                    + "       pj.jurcep, \n "
                    + "       pj.jurlogradouro, \n "
                    + "       pj.jurendereco, \n "
                    + "       pj.jurnumero, \n "
                    + "       pj.jurcomplemento, \n "
                    + "       pj.jurbairro, \n "
                    + "       se.ds_descricao    AS movServico, \n "
                    + "       ts.ds_descricao    AS movTipoServico, \n "
                    + "       m.ds_referencia    AS movReferencia, \n "
                    + "       m.ds_documento     AS movNumeroBoleto, \n "
                    + "       l.ds_mensagem, \n "
                    + "       pj.escid, \n "
                    + "       pj.id_pessoa \n "
                    + "  FROM pes_juridica_vw      AS pj \n "
                    + " INNER JOIN fin_movimento          AS m    ON m.id_pessoa    = pj.id_pessoa \n "
                    + " INNER JOIN fin_servicos           AS se   ON se.id          = m.id_servicos \n "
                    + " INNER JOIN fin_tipo_servico       AS ts   ON ts.id          = m.id_tipo_servico \n "
                    //                    + " inner join pes_filial as fi on fi.id = (select id_filial from conf_arrecadacao) \n "
                    //                    + " inner join pes_juridica as ju on ju.id = fi.id_filial \n "
                    //                    + " inner join pes_juridica_vw as sind on sind.id_pessoa = ju.id_pessoa \n "

                    + " INNER JOIN pes_juridica           AS j    ON j.id_pessoa    = pj.id_pessoa \n "
                    + " INNER JOIN fin_cobranca           AS c    ON c.id_movimento = m.id \n "
                    + " INNER JOIN fin_cobranca_lote      AS l    ON l.id           = c.id_lote \n "
                    + " WHERE m.id_baixa IS NULL AND is_ativo = TRUE AND l.id = " + id_lote + " \n ";

            // 1 "ESCRITÓRIO"
            if (tipo_envio == 1) {
                textQry += "  AND pj.escNome is not null \n "
                        + " ORDER BY pj.escNome,pj.escid, pj.jurNome, pj.jurid, substring(m.ds_referencia,4,4) || substring(m.ds_referencia,1,2) \n ";
                // 2 "EMPRESA COM ESCRITÓRIO"
            } else if (tipo_envio == 2) {
                textQry += "  AND pj.escNome is not null \n "
                        + " ORDER BY pj.jurNome, pj.jurid, substring(m.ds_referencia,4,4) || substring(m.ds_referencia,1,2) \n ";
                // 3 "EMPRESA SEM ESCRITÓRIO"    
            } else if (tipo_envio == 3) {
                textQry += "  AND pj.escNome is null \n "
                        + " ORDER BY pj.jurNome, pj.jurid, substring(m.ds_referencia,4,4) || substring(m.ds_referencia,1,2) \n ";
                // 4 "EMAIL PARA OS ESCRITÓRIO"    AGRUPAR POR pj.escid -- id_escritorio
            } else if (tipo_envio == 4) {
                textQry += "  AND pj.escNome is not null \n "
                        + " ORDER BY pj.escNome, pj.escid, pj.jurNome, pj.jurid, substring(m.ds_referencia,4,4) || substring(m.ds_referencia,1,2) \n ";
                // 5 "EMAIL PARA AS EMPRESAS" -- AGRUPAR POR pj.id_pessoa -- id_pessoa
            } else if (tipo_envio == 5) {
                textQry += " ORDER BY pj.jurNome, pj.jurid, substring(m.ds_referencia,4,4) || substring(m.ds_referencia,1,2) \n ";
            }

            Query qry = getEntityManager().createNativeQuery(textQry);

            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List pollingEmail(int limite, int id_usuario) {
//        String text = "select * from fin_polling_email " +
//                      " where (replace(ds_hora,':','') <= '"+ DataHoje.horaMinuto().replace(":", "")+"' and dt_emissao = '"+DataHoje.data()+"' and is_ativo = true) " +
//                      "    or (replace(ds_hora,':','') <> '"+ DataHoje.horaMinuto().replace(":", "")+"' and dt_emissao < '"+DataHoje.data()+"' and is_ativo = true) " +
//                      " order by dt_emissao, replace(ds_hora,':','') asc" +
//                      " limit "+limite;
//        
//        String text = "select * from fin_polling_email pe " +
//                      " inner join fin_cobranca_envio ce on ce.id = pe.id_cobranca_envio " +
//                      " where (replace(pe.ds_hora,':','') <= '"+ DataHoje.horaMinuto().replace(":", "")+"' and pe. dt_emissao = '"+DataHoje.data()+"' and pe.is_ativo = true) " +
//                      "    or (replace(pe.ds_hora,':','') <> '"+ DataHoje.horaMinuto().replace(":", "")+"' and pe.dt_emissao <> '"+DataHoje.data()+"' and pe.is_ativo = true) " +
//                      "   and ce.id_usuario = " +id_usuario+
//                      " order by pe.dt_emissao, replace(pe.ds_hora,':','') asc " +
//                      " limit "+limite;
        String text = "select * from fin_polling_email pe "
                + " inner join fin_cobranca_envio ce on ce.id = pe.id_cobranca_envio "
                + " where ( "
                + "            (replace(pe.ds_hora,':','') <= '" + DataHoje.horaMinuto().replace(":", "") + "' and pe. dt_emissao = '" + DataHoje.data() + "') "
                + "         or (pe.dt_emissao < '" + DataHoje.data() + "') "
                + "       ) "
                + "   and pe.is_ativo = true"
                + "   and ce.id_usuario = " + id_usuario
                + " order by pe.dt_emissao, replace(pe.ds_hora,':','') asc "
                + " limit " + limite;
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List pollingEmailTrue() {
        String text = "select * from fin_polling_email where is_ativo = true limit 1";
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List pollingEmailNovo(int limite) {
        String text = "select * from fin_polling_email "
                + " where is_ativo = false "
                + "   and ds_hora = '' or ds_hora is null "
                + " order by dt_emissao "
                + " limit " + limite;
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List pesquisaUltimoLote() {
        String text = "select case when max(nr_lote_envio) = null then 1 else max(nr_lote_envio) end from fin_polling_email";
        try {
            Query qry = getEntityManager().createNativeQuery(text);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public CobrancaEnvio pesquisaCobrancaEnvio(int id_lote) {
        //String text = "select ce from CobrancaEnvio ce where ce.dtEmissao = :data and ce.lote.id = "+id_lote;
        String text = "select ce from CobrancaEnvio ce where ce.lote.id = " + id_lote;
        try {
            Query qry = getEntityManager().createQuery(text);
            //qry.setParameter("data", DataHoje.dataHoje());
            return (CobrancaEnvio) qry.getSingleResult();
        } catch (Exception e) {
            return new CobrancaEnvio();
        }
    }

    @Override
    public List<Vector> listaParaEtiqueta(String string_qry, CobrancaTipo ct) {
        List<Vector> result = null;

        String text;
        String text_group_by;
        String text_order_by;
        // 6 - ETIQUETA PARA EMPRESAS
        if (ct.getId() == 6) {
            text = "SELECT c.id_pessoa, \n "
                    + "    c.ds_nome, \n "
                    + "    fc.id_lote, \n "
                    + "    ce.id_lote, \n "
                    + "    ce.dt_emissao \n "
                    + string_qry;
            text_group_by = " GROUP BY c.id_pessoa, c.ds_nome, fc.id_lote, ce.id_lote, ce.dt_emissao ";
            text_order_by = " ORDER BY c.ds_nome, c.id_pessoa ";
        } else {
            // 7 - ETIQUETA PARA ESCRITÓRIOS
            text = "SELECT c.id_contabilidade, \n "
                    + "    fc.id_lote, \n "
                    + "    ce.id_lote, \n "
                    + "    ce.dt_emissao \n "
                    + string_qry;
            text_group_by = " GROUP BY c.id_contabilidade, fc.id_lote, ce.id_lote, ce.dt_emissao ";
            text_order_by = " ORDER BY c.id_contabilidade";
        }

        text += text_group_by + text_order_by;
        Query qry = getEntityManager().createNativeQuery(text);
        try {
            result = qry.getResultList();
        } catch (Exception e) {

        }
        return result;
    }

}

//                String text_from = "  from arr_contribuintes_vw as c  "
//                    + "  left join pes_juridica as je on je.id = c.id_contabilidade "
//                    + "  left join pes_pessoa as pe on pe.id = je.id_pessoa "
//                    + " inner join fin_movimento as m on m.id_pessoa = c.id_pessoa "
//                    + "  left join pes_pessoa_endereco as pee on pee.id_pessoa = c.id_pessoa "
//                    + "  left join end_endereco as en on en.id = pee.id_endereco "
//                    + "  left join fin_cobranca       as fc on fc.id_movimento = m.id  "
//                    + "  left join fin_cobranca_envio as ce on ce.id_lote = fc.id_lote "
//                    + " where c.dt_inativacao is null    "
//                    + "   and m.is_ativo = true          "
//                    + "   and m.id_baixa is null         "
//                    + "   and m.dt_vencimento < '" + data + "' "
//                    + "   and pee.id_tipo_endereco = 5 ";    
