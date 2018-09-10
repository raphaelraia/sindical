
-- Function: func_catraca(integer, integer, integer) 

-- DROP FUNCTION func_catraca(integer, integer, integer); 

CREATE OR REPLACE FUNCTION func_catraca( 
    cartao integer, 
    depto integer, 
    tipo integer) 
  RETURNS integer AS 
$BODY$ 
   declare pessoa int; 
   declare xpessoa int; 
   declare liberado boolean; 
   declare via integer; 
   declare jogador int; 


BEGIN 

    via=null; 
/* 
1-cartão 
2-biometria 
3-convite 

select * from soc_socios_vw where nome like 'MARCELO RIBEIRO T%' 


select * from soc_socios_vw where nome like 'BRUNO VIEIRA%' 

select * from soc_carteirinha where id_pessoa=174660 




select func_catraca(74660,12,1,null) 
select func_catraca(74660,12,1) 





28502 


select 
usa_clube_domingo, 
usa_clube_segunda,   
usa_clube_terca, 
usa_clube_quarta, 
usa_clube_quinta, 
usa_clube_sexta, 
usa_clube_sabado 
from soc_categoria where id=4 




*/ 
  
   liberado = false; 

   if (tipo=1) then 
      pessoa = (select id_pessoa from soc_carteirinha where nr_cartao=cartao); 
   end if; 
  if (tipo=2) then 
       pessoa = cartao; 
   end if; 

  

---------------------- LIBERAÇÃO DE DEPARTAMENTO 
   if (tipo<>3) then 

       xpessoa=pessoa; 

       pessoa = (select id_pessoa from seg_permissao_catraca where id_departamento=depto and id_pessoa=pessoa); 
  
       if ( pessoa > 0 ) then 
              liberado=true; 
       else 
               pessoa=xpessoa; 
       end if; 
   end if; 



-------- Se jogador No clube 

        if (pessoa is not null and depto=12 and liberado=false and tipo <> 3) then 
  
-- Dependente 

            jogador = 
        ( 
select sp.id_pessoa from fin_servico_pessoa as sp 
inner join eve_campeonato_dependente as cd on cd.id_servico_pessoa=sp.id 
inner join matr_campeonato as m on m.id = cd.id_matricula_campeonato 
inner join eve_campeonato as ec on ec.id=m.id_campeonato 
where sp.is_ativo=true 
and ec.dt_inicio <= current_date and ec.dt_fim >= current_date 
                and func_inadimplente_clube(sp.id_pessoa, 2)=false 
                and sp.id_pessoa = pessoa limit 1 
     ); 

-- Titular 

        if (jogador is null) then 
            jogador= 
        ( 
select sp.id_pessoa from fin_servico_pessoa as sp 
inner join matr_campeonato as m on m.id_servico_pessoa = sp.id 
inner join eve_campeonato as ec on ec.id=m.id_campeonato 
where sp.is_ativo=true 
                and sp.id_pessoa=pessoa 
and ec.dt_inicio <= current_date and ec.dt_fim >= current_date 
                and func_inadimplente_clube(sp.id_pessoa, 2)=false limit 1 
     ); 
         end if; 
    end if; --- if (pessoa is null and depto=12 and liberado=false) then 



    if (jogador >0 and jogador in (select codsocio from soc_socios_vw where codsocio=pessoa)) then jogador=null; end if; 



---------------------- 


  
/* 
  
select * from soc_carteirinha where id_pessoa=28502 

academia 
select func_catraca(28502, 11, 1) 

clube 
select func_catraca(22, 12, 3) 



  
12 - Recepção Clube" 

11 - Recepção Academia 
16 - HIDROGINÁSTICA 
17 - SALÃO DE DANÇA 
18 - RECEPÇÃO EDUCAÇÃO INFANTIL 

  
*/ 


------------------------- bug Associação 

        if ('45.236.239/0001-01'=(select ds_documento from pes_pessoa where id=1) and cartao=107970) then 
           pessoa = -9; 
           liberado = true; 
       end if; 
-------------------- RECEPÇÃO EDUCAÇÃO INFANTIL 
  
   if (depto in (18) and tipo in (1,2) and liberado=false) then 
        pessoa = 
        ( 
  select a.id_pessoa from esc_autorizados as a 
      inner join matr_escola as m on m.id=a.id_matricula_escola 
  inner join fin_servico_pessoa as sp on sp.id=m.id_servico_pessoa 
  where a.id_pessoa=pessoa and sp.is_ativo=true limit 1 
        ); 

        if (pessoa is null) then 
           pessoa = -2; 
        end if; 
   end if; 
