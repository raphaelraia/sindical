update fin_boleto set dt_vencimento=x.dt_vencimento 
from 
( 
select mb.id_boleto as id_boleto,max(m.dt_vencimento) as dt_vencimento from fin_movimento_boleto as mb 
inner join fin_boleto as b on b.id=mb.id_boleto 
inner join fin_movimento as m on m.id=mb.id_movimento 
where b.dt_vencimento is null 
group by mb.id_boleto 
) as x 
where fin_boleto.id=x.id_boleto and fin_boleto.dt_vencimento is null; 


update fin_boleto set dt_vencimento=x.dt_vencimento 
from 
( 
select b.id as id_boleto,max(m.dt_vencimento) as dt_vencimento from fin_boleto as b 
inner join fin_movimento as m on m.nr_ctr_boleto<>'' and m.nr_ctr_boleto=b.nr_ctr_boleto 
left join fin_movimento_boleto as mb on mb.id_movimento = m.id and mb.id_boleto=b.id 
where mb.id is null and b.dt_vencimento is null 
group by b.id 
) as x 
where fin_boleto.id=x.id_boleto and fin_boleto.dt_vencimento is null; 


------------------------------------------------------------------------------------------------------------ 
------------------------------------------------------------------------------------------------------------ 
------------------------------------------------------------------------------------------------------------ 

--- executar este trecho até zerar, pq se executar sem o limit vai travar 

update fin_boleto set dt_vencimento_original=dt_vencimento where id in 
( 
select id from fin_boleto 
where dt_vencimento_original is null and dt_vencimento is not null 
limit 50000 
) 

------------------------------------------------------------------------------------------------------------ 
------------------------------------------------------------------------------------------------------------ 
------------------------------------------------------------------------------------------------------------ 


update fin_boleto set dt_vencimento=dt_vencimento_original where dt_vencimento is null and dt_vencimento_original is not null;

update fin_boleto set dt_vencimento='01/01/1900',dt_vencimento_original='01/01/1900' where dt_vencimento is null; 
  

------------------------------------------------------------------------------------------------------------ 

alter table fin_boleto alter column dt_vencimento set not null; 

alter table fin_boleto alter column dt_vencimento_original set not null; 

alter table fin_boleto add column nr_valor double precision default 0 not null;
--------------------EXECUTAR SOMENTE NO INICIOA QUERY ABAIXO


update fin_boleto set nr_valor = 9999999;


-------------------- EXECUTAR ATÉ ZERAR A QUERY ABAIXO
 

update fin_boleto set nr_valor = x.valor 
from 
( 
select b.id as id_baixa,sum(m.nr_valor) as valor from fin_boleto as b 
inner join fin_movimento as m on m.nr_ctr_boleto=b.nr_ctr_boleto 
where m.is_ativo=true and b.nr_valor = 9999999 and m.id_baixa IS NULL
group by b.id 
limit 100000
) as x 
where x.id_baixa = fin_boleto.id


--------------------EXECUTAR SOMENTE NO FINAL A QUERY ABAIXO



update fin_boleto set nr_valor =0 WHERE nr_valor = 9999999; 


  

DROP INDEX xnr_ctr_boleto_fin_boleto;

CREATE UNIQUE INDEX xnr_ctr_boleto_fin_boleto
    ON public.fin_boleto USING btree
    (nr_ctr_boleto COLLATE pg_catalog."default")
    TABLESPACE pg_default WHERE nr_ctr_boleto::text <> ''::text;







ALTER TABLE public.arr_repis_movimento ADD COLUMN dt_inativacao date;


ALTER TABLE public.arr_repis_movimento ADD COLUMN id_operador_inativacao integer;
	
