package br.com.rtools.pessoa.dao;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.pessoa.DocumentoInvalido;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class DocumentoInvalidoDao extends DB {

    public List<DocumentoInvalido> pesquisaTodos() {
        // AND LENGHT(di.ds_documento_invalido) = 14
        // COM A NOVA VERSÃO DO ARQUIVO RETORNO DA SINDICAL O CNPJ DA EMPRESA VEM COMPLETO, A ROTINA NÃO TRATA MAIS OS CNPJS ANTIGOS
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT di.* \n "
                    + " FROM pes_documento_invalido di \n "
                    + "WHERE di.checado = false \n "
                    + "  AND LENGTH(di.ds_documento_invalido) = 14", DocumentoInvalido.class
            );
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<DocumentoInvalido> pesquisaNumeroBoleto(String numero) {
        List<DocumentoInvalido> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select docInv "
                    + "  from DocumentoInvalido docInv "
                    + " where docInv.documentoInvalido like '%" + numero + "%'");
            result = qry.getResultList();
            return result;
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<DocumentoInvalido> pesquisaNumeroBoletoPessoa() {
        List<DocumentoInvalido> result = new ArrayList();
        String textQuery
                = "SELECT di.* "
                + "  FROM pes_documento_invalido di "
                + " WHERE (di.ds_documento_invalido "
                + "	IN ( "
                + "            SELECT REPLACE(REPLACE(REPLACE(p.ds_documento,'/',''),'-',''),'.','') "
                + "              FROM pes_pessoa p ORDER BY p.ds_documento "
                + "        ) "
                + "        OR di.ds_documento_invalido IN ( "
                + "			SELECT REPLACE(REPLACE(REPLACE(p.ds_documento,'/',''),'-',''),'.','') "
                + "                               FROM pes_pessoa p ORDER BY p.ds_documento "
                + "        ) "
                + " ) "
                + " AND di.checado = false AND LENGTH(di.ds_documento_invalido) = 14";
//            textQuery = "select doc.id as idi, doc.ds_documento_invalido,pes.id "
//                    + "  from pes_documento_invalido doc, pes_pessoa pes "
//                    + " where '000'||substring(replace(replace(replace(pes.ds_documento,'/',''),'-',''),'.',''),1,12) = doc.ds_documento_invalido";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery, DocumentoInvalido.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }
    

    public ContaCobranca pesquisaContaCobrancaSindical() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT cc.* \n "
                    + " FROM fin_conta_cobranca cc \n "
                    + "WHERE cc.id_layout = 2", ContaCobranca.class
            );
            return (ContaCobranca) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }    
}
