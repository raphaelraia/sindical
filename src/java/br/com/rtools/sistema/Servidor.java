package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_servidor")
@NamedQueries({
    @NamedQuery(name = "Servidor.findAll", query = "SELECT S FROM Servidor AS S ORDER BY S.apelido ASC")
})
public class Servidor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_apelido", length = 50)
    private String apelido;
    @Column(name = "ds_alias", length = 50)
    private String alias;
    @Column(name = "ds_uri", length = 300)
    private String uri;
    @Column(name = "ds_so", length = 50)
    private String so;
    @Column(name = "is_ativo", columnDefinition = "boolean default false")
    private Boolean ativo;

    public Servidor() {
        this.id = null;
        this.apelido = null;
        this.alias = null;
        this.uri = null;
        this.so = null;
        this.ativo = false;
    }

    public Servidor(Integer id, String apelido, String alias, String uri, String so, Boolean ativo) {
        this.id = id;
        this.apelido = apelido;
        this.alias = alias;
        this.uri = uri;
        this.so = so;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
