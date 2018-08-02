package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.CobrancaRegistrada;
import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Layout;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ContaCobrancaBean {

    private ContaCobranca contaCobranca = new ContaCobranca();
    private List<ContaCobranca> listaContaCobranca = new ArrayList();
    private int idLayout = 0;
    private String msgConfirma = "";
    private String repasse = "0.0";
    private String sicas = "";
    private String codigoCedente = "";
    private boolean limpar = false;

    private Integer indexListaCobrancaRegistrada = 0;
    private List<SelectItem> listaCobrancaRegistrada = new ArrayList();
    private String tipoDeCobranca = "arrecadacao";

    public ContaCobrancaBean() {
        loadListaCobrancaRegistrada();
    }

    public final void loadListaCobrancaRegistrada() {
        listaCobrancaRegistrada.clear();

        List<CobrancaRegistrada> result = new Dao().list(new CobrancaRegistrada());

        for (int i = 0; i < result.size(); i++) {
            listaCobrancaRegistrada.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public String salvar() {

        ContaCobrancaDao db = new ContaCobrancaDao();
        Layout la = db.pesquisaLayoutId(Integer.valueOf(getListaLayout().get(idLayout).getDescription()));
        Dao dao = new Dao();

        msgConfirma = "";

        contaCobranca.setSicasSindical(sicas);
        contaCobranca.setCodigoSindical(codigoCedente);
        contaCobranca.setLayout(la);
        contaCobranca.setCobrancaRegistrada((CobrancaRegistrada) dao.find(new CobrancaRegistrada(), Integer.valueOf(listaCobrancaRegistrada.get(indexListaCobrancaRegistrada).getDescription())));

        if (tipoDeCobranca.equals("arrecadacao")) {
            contaCobranca.setArrecadacao(true);
            contaCobranca.setAssociativo(false);
        } else {
            contaCobranca.setArrecadacao(false);
            contaCobranca.setAssociativo(true);
        }

        if (contaCobranca.getContaBanco().getBanco().getBanco().isEmpty()) {
            msgConfirma = "Atenção, é preciso pesquisar um Banco!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        // CASO FOR BANCO DO BRASIL E WEBSERVICE O CAMPO nr_comercio_eletronico É OBRIGATÓRIO
        if (contaCobranca.getContaBanco().getBanco().getNumero().equals("001") && contaCobranca.getCobrancaRegistrada().getId() == 2 && contaCobranca.getNrComercioEletronico() == null) {
            msgConfirma = "Atenção, o campo Comércio Eletrônico é obrigatório!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if ((contaCobranca.getCodCedente().isEmpty()) || (contaCobranca.getCodCedente().equals("0"))) {
            msgConfirma = "Digite um Código Cedente!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getCedente().isEmpty()) {
            msgConfirma = "Digite um Cedente!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getLocalPagamento().isEmpty()) {
            msgConfirma = "Local de Pagamento não pode ser nulo!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if ((contaCobranca.getBoletoInicial().equals("0")) || (contaCobranca.getBoletoInicial().isEmpty())) {
            msgConfirma = "Boleto Inicial está em branco!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getMoeda().isEmpty()) {
            msgConfirma = "O campo Moeda está em branco!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getEspecieMoeda().isEmpty()) {
            msgConfirma = "O campo Espécie Moeda está em branco!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getEspecieDoc().isEmpty()) {
            msgConfirma = "Digite uma Espécie de Documento!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (contaCobranca.getAceite().isEmpty()) {
            msgConfirma = "Digite um Aceite!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        // SE FOR SINDICAL
        if (contaCobranca.getLayout().getId() == 2) {
            // REMESSA - WEB SERVICE
            if (contaCobranca.getCobrancaRegistrada().getId() == 1 || contaCobranca.getCobrancaRegistrada().getId() == 2) {
                if (contaCobranca.getCodigoSindical().length() != 14) {
                    GenericaMensagem.error("Atenção", "Código Sindical deve conter 14 digitos!");
                    return null;
                }

                if (contaCobranca.getCodCedente().length() != 6) {
                    GenericaMensagem.error("Atenção", "Código Cedente deve conter 6 digitos!");
                    return null;
                }

                if (contaCobranca.getBoletoInicial().length() != 17) {
                    GenericaMensagem.error("Atenção", "Boleto Inicial deve conter 17 digitos!");
                    return null;
                }

                if (!contaCobranca.getBoletoInicial().substring(0, 2).equals("14")) {
                    GenericaMensagem.error("Atenção", "Boleto Inicial deve começar com 14!");
                    return null;
                }
            }
        }

        NovoLog log = new NovoLog();
        if (contaCobranca.getId() == -1) {
            if (db.idContaCobranca(contaCobranca) != null) {
                msgConfirma = "Este cadastro já existe no Sistema.";
                GenericaMensagem.warn("Erro", msgConfirma);
            } else {
                atualizarSicas();
                if (dao.save(contaCobranca, true)) {
                    log.save("ID: " + contaCobranca.getId() + " Banco: " + contaCobranca.getContaBanco().getBanco().getBanco() + " - Agência: " + contaCobranca.getContaBanco().getAgencia() + " - Conta: " + contaCobranca.getContaBanco().getConta() + " - Cedente: " + contaCobranca.getCedente() + " - Código Cedente: " + contaCobranca.getCodCedente());
                    msgConfirma = "Cadastro salvo com Sucesso!";
                    GenericaMensagem.info("Sucesso", msgConfirma);
                } else {
                    msgConfirma = "Erro ao Salvar!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                }
            }
        } else {
            ContaCobranca conta = (ContaCobranca) dao.find(new ContaCobranca(), contaCobranca.getId());
            String antes = "ID: " + conta.getId() + " Banco: " + conta.getContaBanco().getBanco().getBanco() + " - Agência: " + conta.getContaBanco().getAgencia() + " - Conta: " + conta.getContaBanco().getConta() + " - Cedente: " + conta.getCedente() + " - Código Cedente: " + conta.getCodCedente();
            atualizarSicas();
            if (dao.update(contaCobranca, true)) {
                log.update(antes, "ID: " + contaCobranca.getId() + " Banco: " + contaCobranca.getContaBanco().getBanco().getBanco() + " - Agência: " + contaCobranca.getContaBanco().getAgencia() + " - Conta: " + contaCobranca.getContaBanco().getConta() + " - Cedente: " + contaCobranca.getCedente() + " - Código Cedente: " + contaCobranca.getCodCedente());
                msgConfirma = "Cadastro atualizado com sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
            } else {
                msgConfirma = "Falha na atualização do cadastro!";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        }
        limpar = false;
        return null;
    }

    private void atualizarSicas() {
        String codigoSindical = "";
        try {
            FilialDao filialDB = new FilialDao();
            String entidade = filialDB.pesquisaRegistroPorFilial(1).getTipoEntidade();
            codigoSindical = contaCobranca.getCodigoSindical();
            if (entidade.equals("S")) {
                contaCobranca.setSicasSindical(codigoSindical.substring(codigoSindical.length() - 5, codigoSindical.length()));
            } else if (entidade.equals("C")) {
                contaCobranca.setSicasSindical("00" + codigoSindical.substring(0, 3));
            } else if (entidade.equals("F")) {
                contaCobranca.setSicasSindical("00" + codigoSindical.substring(3, 6));
            }
        } catch (Exception e) {
            contaCobranca.setSicasSindical("");
        }
    }

    public String novo() {
        contaCobranca = new ContaCobranca();
        listaContaCobranca.clear();
        sicas = "";
        idLayout = 0;
        repasse = "0.0";
        codigoCedente = "";
        msgConfirma = "";
        limpar = false;
        return "contaCobranca";
    }

    public void limpar() {
        if (limpar == true) {
            novo();
        }
    }

    public String excluir() {
        Dao dao = new Dao();
        if (contaCobranca.getId() != -1) {
            dao.openTransaction();
            contaCobranca = (ContaCobranca) dao.find(new ContaCobranca(), contaCobranca.getId());
            NovoLog log = new NovoLog();
            if (dao.delete(contaCobranca)) {
                dao.commit();
                log.delete(" ID: " + contaCobranca.getId() + " - Banco: " + contaCobranca.getContaBanco().getBanco().getBanco() + " - Agência: " + contaCobranca.getContaBanco().getAgencia() + " - Conta: " + contaCobranca.getContaBanco().getConta() + " - Cedente: " + contaCobranca.getCedente() + " - Código Cedente: " + contaCobranca.getCodCedente());
                msgConfirma = "Cadastro Excluido com sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                limpar = true;
            } else {
                dao.rollback();
                msgConfirma = "Não foi possível excluir esse cadastro. Verifique se há vínculos externos!";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        }
        return null;
    }

    public List<ContaCobranca> getListaContaCobranca() {
        listaContaCobranca = new Dao().list(new ContaCobranca());
        return listaContaCobranca;
    }

    public void setListaContaCobranca(List<ContaCobranca> listaContaCobranca) {
        this.listaContaCobranca = listaContaCobranca;
    }

    public List<SelectItem> getListaLayout() {
        ContaCobrancaDao db = new ContaCobrancaDao();
        List<SelectItem> result = new ArrayList<>();
        List layouts = db.pesquisaLayouts();
        for (int i = 0; i < layouts.size(); i++) {
            result.add(new SelectItem(i, ((Layout) layouts.get(i)).getDescricao(), Integer.toString(((Layout) layouts.get(i)).getId())));
        }
        return result;
    }

    public String editar(ContaCobranca c) {
        contaCobranca = c;
        ContaCobrancaDao db = new ContaCobrancaDao();
        List<Layout> layouts = db.pesquisaLayouts();
        for (int i = 0; i < layouts.size(); i++) {
            if (layouts.get(i).getId() == contaCobranca.getLayout().getId()) {
                idLayout = i;
            }
        }

        loadListaCobrancaRegistrada();
        for (int i = 0; i < listaCobrancaRegistrada.size(); i++) {
            if (Integer.valueOf(listaCobrancaRegistrada.get(i).getDescription()) == contaCobranca.getCobrancaRegistrada().getId()) {
                indexListaCobrancaRegistrada = i;
            }
        }

        setSicas(contaCobranca.getSicasSindical());
        setCodigoCedente(contaCobranca.getCodigoSindical());

        tipoDeCobranca = contaCobranca.isArrecadacao() ? "arrecadacao" : "associativo";

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("contaCobrancaPesquisa", contaCobranca);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno") == null) {
            return "contaCobranca";
        } else {
            return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        }
    }

    public String preencheSicasECodSindical() {
        if (codigoCedente.length() >= 5) {
            sicas = codigoCedente.substring(codigoCedente.length() - 5, codigoCedente.length());
        } else {
            sicas = "";
        }
        //codigoCedente = contaCobranca.getCodCedente();
        return null;
    }

    public String getRepasse() {
        return repasse;
    }

    public void setRepasse(String repasse) {
        this.repasse = Moeda.substituiVirgula(repasse);
    }

    public ContaCobranca getContaCobranca() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("contaCobrancaPesquisa") != null) {
            contaCobranca = (ContaCobranca) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("contaCobrancaPesquisa");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("contaCobrancaPesquisa");
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("contaBancoPesquisa") != null) {
            contaCobranca.setContaBanco((ContaBanco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("contaBancoPesquisa"));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("contaBancoPesquisa");
        }
        return contaCobranca;
    }

    public void setContaCobranca(ContaCobranca contaCobranca) {
        this.contaCobranca = contaCobranca;
    }

    public int getIdLayout() {
        return idLayout;
    }

    public void setIdLayout(int idLayout) {
        this.idLayout = idLayout;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public String getSicas() {
        return sicas;
    }

    public void setSicas(String sicas) {
        this.sicas = sicas;
    }

    public String getCodigoCedente() {
        return codigoCedente;
    }

    public void setCodigoCedente(String codigoCedente) {
        this.codigoCedente = codigoCedente;
    }

    public boolean isLimpar() {
        return limpar;
    }

    public void setLimpar(boolean limpar) {
        this.limpar = limpar;
    }

    public List<SelectItem> getListaCobrancaRegistrada() {
        return listaCobrancaRegistrada;
    }

    public void setListaCobrancaRegistrada(List<SelectItem> listaCobrancaRegistrada) {
        this.listaCobrancaRegistrada = listaCobrancaRegistrada;
    }

    public Integer getIndexListaCobrancaRegistrada() {
        return indexListaCobrancaRegistrada;
    }

    public void setIndexListaCobrancaRegistrada(Integer indexListaCobrancaRegistrada) {
        this.indexListaCobrancaRegistrada = indexListaCobrancaRegistrada;
    }

    public String getTipoDeCobranca() {
        return tipoDeCobranca;
    }

    public void setTipoDeCobranca(String tipoDeCobranca) {
        this.tipoDeCobranca = tipoDeCobranca;
    }
}