ALTER TABLE public.arr_repis_movimento
    ADD CONSTRAINT fk_arr_repis_movimento_id_operador_inativacao FOREIGN KEY (id_operador_inativacao)
    REFERENCES public.pes_pessoa (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


INSERT INTO arr_repis_status (id, ds_descricao) SELECT 6, 'Retirado' WHERE NOT EXISTS ( SELECT id FROM arr_repis_status WHERE id = 6);
SELECT setval('arr_repis_status_id_seq', max(id)) FROM arr_repis_status;

ALTER TABLE public.arr_repis_movimento ADD COLUMN ds_retirado_por character varying(150);

ALTER TABLE public.arr_repis_movimento ADD COLUMN dt_retirada date;

INSERT INTO sis_relatorios (id, ds_nome, ds_jasper, id_rotina, ds_qry_ordem, ds_qry, id_relatorio_tipo) SELECT 130, 'Não Solicitaram Certificado','/Relatorios/NAO_SOLICITARAM_CERTIFICADOS.jasper', 280,'','', 1 WHERE NOT EXISTS ( SELECT id FROM sis_relatorios WHERE id = 130);
SELECT setval('sis_relatorios_id_seq', max(id)) FROM sis_relatorios;






 -- Function: func_geramensalidades(integer, character varying) 

-- DROP FUNCTION func_geramensalidades(integer, character varying); 

CREATE OR REPLACE FUNCTION func_geramensalidades( 
    pessoa integer, 
    mesano character varying) 
  RETURNS integer AS 
$BODY$ 
  
declare wlote int :=0; 
declare wlote_geracao int :=0; 
declare wref int :=(select nr_referencia from conf_social); 
BEGIN 

/* 
***************************************************************************************************** 
                                            OFICIAL 25/06/2018 
***************************************************************************************************** 
*/ 
--------- Inativa Convênios de sócios Inativos 


  
update matr_convenio_medico set dt_inativo=current_date,ds_motivo_inativacao='SÓCIO INATIVO' where id_servico_pessoa in 
( 
select sp.id from matr_convenio_medico as m 
inner join fin_servico_pessoa as sp on sp.id=m.id_servico_pessoa 
where sp.is_ativo=true and sp.id_pessoa not in 
( 
select codsocio from soc_socios_vw 
) 
); 


update fin_servico_pessoa 
set is_ativo=false 
from matr_convenio_medico 
where fin_servico_pessoa.is_ativo=true and matr_convenio_medico.dt_inativo is not null and matr_convenio_medico.id_servico_pessoa=fin_servico_pessoa.id; 
  
  

--------- ACERTA VENCIMENTOS 

update pes_pessoa_complemento set nr_dia_vencimento=28 where nr_dia_vencimento > 28; 
--- update pes_pessoa_complemento set nr_dia_vencimento = (select fin_dia_vencimento_cobranca from seg_registro) where nr_dia_vencimento<(select fin_dia_vencimento_cobranca from seg_registro); 


--------------- EXCLUI LOTES NÃO GERADOS 

update car_venda set id_lote=null where id_lote in 
( 
select l.id from fin_lote as l 
left join fin_movimento as m on m.id_lote=l.id 
where m.id_lote is null and l.dt_lancamento > (cast(now() as date)-20) 
group by l.id 
); 


delete from fin_lote where id in 
( 
select l.id from fin_lote as l 
left join fin_movimento as m on m.id_lote=l.id 
where m.id_lote is null and l.dt_lancamento > (cast(now() as date)-20) 
group by l.id 
); 



delete from soc_lote_geracao where id in 
( 
select g.id from soc_lote_geracao as g 
left join fin_lote as l on l.id=g.id 
where l.id is null 
); 


----- Período Cobranca, mensal se for null 

update fin_servico_pessoa set id_periodo_cobranca=3 where id_periodo_cobranca is null; 


--------------- atualiza id_cobranca em fin_servico_pessoa 

update fin_servico_pessoa 
set id_cobranca=soc_socios_vw.titular 
from soc_socios_vw 
where soc_socios_vw.codsocio=fin_servico_pessoa.id_pessoa and fin_servico_pessoa.is_ativo=true 
and id_cobranca<>soc_socios_vw.titular; 

-------------------------- Acerta Desconto em Folha ------------------------------------------- 

update fin_servico_pessoa set id_cobranca_movimento=id_cobranca where desconto_folha=false and (id_cobranca_movimento<>id_cobranca or id_cobranca_movimento is null); 
update fin_servico_pessoa set ds_ref_validade='' where ds_ref_validade is null; 

--- Tira Desconto em Folha de quem não tem Empresa 
update fin_servico_pessoa set id_cobranca_movimento=id_cobranca,desconto_folha=false 
where desconto_folha=true 
and id_cobranca not in 
( 
--- Verifica se o id_cobranca do fin_servico_pessoa com desconto em folha tem empresa vinculada, senão 
select sp.id_cobranca from fin_servico_pessoa as sp 
inner join pes_fisica as f on f.id_pessoa=sp.id_cobranca 
inner join pes_pessoa_empresa as pe on pe.id_fisica=f.id and is_principal=true and dt_demissao is null 
where sp.is_ativo=true 
); 

  
update fin_servico_pessoa set id_cobranca_movimento=pes_pessoa_vw.e_id_pessoa 
from pes_pessoa_vw 
where id_cobranca=codigo 
and (id_cobranca_movimento<>pes_pessoa_vw.e_id_pessoa or id_cobranca_movimento is null) 
and fin_servico_pessoa.desconto_folha=true; 
  
---------------------------- Administradoras ------------------------------------------------------------------- 

update fin_servico_pessoa set id_cobranca_movimento=a.adm 
from 
( 
select sp.id_cobranca as id_cobranca,a.id_pessoa as adm from fin_servico_pessoa as sp 
inner join fin_servicos as se on se.id=sp.id_servico 
inner join pes_administradora as a on a.id=se.id_administradora 
and sp.is_ativo=true 
) as a 
where fin_servico_pessoa.id_cobranca=a.id_cobranca and fin_servico_pessoa.is_ativo=true; 

----------------------------------------------------------------------------------------------- 

---- CANCELADO wlote_geracao = (select max(id) from soc_lote_geracao); 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
insert into soc_lote_geracao (ds_vencimento , id_servico_pessoa, dt_lancamento,nr_valor,nr_desconto_ate_vencimento) 
( 
select 
mesano, 
sp.id, 
current_date, 
  
-------------------------------------- valor 
  
CASE 

WHEN (sp.nr_desconto = 0 and sp.nr_valor_fixo = 0) then 

func_periodo_valor(sp.id_periodo_cobranca, 

round( 
cast 
( 
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano ,10) as date), 0, m.id_categoria)) 
as numeric 
) 
,2) 

) 


