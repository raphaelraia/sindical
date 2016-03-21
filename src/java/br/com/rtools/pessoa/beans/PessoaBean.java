package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.beans.RaisBean;
import br.com.rtools.arrecadacao.beans.WebREPISBean;
import br.com.rtools.associativo.beans.CupomMovimentoBean;
import br.com.rtools.associativo.beans.FrequenciaCatracaBean;
import br.com.rtools.associativo.beans.SorteioMovimentoBean;
import br.com.rtools.cobranca.beans.TmktHistoricoBean;
import br.com.rtools.digitalizacao.beans.DigitalizacaoBean;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.db.PessoaDB;
import br.com.rtools.pessoa.db.PessoaDBToplink;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DaoInterface;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.SelectItemSort;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class PessoaBean implements Serializable {

    private Pessoa pessoa;
    private Fisica fisica;
    private Juridica juridica;
    private String descPesquisa;
    private String porPesquisa;
    private String comoPesquisa;
    private String masc;
    private String maxl;
    private List<Pessoa> listaPessoa;
    private List<SelectItem> listSelectDetalhes;
    private String selectDetalhes;
    private String tipoPessoa;

    @PostConstruct
    public void init() {
        pessoa = new Pessoa();
        fisica = new Fisica();
        juridica = new Juridica();
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        masc = "";
        maxl = "";
        listaPessoa = new ArrayList();
        listSelectDetalhes = new ArrayList();
        selectDetalhes = "";
        if (GenericaSessao.exists("tipoPessoa")) {
            tipoPessoa = GenericaSessao.getString("tipoPessoa", true);
            if (tipoPessoa.equals("pessoaFisica")) {
                // JURÍDICA BEAN PODE ESTA NA SESSÃO, O LINK VEIO DA FÍSICA
                GenericaSessao.remove("juridicaBean");
                fisica = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getFisica();
                pessoa = fisica.getPessoa();
            }
            if (tipoPessoa.equals("pessoaJuridica")) {
                // FÍSICA BEAN PODE ESTA NA SESSÃO, O LINK VEIO DA JURÍDICA
                GenericaSessao.remove("fisicaBean");
                juridica = ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).getJuridica();
                pessoa = juridica.getPessoa();
            }
            loadListSelectDetalhes();
            load();
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("poessoaPesquisa");
        GenericaSessao.remove("pessoaBean");
        GenericaSessao.remove("sorteioMovimentoBean");
        GenericaSessao.remove("cupomMovimentoBean");
        GenericaSessao.remove("tmktHistoricoBean");
        GenericaSessao.remove("digitalizacaoBean");
        GenericaSessao.remove("frequenciaCatracaBean");
        GenericaSessao.remove("spcBean");
        GenericaSessao.remove("webREPISBean");
    }

    public void load() {
        GenericaSessao.remove("sorteioMovimentoBean");
        GenericaSessao.remove("cupomMovimentoBean");
        GenericaSessao.remove("tmktHistoricoBean");
        GenericaSessao.remove("digitalizacaoBean");
        GenericaSessao.remove("frequenciaCatracaBean");
        GenericaSessao.remove("spcBean");
        GenericaSessao.remove("webREPISBean");
        GenericaSessao.remove("raisBean");
        switch (selectDetalhes) {
            case "sorteios":
                SorteioMovimentoBean sorteioMovimentoBean = new SorteioMovimentoBean();
                sorteioMovimentoBean.loadListSorteioMovimento(pessoa.getId());
                GenericaSessao.put("sorteioMovimentoBean", sorteioMovimentoBean);
                break;
            case "cupons":
                CupomMovimentoBean cupomMovimentoBean = new CupomMovimentoBean();
                cupomMovimentoBean.loadListCupomMovimento(pessoa.getId());
                GenericaSessao.put("cupomMovimentoBean", cupomMovimentoBean);
                break;
            case "telemarketing":
                TmktHistoricoBean tmktHistoricoBean = new TmktHistoricoBean();
                tmktHistoricoBean.loadListTmktHistorico(pessoa.getId());
                GenericaSessao.put("tmktHistoricoBean", tmktHistoricoBean);
                break;
            case "documentos":
                DigitalizacaoBean digitalizacaoBean = new DigitalizacaoBean();
                digitalizacaoBean.loadListDocumentos(pessoa.getId());
                GenericaSessao.put("digitalizacaoBean", digitalizacaoBean);
                break;
            case "frequencia_catraca":
                FrequenciaCatracaBean frequenciaCatracaBean = new FrequenciaCatracaBean();
                frequenciaCatracaBean.loadListByPessoa(pessoa.getId());
                GenericaSessao.put("frequenciaCatracaBean", frequenciaCatracaBean);
                break;
            case "spc":
                SpcBean spcBean = new SpcBean();
                spcBean.setListaSPC(new ArrayList());
                spcBean.loadListByPessoa(pessoa.getId());
                GenericaSessao.put("spcBean", spcBean);
                break;
            case "repis":
                WebREPISBean webREPISBean = new WebREPISBean();
                webREPISBean.loadListRepisMovimentoPessoa(pessoa.getId());
                GenericaSessao.put("webREPISBean", webREPISBean);
                break;
            case "rais":
                RaisBean raisBean = new RaisBean();
                if (tipoPessoa.equals("pessoaJuridica")) {
                    raisBean.loadListRaisEmpresa(pessoa.getId());
                }
                if (tipoPessoa.equals("pessoaFisica")) {
                    if (!pessoa.getDocumento().isEmpty()) {
                        raisBean.loadListRaisPessoa(pessoa.getDocumento());
                    }
                }
                GenericaSessao.put("raisBean", raisBean);
                break;
        }
    }

    public void loadListaPessoa() {
        listaPessoa.clear();
        PessoaDB pesquisa = new PessoaDBToplink();
        if (!descPesquisa.isEmpty()) {
            listaPessoa = pesquisa.pesquisarPessoa(descPesquisa, porPesquisa, comoPesquisa);
        }
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getMasc() {
        return masc;
    }

    public void setMasc(String masc) {
        this.masc = masc;
    }

    public String salvar() {
        DaoInterface di = new Dao();
        if (pessoa.getId() == -1) {
            di.openTransaction();
            if (di.save(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        } else {
            di.openTransaction();
            if (di.update(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        }
        return null;
    }

    public String novo() {
        setPessoa(pessoa = new Pessoa());
        return "pessoa";
    }

    public String excluir() {
        DaoInterface di = new Dao();
        if (pessoa.getId() != -1) {
            di.openTransaction();
            if (di.delete(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        }
        setPessoa(pessoa = new Pessoa());
        return "pesquisaPessoa";
    }

    public void CarregarPessoa() {
    }

    public void refreshForm() {
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        loadListaPessoa();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        loadListaPessoa();
    }

    public String pesquisarPessoa() {
        GenericaSessao.put("urlRetorno", "agenda");
        return "pesquisaPessoa";
    }

    public synchronized String editar(Pessoa p) {
        pessoa = p;
        GenericaSessao.put("pessoaPesquisa", pessoa);
        GenericaSessao.put("linkClicado", true);
        pessoa = new Pessoa();
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        if (GenericaSessao.exists("urlRetorno")) {
            return GenericaSessao.getString("urlRetorno");
        } else {
            return null;
        }
    }

    public String getColocarMascara() {
        if (porPesquisa.equals("telefone1")) {
            masc = "telefone";
        } else {
            masc = "";
        }
        return masc;
    }

    public String getColocarMaxlenght() {
        if (porPesquisa.equals("telefone1")) {
            maxl = "14";
        } else {
            maxl = "50";
        }
        return maxl;
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public String getMascaraPesquisa() {
        return Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public void limparMascara() {
        descPesquisa = "";
    }

    public List<SelectItem> getListSelectDetalhes() {
        return listSelectDetalhes;
    }

    public void setListSelectDetalhes(List<SelectItem> listSelectDetalhes) {
        this.listSelectDetalhes = listSelectDetalhes;
    }

    public void loadListSelectDetalhes() {
        ControleAcessoBean cab = new ControleAcessoBean();
        if (tipoPessoa.isEmpty()) {
            return;
        }
        // PESSOA FÍSICA
        if (tipoPessoa.equals("pessoaFisica")) {
            listSelectDetalhes.add(new SelectItem("cupons", "Cupons", "CONSULTA CUPONS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_cupons", 4)));
            listSelectDetalhes.add(new SelectItem("sorteios", "Sorteios", "CONSULTA SORTEIOS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_sorteios", 4)));
            listSelectDetalhes.add(new SelectItem("historico_fisica", "Histórico", "CONSULTA HISTÓRICO (PESSOA FÍSICA)", cab.verificarPermissao("consulta_historico_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("oposicoes", "Oposições", "CONSULTA OPOSIÇÃO (PESSOA FÍSICA)", cab.verificarPermissao("consulta_oposicao_pessoa_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("movimentos_fisica", "Movimentos", "CONSULTA MOVIMENTOS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_movimentos_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("frequencia_catraca", "Frequência Catraca", "CONSULTA FREQUÊNCIA CATRACA", cab.verificarPermissao("consulta_frequencia_catraca", 4)));
        }
        // PESSOA JURÍDICA
        if (tipoPessoa.equals("pessoaJuridica")) {
            listSelectDetalhes.add(new SelectItem("repis", "Repis", "CONSULTA REPIS (PESSOA JURÍDICA)", cab.verificarPermissao("consulta_repis", 4)));
        }
        // PESSOA FÍSICA E JURÍDICA
        if (tipoPessoa.equals("pessoaFisica") || tipoPessoa.equals("pessoaJuridica")) {
            listSelectDetalhes.add(new SelectItem("telemarketing", "Telemarketing", "CONSULTA ATENDIMENTOS TELEMARKETING (PESSOA FÍSICA E JURÍDICA)", cab.verificarPermissao("consulta_telemarketing", 4)));
            listSelectDetalhes.add(new SelectItem("documentos", "Documentos", "CONSULTA DOCUMENTOS (PESSOA FÍSICA E JURÍDICA)", cab.verificarPermissao("consulta_documentos", 4)));
            listSelectDetalhes.add(new SelectItem("spc", "SPC", "CONSULTA PESSOA SPC", cab.verificarPermissao("consulta_pessoa_spc", 4)));
            listSelectDetalhes.add(new SelectItem("rais", "RAIS", "CONSULTA RAIS", cab.verificarPermissao("consulta_rais", 4)));
        }
        if (!listSelectDetalhes.isEmpty()) {
            SelectItemSort.sort(listSelectDetalhes);
            int y = 0;
            selectDetalhes = "";
            for (int i = 0; i < listSelectDetalhes.size(); i++) {
                if (!listSelectDetalhes.get(i).isDisabled()) {
                    if (y == 0) {
                        selectDetalhes = "" + listSelectDetalhes.get(i).getValue();
                        break;
                    }
                }

            }
        }
    }

    public String getSelectDetalhes() {
        return selectDetalhes;
    }

    public void setSelectDetalhes(String selectDetalhes) {
        this.selectDetalhes = selectDetalhes;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }
}