-------------------- ACADEMIA 
  
   if (depto in (11,16,17) and tipo in (1,2) and liberado=false) then 

-------------- Verifica se é Aluno 
            pessoa= 
          ( 
        
             select p.id from pes_pessoa as p 
             where p.id=pessoa and 
                 p.id in 
               ( 
                 select sp.id_pessoa from fin_servico_pessoa as sp 
                 inner join matr_academia as m on m.id_servico_pessoa=sp.id 
                 where sp.is_ativo=true and m.dt_inativo is null and sp.id_pessoa=pessoa and false in (select func_inadimplente(pessoa, 2)) 
              ) limit 1 
         ); 
 ------------- se não for aluno 
        if (pessoa is null) then 
            pessoa = -1; 
        end if; 

----------------- verificação do DEPARTAMENTO DA CATRACA 


        if (pessoa > 0) then 
              pessoa = 
          ( 
              select p.id from pes_pessoa as p 
              where p.id=pessoa and 
              ( 
                   p.id in 
                  ( 
                     select sp.id_pessoa from fin_servico_pessoa as sp 
                     inner join matr_academia as m on m.id_servico_pessoa=sp.id 
                     where sp.is_ativo=true and m.dt_inativo is null and sp.id_pessoa=pessoa and false in (select func_inadimplente(pessoa, 2)) 
                     and sp.id_servico in (select id_servico from soc_catraca_servico_depto where id_departamento=depto) 
                  ) 
             ) limit 1 
          ); 
          end if; 

-------------------- se não pertencer ao departamento           
          if (pessoa is null) then 
             pessoa = -2; 
          end if; 
----------------- verificar horários da matrícula 
        if (pessoa >0) then 
           pessoa = (select id_pessoa from aca_grade_vw where dia_hoje=id_semana and horario_intervalo=true and id_pessoa=pessoa and id_departamento=depto); 
        end if; 
 -------------------- se não pertencer a grade de horários 

        if (pessoa is null ) then 
             pessoa = -5; 
        end if; 

    end if; 


 -------------------- CLUBE CARTÃO OU BIOMETRIA 

  

    if (depto=12 and tipo in (1,2) and liberado=false and jogador is null) then 
        pessoa= 
        ( 
           select codsocio from soc_socios_vw 
           where 
          codsocio=pessoa and func_inadimplente_clube(codsocio, 2)=false 
          limit 1 
        ); 

        ---- suspensão 
        if (pessoa > 0) then 
           if ((select count(*) from soc_suspencao where id_pessoa=pessoa and dt_inicial<=current_date and dt_final>=current_date )>0) then 
           pessoa = -4; 
            end if; 
        end if; 

-------------- Verifica se está liberado o dia da semana 
         if (pessoa > 0) then 
             if 
             ( 
                  select 
                 c.id 
                 from soc_socios_vw as s 
                 inner join soc_categoria as c on c.id=s.id_categoria 
                 where s.codsocio= pessoa 
                 and 
                 ( 
                   (EXTRACT( DOW FROM now())=0 and usa_clube_domingo=true) or 
                   (EXTRACT( DOW FROM now())=1 and usa_clube_segunda=true) or 
                   (EXTRACT( DOW FROM now())=2 and usa_clube_terca=true) or 
                   (EXTRACT( DOW FROM now())=3 and usa_clube_quarta=true) or 
                   (EXTRACT( DOW FROM now())=4 and usa_clube_quinta=true) or 
                   (EXTRACT( DOW FROM now())=5 and usa_clube_sexta=true) or 
                   (EXTRACT( DOW FROM now())=6 and usa_clube_sabado=true) 
                 ) 

              ) is null then pessoa = -5;   
                    
              end if; 
          end if; 
  
     end if; 

 -------------------- CLUBE CONVITE 
  
    if (depto=12 and tipo=3 and liberado=false) then 
        ---- se existe a pessoa 
        pessoa=(select id_sis_pessoa from conv_movimento where id=cartao); 

        if (pessoa is null) then pessoa= -8; end if; 
  
        ---- se convite já utilizado 
       if (pessoa > 0) then 
           if ( 
                   ((select count(*) from conv_movimento as cm inner join conf_social as c on c.id >0 where cm.id=4 and is_ativo=false and c.is_libera_convite_dia = false) > 0) or 
                   ((select count(*) from conv_movimento as cm inner join conf_social as c on c.id >0 where cm.id=4 and is_ativo=false and c.is_libera_convite_dia = true and current_date <> dt_entrada) > 0) 
               ) then 
              pessoa=-6; 
            end if; 
        end if; 
  
         ---- se convite está vencido 
        if (pessoa > 0) then 
           if (select count(*) from conv_movimento where dt_validade >= current_date and id=cartao)=0 then 
              pessoa=-7; 
            end if; 
        end if; 
  
        ----- se convite pode ser utilizado hoje. 

        if (pessoa > 0) then 
