package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.JuridicaImportacao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

public class JuridicaImportacaoDao extends DB {

    public List find(String nome, String documento) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE TRIM(UPPER(FUNC_TRANSLATE(I.ds_nome))) = TRIM(UPPER(FUNC_TRANSLATE('" + nome + "'))) AND I.ds_documento_original = '" + documento + "'", JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List find(String nome, String documento, Boolean is_contabilidade) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE TRIM(UPPER(FUNC_TRANSLATE(I.ds_nome))) = TRIM(UPPER(FUNC_TRANSLATE('" + nome + "'))) AND I.ds_documento_original = '" + documento + "' AND is_contabilidade = " + is_contabilidade, JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Integer countEmpresas() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT id FROM pes_juridica_importacao WHERE is_contabilidade = false");
            return query.getResultList().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public Integer countContabilidade() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT id FROM pes_juridica_importacao WHERE is_contabilidade = true");
            return query.getResultList().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<JuridicaImportacao> findEmpresasPorContabilidade(String contabilidade_codigo) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE I.nr_contabilidade_codigo = '" + contabilidade_codigo + "' AND I.is_contabilidade = false", JuridicaImportacao.class);

            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<JuridicaImportacao> findAllEmpresas() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE I.is_contabilidade = false", JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<JuridicaImportacao> findAll() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I", JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<JuridicaImportacao> findAllContabilidades() {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE I.is_contabilidade = true", JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<JuridicaImportacao> findByContabilidade(Integer contabilidade_id) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE I.id_contabilidade = " + contabilidade_id, JuridicaImportacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public JuridicaImportacao findByJuridica(Integer juridica_id) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT I.* FROM pes_juridica_importacao AS I WHERE I.id_juridica = " + juridica_id, JuridicaImportacao.class);
            return (JuridicaImportacao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
