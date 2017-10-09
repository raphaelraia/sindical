package br.com.rtools.arrecadacao.beans;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoContaCobranca;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class BaixaBoletoBean {

    private int idServicos = 0;
    private String numBoleto = "";
    private String caixaBanco = "banco";
    private String msgConfirma = "";
    private List<DataObject> listBoletos = new ArrayList();
    //private boolean carregarGrid = false;
    private boolean marcarTodos = false;
    private Pessoa pessoa = new Pessoa();
    private boolean novoNumero = false;
    private boolean habPessoa = false;
    private boolean disBtnBaixar = true;

    public String refreshFormCaixa() {
        return "baixaBoleto";
    }

    public void loadListaBoleto() {
        DataObject dt = null;
        MovimentoDao db = new MovimentoDao();
        String pesquisado;

        getListBoletos().clear();

        List<Movimento> listaQuery;
        if (caixaBanco.equals("banco")) {
            listaQuery = db.movimentosAberto(getPessoa().getId(), true);
        } else {
            listaQuery = db.movimentosAberto(getPessoa().getId(), false);
        }

        for (int i = 0; i < listaQuery.size(); i++) {
            if (listaQuery.get(i).getDocumento().equals(numBoleto)) {
                pesquisado = "font-weight: bold";
            } else {
                pesquisado = "";
            }

            dt = new DataObject(pesquisado,
                    listaQuery.get(i),
                    Moeda.converteR$(Double.toString(listaQuery.get(i).getValor())), // valor
                    Moeda.converteR$(Double.toString(listaQuery.get(i).getMulta())), // multa
                    Moeda.converteR$(Double.toString(listaQuery.get(i).getJuros())), // juros
                    Moeda.converteR$(Double.toString(listaQuery.get(i).getCorrecao())), // correcao
                    Moeda.converteR$(Double.toString(listaQuery.get(i).getDesconto())), // desconto
                    Moeda.converteR$(somarValorRecebido(Double.toString(listaQuery.get(i).getValor()),
                            Double.toString(listaQuery.get(i).getMulta()),
                            Double.toString(listaQuery.get(i).getJuros()),
                            Double.toString(listaQuery.get(i).getCorrecao()),
                            Double.toString(listaQuery.get(i).getDesconto()))), // valor pago
                    false,
                    null);
            getListBoletos().add(dt);
            converteDescontoFora(i, (String) ((DataObject) getListBoletos().get(i)).getArgumento7());
        }
    }

    public List<SelectItem> getListaServicoCobranca() {
        List<SelectItem> servicoCobranca = new ArrayList();
        int i = 0;
        ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
        List<ServicoContaCobranca> select = servDB.pesquisaTodosFiltrado();
        if (select == null) {
            select = new ArrayList();
        }
        while (i < select.size()) {
            if (select.get(i).getServicos().getId() == 1) {
                servicoCobranca.add(
                        new SelectItem(
                                i,
                                select.get(i).getServicos().getDescricao() + " - "
                                + select.get(i).getContaCobranca().getSicasSindical(),//SICAS NO CASO DE SINDICAL
                                Integer.toString(select.get(i).getId())
                        )
                );
            } else {
                servicoCobranca.add(
                        new SelectItem(
                                i,
                                select.get(i).getServicos().getDescricao() + " - "
                                + select.get(i).getContaCobranca().getCodCedente(),//CODCEDENTE NO CASO DE OUTRAS
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
            i++;
        }
        return servicoCobranca;
    }
//
//    public String pesquisarBoleto() {
//        carregarGrid = true;
//        listBoletos.clear();
//        return "baixaBoleto";
//    }

    public synchronized String baixarBoletos(DataObject dob) {
        Movimento mov;
        List<Movimento> lista = new ArrayList();

        if (dob == null) {
            Integer id_conta_banco = null;
            MovimentoDao db = new MovimentoDao();
            for (DataObject listBoleto : getListBoletos()) {
                if ((Boolean) listBoleto.getArgumento8() == true) {
                    mov = (Movimento) listBoleto.getArgumento1();
                    mov.setValor(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento2())));
                    mov.setValorBaixa(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento7())));
                    mov.setMulta(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento3())));
                    mov.setJuros(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento4())));
                    mov.setCorrecao(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento5())));
                    mov.setDesconto(Double.parseDouble(Moeda.substituiVirgula((String) listBoleto.getArgumento6())));

                    if (mov.getValorBaixa() <= 0) {
                        GenericaMensagem.warn("Atençao", "Nenhum valor não pode estar zerado!");
                        return null;
                    }

                    if (caixaBanco.equals("banco")) {
                        Boleto b = db.pesquisaBoletos(mov.getNrCtrBoleto());
                        if (b == null) {
                            GenericaMensagem.warn("Erro", "BOLETO NÃO ENCONTRADO!");
                            return null;
                        }
                        if (id_conta_banco == null) {
                            id_conta_banco = b.getContaCobranca().getContaBanco().getId();
                        } else if (id_conta_banco != b.getContaCobranca().getContaBanco().getId()) {
                            GenericaMensagem.error("Atençao", "Boletos de Contas diferentes não podem ser Baixados!");
                            return null;
                        }
                    }
                    lista.add(mov);
                }
            }
        } else {
            mov = (Movimento) dob.getArgumento1();
            mov.setValor(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento2().toString())));
            mov.setValorBaixa(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento7().toString())));

            mov.setMulta(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento3().toString())));
            mov.setJuros(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento4().toString())));
            mov.setCorrecao(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento5().toString())));
            mov.setDesconto(Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento6().toString())));

            if (mov.getValorBaixa() <= 0) {
                GenericaMensagem.warn("Atençao", "Nenhum valor não pode estar zerado!");
                return null;
            }
            lista.add(mov);
        }

        if (!lista.isEmpty()) {
            GenericaSessao.put("listaMovimento", lista);
        } else {
            GenericaMensagem.error("Atençao", "Nenhum boleto foi selecionado!");
            return null;
        }

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("caixa_banco", caixaBanco);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
    }