--------------- 
           if 
           ( 
              select 
              is_ativo 
              from conv_movimento as m 
              inner join conv_servico as se on se.id=m.id_servicos 
              where   
              m.id=cartao 
              and 
              ( 
                (EXTRACT( DOW FROM now())=0 and is_domingo=true) or 
                (EXTRACT( DOW FROM now())=1 and is_segunda=true) or 
                (EXTRACT( DOW FROM now())=2 and is_terca=true) or 
                (EXTRACT( DOW FROM now())=3 and is_quarta=true) or 
                (EXTRACT( DOW FROM now())=4 and is_quinta=true) or 
                (EXTRACT( DOW FROM now())=5 and is_sexta=true) or 
                (EXTRACT( DOW FROM now())=6 and is_sabado=true) or 

( 
 	     ( 
select count(*) from hom_feriados where (dt_data=current_date and is_repete=false) or 
(extract(day from dt_data)=extract(day from current_date) and extract(month from dt_data)=extract(month from current_date) and is_repete=true) limit 1 
      ) > 0 and is_feriado=true   
      

                 ) 
              ) 

           ) is null then 
                 pessoa = -5; 
           end if; 
-------------------- 
        end if; 
    end if; 


    if (jogador>0) then pessoa=jogador; end if; 

  
---------------- verifica se o cartão já foi passado e bloqueia temporariamente ---------- 
   if (tipo=1 and liberado=false and pessoa > 0) then 
        if ( func_libera_catraca_minutos(depto, pessoa)=false ) then 
              pessoa=-10; 
        end if; 
    end if; 
 ------------------------------------------------------------------------------------------       
    if (pessoa is null) then pessoa=-1; end if; 
  
    RETURN pessoa; 
END; 
$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 
ALTER FUNCTION func_catraca(integer, integer, integer) 
  OWNER TO postgres; 



-- Function: func_catraca(integer, integer, integer, integer) 

-- DROP FUNCTION func_catraca(integer, integer, integer, integer); 

CREATE OR REPLACE FUNCTION func_catraca( 
    cartao integer, 
    depto integer, 
    tipo integer, 
    via integer) 
  RETURNS integer AS 
$BODY$ 

   declare pessoa int; 

   declare xpessoa int; 

   declare liberado boolean; 

   declare jogador int; 

  
BEGIN 

  via=null; 

/* 

1-cartão 

2-biometria 

3-convite 
  
  
--- teste 

select func_catraca(2286,11,1,null) 
  
select func_catraca(74660,12,1) 

    

select 

usa_clube_domingo, 

usa_clube_segunda, 

usa_clube_terca, 

usa_clube_quarta, 

usa_clube_quinta, 

usa_clube_sexta, 

usa_clube_sabado 

from soc_categoria where id=4 
    
*/ 
  
   liberado = false; 
  
  if (tipo=1) then 

      pessoa = (select id_pessoa from soc_carteirinha where nr_cartao=cartao); 

   end if; 

  if (tipo=2) then 

       pessoa = cartao; 

   end if; 
  
---------------------- LIBERAÇÃO DE DEPARTAMENTO 

   if (tipo<>3) then 

  

      xpessoa=pessoa; 

  

      pessoa = (select id_pessoa from seg_permissao_catraca where id_departamento=depto and id_pessoa=pessoa); 

  

       if ( pessoa > 0 ) then 

              liberado = true; 

       else 

               pessoa=xpessoa; 

       end if; 

   end if; 






-------- Se jogador No clube 

        if (pessoa is not null and depto=12 and liberado=false and tipo <> 3) then 
  
-- Dependente 

            jogador = 
        ( 
select sp.id_pessoa from fin_servico_pessoa as sp 
inner join eve_campeonato_dependente as cd on cd.id_servico_pessoa=sp.id 
inner join matr_campeonato as m on m.id = cd.id_matricula_campeonato 
inner join eve_campeonato as ec on ec.id=m.id_campeonato 
where sp.is_ativo=true 
and ec.dt_inicio <= current_date and ec.dt_fim >= current_date 
                and func_inadimplente_clube(sp.id_pessoa, 2)=false 
                and sp.id_pessoa = pessoa limit 1 
     ); 

