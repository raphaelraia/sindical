-- View: soc_boletos_processa_vw 

-- DROP VIEW soc_boletos_processa_vw; 

CREATE OR REPLACE VIEW soc_boletos_processa_vw AS 
 SELECT l.id AS id_fin_lote, m.id AS id_fin_movimento, m.nr_ctr_boleto, 
    sl.id AS id_lote_boleto, sl.dt_processamento AS processamento, 
    pr.id AS codigo, pr.ds_nome AS responsavel, 
    '1997-10-07'::date + "substring"(m.nr_ctr_boleto::text, 9, 4)::integer AS vencimento, 
    mtr.matricula, mtr.grupo_categoria, mtr.categoria, 
    se.ds_descricao AS servico, m.id_beneficiario, 
    pb.ds_nome AS nome_beneficiario, 
        CASE 
            WHEN m.dt_vencimento >= 'now'::text::date THEN func_multa_ass(m.id, b.dt_processamento) + func_juros_ass(m.id, b.dt_processamento) + func_correcao_ass(m.id, b.dt_processamento) + (m.nr_valor - m.nr_desconto_ate_vencimento) 
            ELSE func_multa_ass(m.id, b.dt_processamento) + func_juros_ass(m.id, b.dt_processamento) + func_correcao_ass(m.id, b.dt_processamento) + m.nr_valor 
        END AS valor, 
    0 AS mensalidades_corrigidas, c.ds_mensagem_associativo AS mensagem_boleto, 
    bco.nr_num_banco AS banco, cb.ds_agencia AS agencia, 
    c.ds_cod_cedente AS cedente, b.ds_boleto AS boleto, f.juremail1 AS email, 
    f.jurnome AS nome_filial, f.jursite AS site_filial, 
    f.jurdocumento AS cnpj_filial, f.jurtelefone AS tel_filial, 
    (((((f.jurlogradouro::text || ' '::text) || f.jurendereco::text) || ', '::text) || f.jurnumero::text) || ' '::text) || f.jurcomplemento::text AS endereco_filial, 
    f.jurbairro AS bairro_filial, f.jurcidade AS cidade_filial, 
    f.juruf AS uf_filial, 
    ("substring"(f.jurcep::text, 1, 5) || '-'::text) || "right"(f.jurcep::text, 3) AS cep_filial, 
    er.logradouro AS logradouro_responsavel, 
    rtrim(((((er.endereco::text || ', '::text) || per.ds_numero::text) || ' '::text) || per.ds_complemento::text) || er.bairro::text) AS endereco_responsavel, 
    ("left"(er.cep::text, 5) || '-'::text) || "right"(er.cep::text, 3) AS cep_responsavel, 
    er.uf AS uf_responsavel, er.cidade AS cidade_responsavel, 
    co.ds_informativo AS informativo, co.ds_local_pagamento AS local_pagamento, 
    pt.id AS codigo_titular, pt.ds_nome AS nome_titular, m.is_ativo AS ativo, 
    m.dt_vencimento AS vencimento_movimento, 
    b.dt_vencimento AS vencimento_boleto, 
    b.dt_vencimento_original AS vencimento_original_boleto, 
    m.nr_valor AS valor_sem_acrescimo, 
    b.dt_cobranca_registrada AS data_cobranca_registrada, 
    b.dt_registro_baixa AS data_registro_baixa, b.id_conta_cobranca, m.id_baixa, 
    b.dt_processamento AS processamento_boleto, 
    m.ds_referencia AS referencia 
    FROM fin_lote l 
   JOIN fin_movimento m ON m.id_lote = l.id 
   JOIN soc_lote_boleto sl ON "right"('00000'::text || sl.id::text, 5) = "substring"("right"('0000000000000000000000'::text || m.nr_ctr_boleto::text, 22), 16, 5) AND length(m.nr_ctr_boleto::text) = 22 
   JOIN fin_servicos se ON se.id = m.id_servicos AND m.id_servicos > 0 
   JOIN fin_boleto b ON b.nr_ctr_boleto::text = m.nr_ctr_boleto::text 
   JOIN pes_pessoa pr ON pr.id = m.id_pessoa 
   JOIN pes_pessoa pb ON pb.id = m.id_beneficiario 
   JOIN pes_pessoa pt ON pt.id = m.id_titular 
   JOIN pes_juridica_vw f ON f.id_pessoa = 1 
   JOIN fin_conta_cobranca c ON c.id = b.id_conta_cobranca 
   JOIN fin_conta_banco cb ON cb.id = c.id_conta_banco 
   JOIN fin_banco bco ON bco.id = cb.id_banco 
   JOIN soc_cobranca co ON co.id = 1 
   LEFT JOIN pes_pessoa_endereco per ON per.id_pessoa = pr.id AND per.id_tipo_endereco = 3 
   LEFT JOIN endereco_vw er ON er.id = per.id_endereco 
   LEFT JOIN soc_socios_vw mtr ON mtr.codsocio = pr.id 
  WHERE m.is_ativo = true; 

