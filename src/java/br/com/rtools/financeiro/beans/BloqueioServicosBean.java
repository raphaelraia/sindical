package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.BloqueiaServicoPessoa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

public class BloqueioServicosBean {

    private BloqueiaServicoPessoa bloqueia = new BloqueiaServicoPessoa();
    private Pessoa pessoa = new Pessoa();
    private int idServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();
    private List<BloqueiaServicoPessoa> listaBloqueios = new ArrayList();
    private String msgConfirma = "";
    private String refInicial = "";
    private String refFinal = "";

    public BloqueioServicosBean() {
        refInicial = DataHoje.dataReferencia(DataHoje.data());
        refFinal = refInicial.substring(0, 2) + "/2050";
    }

    public void novo() {
        bloqueia = new BloqueiaServicoPessoa();

        refInicial = DataHoje.dataReferencia(DataHoje.data());
        refFinal = refInicial.substring(0, 2) + "/2050";

        listaBloqueios.clear();
    }

    public String salvar() {
        Dao dao = new Dao();

        if (pessoa.getId() == -1) {
            msgConfirma = "Pesquise uma pessoa para bloquear!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (refInicial.isEmpty()) {
            msgConfirma = "Referência inicial esta vazia!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (refFinal.isEmpty()) {
            msgConfirma = "Referência final esta vazia!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        refInicial = new DataHoje().incrementarMeses(1, "01/" + refInicial).substring(3);
        refFinal = new DataHoje().incrementarMeses(1, "01/" + refFinal).substring(3);

        Servicos servicos = (Servicos) dao.find(new Servicos(), Integer.parseInt(this.getListaServico().get(idServicos).getDescription()));
        NovoLog novoLog = new NovoLog();
        FinanceiroDao db = new FinanceiroDao();

        int d_fim = DataHoje.qtdeDiasDoMes(Integer.valueOf(refFinal.substring(0, 2)), Integer.valueOf(refFinal.substring(3, 7)));

        bloqueia.setInicio("01/" + refInicial);
        bloqueia.setFim(d_fim + "/" + refFinal);
        bloqueia.setPessoa(pessoa);
        bloqueia.setServicos(servicos);

        if (bloqueia.getId() == -1) {

            if (db.pesquisaBloqueiaServicoPessoa(pessoa.getId(), servicos.getId(), bloqueia.getDtInicio(), bloqueia.getDtFim(), null)) {
                novo();

                msgConfirma = "Este bloqueio já existe!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            dao.openTransaction();
            if (dao.save(bloqueia)) {
                novoLog.save(
                        "ID: " + bloqueia.getId()
                        + " - Pessoa: (" + bloqueia.getPessoa().getId() + ") " + bloqueia.getPessoa().getNome()
                        + " - Serviços: (" + bloqueia.getServicos().getId() + ") " + bloqueia.getServicos().getDescricao()
                        + " - Período: " + bloqueia.getInicio() + " - " + bloqueia.getFim()
                        + " - Gerar Guias: " + bloqueia.isGeracao()
                        + " - Impressão: " + bloqueia.isImpressao()
                );
                msgConfirma = "Bloqueio salvo com Sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                dao.commit();
            } else {
                msgConfirma = "Erro ao salvar Bloqueio!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }
            novo();
        } else {

            if (db.pesquisaBloqueiaServicoPessoa(pessoa.getId(), servicos.getId(), bloqueia.getDtInicio(), bloqueia.getDtFim(), bloqueia.getId())) {
                novo();
                msgConfirma = "Este bloqueio já existe!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            dao.openTransaction();

            BloqueiaServicoPessoa bsp = (BloqueiaServicoPessoa) dao.find(bloqueia);

            String beforeUpdate
                    = "ID: " + bsp.getId()
                    + " - Pessoa: (" + bsp.getPessoa().getId() + ") " + bsp.getPessoa().getNome()
                    + " - Serviços: (" + bsp.getServicos().getId() + ") " + bsp.getServicos().getDescricao()
                    + " - Período: " + bsp.getInicio() + " - " + bsp.getFim()
                    + " - Gerar Guias: " + bsp.isGeracao()
                    + " - Impressão: " + bsp.isImpressao();

            if (dao.update(bloqueia)) {
                novoLog.update(beforeUpdate,
                        "ID: " + bloqueia.getId()
                        + " - Pessoa: (" + bloqueia.getPessoa().getId() + ") " + bloqueia.getPessoa().getNome()
                        + " - Serviços: (" + bloqueia.getServicos().getId() + ") " + bloqueia.getServicos().getDescricao()
                        + " - Período: " + bloqueia.getInicio() + " - " + bloqueia.getFim()
                        + " - Gerar Guias: " + bloqueia.isGeracao()
                        + " - Impressão: " + bloqueia.isImpressao()
                );
                msgConfirma = "Bloqueio alterado com Sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                dao.commit();
            } else {
                msgConfirma = "Erro ao excluir Bloqueio!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }
            novo();
        }
        return null;
    }

    public String excluir(BloqueiaServicoPessoa bl) {
        NovoLog novoLog = new NovoLog();
        if (new Dao().delete(bl, true)) {
            novoLog.delete(
                    "ID: " + bl.getId()
                    + " - Pessoa: (" + bl.getPessoa().getId() + ") " + bl.getPessoa().getNome()
                    + " - Serviços: (" + bl.getServicos().getId() + ") " + bl.getServicos().getDescricao()
                    + " - Período: " + bl.getInicio() + " - " + bl.getFim()
                    + " - Gerar Guias: " + bl.isGeracao()
                    + " - Impressão: " + bl.isImpressao()
            );
            msgConfirma = "Bloqueio excluído com Sucesso!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            listaBloqueios.clear();
        } else {
            msgConfirma = "Erro ao excluir bloqueio!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }

        novo();

        return null;
    }

    public void editar(BloqueiaServicoPessoa bl) {
        bloqueia = bl;

        refInicial = DataHoje.dataReferencia(bloqueia.getInicio());
        refFinal = DataHoje.dataReferencia(bloqueia.getFim());

        for (int i = 0; i < listaServicos.size(); i++) {
            if (Integer.valueOf(listaServicos.get(i).getDescription()) == bloqueia.getServicos().getId()) {
                idServicos = i;
            }
        }
    }

    public String alteraImprime(BloqueiaServicoPessoa bl) {
        if (bl.isImpressao()) {
            bl.setImpressao(false);
        } else {
            bl.setImpressao(true);
        }
        if (new Dao().update(bl, true)) {
            msgConfirma = "Bloqueio atualizado!";
            GenericaMensagem.info("Sucesso", msgConfirma);
        } else {
            msgConfirma = "Erro ao atualizar status de Bloqueio!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }

        listaBloqueios.clear();
        return null;
    }

    public String alteraGera(BloqueiaServicoPessoa bl) {
        if (bl.isGeracao()) {
            bl.setGeracao(false);
        } else {
            bl.setGeracao(true);
        }
        if (new Dao().update(bl, true)) {
            msgConfirma = "Bloqueio atualizado!";
            GenericaMensagem.info("Sucesso", msgConfirma);
        } else {
            msgConfirma = "Erro ao atualizar status de Bloqueio!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }
        listaBloqueios.clear();
        return null;
    }

    public List<SelectItem> getListaServico() {
        if (listaServicos.isEmpty()) {
            List select = new Dao().list(new Servicos(), true);
            for (int i = 0; i < select.size(); i++) {
                listaServicos.add(new SelectItem(
                        new Integer(i),
                        (String) ((Servicos) select.get(i)).getDescricao(),
                        Integer.toString(((Servicos) select.get(i)).getId())));
            }
        }
        return listaServicos;
    }

    public String removerPesquisa() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
        pessoa = new Pessoa();
        listaBloqueios.clear();
        return "bloqueioServicos";
    }

    public BloqueiaServicoPessoa getBloqueia() {
        return bloqueia;
    }

    public void setBloqueia(BloqueiaServicoPessoa bloqueia) {
        this.bloqueia = bloqueia;
    }

    public Pessoa getPessoa() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pessoaPesquisa") != null) {
            pessoa = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pessoaPesquisa");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
            listaBloqueios.clear();
            msgConfirma = "";
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public List<BloqueiaServicoPessoa> getListaBloqueios() {
        if (listaBloqueios.isEmpty() && pessoa.getId() != -1) {
            FinanceiroDao dao = new FinanceiroDao();
            listaBloqueios = dao.listaBloqueiaServicoPessoas(pessoa.getId());
        }
        return listaBloqueios;
    }

    public void setListaBloqueios(List<BloqueiaServicoPessoa> listaBloqueios) {
        this.listaBloqueios = listaBloqueios;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public String getRefInicial() {
        return refInicial;
    }

    public void setRefInicial(String refInicial) {
        this.refInicial = refInicial;
    }

    public String getRefFinal() {
        return refFinal;
    }

    public void setRefFinal(String refFinal) {
        this.refFinal = refFinal;
    }
}
