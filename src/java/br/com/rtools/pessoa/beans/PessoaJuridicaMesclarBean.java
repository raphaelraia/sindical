package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.ContribuintesInativos;
import br.com.rtools.arrecadacao.Empregados;
import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.Rais;
import br.com.rtools.arrecadacao.RepisMovimento;
import br.com.rtools.arrecadacao.dao.ContribuintesInativosDao;
import br.com.rtools.arrecadacao.dao.EmpregadosDao;
import br.com.rtools.arrecadacao.dao.FolhaEmpresaDao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.arrecadacao.dao.RaisDao;
import br.com.rtools.arrecadacao.dao.RepisMovimentoDao;
import br.com.rtools.associativo.Convenio;
import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.EmissaoGuiasDao;
import br.com.rtools.associativo.dao.HistoricoEmissaoGuiasDao;
import br.com.rtools.associativo.dao.MatriculaSociosDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.cobranca.dao.TmktHistoricoDao;
import br.com.rtools.digitalizacao.Documento;
import br.com.rtools.digitalizacao.dao.DigitalizacaoDao;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.MovimentoBoleto;
import br.com.rtools.financeiro.MovimentoInativo;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.dao.DescontoServicoEmpresaDao;
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
import br.com.rtools.pessoa.dao.EnvioEmailsDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.JuridicaImportacaoDao;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.EmailPrioridade;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.sistema.dao.EmailPessoaDao;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.sistema.dao.SisAutorizacoesDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PessoaJuridicaMesclarBean implements Serializable {

    private Juridica juridica;
    private List<Juridica> listPessoaJuridica;

    @PostConstruct
    public void init() {
        juridica = new Juridica();
        listPessoaJuridica = new ArrayList();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("pessoaJuridicaMesclarBean");
        GenericaSessao.remove("juridicaPesquisa");
    }

    public void add() {
        if (juridica.getId() == -1) {
            GenericaMensagem.warn("Validação", "INFORMAR PESSOA!");
            return;
        }
        if (listPessoaJuridica.size() == 2) {
            GenericaMensagem.warn("Validação", "JÁ EXISTEM DUAS PESSOAS INSERIDAS PARA MESCLAGEM!");
            return;
        }
        for (int x = 0; x < listPessoaJuridica.size(); x++) {
            if (juridica.getId() == listPessoaJuridica.get(x).getId()) {
                GenericaMensagem.warn("Validação", "JÁ ADICIONADA!");
                return;
            }
        }
        for (int x = 0; x < listPessoaJuridica.size(); x++) {
            if (!juridica.getPessoa().getNome().toLowerCase().contains(listPessoaJuridica.get(x).getPessoa().getNome().toLowerCase())) {
                GenericaMensagem.warn("Validação", "AS PESSOA ADICIONADAS TEM NOMES DIFERENTES!");
                break;
            }
        }
        if (listPessoaJuridica.isEmpty()) {
            juridica.setSelected(true);
        }
        listPessoaJuridica.add(juridica);
        juridica = new Juridica();
    }

    public void update() {
        if (listPessoaJuridica.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        if (listPessoaJuridica.size() == 1) {
            GenericaMensagem.warn("Validação", "ESPECÍFICAR MAIS DE UMA PESSOA!");
            return;
        }
        Juridica manter = null;
        Juridica remover = null;
        Boolean principal = false;
        for (int i = 0; i < listPessoaJuridica.size(); i++) {
            if (listPessoaJuridica.get(i).getSelected()) {
                principal = true;
                break;
            }
        }
        if (!principal) {
            GenericaMensagem.warn("Validação", "SELECIONAR CADASTRO A SER MANTIDO!");
            return;
        }
        for (int i = 0; i < listPessoaJuridica.size(); i++) {
            if (listPessoaJuridica.get(i).getSelected()) {
                manter = listPessoaJuridica.get(i);
            } else {
                remover = listPessoaJuridica.get(i);
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
        List<EmailPessoa> listEmailPessoa = new EmailPessoaDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listEmailPessoa.size(); i++) {
            listEmailPessoa.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listEmailPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EMAIL PESSOA!");
                return;
            }
        }
        List<RepisMovimento> listRepisMovimento = new RepisMovimentoDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listRepisMovimento.size(); i++) {
            listRepisMovimento.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listRepisMovimento.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR REPIS MOVIMENTO!");
                return;
            }
        }
        List<DescontoServicoEmpresa> listDescontoServicoEmpresa = new DescontoServicoEmpresaDao().listaTodosPorEmpresa(remover.getId());
        for (int i = 0; i < listDescontoServicoEmpresa.size(); i++) {
            listDescontoServicoEmpresa.get(i).setJuridica(manter);
            if (!dao.update(listDescontoServicoEmpresa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EMAIL PESSOA!");
                return;
            }
        }
        List<PessoaEmpresa> listPessoaEmpresaManter = new PessoaEmpresaDao().findAllByJuridica(manter.getId());
        List<PessoaEmpresa> listPessoaEmpresaRemover = new PessoaEmpresaDao().findAllByJuridica(remover.getId());
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
                listPessoaEmpresaRemover.get(i).setJuridica(manter);
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
        List<EnvioEmails> listEnvioEmails = new EnvioEmailsDao().pesquisaTodosPorPessoa(remover.getPessoa().getId());
        Usuario u = Usuario.getUsuario();
        EmailPrioridade ep = (EmailPrioridade) dao.find(new EmailPrioridade(), 1);
        Rotina r = new Rotina().get();
        for (int i = 0; i < listEnvioEmails.size(); i++) {
            Email email = new Email();
            email.setAssunto(listEnvioEmails.get(i).getOperacao());
            email.setMensagem(listEnvioEmails.get(i).getHistorico() + " (CADASTRO MESCLADO)");
            email.setEmailPrioridade(ep);
            email.setUsuario(u);
            email.setData(listEnvioEmails.get(i).getDtEnvio());
            email.setHora("00:00");
            email.setRotina(r);
            if (!dao.save(email)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EMAIL! " + dao.EXCEPCION);
                return;
            }
            EmailPessoa emailPessoa = new EmailPessoa();
            emailPessoa.setPessoa(listEnvioEmails.get(i).getPessoa());
            emailPessoa.setEmail(email);
            emailPessoa.setDestinatario(listEnvioEmails.get(i).getEmail());
            emailPessoa.setHoraSaida("00:00");
            if (!dao.save(emailPessoa)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EMAIL PESSOA! " + dao.EXCEPCION);
                return;
            }
            if (!dao.delete(listEnvioEmails.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO EMAIL PESSOA! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR EMAIL PESSOA: " + listTmktHistorico.get(i).toString());
        }
        List<Convenio> listConvenio = new ConvenioDao().findByJuridica(remover.getId());
        for (int i = 0; i < listConvenio.size(); i++) {
            listConvenio.get(i).setJuridica(manter);
            if (!dao.update(listConvenio.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER CONVÊNIO! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR CONVÊNIO : " + listConvenio.get(i).toString());
        }
        List<Oposicao> listOposicao = new OposicaoDao().findByJuridica(remover.getId());
        for (int i = 0; i < listOposicao.size(); i++) {
            listOposicao.get(i).setJuridica(manter);
            if (!dao.update(listOposicao.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR OPOSIÇÃO! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR OPOSIÇÃO: " + listOposicao.get(i).toString());
        }
        List<Rais> listRais = new RaisDao().findByEmpresa(remover.getPessoa().getId());
        if (listRais != null) {
            for (int i = 0; i < listRais.size(); i++) {
                listRais.get(i).setEmpresa(manter);
                if (!dao.update(listRais.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR RAIS! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("ATUALIZAR RAIS: " + listRais.get(i).toString());
            }
        }
        List<FolhaEmpresa> listFolhaEmpresamManter = new FolhaEmpresaDao().findByJuridica(manter.getPessoa().getId());
        List<FolhaEmpresa> listFolhaEmpresamRemover = new FolhaEmpresaDao().findByJuridica(remover.getPessoa().getId());
        if (listFolhaEmpresamManter.isEmpty()) {
            for (int i = 0; i < listFolhaEmpresamRemover.size(); i++) {
                listFolhaEmpresamRemover.get(i).setJuridica(manter);
                if (!dao.update(listFolhaEmpresamRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR FATURAMENTO FOLHA EMPRESA! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("ATUALIZAR FATURAMENTO FOLHA EMPRESA: " + listFolhaEmpresamRemover.get(i).toString());
            }
        } else {
            for (int i = 0; i < listFolhaEmpresamRemover.size(); i++) {
                if (!dao.delete(listFolhaEmpresamRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER FATURAMENTO FOLHA EMPRESA! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("REMOVER FATURAMENTO FOLHA EMPRESA: " + listFolhaEmpresamRemover.get(i).toString());
            }
        }
        List<Empregados> listEmpregadosManter = new EmpregadosDao().findByJuridica(manter.getPessoa().getId());
        List<Empregados> listEmpregadosRemover = new EmpregadosDao().findByJuridica(remover.getPessoa().getId());
        if (listEmpregadosManter.isEmpty()) {
            for (int i = 0; i < listEmpregadosRemover.size(); i++) {
                listEmpregadosRemover.get(i).setJuridica(manter);
                if (!dao.update(listEmpregadosRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR EMPREGADOS! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("ATUALIZAR FATURAMENTO FOLHA EMPRESA: " + listFolhaEmpresamRemover.get(i).toString());
            }
        } else {
            for (int i = 0; i < listEmpregadosRemover.size(); i++) {
                if (!dao.delete(listEmpregadosRemover.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER EMPREGADOS! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("REMOVER EMPREGADOS " + listEmpregadosRemover.get(i).toString());
            }
        }
        List<Documento> listDocumentos = new DigitalizacaoDao().listaDocumento(remover.getPessoa().getId());
        for (int i = 0; i < listDocumentos.size(); i++) {
            listDocumentos.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listDocumentos.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR DOCUMENTOS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("ATUALIZAR DOCUMENTOS: " + listDocumentos.get(i).toString());
        }
        List<SisAutorizacoes> listSisAutorizacoes = new SisAutorizacoesDao().findByPessoa(remover.getPessoa().getId(), true);
        for (int i = 0; i < listSisAutorizacoes.size(); i++) {
            listSisAutorizacoes.get(i).setPessoa(manter.getPessoa());
            if (!dao.update(listSisAutorizacoes.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR AUTORIZAÇÕES DA PESSOA! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("AUTORIZAÇÕES: " + listSisAutorizacoes.get(i).toString());
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
            GenericaMensagem.warn("Erro", "AO REMOVER PESSOA JURÍDICA! " + dao.EXCEPCION);
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
            GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA JURÍDICA (PRINCIPAL)! " + dao.EXCEPCION);
            return;
        }
        if (!dao.update(manter.getPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ATUALIZAR PESSOA (PRINCIPAL)! " + dao.EXCEPCION);
            return;
        }
        novoLog.save("DELETAR PESSOA: " + remover.getPessoa().toString());
        listPessoaJuridica.clear();
        dao.commit();
        novoLog.saveList();
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.setDescPesquisa(manter.getPessoa().getNome());
        juridicaBean.acaoPesquisaParcial();
        GenericaSessao.put("juridicaBean", juridicaBean);
        GenericaMensagem.info("SUCESSO", "REGISTROS MESCLADOS");
    }

    public void forceDelete() {
        if (listPessoaJuridica.isEmpty()) {
            GenericaMensagem.warn("Validação", "NENHUMA PESSOA SELECIONADA!");
            return;
        }
        if (listPessoaJuridica.size() > 1) {
            GenericaMensagem.warn("Validação", "ESPECÍFICAR SOMENTE UMA PESSOA!");
            return;
        }
        Juridica remover = null;
        Boolean principal = false;
        for (int i = 0; i < listPessoaJuridica.size(); i++) {
            if (listPessoaJuridica.get(i).getSelected()) {
                principal = true;
                break;
            }
        }
        if (!principal) {
            GenericaMensagem.warn("Validação", "SELECIONAR CADASTRO A SER MANTIDO!");
            return;
        }
        for (int i = 0; i < listPessoaJuridica.size(); i++) {
            if (listPessoaJuridica.get(i).getSelected()) {
                remover = listPessoaJuridica.get(i);
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
        List<FolhaEmpresa> listFolhaEmpresamRemover = new FolhaEmpresaDao().findByJuridica(remover.getPessoa().getId());
        for (int i = 0; i < listFolhaEmpresamRemover.size(); i++) {
            if (!dao.delete(listFolhaEmpresamRemover.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER FATURAMENTO FOLHA EMPRESA! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("REMOVER FATURAMENTO FOLHA EMPRESA: " + listFolhaEmpresamRemover.get(i).toString());
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
                    GenericaMensagem.warn("Erro", "AO DELETAR MOVIMENTOS INÁTIVOS!");
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
                GenericaMensagem.warn("Erro", "AO DELETAR PESSOA MOVIMENTO!");
                return;
            }
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
        List<EmailPessoa> listEmailPessoa = new EmailPessoaDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listEmailPessoa.size(); i++) {
            if (!dao.delete(listEmailPessoa.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER EMAIL PESSOA!");
                return;
            }
        }
        List<JuridicaImportacao> listJuridicaImportacao = new JuridicaImportacaoDao().findByContabilidade(remover.getId());
        for (int i = 0; i < listJuridicaImportacao.size(); i++) {
            listJuridicaImportacao.get(i).setContabilidade(null);
            if (!dao.update(listJuridicaImportacao.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR CONTABILIDADE!");
                return;
            }
        }
        JuridicaImportacao juridicaImportacao = new JuridicaImportacaoDao().findByJuridica(remover.getId());
        if (juridicaImportacao != null) {
            if (!dao.delete(juridicaImportacao)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER JURÍDICA IMPORTAÇÃO! " + dao.EXCEPCION);
                return;
            }
        }
        List<PessoaEmpresa> listPessoaEmpresaRemover = new PessoaEmpresaDao().findAllByJuridica(remover.getId());
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
        List<Convenio> listConvenio = new ConvenioDao().findByJuridica(remover.getId());
        for (int i = 0; i < listConvenio.size(); i++) {
            if (!dao.delete(listConvenio.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER CONVÊNIO! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR PESSOA EMPRESA: " + listTmktHistorico.get(i).toString());
        }
        List<Oposicao> listOposicao = new OposicaoDao().findByJuridica(remover.getId());
        for (int i = 0; i < listOposicao.size(); i++) {
            if (!dao.delete(listOposicao.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO Oposicao! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR OPOSIÇÃO: " + listTmktHistorico.get(i).toString());
        }
        List<EnvioEmails> listEnvioEmails = new EnvioEmailsDao().pesquisaTodosPorPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listEnvioEmails.size(); i++) {
            if (!dao.delete(listEnvioEmails.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ENVIO DE E-MAILS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR ENVIO DE E-MAILS: " + listEnvioEmails.get(i).toString());
        }
        List<Rais> listRais = new RaisDao().findByEmpresa(remover.getPessoa().getId());
        if (listRais != null) {
            for (int i = 0; i < listRais.size(); i++) {
                if (!dao.delete(listRais.get(i))) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR RAIS! " + dao.EXCEPCION);
                    return;
                }
                novoLog.save("DELETAR RAIS: " + listRais.get(i).toString());
            }
        }
        List<Empregados> listEmpregadosRemover = new EmpregadosDao().findByJuridica(remover.getPessoa().getId());
        for (int i = 0; i < listEmpregadosRemover.size(); i++) {
            if (!dao.delete(listEmpregadosRemover.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR EMPREGADOS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR FATURAMENTO FOLHA EMPRESA: " + listEmpregadosRemover.get(i).toString());
        }
        List<Documento> listDocumentos = new DigitalizacaoDao().listaDocumento(remover.getPessoa().getId());
        for (int i = 0; i < listDocumentos.size(); i++) {
            if (!dao.delete(listDocumentos.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR DOCUMENTOS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR DOCUMENTOS: " + listDocumentos.get(i).toString());
        }
        List<SisAutorizacoes> listSisAutorizacoes = new SisAutorizacoesDao().findByPessoa(remover.getPessoa().getId(), true);
        for (int i = 0; i < listSisAutorizacoes.size(); i++) {
            if (!dao.delete(listSisAutorizacoes.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR AUTORIZAÇÕES DA PESSOA! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR AUTORIZAÇÕES: " + listSisAutorizacoes.get(i).toString());
        }
        List<Links> listLinks = new LinksDao().findAllByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listLinks.size(); i++) {
            if (!dao.delete(listLinks.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR LINKS! " + dao.EXCEPCION);
                return;
            }
            novoLog.save("DELETAR LINKS: " + listLinks.get(i).toString());
        }
        List<Juridica> listEmpresasDaContabilidade = new JuridicaDao().findByContabilidade(remover.getId());
        for (int i = 0; i < listEmpresasDaContabilidade.size(); i++) {
            listEmpresasDaContabilidade.get(i).setContabilidade(null);
            listEmpresasDaContabilidade.get(i).setCobrancaEscritorio(false);
            if (!dao.update(listEmpresasDaContabilidade.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR EMPRESA VÍNCULADA!");
                return;
            }
            novoLog.save("DELETAR EMPRESA VÍNCULADA: " + listEmpresasDaContabilidade.get(i).toString());
        }
        List<ContribuintesInativos> listContribuintesInativos = new ContribuintesInativosDao().findByJuridica(remover.getId());
        for (int i = 0; i < listContribuintesInativos.size(); i++) {
            if (!dao.delete(listContribuintesInativos.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR CONTRIBUINTE INATIVO!");
                return;
            }
            novoLog.save("DELETAR CONTRIBUINTE INÁTIVO: " + listContribuintesInativos.get(i).toString());
        }
        List<RepisMovimento> listRepisMovimento = new RepisMovimentoDao().findByPessoa(remover.getPessoa().getId());
        for (int i = 0; i < listRepisMovimento.size(); i++) {
            if (!dao.delete(listRepisMovimento.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO DELETAR REPIS MOVIMENTO!");
                return;
            }
            novoLog.save("DELETAR REPIS MOVIMENTO: " + listRepisMovimento.get(i).toString());
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
        listPessoaJuridica.clear();
        dao.commit();
        novoLog.saveList();
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.setDescPesquisa(remover.getPessoa().getNome());
        juridicaBean.acaoPesquisaParcial();
        GenericaSessao.put("juridicaBean", juridicaBean);
        GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
    }

    public void loadDefault(Fisica f) {
        if (!listPessoaJuridica.isEmpty()) {
            for (int i = 0; i < listPessoaJuridica.size(); i++) {
                listPessoaJuridica.get(i).setSelected(Boolean.FALSE);
                if (f.getId() == listPessoaJuridica.get(i).getId()) {
                    listPessoaJuridica.get(i).setSelected(Boolean.TRUE);
                }
            }
        }
    }

    public Juridica getJuridica() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public List<Juridica> getListPessoaJuridica() {
        return listPessoaJuridica;
    }

    public void setListPessoaJuridica(List<Juridica> listPessoaJuridica) {
        this.listPessoaJuridica = listPessoaJuridica;
    }

}