WHEN (sp.nr_desconto <> 0 and sp.nr_valor_fixo = 0) then 

func_periodo_valor(sp.id_periodo_cobranca, 
  
round( 
cast 
( 
    func_valor_servico_cheio(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano ,10) as date)) *(1-(sp.nr_desconto/100)) 
as numeric 
) 
,2) 

) 

else sp.nr_valor_fixo 
  
end as valor, 

-------------------------------------- valor até o vencimento 

CASE 

WHEN (sp.nr_desconto = 0 and sp.nr_valor_fixo = 0) then 


func_periodo_valor(sp.id_periodo_cobranca, 


round( 
cast 
( 
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano ,10) as date), 1, m.id_categoria)) 
as numeric 
) 
,2) 

) 

WHEN (sp.nr_desconto <> 0 and sp.nr_valor_fixo = 0) then 

func_periodo_valor(sp.id_periodo_cobranca, 

round( 
cast 
( 
    func_valor_servico_cheio_desconto_ate_vencimento(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano ,10) as date)) *(1-(sp.nr_desconto/100)) 
as numeric 
) 
,2) 

) 

else 0 

  
end as desconto_ate_vencimento 
  
-----func_valor_servico_cheio(pessoa integer, servico integer, vencimento date) 
------------------------------------------------------------------------------------------------------- 


from 

fin_servico_pessoa as sp 
inner join pes_pessoa_complemento as pc on pc.id_pessoa=sp.id_pessoa 
left join soc_socios_vw as m on m.codsocio=pc.id_pessoa 
where sp.id not in 

------------- A REGRA DO CAMPEONATO, É NÃO COBRAR DE SÓCIO, PORTANTO SE FIN_SERVICO_PESSOA FOR CAMPEONATO E FOR SÓCIO, NÃO GERARÁ ------------------------------------------------- 

( 
select id from fin_servico_pessoa 
where is_ativo=true and id_pessoa in (select codsocio from soc_socios_vw) 
and id in 
( 
select id_servico_pessoa from matr_campeonato 
union 
select id_servico_pessoa from eve_campeonato_dependente 
) 
) 

AND 

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ 

---- Verifica período de Geração 

func_gera_periodo(sp.id_periodo_cobranca,cast(left(mesano,2) as integer)) > 0 and 

