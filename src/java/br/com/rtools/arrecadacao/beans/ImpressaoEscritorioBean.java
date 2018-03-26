/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.ImpressaoEscritorioDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.MovimentoValorBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Windows
 */
@ManagedBean
@SessionScoped
public class ImpressaoEscritorioBean extends MovimentoValorBean implements Serializable {

    private List<SelectItem> listaServico = new ArrayList();
    private Integer indexServico = 0;
    private List<SelectItem> listaTipoServico = new ArrayList();
    private Integer indexTipoServico = 0;
    private String referencia = "";
    private String vencimento = "";
    private Juridica escritorio = new Juridica();

    private Boolean podeListarEmpresa = false;
    private List<ListaEmpresaEscritorio> listaEmpresa = new ArrayList();
    private List<ListaEmpresaEscritorio> listaEmpresaSelecionada = new ArrayList();

    private Boolean modalBoletosVisible = false;

    ListaEmpresaEscritorio obListaEmpresa = null;
    
    public ImpressaoEscritorioBean() {
        loadListaServico();
        loadListaTipoServico();
    }

    @Override
    public void carregarFolha() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void carregarFolha(DataObject valor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void carregarFolha(Object valor) {
        obListaEmpresa = (ListaEmpresaEscritorio) valor;
        
        super.carregarFolha(obListaEmpresa.getMovimento());
    }

    @Override
    public void atualizaValorGrid(String tipo) {
        obListaEmpresa.getMovimento().setValorString(super.atualizaValor(true, tipo));
    }
    
    public void gerarBoleto() {
        if (listaEmpresaSelecionada.isEmpty()) {
            GenericaMensagem.warn("Atenção", "SELECIONE ALGUMA EMPRESA PARA GERAR BOLETOS");
            return;
        }

        modalBoletosVisible = true;
        
    }
    
    public void fecharModalGerarBoleto(){
        modalBoletosVisible = false;
    }

    public void atualizaGeracao() {
        podeListarEmpresa = false;

        listaEmpresa.clear();
        listaEmpresaSelecionada.clear();
    }

    public final void loadListaEmpresa() {
        listaEmpresa.clear();
        listaEmpresaSelecionada.clear();
        
        if (referencia.isEmpty() || vencimento.isEmpty()) {
            GenericaMensagem.error("Atenção", "DIGITE UMA REFERÊNCIA E VENCIMENTO!");
            return;
        }

        if (!new DataHoje().integridadeReferencia(referencia)) {
            GenericaMensagem.error("Atenção", "REFERÊNCIA INVÁLIDA!");
            return;
        }

        ImpressaoEscritorioDao idao = new ImpressaoEscritorioDao();

        List<Object> result = idao.listaEmpresa(escritorio.getId());

        if (result.isEmpty()) {
            GenericaMensagem.error("Atenção", "ESCRITÓRIO SEM EMPRESA VINCULADA!");
            return;
        }

        MensagemConvencaoDao menDB = new MensagemConvencaoDao();
        MovimentoDao finDB = new MovimentoDao();

        Dao dao = new Dao();

        Servicos servico = (Servicos) dao.find(new Servicos(), Integer.valueOf(listaServico.get(indexServico).getDescription()));
        TipoServico tipoServico = (TipoServico) dao.find(new TipoServico(), Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()));

        for (Object ob : result) {
            List linha = ((List) ob);

            MensagemConvencao mc = menDB.retornaDiaString(
                    (Integer) linha.get(1),
                    referencia,
                    Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()),
                    Integer.valueOf(listaServico.get(indexServico).getDescription())
            );

            Boolean podeGerar = true;
            String erros = "";

            // VALIDA MENSAGEM -------------------------------------------------
            Boolean temMensagem = true;
            if (mc == null) {
                temMensagem = false;
                podeGerar = false;
                erros = "| NÃO TEM MENSAGEM |";
            }

            // VALIDA ACORDO ---------------------------------------------------
            List<Movimento> lm_acordado = finDB.listaMovimentoAcordado(
                    (Integer) linha.get(0),
                    referencia,
                    Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()),
                    Integer.valueOf(listaServico.get(indexServico).getDescription())
            );

            if (!lm_acordado.isEmpty()) {
                erros += "| REFERÊNCIA JÁ FOI ACORDADA |";
                podeGerar = false;
            }

