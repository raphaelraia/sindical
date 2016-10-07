package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.JuridicaImportacao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class JuridicaImportacaoDao extends DB {

    public List find(String nome, String documento) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE TRIM(UPPER(FUNC_TRANSLATE(I.ds_nome))) = TRIM(UPPER(FUNC_TRANSLATE('" + nome + "'))) AND I.ds_documento = '" + documento + "'", JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Integer count() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT id FROM pes_juridica_importacao");
            return query.getResultList().size();
        } catch (Exception e) {
            return 0;
        }
    }
}
