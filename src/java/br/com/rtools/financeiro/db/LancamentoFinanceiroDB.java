package br.com.rtools.financeiro.db;

import br.com.rtools.financeiro.CentroCusto;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.TipoDocumento;
import java.util.List;

public interface LancamentoFinanceiroDB {

    public List<TipoDocumento> listaTipoDocumento();

    public Juridica pesquisaJuridica(String documento);

    public Fisica pesquisaFisica(String documento);


    public List<CentroCusto> listaCentroCusto(int id_filial);

    public List<Plano5> listaComboPagamentoBaixa();

    public ChequePag pesquisaChequeConta(String numero, int id_plano);
}
