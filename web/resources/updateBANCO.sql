-- RODAR COMERCIO RP
------------------------------

ALTER TABLE conf_social ADD nr_meses_inadimplentes_impressao_boletos_social int;
-- COMERCIO RP
UPDATE conf_social SET nr_meses_inadimplentes_impressao_boletos_social = 3;
-- DEMAIS CLIENTES
UPDATE conf_social SET nr_meses_inadimplentes_impressao_boletos_social = 1000;



CREATE OR REPLACE FUNCTION func_subtrai_meses(data date, meses int) 
  RETURNS date AS 
$BODY$ 
    declare data_ret date; 
BEGIN 
  
      data = (select current_date - (meses||' month')::interval); 
    
    RETURN data; 
END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_subtrai_meses(date, int) 
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
    soc_boletos_processa_vw.id_conta_cobranca 
   FROM soc_boletos_processa_vw 
    
    WHERE NOT (soc_boletos_processa_vw.codigo IN ( SELECT m.id_pessoa 
           FROM fin_movimento m 
      JOIN pes_fisica f ON f.id_pessoa = m.id_pessoa 
     WHERE m.is_ativo = true AND m.id_baixa IS NULL AND m.dt_vencimento < (select func_subtrai_meses(current_date-(extract(day from current_date)::integer),nr_meses_inadimplentes_impressao_boletos_social -1) from conf_social) 
     GROUP BY m.id_pessoa)); 

ALTER TABLE soc_boletos_vw 
  OWNER TO postgres; 

UPDATE fin_status_retorno SET ds_descricao = 'Agendamento de Registro' WHERE id = 4;


-- RODAR COMERCIO RP
---------------------------------------




ALTER TABLE fin_forma_pagamento ADD COLUMN ds_documento character varying (50) DEFAULT '';

ALTER TABLE fin_boleto ADD COLUMN dt_processamento date NOT NULL DEFAULT CURRENT_DATE;


CREATE OR REPLACE FUNCTION func_multa_ass( 
    id_movimento integer, 
    data date) 
  RETURNS double precision AS 
$BODY$ 

declare idMov        int  :=id_movimento; 
declare qMeses       int  :=0; 
declare qDias        int  :=0; 
declare mPrimeiroMes float :=0; 
declare mSegundoMes  float :=0; 
declare multa        float :=0; 
    
  

declare idBaixa      int  := 0; 
declare idservico    int  := 0; 
declare vencto       date := null; 
declare valor        float := 0; 
declare es           varchar(1) :=''; 



BEGIN 
  
    if (data is null) then data=current_date; end if; 

    idBaixa    := (select id_baixa     from fin_movimento where id=idMov); 
    vencto     := (select dt_vencimento from fin_movimento where id=idMov); 
    es         := (select ds_es   from fin_movimento where id=idMov); 
    
    if (idBaixa > 0 ) then 
       multa := (select nr_multa from fin_movimento where id=idMov); 
    end if; 
  
    if (data>vencto and idBaixa is null and es='E') then 