ALTER TABLE soc_boletos_processa_vw 
  OWNER TO postgres; 


-- View: soc_boletos_vw 

-- DROP VIEW soc_boletos_vw; 

CREATE OR REPLACE VIEW soc_boletos_vw AS 
 SELECT soc_boletos_processa_vw.id_fin_lote, 
    soc_boletos_processa_vw.id_fin_movimento, 
    soc_boletos_processa_vw.nr_ctr_boleto, 
    soc_boletos_processa_vw.id_lote_boleto, 
    soc_boletos_processa_vw.processamento, soc_boletos_processa_vw.codigo, 
    soc_boletos_processa_vw.responsavel, soc_boletos_processa_vw.vencimento, 
    soc_boletos_processa_vw.matricula, soc_boletos_processa_vw.grupo_categoria, 
    soc_boletos_processa_vw.categoria, soc_boletos_processa_vw.servico, 
    soc_boletos_processa_vw.id_beneficiario, 
    soc_boletos_processa_vw.nome_beneficiario, soc_boletos_processa_vw.valor, 
    soc_boletos_processa_vw.mensalidades_corrigidas, 
    soc_boletos_processa_vw.mensagem_boleto, soc_boletos_processa_vw.banco, 
    soc_boletos_processa_vw.agencia, soc_boletos_processa_vw.cedente, 
    soc_boletos_processa_vw.boleto, soc_boletos_processa_vw.email, 
    soc_boletos_processa_vw.nome_filial, soc_boletos_processa_vw.site_filial, 
    soc_boletos_processa_vw.cnpj_filial, soc_boletos_processa_vw.tel_filial, 
    soc_boletos_processa_vw.endereco_filial, 
    soc_boletos_processa_vw.bairro_filial, 
    soc_boletos_processa_vw.cidade_filial, soc_boletos_processa_vw.uf_filial, 
    soc_boletos_processa_vw.cep_filial, 
    soc_boletos_processa_vw.logradouro_responsavel, 
    soc_boletos_processa_vw.endereco_responsavel, 
    soc_boletos_processa_vw.cep_responsavel, 
    soc_boletos_processa_vw.uf_responsavel, 
    soc_boletos_processa_vw.cidade_responsavel, 
    soc_boletos_processa_vw.informativo, 
    soc_boletos_processa_vw.local_pagamento, 
    soc_boletos_processa_vw.codigo_titular, 
    soc_boletos_processa_vw.nome_titular, soc_boletos_processa_vw.ativo, 
    soc_boletos_processa_vw.vencimento_movimento, 
    soc_boletos_processa_vw.vencimento_boleto, 
    soc_boletos_processa_vw.vencimento_original_boleto, 
    soc_boletos_processa_vw.valor_sem_acrescimo, 
    soc_boletos_processa_vw.data_cobranca_registrada, 
    soc_boletos_processa_vw.data_registro_baixa, 
    soc_boletos_processa_vw.id_conta_cobranca, soc_boletos_processa_vw.id_baixa, 
    soc_boletos_processa_vw.processamento_boleto, 
    soc_boletos_processa_vw.referencia 
   FROM soc_boletos_processa_vw 
  WHERE NOT (soc_boletos_processa_vw.codigo IN ( SELECT m.id_pessoa 
           FROM fin_movimento m 
      JOIN pes_fisica f ON f.id_pessoa = m.id_pessoa 
     WHERE m.is_ativo = true AND m.id_baixa IS NULL AND m.dt_vencimento < ('now'::text::date - (( SELECT conf_social.nr_bloqueio_meses_boleto * 30 
              FROM conf_social)) - date_part('day'::text, 'now'::text::date)::integer) 
     GROUP BY m.id_pessoa)); 

ALTER TABLE soc_boletos_vw 
  OWNER TO postgres; 


-- View: soc_boletos_geral_vw 

-- DROP VIEW soc_boletos_geral_vw; 