-- Titular 

        if (jogador is null) then 
            jogador= 
        ( 
select sp.id_pessoa from fin_servico_pessoa as sp 
inner join matr_campeonato as m on m.id_servico_pessoa = sp.id 
inner join eve_campeonato as ec on ec.id=m.id_campeonato 
where sp.is_ativo=true 
                and sp.id_pessoa=pessoa 
and ec.dt_inicio <= current_date and ec.dt_fim >= current_date 
                and func_inadimplente_clube(sp.id_pessoa, 2)=false limit 1 
     ); 
         end if; 
    end if; --- if (pessoa is null and depto=12 and liberado=false) then 

  
    if (jogador >0 and jogador in (select codsocio from soc_socios_vw where codsocio=pessoa)) then jogador=null; end if; 


----------------------------------------------------------- 
    
/* 

  

select * from soc_carteirinha where id_pessoa=28502 

  

academia 

select func_catraca(28502, 11, 1) 

  

clube 

select func_catraca(22, 12, 3) 
  

12 - Recepção Clube" 
  
11 - Recepção Academia 

16 - HIDROGINÁSTICA 

17 - SALÃO DE DANÇA 

18 - RECEPÇÃO EDUCAÇÃO INFANTIL 

  

  

*/ 
  
------------------------- bug Associação 

  

        if ('45.236.239/0001-01'=(select ds_documento from pes_pessoa where id=1) and cartao=107970) then 

           pessoa = -9; 

           liberado = true; 

       end if; 

-------------------- RECEPÇÃO EDUCAÇÃO INFANTIL 

  

   if (depto in (18) and tipo in (1,2) and liberado=false) then 

        pessoa = 

        ( 

  select a.id_pessoa from esc_autorizados as a 

      inner join matr_escola as m on m.id=a.id_matricula_escola 

  inner join fin_servico_pessoa as sp on sp.id=m.id_servico_pessoa 

  where a.id_pessoa=pessoa and sp.is_ativo=true limit 1 

        ); 

  

        if (pessoa is null) then 

           pessoa = -2; 

        end if; 

   end if; 

-------------------- ACADEMIA 

  

   if (depto in (11,16,17) and tipo in (1,2) and liberado=false) then 

  

-------------- Verifica se é Aluno 

            pessoa= 

          ( 

        

             select p.id from pes_pessoa as p 

             where p.id=pessoa and 

                 p.id in 

               ( 

                 select sp.id_pessoa from fin_servico_pessoa as sp 

                 inner join matr_academia as m on m.id_servico_pessoa=sp.id 

                 where sp.is_ativo=true and m.dt_inativo is null and sp.id_pessoa=pessoa and false in (select func_inadimplente(pessoa, 2)) 

              ) limit 1 

         ); 

 ------------- se não for aluno 

        if (pessoa is null) then 

            pessoa = -1; 

        end if; 

  
----------------- verificação do DEPARTAMENTO DA CATRACA 
  
        if (pessoa > 0) then 

              pessoa = 

          ( 

              select p.id from pes_pessoa as p 

              where p.id=pessoa and 

              ( 

                   p.id in 

                  ( 

                     select sp.id_pessoa from fin_servico_pessoa as sp 

                     inner join matr_academia as m on m.id_servico_pessoa=sp.id 

                     where sp.is_ativo=true and m.dt_inativo is null and sp.id_pessoa=pessoa and false in (select func_inadimplente(pessoa, 2)) 

                     and sp.id_servico in (select id_servico from soc_catraca_servico_depto where id_departamento=depto) 

                  ) 

             ) limit 1 

          ); 

          end if; 

  
-------------------- se não pertencer ao departamento         

          if (pessoa is null) then 

             pessoa = -2; 

          end if; 

----------------- verificar horários da matrícula 

        if (pessoa >0) then 

           pessoa = (select id_pessoa from aca_grade_vw where dia_hoje=id_semana and horario_intervalo=true and id_pessoa=pessoa and id_departamento=depto limit 1); 

        end if; 

-------------------- se não pertencer a grade de horários 
  

        if (pessoa is null ) then 

             pessoa = -5; 

        end if; 

  

    end if; 

 -------------------- CLUBE CARTÃO OU BIOMETRIA 

  

    if (depto=12 and tipo in (1,2) and liberado=false and jogador is null) then 

        pessoa= 

        ( 

           select codsocio from soc_socios_vw 

           where 

          codsocio=pessoa and func_inadimplente_clube(codsocio, 2)=false 

          limit 1 

        ); 

  
        ---- suspensão 

        if (pessoa > 0) then 

           if ((select count(*) from soc_suspencao where id_pessoa=pessoa and dt_inicial<=current_date and dt_final>=current_date )>0) then 

           pessoa = -4; 

            end if; 

        end if; 

  

