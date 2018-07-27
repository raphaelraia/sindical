ALTER TABLE public.fin_plano5 ADD COLUMN nr_ordem integer DEFAULT NULL;

ALTER TABLE public.fin_conta_cobranca
    ADD COLUMN nr_comercio_eletronico integer;