CREATE OR REPLACE VIEW soc_boletos_geral_vw AS 
 SELECT soc_boletos_processa_vw.id_fin_lote, 
    soc_boletos_processa_vw.id_fin_movimento, 
    soc_boletos_processa_vw.nr_ctr_boleto, 
    soc_boletos_processa_vw.id_lote_boleto, 
    soc_boletos_processa_vw.processamento, soc_boletos_processa_vw.codigo, 
    soc_boletos_processa_vw.responsavel, soc_boletos_processa_vw.vencimento, 
    soc_boletos_processa_vw.matricula, soc_boletos_processa_vw.grupo_categoria, 
    soc_boletos_processa_vw.categoria, soc_boletos_processa_vw.servico, 
    soc_boletos_processa_vw.id_beneficiario, 
    soc_boletos_processa_vw.nome_beneficiario, soc_boletos_processa_vw.valor, 
    soc_boletos_processa_vw.mensalidades_corrigidas, 
    soc_boletos_processa_vw.mensagem_boleto, soc_boletos_processa_vw.banco, 
    soc_boletos_processa_vw.agencia, soc_boletos_processa_vw.cedente, 
    soc_boletos_processa_vw.boleto, soc_boletos_processa_vw.email, 
    soc_boletos_processa_vw.nome_filial, soc_boletos_processa_vw.site_filial, 
    soc_boletos_processa_vw.cnpj_filial, soc_boletos_processa_vw.tel_filial, 
    soc_boletos_processa_vw.endereco_filial, 
    soc_boletos_processa_vw.bairro_filial, 
    soc_boletos_processa_vw.cidade_filial, soc_boletos_processa_vw.uf_filial, 
    soc_boletos_processa_vw.cep_filial, 
    soc_boletos_processa_vw.logradouro_responsavel, 
    soc_boletos_processa_vw.endereco_responsavel, 
    soc_boletos_processa_vw.cep_responsavel, 
    soc_boletos_processa_vw.uf_responsavel, 
    soc_boletos_processa_vw.cidade_responsavel, 
    soc_boletos_processa_vw.informativo, 
    soc_boletos_processa_vw.local_pagamento, 
    soc_boletos_processa_vw.codigo_titular, 
    soc_boletos_processa_vw.nome_titular, soc_boletos_processa_vw.ativo, 
    soc_boletos_processa_vw.vencimento_movimento, 
    soc_boletos_processa_vw.vencimento_boleto, 
    soc_boletos_processa_vw.vencimento_original_boleto, 
    soc_boletos_processa_vw.valor_sem_acrescimo, 
    soc_boletos_processa_vw.data_cobranca_registrada, 
    soc_boletos_processa_vw.data_registro_baixa, 
    soc_boletos_processa_vw.id_conta_cobranca, soc_boletos_processa_vw.id_baixa, 
    soc_boletos_processa_vw.processamento_boleto, 
    soc_boletos_processa_vw.referencia 
   FROM soc_boletos_processa_vw; 

ALTER TABLE soc_boletos_geral_vw 
  OWNER TO postgres; 


  
CREATE OR REPLACE FUNCTION func_limpa_movimento_boleto() 
  RETURNS boolean AS 
$BODY$ 
BEGIN 

delete from fin_movimento_boleto where id in 
( 
select 
mb.id 
from fin_movimento_boleto as mb 
inner join fin_movimento as m on m.id=mb.id_movimento 
inner join fin_baixa as b on b.id = m.id_baixa 
where m.is_ativo=true and b.dt_baixa < current_date - 365 
); 

  
delete from fin_movimento_boleto where id in 
( 
select 
        mb.id 
 	from fin_movimento_boleto as mb 
inner join fin_movimento_inativo as mi on mi.id_movimento=mb.id_movimento 
inner join fin_movimento as m on m.id=mi.id_movimento 
where mi.dt_data < current_date-365 and m.is_ativo=false 
 	); 
  
      RETURN true; 

END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_limpa_movimento_boleto() 
  OWNER TO postgres; 


-- Function: func_demissiona_socios(integer, integer) 

-- DROP FUNCTION func_demissiona_socios(integer, integer); 

CREATE OR REPLACE FUNCTION func_demissiona_socios( 
    x_id_grupo_categoria integer, 
    x_nr_quantidade_dias integer) 
  RETURNS boolean AS 
$BODY$ 
declare ret boolean; 
BEGIN 
  

