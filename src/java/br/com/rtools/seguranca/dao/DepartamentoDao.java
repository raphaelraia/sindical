package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDao extends DB {

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
        return findNotInByTabela(table, "id_departamento", colum_filter_key, colum_filter_value, true);
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
            column = "id_rotina";
        }
        if (colum_filter_key == null || colum_filter_key.isEmpty() || colum_filter_value == null || colum_filter_value.isEmpty()) {
            return new ArrayList();
        }
        String where = "";
        return new FindDao().findNotInByTabela(Departamento.class, "seg_departamento", new String[]{"ds_descricao"}, table, column, colum_filter_key, colum_filter_value, where);
    }
}
