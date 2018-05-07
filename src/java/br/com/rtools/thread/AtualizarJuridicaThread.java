package br.com.rtools.thread;

import br.com.rtools.arrecadacao.ContribuintesInativos;
import br.com.rtools.arrecadacao.MotivoInativacao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.JuridicaReceitaAutomatica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoLog;
import br.com.rtools.sistema.ProcessoAutomaticoRegistros;
import br.com.rtools.sistema.dao.ProcessoAutomaticoRegistrosDao;
import br.com.rtools.sistema.utils.ProcessoAutomaticoUtils;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.JuridicaReceitaJSON;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Claudemir Rtools
 */
//public class AtualizarJuridicaThread extends InheritableThreadLocal<Void>, DB {
public class AtualizarJuridicaThread extends ThreadLocal<Object> {

//    String query = "";
    List<Juridica> listaJuridica = new ArrayList();

    public AtualizarJuridicaThread(List<Juridica> lista) {
        this.listaJuridica = lista;
//        this.query = query;
    }

    /**
     * NÃO FUNCIONA COM SESSÃO
     */
    public void run() {
        FutureTask theTask;
        theTask = new FutureTask(new Runnable() {
            @Override
            public void run() {
                try {
                    metodo();
                } catch (Exception e) {
                    e.getMessage();
                }
                if (Thread.interrupted()) {
                }
            }
        }, null);
        new Thread(theTask).start();
    }

    public void runDebug() {
        metodo();
    }

    public Void metodo() {

        if (listaJuridica.isEmpty()) {
            return null;
        }

        //List<Juridica> lista_juridica = new AtualizacaoAutomaticaJuridicaDao().listaJuridicaParaAtualizacao(this.query);
        Dao dao = new Dao();
        System.err.println("Começou a thread");

        ProcessoAutomatico pa = new ProcessoAutomatico();
        pa.setDataInicio(DataHoje.dataHoje());
        pa.setHoraInicio(DataHoje.hora());
        pa.setUsuario(Usuario.getUsuario());
        pa.setTodosUsuarios(false);
        pa.setNrProgresso(0);
        pa.setNrProgressoFinal(listaJuridica.size());
        pa.setProcesso("atualizar_juridica");

        dao.save(pa, true);

        for (int i = 0; i < listaJuridica.size(); i++) {
            ProcessoAutomaticoRegistros par = new ProcessoAutomaticoRegistros();
            par.setProcessoAutomatico(pa);
            par.setTabela("pes_juridica");
            par.setColuna("id");
            par.setCodigo(listaJuridica.get(i).getId());
            dao.save(par, true);

        }

        ProcessoAutomaticoUtils.execute(pa);

        return null;
    }

    public static void init(ProcessoAutomatico pa) {
        List<ProcessoAutomaticoRegistros> list = new ProcessoAutomaticoRegistrosDao().find(pa.getId());
        Dao dao = new Dao();
        for (int i = 0; i < list.size(); i++) {
            ProcessoAutomaticoRegistros pr = (ProcessoAutomaticoRegistros) dao.rebind(dao.find(list.get(i)));
            if (pr.getDtConclusao() == null) {
                Juridica j = (Juridica) dao.find(new Juridica(), list.get(i).getCodigo());
                try {
                    String retorno = atualizar(j, pa.getUsuario());

                    ProcessoAutomaticoLog pal = new ProcessoAutomaticoLog(
                            -1,
                            pa,
                            "[" + DataHoje.hora() + "] N° " + (i + 1) + "\n"
                            + (!retorno.isEmpty() ? "[" + retorno + "]\n" : " ")
                            + " Juridica ID: " + j.getId() + "\n"
                            + " Juridica Nome: " + j.getPessoa().getNome()
                    );

                    list.get(i).setDtConclusao(new Date());
                    dao.update(list.get(i), true);

                    dao.save(pal, true);
                    pa.setNrProgresso(i + 1);

                    dao.update(pa, true);

                    System.err.println("Empresa Atualizada N° " + i + ": " + j.getPessoa().getNome() + " [" + retorno + "]");
                    // TEM QUE SER 2 SEG. SE NÃO TRAVA
                    Thread.sleep(2000);
                    dao.refresh(pa);

                    if (pa.getCancelarProcesso()) {
                        break;
                    }
                } catch (Exception e) {
                    if (j != null) {
                        System.err.println("[Erro] Empresa Atualizada N° " + i + ": " + j.getPessoa().getNome());
                    }
                }
            }
        }
    }

