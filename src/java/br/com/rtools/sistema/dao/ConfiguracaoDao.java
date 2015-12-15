package br.com.rtools.sistema.dao;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.Resolucao;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ConfiguracaoDao extends DB {

    public boolean existeIdentificador(Configuracao configuracao) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Configuracao AS C WHERE C.identifica = :identificador ");
            query.setParameter("identificador", configuracao.getIdentifica());
            if (!query.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean existeIdentificadorPessoa(Configuracao configuracao) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Configuracao AS C WHERE C.identifica = :identificador AND C.juridica.id = :idJuridica ");
            query.setParameter("identificador", configuracao.getIdentifica());
            query.setParameter("idJuridica", configuracao.getJuridica().getId());
            if (!query.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List listaConfiguracao(String descricaoPesquisa) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Configuracao AS C WHERE C.juridica.fantasia LIKE '%" + descricaoPesquisa + "%' OR C.identifica LIKE '%" + descricaoPesquisa + "%' OR C.juridica.pessoa.nome LIKE '%" + descricaoPesquisa + "%' ");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Resolucao pesquisaResolucaoUsuario(int id_usuario) {
        try {
            Query query = getEntityManager().createQuery(" SELECT r FROM Resolucao r WHERE r.usuario.id  = :pid");
            query.setParameter("pid", id_usuario);
            Resolucao result = (Resolucao) query.getSingleResult();

            return result;
        } catch (Exception e) {
            return new Resolucao();
        }
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findNotInByTabela('matr_escola');
     *
     * @param table (Use alias T+colum
     * @param colum_filter_key Nome da coluna do filtro
     * @return Todas as rotinas da tabela específicada
     * @param colum_filter_value Valor do filtro
     */
    public List findNotInByTabela(String table, String colum_filter_key, String colum_filter_value) {
        return findNotInByTabela(table, "id_configuracao", colum_filter_key, colum_filter_value, true);
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findNotInByTabela('seg_filial_rotina', 'id_filial', 1);
     *
     * @param table (Use alias T+colum)
     * @param column
     * @param colum_filter_key Nome da coluna do filtro
     * @return Todas as rotinas não usadas em uma chave conforme o valor
     * @param colum_filter_value Valor do filtro
     * @param is_ativo default null
     */
    public List findNotInByTabela(String table, String column, String colum_filter_key, String colum_filter_value, Boolean is_ativo) {
        if (column == null || column.isEmpty()) {
            column = "id_configuracao";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        return new FindDao().findNotInByTabela(Configuracao.class, "sis_configuracao", new String[]{"ds_identifica"}, table, column, colum_filter_key, colum_filter_value, "");
    }
}
