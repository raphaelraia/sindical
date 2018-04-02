

-- RODAR EM RTOOLS E COMERCIO LIMEIRA ------------------------------------------
--------------------------------------------------------------------------------

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


CREATE TABLE public.arr_certidao_disponivel_mensagem
(
    id serial NOT NULL,
    ds_observacao character varying(8000) COLLATE pg_catalog."default",
    id_convencao_periodo integer NOT NULL,
    id_certidao_disponivel integer NOT NULL,
    CONSTRAINT arr_certidao_disponivel_mensagem_pkey PRIMARY KEY (id),
    CONSTRAINT fk_arr_certidao_disponivel_mensagem_id_certidao_disponivel FOREIGN KEY (id_certidao_disponivel)
        REFERENCES public.arr_certidao_disponivel (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_arr_certidao_disponivel_mensagem_id_convencao_periodo FOREIGN KEY (id_convencao_periodo)
        REFERENCES public.arr_convencao_periodo (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.arr_certidao_disponivel_mensagem
    OWNER to postgres;