------------------------------------- 
sp.is_ativo=true 
---- se está dentro da vigoração >= mes/ano vecto (mesano = mes/ano parâmetro da tela de chamada) 
and (substring(mesano,4,4)||substring(mesano,1,2))>=(substring(sp.ds_ref_vigoracao,4,4)||substring(sp.ds_ref_vigoracao,1,2)) 
---- se está dentro da validade 
and 
( 
sp.ds_ref_validade='' or (substring(sp.ds_ref_validade,4,4)||substring(sp.ds_ref_validade,1,2)) >= (substring(mesano,4,4)||substring(mesano,1,2)) 
) 
------------------------ Verifica se existe no movimento -------------------------------------------------------------------------------------------------------------------------------------- 
and sp.id_pessoa||'S'||sp.id_servico not in (select id_beneficiario||'S'||id_servicos from fin_movimento where is_ativo = true and id_tipo_servico=1 and CAST(substring(mesano,1,2) AS int)=extract(month from dt_vencimento) and CAST(substring(mesano,4,4) AS int)=extract(year from dt_vencimento)) 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
------------------------ Verifica se existe no soc_lote_geracao 
-----CANCELADO---and sp.id||'REF'||mesano not in (select id_servico_pessoa||'REF'||ds_vencimento FROM soc_lote_geracao) 
------------------------------ VALOR > 0 -------------------------------------------------------------------------------------------------------------------------------------------------------------- 

and 

( 

round( 
cast 
( 
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano ,10) as date), 0, m.id_categoria)) *(1-(sp.nr_desconto/100)) 
as numeric 
) 
,2) 
  
>0 

or 

sp.nr_valor_fixo > 0 

) 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
and 
( 
    sp.id_cobranca_movimento in 
    ( 
  select id_cobranca_movimento   
from fin_servico_pessoa 
where 
id_pessoa=pessoa or 
id_cobranca=pessoa or 
id_cobranca_movimento=pessoa 
group by id_cobranca_movimento 
    )   
    or pessoa is null 
) 
order by sp.id 
); 

  
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
---------------------------------------------------------------------------------------------------------------------------------------- 
insert into fin_lote 
( 
id, 
dt_emissao, 
is_avencer_contabil, 
ds_pag_rec, 
ds_documento, 
nr_valor, 
dt_lancamento, 
ds_historico, 
id_filial, 
id_evt, 
id_pessoa, 
id_tipo_documento, 
id_rotina, 
id_status, 
id_pessoa_sem_cadastro, 
id_departamento, 
id_condicao_pagamento, 
id_plano_5, 
is_desconto_folha 
) 

( 
select 
g.id, 
current_date as dt_emissao, 
false as is_avencer_contabil, 
'R' as ds_pag_rec, 
null as ds_documento,------------------------------??? 
g.nr_valor, 
current_date as dt_lancamento, 
null as ds_historico, 
se.id_filial, 
sp.id_evt, 
sp.id_pessoa, 
sp.id_tipo_documento, 
118 as id_rotina, 
1 as id_status, 
null as id_pessoa_sem_cadastro, 
se.id_departamento, 
2 as id_condicao_pagamento, 
se.id_plano5, 
sp.desconto_folha 
----is_banco true/false ver se gera boleto ou não 
from 
fin_servico_pessoa as sp 
inner join soc_lote_geracao as g on g.id_servico_pessoa=sp.id and g.ds_vencimento=mesano ----and g.id not in (select id from fin_lote) 
left join fin_lote as l on l.id=g.id 
inner join pes_pessoa_complemento as pc on pc.id_pessoa=sp.id_cobranca_movimento 
inner join fin_servicos se on se.id=sp.id_servico 
where 
l.id is null --- fin_lote ainda NÃO EXISTENTE 
order by sp.id 
); 

  
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 
-------------------------------------------------------------------------------------------------------------------------------------------------- 

insert into fin_movimento 
( 
nr_juros, 
nr_multa, 
nr_desconto, 
dt_vencimento, 
nr_taxa, 
is_ativo, 
nr_valor_baixa, 
is_obrigacao, 
nr_repasse_automatico, 
nr_ctr_boleto, 
ds_referencia, 
nr_quantidade, 
nr_correcao, 
ds_es, 
nr_desconto_ate_vencimento, 
ds_documento, 
nr_valor, 
dt_vencimento_original, 
id_plano5, 
id_servicos, 
id_pessoa, 
id_baixa, 
id_lote, 
id_acordo, 
id_tipo_servico, 
id_beneficiario, 
id_tipo_documento, 
id_matricula_socios, 
id_titular 
) 

