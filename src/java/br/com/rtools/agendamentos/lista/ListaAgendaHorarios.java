package br.com.rtools.agendamentos.lista;

import br.com.rtools.agendamentos.AgendaHorarios;

public class ListaAgendaHorarios {

    public ListaAgendaHorarios() {
        this.horarios = new AgendaHorarios();
        this.ativo = "";
    }

    public ListaAgendaHorarios(AgendaHorarios horarios, String ativo) {
        this.horarios = horarios;
        this.ativo = ativo;
    }

    private AgendaHorarios horarios;
    private String ativo;

    public AgendaHorarios getHorarios() {
        return horarios;
    }

    public void setHorarios(AgendaHorarios horarios) {
        this.horarios = horarios;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }
}
