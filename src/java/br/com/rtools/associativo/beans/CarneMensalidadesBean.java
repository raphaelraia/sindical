package br.com.rtools.associativo.beans;

import br.com.rtools.financeiro.dao.CarneMensalidadesDao;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.impressao.ParametroCarneMensalidades;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Mes;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

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

        Juridica sindicato = (Juridica) (new Dao()).find(new Juridica(), 1);
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        PessoaEndereco pe = dao.pesquisaEndPorPessoaTipo(1, 2);
        CarneMensalidadesDao db = new CarneMensalidadesDao();
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

        List<Vector> result = db.listaCarneMensalidadesAgrupado((id_pessoa.isEmpty()) ? null : id_pessoa, datas);
        Collection lista = new ArrayList();

        Map hash_subreport = new HashMap();
        hash_subreport.put("subreport_file", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CARNE_MENSALIDADES_subreport.jasper"));
        String logo_sindicato = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");

        for (Vector result1 : result) {
            List<Vector> result_servico = db.listaServicosCarneMensalidades(Integer.valueOf(result1.get(3).toString()), datas);
            List listax = new ArrayList();
            for (Vector result_servico1 : result_servico) {
                Map hash = new HashMap();
                hash.put("servicos", result_servico1.get(0).toString());
                hash.put("quantidade", result_servico1.get(1).toString());
                listax.add(hash);
            }
            List<Vector> result_2 = db.listaCarneMensalidades(Integer.valueOf(result1.get(3).toString()), datas);
            String valor_total = "";
            float soma = 0;
            for (int w = 0; w < result_2.size(); w++) {
                float valor_linha = Moeda.converteFloatR$Float(Float.parseFloat(Double.toString((Double) result_2.get(w).get(5))));
                soma = Moeda.somaValores(soma, valor_linha);
                valor_total = Moeda.converteR$Float(soma);
                lista.add(
                        new ParametroCarneMensalidades(
                                logo_sindicato,
                                sindicato.getPessoa().getNome(),
                                pe.getEndereco().getDescricaoEndereco().getDescricao(),
                                pe.getEndereco().getLogradouro().getDescricao(),
                                pe.getNumero(),
                                pe.getComplemento(),
                                pe.getEndereco().getBairro().getDescricao(),
                                pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5),
                                pe.getEndereco().getCidade().getCidade(),
                                pe.getEndereco().getCidade().getUf(),
                                sindicato.getPessoa().getTelefone1(),
                                sindicato.getPessoa().getEmail1(),
                                sindicato.getPessoa().getSite(),
                                sindicato.getPessoa().getDocumento(),
                                result_2.get(w).get(0).toString(),
                                (result_2.get(w).get(1) != null) ? result_2.get(w).get(1).toString() : "",
                                result_2.get(w).get(3).toString(),
                                (result_2.get(w).get(2) != null) ? result_2.get(w).get(2).toString() : "",
                                w + 1,
                                DataHoje.converteData((Date) result_2.get(w).get(4)),
                                Moeda.converteR$Float(valor_linha),
                                valor_total,
                                listax
                        )
                );
            }
        }

        try {
            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CARNE_MENSALIDADES.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);

            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hash_subreport, dtSource);
            byte[] arquivo = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            res.setContentType("application/pdf");
            res.setHeader("Content-disposition", "inline; filename=\"Carnê de Mensalidades.pdf\"");
            res.getOutputStream().write(arquivo);
            res.getCharacterEncoding();
            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException | IOException e) {
            e.getMessage();
        }
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
        List list = cmd.listaCarneMensalidadesAgrupadoEtiqueta((id_pessoa.isEmpty()) ? null : id_pessoa);
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