( 
select 
--------------------------------------------------------------- 
0 as nr_juros, 
0 as nr_multa, 
0 as nr_desconto, 
to_date(substring('0'||(pc.nr_dia_vencimento),length('0'||(pc.nr_dia_vencimento))-1,2)||'/'||mesano,'dd/mm/yyyy') as dt_vencimento, 
0 as nr_taxa, 
true as is_ativo, 
0 as nr_valor_baixa, 
false as is_obrigacao, 
0 as nr_repasse_automatico, 
'' as nr_ctr_boleto, ------------------------------------------------------------------------------------------ ???? 
--------------referencia------------------------------ 
substring( 

 '0'||extract(month from to_date('01/'||mesano,'dd/mm/yyyy')-wref), 
 length('0'||extract(month from to_date('01/'||mesano,'dd/mm/yyyy')-wref))-1, 
 length('0'||extract(month from to_date('01/'||mesano,'dd/mm/yyyy')-wref)) 
)||'/'||extract(year from to_date('01/'||mesano,'dd/mm/yyyy')-wref) 
as ds_referencia, 
------------------------------------------------------- 
1 as nr_quantidade, 
0 as nr_correcao, 
'E' as ds_es, 
g.nr_desconto_ate_vencimento, 
'' as ds_documento,---------------------------------------- ???? 
g.nr_valor, 
to_date(substring('0'||(pc.nr_dia_vencimento),length('0'||(pc.nr_dia_vencimento))-wref,2)||'/'||mesano,'dd/mm/yyyy') as dt_vencimento_original, 
se.id_plano5, 
sp.id_servico, 
sp.id_cobranca_movimento as id_pessoa, 
null as id_baixa, 
g.id as id_lote, 
null as id_acordo, 
1 as id_tipo_servico, 
sp.id_pessoa as id_beneficiario, 
2 as id_tipo_documento, -------------------------------------------------- 
------------------------------------------------------------------------- 
s.id_matricula as id_matricula_socios, 
CASE WHEN s.titular is null then sp.id_cobranca_movimento else s.titular end as id_titular 
from 
fin_servico_pessoa as sp 
inner join soc_lote_geracao as g on g.id_servico_pessoa=sp.id and g.ds_vencimento=mesano ----and g.id in (select l.id from fin_lote as l left join fin_movimento as m on m.id_lote=l.id where m.id_lote is null) 
inner join fin_lote as l on l.id=g.id 
left join fin_movimento as m on m.id_lote=l.id 
inner join fin_servicos se on se.id=sp.id_servico 
inner join pes_pessoa_complemento as pc on pc.id_pessoa=sp.id_cobranca_movimento 
left join soc_socios_vw as s on s.codsocio=sp.id_pessoa 
where m.id_lote is null 
order by sp.id 
); 

/* 

select 
responsavel(8) 
fator de vencimento(4) 
conta_cobraça(3) 
lote de geracao(5) 
id_tipo_cobranca (soc_cobranca) (2) 
*/ 

-------- Cria o lote de Geração dos boletos SE NÃO FOR INDIVIDUAL 
/* 
Agrupa todos os registros neste lote com este boleto 
*/ 


if (pessoa is null) then 


insert into soc_lote_boleto (dt_processamento) (select current_date); 
wlote := (select max(id) from soc_lote_boleto); 
  
-------->>>> Grava nr_ctr_boleto em fin_movimento 
update fin_movimento 
set nr_ctr_boleto= 
----Pessoa 
right('00000000'||id_pessoa,8)|| 
----Fator de Vencimento 
right('0000'||(select CAST(right('00'||nr_dia_vencimento,2)||'/'||mesano as date)-CAST('07/10/1997' as date) from pes_pessoa_complemento where id_pessoa=fin_movimento.id_pessoa),4)|| 
----Conta Cobrança 
right('000'||(select id_conta_cobranca from fin_servico_conta_cobranca where id_servicos=fin_movimento.id_servicos and id_tipo_servico=fin_movimento.id_tipo_servico),3)|| 
----Lote de Geração 
right('00000'||text(wlote),5)|| 
----id do soc_cobranca (extrato) 
'01' 
where is_ativo=true and id_baixa is null and 
extract(month from dt_vencimento)=cast(left(mesano,2)as int) and extract(year from dt_vencimento)=cast(right(mesano,4)as int) and 
(nr_ctr_boleto='' or nr_ctr_boleto is null) and 
id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina=4); 