    public static String atualizar(Juridica juridica, Usuario usuario) {
        try {
            if (juridica.getPessoa().getDocumento().isEmpty()) {
                return "Documento Vazio";
            }

            String documento = AnaliseString.extrairNumeros(juridica.getPessoa().getDocumento());

            JuridicaDao dbj = new JuridicaDao();
            JuridicaReceitaAutomatica juridicaRA_nova, juridicaRA_antiga;

            Dao dao = new Dao();

            juridica = (Juridica) dao.find(juridica);

            // tipo = wokki = pago / '' = gratis
            JuridicaReceitaJSON.JuridicaReceitaObject jro = new JuridicaReceitaJSON(documento, "hubdodesenvolvedor").pesquisar();

            if (jro.getStatus() == 6) {
                return "Limite de acessos excedido!";
            }

            if (jro.getStatus() == 1) {
                return "Atualizando esse CNPJ na receita, pesquise novamente em 30 segundos!";
            }

            if (jro.getStatus() != 0) {
                return jro.getMsg();
            }

            // JURIDICA QUE VEIO DA RECEITA
            juridicaRA_nova = new JuridicaReceitaAutomatica(
                    -1,
                    juridica.getPessoa(),
                    DataHoje.dataHoje(),
                    documento,
                    jro.getNome_empresarial(),
                    jro.getTitulo_estabelecimento(),
                    jro.getCep(),
                    jro.getLogradouro(),
                    jro.getBairro(),
                    jro.getComplemento(),
                    jro.getNumero(),
                    jro.getAtividade_principal(),
                    jro.getSituacao_cadastral(),
                    DataHoje.converte(jro.getData_abertura()),
                    jro.getAtividades_secundarias(),
                    jro.getMunicipio(),
                    jro.getUf(),
                    jro.getEmail_rf(),
                    jro.getTelefone_rf(),
                    Boolean.TRUE
            );

            // ---
            PessoaEndereco pex = new PessoaEnderecoDao().pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 3);
            // JURIDICA QUE FOI ATUALIZADA
            List listax = dbj.listaJuridicaContribuinte(juridica.getId());
            String status_j;
            if (listax.isEmpty()) {
                status_j = "NÃO CONTRIBUINTE";
            } else if (((List) listax.get(0)).get(11) != null) {
                status_j = "CONTRIBUINTE INATIVO";
            } else {
                status_j = "ATIVO";
            }

            juridicaRA_antiga = new JuridicaReceitaAutomatica(
                    -1,
                    juridica.getPessoa(),
                    DataHoje.dataHoje(),
                    documento,
                    juridica.getPessoa().getNome(),
                    juridica.getFantasia(),
                    AnaliseString.mascaraCep(pex.getEndereco().getCep()),
                    pex.getEndereco().getLogradouro().getDescricao(),
                    pex.getEndereco().getBairro().getDescricao(),
                    pex.getComplemento(),
                    pex.getNumero(),
                    juridica.getCnae().getCnae(),
                    status_j, // SITUAÇÃO CADASTRAL
                    juridica.getDtAbertura(),
                    "", // CNAE SEGUNDARIO
                    pex.getEndereco().getCidade().getCidade(),
                    pex.getEndereco().getCidade().getUf(),
                    juridica.getPessoa().getEmail1(),
                    juridica.getPessoa().getTelefone1(),
                    Boolean.FALSE
            );
            // ---

            dao.openTransaction();

            if (!dao.save(juridicaRA_nova)) {
                dao.rollback();
                return "Erro ao Salvar Juridica Receita Nova!";
            }

            if (!dao.save(juridicaRA_antiga)) {
                dao.rollback();
                return "Erro ao Salvar Juridica Receita Antiga!";
            }

            dao.commit();

//            juridica.getPessoa().setDtRecadastro(DataHoje.dataHoje());
//            // NOME
//            if (juridica.getPessoa().getNome() == null || juridica.getPessoa().getNome().isEmpty()) {
//                juridica.getPessoa().setNome(juridicaRA_nova.getNome().toUpperCase());
//            }
//
//            // FANTASIA
//            if (juridica.getFantasia() == null || juridica.getFantasia().isEmpty()) {
//                juridica.setFantasia(juridicaRA_nova.getFantasia().toUpperCase());
//            }
//
//            // EMAIL 1
//            if (juridica.getPessoa().getEmail1() == null || juridica.getPessoa().getEmail1().isEmpty()) {
//                juridica.getPessoa().setEmail1(jro.getEmail1());
//            }
//
//            // EMAIL 2
//            if (juridica.getPessoa().getEmail2() == null || juridica.getPessoa().getEmail2().isEmpty()) {
//                juridica.getPessoa().setEmail2(jro.getEmail2());
//            }
//
//            // EMAIL 3
//            if (juridica.getPessoa().getEmail3() == null || juridica.getPessoa().getEmail3().isEmpty()) {
//                juridica.getPessoa().setEmail3(jro.getEmail3());
//            }
//
//            // TELEFONE 1
//            if (juridica.getPessoa().getTelefone1() == null || juridica.getPessoa().getTelefone1().isEmpty()) {
//                juridica.getPessoa().setTelefone1(jro.getTelefone1());
//            }
//
//            // TELEFONE 2
//            if (juridica.getPessoa().getTelefone2() == null || juridica.getPessoa().getTelefone2().isEmpty()) {
//                juridica.getPessoa().setTelefone2(jro.getTelefone2());
//            }
//
//            // TELEFONE 3
//            if (juridica.getPessoa().getTelefone3() == null || juridica.getPessoa().getTelefone3().isEmpty()) {
//                juridica.getPessoa().setTelefone3(jro.getTelefone3());
//            }
//
//            if (!dao.update(juridica.getPessoa(), true)) {
//                return "Erro ao Salvar Pessoa Receita Antiga!";
//            }
//
//            if (!dao.update(juridica, true)) {
//                return "Erro ao Salvar Juridica Receita Antiga!";
//            }
//
//            if (!jro.getSituacao_cadastral().toLowerCase().equals("ativo") && !jro.getSituacao_cadastral().toLowerCase().equals("ativa") && !jro.getSituacao_cadastral().isEmpty()) {
//                if (!inativarContribuintes(juridica, jro.getSituacao_cadastral(), usuario)) {
//                    return "Erro ao Inativar Empresa!";
//                }
//            }
        } catch (Exception e) {
            //System.err.println(e.getMessage());
            return (e.getMessage() == null) ? "ERRO NA PESQUISA WEB SERVICE" : e.getMessage();
        }
        return "";
    }

    public static Boolean inativarContribuintes(Juridica juridica, String obs, Usuario usuario) {
        try {
            Dao dao = new Dao();

            NovoLog logs = new NovoLog();

            logs.setCodigo(juridica.getId());
            logs.setTabela("arr_contribuintes_inativos");

            ContribuintesInativos ci = new ContribuintesInativos(
                    -1,
                    juridica,
                    "",
                    DataHoje.data(),
                    (MotivoInativacao) dao.find(new MotivoInativacao(), 1),
                    usuario.getPessoa().getNome(),
                    obs
            );

            dao.openTransaction();

            if (!dao.save(ci)) {
                dao.rollback();
                return false;
            }

            PessoaEmpresaDao dbp = new PessoaEmpresaDao();
            List<PessoaEmpresa> result = dbp.listaPessoaEmpresaPorJuridica(juridica.getId());
            String ids_pessoa_empresa = "";
            if (!result.isEmpty()) {
                for (PessoaEmpresa pe : result) {
                    pe.setPrincipal(false);
                    pe.setDemissao(DataHoje.data());

                    if (!dao.update(pe)) {
                        dao.rollback();
                        return false;
                    }

                    if (ids_pessoa_empresa.isEmpty()) {
                        ids_pessoa_empresa = "" + pe.getId();
                    } else {
                        ids_pessoa_empresa += ", " + pe.getId();
                    }
                }
            }

            dao.commit();

            logs.update("",
                    "** Inativação de Empresas Automática **\n"
                    + " ID: " + juridica.getId() + "\n"
                    + " NOME: " + juridica.getPessoa().getNome() + "\n"
                    + " MOTIVO: " + ci.getMotivoInativacao().getDescricao() + "\n"
                    + " SOCILITANTE: " + ci.getSolicitante() + "\n"
                    + " OBS: " + ci.getObservacao() + "\n"
                    + " ATIVAÇÃO: " + ci.getAtivacao() + "\n"
                    + " INATIVAÇÃO: " + ci.getInativacao() + "\n"
                    + " PESSOA EMPRESA ID: {" + ids_pessoa_empresa + "}"
            );
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
        return true;
    }

}