            // VALIDA MOVIMENTO ----------------------------------------------------
            List<Movimento> lm = finDB.pesquisaMovimentos(
                    (Integer) linha.get(0),
                    referencia,
                    Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()),
                    Integer.valueOf(listaServico.get(indexServico).getDescription())
            );

            Movimento movimento_gerado;

            if (!lm.isEmpty()) {

                if (lm.size() > 1) {
                    erros += "| MOVIMENTO DUPLICADO NO SISTEMA, CONTATE ADMINISTRADOR |";
                    podeGerar = false;
                }

                if (lm.get(0).getBaixa() != null && lm.get(0).getBaixa().getId() != -1) {
                    erros += "| MOVIMENTO JÁ FOI BAIXADO |";
                    podeGerar = false;
                }

                movimento_gerado = lm.get(0);

            } else {
                Double valor_boleto = Moeda.converteDoubleR$Double(
                        super.carregarValor(
                                Integer.valueOf(listaServico.get(indexServico).getDescription()),
                                Integer.valueOf(listaTipoServico.get(indexTipoServico).getDescription()),
                                referencia,
                                (Integer) linha.get(0)
                        )
                );

                Pessoa pessoa = (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(0));

                movimento_gerado = new Movimento(
                        -1,
                        null,
                        servico.getPlano5(),
                        pessoa,
                        servico,
                        null,
                        tipoServico,
                        null,
                        valor_boleto,
                        referencia,
                        vencimento,
                        1,
                        true,
                        "E",
                        false,
                        pessoa,
                        pessoa,
                        "",
                        "",
                        vencimento,
                        0, 0, 0, 0, 0, 0, 0, (FTipoDocumento) dao.find(new FTipoDocumento(), 2), 0, null
                );
            }

            listaEmpresa.add(
                    new ListaEmpresaEscritorio(
                            (Juridica) dao.find(new Juridica(), (Integer) linha.get(1)),
                            temMensagem,
                            podeGerar,
                            erros,
                            movimento_gerado
                    )
            );

        }
        
        podeListarEmpresa = true;
    }

    public final void loadListaServico() {
        listaServico.clear();
        listaEmpresaSelecionada.clear();

        ServicosDao db = new ServicosDao();
        List<Servicos> result = db.pesquisaTodos(4);
        for (int i = 0; i < result.size(); i++) {
            listaServico.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }

    }

    public final void loadListaTipoServico() {
        listaTipoServico.clear();

        List<Integer> listaIds = new ArrayList();

        listaIds.add(1);
        listaIds.add(2);
        listaIds.add(3);

        TipoServicoDao db = new TipoServicoDao();
        List<TipoServico> result = db.pesquisaTodosComIds(listaIds);

        for (int i = 0; i < result.size(); i++) {
            listaTipoServico.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public List<SelectItem> getListaServico() {
        return listaServico;
    }

    public void setListaServico(List<SelectItem> listaServico) {
        this.listaServico = listaServico;
    }

    public Integer getIndexServico() {
        return indexServico;
    }

    public void setIndexServico(Integer indexServico) {
        this.indexServico = indexServico;
    }

    public List<SelectItem> getListaTipoServico() {
        return listaTipoServico;
    }

    public void setListaTipoServico(List<SelectItem> listaTipoServico) {
        this.listaTipoServico = listaTipoServico;
    }

    public Integer getIndexTipoServico() {
        return indexTipoServico;
    }

    public void setIndexTipoServico(Integer indexTipoServico) {
        this.indexTipoServico = indexTipoServico;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public Juridica getEscritorio() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            escritorio = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return escritorio;
    }

    public void setEscritorio(Juridica escritorio) {
        this.escritorio = escritorio;
    }


    public class ListaEmpresaEscritorio {

        private Juridica empresa;
        private Boolean temMensagem;
        private Boolean podeGerarBoleto;
        private String mensagemErro;
        private Movimento movimento;

        public ListaEmpresaEscritorio(Juridica empresa, Boolean temMensagem, Boolean podeGerarBoleto, String mensagemErro, Movimento movimento) {
            this.empresa = empresa;
            this.temMensagem = temMensagem;
            this.podeGerarBoleto = podeGerarBoleto;
            this.mensagemErro = mensagemErro;
            this.movimento = movimento;
        }

        public Juridica getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Juridica empresa) {
            this.empresa = empresa;
        }

        public Boolean getTemMensagem() {
            return temMensagem;
        }

        public void setTemMensagem(Boolean temMensagem) {
            this.temMensagem = temMensagem;
        }

        public Boolean getPodeGerarBoleto() {
            return podeGerarBoleto;
        }

        public void setPodeGerarBoleto(Boolean podeGerarBoleto) {
            this.podeGerarBoleto = podeGerarBoleto;
        }

        public String getMensagemErro() {
            return mensagemErro;
        }

        public void setMensagemErro(String mensagemErro) {
            this.mensagemErro = mensagemErro;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

    }

    public List<ListaEmpresaEscritorio> getListaEmpresa() {
        return listaEmpresa;
    }

    public void setListaEmpresa(List<ListaEmpresaEscritorio> listaEmpresa) {
        this.listaEmpresa = listaEmpresa;
    }

    public List<ListaEmpresaEscritorio> getListaEmpresaSelecionada() {
        return listaEmpresaSelecionada;
    }

    public void setListaEmpresaSelecionada(List<ListaEmpresaEscritorio> listaEmpresaSelecionada) {
        this.listaEmpresaSelecionada = listaEmpresaSelecionada;
    }

    public Boolean getModalBoletosVisible() {
        return modalBoletosVisible;
    }

    public void setModalBoletosVisible(Boolean modalBoletosVisible) {
        this.modalBoletosVisible = modalBoletosVisible;
    }

    public Boolean getPodeListarEmpresa() {
        return podeListarEmpresa;
    }

    public void setPodeListarEmpresa(Boolean podeListarEmpresa) {
        this.podeListarEmpresa = podeListarEmpresa;
    }
}
