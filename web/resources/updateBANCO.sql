-- RODAR EM COMERCIO LIMEIRA E COMERCIO RP
------------------------------------------
------------------------------------------
------------------------------------------



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











-- FUNCTION: public.func_geramensalidades(integer, character varying)

-- DROP FUNCTION public.func_geramensalidades(integer, character varying);

CREATE OR REPLACE FUNCTION public.func_geramensalidades(
	pessoa integer,
	mesano character varying)
RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$
 
declare wlote int :=0; 
declare wlote_geracao int :=0; 
declare wref int :=(select nr_referencia from conf_social); 
BEGIN 

/* 
*****************************************************************************************************
                                            OFICIAL
*****************************************************************************************************
*/ 
--------- Inativa Convênios de sócios Inativos 


  
update matr_convenio_medico set dt_inativo=current_date,ds_motivo_inativacao='SÓCIO INATIVO' where id_servico_pessoa in 
( 
select sp.id  from matr_convenio_medico as m 
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

delete from fin_lote where id in 
( 
select l.id from  fin_lote as l 
left join fin_movimento as m on m.id_lote=l.id 
where m.id_lote is null  and l.dt_lancamento > (cast(now() as date)-20) 
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
from  pes_pessoa_vw 
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
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano  ,10) as date), 0, m.id_categoria)) 
as numeric 
) 
,2) 

)


WHEN (sp.nr_desconto <> 0 and sp.nr_valor_fixo = 0) then 

func_periodo_valor(sp.id_periodo_cobranca,
  
round( 
cast 
( 
    func_valor_servico_cheio(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano  ,10) as date)) *(1-(sp.nr_desconto/100)) 
as numeric 
) 
,2) 

)

else  sp.nr_valor_fixo 
  
end as valor, 

-------------------------------------- valor até o vencimento 

CASE 

WHEN (sp.nr_desconto <> 0 and sp.nr_valor_fixo = 0) then 


func_periodo_valor(sp.id_periodo_cobranca,


round( 
cast 
( 
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano  ,10) as date), 1, m.id_categoria)) 
as numeric 
) 
,2) 

)

WHEN (sp.nr_desconto <> 0 and sp.nr_valor_fixo = 0) then 

func_periodo_valor(sp.id_periodo_cobranca,

round( 
cast 
( 
    func_valor_servico_cheio_desconto_ate_vencimento(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano  ,10) as date)) *(1-(sp.nr_desconto/100)) 
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
(func_valor_servico(sp.id_pessoa, sp.id_servico, cast(right('0'||pc.nr_dia_vencimento||'/'|| mesano  ,10) as date), 0, m.id_categoria)) *(1-(sp.nr_desconto/100)) 
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
'SR.S(A)  ASSOCIADOS(A), GENTILEZA TRAZER CPF, RG, COMPROVANTE DE ENDEREÇO E CARTEIRA DE TRABALHO (PÁGINAS: NÚMERO DA CARTEIRA PROF., QUALIFICAÇÃO CIVIL E CONTRATO DE TRABALHO VIGENTE), PARA FINS DE RECADASTRAMENTO.'
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
--------------------------------  insere fin_movimento_boleto  ------------------------------------------------- 

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

$BODY$;

ALTER FUNCTION public.func_geramensalidades(integer, character varying)
    OWNER TO postgres;