update matr_socios set dt_inativo = CURRENT_DATE, id_motivo_inativacao = 5,ds_motivo='** INATIVAÇÃO AUTOMÁTICA **' 
 where dt_inativo is null and id_titular in 
 ( 
select m.id_titular from matr_socios as m 
inner join soc_categoria as ct on ct.id=m.id_categoria 
inner join pes_fisica as f on f.id_pessoa = m.id_titular 
        where 
m.dt_inativo is null and   ----> sócio ativo 
ct.id_grupo_categoria = x_id_grupo_categoria and ---> grupo categoria de inativação 
f.dt_aposentadoria is null and --> Não pode ser Aposentado 
f.id not in (select id_fisica from pes_pessoa_empresa where dt_demissao is null) and ---> Não pode ter empresa viculada 
f.id   in ----> ver se a admissão está dento da carência 
( 
  
select f.id as id_fisica from pes_fisica as f 
inner join (select id_fisica,max(dt_demissao) as dt_demissao from pes_pessoa_empresa where dt_demissao is not null group by id_fisica) as pe 
on pe.id_fisica=f.id 
where (CURRENT_DATE >= (dt_demissao + x_nr_quantidade_dias) ) 
  
) 
 ); 
  
update fin_servico_pessoa set is_ativo=false where is_ativo=true and id in 
( 
select s.id_servico_pessoa from soc_socios as s 
inner join matr_socios as m on m.id=s.id_matricula_socios 
inner join fin_servico_pessoa as sp on sp.id=s.id_servico_pessoa 
where m.dt_inativo is not null and sp.is_ativo=true 
); 


---- INATIVA PERIODO DIÁRIO E SEMANAL 
  

update fin_servico_pessoa set is_ativo=false where is_ativo=true and id_periodo_cobranca=1 and dt_emissao < current_date; 

update fin_servico_pessoa set is_ativo=false where is_ativo=true and id_periodo_cobranca=2 and (dt_emissao+7) < current_date; 

---- INATIVA PERIODO DIÁRIO E SEMANAL ACADEMIA 


update matr_academia set dt_inativo=current_date where dt_inativo is null and id_servico_pessoa in (select id from fin_servico_pessoa where is_ativo=false and id_periodo_cobranca in (1,2)); 
  


ret := func_inativa_dependentes_vencidos(); 

ret := (select func_prescreve_movimentos()); 

ret := (select func_inativa_academia_inadimplente()); 
  
ret := (select func_inativa_socios_inadimplentes()); 


ret := (select func_limpa_movimento_boleto()); 

------ CORRIGE MATRICULA DE TITULARES ATIVOS COM FIN_SERVICO_PESSOA.IS_ATIVO=FALSE 

update fin_servico_pessoa set is_ativo=true where id in 
( 
select sp.id from fin_servico_pessoa as sp 
inner join soc_socios as s on s.id_servico_pessoa=sp.id 
inner join matr_socios as m on m.id=s.id_matricula_socios 
where m.dt_inativo is null and sp.is_ativo=false and s.id_parentesco=1 
); 
----------------------------------------------------------------------------------- 
    
RETURN true; 
END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_demissiona_socios(integer, integer) 
  OWNER TO postgres; 


-- Function: func_gerar_boleto_ass(integer[], date) 

-- DROP FUNCTION func_gerar_boleto_ass(integer[], date); 

CREATE OR REPLACE FUNCTION func_gerar_boleto_ass( 
    id_movimento integer[], 
    vencimento date) 
  RETURNS boolean AS 
$BODY$ 
    declare qt int; 
    declare lote int; 
    declare ret boolean; 
    
BEGIN 

/* 
select func_gerar_boleto_ass('{4906164,4906165}', '04/05/2015') 
*/ 

qt = (select count(*) from fin_movimento where ds_documento<>'' and ds_documento is not null and length(nr_ctr_boleto)=22 and id=ANY(id_movimento)); 

if (qt > 0) then ret=false; else ret=true; end if; 


if (ret) then 
------ Criar Lote Boleto 
insert into soc_lote_boleto (dt_processamento) (select current_date); 
lote = (select (select max(id) from soc_lote_boleto)); 
  
  
-------->>>> Grava nr_ctr_boleto em fin_movimento 
update fin_movimento 
set nr_ctr_boleto= 

