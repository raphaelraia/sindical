package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.Profissao;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.SelectTranslate;
import java.util.List;
import javax.persistence.Query;

public class ProfissaoDao extends DB {

    public List<String> pesquisaProfissao(String des_tipo) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery("select prof.profissao from Profissao prof where prof.profissao like :texto");
            qry.setParameter("texto", des_tipo);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Profissao idProfissao(Profissao des_prof) {
        Profissao result = null;
        try {
            Query qry = getEntityManager().createQuery("select prof from Profissao prof where prof.profissao = :d_prof");
            qry.setParameter("d_prof", des_prof.getProfissao());
            result = (Profissao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaProfParametros(String por, String combo, String desc) {
        SelectTranslate st = new SelectTranslate();
        desc = (por.equals("I") ? desc + "%" : "%" + desc + "%");
        return st.select(new Profissao()).where("profissao", desc).find();
    }
}
