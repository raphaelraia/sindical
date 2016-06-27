package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.dao.AcordoComissaoDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class FechamentoComissaoAcordoBean {

    private List<SelectItem> listaData;
    private String dataFechamento;
    private Acordo acordo;
    private Map<String, Integer> listUsuario;
    private List selectedUsuario;
    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    @PostConstruct
    public void init() {
        Jasper jasper = new Jasper();
        jasper.init();
        listaData = new ArrayList();
        dataFechamento = "";
        acordo = new Acordo();
        loadUsuariosAcordo();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("fechamentoComissaoAcordoBean");
    }

    public List<SelectItem> getListaData() {
        if (listaData.isEmpty()) {
            int i = 0;
            List<Date> select = new AcordoComissaoDao().pesquisaTodosFechamento();
            if (select != null) {
                while (i < select.size()) {
                    listaData.add(new SelectItem(DataHoje.converteData(select.get(i)), DataHoje.converteData(select.get(i))));
                    i++;
                }
            }
        }
        return listaData;
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        Rotina r = new Rotina().get();
        List<Relatorios> list = new ArrayList<>();
        if (r != null) {
            list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
        }
        Integer idDefault = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelatorio = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                idDefault = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
        if (idDefault != null) {
            idRelatorio = idDefault;
        }
        loadRelatorioOrdem();
    }

    public void loadRelatorioOrdem() {
        if (idRelatorio != null) {
            listRelatorioOrdem = new ArrayList();
            List<RelatorioOrdem> list = new RelatorioOrdemDao().findAllByRelatorio(idRelatorio);
            Integer idDefault = null;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                if (list.get(i).getPrincipal()) {
                    idDefault = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            if (idDefault != null) {
                idRelatorioOrdem = idDefault;
            }
        }
    }

    public void loadUsuariosAcordo() {
        listUsuario = new LinkedHashMap<>();
        selectedUsuario = new ArrayList();
        List<Usuario> list = new AcordoComissaoDao().listaUsuariosAgrupados(dataFechamento);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listUsuario.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
                selectedUsuario.add(list.get(i).getId());
            }
        }
    }

    public String inIdUsuarios() {
        String ids = null;
        if (selectedUsuario != null) {
            for (int i = 0; i < selectedUsuario.size(); i++) {
                if (selectedUsuario.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedUsuario.get(i);
                    } else {
                        ids += "," + selectedUsuario.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public synchronized void processar() {
        if (new AcordoComissaoDao().inserirAcordoComissao()) {
            listaData.clear();
            GenericaMensagem.info("Sucesso", "Concluído com sucesso");
        } else {
            GenericaMensagem.warn("Erro", "Ao gerar comissão!");

        }
    }

    public void visualizar() {
        if (!listaData.isEmpty()) {
            AcordoComissaoDao acd = new AcordoComissaoDao();
            acd.setRelatorios(getRelatorios());
            acd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
            List result = acd.listaAcordoComissao(dataFechamento, inIdUsuarios());
            Collection c = new ArrayList();
            BigDecimal repasse;
            BigDecimal liquido;
            BigDecimal comissao;
            BigDecimal valor;
            BigDecimal taxa;
            for (Object result1 : result) {
                List o = (List) result1;
                valor = new BigDecimal(Double.valueOf(((List) result1).get(5).toString()));
                taxa = new BigDecimal(Double.valueOf(((List) result1).get(6).toString()));
                repasse = new BigDecimal(Double.valueOf(((List) result1).get(7).toString()));
                repasse = (valor.subtract(taxa).multiply(repasse)).divide(new BigDecimal(100));
                liquido = valor.subtract(taxa).subtract(repasse);
                comissao = valor.subtract(taxa).subtract(repasse).multiply(new BigDecimal(0.015));
                c.add(
                        new AcordoAnalitico(
                                o.get(0),
                                o.get(1),
                                o.get(2),
                                o.get(3),
                                o.get(4),
                                valor, // o.get(5)
                                taxa, // o.get(6)
                                repasse, // o.get(7)
                                o.get(8),
                                o.get(9),
                                o.get(10),
                                o.get(11),
                                o.get(12),
                                o.get(13),
                                comissao, // o.get(14)
                                liquido, //  // o.get(15)
                                o.get(16)
                        )
                );
            }
            Relatorios r = getRelatorios();
            Jasper.TITLE = r.getNome();
            Jasper.IS_HEADER = true;
            Jasper.TYPE = "default";
            Jasper.printReports(r.getJasper(), r.getNome(), (Collection) c);
        }
    }

    public void estornar() {
        if (!listaData.isEmpty()) {
            AcordoComissaoDao acordoComissaoDB = new AcordoComissaoDao();
            if (acordoComissaoDB.estornarAcordoComissao(dataFechamento)) {
                GenericaMensagem.info("Sucesso", "Fechamento de acordo estornado");
            } else {
                GenericaMensagem.warn("Erro", "Ao estornar fechamento!");
            }
            listaData = new ArrayList();
        } else {
            GenericaMensagem.warn("Validação", "Data de Fechamento vazia!");
        }
    }

    public void setListaData(List<SelectItem> listaData) {
        this.listaData = listaData;
    }

    public String getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(String dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Acordo getAcordo() {
        return acordo;
    }

    public void setAcordo(Acordo acordo) {
        this.acordo = acordo;
    }

    public Map<String, Integer> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(Map<String, Integer> listUsuario) {
        this.listUsuario = listUsuario;
    }

    public List getSelectedUsuario() {
        return selectedUsuario;
    }

    public void setSelectedUsuario(List selectedUsuario) {
        this.selectedUsuario = selectedUsuario;
    }

    // GETTERS AND SETTERS
    public List<SelectItem> getListRelatorios() {
        return listRelatorio;
    }

    public void setListRelatorios(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public class AcordoAnalitico {

        private Object cnpj;
        private Object empresa;
        private Object acordo_id;
        private Object boleto;
        private Object contribuicao;
        private Object valor_recebido;
        private Object taxa;
        private Object repasse;
        private Object data_importacao;
        private Object data_recebimento;
        private Object data_fechamento;
        private Object data_vencimento;
        private Object data_inicio;
        private Object data_emissao;
        private Object comissao;
        private Object liquido;
        private Object usuario_nome;

        public AcordoAnalitico() {
            this.cnpj = null;
            this.empresa = null;
            this.acordo_id = null;
            this.boleto = null;
            this.contribuicao = null;
            this.valor_recebido = null;
            this.taxa = null;
            this.repasse = null;
            this.data_importacao = null;
            this.data_recebimento = null;
            this.data_fechamento = null;
            this.data_vencimento = null;
            this.data_inicio = null;
            this.data_emissao = null;
            this.comissao = null;
            this.liquido = null;
            this.usuario_nome = null;
        }

        /**
         *
         * @param cnpj
         * @param empresa
         * @param acordo_id
         * @param boleto
         * @param contribuicao
         * @param valor_recebido
         * @param taxa
         * @param repasse
         * @param data_importacao
         * @param data_recebimento
         * @param data_fechamento
         * @param data_vencimento
         * @param data_inicio
         * @param data_emissao
         * @param comissao
         * @param liquido
         * @param usuario_nome
         */
        public AcordoAnalitico(Object cnpj, Object empresa, Object acordo_id, Object boleto, Object contribuicao, Object valor_recebido, Object taxa, Object repasse, Object data_importacao, Object data_recebimento, Object data_fechamento, Object data_vencimento, Object data_inicio, Object data_emissao, Object comissao, Object liquido, Object usuario_nome) {
            this.cnpj = cnpj;
            this.empresa = empresa;
            this.acordo_id = acordo_id;
            this.boleto = boleto;
            this.contribuicao = contribuicao;
            this.valor_recebido = valor_recebido;
            this.taxa = taxa;
            this.repasse = repasse;
            this.data_importacao = data_importacao;
            this.data_recebimento = data_recebimento;
            this.data_fechamento = data_fechamento;
            this.data_vencimento = data_vencimento;
            this.data_inicio = data_inicio;
            this.data_emissao = data_emissao;
            this.comissao = comissao;
            this.liquido = liquido;
            this.usuario_nome = usuario_nome;
        }

        public Object getCnpj() {
            return cnpj;
        }

        public void setCnpj(Object cnpj) {
            this.cnpj = cnpj;
        }

        public Object getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Object empresa) {
            this.empresa = empresa;
        }

        public Object getAcordo_id() {
            return acordo_id;
        }

        public void setAcordo_id(Object acordo_id) {
            this.acordo_id = acordo_id;
        }

        public Object getBoleto() {
            return boleto;
        }

        public void setBoleto(Object boleto) {
            this.boleto = boleto;
        }

        public Object getContribuicao() {
            return contribuicao;
        }

        public void setContribuicao(Object contribuicao) {
            this.contribuicao = contribuicao;
        }

        public Object getValor_recebido() {
            return valor_recebido;
        }

        public void setValor_recebido(Object valor_recebido) {
            this.valor_recebido = valor_recebido;
        }

        public Object getTaxa() {
            return taxa;
        }

        public void setTaxa(Object taxa) {
            this.taxa = taxa;
        }

        public Object getRepasse() {
            return repasse;
        }

        public void setRepasse(Object repasse) {
            this.repasse = repasse;
        }

        public Object getData_importacao() {
            return data_importacao;
        }

        public void setData_importacao(Object data_importacao) {
            this.data_importacao = data_importacao;
        }

        public Object getData_recebimento() {
            return data_recebimento;
        }

        public void setData_recebimento(Object data_recebimento) {
            this.data_recebimento = data_recebimento;
        }

        public Object getData_fechamento() {
            return data_fechamento;
        }

        public void setData_fechamento(Object data_fechamento) {
            this.data_fechamento = data_fechamento;
        }

        public Object getData_vencimento() {
            return data_vencimento;
        }

        public void setData_vencimento(Object data_vencimento) {
            this.data_vencimento = data_vencimento;
        }

        public Object getData_inicio() {
            return data_inicio;
        }

        public void setData_inicio(Object data_inicio) {
            this.data_inicio = data_inicio;
        }

        public Object getData_emissao() {
            return data_emissao;
        }

        public void setData_emissao(Object data_emissao) {
            this.data_emissao = data_emissao;
        }

        public Object getComissao() {
            return comissao;
        }

        public void setComissao(Object comissao) {
            this.comissao = comissao;
        }

        public Object getLiquido() {
            return liquido;
        }

        public void setLiquido(Object liquido) {
            this.liquido = liquido;
        }

        public Object getUsuario_nome() {
            return usuario_nome;
        }

        public void setUsuario_nome(Object usuario_nome) {
            this.usuario_nome = usuario_nome;
        }

    }
}
