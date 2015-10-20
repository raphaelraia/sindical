package br.com.rtools.financeiro.db;

import java.util.List;

public interface MovimentosReceberDB {

    public List pesquisaListaMovimentosDesconto(int id_juridica, float desconto, float total);

    public List pesquisaListaMovimentos(Integer id_juridica);
}
