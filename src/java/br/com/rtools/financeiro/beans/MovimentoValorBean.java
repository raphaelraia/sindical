package br.com.rtools.financeiro.beans;

import br.com.rtools.arrecadacao.DescontoEmpregado;
import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.arrecadacao.dao.DescontoEmpregadoDao;
import br.com.rtools.arrecadacao.dao.FolhaEmpresaDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.financeiro.db.MovimentoDB;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import br.com.rtools.pessoa.db.JuridicaDB;
import br.com.rtools.pessoa.db.JuridicaDBToplink;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Moeda;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public abstract class MovimentoValorBean {

    private DescontoEmpregado descontoEmpregado = new DescontoEmpregado();
    private FolhaEmpresa folhaEmpresa = new FolhaEmpresa();
    private String valor = "0";
    private String valorBoleto = "0";
    private int qtdFuncionario = 0;
    private boolean mostrarPainel;
    private String labelFolha = "";
    private String labelFolha2 = "";
    private String labelLink = "";
    private Movimento movimento = null;
    private int idTipoServico = -1;

    public MovimentoValorBean() {
    }

    public abstract void carregarFolha();

    public abstract void carregarFolha(DataObject valor);

    public abstract void atualizaValorGrid(String tipo);

    public synchronized float carregarValor(int idServico, int idTipo, String ref, int idPessoa) {
        MovimentoDB movDB = new MovimentoDBToplink();
        Object[] valorFloat = movDB.pesquisaValorFolha(idServico, idTipo, ref, idPessoa);
        this.idTipoServico = idTipo;
        FolhaEmpresa folha = null;
        DescontoEmpregadoDao desDB = new DescontoEmpregadoDao();
        DescontoEmpregado desEmpregado = desDB.pesquisaEntreReferencias(
                ref,
                idServico,
                idPessoa);
        FolhaEmpresaDao dbFolha = new FolhaEmpresaDao();
        folha = dbFolha.pesquisaPorPessoa(
                idPessoa,
                idTipo,
                ref);
        if (valorFloat == null) {
            valorFloat = new Float[]{new Float(0), new Float(0)};
        }
        if (((Float) valorFloat[0]) != 0) {
            return Moeda.converteFloatR$Float(
                    Moeda.somaValores(
                            Moeda.multiplicarValores(
                                    ((Float) valorFloat[0]),
                                    (((Float) valorFloat[1]) / 100)),
                            Moeda.multiplicarValores(
                                    folha.getNumFuncionarios(),
                                    desEmpregado.getValorEmpregado())));
        } else {
            return (float) 0.0;
        }
    }

    public boolean carregarFolha(Movimento movimento) {
        folhaEmpresa = new FolhaEmpresa();
        descontoEmpregado = new DescontoEmpregado();
        if (movimento == null) {
            return false;
        }
        this.movimento = movimento;
        DescontoEmpregadoDao desDB = new DescontoEmpregadoDao();
        descontoEmpregado = desDB.pesquisaEntreReferencias(
                movimento.getReferencia(),
                movimento.getServicos().getId(),
                movimento.getPessoa().getId());

        if (descontoEmpregado == null) {
            descontoEmpregado = new DescontoEmpregado();
            mostrarPainel = true;
            labelLink = "Informe o desconto empregado para referência - Click aqui " + movimento.getReferencia();
            labelFolha = "";
            labelFolha2 = "";
        } else {
            labelLink = "";
            labelFolha2 = "Número de Funcionários: ";
            labelFolha = "Atualizar valor da folha do mês para referência " + movimento.getReferencia();
            mostrarPainel = false;
        }

        if (idTipoServico != 4) {
            folhaEmpresa = this.pesquisaFolhaEmpresa(
                    movimento.getPessoa().getId(),
                    movimento.getTipoServico().getId(),
                    movimento.getReferencia());

            if (folhaEmpresa.getId() != -1) {
                String valorFolha = Float.toString(folhaEmpresa.getValorMes());
                setValor(valorFolha);
                setQtdFuncionario(folhaEmpresa.getNumFuncionarios());
            } else {
                setValor("0");
                setQtdFuncionario(0);
            }
        }

        setValorBoleto(Moeda.converteR$Float(movimento.getValor()));
        return true;
    }

    public void esconder() {
        setQtdFuncionario(0);
        setValor("0");
        setValorBoleto("0");
    }

    public String getLabelFolha() {
        return labelFolha;
    }

    public void setLabelFolha(String labelFolha) {
        this.labelFolha = labelFolha;
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public String getValorBoleto() {
        return Moeda.converteR$(valorBoleto);
    }

    public void setValorBoleto(String valorBoleto) {
        this.valorBoleto = Moeda.substituiVirgula(valorBoleto);
    }

    public synchronized String atualizaValor(boolean salvar, String tipo) {
        if (movimento == null) {
            return "";
        }
        try {
            float valorMes = Float.parseFloat(valor);
            float valorGuia = Float.parseFloat(valorBoleto);

            if (tipo.equals("valor")) {
                JuridicaDB jurDB = new JuridicaDBToplink();
                Dao dao = new Dao();
                dao.openTransaction();

                if (descontoEmpregado != null && descontoEmpregado.getId() != -1) {
                    valorMes
                            = Moeda.converteFloatR$Float(
                                    Moeda.somaValores(
                                            Moeda.divisaoValores(
                                                    valorGuia,
                                                    (descontoEmpregado.getPercentual() / 100)),
                                            Moeda.divisaoValores(
                                                    folhaEmpresa.getNumFuncionarios(),
                                                    descontoEmpregado.getValorEmpregado())));
                } else {
                    valorMes = 0;
                }

                if (folhaEmpresa.getId() == -1) {
                    folhaEmpresa.setValorMes(valorMes);
                    folhaEmpresa.setNumFuncionarios(qtdFuncionario);
                    folhaEmpresa.setJuridica(jurDB.pesquisaJuridicaPorPessoa(movimento.getPessoa().getId()));
                    folhaEmpresa.setReferencia(movimento.getReferencia());
                    folhaEmpresa.setTipoServico(movimento.getTipoServico());
                    dao.save(folhaEmpresa);
                } else {
                    folhaEmpresa.setValorMes(valorMes);
                    folhaEmpresa.setAlteracoes(folhaEmpresa.getAlteracoes() + 1);
                    folhaEmpresa.setNumFuncionarios(qtdFuncionario);
                    dao.update(folhaEmpresa);
                }
                movimento.setValor(valorGuia);

                if (salvar) {
                    if (movimento.getId() == -1) {
                        //sv.inserirObjeto(movimento);
                    } else {
                        // SE ALTERAR O VENCIMENTO E FOR COBRANÇA REGISTRADA, ENTÃO ALTERAR A DATA DE REGISTRO PARA QUANDO IMPRIMIR REGISTRAR NOVAMENTE
                        MovimentoDB dbm = new MovimentoDBToplink();
                        Boleto bol = dbm.pesquisaBoletos(movimento.getNrCtrBoleto());
                        if (bol != null) {
                            if (bol.getContaCobranca().isCobrancaRegistrada()) {
                                bol.setDtCobrancaRegistrada(null);
                                dao.update(bol);
                            }
                        }

                        dao.update(movimento);
                        Lote lote = (Lote) dao.find(new Lote(), movimento.getLote().getId());
                        lote.setValor(movimento.getValor());
                        dao.update(lote);
                    }
                }
                dao.commit();
            } else if (valorMes != 0) {
                JuridicaDB jurDB = new JuridicaDBToplink();
                Dao dao = new Dao();
                dao.openTransaction();

                if (folhaEmpresa.getId() == -1) {
                    folhaEmpresa.setValorMes(valorMes);
                    folhaEmpresa.setNumFuncionarios(qtdFuncionario);
                    folhaEmpresa.setJuridica(jurDB.pesquisaJuridicaPorPessoa(movimento.getPessoa().getId()));
                    folhaEmpresa.setReferencia(movimento.getReferencia());
                    folhaEmpresa.setTipoServico(movimento.getTipoServico());
                    dao.save(folhaEmpresa);
                } else {
                    folhaEmpresa.setValorMes(valorMes);
                    folhaEmpresa.setAlteracoes(folhaEmpresa.getAlteracoes() + 1);
                    folhaEmpresa.setNumFuncionarios(qtdFuncionario);
                    dao.update(folhaEmpresa);
                }

                if (valorMes == 0) {
                    return "";
                }
                movimento.setValor(
                        Moeda.converteFloatR$Float(
                                Moeda.somaValores(
                                        Moeda.multiplicarValores(
                                                valorMes,
                                                (descontoEmpregado.getPercentual() / 100)),
                                        Moeda.multiplicarValores(
                                                folhaEmpresa.getNumFuncionarios(),
                                                descontoEmpregado.getValorEmpregado()))));
                if (salvar) {
                    if (movimento.getId() == -1) {
                    } else {
                        // SE ALTERAR O VENCIMENTO E FOR COBRANÇA REGISTRADA, ENTÃO ALTERAR A DATA DE REGISTRO PARA QUANDO IMPRIMIR REGISTRAR NOVAMENTE
                        MovimentoDB dbm = new MovimentoDBToplink();
                        Boleto bol = dbm.pesquisaBoletos(movimento.getNrCtrBoleto());
                        if (bol != null) {
                            if (bol.getContaCobranca().isCobrancaRegistrada()) {
                                bol.setDtCobrancaRegistrada(null);
                                dao.update(bol);
                            }
                        }

                        dao.update(movimento);
                        Lote lote = (Lote) dao.find(new Lote(), movimento.getLote().getId());
                        lote.setValor(movimento.getValor());
                        dao.update(lote);

                    }
                }
                dao.commit();

                folhaEmpresa = new FolhaEmpresa();
                descontoEmpregado = new DescontoEmpregado();
            }

            esconder();
            return Moeda.converteR$Float(movimento.getValor());
        } catch (Exception e) {
            String a = e.getMessage();
            esconder();
            return "";
        }

    }

    protected FolhaEmpresa pesquisaFolhaEmpresa(int idPessoa, int idTipoServico, String referencia) {
        FolhaEmpresa result = null;
        FolhaEmpresaDao dbFolha = new FolhaEmpresaDao();
        result = dbFolha.pesquisaPorPessoa(idPessoa, idTipoServico, referencia);
        if (result != null) {
            return result;
        } else {
            return new FolhaEmpresa();
        }
    }

    public String salvar(Object object, int id) {
        Dao dao = new Dao();
        if (id == -1) {
            dao.save(object, true);
        } else {
            dao.update(object, true);
        }
        return null;
    }

    public String salvar(FolhaEmpresa object) {
        Dao dao = new Dao();
        if (object.getId() == -1) {
            dao.save(object, true);
        } else {
            dao.update(object, true);
        }
        return null;
    }

    public DescontoEmpregado getDescontoEmpregado() {
        return descontoEmpregado;
    }

    public void setDescontoEmpregado(DescontoEmpregado descontoEmpregado) {
        this.descontoEmpregado = descontoEmpregado;
    }

    public FolhaEmpresa getFolhaEmpresa() {
        return folhaEmpresa;
    }

    public void setFolhaEmpresa(FolhaEmpresa folhaEmpresa) {
        this.folhaEmpresa = folhaEmpresa;
    }

    public int getQtdFuncionario() {
        return qtdFuncionario;
    }

    public void setQtdFuncionario(int qtdFuncionario) {
        this.qtdFuncionario = qtdFuncionario;
    }

    public boolean isMostrarPainel() {
        return mostrarPainel;
    }

    public void setMostrarPainel(boolean mostrarPainel) {
        this.mostrarPainel = mostrarPainel;
    }

    public String getLabelFolha2() {
        return labelFolha2;
    }

    public void setLabelFolha2(String labelFolha2) {
        this.labelFolha2 = labelFolha2;
    }

    public String getLabelLink() {
        return labelLink;
    }

    public void setLabelLink(String labelLink) {
        this.labelLink = labelLink;
    }
}
