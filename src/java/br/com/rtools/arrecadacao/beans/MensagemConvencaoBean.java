package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class MensagemConvencaoBean {

    private MensagemConvencao mensagemConvencao = new MensagemConvencao();
    private String msgConfirma;
    private int idGrupo;
    private int idConvencao;
    private int idServico = 0;
    private int idTipoServico = 0;
    private int idReplica = 1;
    private int processarGrupos = 4;
    private int processarGruposAlterar = 4;
    private int idIndex = -1;
    private List listaMensagens = new ArrayList();
    private boolean disAcordo = false;
    private boolean processarTipoServicos = false;
    private boolean gerarAno = false;
    private boolean gerarAnoVencimento = false;
    private String vencimento = DataHoje.data();
    private String replicaPara = "";

    private final List<SelectItem> listaConvencoes = new ArrayList();
    private final List<SelectItem> listaGrupoCidade = new ArrayList();
    private final List<SelectItem> listaServico = new ArrayList();
    private final List<SelectItem> listaTipoServico = new ArrayList();

    public MensagemConvencaoBean() {
        mensagemConvencao.setReferencia(DataHoje.data().substring(3));

        this.loadListaConvencoes();
        this.loadListaGrupoCidade();
        this.loadListaServico();
        this.loadListaTipoServico();
    }

    public final void loadListaConvencoes() {
        listaConvencoes.clear();

        List<Convencao> list = (List<Convencao>) new Dao().list(new Convencao(), true);
        for (int i = 0; i < list.size(); i++) {
            listaConvencoes.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
        }
    }

    public final void loadListaGrupoCidade() {
        listaGrupoCidade.clear();
        ConvencaoCidadeDao convencaoCidadeDB = new ConvencaoCidadeDao();

        Convencao convencao = (Convencao) new Dao().find(new Convencao(), Integer.parseInt(listaConvencoes.get(idConvencao).getDescription()));
        if (convencao == null) {
            return;
        }

        int i = 0;
        List<GrupoCidade> select = convencaoCidadeDB.pesquisarGruposPorConvencao(convencao.getId());
        if (select != null) {
            while (i < select.size()) {
                listaGrupoCidade.add(new SelectItem(
                        i,
                        (String) ((GrupoCidade) select.get(i)).getDescricao(),
                        Integer.toString(select.get(i).getId()))
                );
                i++;
            }
        }
    }

    public final void loadListaServico() {
        listaServico.clear();

        int i = 0;
        ServicosDao db = new ServicosDao();
        List<Servicos> select = db.pesquisaTodosPeloContaCobranca(4);
        while (i < select.size()) {
            listaServico.add(new SelectItem(
                    i,
                    select.get(i).getDescricao(),
                    Integer.toString(select.get(i).getId()))
            );
            i++;
        }
    }

    public final void loadListaTipoServico() {
        listaTipoServico.clear();

        TipoServicoDao db = new TipoServicoDao();
        List<TipoServico> select = db.pesquisaTodosPeloContaCobranca();

        if (select.isEmpty()) {
            GenericaMensagem.error("Atenção", "Serviço Conta Cobrança não encontrado!");
            return;
        }

        for (int i = 0; i < select.size(); i++) {
            listaTipoServico.add(new SelectItem(
                    i,
                    select.get(i).getDescricao(),
                    Integer.toString(select.get(i).getId())
            )
            );
        }
    }

    public String replicar() {
        MensagemConvencaoDao db = new MensagemConvencaoDao();
        List<MensagemConvencao> listam = db.pesquisaTodosAno(this.getListaRefReplica().get(idReplica).getLabel());

        Dao dao = new Dao();
        if (!listam.isEmpty()) {
            dao.openTransaction();
        }
        DataHoje dh = new DataHoje();
        boolean comita = false;
        for (int i = 0; i < listam.size(); i++) {
            MensagemConvencao mc = new MensagemConvencao();
            mc = db.verificaMensagem(listam.get(i).getConvencao().getId(), listam.get(i).getServicos().getId(),
                    listam.get(i).getTipoServico().getId(), listam.get(i).getGrupoCidade().getId(),
                    listam.get(i).getReferencia().substring(0, 3) + replicaPara);
            if (mc != null && mc.getId() != -1) {
                continue;
            }

            MensagemConvencao men = new MensagemConvencao(-1, listam.get(i).getGrupoCidade(),
                    listam.get(i).getConvencao(), listam.get(i).getServicos(),
                    listam.get(i).getTipoServico(),
                    listam.get(i).getMensagemContribuinte(),
                    listam.get(i).getMensagemCompensacao(),
                    listam.get(i).getReferencia().substring(0, 3) + replicaPara, DataHoje.converte(dh.incrementarAnos(1, listam.get(i).getVencimento())));

            if (dao.save(men)) {
                comita = true;
            } else {
            }

        }
        if (comita) {
            dao.commit();
            msgConfirma = "Registro replicado com Sucesso!";
            GenericaMensagem.info("Sucesso", msgConfirma);
        } else {
            dao.rollback();
            msgConfirma = "Nenhuma mensagem para Replicar!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }
        return "";
    }

    public List<SelectItem> getListaRefReplica() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        List select = new ArrayList();
        select.add(Integer.valueOf(DataHoje.data().substring(6)) - 1);
        select.add(DataHoje.data().substring(6));
        for (int i = 0; i < select.size(); i++) {
            lista.add(new SelectItem(i, select.get(i).toString(), Integer.toString(i)));
        }
        return lista;
    }

    public MensagemConvencao getMensagemConvencao() {
        if (mensagemConvencao.getId() == -1) {
            if (GenericaSessao.exists("mensagemPesquisa")) {
                mensagemConvencao = (MensagemConvencao) GenericaSessao.getObject("mensagemPesquisa", true);
                editar(mensagemConvencao);
            }
        }
        return mensagemConvencao;
    }

//    public MensagemConvencao getMensagemConvencaoPesquisa() {
//        try {
//            MensagemConvencao c = (MensagemConvencao) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("mensagemPesquisa");
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("mensagemPesquisa");
//            return c;
//        } catch (Exception e) {
//            MensagemConvencao c = new MensagemConvencao();
//            return c;
//        }
//    }
    public void setMensagemConvencao(MensagemConvencao mensagemConvencao) {
        this.mensagemConvencao = mensagemConvencao;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdConvencao() {
        return idConvencao;
    }

    public void setIdConvencao(int idConvencao) {
        this.idConvencao = idConvencao;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public int getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(int idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public synchronized String salvar() {
        MensagemConvencaoDao db = new MensagemConvencaoDao();
        ConvencaoCidadeDao dbc = new ConvencaoCidadeDao();
        DataHoje dataHoje = new DataHoje();
        mensagemConvencao.setVencimento(vencimento);

        if (!mensagemConvencao.getVencimento().equals(vencimento)) {
            msgConfirma = "Este vencimento esta incorreto!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if ((mensagemConvencao.getReferencia().length() != 7)
                && (Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription()) != 4)) {
            msgConfirma = "Referência esta incorreta";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (DataHoje.converteData(mensagemConvencao.getDtVencimento()) == null) {
            msgConfirma = "Informe o vencimento";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        try {
            if (mensagemConvencao.getId() == -1) {
                // SE ACORDO FOR FALSO ----------------------------------------------------
                if (mensagemConvencao.getReferencia().length() != 7 && !disAcordo) {
                    msgConfirma = "Digite uma referencia!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    return null;
                }

                int ano = 0;
                String referencia = "",
                        vencto = "",
                        diaOriginal = "";
                int iservicos = Integer.parseInt(listaServico.get(idServico).getDescription()),
                        itiposervico = Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription());
                if (gerarAno && !disAcordo) {
                    ano = 12;
                    referencia = "01/01/" + mensagemConvencao.getReferencia().substring(3);
                    diaOriginal = mensagemConvencao.getVencimento().substring(0, 2);
                    vencto = diaOriginal + "/01/" + mensagemConvencao.getVencimento().substring(6, 10);
                    if (iservicos == 1) {
                        vencto = dataHoje.incrementarMesesUltimoDia(1, vencto);
                    } else {
                        vencto = dataHoje.incrementarMeses(1, vencto);
                    }
                } else {
                    ano = 1;
                    referencia = mensagemConvencao.getReferencia();
                    diaOriginal = mensagemConvencao.getVencimento().substring(0, 2);
                    vencto = mensagemConvencao.getVencimento();
                }

                switch (processarGrupos) {
                    //  SALVAR PARA TODOS OS GRUPOS DESTA CONVENÇÃO
                    case 1: {
                        int conv = Integer.parseInt(listaConvencoes.get(idConvencao).getDescription());
                        List<GrupoCidade> listgc = dbc.pesquisarGruposPorConvencao(conv);
                        for (int l = 0; l < ano; l++) {
                            for (int k = 0; k < listgc.size(); k++) {
                                if (gerarAno && !disAcordo) {
                                    msgConfirma = this.insertMensagem(conv, listgc.get(k).getId(), iservicos, itiposervico, referencia.substring(3), vencto);
                                } else {
                                    msgConfirma = this.insertMensagem(conv, listgc.get(k).getId(), iservicos, itiposervico, referencia, vencto);
                                }
                            }
                            referencia = dataHoje.incrementarMeses(1, referencia);
                            if (iservicos == 1) {
                                vencto = dataHoje.incrementarMesesUltimoDia(1, vencto);
                            } else {
                                vencto = diaOriginal + vencto.substring(2, 10);
                                vencto = dataHoje.incrementarMeses(1, vencto);
                            }
                        }
                        break;
                    }
                    // SALVAR PARA TODAS AS CONVENÇÕES DESTE GRUPO
                    case 2: {
                        int grupoC = Integer.parseInt(listaGrupoCidade.get(idGrupo).getDescription());
                        List<Convencao> listc = dbc.pesquisarConvencaoPorGrupos(grupoC);
                        for (int l = 0; l < ano; l++) {
                            for (int k = 0; k < listc.size(); k++) {
                                if (gerarAno && !disAcordo) {
                                    msgConfirma = this.insertMensagem(listc.get(k).getId(), grupoC, iservicos, itiposervico, referencia.substring(3), vencto);
                                } else {
                                    msgConfirma = this.insertMensagem(listc.get(k).getId(), grupoC, iservicos, itiposervico, referencia, vencto);
                                }

                            }
                            referencia = dataHoje.incrementarMeses(1, referencia);
                            if (iservicos == 1) {
                                vencto = dataHoje.incrementarMesesUltimoDia(1, vencto);
                            } else {
                                vencto = diaOriginal + vencto.substring(2, 10);
                                vencto = dataHoje.incrementarMeses(1, vencto);
                            }
                        }
                        break;
                    }
                    // SALVAR PARA TODOS OS GRUPOS E CONVENÇÕES
                    case 3: {
                        for (int l = 0; l < ano; l++) {
                            for (int k = 0; k < listaConvencoes.size(); k++) {
                                List<GrupoCidade> listgc = dbc.pesquisarGruposPorConvencao(Integer.parseInt(listaConvencoes.get(k).getDescription()));
                                for (int w = 0; w < listgc.size(); w++) {
                                    if (gerarAno && !disAcordo) {
                                        msgConfirma = this.insertMensagem(Integer.parseInt(listaConvencoes.get(k).getDescription()), listgc.get(w).getId(), iservicos, itiposervico, referencia.substring(3), vencto);
                                    } else {
                                        msgConfirma = this.insertMensagem(Integer.parseInt(listaConvencoes.get(k).getDescription()), listgc.get(w).getId(), iservicos, itiposervico, referencia, vencto);
                                    }
                                }
                            }
                            referencia = dataHoje.incrementarMeses(1, referencia);
                            if (iservicos == 1) {
                                vencto = dataHoje.incrementarMesesUltimoDia(1, vencto);
                            } else {
                                vencto = diaOriginal + vencto.substring(2, 10);
                                vencto = dataHoje.incrementarMeses(1, vencto);
                            }
                        }
                        break;
                    }
                    // NENHUMA DESTAS OPÇÕES
                    case 4: {
                        int conv = Integer.parseInt(listaConvencoes.get(idConvencao).getDescription()),
                                grupoC = Integer.parseInt(listaGrupoCidade.get(idGrupo).getDescription());
                        for (int l = 0; l < ano; l++) {
                            if (gerarAno && !disAcordo) {
                                msgConfirma = this.insertMensagem(conv, grupoC, iservicos, itiposervico, referencia.substring(3), vencto);
                            } else {
                                msgConfirma = this.insertMensagem(conv, grupoC, iservicos, itiposervico, referencia, vencto);
                            }

                            referencia = dataHoje.incrementarMeses(1, referencia);
                            if (iservicos == 1) {
                                vencto = dataHoje.incrementarMesesUltimoDia(1, vencto);
                            } else {
                                vencto = diaOriginal + vencto.substring(2, 10);
                                vencto = dataHoje.incrementarMeses(1, vencto);
                            }
                        }
                        break;
                    }
                }
            } else {
                Dao dao = new Dao();
                MensagemConvencao men = null;
                NovoLog novoLog = new NovoLog();
                if (processarTipoServicos) {
                    List<MensagemConvencao> lista = db.mesmoTipoServico(
                            Integer.parseInt(listaServico.get(idServico).getDescription()),
                            Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription()),
                            mensagemConvencao.getReferencia().substring(3));
                    for (int i = 0; i < lista.size(); i++) {
                        lista.get(i).setMensagemCompensacao(mensagemConvencao.getMensagemCompensacao());
                        lista.get(i).setMensagemContribuinte(mensagemConvencao.getMensagemContribuinte());
                        lista.get(i).setVencimento(vencimento);
                        men = db.verificaMensagem(lista.get(i).getConvencao().getId(),
                                lista.get(i).getServicos().getId(),
                                lista.get(i).getTipoServico().getId(),
                                lista.get(i).getGrupoCidade().getId(),
                                lista.get(i).getReferencia());
                        if ((men == null) || (men.getId() != -1)) {
                            MensagemConvencao mcBefore = (MensagemConvencao) dao.find(men);
                            String beforeUpdate = " - Referência: " + mcBefore.getReferencia()
                                    + " - Vencimento: " + mcBefore.getVencimento()
                                    + " - Serviço: (" + mcBefore.getServicos().getId() + ") "
                                    + " - Tipo Serviço: (" + mcBefore.getTipoServico().getId() + ") " + mcBefore.getTipoServico().getDescricao()
                                    + " - Convenção: (" + mcBefore.getConvencao().getId() + ") " + mcBefore.getConvencao().getDescricao()
                                    + " - Grupo Cidade: (" + mcBefore.getGrupoCidade().getId() + ") " + mcBefore.getGrupoCidade().getDescricao()
                                    + " - Mensagem Compensação: " + mcBefore.getMensagemCompensacao();
                            if (db.update(lista.get(i))) {
                                msgConfirma = "Mensagem atualizado com sucesso!";
                                novoLog.update(beforeUpdate,
                                        " - Referência: " + men.getReferencia()
                                        + " - Vencimento: " + men.getVencimento()
                                        + " - Serviço: (" + men.getServicos().getId() + ") "
                                        + " - Tipo Serviço: (" + men.getTipoServico().getId() + ") " + men.getTipoServico().getDescricao()
                                        + " - Convenção: (" + men.getConvencao().getId() + ") " + men.getConvencao().getDescricao()
                                        + " - Grupo Cidade: (" + men.getGrupoCidade().getId() + ") " + men.getGrupoCidade().getDescricao()
                                        + " - Mensagem Compensação: " + men.getMensagemCompensacao()
                                        + " - Mensagem Contribuinte: " + men.getMensagemContribuinte()
                                );
                                GenericaMensagem.info("Sucesso", msgConfirma);
                            } else {
                                msgConfirma = "Ocorreu um erro ao atualizar!";
                                GenericaMensagem.warn("Erro", msgConfirma);
                            }
                        }
                    }
                } else {
                    men = db.verificaMensagem(mensagemConvencao.getConvencao().getId(),
                            mensagemConvencao.getServicos().getId(),
                            mensagemConvencao.getTipoServico().getId(),
                            mensagemConvencao.getGrupoCidade().getId(),
                            mensagemConvencao.getReferencia());
                    MensagemConvencao mcBefore = (MensagemConvencao) dao.find(mensagemConvencao);
                    String beforeUpdate = " - Referência: " + mcBefore.getReferencia()
                            + " - Vencimento: " + mcBefore.getVencimento()
                            + " - Serviço: (" + mcBefore.getServicos().getId() + ") "
                            + " - Tipo Serviço: (" + mcBefore.getTipoServico().getId() + ") " + mcBefore.getTipoServico().getDescricao()
                            + " - Convenção: (" + mcBefore.getConvencao().getId() + ") " + mcBefore.getConvencao().getDescricao()
                            + " - Grupo Cidade: (" + mcBefore.getGrupoCidade().getId() + ") " + mcBefore.getGrupoCidade().getDescricao()
                            + " - Mensagem Compensação: " + mcBefore.getMensagemCompensacao();
                    if (men == null || (men.getId() == mensagemConvencao.getId())) {

                        switch (processarGruposAlterar) {
                            //  ALTERAR MENSAGEM PARA TODOS OS GRUPOS DESTA CONVENÇÃO
                            case 1: {
                                // ESTE CASO ESTA DESABILITADO
                                break;
                            }
                            // ALTERAR MENSAGEM PARA TODAS AS CONVENÇÕES DESTE GRUPO
                            case 2: {
                                // ESTE CASO ESTA DESABILITADO
                                break;
                            }
                            // ALTERAR MENSAGEM PARA TODOS OS GRUPOS E CONVENÇÕES
                            case 3: {
                                updateMensagem(null, null, mensagemConvencao.getServicos().getId(), mensagemConvencao.getTipoServico().getId(), mensagemConvencao.getId(), beforeUpdate, "mensagem");
                                break;
                            }

                            //  ALTERAR VENCIMENTO PARA TODOS OS GRUPOS DESTA CONVENÇÃO
                            case 5: {
                                // ESTE CASO ESTA DESABILITADO
                                break;
                            }
                            // ALTERAR VENCIMENTO PARA TODAS AS CONVENÇÕES DESTE GRUPO
                            case 6: {
                                // ESTE CASO ESTA DESABILITADO
                                break;
                            }
                            // ALTERAR VENCIMENTO PARA TODOS OS GRUPOS E CONVENÇÕES
                            case 7: {
                                updateMensagem(null, null, mensagemConvencao.getServicos().getId(), mensagemConvencao.getTipoServico().getId(), mensagemConvencao.getId(), beforeUpdate, "vencimento");
                                break;
                            }

                            default: {
                                updateMensagem(mensagemConvencao.getConvencao().getId(), mensagemConvencao.getGrupoCidade().getId(), mensagemConvencao.getServicos().getId(), mensagemConvencao.getTipoServico().getId(), null, beforeUpdate, "");
                                break;
                            }
                        }

                        if (!db.update(mensagemConvencao)) {
                            msgConfirma = "Ocorreu um erro ao atualizar!";
                            GenericaMensagem.warn("Erro", msgConfirma);
                        }

                        msgConfirma = "Mensagem atualizado com sucesso!";
                        GenericaMensagem.info("Sucesso", msgConfirma);

                        novoLog.update(beforeUpdate,
                                " - Referência: " + mensagemConvencao.getReferencia()
                                + " - Vencimento: " + mensagemConvencao.getVencimento()
                                + " - Serviço: (" + mensagemConvencao.getServicos().getId() + ") "
                                + " - Tipo Serviço: (" + mensagemConvencao.getTipoServico().getId() + ") " + mensagemConvencao.getTipoServico().getDescricao()
                                + " - Convenção: (" + mensagemConvencao.getConvencao().getId() + ") " + mensagemConvencao.getConvencao().getDescricao()
                                + " - Grupo Cidade: (" + mensagemConvencao.getGrupoCidade().getId() + ") " + mensagemConvencao.getGrupoCidade().getDescricao()
                                + " - Mensagem Compensação: " + mensagemConvencao.getMensagemCompensacao()
                                + " - Mensagem Contribuinte: " + mensagemConvencao.getMensagemContribuinte()
                        );
                    } else {
                        msgConfirma = "Mensagem já existe!";
                        GenericaMensagem.warn("Erro", msgConfirma);
                    }
                }
            }
        } catch (Exception e) {
            msgConfirma = e.getMessage();
            GenericaMensagem.warn("Erro", msgConfirma);
        }
//        mensagemConvencao = new MensagemConvencao();
//        idGrupo = 0;
//        idConvencao = 0;
//        idServico = 0;
//        idTipoServico = 0;
        return null;
    }

    private synchronized void updateMensagem(Integer id_convencao, Integer id_grupo_cidade, Integer id_servico, Integer id_tipo_servico, Integer id_mensagem_cobranca, String beforeUpdate, String tipoAlteracao) {
        MensagemConvencaoDao db = new MensagemConvencaoDao();
        String ref = mensagemConvencao.getReferencia();
        int meses = 1;
        //int mes_atual = Integer.parseInt(mensagemConvencao.getReferencia().substring(0, 2));

        if (gerarAno || gerarAnoVencimento) {
            meses = 12;
            ref = "01/" + mensagemConvencao.getReferencia().substring(3);
        }

        for (int i = 0; i < meses; i++) {

            List<MensagemConvencao> list = db.listaMensagemConvencaoFiltros(id_convencao, id_grupo_cidade, id_servico, id_tipo_servico, ref, id_mensagem_cobranca);
            for (MensagemConvencao mc : list) {
                switch (tipoAlteracao) {
                    case "mensagem":
                        mc.setMensagemCompensacao(mensagemConvencao.getMensagemCompensacao());
                        mc.setMensagemContribuinte(mensagemConvencao.getMensagemContribuinte());
                        break;

                    case "vencimento":
                        mc.setVencimento(mensagemConvencao.getVencimento());
                        break;

                    default:

                        if (gerarAno && gerarAnoVencimento) {
                            mc.setMensagemCompensacao(mensagemConvencao.getMensagemCompensacao());
                            mc.setMensagemContribuinte(mensagemConvencao.getMensagemContribuinte());
                            mc.setVencimento(mensagemConvencao.getVencimento());
                        } else if (gerarAno) {
                            mc.setMensagemCompensacao(mensagemConvencao.getMensagemCompensacao());
                            mc.setMensagemContribuinte(mensagemConvencao.getMensagemContribuinte());
                        } else if (gerarAnoVencimento) {
                            mc.setVencimento(mensagemConvencao.getVencimento());
                        } else {
                            mc.setMensagemCompensacao(mensagemConvencao.getMensagemCompensacao());
                            mc.setMensagemContribuinte(mensagemConvencao.getMensagemContribuinte());
                        }

                        break;
                }

                if (!db.update(mc)) {
                    msgConfirma = "Ocorreu um erro ao atualizar!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    return;
                }

                new NovoLog().update(beforeUpdate,
                        " - Referência: " + mc.getReferencia()
                        + " - Vencimento: " + mc.getVencimento()
                        + " - Serviço: (" + mc.getServicos().getId() + ") "
                        + " - Tipo Serviço: (" + mc.getTipoServico().getId() + ") " + mc.getTipoServico().getDescricao()
                        + " - Convenção: (" + mc.getConvencao().getId() + ") " + mc.getConvencao().getDescricao()
                        + " - Grupo Cidade: (" + mc.getGrupoCidade().getId() + ") " + mc.getGrupoCidade().getDescricao()
                        + " - Mensagem Compensação: " + mc.getMensagemCompensacao()
                        + " - Mensagem Contribuinte: " + mc.getMensagemContribuinte()
                );

            }

            ref = new DataHoje().incrementarMeses(1, "01/" + ref).substring(3);

        }
    }

    private synchronized String insertMensagem(int idConv, int idGrupo, int idServ, int idTipo, String referencia, String vencimento) {
        Dao dao = new Dao();
        MensagemConvencaoDao db = new MensagemConvencaoDao();
        String result = "";
        mensagemConvencao.setConvencao((Convencao) dao.find(new Convencao(), idConv));
        mensagemConvencao.setGrupoCidade((GrupoCidade) dao.find(new GrupoCidade(), idGrupo));
        mensagemConvencao.setServicos((Servicos) dao.find(new Servicos(), idServ));
        mensagemConvencao.setTipoServico((TipoServico) dao.find(new TipoServico(), idTipo));
        mensagemConvencao.setReferencia(referencia);
        mensagemConvencao.setVencimento(vencimento);
        NovoLog novoLog = new NovoLog();
        MensagemConvencao menConvencao = db.verificaMensagem(idConv, idServ, idTipo, idGrupo, referencia);
        try {
            if (menConvencao == null) {
                dao.openTransaction();
                if (dao.save(mensagemConvencao)) {
                    novoLog.save(
                            " - Referência: " + mensagemConvencao.getReferencia()
                            + " - Vencimento: " + mensagemConvencao.getVencimento()
                            + " - Serviço: (" + mensagemConvencao.getServicos().getId() + ") "
                            + " - Tipo Serviço: (" + mensagemConvencao.getTipoServico().getId() + ") " + mensagemConvencao.getTipoServico().getDescricao()
                            + " - Convenção: (" + mensagemConvencao.getConvencao().getId() + ") " + mensagemConvencao.getConvencao().getDescricao()
                            + " - Grupo Cidade: (" + mensagemConvencao.getGrupoCidade().getId() + ") " + mensagemConvencao.getGrupoCidade().getDescricao()
                            + " - Mensagem Compensação: " + mensagemConvencao.getMensagemCompensacao()
                            + " - Mensagem Contribuinte: " + mensagemConvencao.getMensagemContribuinte()
                    );
                    dao.commit();
                    mensagemConvencao.setId(-1);
                    result = "Mensagem salva com Sucesso!";
                } else {
                    result = "Erro ao salvar mensagem!";
                    dao.rollback();
                }
            } else if (menConvencao.getId() == -1) {
                result = "Mensagem ja existe!";
            } else {
                result = "Mensagem ja existe!";
            }
        } catch (Exception e) {
        }
        return result;
    }

    public String novo() {
        GenericaSessao.remove("mensagemPesquisa");
        GenericaSessao.remove("mensagemConvencaoBean");
//        mensagemConvencao = new MensagemConvencao();
//        msgConfirma = "";
//        idGrupo = 0;
//        idConvencao = 0;
//        idServico = 0;
//        idTipoServico = 0;
//        vencimento = DataHoje.data();

        mensagemConvencao.setReferencia(DataHoje.data().substring(3));
        return "mensagem";
    }

    public String excluir() {
        if (mensagemConvencao.getId() != -1) {
            NovoLog novoLog = new NovoLog();
            Dao dao = new Dao();
            mensagemConvencao = (MensagemConvencao) dao.find(mensagemConvencao);
            dao.openTransaction();
            if (dao.delete(mensagemConvencao)) {
                novoLog.delete(
                        " - Referência: " + mensagemConvencao.getReferencia()
                        + " - Vencimento: " + mensagemConvencao.getVencimento()
                        + " - Serviço: (" + mensagemConvencao.getServicos().getId() + ") "
                        + " - Tipo Serviço: (" + mensagemConvencao.getTipoServico().getId() + ") " + mensagemConvencao.getTipoServico().getDescricao()
                        + " - Convenção: (" + mensagemConvencao.getConvencao().getId() + ") " + mensagemConvencao.getConvencao().getDescricao()
                        + " - Grupo Cidade: (" + mensagemConvencao.getGrupoCidade().getId() + ") " + mensagemConvencao.getGrupoCidade().getDescricao()
                        + " - Mensagem Compensação: " + mensagemConvencao.getMensagemCompensacao()
                        + " - Mensagem Contribuinte: " + mensagemConvencao.getMensagemContribuinte()
                );
                dao.commit();
                msgConfirma = "Mensagem Excluida com Sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
            } else {
                dao.rollback();
                msgConfirma = "Mensagem não pode ser Excluida!";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        } else {
            GenericaMensagem.warn("Erro", "Pesquise uma mensagem para ser Excluída!");
        }
        return null;
    }

    public List getListaMensagens() {
        if ((!listaServico.isEmpty()) && (!listaTipoServico.isEmpty())) {
            MensagemConvencaoDao db = new MensagemConvencaoDao();
            int vetorInt[] = new int[2];
            vetorInt[0] = Integer.parseInt(listaServico.get(idServico).getDescription());
            vetorInt[1] = Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription());
            listaMensagens = db.pesquisaTodosOrdenados(
                    mensagemConvencao.getReferencia(),
                    vetorInt[0],
                    vetorInt[1]);

            if (listaMensagens == null) {
                listaMensagens = new ArrayList();
            }
        }

        return listaMensagens;
    }

    public void refreshForm() {
    }

    public boolean getAtualizar() {
        if (mensagemConvencao.getId() != -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getNovox() {
        if (mensagemConvencao.getId() == -1) {
            return true;
        } else {
            return false;
        }
    }

    public String editar(MensagemConvencao me) {
        mensagemConvencao = me;
        vencimento = mensagemConvencao.getVencimento();
        //listaMensagens.remove(listaMensagens.get(idIndex));
        msgConfirma = "";

        if (mensagemConvencao.getConvencao().getId() != -1) {
            for (int i = 0; i < listaConvencoes.size(); i++) {
                if (Integer.parseInt(listaConvencoes.get(i).getDescription()) == mensagemConvencao.getConvencao().getId()) {
                    idConvencao = (Integer) listaConvencoes.get(i).getValue();
                    break;
                }
            }
        }

        loadListaGrupoCidade();
        
        if (mensagemConvencao.getGrupoCidade().getId() != -1) {
            for (SelectItem grupo1 : listaGrupoCidade) {
                if (Integer.parseInt(grupo1.getDescription()) == mensagemConvencao.getGrupoCidade().getId()) {
                    idGrupo = (Integer) grupo1.getValue();
                    break;
                }
            }
        }

        if (mensagemConvencao.getTipoServico().getId() != -1) {
            for (SelectItem tipoServico1 : listaTipoServico) {
                if (Integer.parseInt(tipoServico1.getDescription()) == mensagemConvencao.getTipoServico().getId()) {
                    idTipoServico = (Integer) tipoServico1.getValue();
                    break;
                }
            }
        }

        if (mensagemConvencao.getServicos().getId() != -1) {
            for (SelectItem servico : listaServico) {
                if (Integer.parseInt(servico.getDescription()) == mensagemConvencao.getServicos().getId()) {
                    idServico = (Integer) servico.getValue();
                    break;
                }
            }
        }
        return "mensagem";
    }

    public final List<SelectItem> getListaConvencoes() {
        return listaConvencoes;
    }

    public final List<SelectItem> getListaGrupoCidade() {
        return listaGrupoCidade;
    }

    public List<SelectItem> getListaTipoServico() {
        return listaTipoServico;
    }

    public final List<SelectItem> getListaServico() {
        return listaServico;
    }

    public void capturarUltimaMensagem() {
        MensagemConvencaoDao mensagemDB = new MensagemConvencaoDao();
        this.mensagemConvencao.getConvencao().setId(-1);
        this.mensagemConvencao.getGrupoCidade().setId(-1);
        this.mensagemConvencao.getTipoServico().setId(-1);
        this.mensagemConvencao.getServicos().setId(-1);
        this.mensagemConvencao.setVencimento("");
        int[] id = new int[4];
        id[0] = Integer.parseInt(listaConvencoes.get(idConvencao).getDescription());
        id[1] = Integer.parseInt(listaServico.get(idServico).getDescription());
        id[2] = Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription());
        id[3] = Integer.parseInt(listaGrupoCidade.get(idGrupo).getDescription());
        MensagemConvencao msgConvencao = mensagemDB.pesquisarUltimaMensagem(id[0], id[1], id[2], id[3]);
        this.mensagemConvencao.setConvencao(msgConvencao.getConvencao());
        this.mensagemConvencao.setGrupoCidade(msgConvencao.getGrupoCidade());
        this.mensagemConvencao.setTipoServico(msgConvencao.getTipoServico());
        this.mensagemConvencao.setServicos(msgConvencao.getServicos());
        this.mensagemConvencao.setMensagemCompensacao(msgConvencao.getMensagemCompensacao());
        this.mensagemConvencao.setMensagemContribuinte(msgConvencao.getMensagemContribuinte());
        this.mensagemConvencao.setDtVencimento(msgConvencao.getDtVencimento());
    }

    public void setListaMensagens(List listaMensagens) {
        this.listaMensagens = listaMensagens;
    }

    public String getIdentificador() {
        if (mensagemConvencao.getId() == -1) {
            return "";
        } else {
            return Integer.toString(mensagemConvencao.getId());
        }
    }

    public String getHabilitar() {
        if (mensagemConvencao.getId() != -1) {
            return "true";
        } else {
            return "false";
        }
    }

    public boolean isDisAcordo() {
        if (!listaTipoServico.isEmpty()) {
            if (Integer.parseInt(listaTipoServico.get(idTipoServico).getDescription()) == 4) {
                disAcordo = true;
                mensagemConvencao.setDtVencimento(new Date());
                mensagemConvencao.setReferencia("");
                mensagemConvencao.setMensagemContribuinte("");
                mensagemConvencao.setVencimento("");
            } else {
                disAcordo = false;
            }
        }
        return disAcordo;
    }

    public void setDisAcordo(boolean disAcordo) {
        this.disAcordo = disAcordo;
    }

    public boolean isProcessarTipoServicos() {
        return processarTipoServicos;
    }

    public void setProcessarTipoServicos(boolean processarTipoServicos) {
        this.processarTipoServicos = processarTipoServicos;
    }

    public boolean isGerarAno() {
        return gerarAno;
    }

    public void setGerarAno(boolean gerarAno) {
        this.gerarAno = gerarAno;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public int getProcessarGrupos() {
        return processarGrupos;
    }

    public void setProcessarGrupos(int processarGrupos) {
        this.processarGrupos = processarGrupos;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getIdReplica() {
        return idReplica;
    }

    public void setIdReplica(int idReplica) {
        this.idReplica = idReplica;
    }

    public String getReplicaPara() {
        replicaPara = Integer.toString(Integer.valueOf(this.getListaRefReplica().get(idReplica).getLabel()) + 1);
        return replicaPara;
    }

    public void setReplicaPara(String replicaPara) {
        this.replicaPara = replicaPara;
    }

    public int getProcessarGruposAlterar() {
        return processarGruposAlterar;
    }

    public void setProcessarGruposAlterar(int processarGruposAlterar) {
        this.processarGruposAlterar = processarGruposAlterar;
    }

    public boolean isGerarAnoVencimento() {
        return gerarAnoVencimento;
    }

    public void setGerarAnoVencimento(boolean gerarAnoVencimento) {
        this.gerarAnoVencimento = gerarAnoVencimento;
    }
}
