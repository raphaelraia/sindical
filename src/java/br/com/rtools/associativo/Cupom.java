package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.CupomCategoriaDao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_cupom",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ds_descricao", "dt_data"})
)
public class Cupom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", nullable = false, length = 250, columnDefinition = "character varying default ''")
    private String descricao;
    @Lob
    @Column(name = "ds_obs", nullable = false, columnDefinition = "character varying default ''")
    private String obs;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data", nullable = false)
    private Date dtData;
    @Column(name = "nr_carencia_inadimplencia_dias", columnDefinition = "integer default 0")
    private Integer carenciaInadimplenciaDias;
    @Column(name = "is_ativo", columnDefinition = "boolean default true")
    private Boolean ativo;
    @Column(name = "is_bloqueia_debito", columnDefinition = "boolean default true")
    private Boolean bloqueiaDebito;

    @Transient
    private Boolean selected;

    @Transient
    private List listCupomCategoria;

    public Cupom() {
        this.id = null;
        this.descricao = "";
        this.obs = "";
        this.dtData = null;
        this.carenciaInadimplenciaDias = 0;
        this.ativo = true;
        this.selected = false;
        this.listCupomCategoria = null;
        this.bloqueiaDebito = false;
    }

    public Cupom(Integer id, String descricao, String obs, Date dtData, Integer carenciaInadimplenciaDias, Boolean ativo, Boolean bloqueiaDebito) {
        this.id = id;
        this.descricao = descricao;
        this.obs = obs;
        this.dtData = dtData;
        this.carenciaInadimplenciaDias = carenciaInadimplenciaDias;
        this.ativo = ativo;
        this.selected = false;
        this.listCupomCategoria = null;
        this.bloqueiaDebito = bloqueiaDebito;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.trim();
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getData() {
        return DataHoje.converteData(dtData);
    }

    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
    }

    public Integer getCarenciaInadimplenciaDias() {
        return carenciaInadimplenciaDias;
    }

    public void setCarenciaInadimplenciaDias(Integer carenciaInadimplenciaDias) {
        this.carenciaInadimplenciaDias = carenciaInadimplenciaDias;
    }

    public String getCarenciaInadimplenciaDiasString() {
        return Integer.toString(carenciaInadimplenciaDias);
    }

    public void setCarenciaInadimplenciaDiasString(String carenciaInadimplenciaDiasString) {
        this.carenciaInadimplenciaDias = Integer.parseInt(carenciaInadimplenciaDiasString);
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Cupom{" + "id=" + id + ", descricao=" + descricao + ", dtData=" + dtData + ", carenciaInadimplenciaDias=" + carenciaInadimplenciaDias + ", ativo=" + ativo + '}';
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public List getListCupomCategoria() {
        if (listCupomCategoria == null) {
            if (this.id != null) {
                listCupomCategoria = new ArrayList();
                listCupomCategoria = new CupomCategoriaDao().find(this.id);
            }
        }
        return listCupomCategoria;
    }

    public void setListCupomCategoria(List listCupomCategoria) {
        this.listCupomCategoria = listCupomCategoria;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Boolean getBloqueiaDebito() {
        return bloqueiaDebito;
    }

    public void setBloqueiaDebito(Boolean bloqueiaDebito) {
        this.bloqueiaDebito = bloqueiaDebito;
    }

}
