package br.com.rtools.digitalizacao.dao;

import br.com.rtools.digitalizacao.Documento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class DigitalizacaoDao extends DB {

    public List<Documento> listaDocumento() {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT d.* FROM dig_documento d ORDER BY d.dt_emissao, d.ds_titulo", Documento.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
    
    public List<Documento> listaDocumento(Integer id_pessoa) {
        Query qry = getEntityManager().createNativeQuery(
                "SELECT d.* FROM dig_documento d WHERE d.id_pessoa = "+id_pessoa+" ORDER BY d.dt_emissao, d.ds_titulo", Documento.class
        );

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
