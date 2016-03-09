/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.thread;

import br.com.rtools.arrecadacao.ContribuintesInativos;
import br.com.rtools.arrecadacao.MotivoInativacao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.JuridicaReceitaAutomatica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.db.JuridicaDB;
import br.com.rtools.pessoa.db.JuridicaDBToplink;
import br.com.rtools.pessoa.db.PessoaEmpresaDB;
import br.com.rtools.pessoa.db.PessoaEmpresaDBToplink;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoLog;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.JuridicaReceitaJSON;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import javax.faces.model.SelectItem;

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
        try {
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
                String retorno = atualizar(listaJuridica.get(i));

                ProcessoAutomaticoLog pal = new ProcessoAutomaticoLog(
                        -1,
                        pa,
                        "[" + DataHoje.hora() + "] N° " + (i + 1) + "\n"
                        + (!retorno.isEmpty() ? "[" + retorno + "]\n" : " ")
                        + " Juridica ID: " + listaJuridica.get(i).getId() + "\n"
                        + " Juridica Nome: " + listaJuridica.get(i).getPessoa().getNome() + "["+retorno+"]"
                );

//                dao.refresh(pa);
//                if ( !(pa).getDataFinalString().isEmpty() ){
//                    pa.setDataFinal(null);
//                    pa.setHoraFinal("");
//                    pa.setVisualizadoFimProcesso(Boolean.FALSE);
//                    dao.update(pa, true);
//                }
                
                dao.save(pal, true);
                pa.setNrProgresso(i + 1);

                dao.update(pa, true);

                System.err.println("Empresa Atualizada N° " + i + ": " + listaJuridica.get(i).getPessoa().getNome());
                // TEM QUE SER 2 SEG. SE NÃO TRAVA
                Thread.sleep(2000);
            }
            System.err.println("Terminou a thread");

            pa.setDataFinal(DataHoje.dataHoje());
            pa.setHoraFinal(DataHoje.hora());

            dao.update(pa, true);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public String atualizar(Juridica juridica) {
        try {
            if (juridica.getPessoa().getDocumento().isEmpty()) {
                return "Documento Vazio";
            }

            String documento = AnaliseString.extrairNumeros(juridica.getPessoa().getDocumento());

            JuridicaDB dbj = new JuridicaDBToplink();
            JuridicaReceitaAutomatica juridicaRA_nova, juridicaRA_antiga;

            Dao dao = new Dao();

            juridica = (Juridica) dao.find(juridica);

            // tipo = wokki = pago / '' = gratis
            JuridicaReceitaJSON.JuridicaReceitaObject jro = new JuridicaReceitaJSON(documento, "").pesquisar();

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

            juridica.getPessoa().setNome(juridicaRA_nova.getNome().toUpperCase());
            juridica.setFantasia(juridicaRA_nova.getFantasia().toUpperCase());

            juridica.getPessoa().setEmail1(jro.getEmail1());
            juridica.getPessoa().setEmail2(jro.getEmail2());
            juridica.getPessoa().setEmail3(jro.getEmail3());

            juridica.getPessoa().setTelefone1(jro.getTelefone1());
            juridica.getPessoa().setTelefone2(jro.getTelefone2());
            juridica.getPessoa().setTelefone3(jro.getTelefone3());

            if (!dao.update(juridica, true)) {
                return "Erro ao Salvar Juridica Receita Antiga!";
            }

            if (!jro.getSituacao_cadastral().toLowerCase().equals("ativo") && !jro.getSituacao_cadastral().toLowerCase().equals("ativa") && !jro.getSituacao_cadastral().isEmpty()) {
                if (!inativarContribuintes(juridica, jro.getSituacao_cadastral())) {
                    return "Erro ao Inativar Empresa!";
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return e.getMessage();
        }
        return "";
    }

    public Boolean inativarContribuintes(Juridica juridica, String obs) {
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
                    Usuario.getUsuario().getPessoa().getNome(),
                    obs
            );

            dao.openTransaction();

            if (!dao.save(ci)) {
                dao.rollback();
                return false;
            }

            PessoaEmpresaDB dbp = new PessoaEmpresaDBToplink();
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
