package br.com.rtools.relatorios.db;

import br.com.rtools.relatorios.Relatorios;
import java.util.List;
import java.util.Vector;

public interface RelatorioSociosDB {

    public List listaEmpresaDoSocio();

    public List listaCidadeDoSocio();

    public List listaCidadeDaEmpresa();

    public List listaSPSocios();

    public List listaSPAcademia();

    public List listaSPEscola();

    public List listaSPConvenioMedico();

    public List pesquisaSocios(Relatorios relatorio, Integer matricula_inicial, Integer matricula_final, Integer idade_inicial, Integer idade_final, String ids_gc, String ids_c, String tipo_sexo, String ids_parentesco, String tipo_foto, String tipo_carteirinha,
            Boolean booTipoPagamento, String ids_pagamento, Boolean booCidadeSocio, String ids_cidade_socio, Boolean booCidadeEmpresa, String ids_cidade_empresa,
            Boolean booAniversario, String meses_aniversario, String dia_inicial, String dia_final, Boolean ordemAniversario, Boolean booData, String dt_cadastro, String dt_cadastro_fim, String dt_recadastro,
            String dt_recadastro_fim, String dt_demissao, String dt_demissao_fim, String dt_admissao_socio, String dt_admissao_socio_fim, String dt_admissao_empresa, String dt_admissao_empresa_fim, Boolean booVotante, String tipo_votante,
            Boolean booEmail, String tipo_email, Boolean booTelefone, String tipo_telefone, Boolean booEstadoCivil, String tipo_estado_civil, Boolean booEmpresas, String tipo_empresa, Integer id_juridica, Integer minQtdeFuncionario, Integer maxQtdeFuncionario,
            String data_aposentadoria, String data_aposentadoria_fim, String ordem, String tipoCarencia, Integer carenciaDias, String situacao, Boolean booBiometria, String tipoBiometria, Boolean booDescontoFolha, String tipoDescontoFolha,
            String data_atualizacao, String data_atualizacao_fim, Boolean contemServicos, String inIdGrupoFinanceiro, String inIdSubGrupoFinanceiro, String inIdServicos, String inIdDescontoSocial);

    public List<Vector> listaSociosInativos(boolean comDependentes, boolean chkInativacao, boolean chkFiliacao, String dt_inativacao_i, String dt_inativacao_f, String dt_filiacao_i, String dt_filiacao_f, int categoria, int grupoCategoria, String ordernarPor);
}