---select 
----Pessoa 
right('00000000'||id_pessoa,8)|| 
----Fator de Vencimento 
right('0000'||vencimento-CAST('07/10/1997' as date),4)|| ------------------->>>> vencimento 
----Conta Cobrança 
right('000'||(select id_conta_cobranca from fin_servico_conta_cobranca where id_servicos=fin_movimento.id_servicos and id_tipo_servico=fin_movimento.id_tipo_servico),3)|| 
----Lote de Geração 
right('00000'||text(lote),5)|| --------------------------------------------------->>> numero do lote do boleto 
----id do soc_cobranca (extrato) 
'01' 
---from fin_movimento 
where id = ANY(id_movimento); -------------------->>>>>>>> filtro fin_movimento 

  
-------->>>> Insere Boleto 

insert into fin_boleto (nr_ctr_boleto,id_conta_cobranca,is_ativo,dt_vencimento,dt_vencimento_original,ds_mensagem) 
( 
select nr_ctr_boleto,cast(substring(nr_ctr_boleto,13,3) as int),true,vencimento,vencimento,(select ds_mensagem_boleto_associativo_pagador from conf_social) 
 from fin_movimento 
where 
length(nr_ctr_boleto)=22 and nr_ctr_boleto not in (select nr_ctr_boleto from fin_boleto where length(nr_ctr_boleto) = 22) 
and id = ANY(id_movimento) -------------------->>>>>>>> filtro fin_movimento 
group by nr_ctr_boleto,cast(substring(nr_ctr_boleto,13,3) as int) 
); 

----------------------------------------------------------------------------------------------------- 

update fin_movimento set ds_documento=fin_boleto.ds_boleto from fin_boleto where fin_movimento.nr_ctr_boleto=fin_boleto.nr_ctr_boleto and (ds_documento is null or ds_documento='') and length(fin_movimento.nr_ctr_boleto)=22 
and fin_movimento.id = ANY(id_movimento); -------------------->>>>>>>> filtro fin_movimento 
----------------------------------------------------------------------------------------------------- 

--- Corrige fin_boleto.dt_vencimento_original 
/* 
Coloca como vencimento original a maior data de vencimento do fin_movimento 
*/ 
  
update fin_boleto set dt_vencimento_original=(x.m_vencimento-cast(extract(day from x.m_vencimento) as int)+x.nr_dia_vencimento) 
from 
( 
select b.id as id_boleto,max(m.dt_vencimento) m_vencimento,b.dt_vencimento_original as b_vencimento_original,pc.nr_dia_vencimento from fin_movimento as m 
inner join fin_boleto as b on b.nr_ctr_boleto=m.nr_ctr_boleto 
inner join pes_pessoa_complemento as pc on pc.id_pessoa=m.id_pessoa 
where m.is_ativo=true and id_baixa is null and m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina=4) and m.nr_ctr_boleto<>'' 
group by b.id,pc.nr_dia_vencimento,b.dt_vencimento_original 
) as x 
where 
fin_boleto.id=x.id_boleto and 
dt_vencimento_original<>(x.m_vencimento-cast(extract(day from x.m_vencimento) as int)+x.nr_dia_vencimento); 

  
    
-- APAGA OS LOTE BOLETO GERADOS QUE NÃO TEM MOVIMENTO 
delete from soc_lote_boleto where id not in (select cast(substring(nr_ctr_boleto,16,5) as int) from fin_movimento where length(nr_ctr_boleto) = 22); 


--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
-------------------------------- insere fin_movimento_boleto ------------------------------------------------- 

insert into fin_movimento_boleto (id_movimento,id_boleto) 
( 
select 
m.id,b.id 
from fin_boleto as b 
inner join fin_movimento as m on m.nr_ctr_boleto<>'' and m.nr_ctr_boleto=b.nr_ctr_boleto 
left join fin_movimento_boleto as mb on mb.id_boleto=b.id and mb.id_movimento=m.id 
where 
mb.id is null and 
m.is_ativo=true and 
m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina=4) and 
m.id_baixa is null 
and b.dt_processamento >= current_date-20 
group by m.id,b.id 
); 

--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
  
end if; 

     RETURN ret; 
END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_gerar_boleto_ass(integer[], date) 
  OWNER TO postgres; 




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


INSERT INTO seg_rotina (id, ds_rotina, ds_nome_pagina, ds_classe, is_ativo, ds_acao) SELECT 498, 'ESTORNAR MOVIMENTO BANCÁRIO' , '', '', true, 'estornar_movimento_bancario' WHERE NOT EXISTS ( SELECT id FROM seg_rotina WHERE id = 498);
SELECT setval('seg_rotina_id_seq', max(id)) FROM seg_rotina;