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


update fin_boleto set dt_vencimento=dt_vencimento_original where dt_vencimento is null and dt_vencimento_original is not null 

update fin_boleto set dt_vencimento='01/01/1900',dt_vencimento_original='01/01/1900' where dt_vencimento is null; 
  

------------------------------------------------------------------------------------------------------------ 

alter table fin_boleto alter column dt_vencimento set not null; 

alter table fin_boleto alter column dt_vencimento_original set not null; 