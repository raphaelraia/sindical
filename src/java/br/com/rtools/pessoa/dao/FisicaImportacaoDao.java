package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.FisicaImportacao;
import br.com.rtools.principal.DB;
import javax.persistence.*;

public class FisicaImportacaoDao extends DB {

    public FisicaImportacao find(String nome, String documento) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_fisica_importacao AS I WHERE TRIM(UPPER(FUNC_TRANSLATE(I.ds_nome))) = TRIM(UPPER(FUNC_TRANSLATE('" + nome + "'))) AND I.ds_documento = '" + documento + "' LIMIT 1", FisicaImportacao.class);
            return (FisicaImportacao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer count() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT id FROM pes_fisica_importacao");
            return query.getResultList().size();
        } catch (Exception e) {
            return 0;
        }
    }
}
