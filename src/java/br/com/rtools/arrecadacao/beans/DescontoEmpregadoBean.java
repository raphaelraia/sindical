package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.dao.DescontoEmpregadoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.DescontoEmpregado;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class DescontoEmpregadoBean implements Serializable {

    private DescontoEmpregado descontoEmpregado = new DescontoEmpregado();
    private int idServicos = 0;
    private int idGrupoCidade = 0;
    private int idConvencao = 0;
    private String msgConfirma = "";
    private String percentual = "0.0";
    private String valor = "0";
    private List<SelectItem> listaServicos = new ArrayList<>();
    private List<SelectItem> listaGrupoCidade = new ArrayList<>();
    private List<SelectItem> listaConvencao = new ArrayList<>();
    private List<DescontoEmpregado> listaDescontoEmpregado = new ArrayList();
    private int idIndex = -1;
    private boolean limpar = false;

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public DescontoEmpregado getDescontoEmpregado() {
        return descontoEmpregado;
    }

    public void setDescontoEmpregado(DescontoEmpregado descontoEmpregado) {
        this.descontoEmpregado = descontoEmpregado;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public String novo() {
        descontoEmpregado = new DescontoEmpregado();
        listaDescontoEmpregado.clear();
        msgConfirma = "";
        limpar = false;
        idServicos = 0;
        idGrupoCidade = 0;
        idConvencao = 0;
        valor = "0";
        percentual = "0.0";
        return "descontoEmpregado";
    }

    public void limpar() {
        if (limpar == true) {
            novo();
        }
        descontoEmpregado = new DescontoEmpregado();
        listaDescontoEmpregado.clear();
        msgConfirma = "";
        limpar = false;
        idServicos = 0;
        idGrupoCidade = 0;
        idConvencao = 0;
        valor = "0";
        percentual = "0.0";
    }

    public String salvar() {
        Dao dao = new Dao();
        NovoLog log = new NovoLog();
        dao.openTransaction();
        Servicos servicos = (Servicos) dao.find(new Servicos(), Integer.parseInt(listaServicos.get(idServicos).getDescription()));
        GrupoCidade grupoCidade = (GrupoCidade) dao.find(new GrupoCidade(), Integer.parseInt(listaGrupoCidade.get(idGrupoCidade).getDescription()));
        Convencao convencao = (Convencao) dao.find(new Convencao(), Integer.parseInt(listaConvencao.get(idConvencao).getDescription()));
        if (percentual.isEmpty()) {
            percentual = "0.0";
        }
        descontoEmpregado.setPercentual(Moeda.substituiVirgulaFloat(percentual));
        if (valor.isEmpty()) {
            valor = "0";
        }
        descontoEmpregado.setValorEmpregado(Moeda.substituiVirgulaFloat(valor));

        if (descontoEmpregado.getPercentual() <= 0 && descontoEmpregado.getValorEmpregado() <= 0) {
            msgConfirma = "Digite um Percentual ou Valor de empregado!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (descontoEmpregado.getId() == -1) {
            if (DataHoje.validaReferencias(descontoEmpregado.getReferenciaInicial(), descontoEmpregado.getReferenciaFinal())) {
                descontoEmpregado.setServicos(servicos);
                descontoEmpregado.setGrupoCidade(grupoCidade);
                descontoEmpregado.setConvencao(convencao);
                if (dao.save(descontoEmpregado)) {
                    dao.commit();
                    msgConfirma = "Desconto salvo com Sucesso!";
                    GenericaMensagem.info("Sucesso", msgConfirma);
                    log.save(
                            "ID: " + descontoEmpregado.getId()
                            + " - Servico: (" + descontoEmpregado.getServicos().getId() + ") " + descontoEmpregado.getServicos().getDescricao()
                            + " - Valor: " + descontoEmpregado.getValorEmpregado()
                            + " - Grupo Cidade: (" + descontoEmpregado.getGrupoCidade().getId() + ") " + descontoEmpregado.getGrupoCidade().getDescricao()
                            + " - Convencao: (" + descontoEmpregado.getConvencao().getId() + ") " + descontoEmpregado.getConvencao().getDescricao()
                    );
                } else {
                    dao.rollback();
                    msgConfirma = "Erro ao salvar Desconto!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                }
            } else {
                dao.rollback();
                msgConfirma = "Referência incorreta!";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        } else {
            DescontoEmpregado d = (DescontoEmpregado) dao.find(new DescontoEmpregado(), descontoEmpregado.getId());
            String beforeUpdate
                    = "ID: " + d.getId()
                    + " - Servico: (" + d.getServicos().getId() + ") " + d.getServicos().getDescricao()
                    + " - Valor: " + d.getValorEmpregado()
                    + " - Grupo Cidade: (" + d.getGrupoCidade().getId() + ") " + d.getGrupoCidade().getDescricao()
                    + " - Convencao: (" + d.getConvencao().getId() + ") " + d.getConvencao().getDescricao();
            descontoEmpregado.setServicos(servicos);
            descontoEmpregado.setGrupoCidade(grupoCidade);
            descontoEmpregado.setConvencao(convencao);
            if (dao.update(descontoEmpregado)) {
                dao.commit();
                msgConfirma = "Desconto atualizado com Sucesso!";
                GenericaMensagem.info("Sucesso", msgConfirma);
                log.update(beforeUpdate,
                        "ID: " + descontoEmpregado.getId()
                        + " - Servico: (" + descontoEmpregado.getServicos().getId() + ") " + descontoEmpregado.getServicos().getDescricao()
                        + " - Valor: " + descontoEmpregado.getValorEmpregado()
                        + " - Grupo Cidade: (" + descontoEmpregado.getGrupoCidade().getId() + ") " + descontoEmpregado.getGrupoCidade().getDescricao()
                        + " - Convencao: (" + descontoEmpregado.getConvencao().getId() + ") " + descontoEmpregado.getConvencao().getDescricao()
                );
            } else {
                dao.rollback();
            }
        }
        return null;
    }

    public String btnExcluir(DescontoEmpregado de) {
        NovoLog log = new NovoLog();
        Dao dao = new Dao();
        dao.openTransaction();
        if (dao.delete(descontoEmpregado)) {
            limpar = true;
            msgConfirma = "Desconto Excluido com Sucesso!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            log.delete(
                    "ID: " + descontoEmpregado.getId()
                    + " - Servico: (" + descontoEmpregado.getServicos().getId() + ") " + descontoEmpregado.getServicos().getDescricao()
                    + " - Valor: " + descontoEmpregado.getValorEmpregado()
                    + " - Grupo Cidade: (" + descontoEmpregado.getGrupoCidade().getId() + ") " + descontoEmpregado.getGrupoCidade().getDescricao()
                    + " - Convencao: (" + descontoEmpregado.getConvencao().getId() + ") " + descontoEmpregado.getConvencao().getDescricao()
            );
            dao.commit();
        } else {
            msgConfirma = "Desconto não pode ser Excluido!";
            GenericaMensagem.warn("Erro", msgConfirma);
            dao.rollback();
        }
        return null;
    }

    public String editar(DescontoEmpregado de) {
        descontoEmpregado = de;//(DescontoEmpregado) listaDescontoEmpregado.get(idIndex);
        if (descontoEmpregado != null) {
            if (descontoEmpregado.getServicos().getId() != -1) {
                for (int i = 0; i < listaServicos.size(); i++) {
                    if (Integer.parseInt(listaServicos.get(i).getDescription()) == descontoEmpregado.getServicos().getId()) {
                        setIdServicos(i);
                        break;
                    }
                }
            }
            if (descontoEmpregado.getGrupoCidade().getId() != -1) {
                for (int i = 0; i < listaGrupoCidade.size(); i++) {
                    if (Integer.parseInt(listaGrupoCidade.get(i).getDescription()) == descontoEmpregado.getGrupoCidade().getId()) {
                        setIdGrupoCidade(i);
                        break;
                    }
                }
            }
            if (descontoEmpregado.getConvencao().getId() != -1) {
                for (int i = 0; i < listaConvencao.size(); i++) {
                    if (Integer.parseInt(listaConvencao.get(i).getDescription()) == descontoEmpregado.getConvencao().getId()) {
                        setIdConvencao(i);
                        break;
                    }
                }
            }
            percentual = Float.toString(descontoEmpregado.getPercentual());
            valor = Float.toString(descontoEmpregado.getValorEmpregado());
        }
        return "descontoEmpregado";
    }

    public List<SelectItem> getListaServico() {
        if (listaServicos.isEmpty()) {
            int i = 0;
            ServicosDao db = new ServicosDao();
            List select = db.pesquisaTodos(4);
            while (i < select.size()) {
                listaServicos.add(new SelectItem(i,
                        (String) ((Servicos) select.get(i)).getDescricao(),
                        Integer.toString(((Servicos) select.get(i)).getId())));
                i++;
            }
        }
        return listaServicos;
    }

    public List<SelectItem> getListaGrupoCidade() {
        listaGrupoCidade = new ArrayList();
        int i = 0;
        ConvencaoCidadeDao db = new ConvencaoCidadeDao();
        List select = db.pesquisarGruposPorConvencao(Integer.parseInt(getListaConvencao().get(idConvencao).getDescription()));
        while (i < select.size()) {
            listaGrupoCidade.add(new SelectItem(i,
                    (String) ((GrupoCidade) select.get(i)).getDescricao(),
                    Integer.toString(((GrupoCidade) select.get(i)).getId())));
            i++;
        }
        return listaGrupoCidade;
    }

    public List<SelectItem> getListaConvencao() {
        if (listaConvencao.isEmpty()) {
            List<Convencao> list = new Dao().list(new Convencao(), true);
            for (int i = 0; i < list.size(); i++) {
                listaConvencao.add(new SelectItem(i,
                        list.get(i).getDescricao(),
                        Integer.toString(list.get(i).getId())));
            }
        }

        return listaConvencao;
    }

    public List<DescontoEmpregado> getListaDescontoEmpregado() {
        DescontoEmpregadoDao db = new DescontoEmpregadoDao();
        listaDescontoEmpregado = db.pesquisaTodos();
        return listaDescontoEmpregado;
    }

    public int getIdGrupoCidade() {
        return idGrupoCidade;
    }

    public void setIdGrupoCidade(int idGrupoCidade) {
        this.idGrupoCidade = idGrupoCidade;
    }

    public int getIdConvencao() {
        return idConvencao;
    }

    public void setIdConvencao(int idConvencao) {
        this.idConvencao = idConvencao;
    }

    public String getPercentual() {
        return percentual;
    }

    public void setPercentual(String percentual) {
        this.percentual = Moeda.substituiVirgula(percentual);
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public boolean isLimpar() {
        return limpar;
    }

    public void setLimpar(boolean limpar) {
        this.limpar = limpar;
    }
}
