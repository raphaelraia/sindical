package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;

public class ObjectAgenda {

    private Object data;
    private Object horario_inicial;
    private Object horario_final;
    private Object tempo_servico;
    private Object id_status;
    private Object status;
    private Object id_agendamento;
    private Object id_servico;
    private Object servico;
    private Object codigo;
    private Object nome;
    private Object documento;
    private Object agendador;
    private Object id_filial;
    private Object filial;
    private Object filial_documento;
    private Object id_colaborador;
    private Object colaborador;
    private Object colaborador_documento;
    private Object id_convenio_sub_grupo;
    private Object convenio_sub_grupo;
    private Agendamentos agendamentos;
    private Object convenio_grupo;
    private Object id_movimento;
    private Object id_convenio_grupo;
    private Object valor;
    private Pessoa pessoa;
    private Boolean selected;
    private Boolean disabled;
    private Boolean rendered;
    private Movimento movimento;

    public ObjectAgenda() {
        this.data = null;
        this.horario_inicial = null;
        this.horario_final = null;
        this.tempo_servico = null;
        this.id_status = null;
        this.status = null;
        this.id_agendamento = null;
        this.id_servico = null;
        this.servico = null;
        this.codigo = null;
        this.nome = null;
        this.documento = null;
        this.agendador = null;
        this.id_filial = null;
        this.filial = null;
        this.filial_documento = null;
        this.id_colaborador = null;
        this.colaborador = null;
        this.colaborador_documento = null;
        this.id_convenio_sub_grupo = null;
        this.convenio_sub_grupo = null;
        this.agendamentos = null;
        this.convenio_grupo = null;
        this.pessoa = null;
        this.valor = null;
        this.id_convenio_grupo = null;
        this.selected = false;
        this.disabled = false;
        this.rendered = true;
        this.movimento = null;
        this.id_movimento = null;
    }

