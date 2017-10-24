package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.Suspencao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.SuspencaoDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.MessagesPut;
import br.com.rtools.utilitarios.ValidaDocumentos;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.util.ArrayList;
import java.util.List;

public class FisicaUtils {

    public static Boolean validation(Fisica f, String tcase) {
        return validation(f, tcase, "", false);
    }

    public static Boolean validation(Fisica f, String tcase, String typePerson, Boolean sessionMessage) {
        ConfiguracaoSocial cs = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
        Boolean permite = true;
        Pessoa p = f.getPessoa();
        Socios s;
        List<MessagesPut> listMessages = new ArrayList();
        Integer count = 0;
        if (tcase.isEmpty()) {
            return permite;
        }
        // SÓCIO
        if (tcase.equals("socioativo") || tcase.equals("socio_titular_ativo")) {
            SociosDao sociosDB = new SociosDao();
            s = sociosDB.pesquisaSocioPorPessoa(p.getId());
            if (s.getId() == -1) {
                count++;
                if (sessionMessage) {
                    listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Pessoa não é sócia!"));
                } else {
                    GenericaMensagem.warn("Mensagem: (" + count + ")", "Pessoa não é sócia!");
                }
                permite = false;
            }
            if (!s.getServicoPessoa().isAtivo()) {
                count++;
                if (sessionMessage) {
                    listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Sócio está inátivo!"));
                } else {
                    GenericaMensagem.warn("Mensagem: (" + count + ")", "Sócio está inátivo!");
                }
                permite = false;
            }
            if (tcase.equals("socio_titular_ativo")) {
                if (s.getMatriculaSocios().getTitular().getId() != p.getId()) {
                    count++;
                    if (sessionMessage) {
                        listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Sócio não é titular!"));
                    } else {
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "Sócio não é titular!");
                    }
                    permite = false;
                }
            }
        }
        // MAIOR DE IDADE
        switch (tcase) {
            case "vendasCaravana":
                if (typePerson.equals("responsavel")) {
                    if (f.getDtNascimento() == null) {
                        if (sessionMessage) {
                            listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "INFORMAR DATA DE NASCIMENTO!"));
                        } else {
                            GenericaMensagem.warn("Mensagem: (" + count + ")", "INFORMAR DATA DE NASCIMENTO!");
                        }
                        permite = false;
                    } else if (f.getIdade() < 18) {
                        if (sessionMessage) {
                            listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "RESPONSÁVEL DEVE SER MAIOR DE IDADE!"));
                        } else {
                            GenericaMensagem.warn("Mensagem: (" + count + ")", "RESPONSÁVEL DEVE SER MAIOR DE IDADE!");
                        }
                        permite = false;
                    }
                }
                break;
        }
        // OPOSIÇÃO
        switch (tcase) {
            case "matriculaEscola":
            case "matriculaAcademia":
            case "locacaoFilme":
            case "agendamentos":
                // case "associarFisica":
                if (!p.getDocumento().isEmpty()) {
                    OposicaoDao odbt = new OposicaoDao();
                    if (odbt.existPessoaDocumentoPeriodo(p.getDocumento())) {
                        count++;
                        if (sessionMessage) {
                            listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Contém carta(s) de oposição!"));
                        } else {
                            GenericaMensagem.warn("Mensagem: (" + count + ")", "Contém carta(s) de oposição!");
                        }
                        permite = false;
                    }
                }
                break;
        }
        // DÉBITOS -- LIBERAR SE FOR UTILIZAR AUTORIZAÇÃO
        switch (tcase) {
            case "convenioMedico":
            case "matriculaAcademia":
            case "emissaoGuias":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
            case "conviteMovimento":
            case "campeonatoEquipe":
            case "agendamentos":
                GenericaSessao.remove("sessaoSisAutorizacao");
                Boolean ignoreCase = false;
                if (tcase.equals("emissaoGuias")) {
//                    SisAutorizacoes sa = new SisAutorizacoesDao().findAutorizado(3, Usuario.getUsuario().getId(), fisica.getPessoa().getId(), new ChamadaPaginaBean().getRotinaRetorno().getId());
//                    if (sa != null) {
//                        if (sa.getDtAutorizacao() == null) {
//                            count++;
//                            GenericaMensagem.warn("Mensagem: (" + count + ")", "SOLICITAÇÃO AGUARDANDO AUTORIZAÇÃO!");
//                            permite = false;
//                        } else {
//                            if (sa.getAutorizado()) {
//                                if (sa.getDtConcluido() == null) {
//                                    GenericaSessao.put("sessaoSisAutorizacao", sa);
//                                    ignoreCase = true;
//                                } else {
//                                    count++;
//                                    GenericaMensagem.warn("Mensagem: (" + count + ")", "ESTÁ SOLICITAÇÃO JÁ FOI CONCLUÍDA E PROCESSADA PELO USUÁRIO NO DIA " + sa.getConcluidoString() + "!");
//                                    permite = false;
//                                }
//                            } else {
//                                count++;
//                                GenericaMensagem.warn("Mensagem: (" + count + ")", " O GESTOR " + sa.getGestor().getPessoa().getNome() + " RECUSOU SEU PEDIDO. MOTIVO: " + sa.getMotivoRecusa());
//                                permite = false;
//
//                            }
//
//                        }
//                    }
                }
                if (!ignoreCase) {
                    FunctionsDao functionsDao = new FunctionsDao();
                    if (functionsDao.inadimplente(p.getId())) {
                        count++;
                        permite = false;
                        if (sessionMessage) {
                            listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "EXISTE(m) DÉBITO(s)!"));
                        } else {
                            GenericaMensagem.warn("Mensagem: (" + count + ")", "EXISTE(m) DÉBITO(s)!");
                        }
                        if (tcase.equals("emissaoGuias")) {
//                            solicitarAutorizacao = "debitos";
//                            loadListServicosAutorizados();
                        }
                    }
                }
                break;
        }
        // EMAIL OBRIGATÓRIO
        switch (tcase) {
            case "matriculaAcademia":
            case "matriculaEscola":
                String tipoFisica = "";
                if (GenericaSessao.exists("pesquisaFisicaTipo")) {
                    tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo");
                }
                if (tipoFisica.equals("aluno")) {
                    if (f.getPessoa().getEmail1().isEmpty()) {
                        if (cs.getObrigatorioEmail()) {
                            count++;
                            GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA CADASTRAR ALUNO!");
                            permite = false;
                        }
                    }
                }
                break;
        }

        // BLOQUEIO
        switch (tcase) {
            case "vendasCaravana":
            case "matriculaAcademia":
            case "matriculaEscola":
            case "emissaoGuias":
            case "lancamentoIndividual":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
                PessoaComplemento pc = f.getPessoa().getPessoaComplemento();
                if (pc.getBloqueiaObsAviso()) {
                    count++;
                    if (sessionMessage) {
                        listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Cadastro bloqueado!"));
                    } else {
                        GenericaMensagem.fatal("Mensagem: (" + count + ")", "Cadastro bloqueado!");
                    }
                    permite = false;
                }
                if (pc.getObsAviso() != null && !pc.getObsAviso().isEmpty()) {
                    count++;
                    GenericaMensagem.warn("Mensagem de bloqueio: (" + count + ")", pc.getObsAviso());
                }
                break;
        }

        // SUSPENSÃO
        switch (tcase) {
            case "agendamentos":
                Suspencao suspencao = new SuspencaoDao().pesquisaSuspensao(f.getPessoa());
                if (suspencao != null) {
                    count++;
                    if (sessionMessage) {
                        listMessages.add(new MessagesPut("Mensagem: (" + count + ")", "Sócio suspenso! Motivo: " + suspencao.getMotivo()));
                    } else {
                        GenericaMensagem.fatal("Mensagem: (" + count + ")", "Sócio suspenso! Motivo: " + suspencao.getMotivo());
                    }
                    permite = false;
                }
                break;
        }

        // CUPOM
        switch (tcase) {
            case "cupomMovimento":
//                getInCategoriaSocio();
//                if (inCategoriaSocio != null && !inCategoriaSocio.isEmpty()) {
//                    Socios soc = fisica.getPessoa().getSocios();
//                    if (soc == null) {
//                        count++;
//                        GenericaMensagem.fatal("Mensagem: (" + count + ")", "NECESSÁRIO SER SÓCIO!");
//                        permite = false;
//                    } else if (soc != null && soc.getId() != -1) {
//                        String[] in = inCategoriaSocio.split(",");
//                        Boolean t = false;
//                        for (int i = 0; i < in.length; i++) {
//                            if (in[i].equals("" + soc.getMatriculaSocios().getCategoria().getId())) {
//                                t = true;
//                                break;
//                            }
//                        }
//                        if (!t) {
//                            count++;
//                            GenericaMensagem.fatal("Mensagem: (" + count + ")", "Sócio não pertence a nenhuma categoria específicada no cupom!");
//                            permite = false;
//                        }
//                    }
//                }
                break;
        }
        GenericaSessao.put("messages", listMessages);
        return permite;
    }

    public static Fisica findByCPF(Fisica f) {
        if (f.getPessoa().getDocumento().equals("___.___.___-__")) {
            f.getPessoa().setDocumento("");
            return null;
        }
        if (!f.getPessoa().getDocumento().isEmpty() && !f.getPessoa().getDocumento().equals("___.___.___-__")) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(f.getPessoa().getDocumento()))) {
                GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + f.getPessoa().getDocumento());
                return null;
            }
        }
        if (f.getPessoa().getId() == -1) {
            FisicaDao db = new FisicaDao();
            List<Fisica> list = db.pesquisaFisicaPorDoc(f.getPessoa().getDocumento());
            Boolean success = false;
            if (!list.isEmpty()) {
                success = true;
                if (!FisicaUtils.validation(list.get(0), "agendamentos")) {
                    return null;
                }
            }

            if (success) {
                return list.get(0);
            }
        }
        return null;

    }
}
