package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisPessoaDao extends DB {

    public List pesquisarSisPessoa(String desc, String por, String como) {
        if (por.equals("cnpj") || por.equals("cpf")) {
            por = "documento";
        }
        List lista;
        String textQuery = null;
        if (como.equals("T")) {
            textQuery = "";
        } else if (como.equals("P")) {
            desc = "%" + desc.toLowerCase().toUpperCase() + "%";
            textQuery = "select objeto from SisPessoa objeto where UPPER(objeto." + por + ") like :desc"
                    + " order by objeto.nome";
        } else if (como.equals("I")) {
            desc = desc.toLowerCase().toUpperCase() + "%";
            textQuery = "select objeto from SisPessoa objeto where UPPER(objeto." + por + ") like :desc"
                    + " order by objeto.nome";
        }
        try {
            Query qry = getEntityManager().createQuery(textQuery);
            if ((desc != null) && (!(como.equals("T")))) {
                qry.setParameter("desc", desc);
            }
            lista = qry.getResultList();
        } catch (Exception e) {
            lista = new ArrayList();
        }
        return lista;
    }

    public SisPessoa sisPessoaExiste(SisPessoa sp) {
        return sisPessoaExiste(sp, false);
    }

    public SisPessoa sisPessoaExiste(SisPessoa sp, boolean porDocumento) {
        Query qry = null;
        Boolean execute = false;
        if (porDocumento) {
            if (!sp.getDocumento().isEmpty()) {
                qry = getEntityManager().createNativeQuery(
                        "SELECT sp.* "
                        + "  FROM sis_pessoa sp "
                        + " WHERE sp.ds_documento LIKE '" + sp.getDocumento() + "'", SisPessoa.class
                );
                execute = true;
            } else if (!sp.getRg().isEmpty()) {
                qry = getEntityManager().createNativeQuery(
                        "SELECT sp.* "
                        + "  FROM sis_pessoa sp "
                        + " WHERE REPLACE(REPLACE(sp.ds_rg, '-', ''), '.', '') LIKE '" + sp.getRg() + "'", SisPessoa.class
                );

                execute = true;
            }
        } else {
            if (!sp.getNome().isEmpty() && !sp.getNascimento().isEmpty()) {
                String nome = AnaliseString.normalizeLower(sp.getNome());
                qry = getEntityManager().createNativeQuery(
                        "SELECT sp.* "
                        + "  FROM sis_pessoa sp "
                        + " WHERE LOWER(FUNC_TRANSLATE(sp.ds_nome)) LIKE '" + nome + "' \n "
                        + "   AND sp.dt_nascimento = '" + DataHoje.converteData(sp.getDtNascimento()) + "'", SisPessoa.class
                );

                execute = true;
            }
        }

        if (execute) {
            try {
                qry.setMaxResults(1);
                if (!qry.getResultList().isEmpty()) {
                    return (SisPessoa) qry.getSingleResult();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
//        Query qry;
//        List list;
//        try {
//            if (porDocumento) {
//                if (!sp.getDocumento().isEmpty()) {
//                    qry = getEntityManager().createQuery("SELECT SP FROM SisPessoa AS SP WHERE SP.documento = :documento");
//                    qry.setParameter("documento", sp.getDocumento());
//                    list = qry.getResultList();
//                    if (!list.isEmpty()) {
//                        return (SisPessoa) qry.getSingleResult();
//                    }
//                }else if (!sp.getRg().isEmpty()) {
//                    qry = getEntityManager().createQuery("SELECT SP FROM SisPessoa AS SP WHERE SP.rg = :documento");
//                    qry.setParameter("documento", sp.getRg());
//                    list = qry.getResultList();
//                    if (!list.isEmpty()) {
//                        return (SisPessoa) qry.getSingleResult();
//                    }
//                }
//            } else {
//                if (!sp.getNome().isEmpty() && !sp.getNascimento().isEmpty()) {
//                    String queryString = "SELECT SP FROM SisPessoa AS SP WHERE SP.nome = :nome AND SP.dtNascimento = :nascimento";
//                    //'"+sp.getNascimento()+"'"
//                    qry = getEntityManager().createQuery(queryString);
//                    qry.setParameter("nome", sp.getNome());
//                    qry.setParameter("nascimento", sp.getDtNascimento(), TemporalType.DATE);
//                    list = qry.getResultList();
//                    if (!list.isEmpty()) {
//                        return (SisPessoa) qry.getSingleResult();
//                    }
//                }
//            }
//        } catch (Exception e) {
//            return null;
//        }
        return null;
    }
}
