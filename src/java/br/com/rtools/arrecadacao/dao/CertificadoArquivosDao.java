package br.com.rtools.arrecadacao.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CertificadoArquivosDao extends DB {

    public List findBy(Integer convencao_periodo_id, Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.convencaoPeriodo.id = :convencao_periodo_id  AND CA.pessoa.id = :pessoa_id ORDER BY CA.dtUpload DESC");
            query.setParameter("convencao_periodo_id", convencao_periodo_id);
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByDownloads(Integer convencao_periodo_id, Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.convencaoPeriodo.id = :convencao_periodo_id  AND CA.pessoa.id = :pessoa_id AND CA.dtDownload IS NOT NULL ORDER BY CA.dtDownload DESC");
            query.setParameter("convencao_periodo_id", convencao_periodo_id);
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.pessoa.id = :pessoa_id ORDER BY CA.dtUpload DESC, CA.convencaoPeriodo.referenciaFinal DESC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByConvencaoPeriodo(Integer convencao_periodo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.convencaoPeriodo.id = :convencao_periodo_id ORDER BY CA.dtUpload DESC");
            query.setParameter("convencao_periodo_id", convencao_periodo_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByPessoaDownloads(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.pessoa.id = :pessoa_id AND CA.dtDownload IS NOT NULL ORDER BY CA.dtUpload DESC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByConvencaoPeriodoDownloads(Integer convencao_periodo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CA FROM CertificadoArquivos AS CA WHERE CA.convencaoPeriodo.id = :convencao_periodo_id AND CA.dtDownload IS NOT NULL ORDER BY CA.dtUpload DESC");
            query.setParameter("convencao_periodo_id", convencao_periodo_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
