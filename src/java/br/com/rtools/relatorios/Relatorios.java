package br.com.rtools.relatorios;

import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.*;
import javax.servlet.ServletContext;

@Entity
@Table(name = "sis_relatorios")
@NamedQueries({
    @NamedQuery(name = "Relatorios.pesquisaID", query = "SELECT R FROM Relatorios R WHERE R.id = :pid")
    ,
    @NamedQuery(name = "Relatorios.findAll", query = "SELECT R FROM Relatorios AS R ORDER BY R.nome ASC")
})
public class Relatorios implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "ds_nome", length = 100, nullable = false)
    private String nome;
    @Column(name = "ds_jasper", length = 50, nullable = false)
    private String jasper;
    @Column(name = "ds_qry", length = 1000)
    private String qry;
    @Column(name = "ds_qry_ordem", length = 1000)
    private String qryOrdem;
    @Column(name = "is_por_folha", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean porFolha;
    @Column(name = "ds_nome_grupo", length = 100)
    private String nomeGrupo;
    @Column(name = "is_excel", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean excel;
    @Column(name = "ds_campos_excel", length = 255)
    private String camposExcel;
    @Column(name = "is_monta_query_string", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean montaQuery;
    @Column(name = "ds_query_string")
    private String queryString;
    @Column(name = "is_default", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean principal;
    @JoinColumn(name = "id_relatorio_tipo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private RelatorioTipo relatorioTipo;

    public Relatorios() {
        this.id = -1;
        this.rotina = new Rotina();
        this.nome = "";
        this.jasper = "";
        this.qry = "";
        this.qryOrdem = "";
        this.porFolha = false;
        this.nomeGrupo = "";
        this.excel = false;
        this.camposExcel = "";
        this.montaQuery = false;
        this.queryString = "";
        this.principal = false;
        this.relatorioTipo = null;
    }

    public Relatorios(Integer id, Rotina rotina, String nome, String jasper, String qry, Boolean porFolha, String nomeGrupo, Boolean excel, String camposExcel, Boolean montaQuery, String queryString, Boolean principal, RelatorioTipo relatorioTipo) {
        this.id = id;
        this.rotina = rotina;
        this.nome = nome;
        this.jasper = jasper;
        this.qry = qry;
        this.porFolha = porFolha;
        this.nomeGrupo = nomeGrupo;
        this.excel = excel;
        this.camposExcel = camposExcel;
        this.montaQuery = montaQuery;
        this.queryString = queryString;
        this.principal = principal;
        this.relatorioTipo = relatorioTipo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getJasper() {
        return jasper;
    }

    public void setJasper(String jasper) {
        this.jasper = jasper;
    }

    public String getQry() {
        return qry;
    }

    public void setQry(String qry) {
        this.qry = qry;
    }

    public String getQryOrdem() {
        return qryOrdem;
    }

    public void setQryOrdem(String qryOrdem) {
        this.qryOrdem = qryOrdem;
    }

    public Boolean getPorFolha() {
        return porFolha;
    }

    public void setPorFolha(Boolean porFolha) {
        this.porFolha = porFolha;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public Boolean getExcel() {
        return excel;
    }

    public void setExcel(Boolean excel) {
        this.excel = excel;
    }

    public String getCamposExcel() {
        return camposExcel;
    }

    public void setCamposExcel(String camposExcel) {
        this.camposExcel = camposExcel;
    }

    public Boolean getMontaQuery() {
        return montaQuery;
    }

    public void setMontaQuery(Boolean montaQuery) {
        this.montaQuery = montaQuery;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public RelatorioTipo getRelatorioTipo() {
        return relatorioTipo;
    }

    public void setRelatorioTipo(RelatorioTipo relatorioTipo) {
        this.relatorioTipo = relatorioTipo;
    }

    @Override
    public String toString() {
        return "Relatorios{" + "id=" + id + ", rotina=" + rotina + ", nome=" + nome + ", jasper=" + jasper + ", qry=" + qry + ", qryOrdem=" + qryOrdem + ", porFolha=" + porFolha + ", nomeGrupo=" + nomeGrupo + ", excel=" + excel + ", camposExcel=" + camposExcel + ", montaQuery=" + montaQuery + ", queryString=" + queryString + ", principal=" + principal + ", relatorioTipo=" + relatorioTipo + '}';
    }

    public List<SelectItem> loadListRelatorios() {
        return loadListRelatorios(new Rotina().get().getId());
    }

    public List<SelectItem> loadListRelatorios(Integer rotina_id) {
        if (rotina_id == null) {
            return new ArrayList();
        }
        List<SelectItem> listRelatorios = new ArrayList();
        List<Relatorios> list = new RelatorioDao().findByRotina(rotina_id);
        Integer mainRotina = null;
        for (int i = 0; i < list.size(); i++) {
            listRelatorios.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), (mainRotina != null ? (mainRotina + "") : "")));
        }
        return listRelatorios;
    }

    public Integer mainRotina() {
        return mainRotina(new Rotina().get().getId());
    }

    public Integer mainRotina(Integer rotina_id) {
        Relatorios r = new RelatorioDao().findDefaultByRotina(rotina_id, true);
        if (r == null) {
            r = new RelatorioDao().findDefaultByRotina(rotina_id, false);
        }
        if (r != null) {
            return r.getId();
        }
        return null;
    }

    public String getJasperName() {
        String j = jasper;
        if (!j.isEmpty()) {
            if (j.contains("Relatorios")) {
                j = j.replace("Relatorios", "");
                j = j.replace("/", "");
            }
        }
        return j;
    }

    public String getModelo(String cliente) {
        String sessaoCliente = GenericaSessao.getString("sessaoCliente");
        String c = sessaoCliente;
        if (sessaoCliente.equals("Rtools")) {
            c = cliente;
        }
        String diretorio = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + c + "/" + "Relatorios" + "/" + this.getJasperName());
        if (new File(diretorio).exists()) {
            return "Personalizado (" + cliente + ")";
        }
        diretorio = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios" + "/" + this.getJasperName());
        if (new File(diretorio).exists()) {
            return "Padrão";
        }
        return "Não Encontrado";
    }

}
