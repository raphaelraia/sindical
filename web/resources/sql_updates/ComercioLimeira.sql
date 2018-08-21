INSERT INTO seg_rotina (id, ds_rotina, ds_nome_pagina, ds_classe, is_ativo, ds_acao) SELECT 498, 'ESTORNAR MOVIMENTO BANCÁRIO' , '', '', true, 'estornar_movimento_bancario' WHERE NOT EXISTS ( SELECT id FROM seg_rotina WHERE id = 498);
SELECT setval('seg_rotina_id_seq', max(id)) FROM seg_rotina;


-- Table: public.fin_conciliacao

-- DROP TABLE public.fin_conciliacao;
CREATE TABLE fin_conciliacao
(
id serial NOT NULL,
dt_lancamento date,
dt_conciliacao date,
id_operador integer NOT NULL,
CONSTRAINT fin_conciliacao_pkey PRIMARY KEY (id),
CONSTRAINT fk_fin_conciliacao_id_operador FOREIGN KEY (id_operador)
REFERENCES seg_usuario (id) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);
ALTER TABLE fin_conciliacao
OWNER TO postgres;


ALTER TABLE fin_forma_pagamento ADD COLUMN id_conciliacao integer;
	
ALTER TABLE fin_forma_pagamento ADD CONSTRAINT fk_fin_conciliacao_id_conciliacao 
      FOREIGN KEY (id_conciliacao) REFERENCES fin_conciliacao (id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

DROP VIEW public.balancete_vw;

DROP VIEW public.contabil_vw;

DROP VIEW public.resumo_anual_realizado_vw;

DROP VIEW public.movimentos_vw;

CREATE OR REPLACE VIEW public.movimentos_vw AS
 SELECT g.id AS id_grupo, g.ds_descricao AS grupo, sb.id AS id_subgrupo, 
    sb.ds_descricao AS subgrupo, m.id_servicos AS id_servico, 
        CASE
            WHEN se.id > 0 THEN se.ds_descricao
            ELSE conta.ds_conta
        END AS servico, 
        CASE
            WHEN (conta.id IN ( SELECT p.id
               FROM fin_plano5 p
          JOIN fin_conta_rotina cr ON cr.id_plano4 = p.id_plano4)) THEN caixa_banco.id
            ELSE conta.id
        END AS id_conta_financeiro, 
        CASE
            WHEN (conta.id IN ( SELECT p.id
               FROM fin_plano5 p
          JOIN fin_conta_rotina cr ON cr.id_plano4 = p.id_plano4)) THEN caixa_banco.ds_conta
            ELSE conta.ds_conta
        END AS conta, 
    caixa_banco.id AS id_caixa_banco, caixa_banco.ds_conta AS caixa_banco, 
        CASE
            WHEN m.id_baixa IS NULL THEN m.nr_valor * func_es(m.ds_es)::double precision
            ELSE m.nr_valor * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS valor, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_valor_baixa * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS valor_baixa, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_juros * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS juros, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_multa * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS multa, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_correcao * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS correcao, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_desconto * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS desconto, 
        CASE
            WHEN m.id_baixa IS NULL THEN 0::double precision
            ELSE m.nr_taxa * fp.nr_valorp / 100::double precision * func_es(m.ds_es)::double precision
        END AS taxa_bancaria, 
    l.id AS id_lote, l.id_evt, l.dt_lancamento AS lancamento, 
    l.dt_emissao AS emissao, l.is_avencer_contabil AS avencer_contabil, 
    l.ds_documento AS lote_documento, td.ds_descricao AS lote_tipo_documento, 
    r.id AS id_rotina, r.ds_rotina AS rotina, st.id AS id_status, 
    st.ds_descricao AS lote_status, l.id_plano_5 AS lote_id_plano5, 
    l.nr_valor * func_es(m.ds_es)::double precision AS lote_valor, 
    l.nr_desconto * func_es(m.ds_es)::double precision AS lote_desconto, 
    l.ds_historico_contabil AS ds_historico, l.id_filial AS filial_criacao, 
    l.id_filial AS filial_centro_custo, m.nr_quantidade AS quantidade, 
    m.id AS id_movimento, m.ds_referencia AS referencia, m.ds_es AS es, 
    m.nr_repasse_automatico * func_es(m.ds_es)::double precision AS repasse_automatico, 
    m.ds_documento AS mov_documento, 
    m.dt_vencimento_original AS vencimento_original, 
    m.dt_vencimento AS vencimento, 
    m.nr_desconto_ate_vencimento * func_es(m.ds_es)::double precision AS desconto_ate_vencimento, 
    m.is_obrigacao AS obrigacao, m.nr_ctr_boleto, m.id_plano5, m.id_pessoa, 
    m.id_tipo_documento, m.id_tipo_servico, m.id_titular, m.id_acordo, 
    m.id_beneficiario, b.id AS id_baixa, b.dt_importacao AS importacao, 
    b.dt_baixa AS baixa, b.id_usuario AS id_usuario_baixa, b.id_caixa, 
    b.id_fechamento_caixa, fp.id AS id_forma_pagamento, fp.nr_valorp AS valorp, 
    fp.id_filial AS id_filial_baixa, fp.id_tipo_pagamento, 
    tp.ds_descricao AS tipo_pagamento, fp.id_plano5 AS id_plano5_baixa, 
    fp.nr_valor_liquido * func_es(m.ds_es)::double precision AS nr_valor_liquido, 
    fp.dt_credito, fc.dt_data AS fechamento_caixa, fp.id_cheque_rec, 
    fp.id_cheque_pag, fp.id_cartao_rec, fp.id_cartao_pag, 
    cr.id_rotina AS id_conta_rotina, conta.id AS id_conta_contabil, 
    conta.ds_conta AS conta_contabil, fp.id_status AS id_baixa_status, 
    fp.id_conciliacao_plano5, null as dt_conciliacao, fp.id_conciliacao, 
    b.dt_ocorrencia AS ocorrencia
   FROM fin_lote l
   JOIN fin_movimento m ON m.id_lote = l.id
   LEFT JOIN fin_baixa b ON b.id = m.id_baixa
   LEFT JOIN fin_forma_pagamento fp ON fp.id_baixa = b.id
   LEFT JOIN fin_cheque_rec chr ON chr.id = fp.id_cheque_rec
   LEFT JOIN fin_cheque_pag chp ON chp.id = fp.id_cheque_pag
   LEFT JOIN fin_cartao_rec crr ON crr.id = fp.id_cartao_rec
   LEFT JOIN fin_fechamento_caixa fc ON fc.id = b.id_fechamento_caixa
   LEFT JOIN fin_tipo_documento td ON td.id = l.id_tipo_documento
   LEFT JOIN seg_rotina r ON r.id = l.id_rotina
   LEFT JOIN fin_status st ON st.id = l.id_status
   LEFT JOIN fin_plano5 conta ON conta.id = m.id_plano5
   LEFT JOIN fin_servicos se ON se.id = m.id_servicos
   LEFT JOIN fin_subgrupo sb ON sb.id = se.id_subgrupo
   LEFT JOIN fin_grupo g ON g.id = sb.id_grupo
   LEFT JOIN seg_usuario u ON u.id = b.id_usuario
   LEFT JOIN fin_plano5 caixa_banco ON caixa_banco.id = fp.id_plano5
   LEFT JOIN fin_tipo_pagamento tp ON tp.id = fp.id_tipo_pagamento
   LEFT JOIN fin_conta_rotina cr ON cr.id_plano4 = caixa_banco.id_plano4 AND (cr.id_rotina = ANY (ARRAY[1, 2]))
  WHERE m.is_ativo = true;

ALTER TABLE public.movimentos_vw
    OWNER TO postgres;


CREATE OR REPLACE VIEW public.contabil_vw AS
         SELECT 'M'::text AS tabela, m.baixa, m.id_movimento, m.id_lote, 
            m.id_conta_contabil AS id_conta, m.conta_contabil, 
                CASE
                    WHEN m.valor_baixa < 0::double precision THEN m.valor_baixa * (-1)::double precision
                    ELSE m.valor_baixa
                END AS valor, 
                CASE
                    WHEN m.es::text = 'E'::text THEN 'C'::text
                    ELSE 'D'::text
                END AS dc, 
            m.tipo_pagamento, m.id_cheque_rec, m.id_cheque_pag, m.es, 
            m.ds_historico AS historico, m.id_forma_pagamento, 
            p5m.ds_acesso AS nr_conta, m.id_plano5_baixa AS id_contra_partida, 
            p5b.ds_conta AS contra_partida, p5b.ds_acesso AS nr_contra_partida, 
            pl.natureza_dc
           FROM movimentos_vw m
      JOIN pes_pessoa p ON p.id = m.id_pessoa
   LEFT JOIN fin_plano5 p5m ON p5m.id = m.id_conta_contabil
   LEFT JOIN fin_plano5 p5b ON p5b.id = m.id_plano5_baixa
   LEFT JOIN plano_vw pl ON pl.id_p5 = m.id_conta_contabil
  WHERE (m.id_status <> 14 OR m.id_status IS NULL) AND m.valor_baixa <> 0::double precision
UNION 
         SELECT 'B'::text AS tabela, m.baixa, m.id_movimento, m.id_lote, 
            m.id_plano5_baixa AS id_conta, p5b.ds_conta AS conta_contabil, 
                CASE
                    WHEN m.valor_baixa < 0::double precision THEN m.valor_baixa * (-1)::double precision
                    ELSE m.valor_baixa
                END AS valor, 
                CASE
                    WHEN m.es::text = 'E'::text THEN 'D'::text
                    ELSE 'C'::text
                END AS dc, 
            m.tipo_pagamento, m.id_cheque_rec, m.id_cheque_pag, m.es, 
            m.ds_historico AS historico, m.id_forma_pagamento, 
            p5b.ds_acesso AS nr_conta, m.id_plano5 AS id_contra_partida, 
            cp.ds_conta AS contra_partida, cp.ds_acesso AS nr_contra_partida, 
            pl.natureza_dc
           FROM movimentos_vw m
      JOIN pes_pessoa p ON p.id = m.id_pessoa
   LEFT JOIN fin_plano5 p5b ON p5b.id = m.id_plano5_baixa
   LEFT JOIN fin_plano5 cp ON cp.id = m.id_plano5
   LEFT JOIN plano_vw pl ON pl.id_p5 = m.id_plano5_baixa
  WHERE (m.id_status <> 14 OR m.id_status IS NULL) AND m.valor_baixa <> 0::double precision
  ORDER BY 3, 7 DESC;

ALTER TABLE public.contabil_vw
    OWNER TO postgres;


CREATE OR REPLACE VIEW public.resumo_anual_realizado_vw AS
 SELECT m.es, m.filial_centro_custo AS filial, 
    func_nullinteger(f.id_plano5) AS fixa, 
    ("right"('0'::text || date_part('month'::text, m.baixa), 2) || '/'::text) || date_part('year'::text, m.baixa) AS mes_ano_baixa, 
    ("right"('0'::text || date_part('month'::text, m.baixa), 2) || '/'::text) || date_part('year'::text, m.baixa) AS mes_ano_ocorrencia, 
    m.servico, sum(m.valor_baixa) AS valor
   FROM movimentos_vw m
   JOIN fin_plano5 p5 ON p5.id = m.id_conta_contabil
   LEFT JOIN ( SELECT fin_conta_operacao.id_plano5
      FROM fin_conta_operacao
     WHERE fin_conta_operacao.is_conta_fixa = true
     GROUP BY fin_conta_operacao.id_plano5) f ON f.id_plano5 = m.id_conta_contabil
  WHERE m.id_rotina <> 4 AND NOT (p5.id_plano4 IN ( SELECT fin_conta_rotina.id_plano4
   FROM fin_conta_rotina))
  GROUP BY m.es, m.filial_centro_custo, f.id_plano5, ("right"('0'::text || date_part('month'::text, m.baixa), 2) || '/'::text) || date_part('year'::text, m.baixa), m.servico;

ALTER TABLE public.resumo_anual_realizado_vw
    OWNER TO postgres;

CREATE OR REPLACE VIEW public.balancete_vw AS
 SELECT x.data, x.codigo1, x.conta1, x.codigo2, x.conta2, x.codigo3, x.conta3, 
    x.codigo4, x.conta4, x.classificador AS codigo5, x.conta5, 
    0::double precision AS saldo_anterior, sum(x.debito) AS debito, 
    sum(x.credito) AS credito, 0::double precision AS saldo_atual, x.id_conta, 
    p5.is_soma_debito, x.natureza_dc
   FROM (         SELECT c.baixa AS data, p.codigo1, p.conta1, p.codigo2, 
                    p.conta2, p.codigo3, p.conta3, p.codigo4, p.conta4, 
                    p.codigo5, p.conta5, sum(c.valor) AS debito, 0 AS credito, 
                    c.id_conta, p.classificador, p.natureza_dc
                   FROM contabil_vw c
              JOIN plano_vw p ON p.id_p5 = c.id_conta
             WHERE c.dc = 'D'::text
             GROUP BY c.baixa, p.codigo1, p.conta1, p.codigo2, p.conta2, p.codigo3, p.conta3, p.codigo4, p.conta4, p.codigo5, p.conta5, c.id_conta, p.classificador, p.natureza_dc
        UNION 
                 SELECT c.baixa AS data, p.codigo1, p.conta1, p.codigo2, 
                    p.conta2, p.codigo3, p.conta3, p.codigo4, p.conta4, 
                    p.codigo5, p.conta5, 0 AS debito, sum(c.valor) AS credito, 
                    c.id_conta, p.classificador, p.natureza_dc
                   FROM contabil_vw c
              JOIN plano_vw p ON p.id_p5 = c.id_conta
             WHERE c.dc = 'C'::text
             GROUP BY c.baixa, p.codigo1, p.conta1, p.codigo2, p.conta2, p.codigo3, p.conta3, p.codigo4, p.conta4, p.codigo5, p.conta5, c.id_conta, p.classificador, p.natureza_dc) x
   JOIN fin_plano5 p5 ON p5.id = x.id_conta
  GROUP BY x.data, x.codigo1, x.conta1, x.codigo2, x.conta2, x.codigo3, x.conta3, x.codigo4, x.conta4, x.codigo5, x.conta5, x.id_conta, p5.is_soma_debito, x.classificador, x.natureza_dc
  ORDER BY x.conta4;

ALTER TABLE public.balancete_vw
    OWNER TO postgres;

ALTER TABLE fin_forma_pagamento DROP COLUMN id_conciliado;
ALTER TABLE fin_forma_pagamento DROP COLUMN dt_conciliacao;


ALTER TABLE fin_forma_pagamento ADD COLUMN is_conciliado BOOLEAN DEFAULT FALSE;

ALTER TABLE arr_repis_status ADD COLUMN is_ativo BOOLEAN DEFAULT TRUE;

insert into arr_repis_status (id,ds_descricao) (select 1,'Recusado Sind.Empregados' where 1 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 2,'Recusado Sind.Empregados' where 2 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 3,'Recusado Sind.Empregados' where 3 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 4,'Recusado Sind.Empregados' where 4 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 5,'Recusado Sind.Empregados' where 5 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 6,'Recusado Sind.Empregados' where 6 not in(select id from arr_repis_status)); 
insert into arr_repis_status (id,ds_descricao) (select 7,'Recusado Sind.Empregados' where 7 not in(select id from arr_repis_status)); 