idservico  := (select id_servicos  from fin_movimento where id=idMov); 
valor      := (select nr_valor     from fin_movimento where id=idMov); 

       qDias        := (data-vencto); 
       qMeses       := (func_intervalo_meses(data,vencto)); 
  
       mPrimeiroMes := (select cr.nr_multa_primeiro_mes from fin_movimento as m 
       left join fin_correcao                 as cr on cr.id_servicos=m.id_servicos and 
       (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
       (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
       where m.id=idMov); 

       mSegundoMes := (select cr.nr_multa_apartir_2mes from fin_movimento as m 
       left join fin_correcao                 as cr on cr.id_servicos=m.id_servicos and 
       (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
       (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
        where m.id=idMov); 

       if (mPrimeiroMes is null) then mPrimeiroMes :=0; end if; 
       if (mSegundoMes is null) then mSegundoMes :=0; end if; 

       multa := multa + ((mPrimeiroMes * valor)/100); 
       multa := multa + (qMeses*((mSegundoMes * valor)/100)); 
       multa := round(cast( multa as decimal) , 2); 

    end if; 

    if (multa is null) then multa=0; end if; 

    
    RETURN multa; 

END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_multa_ass(integer, date) 
  OWNER TO postgres;  
  
  
  

  
CREATE OR REPLACE FUNCTION func_juros_ass( 
    id_movimento integer, 
    data date) 
  RETURNS double precision AS 
$BODY$ 

declare qDias        int :=0; 
declare idMov        int :=id_movimento; 
declare qMeses       int :=0; 
declare jPrimeiroMes float:=0; 
declare jSegundoMes  float:=0; 
declare juros        float:=0; 
declare jJurosDiario float:=0; 
  

declare idBaixa      int  := 0; 
declare idservico    int  := 0; 
declare vencto       date := null; 
declare valor        float := 0; 
declare ref          varchar(7) :=''; 
declare es           varchar(1) :=''; 

  
BEGIN 

   if (data is null) then data=current_date; end if; 

   vencto     := (select dt_vencimento from fin_movimento where id=idMov); 
   idBaixa    := (select id_baixa     from fin_movimento where id=idMov); 
   es         := (select ds_es   from fin_movimento where id=idMov); 
    
   if (idBaixa > 0 ) then 
      juros := (select nr_juros from fin_movimento where id=idMov); 
   end if; 
  
   if (data>vencto and idBaixa is null and es='E') then 

   idservico  := (select id_servicos  from fin_movimento where id=idMov); 
   valor      := (select nr_valor     from fin_movimento where id=idMov); 
   ref        := (select ds_referencia from fin_movimento where id=idMov ); 
    
            qMeses       := (select func_intervalo_meses(data,vencto)); 
            qDias        := (select data-vencto); 

    jPrimeiroMes := (select cr.nr_juros_pri_mes from fin_correcao as cr 
                where cr.id_servicos=idServico and 
(substring(ref,4,4)||substring(ref,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
(substring(ref,4,4)||substring(ref,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
); 

jSegundoMes := (select cr.nr_juros_apartir_2mes from fin_correcao as cr 
                where cr.id_servicos=idServico and 
       (substring(ref,4,4)||substring(ref,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
       (substring(ref,4,4)||substring(ref,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
); 

jJurosDiario:= (select cr.nr_juros_diarios from fin_correcao as cr 
       where cr.id_servicos=idServico and 
     (substring(ref,4,4)||substring(ref,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
     (substring(ref,4,4)||substring(ref,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
); 


        if (jPrimeiroMes is null) then jPrimeiroMes :=0; end if; 
        if (jSegundoMes is null) then jSegundoMes :=0; end if; 

  
juros := juros + (jJurosDiario*qDias)* valor / 100; 
juros := juros + ((jPrimeiroMes * valor)/100); 
juros := juros + (qMeses*((jSegundoMes * valor)/100)); 
juros := round(cast(juros as decimal), 2); 

    end if; 
    
    if (juros is null) then juros=0; end if; 


    RETURN juros; 
END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_juros_ass(integer, date) 
  OWNER TO postgres; 
  
  
CREATE OR REPLACE FUNCTION func_correcao_ass( 
    idmov integer, 
    data date) 
  RETURNS double precision AS 
$BODY$ 

declare indice     int:= 
                 ( 
                 select cr.id_indice from fin_movimento as m 
   left join fin_correcao                 as cr on cr.id_servicos=m.id_servicos and 
   (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) >= (substring(cr.ds_ref_inicial,4,4)||substring(cr.ds_ref_inicial,1,2)) and 
     (substring(m.ds_referencia,4,4)||substring(m.ds_referencia,1,2)) <= (substring(cr.ds_ref_final,4,4)||substring(cr.ds_ref_final,1,2)) 
       where m.id=idMov 
       ); 

declare vlIndice     float := 0; 
  
declare idBaixa      int  := (select id_baixa     from fin_movimento where id=idMov); 
declare idservico    int  := (select id_servicos  from fin_movimento where id=idMov); 
declare vencto       date := (select dt_vencimento from fin_movimento where id=idMov); 
declare valor        float := (select nr_valor     from fin_movimento where id=idMov); 
declare valorBase    float := valor; 
declare ref          varchar(7) :=(select ds_referencia from fin_movimento where id=idMov ); 
declare es           varchar(1) := (select ds_es   from fin_movimento where id=idMov); 
declare correcao        float := 0; 


DECLARE lista CURSOR FOR 
( 
SELECT nr_valor FROM fin_indice_mensal 
where 
id_indice=indice and 
( 
text(nr_ano)||right('0'||text(nr_mes),2) 
>=----'201201' 
(text(extract('year' from vencto))||right('0'||text(extract('month' from vencto)),2)) 
) 
order by nr_ano,nr_mes 
); 
begin   


   if (data is null) then data=current_date; end if; 



   if (idBaixa > 0) then 
      correcao := (select nr_correcao from fin_movimento where id=idMov); 
   end if; 

   if (idBaixa is null and lista is not null) then 
open lista; 
if ( data > vencto and idBaixa is null and es='E') then 
-- Para ir para o primeiro registo: 
FETCH FIRST FROM lista into vlIndice; 
loop 
if (vlIndice is null) then vlIndice:=0; end if; 
valor := valor + ((valor * vlIndice)/100); 
FETCH NEXT FROM lista into vlIndice; 
EXIT WHEN NOT FOUND; 
end loop; 
end if;------ se data vencida 
close lista; 
correcao := round(cast( (valor-valorBase)*100 as decimal) / 100, 2); 
   end if; 
    
   RETURN correcao; 
  
    
end; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_correcao_ass(integer, date) 
  OWNER TO postgres; 
  


CREATE OR REPLACE VIEW soc_boletos_processa_vw AS 
 SELECT l.id AS id_fin_lote, m.id AS id_fin_movimento, m.nr_ctr_boleto, 
    sl.id AS id_lote_boleto, sl.dt_processamento AS processamento, 
    pr.id AS codigo, pr.ds_nome AS responsavel, 
    '1997-10-07'::date + "substring"(m.nr_ctr_boleto::text, 9, 4)::integer AS vencimento, 
    mtr.matricula, mtr.grupo_categoria, mtr.categoria, 
    se.ds_descricao AS servico, m.id_beneficiario, 
    pb.ds_nome AS nome_beneficiario, 
        CASE 
            WHEN m.dt_vencimento >= 'now'::text::date THEN func_multa_ass(m.id,b.dt_processamento) + func_juros_ass(m.id,b.dt_processamento) + func_correcao_ass(m.id,b.dt_processamento) + (m.nr_valor - m.nr_desconto_ate_vencimento) 
            ELSE func_multa_ass(m.id,b.dt_processamento) + func_juros_ass(m.id,b.dt_processamento) + func_correcao_ass(m.id,b.dt_processamento) + m.nr_valor 
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
    b.dt_registro_baixa AS data_registro_baixa, b.id_conta_cobranca,m.id_baixa,b.dt_processamento as processamento_boleto 
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
    soc_boletos_processa_vw.id_conta_cobranca, 
    soc_boletos_processa_vw.id_baixa, 
    soc_boletos_processa_vw.processamento_boleto 

   FROM soc_boletos_processa_vw; 

ALTER TABLE soc_boletos_geral_vw 
  OWNER TO postgres; 

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
    soc_boletos_processa_vw.id_conta_cobranca, 
    soc_boletos_processa_vw.id_baixa, 
    soc_boletos_processa_vw.processamento_boleto 
   FROM soc_boletos_processa_vw 
  WHERE NOT (soc_boletos_processa_vw.codigo IN ( SELECT m.id_pessoa 
           FROM fin_movimento m 
      JOIN pes_fisica f ON f.id_pessoa = m.id_pessoa 
     WHERE m.is_ativo = true AND m.id_baixa IS NULL AND m.dt_vencimento < (date_trunc('month'::text, 'now'::text::date::timestamp with time zone) - '1 mon'::interval) 
     GROUP BY m.id_pessoa)); 

ALTER TABLE soc_boletos_vw 
  OWNER TO postgres; 
  

CREATE OR REPLACE FUNCTION func_correcao_valor_ass( xnr_ctr_boleto character varying(22) )
RETURNS double precision
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$

  declare wvalor double precision;
 
BEGIN 
   wvalor =
	(
		select sum(valor) as valor from 
		( 
		select 
		case when m.dt_vencimento < current_date then
		sum
		(
		func_multa_ass(m.id, b.dt_processamento) + func_juros_ass(m.id, b.dt_processamento) + func_correcao_ass(m.id, b.dt_processamento) + m.nr_valor 
		)
		else  sum(m.nr_valor - m.nr_desconto_ate_vencimento) end as valor 
		from fin_movimento as m
		inner join fin_boleto as b on b.nr_ctr_boleto=m.nr_ctr_boleto
		where b.nr_ctr_boleto=xnr_ctr_boleto
		group by m.dt_vencimento
	) as x

	);
	
	return wvalor;
 
END; 

$BODY$;

ALTER FUNCTION func_correcao_valor_ass(character varying(22))
    OWNER TO postgres;


-- RODAR COMERCIO RP
--------------------------------------------------------------------------------

ALTER TABLE fin_baixa ADD COLUMN id_departamento integer;
ALTER TABLE fin_baixa ADD COLUMN id_filial integer;


ALTER TABLE fin_baixa ADD CONSTRAINT fk_fin_baixa_id_departamento FOREIGN KEY (id_departamento)
    REFERENCES public.seg_departamento (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE fin_baixa ADD CONSTRAINT fk_fin_baixa_id_filial FOREIGN KEY (id_filial)
    REFERENCES public.pes_filial (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;


CREATE OR REPLACE VIEW public.fin_fecha_caixa_geral_vw AS
        (         
SELECT c.ds_descricao AS caixa, 
	   f.dt_data AS dt_fechamento, 
       f.ds_hora AS hora_fechamento, 
       t.dt_lancamento AS dt_transferencia, 
	   b.id_caixa, 
       b.id_fechamento_caixa, 
	   fo.nr_valor AS valor, 
       p.ds_nome AS operador,
       b.dt_baixa, 
       b.id AS id_baixa,
	   b.id_departamento,
	   b.id_filial
  FROM fin_baixa b
  JOIN fin_caixa c ON c.id = b.id_caixa
  JOIN fin_forma_pagamento fo ON fo.id_baixa = b.id
  JOIN seg_usuario u ON u.id = b.id_usuario
  JOIN pes_pessoa p ON p.id = u.id_pessoa
  LEFT JOIN fin_fechamento_caixa f ON f.id = b.id_fechamento_caixa
  LEFT JOIN fin_transferencia_caixa t ON t.id_fechamento_saida = b.id_fechamento_caixa AND t.id_status = 12
 WHERE f.dt_fechamento_geral IS NULL AND b.dt_importacao IS NULL AND (b.dt_baixa < now()::date OR b.id_fechamento_caixa > 0) AND c.nr_caixa <> 1
 UNION 
SELECT c.ds_descricao AS caixa, 
	   f.dt_data AS dt_fechamento, 
       f.ds_hora AS hora_fechamento, 
       t.dt_lancamento AS dt_transferencia, 
       t.id_caixa_entrada AS id_caixa, 
       t.id_fechamento_entrada AS id_fechamento_caixa, 
       t.nr_valor AS valor, 
	   p.ds_nome AS operador, 
       t.dt_lancamento AS dt_baixa, 
       t.id AS id_baixa,
       null AS id_departamento,
       null AS id_filial
  FROM fin_transferencia_caixa t
  JOIN fin_caixa c ON c.id = t.id_caixa_entrada
  JOIN seg_usuario u ON u.id = t.id_usuario
  JOIN pes_pessoa p ON p.id = u.id_pessoa
  LEFT JOIN fin_fechamento_caixa f ON f.id = t.id_fechamento_entrada
 WHERE t.id_status <> 12 AND (t.dt_lancamento < now()::date OR t.id_fechamento_entrada > 0) AND c.nr_caixa <> 1)
 UNION 
SELECT c.ds_descricao AS caixa, 
       f.dt_data AS dt_fechamento, 
       f.ds_hora AS hora_fechamento, 
	   t.dt_lancamento AS dt_transferencia, 
       t.id_caixa_saida AS id_caixa, 
       t.id_fechamento_saida AS id_fechamento_caixa, 
	   t.nr_valor AS valor, 
       p.ds_nome AS operador, 
	   t.dt_lancamento AS dt_baixa, 
       t.id AS id_baixa,
       null AS id_departamento,
       null AS id_filial
  FROM fin_transferencia_caixa t
  JOIN fin_caixa c ON c.id = t.id_caixa_saida
  JOIN seg_usuario u ON u.id = t.id_usuario
  JOIN pes_pessoa p ON p.id = u.id_pessoa
  LEFT JOIN fin_fechamento_caixa f ON f.id = t.id_fechamento_saida
 WHERE t.id_status <> 12 AND (t.dt_lancamento < now()::date OR t.id_fechamento_saida > 0) AND c.nr_caixa <> 1;

ALTER TABLE public.fin_fecha_caixa_geral_vw
    OWNER TO postgres;


INSERT INTO seg_rotina (id, ds_rotina, ds_nome_pagina, ds_classe, is_ativo, ds_acao) SELECT 488, 'IMPRESSÃO POR ESCRITÓRIO', '"/Sindical/impressaoEscritorio.jsf"', '', true, '' WHERE NOT EXISTS ( SELECT id FROM seg_rotina WHERE id = 488);
SELECT setval('seg_rotina_id_seq', max(id)) FROM seg_rotina;


-- RODAR EM TODOS --------------------------------------------------------------
--------------------------------------------------------------------------------

ALTER TABLE arr_certidao_disponivel ADD COLUMN ds_observacao character varying (8000) DEFAULT '';


CREATE OR REPLACE VIEW arr_contribuintes_vw AS 
 SELECT p.id AS id_pessoa, j.id AS id_juridica, j.id_contabilidade, p.ds_nome, 
    p.ds_documento, ccid.id_convencao, ccid.id_grupo_cidade, co.ds_descricao, 
    gc.ds_descricao AS ds_grupo_cidade, i.dt_inativacao, 
    mt.ds_descricao AS motivo, mt.id AS id_motivo, j.id_cnae, c.ds_cnae AS cnae, 
    c.ds_numero 
   FROM pes_pessoa p 
   JOIN pes_juridica j ON j.id_pessoa = p.id 
   JOIN pes_cnae c ON c.id = j.id_cnae 
   JOIN pes_pessoa_endereco pe ON j.id_pessoa = pe.id_pessoa AND pe.id_tipo_endereco = 5 
   JOIN end_endereco e ON pe.id_endereco = e.id 
   JOIN arr_cnae_convencao cc ON cc.id_cnae = j.id_cnae 
   JOIN arr_grupo_cidades gcs ON gcs.id_cidade = e.id_cidade 
   JOIN arr_convencao_cidade ccid ON ccid.id_grupo_cidade = gcs.id_grupo_cidade AND ccid.id_convencao = cc.id_convencao 
   JOIN arr_convencao co ON co.id = ccid.id_convencao 
   JOIN arr_grupo_cidade gc ON gc.id = ccid.id_grupo_cidade 
   LEFT JOIN arr_contribuintes_inativos i ON i.id_juridica = j.id AND i.dt_ativacao IS NULL 
   LEFT JOIN arr_motivo_inativacao mt ON mt.id = i.id_motivo_inativacao 

group by 
 p.id, j.id, j.id_contabilidade, p.ds_nome, 
    p.ds_documento, ccid.id_convencao, ccid.id_grupo_cidade, co.ds_descricao, 
    gc.ds_descricao, i.dt_inativacao, 
    mt.ds_descricao, mt.id, j.id_cnae, c.ds_cnae, 
    c.ds_numero; 

ALTER TABLE arr_contribuintes_vw 
  OWNER TO postgres; 

-- ATENÇÃO ------------------------------------------------
-- JOGAR REPIS.jasper, REPIS_AUXILIAR.jasper EM SINCOVAGASP