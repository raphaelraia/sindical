ALTER TABLE public.fin_plano5 ADD COLUMN nr_ordem integer DEFAULT NULL;

UPDATE fin_plano5 set nr_ordem = 2 WHERE id = 13;
UPDATE fin_plano5 set nr_ordem = 1 WHERE id = 14;
UPDATE fin_plano5 set nr_ordem = 4 WHERE id = 4;
UPDATE fin_plano5 set nr_ordem = 0 WHERE id = 8;
UPDATE fin_plano5 set nr_ordem = 5 WHERE id = 6;
UPDATE fin_plano5 set nr_ordem = 3 WHERE id = 7;
UPDATE fin_plano5 set nr_ordem = 6 WHERE id = 9;
UPDATE fin_plano5 set nr_ordem = 8 WHERE id = 5;
UPDATE fin_plano5 set nr_ordem = 7 WHERE id = 19;




-- JASPERS

/*
RECIBO_FINANCEIRO_FISICA_(E)
RECIBO_FINANCEIRO_FISICA_(S)
RECIBO_FINANCEIRO_JURIDICA_(E)
RECIBO_FINANCEIRO_JURIDICA_(S)
*/



ALTER TABLE public.fin_conta_cobranca
    ADD COLUMN nr_comercio_eletronico integer;