/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "eve_evento_baile_impressao_convite")
public class EventoBaileImpressaoConvite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_evento_baile_convite", referencedColumnName = "id")
    @ManyToOne
    private EventoBaileConvite eventoBaileConvite;
    @JoinColumn(name = "id_evento_baile_mapa", referencedColumnName = "id")
    @ManyToOne
    private EventoBaileMapa eventoBaileMapa;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuario;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_impressao", nullable = false)
    private Date dtImpressao;
    @Column(name = "ds_hora_impressao")
    private String horaImpressao;

    public EventoBaileImpressaoConvite() {
        this.id = -1;
        this.eventoBaileConvite = null;
        this.eventoBaileMapa = null;
        this.usuario = new Usuario();
        this.dtImpressao = null;
        this.horaImpressao = "";
    }
    
    public EventoBaileImpressaoConvite(int id, EventoBaileConvite eventoBaileConvite, EventoBaileMapa eventoBaileMapa, Usuario usuario, Date dtImpressao, String horaImpressao) {
        this.id = id;
        this.eventoBaileConvite = eventoBaileConvite;
        this.eventoBaileMapa = eventoBaileMapa;
        this.usuario = usuario;
        this.dtImpressao = dtImpressao;
        this.horaImpressao = horaImpressao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventoBaileConvite getEventoBaileConvite() {
        return eventoBaileConvite;
    }

    public void setEventoBaileConvite(EventoBaileConvite eventoBaileConvite) {
        this.eventoBaileConvite = eventoBaileConvite;
    }

    public EventoBaileMapa getEventoBaileMapa() {
        return eventoBaileMapa;
    }

    public void setEventoBaileMapa(EventoBaileMapa eventoBaileMapa) {
        this.eventoBaileMapa = eventoBaileMapa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDtImpressao() {
        return dtImpressao;
    }

    public void setDtImpressao(Date dtImpressao) {
        this.dtImpressao = dtImpressao;
    }

    public String getDtImpressaoString() {
        return DataHoje.converteData(dtImpressao);
    }

    public void setDtImpressaoString(String dtImpressaoString) {
        this.dtImpressao = DataHoje.converte(dtImpressaoString);
    }

    public String getHoraImpressao() {
        return horaImpressao;
    }

    public void setHoraImpressao(String horaImpressao) {
        this.horaImpressao = horaImpressao;
    }

}
