package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.ArquivoRetorno;
import java.util.ArrayList;
import java.util.List;

public class RetornoPadrao extends ArquivoRetorno {

    public RetornoPadrao(ContaCobranca contaCobranca) {
        super(contaCobranca);
    }

    @Override
    public List<ObjetoRetorno> sicob(boolean baixar, String host) {
//        Usuario usuario = new Usuario();
//        usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");
//
//        FacesContext context = FacesContext.getCurrentInstance();
//        String linha = null;
//        String cnpj = "";
//        String codigoCedente = "";
//        String nossoNumero = "";
//        String valorTaxa = "";
//        String valorPago = "";
//        String dataPagamento = "";
//        String dataVencimento = "";
//        String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/ArquivoRetorno/Padrao");
//        ServicosDao dbS = new ServicosDao();
//        ContaCobranca contaCobranca = new ContaCobranca();
//        ContaCobrancaDao dbC = new ContaCobrancaDao();
//        List<Movimento> movimento = new ArrayList();
//        MovimentoDao db = new MovimentoDao();
//        List<String> errors = new ArrayList();
//        boolean moverArquivo = true;
//        List<String> listaDtPagamentos = new ArrayList();
//        List<Double> listaTaxa = new ArrayList();
//        List<Double> listaValor = new ArrayList();
//        File fl = new File(caminho);
//        File listFile[] = fl.listFiles();
//        List<GenericaRetorno> listaRetorno = new ArrayList();
//        Dao dao = new Dao();
//        //Rotina rotina = (Rotina) dao.find(new Rotina(), 4);
//        if (listFile != null) {
//            int qntRetornos = listFile.length;
//            for (int u = 0; u < qntRetornos; u++) {
//                try {
//                    FileReader reader = new FileReader(caminho + "/" + listFile[u].getName());
//                    BufferedReader buffReader = new BufferedReader(reader);
//                    while ((linha = buffReader.readLine()) != null) {
//                        cnpj = linha.substring(0, 15).trim();
//                        codigoCedente = linha.substring(17, 30).trim();
//                        nossoNumero = linha.substring(30, 51).trim();
//                        valorTaxa = linha.substring(67, 83).trim();
//                        valorPago = linha.substring(51, 67).trim();
//                        dataPagamento = linha.substring(83, 92).trim();
//                        dataVencimento = "";
//
//                        if (!contaCobranca.getCodCedente().trim().equals(codigoCedente.trim())) {
//                            contaCobranca = dbC.pesquisaCobrancaCedente(codigoCedente);
//                        }
//                        movimento = db.pesquisaMovPorNumDocumentoList(nossoNumero, DataHoje.converte(DataHoje.colocarBarras(dataVencimento)), contaCobranca.getId());
//                        if (!movimento.isEmpty()) {
//                            if (movimento.size() > 1) {
//                                movimento.clear();
//                                listaDtPagamentos.clear();
//                                listaTaxa.clear();
//                                listaValor.clear();
//                                continue;
//                            }
//
//                            Movimento movi = movimento.get(0);
//
//                            movi.setValor(Moeda.substituiVirgulaDouble(Moeda.converteR$(valorPago)) / 100);
//                            movi.setTaxa(Moeda.substituiVirgulaDouble(Moeda.converteR$(valorTaxa)) / 100);
//
//                            GerarMovimento.salvarUmMovimento(null, movi);
//                        }
//                        movimento.clear();
//                        listaDtPagamentos.clear();
//                        listaTaxa.clear();
//                        listaValor.clear();
//                    }
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//            }
//            if (moverArquivo) {
//                for (int i = 0; i < listFile.length; i++) {
//                    try {
//                        if (!listFile[i].getName().toLowerCase().startsWith(DataHoje.ArrayDataHoje()[2] + "-" + DataHoje.ArrayDataHoje()[1])) {
//                            File fileDel = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/ArquivoRetorno/Padrao/ARQUIVOBAIXA.ret"));
//                            if (fileDel.exists()) {
//                                fileDel.delete();
//                            }
//                        }
//                    } catch (Exception e) {
//                        continue;
//                    }
//                }
//            }
//        }
        return new ArrayList();
    }

    @Override
    public List<ObjetoRetorno> sindical(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public List<ObjetoRetorno> sigCB(boolean baixar, String host) {
        return new ArrayList();
    }

    @Override
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCB(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSigCBSocial(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSicob(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSicobSocial(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaPadrao(Usuario usuario) {
        String mensagem = "";
        mensagem = super.baixarArquivoPadrao(this.sicob(true, ""), usuario);
        return mensagem;
    }
}
