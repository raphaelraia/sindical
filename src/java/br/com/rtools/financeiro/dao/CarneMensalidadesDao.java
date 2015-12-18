package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CarneMensalidadesDao extends DB {

    public List listaServicosCarneMensalidades(Integer id_pessoa, String datas) {
        String text
                = "select \n "
                + " se.ds_descricao as servico, count(*) \n "
                + "  from fin_movimento as m \n "
                + " inner join pes_pessoa as p on p.id=m.id_pessoa \n "
                + " inner join fin_servicos as se on se.id=m.id_servicos \n "
                + "  left join soc_socios_vw as s on s.codsocio=m.id_pessoa \n "
                + " where is_ativo = true \n "
                + "   and id_baixa is null \n "
                + "   and to_char(m.dt_vencimento, 'MM/YYYY') in (" + datas + ") \n "
                + "   and m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina = 4) \n "
                + "   and func_titular_da_pessoa(m.id_beneficiario) = " + id_pessoa + " \n"
                + " group by se.ds_descricao \n"
                + " order by se.ds_descricao";
        try {
            Query qry = getEntityManager().createNativeQuery(text);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List listaCarneMensalidades(Integer id_pessoa, String datas) {
        String text
                = " select \n "
                + "  p.ds_nome as Titular, \n "
                + "  s.matricula, \n "
                + "  s.categoria, \n "
                + "  m.id_pessoa id_responsavel, \n"
                + "  m.dt_vencimento vencimento, \n "
                + "  sum(nr_valor-nr_desconto_ate_vencimento) valor \n "
                + "  from fin_movimento as m \n "
                + " inner join pes_pessoa as p on p.id = m.id_pessoa \n"
                + "  left join soc_socios_vw as s on s.codsocio = m.id_pessoa \n "
                + " where is_ativo = true \n "
                + "   and id_baixa is null \n "
                + "   and to_char(m.dt_vencimento, 'MM/YYYY') in (" + datas + ") \n "
                + "   and m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina = 4) \n "
                + "   and func_titular_da_pessoa(m.id_beneficiario) = " + id_pessoa + " \n "
                + " group by \n "
                + "  p.ds_nome, \n "
                + "  s.matricula, \n "
                + "  s.categoria, \n "
                + "  m.id_pessoa, \n "
                + "  m.dt_vencimento \n "
                + " order by p.ds_nome, m.id_pessoa,m.dt_vencimento ";
        try {
            Query qry = getEntityManager().createNativeQuery(text);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List listaCarneMensalidadesAgrupado(String id_pessoa, String datas) {
        String and = "";

        if (id_pessoa != null) {
            and = "   AND func_titular_da_pessoa(M.id_beneficiario) IN (" + id_pessoa + ") \n ";
        }

        String text
                = " -- CarneMensalidadesDao->listaCarneMensalidadesAgrupado(new Pessoa(), new Date()) \n\n"
                + "      SELECT P.ds_nome     AS titular,         \n "
                + "             S.matricula,                      \n "
                + "             S.categoria,                      \n "
                + "             M.id_pessoa   AS id_responsavel,  \n "
                + "             sum(nr_valor - nr_desconto_ate_vencimento) AS valor   \n"
                + "        FROM fin_movimento AS M                                    \n"
                + "  INNER JOIN pes_pessoa    AS P ON P.id        = M.id_pessoa       \n"
                + "   LEFT JOIN soc_socios_vw AS S ON S.codsocio  = M.id_pessoa       \n"
                + "       WHERE is_ativo = true                                       \n"
                + "         AND id_baixa IS NULL                                      \n"
                + "         AND to_char(M.dt_vencimento, 'MM/YYYY') IN (" + datas + ")\n"
                + "         AND M.id_servicos NOT IN (SELECT id_servicos              \n"
                + "                                     FROM fin_servico_rotina       \n"
                + "                                    WHERE id_rotina = 4            \n"
                + ")                                                                  \n"
                + and
                + "    GROUP BY P.ds_nome,    \n "
                + "             S.matricula,  \n "
                + "             S.categoria,  \n "
                + "             M.id_pessoa   \n "
                + "    ORDER BY P.ds_nome,    \n"
                + "             M.id_pessoa";
        try {
            Query query = getEntityManager().createNativeQuery(text);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List listaCarneMensalidadesAgrupadoEtiqueta(String id_pessoa) {
        String and = "";

        if (id_pessoa != null) {
            and = "   AND func_titular_da_pessoa(M.id_beneficiario) IN (" + id_pessoa + ") \n ";
        }

        String text
                = " -- CarneMensalidadesDao->listaCarneMensalidadesAgrupadoEtiqueta(new Pessoa(), new Date()) \n\n"
                + "      SELECT P.nome,        \n"
                + "             P.endereco,    \n"
                + "             P.numero,      \n"
                + "             P.complemento, \n"
                + "             P.bairro,      \n"
                + "             P.cidade,      \n"
                + "             P.uf,          \n"
                + "             P.cep,         \n"
                + "             P.logradouro   \n"
                + "        FROM fin_movimento AS M                                    \n"
                + "  INNER JOIN pes_pessoa_vw AS P ON P.codigo   = M.id_pessoa        \n"
                + "   LEFT JOIN soc_socios_vw AS S ON S.codsocio = M.id_pessoa        \n"
                + "       WHERE is_ativo = true                                       \n"
                + "         AND id_baixa IS NULL                                      \n"
                // + "         AND to_char(M.dt_vencimento, 'MM/YYYY') IN (" + datas + ")\n"
                + "         AND M.id_servicos NOT IN (SELECT id_servicos              \n"
                + "                                     FROM fin_servico_rotina       \n"
                + "                                    WHERE id_rotina = 4            \n"
                + ")                                                                  \n"
                + and
                + "    GROUP BY P.nome,        \n"
                + "             P.logradouro,  \n"
                + "             P.endereco,    \n"
                + "             P.numero,      \n"
                + "             P.complemento, \n"
                + "             P.bairro,      \n"
                + "             P.cidade,      \n"
                + "             P.uf,          \n"
                + "             P.cep          \n"
                + "    ORDER BY P.nome";
        try {
            Query query = getEntityManager().createNativeQuery(text);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
