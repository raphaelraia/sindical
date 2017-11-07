package br.com.rtools.associativo;

import br.com.rtools.seguranca.Departamento;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "soc_catraca")
public class Catraca implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @Column(name = "ds_numero")
    private String numero;
    @Column(name = "ds_ip")
    private String ip;
    @Column(name = "nr_numero")
    private Integer nrNumero;
    @Column(name = "nr_porta")
    private Integer porta;
    @Column(name = "nr_quantidade_digitos")
    private Integer quantidadeDigitos;
    @Column(name = "is_bloquear_sem_foto")
    private Boolean bloquearSemFoto;
    @Column(name = "nr_tipo_giro_catraca")
    private Integer tipoGiroCatraca;
    @Column(name = "ds_lado_giro_catraca")
    private String ladoGiroCatraca;
    @Column(name = "ds_servidor_foto")
    private String servidorFoto;
    @Column(name = "is_servidor_beep")
    private Boolean servidorBeep;
    @Column(name = "is_biometrico")
    private Boolean biometrico;
    @Column(name = "is_leitor_biometrico_externo")
    private Boolean leitorBiometricoExterno;
    @Column(name = "is_grava_frequencia_catraca")
    private Boolean gravaFrequenciaCatraca;
    @Column(name = "is_verifica_biometria")
    private Boolean verificaBiometria;
    @Column(name = "is_verifica_liberacao")
    private Boolean verificaLiberacao;
    @Column(name = "is_ativo")
    private Boolean ativo;
    @Column(name = "ds_mac", length = 30)
    private String mac;
    @Column(name = "nr_servidor")
    private Integer servidor;

    public Catraca() {
        this.id = -1;
        this.departamento = new Departamento();
        this.numero = "01";
        this.ip = "";
        this.nrNumero = 1;
        this.porta = 3570;
        this.quantidadeDigitos = 14;
        this.bloquearSemFoto = false;
        this.tipoGiroCatraca = 1;
        this.ladoGiroCatraca = "direita";
        this.servidorFoto = "";
        this.servidorBeep = false;
        this.biometrico = false;
        this.leitorBiometricoExterno = true;
        this.gravaFrequenciaCatraca = false;
        this.verificaBiometria = false;
        this.verificaLiberacao = true;
        this.ativo = true;
        this.mac = "";
        this.servidor = null;
    }

    public Catraca(Integer id, Departamento departamento, String numero, String ip, Integer nrNumero, Integer porta, Integer quantidadeDigitos, Boolean bloquearSemFoto, Integer tipoGiroCatraca, String ladoGiroCatraca, String servidorFoto, Boolean servidorBeep, Boolean biometrico, Boolean leitorBiometricoExterno, Boolean gravaFrequenciaCatraca, Boolean verificaBiometria, Boolean verificaLiberacao, Boolean ativo, String mac, Integer servidor) {
        this.id = id;
        this.departamento = departamento;
        this.numero = numero;
        this.ip = ip;
        this.nrNumero = nrNumero;
        this.porta = porta;
        this.quantidadeDigitos = quantidadeDigitos;
        this.bloquearSemFoto = bloquearSemFoto;
        this.tipoGiroCatraca = tipoGiroCatraca;
        this.ladoGiroCatraca = ladoGiroCatraca;
        this.servidorFoto = servidorFoto;
        this.servidorBeep = servidorBeep;
        this.biometrico = biometrico;
        this.leitorBiometricoExterno = leitorBiometricoExterno;
        this.gravaFrequenciaCatraca = gravaFrequenciaCatraca;
        this.verificaBiometria = verificaBiometria;
        this.verificaLiberacao = verificaLiberacao;
        this.ativo = ativo;
        this.mac = mac;
        this.servidor = servidor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getNrNumero() {
        return nrNumero;
    }

    public void setNrNumero(Integer nrNumero) {
        this.nrNumero = nrNumero;
    }

    public Integer getPorta() {
        return porta;
    }

    public void setPorta(Integer porta) {
        this.porta = porta;
    }

    public Integer getQuantidadeDigitos() {
        return quantidadeDigitos;
    }

    public void setQuantidadeDigitos(Integer quantidadeDigitos) {
        this.quantidadeDigitos = quantidadeDigitos;
    }

    public Boolean getBloquearSemFoto() {
        return bloquearSemFoto;
    }

    public void setBloquearSemFoto(Boolean bloquearSemFoto) {
        this.bloquearSemFoto = bloquearSemFoto;
    }

    public Integer getTipoGiroCatraca() {
        return tipoGiroCatraca;
    }

    public void setTipoGiroCatraca(Integer tipoGiroCatraca) {
        this.tipoGiroCatraca = tipoGiroCatraca;
    }

    public String getLadoGiroCatraca() {
        return ladoGiroCatraca;
    }

    public void setLadoGiroCatraca(String ladoGiroCatraca) {
        this.ladoGiroCatraca = ladoGiroCatraca;
    }

    public String getServidorFoto() {
        return servidorFoto;
    }

    public void setServidorFoto(String servidorFoto) {
        this.servidorFoto = servidorFoto;
    }

    public Boolean getServidorBeep() {
        return servidorBeep;
    }

    public void setServidorBeep(Boolean servidorBeep) {
        this.servidorBeep = servidorBeep;
    }

    public Boolean getBiometrico() {
        return biometrico;
    }

    public void setBiometrico(Boolean biometrico) {
        this.biometrico = biometrico;
    }

    public Boolean getLeitorBiometricoExterno() {
        return leitorBiometricoExterno;
    }

    public void setLeitorBiometricoExterno(Boolean leitorBiometricoExterno) {
        this.leitorBiometricoExterno = leitorBiometricoExterno;
    }

    public Boolean getGravaFrequenciaCatraca() {
        return gravaFrequenciaCatraca;
    }

    public void setGravaFrequenciaCatraca(Boolean gravaFrequenciaCatraca) {
        this.gravaFrequenciaCatraca = gravaFrequenciaCatraca;
    }

    public Boolean getVerificaBiometria() {
        return verificaBiometria;
    }

    public void setVerificaBiometria(Boolean verificaBiometria) {
        this.verificaBiometria = verificaBiometria;
    }

    public Boolean getVerificaLiberacao() {
        return verificaLiberacao;
    }

    public void setVerificaLiberacao(Boolean verificaLiberacao) {
        this.verificaLiberacao = verificaLiberacao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getServidor() {
        return servidor;
    }

    public void setServidor(Integer servidor) {
        this.servidor = servidor;
    }

}
