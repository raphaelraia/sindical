package br.com.rtools.arrecadacao.beans;

import br.com.rtools.associativo.LoteBoleto;
import br.com.rtools.associativo.beans.ImpressaoBoletoSocialBean;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class GerarBoletoBean {

    private Pessoa pessoa = new Pessoa();
    private List<Pessoa> listaPessoa = new ArrayList();
    private List<DataObject> listaGerados = new ArrayList();
    private List<DataObject> listaGeradosSelecionado = new ArrayList();
    private boolean imprimeVerso = true;

    private String ano;
    private String mes;
    private List listaData = new ArrayList();

    private List<Vector> listaServicoSemCobranca = new ArrayList();
    private List<Vector> listaPessoaSemComplemento = new ArrayList();

    private List<Vector> listaPessoaFisicaSemEndereco = new ArrayList();
    private List<Vector> listaPessoaJuridicaSemEndereco = new ArrayList();

    public GerarBoletoBean() {
        DataHoje dh = new DataHoje();

        ano = DataHoje.DataToArrayString(DataHoje.data())[2];
        mes = DataHoje.DataToArrayString(dh.incrementarMeses(1, DataHoje.data()))[1];

        getListaServicoSemCobranca();
        getListaPessoaSemComplemento();

        new FunctionsDao().incluiPessoaComplemento();
//        NAO USA --- EXCLUIR DEPOIS DE 01/04/2015
//        getListaPessoaFisicaSemEndereco();
//        getListaPessoaJuridicaSemEndereco();
    }

//        NAO USA --- EXCLUIR DEPOIS DE 01/04/2015
//    public void atualizarListaNotificacao(){
//        listaPessoaFisicaSemEndereco.clear();
//        getListaPessoaFisicaSemEndereco();
//        listaPessoaJuridicaSemEndereco.clear();
//        getListaPessoaJuridicaSemEndereco();
//    }
    public String imprimirLote(LoteBoleto lb) {
        ChamadaPaginaBean cp = (ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean");
        String pagina = cp.impressaoBoletoSocial();

        //GenericaSessao.put("linkClicado", true);
        ImpressaoBoletoSocialBean ibs = new ImpressaoBoletoSocialBean();
        
        ibs.init();
        
        ibs.setStrData(lb.getProcessamento());
        ibs.setStrLote("" + lb.getId());
        ibs.loadLista();
        GenericaSessao.put("impressaoBoletoSocialBean", ibs);

        return pagina;
    }

    public void gerarTodos() {
        if (!listaServicoSemCobranca.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não é possível gerar mensalidade, verifique os Serviços e Conta Cobrança!");
            listaServicoSemCobranca.clear();
            getListaServicoSemCobranca();
            return;
        }

        if (!listaPessoaSemComplemento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não é possível gerar mensalidade, verifique as Pessoa Complemento!");
            listaPessoaSemComplemento.clear();
            getListaPessoaSemComplemento();
            return;
        }
//        NAO USA --- EXCLUIR DEPOIS DE 01/04/2015
//        if (!listaPessoaFisicaSemEndereco.isEmpty()){
//            GenericaMensagem.warn("Atenção", "Não é possível gerar mensalidade, verifique as Pessoa Física sem Endereço!");
//            listaPessoaFisicaSemEndereco.clear();
//            getListaPessoaFisicaSemEndereco();
//            return;
//        }
//        
//        if (!listaPessoaJuridicaSemEndereco.isEmpty()){
//            GenericaMensagem.warn("Atenção", "Não é possível gerar mensalidade, verifique as Pessoa Jurídica sem Endereço!");
//            listaPessoaJuridicaSemEndereco.clear();
//            getListaPessoaJuridicaSemEndereco();
//            return;
//        }
//        
        Dao dao = new Dao();
        dao.openTransaction();
        NovoLog novoLog = new NovoLog();
        novoLog.saveList();
        if (listaData.isEmpty()) {
            if (dao.executeQueryObject("select func_geramensalidades(null, '" + mes + "/" + ano + "')")) {
                dao.commit();
                listaGerados.clear();
                GenericaMensagem.info("Sucesso", "Geração de Mensalidades concluída!");
                novoLog.save("Referência: " + mes + "/" + ano);
            } else {
                dao.rollback();
                novoLog.cancelList();
                GenericaMensagem.warn("Erro", "Erro ao gerar Mensalidades!");
            }
        } else {
            for (Object listaDatax : listaData) {
                String vencto = listaDatax.toString().substring(0, 2) + "/" + listaDatax.toString().substring(3, 7);
                if (dao.executeQueryObject("select func_geramensalidades(null, '" + vencto + "')")) {
                    listaGerados.clear();
                    GenericaMensagem.info("Sucesso", "Geração de Mensalidades " + vencto + " concluída!");
                    novoLog.save("Referência: " + vencto);
                } else {
                    novoLog.cancelList();
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Erro ao gerar Mensalidades!");
                    return;
                }
            }

            dao.commit();
        }
        novoLog.saveList();
    }

    public void gerarLista() {
        if (!listaServicoSemCobranca.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não é possível gerar mensalidade, verifique os Serviços e Conta Cobrança!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();
        boolean erro = false;

        NovoLog novoLog = new NovoLog();
        novoLog.saveList();
        for (Pessoa pe : listaPessoa) {
            if (listaData.isEmpty()) {
                if (dao.executeQueryObject("select func_geramensalidades(" + pe.getId() + ", '" + mes + "/" + ano + "')")) {
                    GenericaMensagem.info("Sucesso", "Geração de Mensalidades concluída!");
                    novoLog.save("Referência: " + mes + "/" + ano + " - Pessoa: (" + pe.getId() + ") " + pe.getNome());
                } else {
                    erro = true;
                    GenericaMensagem.warn("Erro", "Erro ao gerar Mensalidades!");
                }
            } else {
                for (Object listaDatax : listaData) {
                    String vencto = listaDatax.toString().substring(0, 2) + "/" + listaDatax.toString().substring(3, 7);
                    if (dao.executeQueryObject("select func_geramensalidades(" + pe.getId() + ", '" + vencto + "')")) {
                        GenericaMensagem.info("Sucesso", "Geração de Mensalidades " + vencto + " concluída!");
                        novoLog.save("Referência: " + vencto + " - Pessoa: (" + pe.getId() + ") " + pe.getNome());
                    } else {
                        erro = true;
                        GenericaMensagem.warn("Erro", "Erro ao gerar Mensalidades!");
                    }
                }
            }
        }

        if (erro) {
            dao.rollback();
            novoLog.cancelList();
        } else {
            novoLog.saveList();
            dao.commit();
            listaGerados.clear();
        }
    }

    public void adicionarPessoa() {
        listaPessoa.add(pessoa);
        pessoa = new Pessoa();
    }

    public void removerPessoaLista(int index) {
        listaPessoa.remove(index);
    }

    public void adicionarData() {
        if (!listaData.isEmpty()) {
            boolean existe = false;
            for (int i = 0; i < listaData.size(); i++) {
                if (listaData.get(i).toString().equals(mes + "/" + ano)) {
                    existe = true;
                }
            }
            if (!existe) {
                listaData.add(mes + "/" + ano);
            }

        } else {
            listaData.add(mes + "/" + ano);
        }
    }

    public void adicionarTodasData() {
        if (!listaData.isEmpty()) {
            boolean existe = false;
            for (int w = 1; w <= 12; w++) {
                String mesx = (w < 10) ? "0" + w : "" + w;
                for (int i = 0; i < listaData.size(); i++) {
                    if (listaData.get(i).toString().equals(mesx + "/" + ano)) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    listaData.add(mesx + "/" + ano);
                }
                existe = false;
            }
        } else {
            for (int w = 1; w <= 12; w++) {
                String mesx = (w < 10) ? "0" + w : "" + w;
                listaData.add(mesx + "/" + ano);
            }
        }
    }

    public void removerDataLista(int index) {
        listaData.remove(index);
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.getObject("pessoaPesquisa") != null) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            GenericaSessao.remove("pessoaPesquisa");
            adicionarPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public List<DataObject> getListaGerados() {
        if (listaGerados.isEmpty()) {
            FinanceiroDao dao = new FinanceiroDao();
            List<LoteBoleto> lista = dao.listaLoteBoleto();
            for (LoteBoleto lb : lista) {
                listaGerados.add(new DataObject(lb, null));
            }
        }
        return listaGerados;
    }

    public void setListaGerados(List<DataObject> listaGerados) {
        this.listaGerados = listaGerados;
    }

    public List<DataObject> getListaGeradosSelecionado() {
        return listaGeradosSelecionado;
    }

    public void setListaGeradosSelecionado(List<DataObject> listaGeradosSelecionado) {
        this.listaGeradosSelecionado = listaGeradosSelecionado;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public String getAno() {
        if (ano.length() != 4) {
            ano = DataHoje.DataToArrayString(DataHoje.data())[2];
        }
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public List getListaData() {
        return listaData;
    }

    public void setListaData(List listaData) {
        this.listaData = listaData;
    }

    public List<Vector> getListaServicoSemCobranca() {
        if (listaServicoSemCobranca.isEmpty()) {
            FinanceiroDao dao = new FinanceiroDao();

            listaServicoSemCobranca = dao.listaServicosSemCobranca();

            if (!listaServicoSemCobranca.isEmpty()) {
                GenericaMensagem.fatal("Atenção", "Não é possível gerar mensalidades sem antes definir Conta Cobrança para os seguintes Serviços:");
                for (Vector linha : listaServicoSemCobranca) {
                    GenericaMensagem.info("Serviço / Tipo: ", linha.get(1).toString() + " - " + linha.get(3).toString());
                }
            }
        }
        return listaServicoSemCobranca;
    }

    public void setListaServicoSemCobranca(List<Vector> listaServicoSemCobranca) {
        this.listaServicoSemCobranca = listaServicoSemCobranca;
    }

    public List<Vector> getListaPessoaSemComplemento() {
        if (listaPessoaSemComplemento.isEmpty()) {
            FinanceiroDao dao = new FinanceiroDao();

            if (listaData.isEmpty()) {
                listaPessoaSemComplemento = dao.listaPessoaSemComplemento(mes + "/" + ano);
            } else {
                for (Object data : listaData) {
                    listaPessoaSemComplemento.addAll(dao.listaPessoaSemComplemento(data.toString()));
                }
            }

            if (!listaPessoaSemComplemento.isEmpty()) {
                GenericaMensagem.fatal("Atenção", "Pessoas não contém dia de vencimento: ");
                for (Vector linha : listaPessoaSemComplemento) {
                    GenericaMensagem.info("ID / Nome: ", linha.get(0).toString() + " - " + linha.get(1).toString());
                }
            }
        }
        return listaPessoaSemComplemento;
    }

    public void setListaPessoaSemComplemento(List<Vector> listaPessoaSemComplemento) {
        this.listaPessoaSemComplemento = listaPessoaSemComplemento;
    }
}
