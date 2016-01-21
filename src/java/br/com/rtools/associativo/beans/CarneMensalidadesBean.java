package br.com.rtools.associativo.beans;

import br.com.rtools.financeiro.dao.CarneMensalidadesDao;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.sistema.Mes;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
import br.com.rtools.utilitarios.Jasper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class CarneMensalidadesBean {

    private String idMes;
    private String ano;
    private List listaData;
    private Pessoa pessoa;
    private List<Pessoa> listaPessoa;
    private List<SelectItem> listMeses;

    @PostConstruct
    public void init() {
        DataHoje dh = new DataHoje();
        ano = DataHoje.DataToArrayString(DataHoje.data())[2];
        listaData = new ArrayList();
        pessoa = new Pessoa();
        listaPessoa = new ArrayList();
        listMeses = Mes.loadSelectItem();
        Integer mes_corrent = Integer.parseInt(DataHoje.livre(new Date(), "MM"));
        idMes = DataHoje.DataToArrayString(dh.incrementarMeses(1, DataHoje.data()))[1];
        if (mes_corrent > Integer.parseInt(idMes)) {
            Integer a = Integer.parseInt(ano) + 1;
            ano = "" + a;
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("carneMensalidadesBean");
    }

    public void imprimirCarne() {
        imprimirCarne(false);
    }

    public void imprimirCarne(Boolean todos) {
        String id_pessoa = "";
        if (todos) {
            listaPessoa.clear();
        } else {
            for (int i = 0; i < listaPessoa.size(); i++) {
                if (id_pessoa.length() > 0 && i != listaPessoa.size()) {
                    id_pessoa = id_pessoa + ",";
                }
                id_pessoa = id_pessoa + String.valueOf(listaPessoa.get(i).getId());
            }
        }

        List list = new CarneMensalidadesDao().listaCarneMensalidadesAgrupado((id_pessoa.isEmpty()) ? null : id_pessoa, getDatas());
        Map hash_subreport = new HashMap();
        hash_subreport.put("subreport_file", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CARNE_MENSALIDADES_subreport.jasper"));
        List listServicos = new ArrayList();
        Map mapServicos = new LinkedHashMap();
        Collection listCarnesMensalidade = new ArrayList();
        Integer parcela = 1;
        for (int i = 0; i < list.size(); i++) {
            List carneMensalidades = (List) list.get(i);
            if (carneMensalidades.get(5).equals(DataHoje.converteDateSqlToDate("1900-01-01"))) {
                parcela = 1;
                mapServicos.put("servico", carneMensalidades.get(6).toString());
                mapServicos.put("quantidade", carneMensalidades.get(7).toString());
                listServicos.add(mapServicos);
            } else {
                listCarnesMensalidade.add(
                        new CarneMensalidades(
                                carneMensalidades.get(0),
                                carneMensalidades.get(1),
                                carneMensalidades.get(2),
                                carneMensalidades.get(3),
                                carneMensalidades.get(4),
                                carneMensalidades.get(5),
                                parcela,
                                listServicos
                        )
                );
                parcela++;
                mapServicos = new LinkedHashMap();
                listServicos = new ArrayList();
            }
        }
        Jasper.IS_HEADER = true;
        Jasper.TYPE = "recibo_sem_logo";
        Jasper.printReports("/Relatorios/CARNE_MENSALIDADES.jasper", "Carnê de Mensalidades", listCarnesMensalidade, hash_subreport);
    }

    public void printEtiqueta() {
        printEtiqueta(false);
    }

    public void printEtiqueta(Boolean todos) {
        String id_pessoa = "";
        if (todos) {
            listaPessoa.clear();
        } else {
            for (int i = 0; i < listaPessoa.size(); i++) {
                if (id_pessoa.length() > 0 && i != listaPessoa.size()) {
                    id_pessoa = id_pessoa + ",";
                }
                id_pessoa = id_pessoa + String.valueOf(listaPessoa.get(i).getId());
            }
        }
        List<Etiquetas> c = new ArrayList<>();
        CarneMensalidadesDao cmd = new CarneMensalidadesDao();
        List list = cmd.listaCarneMensalidadesAgrupadoEtiqueta((id_pessoa.isEmpty()) ? null : id_pessoa, getDatas());
        for (Object list1 : list) {
            Etiquetas e;
            try {
                e = new Etiquetas(
                        GenericaString.converterNullToString(((List) list1).get(0)), // Nome
                        GenericaString.converterNullToString(((List) list1).get(1)), // Logradouro
                        GenericaString.converterNullToString(((List) list1).get(2)), // Endereço
                        GenericaString.converterNullToString(((List) list1).get(3)), // Número
                        GenericaString.converterNullToString(((List) list1).get(4)), // Bairro
                        GenericaString.converterNullToString(((List) list1).get(5)), // Cidade
                        GenericaString.converterNullToString(((List) list1).get(6)), // UF
                        GenericaString.converterNullToString(((List) list1).get(7)), // Cep
                        GenericaString.converterNullToString(((List) list1).get(8)), // Complemento
                        "" /// Observação
                );
            } catch (Exception ex) {
                e = new Etiquetas(
                        GenericaString.converterNullToString(((List) list1).get(0)), // Nome
                        GenericaString.converterNullToString(((List) list1).get(1)), // Logradouro
                        GenericaString.converterNullToString(((List) list1).get(2)), // Endereço
                        GenericaString.converterNullToString(((List) list1).get(3)), // Número
                        GenericaString.converterNullToString(((List) list1).get(4)), // Bairro
                        GenericaString.converterNullToString(((List) list1).get(5)), // Cidade
                        GenericaString.converterNullToString(((List) list1).get(6)), // UF
                        GenericaString.converterNullToString(((List) list1).get(7)), // Cep               
                        GenericaString.converterNullToString(((List) list1).get(8)) /// Complemento
                );
            }
            c.add(e);
        }
        if (c.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Jasper.printReports(
                "/Relatorios/ETIQUETAS.jasper",
                "etiquetas",
                (Collection) c
        );
    }

    public String getDatas() {
        String datas = "";
        if (!listaData.isEmpty()) {
            for (int i = 0; i < listaData.size(); i++) {
                if (datas.length() > 0 && i != listaData.size()) {
                    datas = datas + ",";
                }
                datas = datas + "'" + String.valueOf(listaData.get(i)) + "'";
            }
        } else {
            datas = "'" + idMes + "/" + ano + "'";
        }
        return datas;
    }

    public void adicionarData() {
        if (!listaData.isEmpty()) {
            boolean existe = false;
            for (Object data : listaData) {
                if (data.toString().equals(idMes + "/" + ano)) {
                    existe = true;
                }
            }
            if (!existe) {
                listaData.add(idMes + "/" + ano);
            }

        } else {
            listaData.add(idMes + "/" + ano);
        }
    }

    public void adicionarTodasData() {
        if (!listaData.isEmpty()) {
            boolean existe = false;
            for (int w = 1; w <= 12; w++) {
                String mesx = (w < 10) ? "0" + w : "" + w;
                for (int i = 0; i < listaData.size(); i++) {
                    if (listaData.get(i).toString().equals(mesx + "/" + ano)) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    listaData.add(mesx + "/" + ano);
                }
                existe = false;
            }
        } else {
            for (int w = 1; w <= 12; w++) {
                String mesx = (w < 10) ? "0" + w : "" + w;
                listaData.add(mesx + "/" + ano);
            }
        }
    }

    public void removerDataLista(int index) {
        listaData.remove(index);
    }

    public void adicionarPessoa() {
        listaPessoa.add(pessoa);
        pessoa = new Pessoa();
    }

    public void removerPessoaLista(int index) {
        listaPessoa.remove(index);
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
    }

    public String getIdMes() {
        return idMes;
    }

    public void setIdMes(String idMes) {
        this.idMes = idMes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public List getListaData() {
        return listaData;
    }

    public void setListaData(List listaData) {
        this.listaData = listaData;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.getObject("fisicaPesquisa") != null) {
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa")).getPessoa();
            GenericaSessao.remove("fisicaPesquisa");
            adicionarPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public List<SelectItem> getListMeses() {
        return listMeses;
    }

    public void setListMeses(List<SelectItem> listMeses) {
        this.listMeses = listMeses;
    }

    public class CarneMensalidades {

        private Object titular;
        private Object matricula;
        private Object categoria;
        private Object responsavel_id;
        private Object valor;
        private Object vencimento;
        private List list_servicos;
        private Integer parcela;
        private Object servico;
        private Object quantidade;

        public CarneMensalidades() {
            this.titular = "";
            this.matricula = 0;
            this.categoria = "";
            this.responsavel_id = 0;
            this.valor = null;
            this.vencimento = new Date();
            this.list_servicos = new ArrayList();
            this.parcela = 0;
        }

        public CarneMensalidades(Object titular, Object matricula, Object categoria, Object responsavel_id, Object valor, Object vencimento, Integer parcela, List list_servicos) {
            this.titular = titular;
            this.matricula = matricula;
            this.categoria = categoria;
            this.responsavel_id = responsavel_id;
            this.vencimento = vencimento;
            this.valor = valor;
            this.parcela = parcela;
            this.list_servicos = list_servicos;
            this.servico = "";
            this.quantidade = "";
        }

        public CarneMensalidades(Object titular, Object matricula, Object responsavel_id, Object categoria, Object valor, Object vencimento, Integer parcela, Object servico, Object quantidade, List list_servicos) {
            this.titular = titular;
            this.matricula = matricula;
            this.responsavel_id = responsavel_id;
            this.categoria = categoria;
            this.vencimento = vencimento;
            this.valor = valor;
            this.parcela = parcela;
            this.servico = servico;
            this.quantidade = quantidade;
            this.list_servicos = list_servicos;
        }

        public Object getTitular() {
            return titular;
        }

        public void setTitular(Object titular) {
            this.titular = titular;
        }

        public Object getMatricula() {
            return matricula;
        }

        public void setMatricula(Object matricula) {
            this.matricula = matricula;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

        public Object getResponsavel_id() {
            return responsavel_id;
        }

        public void setResponsavel_id(Object responsavel_id) {
            this.responsavel_id = responsavel_id;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public List getList_servicos() {
            return list_servicos;
        }

        public void setList_servicos(List list_servicos) {
            this.list_servicos = list_servicos;
        }

        public Object getServico() {
            return servico;
        }

        public void setServico(Object servico) {
            this.servico = servico;
        }

        public Object getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Object quantidade) {
            this.quantidade = quantidade;
        }

        public Integer getParcela() {
            return parcela;
        }

        public void setParcela(Integer parcela) {
            this.parcela = parcela;
        }

    }

}
// select       se.ds_descricao as servico,  
//     tp.ds_descricao as tipo, 
//     m.ds_referencia, 
//     m.dt_vencimento as vencimento, 
//     func_valor(m.id) as valor, 
//     func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as acrescimo, 
//     m.nr_desconto as desconto, 
//     m.nr_valor+func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as vl_calculado, 
//     bx.dt_baixa, 
//     nr_valor_baixa as valor_pago,  
//     m.ds_es as es, 
//     p.ds_nome as responsavel, 
//     b.ds_nome as beneficiario, 
//     p.id as id_responsavel, 
//     m.id as id_movimento, 
//     m.id_lote as lote, 
//     l.dt_lancamento as criacao,  
//     m.ds_documento as boleto, 
//     func_intervalo_dias(m.dt_vencimento,CURRENT_DATE) as dias_atraso, 
//     func_multa_ass(m.id) as multa,  
//     func_juros_ass(m.id) as juros, 
//     func_correcao_ass(m.id) as correcao, 
//     us.ds_nome as caixa,  
//     m.id_baixa as lote_baixa, 
//     l.ds_documento as documento 
// from fin_movimento as m  
//inner join fin_lote as l on l.id=m.id_lote 
//inner join pes_pessoa as p on p.id=m.id_pessoa 
//inner join pes_pessoa as b on b.id=m.id_beneficiario 
//inner join fin_servicos as se on se.id=m.id_servicos 
//inner join fin_tipo_servico as tp on tp.id=m.id_tipo_servico  
// left join fin_baixa as bx on bx.id=m.id_baixa 
// left join seg_usuario as u on u.id=bx.id_usuario 
// left join pes_pessoa as us on us.id=u.id_pessoa 
// where (m.id_pessoa in (50380) or m.id_beneficiario in (50380)) and m.id_baixa is null and m.is_ativo = true and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) 
// order by m.dt_vencimento asc, p.ds_nome, b.ds_nome, se.ds_descricao 