    /**
     *
     * @param data
     * @param horario_inicial
     * @param horario_final
     * @param tempo_servico
     * @param id_status
     * @param status
     * @param id_agendamento
     * @param id_servico
     * @param servico
     * @param codigo
     * @param nome
     * @param documento
     * @param agendador
     * @param id_filial
     * @param filial
     * @param filial_documento
     * @param id_colaborador
     * @param colaborador
     * @param colaborador_documento
     * @param id_convenio_sub_grupo
     * @param convenio_sub_grupo
     * @param agendamentos
     * @param convenio_grupo
     * @param valor
     * @param id_convenio_grupo
     * @param id_movimento
     */
    public ObjectAgenda(Object data, Object horario_inicial, Object horario_final, Object tempo_servico, Object id_status, Object status, Object id_agendamento, Object id_servico, Object servico, Object codigo, Object nome, Object documento, Object agendador, Object id_filial, Object filial, Object filial_documento, Object id_colaborador, Object colaborador, Object colaborador_documento, Object id_convenio_sub_grupo, Object convenio_sub_grupo, Agendamentos agendamentos, Object convenio_grupo, Object valor, Object id_convenio_grupo, Object id_movimento) {
        this.data = data;
        this.horario_inicial = horario_inicial;
        this.horario_final = horario_final;
        this.tempo_servico = tempo_servico;
        this.id_status = id_status;
        this.status = status;
        this.id_agendamento = id_agendamento;
        this.id_servico = id_servico;
        this.servico = servico;
        this.codigo = codigo;
        this.nome = nome;
        this.documento = documento;
        this.agendador = agendador;
        this.id_filial = id_filial;
        this.filial = filial;
        this.filial_documento = filial_documento;
        this.id_colaborador = id_colaborador;
        this.colaborador = colaborador;
        this.colaborador_documento = colaborador_documento;
        this.id_convenio_sub_grupo = id_convenio_sub_grupo;
        this.convenio_sub_grupo = convenio_sub_grupo;
        this.agendamentos = agendamentos;
        this.convenio_grupo = convenio_grupo;
        this.valor = valor;
        this.id_convenio_grupo = id_convenio_grupo;
        this.id_movimento = id_movimento;
        this.pessoa = null;
        this.selected = false;
        this.disabled = false;
        this.rendered = true;
        this.movimento = null;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getHorario_inicial() {
        return horario_inicial;
    }

    public void setHorario_inicial(Object horario_inicial) {
        this.horario_inicial = horario_inicial;
    }

    public Object getHorario_final() {
        return horario_final;
    }

    public void setHorario_final(Object horario_final) {
        this.horario_final = horario_final;
    }

    public Object getTempo_servico() {
        return tempo_servico;
    }

    public void setTempo_servico(Object tempo_servico) {
        this.tempo_servico = tempo_servico;
    }

    public Object getId_status() {
        return id_status;
    }

    public void setId_status(Object id_status) {
        this.id_status = id_status;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getId_agendamento() {
        return id_agendamento;
    }

    public void setId_agendamento(Object id_agendamento) {
        this.id_agendamento = id_agendamento;
    }

    public Object getId_servico() {
        return id_servico;
    }

    public void setId_servico(Object id_servico) {
        this.id_servico = id_servico;
    }

    public Object getServico() {
        return servico;
    }

    public void setServico(Object servico) {
        this.servico = servico;
    }

    public Object getCodigo() {
        return codigo;
    }

    public void setCodigo(Object codigo) {
        this.codigo = codigo;
    }

    public Object getNome() {
        return nome;
    }

    public void setNome(Object nome) {
        this.nome = nome;
    }

    public Object getDocumento() {
        return documento;
    }

    public void setDocumento(Object documento) {
        this.documento = documento;
    }

    public Object getAgendador() {
        return agendador;
    }

    public void setAgendador(Object agendador) {
        this.agendador = agendador;
    }

    public Object getId_filial() {
        return id_filial;
    }

    public void setId_filial(Object id_filial) {
        this.id_filial = id_filial;
    }

    public Object getFilial() {
        return filial;
    }

    public void setFilial(Object filial) {
        this.filial = filial;
    }

    public Object getFilial_documento() {
        return filial_documento;
    }

    public void setFilial_documento(Object filial_documento) {
        this.filial_documento = filial_documento;
    }

    public Object getId_colaborador() {
        return id_colaborador;
    }

    public void setId_colaborador(Object id_colaborador) {
        this.id_colaborador = id_colaborador;
    }

    public Object getColaborador() {
        return colaborador;
    }

    public void setColaborador(Object colaborador) {
        this.colaborador = colaborador;
    }

    public Object getColaborador_documento() {
        return colaborador_documento;
    }

    public void setColaborador_documento(Object colaborador_documento) {
        this.colaborador_documento = colaborador_documento;
    }

    public Object getId_convenio_sub_grupo() {
        return id_convenio_sub_grupo;
    }

    public void setId_convenio_sub_grupo(Object id_convenio_sub_grupo) {
        this.id_convenio_sub_grupo = id_convenio_sub_grupo;
    }

    public Object getConvenio_sub_grupo() {
        return convenio_sub_grupo;
    }

    public void setConvenio_sub_grupo(Object convenio_sub_grupo) {
        this.convenio_sub_grupo = convenio_sub_grupo;
    }

    public Agendamentos getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(Agendamentos agendamentos) {
        this.agendamentos = agendamentos;
    }

    public Object getConvenio_grupo() {
        return convenio_grupo;
    }

    public void setConvenio_grupo(Object convenio_grupo) {
        this.convenio_grupo = convenio_grupo;
    }

    public Pessoa getPessoa() {
        if (pessoa == null) {
            try {
                if (!codigo.toString().isEmpty()) {
                    pessoa = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(codigo.toString()));
                }
            } catch (Exception e) {
                return null;
            }

        }
        return pessoa;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getRendered() {
        return rendered;
    }

    public void setRendered(Boolean rendered) {
        this.rendered = rendered;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Object getId_convenio_grupo() {
        return id_convenio_grupo;
    }

    public void setId_convenio_grupo(Object id_convenio_grupo) {
        this.id_convenio_grupo = id_convenio_grupo;
    }

    public Movimento getMovimento() {
        if (movimento == null) {
            if(id_movimento != null) {
                movimento = (Movimento) new Dao().find(new Movimento(), Integer.parseInt(id_movimento.toString()));                
            }
        }
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

}
