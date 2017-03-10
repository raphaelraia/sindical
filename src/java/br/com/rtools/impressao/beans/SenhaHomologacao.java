package br.com.rtools.impressao.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.ConfiguracaoHomologacao;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.impressao.ParametroSenha;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Dispositivo;
import br.com.rtools.sistema.ImpressoraMatricial;
import br.com.rtools.sistema.ImpressoraMatricialLinhas;
import br.com.rtools.sistema.dao.DispositivoDao;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SenhaHomologacao implements Serializable {

    public void imprimir(Agendamento a) {
        ConfiguracaoHomologacao ch = ConfiguracaoHomologacao.get();
        if (ch != null) {
            if (ch.getImprimeSenhaMatricial()) {
                MacFilial mf = MacFilial.getAcessoFilial();
                Dispositivo d = new DispositivoDao().findByMacFilial(mf.getId(), 1);
                if (d == null) {
                    d = new DispositivoDao().findByFilial(mf.getFilial().getId(), 1);
                    if (d == null) {
                        GenericaMensagem.warn("Sistema", "NENHUM DISPOSITIVO ENCONTRADO!");
                        return;
                    }
                }
                imprimir(a, true, d);
                return;
            }
        }
        imprimir(a, false, null);
    }

    public void imprimir(Agendamento a, Boolean matricial, Dispositivo d) {
        Collection lista = parametros(a);
        imprimir(lista, matricial, d);
    }

    public void imprimir(Collection lista) {
        ConfiguracaoHomologacao ch = ConfiguracaoHomologacao.get();
        if (ch != null) {
            if (ch.getImprimeSenhaMatricial()) {
                MacFilial mf = MacFilial.getAcessoFilial();
                Dispositivo d = new DispositivoDao().findByMacFilial(mf.getId(), 1);
                if (d == null) {
                    d = new DispositivoDao().findByFilial(mf.getFilial().getId(), 1);
                    if (d == null) {
                        GenericaMensagem.warn("Sistema", "NENHUM DISPOSITIVO ENCONTRADO!");
                        return;
                    }
                }
                imprimir(lista, true, d);
                return;

            }
        }
        imprimir(lista, false);
    }

    public void imprimir(Collection lista, Boolean matricial) {
        imprimir(lista, matricial, null);
    }

    public void imprimir(Collection lista, Boolean matricial, Dispositivo dispositivo) {
        if (matricial) {
            if (dispositivo == null) {
                GenericaMensagem.warn("Sistema", "CADASTRAR DISPOSITIVO!");
                return;
            }
            Dao dao = new Dao();
            dispositivo = (Dispositivo) dao.find(dispositivo);
            if (dispositivo.getConectado() == null) {
                GenericaMensagem.warn("Sistema", "DISPOSITIVO OFFLINE!");
                return;
            }
            if (!lista.isEmpty()) {
                List<ImpressoraMatricialLinhas> listDeleteIML = dao.list(new ImpressoraMatricialLinhas());
                List<ImpressoraMatricial> listDeleteIM = dao.list(new ImpressoraMatricial());
                dao.openTransaction();
                for (int i = 0; i < listDeleteIML.size(); i++) {
                    if (!dao.delete(listDeleteIML.get(i))) {
                        dao.rollback();
                        return;
                    }
                }
                for (int i = 0; i < listDeleteIM.size(); i++) {
                    if (!dao.delete(listDeleteIM.get(i))) {
                        dao.rollback();
                        return;
                    }
                }
                ParametroSenha ps = (ParametroSenha) ((List) lista).get(0);
                ImpressoraMatricial im = new ImpressoraMatricial();
                im.setDispositivo(dispositivo);
                im.setDate(new Date());
                if (!dao.save(im)) {
                    dao.rollback();
                    return;
                }
                List<ImpressoraMatricialLinhas> imls = new ArrayList();
                Juridica filial = new Registro().get().getFilial();
                Juridica juridica = ConfiguracaoArrecadacao.get().getFilial().getFilial();
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, filial.getPessoa().getDocumento() + " - " + AnaliseString.removerAcentos(juridica.getPessoa().getNome())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, ""));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, AnaliseString.removerAcentos("CNPJ: " + ps.getEmpresa_documento())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, AnaliseString.removerAcentos("EMPRESA: " + ps.getEmpresa_nome())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, ""));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, AnaliseString.removerAcentos("PREPOSTO: " + ps.getPreposto())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, AnaliseString.removerAcentos("FUNCIONÁRIO: " + ps.getFuncionario())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, AnaliseString.removerAcentos("ATENDIMENTO: " + ps.getUsuario_nome())));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, "DATA/HORA: " + ps.getData() + " " + ps.getHora() + " hrs"));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, ""));
                imls.add(new ImpressoraMatricialLinhas(null, im, 6, ""));
                imls.add(new ImpressoraMatricialLinhas(null, im, 15, "                 <<< SENHA: " + ps.getSenha() + " >>>"));
                for (int i = 0; i < imls.size(); i++) {
                    if (!dao.save(imls.get(i))) {
                        dao.rollback();
                        return;
                    }
                }
                dao.commit();
                Socket socket = null;
                try {
                    if (dispositivo.getSocketPort() != null && dispositivo.getSocketPort() != 0 && (dispositivo.getSocketHost() == null && dispositivo.getSocketHost().isEmpty())) {
                        socket = new Socket("localhost", dispositivo.getSocketPort());
                    } else if (dispositivo.getSocketHost() != null && !dispositivo.getSocketHost().isEmpty() && dispositivo.getSocketPort() != null && dispositivo.getSocketPort() != 0) {
                        socket = new Socket(dispositivo.getSocketHost(), dispositivo.getSocketPort());
                    } else {
                        return;
                    }
                    if (socket == null) {
                        GenericaMensagem.warn("Sistema", "DISPOSITIVO DESCONECTADO!");
                        return;
                    }
                    try {
                        if (socket.isClosed()) {
                            GenericaMensagem.warn("Sistema", "DISPOSITIVO DESCONECTADO!");
                            return;
                        }
                    } catch (Exception e) {

                    }
                    // BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // String answer = br.readLine();
                    // br.close();
                    dispositivo = (Dispositivo) dao.find(dispositivo);
                    if (!dispositivo.getMensagemAlerta().isEmpty()) {
                        GenericaMensagem.warn("Sistema", dispositivo.getMensagemAlerta());
                        dispositivo.setMensagemAlerta("");
                        dao.update(dispositivo, true);
                    }
                } catch (Exception e) {
                    GenericaMensagem.warn("Sistema", e.getMessage());
                    return;
                }
            }
            return;
        }
        Jasper jasper = new Jasper();
        jasper.init();
        Jasper.TITLE = "SENHA PARA HOMOLOGAÇÃO";
        Jasper.IS_DOWNLOAD = true;
        Jasper.PATH = "";
        Jasper.PART_NAME = "";
        Jasper.EXPORT_TO = true;
        Jasper.EXPORT_TYPE = "pdf";
        Jasper.printReports("/Relatorios/HOM_SENHA.jasper", "senhas", lista);
    }

    public Collection<ParametroSenha> parametros(Agendamento a) {
        Dao dao = new Dao();
        dao.openTransaction();
        Collection<ParametroSenha> list = parametros(a, dao);
        if (list == null) {
            GenericaMensagem.warn("Erro", "Ao gerar senha!");
            dao.rollback();
            return new ArrayList();
        } else {
            GenericaMensagem.warn("Sucesso", "Senha gerada com sucesso!");
            dao.commit();
        }
        return list;
    }

    public Collection<ParametroSenha> parametros(Agendamento a, Dao dao) {
        if (a.getId() == -1) {
            return null;
        }
        Collection lista = new ArrayList();
        HomologacaoDao db = new HomologacaoDao();
        Senha senha = db.pesquisaSenhaAgendamento(a.getId());
        MacFilial mc = MacFilial.getAcessoFilial();
        if (senha.getId() == -1) {
            senha.setAgendamento(a);
            senha.setDtData(DataHoje.dataHoje());
            senha.setHora(DataHoje.horaMinuto());
            senha.setUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")));
            senha.setFilial(mc.getFilial());
            senha.setSenha(db.pesquisaUltimaSenha(mc.getFilial().getId()) + 1);
            // default DEPARTAMENTO 8 para o caso de HOMOLOGAÇÃO
            senha.setDepartamento((Departamento) dao.find(new Departamento(), 8));
            if (!dao.save(senha)) {
                return null;
            }
        } else if (!dao.update(senha)) {
            return null;
        }
        try {
            if (senha.getId() != -1) {
                lista.add(new ParametroSenha(
                        senha.getAgendamento().getPessoaEmpresa().getJuridica().getPessoa().getNome(),
                        senha.getAgendamento().getPessoaEmpresa().getJuridica().getPessoa().getDocumento(),
                        (senha.getAgendamento().getRecepcao() == null) ? "" : senha.getAgendamento().getRecepcao().getPreposto(),
                        senha.getAgendamento().getPessoaEmpresa().getFisica().getPessoa().getNome(),
                        senha.getUsuario().getPessoa().getNome(),
                        senha.getData(),
                        senha.getHora(),
                        String.valueOf(senha.getSenha())));
            }
        } catch (Exception e) {
            return null;
        }
        return lista;
    }
}
