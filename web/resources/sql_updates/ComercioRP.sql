
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