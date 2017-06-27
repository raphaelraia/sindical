package br.com.rtools.arrecadacao.beans;

import br.com.rtools.associativo.dao.PesquisaBoletosSocialDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PesquisaBoletosSocialBean {

    private String descricaoPesquisa = "";
    private List<DataObject> lista = new ArrayList();
    //private List<MovimentoBoleto> listaMovimentoBoleto = new ArrayList();
    private List<LinhaBoleto> listaMovimentoBoleto = new ArrayList();
    private String tipoPesquisa = "boleto_anterior";
    private String placeholder = "DIGITE UM NÚMERO DE BOLETO ANTERIOR";

    // NÃO UTILIZAVEL POR ENQUANTO
//    @PostConstruct
//    public void init(){
//    
//    }
    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("pesquisaBoletosSocialBean");
    }

    public void alterarPlaceholder() {
        if (tipoPesquisa.equals("boleto_atual")) {
            placeholder = "DIGITE UM NÚMERO DE BOLETO ATUAL";
        } else if (tipoPesquisa.equals("boleto_anterior")) {
            placeholder = "DIGITE UM NÚMERO DE BOLETO ANTERIOR";
        } else if (tipoPesquisa.equals("beneficiario")) {
            placeholder = "DIGITE O NOME DO BENEFICIÁRIO";
        } else if (tipoPesquisa.equals("codigo")) {
            placeholder = "DIGITE O CÓDIGO DO BENEFICIÁRIO";
        }
    }

    public void voltarBoletos() {
        if (listaMovimentoBoleto.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Lista de Movimentos vazia!");
            return;
        }

        for (LinhaBoleto lb : listaMovimentoBoleto) {
            if (!lb.getQuitacao().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Existem Movimentos Baixados!");
                return;
            }
        }
        
        Dao dao = new Dao();

        // PEGA O PRIMEIRO BOLETO POIS SERÃO TODOS IGUAIS E NÃO TEM NECESSIDADE DE COLOCAR NO LOPPING
        Boleto b = (Boleto) dao.find(new Boleto(), listaMovimentoBoleto.get(0).getId_boleto());
        
        dao.openTransaction();
        for (LinhaBoleto lb : listaMovimentoBoleto) {
            Movimento m = (Movimento) dao.find(new Movimento(), lb.getId_movimento());
            
            m.setDocumento(b.getBoletoComposto());
            m.setNrCtrBoleto(b.getNrCtrBoleto());
            
            if (!dao.update(m)){
                GenericaMensagem.error("Erro", "Não foi possível atualizar Boleto!");
                dao.rollback();
                return;
            }
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "Movimentos atualizados!");
        loadList();
    }

    public void loadList() {
        lista.clear();
        listaMovimentoBoleto.clear();

        if (descricaoPesquisa.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite uma pesquisa!");
            return;
        }

        //listaMovimentoBoleto = new PesquisaBoletosSocialDao().listaMovimentoBoleto(tipoPesquisa, descricaoPesquisa);
        List<Object> result = new PesquisaBoletosSocialDao().listaMovimentoBoleto(tipoPesquisa, descricaoPesquisa);

        for (Object linha : result) {
            listaMovimentoBoleto.add(
                    new LinhaBoleto(
                            (Integer) ((List) linha).get(0), // id_titular
                            (String) ((List) linha).get(1), // nome_titular
                            (Integer) ((List) linha).get(2), // id_beneficiario
                            (String) ((List) linha).get(3), // nome_beneficiario
                            (Integer) ((List) linha).get(4), // id_servico
                            (String) ((List) linha).get(5), // nome_servico
                            ((Double) ((List) linha).get(6)).floatValue(), // valor
                            DataHoje.converteData( (Date) ((List) linha).get(7) ), // vencimento
                            (String) ((List) linha).get(8), // boleto atual
                            (String) ((List) linha).get(9), // boleto anterior
                            DataHoje.converteData( (Date) ((List) linha).get(10) ), // quitacao
                            (Integer) ((List) linha).get(11), // id_movimento (atual)
                            (Integer) ((List) linha).get(12), // id_boleto (anterior)
                            (ContaCobranca) new Dao().find(new ContaCobranca(), (Integer) ((List) linha).get(13)) // id_conta_cobranca
                    )
            );
        }

        if (listaMovimentoBoleto.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum Boleto encontrado!");
        }
    }

    public List<DataObject> getLista() {
        return lista;
    }

    public void setLista(List<DataObject> lista) {
        this.lista = lista;
    }

    public List<LinhaBoleto> getListaMovimentoBoleto() {
        return listaMovimentoBoleto;
    }

    public void setListaMovimentoBoleto(List<LinhaBoleto> listaMovimentoBoleto) {
        this.listaMovimentoBoleto = listaMovimentoBoleto;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public class LinhaBoleto {

        private Integer id_titular;
        private String nome_titular;
        private Integer id_beneficiario;
        private String nome_beneficiario;
        private Integer id_servico;
        private String nome_servico;
        private double valor;
        private String vencimento;
        private String boleto_atual;
        private String boleto_anterior;
        private String quitacao;
        private Integer id_movimento;
        private Integer id_boleto;
        private ContaCobranca contaCobranca;

        public LinhaBoleto(Integer id_titular, String nome_titular, Integer id_beneficiario, String nome_beneficiario, Integer id_servico, String nome_servico, double valor, String vencimento, String boleto_atual, String boleto_anterior, String quitacao, Integer id_movimento, Integer id_boleto, ContaCobranca contaCobranca) {
            this.id_titular = id_titular;
            this.nome_titular = nome_titular;
            this.id_beneficiario = id_beneficiario;
            this.nome_beneficiario = nome_beneficiario;
            this.id_servico = id_servico;
            this.nome_servico = nome_servico;
            this.valor = valor;
            this.vencimento = vencimento;
            this.boleto_atual = boleto_atual;
            this.boleto_anterior = boleto_anterior;
            this.quitacao = quitacao;
            this.id_movimento = id_movimento;
            this.id_boleto = id_boleto;
            this.contaCobranca = contaCobranca;
        }

        public Integer getId_titular() {
            return id_titular;
        }

        public void setId_titular(Integer id_titular) {
            this.id_titular = id_titular;
        }

        public String getNome_titular() {
            return nome_titular;
        }

        public void setNome_titular(String nome_titular) {
            this.nome_titular = nome_titular;
        }

        public Integer getId_beneficiario() {
            return id_beneficiario;
        }

        public void setId_beneficiario(Integer id_beneficiario) {
            this.id_beneficiario = id_beneficiario;
        }

        public String getNome_beneficiario() {
            return nome_beneficiario;
        }

        public void setNome_beneficiario(String nome_beneficiario) {
            this.nome_beneficiario = nome_beneficiario;
        }

        public Integer getId_servico() {
            return id_servico;
        }

        public void setId_servico(Integer id_servico) {
            this.id_servico = id_servico;
        }

        public String getNome_servico() {
            return nome_servico;
        }

        public void setNome_servico(String nome_servico) {
            this.nome_servico = nome_servico;
        }

        public double getValor() {
            return valor;
        }

        public void setValor(double valor) {
            this.valor = valor;
        }
        
        public String getValorString() {
            return Moeda.converteR$Double(valor);
        }

        public void setValor(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

        public String getVencimento() {
            return vencimento;
        }

        public void setVencimento(String vencimento) {
            this.vencimento = vencimento;
        }

        public String getBoleto_atual() {
            return boleto_atual;
        }

        public void setBoleto_atual(String boleto_atual) {
            this.boleto_atual = boleto_atual;
        }

        public String getBoleto_anterior() {
            return boleto_anterior;
        }

        public void setBoleto_anterior(String boleto_anterior) {
            this.boleto_anterior = boleto_anterior;
        }

        public String getQuitacao() {
            return quitacao;
        }

        public void setQuitacao(String quitacao) {
            this.quitacao = quitacao;
        }

        public Integer getId_movimento() {
            return id_movimento;
        }

        public void setId_movimento(Integer id_movimento) {
            this.id_movimento = id_movimento;
        }

        public Integer getId_boleto() {
            return id_boleto;
        }

        public void setId_boleto(Integer id_boleto) {
            this.id_boleto = id_boleto;
        }

        public ContaCobranca getContaCobranca() {
            return contaCobranca;
        }

        public void setContaCobranca(ContaCobranca contaCobranca) {
            this.contaCobranca = contaCobranca;
        }

    }

}