-------------- Verifica se está liberado o dia da semana 

        if (pessoa > 0) then 

             if 

             ( 

                  select 

                 c.id 

                 from soc_socios_vw as s 

                 inner join soc_categoria as c on c.id=s.id_categoria 

                where s.codsocio= pessoa 

                and 

                 ( 

                   (EXTRACT( DOW FROM now())=0 and usa_clube_domingo=true) or 

                   (EXTRACT( DOW FROM now())=1 and usa_clube_segunda=true) or 

                   (EXTRACT( DOW FROM now())=2 and usa_clube_terca=true) or 

                   (EXTRACT( DOW FROM now())=3 and usa_clube_quarta=true) or 

                   (EXTRACT( DOW FROM now())=4 and usa_clube_quinta=true) or 

                   (EXTRACT( DOW FROM now())=5 and usa_clube_sexta=true) or 

                   (EXTRACT( DOW FROM now())=6 and usa_clube_sabado=true) 

                 ) 

  

              ) is null then pessoa = -5; 

                    

              end if; 

          end if; 

  

     end if; 

  

-------------------- CLUBE CONVITE 

  

    if (depto=12 and tipo=3 and liberado=false) then 

        ---- se existe a pessoa 

        pessoa=(select id_sis_pessoa from conv_movimento where id=cartao); 

  

        if (pessoa is null) then pessoa= -8; end if; 

  

        ---- se convite já utilizado 

       if (pessoa > 0) then 

           if ( 

                   ((select count(*) from conv_movimento as cm inner join conf_social as c on c.id >0 where cm.id=cartao and is_ativo=false and c.is_libera_convite_dia = false) > 0) or 

                   ((select count(*) from conv_movimento as cm inner join conf_social as c on c.id >0 where cm.id=cartao and is_ativo=false and c.is_libera_convite_dia = true and current_date <> dt_entrada) > 0) 

               ) then 

              pessoa=-6; 

            end if; 

        end if; 

  

         ---- se convite está vencido 

        if (pessoa > 0) then 

           if (select count(*) from conv_movimento where dt_validade >= current_date and id=cartao)=0 then 

              pessoa=-7; 

            end if; 

        end if; 

  

        ----- se convite pode ser utilizado hoje. 

  

        if (pessoa > 0) then 

--------------- 

           if 

           ( 

              select 

              is_ativo 

              from conv_movimento as m 
  
              inner join conv_servico as se on se.id=m.id_servicos 

              where   

              m.id=cartao 

              and 

              ( 

                (EXTRACT( DOW FROM now())=0 and is_domingo=true) or 

                (EXTRACT( DOW FROM now())=1 and is_segunda=true) or 

                (EXTRACT( DOW FROM now())=2 and is_terca=true) or 

                (EXTRACT( DOW FROM now())=3 and is_quarta=true) or 

                (EXTRACT( DOW FROM now())=4 and is_quinta=true) or 

                (EXTRACT( DOW FROM now())=5 and is_sexta=true) or 

                (EXTRACT( DOW FROM now())=6 and is_sabado=true) or 
( 
 	     ( 
select count(*) from hom_feriados where (dt_data=current_date and is_repete=false) or 
(extract(day from dt_data)=extract(day from current_date) and extract(month from dt_data)=extract(month from current_date) and is_repete=true) limit 1 
      ) > 0 and is_feriado=true   
      

                 ) 
             ) 
  
          ) is null then 

                 pessoa = -5; 

           end if; 

-------------------- 

        end if; 

    end if; 






    if (jogador>0) then pessoa=jogador; end if; 



    
---------------- verifica se o cartão já foi passado e bloqueia temporariamente ---------- 
/* 

** Ajuste em 14/09/2017 

Caso a pessoa esteja em seg_permissao_catraca "liberado=true", então NÃO SERÁ BLOQUEADA POR TEMPO 
*/ 



   if (liberado=false and pessoa > 0 and liberado=false) then 

        if ( func_libera_catraca_minutos(depto, pessoa)=false ) then 

              pessoa=-10; 

        end if; 

    end if; 

 ------------------------------------------------------------------------------------------     

    if (pessoa is null) then pessoa=-1; end if; 

  

    RETURN pessoa; 

END; 

$BODY$ 
  LANGUAGE plpgsql VOLATILE 
  COST 100; 


ALTER TABLE fin_cartao_rec ADD COLUMN ds_parcela character varying(5) DEFAULT ''; 