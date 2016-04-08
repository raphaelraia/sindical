package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.financeiro.Movimento;
import br.com.rtools.locadoraFilme.LocadoraMovimento;
import br.com.rtools.locadoraFilme.LocadoraStatus;
import br.com.rtools.locadoraFilme.dao.LocadoraMovimentoDao;
import br.com.rtools.locadoraFilme.dao.LocadoraStatusDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.db.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class MenuLocadoraBean implements Serializable {

    private List<LocadoraMovimento> listLocadoraMovimento;
    private List<LocadoraMovimento> listLocadoraHistorico;
    private LocadoraStatus locadoraStatus;
    private String status;
    private Boolean liberaAcessaFilial;
    private List<SelectItem> listFiliais;
    private Integer filial_id;

    @PostConstruct
    public void init() {
        liberaAcessaFilial = false;
        listLocadoraMovimento = new ArrayList();
        listLocadoraHistorico = new ArrayList();
        loadLiberaAcessaFilial();
        listFiliais = new ArrayList<>();
        filial_id = null;
        locadoraStatus = new LocadoraStatusDao().findByFilialData(filial_id);
        if (locadoraStatus == null) {
            locadoraStatus = new LocadoraStatusDao().findByFilialSemana(filial_id);
        }
        status = "nao_devolvidos";
        getListFiliais();
        loadLocadoraMovimento();
    }

    public void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public void loadLocadoraMovimento() {
        listLocadoraMovimento.clear();
        listLocadoraMovimento = new LocadoraMovimentoDao().pesquisaHistoricoPorFilial(status, filial_id);
        listLocadoraHistorico.clear();
        listLocadoraHistorico = new LocadoraMovimentoDao().pesquisaHistoricoPorFilial("nao_devolvidos", filial_id);
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("menuLocadoraBean");
    }

    public List<LocadoraMovimento> getListLocadoraMovimento() {
        return listLocadoraMovimento;
    }

    public void setListLocadoraMovimento(List<LocadoraMovimento> listLocadoraMovimento) {
        this.listLocadoraMovimento = listLocadoraMovimento;
    }

    public LocadoraStatus getLocadoraStatus() {
        return locadoraStatus;
    }

    public void setLocadoraStatus(LocadoraStatus locadoraStatus) {
        this.locadoraStatus = locadoraStatus;
    }

    public List<LocadoraMovimento> getListLocadoraHistorico() {
        return listLocadoraHistorico;
    }

    public void setListLocadoraHistorico(List<LocadoraMovimento> listLocadoraHistorico) {
        this.listLocadoraHistorico = listLocadoraHistorico;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDevolver() {
        Boolean devolver = false;
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getSelected()) {
                devolver = true;
                break;
            }
        }
        return devolver;
    }

    public Boolean getReceber() {
        Boolean receber = false;
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getMovimento() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                receber = true;
                break;
            }
        }
        return receber;
    }

    public Float getValorTotalMultaDiaria() {
        Float total = new Float(0);
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getSelected()) {
                Integer dias = DataHoje.calculoDosDiasInt(listLocadoraMovimento.get(i).getDtDevolucaoPrevisao(), new Date());
                if (dias > 0) {
                    total += new FunctionsDao().multaDiariaLocadora(listLocadoraMovimento.get(i).getLocadoraLote().getFilial().getId(), listLocadoraMovimento.get(i).getLocadoraLote().getDtLocacao()) * dias;
                }
            }
        }
        return total;
    }

    public String getValorTotalMultaDiariaString() {
        try {
            return Moeda.converteR$Float(getValorTotalMultaDiaria());
        } catch (Exception e) {
            return "0,00";
        }
    }

    public Float getValorTotalReceber() {
        Float total = new Float(0);
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getDtDevolucao() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                total += listLocadoraMovimento.get(i).getMovimento().getValor();
            }
        }
        return total;
    }

    public String getValorTotalReceberString() {
        try {
            return Moeda.converteR$Float(getValorTotalReceber());
        } catch (Exception e) {
            return "0,00";
        }
    }

    public Integer getQuantidadeDevolucaoes() {
        Integer qtde = 0;
        try {
            for (int i = 0; i < listLocadoraMovimento.size(); i++) {
                if (listLocadoraMovimento.get(i).getSelected()) {
                    qtde++;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return qtde;
    }

    public List<Movimento> getListMovimentoPendente() {
        List<Movimento> listMovimento = new ArrayList<>();
        for (int i = 0; i < listLocadoraMovimento.size(); i++) {
            if (listLocadoraMovimento.get(i).getDtDevolucao() != null && listLocadoraMovimento.get(i).getMovimento() != null && listLocadoraMovimento.get(i).getMovimento().getBaixa() == null) {
                Movimento m = new Movimento();
                m = listLocadoraMovimento.get(i).getMovimento();
                m.setValorBaixa(listLocadoraMovimento.get(i).getMovimento().getValor());
                listMovimento.add(m);
            }
        }
        return listMovimento;
    }

    public void put(LocadoraMovimento lm) {
        GenericaSessao.put("fisicaPesquisa", (Fisica) new Dao().find(new Fisica(), lm.getLocadoraLote().getPessoa().getFisica().getId()));
        GenericaSessao.put("locadora_status", status);
        GenericaSessao.put("locadora_movimento", lm);
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public List<SelectItem> getListFiliais() {
        if (listFiliais.isEmpty()) {
            Filial f = MacFilial.getAcessoFilial().getFilial();
            if (f.getId() != -1) {
                if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                    liberaAcessaFilial = true;
                    // ROTINA MENU LOCADORA
                    List<FilialRotina> list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
                    // ID DA FILIAL
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 0) {
                                filial_id = list.get(i).getFilial().getId();
                            }
                            if (Objects.equals(f.getId(), list.get(i).getFilial().getId())) {
                                filial_id = list.get(i).getFilial().getId();
                            }
                            listFiliais.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getFilial().getPessoa().getNome()));
                        }
                    } else {
                        filial_id = f.getId();
                        listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome()));
                    }
                } else {
                    filial_id = f.getId();
                    listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome()));
                }
            }
        }
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public Integer getFilial_id() {
        return filial_id;
    }

    public void setFilial_id(Integer filial_id) {
        this.filial_id = filial_id;
    }
}