-------->>>> Insere Boleto 
insert into fin_boleto (nr_ctr_boleto,id_conta_cobranca,is_ativo,dt_vencimento,dt_vencimento_original,ds_mensagem) 
( 
select nr_ctr_boleto,cast(substring(nr_ctr_boleto,13,3) as int),true,cast(right(p.nr_dia_vencimento||'/'||mesano,10) as date),cast(right(p.nr_dia_vencimento||'/'||mesano,10) as date), 
(select ds_mensagem_boleto_associativo_pagador from conf_social) 
from fin_movimento as m 
inner join pes_pessoa_complemento as p on p.id_pessoa=m.id_pessoa 
where length(nr_ctr_boleto)=22 and nr_ctr_boleto not in (select nr_ctr_boleto from fin_boleto where length(nr_ctr_boleto) = 22) 
group by nr_ctr_boleto,cast(substring(nr_ctr_boleto,13,3) as int),p.nr_dia_vencimento 
); 


----------------------------------------------------------------------------------------------------- 
update fin_movimento set ds_documento=fin_boleto.ds_boleto from fin_boleto where fin_movimento.nr_ctr_boleto=fin_boleto.nr_ctr_boleto and (ds_documento is null or ds_documento='') and length(fin_movimento.nr_ctr_boleto)=22; 
----------------------------------------------------------------------------------------------------- 
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
inner join fin_movimento as m on m.nr_ctr_boleto=b.nr_ctr_boleto 
left join fin_movimento_boleto as mb on mb.id_boleto=b.id and mb.id_movimento=m.id 
where 
m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina=4) and 
mb.id is null 
and m.id_baixa is null 
group by m.id,b.id 
); 
--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 


end if; 

  
RETURN 1; 
END; 

$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_geramensalidades(integer, character varying) 
  OWNER TO postgres; 



DROP VIEW balancete_vw; 
DROP VIEW contabil_vw; 
DROP VIEW movimentos_vw; 


-- View: movimentos_vw 

-- DROP VIEW movimentos_vw; 

CREATE OR REPLACE VIEW movimentos_vw AS 
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
    fp.id_conciliacao_plano5, fp.dt_conciliacao, fp.id_conciliado, 
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

ALTER TABLE movimentos_vw 
  OWNER TO postgres; 







-- View: contabil_vw 

-- DROP VIEW contabil_vw; 

CREATE OR REPLACE VIEW contabil_vw AS 
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

ALTER TABLE contabil_vw 
  OWNER TO postgres; 




-- View: balancete_vw 

-- DROP VIEW balancete_vw; 

CREATE OR REPLACE VIEW balancete_vw AS 
 SELECT x.data, x.codigo1, x.conta1, x.codigo2, x.conta2, x.codigo3, x.conta3, 
    x.codigo4, x.conta4, x.classificador AS codigo5, x.conta5, 
    0::double precision AS saldo_anterior, sum(x.debito) AS debito, 
    sum(x.credito) AS credito, 0::double precision AS saldo_atual, x.id_conta, 
    p5.is_soma_debito, x.natureza_dc 
   FROM (        SELECT c.baixa AS data, p.codigo1, p.conta1, p.codigo2, 
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

ALTER TABLE balancete_vw 
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
    soc_boletos_processa_vw.processamento_boleto 
   FROM soc_boletos_processa_vw 
  WHERE NOT (soc_boletos_processa_vw.codigo IN ( SELECT m.id_pessoa 
           FROM fin_movimento m 
      JOIN pes_fisica f ON f.id_pessoa = m.id_pessoa 
     WHERE m.is_ativo = true AND m.id_baixa IS NULL AND m.dt_vencimento < ('now'::text::date - (( SELECT conf_social.nr_bloqueio_meses_boleto * 30 FROM conf_social)) - cast(extract(day from current_date) as int)) 
     GROUP BY m.id_pessoa)); 

ALTER TABLE soc_boletos_vw 
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

  
------------------------------------------************************************************** 
-------->>>> Insere fin_movimento_boleto 
insert into fin_movimento_boleto (id_movimento,id_boleto) 
( 
select m.id,b.id from fin_movimento as m 
inner join fin_boleto as b on b.nr_ctr_boleto=m.nr_ctr_boleto 
where m.id = ANY(id_movimento) 
); 

  
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
inner join fin_movimento as m on m.nr_ctr_boleto=b.nr_ctr_boleto 
left join fin_movimento_boleto as mb on mb.id_boleto=b.id and mb.id_movimento=m.id 
where 
m.id_servicos not in (select id_servicos from fin_servico_rotina where id_rotina=4) and 
mb.id is null 
and m.id_baixa is null 
group by m.id,b.id 
); 


--------------------------------------------------------------------------------- 
--------------------------------------------------------------------------------- 
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



ALTER TABLE public.fin_conta_cobranca
    ADD COLUMN ds_variacao character varying(10) DEFAULT '';