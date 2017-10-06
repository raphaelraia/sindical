package br.com.rtools.financeiro.lista;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import java.util.ArrayList;
import java.util.List;

public class ListServicosSubGrupoFinanceiro {

    private Servicos servicos;
    private SubGrupoFinanceiro subGrupoFinanceiro;
    private String descricao;
    private List<Servicos> listServicos;
    private Boolean agendamento;

    public ListServicosSubGrupoFinanceiro() {
        this.servicos = null;
        this.subGrupoFinanceiro = null;
        this.descricao = "";
        this.listServicos = new ArrayList();
        this.agendamento = false;
    }

    public ListServicosSubGrupoFinanceiro(Servicos servicos, SubGrupoFinanceiro subGrupoFinanceiro, String descricao, List listServicos, Boolean agendamento) {
        this.servicos = servicos;
        this.subGrupoFinanceiro = subGrupoFinanceiro;
        this.descricao = descricao;
        this.listServicos = listServicos;
        this.agendamento = agendamento;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public SubGrupoFinanceiro getSubGrupoFinanceiro() {
        return subGrupoFinanceiro;
    }

    public void setSubGrupoFinanceiro(SubGrupoFinanceiro subGrupoFinanceiro) {
        this.subGrupoFinanceiro = subGrupoFinanceiro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Servicos> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<Servicos> listServicos) {
        this.listServicos = listServicos;
    }

    public Boolean getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Boolean agendamento) {
        this.agendamento = agendamento;
    }

}