//    public synchronized String baixarBoletos() {
//        if (!listBoletos.isEmpty()) {
//            Movimento mov = null;
//            List<Movimento> lista = new ArrayList();
//
//            // NECESSÁRIO BLOQUEAR PORQ NA BAIXA POR RETORNO ELE FAZ VINCULO COM O DOCUMENTO DA PESSOA NA PESQUISA DO BOLETO CASO SEJA SINDICAL
////            if (pessoa.getDocumento().isEmpty() || pessoa.getDocumento().equals("0")) {
////                msgConfirma = "Pessoa não possui documento!";
////                return null;
////            }
//
//            // ROGERIO PEDIU PARA DEIXAR CAIXA IGUAL AO BANCO, PORQ pois o campo ds_documento_baixa da tabela fin_baixa está gravando o número do boleto -- EMAIL RECEBIDO
//            if (caixaBanco.equals("caixa")) {
//                for (int i = 0; i < listBoletos.size(); i++) {
//                    if ((Boolean) listBoletos.get(i).getArgumento8() == true) {
//                        mov = (Movimento) listBoletos.get(i).getArgumento1();
//                        mov.setValorBaixa(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(i).getArgumento7()))));
//
//                        mov.setMulta(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(i).getArgumento3()))));
//                        mov.setJuros(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(i).getArgumento4()))));
//                        mov.setCorrecao(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(i).getArgumento5()))));
//                        mov.setDesconto(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(i).getArgumento6()))));
//
//                        if (mov.getValorBaixa() <= 0) {
//                            msgConfirma = "Valor não pode ser zerado";
//                            return null;
//                        }
//                        lista.add(mov);
//                    }
//                }
//            } else {
//                mov = (Movimento) listBoletos.get(index).getArgumento1();
//                mov.setValorBaixa(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(index).getArgumento7()))));
//
//                mov.setMulta(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(index).getArgumento3()))));
//                mov.setJuros(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(index).getArgumento4()))));
//                mov.setCorrecao(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(index).getArgumento5()))));
//                mov.setDesconto(Double.parseDouble(Moeda.substituiVirgula(((String) listBoletos.get(index).getArgumento6()))));
//
//                if (mov.getValorBaixa() <= 0) {
//                    msgConfirma = "Valor não pode ser zerado";
//                    return null;
//                }
//                lista.add(mov);
//            }
//            
//            if (!lista.isEmpty()) {
//                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);
//            } else {
//                msgConfirma = "Nenhum boleto foi selecionado!";
//                return null;
//            }
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("caixa_banco", caixaBanco);
//        } else {
//            msgConfirma = "Lista vazia";
//            return null;
//        }
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
//        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
//    }
    public void atualizaValoresGrid(DataObject dob) {
        converteDesconto(dob);
        dob.setArgumento7(
                somarValorRecebido(
                        Moeda.converteR$(dob.getArgumento2().toString()),
                        Moeda.converteR$(dob.getArgumento3().toString()),
                        Moeda.converteR$(dob.getArgumento4().toString()),
                        Moeda.converteR$(dob.getArgumento5().toString()),
                        Moeda.converteR$(dob.getArgumento6().toString())
                )
        );
// aqui descomentar
        converteValor(dob);
        converteMulta(dob);
        converteJuros(dob);
        converteCorrecao(dob);
        converteValorPago(dob);
    }

    public String somarValorRecebido(String valor, String multa, String juros, String correcao, String desconto) {
        double v = Double.parseDouble(Moeda.substituiVirgula(valor));
        double m = Double.parseDouble(Moeda.substituiVirgula(multa));
        double j = Double.parseDouble(Moeda.substituiVirgula(juros));
        double c = Double.parseDouble(Moeda.substituiVirgula(correcao));
        double d = Double.parseDouble(Moeda.substituiVirgula(desconto));
        double soma = Moeda.soma(
                Moeda.soma(j, c),
                Moeda.soma(v, m));
        if (d > soma) {
            d = 0;
        }

        double subtracao = Moeda.subtracao(soma, d);

        return Moeda.converteR$(String.valueOf(subtracao));
    }

    public String limparPessoa() {
        pessoa = new Pessoa();
        habPessoa = false;
        listBoletos.clear();
        return "baixaBoleto";
    }

    public void converteValor(DataObject dob) {
        dob.setArgumento2(Moeda.converteR$(dob.getArgumento2().toString()));
    }

    public void converteMulta(DataObject dob) {
        dob.setArgumento3(Moeda.converteR$(dob.getArgumento3().toString()));
    }

    public void converteJuros(DataObject dob) {
        dob.setArgumento4(Moeda.converteR$(dob.getArgumento4().toString()));
    }

    public void converteCorrecao(DataObject dob) {
        dob.setArgumento5(Moeda.converteR$(dob.getArgumento5().toString()));
    }

    public void converteDesconto(DataObject dob) {
        if (Double.parseDouble(Moeda.substituiVirgula(Moeda.converteR$(dob.getArgumento6().toString()))) > Double.parseDouble(Moeda.substituiVirgula(dob.getArgumento7().toString()))) {
            dob.setArgumento6(Moeda.converteR$("0.0"));
        } else {
            dob.setArgumento6(Moeda.converteR$(dob.getArgumento6().toString()));
        }
    }

    public void converteDescontoFora(int index, String soma) {
        try {
            if (Double.parseDouble(Moeda.substituiVirgula(Moeda.converteR$((String) getListBoletos().get(index).getArgumento6()))) > Double.parseDouble(Moeda.substituiVirgula(soma))) {
                getListBoletos().get(index).setArgumento6(Moeda.converteR$("0.0"));
            } else {
                getListBoletos().get(index).setArgumento6(Moeda.converteR$((String) getListBoletos().get(index).getArgumento6()));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void converteValorPago(DataObject dob) {
        dob.setArgumento7(Moeda.converteR$(dob.getArgumento7().toString()));
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public String getNumBoleto() {
        return numBoleto;
    }

    public void setNumBoleto(String numBoleto) {
        this.numBoleto = numBoleto;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.getObject("juridicaPesquisa") == null) {
            if (this.novoNumero) {
                ServicoContaCobranca scc = new ServicoContaCobranca();
                ServicoContaCobrancaDao dbS = new ServicoContaCobrancaDao();
                MovimentoDao db = new MovimentoDao();

                scc = dbS.pesquisaCodigo(Integer.parseInt(getListaServicoCobranca().get(idServicos).getDescription()));

                //Movimento mov = db.pesquisaMovPorNumDocumento(concatBoleto(scc.getContaCobranca().getBoletoInicial(), numBoleto));
                Movimento mov = db.pesquisaMovPorNumDocumento(numBoleto);
                if (mov.getId() == -1) {
                    pessoa = new Pessoa();
                } else {
                    pessoa = mov.getPessoa();
                    numBoleto = mov.getDocumento();
                }
                novoNumero = false;
                habPessoa = false;
            }
        } else {
            pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
            habPessoa = true;
            loadListaBoleto();
        }
        return pessoa;
    }

    public void marcar() {
        for (DataObject listBoleto : getListBoletos()) {
            listBoleto.setArgumento8(marcarTodos);
        }
    }

    public String concatBoleto(String contaCobranca, String numero) {
        String zero = "000000000000000000";
        String result = "";
        if (contaCobranca.length() > 11) {
            result = contaCobranca.substring(0, 4);
            result = result + zero;
            result = result.substring(0, contaCobranca.length() - numero.length()) + numero;
            return result;
        } else {
            return numero;
        }
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public boolean isNovoNumero() {
        return novoNumero;
    }

    public void setNovoNumero(boolean novoNumero) {
        this.novoNumero = novoNumero;
    }

    public boolean isHabPessoa() {
        return habPessoa;
    }

    public void setHabPessoa(boolean habPessoa) {
        this.habPessoa = habPessoa;
    }

    public String getCaixaBanco() {
        if (!listBoletos.isEmpty()) {
            disBtnBaixar = false;
        } else {
            disBtnBaixar = true;
        }
        return caixaBanco;
    }

    public void setCaixaBanco(String caixaBanco) {
        this.caixaBanco = caixaBanco;
    }

    public boolean isDisBtnBaixar() {
        return disBtnBaixar;
    }

    public void setDisBtnBaixar(boolean disBtnBaixar) {
        this.disBtnBaixar = disBtnBaixar;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public boolean isMarcarTodos() {
        return marcarTodos;
    }

    public void setMarcarTodos(boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public List<DataObject> getListBoletos() {
        return listBoletos;
    }

    public void setListBoletos(List<DataObject> listBoletos) {
        this.listBoletos = listBoletos;
    }
}
