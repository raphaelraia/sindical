package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.associativo.beans.MovimentosReceberSocialBean;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.MovimentosReceberBean;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.FTipoDocumentoDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.Links;
import br.com.rtools.sistema.dao.ConfiguracaoDepartamentoDao;
import br.com.rtools.sistema.dao.LinksDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.EnviarEmail;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class AcordoBean implements Serializable {

    private List<GridAcordo> listaVizualizado = new ArrayList();
    private List<GridAcordo> listTotalizacao = new ArrayList();
    private List<DataObject> listaOperado = new ArrayList();
    private Acordo acordo = new Acordo();
    private int idServicos = 0;
    private int idVencimento = 0;
    private int idVencimentoSind = 0;
    private int parcela = 1;
    private String valorEntrada = "0";
    private String valorEntradaSind = "0";
    private String vencimento = DataHoje.data();
    private int frequencia = 30;
    private int frequenciaSind = 30;
    private List<int[]> quantidade = new ArrayList();
    List<Boolean> listaMarcados = new ArrayList();
    private String ultimaData = "";
    //private String mensagem = "";
    private boolean imprimeVerso = false;
    private Historico historico = new Historico();
    private boolean imprimir = true;
    private boolean imprimir_pro = false;
    private List<Movimento> listaMovs = new ArrayList();
    private Pessoa pessoa = new Pessoa();
    private Pessoa pessoaEnvio = new Pessoa();
    private String emailPara = "contabilidade";

    private String emailContato = "";
    private String emailAntigo = "";

    public String converteValorString(String valor) {
        return Moeda.converteR$(valor);
    }

    public void alterarEmailEnvio(Boolean alterarEmail) {
        if (alterarEmail) {
            JuridicaDao db = new JuridicaDao();
            Juridica jur = db.pesquisaJuridicaPorPessoa(pessoa.getId());

            Dao dao = new Dao();
            if (emailPara.equals("contabilidade")) {
                if (!jur.getContabilidade().getPessoa().getEmail1().equals(pessoaEnvio.getEmail1())) {

                }
                jur.getContabilidade().getPessoa().setEmail1(pessoaEnvio.getEmail1());
                dao.update(jur.getContabilidade().getPessoa(), true);
                pessoaEnvio = jur.getContabilidade().getPessoa();
            } else {
                jur.getPessoa().setEmail1(pessoaEnvio.getEmail1());
                dao.update(jur.getPessoa(), true);
                pessoaEnvio = jur.getPessoa();
            }
        }

        send();
        PF.update("form_acordo");
    }

    public void confirmarVerificarEmail() {
        if (pessoaEnvio.getEmail1().isEmpty() || pessoaEnvio.getEmail1().length() < 5) {
            GenericaMensagem.warn("Atenção", "Digite um email válido!");
            PF.openDialog("form_acordo:i_panel_enviar_email");
            return;
        }

        JuridicaDao db = new JuridicaDao();
        Juridica jur = db.pesquisaJuridicaPorPessoa(pessoa.getId());

        if (emailPara.equals("contabilidade")) {
            if (!jur.getContabilidade().getPessoa().getEmail1().equals(pessoaEnvio.getEmail1())) {
                emailAntigo = jur.getContabilidade().getPessoa().getEmail1();
                PF.openDialog("dlg_atualizar_email");
                PF.update("form_acordo:panel_atualizar_email");
                return;
            }
        } else if (!jur.getPessoa().getEmail1().equals(pessoaEnvio.getEmail1())) {
            emailAntigo = jur.getPessoa().getEmail1();
            PF.openDialog("dlg_atualizar_email");
            PF.update("form_acordo:panel_atualizar_email");
            return;
        }
        send();
        PF.update("form_acordo");
    }

    public void verificaEmail() {
        JuridicaDao db = new JuridicaDao();
        Juridica jur = db.pesquisaJuridicaPorPessoa(pessoa.getId());

        if (emailPara.equals("contabilidade")) {
            if (jur.getContabilidade() == null) {
                GenericaMensagem.warn("Atenção", "Empresa sem contabilidade vinculada!");
                pessoaEnvio = new Pessoa();
                PF.update("form_acordo");
                return;
            }

            if (!jur.getContabilidade().getPessoa().getEmail1().isEmpty()) {
                pessoaEnvio = jur.getContabilidade().getPessoa();
            } else {
                pessoaEnvio = new Pessoa();
            }

            PF.openDialog("dlg_enviar_email");
        } else {
            if (!jur.getPessoa().getEmail1().isEmpty()) {
                pessoaEnvio = jur.getPessoa();
            }

            PF.openDialog("dlg_enviar_email");
        }
    }

    public void send( ) {
        if (pessoaEnvio.getEmail1().isEmpty()) {
            GenericaMensagem.info("Validação", "Informar e-mail");
            return;
        }

        // CARREGA A LISTAGEM DOS BOLETOS, VALORES E VENCIMENTOS
        List<Movimento> listaImp = new ArrayList();
        List<Float> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();
        Registro registro = Registro.get();
        for (int i = 0; i < listaOperado.size(); i++) {
            listaImp.add(((Movimento) listaOperado.get(i).getArgumento2()));
            listaValores.add(((Movimento) listaOperado.get(i).getArgumento2()).getValor());
            listaVencimentos.add(((Movimento) listaOperado.get(i).getArgumento2()).getVencimento());
        }
        if (listaImp.isEmpty() && pessoaEnvio.getId() == -1) {
            return;
        }
        String filename = "";
        String assunto = "";
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos");
        if (!new File(path + "/downloads/boletos").exists()) {
            File file = new File(path + "/downloads/boletos");
            file.mkdirs();
        }
        List<File> fls = new ArrayList<>();
        for (int i = 0; i < listaImp.size(); i++) {
            ImprimirBoleto imp = new ImprimirBoleto();
            imp.imprimirBoleto(listaImp, listaValores, listaVencimentos, imprimeVerso);
            filename = imp.criarLink(listaImp.get(i).getPessoa(), registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            if (listaImp.size() == 1) {
                assunto = "Boleto " + listaImp.get(0).getServicos().getDescricao() + " N° " + listaImp.get(0).getDocumento();
            } else {
                assunto = "Boleto";
            }
            if (registro.isEnviarEmailAnexo()) {
                fls.add(new File(imp.getPathPasta() + "/" + filename));
            }
            break;
        }

        try {
            List<Pessoa> p = new ArrayList();
            p.add(pessoaEnvio);
            Mail mail = new Mail();
            Email email = new Email(
                    -1,
                    DataHoje.dataHoje(),
                    DataHoje.horaMinuto(),
                    (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                    new Rotina().get(),
                    null,
                    assunto,
                    "",
                    false,
                    false
            );
            if (registro.isEnviarEmailAnexo()) {
                mail.setFiles(fls);
                email.setMensagem("<h5>Baixe seu boleto anexado neste email</h5><br /><br />");
            } else {
                email.setMensagem(" <h5>Visualize seu boleto clicando no link abaixo</h5><br /><br />    "
                        + " <a href='" + registro.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + filename + "" + "' target='_blank'>Clique aqui para abrir boleto</a><br />"
                );
            }
            mail.setEmail(email);
            List<EmailPessoa> emailPessoas = new ArrayList<>();
            EmailPessoa emailPessoa = new EmailPessoa();
            List<Pessoa> pessoas = (List<Pessoa>) p;
            for (Pessoa p1 : pessoas) {
                emailPessoa.setDestinatario(p1.getEmail1());
                emailPessoa.setPessoa(p1);
                emailPessoa.setRecebimento(null);
                emailPessoas.add(emailPessoa);
                mail.setEmailPessoas(emailPessoas);
                emailPessoa = new EmailPessoa();
            }
            ConfiguracaoDepartamento configuracaoDepartamento;
            if (MacFilial.getAcessoFilial().getId() != -1) {
                configuracaoDepartamento = new ConfiguracaoDepartamentoDao().findBy(14, MacFilial.getAcessoFilial().getFilial().getId());
                if (configuracaoDepartamento != null) {
                    mail.setConfiguracaoDepartamento(configuracaoDepartamento);
                }
            }
            String[] retorno = mail.send("personalizado");
            if (!retorno[1].isEmpty()) {
                GenericaMensagem.warn("E-mail", retorno[1]);
            } else {
                GenericaMensagem.info("E-mail", retorno[0]);
            }
            if (!mail.getEmailArquivos().isEmpty()) {
                for(int i = 0; i < fls.size(); i++) {
                    fls.get(i).delete();
                }
            }
        } catch (Exception e) {
            NovoLog log = new NovoLog();
            log.live("Erro de envio de boleto acordo por e-mail: Mensagem: " + e.getMessage() + " - Causa: " + e.getCause() + " - Caminho: " + e.getStackTrace().toString());
        }
    }

    public String sends() {
        List<Movimento> listaImp = new ArrayList();
        List<Float> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();
        Registro reg = new Registro();

        for (int i = 0; i < listaOperado.size(); i++) {
            listaImp.add(((Movimento) listaOperado.get(i).getArgumento2()));
            listaValores.add(((Movimento) listaOperado.get(i).getArgumento2()).getValor());
            listaVencimentos.add(((Movimento) listaOperado.get(i).getArgumento2()).getVencimento());
        }
        if (!listaImp.isEmpty() && pessoaEnvio.getId() != -1) {
            for (int i = 0; i < listaImp.size(); i++) {
                ImprimirBoleto imp = new ImprimirBoleto();
                imp.imprimirBoleto(listaImp, listaValores, listaVencimentos, imprimeVerso);
                String patch = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos");
                if (!new File(patch + "/downloads").exists()) {
                    File file = new File(patch + "/downloads");
                    file.mkdir();
                }
                if (!new File(patch + "/downloads/boletos").exists()) {
                    File file = new File(patch + "/downloads/boletos");
                    file.mkdir();
                }
                String nome = imp.criarLink(listaImp.get(i).getPessoa(), reg.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");

                reg = Registro.get();
                List<Pessoa> p = new ArrayList();
                p.add(pessoaEnvio);

                String[] ret = new String[2];
                String nome_envio = "";
                if (listaImp.size() == 1) {
                    nome_envio = "Boleto " + listaImp.get(0).getServicos().getDescricao() + " N° " + listaImp.get(0).getDocumento();
                } else {
                    nome_envio = "Boleto";
                }

                if (!reg.isEnviarEmailAnexo()) {
                    ret = EnviarEmail.EnviarEmailPersonalizado(
                            reg,
                            p,
                            " <h5>Visualize seu boleto clicando no link abaixo</5><br /><br />"
                            + " <a href='" + reg.getUrlPath() + "/Sindical/acessoLinks.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;arquivo=" + nome + "' target='_blank'>Clique aqui para abrir boleto</a><br />",
                            new ArrayList(),
                            nome_envio
                    );
                } else {
                    List<File> fls = new ArrayList();
                    fls.add(new File(imp.getPathPasta() + "/" + nome));

                    ret = EnviarEmail.EnviarEmailPersonalizado(
                            reg,
                            p,
                            " <h5>Baixe seu boleto anexado neste email</5><br /><br />",
                            fls,
                            nome_envio
                    );
                }
                if (!ret[1].isEmpty()) {
                    GenericaMensagem.warn("Atenção", ret[1]);
                } else {
                    GenericaMensagem.info("OK", ret[0]);

                }
                listaImp.clear();
                p.clear();
            }
        }
        return null;
    }

    public List<SelectItem> getListaVencimento() {
        List<SelectItem> vencto = new Vector<SelectItem>();
        int i = 0;
        DataHoje data = new DataHoje();
        vencto.add(new SelectItem(
                i,
                vencimento)
        );
        i++;
        while (i < 31) {
            vencto.add(new SelectItem(
                    i,
                    data.incrementarDias(i, vencimento))
            );
            i++;
        }
        return vencto;
    }

    public void imprimirAcordo() {
//        if ((!opMovimentos.getListaAcordo().isEmpty()) && (mensagem.equals("Imprimir Boletos"))){
//            OperacaoMovimento opAcordo = new OperacaoMovimento(opMovimentos.getListaAcordo());
//            opAcordo.imprimirBoleto(imprimeVerso);
//        }
    }

    public List getListaFolha() {
        return new Dao().list(new FolhaEmpresa());
    }

    public void refreshForm() {
    }

    public List<GridAcordo> getListaVizualizado() {
        if (listaVizualizado.isEmpty() && !listaMovs.isEmpty() && pessoa.getId() != -1) {
            String historicoString = "";
            historico.setHistorico("Acordo correspondente a: ");
            Integer rotina_id = 4;
            ServicoRotinaDao srd = new ServicoRotinaDao();
            String breakLine = "\n";
            for (int i = 0; i < listaMovs.size(); i++) {
                boolean next = true;
                boolean next2 = true;
                if (srd.existeServicoRotina(listaMovs.get(i).getServicos().getId(), rotina_id)) {
                    if (listaMovs.get(i).getServicos().getId() == 1) {
                        valorEntradaSind += Moeda.converteR$Float(listaMovs.get(i).getValorBaixa());
                    } else {
                        for (int x = 0; x < listaVizualizado.size(); x++) {
                            if (listaVizualizado.get(x).getServicos().getId() == listaMovs.get(i).getServicos().getId()) {
                                listaVizualizado.get(x).setValorBaixa(+listaVizualizado.get(x).getValorBaixa() + listaMovs.get(i).getValorBaixa());
                                if (listaVizualizado.get(x).getHistorico().isEmpty()) {
                                    listaVizualizado.get(x).setHistorico((listaVizualizado.isEmpty() ? "" : "\n") + listaMovs.get(i).getServicos().getDescricao() + ": " + listaMovs.get(i).getReferencia());
                                } else {
                                    listaVizualizado.get(x).setHistorico(listaVizualizado.get(x).getHistorico() + ", " + listaMovs.get(i).getReferencia());
                                }
                                next = false;
                            }
                        }
                    }
                    for (int x = 0; x < listTotalizacao.size(); x++) {
                        if (listTotalizacao.get(x).getServicos().getId() == listaMovs.get(i).getServicos().getId()) {
                            listTotalizacao.get(x).setValorBaixa(+listTotalizacao.get(x).getValorBaixa() + listaMovs.get(i).getValorBaixa());
                            if (listTotalizacao.get(x).getHistorico().isEmpty()) {
                                listTotalizacao.get(x).setHistorico((listTotalizacao.isEmpty() ? "" : "\n") + listaMovs.get(i).getServicos().getDescricao() + ": " + listaMovs.get(i).getReferencia());
                            } else {
                                listTotalizacao.get(x).setHistorico(listTotalizacao.get(x).getHistorico() + ", " + listaMovs.get(i).getReferencia());
                            }
                            next2 = false;
                        }
                    }
                    if (next) {
                        listaVizualizado.add(new GridAcordo(listaMovs.get(i).getServicos(), listaMovs.get(i).getValorBaixa(), listaMovs.get(i).getReferencia(), (listaVizualizado.isEmpty() ? "" : "\n") + listaMovs.get(i).getServicos().getDescricao() + " - " + listaMovs.get(i).getReferencia()));
                    }
                    if (next2) {
                        listTotalizacao.add(new GridAcordo(listaMovs.get(i).getServicos(), listaMovs.get(i).getValorBaixa(), listaMovs.get(i).getReferencia(), (listaVizualizado.isEmpty() ? "" : "\n") + listaMovs.get(i).getServicos().getDescricao() + " - " + listaMovs.get(i).getReferencia()));
                    }
                }
            }
            for (int x = 0; x < listTotalizacao.size(); x++) {
                historicoString += listTotalizacao.get(x).getHistorico();
            }
            historico.setHistorico(historico.getHistorico() + historicoString);
        }
        return listaVizualizado;
    }

    public void setListaVizualizado(List<GridAcordo> listaVizualizado) {
        this.listaVizualizado = listaVizualizado;
    }

    public synchronized void efetuarAcordo() {
        Float totalAcordo = new Float(0);
        for (int i = 0; i < listaOperado.size(); i++) {
            totalAcordo += ((Movimento) listaOperado.get(i).getArgumento2()).getValor();
        }
        if (Moeda.converteFloatR$Float(totalAcordo) != Moeda.converteUS$(getTotal())) {
            GenericaMensagem.warn("Atenção", "VALOR TOTAL NÃO CONFERE COM OS VALORES DAS PARCELAS!");
            return;
        }
        if (listaOperado.isEmpty()) {
            GenericaMensagem.error("Atenção", "Acordo não foi gerado!");
            return;
        }
        List<Movimento> listaAcordo = new ArrayList();
        List<String> listaHistorico = new ArrayList();

        for (DataObject listaOperado1 : listaOperado) {
            listaAcordo.add((Movimento) listaOperado1.getArgumento2());
            listaHistorico.add((String) listaOperado1.getArgumento3());
        }

        try {
            // 07-11-2011 dep arrecad. secrp rogerio afirmou que o nr_ctr_boleto dos acordados tem que ser zerados,
            // para que nao haja conflito com os novos boletos gerados (* (nr_num_documento, nr_ctr_boleto, id_conta_cobranca) *)
            String mensagem = GerarMovimento.salvarListaAcordo(acordo, listaAcordo, listaMovs, listaHistorico);
            if (mensagem.isEmpty()) {
                GenericaMensagem.info("Sucesso", "Acordo Concluído!");
            }

            imprimir = false;

            String url = (String) GenericaSessao.getString("urlRetorno");
            switch (url) {
                case "movimentosReceber":
                    ((MovimentosReceberBean) GenericaSessao.getObject("movimentosReceberBean")).getListMovimentoReceber().clear();
                    ((MovimentosReceberBean) GenericaSessao.getObject("movimentosReceberBean")).setDesconto("0");
                    break;
                case "movimentosReceberSocial":
                    ((MovimentosReceberSocialBean) GenericaSessao.getObject("movimentosReceberSocialBean")).getListaMovimento().clear();
                    break;
            }
            if (!mensagem.isEmpty()) {
                GenericaMensagem.error("Atenção", mensagem);
            }
        } catch (Exception e) {
            GenericaMensagem.error("Atenção", "Acordo não foi gerado");

        }
    }

    public synchronized String subirData() {
        String vencimentoOut = getListaVencimento().get(idVencimento).getLabel();
        String vencimentoSind = getListaVencimento().get(idVencimentoSind).getLabel();
        if (listaOperado.isEmpty()) {
            return null;
        }

        int i = 0;
        int j = 0;
        List listas = new ArrayList();
        List<Integer> subLista = new ArrayList();
        DataHoje data = new DataHoje();
        String dataPrincipal = "";
        String referencia = "";
        while (i < listaOperado.size()) {
            if ((Boolean) listaOperado.get(i).getArgumento0()) {
                subLista.add(i);
            } else {
                if (!(subLista.isEmpty())) {
                    listas.add(subLista);
                    subLista = new ArrayList();
                }
                while (i < listaOperado.size()) {
                    if (listaOperado.size() > (i + 1)) {
                        if ((Boolean) listaOperado.get(i + 1).getArgumento0()) {
                            break;
                        }
                    }
                    i++;
                }
            }
            i++;
        }
        if (!(subLista.isEmpty())) {
            listas.add(subLista);
            subLista = new ArrayList();
        }
        i = 0;
        j = 0;
        String date = null;
        Servicos servico = null;
        while (i < listas.size()) {
            j = 0;
            Movimento movimento = (Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2();
            date = movimento.getVencimento();
            servico = movimento.getServicos();
            if (servico.getId() == 1) {
                if (frequenciaSind == 30) {
                    if ((DataHoje.menorData(data.decrementarMeses(1, date), vencimentoSind))
                            && (!DataHoje.igualdadeData(data.decrementarMeses(1, date), vencimentoSind))) {
                        i++;
                        continue;
                    }
                    dataPrincipal = movimento.getVencimento();
                    dataPrincipal = data.decrementarMeses(1, dataPrincipal);
                    referencia = data.decrementarMeses(1, dataPrincipal);// AQUI
                } else if (frequenciaSind == 7) {
                    if ((DataHoje.menorData(data.decrementarSemanas(1, date), vencimentoSind))
                            && (!DataHoje.igualdadeData(data.decrementarSemanas(1, date), vencimentoSind))) {
                        i++;
                        continue;
                    }
                    dataPrincipal = movimento.getVencimento();
                    dataPrincipal = data.decrementarSemanas(1, dataPrincipal);
                    referencia = data.decrementarSemanas(1, dataPrincipal);// AQUI
                } else if (frequenciaSind == 15) {
                    if ((DataHoje.menorData(data.decrementarDias(15, date), vencimentoSind))
                            && (!DataHoje.igualdadeData(data.decrementarDias(15, date), vencimentoSind))) {
                        i++;
                        continue;
                    }
                    dataPrincipal = movimento.getVencimento();
                    dataPrincipal = data.decrementarSemanas(1, dataPrincipal);
                    referencia = data.decrementarSemanas(1, dataPrincipal);// AQUI
                }
            } else if (frequencia == 30) {
                if ((DataHoje.menorData(data.decrementarMeses(1, date), vencimentoOut))
                        && (!DataHoje.igualdadeData(data.decrementarMeses(1, date), vencimentoOut))) {
                    i++;
                    continue;
                }
                dataPrincipal = movimento.getVencimento();
                dataPrincipal = data.decrementarMeses(1, dataPrincipal);
                referencia = data.decrementarMeses(1, dataPrincipal);
            } else if (frequencia == 7) {
                if ((DataHoje.menorData(data.decrementarSemanas(1, date), vencimentoOut))
                        && (!DataHoje.igualdadeData(data.decrementarSemanas(1, date), vencimentoOut))) {
                    i++;
                    continue;
                }
                dataPrincipal = movimento.getVencimento();
                dataPrincipal = data.decrementarSemanas(1, dataPrincipal);
                referencia = data.decrementarSemanas(1, dataPrincipal);
            } else if (frequencia == 15) {
                if ((DataHoje.menorData(data.decrementarDias(15, date), vencimentoOut))
                        && (!DataHoje.igualdadeData(data.decrementarDias(15, date), vencimentoOut))) {
                    i++;
                    continue;
                }
                dataPrincipal = movimento.getVencimento();
                dataPrincipal = data.decrementarSemanas(1, dataPrincipal);
                referencia = data.decrementarSemanas(1, dataPrincipal);
            }

            while (j < ((List<Integer>) listas.get(i)).size()) {
                ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setVencimento(dataPrincipal);
                if (movimento.getServicos().getId() != 1) {
                    ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setReferencia(referencia.substring(3));
                }
                j++;
            }

            i++;
        }
        BubbleSort(listaOperado);
        ordernarPorServico();
        while (i < listaOperado.size()) {
            listaOperado.get(i).setArgumento1(i + 1);
            i++;
        }
        return null;
    }

    public synchronized String descerData() {
        if (listaOperado.isEmpty()) {
            return null;
        }
        int i = 0;
        int j = 0;
        List listas = new ArrayList();
        List<Integer> subLista = new ArrayList();
        DataHoje data = new DataHoje();
        String dataPrincipal = "";
        String referencia = "";
        while (i < listaOperado.size()) {
            if ((Boolean) listaOperado.get(i).getArgumento0()) {
                subLista.add(i);
            } else {
                if (!(subLista.isEmpty())) {
                    listas.add(subLista);
                    subLista = new ArrayList();
                }
                while (i < listaOperado.size()) {
                    if (listaOperado.size() > (i + 1)) {
                        if ((Boolean) listaOperado.get(i + 1).getArgumento0()) {
                            break;
                        }
                    }
                    i++;
                }
            }
            i++;
        }
        if (!(subLista.isEmpty())) {
            listas.add(subLista);
            subLista = new ArrayList();
        }
        i = 0;
        j = 0;
        String date = null;
        Servicos servico = null;
        while (i < listas.size()) {
            j = 0;
            Movimento movimento = ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2());
            date = movimento.getVencimento();
            servico = movimento.getServicos();

            if (servico.getId() == 1) {
                if (frequenciaSind == 30) {
                    if (DataHoje.maiorData(data.incrementarMeses(1, date), ultimaData)) {
                        i++;
                        continue;
                    }
                    referencia = movimento.getVencimento(); // AQUI
                    dataPrincipal = data.incrementarMeses(1, referencia);
                } else if (frequenciaSind == 7) {
                    if (DataHoje.maiorData(data.incrementarSemanas(1, date), ultimaData)) {
                        i++;
                        continue;
                    }
                    referencia = movimento.getVencimento();// AQUI
                    dataPrincipal = data.incrementarSemanas(1, referencia);
                } else if (frequenciaSind == 15) {
                    if (DataHoje.maiorData(data.incrementarDias(15, date), ultimaData)) {
                        i++;
                        continue;
                    }
                    referencia = movimento.getVencimento();// AQUI
                    dataPrincipal = data.incrementarDias(15, referencia);
                }
            } else if (frequencia == 30) {
                if (DataHoje.maiorData(data.incrementarMeses(1, date), ultimaData)) {
                    i++;
                    continue;
                }
                referencia = movimento.getVencimento();
                dataPrincipal = data.incrementarMeses(1, referencia);
            } else if (frequencia == 7) {
                if (DataHoje.maiorData(data.incrementarSemanas(1, date), ultimaData)) {
                    i++;
                    continue;
                }
                referencia = movimento.getVencimento();
                dataPrincipal = data.incrementarDias(15, referencia);
            } else if (frequencia == 15) {
                if (DataHoje.maiorData(data.incrementarDias(15, date), ultimaData)) {
                    i++;
                    continue;
                }
                referencia = movimento.getVencimento();
                dataPrincipal = data.incrementarSemanas(1, referencia);
            }
            while (j < ((List<Integer>) listas.get(i)).size()) {
                ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setVencimento(dataPrincipal);
                if (((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).getServicos().getId() != 1) {
                    ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setReferencia(referencia.substring(3));
                }
                j++;
            }
            i++;
        }
        BubbleSort(listaOperado);
        ordernarPorServico();
        i = 0;
        while (i < listaOperado.size()) {
            listaOperado.get(i).setArgumento1(i + 1);
            i++;
        }

        return null;
    }

    public synchronized void adicionarParcela() {
        try {
            Dao dao = new Dao();
            TipoServicoDao dbTipoServico = new TipoServicoDao();
            ContaCobrancaDao ctaCobraDB = new ContaCobrancaDao();
            FTipoDocumentoDao dbft = new FTipoDocumentoDao();
            TipoServico tipoServico = dbTipoServico.pesquisaCodigo(4);
            DataHoje data = new DataHoje();
            int j = 0, k = 0;
            Servicos servico = null;
            ContaCobranca contaCobranca = null;
            listaOperado = new ArrayList();
            String ultimoVencimento = getListaVencimento().get(idVencimento).getLabel();
            String ultimoVencimentoSind = getListaVencimento().get(idVencimentoSind).getLabel();
            float valorTotalOutras = 0;
            float valorSwap = Moeda.substituiVirgulaFloat(valorEntrada);
            float valorTotal = Moeda.converteFloatR$Float(Moeda.substituiVirgulaFloat(getTotalOutras()));
            float[] vetorEntrada = new float[listaVizualizado.size()];
            float pdE = Moeda.divisaoValores(valorSwap, valorTotal);
            float valorParcela = 0;
            for (int i = 0; i < listaVizualizado.size(); i++) {
                if (listaVizualizado.get(i).getServicos().getId() != 1) {
                    vetorEntrada[i] = Moeda.substituiVirgulaFloat(listaVizualizado.get(i).getValorBaixaString());
                    if (listaVizualizado.size() > 1) {
                        vetorEntrada[i] = Moeda.converteFloatR$Float(Moeda.multiplicarValores(vetorEntrada[i], pdE));
                    } else {
                        vetorEntrada[i] = valorSwap;
                    }
                } else {
                    vetorEntrada[i] = 0;
                }
            }
            String ultimoVencimentoTemp = null;
            Boolean validDate = true;
            for (int i = 0; i < listaVizualizado.size(); i++) {
                servico = listaVizualizado.get(i).getServicos();
                contaCobranca = ctaCobraDB.pesquisaServicoCobranca(servico.getId(), tipoServico.getId());
                if (contaCobranca != null) {
                    if (servico.getId() != 1) {
                        ultimoVencimento = getListaVencimento().get(idVencimento).getLabel();
                        j = 0;
                        if (parcela > 1) {
                            valorTotalOutras = Moeda.substituiVirgulaFloat(listaVizualizado.get(i).getValorBaixaString());
                            valorTotalOutras = Moeda.subtracaoValores(valorTotalOutras, vetorEntrada[i]);
                            valorSwap = vetorEntrada[i];
                            valorParcela = Moeda.converteFloatR$Float(Moeda.divisaoValores(valorTotalOutras, parcela - 1));
                        } else {
                            valorSwap = Moeda.substituiVirgulaFloat((String) listaVizualizado.get(i).getValorBaixaString());
                        }
                        while (j < parcela) {
//                            if (ultimoVencimentoTemp != null) {
//                                ultimoVencimento = ultimoVencimentoTemp;
//                                Integer ano = Integer.parseInt(DataHoje.livre(DataHoje.converte(ultimoVencimento), "YYYY"));
//                                if (!DataHoje.isBisexto(ano)) {
//                                    ultimoVencimento = DataHoje.alterDay(28, ultimoVencimento);
//                                    ultimoVencimento = data.incrementarMeses(1, ultimoVencimento);
//                                }
//                            }
                            if (j != 0) {
                                if ((Moeda.subtracaoValores(valorTotalOutras, valorParcela) != 0) && ((j + 1) == parcela)) {
                                    valorParcela = valorTotalOutras;
                                } else {
                                    valorTotalOutras = Moeda.subtracaoValores(valorTotalOutras, valorParcela);
                                }
                                valorSwap = valorParcela;
                            }

                            Movimento mov = new Movimento(-1,
                                    null,
                                    servico.getPlano5(),
                                    pessoa,
                                    servico,
                                    null,
                                    tipoServico,
                                    null,
                                    valorSwap,
                                    referencia(ultimoVencimento),
                                    ultimoVencimento,
                                    1,
                                    true,
                                    "E",
                                    false,
                                    pessoa,
                                    pessoa,
                                    "",
                                    "",
                                    ultimoVencimento,
                                    0,
                                    0, 0, 0, 0, 0, 0, (FTipoDocumento) dao.find(new FTipoDocumento(), 2), 0, null);

                            listaOperado.add(new DataObject(false, ++k, mov, (String) listaVizualizado.get(i).getHistorico(), null, null));
//                            if (ultimoVencimentoTemp != null) {
//                                String dia = getListaVencimento().get(idVencimento).getLabel().substring(0, 2);
//                                ultimoVencimento = DataHoje.alterDay(Integer.parseInt(dia), ultimoVencimento);
//                                ultimoVencimentoTemp = null;
//                            }
                            if (j == 0) {
                                ultimoVencimento = acordo.getData();
                            }

                            if (frequencia == 30) {
                                // String uv = ultimoVencimento;
                                try {
                                    if (!DataHoje.isDataValida(ultimoVencimento) && validDate) {
                                        ultimoVencimento = data.incrementarMeses(1, DataHoje.alterDay(28, ultimoVencimento));
                                        if (ultimoVencimento.substring(3, 5).equals("03")) {
                                            String dia = getListaVencimento().get(idVencimento).getLabel().substring(0, 2);
                                            ultimoVencimento = DataHoje.alterDay(Integer.parseInt(dia), acordo.getData());
                                        }
                                        validDate = false;
                                    } else if (!validDate) {
                                        ultimoVencimento = data.incrementarMeses(1, ultimoVencimento);
                                        validDate = true;
                                    } else {
                                        ultimoVencimento = data.incrementarMeses(1, ultimoVencimento);
                                    }
                                } catch (Exception e) {

                                }
//                                if (ultimoVencimento == null) {
//                                    ultimoVencimentoTemp = uv;
                                if (ultimoVencimento.substring(3, 5).equals("02")) {
                                    ultimoVencimento = acordo.getData().substring(0, 2) + ultimoVencimento.substring(2);
                                }
                            } else if (frequencia == 7) {
                                ultimoVencimento = data.incrementarSemanas(1, ultimoVencimento);
                            } else if (frequencia == 15) {
                                ultimoVencimento = data.incrementarDias(15, ultimoVencimento);
                            }
                            j++;

                        }
                    } else {
                        Movimento mov = new Movimento(-1,
                                null,
                                servico.getPlano5(),
                                pessoa,
                                servico,
                                null,
                                tipoServico,
                                null,
                                Moeda.substituiVirgulaFloat(listaVizualizado.get(i).getValorBaixaString()),
                                (String) listaVizualizado.get(i).getReferencia(),
                                //referencia(ultimoVencimentoSind), 
                                ultimoVencimentoSind,
                                1,
                                true,
                                "E",
                                false,
                                pessoa,
                                pessoa,
                                "",
                                "",
                                ultimoVencimentoSind,
                                0,
                                0, 0, 0, 0, 0, 0, (FTipoDocumento) dao.find(new FTipoDocumento(), 2), 0, null);

                        listaOperado.add(new DataObject(false, ++k, mov, listaVizualizado.get(i).getReferencia(), null, null));

                        if (parcela > 1) {
                            if (frequenciaSind == 30) {
                                ultimoVencimentoSind = data.incrementarMeses(1, ultimoVencimentoSind);
                                if (ultimoVencimentoSind.substring(3, 5).equals("02")) {
                                    ultimoVencimentoSind = acordo.getData().substring(0, 2) + ultimoVencimentoSind.substring(2);
                                }
                            } else if (frequenciaSind == 7) {
                                ultimoVencimentoSind = data.incrementarDias(15, ultimoVencimentoSind);
                            } else if (frequenciaSind == 15) {
                                ultimoVencimentoSind = data.incrementarDias(15, ultimoVencimentoSind);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        BubbleSort(listaOperado);
        ultimaData = ((Movimento) listaOperado.get(listaOperado.size() - 1).getArgumento2()).getVencimento();
    }

    public void imprimirBoletos() {
        ImprimirBoleto imp = new ImprimirBoleto();
        List<Float> listaValores = new ArrayList<Float>();
        List<String> listaVencimentos = new ArrayList<String>();
        List listaImp = new ArrayList();
        for (int i = 0; i < listaOperado.size(); i++) {
            listaImp.add(((Movimento) listaOperado.get(i).getArgumento2()));
            listaValores.add(((Movimento) listaOperado.get(i).getArgumento2()).getValor());
            listaVencimentos.add(((Movimento) listaOperado.get(i).getArgumento2()).getVencimento());

        }
        if (!listaImp.isEmpty()) {
            imp.imprimirBoleto(listaImp, listaValores, listaVencimentos, false);
            imp.visualizar(null);
        }
    }

    public void imprimirPlanilha() {
        ImprimirBoleto imp = new ImprimirBoleto();
        List listaImp = new ArrayList();
//        for (int i = 0; i < listaOperado.size(); i++){
//            listaImp.add(((Movimento) listaOperado.get(i).getArgumento2()));
//        }
        MovimentoDao db = new MovimentoDao();
        listaImp.addAll(db.pesquisaAcordoTodos(acordo.getId()));

        if (!listaImp.isEmpty()) {
            imp.imprimirAcordoPromissoria(listaImp, acordo, historico, imprimir_pro);
            imp.visualizar(null);
        }

    }

    public String referencia(String data) {
        if (data.length() == 10) {
            String ref = data.substring(3);
            String mes = ref.substring(0, 2);
            if (!(mes.equals("01"))) {
                if ((Integer.parseInt(mes) - 1) < 10) {
                    ref = "0" + Integer.toString(Integer.parseInt(mes) - 1) + data.substring(5);
                } else {
                    ref = Integer.toString(Integer.parseInt(mes) - 1) + data.substring(5);
                }
            } else {
                ref = "12/" + Integer.toString(Integer.parseInt(data.substring(6)) - 1);
            }
            return ref;
        } else {
            return null;
        }
    }

    public List<DataObject> getListaOperado() {
        return listaOperado;
    }

    public void setListaOperado(List<DataObject> listaOperado) {
        this.listaOperado = listaOperado;
    }

    public Acordo getAcordo() {
        return acordo;
    }

    public void setAcordo(Acordo acordo) {
        this.acordo = acordo;
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public int getParcela() {
        return parcela;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela;
    }

    public String getTotal() {
        return Moeda.converteR$Float(Moeda.converteUS$(getTotalSindical()) + Moeda.converteUS$(getTotalOutras()));
    }

    public void limparEntrada() {
        valorEntrada = "0";
    }

    public String getValorEntrada() {
        float valorTmp = Moeda.substituiVirgulaFloat(valorEntrada);
        float totalOutra = Moeda.substituiVirgulaFloat(getTotalOutras());
        if (valorEntrada.equals("0") || valorEntrada.equals("0,00")) {
            float valorTmp2 = Moeda.divisaoValores(totalOutra, parcela);
            if (parcela > 1) {
                valorEntrada = Moeda.converteR$Float(valorTmp2);
                return valorEntrada;
            }
        } else if (valorTmp > (Moeda.multiplicarValores(totalOutra, (float) 0.05))
                && valorTmp < (Moeda.multiplicarValores(totalOutra, (float) 0.8))) {
            return Moeda.converteR$(valorEntrada);
        } else {
            float valorTmp2 = Moeda.divisaoValores(totalOutra, parcela);
            if (parcela > 1) {
                valorEntrada = Moeda.converteR$Float(valorTmp2);
//                    return Moeda.converteR$(valorEntrada);
            }
        }
        return Moeda.converteR$(valorEntrada);
    }

    public void setValorEntrada(String valorEntrada) {
        if (valorEntrada.isEmpty()) {
            return;
        }
        try {
            Moeda.converteR$(valorEntrada);
        } catch (Exception e) {
            return;
        }
        this.valorEntrada = valorEntrada;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }

    public int getIdVencimento() {
        return idVencimento;
    }

    public void setIdVencimento(int idVencimento) {
        this.idVencimento = idVencimento;
    }

    public String getValorEntradaSind() {
        for (int i = 0; i < quantidade.size(); i++) {
            if (quantidade.get(i)[0] == 1) { // 1 ref. id sindical
                for (int j = 0; j < listaVizualizado.size(); j++) {
                    if (listaVizualizado.get(j).getServicos().getId() == 1) {
                        if (Moeda.substituiVirgulaFloat(valorEntradaSind) != (Float) listaVizualizado.get(j).getValorBaixa()) {
                            valorEntradaSind = getListaVizualizado().get(j).getValorBaixaString();
                        }
                    }
                }
            }
        }

        return Moeda.converteR$(valorEntradaSind);
    }

    public void setValorEntradaSind(String valorEntradaSind) {
        this.valorEntradaSind = Moeda.substituiVirgula(valorEntradaSind);
    }

    public String getTotalSindical() {
        Float v = new Float(0);
        for (int i = 0; i < listaVizualizado.size(); i++) {
            if (listaVizualizado.get(i).getServicos().getId() == 1) {
                v += listaVizualizado.get(i).getValorBaixa();
            }
        }
        return Moeda.converteR$Float(v);
    }

    public String getTotalOutras() {
        Float v = new Float(0);
        for (int i = 0; i < listaVizualizado.size(); i++) {
            if (listaVizualizado.get(i).getServicos().getId() != 1) {
                v += listaVizualizado.get(i).getValorBaixa();
            }
        }
        return Moeda.converteR$Float(v);
    }

    public synchronized void ordernarPorServico() {
        int i = 0;
        int indI = 0, indF = 0;
        String data = ((Movimento) listaOperado.get(i).getArgumento2()).getVencimento();
        while (i < listaOperado.size()) {
            if (!data.equals(((Movimento) listaOperado.get(i).getArgumento2()).getVencimento())) {
                BubbleSortServico(listaOperado.subList(indI, indF));
                indI = indF;
                indF++;
                data = ((Movimento) listaOperado.get(i).getArgumento2()).getVencimento();
            } else {
                indF++;
            }
            i++;
        }
    }

    public static void BubbleSort(List<DataObject> dados) {
        boolean trocou;
        int limite = dados.size() - 1;
        Object swap1 = null;
        Object swap2 = null;
        int i = 0;
        do {
            trocou = false;
            i = 0;
            while (i < limite) {
                if (((Movimento) dados.get(i).getArgumento2()).getDtVencimento().after(
                        ((Movimento) dados.get(i + 1).getArgumento2()).getDtVencimento())) {

                    swap1 = dados.get(i).getArgumento0();
                    swap2 = dados.get(i + 1).getArgumento0();
                    dados.get(i).setArgumento0(swap2);
                    dados.get(i + 1).setArgumento0(swap1);

                    swap1 = dados.get(i).getArgumento1();
                    swap2 = dados.get(i + 1).getArgumento1();
                    dados.get(i).setArgumento1(swap2);
                    dados.get(i + 1).setArgumento1(swap1);

                    swap1 = dados.get(i).getArgumento2();
                    swap2 = dados.get(i + 1).getArgumento2();
                    dados.get(i).setArgumento2(swap2);
                    dados.get(i + 1).setArgumento2(swap1);

                    swap1 = dados.get(i).getArgumento3();
                    swap2 = dados.get(i + 1).getArgumento3();
                    dados.get(i).setArgumento3(swap2);
                    dados.get(i + 1).setArgumento3(swap1);
                    trocou = true;
                }
                i++;
            }
            limite--;
        } while (trocou);
    }

    public static void BubbleSortServico(List<DataObject> dados) {
        boolean trocou;
        int limite = dados.size() - 1;
        Object swap1 = null;
        Object swap2 = null;
        int i = 0;
        int result = -1;
        do {
            trocou = false;
            i = 0;
            while (i < limite) {
                result = ((Movimento) dados.get(i).getArgumento2()).getServicos().getDescricao().compareTo(
                        ((Movimento) dados.get(i + 1).getArgumento2()).getServicos().getDescricao());
                if (result > 0) {
                    swap1 = dados.get(i).getArgumento0();
                    swap2 = dados.get(i + 1).getArgumento0();
                    dados.get(i).setArgumento0(swap2);
                    dados.get(i + 1).setArgumento0(swap1);

                    swap1 = dados.get(i).getArgumento1();
                    swap2 = dados.get(i + 1).getArgumento1();
                    dados.get(i).setArgumento1(swap2);
                    dados.get(i + 1).setArgumento1(swap1);

                    swap1 = dados.get(i).getArgumento2();
                    swap2 = dados.get(i + 1).getArgumento2();
                    dados.get(i).setArgumento2(swap2);
                    dados.get(i + 1).setArgumento2(swap1);
                    trocou = true;
                }
                i++;
            }
            limite--;
        } while (trocou);
    }

    public int getIdVencimentoSind() {
        return idVencimentoSind;
    }

    public void setIdVencimentoSind(int idVencimentoSind) {
        this.idVencimentoSind = idVencimentoSind;
    }

    public int getFrequenciaSind() {
        return frequenciaSind;
    }

    public void setFrequenciaSind(int frequenciaSind) {
        this.frequenciaSind = frequenciaSind;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public boolean isImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    public List<Movimento> getListaMovs() {
        return listaMovs;
    }

    public void setListaMovs(List<Movimento> listaMovs) {
        this.listaMovs = listaMovs;
    }

    public Pessoa getPessoa() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("listaMovimento") != null) {
            listaMovs = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("listaMovimento");
            pessoa = listaMovs.get(0).getPessoa();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("listaMovimento");
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getEmailPara() {
        return emailPara;
    }

    public void setEmailPara(String emailPara) {
        this.emailPara = emailPara;
    }

    public Pessoa getPessoaEnvio() {
        return pessoaEnvio;
    }

    public void setPessoaEnvio(Pessoa pessoaEnvio) {
        this.pessoaEnvio = pessoaEnvio;
    }

    public boolean isImprimir_pro() {
        return imprimir_pro;
    }

    public void setImprimir_pro(boolean imprimir_pro) {
        this.imprimir_pro = imprimir_pro;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getEmailAntigo() {
        return emailAntigo;
    }

    public void setEmailAntigo(String emailAntigo) {
        this.emailAntigo = emailAntigo;
    }

    public List<GridAcordo> getListTotalizacao() {
        return listTotalizacao;
    }

    public void setListTotalizacao(List<GridAcordo> listTotalizacao) {
        this.listTotalizacao = listTotalizacao;
    }

    public class GridAcordo {

        private Servicos servicos;
        private float valorBaixa;
        private String referencia;
        private String historico;

        public GridAcordo() {
            this.servicos = null;
            this.valorBaixa = 0;
            this.referencia = "";
            this.historico = "";
        }

        public GridAcordo(Servicos servicos, float valorBaixa, String referencia, String historico) {
            this.servicos = servicos;
            this.valorBaixa = valorBaixa;
            this.referencia = referencia;
            this.historico = historico;
        }

        public Servicos getServicos() {
            return servicos;
        }

        public void setServicos(Servicos servicos) {
            this.servicos = servicos;
        }

        public float getValorBaixa() {
            return valorBaixa;
        }

        public void setValorBaixa(float valorBaixa) {
            this.valorBaixa = valorBaixa;
        }

        public String getValorBaixaString() {
            return Moeda.converteR$Float(valorBaixa);
        }

        public void setValorBaixaString(String valorBaixaString) {
            this.valorBaixa = Moeda.converteUS$(valorBaixaString);
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getHistorico() {
            return historico;
        }

        public void setHistorico(String historico) {
            this.historico = historico;
        }
    }
}
