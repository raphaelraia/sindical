package br.com.rtools.estoque.dao;

import br.com.rtools.estoque.Estoque;
import br.com.rtools.estoque.EstoqueSaidaConsumo;
import br.com.rtools.estoque.Produto;
import br.com.rtools.financeiro.IndiceMoeda;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ProdutoDao extends DB {

    public List pesquisaProduto(String descricaoPesquisa, Integer tipoPesquisa, String porPesquisa, Integer grupo_id, Integer subgrupo_id) {
        try {
            String por;
            if (porPesquisa.equals("Inicial")) {
                por = "";
            } else {
                por = "%";
            }
            List listWhere = new ArrayList<>();
            String queryString = " SELECT P.* FROM est_produto AS P \n";

            if (!descricaoPesquisa.isEmpty()) {
                String descricao = por + descricaoPesquisa + "%";
                listWhere.add(" (trim(UPPER(func_translate(P.ds_descricao))) LIKE trim(UPPER(func_translate('" + descricao + "')))               \n"
                        + "   OR (                                                                                                              \n"
                        + "     trim(UPPER(func_translate(P.ds_modelo))) LIKE trim(UPPER(func_translate('" + descricaoPesquisa + "')))          \n"
                        + "     OR trim(UPPER(func_translate(P.ds_marca))) LIKE trim(UPPER(func_translate('" + descricaoPesquisa + "')))        \n"
                        + "     OR trim(UPPER(func_translate(P.ds_fabricante))) LIKE trim(UPPER(func_translate('" + descricaoPesquisa + "')))   \n"
                        + ") )");
            }
            if (grupo_id != null && subgrupo_id == null) {
                listWhere.add("P.id_grupo = " + grupo_id + " ");
            }
            if (subgrupo_id != null) {
                listWhere.add("P.id_subgrupo = " + subgrupo_id + " ");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += " ORDER BY P.ds_descricao \n";
            if (listWhere.isEmpty()) {
                queryString += " LIMIT 250 \n";

            }
            Query query;
            query = getEntityManager().createNativeQuery(queryString, Produto.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public boolean existeProdutoGrupo(String p) {
        try {
            Query q = getEntityManager().createQuery("SELECT P FROM ProdutoGrupo AS P WHERE UPPER(P.descricao) LIKE :p");
            q.setParameter("p", p.toUpperCase());
            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean existeProdutoSubGrupo(String p) {
        try {
            Query q = getEntityManager().createQuery("SELECT P FROM ProdutoSubGrupo AS P WHERE UPPER(P.descricao) LIKE :p");
            q.setParameter("p", p.toUpperCase());
            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean existeProdutoUnidade(String p) {
        try {
            Query q = getEntityManager().createQuery("SELECT P FROM ProdutoUnidade AS P WHERE UPPER(P.descricao) LIKE :p");
            q.setParameter("p", p.toUpperCase());
            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean existeCor(String p) {
        try {
            Query q = getEntityManager().createQuery("SELECT C FROM Cor AS C WHERE UPPER(C.descricao) LIKE :p");
            q.setParameter("p", p.toUpperCase());
            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean existeProdutoEstoqueFilialTipo(Estoque es) {
        try {
            Query q = getEntityManager().createQuery("SELECT EST FROM Estoque AS EST WHERE EST.produto.id = :p1 AND EST.filial.id = :p2 AND EST.estoqueTipo.id = :p3");
            q.setParameter("p1", es.getProduto().getId());
            q.setParameter("p2", es.getFilial().getId());
            q.setParameter("p3", es.getEstoqueTipo().getId());
            if (!q.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public List listaEstoquePorProduto(Produto p) {
        try {
            Query q = getEntityManager().createQuery("SELECT E FROM Estoque AS E WHERE E.produto.id = :p1 ORDER BY E.filial.filial.pessoa.nome ASC, E.estoqueTipo.descricao ASC");
            q.setParameter("p1", p.getId());
            List list = q.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

//    public Estoque listaEstoquePorProdutoFilial(Produto p, Filial l) {
//        try {
//            Query q = getEntityManager().createQuery("SELECT E FROM Estoque AS E WHERE E.produto.id = :p1 AND E.filial.id = :p2 ORDER BY E.filial.filial.pessoa.nome");
//            q.setParameter("p1", p.getId());
//            q.setParameter("p2", l.getId());
//            List list = q.getResultList();
//            if (!list.isEmpty()) {
//                return (Estoque) q.getSingleResult();
//            }
//        } catch (Exception e) {
//        }
//        return null;
//    }
//
//    public Estoque listaEstoquePorProdutoFilial(Integer produto_id, Integer filial_id, Integer estoque_tipo_id) {
//        try {
//            Query q = getEntityManager().createQuery("SELECT E FROM Estoque AS E WHERE E.produto.id = :produto_id AND E.filial.id = :filial_id AND E.estoqueTipo.id = :estoque_tipo_id ");
//            q.setParameter("produto_id", produto_id);
//            q.setParameter("filial_id", filial_id);
//            q.setParameter("estoque_tipo_id", estoque_tipo_id);
//            List list = q.getResultList();
//            if (!list.isEmpty()) {
//                return (Estoque) q.getSingleResult();
//            }
//        } catch (Exception e) {
//        }
//        return null;
//    }

    public List<EstoqueSaidaConsumo> listaEstoqueSaidaConsumoProdutoTipo(Integer produto, Integer estoqueTipo, String orderLancamento, String orderDepartamento, String orderProduto, String orderQuantidade, String orderFilial) {
        if (produto == -1) {
            return new ArrayList();
        }
        try {
            Query q = getEntityManager().createQuery("SELECT ESC FROM EstoqueSaidaConsumo AS ESC WHERE ESC.produto.id = :p1 AND ESC.estoqueTipo.id = :p2 ORDER BY ESC.dtLancamento " + orderLancamento + ", ESC.departamento.descricao " + orderDepartamento + ", ESC.produto.descricao " + orderProduto + ", ESC.filialSaida.filial.pessoa.nome " + orderFilial + "");
            q.setParameter("p1", produto);
            q.setParameter("p2", estoqueTipo);
            List list = q.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<IndiceMoeda> listaIndiceMoeda() {
        try {
            Query q = getEntityManager().createNativeQuery(
                    " SELECT im.* \n "
                    + " FROM fin_indice_moeda im \n "
                    + "ORDER BY im.id", IndiceMoeda.class
            );

            return q.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }

        return new ArrayList();
    }
}
