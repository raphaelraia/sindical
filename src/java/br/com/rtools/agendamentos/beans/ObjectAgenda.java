package br.com.rtools.agendamentos.beans;

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
     */
    public ObjectAgenda(Object data, Object horario_inicial, Object horario_final, Object tempo_servico, Object id_status, Object status, Object id_agendamento, Object id_servico, Object servico, Object codigo, Object nome, Object documento, Object agendador, Object id_filial, Object filial, Object filial_documento, Object id_colaborador, Object colaborador, Object colaborador_documento, Object id_convenio_sub_grupo, Object convenio_sub_grupo) {
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

}
