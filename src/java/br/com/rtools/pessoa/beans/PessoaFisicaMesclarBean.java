package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.MotivoInativacao;
import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.SMotivoInativacao;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.dao.EmissaoGuiasDao;
import br.com.rtools.associativo.dao.HistoricoEmissaoGuiasDao;
import br.com.rtools.associativo.dao.MatriculaSociosDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.cobranca.dao.TmktHistoricoDao;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.MovimentoBoleto;
import br.com.rtools.financeiro.MovimentoInativo;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.dao.FormaPagamentoDao;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.financeiro.dao.MovimentoBoletoDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.MovimentoInativoDao;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.dao.CancelamentoDao;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.homologacao.dao.SenhaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.PessoaProfissaoDao;
import br.com.rtools.seguranca.SisEmailProtocolo;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.dao.EmailDao;
import br.com.rtools.sistema.dao.EmailPessoaDao;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PessoaFisicaMesclarBean implements Serializable {

    private Fisica fisica;
    private List<Fisica> listPessoaFisica;

    @PostConstruct
    public void init() {
        fisica = new Fisica();
        listPessoaFisica = new ArrayList();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("pessoaFisicaMesclarBean");
        GenericaSessao.remove("fisicaPesquisa");
    }

    public void add() {
        if (fisica.getId() == -1) {
            GenericaMensagem.warn("Validação", "INFORMAR PESSOA!");
            return;
        }
        if (listPessoaFisica.size() == 2) {
            GenericaMensagem.warn("Validação", "JÁ EXISTEM DUAS PESSOAS INSERIDAS PARA MESCLAGEM!");
            return;
        }
        for (int x = 0; x < listPessoaFisica.size(); x++) {
            if (fisica.getId() == listPessoaFisica.get(x).getId()) {
                GenericaMensagem.warn("Validação", "JÁ ADICIONADA!");
                return;
            }
        }
        for (int x = 0; x < listPessoaFisica.size(); x++) {
            if (!fisica.getPessoa().getNome().toLowerCase().contains(listPessoaFisica.get(x).getPessoa().getNome().toLowerCase())) {
                GenericaMensagem.warn("Validação", "AS PESSOA ADICIONADAS TEM NOMES DIFERENTES!");
                break;
            }
        }
        if (listPessoaFisica.isEmpty()) {
            fisica.setSelected(true);
        }
        listPessoaFisica.add(fisica);
        fisica = new Fisica();
    }

    public void update() {
        if (listPessoaFisica.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        if (listPessoaFisica.size() == 1) {
            GenericaMensagem.warn("Validação", "ESPECÍFICAR MAIS DE UMA PESSOA!");
            return;
        }
        Fisica manter = null;
        Fisica remover = null;
        Boolean principal = false;
        for (int i = 0; i < listPessoaFisica.size(); i++) {
            if (listPessoaFisica.get(i).getSelected()) {
                principal = true;
                break;
            }
        }
        if (!principal) {
            GenericaMensagem.warn("Validação", "SELECIONAR CADASTRO A SER MANTIDO!");
            return;
        }
        for (int i = 0; i < listPessoaFisica.size(); i++) {
            if (listPessoaFisica.get(i).getSelected()) {
                manter = listPessoaFisica.get(i);
            } else {
                remover = listPessoaFisica.get(i);
            }
        }
        if (manter == null || remover == null) {
            GenericaMensagem.warn("Erro", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        Dao dao = new Dao();
        dao.openTransaction();
        List<SocioCarteirinha> scsManter = new SocioCarteirinhaDao().findByPessoa(manter.getPessoa().getId());
        List<SocioCarteirinha> scsRemover = new SocioCarteirinhaDao().findByPessoa(remover.getPessoa().getId());
        if (scsManter.isEmpty()) {
            for (int i = 0; i < scsRemover.size(); i++) {
                scsRemover.get(i).setPessoa(manter.getPessoa());
                if (!dao.update(scsRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR SÓCIO CARTEIRINHA!" + dao.EXCEPCION);
                    return;
                }
                novoLog.save("MESCLAR CADASTRO SÓCIO CARTEIRINHA: " + scsRemover.get(i).toString());
            }
        } else {
            for (int i = 0; i < scsRemover.size(); i++) {
                List<HistoricoCarteirinha> listHistoricoCarteirinha = new SocioCarteirinhaDao().listaHistoricoCarteirinha(scsRemover.get(i).getPessoa().getId());
                for (int j = 0; j < listHistoricoCarteirinha.size(); j++) {
                    if (!dao.delete(listHistoricoCarteirinha.get(j))) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO REMOVER HISTÓRICO DA CARTEIRINHA!" + dao.EXCEPCION);
                        return;
                    }
                    novoLog.save("MESCLAR CADASTRO HISTÓRICO CARTEIRINHA: " + listHistoricoCarteirinha.get(i).toString());
                }
                if (!dao.delete(scsRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SÓCIO CARTEIRINHA!" + dao.EXCEPCION);
                    return;
                }
                novoLog.save("MESCLAR CADASTRO SÓCIO CARTEIRINHA: " + scsRemover.get(i).toString());
            }
        }
        List<PessoaEndereco> pesManter = new PessoaEnderecoDao().pesquisaEndPorPessoa(manter.getPessoa().getId());
        List<PessoaEndereco> pesRemover = new PessoaEnderecoDao().pesquisaEndPorPessoa(remover.getPessoa().getId());
        if (pesManter.isEmpty()) {
            for (int i = 0; i < pesRemover.size(); i++) {
                pesRemover.get(i).setPessoa(manter.getPessoa());
                if (!dao.update(pesRemover)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA ENDEREÇO!");
                    return;
                }
                novoLog.save("ATUALIZAR PESSOA ENDEREÇO: " + pesRemover.get(i).toString());
            }
        } else {
            for (int i = 0; i < pesRemover.size(); i++) {
                if (!dao.delete(pesRemover.get(i))) {
                    GenericaMensagem.warn("Erro", "AO DELETAR PESSOA ENDEREÇO!");
                    return;
                }
                novoLog.save("ATUALIZAR PESSOA ENDEREÇO: " + pesRemover.get(i).toString());
            }

        }
        PessoaComplemento pc = new PessoaComplementoDao().findByPessoa(remover.getPessoa().getId());
        if (pc != null) {
            if (!dao.delete(pc)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER PESSOA COMPLEMENTO!");
                return;
            }
            novoLog.save("REMOVER PESSOA COMPLEMENTO: " + pc.toString());
        }
        String movimentoLogString = "";
        List<Movimento> listMovimentosPessoa = new MovimentoDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listMovimentosPessoa.size(); i++) {
            listMovimentosPessoa.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listMovimentosPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
            movimentoLogString += "ATUALIZAR MOVIMENTO: " + listMovimentosPessoa.get(i).toString() + "; ";
        }
        List<Movimento> listMovimentosTitular = new MovimentoDao().findByTitular(remover.getPessoa().getId());
        if (!listMovimentosTitular.isEmpty()) {
            movimentoLogString = "";
        }
        for (int i = 0; i < listMovimentosTitular.size(); i++) {
            if (listMovimentosTitular.get(i).getPessoa().getId() == remover.getPessoa().getId()) {
                listMovimentosTitular.get(i).setPessoa(manter.getPessoa());
            }
            listMovimentosTitular.get(i).setTitular(manter.getPessoa());
            if (!dao.update(listMovimentosTitular.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
            movimentoLogString += "ATUALIZAR MOVIMENTO: " + listMovimentosTitular.get(i).toString() + "; ";
        }
        List<Movimento> listMovimentosBeneficiario = new MovimentoDao().findByBeneficiario(remover.getPessoa().getId());
        if (!listMovimentosBeneficiario.isEmpty()) {
            movimentoLogString = "";
        }
        for (int i = 0; i < listMovimentosBeneficiario.size(); i++) {
            listMovimentosBeneficiario.get(i).setBeneficiario(manter.getPessoa());
            if (listMovimentosBeneficiario.get(i).getPessoa().getId() == remover.getPessoa().getId()) {
                listMovimentosBeneficiario.get(i).setPessoa(manter.getPessoa());
            }
            if (listMovimentosBeneficiario.get(i).getTitular().getId() == remover.getPessoa().getId()) {
                listMovimentosBeneficiario.get(i).setTitular(manter.getPessoa());
            }
            listMovimentosBeneficiario.get(i).setBeneficiario(manter.getPessoa());
            if (!dao.update(listMovimentosBeneficiario.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
            movimentoLogString += "ATUALIZAR MOVIMENTO: " + listMovimentosBeneficiario.get(i).toString() + "; ";
        }
        if (!movimentoLogString.isEmpty()) {
            novoLog.save("ATUALIZAR MOVIMENTOS DESSA PESSOA: " + movimentoLogString);
        }
        List<Lote> listLote = new LoteDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listLote.size(); i++) {
            listLote.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listLote.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
        }
        List<PessoaProfissao> listPessoaProfissao = new PessoaProfissaoDao().findByFisica(remover.getId());
        for (int i = 0; i < listPessoaProfissao.size(); i++) {
            if (!dao.delete(listPessoaProfissao.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
            novoLog.save("DELETAR PESSOA PROFISSÃO: " + listPessoaProfissao.get(i).toString());
        }
        String servicoPessoaLog = "";
        // SERVIÇO PESSOA -> PESSOA
        List<ServicoPessoa> listServicoPessoaRemover = new ServicoPessoaDao().listAllByPessoa(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                listServicoPessoaRemover.get(i).setPessoa(manter.getPessoa());
                if (!dao.update(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        // SERVIÇO PESSOA -> COBRANÇA
        listServicoPessoaRemover = new ServicoPessoaDao().listAllByCobranca(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            servicoPessoaLog = "";
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                if (listServicoPessoaRemover.get(i).getPessoa().getId() == remover.getPessoa().getId()) {
                    listServicoPessoaRemover.get(i).setPessoa(manter.getPessoa());
                }
                listServicoPessoaRemover.get(i).setCobranca(manter.getPessoa());
                if (!dao.update(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        // SERVIÇO PESSOA -> COBRANÇA MOVIMENTO
        listServicoPessoaRemover = new ServicoPessoaDao().listAllByCobrancaMovimento(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            servicoPessoaLog = "";
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                if (listServicoPessoaRemover.get(i).getPessoa().getId() == remover.getPessoa().getId()) {
                    listServicoPessoaRemover.get(i).setPessoa(manter.getPessoa());
                }
                if (listServicoPessoaRemover.get(i).getCobranca().getId() == remover.getPessoa().getId()) {
                    listServicoPessoaRemover.get(i).setCobranca(manter.getPessoa());
                }
                listServicoPessoaRemover.get(i).setCobrancaMovimento(manter.getPessoa());
                if (!dao.update(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        if (!servicoPessoaLog.isEmpty()) {
            novoLog.save("ATUALIZAR SERVIÇO PESSOA : " + servicoPessoaLog);
        }
        List<MatriculaSocios> msManter = new MatriculaSociosDao().findAllByTitular(manter.getPessoa().getId());
        List<MatriculaSocios> msRemover = new MatriculaSociosDao().findAllByTitular(remover.getPessoa().getId());
        if (msManter.isEmpty() && !msRemover.isEmpty()) {
            for (int i = 0; i < msRemover.size(); i++) {
                msRemover.get(i).setTitular(manter.getPessoa());
                if (!dao.update(msRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR MATRÍCULA SÓCIO! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("ATUALIZAR MATRÍCULA SÓCIO: " + msRemover.get(i).toString());
            }
        } else if (!msManter.isEmpty() && !msRemover.isEmpty()) {
            for (int i = 0; i < msRemover.size(); i++) {
                msRemover.get(i).setDtInativo(new Date());
                msRemover.get(i).setTitular(manter.getPessoa());
                msRemover.get(i).setMotivo("DESABILITADO PELO SISTEMA!");
                msRemover.get(i).setMotivoInativacao((SMotivoInativacao) dao.find(new SMotivoInativacao(), 1));
                if (!dao.update(msRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR MATRÍCULA SÓCIO! " + dao.EXCEPCION);
                    return;
                }
            }
        }
        List<EmailPessoa> listEmailPessoa = new EmailPessoaDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listEmailPessoa.size(); i++) {
            listEmailPessoa.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listEmailPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EMAIL PESSOA!");
                return;
            }
        }
        List<PessoaEmpresa> listPessoaEmpresaManter = new PessoaEmpresaDao().findAllByFisica(manter.getId());
        List<PessoaEmpresa> listPessoaEmpresaRemover = new PessoaEmpresaDao().findAllByFisica(remover.getId());
        for (int i = 0; i < listPessoaEmpresaRemover.size(); i++) {
            Boolean peUpdate = false;
            for (int x = 0; x < listPessoaEmpresaManter.size(); x++) {
                if (listPessoaEmpresaManter.get(x).getJuridica().getId() == listPessoaEmpresaRemover.get(i).getJuridica().getId()) {
                    List<Agendamento> listAgendamento = new HomologacaoDao().pesquisaAgendamentoPorPessoaEmpresa(listPessoaEmpresaRemover.get(i).getId());
                    for (int z = 0; z < listAgendamento.size(); z++) {
                        listAgendamento.get(z).setPessoaEmpresa(listPessoaEmpresaManter.get(x));
                        if (!dao.update(listAgendamento.get(z))) {
                            dao.rollback();
                            GenericaMensagem.warn("Erro", "AO ATUALIZAR AGENDAMENTO! " + dao.EXCEPCION);
                            return;
                        }
                        peUpdate = true;
                    }
                }
            }
            if (peUpdate) {
                if (!dao.delete(listPessoaEmpresaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER PESSOA EMPRESA! " + dao.EXCEPCION);
                    return;
                }
            } else {
                listPessoaEmpresaRemover.get(i).setFisica(manter);
                if (!dao.update(listPessoaEmpresaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA EMPRESA! " + dao.EXCEPCION);
                    return;
                }
            }
            novoLog.save("ATUALIZAR PESSOA EMPRESA: " + listPessoaEmpresaRemover.get(i).toString());
        }

        List<TmktHistorico> listTmktHistorico = new TmktHistoricoDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listTmktHistorico.size(); i++) {
            listTmktHistorico.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listTmktHistorico.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR HISTÓRICO DE TELEMARKETING! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR PESSOA EMPRESA: " + listTmktHistorico.get(i).toString());
        }
        List<Links> listLinks = new LinksDao().findAllByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listLinks.size(); i++) {
            listLinks.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listLinks.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR LINKS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR LINKS: " + listLinks.get(i).toString());
        }
        if (!dao.delete(remover)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER PESSOA FÍSICA! " + dao.EXCEPCION);
            return;
        }
        novoLog.save("DELETAR PESSOA: " + remover.toString());
        if (!dao.delete(remover.getPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER PESSOA! " + dao.EXCEPCION);
            return;
        }
        if (!dao.update(manter)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA FÍSICA (PRINCIPAL)! " + dao.EXCEPCION);
            return;
        }
        if (!dao.update(manter.getPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA (PRINCIPAL)! " + dao.EXCEPCION);
            return;
        }
        novoLog.save("DELETAR PESSOA: " + remover.getPessoa().toString());
        listPessoaFisica.clear();
        dao.commit();
        novoLog.saveList();
        FisicaBean fisicaBean = new FisicaBean();
        fisicaBean.setDescPesquisa(manter.getPessoa().getNome());
        fisicaBean.acaoPesquisaParcial();
        GenericaSessao.put("fisicaBean", fisicaBean);
        GenericaMensagem.info("SUCESSO", "REGISTROS MESCLADOS");
    }

    public void forceDelete() {
        if (listPessoaFisica.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        if (listPessoaFisica.size() > 1) {
            GenericaMensagem.warn("Validação", "ESPECÍFICAR SOMENTE UMA PESSOA!");
            return;
        }
        Fisica remover = null;
        Boolean principal = false;
        for (int i = 0; i < listPessoaFisica.size(); i++) {
            if (listPessoaFisica.get(i).getSelected()) {
                principal = true;
                break;
            }
        }
        if (!principal) {
            GenericaMensagem.warn("Validação", "SELECIONAR CADASTRO A SER MANTIDO!");
            return;
        }
        for (int i = 0; i < listPessoaFisica.size(); i++) {
            if (listPessoaFisica.get(i).getSelected()) {
                remover = listPessoaFisica.get(i);
            }
        }
        if (remover == null) {
            GenericaMensagem.warn("Erro", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        Dao dao = new Dao();
        dao.openTransaction();
        List<SocioCarteirinha> scsRemover = new SocioCarteirinhaDao().findByPessoa(remover.getPessoa().getId());
        if (!scsRemover.isEmpty()) {
            for (int i = 0; i < scsRemover.size(); i++) {
                List<HistoricoCarteirinha> listHistoricoCarteirinha = new SocioCarteirinhaDao().listaHistoricoCarteirinha(scsRemover.get(i).getPessoa().getId());
                for (int j = 0; j < listHistoricoCarteirinha.size(); j++) {
                    if (!dao.delete(listHistoricoCarteirinha.get(j))) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO REMOVER HISTÓRICO DA CARTEIRINHA!" + dao.EXCEPCION);
                        return;
                    }
                    novoLog.save("MESCLAR CADASTRO HISTÓRICO CARTEIRINHA: " + listHistoricoCarteirinha.get(i).toString());
                }
                if (!dao.delete(scsRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SÓCIO CARTEIRINHA!" + dao.EXCEPCION);
                    return;
                }
                novoLog.save("REMOVER CADASTRO SÓCIO CARTEIRINHA: " + scsRemover.get(i).toString());
            }
        }
        List<PessoaEndereco> pesRemover = new PessoaEnderecoDao().pesquisaEndPorPessoa(remover.getPessoa().getId());
        if (!pesRemover.isEmpty()) {
            for (int i = 0; i < pesRemover.size(); i++) {
                if (!dao.delete(pesRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER PESSOA ENDEREÇO!");
                    return;
                }
                novoLog.save("REMOVER PESSOA ENDEREÇO: " + pesRemover.get(i).toString());
            }
        }
        PessoaComplemento pc = new PessoaComplementoDao().findByPessoa(remover.getPessoa().getId());
        if (pc != null) {
            if (!dao.delete(pc)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER PESSOA COMPLEMENTO!");
                return;
            }
            novoLog.save("REMOVER PESSOA COMPLEMENTO: " + pc.toString());
        }
        String movimentoLogString = "";
        List<Movimento> listMovimentosPessoa = new MovimentoDao().findByAllColumnsByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listMovimentosPessoa.size(); i++) {
            MovimentoBoleto movimentoBoleto = new MovimentoBoletoDao().findByMovimento(listMovimentosPessoa.get(i).getId());
            if (movimentoBoleto != null) {
                dao.delete(movimentoBoleto);
            }
            if (listMovimentosPessoa.get(i).getBaixa() != null) {
                List<FormaPagamento> fps = new FormaPagamentoDao().findByBaixa(listMovimentosPessoa.get(i).getBaixa().getId());
                for (int j = 0; j < fps.size(); j++) {
                    dao.delete(fps.get(j));
                }
                Baixa b = listMovimentosPessoa.get(i).getBaixa();
                listMovimentosPessoa.get(i).setBaixa(null);
                dao.update(listMovimentosPessoa.get(i));
                dao.delete(b);

            }
            if (listMovimentosPessoa.get(i).getAcordo() != null) {
                Acordo a = listMovimentosPessoa.get(i).getAcordo();
                listMovimentosPessoa.get(i).setAcordo(null);
                dao.update(listMovimentosPessoa.get(i));
                dao.delete(a);
            }
            List<HistoricoEmissaoGuias> heg = new HistoricoEmissaoGuiasDao().findByMovimento(listMovimentosPessoa.get(i).getId());
            for (int j = 0; j < heg.size(); j++) {
                if (!dao.delete(heg.get(j))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO HISTÓRICO DE EMISSÃO DE GUIAS!");
                    return;
                }
            }
            MovimentoInativo movimentoInativo = new MovimentoInativoDao().findByMovimento(listMovimentosPessoa.get(i).getId());
            if (movimentoInativo != null) {
                if (!dao.delete(movimentoInativo)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO EXCLUIR MOVIMENTOS INÁTIVOS!");
                    return;
                }
            }
            if (!dao.delete(listMovimentosPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER PESSOA MOVIMENTO!");
                return;
            }
            movimentoLogString += "REMOVER MOVIMENTO: " + listMovimentosPessoa.get(i).toString() + "; ";
        }
        List<Lote> listLote = new LoteDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listLote.size(); i++) {
            List<Guia> guias = new EmissaoGuiasDao().findByLote(listLote.get(i).getId());
            for (int j = 0; j < guias.size(); j++) {
                if (!dao.delete(guias.get(j))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER GUIA!");
                    return;
                }
            }
            if (!dao.delete(listLote.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA MOVIMENTO!");
                return;
            }
        }
        List<PessoaProfissao> listPessoaProfissao = new PessoaProfissaoDao().findByFisica(remover.getId());
        for (int i = 0; i < listPessoaProfissao.size(); i++) {
            if (!dao.delete(listPessoaProfissao.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER PESSOA MOVIMENTO!");
                return;
            }
            novoLog.save("DELETAR PESSOA PROFISSÃO: " + listPessoaProfissao.get(i).toString());
        }
        String servicoPessoaLog = "";
        // SERVIÇO PESSOA -> PESSOA
        List<ServicoPessoa> listServicoPessoaRemover = new ServicoPessoaDao().listAllByPessoa(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                if (!dao.delete(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        // SERVIÇO PESSOA -> COBRANÇA
        listServicoPessoaRemover = new ServicoPessoaDao().listAllByCobranca(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            servicoPessoaLog = "";
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                if (!dao.delete(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        // SERVIÇO PESSOA -> COBRANÇA MOVIMENTO
        listServicoPessoaRemover = new ServicoPessoaDao().listAllByCobrancaMovimento(remover.getPessoa().getId());
        if (!listServicoPessoaRemover.isEmpty()) {
            servicoPessoaLog = "";
            for (int i = 0; i < listServicoPessoaRemover.size(); i++) {
                if (!dao.delete(listServicoPessoaRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                    return;
                }
                servicoPessoaLog += listServicoPessoaRemover.get(i).toString() + ";";
            }
        }
        if (!servicoPessoaLog.isEmpty()) {
            novoLog.save("REMOVER SERVIÇO PESSOA : " + servicoPessoaLog);
        }
        List<MatriculaSocios> msRemover = new MatriculaSociosDao().findAllByTitular(remover.getPessoa().getId());
        for (int i = 0; i < msRemover.size(); i++) {
            if (!dao.delete(msRemover.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER MATRÍCULA SÓCIO! " + dao.EXCEPCION);
                return;
            }
        }
        List<EmailPessoa> listEmailPessoa = new EmailPessoaDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listEmailPessoa.size(); i++) {
            if (!dao.delete(listEmailPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER EMAIL PESSOA!");
                return;
            }
        }
        List<PessoaEmpresa> listPessoaEmpresaRemover = new PessoaEmpresaDao().findAllByFisica(remover.getId());
        for (int i = 0; i < listPessoaEmpresaRemover.size(); i++) {
            List<Agendamento> listAgendamento = new HomologacaoDao().pesquisaAgendamentoPorPessoaEmpresa(listPessoaEmpresaRemover.get(i).getId());
            for (int z = 0; z < listAgendamento.size(); z++) {
                Senha senha = new SenhaDao().find(listAgendamento.get(i).getId());
                if (senha != null) {
                    if (!dao.delete(senha)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO REMOVER SENHA! " + dao.EXCEPCION);
                        return;
                    }
                }
                Cancelamento cancelamento = new CancelamentoDao().findByAgendamento(listAgendamento.get(i).getId());
                if (cancelamento != null) {
                    if (!dao.delete(senha)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO REMOVER CANCELAMENTO! " + dao.EXCEPCION);
                        return;
                    }
                }
                if (!dao.delete(listAgendamento.get(z))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER AGENDAMENTO! " + dao.EXCEPCION);
                    return;
                }
            }
            if (!dao.delete(listPessoaEmpresaRemover.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER PESSOA EMPRESA! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR PESSOA EMPRESA: " + listPessoaEmpresaRemover.get(i).toString());
        }

        List<TmktHistorico> listTmktHistorico = new TmktHistoricoDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listTmktHistorico.size(); i++) {
            if (!dao.delete(listTmktHistorico.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER HISTÓRICO DE TELEMARKETING! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR PESSOA EMPRESA: " + listTmktHistorico.get(i).toString());
        }
        List<Links> listLinks = new LinksDao().findAllByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listLinks.size(); i++) {
            if (!dao.delete(listLinks.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER LINKS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR LINKS: " + listLinks.get(i).toString());
        }
        if (!dao.delete(remover)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER PESSOA FÍSICA! " + dao.EXCEPCION);
            return;
        }
        novoLog.save("DELETAR PESSOA: " + remover.toString());
        if (!dao.delete(remover.getPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER PESSOA! " + dao.EXCEPCION);
            return;
        }
        novoLog.save("DELETAR PESSOA: " + remover.getPessoa().toString());
        listPessoaFisica.clear();
        dao.commit();
        novoLog.saveList();
        FisicaBean fisicaBean = new FisicaBean();
        fisicaBean.setDescPesquisa(remover.getPessoa().getNome());
        fisicaBean.acaoPesquisaParcial();
        GenericaSessao.put("fisicaBean", fisicaBean);
        GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
    }

    public void loadDefault(Fisica f) {
        if (!listPessoaFisica.isEmpty()) {
            for (int i = 0; i < listPessoaFisica.size(); i++) {
                listPessoaFisica.get(i).setSelected(Boolean.FALSE);
                if (f.getId() == listPessoaFisica.get(i).getId()) {
                    listPessoaFisica.get(i).setSelected(Boolean.TRUE);
                }
            }
        }
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<Fisica> getListPessoaFisica() {
        return listPessoaFisica;
    }

    public void setListPessoaFisica(List<Fisica> listPessoaFisica) {
        this.listPessoaFisica = listPessoaFisica;
    }

}
